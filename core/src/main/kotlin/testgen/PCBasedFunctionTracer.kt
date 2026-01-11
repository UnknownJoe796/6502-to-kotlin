@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.testgen

import com.ivieleague.decompiler6502tokotlin.interpreter.BinaryInterpreter6502

/**
 * Traces function execution using PC-based entry detection.
 *
 * Unlike FunctionCallTracer which hooks JSR instructions, this tracer
 * detects function entry by checking if PC matches a known function address.
 * This is more accurate because it captures exactly the functions we care about,
 * regardless of how they were called (JSR, JMP, fall-through, etc.).
 *
 * Usage:
 * ```
 * val functions = mapOf(0x8182 to "pauseRoutine", 0x81C6 to "spriteShuffler")
 * val tracer = PCBasedFunctionTracer(interpreter, functions)
 * tracer.attach()
 *
 * while (running) {
 *     tracer.beforeStep()  // Check for function entry
 *     interpreter.step()
 *     tracer.afterStep()   // Track memory and check for exit
 * }
 *
 * val captures = tracer.getCaptures()
 * ```
 */
class PCBasedFunctionTracer(
    private val interpreter: BinaryInterpreter6502,
    /** Map of address -> function name for functions to capture */
    private val targetFunctions: Map<Int, String>,
    /** Map of address -> function metadata for parameter info */
    private val functionMetadata: Map<Int, DecompiledFunctionMetadata> = emptyMap()
) {
    private val captures = mutableListOf<FunctionCallCapture>()
    private val callStack = mutableListOf<PendingCapture>()
    private var currentFrame = 0
    private var instructionCounter = 0L

    // Track call depth for proper exit detection
    private var callDepth = 0

    // NMI context management
    private var savedCallStack: MutableList<PendingCapture>? = null
    private var savedCallDepth: Int = 0
    private var inNmiContext: Boolean = false

    // Original hooks to chain
    private var originalReadHook: ((Int) -> UByte?)? = null
    private var originalWriteHook: ((Int, UByte) -> Boolean)? = null
    private var originalRtsHook: (() -> Unit)? = null
    private var originalRtiHook: (() -> Unit)? = null
    private var originalNmiHook: (() -> Unit)? = null
    private var originalJsrHook: ((Int, Int) -> Unit)? = null

    // Track the PC value before each instruction for entry detection
    private var lastCheckedPC: Int = -1

    /**
     * Pending capture waiting for function exit.
     */
    private data class PendingCapture(
        val functionAddress: Int,
        val functionName: String,
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
     */
    fun attach() {
        originalReadHook = interpreter.memoryReadHook
        originalWriteHook = interpreter.memoryWriteHook
        originalRtsHook = interpreter.rtsHook
        originalRtiHook = interpreter.rtiHook
        originalNmiHook = interpreter.nmiHook
        originalJsrHook = interpreter.jsrHook

        // NMI hook to save/restore context
        interpreter.nmiHook = {
            onNmiEntry()
            originalNmiHook?.invoke()
        }

        // JSR hook to track call depth
        interpreter.jsrHook = { targetAddress, callerAddress ->
            originalJsrHook?.invoke(targetAddress, callerAddress)
            callDepth++
        }

        // RTS hook to detect function exit
        interpreter.rtsHook = {
            onRts()
            originalRtsHook?.invoke()
        }

        // RTI hook for NMI returns
        interpreter.rtiHook = {
            onRti()
            originalRtiHook?.invoke()
        }

        // Memory read hook
        interpreter.memoryReadHook = { addr ->
            val result = originalReadHook?.invoke(addr)
            trackMemoryRead(addr, result)
            result
        }

        // Memory write hook
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
        interpreter.rtsHook = originalRtsHook
        interpreter.rtiHook = originalRtiHook
        interpreter.nmiHook = originalNmiHook
        interpreter.jsrHook = originalJsrHook
    }

    /**
     * Call BEFORE interpreter.step() to detect function entry.
     *
     * This checks if the current PC matches a target function address.
     * If so, we snapshot the CPU state before the function starts executing.
     */
    fun beforeStep() {
        val pc = interpreter.cpu.PC.toInt()

        // Check if we're at a target function entry
        val funcName = targetFunctions[pc]
        if (funcName != null && pc != lastCheckedPC) {
            // We're entering a target function!
            // Capture the state BEFORE the first instruction executes
            onFunctionEntry(pc, funcName)
        }
        lastCheckedPC = pc
    }

    /**
     * Call AFTER interpreter.step() to increment counters.
     */
    fun afterStep() {
        instructionCounter++
    }

    /**
     * Called when PC matches a target function address.
     */
    private fun onFunctionEntry(address: Int, functionName: String) {
        // Don't capture the same function multiple times at the same call depth
        // (prevents issues with loops that jump back to function start)
        if (callStack.any { it.functionAddress == address && it.callDepth == callDepth }) {
            return
        }

        val pending = PendingCapture(
            functionAddress = address,
            functionName = functionName,
            callerAddress = 0, // We don't know the caller in PC-based detection
            frame = currentFrame,
            timestamp = instructionCounter,
            callDepth = callDepth,
            inputState = CpuState.from(interpreter.cpu),
            memoryReads = mutableMapOf(),
            memoryWrites = mutableMapOf(),
            nestedCalls = mutableListOf()
        )

        callStack.add(pending)
    }

    /**
     * Called when RTS executes.
     */
    private fun onRts() {
        // by Claude: Look for pending capture at current depth BEFORE decrementing
        // When function was entered via JSR, callDepth was incremented to N
        // When RTS executes, we need to find the function at depth N, then decrement
        if (callStack.isNotEmpty()) {
            val pendingIdx = callStack.indexOfLast { it.callDepth == callDepth }
            if (pendingIdx >= 0) {
                val pending = callStack.removeAt(pendingIdx)
                completePendingCapture(pending)
            }
        }

        callDepth = maxOf(0, callDepth - 1)
    }

    /**
     * Complete a pending function capture and add to results.
     */
    private fun completePendingCapture(pending: PendingCapture) {
        // Record nested call in parent
        if (callStack.isNotEmpty()) {
            callStack.last().nestedCalls.add(pending.functionAddress)
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
            ),
            functionName = pending.functionName
        )

        captures.add(capture)
    }

    /**
     * Track memory read for current function.
     * Only records reads made directly by the target function, not nested calls.
     */
    private fun trackMemoryRead(addr: Int, hookedValue: UByte?) {
        if (callStack.isEmpty()) return

        // Only track RAM reads
        if (addr >= 0x8000) return
        if (addr in 0x2000..0x2007) return  // PPU
        if (addr in 0x4000..0x401F) return  // APU/IO

        // by Claude - Find pending at current call depth, not just last on stack
        // This fixes issues with JumpEngine-style indirect jumps where orphaned
        // pendings may remain on the stack at different depths
        val pending = callStack.lastOrNull { it.callDepth == callDepth } ?: return
        if (addr !in pending.memoryReads) {
            val value = hookedValue?.toInt() ?: interpreter.memory.readByte(addr).toInt()
            pending.memoryReads[addr] = value
        }
    }

    /**
     * Track memory write for current function.
     * Only records writes made directly by the target function, not nested calls.
     */
    private fun trackMemoryWrite(addr: Int, value: UByte) {
        if (callStack.isEmpty()) return

        // Only track RAM writes
        if (addr >= 0x8000) return
        if (addr in 0x2000..0x2007) return
        if (addr in 0x4000..0x401F) return

        // by Claude - Find pending at current call depth, not just last on stack
        // This fixes issues with JumpEngine-style indirect jumps where orphaned
        // pendings may remain on the stack at different depths
        val pending = callStack.lastOrNull { it.callDepth == callDepth } ?: return
        pending.memoryWrites[addr] = value.toInt()
    }

    // NMI handling
    private var nmiEntryCount = 0
    private var rtiCount = 0

    private fun onNmiEntry() {
        nmiEntryCount++
        if (inNmiContext) return

        savedCallStack = callStack.toMutableList()
        savedCallDepth = callDepth
        inNmiContext = true

        callStack.clear()
        callDepth = 0
    }

    private fun onRti() {
        rtiCount++

        // Complete pending NMI captures
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
                ),
                functionName = pending.functionName
            )
            captures.add(capture)
        }

        // Restore saved context
        val saved = savedCallStack
        if (saved != null && inNmiContext) {
            callStack.clear()
            callStack.addAll(saved)
            callDepth = savedCallDepth
            savedCallStack = null
            inNmiContext = false
        } else {
            callStack.clear()
            callDepth = 0
        }
    }

    fun onFrameStart(frameNumber: Int) {
        currentFrame = frameNumber
    }

    fun getCaptures(): List<FunctionCallCapture> = captures.toList()

    fun getCapturesForFunction(address: Int): List<FunctionCallCapture> =
        captures.filter { it.functionAddress == address }

    fun getCaptureCount(): Int = captures.size

    fun getCapturedFunctions(): Set<Int> = captures.map { it.functionAddress }.toSet()

    fun getNmiStats(): String = "NMI entries: $nmiEntryCount, RTI returns: $rtiCount"

    fun clear() {
        captures.clear()
        callStack.clear()
        callDepth = 0
        currentFrame = 0
        instructionCounter = 0
        savedCallStack = null
        savedCallDepth = 0
        inNmiContext = false
        lastCheckedPC = -1
    }

    companion object {
        /**
         * Create a tracer from decompiled function metadata.
         */
        fun fromMetadata(
            interpreter: BinaryInterpreter6502,
            functions: List<DecompiledFunctionMetadata>
        ): PCBasedFunctionTracer {
            val targetFunctions = functions
                .filter { it.address != null }
                .associate { it.address!! to it.functionName }

            val metadata = functions
                .filter { it.address != null }
                .associateBy { it.address!! }

            return PCBasedFunctionTracer(interpreter, targetFunctions, metadata)
        }
    }
}
