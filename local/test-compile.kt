package com.ivieleague.smb

// Decompiled Super Mario Bros. NES ROM
// Generated from smbdism.asm

// Decompiled from @0
fun func_0() {
    var X: Int = 0
    //> ;SMBDIS.ASM - A COMPREHENSIVE SUPER MARIO BROS. DISASSEMBLY
    //> ;by doppelganger (doppelheathen@gmail.com)
    //> ;This file is provided for your own use as-is.  It will require the character rom data
    //> ;and an iNES file header to get it to work.
    //> ;There are so many people I have to thank for this, that taking all the credit for
    //> ;myself would be an unforgivable act of arrogance. Without their help this would
    //> ;probably not be possible.  So I thank all the peeps in the nesdev scene whose insight into
    //> ;the 6502 and the NES helped me learn how it works (you guys know who you are, there's no
    //> ;way I could have done this without your help), as well as the authors of x816 and SMB
    //> ;Utility, and the reverse-engineers who did the original Super Mario Bros. Hacking Project,
    //> ;which I compared notes with but did not copy from.  Last but certainly not least, I thank
    //> ;Nintendo for creating this game and the NES, without which this disassembly would
    //> ;only be theory.
    //> ;Assembles with x816.
    //> ;-------------------------------------------------------------------------------------
    //> ;DEFINES
    //> ;NES specific hardware defines
    //> PPU_CTRL_REG1         = $2000
    //> PPU_CTRL_REG2         = $2001
    //> PPU_STATUS            = $2002
    //> PPU_SPR_ADDR          = $2003
    //> PPU_SPR_DATA          = $2004
    //> PPU_SCROLL_REG        = $2005
    //> PPU_ADDRESS           = $2006
    //> PPU_DATA              = $2007
    //> SND_REGISTER          = $4000
    //> SND_SQUARE1_REG       = $4000
    //> SND_SQUARE2_REG       = $4004
    //> SND_TRIANGLE_REG      = $4008
    //> SND_NOISE_REG         = $400c
    //> SND_DELTA_REG         = $4010
    //> SND_MASTERCTRL_REG    = $4015
    //> SPR_DMA               = $4014
    //> JOYPAD_PORT           = $4016
    //> JOYPAD_PORT1          = $4016
    //> JOYPAD_PORT2          = $4017
    //> ; GAME SPECIFIC DEFINES
    //> ObjectOffset          = $08
    //> FrameCounter          = $09
    //> SavedJoypadBits       = $06fc
    //> SavedJoypad1Bits      = $06fc
    //> SavedJoypad2Bits      = $06fd
    //> JoypadBitMask         = $074a
    //> JoypadOverride        = $0758
    //> A_B_Buttons           = $0a
    //> PreviousA_B_Buttons   = $0d
    //> Up_Down_Buttons       = $0b
    //> Left_Right_Buttons    = $0c
    //> GameEngineSubroutine  = $0e
    //> Mirror_PPU_CTRL_REG1  = $0778
    //> Mirror_PPU_CTRL_REG2  = $0779
    //> OperMode              = $0770
    //> OperMode_Task         = $0772
    //> ScreenRoutineTask     = $073c
    //> GamePauseStatus       = $0776
    //> GamePauseTimer        = $0777
    //> DemoAction            = $0717
    //> DemoActionTimer       = $0718
    //> TimerControl          = $0747
    //> IntervalTimerControl  = $077f
    //> Timers                = $0780
    //> SelectTimer           = $0780
    //> PlayerAnimTimer       = $0781
    //> JumpSwimTimer         = $0782
    //> RunningTimer          = $0783
    //> BlockBounceTimer      = $0784
    //> SideCollisionTimer    = $0785
    //> JumpspringTimer       = $0786
    //> GameTimerCtrlTimer    = $0787
    //> ClimbSideTimer        = $0789
    //> EnemyFrameTimer       = $078a
    //> FrenzyEnemyTimer      = $078f
    //> BowserFireBreathTimer = $0790
    //> StompTimer            = $0791
    //> AirBubbleTimer        = $0792
    //> ScrollIntervalTimer   = $0795
    //> EnemyIntervalTimer    = $0796
    //> BrickCoinTimer        = $079d
    //> InjuryTimer           = $079e
    //> StarInvincibleTimer   = $079f
    //> ScreenTimer           = $07a0
    //> WorldEndTimer         = $07a1
    //> DemoTimer             = $07a2
    //> Sprite_Data           = $0200
    //> Sprite_Y_Position     = $0200
    //> Sprite_Tilenumber     = $0201
    //> Sprite_Attributes     = $0202
    //> Sprite_X_Position     = $0203
    //> ScreenEdge_PageLoc    = $071a
    //> ScreenEdge_X_Pos      = $071c
    //> ScreenLeft_PageLoc    = $071a
    //> ScreenRight_PageLoc   = $071b
