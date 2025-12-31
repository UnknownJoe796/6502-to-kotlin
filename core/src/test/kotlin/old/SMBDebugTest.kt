package com.ivieleague.decompiler6502tokotlin.hand

import java.io.File
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue

class SMBDebugTest {
    @Test
    fun emitAnnotatedSmb() {
        // Parse the SMB disassembly
        val asmFile = File("smbdism.asm")
        assertTrue(asmFile.exists(), "smbdism.asm must exist at project root")
        val code = asmFile.readText().parseToAssemblyCodeFile()

        // Basic block and function analysis (non-ai)
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        // Ensure outputs directory exists
        val outDir = File("outputs")
        if (!outDir.exists()) outDir.mkdirs()
        val outFile = outDir.resolve("smbdism-debug.txt")

        outFile.bufferedWriter().use { out ->
            // Emit each line with extra comments from analysis
            for(function in functions) {
                val traversed = HashSet<AssemblyBlock>()
                out.appendLine("Function ${function.startingBlock.label}")
                out.appendLine("  Inputs: ${function.inputs}")
                out.appendLine("  Outputs: ${function.outputs}")
                fun emitBlock(block: AssemblyBlock) {
                    if (!traversed.add(block)) return
                    val tabLevel = "  ".repeat(block.dominationDepth)
                    if (block.function != function) {
                        out.appendLine("  ${tabLevel}${block} (as a call, not in function)")
                    } else {
                        out.appendLine("  ${tabLevel}${block}")
//                        out.appendLine("    Fallthrough exit: ${block.fallThroughExit}")
                        if (block.branchExit != null) out.appendLine("  ${tabLevel}  Branch exit: ${block.branchExit}")
                        block.dominates.sortedBy { it.originalLineIndex }.forEach { emitBlock(it) }
//                        block.fallThroughExit?.let { emitBlock(it) }
//                        block.branchExit?.let { emitBlock(it) }
                    }
                }
                emitBlock(function.startingBlock)
                out.appendLine()
            }
        }
    }
    @Test
    fun emitAnnotatedSmbPartial() {
        // Parse the SMB disassembly
        val code = """
            DrawMushroomIcon:
                          ldy #$07                ;read eight bytes to be read by transfer routine
            IconDataRead: lda MushroomIconData,y  ;note that the default position is set for a
                          sta VRAM_Buffer1-1,y    ;1-player game
                          dey
                          bpl IconDataRead
                          lda NumberOfPlayers     ;check number of players
                          beq ExitIcon            ;if set to 1-player game, we're done
                          lda #$24                ;otherwise, load blank tile in 1-player position
                          sta VRAM_Buffer1+3
                          lda #${'$'}ce                ;then load shroom icon tile in 2-player position
                          sta VRAM_Buffer1+5
            ExitIcon:     rts

            ;-------------------------------------------------------------------------------------

            DemoActionData:
                  .db $01, $80, $02, $81, $41, $80, $01
                  .db $42, ${'$'}c2, $02, $80, $41, ${'$'}c1, $41, ${'$'}c1
                  .db $01, ${'$'}c1, $01, $02, $80, $00

            DemoTimingData:
                  .db $9b, $10, $18, $05, $2c, $20, $24
                  .db $15, $5a, $10, $20, $28, $30, $20, $10
                  .db $80, $20, $30, $30, $01, ${'$'}ff, $00

            DemoEngine:
                      ldx DemoAction         ;load current demo action
                      lda DemoActionTimer    ;load current action timer
                      bne DoAction           ;if timer still counting down, skip
                      inx
                      inc DemoAction         ;if expired, increment action, X, and
                      sec                    ;set carry by default for demo over
                      lda DemoTimingData-1,x ;get next timer
                      sta DemoActionTimer    ;store as current timer
                      beq DemoOver           ;if timer already at zero, skip
            DoAction: lda DemoActionData-1,x ;get and perform action (current or next)
                      sta SavedJoypad1Bits
                      dec DemoActionTimer    ;decrement action timer
                      clc                    ;clear carry if demo still going
            DemoOver: rts
        """.trimIndent().parseToAssemblyCodeFile()

        // Basic block and function analysis (non-ai)
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        // Ensure outputs directory exists
        val outDir = File("outputs")
        if (!outDir.exists()) outDir.mkdirs()
        val outFile = outDir.resolve("smbdism-debug-part.txt")

        outFile.bufferedWriter().use { out ->
            // Emit each line with extra comments from analysis
            for(function in functions) {
                val traversed = HashSet<AssemblyBlock>()
                out.appendLine("Function ${function.startingBlock.label}")
                out.appendLine("  Inputs: ${function.inputs}")
                out.appendLine("  Outputs: ${function.inputs}")
                fun emitBlock(block: AssemblyBlock) {
                    if (!traversed.add(block)) return
                    if (block.function != function) {
                        out.appendLine("  ${block} (as a call, not in function)")
                    } else {
                        out.appendLine("  ${block}")
                        out.appendLine("    Fallthrough exit: ${block.fallThroughExit}")
                        out.appendLine("    Branch exit: ${block.branchExit}")
                        block.fallThroughExit?.let { emitBlock(it) }
                        block.branchExit?.let { emitBlock(it) }
                    }
                }
                emitBlock(function.startingBlock)
                out.appendLine()
            }
        }
    }
}
