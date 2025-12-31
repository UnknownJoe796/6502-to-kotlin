// Debug script to understand NMI timing differences
// Add this code to FullTASValidationTest to track $2000 writes

// In memoryWriteHook, add logging:
interp.memoryWriteHook = { addr, value ->
    when (addr) {
        in 0x2000..0x2007 -> {
            if (addr == 0x2000 && frame < 50) {
                val nmiEnabled = (value.toInt() and 0x80) != 0
                println("Frame $frame: Write $2000 = 0x${value.toString(16)} (NMI=${if (nmiEnabled) "EN" else "DIS"})")
            }
            ppu.write(addr, value, totalCycles);
            true
        }
        // ... rest of hook
    }
}

// Expected output will show when SMB enables/disables NMI during boot
// Compare with FCEUX frame index to find the mismatch
