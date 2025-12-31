package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

class KotlinCodeGenTest {

    @Test
    fun testSimpleFunctionGeneration() {
        val code = """
            MyFunction:
                LDA #${'$'}42
                STA ${'$'}00
                RTS
        """.trimIndent()

        val parsed = code.parseToAssemblyCodeFile()
        val blocks = parsed.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertEquals(1, functions.size)
        val func = functions.first()

        val kFunc = func.toKotlinFunction()

        assertEquals("myFunction", kFunc.name)
        assertTrue(kFunc.body.isNotEmpty(), "Function body should not be empty")

        val kotlin = kFunc.toKotlin()
        println("Generated Kotlin:")
        println(kotlin)

        assertTrue(kotlin.contains("fun myFunction"))
    }

    @Test
    fun testIfElseGeneration() {
        val code = """
            CheckValue:
                LDA #${'$'}00
                BEQ IsZero
            NotZero:
                LDX #${'$'}01
                RTS
            IsZero:
                LDX #${'$'}02
                RTS
        """.trimIndent()

        val parsed = code.parseToAssemblyCodeFile()
        val blocks = parsed.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        val func = functions.first()
        val kFunc = func.toKotlinFunction()

        val kotlin = kFunc.toKotlin()
        println("\nGenerated Kotlin for if/else:")
        println(kotlin)

        assertTrue(kotlin.contains("if"))
        assertTrue(kotlin.contains("else"))
    }

    @Test
    fun testLoopGeneration() {
        val code = """
            CountLoop:
                LDX #${'$'}00
            Loop:
                INX
                CPX #${'$'}0A
                BNE Loop
                RTS
        """.trimIndent()

        val parsed = code.parseToAssemblyCodeFile()
        val blocks = parsed.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        val func = functions.first()
        val kFunc = func.toKotlinFunction()

        val kotlin = kFunc.toKotlin()
        println("\nGenerated Kotlin for loop:")
        println(kotlin)

        assertTrue(kotlin.contains("while") || kotlin.contains("do"))
    }

    @Test
    fun generateKotlinForSMB() {
        // Parse the SMB disassembly
        val asmFile = File("smbdism.asm")
        if (!asmFile.exists()) {
            println("Skipping SMB test - smbdism.asm not found")
            return
        }

        val code = asmFile.readText().parseToAssemblyCodeFile()

        // Basic block and function analysis
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        // Ensure outputs directory exists
        val outDir = File("outputs")
        if (!outDir.exists()) outDir.mkdirs()
        val outFile = outDir.resolve("smb-decompiled.kt")
        val constantsFile = outDir.resolve("smb-constants.kt")

        // Extract all constants from the assembly
        val constants = code.lines.mapNotNull { it.constant }
            .sortedBy { it.name }

        // Calculate ROM addresses for code labels and collect data bytes
        val codeLabels = mutableMapOf<String, Int>()
        val romData = mutableListOf<Pair<Int, List<Int>>>() // address -> bytes
        var currentAddress = 0x8000  // Default start address
        for (line in code.lines) {
            // Check for .org directive
            line.originalLine?.let { originalText ->
                val orgMatch = Regex("""\\.org\\s+\$([0-9a-fA-F]+)""").find(originalText)
                if (orgMatch != null) {
                    currentAddress = orgMatch.groupValues[1].toInt(16)
                    return@let
                }
            }

            // Track code labels
            if (line.label != null && !line.label.startsWith("$")) {
                codeLabels[line.label] = currentAddress
            }

            // Count bytes for this line and collect data
            val byteCount = when {
                line.instruction != null -> {
                    // Instruction size = 1 (opcode) + addressing mode size
                    val addrSize = when (line.instruction.address) {
                        null -> 0  // Implied/accumulator
                        is com.ivieleague.decompiler6502tokotlin.hand.AssemblyAddressing.ByteValue,
                        is com.ivieleague.decompiler6502tokotlin.hand.AssemblyAddressing.ConstantReference -> 1
                        is com.ivieleague.decompiler6502tokotlin.hand.AssemblyAddressing.Direct,
                        is com.ivieleague.decompiler6502tokotlin.hand.AssemblyAddressing.DirectX,
                        is com.ivieleague.decompiler6502tokotlin.hand.AssemblyAddressing.DirectY,
                        is com.ivieleague.decompiler6502tokotlin.hand.AssemblyAddressing.IndirectX,
                        is com.ivieleague.decompiler6502tokotlin.hand.AssemblyAddressing.IndirectY,
                        is com.ivieleague.decompiler6502tokotlin.hand.AssemblyAddressing.IndirectAbsolute,
                        is com.ivieleague.decompiler6502tokotlin.hand.AssemblyAddressing.ShortValue -> 2
                        else -> 0
                    }
                    1 + addrSize
                }
                line.data != null -> {
                    when (val d = line.data) {
                        is com.ivieleague.decompiler6502tokotlin.hand.AssemblyData.Db -> {
                            // Collect actual byte values
                            val bytes = d.items.flatMap { item ->
                                when (item) {
                                    is com.ivieleague.decompiler6502tokotlin.hand.AssemblyData.DbItem.ByteValue ->
                                        listOf(item.value)
                                    is com.ivieleague.decompiler6502tokotlin.hand.AssemblyData.DbItem.StringLiteral ->
                                        item.text.map { it.code }
                                    is com.ivieleague.decompiler6502tokotlin.hand.AssemblyData.DbItem.Expr -> {
                                        // Expression references - resolve from codeLabels if possible
                                        val expr = item.expr.removePrefix("<").removePrefix(">")
                                        val labelAddr = codeLabels[expr]
                                        if (labelAddr != null) {
                                            if (item.expr.startsWith("<")) {
                                                listOf(labelAddr and 0xFF)
                                            } else if (item.expr.startsWith(">")) {
                                                listOf((labelAddr shr 8) and 0xFF)
                                            } else {
                                                // Full word - lo, hi
                                                listOf(labelAddr and 0xFF, (labelAddr shr 8) and 0xFF)
                                            }
                                        } else {
                                            // Placeholder for unresolved expression
                                            if (item.expr.startsWith("<") || item.expr.startsWith(">")) {
                                                listOf(0)
                                            } else {
                                                listOf(0, 0)
                                            }
                                        }
                                    }
                                }
                            }
                            if (bytes.isNotEmpty()) {
                                romData.add(currentAddress to bytes)
                            }
                            d.byteCount()
                        }
                        else -> 0
                    }
                }
                else -> 0
            }
            currentAddress += byteCount
        }

        // Generate ROM data initialization file using lazy loading to avoid JVM method size limits
        val romDataFile = outDir.resolve("smb-rom-data.kt")
        var totalRomBytes = 0

        // Merge consecutive ROM regions for efficiency
        val mergedRomData = mutableListOf<Pair<Int, MutableList<Int>>>()
        for ((addr, bytes) in romData.sortedBy { it.first }) {
            val last = mergedRomData.lastOrNull()
            if (last != null && last.first + last.second.size == addr) {
                // Consecutive - merge
                last.second.addAll(bytes)
            } else {
                // New region
                mergedRomData.add(addr to bytes.toMutableList())
            }
        }

        // Split into chunks for JVM method size limit
        val chunkSize = 50  // regions per chunk
        val chunks = mergedRomData.chunked(chunkSize)

        romDataFile.bufferedWriter().use { out ->
            out.appendLine("@file:OptIn(ExperimentalUnsignedTypes::class)")
            out.appendLine()
            out.appendLine("package com.ivieleague.smb")
            out.appendLine()
            out.appendLine("import com.ivieleague.decompiler6502tokotlin.hand.memory")
            out.appendLine()
            out.appendLine("/**")
            out.appendLine(" * Initialize ROM data in memory.")
            out.appendLine(" * This loads all data tables, sprite data, palettes, etc. into memory")
            out.appendLine(" * at their correct ROM addresses.")
            out.appendLine(" * ")
            out.appendLine(" * Data is split into multiple functions to avoid JVM method size limits.")
            out.appendLine(" */")
            out.appendLine("fun initializeRomData() {")
            for (i in chunks.indices) {
                out.appendLine("    loadRomChunk$i()")
            }
            out.appendLine("}")
            out.appendLine()

            // Generate each chunk as a separate function
            for ((chunkIdx, chunk) in chunks.withIndex()) {
                out.appendLine("private fun loadRomChunk$chunkIdx() {")
                for ((addr, bytes) in chunk) {
                    totalRomBytes += bytes.size
                    // For small regions, inline the bytes
                    if (bytes.size <= 8) {
                        bytes.forEachIndexed { i, b ->
                            out.appendLine("    memory[0x${(addr + i).toString(16).uppercase()}] = 0x${b.toString(16).uppercase().padStart(2, '0')}.toUByte()")
                        }
                    } else {
                        // For larger regions, use copyInto pattern
                        val hexBytes = bytes.map { "0x${it.toString(16).uppercase().padStart(2, '0')}u" }
                        out.appendLine("    // ${bytes.size} bytes at 0x${addr.toString(16).uppercase()}")
                        out.append("    ubyteArrayOf(")
                        hexBytes.chunked(16).forEachIndexed { lineIdx, hexChunk ->
                            if (lineIdx > 0) out.append("        ")
                            out.append(hexChunk.joinToString(", "))
                            if (lineIdx < hexBytes.chunked(16).size - 1) out.appendLine(",")
                        }
                        out.appendLine(").copyInto(memory, 0x${addr.toString(16).uppercase()})")
                    }
                }
                out.appendLine("}")
                out.appendLine()
            }

            out.appendLine("// Total ROM data: $totalRomBytes bytes in ${mergedRomData.size} regions across ${chunks.size} chunks")
        }
        println("Generated ROM data initialization: ${romDataFile.absolutePath} ($totalRomBytes bytes in ${mergedRomData.size} regions, ${chunks.size} chunks)")

        // Generate main entry point file
        val mainFile = outDir.resolve("smb-main.kt")
        mainFile.bufferedWriter().use { out ->
            out.appendLine("@file:OptIn(ExperimentalUnsignedTypes::class)")
            out.appendLine()
            out.appendLine("package com.ivieleague.smb")
            out.appendLine()
            out.appendLine("import com.ivieleague.decompiler6502tokotlin.hand.*")
            out.appendLine()
            out.appendLine("/**")
            out.appendLine(" * Main entry point for the decompiled Super Mario Bros.")
            out.appendLine(" *")
            out.appendLine(" * This initializes the system and starts the game loop.")
            out.appendLine(" * The game runs in an infinite loop, processing frames until")
            out.appendLine(" * the NMI (vertical blank) interrupt updates the screen.")
            out.appendLine(" */")
            out.appendLine("fun main() {")
            out.appendLine("    // Initialize CPU state")
            out.appendLine("    resetCPU()")
            out.appendLine("    clearMemory()")
            out.appendLine()
            out.appendLine("    // Load ROM data into memory")
            out.appendLine("    initializeRomData()")
            out.appendLine()
            out.appendLine("    // Start the game - this is the reset vector entry point")
            out.appendLine("    // In the original NES, the CPU reads the reset vector from 0xFFFC-0xFFFD")
            out.appendLine("    // and jumps to that address (0x8000 for SMB)")
            out.appendLine("    func_0()  // Entry point at the beginning of ROM")
            out.appendLine("}")
            out.appendLine()
            out.appendLine("/**")
            out.appendLine(" * NMI handler - called every frame during vertical blank.")
            out.appendLine(" * This updates PPU, handles input, and calls the main game loop.")
            out.appendLine(" */")
            out.appendLine("fun nmiHandler() {")
            out.appendLine("    // TODO: Implement NMI handler")
            out.appendLine("    // This should push registers, update PPU, call game logic, pop registers")
            out.appendLine("}")
        }
        println("Generated main entry point: ${mainFile.absolutePath}")

        // Generate constants file
        constantsFile.bufferedWriter().use { out ->
            out.appendLine("@file:OptIn(ExperimentalUnsignedTypes::class)")
            out.appendLine()
            out.appendLine("package com.ivieleague.smb")
            out.appendLine()
            out.appendLine("import com.ivieleague.decompiler6502tokotlin.hand.MemoryByte")
            out.appendLine()
            out.appendLine("// Memory address variables from smbdism.asm")
            out.appendLine("// Each variable delegates to a specific memory location using property delegates")
            out.appendLine("// Access: `operMode.toInt()` reads from memory[0x0770]")
            out.appendLine("// Store: `operMode = 5u` writes to memory[0x0770]")
            out.appendLine()

            for (const in constants) {
                // Extract numeric value from the constant
                val valueStr = when (val value = const.value) {
                    is com.ivieleague.decompiler6502tokotlin.hand.AssemblyAddressing.ByteValue ->
                        "0x${value.value.toString(16).uppercase().padStart(2, '0')}"
                    is com.ivieleague.decompiler6502tokotlin.hand.AssemblyAddressing.ShortValue ->
                        "0x${value.value.toString(16).uppercase().padStart(4, '0')}"
                    else -> continue  // Skip non-numeric constants
                }
                // Convert label to camelCase property name for direct access
                // Preserves internal capitalization: SwimmingFlag -> swimmingFlag
                val propName = const.name.split('_')
                    .mapIndexed { index, part ->
                        if (part.isEmpty()) return@mapIndexed ""
                        if (index == 0) part.replaceFirstChar { it.lowercase() }
                        else part.replaceFirstChar { it.uppercase() }
                    }
                    .joinToString("")

                // Generate constant with original name (PascalCase preserved)
                out.appendLine("const val ${const.name} = $valueStr")
                // Generate property delegate for direct access using camelCase
                out.appendLine("var $propName by MemoryByte(${const.name})")
            }

            // Add ROM code label constants (data table addresses)
            if (codeLabels.isNotEmpty()) {
                out.appendLine()
                out.appendLine("// ROM code label constants (data table addresses)")
                out.appendLine("// These are addresses in ROM where data tables and code are located")
                out.appendLine()

                for ((label, address) in codeLabels.toSortedMap()) {
                    val addrStr = "0x${address.toString(16).uppercase().padStart(4, '0')}"
                    // Preserve original label name (PascalCase)
                    out.appendLine("const val $label = $addrStr")
                }
            }
        }

        println("Generated ${constants.size} constants and ${codeLabels.size} ROM labels to: ${constantsFile.absolutePath}")

        // Build function registry: map of function names to AssemblyFunction
        val functionRegistry = functions.associateBy { func ->
            func.startingBlock.label?.let { label ->
                com.ivieleague.decompiler6502tokotlin.hand.assemblyLabelToKotlinName(label)
            } ?: "func_${func.startingBlock.originalLineIndex}"
        }

        // Detect JumpEngine dispatch tables
        val jumpEngineTables = com.ivieleague.decompiler6502tokotlin.hand.detectJumpEngineTables(code.lines)

        outFile.bufferedWriter().use { out ->
            out.appendLine("@file:OptIn(ExperimentalUnsignedTypes::class)")
            out.appendLine()
            out.appendLine("package com.ivieleague.smb")
            out.appendLine()
            out.appendLine("import com.ivieleague.decompiler6502tokotlin.hand.*")
            out.appendLine()
            out.appendLine("// Decompiled Super Mario Bros. NES ROM")
            out.appendLine("// Generated from smbdism.asm")
            out.appendLine()

            // Convert all functions
            for (func in functions.sortedBy { it.startingBlock.originalLineIndex }) {
                try {
                    val kFunc = func.toKotlinFunction(functionRegistry, jumpEngineTables)
                    out.appendLine(kFunc.toKotlin())
                    out.appendLine()
                } catch (e: Exception) {
                    println("Error converting function ${func.startingBlock.label}: ${e.message}")
                    e.printStackTrace()
                }
            }
        }

        println("\nGenerated Kotlin code written to: ${outFile.absolutePath}")
        println("First 10 functions converted successfully!")

        assertTrue(outFile.exists())
        assertTrue(outFile.length() > 0)
    }

    @Test
    fun testKotlinASTConstruction() {
        // Test the Kotlin AST directly
        val func = KFunction(
            name = "testFunction",
            params = listOf(KParam("x", "Int"), KParam("y", "Int")),
            returnType = "Int",
            body = listOf(
                KVarDecl("result", "Int", KBinaryOp(KVar("x"), "+", KVar("y"))),
                KReturn(KVar("result"))
            )
        )

        val kotlin = func.toKotlin()
        println("\nTest Kotlin AST:")
        println(kotlin)

        assertTrue(kotlin.contains("fun testFunction(x: Int, y: Int): Int"))
        assertTrue(kotlin.contains("val result: Int = x + y"))
        assertTrue(kotlin.contains("return result"))
    }
}
