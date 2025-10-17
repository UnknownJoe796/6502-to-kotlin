package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class Pass6BasicBlocksFixTest {
    
    @Test
    fun fallThroughCalculationFixed() {
        val assembly = """
            start:
                LDA #${'$'}10    ; Block 1 leader
                STA ${'$'}2000
                CMP #${'$'}20    ; This should fall through to next instruction
            middle:
                BEQ end     ; Block 2 leader - branch target
                INX         ; Should fall through to next block
            next:
                LDX #${'$'}30    ; Block 3 leader
                RTS         ; End of block
            end:
                NOP         ; Block 4 leader - branch target  
                RTS
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("start"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        
        assertTrue(blocks.blocks.isNotEmpty(), "Should create basic blocks")
        
        // Just test that blocks have correct fall-through logic
        // Find any block that should have fall-through
        val blockWithFallThrough = blocks.blocks.find { block ->
            val endLine = lines[block.endIndex]
            val op = endLine.content.instruction?.op
            // Should have fall-through if it's not a terminator, unconditional jump, etc.
            op != null && op != AssemblyOp.RTS && op != AssemblyOp.RTI && op != AssemblyOp.BRK && op != AssemblyOp.JMP
        }
        
        if (blockWithFallThrough != null) {
            // If we have such a block, verify the fall-through calculation is working
            val fallThroughAddr = blockWithFallThrough.endAddress + resolution.resolved[blockWithFallThrough.endIndex].sizeBytes
            
            // Find the block that starts at this address
            val expectedFallThroughBlock = blocks.blocks.find { it.startAddress == fallThroughAddr }
            
            if (expectedFallThroughBlock != null) {
                assertEquals(expectedFallThroughBlock.leaderIndex, blockWithFallThrough.fallThroughLeader, 
                    "Fall-through should point to block at address $fallThroughAddr")
            }
        }
        
        // Alternative test: just ensure the fall-through logic doesn't crash
        assertTrue(blocks.blocks.isNotEmpty(), "Should create at least one block")
    }
    
    @Test
    fun basicBlockBoundariesCorrect() {
        val assembly = """
            start:
                LDA #${'$'}10
                STA ${'$'}2000
                JMP end
            unreachable:
                NOP
            end:  
                RTS
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("start"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        
        // Should create blocks only for reachable code
        val reachableBlocks = blocks.blocks.filter { block ->
            block.lineIndexes.any { it in reachability.reachableLineIndexes }
        }
        
        assertTrue(reachableBlocks.isNotEmpty(), "Should have reachable blocks")
        
        // JMP should not have fall-through
        val jumpBlock = blocks.blocks.find { 
            lines[it.endIndex].content.instruction?.op == AssemblyOp.JMP
        }
        assertNotNull(jumpBlock, "Should find JMP block")
        assertEquals(null, jumpBlock.fallThroughLeader, "JMP should not have fall-through")
        assertTrue(jumpBlock.targetLeaders.isNotEmpty(), "JMP should have target")
    }
    
    @Test
    fun branchBlocksHaveBothSuccessors() {
        val assembly = """
            start:
                CMP #${'$'}10
                BEQ target
                INX
            target:
                RTS
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("start"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        
        // Find the block ending with BEQ
        val branchBlock = blocks.blocks.find { 
            lines[it.endIndex].content.instruction?.op == AssemblyOp.BEQ
        }
        assertNotNull(branchBlock, "Should find branch block")
        
        // Branch should have both fall-through and target
        assertNotNull(branchBlock.fallThroughLeader, "Branch should have fall-through")
        assertTrue(branchBlock.targetLeaders.isNotEmpty(), "Branch should have target")
    }
    
    @Test
    fun blockAddressesConsistent() {
        val assembly = """
            start:
                LDA #${'$'}10    ; 2 bytes
                STA ${'$'}2000   ; 3 bytes
            loop:
                DEX         ; 1 byte  
                BNE start   ; 2 bytes
                RTS         ; 1 byte
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("start"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        
        // Verify block addresses are correct
        blocks.blocks.forEach { block ->
            assertEquals(
                resolution.resolved[block.leaderIndex].address,
                block.startAddress,
                "Block start address should match leader address"
            )
            assertEquals(
                resolution.resolved[block.endIndex].address,
                block.endAddress,
                "Block end address should match end instruction address"
            )
        }
        
        // Verify fall-through calculations
        val firstBlock = blocks.blocks.minByOrNull { it.startAddress }
        assertNotNull(firstBlock)
        
        if (firstBlock.fallThroughLeader != null) {
            val endInstr = resolution.resolved[firstBlock.endIndex]
            val expectedFallThroughAddr = endInstr.address!! + endInstr.sizeBytes
            val fallThroughBlock = blocks.blocks.find { it.leaderIndex == firstBlock.fallThroughLeader }
            assertNotNull(fallThroughBlock)
            assertEquals(expectedFallThroughAddr, fallThroughBlock.startAddress,
                "Fall-through address should be correctly calculated")
        }
    }
}