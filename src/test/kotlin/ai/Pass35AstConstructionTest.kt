package com.ivieleague.decompiler6502tokotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for Pass 35: AST Construction
 *
 * This pass builds a Kotlin-specific AST from the analysis results,
 * creating the structure that will be emitted as actual Kotlin code.
 */
class Pass35AstConstructionTest {

    @Test
    fun `test basic file structure creation`() {
        // Create simple assembly with one function
        val asm = """
            Init:
                LDA #$00
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Init"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        // Build AST
        val ast = cfg.constructKotlinAst(codeFile)

        // Verify file structure
        assertEquals("decompiled", ast.packageName)
        assertEquals("assembly", ast.header.originalSource)

        // Should have bitwise operation imports
        assertTrue(ast.imports.isNotEmpty())
        assertTrue(ast.imports.any { it.packagePath == "kotlin.experimental" })

        // Should have CPU state declarations + function
        assertTrue(ast.declarations.any { it is KotlinAst.Declaration.Property && it.name == "memory" })
        assertTrue(ast.declarations.any { it is KotlinAst.Declaration.Property && it.name == "A" })
        assertTrue(ast.declarations.any { it is KotlinAst.Declaration.Function && it.name == "Init" })
    }

    @Test
    fun `test CPU register declarations`() {
        val asm = "NOP"
        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = emptySet())
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val ast = cfg.constructKotlinAst(codeFile)

        // Find register declarations
        val properties = ast.declarations.filterIsInstance<KotlinAst.Declaration.Property>()

        // Should have all CPU registers
        val registers = properties.filter { it.name in setOf("A", "X", "Y", "SP") }
        assertEquals(4, registers.size)

        // All should be mutable (var) Int with initial values
        registers.forEach { prop ->
            assertTrue(prop.isVar)
            assertEquals(KotlinAst.KotlinType.Int, prop.type)
            assertNotNull(prop.initializer)
        }

        // Check specific initial values
        val regA = properties.find { it.name == "A" }!!
        assertTrue(regA.initializer is KotlinAst.Expression.Literal)
        assertEquals(0, (regA.initializer as KotlinAst.Expression.Literal).value)

        val regSP = properties.find { it.name == "SP" }!!
        assertEquals(0xFF, (regSP.initializer as KotlinAst.Expression.Literal).value)
    }

    @Test
    fun `test CPU flag declarations`() {
        val asm = "NOP"
        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = emptySet())
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val ast = cfg.constructKotlinAst(codeFile)

        // Find flag declarations
        val properties = ast.declarations.filterIsInstance<KotlinAst.Declaration.Property>()
        val flags = properties.filter { it.name in setOf("flagN", "flagV", "flagZ", "flagC") }

        assertEquals(4, flags.size)

        // All should be mutable (var) Boolean initialized to false
        flags.forEach { flag ->
            assertTrue(flag.isVar)
            assertEquals(KotlinAst.KotlinType.Boolean, flag.type)
            assertTrue(flag.initializer is KotlinAst.Expression.Literal)
            assertEquals(false, (flag.initializer as KotlinAst.Expression.Literal).value)
        }
    }

    @Test
    fun `test memory array declaration`() {
        val asm = "NOP"
        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = emptySet())
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val ast = cfg.constructKotlinAst(codeFile)

        // Find memory declaration
        val properties = ast.declarations.filterIsInstance<KotlinAst.Declaration.Property>()
        val memory = properties.find { it.name == "memory" }

        assertNotNull(memory)
        assertFalse(memory!!.isVar) // val, not var
        assertTrue(memory.type is KotlinAst.KotlinType.ByteArray)

        // Should have initializer: ByteArray(65536)
        assertNotNull(memory.initializer)
        assertTrue(memory.initializer is KotlinAst.Expression.Call)
        val call = memory.initializer as KotlinAst.Expression.Call
        assertEquals("ByteArray", call.name)
        assertEquals(1, call.arguments.size)
    }

    @Test
    fun `test function declarations created`() {
        val asm = """
            Init:
                LDA #$00
                RTS

            ProcessData:
                LDA ${'$'}0200
                ADC #${'$'}01
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Init", "ProcessData"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val ast = cfg.constructKotlinAst(codeFile)

        // Should have both functions
        val functions = ast.declarations.filterIsInstance<KotlinAst.Declaration.Function>()
        assertEquals(2, functions.size)

        val initFunc = functions.find { it.name == "Init" }
        val processFunc = functions.find { it.name == "ProcessData" }

        assertNotNull(initFunc)
        assertNotNull(processFunc)

        // Functions should have null return type (will be inferred by Kotlin)
        assertNull(initFunc!!.returnType)
        assertNull(processFunc!!.returnType)

        // Functions should have empty parameters (for now)
        assertTrue(initFunc.parameters.isEmpty())
        assertTrue(processFunc.parameters.isEmpty())

        // Functions should have bodies
        assertNotNull(initFunc.body)
        assertNotNull(processFunc.body)
    }

    @Test
    fun `test type mapping from InferredType to KotlinType`() {
        // Test the type mapping directly
        assertEquals(KotlinAst.KotlinType.UByte, mapInferredTypeToKotlinType(InferredType.UInt8()))
        assertEquals(KotlinAst.KotlinType.UShort, mapInferredTypeToKotlinType(InferredType.UInt16()))
        assertEquals(KotlinAst.KotlinType.Int, mapInferredTypeToKotlinType(InferredType.Int8()))
        assertEquals(KotlinAst.KotlinType.Boolean, mapInferredTypeToKotlinType(InferredType.Boolean))

        // Counter and Index should map to Int
        assertEquals(KotlinAst.KotlinType.Int, mapInferredTypeToKotlinType(InferredType.Counter))
        assertEquals(KotlinAst.KotlinType.Int, mapInferredTypeToKotlinType(InferredType.Index))
    }

    @Test
    fun `test variable name sanitization`() {
        // Test sanitization
        assertEquals("my_var", "my-var".sanitizeKotlinIdentifier())
        assertEquals("_123var", "123var".sanitizeKotlinIdentifier())
        assertEquals("`if`", "if".sanitizeKotlinIdentifier()) // Kotlin keyword gets backticks
        assertEquals("`class`", "class".sanitizeKotlinIdentifier())
        assertEquals("player_X", "player_X".sanitizeKotlinIdentifier())
        assertEquals("valid123", "valid123".sanitizeKotlinIdentifier())
    }

    @Test
    fun `test AST structure is complete and well-formed`() {
        val asm = """
            Main:
                LDX #${'$'}00
            Loop:
                LDA ${'$'}0200,X
                STA ${'$'}0300,X
                INX
                CPX #${'$'}10
                BNE Loop
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Main"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val ast = cfg.constructKotlinAst(codeFile)

        // Verify overall structure
        assertNotNull(ast.packageName)
        assertNotNull(ast.header)
        assertNotNull(ast.declarations)

        // Should have at least: memory, 4 registers, 4 flags, 1 function = 10 declarations minimum
        assertTrue(ast.declarations.size >= 10)

        // All declarations should be well-formed
        ast.declarations.forEach { decl ->
            when (decl) {
                is KotlinAst.Declaration.Property -> {
                    assertNotNull(decl.name)
                    assertNotNull(decl.type)
                    assertTrue(decl.name.isNotEmpty())
                }
                is KotlinAst.Declaration.Function -> {
                    assertNotNull(decl.name)
                    // returnType can be null (Kotlin will infer)
                    assertNotNull(decl.parameters)
                    assertNotNull(decl.body)
                    assertTrue(decl.name.isNotEmpty())
                }
                else -> fail("Unexpected declaration type: ${decl::class}")
            }
        }

        // Function should have a body
        val function = ast.declarations.filterIsInstance<KotlinAst.Declaration.Function>().first()
        assertTrue(function.body.statements.isNotEmpty())
    }
}
