@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.testgen

import com.ivieleague.decompiler6502tokotlin.interpreter.BinaryInterpreter6502

/**
 * Traces function calls during interpreter execution.
 * Attaches to BinaryInterpreter6502 via hooks to capture input/output states.
 *
 * Usage:
 * ```
 * val tracer = FunctionCallTracer(interpreter)
 * tracer.attach()
 * // Run TAS...
 * val captures = tracer.getCaptures()
 * ```
 */
class FunctionCallTracer(
    private val interpreter: BinaryInterpreter6502,
    /** Set of function addresses to capture. Null = capture all functions. */
    private val targetFunctions: Set<Int>? = null
) {
    private val captures = mutableListOf<FunctionCallCapture>()
    private val callStack = mutableListOf<PendingCapture>()
    private var currentFrame = 0
    private var instructionCounter = 0L

    // Track call depth for proper RTS matching
    private var callDepth = 0

    // NMI context management - save main context when NMI fires
    private var savedCallStack: MutableList<PendingCapture>? = null
    private var savedCallDepth: Int = 0
    private var inNmiContext: Boolean = false

    // Original hooks to chain
    private var originalReadHook: ((Int) -> UByte?)? = null
    private var originalWriteHook: ((Int, UByte) -> Boolean)? = null
    private var originalJsrHook: ((Int, Int) -> Unit)? = null
    private var originalRtsHook: (() -> Unit)? = null
    private var originalRtiHook: (() -> Unit)? = null
    private var originalNmiHook: (() -> Unit)? = null

    /**
     * Pending capture waiting for RTS completion.
     */
    private data class PendingCapture(
        val functionAddress: Int,
        val callerAddress: Int,
        val frame: Int,
        val timestamp: Long,
        val callDepth: Int,
        val inputState: CpuState,
        val memoryReads: MutableMap<Int, Int>,
        val memoryWrites: MutableMap<Int, Int>,
        val nestedCalls: MutableList<Int>
    )

    /**
     * Attach hooks to the interpreter.
     * Call this before running the TAS.
     */
    fun attach() {
        // Save original hooks to chain them
        originalReadHook = interpreter.memoryReadHook
        originalWriteHook = interpreter.memoryWriteHook
        originalJsrHook = interpreter.jsrHook
        originalRtsHook = interpreter.rtsHook
        originalRtiHook = interpreter.rtiHook
        originalNmiHook = interpreter.nmiHook

        // Wrap NMI hook to save context before NMI handler runs
        interpreter.nmiHook = {
            onNmiEntry()
            originalNmiHook?.invoke()
        }

        // Wrap JSR hook
        interpreter.jsrHook = { targetAddress, callerAddress ->
            originalJsrHook?.invoke(targetAddress, callerAddress)
            onJsr(targetAddress, callerAddress)
        }

        // Wrap RTS hook
        interpreter.rtsHook = {
            onRts()
            originalRtsHook?.invoke()
        }

        // Wrap RTI hook (NMI returns)
        interpreter.rtiHook = {
            onRti()
            originalRtiHook?.invoke()
        }

        // Wrap memory read hook to track reads
        interpreter.memoryReadHook = { addr ->
            val result = originalReadHook?.invoke(addr)
            trackMemoryRead(addr, result)
            result
        }

        // Wrap memory write hook to track writes
        interpreter.memoryWriteHook = { addr, value ->
            val result = originalWriteHook?.invoke(addr, value)
            trackMemoryWrite(addr, value)
            result ?: false
        }
    }

    /**
     * Detach hooks and restore originals.
     */
    fun detach() {
        interpreter.memoryReadHook = originalReadHook
        interpreter.memoryWriteHook = originalWriteHook
        interpreter.jsrHook = originalJsrHook
        interpreter.rtsHook = originalRtsHook
        interpreter.rtiHook = originalRtiHook
        interpreter.nmiHook = originalNmiHook
    }

    /**
     * Track memory read for current function call.
     */
    private fun trackMemoryRead(addr: Int, hookedValue: UByte?) {
        if (callStack.isEmpty()) return

        // Only track RAM reads (not ROM or I/O)
        if (addr >= 0x8000) return
        if (addr in 0x2000..0x2007) return  // PPU registers
        if (addr in 0x4000..0x401F) return  // APU/IO registers

        // by Claude - Find pending with depth matching current context
        // Inside a function after JSR: callDepth = pending.callDepth + 1
        // After nested RTS returns: callDepth = pending.callDepth
        // First try callDepth - 1 (normal case), then callDepth (post-RTS case)
        val pending = callStack.lastOrNull { it.callDepth == callDepth - 1 }
            ?: callStack.lastOrNull { it.callDepth == callDepth }
            ?: return
        if (addr !in pending.memoryReads) {
            // Get the actual value (from hook or memory)
            val value = hookedValue?.toInt() ?: interpreter.memory.readByte(addr).toInt()
            pending.memoryReads[addr] = value
        }
    }

    /**
     * Track memory write for current function call.
     */
    private fun trackMemoryWrite(addr: Int, value: UByte) {
        if (callStack.isEmpty()) return

        // Only track RAM writes
        if (addr >= 0x8000) return
        if (addr in 0x2000..0x2007) return
        if (addr in 0x4000..0x401F) return

        // by Claude - Find pending with depth matching current context
        // Inside a function after JSR: callDepth = pending.callDepth + 1
        // After nested RTS returns: callDepth = pending.callDepth
        // First try callDepth - 1 (normal case), then callDepth (post-RTS case)
        val pending = callStack.lastOrNull { it.callDepth == callDepth - 1 }
            ?: callStack.lastOrNull { it.callDepth == callDepth }
            ?: return
        pending.memoryWrites[addr] = value.toInt()
    }

    /**
     * Called when JSR instruction executes.
     */
    private fun onJsr(targetAddress: Int, callerAddress: Int) {
        // Check if we should capture this function
        if (targetFunctions != null && targetAddress !in targetFunctions) {
            // Still track depth for proper RTS matching
            callDepth++
            return
        }

        // Create pending capture
        val pending = PendingCapture(
            functionAddress = targetAddress,
            callerAddress = callerAddress,
            frame = currentFrame,
            timestamp = instructionCounter,
            callDepth = callDepth,
            inputState = CpuState.from(interpreter.cpu),
            memoryReads = mutableMapOf(),
            memoryWrites = mutableMapOf(),
            nestedCalls = mutableListOf()
        )

        // Push onto call stack
        callStack.add(pending)
        callDepth++
    }

    /**
     * Called when RTS instruction executes.
     */
    private fun onRts() {
        callDepth = maxOf(0, callDepth - 1)

        if (callStack.isEmpty()) return

        // Find the pending capture for this call depth
        val pendingIdx = callStack.indexOfLast { it.callDepth == callDepth }
        if (pendingIdx < 0) return

        val pending = callStack.removeAt(pendingIdx)

        // Record nested call in parent (if any)
        // by Claude - Propagate memory READS from child to parent so that parent's
        // test case includes all the memory setup needed by nested functions.
        // We do NOT propagate writes - writes should stay local to the function that made them.
        if (callStack.isNotEmpty()) {
            val parent = callStack.last()
            parent.nestedCalls.add(pending.functionAddress)
            // Propagate reads that aren't already in parent's reads
            for ((addr, value) in pending.memoryReads) {
                if (addr !in parent.memoryReads) {
                    parent.memoryReads[addr] = value
                }
            }
        }

        // Complete the capture
        val capture = FunctionCallCapture(
            functionAddress = pending.functionAddress,
            frame = pending.frame,
            timestamp = pending.timestamp,
            callerAddress = pending.callerAddress,
            callDepth = pending.callDepth,
            inputState = pending.inputState,
            memoryReads = pending.memoryReads.toMap(),
            outputState = CpuState.from(interpreter.cpu),
            memoryWrites = pending.memoryWrites.toMap(),
            nestedCalls = pending.nestedCalls.toList(),
            inputStateHash = computeInputStateHash(
                pending.functionAddress,
                pending.inputState,
                pending.memoryReads
            )
        )

        captures.add(capture)
    }

    // Debug counter for NMI entries/exits
    private var nmiEntryCount = 0
    private var rtiCount = 0

    /**
     * Called when NMI is about to be serviced.
     * Save the current call stack context so we can restore it after RTI.
     */
    private fun onNmiEntry() {
        nmiEntryCount++
        if (inNmiContext) {
            // Nested NMI (shouldn't happen normally, but handle it)
            return
        }

        // Save the current context - these are functions that were interrupted mid-execution
        // We DON'T want to capture them with NMI's memory accesses
        savedCallStack = callStack.toMutableList()
        savedCallDepth = callDepth
        inNmiContext = true

        // Start fresh for NMI handler
        // NMI handler will have its own function captures
        callStack.clear()
        callDepth = 0
    }

    /** Get NMI statistics for debugging */
    fun getNmiStats(): String = "NMI entries: $nmiEntryCount, RTI returns: $rtiCount"

    /**
     * Called when RTI instruction executes (NMI return).
     * Restore the saved call stack context and discard NMI captures.
     */
    private fun onRti() {
        rtiCount++

        // Complete any pending captures from the NMI handler normally
        // These are functions that completed within the NMI handler
        for (pending in callStack) {
            val capture = FunctionCallCapture(
                functionAddress = pending.functionAddress,
                frame = pending.frame,
                timestamp = pending.timestamp,
                callerAddress = pending.callerAddress,
                callDepth = pending.callDepth,
                inputState = pending.inputState,
                memoryReads = pending.memoryReads.toMap(),
                outputState = CpuState.from(interpreter.cpu),
                memoryWrites = pending.memoryWrites.toMap(),
                nestedCalls = pending.nestedCalls.toList(),
                inputStateHash = computeInputStateHash(
                    pending.functionAddress,
                    pending.inputState,
                    pending.memoryReads
                )
            )
            captures.add(capture)
        }

        // Restore the saved context from before NMI
        val saved = savedCallStack
        if (saved != null && inNmiContext) {
            callStack.clear()
            callStack.addAll(saved)
            callDepth = savedCallDepth
            savedCallStack = null
            inNmiContext = false
        } else {
            // No saved context - just clear (legacy behavior)
            callStack.clear()
            callDepth = 0
        }
    }

    /**
     * Call this at start of each frame.
     */
    fun onFrameStart(frameNumber: Int) {
        currentFrame = frameNumber
    }

    /**
     * Call this after each instruction.
     */
    fun onInstruction() {
        instructionCounter++
    }

    /**
     * Get all captured function calls.
     */
    fun getCaptures(): List<FunctionCallCapture> = captures.toList()

    /**
     * Get captures for a specific function.
     */
    fun getCapturesForFunction(address: Int): List<FunctionCallCapture> =
        captures.filter { it.functionAddress == address }

    /**
     * Get number of captures so far.
     */
    fun getCaptureCount(): Int = captures.size

    /**
     * Get unique function addresses captured.
     */
    fun getCapturedFunctions(): Set<Int> = captures.map { it.functionAddress }.toSet()

    /**
     * Clear all captures.
     */
    fun clear() {
        captures.clear()
        callStack.clear()
        callDepth = 0
        currentFrame = 0
        instructionCounter = 0
        savedCallStack = null
        savedCallDepth = 0
        inNmiContext = false
    }
}
