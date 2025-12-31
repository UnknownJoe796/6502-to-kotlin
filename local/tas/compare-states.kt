import java.io.File

// SMB memory addresses
object SMB {
    const val OperMode = 0x0770
    const val WorldNumber = 0x075F
    const val LevelNumber = 0x0760
    const val Player_X_Position = 0x0086
    const val Player_Y_Position = 0x00CE
    const val ScreenLeft_PageLoc = 0x071A
    const val FrameCounter = 0x09
    const val Player_X_Speed = 0x0057
    const val Player_State = 0x001D
    const val ScrollAmount = 0x073F
    const val ScrollFractional = 0x0768
    const val HorizontalScroll = 0x073F
    const val ColumnPos = 0x06DE  // Screen column position
    const val SideCollisionTimer = 0x0490  // Collision timer
}

fun main() {
    val fceuxRam = File("local/tas/fceux-full-ram.bin").readBytes()
    val interpRam = File("local/tas/interpreter-full-ram.bin").readBytes()
    
    val fceuxMaxFrame = fceuxRam.size / 2048
    val interpMaxFrame = interpRam.size / 2048
    
    println("=== Frame-by-frame comparison (frames 40-100) ===")
    println("Looking for where game states diverge...\n")
    
    // Find gameplay start frames
    var fceuxGameStart = -1
    var interpGameStart = -1
    
    for (f in 0 until minOf(100, fceuxMaxFrame)) {
        val mode = fceuxRam[f * 2048 + SMB.OperMode].toInt() and 0xFF
        if (mode == 1 && fceuxGameStart < 0) {
            fceuxGameStart = f
            println("FCEUX enters gameplay at frame $f")
            break
        }
    }
    
    for (f in 0 until minOf(100, interpMaxFrame)) {
        val mode = interpRam[f * 2048 + SMB.OperMode].toInt() and 0xFF
        if (mode == 1 && interpGameStart < 0) {
            interpGameStart = f
            println("Interpreter enters gameplay at frame $f")
            break
        }
    }
    
    println("\nFrame offset = ${interpGameStart - fceuxGameStart} (interp - fceux)\n")
    
    // Compare aligned frames after gameplay start
    println("=== Comparing aligned frames (FCEUX+$fceuxGameStart vs INTERP+$interpGameStart) ===")
    println("Frame | FCEUX X/Page/Spd | INTERP X/Page/Spd | Match?")
    println("------|-----------------|-------------------|-------")
    
    var firstDivergence = -1
    
    for (gameFrame in 0..300) {
        val fFrame = fceuxGameStart + gameFrame
        val iFrame = interpGameStart + gameFrame
        
        if (fFrame >= fceuxMaxFrame || iFrame >= interpMaxFrame) break
        
        val fOff = fFrame * 2048
        val iOff = iFrame * 2048
        
        val fX = fceuxRam[fOff + SMB.Player_X_Position].toInt() and 0xFF
        val fPage = fceuxRam[fOff + SMB.ScreenLeft_PageLoc].toInt() and 0xFF
        val fSpd = fceuxRam[fOff + SMB.Player_X_Speed].toInt().let { if (it > 127) it - 256 else it }
        val fFC = fceuxRam[fOff + SMB.FrameCounter].toInt() and 0xFF
        val fScroll = fceuxRam[fOff + SMB.HorizontalScroll].toInt() and 0xFF
        
        val iX = interpRam[iOff + SMB.Player_X_Position].toInt() and 0xFF
        val iPage = interpRam[iOff + SMB.ScreenLeft_PageLoc].toInt() and 0xFF
        val iSpd = interpRam[iOff + SMB.Player_X_Speed].toInt().let { if (it > 127) it - 256 else it }
        val iFC = interpRam[iOff + SMB.FrameCounter].toInt() and 0xFF
        val iScroll = interpRam[iOff + SMB.HorizontalScroll].toInt() and 0xFF
        
        val match = (fX == iX && fPage == iPage && fSpd == iSpd)
        
        if (!match && firstDivergence < 0) {
            firstDivergence = gameFrame
        }
        
        // Print every 10 frames or around divergence
        if (gameFrame % 10 == 0 || (firstDivergence >= 0 && gameFrame in (firstDivergence-5)..(firstDivergence+20))) {
            println("${gameFrame.toString().padStart(5)} | X=$fX pg=$fPage spd=$fSpd scroll=$fScroll | X=$iX pg=$iPage spd=$iSpd scroll=$iScroll | ${if (match) "✓" else "✗"}")
        }
    }
    
    if (firstDivergence >= 0) {
        println("\n*** First divergence at game frame $firstDivergence ***")
        println("*** (FCEUX frame ${fceuxGameStart + firstDivergence}, Interp frame ${interpGameStart + firstDivergence}) ***")
    }
}

main()
