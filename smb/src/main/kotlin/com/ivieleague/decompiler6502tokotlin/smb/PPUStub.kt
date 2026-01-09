package com.ivieleague.decompiler6502tokotlin.smb

/**
 * Minimal PPU stub for TAS validation.
 * 
 * The NES PPU has specific behavior that games rely on for timing.
 * For TAS validation, we need to emulate the essential behavior
 * without actual rendering.
 * 
 * Key registers:
 * - $2000 (PPUCTRL): Control register
 * - $2001 (PPUMASK): Mask register  
 * - $2002 (PPUSTATUS): Status (read clears VBlank flag)
 * - $2003 (OAMADDR): OAM address
 * - $2004 (OAMDATA): OAM data
 * - $2005 (PPUSCROLL): Scroll position (write x, then y)
 * - $2006 (PPUADDR): VRAM address (write high, then low)
 * - $2007 (PPUDATA): VRAM data
 */
class PPUStub {
    // PPU registers
    var ppuCtrl: UByte = 0u      // $2000
    var ppuMask: UByte = 0u      // $2001
    var ppuStatus: UByte = 0u    // $2002 - bit 7 = VBlank
    var oamAddr: UByte = 0u      // $2003
    var ppuScroll: UByte = 0u    // $2005
    var ppuAddr: UShort = 0u     // $2006 (16-bit)
    
    // Internal state
    private var vblankFlag: Boolean = false
    private var sprite0HitFlag: Boolean = false  // Sprite 0 hit detection (bit 6 of PPUSTATUS)
    private var ppuAddrLatch: Boolean = false  // false = high byte, true = low byte
    private var ppuScrollLatch: Boolean = false  // false = X scroll, true = Y scroll
    private var scrollX: UByte = 0u
    private var scrollY: UByte = 0u

    // Simulate PPU rendering progression for sprite 0 hit timing
    private var ppuStatusReadsSinceVBlank: Int = 0
    
    // VRAM (8KB for name tables + pattern tables)
    private val vram = UByteArray(0x4000)
    
    // OAM (256 bytes for sprite data)
    private val oam = UByteArray(256)
    
    // Frame counter for timing
    var frameCount: Int = 0
        private set
    
    /**
     * Read from PPU register
     */
    fun readRegister(address: Int): UByte {
        return when (address and 0x2007) {
            0x2002 -> {
                // Simulate sprite 0 hit becoming available after a few PPU_STATUS reads
                // This simulates the PPU rendering progressing while code waits in busy loops
                if (!sprite0HitFlag) {
                    ppuStatusReadsSinceVBlank++
                    // After a few reads (simulating scanlines progressing), set sprite 0 hit
                    if (ppuStatusReadsSinceVBlank >= 3) {
                        sprite0HitFlag = true
                        ppuStatus = (ppuStatus.toInt() or 0x40).toUByte()  // Set sprite 0 hit (bit 6)
                    }
                }

                // PPUSTATUS - reading clears VBlank flag and resets address latch
                val status = ppuStatus
                ppuStatus = (ppuStatus.toInt() and 0x7F).toUByte()  // Clear VBlank (bit 7)
                vblankFlag = false
                ppuAddrLatch = false
                ppuScrollLatch = false
                status
            }
            0x2004 -> {
                // OAMDATA
                oam[oamAddr.toInt()]
            }
            0x2007 -> {
                // PPUDATA - read from VRAM
                val data = vram[ppuAddr.toInt() and 0x3FFF]
                // Auto-increment address
                ppuAddr = ((ppuAddr.toInt() + if ((ppuCtrl.toInt() and 0x04) != 0) 32 else 1) and 0x3FFF).toUShort()
                data
            }
            else -> 0u
        }
    }
    
    /**
     * Write to PPU register
     */
    fun writeRegister(address: Int, value: UByte) {
        when (address and 0x2007) {
            0x2000 -> {
                // PPUCTRL
                ppuCtrl = value
            }
            0x2001 -> {
                // PPUMASK
                ppuMask = value
            }
            0x2003 -> {
                // OAMADDR
                oamAddr = value
            }
            0x2004 -> {
                // OAMDATA - write and increment address
                oam[oamAddr.toInt()] = value
                oamAddr = ((oamAddr.toInt() + 1) and 0xFF).toUByte()
            }
            0x2005 -> {
                // PPUSCROLL - alternates between X and Y
                if (!ppuScrollLatch) {
                    scrollX = value
                } else {
                    scrollY = value
                }
                ppuScrollLatch = !ppuScrollLatch
            }
            0x2006 -> {
                // PPUADDR - write high byte, then low byte
                if (!ppuAddrLatch) {
                    ppuAddr = ((value.toInt() shl 8) or (ppuAddr.toInt() and 0x00FF)).toUShort()
                } else {
                    ppuAddr = ((ppuAddr.toInt() and 0xFF00) or value.toInt()).toUShort()
                }
                ppuAddrLatch = !ppuAddrLatch
            }
            0x2007 -> {
                // PPUDATA - write to VRAM
                vram[ppuAddr.toInt() and 0x3FFF] = value
                // Auto-increment address
                ppuAddr = ((ppuAddr.toInt() + if ((ppuCtrl.toInt() and 0x04) != 0) 32 else 1) and 0x3FFF).toUShort()
            }
        }
    }
    
    /**
     * OAM DMA transfer (triggered by write to $4014)
     */
    fun oamDma(data: UByteArray, startIndex: Int = 0) {
        for (i in 0 until 256) {
            oam[(oamAddr.toInt() + i) and 0xFF] = data[startIndex + i]
        }
    }
    
    /**
     * Begin VBlank period (called at end of visible frame)
     * Sets the VBlank flag in PPUSTATUS and clears sprite 0 hit
     */
    fun beginVBlank() {
        vblankFlag = true
        ppuStatus = (ppuStatus.toInt() or 0x80).toUByte()  // Set VBlank (bit 7)

        // Clear sprite 0 hit at start of VBlank (bit 6)
        sprite0HitFlag = false
        ppuStatus = (ppuStatus.toInt() and 0xBF).toUByte()

        // Reset read counter for sprite 0 hit simulation
        ppuStatusReadsSinceVBlank = 0
    }

    /**
     * End VBlank period (called at start of new frame)
     * Sets sprite 0 hit flag to simulate detection during rendering
     */
    fun endVBlank() {
        vblankFlag = false
        ppuStatus = (ppuStatus.toInt() and 0x7F).toUByte()  // Clear VBlank (bit 7)

        // Set sprite 0 hit flag when rendering begins
        // In real hardware this happens when sprite 0 overlaps background
        // For TAS validation, we always set it (sprite 0 is always present)
        sprite0HitFlag = true
        ppuStatus = (ppuStatus.toInt() or 0x40).toUByte()  // Set sprite 0 hit (bit 6)

        frameCount++
    }
    
    /**
     * Check if NMI should be triggered
     * NMI fires when VBlank starts AND NMI enable bit is set in PPUCTRL
     */
    fun shouldTriggerNmi(): Boolean {
        return vblankFlag && (ppuCtrl.toInt() and 0x80) != 0
    }
    
    /**
     * Get current scroll position
     */
    fun getScroll(): Pair<Int, Int> = Pair(scrollX.toInt(), scrollY.toInt())
    
    /**
     * Reset PPU state
     */
    fun reset() {
        ppuCtrl = 0u
        ppuMask = 0u
        ppuStatus = 0u
        oamAddr = 0u
        ppuAddr = 0u
        ppuAddrLatch = false
        ppuScrollLatch = false
        scrollX = 0u
        scrollY = 0u
        vblankFlag = false
        sprite0HitFlag = false
        ppuStatusReadsSinceVBlank = 0
        frameCount = 0
        vram.fill(0u)
        oam.fill(0u)
    }
}
