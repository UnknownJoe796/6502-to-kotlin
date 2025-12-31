@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.smb

import com.ivieleague.decompiler6502tokotlin.hand.*

// Debug flag for tracing gameCoreRoutine
private const val DEBUG_GAME_CORE = false
// Debug flag for tracing player entrance setup
private const val DEBUG_ENTRANCE = false

// Helper function for RunEnemyObjectsCore (extracted from unreachable code in enemiesAndLoopsCore)
// This runs the appropriate handler based on Enemy_ID
private fun runEnemyObjectsCoreInline(
    X: Int,
    objectOffset: Int,
    enemyFlag: MemoryByteArray,
    enemyId: MemoryByteArray,
    enemyState: MemoryByteArray,
    enemyYHighpos: MemoryByteArray,
    enemyYPosition: MemoryByteArray,
    enemyXPosition: MemoryByteArray,
    enemyPageloc: MemoryByteArray
) {
    //> RunEnemyObjectsCore:
    //> ldx ObjectOffset  ;get offset for enemy object buffer
    val offset = objectOffset
    //> lda #$00          ;load value 0 for jump engine by default
    var jumpIndex = 0
    //> ldy Enemy_ID,x
    val id = enemyId[offset]
    //> cpy #$15          ;if enemy object < $15, use default value
    //> bcc JmpEO
    if (id >= 0x15) {
        //> tya               ;otherwise subtract $14 from the value and use
        //> sbc #$14          ;as value for jump engine
        jumpIndex = (id - 0x14) and 0xFF
    }
    //> JmpEO: jsr JumpEngine
    when (jumpIndex) {
        0 -> runNormalEnemies()
        1 -> runBowserFlame()
        2 -> runFireworks()
        3 -> noRunCode()
        4 -> noRunCode()
        5 -> noRunCode()
        6 -> noRunCode()
        7 -> runFirebarObj()
        8 -> runFirebarObj()
        9 -> runFirebarObj()
        10 -> runFirebarObj()
        11 -> runFirebarObj()
        12 -> runFirebarObj()
        13 -> runFirebarObj()
        14 -> runFirebarObj()
        15 -> noRunCode()
        16 -> runLargePlatform()
        17 -> runLargePlatform()
        18 -> runLargePlatform()
        19 -> runLargePlatform()
        20 -> runLargePlatform()
        21 -> runLargePlatform()
        22 -> runLargePlatform()
        23 -> runSmallPlatform()
        24 -> runSmallPlatform()
        25 -> runBowser()
        26 -> powerUpObjHandler()
        27 -> vineObjectHandler()
        28 -> noRunCode()
        29 -> runStarFlagObj()
        30 -> jumpspringHandler()
        31 -> noRunCode()
        32 -> warpZoneObject()
        33 -> runRetainerObj()
        else -> { /* Unknown JumpEngine index */ }
    }
}

// Decompiled Super Mario Bros. NES ROM
// Generated from smbdism.asm

// Decompiled from @0
fun func_0() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var disableScreenFlag by MemoryByte(DisableScreenFlag)
    var mirrorPpuCtrlReg1 by MemoryByte(Mirror_PPU_CTRL_REG1)
    var operMode by MemoryByte(OperMode)
    var ppuCtrlReg1 by MemoryByte(PPU_CTRL_REG1)
    var ppuCtrlReg2 by MemoryByte(PPU_CTRL_REG2)
    var ppuStatus by MemoryByte(PPU_STATUS)
    var pseudoRandomBitReg by MemoryByte(PseudoRandomBitReg)
    var sndMasterctrlReg by MemoryByte(SND_MASTERCTRL_REG)
    var warmBootValidation by MemoryByte(WarmBootValidation)
    val sndDeltaReg by MemoryByteIndexed(SND_DELTA_REG)
    val topScoreDisplay by MemoryByteIndexed(TopScoreDisplay)
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
    //> ScreenLeft_X_Pos      = $071c
    //> ScreenRight_X_Pos     = $071d
    //> PlayerFacingDir       = $33
    //> DestinationPageLoc    = $34
    //> VictoryWalkControl    = $35
    //> ScrollFractional      = $0768
    //> PrimaryMsgCounter     = $0719
    //> SecondaryMsgCounter   = $0749
    //> HorizontalScroll      = $073f
    //> VerticalScroll        = $0740
    //> ScrollLock            = $0723
    //> ScrollThirtyTwo       = $073d
    //> Player_X_Scroll       = $06ff
    //> Player_Pos_ForScroll  = $0755
    //> ScrollAmount          = $0775
    //> AreaData              = $e7
    //> AreaDataLow           = $e7
    //> AreaDataHigh          = $e8
    //> EnemyData             = $e9
    //> EnemyDataLow          = $e9
    //> EnemyDataHigh         = $ea
    //> AreaParserTaskNum     = $071f
    //> ColumnSets            = $071e
    //> CurrentPageLoc        = $0725
    //> CurrentColumnPos      = $0726
    //> BackloadingFlag       = $0728
    //> BehindAreaParserFlag  = $0729
    //> AreaObjectPageLoc     = $072a
    //> AreaObjectPageSel     = $072b
    //> AreaDataOffset        = $072c
    //> AreaObjOffsetBuffer   = $072d
    //> AreaObjectLength      = $0730
    //> StaircaseControl      = $0734
    //> AreaObjectHeight      = $0735
    //> MushroomLedgeHalfLen  = $0736
    //> EnemyDataOffset       = $0739
    //> EnemyObjectPageLoc    = $073a
    //> EnemyObjectPageSel    = $073b
    //> MetatileBuffer        = $06a1
    //> BlockBufferColumnPos  = $06a0
    //> CurrentNTAddr_Low     = $0721
    //> CurrentNTAddr_High    = $0720
    //> AttributeBuffer       = $03f9
    //> LoopCommand           = $0745
    //> DisplayDigits         = $07d7
    //> TopScoreDisplay       = $07d7
    //> ScoreAndCoinDisplay   = $07dd
    //> PlayerScoreDisplay    = $07dd
    //> GameTimerDisplay      = $07f8
    //> DigitModifier         = $0134
    //> VerticalFlipFlag      = $0109
    //> FloateyNum_Control    = $0110
    //> ShellChainCounter     = $0125
    //> FloateyNum_Timer      = $012c
    //> FloateyNum_X_Pos      = $0117
    //> FloateyNum_Y_Pos      = $011e
    //> FlagpoleFNum_Y_Pos    = $010d
    //> FlagpoleFNum_YMFDummy = $010e
    //> FlagpoleScore         = $010f
    //> FlagpoleCollisionYPos = $070f
    //> StompChainCounter     = $0484
    //> VRAM_Buffer1_Offset   = $0300
    //> VRAM_Buffer1          = $0301
    //> VRAM_Buffer2_Offset   = $0340
    //> VRAM_Buffer2          = $0341
    //> VRAM_Buffer_AddrCtrl  = $0773
    //> Sprite0HitDetectFlag  = $0722
    //> DisableScreenFlag     = $0774
    //> DisableIntermediate   = $0769
    //> ColorRotateOffset     = $06d4
    //> TerrainControl        = $0727
    //> AreaStyle             = $0733
    //> ForegroundScenery     = $0741
    //> BackgroundScenery     = $0742
    //> CloudTypeOverride     = $0743
    //> BackgroundColorCtrl   = $0744
    //> AreaType              = $074e
    //> AreaAddrsLOffset      = $074f
    //> AreaPointer           = $0750
    //> PlayerEntranceCtrl    = $0710
    //> GameTimerSetting      = $0715
    //> AltEntranceControl    = $0752
    //> EntrancePage          = $0751
    //> NumberOfPlayers       = $077a
    //> WarpZoneControl       = $06d6
    //> ChangeAreaTimer       = $06de
    //> MultiLoopCorrectCntr  = $06d9
    //> MultiLoopPassCntr     = $06da
    //> FetchNewGameTimerFlag = $0757
    //> GameTimerExpiredFlag  = $0759
    //> PrimaryHardMode       = $076a
    //> SecondaryHardMode     = $06cc
    //> WorldSelectNumber     = $076b
    //> WorldSelectEnableFlag = $07fc
    //> ContinueWorld         = $07fd
    //> CurrentPlayer         = $0753
    //> PlayerSize            = $0754
    //> PlayerStatus          = $0756
    //> OnscreenPlayerInfo    = $075a
    //> NumberofLives         = $075a ;used by current player
    //> HalfwayPage           = $075b
    //> LevelNumber           = $075c ;the actual dash number
    //> Hidden1UpFlag         = $075d
    //> CoinTally             = $075e
    //> WorldNumber           = $075f
    //> AreaNumber            = $0760 ;internal number used to find areas
    //> CoinTallyFor1Ups      = $0748
    //> OffscreenPlayerInfo   = $0761
    //> OffScr_NumberofLives  = $0761 ;used by offscreen player
    //> OffScr_HalfwayPage    = $0762
    //> OffScr_LevelNumber    = $0763
    //> OffScr_Hidden1UpFlag  = $0764
    //> OffScr_CoinTally      = $0765
    //> OffScr_WorldNumber    = $0766
    //> OffScr_AreaNumber     = $0767
    //> BalPlatformAlignment  = $03a0
    //> Platform_X_Scroll     = $03a1
    //> PlatformCollisionFlag = $03a2
    //> YPlatformTopYPos      = $0401
    //> YPlatformCenterYPos   = $58
    //> BrickCoinTimerFlag    = $06bc
    //> StarFlagTaskControl   = $0746
    //> PseudoRandomBitReg    = $07a7
    //> WarmBootValidation    = $07ff
    //> SprShuffleAmtOffset   = $06e0
    //> SprShuffleAmt         = $06e1
    //> SprDataOffset         = $06e4
    //> Player_SprDataOffset  = $06e4
    //> Enemy_SprDataOffset   = $06e5
    //> Block_SprDataOffset   = $06ec
    //> Alt_SprDataOffset     = $06ec
    //> Bubble_SprDataOffset  = $06ee
    //> FBall_SprDataOffset   = $06f1
    //> Misc_SprDataOffset    = $06f3
    //> SprDataOffset_Ctrl    = $03ee
    //> Player_State          = $1d
    //> Enemy_State           = $1e
    //> Fireball_State        = $24
    //> Block_State           = $26
    //> Misc_State            = $2a
    //> Player_MovingDir      = $45
    //> Enemy_MovingDir       = $46
    //> SprObject_X_Speed     = $57
    //> Player_X_Speed        = $57
    //> Enemy_X_Speed         = $58
    //> Fireball_X_Speed      = $5e
    //> Block_X_Speed         = $60
    //> Misc_X_Speed          = $64
    //> Jumpspring_FixedYPos  = $58
    //> JumpspringAnimCtrl    = $070e
    //> JumpspringForce       = $06db
    //> SprObject_PageLoc     = $6d
    //> Player_PageLoc        = $6d
    //> Enemy_PageLoc         = $6e
    //> Fireball_PageLoc      = $74
    //> Block_PageLoc         = $76
    //> Misc_PageLoc          = $7a
    //> Bubble_PageLoc        = $83
    //> SprObject_X_Position  = $86
    //> Player_X_Position     = $86
    //> Enemy_X_Position      = $87
    //> Fireball_X_Position   = $8d
    //> Block_X_Position      = $8f
    //> Misc_X_Position       = $93
    //> Bubble_X_Position     = $9c
    //> SprObject_Y_Speed     = $9f
    //> Player_Y_Speed        = $9f
    //> Enemy_Y_Speed         = $a0
    //> Fireball_Y_Speed      = $a6
    //> Block_Y_Speed         = $a8
    //> Misc_Y_Speed          = $ac
    //> SprObject_Y_HighPos   = $b5
    //> Player_Y_HighPos      = $b5
    //> Enemy_Y_HighPos       = $b6
    //> Fireball_Y_HighPos    = $bc
    //> Block_Y_HighPos       = $be
    //> Misc_Y_HighPos        = $c2
    //> Bubble_Y_HighPos      = $cb
    //> SprObject_Y_Position  = $ce
    //> Player_Y_Position     = $ce
    //> Enemy_Y_Position      = $cf
    //> Fireball_Y_Position   = $d5
    //> Block_Y_Position      = $d7
    //> Misc_Y_Position       = $db
    //> Bubble_Y_Position     = $e4
    //> SprObject_Rel_XPos    = $03ad
    //> Player_Rel_XPos       = $03ad
    //> Enemy_Rel_XPos        = $03ae
    //> Fireball_Rel_XPos     = $03af
    //> Bubble_Rel_XPos       = $03b0
    //> Block_Rel_XPos        = $03b1
    //> Misc_Rel_XPos         = $03b3
    //> SprObject_Rel_YPos    = $03b8
    //> Player_Rel_YPos       = $03b8
    //> Enemy_Rel_YPos        = $03b9
    //> Fireball_Rel_YPos     = $03ba
    //> Bubble_Rel_YPos       = $03bb
    //> Block_Rel_YPos        = $03bc
    //> Misc_Rel_YPos         = $03be
    //> SprObject_SprAttrib   = $03c4
    //> Player_SprAttrib      = $03c4
    //> Enemy_SprAttrib       = $03c5
    //> SprObject_X_MoveForce = $0400
    //> Enemy_X_MoveForce     = $0401
    //> SprObject_YMF_Dummy   = $0416
    //> Player_YMF_Dummy      = $0416
    //> Enemy_YMF_Dummy       = $0417
    //> Bubble_YMF_Dummy      = $042c
    //> SprObject_Y_MoveForce = $0433
    //> Player_Y_MoveForce    = $0433
    //> Enemy_Y_MoveForce     = $0434
    //> Block_Y_MoveForce     = $043c
    //> DisableCollisionDet   = $0716
    //> Player_CollisionBits  = $0490
    //> Enemy_CollisionBits   = $0491
    //> SprObj_BoundBoxCtrl   = $0499
    //> Player_BoundBoxCtrl   = $0499
    //> Enemy_BoundBoxCtrl    = $049a
    //> Fireball_BoundBoxCtrl = $04a0
    //> Misc_BoundBoxCtrl     = $04a2
    //> EnemyFrenzyBuffer     = $06cb
    //> EnemyFrenzyQueue      = $06cd
    //> Enemy_Flag            = $0f
    //> Enemy_ID              = $16
    //> PlayerGfxOffset       = $06d5
    //> Player_XSpeedAbsolute = $0700
    //> FrictionAdderHigh     = $0701
    //> FrictionAdderLow      = $0702
    //> RunningSpeed          = $0703
    //> SwimmingFlag          = $0704
    //> Player_X_MoveForce    = $0705
    //> DiffToHaltJump        = $0706
    //> JumpOrigin_Y_HighPos  = $0707
    //> JumpOrigin_Y_Position = $0708
    //> VerticalForce         = $0709
    //> VerticalForceDown     = $070a
    //> PlayerChangeSizeFlag  = $070b
    //> PlayerAnimTimerSet    = $070c
    //> PlayerAnimCtrl        = $070d
    //> DeathMusicLoaded      = $0712
    //> FlagpoleSoundQueue    = $0713
    //> CrouchingFlag         = $0714
    //> MaximumLeftSpeed      = $0450
    //> MaximumRightSpeed     = $0456
    //> SprObject_OffscrBits  = $03d0
    //> Player_OffscreenBits  = $03d0
    //> Enemy_OffscreenBits   = $03d1
    //> FBall_OffscreenBits   = $03d2
    //> Bubble_OffscreenBits  = $03d3
    //> Block_OffscreenBits   = $03d4
    //> Misc_OffscreenBits    = $03d6
    //> EnemyOffscrBitsMasked = $03d8
    //> Cannon_Offset         = $046a
    //> Cannon_PageLoc        = $046b
    //> Cannon_X_Position     = $0471
    //> Cannon_Y_Position     = $0477
    //> Cannon_Timer          = $047d
    //> Whirlpool_Offset      = $046a
    //> Whirlpool_PageLoc     = $046b
    //> Whirlpool_LeftExtent  = $0471
    //> Whirlpool_Length      = $0477
    //> Whirlpool_Flag        = $047d
    //> VineFlagOffset        = $0398
    //> VineHeight            = $0399
    //> VineObjOffset         = $039a
    //> VineStart_Y_Position  = $039d
    //> Block_Orig_YPos       = $03e4
    //> Block_BBuf_Low        = $03e6
    //> Block_Metatile        = $03e8
    //> Block_PageLoc2        = $03ea
    //> Block_RepFlag         = $03ec
    //> Block_ResidualCounter = $03f0
    //> Block_Orig_XPos       = $03f1
    //> BoundingBox_UL_XPos   = $04ac
    //> BoundingBox_UL_YPos   = $04ad
    //> BoundingBox_DR_XPos   = $04ae
    //> BoundingBox_DR_YPos   = $04af
    //> BoundingBox_UL_Corner = $04ac
    //> BoundingBox_LR_Corner = $04ae
    //> EnemyBoundingBoxCoord = $04b0
    //> PowerUpType           = $39
    //> FireballBouncingFlag  = $3a
    //> FireballCounter       = $06ce
    //> FireballThrowingTimer = $0711
    //> HammerEnemyOffset     = $06ae
    //> JumpCoinMiscOffset    = $06b7
    //> Block_Buffer_1        = $0500
    //> Block_Buffer_2        = $05d0
    //> HammerThrowingTimer   = $03a2
    //> HammerBroJumpTimer    = $3c
    //> Misc_Collision_Flag   = $06be
    //> RedPTroopaOrigXPos    = $0401
    //> RedPTroopaCenterYPos  = $58
    //> XMovePrimaryCounter   = $a0
    //> XMoveSecondaryCounter = $58
    //> CheepCheepMoveMFlag   = $58
    //> CheepCheepOrigYPos    = $0434
    //> BitMFilter            = $06dd
    //> LakituReappearTimer   = $06d1
    //> LakituMoveSpeed       = $58
    //> LakituMoveDirection   = $a0
    //> FirebarSpinState_Low  = $58
    //> FirebarSpinState_High = $a0
    //> FirebarSpinSpeed      = $0388
    //> FirebarSpinDirection  = $34
    //> DuplicateObj_Offset   = $06cf
    //> NumberofGroupEnemies  = $06d3
    //> BlooperMoveCounter    = $a0
    //> BlooperMoveSpeed      = $58
    //> BowserBodyControls    = $0363
    //> BowserFeetCounter     = $0364
    //> BowserMovementSpeed   = $0365
    //> BowserOrigXPos        = $0366
    //> BowserFlameTimerCtrl  = $0367
    //> BowserFront_Offset    = $0368
    //> BridgeCollapseOffset  = $0369
    //> BowserGfxFlag         = $036a
    //> BowserHitPoints       = $0483
    //> MaxRangeFromOrigin    = $06dc
    //> BowserFlamePRandomOfs = $0417
    //> PiranhaPlantUpYPos    = $0417
    //> PiranhaPlantDownYPos  = $0434
    //> PiranhaPlant_Y_Speed  = $58
    //> PiranhaPlant_MoveFlag = $a0
    //> FireworksCounter      = $06d7
    //> ExplosionGfxCounter   = $58
    //> ExplosionTimerCounter = $a0
    //> ;sound related defines
    //> Squ2_NoteLenBuffer    = $07b3
    //> Squ2_NoteLenCounter   = $07b4
    //> Squ2_EnvelopeDataCtrl = $07b5
    //> Squ1_NoteLenCounter   = $07b6
    //> Squ1_EnvelopeDataCtrl = $07b7
    //> Tri_NoteLenBuffer     = $07b8
    //> Tri_NoteLenCounter    = $07b9
    //> Noise_BeatLenCounter  = $07ba
    //> Squ1_SfxLenCounter    = $07bb
    //> Squ2_SfxLenCounter    = $07bd
    //> Sfx_SecondaryCounter  = $07be
    //> Noise_SfxLenCounter   = $07bf
    //> PauseSoundQueue       = $fa
    //> Square1SoundQueue     = $ff
    //> Square2SoundQueue     = $fe
    //> NoiseSoundQueue       = $fd
    //> AreaMusicQueue        = $fb
    //> EventMusicQueue       = $fc
    //> Square1SoundBuffer    = $f1
    //> Square2SoundBuffer    = $f2
    //> NoiseSoundBuffer      = $f3
    //> AreaMusicBuffer       = $f4
    //> EventMusicBuffer      = $07b1
    //> PauseSoundBuffer      = $07b2
    //> MusicData             = $f5
    //> MusicDataLow          = $f5
    //> MusicDataHigh         = $f6
    //> MusicOffset_Square2   = $f7
    //> MusicOffset_Square1   = $f8
    //> MusicOffset_Triangle  = $f9
    //> MusicOffset_Noise     = $07b0
    //> NoteLenLookupTblOfs   = $f0
    //> DAC_Counter           = $07c0
    //> NoiseDataLoopbackOfs  = $07c1
    //> NoteLengthTblAdder    = $07c4
    //> AreaMusicBuffer_Alt   = $07c5
    //> PauseModeFlag         = $07c6
    //> GroundMusicHeaderOfs  = $07c7
    //> AltRegContentFlag     = $07ca
    //> ;-------------------------------------------------------------------------------------
    //> ;CONSTANTS
    //> ;sound effects constants
    //> Sfx_SmallJump         = %10000000
    //> Sfx_Flagpole          = %01000000
    //> Sfx_Fireball          = %00100000
    //> Sfx_PipeDown_Injury   = %00010000
    //> Sfx_EnemySmack        = %00001000
    //> Sfx_EnemyStomp        = %00000100
    //> Sfx_Bump              = %00000010
    //> Sfx_BigJump           = %00000001
    //> Sfx_BowserFall        = %10000000
    //> Sfx_ExtraLife         = %01000000
    //> Sfx_PowerUpGrab       = %00100000
    //> Sfx_TimerTick         = %00010000
    //> Sfx_Blast             = %00001000
    //> Sfx_GrowVine          = %00000100
    //> Sfx_GrowPowerUp       = %00000010
    //> Sfx_CoinGrab          = %00000001
    //> Sfx_BowserFlame       = %00000010
    //> Sfx_BrickShatter      = %00000001
    //> ;music constants
    //> Silence               = %10000000
    //> StarPowerMusic        = %01000000
    //> PipeIntroMusic        = %00100000
    //> CloudMusic            = %00010000
    //> CastleMusic           = %00001000
    //> UndergroundMusic      = %00000100
    //> WaterMusic            = %00000010
    //> GroundMusic           = %00000001
    //> TimeRunningOutMusic   = %01000000
    //> EndOfLevelMusic       = %00100000
    //> AltGameOverMusic      = %00010000
    //> EndOfCastleMusic      = %00001000
    //> VictoryMusic          = %00000100
    //> GameOverMusic         = %00000010
    //> DeathMusic            = %00000001
    //> ;enemy object constants
    //> GreenKoopa            = $00
    //> BuzzyBeetle           = $02
    //> RedKoopa              = $03
    //> HammerBro             = $05
    //> Goomba                = $06
    //> Bloober               = $07
    //> BulletBill_FrenzyVar  = $08
    //> GreyCheepCheep        = $0a
    //> RedCheepCheep         = $0b
    //> Podoboo               = $0c
    //> PiranhaPlant          = $0d
    //> GreenParatroopaJump   = $0e
    //> RedParatroopa         = $0f
    //> GreenParatroopaFly    = $10
    //> Lakitu                = $11
    //> Spiny                 = $12
    //> FlyCheepCheepFrenzy   = $14
    //> FlyingCheepCheep      = $14
    //> BowserFlame           = $15
    //> Fireworks             = $16
    //> BBill_CCheep_Frenzy   = $17
    //> Stop_Frenzy           = $18
    //> Bowser                = $2d
    //> PowerUpObject         = $2e
    //> VineObject            = $2f
    //> FlagpoleFlagObject    = $30
    //> StarFlagObject        = $31
    //> JumpspringObject      = $32
    //> BulletBill_CannonVar  = $33
    //> RetainerObject        = $35
    //> TallEnemy             = $09
    //> ;other constants
    //> World1 = 0
    //> World2 = 1
    //> World3 = 2
    //> World4 = 3
    //> World5 = 4
    //> World6 = 5
    //> World7 = 6
    //> World8 = 7
    //> Level1 = 0
    //> Level2 = 1
    //> Level3 = 2
    //> Level4 = 3
    //> WarmBootOffset        = <$07d6
    //> ColdBootOffset        = <$07fe
    //> TitleScreenDataOffset = $1ec0
    //> SoundMemory           = $07b0
    //> SwimTileRepOffset     = PlayerGraphicsTable + $9e
    //> MusicHeaderOffsetData = MusicHeaderData - 1
    //> MHD                   = MusicHeaderData
    //> A_Button              = %10000000
    //> B_Button              = %01000000
    //> Select_Button         = %00100000
    //> Start_Button          = %00010000
    //> Up_Dir                = %00001000
    //> Down_Dir              = %00000100
    //> Left_Dir              = %00000010
    //> Right_Dir             = %00000001
    //> TitleScreenModeValue  = 0
    //> GameModeValue         = 1
    //> VictoryModeValue      = 2
    //> GameOverModeValue     = 3
    //> ;-------------------------------------------------------------------------------------
    //> ;DIRECTIVES
    //> .index 8
    //> .mem 8
    //> .org $8000
    //> ;-------------------------------------------------------------------------------------
    //> Start:
    //> sei                          ;pretty standard 6502 type init here
    //> cld
    //> lda #%00010000               ;init PPU control register 1
    //> sta PPU_CTRL_REG1
    ppuCtrlReg1 = 0x10
    //> ldx #$ff                     ;reset stack pointer
    //> txs
    temp0 = 0xFF
    do {
        //> VBlank1:     lda PPU_STATUS               ;wait two frames
        //> bpl VBlank1
    } while ((ppuStatus and 0x80) == 0)
    do {
        //> VBlank2:     lda PPU_STATUS
        //> bpl VBlank2
    } while ((ppuStatus and 0x80) == 0)
    //> ldy #ColdBootOffset          ;load default cold boot pointer
    //> ldx #$05                     ;this is where we check for a warm boot
    temp0 = 0x05
    temp1 = ColdBootOffset
    while ((temp0 and 0x80) == 0) {
        do {
            //> WBootCheck:  lda TopScoreDisplay,x        ;check each score digit in the top score
            //> cmp #10                      ;to see if we have a valid digit
            //> bcs ColdBoot                 ;if not, give up and proceed with cold boot
            //> dex
            temp0 = (temp0 - 1) and 0xFF
            //> bpl WBootCheck
        } while ((temp0 and 0x80) == 0)
    }
    //> lda WarmBootValidation       ;second checkpoint, check to see if
    //> cmp #$a5                     ;another location has a specific value
    //> bne ColdBoot
    temp2 = warmBootValidation
    if (warmBootValidation == 0xA5) {
        //> ldy #WarmBootOffset          ;if passed both, load warm boot pointer
        temp1 = WarmBootOffset
    }
    //> ColdBoot:    jsr InitializeMemory         ;clear memory using pointer in Y
    initializeMemory(temp1)
    //> sta SND_DELTA_REG+1          ;reset delta counter load register
    sndDeltaReg[1] = temp2
    //> sta OperMode                 ;reset primary mode of operation
    operMode = temp2
    //> lda #$a5                     ;set warm boot flag
    temp2 = 0xA5
    //> sta WarmBootValidation
    warmBootValidation = temp2
    //> sta PseudoRandomBitReg       ;set seed for pseudorandom register
    pseudoRandomBitReg = temp2
    //> lda #%00001111
    temp2 = 0x0F
    //> sta SND_MASTERCTRL_REG       ;enable all sound channels except dmc
    sndMasterctrlReg = temp2
    //> lda #%00000110
    temp2 = 0x06
    //> sta PPU_CTRL_REG2            ;turn off clipping for OAM and background
    ppuCtrlReg2 = temp2
    //> jsr MoveAllSpritesOffscreen
    moveAllSpritesOffscreen()
    //> jsr InitializeNameTables     ;initialize both name tables
    initializeNameTables()
    //> inc DisableScreenFlag        ;set flag to disable screen output
    disableScreenFlag = (disableScreenFlag + 1) and 0xFF
    //> lda Mirror_PPU_CTRL_REG1
    temp2 = mirrorPpuCtrlReg1
    //> ora #%10000000               ;enable NMIs
    temp3 = temp2 or 0x80
    //> jsr WritePPUReg1
    writePPUReg1(temp3)
    while (true) {
        //> EndlessLoop: jmp EndlessLoop              ;endless loop, need I say more?
    }
}

// Decompiled from PauseRoutine
fun pauseRoutine() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var temp5: Int = 0
    var temp6: Int = 0
    var gamePauseStatus by MemoryByte(GamePauseStatus)
    var gamePauseTimer by MemoryByte(GamePauseTimer)
    var operMode by MemoryByte(OperMode)
    var opermodeTask by MemoryByte(OperMode_Task)
    var pauseSoundQueue by MemoryByte(PauseSoundQueue)
    var savedJoypad1Bits by MemoryByte(SavedJoypad1Bits)
    //> PauseRoutine:
    //> lda OperMode           ;are we in victory mode?
    //> cmp #VictoryModeValue  ;if so, go ahead
    //> beq ChkPauseTimer
    temp0 = operMode
    if (operMode != VictoryModeValue) {
        //> cmp #GameModeValue     ;are we in game mode?
        //> bne ExitPause          ;if not, leave
        if (temp0 == GameModeValue) {
            //> lda OperMode_Task      ;if we are in game mode, are we running game engine?
            temp0 = opermodeTask
            //> cmp #$03
            //> bne ExitPause          ;if not, leave
            if (temp0 == 0x03) {
            } else {
                //> ExitPause:     rts
                return
            }
        }
    }
    //> ChkPauseTimer: lda GamePauseTimer     ;check if pause timer is still counting down
    temp0 = gamePauseTimer
    //> beq ChkStart
    if (temp0 != 0) {
        //> dec GamePauseTimer     ;if so, decrement and leave
        gamePauseTimer = (gamePauseTimer - 1) and 0xFF
        //> rts
        return
    } else {
        //> ChkStart:      lda SavedJoypad1Bits   ;check to see if start is pressed
        temp0 = savedJoypad1Bits
        //> and #Start_Button      ;on controller 1
        temp1 = temp0 and Start_Button
        //> beq ClrPauseTimer
        temp0 = temp1
        if (temp1 != 0) {
            //> lda GamePauseStatus    ;check to see if timer flag is set
            temp0 = gamePauseStatus
            //> and #%10000000         ;and if so, do not reset timer (residual,
            temp2 = temp0 and 0x80
            //> bne ExitPause          ;joypad reading routine makes this unnecessary)
            temp0 = temp2
            if (temp2 == 0) {
                //> lda #$2b               ;set pause timer
                temp0 = 0x2B
                //> sta GamePauseTimer
                gamePauseTimer = temp0
                //> lda GamePauseStatus
                temp0 = gamePauseStatus
                //> tay
                //> iny                    ;set pause sfx queue for next pause mode
                temp0 = (temp0 + 1) and 0xFF
                //> sty PauseSoundQueue
                pauseSoundQueue = temp0
                //> eor #%00000001         ;invert d0 and set d7
                temp3 = temp0 xor 0x01
                //> ora #%10000000
                temp4 = temp3 or 0x80
                //> bne SetPause           ;unconditional branch
                temp0 = temp4
                temp5 = temp0
                if (temp4 == 0) {
                }
            }
        }
    }
    //> ClrPauseTimer: lda GamePauseStatus    ;clear timer flag if timer is at zero and start button
    temp0 = gamePauseStatus
    //> and #%01111111         ;is not pressed
    temp6 = temp0 and 0x7F
    //> SetPause:      sta GamePauseStatus
    gamePauseStatus = temp6
}

// Decompiled from SpriteShuffler
fun spriteShuffler() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var areaType by MemoryByte(AreaType)
    var sprShuffleAmtOffset by MemoryByte(SprShuffleAmtOffset)
    val miscSprdataoffset by MemoryByteIndexed(Misc_SprDataOffset)
    val sprDataOffset by MemoryByteIndexed(SprDataOffset)
    val sprShuffleAmt by MemoryByteIndexed(SprShuffleAmt)
    //> SpriteShuffler:
    //> ldy AreaType                ;load level type, likely residual code
    //> lda #$28                    ;load preset value which will put it at
    //> sta $00                     ;sprite #10
    memory[0x0] = 0x28.toUByte()
    //> ldx #$0e                    ;start at the end of OAM data offsets
    temp0 = 0x0E
    temp1 = areaType
    while ((temp0 and 0x80) == 0) {
        //> ldy SprShuffleAmtOffset     ;get current offset to preset value we want to add
        temp1 = sprShuffleAmtOffset
        //> clc
        //> adc SprShuffleAmt,y         ;get shuffle amount, add to current sprite offset
        //> bcc StrSprOffset            ;if not exceeded $ff, skip second add
        temp2 = (0x28 + sprShuffleAmt[temp1]) and 0xFF
        if (0x28 + sprShuffleAmt[temp1] > 0xFF) {
            //> clc
            //> adc $00                     ;otherwise add preset value $28 to offset
        }
        //> StrSprOffset:  sta SprDataOffset,x         ;store new offset here or old one if branched to here
        sprDataOffset[temp0] = temp2
        do {
            //> ShuffleLoop:   lda SprDataOffset,x         ;check for offset value against
            temp2 = sprDataOffset[temp0]
            //> cmp $00                     ;the preset value
            //> bcc NextSprOffset           ;if less, skip this part
            //> NextSprOffset: dex                         ;move backwards to next one
            temp0 = (temp0 - 1) and 0xFF
            //> bpl ShuffleLoop
        } while ((temp0 and 0x80) == 0)
    }
    //> ldx SprShuffleAmtOffset     ;load offset
    temp0 = sprShuffleAmtOffset
    //> inx
    temp0 = (temp0 + 1) and 0xFF
    //> cpx #$03                    ;check if offset + 1 goes to 3
    //> bne SetAmtOffset            ;if offset + 1 not 3, store
    if (temp0 == 0x03) {
        //> ldx #$00                    ;otherwise, init to 0
        temp0 = 0x00
    }
    //> SetAmtOffset:  stx SprShuffleAmtOffset
    sprShuffleAmtOffset = temp0
    //> ldx #$08                    ;load offsets for values and storage
    temp0 = 0x08
    //> ldy #$02
    temp1 = 0x02
    do {
        //> SetMiscOffset: lda SprDataOffset+5,y       ;load one of three OAM data offsets
        temp2 = sprDataOffset[5 + temp1]
        //> sta Misc_SprDataOffset-2,x  ;store first one unmodified, but
        miscSprdataoffset[-2 + temp0] = temp2
        //> clc                         ;add eight to the second and eight
        //> adc #$08                    ;more to the third one
        //> sta Misc_SprDataOffset-1,x  ;note that due to the way X is set up,
        miscSprdataoffset[-1 + temp0] = (temp2 + 0x08) and 0xFF
        //> clc                         ;this code loads into the misc sprite offsets
        //> adc #$08
        //> sta Misc_SprDataOffset,x
        miscSprdataoffset[temp0] = (((temp2 + 0x08) and 0xFF) + 0x08) and 0xFF
        //> dex
        temp0 = (temp0 - 1) and 0xFF
        //> dex
        temp0 = (temp0 - 1) and 0xFF
        //> dex
        temp0 = (temp0 - 1) and 0xFF
        //> dey
        temp1 = (temp1 - 1) and 0xFF
        //> bpl SetMiscOffset           ;do this until all misc spr offsets are loaded
    } while ((temp1 and 0x80) == 0)
    //> rts
    return
}

// Decompiled from OperModeExecutionTree
fun operModeExecutionTree() {
    var operMode by MemoryByte(OperMode)
    //> OperModeExecutionTree:
    //> lda OperMode     ;this is the heart of the entire program,
    //> jsr JumpEngine   ;most of what goes on starts here
    when (operMode) {
        0 -> {
            titleScreenMode_real()
        }
        1 -> {
            gameMode_real()
        }
        2 -> {
            victoryMode()
        }
        3 -> {
            gameOverMode()
        }
        else -> {
            // Unknown JumpEngine index
        }
    }
    return
}

// Decompiled from TitleScreenMode
// Dispatches to sub-tasks based on OperMode_Task
fun titleScreenMode_real() {
    var opermodeTask by MemoryByte(OperMode_Task)
    when (opermodeTask) {
        0 -> initializeGame_real()
        1 -> screenRoutines_real()
        2 -> primaryGameSetup_real()
        3 -> gameMenuRoutine_real()
    }
}

// Decompiled from GameMode (lines 5290-5297 in smbdism.asm)
// Uses the existing gameCoreRoutine from the decompiler output
fun gameMode_real() {
    var opermodeTask by MemoryByte(OperMode_Task)
    when (opermodeTask) {
        0 -> initializeArea_real()
        1 -> screenRoutines_real()
        2 -> secondaryGameSetup_real()
        else -> gameCoreRoutine()  // Task 3+ stays in GameCoreRoutine
    }
}

// Decompiled from InitializeGame (lines 2649-2658 in smbdism.asm)
// Falls through to InitializeArea
fun initializeGame_real() {
    var demoTimer by MemoryByte(DemoTimer)
    val soundMemory by MemoryByteIndexed(SoundMemory)

    // Clear memory as far as $076f
    initializeMemory(0x6F)

    // Clear sound memory
    for (y in 0x1F downTo 0) {
        soundMemory[y] = 0
    }

    // Set demo timer
    demoTimer = 0x18

    // Load area pointer
    loadAreaPointer(A)

    // Fall through to InitializeArea
    initializeArea_real()
}

// Decompiled from InitializeArea (lines 2660-2714 in smbdism.asm)
// Properly decompiled version that calls getAreaDataAddrs() to set PlayerEntranceCtrl
fun initializeArea_real() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var altEntranceControl by MemoryByte(AltEntranceControl)
    var areaMusicQueue by MemoryByte(AreaMusicQueue)
    var backloadingFlag by MemoryByte(BackloadingFlag)
    var blockBufferColumnPos by MemoryByte(BlockBufferColumnPos)
    var columnSets by MemoryByte(ColumnSets)
    var currentntaddrHigh by MemoryByte(CurrentNTAddr_High)
    var currentntaddrLow by MemoryByte(CurrentNTAddr_Low)
    var currentPageLoc by MemoryByte(CurrentPageLoc)
    var disableScreenFlag by MemoryByte(DisableScreenFlag)
    var entrancePage by MemoryByte(EntrancePage)
    var halfwayPage by MemoryByte(HalfwayPage)
    var levelNumber by MemoryByte(LevelNumber)
    var opermodeTask by MemoryByte(OperMode_Task)
    var playerEntranceCtrl by MemoryByte(PlayerEntranceCtrl)
    var primaryHardMode by MemoryByte(PrimaryHardMode)
    var screenleftPageloc by MemoryByte(ScreenLeft_PageLoc)
    var secondaryHardMode by MemoryByte(SecondaryHardMode)
    var worldNumber by MemoryByte(WorldNumber)
    val areaObjectLength by MemoryByteIndexed(AreaObjectLength)
    val timers by MemoryByteIndexed(Timers)

    // Clear all memory as far as $074b
    initializeMemory(0x4B)

    // Clear out memory between $0780 and $07a1
    temp0 = 0x21
    do {
        timers[temp0] = 0x00
        temp0 = (temp0 - 1) and 0xFF
    } while ((temp0 and 0x80) == 0)

    // If AltEntranceControl not set, use halfway page, if any found
    // Otherwise use saved entry page number
    temp1 = altEntranceControl
    temp2 = halfwayPage
    if (temp1 != 0) {
        temp2 = entrancePage
    }

    // Set page locations
    screenleftPageloc = temp2
    currentPageLoc = temp2
    backloadingFlag = temp2

    // Get pixel coordinates for screen borders
    getScreenPosition()

    // Set name table address based on page parity
    temp1 = 0x20
    temp3 = temp2 and 0x01
    temp2 = temp3
    if (temp3 != 0) {
        temp1 = 0x24
    }
    currentntaddrHigh = temp1
    temp1 = 0x80
    currentntaddrLow = temp1

    // Store LSB of page number in high nybble of block buffer column position
    temp2 = (temp2 shl 1) and 0xFF
    temp2 = (temp2 shl 1) and 0xFF
    temp2 = (temp2 shl 1) and 0xFF
    temp2 = (temp2 shl 1) and 0xFF
    blockBufferColumnPos = temp2

    // Set area object lengths for all empty
    areaObjectLength[0] = (areaObjectLength[0] - 1) and 0xFF
    areaObjectLength[1] = (areaObjectLength[1] - 1) and 0xFF
    areaObjectLength[2] = (areaObjectLength[2] - 1) and 0xFF

    // Set value for renderer to update 12 column sets
    columnSets = 0x0B

    // Get enemy and level addresses and load header
    // THIS CALL SETS PlayerEntranceCtrl FROM THE LEVEL HEADER!
    getAreaDataAddrs()

    // Hard mode logic - only set secondary hard mode for areas 5-3 and beyond
    temp2 = primaryHardMode
    var setSecondaryHard = (temp2 != 0)  // If primary hard mode, set secondary

    if (!setSecondaryHard) {
        temp2 = worldNumber
        if (temp2 >= World5) {
            if (temp2 > World5) {
                setSecondaryHard = true  // World > 5
            } else {
                // World == 5, check level
                temp2 = levelNumber
                if (temp2 >= Level3) {
                    setSecondaryHard = true  // World 5, level >= 3
                }
            }
        }
    }

    if (setSecondaryHard) {
        secondaryHardMode = (secondaryHardMode + 1) and 0xFF
    }

    // If halfway page set, overwrite start position from header
    temp2 = halfwayPage
    if (temp2 != 0) {
        playerEntranceCtrl = 0x02
    }

    // Silence music
    areaMusicQueue = Silence

    // Disable screen output
    disableScreenFlag = 0x01

    // Increment one of the modes
    opermodeTask = (opermodeTask + 1) and 0xFF
}

// Decompiled from ScreenRoutines (lines 1361-1380 in smbdism.asm)
fun screenRoutines_real() {
    var screenRoutineTask by MemoryByte(ScreenRoutineTask)
    when (screenRoutineTask) {
        0 -> initScreen()
        1 -> setupIntermediate()
        2 -> writeTopStatusLine()
        3 -> writeBottomStatusLine()
        4 -> displayTimeUp()
        5 -> resetSpritesAndScreenTimer()
        6 -> displayIntermediate()
        7 -> resetSpritesAndScreenTimer()
        8 -> areaParserTaskControl()
        9 -> getAreaPalette()
        10 -> getBackgroundColor()
        11 -> getAlternatePalette1()
        12 -> drawTitleScreen()
        13 -> clearBuffersDrawIcon()
        14 -> writeTopScore()
    }
}

// Decompiled from PrimaryGameSetup (lines 2717-2724 in smbdism.asm)
fun primaryGameSetup_real() {
    var fetchNewGameTimerFlag by MemoryByte(FetchNewGameTimerFlag)
    var playerSize by MemoryByte(PlayerSize)
    var numberOfLives by MemoryByte(NumberofLives)
    var offscrNumberofLives by MemoryByte(OffScr_NumberofLives)

    fetchNewGameTimerFlag = 1
    playerSize = 1  // Small Mario
    numberOfLives = 2  // 3 lives (0-indexed)
    offscrNumberofLives = 2

    // Fall through to SecondaryGameSetup
    secondaryGameSetup_real()
}

// Decompiled from SecondaryGameSetup (lines 2725-2756 in smbdism.asm)
fun secondaryGameSetup_real() {
    var disableScreenFlag by MemoryByte(DisableScreenFlag)
    var gameTimerExpiredFlag by MemoryByte(GameTimerExpiredFlag)
    var disableIntermediate by MemoryByte(DisableIntermediate)
    var backloadingFlag by MemoryByte(BackloadingFlag)
    var balPlatformAlignment by MemoryByte(BalPlatformAlignment)
    var screenLeft_PageLoc by MemoryByte(ScreenLeft_PageLoc)
    var screenRoutineTask by MemoryByte(ScreenRoutineTask)
    var operModeTask by MemoryByte(OperMode_Task)
    val vramBuffer1 by MemoryByteIndexed(VRAM_Buffer1)

    disableScreenFlag = 0

    // Clear VRAM buffer ($0300-$03FF)
    for (y in 0 until 256) {
        vramBuffer1[y] = 0
    }

    gameTimerExpiredFlag = 0
    disableIntermediate = 0
    backloadingFlag = 0
    balPlatformAlignment = 0xFF

    // Set screen routine to handle area parsing
    screenRoutineTask = 0

    // Increment task to move to GameMenuRoutine
    operModeTask = operModeTask + 1
}

// Decompiled from GameMenuRoutine (lines 971-1068 in smbdism.asm)
fun gameMenuRoutine_real() {
    var savedJoypad1Bits by MemoryByte(SavedJoypad1Bits)
    var savedJoypad2Bits by MemoryByte(SavedJoypad2Bits)
    var demoTimer by MemoryByte(DemoTimer)
    var selectTimer by MemoryByte(SelectTimer)
    var worldSelectEnableFlag by MemoryByte(WorldSelectEnableFlag)
    var numberOfPlayers by MemoryByte(NumberOfPlayers)
    var operMode by MemoryByte(OperMode)
    var operModeTask by MemoryByte(OperMode_Task)
    var worldNumber by MemoryByte(WorldNumber)
    var areaNumber by MemoryByte(AreaNumber)

    val combined = savedJoypad1Bits or savedJoypad2Bits

    // Check for Start button
    if (combined == Start_Button || combined == (A_Button + Start_Button)) {
        // Start game - go to ChkContinue logic
        chkContinue()
        return
    }

    // Check for Select button
    if (combined == Select_Button) {
        if (demoTimer != 0) {
            // Reset demo timer and toggle players
            demoTimer = 0x18
            if (selectTimer == 0) {
                selectTimer = 0x10
                numberOfPlayers = numberOfPlayers xor 1
                drawMushroomIcon()
            }
        } else {
            // Demo timer expired, reset title
            resetTitle()
        }
        nullJoypad()
        return
    }

    // Check demo timer
    if (demoTimer == 0) {
        // Run demo
        selectTimer = combined
        demoEngine()
        if (flagC) {
            resetTitle()
            return
        }
        runDemo()
        return
    }

    // Check for B button (world select)
    if (worldSelectEnableFlag != 0 && combined == B_Button) {
        if (demoTimer != 0) {
            demoTimer = 0x18
            if (selectTimer == 0) {
                selectTimer = 0x10
                // Increment world selection
                worldNumber = (worldNumber + 1) and 0x07
                if (worldNumber > 7) worldNumber = 0
            }
        }
    }

    nullJoypad()
}

// Decompiled from ChkContinue (lines 1048-1068 in smbdism.asm)
fun chkContinue() {
    var continueWorld by MemoryByte(ContinueWorld)
    var worldNumber by MemoryByte(WorldNumber)
    var offscrWorldNumber by MemoryByte(OffScr_WorldNumber)
    var areaNumber by MemoryByte(AreaNumber)
    var offscrAreaNumber by MemoryByte(OffScr_AreaNumber)
    var operMode by MemoryByte(OperMode)
    var operModeTask by MemoryByte(OperMode_Task)

    // Check if continuing from a saved world
    val contWorld = continueWorld
    worldNumber = contWorld
    offscrWorldNumber = contWorld
    areaNumber = 0
    offscrAreaNumber = 0

    // Transition to game mode - reset task to 0 for InitializeArea
    operModeTask = 0
    operMode = 1  // GameModeValue
}

// Helper stubs for missing functions
fun resetTitle() {
    var operModeTask by MemoryByte(OperMode_Task)
    operModeTask = 0  // Reset to InitializeGame
}

fun nullJoypad() {
    var savedJoypad1Bits by MemoryByte(SavedJoypad1Bits)
    var savedJoypad2Bits by MemoryByte(SavedJoypad2Bits)
    savedJoypad1Bits = 0
    savedJoypad2Bits = 0
}

fun runDemo() {
    // Demo mode - run game engine with demo input
    gameEngine()
}

fun initScreen() {
    var screenRoutineTask by MemoryByte(ScreenRoutineTask)
    screenRoutineTask = screenRoutineTask + 1
}

fun setupIntermediate() {
    var screenRoutineTask by MemoryByte(ScreenRoutineTask)
    screenRoutineTask = screenRoutineTask + 1
}

fun writeTopStatusLine() {
    var screenRoutineTask by MemoryByte(ScreenRoutineTask)
    screenRoutineTask = screenRoutineTask + 1
}

fun writeBottomStatusLine() {
    var screenRoutineTask by MemoryByte(ScreenRoutineTask)
    screenRoutineTask = screenRoutineTask + 1
}

fun displayTimeUp() {
    var screenRoutineTask by MemoryByte(ScreenRoutineTask)
    screenRoutineTask = screenRoutineTask + 1
}

fun resetSpritesAndScreenTimer() {
    var screenRoutineTask by MemoryByte(ScreenRoutineTask)
    screenRoutineTask = screenRoutineTask + 1
}

fun displayIntermediate() {
    var screenRoutineTask by MemoryByte(ScreenRoutineTask)
    screenRoutineTask = screenRoutineTask + 1
}

fun areaParserTaskControl() {
    var screenRoutineTask by MemoryByte(ScreenRoutineTask)
    screenRoutineTask = screenRoutineTask + 1
}

fun getAreaPalette() {
    var screenRoutineTask by MemoryByte(ScreenRoutineTask)
    screenRoutineTask = screenRoutineTask + 1
}

fun getBackgroundColor() {
    var screenRoutineTask by MemoryByte(ScreenRoutineTask)
    screenRoutineTask = screenRoutineTask + 1
}

fun getAlternatePalette1() {
    var screenRoutineTask by MemoryByte(ScreenRoutineTask)
    screenRoutineTask = screenRoutineTask + 1
}

fun drawTitleScreen() {
    var screenRoutineTask by MemoryByte(ScreenRoutineTask)
    screenRoutineTask = screenRoutineTask + 1
}

fun clearBuffersDrawIcon() {
    var screenRoutineTask by MemoryByte(ScreenRoutineTask)
    screenRoutineTask = screenRoutineTask + 1
}

fun writeTopScore() {
    var screenRoutineTask by MemoryByte(ScreenRoutineTask)
    var operModeTask by MemoryByte(OperMode_Task)
    screenRoutineTask = 0
    operModeTask = operModeTask + 1  // Move to next OperMode task
}

fun gameEngine() {
    // Main game engine - stub
    gameRoutines()
}

// Decompiled from MoveAllSpritesOffscreen
fun moveAllSpritesOffscreen() {
    //> MoveAllSpritesOffscreen:
    //> ldy #$00                ;this routine moves all sprites off the screen
    //> .db $2c                 ;BIT instruction opcode
}

// Decompiled from MoveSpritesOffscreen
fun moveSpritesOffscreen() {
    var temp0: Int = 0
    val spriteYPosition by MemoryByteIndexed(Sprite_Y_Position)
    //> MoveSpritesOffscreen:
    //> ldy #$04                ;this routine moves all but sprite 0
    //> lda #$f8                ;off the screen
    temp0 = 0x04
    do {
        //> SprInitLoop:  sta Sprite_Y_Position,y ;write 248 into OAM data's Y coordinate
        spriteYPosition[temp0] = 0xF8
        //> iny                     ;which will move it off the screen
        temp0 = (temp0 + 1) and 0xFF
        //> iny
        temp0 = (temp0 + 1) and 0xFF
        //> iny
        temp0 = (temp0 + 1) and 0xFF
        //> iny
        temp0 = (temp0 + 1) and 0xFF
        //> bne SprInitLoop
    } while (temp0 != 0)
    //> rts
    return
}

// Decompiled from GoContinue
fun goContinue(A: Int) {
    var areaNumber by MemoryByte(AreaNumber)
    var offscrAreanumber by MemoryByte(OffScr_AreaNumber)
    var offscrWorldnumber by MemoryByte(OffScr_WorldNumber)
    var worldNumber by MemoryByte(WorldNumber)
    //> GoContinue:   sta WorldNumber             ;start both players at the first area
    worldNumber = A
    //> sta OffScr_WorldNumber      ;of the previously saved world number
    offscrWorldnumber = A
    //> ldx #$00                    ;note that on power-up using this function
    //> stx AreaNumber              ;will make no difference
    areaNumber = 0x00
    //> stx OffScr_AreaNumber
    offscrAreanumber = 0x00
    //> rts
    return
}

// Decompiled from DrawMushroomIcon
fun drawMushroomIcon() {
    var temp0: Int = 0
    var temp1: Int = 0
    var numberOfPlayers by MemoryByte(NumberOfPlayers)
    val mushroomIconData by MemoryByteIndexed(MushroomIconData)
    val vramBuffer1 by MemoryByteIndexed(VRAM_Buffer1)
    //> DrawMushroomIcon:
    //> ldy #$07                ;read eight bytes to be read by transfer routine
    temp0 = 0x07
    do {
        //> IconDataRead: lda MushroomIconData,y  ;note that the default position is set for a
        //> sta VRAM_Buffer1-1,y    ;1-player game
        vramBuffer1[-1 + temp0] = mushroomIconData[temp0]
        //> dey
        temp0 = (temp0 - 1) and 0xFF
        //> bpl IconDataRead
    } while ((temp0 and 0x80) == 0)
    //> lda NumberOfPlayers     ;check number of players
    //> beq ExitIcon            ;if set to 1-player game, we're done
    temp1 = numberOfPlayers
    if (numberOfPlayers != 0) {
        //> lda #$24                ;otherwise, load blank tile in 1-player position
        temp1 = 0x24
        //> sta VRAM_Buffer1+3
        vramBuffer1[3] = temp1
        //> lda #$ce                ;then load shroom icon tile in 2-player position
        temp1 = 0xCE
        //> sta VRAM_Buffer1+5
        vramBuffer1[5] = temp1
    }
    //> ExitIcon:     rts
    return
}

// Decompiled from DemoEngine
fun demoEngine() {
    var temp0: Int = 0
    var temp1: Int = 0
    var demoAction by MemoryByte(DemoAction)
    var demoActionTimer by MemoryByte(DemoActionTimer)
    var savedJoypad1Bits by MemoryByte(SavedJoypad1Bits)
    val demoActionData by MemoryByteIndexed(DemoActionData)
    val demoTimingData by MemoryByteIndexed(DemoTimingData)
    //> DemoEngine:
    //> ldx DemoAction         ;load current demo action
    //> lda DemoActionTimer    ;load current action timer
    //> bne DoAction           ;if timer still counting down, skip
    temp0 = demoActionTimer
    temp1 = demoAction
    if (demoActionTimer == 0) {
        //> inx
        temp1 = (temp1 + 1) and 0xFF
        //> inc DemoAction         ;if expired, increment action, X, and
        demoAction = (demoAction + 1) and 0xFF
        //> sec                    ;set carry by default for demo over
        //> lda DemoTimingData-1,x ;get next timer
        temp0 = demoTimingData[-1 + temp1]
        //> sta DemoActionTimer    ;store as current timer
        demoActionTimer = temp0
        //> beq DemoOver           ;if timer already at zero, skip
        if (temp0 != 0) {
        } else {
            //> DemoOver: rts
            return
        }
    }
    //> DoAction: lda DemoActionData-1,x ;get and perform action (current or next)
    temp0 = demoActionData[-1 + temp1]
    //> sta SavedJoypad1Bits
    savedJoypad1Bits = temp0
    //> dec DemoActionTimer    ;decrement action timer
    demoActionTimer = (demoActionTimer - 1) and 0xFF
    //> clc                    ;clear carry if demo still going
}

// Decompiled from VictoryModeSubroutines
fun victoryModeSubroutines() {
    var opermodeTask by MemoryByte(OperMode_Task)
    //> VictoryModeSubroutines:
    //> lda OperMode_Task
    //> jsr JumpEngine
    when (opermodeTask) {
        0 -> {
            bridgeCollapse()
        }
        1 -> {
            setupVictoryMode()
        }
        2 -> {
            playerVictoryWalk()
        }
        3 -> {
            printVictoryMessages()
        }
        4 -> {
            playerEndWorld()
        }
        else -> {
            // Unknown JumpEngine index
        }
    }
    return
}

// Decompiled from FloateyNumbersRoutine
fun floateyNumbersRoutine(X: Int) {
    //> EndExitOne:    rts                        ;and leave
    return
}

// Decompiled from GetPlayerColors
fun getPlayerColors() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var areaType by MemoryByte(AreaType)
    var backgroundColorCtrl by MemoryByte(BackgroundColorCtrl)
    var currentPlayer by MemoryByte(CurrentPlayer)
    var playerStatus by MemoryByte(PlayerStatus)
    var vramBuffer1Offset by MemoryByte(VRAM_Buffer1_Offset)
    val backgroundColors by MemoryByteIndexed(BackgroundColors)
    val playerColors by MemoryByteIndexed(PlayerColors)
    val vramBuffer1 by MemoryByteIndexed(VRAM_Buffer1)
    //> GetPlayerColors:
    //> ldx VRAM_Buffer1_Offset  ;get current buffer offset
    //> ldy #$00
    //> lda CurrentPlayer        ;check which player is on the screen
    //> beq ChkFiery
    temp0 = currentPlayer
    temp1 = vramBuffer1Offset
    temp2 = 0x00
    if (currentPlayer != 0) {
        //> ldy #$04                 ;load offset for luigi
        temp2 = 0x04
    }
    //> ChkFiery:      lda PlayerStatus         ;check player status
    temp0 = playerStatus
    //> cmp #$02
    //> bne StartClrGet          ;if fiery, load alternate offset for fiery player
    if (temp0 == 0x02) {
        //> ldy #$08
        temp2 = 0x08
    }
    //> StartClrGet:   lda #$03                 ;do four colors
    temp0 = 0x03
    //> sta $00
    memory[0x0] = temp0.toUByte()
    do {
        //> ClrGetLoop:    lda PlayerColors,y       ;fetch player colors and store them
        temp0 = playerColors[temp2]
        //> sta VRAM_Buffer1+3,x     ;in the buffer
        vramBuffer1[3 + temp1] = temp0
        //> iny
        temp2 = (temp2 + 1) and 0xFF
        //> inx
        temp1 = (temp1 + 1) and 0xFF
        //> dec $00
        memory[0x0] = ((memory[0x0].toInt() - 1) and 0xFF).toUByte()
        //> bpl ClrGetLoop
    } while (((memory[0x0].toInt() - 1) and 0xFF and 0x80) == 0)
    //> ldx VRAM_Buffer1_Offset  ;load original offset from before
    temp1 = vramBuffer1Offset
    //> ldy BackgroundColorCtrl  ;if this value is four or greater, it will be set
    temp2 = backgroundColorCtrl
    //> bne SetBGColor           ;therefore use it as offset to background color
    if (temp2 == 0) {
        //> ldy AreaType             ;otherwise use area type bits from area offset as offset
        temp2 = areaType
    }
    //> SetBGColor:    lda BackgroundColors,y   ;to background color instead
    temp0 = backgroundColors[temp2]
    //> sta VRAM_Buffer1+3,x
    vramBuffer1[3 + temp1] = temp0
    //> lda #$3f                 ;set for sprite palette address
    temp0 = 0x3F
    //> sta VRAM_Buffer1,x       ;save to buffer
    vramBuffer1[temp1] = temp0
    //> lda #$10
    temp0 = 0x10
    //> sta VRAM_Buffer1+1,x
    vramBuffer1[1 + temp1] = temp0
    //> lda #$04                 ;write length byte to buffer
    temp0 = 0x04
    //> sta VRAM_Buffer1+2,x
    vramBuffer1[2 + temp1] = temp0
    //> lda #$00                 ;now the null terminator
    temp0 = 0x00
    //> sta VRAM_Buffer1+7,x
    vramBuffer1[7 + temp1] = temp0
    //> txa                      ;move the buffer pointer ahead 7 bytes
    //> clc                      ;in case we want to write anything else later
    //> adc #$07
    //> SetVRAMOffset: sta VRAM_Buffer1_Offset  ;store as new vram buffer offset
    vramBuffer1Offset = (temp1 + 0x07) and 0xFF
    //> rts
    return
}

// Decompiled from WriteGameText
fun writeGameText(A: Int) {
    var vramBuffer1Offset by MemoryByte(VRAM_Buffer1_Offset)
    //> SetVRAMOffset: sta VRAM_Buffer1_Offset  ;store as new vram buffer offset
    vramBuffer1Offset = A
    //> rts
    return
}

// Decompiled from ResetScreenTimer
fun resetScreenTimer() {
    var screenRoutineTask by MemoryByte(ScreenRoutineTask)
    var screenTimer by MemoryByte(ScreenTimer)
    //> ResetScreenTimer:
    //> lda #$07                    ;reset timer again
    //> sta ScreenTimer
    screenTimer = 0x07
    //> inc ScreenRoutineTask       ;move onto next task
    screenRoutineTask = (screenRoutineTask + 1) and 0xFF
    //> NoReset: rts
    return
}

// Decompiled from RenderAttributeTables
fun renderAttributeTables() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var temp5: Int = 0
    var temp6: Int = 0
    var temp7: Int = 0
    var currentntaddrHigh by MemoryByte(CurrentNTAddr_High)
    var currentntaddrLow by MemoryByte(CurrentNTAddr_Low)
    var vramBuffer2Offset by MemoryByte(VRAM_Buffer2_Offset)
    var vramBufferAddrctrl by MemoryByte(VRAM_Buffer_AddrCtrl)
    val attributeBuffer by MemoryByteIndexed(AttributeBuffer)
    val vramBuffer2 by MemoryByteIndexed(VRAM_Buffer2)
    //> RenderAttributeTables:
    //> lda CurrentNTAddr_Low    ;get low byte of next name table address
    //> and #%00011111           ;to be written to, mask out all but 5 LSB,
    temp0 = currentntaddrLow and 0x1F
    //> sec                      ;subtract four
    //> sbc #$04
    //> and #%00011111           ;mask out bits again and store
    temp1 = (temp0 - 0x04) and 0xFF and 0x1F
    //> sta $01
    memory[0x1] = temp1.toUByte()
    //> lda CurrentNTAddr_High   ;get high byte and branch if borrow not set
    //> bcs SetATHigh
    temp2 = currentntaddrHigh
    if (!(temp0 - 0x04 >= 0)) {
        //> eor #%00000100           ;otherwise invert d2
        temp3 = temp2 xor 0x04
    }
    //> SetATHigh:   and #%00000100           ;mask out all other bits
    temp4 = temp2 and 0x04
    //> ora #$23                 ;add $2300 to the high byte and store
    temp5 = temp4 or 0x23
    //> sta $00
    memory[0x0] = temp5.toUByte()
    //> lda $01                  ;get low byte - 4, divide by 4, add offset for
    temp2 = memory[0x1].toInt()
    //> lsr                      ;attribute table and store
    temp2 = temp2 shr 1
    //> lsr
    temp2 = temp2 shr 1
    //> adc #$c0                 ;we should now have the appropriate block of
    //> sta $01                  ;attribute table in our temp address
    memory[0x1] = ((temp2 + 0xC0 + (if ((temp2 and 0x01) != 0) 1 else 0)) and 0xFF).toUByte()
    //> ldx #$00
    //> ldy VRAM_Buffer2_Offset  ;get buffer offset
    temp6 = 0x00
    temp7 = vramBuffer2Offset
    do {
        //> AttribLoop:  lda $00
        temp2 = memory[0x0].toInt()
        //> sta VRAM_Buffer2,y       ;store high byte of attribute table address
        vramBuffer2[temp7] = temp2
        //> lda $01
        temp2 = memory[0x1].toInt()
        //> clc                      ;get low byte, add 8 because we want to start
        //> adc #$08                 ;below the status bar, and store
        //> sta VRAM_Buffer2+1,y
        vramBuffer2[1 + temp7] = (temp2 + 0x08) and 0xFF
        //> sta $01                  ;also store in temp again
        memory[0x1] = ((temp2 + 0x08) and 0xFF).toUByte()
        //> lda AttributeBuffer,x    ;fetch current attribute table byte and store
        temp2 = attributeBuffer[temp6]
        //> sta VRAM_Buffer2+3,y     ;in the buffer
        vramBuffer2[3 + temp7] = temp2
        //> lda #$01
        temp2 = 0x01
        //> sta VRAM_Buffer2+2,y     ;store length of 1 in buffer
        vramBuffer2[2 + temp7] = temp2
        //> lsr
        temp2 = temp2 shr 1
        //> sta AttributeBuffer,x    ;clear current byte in attribute buffer
        attributeBuffer[temp6] = temp2
        //> iny                      ;increment buffer offset by 4 bytes
        temp7 = (temp7 + 1) and 0xFF
        //> iny
        temp7 = (temp7 + 1) and 0xFF
        //> iny
        temp7 = (temp7 + 1) and 0xFF
        //> iny
        temp7 = (temp7 + 1) and 0xFF
        //> inx                      ;increment attribute offset and check to see
        temp6 = (temp6 + 1) and 0xFF
        //> cpx #$07                 ;if we're at the end yet
        //> bcc AttribLoop
    } while (!(temp6 >= 0x07))
    //> sta VRAM_Buffer2,y       ;put null terminator at the end
    vramBuffer2[temp7] = temp2
    //> sty VRAM_Buffer2_Offset  ;store offset in case we want to do any more
    vramBuffer2Offset = temp7
    //> SetVRAMCtrl: lda #$06
    temp2 = 0x06
    //> sta VRAM_Buffer_AddrCtrl ;set buffer to $0341 and leave
    vramBufferAddrctrl = temp2
    //> rts
    return
}

// Decompiled from ColorRotation
fun colorRotation() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var areaType by MemoryByte(AreaType)
    var colorRotateOffset by MemoryByte(ColorRotateOffset)
    var frameCounter by MemoryByte(FrameCounter)
    var vramBuffer1Offset by MemoryByte(VRAM_Buffer1_Offset)
    val blankPalette by MemoryByteIndexed(BlankPalette)
    val colorRotatePalette by MemoryByteIndexed(ColorRotatePalette)
    val palette3Data by MemoryByteIndexed(Palette3Data)
    val vramBuffer1 by MemoryByteIndexed(VRAM_Buffer1)
    //> ColorRotation:
    //> lda FrameCounter         ;get frame counter
    //> and #$07                 ;mask out all but three LSB
    temp0 = frameCounter and 0x07
    //> bne ExitColorRot         ;branch if not set to zero to do this every eighth frame
    temp1 = temp0
    if (temp0 == 0) {
        //> ldx VRAM_Buffer1_Offset  ;check vram buffer offset
        //> cpx #$31
        //> bcs ExitColorRot         ;if offset over 48 bytes, branch to leave
        temp2 = vramBuffer1Offset
        if (!(vramBuffer1Offset >= 0x31)) {
            //> tay                      ;otherwise use frame counter's 3 LSB as offset here
            temp3 = temp1
            do {
                //> GetBlankPal:  lda BlankPalette,y       ;get blank palette for palette 3
                temp1 = blankPalette[temp3]
                //> sta VRAM_Buffer1,x       ;store it in the vram buffer
                vramBuffer1[temp2] = temp1
                //> inx                      ;increment offsets
                temp2 = (temp2 + 1) and 0xFF
                //> iny
                temp3 = (temp3 + 1) and 0xFF
                //> cpy #$08
                //> bcc GetBlankPal          ;do this until all bytes are copied
            } while (!(temp3 >= 0x08))
            //> ldx VRAM_Buffer1_Offset  ;get current vram buffer offset
            temp2 = vramBuffer1Offset
            //> lda #$03
            temp1 = 0x03
            //> sta $00                  ;set counter here
            memory[0x0] = temp1.toUByte()
            //> lda AreaType             ;get area type
            temp1 = areaType
            //> asl                      ;multiply by 4 to get proper offset
            temp1 = (temp1 shl 1) and 0xFF
            //> asl
            temp1 = (temp1 shl 1) and 0xFF
            //> tay                      ;save as offset here
            temp3 = temp1
            do {
                //> GetAreaPal:   lda Palette3Data,y       ;fetch palette to be written based on area type
                temp1 = palette3Data[temp3]
                //> sta VRAM_Buffer1+3,x     ;store it to overwrite blank palette in vram buffer
                vramBuffer1[3 + temp2] = temp1
                //> iny
                temp3 = (temp3 + 1) and 0xFF
                //> inx
                temp2 = (temp2 + 1) and 0xFF
                //> dec $00                  ;decrement counter
                memory[0x0] = ((memory[0x0].toInt() - 1) and 0xFF).toUByte()
                //> bpl GetAreaPal           ;do this until the palette is all copied
            } while (((memory[0x0].toInt() - 1) and 0xFF and 0x80) == 0)
            //> ldx VRAM_Buffer1_Offset  ;get current vram buffer offset
            temp2 = vramBuffer1Offset
            //> ldy ColorRotateOffset    ;get color cycling offset
            temp3 = colorRotateOffset
            //> lda ColorRotatePalette,y
            temp1 = colorRotatePalette[temp3]
            //> sta VRAM_Buffer1+4,x     ;get and store current color in second slot of palette
            vramBuffer1[4 + temp2] = temp1
            //> lda VRAM_Buffer1_Offset
            temp1 = vramBuffer1Offset
            //> clc                      ;add seven bytes to vram buffer offset
            //> adc #$07
            //> sta VRAM_Buffer1_Offset
            vramBuffer1Offset = (temp1 + 0x07) and 0xFF
            //> inc ColorRotateOffset    ;increment color cycling offset
            colorRotateOffset = (colorRotateOffset + 1) and 0xFF
            //> lda ColorRotateOffset
            temp1 = colorRotateOffset
            //> cmp #$06                 ;check to see if it's still in range
            //> bcc ExitColorRot         ;if so, branch to leave
            if (temp1 >= 0x06) {
                //> lda #$00
                temp1 = 0x00
                //> sta ColorRotateOffset    ;otherwise, init to keep it in range
                colorRotateOffset = temp1
            }
        }
    }
    //> ExitColorRot: rts                      ;leave
    return
}

// Decompiled from RemoveCoin_Axe
fun removecoinAxe() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var areaType by MemoryByte(AreaType)
    var vramBufferAddrctrl by MemoryByte(VRAM_Buffer_AddrCtrl)
    //> RemoveCoin_Axe:
    //> ldy #$41                 ;set low byte so offset points to $0341
    //> lda #$03                 ;load offset for default blank metatile
    //> ldx AreaType             ;check area type
    //> bne WriteBlankMT         ;if not water type, use offset
    temp0 = 0x03
    temp1 = areaType
    temp2 = 0x41
    if (areaType == 0) {
        //> lda #$04                 ;otherwise load offset for blank metatile used in water
        temp0 = 0x04
    }
    //> WriteBlankMT: jsr PutBlockMetatile     ;do a sub to write blank metatile to vram buffer
    putBlockMetatile(temp0, temp1, temp2)
    //> lda #$06
    temp0 = 0x06
    //> sta VRAM_Buffer_AddrCtrl ;set vram address controller to $0341 and leave
    vramBufferAddrctrl = temp0
    //> rts
    return
}

// Decompiled from ReplaceBlockMetatile
fun replaceBlockMetatile(X: Int) {
    var A: Int = 0
    var blockResidualcounter by MemoryByte(Block_ResidualCounter)
    val blockRepflag by MemoryByteIndexed(Block_RepFlag)
    //> ReplaceBlockMetatile:
    //> jsr WriteBlockMetatile    ;write metatile to vram buffer to replace block object
    writeBlockMetatile(A)
    //> inc Block_ResidualCounter ;increment unused counter (residual code)
    blockResidualcounter = (blockResidualcounter + 1) and 0xFF
    //> dec Block_RepFlag,x       ;decrement flag (residual code)
    blockRepflag[X] = (blockRepflag[X] - 1) and 0xFF
    //> rts                       ;leave
    return
}

// Decompiled from DestroyBlockMetatile
fun destroyBlockMetatile() {
    //> DestroyBlockMetatile:
    //> lda #$00       ;force blank metatile if branched/jumped to this point
}

// Decompiled from WriteBlockMetatile
fun writeBlockMetatile(A: Int) {
    var X: Int = 0
    var temp0: Int = 0
    var vramBuffer1Offset by MemoryByte(VRAM_Buffer1_Offset)
    //> WriteBlockMetatile:
    //> ldy #$03                ;load offset for blank metatile
    //> cmp #$00                ;check contents of A for blank metatile
    //> beq UseBOffset          ;branch if found (unconditional if branched from 8a6b)
    temp0 = 0x03
    if (A != 0) {
        //> ldy #$00                ;load offset for brick metatile w/ line
        temp0 = 0x00
        //> cmp #$58
        //> beq UseBOffset          ;use offset if metatile is brick with coins (w/ line)
        if (A != 0x58) {
            //> cmp #$51
            //> beq UseBOffset          ;use offset if metatile is breakable brick w/ line
            if (A != 0x51) {
                //> iny                     ;increment offset for brick metatile w/o line
                temp0 = (temp0 + 1) and 0xFF
                //> cmp #$5d
                //> beq UseBOffset          ;use offset if metatile is brick with coins (w/o line)
                if (A != 0x5D) {
                    //> cmp #$52
                    //> beq UseBOffset          ;use offset if metatile is breakable brick w/o line
                    if (A != 0x52) {
                        //> iny                     ;if any other metatile, increment offset for empty block
                        temp0 = (temp0 + 1) and 0xFF
                    }
                }
            }
        }
    }
    //> UseBOffset:  tya                     ;put Y in A
    //> ldy VRAM_Buffer1_Offset ;get vram buffer offset
    temp0 = vramBuffer1Offset
    //> iny                     ;move onto next byte
    temp0 = (temp0 + 1) and 0xFF
    //> jsr PutBlockMetatile    ;get appropriate block data and write to vram buffer
    putBlockMetatile(temp0, X, temp0)
}

// Decompiled from MoveVOffset
fun moveVOffset(Y: Int) {
    var A: Int = 0
    var vramBuffer1Offset by MemoryByte(VRAM_Buffer1_Offset)
    //> SetVRAMOffset: sta VRAM_Buffer1_Offset  ;store as new vram buffer offset
    vramBuffer1Offset = A
    //> rts
    return
}

// Decompiled from PutBlockMetatile
fun putBlockMetatile(A: Int, X: Int, Y: Int) {
    var A: Int = A
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    //> PutBlockMetatile:
    //> stx $00               ;store control bit from SprDataOffset_Ctrl
    memory[0x0] = X.toUByte()
    //> sty $01               ;store vram buffer offset for next byte
    memory[0x1] = Y.toUByte()
    //> asl
    A = (A shl 1) and 0xFF
    //> asl                   ;multiply A by four and use as X
    A = (A shl 1) and 0xFF
    //> tax
    //> ldy #$20              ;load high byte for name table 0
    //> lda $06               ;get low byte of block buffer pointer
    //> cmp #$d0              ;check to see if we're on odd-page block buffer
    //> bcc SaveHAdder        ;if not, use current high byte
    temp0 = memory[0x6].toInt()
    temp1 = A
    temp2 = 0x20
    if (memory[0x6].toInt() >= 0xD0) {
        //> ldy #$24              ;otherwise load high byte for name table 1
        temp2 = 0x24
    }
    //> SaveHAdder: sty $03               ;save high byte here
    memory[0x3] = temp2.toUByte()
    //> and #$0f              ;mask out high nybble of block buffer pointer
    temp3 = temp0 and 0x0F
    //> asl                   ;multiply by 2 to get appropriate name table low byte
    temp3 = (temp3 shl 1) and 0xFF
    //> sta $04               ;and then store it here
    memory[0x4] = temp3.toUByte()
    //> lda #$00
    temp0 = 0x00
    //> sta $05               ;initialize temp high byte
    memory[0x5] = temp0.toUByte()
    //> lda $02               ;get vertical high nybble offset used in block buffer routine
    temp0 = memory[0x2].toInt()
    //> clc
    //> adc #$20              ;add 32 pixels for the status bar
    //> asl
    //> rol $05               ;shift and rotate d7 onto d0 and d6 into carry
    memory[0x5] = (((memory[0x5].toInt() shl 1) and 0xFE or if (((temp0 + 0x20) and 0xFF and 0x80) != 0) 1 else 0) and 0xFF).toUByte()
    //> asl
    //> rol $05               ;shift and rotate d6 onto d0 and d5 into carry
    memory[0x5] = (((memory[0x5].toInt() shl 1) and 0xFE or if (((((temp0 + 0x20) and 0xFF) shl 1) and 0xFF and 0x80) != 0) 1 else 0) and 0xFF).toUByte()
    //> adc $04               ;add low byte of name table and carry to vertical high nybble
    //> sta $04               ;and store here
    memory[0x4] = ((((((((temp0 + 0x20) and 0xFF) shl 1) and 0xFF) shl 1) and 0xFF) + memory[0x4].toInt() + (if ((memory[0x5].toInt() and 0x80) != 0) 1 else 0)) and 0xFF).toUByte()
    //> lda $05               ;get whatever was in d7 and d6 of vertical high nybble
    temp0 = memory[0x5].toInt()
    //> adc #$00              ;add carry
    //> clc
    //> adc $03               ;then add high byte of name table
    //> sta $05               ;store here
    memory[0x5] = ((((temp0 + (if (((((((temp0 + 0x20) and 0xFF) shl 1) and 0xFF) shl 1) and 0xFF) + memory[0x4].toInt() + (if ((memory[0x5].toInt() and 0x80) != 0) 1 else 0) > 0xFF) 1 else 0)) and 0xFF) + memory[0x3].toInt()) and 0xFF).toUByte()
    //> ldy $01               ;get vram buffer offset to be used
    temp2 = memory[0x1].toInt()
}

// Decompiled from RemBridge
fun remBridge(X: Int, Y: Int) {
    val blockGfxData by MemoryByteIndexed(BlockGfxData)
    val vramBuffer1 by MemoryByteIndexed(VRAM_Buffer1)
    //> RemBridge:  lda BlockGfxData,x    ;write top left and top right
    //> sta VRAM_Buffer1+2,y  ;tile numbers into first spot
    vramBuffer1[2 + Y] = blockGfxData[X]
    //> lda BlockGfxData+1,x
    //> sta VRAM_Buffer1+3,y
    vramBuffer1[3 + Y] = blockGfxData[1 + X]
    //> lda BlockGfxData+2,x  ;write bottom left and bottom
    //> sta VRAM_Buffer1+7,y  ;right tiles numbers into
    vramBuffer1[7 + Y] = blockGfxData[2 + X]
    //> lda BlockGfxData+3,x  ;second spot
    //> sta VRAM_Buffer1+8,y
    vramBuffer1[8 + Y] = blockGfxData[3 + X]
    //> lda $04
    //> sta VRAM_Buffer1,y    ;write low byte of name table
    vramBuffer1[Y] = memory[0x4].toInt()
    //> clc                   ;into first slot as read
    //> adc #$20              ;add 32 bytes to value
    //> sta VRAM_Buffer1+5,y  ;write low byte of name table
    vramBuffer1[5 + Y] = (memory[0x4].toInt() + 0x20) and 0xFF
    //> lda $05               ;plus 32 bytes into second slot
    //> sta VRAM_Buffer1-1,y  ;write high byte of name
    vramBuffer1[-1 + Y] = memory[0x5].toInt()
    //> sta VRAM_Buffer1+4,y  ;table address to both slots
    vramBuffer1[4 + Y] = memory[0x5].toInt()
    //> lda #$02
    //> sta VRAM_Buffer1+1,y  ;put length of 2 in
    vramBuffer1[1 + Y] = 0x02
    //> sta VRAM_Buffer1+6,y  ;both slots
    vramBuffer1[6 + Y] = 0x02
    //> lda #$00
    //> sta VRAM_Buffer1+9,y  ;put null terminator at end
    vramBuffer1[9 + Y] = 0x00
    //> ldx $00               ;get offset control bit here
    //> rts                   ;and leave
    return
}

// Decompiled from JumpEngine
fun jumpEngine(A: Int) {
    var A: Int = A
    var temp0: Int = 0
    var temp1: Int = 0
    //> JumpEngine:
    //> asl          ;shift bit from contents of A
    A = (A shl 1) and 0xFF
    //> tay
    //> pla          ;pull saved return address from stack
    temp0 = pull()
    //> sta $04      ;save to indirect
    memory[0x4] = temp0.toUByte()
    //> pla
    temp1 = pull()
    //> sta $05
    memory[0x5] = temp1.toUByte()
    //> iny
    A = (A + 1) and 0xFF
    //> lda ($04),y  ;load pointer from indirect
    //> sta $06      ;note that if an RTS is performed in next routine
    memory[0x6] = memory[readWord(0x4) + A].toInt().toUByte()
    //> iny          ;it will return to the execution before the sub
    A = (A + 1) and 0xFF
    //> lda ($04),y  ;that called this routine
    //> sta $07
    memory[0x7] = memory[readWord(0x4) + A].toInt().toUByte()
    //> jmp ($06)    ;jump to the address we loaded
}

// Decompiled from InitializeNameTables
fun initializeNameTables() {
    var temp0: Int = 0
    var temp1: Int = 0
    var mirrorPpuCtrlReg1 by MemoryByte(Mirror_PPU_CTRL_REG1)
    var ppuStatus by MemoryByte(PPU_STATUS)
    //> InitializeNameTables:
    //> lda PPU_STATUS            ;reset flip-flop
    //> lda Mirror_PPU_CTRL_REG1  ;load mirror of ppu reg $2000
    //> ora #%00010000            ;set sprites for first 4k and background for second 4k
    temp0 = mirrorPpuCtrlReg1 or 0x10
    //> and #%11110000            ;clear rest of lower nybble, leave higher alone
    temp1 = temp0 and 0xF0
    //> jsr WritePPUReg1
    writePPUReg1(temp1)
    //> lda #$24                  ;set vram address to start of name table 1
    //> jsr WriteNTAddr
    writeNTAddr(0x24)
    //> lda #$20                  ;and then set it to name table 0
}

// Decompiled from WriteNTAddr
fun writeNTAddr(A: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var horizontalScroll by MemoryByte(HorizontalScroll)
    var ppuAddress by MemoryByte(PPU_ADDRESS)
    var ppuData by MemoryByte(PPU_DATA)
    var vramBuffer1 by MemoryByte(VRAM_Buffer1)
    var vramBuffer1Offset by MemoryByte(VRAM_Buffer1_Offset)
    var verticalScroll by MemoryByte(VerticalScroll)
    //> WriteNTAddr:  sta PPU_ADDRESS
    ppuAddress = A
    //> lda #$00
    //> sta PPU_ADDRESS
    ppuAddress = 0x00
    //> ldx #$04                  ;clear name table with blank tile #24
    //> ldy #$c0
    //> lda #$24
    temp0 = 0x04
    temp1 = 0xC0
    while (temp0 != 0) {
        do {
            //> InitNTLoop:   sta PPU_DATA              ;count out exactly 768 tiles
            ppuData = 0x24
            //> dey
            temp1 = (temp1 - 1) and 0xFF
            //> bne InitNTLoop
            //> dex
            temp0 = (temp0 - 1) and 0xFF
            //> bne InitNTLoop
        } while (temp0 != 0)
    }
    //> ldy #64                   ;now to clear the attribute table (with zero this time)
    temp1 = 0x40
    //> txa
    //> sta VRAM_Buffer1_Offset   ;init vram buffer 1 offset
    vramBuffer1Offset = temp0
    //> sta VRAM_Buffer1          ;init vram buffer 1
    vramBuffer1 = temp0
    do {
        //> InitATLoop:   sta PPU_DATA
        ppuData = temp0
        //> dey
        temp1 = (temp1 - 1) and 0xFF
        //> bne InitATLoop
    } while (temp1 != 0)
    //> sta HorizontalScroll      ;reset scroll variables
    horizontalScroll = temp0
    //> sta VerticalScroll
    verticalScroll = temp0
    //> jmp InitScroll            ;initialize scroll registers to zero
}

// Decompiled from ReadJoypads
fun readJoypads() {
    var temp0: Int = 0
    var joypadPort by MemoryByte(JOYPAD_PORT)
    //> ReadJoypads:
    //> lda #$01               ;reset and clear strobe of joypad ports
    //> sta JOYPAD_PORT
    joypadPort = 0x01
    //> lsr
    //> tax                    ;start with joypad 1's port
    //> sta JOYPAD_PORT
    joypadPort = 0x01 shr 1
    //> jsr ReadPortBits
    readPortBits(0x01 shr 1, 0x01 shr 1)
    //> inx                    ;increment for joypad 2's port
    temp0 = 0x01 shr 1
    temp0 = (temp0 + 1) and 0xFF
    // Fall-through tail call to readPortBits (reads player 2 controller)
    readPortBits(0x01 shr 1, temp0)
}

// Debug flag for controller reading
private const val DEBUG_CONTROLLER = true

// Decompiled from ReadPortBits
// Fixed version - properly accumulates bits across iterations
fun readPortBits(A: Int, X: Int) {
    var accumulated: Int = A  // Use local var that accumulates across iterations
    var count: Int = 8
    val joypadPort by MemoryByteIndexed(JOYPAD_PORT)
    val joypadBitMask by MemoryByteIndexed(JoypadBitMask)
    val savedJoypadBits by MemoryByteIndexed(SavedJoypadBits)

    //> ReadPortBits: ldy #$08
    //> PortLoop: pha / lda JOYPAD_PORT,x / ... / pla / rol / dey / bne PortLoop
    do {
        // Read bit from controller port
        val portValue = joypadPort[X]
        val bit = ((portValue shr 1) or portValue) and 0x01

        if (DEBUG_CONTROLLER) System.err.println("readPortBits: X=$X, portValue=$portValue, bit=$bit")

        // ROL: shift accumulated left, carry in from bit
        // Note: ROL puts previous bit7 into carry, carry into bit0
        // But here we're building up the byte, so bit goes into bit0 after shift
        accumulated = ((accumulated shl 1) and 0xFE) or bit

        count--
    } while (count != 0)

    if (DEBUG_CONTROLLER) System.err.println("readPortBits: X=$X, accumulated=${accumulated.toString(16)}")

    //> sta SavedJoypadBits,x  ;save controller status here always
    savedJoypadBits[X] = accumulated

    if (DEBUG_CONTROLLER && accumulated != 0) System.err.println("readPortBits: Stored ${accumulated.toString(16)} at SavedJoypadBits[$X] = ${(SavedJoypadBits + X).toString(16)}")

    //> and #%00110000         ;check for select or start
    val selectStartBits = accumulated and 0x30

    //> and JoypadBitMask,x    ;if neither saved state nor current state have these set, branch
    if ((selectStartBits and joypadBitMask[X]) != 0) {
        //> and #%11001111         ;otherwise store without select or start bits
        savedJoypadBits[X] = accumulated and 0xCF
    } else {
        //> sta JoypadBitMask,x    ;save with all bits in another place
        joypadBitMask[X] = accumulated
    }
}

// Decompiled from UpdateScreen
fun updateScreen() {
    var A: Int = 0
    var Y: Int = 0
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var mirrorPpuCtrlReg1 by MemoryByte(Mirror_PPU_CTRL_REG1)
    var ppuAddress by MemoryByte(PPU_ADDRESS)
    var ppuData by MemoryByte(PPU_DATA)
    var ppuStatus by MemoryByte(PPU_STATUS)
    //> WriteBufferToScreen:
    //> sta PPU_ADDRESS           ;store high byte of vram address
    ppuAddress = A
    //> iny
    Y = (Y + 1) and 0xFF
    //> lda ($00),y               ;load next byte (second)
    //> sta PPU_ADDRESS           ;store low byte of vram address
    ppuAddress = memory[readWord(0x0) + Y].toInt()
    //> iny
    Y = (Y + 1) and 0xFF
    //> lda ($00),y               ;load next byte (third)
    //> asl                       ;shift to left and save in stack
    //> pha
    push((memory[readWord(0x0) + Y].toInt() shl 1) and 0xFF)
    //> lda Mirror_PPU_CTRL_REG1  ;load mirror of $2000,
    //> ora #%00000100            ;set ppu to increment by 32 by default
    temp0 = mirrorPpuCtrlReg1 or 0x04
    //> bcs SetupWrites           ;if d7 of third byte was clear, ppu will
    temp1 = temp0
    if ((memory[readWord(0x0) + Y].toInt() and 0x80) == 0) {
        //> and #%11111011            ;only increment by 1
        temp2 = temp1 and 0xFB
    }
    //> SetupWrites:   jsr WritePPUReg1          ;write to register
    writePPUReg1(temp1)
    //> pla                       ;pull from stack and shift to left again
    temp1 = pull()
    //> asl
    temp1 = (temp1 shl 1) and 0xFF
    //> bcc GetLength             ;if d6 of third byte was clear, do not repeat byte
    if ((temp1 and 0x80) != 0) {
        //> ora #%00000010            ;otherwise set d1 and increment Y
        temp3 = temp1 or 0x02
        //> iny
        Y = (Y + 1) and 0xFF
    }
    //> GetLength:     lsr                       ;shift back to the right to get proper length
    temp1 = temp1 shr 1
    //> lsr                       ;note that d1 will now be in carry
    temp1 = temp1 shr 1
    //> tax
    temp4 = temp1
    while (temp4 != 0) {
        //> iny                       ;otherwise increment Y to load next byte
        Y = (Y + 1) and 0xFF
        do {
            //> OutputToVRAM:  bcs RepeatByte            ;if carry set, repeat loading the same byte
            //> RepeatByte:    lda ($00),y               ;load more data from buffer and write to vram
            temp1 = memory[readWord(0x0) + Y].toInt()
            //> sta PPU_DATA
            ppuData = temp1
            //> dex                       ;done writing?
            temp4 = (temp4 - 1) and 0xFF
            //> bne OutputToVRAM
        } while (temp4 != 0)
    }
    //> sec
    //> tya
    //> adc $00                   ;add end length plus one to the indirect at $00
    //> sta $00                   ;to allow this routine to read another set of updates
    memory[0x0] = ((Y + memory[0x0].toInt() + 1) and 0xFF).toUByte()
    //> lda #$00
    temp1 = 0x00
    //> adc $01
    //> sta $01
    memory[0x1] = ((temp1 + memory[0x1].toInt() + (if (Y + memory[0x0].toInt() + 1 > 0xFF) 1 else 0)) and 0xFF).toUByte()
    //> lda #$3f                  ;sets vram address to $3f00
    temp1 = 0x3F
    //> sta PPU_ADDRESS
    ppuAddress = temp1
    //> lda #$00
    temp1 = 0x00
    //> sta PPU_ADDRESS
    ppuAddress = temp1
    //> sta PPU_ADDRESS           ;then reinitializes it for some reason
    ppuAddress = temp1
    //> sta PPU_ADDRESS
    ppuAddress = temp1
    //> UpdateScreen:  ldx PPU_STATUS            ;reset flip-flop
    temp4 = ppuStatus
    //> ldy #$00                  ;load first byte from indirect as a pointer
    //> lda ($00),y
    temp1 = memory[readWord(0x0)].toInt()
    //> bne WriteBufferToScreen   ;if byte is zero we have no further updates to make here
}

// Decompiled from InitScroll
fun initScroll(A: Int) {
    var ppuScrollReg by MemoryByte(PPU_SCROLL_REG)
    //> InitScroll:    sta PPU_SCROLL_REG        ;store contents of A into scroll registers
    ppuScrollReg = A
    //> sta PPU_SCROLL_REG        ;and end whatever subroutine led us here
    ppuScrollReg = A
    //> rts
    return
}

// Decompiled from WritePPUReg1
fun writePPUReg1(A: Int) {
    var mirrorPpuCtrlReg1 by MemoryByte(Mirror_PPU_CTRL_REG1)
    var ppuCtrlReg1 by MemoryByte(PPU_CTRL_REG1)
    //> WritePPUReg1:
    //> sta PPU_CTRL_REG1         ;write contents of A to PPU register 1
    ppuCtrlReg1 = A
    //> sta Mirror_PPU_CTRL_REG1  ;and its mirror
    mirrorPpuCtrlReg1 = A
    //> rts
    return
}

// Decompiled from PrintStatusBarNumbers
fun printStatusBarNumbers(A: Int) {
    //> PrintStatusBarNumbers:
    //> sta $00            ;store player-specific offset
    memory[0x0] = A.toUByte()
    //> jsr OutputNumbers  ;use first nybble to print the coin display
    outputNumbers(A)
    //> lda $00            ;move high nybble to low
    //> lsr                ;and print to score display
    //> lsr
    //> lsr
    //> lsr
}

// Decompiled from OutputNumbers
fun outputNumbers(A: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var vramBuffer1Offset by MemoryByte(VRAM_Buffer1_Offset)
    val displayDigits by MemoryByteIndexed(DisplayDigits)
    val statusBarData by MemoryByteIndexed(StatusBarData)
    val statusBarOffset by MemoryByteIndexed(StatusBarOffset)
    val vramBuffer1 by MemoryByteIndexed(VRAM_Buffer1)
    //> OutputNumbers:
    //> clc                      ;add 1 to low nybble
    //> adc #$01
    //> and #%00001111           ;mask out high nybble
    temp0 = (A + 0x01) and 0xFF and 0x0F
    //> cmp #$06
    //> bcs ExitOutputN
    temp1 = temp0
    if (!(temp0 >= 0x06)) {
        //> pha                      ;save incremented value to stack for now and
        push(temp1)
        //> asl                      ;shift to left and use as offset
        temp1 = (temp1 shl 1) and 0xFF
        //> tay
        //> ldx VRAM_Buffer1_Offset  ;get current buffer pointer
        //> lda #$20                 ;put at top of screen by default
        temp1 = 0x20
        //> cpy #$00                 ;are we writing top score on title screen?
        //> bne SetupNums
        temp2 = vramBuffer1Offset
        temp3 = temp1
        if (temp1 == 0x00) {
            //> lda #$22                 ;if so, put further down on the screen
            temp1 = 0x22
        }
        //> SetupNums:   sta VRAM_Buffer1,x
        vramBuffer1[temp2] = temp1
        //> lda StatusBarData,y      ;write low vram address and length of thing
        temp1 = statusBarData[temp3]
        //> sta VRAM_Buffer1+1,x     ;we're printing to the buffer
        vramBuffer1[1 + temp2] = temp1
        //> lda StatusBarData+1,y
        temp1 = statusBarData[1 + temp3]
        //> sta VRAM_Buffer1+2,x
        vramBuffer1[2 + temp2] = temp1
        //> sta $03                  ;save length byte in counter
        memory[0x3] = temp1.toUByte()
        //> stx $02                  ;and buffer pointer elsewhere for now
        memory[0x2] = temp2.toUByte()
        //> pla                      ;pull original incremented value from stack
        temp1 = pull()
        //> tax
        //> lda StatusBarOffset,x    ;load offset to value we want to write
        temp1 = statusBarOffset[temp1]
        //> sec
        //> sbc StatusBarData+1,y    ;subtract from length byte we read before
        //> tay                      ;use value as offset to display digits
        //> ldx $02
        temp2 = memory[0x2].toInt()
        temp3 = (temp1 - statusBarData[1 + temp3]) and 0xFF
        do {
            //> DigitPLoop:  lda DisplayDigits,y      ;write digits to the buffer
            temp1 = displayDigits[temp3]
            //> sta VRAM_Buffer1+3,x
            vramBuffer1[3 + temp2] = temp1
            //> inx
            temp2 = (temp2 + 1) and 0xFF
            //> iny
            temp3 = (temp3 + 1) and 0xFF
            //> dec $03                  ;do this until all the digits are written
            memory[0x3] = ((memory[0x3].toInt() - 1) and 0xFF).toUByte()
            //> bne DigitPLoop
        } while (((memory[0x3].toInt() - 1) and 0xFF) != 0)
        //> lda #$00                 ;put null terminator at end
        temp1 = 0x00
        //> sta VRAM_Buffer1+3,x
        vramBuffer1[3 + temp2] = temp1
        //> inx                      ;increment buffer pointer by 3
        temp2 = (temp2 + 1) and 0xFF
        //> inx
        temp2 = (temp2 + 1) and 0xFF
        //> inx
        temp2 = (temp2 + 1) and 0xFF
        //> stx VRAM_Buffer1_Offset  ;store it in case we want to use it again
        vramBuffer1Offset = temp2
    }
    //> ExitOutputN: rts
    return
}

// Decompiled from DigitsMathRoutine
fun digitsMathRoutine(Y: Int) {
    var Y: Int = Y
    var temp0: Int = 0
    var temp1: Int = 0
    var operMode by MemoryByte(OperMode)
    val digitModifier by MemoryByteIndexed(DigitModifier)
    val displayDigits by MemoryByteIndexed(DisplayDigits)
    //> DigitsMathRoutine:
    //> lda OperMode              ;check mode of operation
    //> cmp #TitleScreenModeValue
    //> beq EraseDMods            ;if in title screen mode, branch to lock score
    temp0 = operMode
    if (operMode != TitleScreenModeValue) {
        //> ldx #$05
        temp1 = 0x05
        while (!flagN) {
            //> cmp #10
            //> bcs CarryOne              ;if digit greater than $09, branch to add
            if (temp0 >= 0x0A) {
                //  goto CarryOne
                return
            }
            if (!(temp0 >= 0x0A)) {
                do {
                    //> AddModLoop: lda DigitModifier,x       ;load digit amount to increment
                    temp0 = digitModifier[temp1]
                    //> clc
                    //> adc DisplayDigits,y       ;add to current digit
                    //> bmi BorrowOne             ;if result is a negative number, branch to subtract
                    //> StoreNewD:  sta DisplayDigits,y       ;store as new score or game timer digit
                    displayDigits[Y] = (temp0 + displayDigits[Y]) and 0xFF
                    //> dey                       ;move onto next digits in score or game timer
                    Y = (Y - 1) and 0xFF
                    //> dex                       ;and digit amounts to increment
                    temp1 = (temp1 - 1) and 0xFF
                    //> bpl AddModLoop            ;loop back if we're not done yet
                } while ((temp1 and 0x80) == 0)
            }
        }
    }
    //> EraseDMods: lda #$00                  ;store zero here
    temp0 = 0x00
    //> ldx #$06                  ;start with the last digit
    temp1 = 0x06
    do {
        //> EraseMLoop: sta DigitModifier-1,x     ;initialize the digit amounts to increment
        digitModifier[-1 + temp1] = temp0
        //> dex
        temp1 = (temp1 - 1) and 0xFF
        //> bpl EraseMLoop            ;do this until they're all reset, then leave
    } while ((temp1 and 0x80) == 0)
    //> rts
    return
}

// Decompiled from UpdateTopScore
fun updateTopScore() {
    //> UpdateTopScore:
    //> ldx #$05          ;start with mario's score
    //> jsr TopScoreCheck
    topScoreCheck(0x05)
    //> ldx #$0b          ;now do luigi's score
}

// Decompiled from TopScoreCheck
fun topScoreCheck(X: Int) {
    var X: Int = X
    var temp0: Int = 0
    var temp1: Int = 0
    val playerScoreDisplay by MemoryByteIndexed(PlayerScoreDisplay)
    val topScoreDisplay by MemoryByteIndexed(TopScoreDisplay)
    //> TopScoreCheck:
    //> ldy #$05                 ;start with the lowest digit
    //> sec
    temp0 = 0x05
    do {
        //> GetScoreDiff: lda PlayerScoreDisplay,x ;subtract each player digit from each high score digit
        //> sbc TopScoreDisplay,y    ;from lowest to highest, if any top score digit exceeds
        //> dex                      ;any player digit, borrow will be set until a subsequent
        X = (X - 1) and 0xFF
        //> dey                      ;subtraction clears it (player digit is higher than top)
        temp0 = (temp0 - 1) and 0xFF
        //> bpl GetScoreDiff
    } while ((temp0 and 0x80) == 0)
    //> bcc NoTopSc              ;check to see if borrow is still set, if so, no new high score
    temp1 = (playerScoreDisplay[X] - topScoreDisplay[temp0]) and 0xFF
    if (playerScoreDisplay[X] - topScoreDisplay[temp0] >= 0) {
        //> inx                      ;increment X and Y once to the start of the score
        X = (X + 1) and 0xFF
        //> iny
        temp0 = (temp0 + 1) and 0xFF
        do {
            //> CopyScore:    lda PlayerScoreDisplay,x ;store player's score digits into high score memory area
            temp1 = playerScoreDisplay[X]
            //> sta TopScoreDisplay,y
            topScoreDisplay[temp0] = temp1
            //> inx
            X = (X + 1) and 0xFF
            //> iny
            temp0 = (temp0 + 1) and 0xFF
            //> cpy #$06                 ;do this until we have stored them all
            //> bcc CopyScore
        } while (!(temp0 >= 0x06))
    }
    //> NoTopSc:      rts
    return
}

// Decompiled from InitializeMemory
fun initializeMemory(Y: Int): Int {
    var Y: Int = Y
    var temp0: Int = 0
    //> InitializeMemory:
    //> ldx #$07          ;set initial high byte to $0700-$07ff
    //> lda #$00          ;set initial low byte to start of page (at $00 of page)
    //> sta $06
    memory[0x6] = 0x00.toUByte()
    temp0 = 0x07
    while ((temp0 and 0x80) == 0) {
        //> InitPageLoop: stx $07
        memory[0x7] = temp0.toUByte()
        //> InitByteLoop: cpx #$01          ;check to see if we're on the stack ($0100-$01ff)
        //> bne InitByte      ;if not, go ahead anyway
        //> cpy #$60          ;otherwise, check to see if we're at $0160-$01ff
        //> bcs SkipByte      ;if so, skip write
        //> InitByte:     sta ($06),y       ;otherwise, initialize byte with current low byte in Y
        memory[readWord(0x6) + Y] = 0x00.toUByte()
        //> SkipByte:     dey
        Y = (Y - 1) and 0xFF
        //> cpy #$ff          ;do this until all bytes in page have been erased
        //> bne InitByteLoop
        //> dex               ;go onto the next page
        temp0 = (temp0 - 1) and 0xFF
        //> bpl InitPageLoop  ;do this until all pages of memory have been erased
    }
    //> rts
    return A
}

// Decompiled from GetAreaMusic
fun getAreaMusic() {
    var temp0: Int = 0
    var temp1: Int = 0
    var altEntranceControl by MemoryByte(AltEntranceControl)
    var areaMusicQueue by MemoryByte(AreaMusicQueue)
    var areaType by MemoryByte(AreaType)
    var cloudTypeOverride by MemoryByte(CloudTypeOverride)
    var operMode by MemoryByte(OperMode)
    var playerEntranceCtrl by MemoryByte(PlayerEntranceCtrl)
    val musicSelectData by MemoryByteIndexed(MusicSelectData)
    //> GetAreaMusic:
    //> lda OperMode           ;if in title screen mode, leave
    //> beq ExitGetM
    temp0 = operMode
    if (operMode != 0) {
        //> lda AltEntranceControl ;check for specific alternate mode of entry
        temp0 = altEntranceControl
        //> cmp #$02               ;if found, branch without checking starting position
        //> beq ChkAreaType        ;from area object data header
        if (temp0 != 0x02) {
            //> ldy #$05               ;select music for pipe intro scene by default
            //> lda PlayerEntranceCtrl ;check value from level header for certain values
            temp0 = playerEntranceCtrl
            //> cmp #$06
            //> beq StoreMusic         ;load music for pipe intro scene if header
            temp1 = 0x05
            if (temp0 != 0x06) {
                //> cmp #$07               ;start position either value $06 or $07
                //> beq StoreMusic
                if (temp0 != 0x07) {
                }
            }
        }
        //> ChkAreaType: ldy AreaType           ;load area type as offset for music bit
        temp1 = areaType
        //> lda CloudTypeOverride
        temp0 = cloudTypeOverride
        //> beq StoreMusic         ;check for cloud type override
        if (temp0 != 0) {
            //> ldy #$04               ;select music for cloud type level if found
            temp1 = 0x04
        }
        //> StoreMusic:  lda MusicSelectData,y  ;otherwise select appropriate music for level type
        temp0 = musicSelectData[temp1]
        //> sta AreaMusicQueue     ;store in queue and leave
        areaMusicQueue = temp0
    }
    //> ExitGetM:    rts
    return
}

// Decompiled from TerminateGame
fun terminateGame() {
    var temp0: Int = 0
    var continueWorld by MemoryByte(ContinueWorld)
    var eventMusicQueue by MemoryByte(EventMusicQueue)
    var fetchNewGameTimerFlag by MemoryByte(FetchNewGameTimerFlag)
    var gameEngineSubroutine by MemoryByte(GameEngineSubroutine)
    var operMode by MemoryByte(OperMode)
    var opermodeTask by MemoryByte(OperMode_Task)
    var playerSize by MemoryByte(PlayerSize)
    var playerStatus by MemoryByte(PlayerStatus)
    var screenTimer by MemoryByte(ScreenTimer)
    var timerControl by MemoryByte(TimerControl)
    var worldNumber by MemoryByte(WorldNumber)
    //> TerminateGame:
    //> lda #Silence          ;silence music
    //> sta EventMusicQueue
    eventMusicQueue = Silence
    //> jsr TransposePlayers  ;check if other player can keep
    transposePlayers()
    //> bcc ContinueGame      ;going, and do so if possible
    temp0 = Silence
    if (flagC) {
        //> lda WorldNumber       ;otherwise put world number of current
        temp0 = worldNumber
        //> sta ContinueWorld     ;player into secret continue function variable
        continueWorld = temp0
        //> lda #$00
        temp0 = 0x00
        //> asl                   ;residual ASL instruction
        temp0 = (temp0 shl 1) and 0xFF
        //> sta OperMode_Task     ;reset all modes to title screen and
        opermodeTask = temp0
        //> sta ScreenTimer       ;leave
        screenTimer = temp0
        //> sta OperMode
        operMode = temp0
        //> rts
        return
    } else {
        //> ContinueGame:
        //> jsr LoadAreaPointer       ;update level pointer with
        loadAreaPointer(temp0)
        //> lda #$01                  ;actual world and area numbers, then
        temp0 = 0x01
        //> sta PlayerSize            ;reset player's size, status, and
        playerSize = temp0
        //> inc FetchNewGameTimerFlag ;set game timer flag to reload
        fetchNewGameTimerFlag = (fetchNewGameTimerFlag + 1) and 0xFF
        //> lda #$00                  ;game timer from header
        temp0 = 0x00
        //> sta TimerControl          ;also set flag for timers to count again
        timerControl = temp0
        //> sta PlayerStatus
        playerStatus = temp0
        //> sta GameEngineSubroutine  ;reset task for game core
        gameEngineSubroutine = temp0
        //> sta OperMode_Task         ;set modes and leave
        opermodeTask = temp0
        //> lda #$01                  ;if in game over mode, switch back to
        temp0 = 0x01
        //> sta OperMode              ;game mode, because game is still on
        operMode = temp0
    }
    //> GameIsOn:  rts
    return
}

// Decompiled from TransposePlayers
fun transposePlayers() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var currentPlayer by MemoryByte(CurrentPlayer)
    var numberOfPlayers by MemoryByte(NumberOfPlayers)
    var offscrNumberoflives by MemoryByte(OffScr_NumberofLives)
    val offscreenPlayerInfo by MemoryByteIndexed(OffscreenPlayerInfo)
    val onscreenPlayerInfo by MemoryByteIndexed(OnscreenPlayerInfo)
    //> TransposePlayers:
    //> sec                       ;set carry flag by default to end game
    //> lda NumberOfPlayers       ;if only a 1 player game, leave
    //> beq ExTrans
    temp0 = numberOfPlayers
    if (numberOfPlayers != 0) {
        //> lda OffScr_NumberofLives  ;does offscreen player have any lives left?
        temp0 = offscrNumberoflives
        //> bmi ExTrans               ;branch if not
        if ((temp0 and 0x80) == 0) {
            //> lda CurrentPlayer         ;invert bit to update
            temp0 = currentPlayer
            //> eor #%00000001            ;which player is on the screen
            temp1 = temp0 xor 0x01
            //> sta CurrentPlayer
            currentPlayer = temp1
            //> ldx #$06
            temp2 = 0x06
            do {
                //> TransLoop: lda OnscreenPlayerInfo,x    ;transpose the information
                temp0 = onscreenPlayerInfo[temp2]
                //> pha                         ;of the onscreen player
                push(temp0)
                //> lda OffscreenPlayerInfo,x   ;with that of the offscreen player
                temp0 = offscreenPlayerInfo[temp2]
                //> sta OnscreenPlayerInfo,x
                onscreenPlayerInfo[temp2] = temp0
                //> pla
                temp0 = pull()
                //> sta OffscreenPlayerInfo,x
                offscreenPlayerInfo[temp2] = temp0
                //> dex
                temp2 = (temp2 - 1) and 0xFF
                //> bpl TransLoop
            } while ((temp2 and 0x80) == 0)
            //> clc            ;clear carry flag to get game going
        }
    }
    //> ExTrans:   rts
    return
}

// Decompiled from DoNothing1
fun doNothing1() {
    //> DoNothing1:
    //> lda #$ff       ;this is residual code, this value is
    //> sta $06c9      ;not used anywhere in the program
    memory[0x6C9] = 0xFF.toUByte()
}

// Decompiled from DoNothing2
fun doNothing2() {
    //> DoNothing2:
    //> rts
    return
}

// Decompiled from AreaParserTaskHandler
fun areaParserTaskHandler() {
    var temp0: Int = 0
    var temp1: Int = 0
    var areaParserTaskNum by MemoryByte(AreaParserTaskNum)
    //> AreaParserTaskHandler:
    //> ldy AreaParserTaskNum     ;check number of tasks here
    //> bne DoAPTasks             ;if already set, go ahead
    temp0 = areaParserTaskNum
    if (areaParserTaskNum == 0) {
        //> ldy #$08
        temp0 = 0x08
        //> sty AreaParserTaskNum     ;otherwise, set eight by default
        areaParserTaskNum = temp0
    }
    //> DoAPTasks:    dey
    temp0 = (temp0 - 1) and 0xFF
    //> tya
    //> jsr AreaParserTasks
    areaParserTasks()
    //> dec AreaParserTaskNum     ;if all tasks not complete do not
    areaParserTaskNum = (areaParserTaskNum - 1) and 0xFF
    //> bne SkipATRender          ;render attribute table yet
    temp1 = temp0
    if (((areaParserTaskNum - 1) and 0xFF) == 0) {
        //> jsr RenderAttributeTables
        renderAttributeTables()
    }
    //> SkipATRender: rts
    return
}

// Decompiled from AreaParserTasks
fun areaParserTasks() {
    //> AreaParserTasks:
    //> jsr JumpEngine
    when (A) {
        0 -> {
            incrementColumnPos()
        }
        1 -> {
            renderAreaGraphics()
        }
        2 -> {
            renderAreaGraphics()
        }
        3 -> {
            areaParserCore()
        }
        4 -> {
            incrementColumnPos()
        }
        5 -> {
            renderAreaGraphics()
        }
        6 -> {
            renderAreaGraphics()
        }
        7 -> {
            areaParserCore()
        }
        else -> {
            // Unknown JumpEngine index
        }
    }
    return
}

// Decompiled from ProcessAreaData
fun processAreaData() {
    var Y: Int = 0
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var areaObjectPageLoc by MemoryByte(AreaObjectPageLoc)
    var areaObjectPageSel by MemoryByte(AreaObjectPageSel)
    var backloadingFlag by MemoryByte(BackloadingFlag)
    var behindAreaParserFlag by MemoryByte(BehindAreaParserFlag)
    var currentPageLoc by MemoryByte(CurrentPageLoc)
    var objectOffset by MemoryByte(ObjectOffset)
    val areaObjectLength by MemoryByteIndexed(AreaObjectLength)
    //> ProcessAreaData:
    //> ldx #$02                 ;start at the end of area object buffer
    temp0 = 0x02
    while ((temp0 and 0x80) == 0) {
        //> lda AreaObjectLength,x   ;check area object buffer flag
        //> bpl RdyDecode            ;if buffer not negative, branch, otherwise
        if (!((areaObjectLength[temp0] and 0x80) != 0)) {
            //  goto RdyDecode
            return
        }
        temp1 = areaObjectLength[temp0]
        if ((areaObjectLength[temp0] and 0x80) != 0) {
            //> iny
            Y = (Y + 1) and 0xFF
            //> lda (AreaData),y         ;get second byte of area object
            temp1 = memory[readWord(AreaData) + Y].toInt()
            //> asl                      ;check for page select bit (d7), branch if not set
            temp1 = (temp1 shl 1) and 0xFF
            //> bcc Chk1Row13
            if ((temp1 and 0x80) != 0) {
                //> lda AreaObjectPageSel    ;check page select
                temp1 = areaObjectPageSel
                //> bne Chk1Row13
                if (temp1 == 0) {
                    //> inc AreaObjectPageSel    ;if not already set, set it now
                    areaObjectPageSel = (areaObjectPageSel + 1) and 0xFF
                    //> inc AreaObjectPageLoc    ;and increment page location
                    areaObjectPageLoc = (areaObjectPageLoc + 1) and 0xFF
                }
            }
            //> Chk1Row13:  dey
            Y = (Y - 1) and 0xFF
            //> lda (AreaData),y         ;reread first byte of level object
            temp1 = memory[readWord(AreaData) + Y].toInt()
            //> and #$0f                 ;mask out high nybble
            temp2 = temp1 and 0x0F
            //> cmp #$0d                 ;row 13?
            //> bne Chk1Row14
            temp1 = temp2
            if (temp2 == 0x0D) {
                //> iny                      ;if so, reread second byte of level object
                Y = (Y + 1) and 0xFF
                //> lda (AreaData),y
                temp1 = memory[readWord(AreaData) + Y].toInt()
                //> dey                      ;decrement to get ready to read first byte
                Y = (Y - 1) and 0xFF
                //> and #%01000000           ;check for d6 set (if not, object is page control)
                temp3 = temp1 and 0x40
                //> bne CheckRear
                temp1 = temp3
                if (temp3 == 0) {
                    //> lda AreaObjectPageSel    ;if page select is set, do not reread
                    temp1 = areaObjectPageSel
                    //> bne CheckRear
                    if (temp1 == 0) {
                        //> iny                      ;if d6 not set, reread second byte
                        Y = (Y + 1) and 0xFF
                        //> lda (AreaData),y
                        temp1 = memory[readWord(AreaData) + Y].toInt()
                        //> and #%00011111           ;mask out all but 5 LSB and store in page control
                        temp4 = temp1 and 0x1F
                        //> sta AreaObjectPageLoc
                        areaObjectPageLoc = temp4
                        //> inc AreaObjectPageSel    ;increment page select
                        areaObjectPageSel = (areaObjectPageSel + 1) and 0xFF
                        //> jmp NextAObj
                    }
                }
            }
            //> Chk1Row14:  cmp #$0e                 ;row 14?
            //> bne CheckRear
            if (temp1 == 0x0E) {
                //> lda BackloadingFlag      ;check flag for saved page number and branch if set
                temp1 = backloadingFlag
                //> bne RdyDecode            ;to render the object (otherwise bg might not look right)
                if (!(temp1 == 0)) {
                    //  goto RdyDecode
                    return
                }
                if (temp1 == 0) {
                }
            }
            //> CheckRear:  lda AreaObjectPageLoc    ;check to see if current page of level object is
            temp1 = areaObjectPageLoc
            //> cmp CurrentPageLoc       ;behind current page of renderer
            //> bcc SetBehind            ;if so branch
            if (temp1 >= currentPageLoc) {
            }
        }
        //> RdyDecode:  jsr DecodeAreaData       ;do sub and do not turn on flag
        decodeAreaData(temp0)
        //> jmp ChkLength
        //> SetBehind:  inc BehindAreaParserFlag ;turn on flag if object is behind renderer
        behindAreaParserFlag = (behindAreaParserFlag + 1) and 0xFF
        //> NextAObj:   jsr IncAreaObjOffset     ;increment buffer offset and move on
        incAreaObjOffset()
        //> ChkLength:  ldx ObjectOffset         ;get buffer offset
        temp0 = objectOffset
        //> lda AreaObjectLength,x   ;check object length for anything stored here
        temp1 = areaObjectLength[temp0]
        //> bmi ProcLoopb            ;if not, branch to handle loopback
        if ((temp1 and 0x80) == 0) {
            //> dec AreaObjectLength,x   ;otherwise decrement length or get rid of it
            areaObjectLength[temp0] = (areaObjectLength[temp0] - 1) and 0xFF
        }
        //> ProcLoopb:  dex                      ;decrement buffer offset
        temp0 = (temp0 - 1) and 0xFF
        //> bpl ProcADLoop           ;and loopback unless exceeded buffer
    }
    //> lda BehindAreaParserFlag ;check for flag set if objects were behind renderer
    temp1 = behindAreaParserFlag
    //> bne ProcessAreaData      ;branch if true to load more level data, otherwise
    //> lda BackloadingFlag      ;check for flag set if starting right of page $00
    temp1 = backloadingFlag
    //> bne ProcessAreaData      ;branch if true to load more level data, otherwise leave
    //> EndAParse:  rts
    return
}

// Decompiled from IncAreaObjOffset
fun incAreaObjOffset() {
    var areaDataOffset by MemoryByte(AreaDataOffset)
    var areaObjectPageSel by MemoryByte(AreaObjectPageSel)
    //> IncAreaObjOffset:
    //> inc AreaDataOffset    ;increment offset of level pointer
    areaDataOffset = (areaDataOffset + 1) and 0xFF
    //> inc AreaDataOffset
    areaDataOffset = (areaDataOffset + 1) and 0xFF
    //> lda #$00              ;reset page select
    //> sta AreaObjectPageSel
    areaObjectPageSel = 0x00
    //> rts
    return
}

// Decompiled from DecodeAreaData
fun decodeAreaData(X: Int) {
    //> EndAParse:  rts
    return
}

// Decompiled from KillEnemies
fun killEnemies(A: Int) {
    var temp0: Int = 0
    val enemyFlag by MemoryByteIndexed(Enemy_Flag)
    val enemyId by MemoryByteIndexed(Enemy_ID)
    //> KillEnemies:
    //> sta $00           ;store identifier here
    memory[0x0] = A.toUByte()
    //> lda #$00
    //> ldx #$04          ;check for identifier in enemy object buffer
    temp0 = 0x04
    while ((temp0 and 0x80) == 0) {
        //> sta Enemy_Flag,x  ;if found, deactivate enemy object flag
        enemyFlag[temp0] = 0x00
        do {
            //> KillELoop: ldy Enemy_ID,x
            //> cpy $00           ;if not found, branch
            //> bne NoKillE
            //> NoKillE:   dex               ;do this until all slots are checked
            temp0 = (temp0 - 1) and 0xFF
            //> bpl KillELoop
        } while ((temp0 and 0x80) == 0)
    }
    //> rts
    return
}

// Decompiled from RenderSidewaysPipe
fun renderSidewaysPipe(X: Int, Y: Int) {
    var Y: Int = Y
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    val areaObjectLength by MemoryByteIndexed(AreaObjectLength)
    val metatileBuffer by MemoryByteIndexed(MetatileBuffer)
    val sidePipeBottomPart by MemoryByteIndexed(SidePipeBottomPart)
    val sidePipeShaftData by MemoryByteIndexed(SidePipeShaftData)
    val sidePipeTopPart by MemoryByteIndexed(SidePipeTopPart)
    //> RenderSidewaysPipe:
    //> dey                       ;decrement twice to make room for shaft at bottom
    Y = (Y - 1) and 0xFF
    //> dey                       ;and store here for now as vertical length
    Y = (Y - 1) and 0xFF
    //> sty $05
    memory[0x5] = Y.toUByte()
    //> ldy AreaObjectLength,x    ;get length left over and store here
    //> sty $06
    memory[0x6] = (areaObjectLength[X] and 0xFF).toUByte()
    //> ldx $05                   ;get vertical length plus one, use as buffer offset
    //> inx
    temp0 = memory[0x5].toInt()
    temp0 = (temp0 + 1) and 0xFF
    //> lda SidePipeShaftData,y   ;check for value $00 based on horizontal offset
    //> cmp #$00
    //> beq DrawSidePart          ;if found, do not draw the vertical pipe shaft
    temp1 = sidePipeShaftData[areaObjectLength[X]]
    temp2 = areaObjectLength[X]
    if (sidePipeShaftData[areaObjectLength[X]] != 0) {
        //> ldx #$00
        temp0 = 0x00
        //> ldy $05                   ;init buffer offset and get vertical length
        temp2 = memory[0x5].toInt()
        //> jsr RenderUnderPart       ;and render vertical shaft using tile number in A
        renderUnderPart(temp1, temp0, temp2)
        //> clc                       ;clear carry flag to be used by IntroPipe
    }
    //> DrawSidePart: ldy $06                   ;render side pipe part at the bottom
    temp2 = memory[0x6].toInt()
    //> lda SidePipeTopPart,y
    temp1 = sidePipeTopPart[temp2]
    //> sta MetatileBuffer,x      ;note that the pipe parts are stored
    metatileBuffer[temp0] = temp1
    //> lda SidePipeBottomPart,y  ;backwards horizontally
    temp1 = sidePipeBottomPart[temp2]
    //> sta MetatileBuffer+1,x
    metatileBuffer[1 + temp0] = temp1
    //> rts
    return
}

// Decompiled from GetPipeHeight
fun getPipeHeight(X: Int) {
    var temp0: Int = 0
    val areaObjectLength by MemoryByteIndexed(AreaObjectLength)
    //> GetPipeHeight:
    //> ldy #$01       ;check for length loaded, if not, load
    //> jsr ChkLrgObjFixedLength ;pipe length of 2 (horizontal)
    chkLrgObjFixedLength(X, 0x01)
    //> jsr GetLrgObjAttrib
    getLrgObjAttrib(X)
    //> tya            ;get saved lower nybble as height
    //> and #$07       ;save only the three lower bits as
    temp0 = 0x01 and 0x07
    //> sta $06        ;vertical length, then load Y with
    memory[0x6] = temp0.toUByte()
    //> ldy AreaObjectLength,x    ;length left over
    //> rts
    return
}

// Decompiled from FindEmptyEnemySlot
fun findEmptyEnemySlot(): Int {
    var temp0: Int = 0
    val enemyFlag by MemoryByteIndexed(Enemy_Flag)
    //> FindEmptyEnemySlot:
    //> ldx #$00          ;start at first enemy slot
    temp0 = 0x00
    while (temp0 != 0x05) {
        do {
            //> EmptyChkLoop: clc               ;clear carry flag by default
            //> lda Enemy_Flag,x  ;check enemy buffer for nonzero
            //> beq ExitEmptyChk  ;if zero, leave
            //> inx
            temp0 = (temp0 + 1) and 0xFF
            //> cpx #$05          ;if nonzero, check next value
            //> bne EmptyChkLoop
        } while (temp0 != 0x05)
    }
    //> ExitEmptyChk: rts               ;if all values nonzero, carry flag is set
    return A
}

// Decompiled from GetAreaObjectID
fun getAreaObjectID() {
    //> GetAreaObjectID:
    //> lda $00    ;get value saved from area parser routine
    //> sec
    //> sbc #$00   ;possibly residual code
    //> tay        ;save to Y
    //> ExitDecBlock: rts
    return
}

// Decompiled from RenderUnderPart
fun renderUnderPart(A: Int, X: Int, Y: Int) {
    var X: Int = X
    var areaObjectHeight by MemoryByte(AreaObjectHeight)
    val metatileBuffer by MemoryByteIndexed(MetatileBuffer)
    while (!flagN) {
        //> cpy #$17
        //> beq WaitOneRow        ;if middle part (tree ledge), wait until next row
        if (Y != 0x17) {
            //> cpy #$1a
            //> beq WaitOneRow        ;if middle part (mushroom ledge), wait until next row
            if (Y != 0x1A) {
                //> cpy #$c0
                //> beq DrawThisRow       ;if question block w/ coin, overwrite
                if (Y != 0xC0) {
                    //> cpy #$c0
                    //> bcs WaitOneRow        ;if any other metatile with palette 3, wait until next row
                    if (!(Y >= 0xC0)) {
                        //> cpy #$54
                        //> bne DrawThisRow       ;if cracked rock terrain, overwrite
                        if (Y == 0x54) {
                            //> cmp #$50
                            //> beq WaitOneRow        ;if stem top of mushroom, wait until next row
                            if (A != 0x50) {
                            }
                        }
                    }
                }
                //> DrawThisRow: sta MetatileBuffer,x  ;render contents of A from routine that called this
                metatileBuffer[X] = A
            }
        }
        //> WaitOneRow:  inx
        X = (X + 1) and 0xFF
        //> cpx #$0d              ;stop rendering if we're at the bottom of the screen
        //> bcs ExitUPartR
        if (!(X >= 0x0D)) {
            do {
                //> RenderUnderPart:
                //> sty AreaObjectHeight  ;store vertical length to render
                areaObjectHeight = Y
                //> ldy MetatileBuffer,x  ;check current spot to see if there's something
                //> beq DrawThisRow       ;we need to keep, if nothing, go ahead
                //> ldy AreaObjectHeight  ;decrement, and stop rendering if there is no more length
                //> dey
                areaObjectHeight = (areaObjectHeight - 1) and 0xFF
                //> bpl RenderUnderPart
            } while ((areaObjectHeight and 0x80) == 0)
        }
    }
    //> ExitUPartR:  rts
    return
}

// Decompiled from ChkLrgObjLength
fun chkLrgObjLength() {
    var X: Int = 0
    //> ChkLrgObjLength:
    //> jsr GetLrgObjAttrib     ;get row location and size (length if branched to from here)
    getLrgObjAttrib(X)
}

// Decompiled from ChkLrgObjFixedLength
fun chkLrgObjFixedLength(X: Int, Y: Int) {
    var temp0: Int = 0
    val areaObjectLength by MemoryByteIndexed(AreaObjectLength)
    //> ChkLrgObjFixedLength:
    //> lda AreaObjectLength,x  ;check for set length counter
    //> clc                     ;clear carry flag for not just starting
    //> bpl LenSet              ;if counter not set, load it, otherwise leave alone
    temp0 = areaObjectLength[X]
    if ((areaObjectLength[X] and 0x80) != 0) {
        //> tya                     ;save length into length counter
        //> sta AreaObjectLength,x
        areaObjectLength[X] = Y
        //> sec                     ;set carry flag if just starting
    }
    //> LenSet: rts
    return
}

// Decompiled from GetLrgObjAttrib
fun getLrgObjAttrib(X: Int): Int {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    val areaObjOffsetBuffer by MemoryByteIndexed(AreaObjOffsetBuffer)
    //> GetLrgObjAttrib:
    //> ldy AreaObjOffsetBuffer,x ;get offset saved from area obj decoding routine
    //> lda (AreaData),y          ;get first byte of level object
    //> and #%00001111
    temp0 = memory[readWord(AreaData) + areaObjOffsetBuffer[X]].toInt() and 0x0F
    //> sta $07                   ;save row location
    memory[0x7] = temp0.toUByte()
    //> iny
    temp1 = areaObjOffsetBuffer[X]
    temp1 = (temp1 + 1) and 0xFF
    //> lda (AreaData),y          ;get next byte, save lower nybble (length or height)
    //> and #%00001111            ;as Y, then leave
    temp2 = memory[readWord(AreaData) + temp1].toInt() and 0x0F
    //> tay
    //> rts
    return A
}

// Decompiled from GetAreaObjXPosition
fun getAreaObjXPosition(): Int {
    var currentColumnPos by MemoryByte(CurrentColumnPos)
    //> GetAreaObjXPosition:
    //> lda CurrentColumnPos    ;multiply current offset where we're at by 16
    //> asl                     ;to obtain horizontal pixel coordinate
    currentColumnPos = (currentColumnPos shl 1) and 0xFF
    //> asl
    currentColumnPos = (currentColumnPos shl 1) and 0xFF
    //> asl
    currentColumnPos = (currentColumnPos shl 1) and 0xFF
    //> asl
    currentColumnPos = (currentColumnPos shl 1) and 0xFF
    //> rts
    return A
}

// Decompiled from GetAreaObjYPosition
fun getAreaObjYPosition(): Int {
    //> GetAreaObjYPosition:
    //> lda $07  ;multiply value by 16
    //> asl
    //> asl      ;this will give us the proper vertical pixel coordinate
    //> asl
    //> asl
    //> clc
    //> adc #32  ;add 32 pixels for the status bar
    //> rts
    return A
}

// Decompiled from GetBlockBufferAddr
fun getBlockBufferAddr(A: Int) {
    var A: Int = A
    var temp0: Int = 0
    var temp1: Int = 0
    val blockBufferAddr by MemoryByteIndexed(BlockBufferAddr)
    //> GetBlockBufferAddr:
    //> pha                      ;take value of A, save
    push(A)
    //> lsr                      ;move high nybble to low
    A = A shr 1
    //> lsr
    A = A shr 1
    //> lsr
    A = A shr 1
    //> lsr
    A = A shr 1
    //> tay                      ;use nybble as pointer to high byte
    //> lda BlockBufferAddr+2,y  ;of indirect here
    //> sta $07
    memory[0x7] = (blockBufferAddr[2 + A] and 0xFF).toUByte()
    //> pla
    temp0 = pull()
    //> and #%00001111           ;pull from stack, mask out high nybble
    temp1 = temp0 and 0x0F
    //> clc
    //> adc BlockBufferAddr,y    ;add to low byte
    //> sta $06                  ;store here and leave
    memory[0x6] = ((temp1 + blockBufferAddr[A]) and 0xFF).toUByte()
    //> rts
    return
}

// Decompiled from LoadAreaPointer
// Fixed: Use the result from findAreaPointer(), not the parameter A
fun loadAreaPointer(areaNum: Int) {
    var areaPointer by MemoryByte(AreaPointer)
    //> LoadAreaPointer:
    //> jsr FindAreaPointer  ;find it and store it here
    val result = findAreaPointer()
    //> sta AreaPointer
    areaPointer = result
}

// Decompiled from GetAreaType
// Fixed: Proper carry handling for ASL/ROL sequence
// This extracts bits 5-6 from A and shifts them to bits 0-1
fun getAreaType(inputA: Int): Int {
    var areaType by MemoryByte(AreaType)
    //> GetAreaType: and #%01100000       ;mask out all but d6 and d5
    val masked = inputA and 0x60
    // Bits 5-6 of inputA are now in bits 5-6 of masked
    // We need to shift them to bits 0-1
    // ASL shifts left, putting bit 7 into carry
    // ROL rotates left through carry, putting carry into bit 0 and bit 7 into carry
    // After ASL + 3 ROLs, bits 5-6 end up in bits 0-1
    //
    // Simpler: just shift right by 5
    val result = (masked shr 5) and 0x03
    //> sta AreaType         ;save 2 MSB as area type
    areaType = result
    //> rts
    return inputA
}

// Decompiled from FindAreaPointer
// Fixed: Actually implement the lookup logic
fun findAreaPointer(): Int {
    var areaNumber by MemoryByte(AreaNumber)
    var worldNumber by MemoryByte(WorldNumber)
    val areaAddrOffsets by MemoryByteIndexed(AreaAddrOffsets)
    val worldAddrOffsets by MemoryByteIndexed(WorldAddrOffsets)
    //> FindAreaPointer:
    //> ldy WorldNumber        ;load offset from world variable
    //> lda WorldAddrOffsets,y
    val worldOffset = worldAddrOffsets[worldNumber]
    //> clc                    ;add area number used to find data
    //> adc AreaNumber
    val index = (worldOffset + areaNumber) and 0xFF
    //> tay
    //> lda AreaAddrOffsets,y  ;from there we have our area pointer
    A = areaAddrOffsets[index]
    //> rts
    return A
}

// Decompiled from GetAreaDataAddrs
fun getAreaDataAddrs() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var temp5: Int = 0
    var temp6: Int = 0
    var temp7: Int = 0
    var temp8: Int = 0
    var areaAddrsLOffset by MemoryByte(AreaAddrsLOffset)
    var areaDataHigh by MemoryByte(AreaDataHigh)
    var areaDataLow by MemoryByte(AreaDataLow)
    var areaPointer by MemoryByte(AreaPointer)
    var areaStyle by MemoryByte(AreaStyle)
    var areaType by MemoryByte(AreaType)
    var backgroundColorCtrl by MemoryByte(BackgroundColorCtrl)
    var backgroundScenery by MemoryByte(BackgroundScenery)
    var cloudTypeOverride by MemoryByte(CloudTypeOverride)
    var enemyDataHigh by MemoryByte(EnemyDataHigh)
    var enemyDataLow by MemoryByte(EnemyDataLow)
    var foregroundScenery by MemoryByte(ForegroundScenery)
    var gameTimerSetting by MemoryByte(GameTimerSetting)
    var playerEntranceCtrl by MemoryByte(PlayerEntranceCtrl)
    var terrainControl by MemoryByte(TerrainControl)
    val areaDataAddrHigh by MemoryByteIndexed(AreaDataAddrHigh)
    val areaDataAddrLow by MemoryByteIndexed(AreaDataAddrLow)
    val areaDataHOffsets by MemoryByteIndexed(AreaDataHOffsets)
    val enemyAddrHOffsets by MemoryByteIndexed(EnemyAddrHOffsets)
    val enemyDataAddrHigh by MemoryByteIndexed(EnemyDataAddrHigh)
    val enemyDataAddrLow by MemoryByteIndexed(EnemyDataAddrLow)
    //> GetAreaDataAddrs:
    //> lda AreaPointer          ;use 2 MSB for Y
    //> jsr GetAreaType
    getAreaType(areaPointer)
    //> tay
    //> lda AreaPointer          ;mask out all but 5 LSB
    //> and #%00011111
    temp0 = areaPointer and 0x1F
    //> sta AreaAddrsLOffset     ;save as low offset
    areaAddrsLOffset = temp0
    //> lda EnemyAddrHOffsets,y  ;load base value with 2 altered MSB,
    //> clc                      ;then add base value to 5 LSB, result
    //> adc AreaAddrsLOffset     ;becomes offset for level data
    //> tay
    //> lda EnemyDataAddrLow,y   ;use offset to load pointer
    //> sta EnemyDataLow
    enemyDataLow = enemyDataAddrLow[(enemyAddrHOffsets[areaPointer] + areaAddrsLOffset) and 0xFF]
    //> lda EnemyDataAddrHigh,y
    //> sta EnemyDataHigh
    enemyDataHigh = enemyDataAddrHigh[(enemyAddrHOffsets[areaPointer] + areaAddrsLOffset) and 0xFF]
    //> ldy AreaType             ;use area type as offset
    //> lda AreaDataHOffsets,y   ;do the same thing but with different base value
    //> clc
    //> adc AreaAddrsLOffset
    //> tay
    //> lda AreaDataAddrLow,y    ;use this offset to load another pointer
    //> sta AreaDataLow
    areaDataLow = areaDataAddrLow[(areaDataHOffsets[areaType] + areaAddrsLOffset) and 0xFF]
    //> lda AreaDataAddrHigh,y
    //> sta AreaDataHigh
    areaDataHigh = areaDataAddrHigh[(areaDataHOffsets[areaType] + areaAddrsLOffset) and 0xFF]
    //> ldy #$00                 ;load first byte of header
    //> lda (AreaData),y
    //> pha                      ;save it to the stack for now
    push(memory[readWord(AreaData)].toInt())
    //> and #%00000111           ;save 3 LSB for foreground scenery or bg color control
    temp1 = memory[readWord(AreaData)].toInt() and 0x07
    //> cmp #$04
    //> bcc StoreFore
    temp2 = temp1
    temp3 = 0x00
    if (temp1 >= 0x04) {
        //> sta BackgroundColorCtrl  ;if 4 or greater, save value here as bg color control
        backgroundColorCtrl = temp2
        //> lda #$00
        temp2 = 0x00
    }
    //> StoreFore:  sta ForegroundScenery    ;if less, save value here as foreground scenery
    foregroundScenery = temp2
    //> pla                      ;pull byte from stack and push it back
    temp2 = pull()
    //> pha
    push(temp2)
    //> and #%00111000           ;save player entrance control bits
    temp4 = temp2 and 0x38
    //> lsr                      ;shift bits over to LSBs
    temp4 = temp4 shr 1
    //> lsr
    temp4 = temp4 shr 1
    //> lsr
    temp4 = temp4 shr 1
    //> sta PlayerEntranceCtrl       ;save value here as player entrance control
    playerEntranceCtrl = temp4
    //> pla                      ;pull byte again but do not push it back
    temp2 = pull()
    //> and #%11000000           ;save 2 MSB for game timer setting
    temp5 = temp2 and 0xC0
    //> clc
    //> rol                      ;rotate bits over to LSBs
    temp5 = (temp5 shl 1) and 0xFE
    //> rol
    temp5 = (temp5 shl 1) and 0xFE or if ((temp5 and 0x80) != 0) 1 else 0
    //> rol
    temp5 = (temp5 shl 1) and 0xFE or if ((temp5 and 0x80) != 0) 1 else 0
    //> sta GameTimerSetting     ;save value here as game timer setting
    gameTimerSetting = temp5
    //> iny
    temp3 = (temp3 + 1) and 0xFF
    //> lda (AreaData),y         ;load second byte of header
    temp2 = memory[readWord(AreaData) + temp3].toInt()
    //> pha                      ;save to stack
    push(temp2)
    //> and #%00001111           ;mask out all but lower nybble
    temp6 = temp2 and 0x0F
    //> sta TerrainControl
    terrainControl = temp6
    //> pla                      ;pull and push byte to copy it to A
    temp2 = pull()
    //> pha
    push(temp2)
    //> and #%00110000           ;save 2 MSB for background scenery type
    temp7 = temp2 and 0x30
    //> lsr
    temp7 = temp7 shr 1
    //> lsr                      ;shift bits to LSBs
    temp7 = temp7 shr 1
    //> lsr
    temp7 = temp7 shr 1
    //> lsr
    temp7 = temp7 shr 1
    //> sta BackgroundScenery    ;save as background scenery
    backgroundScenery = temp7
    //> pla
    temp2 = pull()
    //> and #%11000000
    temp8 = temp2 and 0xC0
    //> clc
    //> rol                      ;rotate bits over to LSBs
    temp8 = (temp8 shl 1) and 0xFE
    //> rol
    temp8 = (temp8 shl 1) and 0xFE or if ((temp8 and 0x80) != 0) 1 else 0
    //> rol
    temp8 = (temp8 shl 1) and 0xFE or if ((temp8 and 0x80) != 0) 1 else 0
    //> cmp #%00000011           ;if set to 3, store here
    //> bne StoreStyle           ;and nullify other value
    temp2 = temp8
    if (temp8 == 0x03) {
        //> sta CloudTypeOverride    ;otherwise store value in other place
        cloudTypeOverride = temp2
        //> lda #$00
        temp2 = 0x00
    }
    //> StoreStyle: sta AreaStyle
    areaStyle = temp2
    //> lda AreaDataLow          ;increment area data address by 2 bytes
    temp2 = areaDataLow
    //> clc
    //> adc #$02
    //> sta AreaDataLow
    areaDataLow = (temp2 + 0x02) and 0xFF
    //> lda AreaDataHigh
    temp2 = areaDataHigh
    //> adc #$00
    //> sta AreaDataHigh
    areaDataHigh = (temp2 + (if (temp2 + 0x02 > 0xFF) 1 else 0)) and 0xFF
    //> rts
    return
}

// Decompiled from GameCoreRoutine
fun gameCoreRoutine() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var aBButtons by MemoryByte(A_B_Buttons)
    var currentPlayer by MemoryByte(CurrentPlayer)
    var frameCounter by MemoryByte(FrameCounter)
    var intervalTimerControl by MemoryByte(IntervalTimerControl)
    var leftRightButtons by MemoryByte(Left_Right_Buttons)
    var objectOffset by MemoryByte(ObjectOffset)
    var opermodeTask by MemoryByte(OperMode_Task)
    var playerYHighpos by MemoryByte(Player_Y_HighPos)
    var previousaBButtons by MemoryByte(PreviousA_B_Buttons)
    var starInvincibleTimer by MemoryByte(StarInvincibleTimer)
    val savedJoypadBits by MemoryByteIndexed(SavedJoypadBits)
    //> GameCoreRoutine:
    //> ldx CurrentPlayer          ;get which player is on the screen
    //> lda SavedJoypadBits,x      ;use appropriate player's controller bits
    //> sta SavedJoypadBits        ;as the master controller bits
    val inputBits = savedJoypadBits[currentPlayer]
    if (DEBUG_CONTROLLER && inputBits != 0) System.err.println("gameCoreRoutine: currentPlayer=$currentPlayer, savedJoypadBits[cp]=${inputBits.toString(16)}")
    savedJoypadBits[0] = inputBits
    //> jsr GameRoutines           ;execute one of many possible subs
    if (DEBUG_GAME_CORE) { System.err.print("GCR: gameRoutines()..."); System.err.flush() }
    gameRoutines()
    if (DEBUG_GAME_CORE) System.err.println("done")
    //> lda OperMode_Task          ;check major task of operating mode
    //> cmp #$03                   ;if we are supposed to be here,
    //> bcs GameEngine             ;branch to the game engine itself
    temp0 = opermodeTask
    temp1 = currentPlayer
    if (!(opermodeTask >= 0x03)) {
        //> rts
        if (DEBUG_GAME_CORE) System.err.println("GCR: early exit, task<3")
        return
    } else {
        //> GameEngine:
        //> jsr ProcFireball_Bubble    ;process fireballs and air bubbles
        if (DEBUG_GAME_CORE) { System.err.print("GCR: procfireballBubble()..."); System.err.flush() }
        procfireballBubble()
        if (DEBUG_GAME_CORE) System.err.println("done")
        //> ldx #$00
        temp1 = 0x00
    }
    do {
        //> ProcELoop:    stx ObjectOffset           ;put incremented offset in X as enemy object offset
        objectOffset = temp1
        //> jsr EnemiesAndLoopsCore    ;process enemy objects
        if (DEBUG_GAME_CORE) { System.err.print("GCR: enemiesAndLoopsCore($temp1)..."); System.err.flush() }
        enemiesAndLoopsCore(temp1)
        if (DEBUG_GAME_CORE) System.err.println("done")
        //> jsr FloateyNumbersRoutine  ;process floatey numbers
        floateyNumbersRoutine(temp1)
        //> inx
        temp1 = (temp1 + 1) and 0xFF
        //> cpx #$06                   ;do these two subroutines until the whole buffer is done
        //> bne ProcELoop
    } while (temp1 != 0x06)
    //> jsr GetPlayerOffscreenBits ;get offscreen bits for player object
    getPlayerOffscreenBits()
    //> jsr RelativePlayerPosition ;get relative coordinates for player object
    relativePlayerPosition()
    //> jsr PlayerGfxHandler       ;draw the player
    playerGfxHandler()
    //> jsr BlockObjMT_Updater     ;replace block objects with metatiles if necessary
    blockobjmtUpdater()
    //> ldx #$01
    temp1 = 0x01
    //> stx ObjectOffset           ;set offset for second
    objectOffset = temp1
    //> jsr BlockObjectsCore       ;process second block object
    blockObjectsCore(temp1)
    //> dex
    temp1 = (temp1 - 1) and 0xFF
    //> stx ObjectOffset           ;set offset for first
    objectOffset = temp1
    //> jsr BlockObjectsCore       ;process first block object
    blockObjectsCore(temp1)
    //> jsr MiscObjectsCore        ;process misc objects (hammer, jumping coins)
    miscObjectsCore()
    //> jsr ProcessCannons         ;process bullet bill cannons
    processCannons()
    //> jsr ProcessWhirlpools      ;process whirlpools
    processWhirlpools()
    //> jsr FlagpoleRoutine        ;process the flagpole
    flagpoleRoutine()
    //> jsr RunGameTimer           ;count down the game timer
    runGameTimer()
    //> jsr ColorRotation          ;cycle one of the background colors
    colorRotation()
    //> lda Player_Y_HighPos
    temp0 = playerYHighpos
    //> cmp #$02                   ;if player is below the screen, don't bother with the music
    //> bpl NoChgMus
    if (temp0 - 0x02 < 0) {
        //> lda StarInvincibleTimer    ;if star mario invincibility timer at zero,
        temp0 = starInvincibleTimer
        //> beq ClrPlrPal              ;skip this part
        if (temp0 != 0) {
            //> cmp #$04
            //> bne NoChgMus               ;if not yet at a certain point, continue
            if (temp0 == 0x04) {
                //> lda IntervalTimerControl   ;if interval timer not yet expired,
                temp0 = intervalTimerControl
                //> bne NoChgMus               ;branch ahead, don't bother with the music
                if (temp0 == 0) {
                    //> jsr GetAreaMusic           ;to re-attain appropriate level music
                    getAreaMusic()
                }
            }
        }
    }
    //> NoChgMus:     ldy StarInvincibleTimer    ;get invincibility timer
    //> lda FrameCounter           ;get frame counter
    temp0 = frameCounter
    //> cpy #$08                   ;if timer still above certain point,
    //> bcs CycleTwo               ;branch to cycle player's palette quickly
    if (starInvincibleTimer >= 0x08) {
        //  goto CycleTwo
        return
    }
    temp2 = starInvincibleTimer
    if (!(starInvincibleTimer >= 0x08)) {
        //> lsr                        ;otherwise, divide by 8 to cycle every eighth frame
        temp0 = temp0 shr 1
        //> lsr
        temp0 = temp0 shr 1
    }
    //> CycleTwo:     lsr                        ;if branched here, divide by 2 to cycle every other frame
    temp0 = temp0 shr 1
    //> jsr CyclePlayerPalette     ;do sub to cycle the palette (note: shares fire flower code)
    cyclePlayerPalette(temp0)
    //> jmp SaveAB                 ;then skip this sub to finish up the game engine
    //> ClrPlrPal:    jsr ResetPalStar           ;do sub to clear player's palette bits in attributes
    resetPalStar()
    //> SaveAB:       lda A_B_Buttons            ;save current A and B button
    temp0 = aBButtons
    //> sta PreviousA_B_Buttons    ;into temp variable to be used on next frame
    previousaBButtons = temp0
    //> lda #$00
    temp0 = 0x00
    //> sta Left_Right_Buttons     ;nullify left and right buttons temp variable
    leftRightButtons = temp0
}

// Decompiled from UpdScrollVar
fun updScrollVar() {
    var temp0: Int = 0
    var areaParserTaskNum by MemoryByte(AreaParserTaskNum)
    var scrollThirtyTwo by MemoryByte(ScrollThirtyTwo)
    var vramBuffer2Offset by MemoryByte(VRAM_Buffer2_Offset)
    var vramBufferAddrctrl by MemoryByte(VRAM_Buffer_AddrCtrl)
    //> UpdScrollVar: lda VRAM_Buffer_AddrCtrl
    //> cmp #$06                   ;if vram address controller set to 6 (one of two $0341s)
    //> beq ExitEng                ;then branch to leave
    temp0 = vramBufferAddrctrl
    if (vramBufferAddrctrl != 0x06) {
        //> lda AreaParserTaskNum      ;otherwise check number of tasks
        temp0 = areaParserTaskNum
        //> bne RunParser
        if (temp0 == 0) {
            //> lda ScrollThirtyTwo        ;get horizontal scroll in 0-31 or $00-$20 range
            temp0 = scrollThirtyTwo
            //> cmp #$20                   ;check to see if exceeded $21
            //> bmi ExitEng                ;branch to leave if not
            if (!(temp0 - 0x20 < 0)) {
                //> lda ScrollThirtyTwo
                temp0 = scrollThirtyTwo
                //> sbc #$20                   ;otherwise subtract $20 to set appropriately
                //> sta ScrollThirtyTwo        ;and store
                scrollThirtyTwo = (temp0 - 0x20 - (if (temp0 >= 0x20) 0 else 1)) and 0xFF
                //> lda #$00                   ;reset vram buffer offset used in conjunction with
                temp0 = 0x00
                //> sta VRAM_Buffer2_Offset    ;level graphics buffer at $0341-$035f
                vramBuffer2Offset = temp0
            } else {
                //> ExitEng:      rts                        ;and after all that, we're finally done!
                return
            }
        }
        //> RunParser:    jsr AreaParserTaskHandler  ;update the name table with more level graphics
        areaParserTaskHandler()
    }
}

// Decompiled from ScrollHandler
fun scrollHandler() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var leftRightButtons by MemoryByte(Left_Right_Buttons)
    var platformXScroll by MemoryByte(Platform_X_Scroll)
    var playerPageloc by MemoryByte(Player_PageLoc)
    var playerPosForscroll by MemoryByte(Player_Pos_ForScroll)
    var playerXPosition by MemoryByte(Player_X_Position)
    var playerXScroll by MemoryByte(Player_X_Scroll)
    var playerXSpeed by MemoryByte(Player_X_Speed)
    var scrollAmount by MemoryByte(ScrollAmount)
    var scrollLock by MemoryByte(ScrollLock)
    var sideCollisionTimer by MemoryByte(SideCollisionTimer)
    val offscrJoypadBitsData by MemoryByteIndexed(OffscrJoypadBitsData)
    val screenedgePageloc by MemoryByteIndexed(ScreenEdge_PageLoc)
    val screenedgeXPos by MemoryByteIndexed(ScreenEdge_X_Pos)
    val xSubtracterdata by MemoryByteIndexed(X_SubtracterData)
    //> ScrollHandler:
    //> lda Player_X_Scroll       ;load value saved here
    //> clc
    //> adc Platform_X_Scroll     ;add value used by left/right platforms
    //> sta Player_X_Scroll       ;save as new value here to impose force on scroll
    playerXScroll = (playerXScroll + platformXScroll) and 0xFF
    //> lda ScrollLock            ;check scroll lock flag
    //> bne InitScrlAmt           ;skip a bunch of code here if set
    temp0 = scrollLock
    if (scrollLock == 0) {
        //> lda Player_Pos_ForScroll
        temp0 = playerPosForscroll
        //> cmp #$50                  ;check player's horizontal screen position
        //> bcc InitScrlAmt           ;if less than 80 pixels to the right, branch
        if (temp0 >= 0x50) {
            //> lda SideCollisionTimer    ;if timer related to player's side collision
            temp0 = sideCollisionTimer
            //> bne InitScrlAmt           ;not expired, branch
            if (temp0 == 0) {
                //> ldy Player_X_Scroll       ;get value and decrement by one
                //> dey                       ;if value originally set to zero or otherwise
                playerXScroll = (playerXScroll - 1) and 0xFF
                //> bmi InitScrlAmt           ;negative for left movement, branch
                temp1 = playerXScroll
                if ((playerXScroll and 0x80) == 0) {
                    //> iny
                    temp1 = (temp1 + 1) and 0xFF
                    //> cpy #$02                  ;if value $01, branch and do not decrement
                    //> bcc ChkNearMid
                    if (temp1 >= 0x02) {
                        //> dey                       ;otherwise decrement by one
                        temp1 = (temp1 - 1) and 0xFF
                    }
                    //> ChkNearMid: lda Player_Pos_ForScroll
                    temp0 = playerPosForscroll
                    //> cmp #$70                  ;check player's horizontal screen position
                    //> bcc ScrollScreen          ;if less than 112 pixels to the right, branch
                    if (!(temp0 >= 0x70)) {
                        //  goto ScrollScreen
                        return
                    }
                    //> ldy Player_X_Scroll       ;otherwise get original value undecremented
                    temp1 = playerXScroll
                }
            }
        }
    }
    //> InitScrlAmt:  lda #$00
    temp0 = 0x00
    //> sta ScrollAmount          ;initialize value here
    scrollAmount = temp0
    //> ChkPOffscr:   ldx #$00                  ;set X for player offset
    //> jsr GetXOffscreenBits     ;get horizontal offscreen bits for player
    getXOffscreenBits(0x00)
    //> sta $00                   ;save them here
    memory[0x0] = temp0.toUByte()
    //> ldy #$00                  ;load default offset (left side)
    temp1 = 0x00
    //> asl                       ;if d7 of offscreen bits are set,
    temp0 = (temp0 shl 1) and 0xFF
    //> bcs KeepOnscr             ;branch with default offset
    temp2 = 0x00
    if ((temp0 and 0x80) == 0) {
        //> iny                         ;otherwise use different offset (right side)
        temp1 = (temp1 + 1) and 0xFF
        //> lda $00
        temp0 = memory[0x0].toInt()
        //> and #%00100000              ;check offscreen bits for d5 set
        temp3 = temp0 and 0x20
        //> beq InitPlatScrl            ;if not set, branch ahead of this part
        temp0 = temp3
        if (temp3 != 0) {
        } else {
            //> InitPlatScrl: lda #$00                    ;nullify platform force imposed on scroll
            temp0 = 0x00
            //> sta Platform_X_Scroll
            platformXScroll = temp0
            //> rts
            return
        }
    }
    //> KeepOnscr:    lda ScreenEdge_X_Pos,y      ;get left or right side coordinate based on offset
    temp0 = screenedgeXPos[temp1]
    //> sec
    //> sbc X_SubtracterData,y      ;subtract amount based on offset
    //> sta Player_X_Position       ;store as player position to prevent movement further
    playerXPosition = (temp0 - xSubtracterdata[temp1]) and 0xFF
    //> lda ScreenEdge_PageLoc,y    ;get left or right page location based on offset
    temp0 = screenedgePageloc[temp1]
    //> sbc #$00                    ;subtract borrow
    //> sta Player_PageLoc          ;save as player's page location
    playerPageloc = (temp0 - (if (temp0 - xSubtracterdata[temp1] >= 0) 0 else 1)) and 0xFF
    //> lda Left_Right_Buttons      ;check saved controller bits
    temp0 = leftRightButtons
    //> cmp OffscrJoypadBitsData,y  ;against bits based on offset
    //> beq InitPlatScrl            ;if not equal, branch
    if (temp0 != offscrJoypadBitsData[temp1]) {
        //> lda #$00
        temp0 = 0x00
        //> sta Player_X_Speed          ;otherwise nullify horizontal speed of player
        playerXSpeed = temp0
    }
}

// Decompiled from ScrollScreen
fun scrollScreen(Y: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var temp5: Int = 0
    var temp6: Int = 0
    var horizontalScroll by MemoryByte(HorizontalScroll)
    var leftRightButtons by MemoryByte(Left_Right_Buttons)
    var mirrorPpuCtrlReg1 by MemoryByte(Mirror_PPU_CTRL_REG1)
    var platformXScroll by MemoryByte(Platform_X_Scroll)
    var playerPageloc by MemoryByte(Player_PageLoc)
    var playerXPosition by MemoryByte(Player_X_Position)
    var playerXSpeed by MemoryByte(Player_X_Speed)
    var screenleftPageloc by MemoryByte(ScreenLeft_PageLoc)
    var screenleftXPos by MemoryByte(ScreenLeft_X_Pos)
    var scrollAmount by MemoryByte(ScrollAmount)
    var scrollIntervalTimer by MemoryByte(ScrollIntervalTimer)
    var scrollThirtyTwo by MemoryByte(ScrollThirtyTwo)
    val offscrJoypadBitsData by MemoryByteIndexed(OffscrJoypadBitsData)
    val screenedgePageloc by MemoryByteIndexed(ScreenEdge_PageLoc)
    val screenedgeXPos by MemoryByteIndexed(ScreenEdge_X_Pos)
    val xSubtracterdata by MemoryByteIndexed(X_SubtracterData)
    //> ScrollScreen:
    //> tya
    //> sta ScrollAmount          ;save value here
    scrollAmount = Y
    //> clc
    //> adc ScrollThirtyTwo       ;add to value already set here
    //> sta ScrollThirtyTwo       ;save as new value here
    scrollThirtyTwo = (Y + scrollThirtyTwo) and 0xFF
    //> tya
    //> clc
    //> adc ScreenLeft_X_Pos      ;add to left side coordinate
    //> sta ScreenLeft_X_Pos      ;save as new left side coordinate
    screenleftXPos = (Y + screenleftXPos) and 0xFF
    //> sta HorizontalScroll      ;save here also
    horizontalScroll = (Y + screenleftXPos) and 0xFF
    //> lda ScreenLeft_PageLoc
    //> adc #$00                  ;add carry to page location for left
    //> sta ScreenLeft_PageLoc    ;side of the screen
    screenleftPageloc = (screenleftPageloc + (if (Y + screenleftXPos > 0xFF) 1 else 0)) and 0xFF
    //> and #$01                  ;get LSB of page location
    temp0 = (screenleftPageloc + (if (Y + screenleftXPos > 0xFF) 1 else 0)) and 0xFF and 0x01
    //> sta $00                   ;save as temp variable for PPU register 1 mirror
    memory[0x0] = temp0.toUByte()
    //> lda Mirror_PPU_CTRL_REG1  ;get PPU register 1 mirror
    //> and #%11111110            ;save all bits except d0
    temp1 = mirrorPpuCtrlReg1 and 0xFE
    //> ora $00                   ;get saved bit here and save in PPU register 1
    temp2 = temp1 or memory[0x0].toInt()
    //> sta Mirror_PPU_CTRL_REG1  ;mirror to be used to set name table later
    mirrorPpuCtrlReg1 = temp2
    //> jsr GetScreenPosition     ;figure out where the right side is
    getScreenPosition()
    //> lda #$08
    //> sta ScrollIntervalTimer   ;set scroll timer (residual, not used elsewhere)
    scrollIntervalTimer = 0x08
    //> jmp ChkPOffscr            ;skip this part
    //> ChkPOffscr:   ldx #$00                  ;set X for player offset
    //> jsr GetXOffscreenBits     ;get horizontal offscreen bits for player
    getXOffscreenBits(0x00)
    //> sta $00                   ;save them here
    memory[0x0] = 0x08.toUByte()
    //> ldy #$00                  ;load default offset (left side)
    //> asl                       ;if d7 of offscreen bits are set,
    //> bcs KeepOnscr             ;branch with default offset
    temp3 = (0x08 shl 1) and 0xFF
    temp4 = 0x00
    temp5 = 0x00
    if ((0x08 and 0x80) == 0) {
        //> iny                         ;otherwise use different offset (right side)
        temp5 = (temp5 + 1) and 0xFF
        //> lda $00
        temp3 = memory[0x0].toInt()
        //> and #%00100000              ;check offscreen bits for d5 set
        temp6 = temp3 and 0x20
        //> beq InitPlatScrl            ;if not set, branch ahead of this part
        temp3 = temp6
        if (temp6 != 0) {
        } else {
            //> InitPlatScrl: lda #$00                    ;nullify platform force imposed on scroll
            temp3 = 0x00
            //> sta Platform_X_Scroll
            platformXScroll = temp3
            //> rts
            return
        }
    }
    //> KeepOnscr:    lda ScreenEdge_X_Pos,y      ;get left or right side coordinate based on offset
    temp3 = screenedgeXPos[temp5]
    //> sec
    //> sbc X_SubtracterData,y      ;subtract amount based on offset
    //> sta Player_X_Position       ;store as player position to prevent movement further
    playerXPosition = (temp3 - xSubtracterdata[temp5]) and 0xFF
    //> lda ScreenEdge_PageLoc,y    ;get left or right page location based on offset
    temp3 = screenedgePageloc[temp5]
    //> sbc #$00                    ;subtract borrow
    //> sta Player_PageLoc          ;save as player's page location
    playerPageloc = (temp3 - (if (temp3 - xSubtracterdata[temp5] >= 0) 0 else 1)) and 0xFF
    //> lda Left_Right_Buttons      ;check saved controller bits
    temp3 = leftRightButtons
    //> cmp OffscrJoypadBitsData,y  ;against bits based on offset
    //> beq InitPlatScrl            ;if not equal, branch
    if (temp3 != offscrJoypadBitsData[temp5]) {
        //> lda #$00
        temp3 = 0x00
        //> sta Player_X_Speed          ;otherwise nullify horizontal speed of player
        playerXSpeed = temp3
    }
}

// Decompiled from GetScreenPosition
fun getScreenPosition(): Int {
    var screenleftPageloc by MemoryByte(ScreenLeft_PageLoc)
    var screenleftXPos by MemoryByte(ScreenLeft_X_Pos)
    var screenrightPageloc by MemoryByte(ScreenRight_PageLoc)
    var screenrightXPos by MemoryByte(ScreenRight_X_Pos)
    //> GetScreenPosition:
    //> lda ScreenLeft_X_Pos    ;get coordinate of screen's left boundary
    //> clc
    //> adc #$ff                ;add 255 pixels
    //> sta ScreenRight_X_Pos   ;store as coordinate of screen's right boundary
    screenrightXPos = (screenleftXPos + 0xFF) and 0xFF
    //> lda ScreenLeft_PageLoc  ;get page number where left boundary is
    //> adc #$00                ;add carry from before
    //> sta ScreenRight_PageLoc ;store as page number where right boundary is
    screenrightPageloc = (screenleftPageloc + (if (screenleftXPos + 0xFF > 0xFF) 1 else 0)) and 0xFF
    //> rts
    return A
}

// Decompiled from Entrance_GameTimerSetup
fun entranceGametimersetup() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var altEntranceControl by MemoryByte(AltEntranceControl)
    var areaType by MemoryByte(AreaType)
    var blockYPosition by MemoryByte(Block_Y_Position)
    var fetchNewGameTimerFlag by MemoryByte(FetchNewGameTimerFlag)
    var gameEngineSubroutine by MemoryByte(GameEngineSubroutine)
    var gameTimerSetting by MemoryByte(GameTimerSetting)
    var halfwayPage by MemoryByte(HalfwayPage)
    var joypadOverride by MemoryByte(JoypadOverride)
    var playerEntranceCtrl by MemoryByte(PlayerEntranceCtrl)
    var playerFacingDir by MemoryByte(PlayerFacingDir)
    var playerCollisionbits by MemoryByte(Player_CollisionBits)
    var playerPageloc by MemoryByte(Player_PageLoc)
    var playerSprattrib by MemoryByte(Player_SprAttrib)
    var playerState by MemoryByte(Player_State)
    var playerXPosition by MemoryByte(Player_X_Position)
    var playerYHighpos by MemoryByte(Player_Y_HighPos)
    var playerYPosition by MemoryByte(Player_Y_Position)
    var screenleftPageloc by MemoryByte(ScreenLeft_PageLoc)
    var starInvincibleTimer by MemoryByte(StarInvincibleTimer)
    var swimmingFlag by MemoryByte(SwimmingFlag)
    var verticalForceDown by MemoryByte(VerticalForceDown)
    val altYPosOffset by MemoryByteIndexed(AltYPosOffset)
    val gameTimerData by MemoryByteIndexed(GameTimerData)
    val gameTimerDisplay by MemoryByteIndexed(GameTimerDisplay)
    val playerBGPriorityData by MemoryByteIndexed(PlayerBGPriorityData)
    val playerstartingXPos by MemoryByteIndexed(PlayerStarting_X_Pos)
    val playerstartingYPos by MemoryByteIndexed(PlayerStarting_Y_Pos)
    //> Entrance_GameTimerSetup:
    //> lda ScreenLeft_PageLoc      ;set current page for area objects
    //> sta Player_PageLoc          ;as page location for player
    playerPageloc = screenleftPageloc
    //> lda #$28                    ;store value here
    //> sta VerticalForceDown       ;for fractional movement downwards if necessary
    verticalForceDown = 0x28
    //> lda #$01                    ;set high byte of player position and
    //> sta PlayerFacingDir         ;set facing direction so that player faces right
    playerFacingDir = 0x01
    //> sta Player_Y_HighPos
    playerYHighpos = 0x01
    //> lda #$00                    ;set player state to on the ground by default
    //> sta Player_State
    playerState = 0x00
    //> dec Player_CollisionBits    ;initialize player's collision bits
    playerCollisionbits = (playerCollisionbits - 1) and 0xFF
    //> ldy #$00                    ;initialize halfway page
    //> sty HalfwayPage
    halfwayPage = 0x00
    //> lda AreaType                ;check area type
    //> bne ChkStPos                ;if water type, set swimming flag, otherwise do not set
    temp0 = areaType
    temp1 = 0x00
    if (areaType == 0) {
        //> iny
        temp1 = (temp1 + 1) and 0xFF
    }
    //> ChkStPos: sty SwimmingFlag
    swimmingFlag = temp1
    //> ldx PlayerEntranceCtrl      ;get starting position loaded from header
    //> ldy AltEntranceControl      ;check alternate mode of entry flag for 0 or 1
    temp1 = altEntranceControl
    //> beq SetStPos
    temp2 = playerEntranceCtrl
    if (temp1 != 0) {
        //> cpy #$01
        //> beq SetStPos
        if (temp1 != 0x01) {
            //> ldx AltYPosOffset-2,y       ;if not 0 or 1, override $0710 with new offset in X
            temp2 = altYPosOffset[-2 + temp1]
        }
    }
    //> SetStPos: lda PlayerStarting_X_Pos,y  ;load appropriate horizontal position
    temp0 = playerstartingXPos[temp1]
    //> sta Player_X_Position       ;and vertical positions for the player, using
    playerXPosition = temp0
    //> lda PlayerStarting_Y_Pos,x  ;AltEntranceControl as offset for horizontal and either $0710
    temp0 = playerstartingYPos[temp2]
    if (DEBUG_ENTRANCE) {
        System.err.println("entranceGametimersetup: Setting player position - X[alt=$temp1]=${playerXPosition.toString(16)}, Y[entrCtrl=$temp2]=${temp0.toString(16)}")
    }
    //> sta Player_Y_Position       ;or value that overwrote $0710 as offset for vertical
    playerYPosition = temp0
    //> lda PlayerBGPriorityData,x
    temp0 = playerBGPriorityData[temp2]
    //> sta Player_SprAttrib        ;set player sprite attributes using offset in X
    playerSprattrib = temp0
    //> jsr GetPlayerColors         ;get appropriate player palette
    getPlayerColors()
    //> ldy GameTimerSetting        ;get timer control value from header
    temp1 = gameTimerSetting
    //> beq ChkOverR                ;if set to zero, branch (do not use dummy byte for this)
    if (temp1 != 0) {
        //> lda FetchNewGameTimerFlag   ;do we need to set the game timer? if not, use
        temp0 = fetchNewGameTimerFlag
        //> beq ChkOverR                ;old game timer setting
        if (temp0 != 0) {
            //> lda GameTimerData,y         ;if game timer is set and game timer flag is also set,
            temp0 = gameTimerData[temp1]
            //> sta GameTimerDisplay        ;use value of game timer control for first digit of game timer
            gameTimerDisplay[0] = temp0
            //> lda #$01
            temp0 = 0x01
            //> sta GameTimerDisplay+2      ;set last digit of game timer to 1
            gameTimerDisplay[2] = temp0
            //> lsr
            temp0 = temp0 shr 1
            //> sta GameTimerDisplay+1      ;set second digit of game timer
            gameTimerDisplay[1] = temp0
            //> sta FetchNewGameTimerFlag   ;clear flag for game timer reset
            fetchNewGameTimerFlag = temp0
            //> sta StarInvincibleTimer     ;clear star mario timer
            starInvincibleTimer = temp0
        }
    }
    //> ChkOverR: ldy JoypadOverride          ;if controller bits not set, branch to skip this part
    temp1 = joypadOverride
    //> beq ChkSwimE
    if (temp1 != 0) {
        //> lda #$03                    ;set player state to climbing
        temp0 = 0x03
        //> sta Player_State
        playerState = temp0
        //> ldx #$00                    ;set offset for first slot, for block object
        temp2 = 0x00
        //> jsr InitBlock_XY_Pos
        initblockXyPos(temp2)
        //> lda #$f0                    ;set vertical coordinate for block object
        temp0 = 0xF0
        //> sta Block_Y_Position
        blockYPosition = temp0
        //> ldx #$05                    ;set offset in X for last enemy object buffer slot
        temp2 = 0x05
        //> ldy #$00                    ;set offset in Y for object coordinates used earlier
        temp1 = 0x00
        //> jsr Setup_Vine              ;do a sub to grow vine
        setupVine(temp2, temp1)
    }
    //> ChkSwimE: ldy AreaType                ;if level not water-type,
    temp1 = areaType
    //> bne SetPESub                ;skip this subroutine
    if (temp1 == 0) {
        //> jsr SetupBubble             ;otherwise, execute sub to set up air bubbles
        setupBubble(temp2)
    }
    //> SetPESub: lda #$07                    ;set to run player entrance subroutine
    temp0 = 0x07
    //> sta GameEngineSubroutine    ;on the next frame of game engine
    gameEngineSubroutine = temp0
    //> rts
    return
}

// Decompiled from PlayerEntrance - manually simplified for correct control flow
fun playerEntrance() {
    var altEntranceControl by MemoryByte(AltEntranceControl)
    var joypadOverride by MemoryByte(JoypadOverride)
    var playerEntranceCtrl by MemoryByte(PlayerEntranceCtrl)
    var playerFacingDir by MemoryByte(PlayerFacingDir)
    var playerSprattrib by MemoryByte(Player_SprAttrib)
    var playerState by MemoryByte(Player_State)
    var playerXPosition by MemoryByte(Player_X_Position)
    var playerYPosition by MemoryByte(Player_Y_Position)
    var vineHeight by MemoryByte(VineHeight)
    var disableCollisionDet by MemoryByte(DisableCollisionDet)
    var gameEngineSubroutine by MemoryByte(GameEngineSubroutine)
    var changeAreaTimer by MemoryByte(ChangeAreaTimer)
    var disableIntermediate by MemoryByte(DisableIntermediate)
    var areaNumber by MemoryByte(AreaNumber)
    var fetchNewGameTimerFlag by MemoryByte(FetchNewGameTimerFlag)
    var halfwayPage by MemoryByte(HalfwayPage)
    var eventMusicQueue by MemoryByte(EventMusicQueue)

    // Check for alternate entry mode
    if (altEntranceControl == 0x02) {
        // EntrMode2 - entering from pipe or with vine
        if (joypadOverride == 0) {
            // Move player upwards (pipe entry)
            movePlayerYAxis(0xFF)  // -1
            if (playerYPosition >= 0x91) {
                return  // Not risen enough yet
            }
            // Fall through to PlayerRdy below
        } else {
            // VineEntr - entering with vine
            if (vineHeight != 0x60) {
                return  // Vine not at max height yet
            }
            // Continue with vine entry logic
            val yPos = playerYPosition
            var controlVal = 0x01
            var disCol = 0x00
            if (yPos >= 0x99) {
                playerState = 0x03  // Climbing
                disCol = 0x01
                controlVal = 0x08
            }
            disableCollisionDet = disCol
            autoControlPlayer(controlVal)
            if (playerXPosition < 0x48) {
                return  // Not far enough right yet
            }
            // Fall through to PlayerRdy
        }
    } else {
        // Normal entry mode (not from pipe/vine)
        if (playerYPosition < 0x30) {
            // Player Y position above threshold, auto control
            autoControlPlayer(0x00)
            return
        }

        // Check entry bits from header
        val entranceCtrl = playerEntranceCtrl
        if (entranceCtrl != 0x06 && entranceCtrl != 0x07) {
            // PlayerRdy - go straight to normal gameplay
            // (Common case for level 1-1 start)
            gameEngineSubroutine = 0x08  // PlayerCtrlRoutine
            playerFacingDir = 0x01
            altEntranceControl = 0x00
            disableCollisionDet = 0x00
            joypadOverride = 0x00
            return
        }

        // ChkBehPipe - pipe intro code
        if (playerSprattrib == 0) {
            // Force player to walk right
            autoControlPlayer(0x01)
            return
        }

        // IntroEntr - move player through pipe
        enterSidePipe()
        changeAreaTimer = (changeAreaTimer - 1) and 0xFF
        if (changeAreaTimer != 0) {
            return  // Timer not expired yet
        }

        // Timer expired, go to next area
        disableIntermediate = (disableIntermediate + 1) and 0xFF
        // Fall through to NextArea
        areaNumber = (areaNumber + 1) and 0xFF
        loadAreaPointer(0)
        fetchNewGameTimerFlag = (fetchNewGameTimerFlag + 1) and 0xFF
        chgAreaMode()
        halfwayPage = 0
        eventMusicQueue = Silence
        return
    }

    // PlayerRdy - set up for normal gameplay
    gameEngineSubroutine = 0x08  // PlayerCtrlRoutine
    playerFacingDir = 0x01
    altEntranceControl = 0x00
    disableCollisionDet = 0x00
    joypadOverride = 0x00
}

// Decompiled from GameRoutines
fun gameRoutines() {
    var gameEngineSubroutine by MemoryByte(GameEngineSubroutine)
    //> GameRoutines:
    //> lda GameEngineSubroutine  ;run routine based on number (a few of these routines are
    //> jsr JumpEngine            ;merely placeholders as conditions for other routines)
    when (gameEngineSubroutine) {
        0 -> {
            entranceGametimersetup()
        }
        1 -> {
            vineAutoclimb()
        }
        2 -> {
            sideExitPipeEntry()
        }
        3 -> {
            verticalPipeEntry()
        }
        4 -> {
            flagpoleSlide()
        }
        5 -> {
            playerEndLevel()
        }
        6 -> {
            playerLoseLife()
        }
        7 -> {
            playerEntrance()
        }
        8 -> {
            playerCtrlRoutine()
        }
        9 -> {
            playerChangeSize()
        }
        10 -> {
            playerInjuryBlink()
        }
        11 -> {
            playerDeath()
        }
        12 -> {
            playerFireFlower()
        }
        else -> {
            // Unknown JumpEngine index
        }
    }
    return
}

// Decompiled from AutoControlPlayer
fun autoControlPlayer(A: Int) {
    var savedJoypadBits by MemoryByte(SavedJoypadBits)
    //> AutoControlPlayer:
    //> sta SavedJoypadBits         ;override controller bits with contents of A if executing here
    savedJoypadBits = A
    // Fall-through tail call to playerCtrlRoutine
    playerCtrlRoutine()
}

// Decompiled from PlayerCtrlRoutine
fun playerCtrlRoutine() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var temp5: Int = 0
    var temp6: Int = 0
    var temp7: Int = 0
    var aBButtons by MemoryByte(A_B_Buttons)
    var altEntranceControl by MemoryByte(AltEntranceControl)
    var areaType by MemoryByte(AreaType)
    var cloudTypeOverride by MemoryByte(CloudTypeOverride)
    var crouchingFlag by MemoryByte(CrouchingFlag)
    var deathMusicLoaded by MemoryByte(DeathMusicLoaded)
    var eventMusicBuffer by MemoryByte(EventMusicBuffer)
    var eventMusicQueue by MemoryByte(EventMusicQueue)
    var gameEngineSubroutine by MemoryByte(GameEngineSubroutine)
    var gameTimerExpiredFlag by MemoryByte(GameTimerExpiredFlag)
    var joypadOverride by MemoryByte(JoypadOverride)
    var leftRightButtons by MemoryByte(Left_Right_Buttons)
    var playerSize by MemoryByte(PlayerSize)
    var playerBoundboxctrl by MemoryByte(Player_BoundBoxCtrl)
    var playerMovingdir by MemoryByte(Player_MovingDir)
    var playerSprattrib by MemoryByte(Player_SprAttrib)
    var playerState by MemoryByte(Player_State)
    var playerXSpeed by MemoryByte(Player_X_Speed)
    var playerYHighpos by MemoryByte(Player_Y_HighPos)
    var playerYPosition by MemoryByte(Player_Y_Position)
    var savedJoypadBits by MemoryByte(SavedJoypadBits)
    var scrollLock by MemoryByte(ScrollLock)
    var upDownButtons by MemoryByte(Up_Down_Buttons)
    //> PlayerCtrlRoutine:
    //> lda GameEngineSubroutine    ;check task here
    //> cmp #$0b                    ;if certain value is set, branch to skip controller bit loading
    //> beq SizeChk
    temp0 = gameEngineSubroutine
    if (gameEngineSubroutine != 0x0B) {
        //> lda AreaType                ;are we in a water type area?
        temp0 = areaType
        //> bne SaveJoyp                ;if not, branch
        if (temp0 == 0) {
            //> ldy Player_Y_HighPos
            //> dey                         ;if not in vertical area between
            // Note: This uses Y register as temp, doesn't modify memory!
            val yHighPos = (playerYHighpos - 1) and 0xFF
            //> bne DisJoyp                 ;status bar and bottom, branch
            temp1 = yHighPos
            if (yHighPos != 0) {
                // DisJoyp - player Y high position is not 1, disable joypad
                //> DisJoyp:    lda #$00                    ;disable controller bits
                temp0 = 0x00
                //> sta SavedJoypadBits
                savedJoypadBits = temp0
            } else {
                //> lda Player_Y_Position
                temp0 = playerYPosition
                //> cmp #$d0                    ;if nearing the bottom of the screen or
                //> bcc SaveJoyp                ;not in the vertical area between status bar or bottom,
                if (temp0 >= 0xD0) {
                    // DisJoyp - Y position >= 0xD0, disable joypad
                    temp0 = 0x00
                    savedJoypadBits = temp0
                }
                // else: SaveJoyp - keep savedJoypadBits as is
            }
        }
        // SaveJoyp - in non-water areas or after water check
        //> SaveJoyp:   lda SavedJoypadBits         ;otherwise store A and B buttons in $0a
        temp0 = savedJoypadBits
        //> and #%11000000
        temp2 = temp0 and 0xC0
        //> sta A_B_Buttons
        aBButtons = temp2
        //> lda SavedJoypadBits         ;store left and right buttons in $0c
        temp0 = savedJoypadBits
        //> and #%00000011
        temp3 = temp0 and 0x03
        //> sta Left_Right_Buttons
        leftRightButtons = temp3
        //> lda SavedJoypadBits         ;store up and down buttons in $0b
        temp0 = savedJoypadBits
        //> and #%00001100
        temp4 = temp0 and 0x0C
        //> sta Up_Down_Buttons
        upDownButtons = temp4
        //> and #%00000100              ;check for pressing down
        temp5 = temp4 and 0x04
        //> beq SizeChk                 ;if not, branch
        temp0 = temp5
        if (temp5 != 0) {
            //> lda Player_State            ;check player's state
            temp0 = playerState
            //> bne SizeChk                 ;if not on the ground, branch
            if (temp0 == 0) {
                //> ldy Left_Right_Buttons      ;check left and right
                temp1 = leftRightButtons
                //> beq SizeChk                 ;if neither pressed, branch
                if (temp1 != 0) {
                    //> lda #$00
                    temp0 = 0x00
                    //> sta Left_Right_Buttons      ;if pressing down while on the ground,
                    leftRightButtons = temp0
                    //> sta Up_Down_Buttons         ;nullify directional bits
                    upDownButtons = temp0
                }
            }
        }
    }
    //> SizeChk:    jsr PlayerMovementSubs      ;run movement subroutines
    playerMovementSubs()
    //> ldy #$01                    ;is player small?
    temp1 = 0x01
    //> lda PlayerSize
    temp0 = playerSize
    //> bne ChkMoveDir
    if (temp0 == 0) {
        //> ldy #$00                    ;check for if crouching
        temp1 = 0x00
        //> lda CrouchingFlag
        temp0 = crouchingFlag
        //> beq ChkMoveDir              ;if not, branch ahead
        if (temp0 != 0) {
            //> ldy #$02                    ;if big and crouching, load y with 2
            temp1 = 0x02
        }
    }
    //> ChkMoveDir: sty Player_BoundBoxCtrl     ;set contents of Y as player's bounding box size control
    playerBoundboxctrl = temp1
    //> lda #$01                    ;set moving direction to right by default
    temp0 = 0x01
    //> ldy Player_X_Speed          ;check player's horizontal speed
    temp1 = playerXSpeed
    //> beq PlayerSubs              ;if not moving at all horizontally, skip this part
    if (temp1 != 0) {
        //> bpl SetMoveDir              ;if moving to the right, use default moving direction
        if ((temp1 and 0x80) != 0) {
            //> asl                         ;otherwise change to move to the left
            temp0 = (temp0 shl 1) and 0xFF
        }
        //> SetMoveDir: sta Player_MovingDir        ;set moving direction
        playerMovingdir = temp0
    }
    //> PlayerSubs: jsr ScrollHandler           ;move the screen if necessary
    scrollHandler()
    //> jsr GetPlayerOffscreenBits  ;get player's offscreen bits
    getPlayerOffscreenBits()
    //> jsr RelativePlayerPosition  ;get coordinates relative to the screen
    relativePlayerPosition()
    //> ldx #$00                    ;set offset for player object
    //> jsr BoundingBoxCore         ;get player's bounding box coordinates
    boundingBoxCore(0x00, temp1)
    //> jsr PlayerBGCollision       ;do collision detection and process
    playerBGCollision()
    //> lda Player_Y_Position
    temp0 = playerYPosition
    //> cmp #$40                    ;check to see if player is higher than 64th pixel
    //> bcc PlayerHole              ;if so, branch ahead
    temp6 = 0x00
    if (temp0 >= 0x40) {
        //> lda GameEngineSubroutine
        temp0 = gameEngineSubroutine
        //> cmp #$05                    ;if running end-of-level routine, branch ahead
        //> beq PlayerHole
        if (temp0 != 0x05) {
            //> cmp #$07                    ;if running player entrance routine, branch ahead
            //> beq PlayerHole
            if (temp0 != 0x07) {
                //> cmp #$04                    ;if running routines $00-$03, branch ahead
                //> bcc PlayerHole
                if (temp0 >= 0x04) {
                    //> lda Player_SprAttrib
                    temp0 = playerSprattrib
                    //> and #%11011111              ;otherwise nullify player's
                    temp7 = temp0 and 0xDF
                    //> sta Player_SprAttrib        ;background priority flag
                    playerSprattrib = temp7
                }
            }
        }
    }
    //> PlayerHole: lda Player_Y_HighPos        ;check player's vertical high byte
    temp0 = playerYHighpos
    //> cmp #$02                    ;for below the screen
    //> bmi ExitCtrl                ;branch to leave if not that far down
    if (!(temp0 - 0x02 < 0)) {
        //> ldx #$01
        temp6 = 0x01
        //> stx ScrollLock              ;set scroll lock
        scrollLock = temp6
        //> ldy #$04
        temp1 = 0x04
        //> sty $07                     ;set value here
        memory[0x7] = temp1.toUByte()
        //> ldx #$00                    ;use X as flag, and clear for cloud level
        temp6 = 0x00
        //> ldy GameTimerExpiredFlag    ;check game timer expiration flag
        temp1 = gameTimerExpiredFlag
        //> bne HoleDie                 ;if set, branch
        if (temp1 == 0) {
            //> ldy CloudTypeOverride       ;check for cloud type override
            temp1 = cloudTypeOverride
            //> bne ChkHoleX                ;skip to last part if found
            if (temp1 == 0) {
            }
        }
        //> HoleDie:    inx                         ;set flag in X for player death
        temp6 = (temp6 + 1) and 0xFF
        //> ldy GameEngineSubroutine
        temp1 = gameEngineSubroutine
        //> cpy #$0b                    ;check for some other routine running
        //> beq ChkHoleX                ;if so, branch ahead
        if (temp1 != 0x0B) {
            //> ldy DeathMusicLoaded        ;check value here
            temp1 = deathMusicLoaded
            //> bne HoleBottom              ;if already set, branch to next part
            if (temp1 == 0) {
                //> iny
                temp1 = (temp1 + 1) and 0xFF
                //> sty EventMusicQueue         ;otherwise play death music
                eventMusicQueue = temp1
                //> sty DeathMusicLoaded        ;and set value here
                deathMusicLoaded = temp1
            }
            //> HoleBottom: ldy #$06
            temp1 = 0x06
            //> sty $07                     ;change value here
            memory[0x7] = temp1.toUByte()
        }
        //> ChkHoleX:   cmp $07                     ;compare vertical high byte with value set here
        //> bmi ExitCtrl                ;if less, branch to leave
        if (!(temp0 - memory[0x7].toInt() < 0)) {
            //> dex                         ;otherwise decrement flag in X
            temp6 = (temp6 - 1) and 0xFF
            //> bmi CloudExit               ;if flag was clear, branch to set modes and other values
            if ((temp6 and 0x80) == 0) {
                //> ldy EventMusicBuffer        ;check to see if music is still playing
                temp1 = eventMusicBuffer
                //> bne ExitCtrl                ;branch to leave if so
                if (temp1 == 0) {
                    //> lda #$06                    ;otherwise set to run lose life routine
                    temp0 = 0x06
                    //> sta GameEngineSubroutine    ;on next frame
                    gameEngineSubroutine = temp0
                }
            } else {
                //> CloudExit:
                //> lda #$00
                temp0 = 0x00
                //> sta JoypadOverride      ;clear controller override bits if any are set
                joypadOverride = temp0
                //> jsr SetEntr             ;do sub to set secondary mode
                setEntr()
                //> inc AltEntranceControl  ;set mode of entry to 3
                altEntranceControl = (altEntranceControl + 1) and 0xFF
                //> rts
                return
            }
        }
    }
    //> ExitCtrl:   rts                         ;leave
    return
}

// Decompiled from SetEntr
fun setEntr() {
    var altEntranceControl by MemoryByte(AltEntranceControl)
    //> SetEntr:   lda #$02               ;set starting position to override
    //> sta AltEntranceControl
    altEntranceControl = 0x02
    //> jmp ChgAreaMode        ;set modes
}

// Decompiled from MovePlayerYAxis
fun movePlayerYAxis(A: Int) {
    var playerYPosition by MemoryByte(Player_Y_Position)
    //> MovePlayerYAxis:
    //> clc
    //> adc Player_Y_Position ;add contents of A to player position
    //> sta Player_Y_Position
    playerYPosition = (A + playerYPosition) and 0xFF
    //> rts
    return
}

// Decompiled from ChgAreaMode
fun chgAreaMode(): Int {
    var disableScreenFlag by MemoryByte(DisableScreenFlag)
    var opermodeTask by MemoryByte(OperMode_Task)
    var sprite0HitDetectFlag by MemoryByte(Sprite0HitDetectFlag)
    //> ChgAreaMode: inc DisableScreenFlag     ;set flag to disable screen output
    disableScreenFlag = (disableScreenFlag + 1) and 0xFF
    //> lda #$00
    //> sta OperMode_Task         ;set secondary mode of operation
    opermodeTask = 0x00
    //> sta Sprite0HitDetectFlag  ;disable sprite 0 check
    sprite0HitDetectFlag = 0x00
    //> ExitCAPipe:  rts                       ;leave
    return A
}

// Decompiled from EnterSidePipe
fun enterSidePipe() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var playerXPosition by MemoryByte(Player_X_Position)
    var playerXSpeed by MemoryByte(Player_X_Speed)
    //> EnterSidePipe:
    //> lda #$08               ;set player's horizontal speed
    //> sta Player_X_Speed
    playerXSpeed = 0x08
    //> ldy #$01               ;set controller right button by default
    //> lda Player_X_Position  ;mask out higher nybble of player's
    //> and #%00001111         ;horizontal position
    temp0 = playerXPosition and 0x0F
    //> bne RightPipe
    temp1 = temp0
    temp2 = 0x01
    if (temp0 == 0) {
        //> sta Player_X_Speed     ;if lower nybble = 0, set as horizontal speed
        playerXSpeed = temp1
        //> tay                    ;and nullify controller bit override here
    }
    //> RightPipe: tya                    ;use contents of Y to
    //> jsr AutoControlPlayer  ;execute player control routine with ctrl bits nulled
    autoControlPlayer(temp2)
    //> rts
    return
}

// Decompiled from DonePlayerTask
fun donePlayerTask() {
    var gameEngineSubroutine by MemoryByte(GameEngineSubroutine)
    var timerControl by MemoryByte(TimerControl)
    //> DonePlayerTask:
    //> lda #$00
    //> sta TimerControl          ;initialize master timer control to continue timers
    timerControl = 0x00
    //> lda #$08
    //> sta GameEngineSubroutine  ;set player control routine to run next frame
    gameEngineSubroutine = 0x08
    //> rts                       ;leave
    return
}

// Decompiled from CyclePlayerPalette
fun cyclePlayerPalette(A: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var playerSprattrib by MemoryByte(Player_SprAttrib)
    //> CyclePlayerPalette:
    //> and #$03              ;mask out all but d1-d0 (previously d3-d2)
    temp0 = A and 0x03
    //> sta $00               ;store result here to use as palette bits
    memory[0x0] = temp0.toUByte()
    //> lda Player_SprAttrib  ;get player attributes
    //> and #%11111100        ;save any other bits but palette bits
    temp1 = playerSprattrib and 0xFC
    //> ora $00               ;add palette bits
    temp2 = temp1 or memory[0x0].toInt()
    //> sta Player_SprAttrib  ;store as new player attributes
    playerSprattrib = temp2
    //> rts                   ;and leave
    return
}

// Decompiled from ResetPalStar
fun resetPalStar() {
    var temp0: Int = 0
    var playerSprattrib by MemoryByte(Player_SprAttrib)
    //> ResetPalStar:
    //> lda Player_SprAttrib  ;get player attributes
    //> and #%11111100        ;mask out palette bits to force palette 0
    temp0 = playerSprattrib and 0xFC
    //> sta Player_SprAttrib  ;store as new player attributes
    playerSprattrib = temp0
    //> rts                   ;and leave
    return
}

// Decompiled from PlayerMovementSubs
fun playerMovementSubs(): Int {  // FIX: Added return type
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var climbSideTimer by MemoryByte(ClimbSideTimer)
    var crouchingFlag by MemoryByte(CrouchingFlag)
    var playerChangeSizeFlag by MemoryByte(PlayerChangeSizeFlag)
    var playerSize by MemoryByte(PlayerSize)
    var playerState by MemoryByte(Player_State)
    var upDownButtons by MemoryByte(Up_Down_Buttons)
    //> PlayerMovementSubs:
    //> lda #$00                  ;set A to init crouch flag by default
    //> ldy PlayerSize            ;is player small?
    //> bne SetCrouch             ;if so, branch
    temp0 = 0x00
    temp1 = playerSize
    if (playerSize == 0) {
        //> lda Player_State          ;check state of player
        temp0 = playerState
        //> bne ProcMove              ;if not on the ground, branch
        if (temp0 == 0) {
            //> lda Up_Down_Buttons       ;load controller bits for up and down
            temp0 = upDownButtons
            //> and #%00000100            ;single out bit for down button
            temp2 = temp0 and 0x04
        }
    }
    //> SetCrouch: sta CrouchingFlag         ;store value in crouch flag
    crouchingFlag = temp0
    //> ProcMove:  jsr PlayerPhysicsSub      ;run sub related to jumping and swimming
    playerPhysicsSub()
    //> lda PlayerChangeSizeFlag  ;if growing/shrinking flag set,
    temp0 = playerChangeSizeFlag
    //> bne NoMoveSub             ;branch to leave
    if (temp0 == 0) {
        //> lda Player_State
        temp0 = playerState
        //> cmp #$03                  ;get player state
        //> beq MoveSubs              ;if climbing, branch ahead, leave timer unset
        if (temp0 != 0x03) {
            //> ldy #$18
            temp1 = 0x18
            //> sty ClimbSideTimer        ;otherwise reset timer now
            climbSideTimer = temp1
        }
        //> MoveSubs:  jsr JumpEngine
        when (temp0) {
            0 -> {
                onGroundStateSub()
            }
            1 -> {
                jumpSwimSub()
            }
            2 -> {
                fallingSub()
            }
            3 -> {
                climbingSub()
            }
            else -> {
                // Unknown JumpEngine index
            }
        }
        return 0
    } else {
        //> NoMoveSub: rts
        return 0
    }
}

// Decompiled from MovePlayerVertically
// This function wasn't auto-decompiled because it's reached via JMP, not JSR
fun movePlayerVertically() {
    var timerControl by MemoryByte(TimerControl)
    var jumpspringAnimCtrl by MemoryByte(JumpspringAnimCtrl)
    var verticalForce by MemoryByte(VerticalForce)
    //> MovePlayerVertically:
    //> ldx #$00                ;set X for player offset
    val X = 0x00
    //> lda TimerControl
    //> bne NoJSChk             ;if master timer control set, branch ahead
    if (timerControl == 0) {
        //> lda JumpspringAnimCtrl  ;otherwise check to see if jumpspring is animating
        //> bne ExXMove             ;branch to leave if so
        if (jumpspringAnimCtrl != 0) {
            return
        }
    }
    //> NoJSChk: lda VerticalForce       ;dump vertical force
    //> sta $00
    memory[0x00] = verticalForce.toUByte()
    //> lda #$04                ;set maximum vertical speed here
    val A = 0x04
    //> jmp ImposeGravitySprObj ;then jump to move player vertically
    imposeGravitySprObj(A)
}

// Decompiled from OnGroundStateSub
fun onGroundStateSub() {
    var temp0: Int = 0
    var leftRightButtons by MemoryByte(Left_Right_Buttons)
    var playerFacingDir by MemoryByte(PlayerFacingDir)
    var playerXScroll by MemoryByte(Player_X_Scroll)
    //> OnGroundStateSub:
    //> jsr GetPlayerAnimSpeed     ;do a sub to set animation frame timing
    getPlayerAnimSpeed()
    //> lda Left_Right_Buttons
    //> beq GndMove                ;if left/right controller bits not set, skip instruction
    temp0 = leftRightButtons
    if (leftRightButtons != 0) {
        //> sta PlayerFacingDir        ;otherwise set new facing direction
        playerFacingDir = temp0
    }
    //> GndMove: jsr ImposeFriction         ;do a sub to impose friction on player's walk/run
    imposeFriction(temp0)
    //> jsr MovePlayerHorizontally ;do another sub to move player horizontally
    movePlayerHorizontally()
    //> sta Player_X_Scroll        ;set returned value as player's movement speed for scroll
    playerXScroll = temp0
    //> rts
    return
}

// Decompiled from FallingSub
fun fallingSub() {
    var temp0: Int = 0
    var gameEngineSubroutine by MemoryByte(GameEngineSubroutine)
    var leftRightButtons by MemoryByte(Left_Right_Buttons)
    var playerXScroll by MemoryByte(Player_X_Scroll)
    var verticalForce by MemoryByte(VerticalForce)
    var verticalForceDown by MemoryByte(VerticalForceDown)
    //> FallingSub:
    //> lda VerticalForceDown
    //> sta VerticalForce      ;dump vertical movement force for falling into main one
    verticalForce = verticalForceDown
    //> jmp LRAir              ;movement force, then skip ahead to process left/right movement
    //> LRAir:    lda Left_Right_Buttons     ;check left/right controller bits (check for jumping/falling)
    //> beq JSMove                 ;if not pressing any, skip
    temp0 = leftRightButtons
    if (leftRightButtons != 0) {
        //> jsr ImposeFriction         ;otherwise process horizontal movement
        imposeFriction(temp0)
    }
    //> JSMove:   jsr MovePlayerHorizontally ;do a sub to move player horizontally
    movePlayerHorizontally()
    //> sta Player_X_Scroll        ;set player's speed here, to be used for scroll later
    playerXScroll = temp0
    //> lda GameEngineSubroutine
    temp0 = gameEngineSubroutine
    //> cmp #$0b                   ;check for specific routine selected
    //> bne ExitMov1               ;branch if not set to run
    if (temp0 == 0x0B) {
        //> lda #$28
        temp0 = 0x28
        //> sta VerticalForce          ;otherwise set fractional
        verticalForce = temp0
    }
    //> ExitMov1: jmp MovePlayerVertically   ;jump to move player vertically, then leave
    movePlayerVertically()
    return
}

// Decompiled from JumpSwimSub
fun jumpSwimSub() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var aBButtons by MemoryByte(A_B_Buttons)
    var diffToHaltJump by MemoryByte(DiffToHaltJump)
    var gameEngineSubroutine by MemoryByte(GameEngineSubroutine)
    var jumporiginYPosition by MemoryByte(JumpOrigin_Y_Position)
    var leftRightButtons by MemoryByte(Left_Right_Buttons)
    var playerFacingDir by MemoryByte(PlayerFacingDir)
    var playerXScroll by MemoryByte(Player_X_Scroll)
    var playerYPosition by MemoryByte(Player_Y_Position)
    var playerYSpeed by MemoryByte(Player_Y_Speed)
    var previousaBButtons by MemoryByte(PreviousA_B_Buttons)
    var swimmingFlag by MemoryByte(SwimmingFlag)
    var verticalForce by MemoryByte(VerticalForce)
    var verticalForceDown by MemoryByte(VerticalForceDown)
    //> JumpSwimSub:
    //> ldy Player_Y_Speed         ;if player's vertical speed zero
    //> bpl DumpFall               ;or moving downwards, branch to falling
    temp0 = playerYSpeed
    if ((playerYSpeed and 0x80) != 0) {
        //> lda A_B_Buttons
        //> and #A_Button              ;check to see if A button is being pressed
        temp1 = aBButtons and A_Button
        //> and PreviousA_B_Buttons    ;and was pressed in previous frame
        temp2 = temp1 and previousaBButtons
        //> bne ProcSwim               ;if so, branch elsewhere
        temp3 = temp2
        if (temp2 == 0) {
            //> lda JumpOrigin_Y_Position  ;get vertical position player jumped from
            temp3 = jumporiginYPosition
            //> sec
            //> sbc Player_Y_Position      ;subtract current from original vertical coordinate
            //> cmp DiffToHaltJump         ;compare to value set here to see if player is in mid-jump
            //> bcc ProcSwim               ;or just starting to jump, if just starting, skip ahead
            temp3 = (temp3 - playerYPosition) and 0xFF
            if (temp3 >= diffToHaltJump) {
                // fall through to DumpFall
            }
        }
    }
    //> DumpFall: lda VerticalForceDown      ;otherwise dump falling into main fractional
    temp3 = verticalForceDown
    //> sta VerticalForce
    verticalForce = temp3
    //> ProcSwim: lda SwimmingFlag           ;if swimming flag not set,
    temp3 = swimmingFlag
    //> beq LRAir                  ;branch ahead to last part
    if (temp3 != 0) {
        //> jsr GetPlayerAnimSpeed     ;do a sub to get animation frame timing
        getPlayerAnimSpeed()
        //> lda Player_Y_Position
        temp3 = playerYPosition
        //> cmp #$14                   ;check vertical position against preset value
        //> bcs LRWater                ;if not yet reached a certain position, branch ahead
        if (temp3 < 0x14) {
            //> lda #$18
            temp3 = 0x18
            //> sta VerticalForce          ;otherwise set fractional
            verticalForce = temp3
        }
        //> LRWater:  lda Left_Right_Buttons     ;check left/right controller bits (check for swimming)
        temp3 = leftRightButtons
        //> beq LRAir                  ;if not pressing any, skip
        if (temp3 != 0) {
            //> sta PlayerFacingDir        ;otherwise set facing direction accordingly
            playerFacingDir = temp3
        }
    }
    //> LRAir:    lda Left_Right_Buttons     ;check left/right controller bits (check for jumping/falling)
    temp3 = leftRightButtons
    //> beq JSMove                 ;if not pressing any, skip
    if (temp3 != 0) {
        //> jsr ImposeFriction         ;otherwise process horizontal movement
        imposeFriction(temp3)
    }
    //> JSMove:   jsr MovePlayerHorizontally ;do a sub to move player horizontally
    movePlayerHorizontally()
    //> sta Player_X_Scroll        ;set player's speed here, to be used for scroll later
    playerXScroll = temp3
    //> lda GameEngineSubroutine
    temp3 = gameEngineSubroutine
    //> cmp #$0b                   ;check for specific routine selected
    //> bne ExitMov1               ;branch if not set to run
    if (temp3 == 0x0B) {
        //> lda #$28
        temp3 = 0x28
        //> sta VerticalForce          ;otherwise set fractional
        verticalForce = temp3
    }
    //> ExitMov1: jmp MovePlayerVertically   ;jump to move player vertically, then leave
    movePlayerVertically()
    return
}

// Decompiled from ClimbingSub
fun climbingSub() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var climbSideTimer by MemoryByte(ClimbSideTimer)
    var leftRightButtons by MemoryByte(Left_Right_Buttons)
    var playerFacingDir by MemoryByte(PlayerFacingDir)
    var playerCollisionbits by MemoryByte(Player_CollisionBits)
    var playerPageloc by MemoryByte(Player_PageLoc)
    var playerXPosition by MemoryByte(Player_X_Position)
    var playerYmfDummy by MemoryByte(Player_YMF_Dummy)
    var playerYHighpos by MemoryByte(Player_Y_HighPos)
    var playerYMoveforce by MemoryByte(Player_Y_MoveForce)
    var playerYPosition by MemoryByte(Player_Y_Position)
    var playerYSpeed by MemoryByte(Player_Y_Speed)
    val climbAdderHigh by MemoryByteIndexed(ClimbAdderHigh)
    val climbAdderLow by MemoryByteIndexed(ClimbAdderLow)
    //> ClimbingSub:
    //> lda Player_YMF_Dummy
    //> clc                      ;add movement force to dummy variable
    //> adc Player_Y_MoveForce   ;save with carry
    //> sta Player_YMF_Dummy
    val carry = if (playerYmfDummy + playerYMoveforce > 0xFF) 1 else 0
    playerYmfDummy = (playerYmfDummy + playerYMoveforce) and 0xFF
    //> ldy #$00                 ;set default adder here
    //> lda Player_Y_Speed       ;get player's vertical speed
    //> bpl MoveOnVine           ;if not moving upwards, branch
    temp0 = playerYSpeed
    temp1 = 0x00
    if ((playerYSpeed and 0x80) != 0) {
        //> dey                      ;otherwise set adder to $ff
        temp1 = 0xFF
    }
    //> MoveOnVine:  sty $00                  ;store adder here
    memory[0x0] = temp1.toUByte()
    //> adc Player_Y_Position    ;add carry to player's vertical position
    //> sta Player_Y_Position    ;and store to move player up or down
    val newY = (temp0 + playerYPosition + carry) and 0xFF
    val carry2 = if (temp0 + playerYPosition + carry > 0xFF) 1 else 0
    playerYPosition = newY
    //> lda Player_Y_HighPos
    temp0 = playerYHighpos
    //> adc $00                  ;add carry to player's page location
    //> sta Player_Y_HighPos     ;and store
    playerYHighpos = (temp0 + temp1 + carry2) and 0xFF
    //> lda Left_Right_Buttons   ;compare left/right controller bits
    temp0 = leftRightButtons
    //> and Player_CollisionBits ;to collision flag
    temp2 = temp0 and playerCollisionbits
    //> beq InitCSTimer          ;if not set, skip to end
    temp0 = temp2
    if (temp2 != 0) {
        //> ldy ClimbSideTimer       ;otherwise check timer
        temp1 = climbSideTimer
        //> bne ExitCSub             ;if timer not expired, branch to leave
        if (temp1 == 0) {
            //> ldy #$18
            temp1 = 0x18
            //> sty ClimbSideTimer       ;otherwise set timer now
            climbSideTimer = temp1
            //> ldx #$00                 ;set default offset here
            //> ldy PlayerFacingDir      ;get facing direction
            temp1 = playerFacingDir
            //> lsr                      ;move right button controller bit to carry
            temp0 = temp0 shr 1
            //> bcs ClimbFD              ;if controller right pressed, branch ahead
            temp3 = 0x00
            if ((temp0 and 0x01) == 0) {
                //> inx
                temp3 = (temp3 + 1) and 0xFF
                //> inx                      ;otherwise increment offset by 2 bytes
                temp3 = (temp3 + 1) and 0xFF
            }
            //> ClimbFD:     dey                      ;check to see if facing right
            temp1 = (temp1 - 1) and 0xFF
            //> beq CSetFDir             ;if so, branch, do not increment
            if (temp1 != 0) {
                //> inx                      ;otherwise increment by 1 byte
                temp3 = (temp3 + 1) and 0xFF
            }
            //> CSetFDir:    lda Player_X_Position
            temp0 = playerXPosition
            //> clc                      ;add or subtract from player's horizontal position
            //> adc ClimbAdderLow,x      ;using value here as adder and X as offset
            //> sta Player_X_Position
            val xCarry = if (temp0 + climbAdderLow[temp3] > 0xFF) 1 else 0
            playerXPosition = (temp0 + climbAdderLow[temp3]) and 0xFF
            //> lda Player_PageLoc       ;add or subtract carry or borrow using value here
            temp0 = playerPageloc
            //> adc ClimbAdderHigh,x     ;from the player's page location
            //> sta Player_PageLoc
            playerPageloc = (temp0 + climbAdderHigh[temp3] + xCarry) and 0xFF
            //> lda Left_Right_Buttons   ;get left/right controller bits again
            temp0 = leftRightButtons
            //> eor #%00000011           ;invert them and store them while player
            temp4 = temp0 xor 0x03
            //> sta PlayerFacingDir      ;is on vine to face player in opposite direction
            playerFacingDir = temp4
        }
        //> ExitCSub:    rts                      ;then leave
        return
    } else {
        //> InitCSTimer: sta ClimbSideTimer       ;initialize timer here
        climbSideTimer = temp0
        //> rts
        return
    }
}

// Decompiled from PlayerPhysicsSub
fun playerPhysicsSub() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var temp5: Int = 0
    var temp6: Int = 0
    var temp7: Int = 0
    var aBButtons by MemoryByte(A_B_Buttons)
    var areaType by MemoryByte(AreaType)
    var diffToHaltJump by MemoryByte(DiffToHaltJump)
    var frictionAdderHigh by MemoryByte(FrictionAdderHigh)
    var frictionAdderLow by MemoryByte(FrictionAdderLow)
    var gameEngineSubroutine by MemoryByte(GameEngineSubroutine)
    var jumporiginYHighpos by MemoryByte(JumpOrigin_Y_HighPos)
    var jumporiginYPosition by MemoryByte(JumpOrigin_Y_Position)
    var jumpSwimTimer by MemoryByte(JumpSwimTimer)
    var jumpspringAnimCtrl by MemoryByte(JumpspringAnimCtrl)
    var leftRightButtons by MemoryByte(Left_Right_Buttons)
    var maximumLeftSpeed by MemoryByte(MaximumLeftSpeed)
    var maximumRightSpeed by MemoryByte(MaximumRightSpeed)
    var playerAnimTimerSet by MemoryByte(PlayerAnimTimerSet)
    var playerFacingDir by MemoryByte(PlayerFacingDir)
    var playerSize by MemoryByte(PlayerSize)
    var playerCollisionbits by MemoryByte(Player_CollisionBits)
    var playerMovingdir by MemoryByte(Player_MovingDir)
    var playerState by MemoryByte(Player_State)
    var playerXspeedabsolute by MemoryByte(Player_XSpeedAbsolute)
    var playerYmfDummy by MemoryByte(Player_YMF_Dummy)
    var playerYHighpos by MemoryByte(Player_Y_HighPos)
    var playerYMoveforce by MemoryByte(Player_Y_MoveForce)
    var playerYPosition by MemoryByte(Player_Y_Position)
    var playerYSpeed by MemoryByte(Player_Y_Speed)
    var previousaBButtons by MemoryByte(PreviousA_B_Buttons)
    var runningSpeed by MemoryByte(RunningSpeed)
    var runningTimer by MemoryByte(RunningTimer)
    var square1SoundQueue by MemoryByte(Square1SoundQueue)
    var swimmingFlag by MemoryByte(SwimmingFlag)
    var upDownButtons by MemoryByte(Up_Down_Buttons)
    var verticalForce by MemoryByte(VerticalForce)
    var verticalForceDown by MemoryByte(VerticalForceDown)
    var whirlpoolFlag by MemoryByte(Whirlpool_Flag)
    val climbYMforcedata by MemoryByteIndexed(Climb_Y_MForceData)
    val climbYSpeeddata by MemoryByteIndexed(Climb_Y_SpeedData)
    val fallMForceData by MemoryByteIndexed(FallMForceData)
    val frictionData by MemoryByteIndexed(FrictionData)
    val initMForceData by MemoryByteIndexed(InitMForceData)
    val jumpMForceData by MemoryByteIndexed(JumpMForceData)
    val maxLeftXSpdData by MemoryByteIndexed(MaxLeftXSpdData)
    val maxRightXSpdData by MemoryByteIndexed(MaxRightXSpdData)
    val playerYSpdData by MemoryByteIndexed(PlayerYSpdData)
    //> PlayerPhysicsSub:
    //> lda Player_State          ;check player state
    //> cmp #$03
    //> bne CheckForJumping       ;if not climbing, branch
    temp0 = playerState
    if (playerState == 0x03) {
        //> ldy #$00
        //> lda Up_Down_Buttons       ;get controller bits for up/down
        temp0 = upDownButtons
        //> and Player_CollisionBits  ;check against player's collision detection bits
        temp1 = temp0 and playerCollisionbits
        //> beq ProcClimb             ;if not pressing up or down, branch
        temp0 = temp1
        temp2 = 0x00
        if (temp1 != 0) {
            //> iny
            temp2 = (temp2 + 1) and 0xFF
            //> and #%00001000            ;check for pressing up
            temp3 = temp0 and 0x08
            //> bne ProcClimb
            temp0 = temp3
            if (temp3 == 0) {
                //> iny
                temp2 = (temp2 + 1) and 0xFF
            }
        }
        //> ProcClimb: ldx Climb_Y_MForceData,y  ;load value here
        //> stx Player_Y_MoveForce    ;store as vertical movement force
        playerYMoveforce = climbYMforcedata[temp2]
        //> lda #$08                  ;load default animation timing
        temp0 = 0x08
        //> ldx Climb_Y_SpeedData,y   ;load some other value here
        //> stx Player_Y_Speed        ;store as vertical speed
        playerYSpeed = climbYSpeeddata[temp2]
        //> bmi SetCAnim              ;if climbing down, use default animation timing value
        temp4 = climbYSpeeddata[temp2]
        if ((climbYSpeeddata[temp2] and 0x80) == 0) {
            //> lsr                       ;otherwise divide timer setting by 2
            temp0 = temp0 shr 1
        }
        //> SetCAnim:  sta PlayerAnimTimerSet    ;store animation timer setting and leave
        playerAnimTimerSet = temp0
        //> rts
        return
    } else {
        //> CheckForJumping:
        //> lda JumpspringAnimCtrl    ;if jumpspring animating,
        temp0 = jumpspringAnimCtrl
        //> bne NoJump                ;skip ahead to something else
        if (!(temp0 == 0)) {
            //  goto NoJump
            return
        }
        if (temp0 == 0) {
            //> lda A_B_Buttons           ;check for A button press
            temp0 = aBButtons
            //> and #A_Button
            temp5 = temp0 and A_Button
            //> beq NoJump                ;if not, branch to something else
            if (temp5 == 0) {
                //  goto NoJump
                return
            }
            temp0 = temp5
            if (temp5 != 0) {
                //> and PreviousA_B_Buttons   ;if button not pressed in previous frame, branch
                temp6 = temp0 and previousaBButtons
                //> beq ProcJumping
                temp0 = temp6
                if (temp6 != 0) {
                }
            }
        }
    }
    //> NoJump: jmp X_Physics             ;otherwise, jump to something else
    //> ProcJumping:
    //> lda Player_State           ;check player state
    temp0 = playerState
    //> beq InitJS                 ;if on the ground, branch
    if (temp0 != 0) {
        //> lda SwimmingFlag           ;if swimming flag not set, jump to do something else
        temp0 = swimmingFlag
        //> beq NoJump                 ;to prevent midair jumping, otherwise continue
        if (temp0 == 0) {
            //  goto NoJump
            return
        }
        //> lda JumpSwimTimer          ;if jump/swim timer nonzero, branch
        temp0 = jumpSwimTimer
        //> bne InitJS
        if (temp0 == 0) {
            //> lda Player_Y_Speed         ;check player's vertical speed
            temp0 = playerYSpeed
            //> bpl InitJS                 ;if player's vertical speed motionless or down, branch
            if ((temp0 and 0x80) != 0) {
            }
        }
    } else {
        //> InitJS:    lda #$20                   ;set jump/swim timer
        temp0 = 0x20
        //> sta JumpSwimTimer
        jumpSwimTimer = temp0
        //> ldy #$00                   ;initialize vertical force and dummy variable
        temp2 = 0x00
        //> sty Player_YMF_Dummy
        playerYmfDummy = temp2
        //> sty Player_Y_MoveForce
        playerYMoveforce = temp2
        //> lda Player_Y_HighPos       ;get vertical high and low bytes of jump origin
        temp0 = playerYHighpos
        //> sta JumpOrigin_Y_HighPos   ;and store them next to each other here
        jumporiginYHighpos = temp0
        //> lda Player_Y_Position
        temp0 = playerYPosition
        //> sta JumpOrigin_Y_Position
        jumporiginYPosition = temp0
        //> lda #$01                   ;set player state to jumping/swimming
        temp0 = 0x01
        //> sta Player_State
        playerState = temp0
        //> lda Player_XSpeedAbsolute  ;check value related to walking/running speed
        temp0 = playerXspeedabsolute
        //> cmp #$09
        //> bcc ChkWtr                 ;branch if below certain values, increment Y
        if (temp0 >= 0x09) {
            //> iny                        ;for each amount equal or exceeded
            temp2 = (temp2 + 1) and 0xFF
            //> cmp #$10
            //> bcc ChkWtr
            if (temp0 >= 0x10) {
                //> iny
                temp2 = (temp2 + 1) and 0xFF
                //> cmp #$19
                //> bcc ChkWtr
                if (temp0 >= 0x19) {
                    //> iny
                    temp2 = (temp2 + 1) and 0xFF
                    //> cmp #$1c
                    //> bcc ChkWtr                 ;note that for jumping, range is 0-4 for Y
                    if (temp0 >= 0x1C) {
                        //> iny
                        temp2 = (temp2 + 1) and 0xFF
                    }
                }
            }
        }
        //> ChkWtr:    lda #$01                   ;set value here (apparently always set to 1)
        temp0 = 0x01
        //> sta DiffToHaltJump
        diffToHaltJump = temp0
        //> lda SwimmingFlag           ;if swimming flag disabled, branch
        temp0 = swimmingFlag
        //> beq GetYPhy
        if (temp0 != 0) {
            //> ldy #$05                   ;otherwise set Y to 5, range is 5-6
            temp2 = 0x05
            //> lda Whirlpool_Flag         ;if whirlpool flag not set, branch
            temp0 = whirlpoolFlag
            //> beq GetYPhy
            if (temp0 != 0) {
                //> iny                        ;otherwise increment to 6
                temp2 = (temp2 + 1) and 0xFF
            }
        }
        //> GetYPhy:   lda JumpMForceData,y       ;store appropriate jump/swim
        temp0 = jumpMForceData[temp2]
        //> sta VerticalForce          ;data here
        verticalForce = temp0
        //> lda FallMForceData,y
        temp0 = fallMForceData[temp2]
        //> sta VerticalForceDown
        verticalForceDown = temp0
        //> lda InitMForceData,y
        temp0 = initMForceData[temp2]
        //> sta Player_Y_MoveForce
        playerYMoveforce = temp0
        //> lda PlayerYSpdData,y
        temp0 = playerYSpdData[temp2]
        //> sta Player_Y_Speed
        playerYSpeed = temp0
        //> lda SwimmingFlag           ;if swimming flag disabled, branch
        temp0 = swimmingFlag
        //> beq PJumpSnd
        if (temp0 != 0) {
            //> lda #Sfx_EnemyStomp        ;load swim/goomba stomp sound into
            temp0 = Sfx_EnemyStomp
            //> sta Square1SoundQueue      ;square 1's sfx queue
            square1SoundQueue = temp0
            //> lda Player_Y_Position
            temp0 = playerYPosition
            //> cmp #$14                   ;check vertical low byte of player position
            //> bcs X_Physics              ;if below a certain point, branch
            if (!(temp0 >= 0x14)) {
                //> lda #$00                   ;otherwise reset player's vertical speed
                temp0 = 0x00
                //> sta Player_Y_Speed         ;and jump to something else to keep player
                playerYSpeed = temp0
                //> jmp X_Physics              ;from swimming above water level
            }
        } else {
            //> PJumpSnd:  lda #Sfx_BigJump           ;load big mario's jump sound by default
            temp0 = Sfx_BigJump
            //> ldy PlayerSize             ;is mario big?
            temp2 = playerSize
            //> beq SJumpSnd
            if (temp2 != 0) {
                //> lda #Sfx_SmallJump         ;if not, load small mario's jump sound
                temp0 = Sfx_SmallJump
            }
            //> SJumpSnd:  sta Square1SoundQueue      ;store appropriate jump sound in square 1 sfx queue
            square1SoundQueue = temp0
        }
    }
    //> X_Physics: ldy #$00
    temp2 = 0x00
    //> sty $00                    ;init value here
    memory[0x0] = temp2.toUByte()
    //> lda Player_State           ;if mario is on the ground, branch
    temp0 = playerState
    //> beq ProcPRun
    if (temp0 != 0) {
        //> lda Player_XSpeedAbsolute  ;check something that seems to be related
        temp0 = playerXspeedabsolute
        //> cmp #$19                   ;to mario's speed
        //> bcs GetXPhy                ;if =>$19 branch here
        if (!(temp0 >= 0x19)) {
            //> bcc ChkRFast               ;if not branch elsewhere
            if (temp0 >= 0x19) {
            }
        }
    }
    //> ProcPRun:  iny                        ;if mario on the ground, increment Y
    temp2 = (temp2 + 1) and 0xFF
    //> lda AreaType               ;check area type
    temp0 = areaType
    //> beq ChkRFast               ;if water type, branch
    if (temp0 != 0) {
        //> dey                        ;decrement Y by default for non-water type area
        temp2 = (temp2 - 1) and 0xFF
        //> lda Left_Right_Buttons     ;get left/right controller bits
        temp0 = leftRightButtons
        //> cmp Player_MovingDir       ;check against moving direction
        //> bne ChkRFast               ;if controller bits <> moving direction, skip this part
        if (temp0 == playerMovingdir) {
            //> lda A_B_Buttons            ;check for b button pressed
            temp0 = aBButtons
            //> and #B_Button
            temp7 = temp0 and B_Button
            //> bne SetRTmr                ;if pressed, skip ahead to set timer
            temp0 = temp7
            if (temp7 == 0) {
                //> lda RunningTimer           ;check for running timer set
                temp0 = runningTimer
                //> bne GetXPhy                ;if set, branch
                if (temp0 == 0) {
                }
            }
        }
    }
    //> ChkRFast:  iny                        ;if running timer not set or level type is water,
    temp2 = (temp2 + 1) and 0xFF
    //> inc $00                    ;increment Y again and temp variable in memory
    memory[0x0] = ((memory[0x0].toInt() + 1) and 0xFF).toUByte()
    //> lda RunningSpeed
    temp0 = runningSpeed
    //> bne FastXSp                ;if running speed set here, branch
    if (!(temp0 == 0)) {
        //  goto FastXSp
        return
    }
    if (temp0 == 0) {
        //> lda Player_XSpeedAbsolute
        temp0 = playerXspeedabsolute
        //> cmp #$21                   ;otherwise check player's walking/running speed
        //> bcc GetXPhy                ;if less than a certain amount, branch ahead
        if (temp0 >= 0x21) {
        }
    }
    //> FastXSp:   inc $00                    ;if running speed set or speed => $21 increment $00
    memory[0x0] = ((memory[0x0].toInt() + 1) and 0xFF).toUByte()
    //> jmp GetXPhy                ;and jump ahead
    //> SetRTmr:   lda #$0a                   ;if b button pressed, set running timer
    temp0 = 0x0A
    //> sta RunningTimer
    runningTimer = temp0
    //> GetXPhy:   lda MaxLeftXSpdData,y      ;get maximum speed to the left
    temp0 = maxLeftXSpdData[temp2]
    //> sta MaximumLeftSpeed
    maximumLeftSpeed = temp0
    //> lda GameEngineSubroutine   ;check for specific routine running
    temp0 = gameEngineSubroutine
    //> cmp #$07                   ;(player entrance)
    //> bne GetXPhy2               ;if not running, skip and use old value of Y
    if (temp0 == 0x07) {
        //> ldy #$03                   ;otherwise set Y to 3
        temp2 = 0x03
    }
    //> GetXPhy2:  lda MaxRightXSpdData,y     ;get maximum speed to the right
    temp0 = maxRightXSpdData[temp2]
    //> sta MaximumRightSpeed
    maximumRightSpeed = temp0
    //> ldy $00                    ;get other value in memory
    temp2 = memory[0x0].toInt()
    //> lda FrictionData,y         ;get value using value in memory as offset
    temp0 = frictionData[temp2]
    //> sta FrictionAdderLow
    frictionAdderLow = temp0
    //> lda #$00
    temp0 = 0x00
    //> sta FrictionAdderHigh      ;init something here
    frictionAdderHigh = temp0
    //> lda PlayerFacingDir
    temp0 = playerFacingDir
    //> cmp Player_MovingDir       ;check facing direction against moving direction
    //> beq ExitPhy                ;if the same, branch to leave
    if (temp0 != playerMovingdir) {
        //> asl FrictionAdderLow       ;otherwise shift d7 of friction adder low into carry
        frictionAdderLow = (frictionAdderLow shl 1) and 0xFF
        //> rol FrictionAdderHigh      ;then rotate carry onto d0 of friction adder high
        frictionAdderHigh = (frictionAdderHigh shl 1) and 0xFE or if ((frictionAdderLow and 0x80) != 0) 1 else 0
    }
    //> ExitPhy:   rts                        ;and then leave
    return
}

// Decompiled from GetPlayerAnimSpeed
fun getPlayerAnimSpeed() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var playerAnimTimerSet by MemoryByte(PlayerAnimTimerSet)
    var playerFacingDir by MemoryByte(PlayerFacingDir)
    var playerMovingdir by MemoryByte(Player_MovingDir)
    var playerXspeedabsolute by MemoryByte(Player_XSpeedAbsolute)
    var playerXMoveforce by MemoryByte(Player_X_MoveForce)
    var playerXSpeed by MemoryByte(Player_X_Speed)
    var runningSpeed by MemoryByte(RunningSpeed)
    var savedJoypadBits by MemoryByte(SavedJoypadBits)
    val playerAnimTmrData by MemoryByteIndexed(PlayerAnimTmrData)
    //> GetPlayerAnimSpeed:
    //> ldy #$00                   ;initialize offset in Y
    //> lda Player_XSpeedAbsolute  ;check player's walking/running speed
    //> cmp #$1c                   ;against preset amount
    //> bcs SetRunSpd              ;if greater than a certain amount, branch ahead
    if (playerXspeedabsolute >= 0x1C) {
        //  goto SetRunSpd
        return
    }
    temp0 = playerXspeedabsolute
    temp1 = 0x00
    if (!(playerXspeedabsolute >= 0x1C)) {
        //> iny                        ;otherwise increment Y
        temp1 = (temp1 + 1) and 0xFF
        //> cmp #$0e                   ;compare against lower amount
        //> bcs ChkSkid                ;if greater than this but not greater than first, skip increment
        if (!(temp0 >= 0x0E)) {
            //> iny                        ;otherwise increment Y again
            temp1 = (temp1 + 1) and 0xFF
        }
        //> ChkSkid:    lda SavedJoypadBits        ;get controller bits
        temp0 = savedJoypadBits
        //> and #%01111111             ;mask out A button
        temp2 = temp0 and 0x7F
        //> beq SetAnimSpd             ;if no other buttons pressed, branch ahead of all this
        temp0 = temp2
        if (temp2 != 0) {
            //> and #$03                   ;mask out all others except left and right
            temp3 = temp0 and 0x03
            //> cmp Player_MovingDir       ;check against moving direction
            //> bne ProcSkid               ;if left/right controller bits <> moving direction, branch
            temp0 = temp3
            if (temp3 == playerMovingdir) {
                //> lda #$00                   ;otherwise set zero value here
                temp0 = 0x00
            }
        } else {
            //> SetAnimSpd: lda PlayerAnimTmrData,y    ;get animation timer setting using Y as offset
            temp0 = playerAnimTmrData[temp1]
            //> sta PlayerAnimTimerSet
            playerAnimTimerSet = temp0
            //> rts
            return
        }
    }
    //> SetRunSpd:  sta RunningSpeed           ;store zero or running speed here
    runningSpeed = temp0
    //> jmp SetAnimSpd
    //> ProcSkid:   lda Player_XSpeedAbsolute  ;check player's walking/running speed
    temp0 = playerXspeedabsolute
    //> cmp #$0b                   ;against one last amount
    //> bcs SetAnimSpd             ;if greater than this amount, branch
    if (!(temp0 >= 0x0B)) {
        //> lda PlayerFacingDir
        temp0 = playerFacingDir
        //> sta Player_MovingDir       ;otherwise use facing direction to set moving direction
        playerMovingdir = temp0
        //> lda #$00
        temp0 = 0x00
        //> sta Player_X_Speed         ;nullify player's horizontal speed
        playerXSpeed = temp0
        //> sta Player_X_MoveForce     ;and dummy variable for player
        playerXMoveforce = temp0
    }
}

// Decompiled from ImposeFriction
fun imposeFriction(A: Int): Int {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var frictionAdderHigh by MemoryByte(FrictionAdderHigh)
    var frictionAdderLow by MemoryByte(FrictionAdderLow)
    var maximumLeftSpeed by MemoryByte(MaximumLeftSpeed)
    var maximumRightSpeed by MemoryByte(MaximumRightSpeed)
    var playerCollisionbits by MemoryByte(Player_CollisionBits)
    var playerXspeedabsolute by MemoryByte(Player_XSpeedAbsolute)
    var playerXMoveforce by MemoryByte(Player_X_MoveForce)
    var playerXSpeed by MemoryByte(Player_X_Speed)
    //> ImposeFriction:
    //> and Player_CollisionBits  ;perform AND between left/right controller bits and collision flag
    temp0 = A and playerCollisionbits
    //> cmp #$00                  ;then compare to zero (this instruction is redundant)
    //> bne JoypFrict             ;if any bits set, branch to next part
    temp1 = temp0
    if (temp0 == 0) {
        //> lda Player_X_Speed
        temp1 = playerXSpeed
        //> beq SetAbsSpd             ;if player has no horizontal speed, branch ahead to last part
        if (temp1 != 0) {
            //> bpl RghtFrict             ;if player moving to the right, branch to slow
            if ((temp1 and 0x80) != 0) {
                //> bmi LeftFrict             ;otherwise logic dictates player moving left, branch to slow
                if ((temp1 and 0x80) == 0) {
                }
            }
        } else {
            //> SetAbsSpd: sta Player_XSpeedAbsolute ;store walking/running speed here and leave
            playerXspeedabsolute = temp1
            //> rts
            return A
        }
    }
    //> JoypFrict: lsr                       ;put right controller bit into carry
    temp1 = temp1 shr 1
    //> bcc RghtFrict             ;if left button pressed, carry = 0, thus branch
    if ((temp1 and 0x01) != 0) {
        //> LeftFrict: lda Player_X_MoveForce    ;load value set here
        temp1 = playerXMoveforce
        //> clc
        //> adc FrictionAdderLow      ;add to it another value set here
        //> sta Player_X_MoveForce    ;store here
        playerXMoveforce = (temp1 + frictionAdderLow) and 0xFF
        //> lda Player_X_Speed
        temp1 = playerXSpeed
        //> adc FrictionAdderHigh     ;add value plus carry to horizontal speed
        //> sta Player_X_Speed        ;set as new horizontal speed
        playerXSpeed = (temp1 + frictionAdderHigh + (if (temp1 + frictionAdderLow > 0xFF) 1 else 0)) and 0xFF
        //> cmp MaximumRightSpeed     ;compare against maximum value for right movement
        //> bmi XSpdSign              ;if horizontal speed greater negatively, branch
        temp1 = (temp1 + frictionAdderHigh + (if (temp1 + frictionAdderLow > 0xFF) 1 else 0)) and 0xFF
        if (!(((temp1 + frictionAdderHigh + if (temp1 + frictionAdderLow > 0xFF) 1 else 0) and 0xFF) - maximumRightSpeed < 0)) {
            //> lda MaximumRightSpeed     ;otherwise set preset value as horizontal speed
            temp1 = maximumRightSpeed
            //> sta Player_X_Speed        ;thus slowing the player's left movement down
            playerXSpeed = temp1
            //> jmp SetAbsSpd             ;skip to the end
        }
    } else {
        //> RghtFrict: lda Player_X_MoveForce    ;load value set here
        temp1 = playerXMoveforce
        //> sec
        //> sbc FrictionAdderLow      ;subtract from it another value set here
        //> sta Player_X_MoveForce    ;store here
        playerXMoveforce = (temp1 - frictionAdderLow) and 0xFF
        //> lda Player_X_Speed
        temp1 = playerXSpeed
        //> sbc FrictionAdderHigh     ;subtract value plus borrow from horizontal speed
        //> sta Player_X_Speed        ;set as new horizontal speed
        playerXSpeed = (temp1 - frictionAdderHigh - (if (temp1 - frictionAdderLow >= 0) 0 else 1)) and 0xFF
        //> cmp MaximumLeftSpeed      ;compare against maximum value for left movement
        //> bpl XSpdSign              ;if horizontal speed greater positively, branch
        temp1 = (temp1 - frictionAdderHigh - (if (temp1 - frictionAdderLow >= 0) 0 else 1)) and 0xFF
        if (((temp1 - frictionAdderHigh - if (temp1 - frictionAdderLow >= 0) 0 else 1) and 0xFF) - maximumLeftSpeed < 0) {
            //> lda MaximumLeftSpeed      ;otherwise set preset value as horizontal speed
            temp1 = maximumLeftSpeed
            //> sta Player_X_Speed        ;thus slowing the player's right movement down
            playerXSpeed = temp1
        }
        //> XSpdSign:  cmp #$00                  ;if player not moving or moving to the right,
        //> bpl SetAbsSpd             ;branch and leave horizontal speed value unmodified
        if (temp1 < 0) {
            //> eor #$ff
            temp2 = temp1 xor 0xFF
            //> clc                       ;otherwise get two's compliment to get absolute
            //> adc #$01                  ;unsigned walking/running speed
        }
    }
    return 0  // FIX: Default return
}

// Decompiled from ProcFireball_Bubble
fun procfireballBubble() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var temp5: Int = 0
    var aBButtons by MemoryByte(A_B_Buttons)
    var areaType by MemoryByte(AreaType)
    var crouchingFlag by MemoryByte(CrouchingFlag)
    var fireballCounter by MemoryByte(FireballCounter)
    var fireballThrowingTimer by MemoryByte(FireballThrowingTimer)
    var objectOffset by MemoryByte(ObjectOffset)
    var playerAnimTimer by MemoryByte(PlayerAnimTimer)
    var playerAnimTimerSet by MemoryByte(PlayerAnimTimerSet)
    var playerStatus by MemoryByte(PlayerStatus)
    var playerState by MemoryByte(Player_State)
    var playerYHighpos by MemoryByte(Player_Y_HighPos)
    var previousaBButtons by MemoryByte(PreviousA_B_Buttons)
    var square1SoundQueue by MemoryByte(Square1SoundQueue)
    val fireballState by MemoryByteIndexed(Fireball_State)
    //> ProcFireball_Bubble:
    //> lda PlayerStatus           ;check player's status
    //> cmp #$02
    //> bcc ProcAirBubbles         ;if not fiery, branch
    temp0 = playerStatus
    if (DEBUG_GAME_CORE) { System.err.println("  procfireballBubble: playerStatus=$playerStatus, areaType=$areaType") }
    if (playerStatus >= 0x02) {
        //> lda A_B_Buttons
        temp0 = aBButtons
        //> and #B_Button              ;check for b button pressed
        temp1 = temp0 and B_Button
        //> beq ProcFireballs          ;branch if not pressed
        temp0 = temp1
        if (temp1 != 0) {
            //> and PreviousA_B_Buttons
            temp2 = temp0 and previousaBButtons
            //> bne ProcFireballs          ;if button pressed in previous frame, branch
            temp0 = temp2
            if (temp2 == 0) {
                //> lda FireballCounter        ;load fireball counter
                temp0 = fireballCounter
                //> and #%00000001             ;get LSB and use as offset for buffer
                temp3 = temp0 and 0x01
                //> tax
                //> lda Fireball_State,x       ;load fireball state
                temp0 = fireballState[temp3]
                //> bne ProcFireballs          ;if not inactive, branch
                temp4 = temp3
                if (temp0 == 0) {
                    //> ldy Player_Y_HighPos       ;if player too high or too low, branch
                    //> dey
                    playerYHighpos = (playerYHighpos - 1) and 0xFF
                    //> bne ProcFireballs
                    temp5 = playerYHighpos
                    if (playerYHighpos == 0) {
                        //> lda CrouchingFlag          ;if player crouching, branch
                        temp0 = crouchingFlag
                        //> bne ProcFireballs
                        if (temp0 == 0) {
                            //> lda Player_State           ;if player's state = climbing, branch
                            temp0 = playerState
                            //> cmp #$03
                            //> beq ProcFireballs
                            if (temp0 != 0x03) {
                                //> lda #Sfx_Fireball          ;play fireball sound effect
                                temp0 = Sfx_Fireball
                                //> sta Square1SoundQueue
                                square1SoundQueue = temp0
                                //> lda #$02                   ;load state
                                temp0 = 0x02
                                //> sta Fireball_State,x
                                fireballState[temp4] = temp0
                                //> ldy PlayerAnimTimerSet     ;copy animation frame timer setting
                                temp5 = playerAnimTimerSet
                                //> sty FireballThrowingTimer  ;into fireball throwing timer
                                fireballThrowingTimer = temp5
                                //> dey
                                temp5 = (temp5 - 1) and 0xFF
                                //> sty PlayerAnimTimer        ;decrement and store in player's animation timer
                                playerAnimTimer = temp5
                                //> inc FireballCounter        ;increment fireball counter
                                fireballCounter = (fireballCounter + 1) and 0xFF
                            }
                        }
                    }
                }
            }
        }
        //> ProcFireballs:
        //> ldx #$00
        temp4 = 0x00
        //> jsr FireballObjCore  ;process first fireball object
        if (DEBUG_GAME_CORE) { System.err.print("  fireballObjCore(0)..."); System.err.flush() }
        fireballObjCore(temp4)
        if (DEBUG_GAME_CORE) System.err.println("done")
        //> ldx #$01
        temp4 = 0x01
        //> jsr FireballObjCore  ;process second fireball object, then do air bubbles
        if (DEBUG_GAME_CORE) { System.err.print("  fireballObjCore(1)..."); System.err.flush() }
        fireballObjCore(temp4)
        if (DEBUG_GAME_CORE) System.err.println("done")
    }
    //> ProcAirBubbles:
    //> lda AreaType                ;if not water type level, skip the rest of this
    temp0 = areaType
    //> bne BublExit
    if (DEBUG_GAME_CORE) { System.err.print("  procAirBubbles (areaType=$areaType)..."); System.err.flush() }
    if (temp0 == 0) {
        //> ldx #$02                    ;otherwise load counter and use as offset
        temp4 = 0x02
        do {
            if (DEBUG_GAME_CORE) { System.err.print("bubble[$temp4]..."); System.err.flush() }
            //> BublLoop: stx ObjectOffset            ;store offset
            objectOffset = temp4
            //> jsr BubbleCheck             ;check timers and coordinates, create air bubble
            if (DEBUG_GAME_CORE) { System.err.print("check..."); System.err.flush() }
            bubbleCheck(temp4)
            //> jsr RelativeBubblePosition  ;get relative coordinates
            if (DEBUG_GAME_CORE) { System.err.print("rel..."); System.err.flush() }
            relativeBubblePosition()
            //> jsr GetBubbleOffscreenBits  ;get offscreen information
            if (DEBUG_GAME_CORE) { System.err.print("offscr..."); System.err.flush() }
            getBubbleOffscreenBits()
            //> jsr DrawBubble              ;draw the air bubble
            if (DEBUG_GAME_CORE) { System.err.print("draw..."); System.err.flush() }
            drawBubble(temp4)
            //> dex
            temp4 = (temp4 - 1) and 0xFF
            if (DEBUG_GAME_CORE) System.err.println("next=$temp4")
            //> bpl BublLoop                ;do this until all three are handled
        } while ((temp4 and 0x80) == 0)
    }
    if (DEBUG_GAME_CORE) System.err.println("done")
    //> BublExit: rts                         ;then leave
    return
}

// Decompiled from FireballObjCore
fun fireballObjCore(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var fballOffscreenbits by MemoryByte(FBall_OffscreenBits)
    var fireballRelXpos by MemoryByte(Fireball_Rel_XPos)
    var fireballRelYpos by MemoryByte(Fireball_Rel_YPos)
    var objectOffset by MemoryByte(ObjectOffset)
    var playerFacingDir by MemoryByte(PlayerFacingDir)
    var playerPageloc by MemoryByte(Player_PageLoc)
    var playerXPosition by MemoryByte(Player_X_Position)
    var playerYPosition by MemoryByte(Player_Y_Position)
    val altSprdataoffset by MemoryByteIndexed(Alt_SprDataOffset)
    val fballSprdataoffset by MemoryByteIndexed(FBall_SprDataOffset)
    val fireballXSpdData by MemoryByteIndexed(FireballXSpdData)
    val fireballBoundboxctrl by MemoryByteIndexed(Fireball_BoundBoxCtrl)
    val fireballPageloc by MemoryByteIndexed(Fireball_PageLoc)
    val fireballState by MemoryByteIndexed(Fireball_State)
    val fireballXPosition by MemoryByteIndexed(Fireball_X_Position)
    val fireballXSpeed by MemoryByteIndexed(Fireball_X_Speed)
    val fireballYHighpos by MemoryByteIndexed(Fireball_Y_HighPos)
    val fireballYPosition by MemoryByteIndexed(Fireball_Y_Position)
    val fireballYSpeed by MemoryByteIndexed(Fireball_Y_Speed)
    val spriteXPosition by MemoryByteIndexed(Sprite_X_Position)
    val spriteYPosition by MemoryByteIndexed(Sprite_Y_Position)
    //> FireballObjCore:
    //> stx ObjectOffset             ;store offset as current object
    objectOffset = X
    //> lda Fireball_State,x         ;check for d7 = 1
    //> asl
    //> bcs FireballExplosion        ;if so, branch to get relative coordinates and draw explosion
    if ((fireballState[X] and 0x80) != 0) {
        //  goto FireballExplosion
        return
    }
    temp0 = (fireballState[X] shl 1) and 0xFF
    if ((fireballState[X] and 0x80) == 0) {
        //> ldy Fireball_State,x         ;if fireball inactive, branch to leave
        //> beq NoFBall
        temp1 = fireballState[X]
        if (fireballState[X] != 0) {
            //> dey                          ;if fireball state set to 1, skip this part and just run it
            temp1 = (temp1 - 1) and 0xFF
            //> beq RunFB
            if (temp1 != 0) {
                //> lda Player_X_Position        ;get player's horizontal position
                temp0 = playerXPosition
                //> adc #$04                     ;add four pixels and store as fireball's horizontal position
                //> sta Fireball_X_Position,x
                fireballXPosition[X] = (temp0 + 0x04 + (if ((fireballState[X] and 0x80) != 0) 1 else 0)) and 0xFF
                //> lda Player_PageLoc           ;get player's page location
                temp0 = playerPageloc
                //> adc #$00                     ;add carry and store as fireball's page location
                //> sta Fireball_PageLoc,x
                fireballPageloc[X] = (temp0 + (if (temp0 + 0x04 + (if ((fireballState[X] and 0x80) != 0) 1 else 0) > 0xFF) 1 else 0)) and 0xFF
                //> lda Player_Y_Position        ;get player's vertical position and store
                temp0 = playerYPosition
                //> sta Fireball_Y_Position,x
                fireballYPosition[X] = temp0
                //> lda #$01                     ;set high byte of vertical position
                temp0 = 0x01
                //> sta Fireball_Y_HighPos,x
                fireballYHighpos[X] = temp0
                //> ldy PlayerFacingDir          ;get player's facing direction
                temp1 = playerFacingDir
                //> dey                          ;decrement to use as offset here
                temp1 = (temp1 - 1) and 0xFF
                //> lda FireballXSpdData,y       ;set horizontal speed of fireball accordingly
                temp0 = fireballXSpdData[temp1]
                //> sta Fireball_X_Speed,x
                fireballXSpeed[X] = temp0
                //> lda #$04                     ;set vertical speed of fireball
                temp0 = 0x04
                //> sta Fireball_Y_Speed,x
                fireballYSpeed[X] = temp0
                //> lda #$07
                temp0 = 0x07
                //> sta Fireball_BoundBoxCtrl,x  ;set bounding box size control for fireball
                fireballBoundboxctrl[X] = temp0
                //> dec Fireball_State,x         ;decrement state to 1 to skip this part from now on
                fireballState[X] = (fireballState[X] - 1) and 0xFF
            }
            //> RunFB:   txa                          ;add 7 to offset to use
            //> clc                          ;as fireball offset for next routines
            //> adc #$07
            //> tax
            //> lda #$50                     ;set downward movement force here
            temp0 = 0x50
            //> sta $00
            memory[0x0] = temp0.toUByte()
            //> lda #$03                     ;set maximum speed here
            temp0 = 0x03
            //> sta $02
            memory[0x2] = temp0.toUByte()
            //> lda #$00
            temp0 = 0x00
            //> jsr ImposeGravity            ;do sub here to impose gravity on fireball and move vertically
            if (DEBUG_GAME_CORE) { System.err.print("    imposeGravity..."); System.err.flush() }
            imposeGravity(temp0, (X + 0x07) and 0xFF)
            if (DEBUG_GAME_CORE) System.err.println("done")
            //> jsr MoveObjectHorizontally   ;do another sub to move it horizontally
            if (DEBUG_GAME_CORE) { System.err.print("    moveObjectHorizontally..."); System.err.flush() }
            moveObjectHorizontally((X + 0x07) and 0xFF)
            if (DEBUG_GAME_CORE) System.err.println("done")
            //> ldx ObjectOffset             ;return fireball offset to X
            //> jsr RelativeFireballPosition ;get relative coordinates
            if (DEBUG_GAME_CORE) { System.err.print("    relativeFireballPosition..."); System.err.flush() }
            relativeFireballPosition()
            if (DEBUG_GAME_CORE) System.err.println("done")
            //> jsr GetFireballOffscreenBits ;get offscreen information
            if (DEBUG_GAME_CORE) { System.err.print("    getFireballOffscreenBits..."); System.err.flush() }
            getFireballOffscreenBits()
            if (DEBUG_GAME_CORE) System.err.println("done")
            //> jsr GetFireballBoundBox      ;get bounding box coordinates
            if (DEBUG_GAME_CORE) { System.err.print("    getFireballBoundBox..."); System.err.flush() }
            getFireballBoundBox(objectOffset)
            if (DEBUG_GAME_CORE) System.err.println("done")
            //> jsr FireballBGCollision      ;do fireball to background collision detection
            if (DEBUG_GAME_CORE) { System.err.print("    fireballBGCollision..."); System.err.flush() }
            fireballBGCollision(objectOffset)
            if (DEBUG_GAME_CORE) System.err.println("done")
            //> lda FBall_OffscreenBits      ;get fireball offscreen bits
            temp0 = fballOffscreenbits
            //> and #%11001100               ;mask out certain bits
            temp2 = temp0 and 0xCC
            //> bne EraseFB                  ;if any bits still set, branch to kill fireball
            temp0 = temp2
            temp3 = objectOffset
            if (temp2 == 0) {
                //> jsr FireballEnemyCollision   ;do fireball to enemy collision detection and deal with collisions
                fireballEnemyCollision(temp3)
                //> jmp DrawFireball             ;draw fireball appropriately and leave
            }
            //> EraseFB: lda #$00                     ;erase fireball state
            temp0 = 0x00
            //> sta Fireball_State,x
            fireballState[temp3] = temp0
        }
        //> NoFBall: rts                          ;leave
        return
    } else {
        //> FireballExplosion:
        //> jsr RelativeFireballPosition
        relativeFireballPosition()
        //> jmp DrawExplosion_Fireball
    }
    //> DrawFireball:
    //> ldy FBall_SprDataOffset,x  ;get fireball's sprite data offset
    temp1 = fballSprdataoffset[temp3]
    //> lda Fireball_Rel_YPos      ;get relative vertical coordinate
    temp0 = fireballRelYpos
    //> sta Sprite_Y_Position,y    ;store as sprite Y coordinate
    spriteYPosition[temp1] = temp0
    //> lda Fireball_Rel_XPos      ;get relative horizontal coordinate
    temp0 = fireballRelXpos
    //> sta Sprite_X_Position,y    ;store as sprite X coordinate, then do shared code
    spriteXPosition[temp1] = temp0
    //> DrawExplosion_Fireball:
    //> ldy Alt_SprDataOffset,x  ;get OAM data offset of alternate sort for fireball's explosion
    temp1 = altSprdataoffset[temp3]
    //> lda Fireball_State,x     ;load fireball state
    temp0 = fireballState[temp3]
    //> inc Fireball_State,x     ;increment state for next frame
    fireballState[temp3] = (fireballState[temp3] + 1) and 0xFF
    //> lsr                      ;divide by 2
    temp0 = temp0 shr 1
    //> and #%00000111           ;mask out all but d3-d1
    temp4 = temp0 and 0x07
    //> cmp #$03                 ;check to see if time to kill fireball
    //> bcs KillFireBall         ;branch if so, otherwise continue to draw explosion
    temp0 = temp4
    if (!(temp4 >= 0x03)) {
    }
    //> KillFireBall:
    //> lda #$00                    ;clear fireball state to kill it
    temp0 = 0x00
    //> sta Fireball_State,x
    fireballState[temp3] = temp0
    //> rts
    return
}

// Decompiled from BubbleCheck
fun bubbleCheck(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var airBubbleTimer by MemoryByte(AirBubbleTimer)
    val bubbleMforcedata by MemoryByteIndexed(Bubble_MForceData)
    val bubbleYmfDummy by MemoryByteIndexed(Bubble_YMF_Dummy)
    val bubbleYPosition by MemoryByteIndexed(Bubble_Y_Position)
    val pseudoRandomBitReg by MemoryByteIndexed(PseudoRandomBitReg)
    //> BubbleCheck:
    //> lda PseudoRandomBitReg+1,x  ;get part of LSFR
    //> and #$01
    temp0 = pseudoRandomBitReg[1 + X] and 0x01
    //> sta $07                     ;store pseudorandom bit here
    memory[0x7] = temp0.toUByte()
    //> lda Bubble_Y_Position,x     ;get vertical coordinate for air bubble
    //> cmp #$f8                    ;if offscreen coordinate not set,
    //> bne MoveBubl                ;branch to move air bubble
    temp1 = bubbleYPosition[X]
    if (temp1 == 0xF8) {
        // Bubble at offscreen coordinate, check timer to create new bubble
        //> lda AirBubbleTimer          ;if air bubble timer not expired,
        temp1 = airBubbleTimer
        //> bne ExitBubl                ;branch to leave, otherwise create new air bubble
        if (temp1 != 0) {
            //> ExitBubl: rts                      ;leave
            return
        }
        // Timer expired - setup new bubble (but this path isn't implemented, just return)
        return
    }
    // MoveBubl: bubble exists, move it
    //> MoveBubl: ldy $07                  ;get pseudorandom bit again, use as offset
    //> lda Bubble_YMF_Dummy,x
    temp1 = bubbleYmfDummy[X]
    //> sec                      ;subtract pseudorandom amount from dummy variable
    //> sbc Bubble_MForceData,y
    //> sta Bubble_YMF_Dummy,x   ;save dummy variable
    bubbleYmfDummy[X] = (temp1 - bubbleMforcedata[memory[0x7].toInt()]) and 0xFF
    //> lda Bubble_Y_Position,x
    temp1 = bubbleYPosition[X]
    //> sbc #$00                 ;subtract borrow from airbubble's vertical coordinate
    //> cmp #$20                 ;if below the status bar,
    //> bcs Y_Bubl               ;branch to go ahead and use to move air bubble upwards
    temp1 = (temp1 - (if (temp1 - bubbleMforcedata[memory[0x7].toInt()] >= 0) 0 else 1)) and 0xFF
    temp2 = memory[0x7].toInt()
    if (!(((temp1 - if (temp1 - bubbleMforcedata[memory[0x7].toInt()] >= 0) 0 else 1) and 0xFF) >= 0x20)) {
        //> lda #$f8                 ;otherwise set offscreen coordinate
        temp1 = 0xF8
    }
    //> Y_Bubl:   sta Bubble_Y_Position,x  ;store as new vertical coordinate for air bubble
    bubbleYPosition[X] = temp1
}

// Decompiled from SetupBubble
fun setupBubble(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var airBubbleTimer by MemoryByte(AirBubbleTimer)
    var playerFacingDir by MemoryByte(PlayerFacingDir)
    var playerPageloc by MemoryByte(Player_PageLoc)
    var playerXPosition by MemoryByte(Player_X_Position)
    var playerYPosition by MemoryByte(Player_Y_Position)
    val bubbleTimerData by MemoryByteIndexed(BubbleTimerData)
    val bubbleMforcedata by MemoryByteIndexed(Bubble_MForceData)
    val bubblePageloc by MemoryByteIndexed(Bubble_PageLoc)
    val bubbleXPosition by MemoryByteIndexed(Bubble_X_Position)
    val bubbleYmfDummy by MemoryByteIndexed(Bubble_YMF_Dummy)
    val bubbleYHighpos by MemoryByteIndexed(Bubble_Y_HighPos)
    val bubbleYPosition by MemoryByteIndexed(Bubble_Y_Position)
    //> SetupBubble:
    //> ldy #$00                 ;load default value here
    //> lda PlayerFacingDir      ;get player's facing direction
    //> lsr                      ;move d0 to carry
    playerFacingDir = playerFacingDir shr 1
    //> bcc PosBubl              ;branch to use default value if facing left
    temp0 = playerFacingDir
    temp1 = 0x00
    if ((playerFacingDir and 0x01) != 0) {
        //> ldy #$08                 ;otherwise load alternate value here
        temp1 = 0x08
    }
    //> PosBubl:  tya                      ;use value loaded as adder
    //> adc Player_X_Position    ;add to player's horizontal position
    //> sta Bubble_X_Position,x  ;save as horizontal position for airbubble
    bubbleXPosition[X] = (temp1 + playerXPosition + (if ((playerFacingDir and 0x01) != 0) 1 else 0)) and 0xFF
    //> lda Player_PageLoc
    temp0 = playerPageloc
    //> adc #$00                 ;add carry to player's page location
    //> sta Bubble_PageLoc,x     ;save as page location for airbubble
    bubblePageloc[X] = (temp0 + (if (temp1 + playerXPosition + (if ((playerFacingDir and 0x01) != 0) 1 else 0) > 0xFF) 1 else 0)) and 0xFF
    //> lda Player_Y_Position
    temp0 = playerYPosition
    //> clc                      ;add eight pixels to player's vertical position
    //> adc #$08
    //> sta Bubble_Y_Position,x  ;save as vertical position for air bubble
    bubbleYPosition[X] = (temp0 + 0x08) and 0xFF
    //> lda #$01
    temp0 = 0x01
    //> sta Bubble_Y_HighPos,x   ;set vertical high byte for air bubble
    bubbleYHighpos[X] = temp0
    //> ldy $07                  ;get pseudorandom bit, use as offset
    temp1 = memory[0x7].toInt()
    //> lda BubbleTimerData,y    ;get data for air bubble timer
    temp0 = bubbleTimerData[temp1]
    //> sta AirBubbleTimer       ;set air bubble timer
    airBubbleTimer = temp0
    //> MoveBubl: ldy $07                  ;get pseudorandom bit again, use as offset
    temp1 = memory[0x7].toInt()
    //> lda Bubble_YMF_Dummy,x
    temp0 = bubbleYmfDummy[X]
    //> sec                      ;subtract pseudorandom amount from dummy variable
    //> sbc Bubble_MForceData,y
    //> sta Bubble_YMF_Dummy,x   ;save dummy variable
    bubbleYmfDummy[X] = (temp0 - bubbleMforcedata[temp1]) and 0xFF
    //> lda Bubble_Y_Position,x
    temp0 = bubbleYPosition[X]
    //> sbc #$00                 ;subtract borrow from airbubble's vertical coordinate
    //> cmp #$20                 ;if below the status bar,
    //> bcs Y_Bubl               ;branch to go ahead and use to move air bubble upwards
    temp0 = (temp0 - (if (temp0 - bubbleMforcedata[temp1] >= 0) 0 else 1)) and 0xFF
    if (!(((temp0 - if (temp0 - bubbleMforcedata[temp1] >= 0) 0 else 1) and 0xFF) >= 0x20)) {
        //> lda #$f8                 ;otherwise set offscreen coordinate
        temp0 = 0xF8
    }
    //> Y_Bubl:   sta Bubble_Y_Position,x  ;store as new vertical coordinate for air bubble
    bubbleYPosition[X] = temp0
    //> ExitBubl: rts                      ;leave
    return
}

// Decompiled from RunGameTimer
fun runGameTimer() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var eventMusicQueue by MemoryByte(EventMusicQueue)
    var gameEngineSubroutine by MemoryByte(GameEngineSubroutine)
    var gameTimerCtrlTimer by MemoryByte(GameTimerCtrlTimer)
    var gameTimerExpiredFlag by MemoryByte(GameTimerExpiredFlag)
    var operMode by MemoryByte(OperMode)
    var playerStatus by MemoryByte(PlayerStatus)
    var playerYHighpos by MemoryByte(Player_Y_HighPos)
    val digitModifier by MemoryByteIndexed(DigitModifier)
    val gameTimerDisplay by MemoryByteIndexed(GameTimerDisplay)
    //> RunGameTimer:
    //> lda OperMode               ;get primary mode of operation
    //> beq ExGTimer               ;branch to leave if in title screen mode
    temp0 = operMode
    if (operMode != 0) {
        //> lda GameEngineSubroutine
        temp0 = gameEngineSubroutine
        //> cmp #$08                   ;if routine number less than eight running,
        //> bcc ExGTimer               ;branch to leave
        if (temp0 >= 0x08) {
            //> cmp #$0b                   ;if running death routine,
            //> beq ExGTimer               ;branch to leave
            if (temp0 != 0x0B) {
                //> lda Player_Y_HighPos
                temp0 = playerYHighpos
                //> cmp #$02                   ;if player below the screen,
                //> bcs ExGTimer               ;branch to leave regardless of level type
                if (!(temp0 >= 0x02)) {
                    //> lda GameTimerCtrlTimer     ;if game timer control not yet expired,
                    temp0 = gameTimerCtrlTimer
                    //> bne ExGTimer               ;branch to leave
                    if (temp0 == 0) {
                        //> lda GameTimerDisplay
                        temp0 = gameTimerDisplay[0]
                        //> ora GameTimerDisplay+1     ;otherwise check game timer digits
                        temp1 = temp0 or gameTimerDisplay[1]
                        //> ora GameTimerDisplay+2
                        temp2 = temp1 or gameTimerDisplay[2]
                        //> beq TimeUpOn               ;if game timer digits at 000, branch to time-up code
                        temp0 = temp2
                        if (temp2 != 0) {
                            //> ldy GameTimerDisplay       ;otherwise check first digit
                            //> dey                        ;if first digit not on 1,
                            temp3 = gameTimerDisplay[0]
                            temp3 = (temp3 - 1) and 0xFF
                            //> bne ResGTCtrl              ;branch to reset game timer control
                            if (!(temp3 == 0)) {
                                //  goto ResGTCtrl
                                return
                            }
                            if (temp3 == 0) {
                                //> lda GameTimerDisplay+1     ;otherwise check second and third digits
                                temp0 = gameTimerDisplay[1]
                                //> ora GameTimerDisplay+2
                                temp4 = temp0 or gameTimerDisplay[2]
                                //> bne ResGTCtrl              ;if timer not at 100, branch to reset game timer control
                                if (!(temp4 == 0)) {
                                    //  goto ResGTCtrl
                                    return
                                }
                                temp0 = temp4
                                if (temp4 == 0) {
                                    //> lda #TimeRunningOutMusic
                                    temp0 = TimeRunningOutMusic
                                    //> sta EventMusicQueue        ;otherwise load time running out music
                                    eventMusicQueue = temp0
                                }
                            }
                            //> ResGTCtrl: lda #$18                   ;reset game timer control
                            temp0 = 0x18
                            //> sta GameTimerCtrlTimer
                            gameTimerCtrlTimer = temp0
                            //> ldy #$23                   ;set offset for last digit
                            temp3 = 0x23
                            //> lda #$ff                   ;set value to decrement game timer digit
                            temp0 = 0xFF
                            //> sta DigitModifier+5
                            digitModifier[5] = temp0
                            //> jsr DigitsMathRoutine      ;do sub to decrement game timer slowly
                            digitsMathRoutine(temp3)
                            //> lda #$a4                   ;set status nybbles to update game timer display
                            temp0 = 0xA4
                            //> jmp PrintStatusBarNumbers  ;do sub to update the display
                        }
                        //> TimeUpOn:  sta PlayerStatus           ;init player status (note A will always be zero here)
                        playerStatus = temp0
                        //> jsr ForceInjury            ;do sub to kill the player (note player is small here)
                        forceInjury(temp0)
                        //> inc GameTimerExpiredFlag   ;set game timer expiration flag
                        gameTimerExpiredFlag = (gameTimerExpiredFlag + 1) and 0xFF
                    }
                }
            }
        }
    }
    //> ExGTimer:  rts                        ;leave
    return
}

// Decompiled from ProcessWhirlpools
fun processWhirlpools() {
    var temp0: Int = 0
    var temp1: Int = 0
    var areaType by MemoryByte(AreaType)
    var playerPageloc by MemoryByte(Player_PageLoc)
    var playerXPosition by MemoryByte(Player_X_Position)
    var timerControl by MemoryByte(TimerControl)
    var whirlpoolFlag by MemoryByte(Whirlpool_Flag)
    val whirlpoolLeftextent by MemoryByteIndexed(Whirlpool_LeftExtent)
    val whirlpoolLength by MemoryByteIndexed(Whirlpool_Length)
    val whirlpoolPageloc by MemoryByteIndexed(Whirlpool_PageLoc)
    //> ProcessWhirlpools:
    //> lda AreaType                ;check for water type level
    //> bne ExitWh                  ;branch to leave if not found
    temp0 = areaType
    if (areaType == 0) {
        //> sta Whirlpool_Flag          ;otherwise initialize whirlpool flag
        whirlpoolFlag = temp0
        //> lda TimerControl            ;if master timer control set,
        temp0 = timerControl
        //> bne ExitWh                  ;branch to leave
        if (temp0 == 0) {
            //> ldy #$04                    ;otherwise start with last whirlpool data
            temp1 = 0x04
            while ((temp1 and 0x80) == 0) {
                //> adc #$00                    ;add carry
                //> sta $01                     ;store result as page location of right extent here
                memory[0x1] = ((temp0) and 0xFF).toUByte()
                //> lda Player_X_Position       ;get player's horizontal position
                temp0 = playerXPosition
                //> sec
                //> sbc Whirlpool_LeftExtent,y  ;subtract left extent
                //> lda Player_PageLoc          ;get player's page location
                temp0 = playerPageloc
                //> sbc Whirlpool_PageLoc,y     ;subtract borrow
                //> bmi NextWh                  ;if player too far left, branch to get next data
                temp0 = (temp0 - whirlpoolPageloc[temp1] - (if (temp0 - whirlpoolLeftextent[temp1] >= 0) 0 else 1)) and 0xFF
                if (((temp0 - whirlpoolPageloc[temp1] - if (temp0 - whirlpoolLeftextent[temp1] >= 0) 0 else 1) and 0xFF and 0x80) == 0) {
                    //> lda $02                     ;otherwise get right extent
                    temp0 = memory[0x2].toInt()
                    //> sec
                    //> sbc Player_X_Position       ;subtract player's horizontal coordinate
                    //> lda $01                     ;get right extent's page location
                    temp0 = memory[0x1].toInt()
                    //> sbc Player_PageLoc          ;subtract borrow
                    //> bpl WhirlpoolActivate       ;if player within right extent, branch to whirlpool code
                    temp0 = (temp0 - playerPageloc - (if (temp0 - playerXPosition >= 0) 0 else 1)) and 0xFF
                    if (((temp0 - playerPageloc - if (temp0 - playerXPosition >= 0) 0 else 1) and 0xFF and 0x80) != 0) {
                    }
                }
                do {
                    //> WhLoop: lda Whirlpool_LeftExtent,y  ;get left extent of whirlpool
                    temp0 = whirlpoolLeftextent[temp1]
                    //> clc
                    //> adc Whirlpool_Length,y      ;add length of whirlpool
                    //> sta $02                     ;store result as right extent here
                    memory[0x2] = ((temp0 + whirlpoolLength[temp1]) and 0xFF).toUByte()
                    //> lda Whirlpool_PageLoc,y     ;get page location
                    temp0 = whirlpoolPageloc[temp1]
                    //> beq NextWh                  ;if none or page 0, branch to get next data
                    //> NextWh: dey                         ;move onto next whirlpool data
                    temp1 = (temp1 - 1) and 0xFF
                    //> bpl WhLoop                  ;do this until all whirlpools are checked
                } while ((temp1 and 0x80) == 0)
            }
        }
    }
    //> ExitWh: rts                         ;leave
    return
}

// Decompiled from FlagpoleRoutine
fun flagpoleRoutine() {
    var temp0: Int = 0
    var temp1: Int = 0
    var flagpolefnumYmfdummy by MemoryByte(FlagpoleFNum_YMFDummy)
    var flagpolefnumYPos by MemoryByte(FlagpoleFNum_Y_Pos)
    var flagpoleScore by MemoryByte(FlagpoleScore)
    var gameEngineSubroutine by MemoryByte(GameEngineSubroutine)
    var objectOffset by MemoryByte(ObjectOffset)
    var playerState by MemoryByte(Player_State)
    var playerYPosition by MemoryByte(Player_Y_Position)
    val digitModifier by MemoryByteIndexed(DigitModifier)
    val enemyId by MemoryByteIndexed(Enemy_ID)
    val enemyYmfDummy by MemoryByteIndexed(Enemy_YMF_Dummy)
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    val flagpoleScoreDigits by MemoryByteIndexed(FlagpoleScoreDigits)
    val flagpoleScoreMods by MemoryByteIndexed(FlagpoleScoreMods)
    //> FlagpoleRoutine:
    //> ldx #$05                  ;set enemy object offset
    //> stx ObjectOffset          ;to special use slot
    objectOffset = 0x05
    //> lda Enemy_ID,x
    //> cmp #FlagpoleFlagObject   ;if flagpole flag not found,
    //> bne ExitFlagP             ;branch to leave
    temp0 = enemyId[0x05]
    temp1 = 0x05
    if (enemyId[0x05] == FlagpoleFlagObject) {
        //> lda GameEngineSubroutine
        temp0 = gameEngineSubroutine
        //> cmp #$04                  ;if flagpole slide routine not running,
        //> bne SkipScore             ;branch to near the end of code
        if (!(temp0 - 0x04 == 0)) {
            //  goto SkipScore
            return
        }
        if (temp0 == 0x04) {
            //> lda Player_State
            temp0 = playerState
            //> cmp #$03                  ;if player state not climbing,
            //> bne SkipScore             ;branch to near the end of code
            if (!(temp0 - 0x03 == 0)) {
                //  goto SkipScore
                return
            }
            if (temp0 == 0x03) {
                //> lda Enemy_Y_Position,x    ;check flagpole flag's vertical coordinate
                temp0 = enemyYPosition[temp1]
                //> cmp #$aa                  ;if flagpole flag down to a certain point,
                //> bcs GiveFPScr             ;branch to end the level
                if (!(temp0 >= 0xAA)) {
                    //> lda Player_Y_Position     ;check player's vertical coordinate
                    temp0 = playerYPosition
                    //> cmp #$a2                  ;if player down to a certain point,
                    //> bcs GiveFPScr             ;branch to end the level
                    if (!(temp0 >= 0xA2)) {
                        //> lda Enemy_YMF_Dummy,x
                        temp0 = enemyYmfDummy[temp1]
                        //> adc #$ff                  ;add movement amount to dummy variable
                        //> sta Enemy_YMF_Dummy,x     ;save dummy variable
                        enemyYmfDummy[temp1] = (temp0 + 0xFF + (if (temp0 >= 0xA2) 1 else 0)) and 0xFF
                        //> lda Enemy_Y_Position,x    ;get flag's vertical coordinate
                        temp0 = enemyYPosition[temp1]
                        //> adc #$01                  ;add 1 plus carry to move flag, and
                        //> sta Enemy_Y_Position,x    ;store vertical coordinate
                        enemyYPosition[temp1] = (temp0 + 0x01 + (if (temp0 + 0xFF + (if (temp0 >= 0xA2) 1 else 0) > 0xFF) 1 else 0)) and 0xFF
                        //> lda FlagpoleFNum_YMFDummy
                        temp0 = flagpolefnumYmfdummy
                        //> sec                       ;subtract movement amount from dummy variable
                        //> sbc #$ff
                        //> sta FlagpoleFNum_YMFDummy ;save dummy variable
                        flagpolefnumYmfdummy = (temp0 - 0xFF) and 0xFF
                        //> lda FlagpoleFNum_Y_Pos
                        temp0 = flagpolefnumYPos
                        //> sbc #$01                  ;subtract one plus borrow to move floatey number,
                        //> sta FlagpoleFNum_Y_Pos    ;and store vertical coordinate here
                        flagpolefnumYPos = (temp0 - 0x01 - (if (temp0 - 0xFF >= 0) 0 else 1)) and 0xFF
                    }
                }
            }
        }
        //> SkipScore: jmp FPGfx                 ;jump to skip ahead and draw flag and floatey number
        //> GiveFPScr: ldy FlagpoleScore         ;get score offset from earlier (when player touched flagpole)
        //> lda FlagpoleScoreMods,y   ;get amount to award player points
        temp0 = flagpoleScoreMods[flagpoleScore]
        //> ldx FlagpoleScoreDigits,y ;get digit with which to award points
        temp1 = flagpoleScoreDigits[flagpoleScore]
        //> sta DigitModifier,x       ;store in digit modifier
        digitModifier[temp1] = temp0
        //> jsr AddToScore            ;do sub to award player points depending on height of collision
        addToScore()
        //> lda #$05
        temp0 = 0x05
        //> sta GameEngineSubroutine  ;set to run end-of-level subroutine on next frame
        gameEngineSubroutine = temp0
        //> FPGfx:     jsr GetEnemyOffscreenBits ;get offscreen information
        getEnemyOffscreenBits(temp1)
        //> jsr RelativeEnemyPosition ;get relative coordinates
        relativeEnemyPosition()
        //> jsr FlagpoleGfxHandler    ;draw flagpole flag and floatey number
        flagpoleGfxHandler(temp1)
    }
    //> ExitFlagP: rts
    return
}

// Decompiled from Setup_Vine
fun setupVine(X: Int, Y: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var square2SoundQueue by MemoryByte(Square2SoundQueue)
    var vineFlagOffset by MemoryByte(VineFlagOffset)
    var vinestartYPosition by MemoryByte(VineStart_Y_Position)
    val blockPageloc by MemoryByteIndexed(Block_PageLoc)
    val blockXPosition by MemoryByteIndexed(Block_X_Position)
    val blockYPosition by MemoryByteIndexed(Block_Y_Position)
    val enemyFlag by MemoryByteIndexed(Enemy_Flag)
    val enemyId by MemoryByteIndexed(Enemy_ID)
    val enemyPageloc by MemoryByteIndexed(Enemy_PageLoc)
    val enemyXPosition by MemoryByteIndexed(Enemy_X_Position)
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    val vineObjOffset by MemoryByteIndexed(VineObjOffset)
    //> Setup_Vine:
    //> lda #VineObject          ;load identifier for vine object
    //> sta Enemy_ID,x           ;store in buffer
    enemyId[X] = VineObject
    //> lda #$01
    //> sta Enemy_Flag,x         ;set flag for enemy object buffer
    enemyFlag[X] = 0x01
    //> lda Block_PageLoc,y
    //> sta Enemy_PageLoc,x      ;copy page location from previous object
    enemyPageloc[X] = blockPageloc[Y]
    //> lda Block_X_Position,y
    //> sta Enemy_X_Position,x   ;copy horizontal coordinate from previous object
    enemyXPosition[X] = blockXPosition[Y]
    //> lda Block_Y_Position,y
    //> sta Enemy_Y_Position,x   ;copy vertical coordinate from previous object
    enemyYPosition[X] = blockYPosition[Y]
    //> ldy VineFlagOffset       ;load vine flag/offset to next available vine slot
    //> bne NextVO               ;if set at all, don't bother to store vertical
    temp0 = blockYPosition[Y]
    temp1 = vineFlagOffset
    if (vineFlagOffset == 0) {
        //> sta VineStart_Y_Position ;otherwise store vertical coordinate here
        vinestartYPosition = temp0
    }
    //> NextVO: txa                      ;store object offset to next available vine slot
    //> sta VineObjOffset,y      ;using vine flag as offset
    vineObjOffset[temp1] = X
    //> inc VineFlagOffset       ;increment vine flag offset
    vineFlagOffset = (vineFlagOffset + 1) and 0xFF
    //> lda #Sfx_GrowVine
    temp0 = Sfx_GrowVine
    //> sta Square2SoundQueue    ;load vine grow sound
    square2SoundQueue = temp0
    //> rts
    return
}

// Decompiled from ProcessCannons
fun processCannons() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var areaType by MemoryByte(AreaType)
    var objectOffset by MemoryByte(ObjectOffset)
    var secondaryHardMode by MemoryByte(SecondaryHardMode)
    var timerControl by MemoryByte(TimerControl)
    val cannonBitmasks by MemoryByteIndexed(CannonBitmasks)
    val cannonPageloc by MemoryByteIndexed(Cannon_PageLoc)
    val cannonTimer by MemoryByteIndexed(Cannon_Timer)
    val cannonXPosition by MemoryByteIndexed(Cannon_X_Position)
    val cannonYPosition by MemoryByteIndexed(Cannon_Y_Position)
    val enemyBoundboxctrl by MemoryByteIndexed(Enemy_BoundBoxCtrl)
    val enemyFlag by MemoryByteIndexed(Enemy_Flag)
    val enemyId by MemoryByteIndexed(Enemy_ID)
    val enemyPageloc by MemoryByteIndexed(Enemy_PageLoc)
    val enemyState by MemoryByteIndexed(Enemy_State)
    val enemyXPosition by MemoryByteIndexed(Enemy_X_Position)
    val enemyYHighpos by MemoryByteIndexed(Enemy_Y_HighPos)
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    val pseudoRandomBitReg by MemoryByteIndexed(PseudoRandomBitReg)
    //> ProcessCannons:
    //> lda AreaType                ;get area type
    //> beq ExCannon                ;if water type area, branch to leave
    temp0 = areaType
    if (areaType != 0) {
        //> ldx #$02
        //> ThreeSChk: stx ObjectOffset            ;start at third enemy slot
        objectOffset = 0x02
        //> lda Enemy_Flag,x            ;check enemy buffer flag
        temp0 = enemyFlag[0x02]
        //> bne Chk_BB                  ;if set, branch to check enemy
        temp1 = 0x02
        if (temp0 == 0) {
            //> lda PseudoRandomBitReg+1,x  ;otherwise get part of LSFR
            temp0 = pseudoRandomBitReg[1 + temp1]
            //> ldy SecondaryHardMode       ;get secondary hard mode flag, use as offset
            //> and CannonBitmasks,y        ;mask out bits of LSFR as decided by flag
            temp2 = temp0 and cannonBitmasks[secondaryHardMode]
            //> cmp #$06                    ;check to see if lower nybble is above certain value
            //> bcs Chk_BB                  ;if so, branch to check enemy
            temp0 = temp2
            temp3 = secondaryHardMode
            if (!(temp2 >= 0x06)) {
                //> tay                         ;transfer masked contents of LSFR to Y as pseudorandom offset
                //> lda Cannon_PageLoc,y        ;get page location
                temp0 = cannonPageloc[temp0]
                //> beq Chk_BB                  ;if not set or on page 0, branch to check enemy
                temp3 = temp0
                if (temp0 != 0) {
                    //> lda Cannon_Timer,y          ;get cannon timer
                    temp0 = cannonTimer[temp3]
                    //> beq FireCannon              ;if expired, branch to fire cannon
                    if (temp0 != 0) {
                        //> sbc #$00                    ;otherwise subtract borrow (note carry will always be clear here)
                        //> sta Cannon_Timer,y          ;to count timer down
                        cannonTimer[temp3] = (temp0 - (if (temp2 >= 0x06) 0 else 1)) and 0xFF
                        //> jmp Chk_BB                  ;then jump ahead to check enemy
                    } else {
                        //> FireCannon:
                        //> lda TimerControl           ;if master timer control set,
                        temp0 = timerControl
                        //> bne Chk_BB                 ;branch to check enemy
                        if (temp0 == 0) {
                            //> lda #$0e                   ;otherwise we start creating one
                            temp0 = 0x0E
                            //> sta Cannon_Timer,y         ;first, reset cannon timer
                            cannonTimer[temp3] = temp0
                            //> lda Cannon_PageLoc,y       ;get page location of cannon
                            temp0 = cannonPageloc[temp3]
                            //> sta Enemy_PageLoc,x        ;save as page location of bullet bill
                            enemyPageloc[temp1] = temp0
                            //> lda Cannon_X_Position,y    ;get horizontal coordinate of cannon
                            temp0 = cannonXPosition[temp3]
                            //> sta Enemy_X_Position,x     ;save as horizontal coordinate of bullet bill
                            enemyXPosition[temp1] = temp0
                            //> lda Cannon_Y_Position,y    ;get vertical coordinate of cannon
                            temp0 = cannonYPosition[temp3]
                            //> sec
                            //> sbc #$08                   ;subtract eight pixels (because enemies are 24 pixels tall)
                            //> sta Enemy_Y_Position,x     ;save as vertical coordinate of bullet bill
                            enemyYPosition[temp1] = (temp0 - 0x08) and 0xFF
                            //> lda #$01
                            temp0 = 0x01
                            //> sta Enemy_Y_HighPos,x      ;set vertical high byte of bullet bill
                            enemyYHighpos[temp1] = temp0
                            //> sta Enemy_Flag,x           ;set buffer flag
                            enemyFlag[temp1] = temp0
                            //> lsr                        ;shift right once to init A
                            temp0 = temp0 shr 1
                            //> sta Enemy_State,x          ;then initialize enemy's state
                            enemyState[temp1] = temp0
                            //> lda #$09
                            temp0 = 0x09
                            //> sta Enemy_BoundBoxCtrl,x   ;set bounding box size control for bullet bill
                            enemyBoundboxctrl[temp1] = temp0
                            //> lda #BulletBill_CannonVar
                            temp0 = BulletBill_CannonVar
                            //> sta Enemy_ID,x             ;load identifier for bullet bill (cannon variant)
                            enemyId[temp1] = temp0
                            //> jmp Next3Slt               ;move onto next slot
                        }
                    }
                }
            }
        } else {
            //> Chk_BB:   lda Enemy_ID,x             ;check enemy identifier for bullet bill (cannon variant)
            temp0 = enemyId[temp1]
            //> cmp #BulletBill_CannonVar
            //> bne Next3Slt               ;if not found, branch to get next slot
            if (temp0 == BulletBill_CannonVar) {
                //> jsr OffscreenBoundsCheck   ;otherwise, check to see if it went offscreen
                offscreenBoundsCheck(temp1)
                //> lda Enemy_Flag,x           ;check enemy buffer flag
                temp0 = enemyFlag[temp1]
                //> beq Next3Slt               ;if not set, branch to get next slot
                if (temp0 != 0) {
                    //> jsr GetEnemyOffscreenBits  ;otherwise, get offscreen information
                    getEnemyOffscreenBits(temp1)
                    //> jsr BulletBillHandler      ;then do sub to handle bullet bill
                    bulletBillHandler(temp1)
                }
            }
        }
        //> Next3Slt: dex                        ;move onto next slot
        temp1 = (temp1 - 1) and 0xFF
        //> bpl ThreeSChk              ;do this until first three slots are checked
    }
    //> ExCannon: rts                        ;then leave
    return
}

// Decompiled from BulletBillHandler
fun bulletBillHandler(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var enemyOffscreenbits by MemoryByte(Enemy_OffscreenBits)
    var square2SoundQueue by MemoryByte(Square2SoundQueue)
    var timerControl by MemoryByte(TimerControl)
    val bulletBillXSpdData by MemoryByteIndexed(BulletBillXSpdData)
    val enemyFrameTimer by MemoryByteIndexed(EnemyFrameTimer)
    val enemyMovingdir by MemoryByteIndexed(Enemy_MovingDir)
    val enemyState by MemoryByteIndexed(Enemy_State)
    val enemyXSpeed by MemoryByteIndexed(Enemy_X_Speed)
    //> BulletBillHandler:
    //> lda TimerControl          ;if master timer control set,
    //> bne RunBBSubs             ;branch to run subroutines except movement sub
    if (!(timerControl == 0)) {
        //  goto RunBBSubs
        return
    }
    temp0 = timerControl
    if (timerControl == 0) {
        //> lda Enemy_State,x
        temp0 = enemyState[X]
        //> bne ChkDSte               ;if bullet bill's state set, branch to check defeated state
        if (temp0 == 0) {
            //> lda Enemy_OffscreenBits   ;otherwise load offscreen bits
            temp0 = enemyOffscreenbits
            //> and #%00001100            ;mask out bits
            temp1 = temp0 and 0x0C
            //> cmp #%00001100            ;check to see if all bits are set
            //> beq KillBB                ;if so, branch to kill this object
            temp0 = temp1
            if (temp1 != 0x0C) {
                //> ldy #$01                  ;set to move right by default
                //> jsr PlayerEnemyDiff       ;get horizontal difference between player and bullet bill
                playerEnemyDiff(X)
                //> bmi SetupBB               ;if enemy to the left of player, branch
                temp2 = 0x01
                if ((0x01 and 0x80) == 0) {
                    //> iny                       ;otherwise increment to move left
                    temp2 = (temp2 + 1) and 0xFF
                }
                //> SetupBB:   sty Enemy_MovingDir,x     ;set bullet bill's moving direction
                enemyMovingdir[X] = temp2
                //> dey                       ;decrement to use as offset
                temp2 = (temp2 - 1) and 0xFF
                //> lda BulletBillXSpdData,y  ;get horizontal speed based on moving direction
                temp0 = bulletBillXSpdData[temp2]
                //> sta Enemy_X_Speed,x       ;and store it
                enemyXSpeed[X] = temp0
                //> lda $00                   ;get horizontal difference
                temp0 = memory[0x0].toInt()
                //> adc #$28                  ;add 40 pixels
                //> cmp #$50                  ;if less than a certain amount, player is too close
                //> bcc KillBB                ;to cannon either on left or right side, thus branch
                temp0 = (temp0 + 0x28 + (if (temp1 >= 0x0C) 1 else 0)) and 0xFF
                if (((temp0 + 0x28 + if (temp1 >= 0x0C) 1 else 0) and 0xFF) >= 0x50) {
                    //> lda #$01
                    temp0 = 0x01
                    //> sta Enemy_State,x         ;otherwise set bullet bill's state
                    enemyState[X] = temp0
                    //> lda #$0a
                    temp0 = 0x0A
                    //> sta EnemyFrameTimer,x     ;set enemy frame timer
                    enemyFrameTimer[X] = temp0
                    //> lda #Sfx_Blast
                    temp0 = Sfx_Blast
                    //> sta Square2SoundQueue     ;play fireworks/gunfire sound
                    square2SoundQueue = temp0
                } else {
                    //> KillBB:    jsr EraseEnemyObject      ;kill bullet bill and leave
                    eraseEnemyObject(X)
                    //> rts
                    return
                }
            }
        }
        //> ChkDSte:   lda Enemy_State,x         ;check enemy state for d5 set
        temp0 = enemyState[X]
        //> and #%00100000
        temp3 = temp0 and 0x20
        //> beq BBFly                 ;if not set, skip to move horizontally
        temp0 = temp3
        if (temp3 != 0) {
            //> jsr MoveD_EnemyVertically ;otherwise do sub to move bullet bill vertically
            movedEnemyvertically(X)
        }
        //> BBFly:     jsr MoveEnemyHorizontally ;do sub to move bullet bill horizontally
        moveEnemyHorizontally(X)
    }
    //> RunBBSubs: jsr GetEnemyOffscreenBits ;get offscreen information
    getEnemyOffscreenBits(X)
    //> jsr RelativeEnemyPosition ;get relative coordinates
    relativeEnemyPosition()
    //> jsr GetEnemyBoundBox      ;get bounding box coordinates
    getEnemyBoundBox(X)
    //> jsr PlayerEnemyCollision  ;handle player to enemy collisions
    playerEnemyCollision(X)
    //> jmp EnemyGfxHandler       ;draw the bullet bill and leave
}

// Decompiled from SpawnHammerObj
fun spawnHammerObj() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    val enemyFlag by MemoryByteIndexed(Enemy_Flag)
    val hammerEnemyOffset by MemoryByteIndexed(HammerEnemyOffset)
    val hammerEnemyOfsData by MemoryByteIndexed(HammerEnemyOfsData)
    val miscBoundboxctrl by MemoryByteIndexed(Misc_BoundBoxCtrl)
    val miscState by MemoryByteIndexed(Misc_State)
    val pseudoRandomBitReg by MemoryByteIndexed(PseudoRandomBitReg)
    //> SpawnHammerObj:
    //> lda PseudoRandomBitReg+1 ;get pseudorandom bits from
    //> and #%00000111           ;second part of LSFR
    temp0 = pseudoRandomBitReg[1] and 0x07
    //> bne SetMOfs              ;if any bits are set, branch and use as offset
    temp1 = temp0
    if (temp0 == 0) {
        //> lda PseudoRandomBitReg+1
        temp1 = pseudoRandomBitReg[1]
        //> and #%00001000           ;get d3 from same part of LSFR
        temp2 = temp1 and 0x08
    }
    //> SetMOfs:  tay                      ;use either d3 or d2-d0 for offset here
    //> lda Misc_State,y         ;if any values loaded in
    temp1 = miscState[temp1]
    //> bne NoHammer             ;$2a-$32 where offset is then leave with carry clear
    temp3 = temp1
    if (temp1 == 0) {
        //> ldx HammerEnemyOfsData,y ;get offset of enemy slot to check using Y as offset
        //> lda Enemy_Flag,x         ;check enemy buffer flag at offset
        temp1 = enemyFlag[hammerEnemyOfsData[temp3]]
        //> bne NoHammer             ;if buffer flag set, branch to leave with carry clear
        temp4 = hammerEnemyOfsData[temp3]
        if (temp1 == 0) {
            //> ldx ObjectOffset         ;get original enemy object offset
            temp4 = objectOffset
            //> txa
            //> sta HammerEnemyOffset,y  ;save here
            hammerEnemyOffset[temp3] = temp4
            //> lda #$90
            temp1 = 0x90
            //> sta Misc_State,y         ;save hammer's state here
            miscState[temp3] = temp1
            //> lda #$07
            temp1 = 0x07
            //> sta Misc_BoundBoxCtrl,y  ;set something else entirely, here
            miscBoundboxctrl[temp3] = temp1
            //> sec                      ;return with carry set
            //> rts
            return
        }
    }
    //> NoHammer: ldx ObjectOffset         ;get original enemy object offset
    temp4 = objectOffset
    //> clc                      ;return with carry clear
    //> rts
    return
}

// Decompiled from ProcHammerObj
fun procHammerObj(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    var timerControl by MemoryByte(TimerControl)
    val enemyMovingdir by MemoryByteIndexed(Enemy_MovingDir)
    val enemyPageloc by MemoryByteIndexed(Enemy_PageLoc)
    val enemyState by MemoryByteIndexed(Enemy_State)
    val enemyXPosition by MemoryByteIndexed(Enemy_X_Position)
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    val hammerEnemyOffset by MemoryByteIndexed(HammerEnemyOffset)
    val hammerXSpdData by MemoryByteIndexed(HammerXSpdData)
    val miscPageloc by MemoryByteIndexed(Misc_PageLoc)
    val miscState by MemoryByteIndexed(Misc_State)
    val miscXPosition by MemoryByteIndexed(Misc_X_Position)
    val miscXSpeed by MemoryByteIndexed(Misc_X_Speed)
    val miscYHighpos by MemoryByteIndexed(Misc_Y_HighPos)
    val miscYPosition by MemoryByteIndexed(Misc_Y_Position)
    val miscYSpeed by MemoryByteIndexed(Misc_Y_Speed)
    //> ProcHammerObj:
    //> lda TimerControl           ;if master timer control set
    //> bne RunHSubs               ;skip all of this code and go to last subs at the end
    temp0 = timerControl
    if (timerControl == 0) {
        //> lda Misc_State,x           ;otherwise get hammer's state
        temp0 = miscState[X]
        //> and #%01111111             ;mask out d7
        temp1 = temp0 and 0x7F
        //> ldy HammerEnemyOffset,x    ;get enemy object offset that spawned this hammer
        //> cmp #$02                   ;check hammer's state
        //> beq SetHSpd                ;if currently at 2, branch
        temp0 = temp1
        temp2 = hammerEnemyOffset[X]
        if (temp1 != 0x02) {
            //> bcs SetHPos                ;if greater than 2, branch elsewhere
            if (!(temp1 >= 0x02)) {
                //> txa
                //> clc                        ;add 13 bytes to use
                //> adc #$0d                   ;proper misc object
                //> tax                        ;return offset to X
                //> lda #$10
                temp0 = 0x10
                //> sta $00                    ;set downward movement force
                memory[0x0] = temp0.toUByte()
                //> lda #$0f
                temp0 = 0x0F
                //> sta $01                    ;set upward movement force (not used)
                memory[0x1] = temp0.toUByte()
                //> lda #$04
                temp0 = 0x04
                //> sta $02                    ;set maximum vertical speed
                memory[0x2] = temp0.toUByte()
                //> lda #$00                   ;set A to impose gravity on hammer
                temp0 = 0x00
                //> jsr ImposeGravity          ;do sub to impose gravity on hammer and move vertically
                imposeGravity(temp0, (X + 0x0D) and 0xFF)
                //> jsr MoveObjectHorizontally ;do sub to move it horizontally
                moveObjectHorizontally((X + 0x0D) and 0xFF)
                //> ldx ObjectOffset           ;get original misc object offset
                //> jmp RunAllH                ;branch to essential subroutines
            }
        } else {
            //> SetHSpd:  lda #$fe
            temp0 = 0xFE
            //> sta Misc_Y_Speed,x         ;set hammer's vertical speed
            miscYSpeed[X] = temp0
            //> lda Enemy_State,y          ;get enemy object state
            temp0 = enemyState[temp2]
            //> and #%11110111             ;mask out d3
            temp3 = temp0 and 0xF7
            //> sta Enemy_State,y          ;store new state
            enemyState[temp2] = temp3
            //> ldx Enemy_MovingDir,y      ;get enemy's moving direction
            //> dex                        ;decrement to use as offset
            temp4 = enemyMovingdir[temp2]
            temp4 = (temp4 - 1) and 0xFF
            //> lda HammerXSpdData,x       ;get proper speed to use based on moving direction
            temp0 = hammerXSpdData[temp4]
            //> ldx ObjectOffset           ;reobtain hammer's buffer offset
            temp4 = objectOffset
            //> sta Misc_X_Speed,x         ;set hammer's horizontal speed
            miscXSpeed[temp4] = temp0
            //> SetHPos:  dec Misc_State,x           ;decrement hammer's state
            miscState[temp4] = (miscState[temp4] - 1) and 0xFF
            //> lda Enemy_X_Position,y     ;get enemy's horizontal position
            temp0 = enemyXPosition[temp2]
            //> clc
            //> adc #$02                   ;set position 2 pixels to the right
            //> sta Misc_X_Position,x      ;store as hammer's horizontal position
            miscXPosition[temp4] = (temp0 + 0x02) and 0xFF
            //> lda Enemy_PageLoc,y        ;get enemy's page location
            temp0 = enemyPageloc[temp2]
            //> adc #$00                   ;add carry
            //> sta Misc_PageLoc,x         ;store as hammer's page location
            miscPageloc[temp4] = (temp0 + (if (temp0 + 0x02 > 0xFF) 1 else 0)) and 0xFF
            //> lda Enemy_Y_Position,y     ;get enemy's vertical position
            temp0 = enemyYPosition[temp2]
            //> sec
            //> sbc #$0a                   ;move position 10 pixels upward
            //> sta Misc_Y_Position,x      ;store as hammer's vertical position
            miscYPosition[temp4] = (temp0 - 0x0A) and 0xFF
            //> lda #$01
            temp0 = 0x01
            //> sta Misc_Y_HighPos,x       ;set hammer's vertical high byte
            miscYHighpos[temp4] = temp0
            //> bne RunHSubs               ;unconditional branch to skip first routine
            if (temp0 == 0) {
            } else {
                //> RunHSubs: jsr GetMiscOffscreenBits   ;get offscreen information
                getMiscOffscreenBits()
                //> jsr RelativeMiscPosition   ;get relative coordinates
                relativeMiscPosition()
                //> jsr GetMiscBoundBox        ;get bounding box coordinates
                getMiscBoundBox(temp4)
                //> jsr DrawHammer             ;draw the hammer
                drawHammer(temp4)
                //> rts                        ;and we are done here
                return
            }
        }
        //> RunAllH:  jsr PlayerHammerCollision  ;handle collisions
        playerHammerCollision(temp4)
    }
}

// Decompiled from SetupJumpCoin
fun setupJumpCoin(X: Int, Y: Int) {
    var temp0: Int = 0
    var coinTallyFor1Ups by MemoryByte(CoinTallyFor1Ups)
    var objectOffset by MemoryByte(ObjectOffset)
    var square2SoundQueue by MemoryByte(Square2SoundQueue)
    val blockPageloc2 by MemoryByteIndexed(Block_PageLoc2)
    val miscPageloc by MemoryByteIndexed(Misc_PageLoc)
    val miscState by MemoryByteIndexed(Misc_State)
    val miscXPosition by MemoryByteIndexed(Misc_X_Position)
    val miscYHighpos by MemoryByteIndexed(Misc_Y_HighPos)
    val miscYPosition by MemoryByteIndexed(Misc_Y_Position)
    val miscYSpeed by MemoryByteIndexed(Misc_Y_Speed)
    //> SetupJumpCoin:
    //> jsr FindEmptyMiscSlot  ;set offset for empty or last misc object buffer slot
    findEmptyMiscSlot()
    //> lda Block_PageLoc2,x   ;get page location saved earlier
    //> sta Misc_PageLoc,y     ;and save as page location for misc object
    miscPageloc[Y] = blockPageloc2[X]
    //> lda $06                ;get low byte of block buffer offset
    //> asl
    //> asl                    ;multiply by 16 to use lower nybble
    //> asl
    //> asl
    //> ora #$05               ;add five pixels
    temp0 = (((((((memory[0x6].toInt() shl 1) and 0xFF) shl 1) and 0xFF) shl 1) and 0xFF) shl 1) and 0xFF or 0x05
    //> sta Misc_X_Position,y  ;save as horizontal coordinate for misc object
    miscXPosition[Y] = temp0
    //> lda $02                ;get vertical high nybble offset from earlier
    //> adc #$20               ;add 32 pixels for the status bar
    //> sta Misc_Y_Position,y  ;store as vertical coordinate
    miscYPosition[Y] = (memory[0x2].toInt() + 0x20 + (if (((((((memory[0x6].toInt() shl 1) and 0xFF) shl 1) and 0xFF) shl 1) and 0xFF and 0x80) != 0) 1 else 0)) and 0xFF
    //> JCoinC: lda #$fb
    //> sta Misc_Y_Speed,y     ;set vertical speed
    miscYSpeed[Y] = 0xFB
    //> lda #$01
    //> sta Misc_Y_HighPos,y   ;set vertical high byte
    miscYHighpos[Y] = 0x01
    //> sta Misc_State,y       ;set state for misc object
    miscState[Y] = 0x01
    //> sta Square2SoundQueue  ;load coin grab sound
    square2SoundQueue = 0x01
    //> stx ObjectOffset       ;store current control bit as misc object offset
    objectOffset = X
    //> jsr GiveOneCoin        ;update coin tally on the screen and coin amount variable
    giveOneCoin()
    //> inc CoinTallyFor1Ups   ;increment coin tally used to activate 1-up block flag
    coinTallyFor1Ups = (coinTallyFor1Ups + 1) and 0xFF
    //> rts
    return
}

// Decompiled from FindEmptyMiscSlot
fun findEmptyMiscSlot() {
    var temp0: Int = 0
    var jumpCoinMiscOffset by MemoryByte(JumpCoinMiscOffset)
    val miscState by MemoryByteIndexed(Misc_State)
    //> FindEmptyMiscSlot:
    //> ldy #$08                ;start at end of misc objects buffer
    temp0 = 0x08
    while (temp0 != 0x05) {
        do {
            //> FMiscLoop: lda Misc_State,y        ;get misc object state
            //> beq UseMiscS            ;branch if none found to use current offset
            //> dey                     ;decrement offset
            temp0 = (temp0 - 1) and 0xFF
            //> cpy #$05                ;do this for three slots
            //> bne FMiscLoop           ;do this until all slots are checked
        } while (temp0 != 0x05)
    }
    //> ldy #$08                ;if no empty slots found, use last slot
    temp0 = 0x08
    //> UseMiscS:  sty JumpCoinMiscOffset  ;store offset of misc object buffer here (residual)
    jumpCoinMiscOffset = temp0
    //> rts
    return
}

// Decompiled from MiscObjectsCore
fun miscObjectsCore() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    var scrollAmount by MemoryByte(ScrollAmount)
    val miscPageloc by MemoryByteIndexed(Misc_PageLoc)
    val miscState by MemoryByteIndexed(Misc_State)
    val miscXPosition by MemoryByteIndexed(Misc_X_Position)
    val miscYSpeed by MemoryByteIndexed(Misc_Y_Speed)
    //> MiscObjectsCore:
    //> ldx #$08          ;set at end of misc object buffer
    //> MiscLoop: stx ObjectOffset  ;store misc object offset here
    objectOffset = 0x08
    //> lda Misc_State,x  ;check misc object state
    //> beq MiscLoopBack  ;branch to check next slot
    temp0 = miscState[0x08]
    temp1 = 0x08
    if (miscState[0x08] != 0) {
        //> asl               ;otherwise shift d7 into carry
        temp0 = (temp0 shl 1) and 0xFF
        //> bcc ProcJumpCoin  ;if d7 not set, jumping coin, thus skip to rest of code here
        if ((temp0 and 0x80) != 0) {
            //> jsr ProcHammerObj ;otherwise go to process hammer,
            procHammerObj(temp1)
            //> jmp MiscLoopBack  ;then check next slot
        } else {
            //> ProcJumpCoin:
            //> ldy Misc_State,x          ;check misc object state
            //> dey                       ;decrement to see if it's set to 1
            temp2 = miscState[temp1]
            temp2 = (temp2 - 1) and 0xFF
            //> beq JCoinRun              ;if so, branch to handle jumping coin
            if (temp2 != 0) {
                //> inc Misc_State,x          ;otherwise increment state to either start off or as timer
                miscState[temp1] = (miscState[temp1] + 1) and 0xFF
                //> lda Misc_X_Position,x     ;get horizontal coordinate for misc object
                temp0 = miscXPosition[temp1]
                //> clc                       ;whether its jumping coin (state 0 only) or floatey number
                //> adc ScrollAmount          ;add current scroll speed
                //> sta Misc_X_Position,x     ;store as new horizontal coordinate
                miscXPosition[temp1] = (temp0 + scrollAmount) and 0xFF
                //> lda Misc_PageLoc,x        ;get page location
                temp0 = miscPageloc[temp1]
                //> adc #$00                  ;add carry
                //> sta Misc_PageLoc,x        ;store as new page location
                miscPageloc[temp1] = (temp0 + (if (temp0 + scrollAmount > 0xFF) 1 else 0)) and 0xFF
                //> lda Misc_State,x
                temp0 = miscState[temp1]
                //> cmp #$30                  ;check state of object for preset value
                //> bne RunJCSubs             ;if not yet reached, branch to subroutines
                if (temp0 == 0x30) {
                    //> lda #$00
                    temp0 = 0x00
                    //> sta Misc_State,x          ;otherwise nullify object state
                    miscState[temp1] = temp0
                    //> jmp MiscLoopBack          ;and move onto next slot
                }
            } else {
                //> JCoinRun:  txa
                //> clc                       ;add 13 bytes to offset for next subroutine
                //> adc #$0d
                //> tax
                //> lda #$50                  ;set downward movement amount
                temp0 = 0x50
                //> sta $00
                memory[0x0] = temp0.toUByte()
                //> lda #$06                  ;set maximum vertical speed
                temp0 = 0x06
                //> sta $02
                memory[0x2] = temp0.toUByte()
                //> lsr                       ;divide by 2 and set
                temp0 = temp0 shr 1
                //> sta $01                   ;as upward movement amount (apparently residual)
                memory[0x1] = temp0.toUByte()
                //> lda #$00                  ;set A to impose gravity on jumping coin
                temp0 = 0x00
                //> jsr ImposeGravity         ;do sub to move coin vertically and impose gravity on it
                imposeGravity(temp0, (temp1 + 0x0D) and 0xFF)
                //> ldx ObjectOffset          ;get original misc object offset
                temp1 = objectOffset
                //> lda Misc_Y_Speed,x        ;check vertical speed
                temp0 = miscYSpeed[temp1]
                //> cmp #$05
                //> bne RunJCSubs             ;if not moving downward fast enough, keep state as-is
                if (temp0 == 0x05) {
                    //> inc Misc_State,x          ;otherwise increment state to change to floatey number
                    miscState[temp1] = (miscState[temp1] + 1) and 0xFF
                }
                //> RunJCSubs: jsr RelativeMiscPosition  ;get relative coordinates
                relativeMiscPosition()
                //> jsr GetMiscOffscreenBits  ;get offscreen information
                getMiscOffscreenBits()
                //> jsr GetMiscBoundBox       ;get bounding box coordinates (why?)
                getMiscBoundBox(temp1)
                //> jsr JCoinGfxHandler       ;draw the coin or floatey number
                jCoinGfxHandler(temp1)
            }
        }
    }
    do {
        //> MiscLoopBack:
        //> dex                       ;decrement misc object offset
        temp1 = (temp1 - 1) and 0xFF
        //> bpl MiscLoop              ;loop back until all misc objects handled
    } while ((temp1 and 0x80) == 0)
    //> rts                       ;then leave
    return
}

// Decompiled from GiveOneCoin
fun giveOneCoin() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var coinTally by MemoryByte(CoinTally)
    var currentPlayer by MemoryByte(CurrentPlayer)
    var numberofLives by MemoryByte(NumberofLives)
    var square2SoundQueue by MemoryByte(Square2SoundQueue)
    val coinTallyOffsets by MemoryByteIndexed(CoinTallyOffsets)
    val digitModifier by MemoryByteIndexed(DigitModifier)
    //> GiveOneCoin:
    //> lda #$01               ;set digit modifier to add 1 coin
    //> sta DigitModifier+5    ;to the current player's coin tally
    digitModifier[5] = 0x01
    //> ldx CurrentPlayer      ;get current player on the screen
    //> ldy CoinTallyOffsets,x ;get offset for player's coin tally
    //> jsr DigitsMathRoutine  ;update the coin tally
    digitsMathRoutine(coinTallyOffsets[currentPlayer])
    //> inc CoinTally          ;increment onscreen player's coin amount
    coinTally = (coinTally + 1) and 0xFF
    //> lda CoinTally
    //> cmp #100               ;does player have 100 coins yet?
    //> bne CoinPoints         ;if not, skip all of this
    temp0 = coinTally
    temp1 = currentPlayer
    temp2 = coinTallyOffsets[currentPlayer]
    if (coinTally == 0x64) {
        //> lda #$00
        temp0 = 0x00
        //> sta CoinTally          ;otherwise, reinitialize coin amount
        coinTally = temp0
        //> inc NumberofLives      ;give the player an extra life
        numberofLives = (numberofLives + 1) and 0xFF
        //> lda #Sfx_ExtraLife
        temp0 = Sfx_ExtraLife
        //> sta Square2SoundQueue  ;play 1-up sound
        square2SoundQueue = temp0
    }
    //> CoinPoints:
    //> lda #$02               ;set digit modifier to award
    temp0 = 0x02
    //> sta DigitModifier+4    ;200 points to the player
    digitModifier[4] = temp0
}

// Decompiled from AddToScore
fun addToScore() {
    var currentPlayer by MemoryByte(CurrentPlayer)
    val scoreOffsets by MemoryByteIndexed(ScoreOffsets)
    //> AddToScore:
    //> ldx CurrentPlayer      ;get current player
    //> ldy ScoreOffsets,x     ;get offset for player's score
    //> jsr DigitsMathRoutine  ;update the score internally with value in digit modifier
    digitsMathRoutine(scoreOffsets[currentPlayer])
}

// Decompiled from GetSBNybbles
fun getSBNybbles() {
    var currentPlayer by MemoryByte(CurrentPlayer)
    val statusBarNybbles by MemoryByteIndexed(StatusBarNybbles)
    //> GetSBNybbles:
    //> ldy CurrentPlayer      ;get current player
    //> lda StatusBarNybbles,y ;get nybbles based on player, use to update score and coins
}

// Decompiled from UpdateNumber
fun updateNumber() {
    var A: Int = 0
    var temp0: Int = 0
    var temp1: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    var vramBuffer1Offset by MemoryByte(VRAM_Buffer1_Offset)
    val vramBuffer1 by MemoryByteIndexed(VRAM_Buffer1)
    //> UpdateNumber:
    //> jsr PrintStatusBarNumbers ;print status bar numbers based on nybbles, whatever they be
    printStatusBarNumbers(A)
    //> ldy VRAM_Buffer1_Offset
    //> lda VRAM_Buffer1-6,y      ;check highest digit of score
    //> bne NoZSup                ;if zero, overwrite with space tile for zero suppression
    temp0 = vramBuffer1[-6 + vramBuffer1Offset]
    temp1 = vramBuffer1Offset
    if (vramBuffer1[-6 + vramBuffer1Offset] == 0) {
        //> lda #$24
        temp0 = 0x24
        //> sta VRAM_Buffer1-6,y
        vramBuffer1[-6 + temp1] = temp0
    }
    //> NoZSup: ldx ObjectOffset          ;get enemy object buffer offset
    //> rts
    return
}

// Decompiled from PlayerHeadCollision
fun playerHeadCollision(A: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var blockBounceTimer by MemoryByte(BlockBounceTimer)
    var brickCoinTimer by MemoryByte(BrickCoinTimer)
    var brickCoinTimerFlag by MemoryByte(BrickCoinTimerFlag)
    var crouchingFlag by MemoryByte(CrouchingFlag)
    var playerSize by MemoryByte(PlayerSize)
    var playerYPosition by MemoryByte(Player_Y_Position)
    var sprdataoffsetCtrl by MemoryByte(SprDataOffset_Ctrl)
    val blockYPosAdderData by MemoryByteIndexed(BlockYPosAdderData)
    val blockBbufLow by MemoryByteIndexed(Block_BBuf_Low)
    val blockMetatile by MemoryByteIndexed(Block_Metatile)
    val blockOrigYpos by MemoryByteIndexed(Block_Orig_YPos)
    val blockState by MemoryByteIndexed(Block_State)
    val blockYPosition by MemoryByteIndexed(Block_Y_Position)
    //> PlayerHeadCollision:
    //> pha                      ;store metatile number to stack
    push(A)
    //> lda #$11                 ;load unbreakable block object state by default
    //> ldx SprDataOffset_Ctrl   ;load offset control bit here
    //> ldy PlayerSize           ;check player's size
    //> bne DBlockSte            ;if small, branch
    temp0 = 0x11
    temp1 = sprdataoffsetCtrl
    temp2 = playerSize
    if (playerSize == 0) {
        //> lda #$12                 ;otherwise load breakable block object state
        temp0 = 0x12
    }
    //> DBlockSte: sta Block_State,x        ;store into block object buffer
    blockState[temp1] = temp0
    //> jsr DestroyBlockMetatile ;store blank metatile in vram buffer to write to name table
    destroyBlockMetatile()
    //> ldx SprDataOffset_Ctrl   ;load offset control bit
    temp1 = sprdataoffsetCtrl
    //> lda $02                  ;get vertical high nybble offset used in block buffer routine
    temp0 = memory[0x2].toInt()
    //> sta Block_Orig_YPos,x    ;set as vertical coordinate for block object
    blockOrigYpos[temp1] = temp0
    //> tay
    //> lda $06                  ;get low byte of block buffer address used in same routine
    temp0 = memory[0x6].toInt()
    //> sta Block_BBuf_Low,x     ;save as offset here to be used later
    blockBbufLow[temp1] = temp0
    //> lda ($06),y              ;get contents of block buffer at old address at $06, $07
    temp0 = memory[readWord(0x6) + temp0].toInt()
    //> jsr BlockBumpedChk       ;do a sub to check which block player bumped head on
    blockBumpedChk(temp0)
    //> sta $00                  ;store metatile here
    memory[0x0] = temp0.toUByte()
    //> ldy PlayerSize           ;check player's size
    temp2 = playerSize
    //> bne ChkBrick             ;if small, use metatile itself as contents of A
    if (temp2 == 0) {
        //> tya                      ;otherwise init A (note: big = 0)
    }
    //> ChkBrick:  bcc PutMTileB            ;if no match was found in previous sub, skip ahead
    if (flagC) {
        //> ldy #$11                 ;otherwise load unbreakable state into block object buffer
        temp2 = 0x11
        //> sty Block_State,x        ;note this applies to both player sizes
        blockState[temp1] = temp2
        //> lda #$c4                 ;load empty block metatile into A for now
        temp0 = 0xC4
        //> ldy $00                  ;get metatile from before
        temp2 = memory[0x0].toInt()
        //> cpy #$58                 ;is it brick with coins (with line)?
        //> beq StartBTmr            ;if so, branch
        if (temp2 != 0x58) {
            //> cpy #$5d                 ;is it brick with coins (without line)?
            //> bne PutMTileB            ;if not, branch ahead to store empty block metatile
            if (temp2 == 0x5D) {
            }
        }
        //> StartBTmr: lda BrickCoinTimerFlag   ;check brick coin timer flag
        temp0 = brickCoinTimerFlag
        //> bne ContBTmr             ;if set, timer expired or counting down, thus branch
        if (temp0 == 0) {
            //> lda #$0b
            temp0 = 0x0B
            //> sta BrickCoinTimer       ;if not set, set brick coin timer
            brickCoinTimer = temp0
            //> inc BrickCoinTimerFlag   ;and set flag linked to it
            brickCoinTimerFlag = (brickCoinTimerFlag + 1) and 0xFF
        }
        //> ContBTmr:  lda BrickCoinTimer       ;check brick coin timer
        temp0 = brickCoinTimer
        //> bne PutOldMT             ;if not yet expired, branch to use current metatile
        if (temp0 == 0) {
            //> ldy #$c4                 ;otherwise use empty block metatile
            temp2 = 0xC4
        }
        //> PutOldMT:  tya                      ;put metatile into A
    }
    //> PutMTileB: sta Block_Metatile,x     ;store whatever metatile be appropriate here
    blockMetatile[temp1] = temp0
    //> jsr InitBlock_XY_Pos     ;get block object horizontal coordinates saved
    initblockXyPos(temp1)
    //> ldy $02                  ;get vertical high nybble offset
    temp2 = memory[0x2].toInt()
    //> lda #$23
    temp0 = 0x23
    //> sta ($06),y              ;write blank metatile $23 to block buffer
    memory[readWord(0x6) + temp2] = temp0.toUByte()
    //> lda #$10
    temp0 = 0x10
    //> sta BlockBounceTimer     ;set block bounce timer
    blockBounceTimer = temp0
    //> pla                      ;pull original metatile from stack
    temp0 = pull()
    //> sta $05                  ;and save here
    memory[0x5] = temp0.toUByte()
    //> ldy #$00                 ;set default offset
    temp2 = 0x00
    //> lda CrouchingFlag        ;is player crouching?
    temp0 = crouchingFlag
    //> bne SmallBP              ;if so, branch to increment offset
    if (temp0 == 0) {
        //> lda PlayerSize           ;is player big?
        temp0 = playerSize
        //> beq BigBP                ;if so, branch to use default offset
        if (temp0 != 0) {
        }
    }
    //> SmallBP:   iny                      ;increment for small or big and crouching
    temp2 = (temp2 + 1) and 0xFF
    //> BigBP:     lda Player_Y_Position    ;get player's vertical coordinate
    temp0 = playerYPosition
    //> clc
    //> adc BlockYPosAdderData,y ;add value determined by size
    //> and #$f0                 ;mask out low nybble to get 16-pixel correspondence
    temp3 = (temp0 + blockYPosAdderData[temp2]) and 0xFF and 0xF0
    //> sta Block_Y_Position,x   ;save as vertical coordinate for block object
    blockYPosition[temp1] = temp3
    //> ldy Block_State,x        ;get block object state
    temp2 = blockState[temp1]
    //> cpy #$11
    //> beq Unbreak              ;if set to value loaded for unbreakable, branch
    temp0 = temp3
    if (temp2 != 0x11) {
        //> jsr BrickShatter         ;execute code for breakable brick
        brickShatter(temp1)
        //> jmp InvOBit              ;skip subroutine to do last part of code here
    } else {
        //> Unbreak:   jsr BumpBlock            ;execute code for unbreakable brick or question block
        bumpBlock(temp1, temp2)
    }
    //> InvOBit:   lda SprDataOffset_Ctrl   ;invert control bit used by block objects
    temp0 = sprdataoffsetCtrl
    //> eor #$01                 ;and floatey numbers
    temp4 = temp0 xor 0x01
    //> sta SprDataOffset_Ctrl
    sprdataoffsetCtrl = temp4
    //> rts                      ;leave!
    return
}

// Decompiled from InitBlock_XY_Pos
fun initblockXyPos(X: Int) {
    var temp0: Int = 0
    var playerPageloc by MemoryByte(Player_PageLoc)
    var playerXPosition by MemoryByte(Player_X_Position)
    var playerYHighpos by MemoryByte(Player_Y_HighPos)
    val blockPageloc by MemoryByteIndexed(Block_PageLoc)
    val blockPageloc2 by MemoryByteIndexed(Block_PageLoc2)
    val blockXPosition by MemoryByteIndexed(Block_X_Position)
    val blockYHighpos by MemoryByteIndexed(Block_Y_HighPos)
    //> InitBlock_XY_Pos:
    //> lda Player_X_Position   ;get player's horizontal coordinate
    //> clc
    //> adc #$08                ;add eight pixels
    //> and #$f0                ;mask out low nybble to give 16-pixel correspondence
    temp0 = (playerXPosition + 0x08) and 0xFF and 0xF0
    //> sta Block_X_Position,x  ;save as horizontal coordinate for block object
    blockXPosition[X] = temp0
    //> lda Player_PageLoc
    //> adc #$00                ;add carry to page location of player
    //> sta Block_PageLoc,x     ;save as page location of block object
    blockPageloc[X] = (playerPageloc + (if (playerXPosition + 0x08 > 0xFF) 1 else 0)) and 0xFF
    //> sta Block_PageLoc2,x    ;save elsewhere to be used later
    blockPageloc2[X] = (playerPageloc + (if (playerXPosition + 0x08 > 0xFF) 1 else 0)) and 0xFF
    //> lda Player_Y_HighPos
    //> sta Block_Y_HighPos,x   ;save vertical high byte of player into
    blockYHighpos[X] = playerYHighpos
    //> rts                     ;vertical high byte of block object and leave
    return
}

// Decompiled from BumpBlock
fun bumpBlock(X: Int, Y: Int) {
    var temp0: Int = 0
    var playerYSpeed by MemoryByte(Player_Y_Speed)
    var square1SoundQueue by MemoryByte(Square1SoundQueue)
    val blockXSpeed by MemoryByteIndexed(Block_X_Speed)
    val blockYMoveforce by MemoryByteIndexed(Block_Y_MoveForce)
    val blockYSpeed by MemoryByteIndexed(Block_Y_Speed)
    //> BumpBlock:
    //> jsr CheckTopOfBlock     ;check to see if there's a coin directly above this block
    checkTopOfBlock()
    //> lda #Sfx_Bump
    //> sta Square1SoundQueue   ;play bump sound
    square1SoundQueue = Sfx_Bump
    //> lda #$00
    //> sta Block_X_Speed,x     ;initialize horizontal speed for block object
    blockXSpeed[X] = 0x00
    //> sta Block_Y_MoveForce,x ;init fractional movement force
    blockYMoveforce[X] = 0x00
    //> sta Player_Y_Speed      ;init player's vertical speed
    playerYSpeed = 0x00
    //> lda #$fe
    //> sta Block_Y_Speed,x     ;set vertical speed for block object
    blockYSpeed[X] = 0xFE
    //> lda $05                 ;get original metatile from stack
    //> jsr BlockBumpedChk      ;do a sub to check which block player bumped head on
    blockBumpedChk(memory[0x5].toInt())
    //> bcc ExitBlockChk        ;if no match was found, branch to leave
    temp0 = memory[0x5].toInt()
    if (flagC) {
        //> tya                     ;move block number to A
        //> cmp #$09                ;if block number was within 0-8 range,
        //> bcc BlockCode           ;branch to use current number
        temp0 = Y
        if (Y >= 0x09) {
            //> sbc #$05                ;otherwise subtract 5 for second set to get proper number
        }
        //> BlockCode: jsr JumpEngine          ;run appropriate subroutine depending on block number
        when (temp0) {
            0 -> {
                mushFlowerBlock()
            }
            1 -> {
                coinBlock()
            }
            2 -> {
                coinBlock()
            }
            3 -> {
                extraLifeMushBlock()
            }
            4 -> {
                mushFlowerBlock()
            }
            5 -> {
                vineBlock()
            }
            6 -> {
                starBlock()
            }
            7 -> {
                coinBlock()
            }
            8 -> {
                extraLifeMushBlock()
            }
            else -> {
                // Unknown JumpEngine index
            }
        }
        return
    } else {
        //> ExitBlockChk:
        //> rts                     ;leave
        return
    }
}

// Decompiled from BlockBumpedChk
fun blockBumpedChk(A: Int) {
    var temp0: Int = 0
    val brickQBlockMetatiles by MemoryByteIndexed(BrickQBlockMetatiles)
    //> BlockBumpedChk:
    //> ldy #$0d                    ;start at end of metatile data
    temp0 = 0x0D
    while ((temp0 and 0x80) == 0) {
        do {
            //> BumpChkLoop: cmp BrickQBlockMetatiles,y  ;check to see if current metatile matches
            //> beq MatchBump               ;metatile found in block buffer, branch if so
            //> dey                         ;otherwise move onto next metatile
            temp0 = (temp0 - 1) and 0xFF
            //> bpl BumpChkLoop             ;do this until all metatiles are checked
        } while ((temp0 and 0x80) == 0)
    }
    //> clc                         ;if none match, return with carry clear
    //> MatchBump:   rts                         ;note carry is set if found match
    return
}

// Decompiled from BrickShatter
fun brickShatter(X: Int) {
    var noiseSoundQueue by MemoryByte(NoiseSoundQueue)
    var playerYSpeed by MemoryByte(Player_Y_Speed)
    var sprdataoffsetCtrl by MemoryByte(SprDataOffset_Ctrl)
    val blockRepflag by MemoryByteIndexed(Block_RepFlag)
    val digitModifier by MemoryByteIndexed(DigitModifier)
    //> BrickShatter:
    //> jsr CheckTopOfBlock    ;check to see if there's a coin directly above this block
    checkTopOfBlock()
    //> lda #Sfx_BrickShatter
    //> sta Block_RepFlag,x    ;set flag for block object to immediately replace metatile
    blockRepflag[X] = Sfx_BrickShatter
    //> sta NoiseSoundQueue    ;load brick shatter sound
    noiseSoundQueue = Sfx_BrickShatter
    //> jsr SpawnBrickChunks   ;create brick chunk objects
    spawnBrickChunks(X)
    //> lda #$fe
    //> sta Player_Y_Speed     ;set vertical speed for player
    playerYSpeed = 0xFE
    //> lda #$05
    //> sta DigitModifier+5    ;set digit modifier to give player 50 points
    digitModifier[5] = 0x05
    //> jsr AddToScore         ;do sub to update the score
    addToScore()
    //> ldx SprDataOffset_Ctrl ;load control bit and leave
    //> rts
    return
}

// Decompiled from CheckTopOfBlock
fun checkTopOfBlock() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var sprdataoffsetCtrl by MemoryByte(SprDataOffset_Ctrl)
    //> CheckTopOfBlock:
    //> ldx SprDataOffset_Ctrl  ;load control bit
    //> ldy $02                 ;get vertical high nybble offset used in block buffer
    //> beq TopEx               ;branch to leave if set to zero, because we're at the top
    temp0 = sprdataoffsetCtrl
    temp1 = memory[0x2].toInt()
    if (memory[0x2].toInt() != 0) {
        //> tya                     ;otherwise set to A
        //> sec
        //> sbc #$10                ;subtract $10 to move up one row in the block buffer
        //> sta $02                 ;store as new vertical high nybble offset
        memory[0x2] = ((temp1 - 0x10) and 0xFF).toUByte()
        //> tay
        //> lda ($06),y             ;get contents of block buffer in same column, one row up
        //> cmp #$c2                ;is it a coin? (not underwater)
        //> bne TopEx               ;if not, branch to leave
        temp2 = memory[readWord(0x6) + ((temp1 - 0x10) and 0xFF)].toInt()
        temp1 = (temp1 - 0x10) and 0xFF
        if (memory[readWord(0x6) + ((temp1 - 0x10) and 0xFF)].toInt() == 0xC2) {
            //> lda #$00
            temp2 = 0x00
            //> sta ($06),y             ;otherwise put blank metatile where coin was
            memory[readWord(0x6) + temp1] = temp2.toUByte()
            //> jsr RemoveCoin_Axe      ;write blank metatile to vram buffer
            removecoinAxe()
            //> ldx SprDataOffset_Ctrl  ;get control bit
            temp0 = sprdataoffsetCtrl
            //> jsr SetupJumpCoin       ;create jumping coin object and update coin variables
            setupJumpCoin(temp0, temp1)
        }
    }
    //> TopEx: rts                     ;leave!
    return
}

// Decompiled from SpawnBrickChunks
fun spawnBrickChunks(X: Int) {
    val blockOrigXpos by MemoryByteIndexed(Block_Orig_XPos)
    val blockPageloc by MemoryByteIndexed(Block_PageLoc)
    val blockXPosition by MemoryByteIndexed(Block_X_Position)
    val blockXSpeed by MemoryByteIndexed(Block_X_Speed)
    val blockYMoveforce by MemoryByteIndexed(Block_Y_MoveForce)
    val blockYPosition by MemoryByteIndexed(Block_Y_Position)
    val blockYSpeed by MemoryByteIndexed(Block_Y_Speed)
    //> SpawnBrickChunks:
    //> lda Block_X_Position,x     ;set horizontal coordinate of block object
    //> sta Block_Orig_XPos,x      ;as original horizontal coordinate here
    blockOrigXpos[X] = blockXPosition[X]
    //> lda #$f0
    //> sta Block_X_Speed,x        ;set horizontal speed for brick chunk objects
    blockXSpeed[X] = 0xF0
    //> sta Block_X_Speed+2,x
    blockXSpeed[2 + X] = 0xF0
    //> lda #$fa
    //> sta Block_Y_Speed,x        ;set vertical speed for one
    blockYSpeed[X] = 0xFA
    //> lda #$fc
    //> sta Block_Y_Speed+2,x      ;set lower vertical speed for the other
    blockYSpeed[2 + X] = 0xFC
    //> lda #$00
    //> sta Block_Y_MoveForce,x    ;init fractional movement force for both
    blockYMoveforce[X] = 0x00
    //> sta Block_Y_MoveForce+2,x
    blockYMoveforce[2 + X] = 0x00
    //> lda Block_PageLoc,x
    //> sta Block_PageLoc+2,x      ;copy page location
    blockPageloc[2 + X] = blockPageloc[X]
    //> lda Block_X_Position,x
    //> sta Block_X_Position+2,x   ;copy horizontal coordinate
    blockXPosition[2 + X] = blockXPosition[X]
    //> lda Block_Y_Position,x
    //> clc                        ;add 8 pixels to vertical coordinate
    //> adc #$08                   ;and save as vertical coordinate for one of them
    //> sta Block_Y_Position+2,x
    blockYPosition[2 + X] = (blockYPosition[X] + 0x08) and 0xFF
    //> lda #$fa
    //> sta Block_Y_Speed,x        ;set vertical speed...again??? (redundant)
    blockYSpeed[X] = 0xFA
    //> rts
    return
}

// Decompiled from BlockObjectsCore
fun blockObjectsCore(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    val blockRepflag by MemoryByteIndexed(Block_RepFlag)
    val blockState by MemoryByteIndexed(Block_State)
    val blockYHighpos by MemoryByteIndexed(Block_Y_HighPos)
    val blockYPosition by MemoryByteIndexed(Block_Y_Position)
    //> BlockObjectsCore:
    //> lda Block_State,x           ;get state of block object
    //> beq UpdSte                  ;if not set, branch to leave
    temp0 = blockState[X]
    if (blockState[X] != 0) {
        //> and #$0f                    ;mask out high nybble
        temp1 = temp0 and 0x0F
        //> pha                         ;push to stack
        push(temp1)
        //> tay                         ;put in Y for now
        //> txa
        //> clc
        //> adc #$09                    ;add 9 bytes to offset (note two block objects are created
        //> tax                         ;when using brick chunks, but only one offset for both)
        //> dey                         ;decrement Y to check for solid block state
        temp1 = (temp1 - 1) and 0xFF
        //> beq BouncingBlockHandler    ;branch if found, otherwise continue for brick chunks
        temp0 = (X + 0x09) and 0xFF
        temp2 = (X + 0x09) and 0xFF
        temp3 = temp1
        if (temp1 != 0) {
            //> jsr ImposeGravityBlock      ;do sub to impose gravity on one block object object
            imposeGravityBlock()
            //> jsr MoveObjectHorizontally  ;do another sub to move horizontally
            moveObjectHorizontally(temp2)
            //> txa
            //> clc                         ;move onto next block object
            //> adc #$02
            //> tax
            //> jsr ImposeGravityBlock      ;do sub to impose gravity on other block object
            imposeGravityBlock()
            //> jsr MoveObjectHorizontally  ;do another sub to move horizontally
            moveObjectHorizontally((temp2 + 0x02) and 0xFF)
            //> ldx ObjectOffset            ;get block object offset used for both
            temp2 = objectOffset
            //> jsr RelativeBlockPosition   ;get relative coordinates
            relativeBlockPosition(temp2)
            //> jsr GetBlockOffscreenBits   ;get offscreen information
            getBlockOffscreenBits(temp2)
            //> jsr DrawBrickChunks         ;draw the brick chunks
            drawBrickChunks(temp2)
            //> pla                         ;get lower nybble of saved state
            temp0 = pull()
            //> ldy Block_Y_HighPos,x       ;check vertical high byte of block object
            temp3 = blockYHighpos[temp2]
            //> beq UpdSte                  ;if above the screen, branch to kill it
            if (temp3 != 0) {
                //> pha                         ;otherwise save state back into stack
                push(temp0)
                //> lda #$f0
                temp0 = 0xF0
                //> cmp Block_Y_Position+2,x    ;check to see if bottom block object went
                //> bcs ChkTop                  ;to the bottom of the screen, and branch if not
                if (!(temp0 >= blockYPosition[2 + temp2])) {
                    //> sta Block_Y_Position+2,x    ;otherwise set offscreen coordinate
                    blockYPosition[2 + temp2] = temp0
                }
                //> ChkTop: lda Block_Y_Position,x      ;get top block object's vertical coordinate
                temp0 = blockYPosition[temp2]
                //> cmp #$f0                    ;see if it went to the bottom of the screen
                //> pla                         ;pull block object state from stack
                temp0 = pull()
                //> bcc UpdSte                  ;if not, branch to save state
                if (temp0 >= 0xF0) {
                    //> bcs KillBlock               ;otherwise do unconditional branch to kill it
                    if (!(temp0 >= 0xF0)) {
                    }
                } else {
                    //> UpdSte:    sta Block_State,x          ;store contents of A in block object state
                    blockState[temp2] = temp0
                    //> rts
                    return
                }
            }
        }
        //> BouncingBlockHandler:
        //> jsr ImposeGravityBlock     ;do sub to impose gravity on block object
        imposeGravityBlock()
        //> ldx ObjectOffset           ;get block object offset
        temp2 = objectOffset
        //> jsr RelativeBlockPosition  ;get relative coordinates
        relativeBlockPosition(temp2)
        //> jsr GetBlockOffscreenBits  ;get offscreen information
        getBlockOffscreenBits(temp2)
        //> jsr DrawBlock              ;draw the block
        drawBlock(temp2)
        //> lda Block_Y_Position,x     ;get vertical coordinate
        temp0 = blockYPosition[temp2]
        //> and #$0f                   ;mask out high nybble
        temp4 = temp0 and 0x0F
        //> cmp #$05                   ;check to see if low nybble wrapped around
        //> pla                        ;pull state from stack
        temp0 = pull()
        //> bcs UpdSte                 ;if still above amount, not time to kill block yet, thus branch
        if (!(temp4 >= 0x05)) {
            //> lda #$01
            temp0 = 0x01
            //> sta Block_RepFlag,x        ;otherwise set flag to replace metatile
            blockRepflag[temp2] = temp0
            //> KillBlock: lda #$00                   ;if branched here, nullify object state
            temp0 = 0x00
        }
    }
}

// Decompiled from BlockObjMT_Updater
fun blockobjmtUpdater() {
    var temp0: Int = 0
    var temp1: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    var vramBuffer1 by MemoryByte(VRAM_Buffer1)
    val blockBbufLow by MemoryByteIndexed(Block_BBuf_Low)
    val blockMetatile by MemoryByteIndexed(Block_Metatile)
    val blockOrigYpos by MemoryByteIndexed(Block_Orig_YPos)
    val blockRepflag by MemoryByteIndexed(Block_RepFlag)
    //> BlockObjMT_Updater:
    //> ldx #$01                  ;set offset to start with second block object
    temp0 = 0x01
    while ((temp0 and 0x80) == 0) {
        //> lda Block_RepFlag,x       ;if flag for block object already clear,
        //> beq NextBUpd              ;branch to move onto next block object
        temp1 = blockRepflag[temp0]
        if (blockRepflag[temp0] != 0) {
            //> lda Block_BBuf_Low,x      ;get low byte of block buffer
            temp1 = blockBbufLow[temp0]
            //> sta $06                   ;store into block buffer address
            memory[0x6] = temp1.toUByte()
            //> lda #$05
            temp1 = 0x05
            //> sta $07                   ;set high byte of block buffer address
            memory[0x7] = temp1.toUByte()
            //> lda Block_Orig_YPos,x     ;get original vertical coordinate of block object
            temp1 = blockOrigYpos[temp0]
            //> sta $02                   ;store here and use as offset to block buffer
            memory[0x2] = temp1.toUByte()
            //> tay
            //> lda Block_Metatile,x      ;get metatile to be written
            temp1 = blockMetatile[temp0]
            //> sta ($06),y               ;write it to the block buffer
            memory[readWord(0x6) + temp1] = temp1.toUByte()
            //> jsr ReplaceBlockMetatile  ;do sub to replace metatile where block object is
            replaceBlockMetatile(temp0)
            //> lda #$00
            temp1 = 0x00
            //> sta Block_RepFlag,x       ;clear block object flag
            blockRepflag[temp0] = temp1
        }
        do {
            //> UpdateLoop: stx ObjectOffset          ;set offset here
            objectOffset = temp0
            //> lda VRAM_Buffer1          ;if vram buffer already being used here,
            temp1 = vramBuffer1
            //> bne NextBUpd              ;branch to move onto next block object
            //> NextBUpd:   dex                       ;decrement block object offset
            temp0 = (temp0 - 1) and 0xFF
            //> bpl UpdateLoop            ;do this until both block objects are dealt with
        } while ((temp0 and 0x80) == 0)
    }
    //> rts                       ;then leave
    return
}

// Decompiled from MoveEnemyHorizontally
fun moveEnemyHorizontally(X: Int) {
    var X: Int = X
    var objectOffset by MemoryByte(ObjectOffset)
    //> MoveEnemyHorizontally:
    //> inx                         ;increment offset for enemy offset
    X = (X + 1) and 0xFF
    //> jsr MoveObjectHorizontally  ;position object horizontally according to
    moveObjectHorizontally(X)
    //> ldx ObjectOffset            ;counters, return with saved value in A,
    //> rts                         ;put enemy offset back in X and leave
    return
}

// Decompiled from MovePlayerHorizontally
// Fixed: This function falls through to MoveObjectHorizontally
fun movePlayerHorizontally(): Int {
    var jumpspringAnimCtrl by MemoryByte(JumpspringAnimCtrl)
    //> MovePlayerHorizontally:
    //> lda JumpspringAnimCtrl  ;if jumpspring currently animating,
    //> bne ExXMove             ;branch to leave
    if (jumpspringAnimCtrl != 0) {
        // A register still has value from lda JumpspringAnimCtrl
        return jumpspringAnimCtrl
    }
    //> tax                     ;otherwise set zero for offset to use player's stuff
    // Fall through to MoveObjectHorizontally with X=0
    return moveObjectHorizontally(0x00)
}

// Decompiled from MoveObjectHorizontally
// Fixed: Returns the horizontal speed value for scroll calculations
fun moveObjectHorizontally(X: Int): Int {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    val sprobjectPageloc by MemoryByteIndexed(SprObject_PageLoc)
    val sprobjectXMoveforce by MemoryByteIndexed(SprObject_X_MoveForce)
    val sprobjectXPosition by MemoryByteIndexed(SprObject_X_Position)
    val sprobjectXSpeed by MemoryByteIndexed(SprObject_X_Speed)
    //> MoveObjectHorizontally:
    //> lda SprObject_X_Speed,x     ;get currently saved value (horizontal
    //> asl                         ;speed, secondary counter, whatever)
    //> asl                         ;and move low nybble to high
    //> asl
    //> asl
    //> sta $01                     ;store result here
    val speedLowToHigh = (sprobjectXSpeed[X] shl 4) and 0xFF
    memory[0x1] = speedLowToHigh.toUByte()
    //> lda SprObject_X_Speed,x     ;get saved value again
    //> lsr                         ;move high nybble to low
    //> lsr
    //> lsr
    //> lsr
    //> cmp #$08                    ;if < 8, branch, do not change
    //> bcc SaveXSpd
    temp0 = (sprobjectXSpeed[X] shr 4) and 0x0F
    if (temp0 >= 0x08) {
        //> ora #%11110000              ;otherwise alter high nybble
        temp0 = temp0 or 0xF0
    }
    //> SaveXSpd: sta $00                     ;save result here
    memory[0x0] = temp0.toUByte()
    //> ldy #$00                    ;load default Y value here
    //> cmp #$00                    ;if result positive, leave Y alone
    //> bpl UseAdder
    temp2 = 0x00
    if ((temp0 and 0x80) != 0) {
        //> dey                         ;otherwise decrement Y
        temp2 = 0xFF
    }
    //> UseAdder: sty $02                     ;save Y here
    memory[0x2] = temp2.toUByte()
    //> lda SprObject_X_MoveForce,x ;get whatever number's here
    val oldForce = sprobjectXMoveforce[X]
    //> clc
    //> adc $01                     ;add low nybble moved to high
    //> sta SprObject_X_MoveForce,x ;store result here
    val newForce = (oldForce + speedLowToHigh) and 0xFF
    val carry1 = if (oldForce + speedLowToHigh > 0xFF) 1 else 0
    sprobjectXMoveforce[X] = newForce
    //> lda #$00                    ;init A
    //> rol                         ;rotate carry into d0
    val savedCarry = carry1
    //> pha                         ;push onto stack
    //> ror                         ;rotate d0 back onto carry
    // carry remains as savedCarry
    //> lda SprObject_X_Position,x
    val oldXPos = sprobjectXPosition[X]
    //> adc $00                     ;add carry plus saved value (high nybble moved to low
    //> sta SprObject_X_Position,x  ;plus $f0 if necessary) to object's horizontal position
    val newXPos = (oldXPos + memory[0x0].toInt() + savedCarry) and 0xFF
    val carry2 = if (oldXPos + memory[0x0].toInt() + savedCarry > 0xFF) 1 else 0
    sprobjectXPosition[X] = newXPos
    //> lda SprObject_PageLoc,x
    val oldPageLoc = sprobjectPageloc[X]
    //> adc $02                     ;add carry plus other saved value to the
    //> sta SprObject_PageLoc,x     ;object's page location and save
    sprobjectPageloc[X] = (oldPageLoc + memory[0x2].toInt() + carry2) and 0xFF
    //> pla
    //> clc                         ;pull old carry from stack and add
    //> adc $00                     ;to high nybble moved to low
    val result = (savedCarry + memory[0x0].toInt()) and 0xFF
    //> ExXMove:  rts                         ;and leave
    return result
}

// Decompiled from MoveD_EnemyVertically
fun movedEnemyvertically(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    val enemyState by MemoryByteIndexed(Enemy_State)
    //> MoveD_EnemyVertically:
    //> ldy #$3d           ;set quick movement amount downwards
    //> lda Enemy_State,x  ;then check enemy state
    //> cmp #$05           ;if not set to unique state for spiny's egg, go ahead
    //> bne ContVMove      ;and use, otherwise set different movement amount, continue on
    if (!(enemyState[X] - 0x05 == 0)) {
        //  goto ContVMove
        return
    }
    temp0 = enemyState[X]
    temp1 = 0x3D
    if (enemyState[X] == 0x05) {
    }
    //> ContVMove: jmp SetHiMax   ;jump to skip the rest of this
    //> SetHiMax:    lda #$03                ;set maximum speed in A
    temp0 = 0x03
}

// Decompiled from MoveFallingPlatform
fun moveFallingPlatform() {
    //> MoveFallingPlatform:
    //> ldy #$20       ;set movement amount
    //> ContVMove: jmp SetHiMax   ;jump to skip the rest of this
    //> SetHiMax:    lda #$03                ;set maximum speed in A
}

// Decompiled from MoveDropPlatform
fun moveDropPlatform() {
    var temp0: Int = 0
    //> MoveDropPlatform:
    //> ldy #$7f      ;set movement amount for drop platform
    //> bne SetMdMax  ;skip ahead of other value set here
    if (!(0x7F == 0)) {
        //  goto SetMdMax
        return
    }
    temp0 = 0x7F
    if (0x7F == 0) {
    }
    //> SetMdMax: lda #$02         ;set maximum speed in A
    //> bne SetXMoveAmt  ;unconditional branch
    if (!(0x02 == 0)) {
        //  goto SetXMoveAmt
        return
    } else {
        //> ;--------------------------------
    }
}

// Decompiled from MoveEnemySlowVert
fun moveEnemySlowVert() {
    //> MoveEnemySlowVert:
    //> ldy #$0f         ;set movement amount for bowser/other objects
    //> SetMdMax: lda #$02         ;set maximum speed in A
    //> bne SetXMoveAmt  ;unconditional branch
    if (!(0x02 == 0)) {
        //  goto SetXMoveAmt
        return
    } else {
        //> ;--------------------------------
    }
}

// Decompiled from MoveJ_EnemyVertically
fun movejEnemyvertically() {
    //> MoveJ_EnemyVertically:
    //> ldy #$1c                ;set movement amount for podoboo/other objects
    //> SetHiMax:    lda #$03                ;set maximum speed in A
}

// Decompiled from SetXMoveAmt
fun setXMoveAmt(X: Int, Y: Int) {
    var A: Int = 0
    var X: Int = X
    var objectOffset by MemoryByte(ObjectOffset)
    //> SetXMoveAmt: sty $00                 ;set movement amount here
    memory[0x0] = Y.toUByte()
    //> inx                     ;increment X for enemy offset
    X = (X + 1) and 0xFF
    //> jsr ImposeGravitySprObj ;do a sub to move enemy object downwards
    imposeGravitySprObj(A)
    //> ldx ObjectOffset        ;get enemy object buffer offset and leave
    //> rts
    return
}

// Decompiled from ImposeGravityBlock
fun imposeGravityBlock() {
    val maxSpdBlockData by MemoryByteIndexed(MaxSpdBlockData)
    //> ImposeGravityBlock:
    //> ldy #$01       ;set offset for maximum speed
    //> lda #$50       ;set movement amount here
    //> sta $00
    memory[0x0] = 0x50.toUByte()
    //> lda MaxSpdBlockData,y    ;get maximum speed
}

// Decompiled from ImposeGravitySprObj
fun imposeGravitySprObj(A: Int) {
    //> ImposeGravitySprObj:
    //> sta $02            ;set maximum speed here
    memory[0x2] = A.toUByte()
    //> lda #$00           ;set value to move downwards
    //> jmp ImposeGravity  ;jump to the code that actually moves it
}

// Decompiled from MovePlatformDown
fun movePlatformDown() {
    //> MovePlatformDown:
    //> lda #$00    ;save value to stack (if branching here, execute next
    //> .db $2c     ;part as BIT instruction)
}

// Decompiled from MovePlatformUp
fun movePlatformUp(X: Int) {
    var X: Int = X
    var temp0: Int = 0
    var temp1: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    val enemyId by MemoryByteIndexed(Enemy_ID)
    //> MovePlatformUp:
    //> lda #$01        ;save value to stack
    //> pha
    push(0x01)
    //> ldy Enemy_ID,x  ;get enemy object identifier
    //> inx             ;increment offset for enemy object
    X = (X + 1) and 0xFF
    //> lda #$05        ;load default value here
    //> cpy #$29        ;residual comparison, object #29 never executes
    //> bne SetDplSpd   ;this code, thus unconditional branch here
    temp0 = 0x05
    temp1 = enemyId[X]
    if (enemyId[X] == 0x29) {
        //> lda #$09        ;residual code
        temp0 = 0x09
    }
    //> SetDplSpd: sta $00         ;save downward movement amount here
    memory[0x0] = temp0.toUByte()
    //> lda #$0a        ;save upward movement amount here
    temp0 = 0x0A
    //> sta $01
    memory[0x1] = temp0.toUByte()
    //> lda #$03        ;save maximum vertical speed here
    temp0 = 0x03
    //> sta $02
    memory[0x2] = temp0.toUByte()
    //> pla             ;get value from stack
    temp0 = pull()
    //> tay             ;use as Y, then move onto code shared by red koopa
    //> RedPTroopaGrav:
    //> jsr ImposeGravity  ;do a sub to move object gradually
    imposeGravity(temp0, X)
    //> ldx ObjectOffset   ;get enemy object offset and leave
    //> rts
    return
}

// Decompiled from ImposeGravity
fun imposeGravity(A: Int, X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    val sprobjectYmfDummy by MemoryByteIndexed(SprObject_YMF_Dummy)
    val sprobjectYHighpos by MemoryByteIndexed(SprObject_Y_HighPos)
    val sprobjectYMoveforce by MemoryByteIndexed(SprObject_Y_MoveForce)
    val sprobjectYPosition by MemoryByteIndexed(SprObject_Y_Position)
    val sprobjectYSpeed by MemoryByteIndexed(SprObject_Y_Speed)
    //> ImposeGravity:
    //> pha                          ;push value to stack
    push(A)
    //> lda SprObject_YMF_Dummy,x
    //> clc                          ;add value in movement force to contents of dummy variable
    //> adc SprObject_Y_MoveForce,x
    //> sta SprObject_YMF_Dummy,x
    sprobjectYmfDummy[X] = (sprobjectYmfDummy[X] + sprobjectYMoveforce[X]) and 0xFF
    //> ldy #$00                     ;set Y to zero by default
    //> lda SprObject_Y_Speed,x      ;get current vertical speed
    //> bpl AlterYP                  ;if currently moving downwards, do not decrement Y
    temp0 = sprobjectYSpeed[X]
    temp1 = 0x00
    if ((sprobjectYSpeed[X] and 0x80) != 0) {
        //> dey                          ;otherwise decrement Y
        temp1 = (temp1 - 1) and 0xFF
    }
    //> AlterYP: sty $07                      ;store Y here
    memory[0x7] = temp1.toUByte()
    //> adc SprObject_Y_Position,x   ;add vertical position to vertical speed plus carry
    //> sta SprObject_Y_Position,x   ;store as new vertical position
    sprobjectYPosition[X] = (temp0 + sprobjectYPosition[X] + (if (sprobjectYmfDummy[X] + sprobjectYMoveforce[X] > 0xFF) 1 else 0)) and 0xFF
    //> lda SprObject_Y_HighPos,x
    temp0 = sprobjectYHighpos[X]
    //> adc $07                      ;add carry plus contents of $07 to vertical high byte
    //> sta SprObject_Y_HighPos,x    ;store as new vertical high byte
    sprobjectYHighpos[X] = (temp0 + memory[0x7].toInt() + (if (temp0 + sprobjectYPosition[X] + (if (sprobjectYmfDummy[X] + sprobjectYMoveforce[X] > 0xFF) 1 else 0) > 0xFF) 1 else 0)) and 0xFF
    //> lda SprObject_Y_MoveForce,x
    temp0 = sprobjectYMoveforce[X]
    //> clc
    //> adc $00                      ;add downward movement amount to contents of $0433
    //> sta SprObject_Y_MoveForce,x
    sprobjectYMoveforce[X] = (temp0 + memory[0x0].toInt()) and 0xFF
    //> lda SprObject_Y_Speed,x      ;add carry to vertical speed and store
    temp0 = sprobjectYSpeed[X]
    //> adc #$00
    //> sta SprObject_Y_Speed,x
    sprobjectYSpeed[X] = (temp0 + (if (temp0 + memory[0x0].toInt() > 0xFF) 1 else 0)) and 0xFF
    //> cmp $02                      ;compare to maximum speed
    //> bmi ChkUpM                   ;if less than preset value, skip this part
    temp0 = (temp0 + (if (temp0 + memory[0x0].toInt() > 0xFF) 1 else 0)) and 0xFF
    if (!(((temp0 + if (temp0 + memory[0x0].toInt() > 0xFF) 1 else 0) and 0xFF) - memory[0x2].toInt() < 0)) {
        //> lda SprObject_Y_MoveForce,x
        temp0 = sprobjectYMoveforce[X]
        //> cmp #$80                     ;if less positively than preset maximum, skip this part
        //> bcc ChkUpM
        if (temp0 >= 0x80) {
            //> lda $02
            temp0 = memory[0x2].toInt()
            //> sta SprObject_Y_Speed,x      ;keep vertical speed within maximum value
            sprobjectYSpeed[X] = temp0
            //> lda #$00
            temp0 = 0x00
            //> sta SprObject_Y_MoveForce,x  ;clear fractional
            sprobjectYMoveforce[X] = temp0
        }
    }
    //> ChkUpM:  pla                          ;get value from stack
    temp0 = pull()
    //> beq ExVMove                  ;if set to zero, branch to leave
    if (temp0 != 0) {
        //> lda $02
        temp0 = memory[0x2].toInt()
        //> eor #%11111111               ;otherwise get two's compliment of maximum speed
        temp2 = temp0 xor 0xFF
        //> tay
        //> iny
        temp2 = (temp2 + 1) and 0xFF
        //> sty $07                      ;store two's compliment here
        memory[0x7] = temp2.toUByte()
        //> lda SprObject_Y_MoveForce,x
        temp0 = sprobjectYMoveforce[X]
        //> sec                          ;subtract upward movement amount from contents
        //> sbc $01                      ;of movement force, note that $01 is twice as large as $00,
        //> sta SprObject_Y_MoveForce,x  ;thus it effectively undoes add we did earlier
        sprobjectYMoveforce[X] = (temp0 - memory[0x1].toInt()) and 0xFF
        //> lda SprObject_Y_Speed,x
        temp0 = sprobjectYSpeed[X]
        //> sbc #$00                     ;subtract borrow from vertical speed and store
        //> sta SprObject_Y_Speed,x
        sprobjectYSpeed[X] = (temp0 - (if (temp0 - memory[0x1].toInt() >= 0) 0 else 1)) and 0xFF
        //> cmp $07                      ;compare vertical speed to two's compliment
        //> bpl ExVMove                  ;if less negatively than preset maximum, skip this part
        temp0 = (temp0 - (if (temp0 - memory[0x1].toInt() >= 0) 0 else 1)) and 0xFF
        temp1 = temp2
        if (((temp0 - if (temp0 - memory[0x1].toInt() >= 0) 0 else 1) and 0xFF) - memory[0x7].toInt() < 0) {
            //> lda SprObject_Y_MoveForce,x
            temp0 = sprobjectYMoveforce[X]
            //> cmp #$80                     ;check if fractional part is above certain amount,
            //> bcs ExVMove                  ;and if so, branch to leave
            if (!(temp0 >= 0x80)) {
                //> lda $07
                temp0 = memory[0x7].toInt()
                //> sta SprObject_Y_Speed,x      ;keep vertical speed within maximum value
                sprobjectYSpeed[X] = temp0
                //> lda #$ff
                temp0 = 0xFF
                //> sta SprObject_Y_MoveForce,x  ;clear fractional
                sprobjectYMoveforce[X] = temp0
            }
        }
    }
    //> ExVMove: rts                          ;leave!
    return
}

// Decompiled from EnemiesAndLoopsCore
fun enemiesAndLoopsCore(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp10: Int = 0
    var temp11: Int = 0
    var temp12: Int = 0
    var temp13: Int = 0
    var temp14: Int = 0
    var temp15: Int = 0
    var temp16: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var temp5: Int = 0
    var temp6: Int = 0
    var temp7: Int = 0
    var temp8: Int = 0
    var temp9: Int = 0
    var areaParserTaskNum by MemoryByte(AreaParserTaskNum)
    var areaPointer by MemoryByte(AreaPointer)
    var currentColumnPos by MemoryByte(CurrentColumnPos)
    var currentPageLoc by MemoryByte(CurrentPageLoc)
    var enemyDataOffset by MemoryByte(EnemyDataOffset)
    var enemyFrenzyBuffer by MemoryByte(EnemyFrenzyBuffer)
    var enemyFrenzyQueue by MemoryByte(EnemyFrenzyQueue)
    var enemyObjectPageLoc by MemoryByte(EnemyObjectPageLoc)
    var enemyObjectPageSel by MemoryByte(EnemyObjectPageSel)
    var entrancePage by MemoryByte(EntrancePage)
    var loopCommand by MemoryByte(LoopCommand)
    var multiLoopCorrectCntr by MemoryByte(MultiLoopCorrectCntr)
    var multiLoopPassCntr by MemoryByte(MultiLoopPassCntr)
    var numberofGroupEnemies by MemoryByte(NumberofGroupEnemies)
    var objectOffset by MemoryByte(ObjectOffset)
    var playerState by MemoryByte(Player_State)
    var playerYPosition by MemoryByte(Player_Y_Position)
    var primaryHardMode by MemoryByte(PrimaryHardMode)
    var screenrightPageloc by MemoryByte(ScreenRight_PageLoc)
    var screenrightXPos by MemoryByte(ScreenRight_X_Pos)
    var secondaryHardMode by MemoryByte(SecondaryHardMode)
    var vineFlagOffset by MemoryByte(VineFlagOffset)
    var worldNumber by MemoryByte(WorldNumber)
    val enemyFlag by MemoryByteIndexed(Enemy_Flag)
    val enemyId by MemoryByteIndexed(Enemy_ID)
    val enemyPageloc by MemoryByteIndexed(Enemy_PageLoc)
    val enemyState by MemoryByteIndexed(Enemy_State)
    val enemyXPosition by MemoryByteIndexed(Enemy_X_Position)
    val enemyYHighpos by MemoryByteIndexed(Enemy_Y_HighPos)
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    val loopCmdPageNumber by MemoryByteIndexed(LoopCmdPageNumber)
    val loopCmdWorldNumber by MemoryByteIndexed(LoopCmdWorldNumber)
    val loopCmdYPosition by MemoryByteIndexed(LoopCmdYPosition)
    //> EnemiesAndLoopsCore:
    //> lda Enemy_Flag,x         ;check data here for MSB set
    //> pha                      ;save in stack
    push(enemyFlag[X])
    //> asl
    //> bcs ChkBowserF           ;if MSB set in enemy flag, branch ahead of jumps
    temp0 = (enemyFlag[X] shl 1) and 0xFF
    if ((enemyFlag[X] and 0x80) == 0) {
        //> pla                      ;get from stack
        temp0 = pull()
        //> beq ChkAreaTsk           ;if data zero, branch
        if (temp0 != 0) {
            //> jmp RunEnemyObjectsCore  ;otherwise, jump to run enemy subroutines
            // Fixed: Actually call the RunEnemyObjectsCore code and return
            runEnemyObjectsCoreInline(X, objectOffset, enemyFlag, enemyId, enemyState, enemyYHighpos, enemyYPosition, enemyXPosition, enemyPageloc)
            return
        }
        //> ChkAreaTsk: lda AreaParserTaskNum    ;check number of tasks to perform
        temp0 = areaParserTaskNum
        //> and #$07
        temp1 = temp0 and 0x07
        //> cmp #$07                 ;if at a specific task, jump and leave
        //> beq ExitELCore
        temp0 = temp1
        if (temp1 != 0x07) {
        } else {
            //> ExitELCore: rts
            return
        }
    } else {
        //> ChkBowserF: pla                      ;get data from stack
        temp0 = pull()
        //> and #%00001111           ;mask out high nybble
        temp2 = temp0 and 0x0F
        //> tay
        //> lda Enemy_Flag,y         ;use as pointer and load same place with different offset
        temp0 = enemyFlag[temp2]
        //> bne ExitELCore
        temp3 = temp2
        if (temp0 == 0) {
            //> sta Enemy_Flag,x         ;if second enemy flag not set, also clear first one
            enemyFlag[X] = temp0
        } else {
            // ExitELCore: return when flag is non-zero (bowser case)
            return
        }
    }
    //> ProcLoopCommand:
    //> lda LoopCommand           ;check if loop command was found
    temp0 = loopCommand
    //> beq ChkEnemyFrenzy
    if (temp0 != 0) {
        //> lda CurrentColumnPos      ;check to see if we're still on the first page
        temp0 = currentColumnPos
        //> bne ChkEnemyFrenzy        ;if not, do not loop yet
        if (temp0 == 0) {
            //> ldy #$0b                  ;start at the end of each set of loop data
            temp3 = 0x0B
            // Fixed: Correct loop structure for FindLoop
            // Original assembly: DEY, BMI exit, check world (bne FindLoop), check page (bne FindLoop)
            // Both world and page checks go back to FindLoop which decrements first
            run findLoop@{
                while (true) {
                    //> FindLoop: dey
                    temp3 = (temp3 - 1) and 0xFF
                    //> bmi ChkEnemyFrenzy        ;if all data is checked and not match, do not loop
                    if ((temp3 and 0x80) != 0) return@findLoop  // Exit when negative (wrapped from 0)
                    //> lda WorldNumber           ;check to see if one of the world numbers
                    temp0 = worldNumber
                    //> cmp LoopCmdWorldNumber,y  ;matches our current world number
                    //> bne FindLoop
                    if (temp0 != loopCmdWorldNumber[temp3]) continue
                    //> lda CurrentPageLoc        ;check to see if one of the page numbers
                    temp0 = currentPageLoc
                    //> cmp LoopCmdPageNumber,y   ;matches the page we're currently on
                    //> bne FindLoop
                    if (temp0 != loopCmdPageNumber[temp3]) continue
                    // Found a match - exit the loop
                    break
                }
            }
            //> lda Player_Y_Position     ;check to see if the player is at the correct position
            temp0 = playerYPosition
            //> cmp LoopCmdYPosition,y    ;if not, branch to check for world 7
            //> bne WrongChk
            if (temp0 == loopCmdYPosition[temp3]) {
                //> lda Player_State          ;check to see if the player is
                temp0 = playerState
                //> cmp #$00                  ;on solid ground (i.e. not jumping or falling)
                //> bne WrongChk              ;if not, player fails to pass loop, and loopback
                if (temp0 == 0) {
                    //> lda WorldNumber           ;are we in world 7? (check performed on correct
                    temp0 = worldNumber
                    //> cmp #World7               ;vertical position and on solid ground)
                    //> bne InitMLp               ;if not, initialize flags used there, otherwise
                    if (temp0 == World7) {
                        //> inc MultiLoopCorrectCntr  ;increment counter for correct progression
                        multiLoopCorrectCntr = (multiLoopCorrectCntr + 1) and 0xFF
                        //> IncMLoop: inc MultiLoopPassCntr     ;increment master multi-part counter
                        multiLoopPassCntr = (multiLoopPassCntr + 1) and 0xFF
                        //> lda MultiLoopPassCntr     ;have we done all three parts?
                        temp0 = multiLoopPassCntr
                        //> cmp #$03
                        //> bne InitLCmd              ;if not, skip this part
                        if (temp0 == 0x03) {
                            //> lda MultiLoopCorrectCntr  ;if so, have we done them all correctly?
                            temp0 = multiLoopCorrectCntr
                            //> cmp #$03
                            //> beq InitMLp               ;if so, branch past unnecessary check here
                            if (temp0 != 0x03) {
                                //> bne DoLpBack              ;unconditional branch if previous branch fails
                                if (temp0 == 0x03) {
                                }
                            }
                        }
                    }
                }
            }
            do {
                //> WrongChk: lda WorldNumber           ;are we in world 7? (check performed on
                temp0 = worldNumber
                //> cmp #World7               ;incorrect vertical position or not on solid ground)
                //> beq IncMLoop
            } while (temp0 == World7)
            //> DoLpBack: jsr ExecGameLoopback      ;if player is not in right place, loop back
            execGameLoopback(temp3)
            //> jsr KillAllEnemies
            killAllEnemies(temp0)
            //> InitMLp:  lda #$00                  ;initialize counters used for multi-part loop commands
            temp0 = 0x00
            //> sta MultiLoopPassCntr
            multiLoopPassCntr = temp0
            //> sta MultiLoopCorrectCntr
            multiLoopCorrectCntr = temp0
            //> InitLCmd: lda #$00                  ;initialize loop command flag
            temp0 = 0x00
            //> sta LoopCommand
            loopCommand = temp0
            //> ;--------------------------------
        }
    }
    //> ChkEnemyFrenzy:
    //> lda EnemyFrenzyQueue  ;check for enemy object in frenzy queue
    temp0 = enemyFrenzyQueue
    //> beq ProcessEnemyData  ;if not, skip this part
    if (temp0 != 0) {
        //> sta Enemy_ID,x        ;store as enemy object identifier here
        enemyId[X] = temp0
        //> lda #$01
        temp0 = 0x01
        //> sta Enemy_Flag,x      ;activate enemy object flag
        enemyFlag[X] = temp0
        //> lda #$00
        temp0 = 0x00
        //> sta Enemy_State,x     ;initialize state and frenzy queue
        enemyState[X] = temp0
        //> sta EnemyFrenzyQueue
        enemyFrenzyQueue = temp0
        //> jmp InitEnemyObject   ;and then jump to deal with this enemy
    }
    //> ProcessEnemyData:
    //> ldy EnemyDataOffset      ;get offset of enemy object data
    temp3 = enemyDataOffset
    //> lda (EnemyData),y        ;load first byte
    temp0 = memory[readWord(EnemyData) + temp3].toInt()
    //> cmp #$ff                 ;check for EOD terminator
    //> bne CheckEndofBuffer
    if (temp0 == 0xFF) {
    } else {
        //> CheckEndofBuffer:
        //> and #%00001111           ;check for special row $0e
        temp4 = temp0 and 0x0F
        //> cmp #$0e
        //> beq CheckRightBounds     ;if found, branch, otherwise
        temp0 = temp4
        if (temp4 != 0x0E) {
            //> cpx #$05                 ;check for end of buffer
            //> bcc CheckRightBounds     ;if not at end of buffer, branch
            if (X >= 0x05) {
                //> iny
                temp3 = (temp3 + 1) and 0xFF
                //> lda (EnemyData),y        ;check for specific value here
                temp0 = memory[readWord(EnemyData) + temp3].toInt()
                //> and #%00111111           ;not sure what this was intended for, exactly
                temp5 = temp0 and 0x3F
                //> cmp #$2e                 ;this part is quite possibly residual code
                //> beq CheckRightBounds     ;but it has the effect of keeping enemies out of
                temp0 = temp5
                if (temp5 != 0x2E) {
                    //> rts                      ;the sixth slot
                    return
                }
            }
        }
        //> CheckRightBounds:
        //> lda ScreenRight_X_Pos    ;add 48 to pixel coordinate of right boundary
        temp0 = screenrightXPos
        //> clc
        //> adc #$30
        //> and #%11110000           ;store high nybble
        temp6 = (temp0 + 0x30) and 0xFF and 0xF0
        //> sta $07
        memory[0x7] = temp6.toUByte()
        //> lda ScreenRight_PageLoc  ;add carry to page location of right boundary
        temp0 = screenrightPageloc
        //> adc #$00
        //> sta $06                  ;store page location + carry
        memory[0x6] = ((temp0 + (if (temp0 + 0x30 > 0xFF) 1 else 0)) and 0xFF).toUByte()
        //> ldy EnemyDataOffset
        temp3 = enemyDataOffset
        //> iny
        temp3 = (temp3 + 1) and 0xFF
        //> lda (EnemyData),y        ;if MSB of enemy object is clear, branch to check for row $0f
        temp0 = memory[readWord(EnemyData) + temp3].toInt()
        //> asl
        temp0 = (temp0 shl 1) and 0xFF
        //> bcc CheckPageCtrlRow
        if ((temp0 and 0x80) != 0) {
            //> lda EnemyObjectPageSel   ;if page select already set, do not set again
            temp0 = enemyObjectPageSel
            //> bne CheckPageCtrlRow
            if (temp0 == 0) {
                //> inc EnemyObjectPageSel   ;otherwise, if MSB is set, set page select
                enemyObjectPageSel = (enemyObjectPageSel + 1) and 0xFF
                //> inc EnemyObjectPageLoc   ;and increment page control
                enemyObjectPageLoc = (enemyObjectPageLoc + 1) and 0xFF
            }
        }
        //> CheckPageCtrlRow:
        //> dey
        temp3 = (temp3 - 1) and 0xFF
        //> lda (EnemyData),y        ;reread first byte
        temp0 = memory[readWord(EnemyData) + temp3].toInt()
        //> and #$0f
        temp7 = temp0 and 0x0F
        //> cmp #$0f                 ;check for special row $0f
        //> bne PositionEnemyObj     ;if not found, branch to position enemy object
        temp0 = temp7
        if (temp7 == 0x0F) {
            //> lda EnemyObjectPageSel   ;if page select set,
            temp0 = enemyObjectPageSel
            //> bne PositionEnemyObj     ;branch without reading second byte
            if (temp0 == 0) {
                // This block handles special row $0F with page select = 0
                // Original code: jmp CheckFrenzyBuffer / jmp ProcLoopCommand
                // We execute once and return - will be called again on next frame
                //> iny
                temp3 = (temp3 + 1) and 0xFF
                //> lda (EnemyData),y        ;otherwise, get second byte, mask out 2 MSB
                temp0 = memory[readWord(EnemyData) + temp3].toInt()
                //> and #%00111111
                temp8 = temp0 and 0x3F
                //> sta EnemyObjectPageLoc   ;store as page control for enemy object data
                enemyObjectPageLoc = temp8
                //> inc EnemyDataOffset      ;increment enemy object data offset 2 bytes
                enemyDataOffset = (enemyDataOffset + 1) and 0xFF
                //> inc EnemyDataOffset
                enemyDataOffset = (enemyDataOffset + 1) and 0xFF
                //> inc EnemyObjectPageSel   ;set page select for enemy object data and
                enemyObjectPageSel = (enemyObjectPageSel + 1) and 0xFF
                //> jmp ProcLoopCommand      ;jump back to process loop commands
                return  // Exit and restart from beginning on next call
            }
        }
        //> PositionEnemyObj:
        //> lda EnemyObjectPageLoc   ;store page control as page location
        temp0 = enemyObjectPageLoc
        //> sta Enemy_PageLoc,x      ;for enemy object
        enemyPageloc[X] = temp0
        //> lda (EnemyData),y        ;get first byte of enemy object
        temp0 = memory[readWord(EnemyData) + temp3].toInt()
        //> and #%11110000
        temp9 = temp0 and 0xF0
        //> sta Enemy_X_Position,x   ;store column position
        enemyXPosition[X] = temp9
        //> cmp ScreenRight_X_Pos    ;check column position against right boundary
        //> lda Enemy_PageLoc,x      ;without subtracting, then subtract borrow
        temp0 = enemyPageloc[X]
        //> sbc ScreenRight_PageLoc  ;from page location
        //> bcs CheckRightExtBounds  ;if enemy object beyond or at boundary, branch
        temp0 = (temp0 - screenrightPageloc - (if (temp9 >= screenrightXPos) 0 else 1)) and 0xFF
        if (!((temp0 - screenrightPageloc - (if (temp9 >= screenrightXPos) 0 else 1)) >= 0)) {
            //> lda (EnemyData),y
            temp0 = memory[readWord(EnemyData) + temp3].toInt()
            //> and #%00001111           ;check for special row $0e
            temp10 = temp0 and 0x0F
            //> cmp #$0e                 ;if found, jump elsewhere
            //> beq ParseRow0e
            temp0 = temp10
            if (temp10 != 0x0E) {
                //> jmp CheckThreeBytes      ;if not found, unconditional jump
            }
        }
        //> CheckRightExtBounds:
        //> lda $07                  ;check right boundary + 48 against
        temp0 = memory[0x7].toInt()
        //> cmp Enemy_X_Position,x   ;column position without subtracting,
        //> lda $06                  ;then subtract borrow from page control temp
        temp0 = memory[0x6].toInt()
        //> sbc Enemy_PageLoc,x      ;plus carry
        //> bcc CheckFrenzyBuffer    ;if enemy object beyond extended boundary, branch
        temp0 = (temp0 - enemyPageloc[X] - (if (temp0 >= enemyXPosition[X]) 0 else 1)) and 0xFF
        if ((temp0 - enemyPageloc[X] - (if (temp0 >= enemyXPosition[X]) 0 else 1)) >= 0) {
            //> lda #$01                 ;store value in vertical high byte
            temp0 = 0x01
            //> sta Enemy_Y_HighPos,x
            enemyYHighpos[X] = temp0
            //> lda (EnemyData),y        ;get first byte again
            temp0 = memory[readWord(EnemyData) + temp3].toInt()
            //> asl                      ;multiply by four to get the vertical
            temp0 = (temp0 shl 1) and 0xFF
            //> asl                      ;coordinate
            temp0 = (temp0 shl 1) and 0xFF
            //> asl
            temp0 = (temp0 shl 1) and 0xFF
            //> asl
            temp0 = (temp0 shl 1) and 0xFF
            //> sta Enemy_Y_Position,x
            enemyYPosition[X] = temp0
            //> cmp #$e0                 ;do one last check for special row $0e
            //> beq ParseRow0e           ;(necessary if branched to $c1cb)
            if (temp0 != 0xE0) {
                //> iny
                temp3 = (temp3 + 1) and 0xFF
                //> lda (EnemyData),y        ;get second byte of object
                temp0 = memory[readWord(EnemyData) + temp3].toInt()
                //> and #%01000000           ;check to see if hard mode bit is set
                temp11 = temp0 and 0x40
                //> beq CheckForEnemyGroup   ;if not, branch to check for group enemy objects
                temp0 = temp11
                if (temp11 != 0) {
                    //> lda SecondaryHardMode    ;if set, check to see if secondary hard mode flag
                    temp0 = secondaryHardMode
                    //> beq Inc2B                ;is on, and if not, branch to skip this object completely
                    if (temp0 != 0) {
                    } else {
                        //> Inc2B:  inc EnemyDataOffset      ;otherwise increment two bytes
                        enemyDataOffset = (enemyDataOffset + 1) and 0xFF
                        //> inc EnemyDataOffset
                        enemyDataOffset = (enemyDataOffset + 1) and 0xFF
                        //> lda #$00                 ;init page select for enemy objects
                        temp0 = 0x00
                        //> sta EnemyObjectPageSel
                        enemyObjectPageSel = temp0
                        //> ldx ObjectOffset         ;reload current offset in enemy buffers
                        //> rts                      ;and leave
                        return
                    }
                }
                //> CheckForEnemyGroup:
                //> lda (EnemyData),y      ;get second byte and mask out 2 MSB
                temp0 = memory[readWord(EnemyData) + temp3].toInt()
                //> and #%00111111
                temp12 = temp0 and 0x3F
                //> cmp #$37               ;check for value below $37
                //> bcc BuzzyBeetleMutate
                temp0 = temp12
                if (temp12 >= 0x37) {
                    //> cmp #$3f               ;if $37 or greater, check for value
                    //> bcc DoGroup            ;below $3f, branch if below $3f
                    if (!(temp0 >= 0x3F)) {
                        //  goto DoGroup
                        return
                    }
                    if (temp0 >= 0x3F) {
                    }
                }
                //> BuzzyBeetleMutate:
                //> cmp #Goomba          ;if below $37, check for goomba
                //> bne StrID            ;value ($3f or more always fails)
                if (temp0 == Goomba) {
                    //> ldy PrimaryHardMode  ;check if primary hard mode flag is set
                    temp3 = primaryHardMode
                    //> beq StrID            ;and if so, change goomba to buzzy beetle
                    if (temp3 != 0) {
                        //> lda #BuzzyBeetle
                        temp0 = BuzzyBeetle
                    }
                }
                //> StrID:  sta Enemy_ID,x       ;store enemy object number into buffer
                enemyId[X] = temp0
                //> lda #$01
                temp0 = 0x01
                //> sta Enemy_Flag,x     ;set flag for enemy in buffer
                enemyFlag[X] = temp0
                //> jsr InitEnemyObject
                initEnemyObject(X)
                //> lda Enemy_Flag,x     ;check to see if flag is set
                temp0 = enemyFlag[X]
                //> bne Inc2B            ;if not, leave, otherwise branch
                if (temp0 == 0) {
                    //> rts
                    return
                }
            }
        }
    }
    //> CheckFrenzyBuffer:
    //> lda EnemyFrenzyBuffer    ;if enemy object stored in frenzy buffer
    temp0 = enemyFrenzyBuffer
    //> bne StrFre               ;then branch ahead to store in enemy object buffer
    if (temp0 == 0) {
        //> lda VineFlagOffset       ;otherwise check vine flag offset
        temp0 = vineFlagOffset
        //> cmp #$01
        //> bne ExEPar               ;if other value <> 1, leave
        if (temp0 == 0x01) {
            //> lda #VineObject          ;otherwise put vine in enemy identifier
            temp0 = VineObject
        } else {
            //> ExEPar: rts                      ;then leave
            return
        }
    }
    //> StrFre: sta Enemy_ID,x           ;store contents of frenzy buffer into enemy identifier value
    enemyId[X] = temp0
    //> DoGroup:
    //> jmp HandleGroupEnemies   ;handle enemy group objects
    //> ParseRow0e:
    //> iny                      ;increment Y to load third byte of object
    temp3 = (temp3 + 1) and 0xFF
    //> iny
    temp3 = (temp3 + 1) and 0xFF
    //> lda (EnemyData),y
    temp0 = memory[readWord(EnemyData) + temp3].toInt()
    //> lsr                      ;move 3 MSB to the bottom, effectively
    temp0 = temp0 shr 1
    //> lsr                      ;making %xxx00000 into %00000xxx
    temp0 = temp0 shr 1
    //> lsr
    temp0 = temp0 shr 1
    //> lsr
    temp0 = temp0 shr 1
    //> lsr
    temp0 = temp0 shr 1
    //> cmp WorldNumber          ;is it the same world number as we're on?
    //> bne NotUse               ;if not, do not use (this allows multiple uses
    if (!(temp0 - worldNumber == 0)) {
        //  goto NotUse
        return
    }
    if (temp0 == worldNumber) {
        //> dey                      ;of the same area, like the underground bonus areas)
        temp3 = (temp3 - 1) and 0xFF
        //> lda (EnemyData),y        ;otherwise, get second byte and use as offset
        temp0 = memory[readWord(EnemyData) + temp3].toInt()
        //> sta AreaPointer          ;to addresses for level and enemy object data
        areaPointer = temp0
        //> iny
        temp3 = (temp3 + 1) and 0xFF
        //> lda (EnemyData),y        ;get third byte again, and this time mask out
        temp0 = memory[readWord(EnemyData) + temp3].toInt()
        //> and #%00011111           ;the 3 MSB from before, save as page number to be
        temp13 = temp0 and 0x1F
        //> sta EntrancePage         ;used upon entry to area, if area is entered
        entrancePage = temp13
    }
    //> NotUse: jmp Inc3B
    //> CheckThreeBytes:
    //> ldy EnemyDataOffset      ;load current offset for enemy object data
    temp3 = enemyDataOffset
    //> lda (EnemyData),y        ;get first byte
    temp0 = memory[readWord(EnemyData) + temp3].toInt()
    //> and #%00001111           ;check for special row $0e
    temp14 = temp0 and 0x0F
    //> cmp #$0e
    //> bne Inc2B
    temp0 = temp14
    if (temp14 == 0x0E) {
        //> Inc3B:  inc EnemyDataOffset      ;if row = $0e, increment three bytes
        enemyDataOffset = (enemyDataOffset + 1) and 0xFF
    }
    //> HandleGroupEnemies:
    //> ldy #$00                  ;load value for green koopa troopa
    temp3 = 0x00
    //> sec
    //> sbc #$37                  ;subtract $37 from second byte read
    //> pha                       ;save result in stack for now
    push((temp0 - 0x37) and 0xFF)
    //> cmp #$04                  ;was byte in $3b-$3e range?
    //> bcs SnglID                ;if so, branch
    temp0 = (temp0 - 0x37) and 0xFF
    if (!(((temp0 - 0x37) and 0xFF) >= 0x04)) {
        //> pha                       ;save another copy to stack
        push(temp0)
        //> ldy #Goomba               ;load value for goomba enemy
        temp3 = Goomba
        //> lda PrimaryHardMode       ;if primary hard mode flag not set,
        temp0 = primaryHardMode
        //> beq PullID                ;branch, otherwise change to value
        if (temp0 != 0) {
            //> ldy #BuzzyBeetle          ;for buzzy beetle
            temp3 = BuzzyBeetle
        }
        //> PullID: pla                       ;get second copy from stack
        temp0 = pull()
    }
    //> SnglID: sty $01                   ;save enemy id here
    memory[0x1] = temp3.toUByte()
    //> ldy #$b0                  ;load default y coordinate
    temp3 = 0xB0
    //> and #$02                  ;check to see if d1 was set
    temp15 = temp0 and 0x02
    //> beq SetYGp                ;if so, move y coordinate up,
    temp0 = temp15
    if (temp15 != 0) {
        //> ldy #$70                  ;otherwise branch and use default
        temp3 = 0x70
    }
    //> SetYGp: sty $00                   ;save y coordinate here
    memory[0x0] = temp3.toUByte()
    //> lda ScreenRight_PageLoc   ;get page number of right edge of screen
    temp0 = screenrightPageloc
    //> sta $02                   ;save here
    memory[0x2] = temp0.toUByte()
    //> lda ScreenRight_X_Pos     ;get pixel coordinate of right edge
    temp0 = screenrightXPos
    //> sta $03                   ;save here
    memory[0x3] = temp0.toUByte()
    //> ldy #$02                  ;load two enemies by default
    temp3 = 0x02
    //> pla                       ;get first copy from stack
    temp0 = pull()
    //> lsr                       ;check to see if d0 was set
    temp0 = temp0 shr 1
    //> bcc CntGrp                ;if not, use default value
    if ((temp0 and 0x01) != 0) {
        //> iny                       ;otherwise increment to three enemies
        temp3 = (temp3 + 1) and 0xFF
    }
    //> CntGrp: sty NumberofGroupEnemies  ;save number of enemies here
    numberofGroupEnemies = temp3
    while (((numberofGroupEnemies - 1) and 0xFF) != 0) {
        //> GrLoop: ldx #$ff                  ;start at beginning of enemy buffers
        //> GSltLp: inx                       ;increment and branch if past
        temp16 = 0xFF
        temp16 = (temp16 + 1) and 0xFF
        //> cpx #$05                  ;end of buffers
        //> bcs NextED
        if (temp16 >= 0x05) {
            //  goto NextED
            return
        }
        //> lda Enemy_Flag,x          ;check to see if enemy is already
        temp0 = enemyFlag[temp16]
        //> bne GSltLp                ;stored in buffer, and branch if so
        //> lda $01
        temp0 = memory[0x1].toInt()
        //> sta Enemy_ID,x            ;store enemy object identifier
        enemyId[temp16] = temp0
        //> lda $02
        temp0 = memory[0x2].toInt()
        //> sta Enemy_PageLoc,x       ;store page location for enemy object
        enemyPageloc[temp16] = temp0
        //> lda $03
        temp0 = memory[0x3].toInt()
        //> sta Enemy_X_Position,x    ;store x coordinate for enemy object
        enemyXPosition[temp16] = temp0
        //> clc
        //> adc #$18                  ;add 24 pixels for next enemy
        //> sta $03
        memory[0x3] = ((temp0 + 0x18) and 0xFF).toUByte()
        //> lda $02                   ;add carry to page location for
        temp0 = memory[0x2].toInt()
        //> adc #$00                  ;next enemy
        //> sta $02
        memory[0x2] = ((temp0 + (if (temp0 + 0x18 > 0xFF) 1 else 0)) and 0xFF).toUByte()
        //> lda $00                   ;store y coordinate for enemy object
        temp0 = memory[0x0].toInt()
        //> sta Enemy_Y_Position,x
        enemyYPosition[temp16] = temp0
        //> lda #$01                  ;activate flag for buffer, and
        temp0 = 0x01
        //> sta Enemy_Y_HighPos,x     ;put enemy within the screen vertically
        enemyYHighpos[temp16] = temp0
        //> sta Enemy_Flag,x
        enemyFlag[temp16] = temp0
        //> jsr CheckpointEnemyID     ;process each enemy object separately
        checkpointEnemyID(temp16)
        //> dec NumberofGroupEnemies  ;do this until we run out of enemy objects
        numberofGroupEnemies = (numberofGroupEnemies - 1) and 0xFF
        //> bne GrLoop
    }
    // NextED: jmp Inc2B - increment data offset and return
    enemyDataOffset = (enemyDataOffset + 2) and 0xFF
    return
    //> RunEnemyObjectsCore:
    //> ldx ObjectOffset  ;get offset for enemy object buffer
    temp16 = objectOffset
    //> lda #$00          ;load value 0 for jump engine by default
    temp0 = 0x00
    //> ldy Enemy_ID,x
    temp3 = enemyId[temp16]
    //> cpy #$15          ;if enemy object < $15, use default value
    //> bcc JmpEO
    if (temp3 >= 0x15) {
        //> tya               ;otherwise subtract $14 from the value and use
        //> sbc #$14          ;as value for jump engine
    }
    //> JmpEO: jsr JumpEngine
    when (temp0) {
        0 -> {
            runNormalEnemies()
        }
        1 -> {
            runBowserFlame()
        }
        2 -> {
            runFireworks()
        }
        3 -> {
            noRunCode()
        }
        4 -> {
            noRunCode()
        }
        5 -> {
            noRunCode()
        }
        6 -> {
            noRunCode()
        }
        7 -> {
            runFirebarObj()
        }
        8 -> {
            runFirebarObj()
        }
        9 -> {
            runFirebarObj()
        }
        10 -> {
            runFirebarObj()
        }
        11 -> {
            runFirebarObj()
        }
        12 -> {
            runFirebarObj()
        }
        13 -> {
            runFirebarObj()
        }
        14 -> {
            runFirebarObj()
        }
        15 -> {
            noRunCode()
        }
        16 -> {
            runLargePlatform()
        }
        17 -> {
            runLargePlatform()
        }
        18 -> {
            runLargePlatform()
        }
        19 -> {
            runLargePlatform()
        }
        20 -> {
            runLargePlatform()
        }
        21 -> {
            runLargePlatform()
        }
        22 -> {
            runLargePlatform()
        }
        23 -> {
            runSmallPlatform()
        }
        24 -> {
            runSmallPlatform()
        }
        25 -> {
            runBowser()
        }
        26 -> {
            powerUpObjHandler()
        }
        27 -> {
            vineObjectHandler()
        }
        28 -> {
            noRunCode()
        }
        29 -> {
            runStarFlagObj()
        }
        30 -> {
            jumpspringHandler()
        }
        31 -> {
            noRunCode()
        }
        32 -> {
            warpZoneObject()
        }
        33 -> {
            runRetainerObj()
        }
        else -> {
            // Unknown JumpEngine index
        }
    }
    return
}

// Decompiled from ExecGameLoopback
fun execGameLoopback(Y: Int) {
    var areaDataOffset by MemoryByte(AreaDataOffset)
    var areaObjectPageLoc by MemoryByte(AreaObjectPageLoc)
    var areaObjectPageSel by MemoryByte(AreaObjectPageSel)
    var currentPageLoc by MemoryByte(CurrentPageLoc)
    var enemyDataOffset by MemoryByte(EnemyDataOffset)
    var enemyObjectPageLoc by MemoryByte(EnemyObjectPageLoc)
    var enemyObjectPageSel by MemoryByte(EnemyObjectPageSel)
    var playerPageloc by MemoryByte(Player_PageLoc)
    var screenleftPageloc by MemoryByte(ScreenLeft_PageLoc)
    var screenrightPageloc by MemoryByte(ScreenRight_PageLoc)
    val areaDataOfsLoopback by MemoryByteIndexed(AreaDataOfsLoopback)
    //> ExecGameLoopback:
    //> lda Player_PageLoc        ;send player back four pages
    //> sec
    //> sbc #$04
    //> sta Player_PageLoc
    playerPageloc = (playerPageloc - 0x04) and 0xFF
    //> lda CurrentPageLoc        ;send current page back four pages
    //> sec
    //> sbc #$04
    //> sta CurrentPageLoc
    currentPageLoc = (currentPageLoc - 0x04) and 0xFF
    //> lda ScreenLeft_PageLoc    ;subtract four from page location
    //> sec                       ;of screen's left border
    //> sbc #$04
    //> sta ScreenLeft_PageLoc
    screenleftPageloc = (screenleftPageloc - 0x04) and 0xFF
    //> lda ScreenRight_PageLoc   ;do the same for the page location
    //> sec                       ;of screen's right border
    //> sbc #$04
    //> sta ScreenRight_PageLoc
    screenrightPageloc = (screenrightPageloc - 0x04) and 0xFF
    //> lda AreaObjectPageLoc     ;subtract four from page control
    //> sec                       ;for area objects
    //> sbc #$04
    //> sta AreaObjectPageLoc
    areaObjectPageLoc = (areaObjectPageLoc - 0x04) and 0xFF
    //> lda #$00                  ;initialize page select for both
    //> sta EnemyObjectPageSel    ;area and enemy objects
    enemyObjectPageSel = 0x00
    //> sta AreaObjectPageSel
    areaObjectPageSel = 0x00
    //> sta EnemyDataOffset       ;initialize enemy object data offset
    enemyDataOffset = 0x00
    //> sta EnemyObjectPageLoc    ;and enemy object page control
    enemyObjectPageLoc = 0x00
    //> lda AreaDataOfsLoopback,y ;adjust area object offset based on
    //> sta AreaDataOffset        ;which loop command we encountered
    areaDataOffset = areaDataOfsLoopback[Y]
    //> rts
    return
}

// Decompiled from InitEnemyObject
fun initEnemyObject(X: Int) {
    val enemyState by MemoryByteIndexed(Enemy_State)
    //> InitEnemyObject:
    //> lda #$00                 ;initialize enemy state
    //> sta Enemy_State,x
    enemyState[X] = 0x00
    //> jsr CheckpointEnemyID    ;jump ahead to run jump engine and subroutines
    checkpointEnemyID(X)
    //> ExEPar: rts                      ;then leave
    return
}

// Decompiled from CheckpointEnemyID
fun checkpointEnemyID(X: Int) {
    var temp0: Int = 0
    val enemyOffscrBitsMasked by MemoryByteIndexed(EnemyOffscrBitsMasked)
    val enemyId by MemoryByteIndexed(Enemy_ID)
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    //> CheckpointEnemyID:
    //> lda Enemy_ID,x
    //> cmp #$15                     ;check enemy object identifier for $15 or greater
    //> bcs InitEnemyRoutines        ;and branch straight to the jump engine if found
    temp0 = enemyId[X]
    if (!(enemyId[X] >= 0x15)) {
        //> tay                          ;save identifier in Y register for now
        //> lda Enemy_Y_Position,x
        temp0 = enemyYPosition[X]
        //> adc #$08                     ;add eight pixels to what will eventually be the
        //> sta Enemy_Y_Position,x       ;enemy object's vertical coordinate ($00-$14 only)
        enemyYPosition[X] = (temp0 + 0x08 + (if (enemyId[X] >= 0x15) 1 else 0)) and 0xFF
        //> lda #$01
        temp0 = 0x01
        //> sta EnemyOffscrBitsMasked,x  ;set offscreen masked bit
        enemyOffscrBitsMasked[X] = temp0
        //> tya                          ;get identifier back and use as offset for jump engine
    }
    //> InitEnemyRoutines:
    //> jsr JumpEngine
    when (temp0) {
        0 -> {
            initNormalEnemy(X)
        }
        1 -> {
            initNormalEnemy(X)
        }
        2 -> {
            initNormalEnemy(X)
        }
        3 -> {
            initRedKoopa()
        }
        4 -> {
            noInitCode()
        }
        5 -> {
            initHammerBro()
        }
        6 -> {
            initGoomba()
        }
        7 -> {
            initBloober()
        }
        8 -> {
            initBulletBill()
        }
        9 -> {
            noInitCode()
        }
        10 -> {
            initCheepCheep()
        }
        11 -> {
            initCheepCheep()
        }
        12 -> {
            initPodoboo(X)
        }
        13 -> {
            initPiranhaPlant(X)
        }
        14 -> {
            initJumpGPTroopa()
        }
        15 -> {
            initRedPTroopa()
        }
        16 -> {
            initHorizFlySwimEnemy(X)
        }
        17 -> {
            initLakitu()
        }
        18 -> {
            initEnemyFrenzy()
        }
        19 -> {
            noInitCode()
        }
        20 -> {
            initEnemyFrenzy()
        }
        21 -> {
            initEnemyFrenzy()
        }
        22 -> {
            initEnemyFrenzy()
        }
        23 -> {
            initEnemyFrenzy()
        }
        24 -> {
            endFrenzy()
        }
        25 -> {
            noInitCode()
        }
        26 -> {
            noInitCode()
        }
        27 -> {
            initShortFirebar()
        }
        28 -> {
            initShortFirebar()
        }
        29 -> {
            initShortFirebar()
        }
        30 -> {
            initShortFirebar()
        }
        31 -> {
            initLongFirebar()
        }
        32 -> {
            noInitCode()
        }
        33 -> {
            noInitCode()
        }
        34 -> {
            noInitCode()
        }
        35 -> {
            noInitCode()
        }
        36 -> {
            initBalPlatform()
        }
        37 -> {
            initVertPlatform()
        }
        38 -> {
            largeLiftUp()
        }
        39 -> {
            largeLiftDown()
        }
        40 -> {
            initHoriPlatform()
        }
        41 -> {
            initDropPlatform()
        }
        42 -> {
            initHoriPlatform()
        }
        43 -> {
            platLiftUp(X)
        }
        44 -> {
            platLiftDown(X)
        }
        45 -> {
            initBowser()
        }
        46 -> {
            pwrUpJmp()
        }
        47 -> {
            setupVine(X, Y)
        }
        48 -> {
            noInitCode()
        }
        49 -> {
            noInitCode()
        }
        50 -> {
            noInitCode()
        }
        51 -> {
            noInitCode()
        }
        52 -> {
            noInitCode()
        }
        53 -> {
            initRetainerObj()
        }
        54 -> {
            endOfEnemyInitCode()
        }
        else -> {
            // Unknown JumpEngine index
        }
    }
    return
}

// Decompiled from InitPodoboo
fun initPodoboo(X: Int) {
    val enemyIntervalTimer by MemoryByteIndexed(EnemyIntervalTimer)
    val enemyState by MemoryByteIndexed(Enemy_State)
    val enemyYHighpos by MemoryByteIndexed(Enemy_Y_HighPos)
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    //> InitPodoboo:
    //> lda #$02                  ;set enemy position to below
    //> sta Enemy_Y_HighPos,x     ;the bottom of the screen
    enemyYHighpos[X] = 0x02
    //> sta Enemy_Y_Position,x
    enemyYPosition[X] = 0x02
    //> lsr
    //> sta EnemyIntervalTimer,x  ;set timer for enemy
    enemyIntervalTimer[X] = 0x02 shr 1
    //> lsr
    //> sta Enemy_State,x         ;initialize enemy state, then jump to use
    enemyState[X] = 0x02 shr 1 shr 1
    //> jmp SmallBBox             ;$09 as bounding box size and set other things
}

// Decompiled from InitNormalEnemy
fun initNormalEnemy(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var primaryHardMode by MemoryByte(PrimaryHardMode)
    val enemyBoundboxctrl by MemoryByteIndexed(Enemy_BoundBoxCtrl)
    val enemyMovingdir by MemoryByteIndexed(Enemy_MovingDir)
    val enemyXSpeed by MemoryByteIndexed(Enemy_X_Speed)
    val normalXSpdData by MemoryByteIndexed(NormalXSpdData)
    //> InitNormalEnemy:
    //> ldy #$01              ;load offset of 1 by default
    //> lda PrimaryHardMode   ;check for primary hard mode flag set
    //> bne GetESpd
    temp0 = primaryHardMode
    temp1 = 0x01
    if (primaryHardMode == 0) {
        //> dey                   ;if not set, decrement offset
        temp1 = (temp1 - 1) and 0xFF
    }
    //> GetESpd: lda NormalXSpdData,y  ;get appropriate horizontal speed
    temp0 = normalXSpdData[temp1]
    //> SetESpd: sta Enemy_X_Speed,x   ;store as speed for enemy object
    enemyXSpeed[X] = temp0
    //> jmp TallBBox          ;branch to set bounding box control and other data
    //> TallBBox: lda #$03                    ;set specific bounding box size control
    temp0 = 0x03
    //> SetBBox:  sta Enemy_BoundBoxCtrl,x    ;set bounding box control here
    enemyBoundboxctrl[X] = temp0
    //> lda #$02                    ;set moving direction for left
    temp0 = 0x02
    //> sta Enemy_MovingDir,x
    enemyMovingdir[X] = temp0
}

// Decompiled from InitHorizFlySwimEnemy
fun initHorizFlySwimEnemy(X: Int) {
    var A: Int = 0
    val enemyBoundboxctrl by MemoryByteIndexed(Enemy_BoundBoxCtrl)
    val enemyMovingdir by MemoryByteIndexed(Enemy_MovingDir)
    val enemyXSpeed by MemoryByteIndexed(Enemy_X_Speed)
    //> SetESpd: sta Enemy_X_Speed,x   ;store as speed for enemy object
    enemyXSpeed[X] = A
    //> jmp TallBBox          ;branch to set bounding box control and other data
    // (Unreachable code removed: InitHorizFlySwimEnemy)
    //> TallBBox: lda #$03                    ;set specific bounding box size control
    //> SetBBox:  sta Enemy_BoundBoxCtrl,x    ;set bounding box control here
    enemyBoundboxctrl[X] = 0x03
    //> lda #$02                    ;set moving direction for left
    //> sta Enemy_MovingDir,x
    enemyMovingdir[X] = 0x02
}

// Decompiled from SmallBBox
fun smallBBox(X: Int): Int {
    var temp0: Int = 0
    var temp1: Int = 0
    val enemyBoundboxctrl by MemoryByteIndexed(Enemy_BoundBoxCtrl)
    val enemyMovingdir by MemoryByteIndexed(Enemy_MovingDir)
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    val redPTroopaCenterYPos by MemoryByteIndexed(RedPTroopaCenterYPos)
    val redPTroopaOrigXPos by MemoryByteIndexed(RedPTroopaOrigXPos)
    //> SmallBBox: lda #$09               ;set specific bounding box size control
    //> bne SetBBox            ;unconditional branch
    if (!(0x09 == 0)) {
        //  goto SetBBox
        return A
    }
    temp0 = 0x09
    if (0x09 == 0) {
        //> ;--------------------------------
        //> InitRedPTroopa:
        //> ldy #$30                    ;load central position adder for 48 pixels down
        //> lda Enemy_Y_Position,x      ;set vertical coordinate into location to
        temp0 = enemyYPosition[X]
        //> sta RedPTroopaOrigXPos,x    ;be used as original vertical coordinate
        redPTroopaOrigXPos[X] = temp0
        //> bpl GetCent                 ;if vertical coordinate < $80
        temp1 = 0x30
        if ((temp0 and 0x80) != 0) {
            //> ldy #$e0                    ;if => $80, load position adder for 32 pixels up
            temp1 = 0xE0
        }
        //> GetCent:  tya                         ;send central position adder to A
        //> adc Enemy_Y_Position,x      ;add to current vertical coordinate
        //> sta RedPTroopaCenterYPos,x  ;store as central vertical coordinate
        redPTroopaCenterYPos[X] = (temp1 + enemyYPosition[X]) and 0xFF
        //> TallBBox: lda #$03                    ;set specific bounding box size control
        temp0 = 0x03
    }
    //> SetBBox:  sta Enemy_BoundBoxCtrl,x    ;set bounding box control here
    enemyBoundboxctrl[X] = temp0
    //> lda #$02                    ;set moving direction for left
    temp0 = 0x02
    //> sta Enemy_MovingDir,x
    enemyMovingdir[X] = temp0
    return 0
}

// Decompiled from InitVStf
fun initVStf(X: Int): Int {
    val enemyYMoveforce by MemoryByteIndexed(Enemy_Y_MoveForce)
    val enemyYSpeed by MemoryByteIndexed(Enemy_Y_Speed)
    //> InitVStf: lda #$00                    ;initialize vertical speed
    //> sta Enemy_Y_Speed,x         ;and movement force
    enemyYSpeed[X] = 0x00
    //> sta Enemy_Y_MoveForce,x
    enemyYMoveforce[X] = 0x00
    //> rts
    return A
}

// Decompiled from SetupLakitu
fun setupLakitu(X: Int) {
    var lakituReappearTimer by MemoryByte(LakituReappearTimer)
    val enemyBoundboxctrl by MemoryByteIndexed(Enemy_BoundBoxCtrl)
    //> SetupLakitu:
    //> lda #$00                   ;erase counter for lakitu's reappearance
    //> sta LakituReappearTimer
    lakituReappearTimer = 0x00
    //> jsr InitHorizFlySwimEnemy  ;set $03 as bounding box, set other attributes
    initHorizFlySwimEnemy(X)
    //> jmp TallBBox2              ;set $03 as bounding box again (not necessary) and leave
    //> TallBBox2: lda #$03                  ;set specific value for bounding box control
    //> SetBBox2:  sta Enemy_BoundBoxCtrl,x  ;set bounding box control then leave
    enemyBoundboxctrl[X] = 0x03
    //> rts
    return
}

// Decompiled from DuplicateEnemyObj
fun duplicateEnemyObj(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var duplicateobjOffset by MemoryByte(DuplicateObj_Offset)
    val enemyFlag by MemoryByteIndexed(Enemy_Flag)
    val enemyPageloc by MemoryByteIndexed(Enemy_PageLoc)
    val enemyXPosition by MemoryByteIndexed(Enemy_X_Position)
    val enemyYHighpos by MemoryByteIndexed(Enemy_Y_HighPos)
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    //> DuplicateEnemyObj:
    //> ldy #$ff                ;start at beginning of enemy slots
    temp0 = 0xFF
    do {
        //> FSLoop: iny                     ;increment one slot
        temp0 = (temp0 + 1) and 0xFF
        //> lda Enemy_Flag,y        ;check enemy buffer flag for empty slot
        //> bne FSLoop              ;if set, branch and keep checking
    } while (enemyFlag[temp0] != 0)
    //> sty DuplicateObj_Offset ;otherwise set offset here
    duplicateobjOffset = temp0
    //> txa                     ;transfer original enemy buffer offset
    //> ora #%10000000          ;store with d7 set as flag in new enemy
    temp1 = X or 0x80
    //> sta Enemy_Flag,y        ;slot as well as enemy offset
    enemyFlag[temp0] = temp1
    //> lda Enemy_PageLoc,x
    //> sta Enemy_PageLoc,y     ;copy page location and horizontal coordinates
    enemyPageloc[temp0] = enemyPageloc[X]
    //> lda Enemy_X_Position,x  ;from original enemy to new enemy
    //> sta Enemy_X_Position,y
    enemyXPosition[temp0] = enemyXPosition[X]
    //> lda #$01
    //> sta Enemy_Flag,x        ;set flag as normal for original enemy
    enemyFlag[X] = 0x01
    //> sta Enemy_Y_HighPos,y   ;set high vertical byte for new enemy
    enemyYHighpos[temp0] = 0x01
    //> lda Enemy_Y_Position,x
    //> sta Enemy_Y_Position,y  ;copy vertical coordinate from original to new
    enemyYPosition[temp0] = enemyYPosition[X]
    //> FlmEx:  rts                     ;and then leave
    return
}

// Decompiled from PutAtRightExtent
fun putAtRightExtent(A: Int, X: Int): Int {
    var screenrightPageloc by MemoryByte(ScreenRight_PageLoc)
    var screenrightXPos by MemoryByte(ScreenRight_X_Pos)
    val enemyBoundboxctrl by MemoryByteIndexed(Enemy_BoundBoxCtrl)
    val enemyFlag by MemoryByteIndexed(Enemy_Flag)
    val enemyPageloc by MemoryByteIndexed(Enemy_PageLoc)
    val enemyState by MemoryByteIndexed(Enemy_State)
    val enemyXMoveforce by MemoryByteIndexed(Enemy_X_MoveForce)
    val enemyXPosition by MemoryByteIndexed(Enemy_X_Position)
    val enemyYHighpos by MemoryByteIndexed(Enemy_Y_HighPos)
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    //> PutAtRightExtent:
    //> sta Enemy_Y_Position,x    ;set vertical position
    enemyYPosition[X] = A
    //> lda ScreenRight_X_Pos
    //> clc
    //> adc #$20                  ;place enemy 32 pixels beyond right side of screen
    //> sta Enemy_X_Position,x
    enemyXPosition[X] = (screenrightXPos + 0x20) and 0xFF
    //> lda ScreenRight_PageLoc
    //> adc #$00                  ;add carry
    //> sta Enemy_PageLoc,x
    enemyPageloc[X] = (screenrightPageloc + (if (screenrightXPos + 0x20 > 0xFF) 1 else 0)) and 0xFF
    //> jmp FinishFlame           ;skip this part to finish setting values
    //> FinishFlame:
    //> lda #$08                 ;set $08 for bounding box control
    //> sta Enemy_BoundBoxCtrl,x
    enemyBoundboxctrl[X] = 0x08
    //> lda #$01                 ;set high byte of vertical and
    //> sta Enemy_Y_HighPos,x    ;enemy buffer flag
    enemyYHighpos[X] = 0x01
    //> sta Enemy_Flag,x
    enemyFlag[X] = 0x01
    //> lsr
    //> sta Enemy_X_MoveForce,x  ;initialize horizontal movement force, and
    enemyXMoveforce[X] = 0x01 shr 1
    //> sta Enemy_State,x        ;enemy state
    enemyState[X] = 0x01 shr 1
    //> rts
    return A
}

// Decompiled from InitPiranhaPlant
fun initPiranhaPlant(X: Int) {
    val enemyBoundboxctrl by MemoryByteIndexed(Enemy_BoundBoxCtrl)
    val enemyState by MemoryByteIndexed(Enemy_State)
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    val piranhaPlantDownYPos by MemoryByteIndexed(PiranhaPlantDownYPos)
    val piranhaPlantUpYPos by MemoryByteIndexed(PiranhaPlantUpYPos)
    val piranhaplantMoveflag by MemoryByteIndexed(PiranhaPlant_MoveFlag)
    val piranhaplantYSpeed by MemoryByteIndexed(PiranhaPlant_Y_Speed)
    //> InitPiranhaPlant:
    //> lda #$01                     ;set initial speed
    //> sta PiranhaPlant_Y_Speed,x
    piranhaplantYSpeed[X] = 0x01
    //> lsr
    //> sta Enemy_State,x            ;initialize enemy state and what would normally
    enemyState[X] = 0x01 shr 1
    //> sta PiranhaPlant_MoveFlag,x  ;be used as vertical speed, but not in this case
    piranhaplantMoveflag[X] = 0x01 shr 1
    //> lda Enemy_Y_Position,x
    //> sta PiranhaPlantDownYPos,x   ;save original vertical coordinate here
    piranhaPlantDownYPos[X] = enemyYPosition[X]
    //> sec
    //> sbc #$18
    //> sta PiranhaPlantUpYPos,x     ;save original vertical coordinate - 24 pixels here
    piranhaPlantUpYPos[X] = (enemyYPosition[X] - 0x18) and 0xFF
    //> lda #$09
    //> jmp SetBBox2                 ;set specific value for bounding box control
    //> SetBBox2:  sta Enemy_BoundBoxCtrl,x  ;set bounding box control then leave
    enemyBoundboxctrl[X] = 0x09
    //> rts
    return
}

// Decompiled from PlatLiftUp
fun platLiftUp(X: Int) {
    val enemyBoundboxctrl by MemoryByteIndexed(Enemy_BoundBoxCtrl)
    val enemyYMoveforce by MemoryByteIndexed(Enemy_Y_MoveForce)
    val enemyYSpeed by MemoryByteIndexed(Enemy_Y_Speed)
    //> PlatLiftUp:
    //> lda #$10                 ;set movement amount here
    //> sta Enemy_Y_MoveForce,x
    enemyYMoveforce[X] = 0x10
    //> lda #$ff                 ;set moving speed for platforms going up
    //> sta Enemy_Y_Speed,x
    enemyYSpeed[X] = 0xFF
    //> jmp CommonSmallLift      ;skip ahead to part we should be executing
    //> CommonSmallLift:
    //> ldy #$01
    //> jsr PosPlatform           ;do a sub to add 12 pixels due to preset value
    posPlatform(X, 0x01)
    //> lda #$04
    //> sta Enemy_BoundBoxCtrl,x  ;set bounding box control for small platforms
    enemyBoundboxctrl[X] = 0x04
    //> rts
    return
}

// Decompiled from PlatLiftDown
fun platLiftDown(X: Int) {
    val enemyBoundboxctrl by MemoryByteIndexed(Enemy_BoundBoxCtrl)
    val enemyYMoveforce by MemoryByteIndexed(Enemy_Y_MoveForce)
    val enemyYSpeed by MemoryByteIndexed(Enemy_Y_Speed)
    //> PlatLiftDown:
    //> lda #$f0                 ;set movement amount here
    //> sta Enemy_Y_MoveForce,x
    enemyYMoveforce[X] = 0xF0
    //> lda #$00                 ;set moving speed for platforms going down
    //> sta Enemy_Y_Speed,x
    enemyYSpeed[X] = 0x00
    //> ;--------------------------------
    //> CommonSmallLift:
    //> ldy #$01
    //> jsr PosPlatform           ;do a sub to add 12 pixels due to preset value
    posPlatform(X, 0x01)
    //> lda #$04
    //> sta Enemy_BoundBoxCtrl,x  ;set bounding box control for small platforms
    enemyBoundboxctrl[X] = 0x04
    //> rts
    return
}

// Decompiled from PosPlatform
fun posPlatform(X: Int, Y: Int) {
    val enemyPageloc by MemoryByteIndexed(Enemy_PageLoc)
    val enemyXPosition by MemoryByteIndexed(Enemy_X_Position)
    val platPosDataHigh by MemoryByteIndexed(PlatPosDataHigh)
    val platPosDataLow by MemoryByteIndexed(PlatPosDataLow)
    //> PosPlatform:
    //> lda Enemy_X_Position,x  ;get horizontal coordinate
    //> clc
    //> adc PlatPosDataLow,y    ;add or subtract pixels depending on offset
    //> sta Enemy_X_Position,x  ;store as new horizontal coordinate
    enemyXPosition[X] = (enemyXPosition[X] + platPosDataLow[Y]) and 0xFF
    //> lda Enemy_PageLoc,x
    //> adc PlatPosDataHigh,y   ;add or subtract page location depending on offset
    //> sta Enemy_PageLoc,x     ;store as new page location
    enemyPageloc[X] = (enemyPageloc[X] + platPosDataHigh[Y] + (if (enemyXPosition[X] + platPosDataLow[Y] > 0xFF) 1 else 0)) and 0xFF
    //> rts                     ;and go back
    return
}

// Decompiled from RunRetainerObj
fun runRetainerObj() {
    var X: Int = 0
    //> RunRetainerObj:
    //> jsr GetEnemyOffscreenBits
    getEnemyOffscreenBits(X)
    //> jsr RelativeEnemyPosition
    relativeEnemyPosition()
    //> jmp EnemyGfxHandler
}

// Decompiled from EnemyMovementSubs
fun enemyMovementSubs(X: Int) {
    val enemyId by MemoryByteIndexed(Enemy_ID)
    //> EnemyMovementSubs:
    //> lda Enemy_ID,x
    //> jsr JumpEngine
    when (enemyId[X]) {
        0 -> {
            moveNormalEnemy(X)
        }
        1 -> {
            moveNormalEnemy(X)
        }
        2 -> {
            moveNormalEnemy(X)
        }
        3 -> {
            moveNormalEnemy(X)
        }
        4 -> {
            moveNormalEnemy(X)
        }
        5 -> {
            procHammerBro()
        }
        6 -> {
            moveNormalEnemy(X)
        }
        7 -> {
            moveBloober()
        }
        8 -> {
            moveBulletBill()
        }
        9 -> {
            noMoveCode()
        }
        10 -> {
            moveSwimmingCheepCheep()
        }
        11 -> {
            moveSwimmingCheepCheep()
        }
        12 -> {
            movePodoboo()
        }
        13 -> {
            movePiranhaPlant()
        }
        14 -> {
            moveJumpingEnemy()
        }
        15 -> {
            procMoveRedPTroopa()
        }
        16 -> {
            moveFlyGreenPTroopa()
        }
        17 -> {
            moveLakitu()
        }
        18 -> {
            moveNormalEnemy(X)
        }
        19 -> {
            noMoveCode()
        }
        20 -> {
            moveFlyingCheepCheep()
        }
        else -> {
            // Unknown JumpEngine index
        }
    }
    return
}

// Decompiled from LargePlatformSubroutines
fun largePlatformSubroutines(X: Int) {
    val enemyId by MemoryByteIndexed(Enemy_ID)
    //> LargePlatformSubroutines:
    //> lda Enemy_ID,x  ;subtract $24 to get proper offset for jump table
    //> sec
    //> sbc #$24
    //> jsr JumpEngine
    when ((enemyId[X] - 0x24) and 0xFF) {
        0 -> {
            balancePlatform()
        }
        1 -> {
            yMovingPlatform()
        }
        2 -> {
            moveLargeLiftPlat()
        }
        3 -> {
            moveLargeLiftPlat()
        }
        4 -> {
            xMovingPlatform()
        }
        5 -> {
            dropPlatform()
        }
        6 -> {
            rightPlatform()
        }
        else -> {
            // Unknown JumpEngine index
        }
    }
    return
}

// Decompiled from EraseEnemyObject
fun eraseEnemyObject(X: Int) {
    val enemyFrameTimer by MemoryByteIndexed(EnemyFrameTimer)
    val enemyIntervalTimer by MemoryByteIndexed(EnemyIntervalTimer)
    val enemyFlag by MemoryByteIndexed(Enemy_Flag)
    val enemyId by MemoryByteIndexed(Enemy_ID)
    val enemySprattrib by MemoryByteIndexed(Enemy_SprAttrib)
    val enemyState by MemoryByteIndexed(Enemy_State)
    val floateynumControl by MemoryByteIndexed(FloateyNum_Control)
    val shellChainCounter by MemoryByteIndexed(ShellChainCounter)
    //> EraseEnemyObject:
    //> lda #$00                 ;clear all enemy object variables
    //> sta Enemy_Flag,x
    enemyFlag[X] = 0x00
    //> sta Enemy_ID,x
    enemyId[X] = 0x00
    //> sta Enemy_State,x
    enemyState[X] = 0x00
    //> sta FloateyNum_Control,x
    floateynumControl[X] = 0x00
    //> sta EnemyIntervalTimer,x
    enemyIntervalTimer[X] = 0x00
    //> sta ShellChainCounter,x
    shellChainCounter[X] = 0x00
    //> sta Enemy_SprAttrib,x
    enemySprattrib[X] = 0x00
    //> sta EnemyFrameTimer,x
    enemyFrameTimer[X] = 0x00
    //> rts
    return
}

// Decompiled from MoveNormalEnemy
fun moveNormalEnemy(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var temp5: Int = 0
    val enemyId by MemoryByteIndexed(Enemy_ID)
    val enemyState by MemoryByteIndexed(Enemy_State)
    val enemyXSpeed by MemoryByteIndexed(Enemy_X_Speed)
    val xSpeedAdderData by MemoryByteIndexed(XSpeedAdderData)
    //> MoveNormalEnemy:
    //> ldy #$00                   ;init Y to leave horizontal movement as-is
    //> lda Enemy_State,x
    //> and #%01000000             ;check enemy state for d6 set, if set skip
    temp0 = enemyState[X] and 0x40
    //> bne FallE                  ;to move enemy vertically, then horizontally if necessary
    temp1 = temp0
    temp2 = 0x00
    if (temp0 == 0) {
        //> lda Enemy_State,x
        temp1 = enemyState[X]
        //> asl                        ;check enemy state for d7 set
        temp1 = (temp1 shl 1) and 0xFF
        //> bcs SteadM                 ;if set, branch to move enemy horizontally
        if ((temp1 and 0x80) == 0) {
            //> lda Enemy_State,x
            temp1 = enemyState[X]
            //> and #%00100000             ;check enemy state for d5 set
            temp3 = temp1 and 0x20
            //> bne MoveDefeatedEnemy      ;if set, branch to move defeated enemy object
            if (!(temp3 == 0)) {
                //  goto MoveDefeatedEnemy
                return
            }
            temp1 = temp3
            if (temp3 == 0) {
                //> lda Enemy_State,x
                temp1 = enemyState[X]
                //> and #%00000111             ;check d2-d0 of enemy state for any set bits
                temp4 = temp1 and 0x07
                //> beq SteadM                 ;if enemy in normal state, branch to move enemy horizontally
                temp1 = temp4
                if (temp4 != 0) {
                    //> cmp #$05
                    //> beq FallE                  ;if enemy in state used by spiny's egg, go ahead here
                    if (temp1 != 0x05) {
                        //> cmp #$03
                        //> bcs ReviveStunned          ;if enemy in states $03 or $04, skip ahead to yet another part
                        if (!(temp1 >= 0x03)) {
                        }
                    }
                }
            }
        }
    }
    //> FallE: jsr MoveD_EnemyVertically  ;do a sub here to move enemy downwards
    movedEnemyvertically(X)
    //> ldy #$00
    temp2 = 0x00
    //> lda Enemy_State,x          ;check for enemy state $02
    temp1 = enemyState[X]
    //> cmp #$02
    //> beq MEHor                  ;if found, branch to move enemy horizontally
    if (temp1 - 0x02 == 0) {
        //  goto MEHor
        return
    }
    if (temp1 != 0x02) {
        //> and #%01000000             ;check for d6 set
        temp5 = temp1 and 0x40
        //> beq SteadM                 ;if not set, branch to something else
        temp1 = temp5
        if (temp5 != 0) {
            //> lda Enemy_ID,x
            temp1 = enemyId[X]
            //> cmp #PowerUpObject         ;check for power-up object
            //> beq SteadM
            if (temp1 != PowerUpObject) {
                //> bne SlowM                  ;if any other object where d6 set, jump to set Y
                if (temp1 == PowerUpObject) {
                }
            }
        }
    }
    //> MEHor: jmp MoveEnemyHorizontally  ;jump here to move enemy horizontally for <> $2e and d6 set
    //> SlowM:  ldy #$01                  ;if branched here, increment Y to slow horizontal movement
    temp2 = 0x01
    //> SteadM: lda Enemy_X_Speed,x       ;get current horizontal speed
    temp1 = enemyXSpeed[X]
    //> pha                       ;save to stack
    push(temp1)
    //> bpl AddHS                 ;if not moving or moving right, skip, leave Y alone
    if ((temp1 and 0x80) != 0) {
        //> iny
        temp2 = (temp2 + 1) and 0xFF
        //> iny                       ;otherwise increment Y to next data
        temp2 = (temp2 + 1) and 0xFF
    }
    //> AddHS:  clc
    //> adc XSpeedAdderData,y     ;add value here to slow enemy down if necessary
    //> sta Enemy_X_Speed,x       ;save as horizontal speed temporarily
    enemyXSpeed[X] = (temp1 + xSpeedAdderData[temp2]) and 0xFF
    //> jsr MoveEnemyHorizontally ;then do a sub to move horizontally
    moveEnemyHorizontally(X)
    //> pla
    temp1 = pull()
    //> sta Enemy_X_Speed,x       ;get old horizontal speed from stack and return to
    enemyXSpeed[X] = temp1
    //> rts                       ;original memory location, then leave
    return
}

// Decompiled from MoveJumpingEnemy
fun moveJumpingEnemy() {
    //> MoveJumpingEnemy:
    //> jsr MoveJ_EnemyVertically  ;do a sub to impose gravity on green paratroopa
    movejEnemyvertically()
    //> jmp MoveEnemyHorizontally  ;jump to move enemy horizontally
}

// Decompiled from XMoveCntr_GreenPTroopa
fun xmovecntrGreenptroopa() {
    //> XMoveCntr_GreenPTroopa:
    //> lda #$13                    ;load preset maximum value for secondary counter
}

// Decompiled from XMoveCntr_Platform
fun xmovecntrPlatform(A: Int, X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var frameCounter by MemoryByte(FrameCounter)
    val xMovePrimaryCounter by MemoryByteIndexed(XMovePrimaryCounter)
    val xMoveSecondaryCounter by MemoryByteIndexed(XMoveSecondaryCounter)
    //> XMoveCntr_Platform:
    //> sta $01                     ;store value here
    memory[0x1] = A.toUByte()
    //> lda FrameCounter
    //> and #%00000011              ;branch to leave if not on
    temp0 = frameCounter and 0x03
    //> bne NoIncXM                 ;every fourth frame
    temp1 = temp0
    if (temp0 == 0) {
        //> ldy XMoveSecondaryCounter,x ;get secondary counter
        //> lda XMovePrimaryCounter,x   ;get primary counter
        temp1 = xMovePrimaryCounter[X]
        //> lsr
        temp1 = temp1 shr 1
        //> bcs DecSeXM                 ;if d0 of primary counter set, branch elsewhere
        temp2 = xMoveSecondaryCounter[X]
        if ((temp1 and 0x01) == 0) {
            //> cpy $01                     ;compare secondary counter to preset maximum value
            //> beq IncPXM                  ;if equal, branch ahead of this part
            if (temp2 != memory[0x1].toInt()) {
                //> inc XMoveSecondaryCounter,x ;increment secondary counter and leave
                xMoveSecondaryCounter[X] = (xMoveSecondaryCounter[X] + 1) and 0xFF
            } else {
                //> IncPXM:  inc XMovePrimaryCounter,x   ;increment primary counter and leave
                xMovePrimaryCounter[X] = (xMovePrimaryCounter[X] + 1) and 0xFF
                //> rts
                return
            }
        }
    }
    //> NoIncXM: rts
    return
}

// Decompiled from MoveWithXMCntrs
fun moveWithXMCntrs(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    val enemyMovingdir by MemoryByteIndexed(Enemy_MovingDir)
    val xMovePrimaryCounter by MemoryByteIndexed(XMovePrimaryCounter)
    val xMoveSecondaryCounter by MemoryByteIndexed(XMoveSecondaryCounter)
    //> MoveWithXMCntrs:
    //> lda XMoveSecondaryCounter,x  ;save secondary counter to stack
    //> pha
    push(xMoveSecondaryCounter[X])
    //> ldy #$01                     ;set value here by default
    //> lda XMovePrimaryCounter,x
    //> and #%00000010               ;if d1 of primary counter is
    temp0 = xMovePrimaryCounter[X] and 0x02
    //> bne XMRight                  ;set, branch ahead of this part here
    temp1 = temp0
    temp2 = 0x01
    if (temp0 == 0) {
        //> lda XMoveSecondaryCounter,x
        temp1 = xMoveSecondaryCounter[X]
        //> eor #$ff                     ;otherwise change secondary
        temp3 = temp1 xor 0xFF
        //> clc                          ;counter to two's compliment
        //> adc #$01
        //> sta XMoveSecondaryCounter,x
        xMoveSecondaryCounter[X] = (temp3 + 0x01) and 0xFF
        //> ldy #$02                     ;load alternate value here
        temp2 = 0x02
    }
    //> XMRight: sty Enemy_MovingDir,x        ;store as moving direction
    enemyMovingdir[X] = temp2
    //> jsr MoveEnemyHorizontally
    moveEnemyHorizontally(X)
    //> sta $00                      ;save value obtained from sub here
    memory[0x0] = temp1.toUByte()
    //> pla                          ;get secondary counter from stack
    temp1 = pull()
    //> sta XMoveSecondaryCounter,x  ;and return to original place
    xMoveSecondaryCounter[X] = temp1
    //> rts
    return
}

// Decompiled from ProcSwimmingB
fun procSwimmingB(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var frameCounter by MemoryByte(FrameCounter)
    var playerYPosition by MemoryByte(Player_Y_Position)
    val blooperMoveCounter by MemoryByteIndexed(BlooperMoveCounter)
    val blooperMoveSpeed by MemoryByteIndexed(BlooperMoveSpeed)
    val enemyIntervalTimer by MemoryByteIndexed(EnemyIntervalTimer)
    val enemyYMoveforce by MemoryByteIndexed(Enemy_Y_MoveForce)
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    //> ProcSwimmingB:
    //> lda BlooperMoveCounter,x  ;get enemy's movement counter
    //> and #%00000010            ;check for d1 set
    temp0 = blooperMoveCounter[X] and 0x02
    //> bne ChkForFloatdown       ;branch if set
    temp1 = temp0
    if (temp0 == 0) {
        //> lda FrameCounter
        temp1 = frameCounter
        //> and #%00000111            ;get 3 LSB of frame counter
        temp2 = temp1 and 0x07
        //> pha                       ;and save it to the stack
        push(temp2)
        //> lda BlooperMoveCounter,x  ;get enemy's movement counter
        temp1 = blooperMoveCounter[X]
        //> lsr                       ;check for d0 set
        temp1 = temp1 shr 1
        //> bcs SlowSwim              ;branch if set
        if ((temp1 and 0x01) == 0) {
            //> pla                       ;pull 3 LSB of frame counter from the stack
            temp1 = pull()
            //> bne BSwimE                ;branch to leave, execute code only every eighth frame
            if (temp1 == 0) {
                //> lda Enemy_Y_MoveForce,x
                temp1 = enemyYMoveforce[X]
                //> clc                       ;add to movement force to speed up swim
                //> adc #$01
                //> sta Enemy_Y_MoveForce,x   ;set movement force
                enemyYMoveforce[X] = (temp1 + 0x01) and 0xFF
                //> sta BlooperMoveSpeed,x    ;set as movement speed
                blooperMoveSpeed[X] = (temp1 + 0x01) and 0xFF
                //> cmp #$02
                //> bne BSwimE                ;if certain horizontal speed, branch to leave
                temp1 = (temp1 + 0x01) and 0xFF
                if (((temp1 + 0x01) and 0xFF) == 0x02) {
                    //> inc BlooperMoveCounter,x  ;otherwise increment movement counter
                    blooperMoveCounter[X] = (blooperMoveCounter[X] + 1) and 0xFF
                }
            }
            //> BSwimE: rts
            return
        }
        //> SlowSwim:
        //> pla                      ;pull 3 LSB of frame counter from the stack
        temp1 = pull()
        //> bne NoSSw                ;branch to leave, execute code only every eighth frame
        if (temp1 == 0) {
            //> lda Enemy_Y_MoveForce,x
            temp1 = enemyYMoveforce[X]
            //> sec                      ;subtract from movement force to slow swim
            //> sbc #$01
            //> sta Enemy_Y_MoveForce,x  ;set movement force
            enemyYMoveforce[X] = (temp1 - 0x01) and 0xFF
            //> sta BlooperMoveSpeed,x   ;set as movement speed
            blooperMoveSpeed[X] = (temp1 - 0x01) and 0xFF
            //> bne NoSSw                ;if any speed, branch to leave
            temp1 = (temp1 - 0x01) and 0xFF
            if (((temp1 - 0x01) and 0xFF) == 0) {
                //> inc BlooperMoveCounter,x ;otherwise increment movement counter
                blooperMoveCounter[X] = (blooperMoveCounter[X] + 1) and 0xFF
                //> lda #$02
                temp1 = 0x02
                //> sta EnemyIntervalTimer,x ;set enemy's timer
                enemyIntervalTimer[X] = temp1
            }
        }
        //> NoSSw: rts                      ;leave
        return
    } else {
        //> ChkForFloatdown:
        //> lda EnemyIntervalTimer,x ;get enemy timer
        temp1 = enemyIntervalTimer[X]
        //> beq ChkNearPlayer        ;branch if expired
        if (temp1 != 0) {
            //> Floatdown:
            //> lda FrameCounter        ;get frame counter
            temp1 = frameCounter
            //> lsr                     ;check for d0 set
            temp1 = temp1 shr 1
            //> bcs NoFD                ;branch to leave on every other frame
            if ((temp1 and 0x01) == 0) {
                //> inc Enemy_Y_Position,x  ;otherwise increment vertical coordinate
                enemyYPosition[X] = (enemyYPosition[X] + 1) and 0xFF
            }
            //> NoFD: rts                     ;leave
            return
        }
    }
    do {
        //> ChkNearPlayer:
        //> lda Enemy_Y_Position,x    ;get vertical coordinate
        temp1 = enemyYPosition[X]
        //> adc #$10                  ;add sixteen pixels
        //> cmp Player_Y_Position     ;compare result with player's vertical coordinate
        //> bcc Floatdown             ;if modified vertical less than player's, branch
    } while (!(((temp1 + 0x10) and 0xFF) >= playerYPosition))
    //> lda #$00
    temp1 = 0x00
    //> sta BlooperMoveCounter,x  ;otherwise nullify movement counter
    blooperMoveCounter[X] = temp1
    //> rts
    return
}

// Decompiled from ProcFirebar
fun procFirebar(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var duplicateobjOffset by MemoryByte(DuplicateObj_Offset)
    var enemyOffscreenbits by MemoryByte(Enemy_OffscreenBits)
    var enemyRelXpos by MemoryByte(Enemy_Rel_XPos)
    var enemyRelYpos by MemoryByte(Enemy_Rel_YPos)
    var timerControl by MemoryByte(TimerControl)
    val enemyId by MemoryByteIndexed(Enemy_ID)
    val enemySprdataoffset by MemoryByteIndexed(Enemy_SprDataOffset)
    val firebarSpinSpeed by MemoryByteIndexed(FirebarSpinSpeed)
    val firebarspinstateHigh by MemoryByteIndexed(FirebarSpinState_High)
    val spriteXPosition by MemoryByteIndexed(Sprite_X_Position)
    val spriteYPosition by MemoryByteIndexed(Sprite_Y_Position)
    //> ProcFirebar:
    //> jsr GetEnemyOffscreenBits   ;get offscreen information
    getEnemyOffscreenBits(X)
    //> lda Enemy_OffscreenBits     ;check for d3 set
    //> and #%00001000              ;if so, branch to leave
    temp0 = enemyOffscreenbits and 0x08
    //> bne SkipFBar
    temp1 = temp0
    if (temp0 == 0) {
        //> lda TimerControl            ;if master timer control set, branch
        temp1 = timerControl
        //> bne SusFbar                 ;ahead of this part
        if (temp1 == 0) {
            //> lda FirebarSpinSpeed,x      ;load spinning speed of firebar
            temp1 = firebarSpinSpeed[X]
            //> jsr FirebarSpin             ;modify current spinstate
            firebarSpin(temp1, X)
            //> and #%00011111              ;mask out all but 5 LSB
            temp2 = temp1 and 0x1F
            //> sta FirebarSpinState_High,x ;and store as new high byte of spinstate
            firebarspinstateHigh[X] = temp2
        }
        //> SusFbar:  lda FirebarSpinState_High,x ;get high byte of spinstate
        temp1 = firebarspinstateHigh[X]
        //> ldy Enemy_ID,x              ;check enemy identifier
        //> cpy #$1f
        //> bcc SetupGFB                ;if < $1f (long firebar), branch
        temp3 = enemyId[X]
        if (enemyId[X] >= 0x1F) {
            //> cmp #$08                    ;check high byte of spinstate
            //> beq SkpFSte                 ;if eight, branch to change
            if (temp1 != 0x08) {
                //> cmp #$18
                //> bne SetupGFB                ;if not at twenty-four branch to not change
                if (temp1 == 0x18) {
                }
            }
            //> SkpFSte:  clc
            //> adc #$01                    ;add one to spinning thing to avoid horizontal state
            //> sta FirebarSpinState_High,x
            firebarspinstateHigh[X] = (temp1 + 0x01) and 0xFF
        }
        //> SetupGFB: sta $ef                     ;save high byte of spinning thing, modified or otherwise
        memory[0xEF] = temp1.toUByte()
        //> jsr RelativeEnemyPosition   ;get relative coordinates to screen
        relativeEnemyPosition()
        //> jsr GetFirebarPosition      ;do a sub here (residual, too early to be used now)
        getFirebarPosition(temp1)
        //> ldy Enemy_SprDataOffset,x   ;get OAM data offset
        temp3 = enemySprdataoffset[X]
        //> lda Enemy_Rel_YPos          ;get relative vertical coordinate
        temp1 = enemyRelYpos
        //> sta Sprite_Y_Position,y     ;store as Y in OAM data
        spriteYPosition[temp3] = temp1
        //> sta $07                     ;also save here
        memory[0x7] = temp1.toUByte()
        //> lda Enemy_Rel_XPos          ;get relative horizontal coordinate
        temp1 = enemyRelXpos
        //> sta Sprite_X_Position,y     ;store as X in OAM data
        spriteXPosition[temp3] = temp1
        //> sta $06                     ;also save here
        memory[0x6] = temp1.toUByte()
        //> lda #$01
        temp1 = 0x01
        //> sta $00                     ;set $01 value here (not necessary)
        memory[0x0] = temp1.toUByte()
        //> jsr FirebarCollision        ;draw fireball part and do collision detection
        firebarCollision(temp3)
        //> ldy #$05                    ;load value for short firebars by default
        temp3 = 0x05
        //> lda Enemy_ID,x
        temp1 = enemyId[X]
        //> cmp #$1f                    ;are we doing a long firebar?
        //> bcc SetMFbar                ;no, branch then
        if (temp1 >= 0x1F) {
            //> ldy #$0b                    ;otherwise load value for long firebars
            temp3 = 0x0B
        }
        //> SetMFbar: sty $ed                     ;store maximum value for length of firebars
        memory[0xED] = temp3.toUByte()
        //> lda #$00
        temp1 = 0x00
        //> sta $00                     ;initialize counter here
        memory[0x0] = temp1.toUByte()
        while (!(temp1 >= memory[0xED].toInt())) {
            //> ldy DuplicateObj_Offset     ;if we arrive at fifth firebar part,
            temp3 = duplicateobjOffset
            //> lda Enemy_SprDataOffset,y   ;get offset from long firebar and load OAM data offset
            temp1 = enemySprdataoffset[temp3]
            //> sta $06                     ;using long firebar offset, then store as new one here
            memory[0x6] = temp1.toUByte()
            do {
                //> DrawFbar: lda $ef                     ;load high byte of spinstate
                temp1 = memory[0xEF].toInt()
                //> jsr GetFirebarPosition      ;get fireball position data depending on firebar part
                getFirebarPosition(temp1)
                //> jsr DrawFirebar_Collision   ;position it properly, draw it and do collision detection
                drawfirebarCollision()
                //> lda $00                     ;check which firebar part
                temp1 = memory[0x0].toInt()
                //> cmp #$04
                //> bne NextFbar
                //> NextFbar: inc $00                     ;move onto the next firebar part
                memory[0x0] = ((memory[0x0].toInt() + 1) and 0xFF).toUByte()
                //> lda $00
                temp1 = memory[0x0].toInt()
                //> cmp $ed                     ;if we end up at the maximum part, go on and leave
                //> bcc DrawFbar                ;otherwise go back and do another
            } while (!(temp1 >= memory[0xED].toInt()))
        }
    }
    //> SkipFBar: rts
    return
}

// Decompiled from DrawFirebar_Collision
fun drawfirebarCollision() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var enemyRelXpos by MemoryByte(Enemy_Rel_XPos)
    var enemyRelYpos by MemoryByte(Enemy_Rel_YPos)
    val spriteXPosition by MemoryByteIndexed(Sprite_X_Position)
    val spriteYPosition by MemoryByteIndexed(Sprite_Y_Position)
    //> DrawFirebar_Collision:
    //> lda $03                  ;store mirror data elsewhere
    //> sta $05
    memory[0x5] = memory[0x3].toInt().toUByte()
    //> ldy $06                  ;load OAM data offset for firebar
    //> lda $01                  ;load horizontal adder we got from position loader
    //> lsr $05                  ;shift LSB of mirror data
    memory[0x5] = ((memory[0x5].toInt() shr 1) and 0xFF).toUByte()
    //> bcs AddHA                ;if carry was set, skip this part
    temp0 = memory[0x1].toInt()
    temp1 = memory[0x6].toInt()
    if ((memory[0x5].toInt() and 0x01) == 0) {
        //> eor #$ff
        temp2 = temp0 xor 0xFF
        //> adc #$01                 ;otherwise get two's compliment of horizontal adder
    }
    //> AddHA:   clc                      ;add horizontal coordinate relative to screen to
    //> adc Enemy_Rel_XPos       ;horizontal adder, modified or otherwise
    //> sta Sprite_X_Position,y  ;store as X coordinate here
    spriteXPosition[temp1] = (temp0 + enemyRelXpos) and 0xFF
    //> sta $06                  ;store here for now, note offset is saved in Y still
    memory[0x6] = ((temp0 + enemyRelXpos) and 0xFF).toUByte()
    //> cmp Enemy_Rel_XPos       ;compare X coordinate of sprite to original X of firebar
    //> bcs SubtR1               ;if sprite coordinate => original coordinate, branch
    temp0 = (temp0 + enemyRelXpos) and 0xFF
    if (!(((temp0 + enemyRelXpos) and 0xFF) >= enemyRelXpos)) {
        //> lda Enemy_Rel_XPos
        temp0 = enemyRelXpos
        //> sec                      ;otherwise subtract sprite X from the
        //> sbc $06                  ;original one and skip this part
        //> jmp ChkFOfs
    } else {
        //> SubtR1:  sec                      ;subtract original X from the
        //> sbc Enemy_Rel_XPos       ;current sprite X
    }
    //> ChkFOfs: cmp #$59                 ;if difference of coordinates within a certain range,
    //> bcc VAHandl              ;continue by handling vertical adder
    if (temp0 >= 0x59) {
        //> lda #$f8                 ;otherwise, load offscreen Y coordinate
        temp0 = 0xF8
        //> bne SetVFbr              ;and unconditionally branch to move sprite offscreen
        if (temp0 == 0) {
        }
    }
    //> VAHandl: lda Enemy_Rel_YPos       ;if vertical relative coordinate offscreen,
    temp0 = enemyRelYpos
    //> cmp #$f8                 ;skip ahead of this part and write into sprite Y coordinate
    //> beq SetVFbr
    if (temp0 != 0xF8) {
        //> lda $02                  ;load vertical adder we got from position loader
        temp0 = memory[0x2].toInt()
        //> lsr $05                  ;shift LSB of mirror data one more time
        memory[0x5] = ((memory[0x5].toInt() shr 1) and 0xFF).toUByte()
        //> bcs AddVA                ;if carry was set, skip this part
        if ((memory[0x5].toInt() and 0x01) == 0) {
            //> eor #$ff
            temp3 = temp0 xor 0xFF
            //> adc #$01                 ;otherwise get two's compliment of second part
        }
        //> AddVA:   clc                      ;add vertical coordinate relative to screen to
        //> adc Enemy_Rel_YPos       ;the second data, modified or otherwise
    }
    //> SetVFbr: sta Sprite_Y_Position,y  ;store as Y coordinate here
    spriteYPosition[temp1] = temp0
    //> sta $07                  ;also store here for now
    memory[0x7] = temp0.toUByte()
}

// Decompiled from FirebarCollision
fun firebarCollision(Y: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var temp5: Int = 0
    var crouchingFlag by MemoryByte(CrouchingFlag)
    var enemyMovingdir by MemoryByte(Enemy_MovingDir)
    var objectOffset by MemoryByte(ObjectOffset)
    var playerSize by MemoryByte(PlayerSize)
    var playerYHighpos by MemoryByte(Player_Y_HighPos)
    var playerYPosition by MemoryByte(Player_Y_Position)
    var starInvincibleTimer by MemoryByte(StarInvincibleTimer)
    var timerControl by MemoryByte(TimerControl)
    val firebarYPos by MemoryByteIndexed(FirebarYPos)
    val spriteXPosition by MemoryByteIndexed(Sprite_X_Position)
    //> FirebarCollision:
    //> jsr DrawFirebar          ;run sub here to draw current tile of firebar
    drawFirebar(Y)
    //> tya                      ;return OAM data offset and save
    //> pha                      ;to the stack for now
    push(Y)
    //> lda StarInvincibleTimer  ;if star mario invincibility timer
    //> ora TimerControl         ;or master timer controls set
    temp0 = starInvincibleTimer or timerControl
    //> bne NoColFB              ;then skip all of this
    temp1 = temp0
    if (temp0 == 0) {
        //> sta $05                  ;otherwise initialize counter
        memory[0x5] = temp1.toUByte()
        //> ldy Player_Y_HighPos
        //> dey                      ;if player's vertical high byte offscreen,
        playerYHighpos = (playerYHighpos - 1) and 0xFF
        //> bne NoColFB              ;skip all of this
        temp2 = playerYHighpos
        if (playerYHighpos == 0) {
            //> ldy Player_Y_Position    ;get player's vertical position
            temp2 = playerYPosition
            //> lda PlayerSize           ;get player's size
            temp1 = playerSize
            //> bne AdjSm                ;if player small, branch to alter variables
            if (temp1 == 0) {
                //> lda CrouchingFlag
                temp1 = crouchingFlag
                //> beq BigJp                ;if player big and not crouching, jump ahead
                if (temp1 != 0) {
                }
            }
            //> AdjSm:   inc $05                  ;if small or big but crouching, execute this part
            memory[0x5] = ((memory[0x5].toInt() + 1) and 0xFF).toUByte()
            //> inc $05                  ;first increment our counter twice (setting $02 as flag)
            memory[0x5] = ((memory[0x5].toInt() + 1) and 0xFF).toUByte()
            //> tya
            //> clc                      ;then add 24 pixels to the player's
            //> adc #$18                 ;vertical coordinate
            //> tay
            //> BigJp:   tya                      ;get vertical coordinate, altered or otherwise, from Y
            temp2 = (temp2 + 0x18) and 0xFF
            // BigJp / ChkVFBD loop - should execute once per call then break
            run {  // Using run block instead of while(true) since this is checked per frame
                //> eor #$ff                 ;skip two's compliment part
                temp3 = (temp2 + 0x18) and 0xFF xor 0xFF
                //> clc                      ;otherwise get two's compliment
                //> adc #$01
                //> ChkVFBD: cmp #$08                 ;if difference => 8 pixels, skip ahead of this part
                //> bcs Chk2Ofs
                temp1 = (temp3 + 0x01) and 0xFF
                if (!(((temp3 + 0x01) and 0xFF) >= 0x08)) {
                    //> lda $06                  ;if firebar on far right on the screen, skip this,
                    temp1 = memory[0x6].toInt()
                    //> cmp #$f0                 ;because, really, what's the point?
                    //> bcs Chk2Ofs
                    if (!(temp1 >= 0xF0)) {
                        //> lda Sprite_X_Position+4  ;get OAM X coordinate for sprite #1
                        temp1 = spriteXPosition[4]
                        //> clc
                        //> adc #$04                 ;add four pixels
                        //> sta $04                  ;store here
                        memory[0x4] = ((temp1 + 0x04) and 0xFF).toUByte()
                        //> sec                      ;subtract horizontal coordinate of firebar
                        //> sbc $06                  ;from the X coordinate of player's sprite 1
                        //> bpl ChkFBCl              ;if modded X coordinate to the right of firebar
                        temp1 = (((temp1 + 0x04) and 0xFF) - memory[0x6].toInt()) and 0xFF
                        if (((((temp1 + 0x04) and 0xFF) - memory[0x6].toInt()) and 0xFF and 0x80) != 0) {
                            //> eor #$ff                 ;skip two's compliment part
                            temp4 = temp1 xor 0xFF
                            //> clc                      ;otherwise get two's compliment
                            //> adc #$01
                        }
                        //> ChkFBCl: cmp #$08                 ;if difference < 8 pixels, collision, thus branch
                        //> bcc ChgSDir              ;to process
                        if (temp1 >= 0x08) {
                        }
                    }
                }
                //> Chk2Ofs: lda $05                  ;if value of $02 was set earlier for whatever reason,
                temp1 = memory[0x5].toInt()
                //> cmp #$02                 ;branch to increment OAM offset and leave, no collision
                //> beq NoColFB
                if (temp1 != 0x02) {
                    // FBCLoop: Loop to check collision at different Y offsets
                    // Breaks when (temp1 - $07) is positive (player lower than firebar)
                    var loopCount = 0
                    while (loopCount < 10) {  // Safety limit
                        //> FBCLoop: sec                      ;subtract vertical position of firebar
                        //> sbc $07                  ;from the vertical coordinate of the player
                        val diff = (temp1 - memory[0x7].toInt()) and 0xFF
                        //> bpl ChkVFBD              ;if player lower on the screen than firebar,
                        if ((diff and 0x80) == 0) break  // Positive = break to outer loop
                        //> ldy $05                  ;otherwise get temp here and use as offset
                        temp2 = memory[0x5].toInt()
                        //> lda Player_Y_Position
                        temp1 = playerYPosition
                        //> clc
                        //> adc FirebarYPos,y        ;add value loaded with offset to player's vertical coordinate
                        //> inc $05                  ;then increment temp and jump back
                        memory[0x5] = ((memory[0x5].toInt() + 1) and 0xFF).toUByte()
                        loopCount++
                        //> jmp FBCLoop
                    }
                } else {
                    //> NoColFB: pla                      ;get OAM data offset
                    temp1 = pull()
                    //> clc                      ;add four to it and save
                    //> adc #$04
                    //> sta $06
                    memory[0x6] = ((temp1 + 0x04) and 0xFF).toUByte()
                    //> ldx ObjectOffset         ;get enemy object buffer offset and leave
                    //> rts
                    return
                }
            }
            //> ChgSDir: ldx #$01                 ;set movement direction by default
            //> lda $04                  ;if OAM X coordinate of player's sprite 1
            temp1 = memory[0x4].toInt()
            //> cmp $06                  ;is greater than horizontal coordinate of firebar
            //> bcs SetSDir              ;then do not alter movement direction
            temp5 = 0x01
            if (!(temp1 >= memory[0x6].toInt())) {
                //> inx                      ;otherwise increment it
                temp5 = (temp5 + 1) and 0xFF
            }
            //> SetSDir: stx Enemy_MovingDir      ;store movement direction here
            enemyMovingdir = temp5
            //> ldx #$00
            temp5 = 0x00
            //> lda $00                  ;save value written to $00 to stack
            temp1 = memory[0x0].toInt()
            //> pha
            push(temp1)
            //> jsr InjurePlayer         ;perform sub to hurt or kill player
            injurePlayer()
            //> pla
            temp1 = pull()
            //> sta $00                  ;get value of $00 from stack
            memory[0x0] = temp1.toUByte()
        }
    }
}

// Decompiled from GetFirebarPosition
fun getFirebarPosition(A: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var temp5: Int = 0
    val firebarMirrorData by MemoryByteIndexed(FirebarMirrorData)
    val firebarPosLookupTbl by MemoryByteIndexed(FirebarPosLookupTbl)
    val firebarTblOffsets by MemoryByteIndexed(FirebarTblOffsets)
    //> GetFirebarPosition:
    //> pha                        ;save high byte of spinstate to the stack
    push(A)
    //> and #%00001111             ;mask out low nybble
    temp0 = A and 0x0F
    //> cmp #$09
    //> bcc GetHAdder              ;if lower than $09, branch ahead
    temp1 = temp0
    if (temp0 >= 0x09) {
        //> eor #%00001111             ;otherwise get two's compliment to oscillate
        temp2 = temp1 xor 0x0F
        //> clc
        //> adc #$01
    }
    //> GetHAdder: sta $01                    ;store result, modified or not, here
    memory[0x1] = temp1.toUByte()
    //> ldy $00                    ;load number of firebar ball where we're at
    //> lda FirebarTblOffsets,y    ;load offset to firebar position data
    temp1 = firebarTblOffsets[memory[0x0].toInt()]
    //> clc
    //> adc $01                    ;add oscillated high byte of spinstate
    //> tay                        ;to offset here and use as new offset
    //> lda FirebarPosLookupTbl,y  ;get data here and store as horizontal adder
    temp1 = firebarPosLookupTbl[(temp1 + memory[0x1].toInt()) and 0xFF]
    //> sta $01
    memory[0x1] = temp1.toUByte()
    //> pla                        ;pull whatever was in A from the stack
    temp1 = pull()
    //> pha                        ;save it again because we still need it
    push(temp1)
    //> clc
    //> adc #$08                   ;add eight this time, to get vertical adder
    //> and #%00001111             ;mask out high nybble
    temp3 = (temp1 + 0x08) and 0xFF and 0x0F
    //> cmp #$09                   ;if lower than $09, branch ahead
    //> bcc GetVAdder
    temp1 = temp3
    temp4 = (temp1 + memory[0x1].toInt()) and 0xFF
    if (temp3 >= 0x09) {
        //> eor #%00001111             ;otherwise get two's compliment
        temp5 = temp1 xor 0x0F
        //> clc
        //> adc #$01
    }
    //> GetVAdder: sta $02                    ;store result here
    memory[0x2] = temp1.toUByte()
    //> ldy $00
    temp4 = memory[0x0].toInt()
    //> lda FirebarTblOffsets,y    ;load offset to firebar position data again
    temp1 = firebarTblOffsets[temp4]
    //> clc
    //> adc $02                    ;this time add value in $02 to offset here and use as offset
    //> tay
    //> lda FirebarPosLookupTbl,y  ;get data here and store as vertica adder
    temp1 = firebarPosLookupTbl[(temp1 + memory[0x2].toInt()) and 0xFF]
    //> sta $02
    memory[0x2] = temp1.toUByte()
    //> pla                        ;pull out whatever was in A one last time
    temp1 = pull()
    //> lsr                        ;divide by eight or shift three to the right
    temp1 = temp1 shr 1
    //> lsr
    temp1 = temp1 shr 1
    //> lsr
    temp1 = temp1 shr 1
    //> tay                        ;use as offset
    //> lda FirebarMirrorData,y    ;load mirroring data here
    temp1 = firebarMirrorData[temp1]
    //> sta $03                    ;store
    memory[0x3] = temp1.toUByte()
    //> rts
    return
}

// Decompiled from PlayerLakituDiff
fun playerLakituDiff(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var playerXSpeed by MemoryByte(Player_X_Speed)
    var scrollAmount by MemoryByte(ScrollAmount)
    val enemyId by MemoryByteIndexed(Enemy_ID)
    val enemyYSpeed by MemoryByteIndexed(Enemy_Y_Speed)
    val lakituMoveDirection by MemoryByteIndexed(LakituMoveDirection)
    val lakituMoveSpeed by MemoryByteIndexed(LakituMoveSpeed)
    //> PlayerLakituDiff:
    //> ldy #$00                   ;set Y for default value
    //> jsr PlayerEnemyDiff        ;get horizontal difference between enemy and player
    playerEnemyDiff(X)
    //> bpl ChkLakDif              ;branch if enemy is to the right of the player
    temp0 = 0x00
    if ((0x00 and 0x80) != 0) {
        //> iny                        ;increment Y for left of player
        temp0 = (temp0 + 1) and 0xFF
        //> lda $00
        //> eor #$ff                   ;get two's compliment of low byte of horizontal difference
        temp1 = memory[0x0].toInt() xor 0xFF
        //> clc
        //> adc #$01                   ;store two's compliment as horizontal difference
        //> sta $00
        memory[0x0] = ((temp1 + 0x01) and 0xFF).toUByte()
    }
    //> ChkLakDif: lda $00                    ;get low byte of horizontal difference
    //> cmp #$3c                   ;if within a certain distance of player, branch
    //> bcc ChkPSpeed
    temp2 = memory[0x0].toInt()
    if (memory[0x0].toInt() >= 0x3C) {
        //> lda #$3c                   ;otherwise set maximum distance
        temp2 = 0x3C
        //> sta $00
        memory[0x0] = temp2.toUByte()
        //> lda Enemy_ID,x             ;check if lakitu is in our current enemy slot
        temp2 = enemyId[X]
        //> cmp #Lakitu
        //> bne ChkPSpeed              ;if not, branch elsewhere
        if (temp2 == Lakitu) {
            //> tya                        ;compare contents of Y, now in A
            //> cmp LakituMoveDirection,x  ;to what is being used as horizontal movement direction
            //> beq ChkPSpeed              ;if moving toward the player, branch, do not alter
            temp2 = temp0
            if (temp0 != lakituMoveDirection[X]) {
                //> lda LakituMoveDirection,x  ;if moving to the left beyond maximum distance,
                temp2 = lakituMoveDirection[X]
                //> beq SetLMovD               ;branch and alter without delay
                if (temp2 != 0) {
                    //> dec LakituMoveSpeed,x      ;decrement horizontal speed
                    lakituMoveSpeed[X] = (lakituMoveSpeed[X] - 1) and 0xFF
                    //> lda LakituMoveSpeed,x      ;if horizontal speed not yet at zero, branch to leave
                    temp2 = lakituMoveSpeed[X]
                    //> bne ExMoveLak
                    if (temp2 == 0) {
                    } else {
                        //> ExMoveLak: rts                        ;leave!!!
                        return
                    }
                }
                //> SetLMovD:  tya                        ;set horizontal direction depending on horizontal
                //> sta LakituMoveDirection,x  ;difference between enemy and player if necessary
                lakituMoveDirection[X] = temp0
            }
        }
    }
    //> ChkPSpeed: lda $00
    temp2 = memory[0x0].toInt()
    //> and #%00111100             ;mask out all but four bits in the middle
    temp3 = temp2 and 0x3C
    //> lsr                        ;divide masked difference by four
    temp3 = temp3 shr 1
    //> lsr
    temp3 = temp3 shr 1
    //> sta $00                    ;store as new value
    memory[0x0] = temp3.toUByte()
    //> ldy #$00                   ;init offset
    temp0 = 0x00
    //> lda Player_X_Speed
    temp2 = playerXSpeed
    //> beq SubDifAdj              ;if player not moving horizontally, branch
    if (temp2 != 0) {
        //> lda ScrollAmount
        temp2 = scrollAmount
        //> beq SubDifAdj              ;if scroll speed not set, branch to same place
        if (temp2 != 0) {
            //> iny                        ;otherwise increment offset
            temp0 = (temp0 + 1) and 0xFF
            //> lda Player_X_Speed
            temp2 = playerXSpeed
            //> cmp #$19                   ;if player not running, branch
            //> bcc ChkSpinyO
            if (temp2 >= 0x19) {
                //> lda ScrollAmount
                temp2 = scrollAmount
                //> cmp #$02                   ;if scroll speed below a certain amount, branch
                //> bcc ChkSpinyO              ;to same place
                if (temp2 >= 0x02) {
                    //> iny                        ;otherwise increment once more
                    temp0 = (temp0 + 1) and 0xFF
                }
            }
            //> ChkSpinyO: lda Enemy_ID,x             ;check for spiny object
            temp2 = enemyId[X]
            //> cmp #Spiny
            //> bne ChkEmySpd              ;branch if not found
            if (temp2 == Spiny) {
                //> lda Player_X_Speed         ;if player not moving, skip this part
                temp2 = playerXSpeed
                //> bne SubDifAdj
                if (temp2 == 0) {
                }
            }
            //> ChkEmySpd: lda Enemy_Y_Speed,x        ;check vertical speed
            temp2 = enemyYSpeed[X]
            //> bne SubDifAdj              ;branch if nonzero
            if (temp2 == 0) {
                //> ldy #$00                   ;otherwise reinit offset
                temp0 = 0x00
            }
        }
    }
    //> SubDifAdj: lda $0001,y                ;get one of three saved values from earlier
    temp2 = memory[0x1 + temp0].toInt()
    //> ldy $00                    ;get saved horizontal difference
    temp0 = memory[0x0].toInt()
    do {
        //> SPixelLak: sec                        ;subtract one for each pixel of horizontal difference
        //> sbc #$01                   ;from one of three saved values
        //> dey
        temp0 = (temp0 - 1) and 0xFF
        //> bpl SPixelLak              ;branch until all pixels are subtracted, to adjust difference
    } while ((temp0 and 0x80) == 0)
}

// Decompiled from KillAllEnemies
fun killAllEnemies(A: Int) {
    var temp0: Int = 0
    var enemyFrenzyBuffer by MemoryByte(EnemyFrenzyBuffer)
    var objectOffset by MemoryByte(ObjectOffset)
    //> KillAllEnemies:
    //> ldx #$04              ;start with last enemy slot
    temp0 = 0x04
    do {
        //> KillLoop: jsr EraseEnemyObject  ;branch to kill enemy objects
        eraseEnemyObject(temp0)
        //> dex                   ;move onto next enemy slot
        temp0 = (temp0 - 1) and 0xFF
        //> bpl KillLoop          ;do this until all slots are emptied
    } while ((temp0 and 0x80) == 0)
    //> sta EnemyFrenzyBuffer ;empty frenzy buffer
    enemyFrenzyBuffer = A
    //> ldx ObjectOffset      ;get enemy object offset and leave
    temp0 = objectOffset
    //> rts
    return
}

// Decompiled from ProcessBowserHalf
fun processBowserHalf(X: Int) {
    //> ExBGfxH:  rts                      ;leave!
    return
}

// Decompiled from SetFlameTimer
fun setFlameTimer(): Int {
    var temp0: Int = 0
    var bowserFlameTimerCtrl by MemoryByte(BowserFlameTimerCtrl)
    val flameTimerData by MemoryByteIndexed(FlameTimerData)
    //> SetFlameTimer:
    //> ldy BowserFlameTimerCtrl  ;load counter as offset
    //> inc BowserFlameTimerCtrl  ;increment
    bowserFlameTimerCtrl = (bowserFlameTimerCtrl + 1) and 0xFF
    //> lda BowserFlameTimerCtrl  ;mask out all but 3 LSB
    //> and #%00000111            ;to keep in range of 0-7
    temp0 = bowserFlameTimerCtrl and 0x07
    //> sta BowserFlameTimerCtrl
    bowserFlameTimerCtrl = temp0
    //> lda FlameTimerData,y      ;load value to be used then leave
    //> ExFl: rts
    return A
}

// Decompiled from ProcBowserFlame
fun procBowserFlame(X: Int) {
    //> ExFl: rts
    return
}

// Decompiled from DrawStarFlag
fun drawStarFlag(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var enemyRelXpos by MemoryByte(Enemy_Rel_XPos)
    var enemyRelYpos by MemoryByte(Enemy_Rel_YPos)
    var objectOffset by MemoryByte(ObjectOffset)
    val enemySprdataoffset by MemoryByteIndexed(Enemy_SprDataOffset)
    val spriteAttributes by MemoryByteIndexed(Sprite_Attributes)
    val spriteTilenumber by MemoryByteIndexed(Sprite_Tilenumber)
    val spriteXPosition by MemoryByteIndexed(Sprite_X_Position)
    val spriteYPosition by MemoryByteIndexed(Sprite_Y_Position)
    val starFlagTileData by MemoryByteIndexed(StarFlagTileData)
    val starFlagXPosAdder by MemoryByteIndexed(StarFlagXPosAdder)
    val starFlagYPosAdder by MemoryByteIndexed(StarFlagYPosAdder)
    //> DrawStarFlag:
    //> jsr RelativeEnemyPosition  ;get relative coordinates of star flag
    relativeEnemyPosition()
    //> ldy Enemy_SprDataOffset,x  ;get OAM data offset
    //> ldx #$03                   ;do four sprites
    temp0 = 0x03
    temp1 = enemySprdataoffset[X]
    do {
        //> DSFLoop: lda Enemy_Rel_YPos         ;get relative vertical coordinate
        //> clc
        //> adc StarFlagYPosAdder,x    ;add Y coordinate adder data
        //> sta Sprite_Y_Position,y    ;store as Y coordinate
        spriteYPosition[temp1] = (enemyRelYpos + starFlagYPosAdder[temp0]) and 0xFF
        //> lda StarFlagTileData,x     ;get tile number
        //> sta Sprite_Tilenumber,y    ;store as tile number
        spriteTilenumber[temp1] = starFlagTileData[temp0]
        //> lda #$22                   ;set palette and background priority bits
        //> sta Sprite_Attributes,y    ;store as attributes
        spriteAttributes[temp1] = 0x22
        //> lda Enemy_Rel_XPos         ;get relative horizontal coordinate
        //> clc
        //> adc StarFlagXPosAdder,x    ;add X coordinate adder data
        //> sta Sprite_X_Position,y    ;store as X coordinate
        spriteXPosition[temp1] = (enemyRelXpos + starFlagXPosAdder[temp0]) and 0xFF
        //> iny
        temp1 = (temp1 + 1) and 0xFF
        //> iny                        ;increment OAM data offset four bytes
        temp1 = (temp1 + 1) and 0xFF
        //> iny                        ;for next sprite
        temp1 = (temp1 + 1) and 0xFF
        //> iny
        temp1 = (temp1 + 1) and 0xFF
        //> dex                        ;move onto next sprite
        temp0 = (temp0 - 1) and 0xFF
        //> bpl DSFLoop                ;do this until all sprites are done
    } while ((temp0 and 0x80) == 0)
    //> ldx ObjectOffset           ;get enemy object offset and leave
    temp0 = objectOffset
    //> rts
    return
}

// Decompiled from FirebarSpin
fun firebarSpin(A: Int, X: Int): Int {
    var temp0: Int = 0
    val firebarSpinDirection by MemoryByteIndexed(FirebarSpinDirection)
    val firebarspinstateHigh by MemoryByteIndexed(FirebarSpinState_High)
    val firebarspinstateLow by MemoryByteIndexed(FirebarSpinState_Low)
    //> FirebarSpin:
    //> sta $07                     ;save spinning speed here
    memory[0x7] = A.toUByte()
    //> lda FirebarSpinDirection,x  ;check spinning direction
    //> bne SpinCounterClockwise    ;if moving counter-clockwise, branch to other part
    temp0 = firebarSpinDirection[X]
    if (firebarSpinDirection[X] == 0) {
        //> ldy #$18                    ;possibly residual ldy
        //> lda FirebarSpinState_Low,x
        temp0 = firebarspinstateLow[X]
        //> clc                         ;add spinning speed to what would normally be
        //> adc $07                     ;the horizontal speed
        //> sta FirebarSpinState_Low,x
        firebarspinstateLow[X] = (temp0 + memory[0x7].toInt()) and 0xFF
        //> lda FirebarSpinState_High,x ;add carry to what would normally be the vertical speed
        temp0 = firebarspinstateHigh[X]
        //> adc #$00
        //> rts
        return A
    } else {
        //> SpinCounterClockwise:
        //> ldy #$08                    ;possibly residual ldy
        //> lda FirebarSpinState_Low,x
        temp0 = firebarspinstateLow[X]
        //> sec                         ;subtract spinning speed to what would normally be
        //> sbc $07                     ;the horizontal speed
        //> sta FirebarSpinState_Low,x
        firebarspinstateLow[X] = (temp0 - memory[0x7].toInt()) and 0xFF
        //> lda FirebarSpinState_High,x ;add carry to what would normally be the vertical speed
        temp0 = firebarspinstateHigh[X]
        //> sbc #$00
        //> rts
        return A
    }
}

// Decompiled from SetupPlatformRope
fun setupPlatformRope(A: Int, Y: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var temp5: Int = 0
    var temp6: Int = 0
    var temp7: Int = 0
    var temp8: Int = 0
    var secondaryHardMode by MemoryByte(SecondaryHardMode)
    var vramBuffer1Offset by MemoryByte(VRAM_Buffer1_Offset)
    val enemyPageloc by MemoryByteIndexed(Enemy_PageLoc)
    val enemyXPosition by MemoryByteIndexed(Enemy_X_Position)
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    //> SetupPlatformRope:
    //> pha                     ;save second/third copy to stack
    push(A)
    //> lda Enemy_X_Position,y  ;get horizontal coordinate
    //> clc
    //> adc #$08                ;add eight pixels
    //> ldx SecondaryHardMode   ;if secondary hard mode flag set,
    //> bne GetLRp              ;use coordinate as-is
    temp0 = (enemyXPosition[Y] + 0x08) and 0xFF
    temp1 = secondaryHardMode
    if (secondaryHardMode == 0) {
        //> clc
        //> adc #$10                ;otherwise add sixteen more pixels
    }
    //> GetLRp: pha                     ;save modified horizontal coordinate to stack
    push(temp0)
    //> lda Enemy_PageLoc,y
    temp0 = enemyPageloc[Y]
    //> adc #$00                ;add carry to page location
    //> sta $02                 ;and save here
    memory[0x2] = ((temp0) and 0xFF).toUByte()
    //> pla                     ;pull modified horizontal coordinate
    temp0 = pull()
    //> and #%11110000          ;from the stack, mask out low nybble
    temp2 = temp0 and 0xF0
    //> lsr                     ;and shift three bits to the right
    temp2 = temp2 shr 1
    //> lsr
    temp2 = temp2 shr 1
    //> lsr
    temp2 = temp2 shr 1
    //> sta $00                 ;store result here as part of name table low byte
    memory[0x0] = temp2.toUByte()
    //> ldx Enemy_Y_Position,y  ;get vertical coordinate
    temp1 = enemyYPosition[Y]
    //> pla                     ;get second/third copy of vertical speed from stack
    temp0 = pull()
    //> bpl GetHRp              ;skip this part if moving downwards or not at all
    if ((temp0 and 0x80) != 0) {
        //> txa
        //> clc
        //> adc #$08                ;add eight to vertical coordinate and
        //> tax                     ;save as X
    }
    //> GetHRp: txa                     ;move vertical coordinate to A
    //> ldx VRAM_Buffer1_Offset ;get vram buffer offset
    temp1 = vramBuffer1Offset
    //> asl
    temp1 = (temp1 shl 1) and 0xFF
    //> rol                     ;rotate d7 to d0 and d6 into carry
    temp1 = (temp1 shl 1) and 0xFE or if ((temp1 and 0x80) != 0) 1 else 0
    //> pha                     ;save modified vertical coordinate to stack
    push(temp1)
    //> rol                     ;rotate carry to d0, thus d7 and d6 are at 2 LSB
    temp1 = (temp1 shl 1) and 0xFE or if ((temp1 and 0x80) != 0) 1 else 0
    //> and #%00000011          ;mask out all bits but d7 and d6, then set
    temp3 = temp1 and 0x03
    //> ora #%00100000          ;d5 to get appropriate high byte of name table
    temp4 = temp3 or 0x20
    //> sta $01                 ;address, then store
    memory[0x1] = temp4.toUByte()
    //> lda $02                 ;get saved page location from earlier
    temp0 = memory[0x2].toInt()
    //> and #$01                ;mask out all but LSB
    temp5 = temp0 and 0x01
    //> asl
    temp5 = (temp5 shl 1) and 0xFF
    //> asl                     ;shift twice to the left and save with the
    temp5 = (temp5 shl 1) and 0xFF
    //> ora $01                 ;rest of the bits of the high byte, to get
    temp6 = temp5 or memory[0x1].toInt()
    //> sta $01                 ;the proper name table and the right place on it
    memory[0x1] = temp6.toUByte()
    //> pla                     ;get modified vertical coordinate from stack
    temp0 = pull()
    //> and #%11100000          ;mask out low nybble and LSB of high nybble
    temp7 = temp0 and 0xE0
    //> clc
    //> adc $00                 ;add to horizontal part saved here
    //> sta $00                 ;save as name table low byte
    memory[0x0] = ((temp7 + memory[0x0].toInt()) and 0xFF).toUByte()
    //> lda Enemy_Y_Position,y
    temp0 = enemyYPosition[Y]
    //> cmp #$e8                ;if vertical position not below the
    //> bcc ExPRp               ;bottom of the screen, we're done, branch to leave
    if (temp0 >= 0xE8) {
        //> lda $00
        temp0 = memory[0x0].toInt()
        //> and #%10111111          ;mask out d6 of low byte of name table address
        temp8 = temp0 and 0xBF
        //> sta $00
        memory[0x0] = temp8.toUByte()
    }
    //> ExPRp:  rts                     ;leave!
    return
}

// Decompiled from StopPlatforms
fun stopPlatforms(A: Int, Y: Int) {
    var X: Int = 0
    val enemyYMoveforce by MemoryByteIndexed(Enemy_Y_MoveForce)
    val enemyYSpeed by MemoryByteIndexed(Enemy_Y_Speed)
    //> StopPlatforms:
    //> jsr InitVStf             ;initialize vertical speed and low byte
    initVStf(X)
    //> sta Enemy_Y_Speed,y      ;for both platforms and leave
    enemyYSpeed[Y] = A
    //> sta Enemy_Y_MoveForce,y
    enemyYMoveforce[Y] = A
    //> rts
    return
}

// Decompiled from PositionPlayerOnHPlat
fun positionPlayerOnHPlat() {
    var X: Int = 0
    var temp0: Int = 0
    var temp1: Int = 0
    var platformXScroll by MemoryByte(Platform_X_Scroll)
    var playerPageloc by MemoryByte(Player_PageLoc)
    var playerXPosition by MemoryByte(Player_X_Position)
    //> PositionPlayerOnHPlat:
    //> lda Player_X_Position
    //> clc                       ;add saved value from second subroutine to
    //> adc $00                   ;current player's position to position
    //> sta Player_X_Position     ;player accordingly in horizontal position
    playerXPosition = (playerXPosition + memory[0x0].toInt()) and 0xFF
    //> lda Player_PageLoc        ;get player's page location
    //> ldy $00                   ;check to see if saved value here is positive or negative
    //> bmi PPHSubt               ;if negative, branch to subtract
    temp0 = playerPageloc
    temp1 = memory[0x0].toInt()
    if ((memory[0x0].toInt() and 0x80) == 0) {
        //> adc #$00                  ;otherwise add carry to page location
        //> jmp SetPVar               ;jump to skip subtraction
    } else {
        //> PPHSubt: sbc #$00                  ;subtract borrow from page location
    }
    //> SetPVar: sta Player_PageLoc        ;save result to player's page location
    playerPageloc = temp0
    //> sty Platform_X_Scroll     ;put saved value from second sub here to be used later
    platformXScroll = temp1
    //> jsr PositionPlayerOnVPlat ;position player vertically and appropriately
    positionPlayerOnVPlat(X)
    //> ExXMP:   rts                       ;and we are done here
    return
}

// Decompiled from MoveSmallPlatform
fun moveSmallPlatform(X: Int) {
    var temp0: Int = 0
    val platformCollisionFlag by MemoryByteIndexed(PlatformCollisionFlag)
    //> MoveSmallPlatform:
    //> jsr MoveLiftPlatforms      ;execute common to all large and small lift platforms
    moveLiftPlatforms(X)
    //> jmp ChkSmallPlatCollision  ;branch to position player correctly
    //> ChkSmallPlatCollision:
    //> lda PlatformCollisionFlag,x ;get bounding box counter saved in collision flag
    //> beq ExLiftP                 ;if none found, leave player position alone
    temp0 = platformCollisionFlag[X]
    if (platformCollisionFlag[X] != 0) {
        //> jsr PositionPlayerOnS_Plat  ;use to position player correctly
        positionplayeronsPlat(temp0, X)
    }
    //> ExLiftP: rts                         ;then leave
    return
}

// Decompiled from MoveLiftPlatforms
fun moveLiftPlatforms(X: Int) {
    var temp0: Int = 0
    var timerControl by MemoryByte(TimerControl)
    val enemyYmfDummy by MemoryByteIndexed(Enemy_YMF_Dummy)
    val enemyYMoveforce by MemoryByteIndexed(Enemy_Y_MoveForce)
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    val enemyYSpeed by MemoryByteIndexed(Enemy_Y_Speed)
    //> MoveLiftPlatforms:
    //> lda TimerControl         ;if master timer control set, skip all of this
    //> bne ExLiftP              ;and branch to leave
    if (!(timerControl == 0)) {
        //  goto ExLiftP
        return
    }
    temp0 = timerControl
    if (timerControl == 0) {
        //> lda Enemy_YMF_Dummy,x
        temp0 = enemyYmfDummy[X]
        //> clc                      ;add contents of movement amount to whatever's here
        //> adc Enemy_Y_MoveForce,x
        //> sta Enemy_YMF_Dummy,x
        enemyYmfDummy[X] = (temp0 + enemyYMoveforce[X]) and 0xFF
        //> lda Enemy_Y_Position,x   ;add whatever vertical speed is set to current
        temp0 = enemyYPosition[X]
        //> adc Enemy_Y_Speed,x      ;vertical position plus carry to move up or down
        //> sta Enemy_Y_Position,x   ;and then leave
        enemyYPosition[X] = (temp0 + enemyYSpeed[X] + (if (temp0 + enemyYMoveforce[X] > 0xFF) 1 else 0)) and 0xFF
        //> rts
        return
    } else {
        //> ExLiftP: rts                         ;then leave
        return
    }
}

// Decompiled from OffscreenBoundsCheck
fun offscreenBoundsCheck(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var screenleftPageloc by MemoryByte(ScreenLeft_PageLoc)
    var screenleftXPos by MemoryByte(ScreenLeft_X_Pos)
    var screenrightPageloc by MemoryByte(ScreenRight_PageLoc)
    var screenrightXPos by MemoryByte(ScreenRight_X_Pos)
    val enemyId by MemoryByteIndexed(Enemy_ID)
    val enemyPageloc by MemoryByteIndexed(Enemy_PageLoc)
    val enemyState by MemoryByteIndexed(Enemy_State)
    val enemyXPosition by MemoryByteIndexed(Enemy_X_Position)
    //> OffscreenBoundsCheck:
    //> lda Enemy_ID,x          ;check for cheep-cheep object
    //> cmp #FlyingCheepCheep   ;branch to leave if found
    //> beq ExScrnBd
    temp0 = enemyId[X]
    if (enemyId[X] != FlyingCheepCheep) {
        //> lda ScreenLeft_X_Pos    ;get horizontal coordinate for left side of screen
        temp0 = screenleftXPos
        //> ldy Enemy_ID,x
        //> cpy #HammerBro          ;check for hammer bro object
        //> beq LimitB
        temp1 = enemyId[X]
        if (enemyId[X] != HammerBro) {
            //> cpy #PiranhaPlant       ;check for piranha plant object
            //> bne ExtendLB            ;these two will be erased sooner than others if too far left
            if (temp1 == PiranhaPlant) {
            }
        }
        //> LimitB:   adc #$38                ;add 56 pixels to coordinate if hammer bro or piranha plant
        //> ExtendLB: sbc #$48                ;subtract 72 pixels regardless of enemy object
        //> sta $01                 ;store result here
        memory[0x1] = ((((temp0 + 0x38) and 0xFF) - 0x48 - (if (temp0 + 0x38 > 0xFF) 0 else 1)) and 0xFF).toUByte()
        //> lda ScreenLeft_PageLoc
        temp0 = screenleftPageloc
        //> sbc #$00                ;subtract borrow from page location of left side
        //> sta $00                 ;store result here
        memory[0x0] = ((temp0 - (if (((temp0 + 0x38) and 0xFF) - 0x48 - (if (temp0 + 0x38 > 0xFF) 0 else 1) >= 0) 0 else 1)) and 0xFF).toUByte()
        //> lda ScreenRight_X_Pos   ;add 72 pixels to the right side horizontal coordinate
        temp0 = screenrightXPos
        //> adc #$48
        //> sta $03                 ;store result here
        memory[0x3] = ((temp0 + 0x48 + (if (temp0 - (if (((temp0 + 0x38) and 0xFF) - 0x48 - (if (temp0 + 0x38 > 0xFF) 0 else 1) >= 0) 0 else 1) >= 0) 1 else 0)) and 0xFF).toUByte()
        //> lda ScreenRight_PageLoc
        temp0 = screenrightPageloc
        //> adc #$00                ;then add the carry to the page location
        //> sta $02                 ;and store result here
        memory[0x2] = ((temp0 + (if (temp0 + 0x48 + (if (temp0 - (if (((temp0 + 0x38) and 0xFF) - 0x48 - (if (temp0 + 0x38 > 0xFF) 0 else 1) >= 0) 0 else 1) >= 0) 1 else 0) > 0xFF) 1 else 0)) and 0xFF).toUByte()
        //> lda Enemy_X_Position,x  ;compare horizontal coordinate of the enemy object
        temp0 = enemyXPosition[X]
        //> cmp $01                 ;to modified horizontal left edge coordinate to get carry
        //> lda Enemy_PageLoc,x
        temp0 = enemyPageloc[X]
        //> sbc $00                 ;then subtract it from the page coordinate of the enemy object
        //> bmi TooFar              ;if enemy object is too far left, branch to erase it
        temp0 = (temp0 - memory[0x0].toInt() - (if (temp0 >= memory[0x1].toInt()) 0 else 1)) and 0xFF
        if (((temp0 - memory[0x0].toInt() - if (temp0 >= memory[0x1].toInt()) 0 else 1) and 0xFF and 0x80) == 0) {
            //> lda Enemy_X_Position,x  ;compare horizontal coordinate of the enemy object
            temp0 = enemyXPosition[X]
            //> cmp $03                 ;to modified horizontal right edge coordinate to get carry
            //> lda Enemy_PageLoc,x
            temp0 = enemyPageloc[X]
            //> sbc $02                 ;then subtract it from the page coordinate of the enemy object
            //> bmi ExScrnBd            ;if enemy object is on the screen, leave, do not erase enemy
            temp0 = (temp0 - memory[0x2].toInt() - (if (temp0 >= memory[0x3].toInt()) 0 else 1)) and 0xFF
            if (((temp0 - memory[0x2].toInt() - if (temp0 >= memory[0x3].toInt()) 0 else 1) and 0xFF and 0x80) == 0) {
                //> lda Enemy_State,x       ;if at this point, enemy is offscreen to the right, so check
                temp0 = enemyState[X]
                //> cmp #HammerBro          ;if in state used by spiny's egg, do not erase
                //> beq ExScrnBd
                if (temp0 != HammerBro) {
                    //> cpy #PiranhaPlant       ;if piranha plant, do not erase
                    //> beq ExScrnBd
                    if (temp1 != PiranhaPlant) {
                        //> cpy #FlagpoleFlagObject ;if flagpole flag, do not erase
                        //> beq ExScrnBd
                        if (temp1 != FlagpoleFlagObject) {
                            //> cpy #StarFlagObject     ;if star flag, do not erase
                            //> beq ExScrnBd
                            if (temp1 != StarFlagObject) {
                                //> cpy #JumpspringObject   ;if jumpspring, do not erase
                                //> beq ExScrnBd            ;erase all others too far to the right
                                if (temp1 != JumpspringObject) {
                                } else {
                                    //> ExScrnBd: rts                     ;leave
                                    return
                                }
                            }
                        }
                    }
                }
            }
        }
        //> TooFar:   jsr EraseEnemyObject    ;erase object if necessary
        eraseEnemyObject(X)
    }
}

// Decompiled from FireballEnemyCollision
fun fireballEnemyCollision(X: Int) {
    var X: Int = X
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var frameCounter by MemoryByte(FrameCounter)
    var objectOffset by MemoryByte(ObjectOffset)
    val enemyOffscrBitsMasked by MemoryByteIndexed(EnemyOffscrBitsMasked)
    val enemyFlag by MemoryByteIndexed(Enemy_Flag)
    val enemyId by MemoryByteIndexed(Enemy_ID)
    val enemyState by MemoryByteIndexed(Enemy_State)
    val fireballState by MemoryByteIndexed(Fireball_State)
    //> FireballEnemyCollision:
    //> lda Fireball_State,x  ;check to see if fireball state is set at all
    //> beq ExitFBallEnemy    ;branch to leave if not
    temp0 = fireballState[X]
    if (fireballState[X] != 0) {
        //> asl
        temp0 = (temp0 shl 1) and 0xFF
        //> bcs ExitFBallEnemy    ;branch to leave also if d7 in state is set
        if ((temp0 and 0x80) == 0) {
            //> lda FrameCounter
            temp0 = frameCounter
            //> lsr                   ;get LSB of frame counter
            temp0 = temp0 shr 1
            //> bcs ExitFBallEnemy    ;branch to leave if set (do routine every other frame)
            if ((temp0 and 0x01) == 0) {
                //> txa
                //> asl                   ;multiply fireball offset by four
                X = (X shl 1) and 0xFF
                //> asl
                X = (X shl 1) and 0xFF
                //> clc
                //> adc #$1c              ;then add $1c or 28 bytes to it
                //> tay                   ;to use fireball's bounding box coordinates
                //> ldx #$04
                temp1 = 0x04
                temp2 = (X + 0x1C) and 0xFF
                while ((temp1 and 0x80) == 0) {
                    //> lda Enemy_Flag,x            ;check to see if buffer flag is set
                    temp0 = enemyFlag[temp1]
                    //> beq NoFToECol               ;if not, skip to next enemy slot
                    if (temp0 != 0) {
                        //> lda Enemy_ID,x              ;check enemy identifier
                        temp0 = enemyId[temp1]
                        //> cmp #$24
                        //> bcc GoombaDie               ;if < $24, branch to check further
                        if (temp0 >= 0x24) {
                            //> cmp #$2b
                            //> bcc NoFToECol               ;if in range $24-$2a, skip to next enemy slot
                            if (temp0 >= 0x2B) {
                            }
                        }
                        //> GoombaDie: cmp #Goomba                 ;check for goomba identifier
                        //> bne NotGoomba               ;if not found, continue with code
                        if (temp0 == Goomba) {
                            //> lda Enemy_State,x           ;otherwise check for defeated state
                            temp0 = enemyState[temp1]
                            //> cmp #$02                    ;if stomped or otherwise defeated,
                            //> bcs NoFToECol               ;skip to next enemy slot
                            if (!(temp0 >= 0x02)) {
                            }
                        }
                        //> NotGoomba: lda EnemyOffscrBitsMasked,x ;if any masked offscreen bits set,
                        temp0 = enemyOffscrBitsMasked[temp1]
                        //> bne NoFToECol               ;skip to next enemy slot
                        if (temp0 == 0) {
                            //> txa
                            //> asl                         ;otherwise multiply enemy offset by four
                            temp1 = (temp1 shl 1) and 0xFF
                            //> asl
                            temp1 = (temp1 shl 1) and 0xFF
                            //> clc
                            //> adc #$04                    ;add 4 bytes to it
                            //> tax                         ;to use enemy's bounding box coordinates
                            //> jsr SprObjectCollisionCore  ;do fireball-to-enemy collision detection
                            sprObjectCollisionCore((temp1 + 0x04) and 0xFF, temp2)
                            //> ldx ObjectOffset            ;return fireball's original offset
                            temp1 = objectOffset
                            //> bcc NoFToECol               ;if carry clear, no collision, thus do next enemy slot
                            temp0 = (temp1 + 0x04) and 0xFF
                            if (temp1 + 0x04 > 0xFF) {
                                //> lda #%10000000
                                temp0 = 0x80
                                //> sta Fireball_State,x        ;set d7 in enemy state
                                fireballState[temp1] = temp0
                                //> ldx $01                     ;get enemy offset
                                temp1 = memory[0x1].toInt()
                                //> jsr HandleEnemyFBallCol     ;jump to handle fireball to enemy collision
                                handleEnemyFBallCol()
                            }
                        }
                    }
                    do {
                        //> FireballEnemyCDLoop:
                        //> stx $01                     ;store enemy object offset here
                        memory[0x1] = temp1.toUByte()
                        //> tya
                        //> pha                         ;push fireball offset to the stack
                        push(temp2)
                        //> lda Enemy_State,x
                        temp0 = enemyState[temp1]
                        //> and #%00100000              ;check to see if d5 is set in enemy state
                        temp3 = temp0 and 0x20
                        //> bne NoFToECol               ;if so, skip to next enemy slot
                        //> NoFToECol: pla                         ;pull fireball offset from stack
                        temp0 = pull()
                        //> tay                         ;put it in Y
                        //> ldx $01                     ;get enemy object offset
                        temp1 = memory[0x1].toInt()
                        //> dex                         ;decrement it
                        temp1 = (temp1 - 1) and 0xFF
                        //> bpl FireballEnemyCDLoop     ;loop back until collision detection done on all enemies
                    } while ((temp1 and 0x80) == 0)
                }
            }
        }
    }
    //> ExitFBallEnemy:
    //> ldx ObjectOffset                 ;get original fireball offset and leave
    temp1 = objectOffset
    //> rts
    return
}

// Decompiled from HandleEnemyFBallCol
fun handleEnemyFBallCol() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var bowserHitPoints by MemoryByte(BowserHitPoints)
    var enemyFrenzyBuffer by MemoryByte(EnemyFrenzyBuffer)
    var square1SoundQueue by MemoryByte(Square1SoundQueue)
    var square2SoundQueue by MemoryByte(Square2SoundQueue)
    var worldNumber by MemoryByte(WorldNumber)
    val bowserIdentities by MemoryByteIndexed(BowserIdentities)
    val enemyFlag by MemoryByteIndexed(Enemy_Flag)
    val enemyId by MemoryByteIndexed(Enemy_ID)
    val enemyState by MemoryByteIndexed(Enemy_State)
    val enemyXSpeed by MemoryByteIndexed(Enemy_X_Speed)
    val enemyYSpeed by MemoryByteIndexed(Enemy_Y_Speed)
    //> HandleEnemyFBallCol:
    //> jsr RelativeEnemyPosition  ;get relative coordinate of enemy
    relativeEnemyPosition()
    //> ldx $01                    ;get current enemy object offset
    //> lda Enemy_Flag,x           ;check buffer flag for d7 set
    //> bpl ChkBuzzyBeetle         ;branch if not set to continue
    temp0 = enemyFlag[memory[0x1].toInt()]
    temp1 = memory[0x1].toInt()
    if ((enemyFlag[memory[0x1].toInt()] and 0x80) != 0) {
        //> and #%00001111             ;otherwise mask out high nybble and
        temp2 = temp0 and 0x0F
        //> tax                        ;use low nybble as enemy offset
        //> lda Enemy_ID,x
        temp0 = enemyId[temp2]
        //> cmp #Bowser                ;check enemy identifier for bowser
        //> beq HurtBowser             ;branch if found
        temp1 = temp2
        if (temp0 != Bowser) {
            //> ldx $01                    ;otherwise retrieve current enemy offset
            temp1 = memory[0x1].toInt()
        }
    }
    //> ChkBuzzyBeetle:
    //> lda Enemy_ID,x
    temp0 = enemyId[temp1]
    //> cmp #BuzzyBeetle           ;check for buzzy beetle
    //> beq ExHCF                  ;branch if found to leave (buzzy beetles fireproof)
    if (temp0 - BuzzyBeetle == 0) {
        //  goto ExHCF
        return
    }
    if (temp0 != BuzzyBeetle) {
        //> cmp #Bowser                ;check for bowser one more time (necessary if d7 of flag was clear)
        //> bne ChkOtherEnemies        ;if not found, branch to check other enemies
        if (temp0 == Bowser) {
            //> HurtBowser:
            //> dec BowserHitPoints        ;decrement bowser's hit points
            bowserHitPoints = (bowserHitPoints - 1) and 0xFF
            //> bne ExHCF                  ;if bowser still has hit points, branch to leave
            if (!(((bowserHitPoints - 1) and 0xFF) == 0)) {
                //  goto ExHCF
                return
            }
            if (((bowserHitPoints - 1) and 0xFF) == 0) {
                //> jsr InitVStf               ;otherwise do sub to init vertical speed and movement force
                initVStf(temp1)
                //> sta Enemy_X_Speed,x        ;initialize horizontal speed
                enemyXSpeed[temp1] = temp0
                //> sta EnemyFrenzyBuffer      ;init enemy frenzy buffer
                enemyFrenzyBuffer = temp0
                //> lda #$fe
                temp0 = 0xFE
                //> sta Enemy_Y_Speed,x        ;set vertical speed to make defeated bowser jump a little
                enemyYSpeed[temp1] = temp0
                //> ldy WorldNumber            ;use world number as offset
                //> lda BowserIdentities,y     ;get enemy identifier to replace bowser with
                temp0 = bowserIdentities[worldNumber]
                //> sta Enemy_ID,x             ;set as new enemy identifier
                enemyId[temp1] = temp0
                //> lda #$20                   ;set A to use starting value for state
                temp0 = 0x20
                //> cpy #$03                   ;check to see if using offset of 3 or more
                //> bcs SetDBSte               ;branch if so
                temp3 = worldNumber
                if (!(worldNumber >= 0x03)) {
                    //> ora #$03                   ;otherwise add 3 to enemy state
                    temp4 = temp0 or 0x03
                }
                //> SetDBSte: sta Enemy_State,x          ;set defeated enemy state
                enemyState[temp1] = temp0
                //> lda #Sfx_BowserFall
                temp0 = Sfx_BowserFall
                //> sta Square2SoundQueue      ;load bowser defeat sound
                square2SoundQueue = temp0
                //> ldx $01                    ;get enemy offset
                temp1 = memory[0x1].toInt()
                //> lda #$09                   ;award 5000 points to player for defeating bowser
                temp0 = 0x09
                //> bne EnemySmackScore        ;unconditional branch to award points
                if (!(temp0 == 0)) {
                    //  goto EnemySmackScore
                    return
                }
                if (temp0 == 0) {
                }
            } else {
                //> ExHCF: rts                      ;and now let's leave
                return
            }
        }
        //> ChkOtherEnemies:
        //> cmp #BulletBill_FrenzyVar
        //> beq ExHCF                 ;branch to leave if bullet bill (frenzy variant)
        if (temp0 - BulletBill_FrenzyVar == 0) {
            //  goto ExHCF
            return
        }
        if (temp0 != BulletBill_FrenzyVar) {
            //> cmp #Podoboo
            //> beq ExHCF                 ;branch to leave if podoboo
            if (temp0 - Podoboo == 0) {
                //  goto ExHCF
                return
            }
            if (temp0 != Podoboo) {
                //> cmp #$15
                //> bcs ExHCF                 ;branch to leave if identifier => $15
                if (temp0 >= 0x15) {
                    //  goto ExHCF
                    return
                }
                if (!(temp0 >= 0x15)) {
                    //> EnemySmackScore:
                    //> jsr SetupFloateyNumber   ;update necessary score variables
                    setupFloateyNumber(temp0, temp1)
                    //> lda #Sfx_EnemySmack      ;play smack enemy sound
                    temp0 = Sfx_EnemySmack
                    //> sta Square1SoundQueue
                    square1SoundQueue = temp0
                }
            }
        }
    }
}

// Decompiled from ShellOrBlockDefeat
fun shellOrBlockDefeat(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var square1SoundQueue by MemoryByte(Square1SoundQueue)
    val enemyId by MemoryByteIndexed(Enemy_ID)
    val enemyState by MemoryByteIndexed(Enemy_State)
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    //> ShellOrBlockDefeat:
    //> lda Enemy_ID,x            ;check for piranha plant
    //> cmp #PiranhaPlant
    //> bne StnE                  ;branch if not found
    temp0 = enemyId[X]
    if (enemyId[X] == PiranhaPlant) {
        //> lda Enemy_Y_Position,x
        temp0 = enemyYPosition[X]
        //> adc #$18                  ;add 24 pixels to enemy object's vertical position
        //> sta Enemy_Y_Position,x
        enemyYPosition[X] = (temp0 + 0x18 + (if (enemyId[X] >= PiranhaPlant) 1 else 0)) and 0xFF
    }
    //> StnE: jsr ChkToStunEnemies      ;do yet another sub
    chkToStunEnemies(temp0, X)
    //> lda Enemy_State,x
    temp0 = enemyState[X]
    //> and #%00011111            ;mask out 2 MSB of enemy object's state
    temp1 = temp0 and 0x1F
    //> ora #%00100000            ;set d5 to defeat enemy and save as new state
    temp2 = temp1 or 0x20
    //> sta Enemy_State,x
    enemyState[X] = temp2
    //> lda #$02                  ;award 200 points by default
    temp0 = 0x02
    //> ldy Enemy_ID,x            ;check for hammer bro
    //> cpy #HammerBro
    //> bne GoombaPoints          ;branch if not found
    temp3 = enemyId[X]
    if (enemyId[X] == HammerBro) {
        //> lda #$06                  ;award 1000 points for hammer bro
        temp0 = 0x06
    }
    //> GoombaPoints:
    //> cpy #Goomba               ;check for goomba
    //> bne EnemySmackScore       ;branch if not found
    if (temp3 == Goomba) {
        //> lda #$01                  ;award 100 points for goomba
        temp0 = 0x01
    }
    //> EnemySmackScore:
    //> jsr SetupFloateyNumber   ;update necessary score variables
    setupFloateyNumber(temp0, X)
    //> lda #Sfx_EnemySmack      ;play smack enemy sound
    temp0 = Sfx_EnemySmack
    //> sta Square1SoundQueue
    square1SoundQueue = temp0
    //> ExHCF: rts                      ;and now let's leave
    return
}

// Decompiled from PlayerHammerCollision
fun playerHammerCollision(X: Int) {
    var X: Int = X
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var frameCounter by MemoryByte(FrameCounter)
    var miscOffscreenbits by MemoryByte(Misc_OffscreenBits)
    var objectOffset by MemoryByte(ObjectOffset)
    var starInvincibleTimer by MemoryByte(StarInvincibleTimer)
    var timerControl by MemoryByte(TimerControl)
    val miscCollisionFlag by MemoryByteIndexed(Misc_Collision_Flag)
    val miscXSpeed by MemoryByteIndexed(Misc_X_Speed)
    //> PlayerHammerCollision:
    //> lda FrameCounter          ;get frame counter
    //> lsr                       ;shift d0 into carry
    frameCounter = frameCounter shr 1
    //> bcc ExPHC                 ;branch to leave if d0 not set to execute every other frame
    temp0 = frameCounter
    if ((frameCounter and 0x01) != 0) {
        //> lda TimerControl          ;if either master timer control
        temp0 = timerControl
        //> ora Misc_OffscreenBits    ;or any offscreen bits for hammer are set,
        temp1 = temp0 or miscOffscreenbits
        //> bne ExPHC                 ;branch to leave
        temp0 = temp1
        if (temp1 == 0) {
            //> txa
            //> asl                       ;multiply misc object offset by four
            X = (X shl 1) and 0xFF
            //> asl
            X = (X shl 1) and 0xFF
            //> clc
            //> adc #$24                  ;add 36 or $24 bytes to get proper offset
            //> tay                       ;for misc object bounding box coordinates
            //> jsr PlayerCollisionCore   ;do player-to-hammer collision detection
            playerCollisionCore()
            //> ldx ObjectOffset          ;get misc object offset
            //> bcc ClHCol                ;if no collision, then branch
            temp0 = (X + 0x24) and 0xFF
            temp2 = objectOffset
            temp3 = (X + 0x24) and 0xFF
            if (X + 0x24 > 0xFF) {
                //> lda Misc_Collision_Flag,x ;otherwise read collision flag
                temp0 = miscCollisionFlag[temp2]
                //> bne ExPHC                 ;if collision flag already set, branch to leave
                if (temp0 == 0) {
                    //> lda #$01
                    temp0 = 0x01
                    //> sta Misc_Collision_Flag,x ;otherwise set collision flag now
                    miscCollisionFlag[temp2] = temp0
                    //> lda Misc_X_Speed,x
                    temp0 = miscXSpeed[temp2]
                    //> eor #$ff                  ;get two's compliment of
                    temp4 = temp0 xor 0xFF
                    //> clc                       ;hammer's horizontal speed
                    //> adc #$01
                    //> sta Misc_X_Speed,x        ;set to send hammer flying the opposite direction
                    miscXSpeed[temp2] = (temp4 + 0x01) and 0xFF
                    //> lda StarInvincibleTimer   ;if star mario invincibility timer set,
                    temp0 = starInvincibleTimer
                    //> bne ExPHC                 ;branch to leave
                    if (temp0 == 0) {
                        //> jmp InjurePlayer          ;otherwise jump to hurt player, do not return
                    } else {
                        //> ExPHC:  rts
                        return
                    }
                }
            }
            //> ClHCol: lda #$00                  ;clear collision flag
            temp0 = 0x00
            //> sta Misc_Collision_Flag,x
            miscCollisionFlag[temp2] = temp0
        }
    }
}

// Decompiled from PlayerEnemyCollision
fun playerEnemyCollision(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var areaMusicQueue by MemoryByte(AreaMusicQueue)
    var frameCounter by MemoryByte(FrameCounter)
    var gameEngineSubroutine by MemoryByte(GameEngineSubroutine)
    var objectOffset by MemoryByte(ObjectOffset)
    var playerStatus by MemoryByte(PlayerStatus)
    var powerUpType by MemoryByte(PowerUpType)
    var square2SoundQueue by MemoryByte(Square2SoundQueue)
    var starInvincibleTimer by MemoryByte(StarInvincibleTimer)
    val enemyOffscrBitsMasked by MemoryByteIndexed(EnemyOffscrBitsMasked)
    val enemyCollisionbits by MemoryByteIndexed(Enemy_CollisionBits)
    val enemyState by MemoryByteIndexed(Enemy_State)
    val floateynumControl by MemoryByteIndexed(FloateyNum_Control)
    //> HandlePowerUpCollision:
    //> jsr EraseEnemyObject    ;erase the power-up object
    eraseEnemyObject(X)
    //> lda #$06
    //> jsr SetupFloateyNumber  ;award 1000 points to player by default
    setupFloateyNumber(0x06, X)
    //> lda #Sfx_PowerUpGrab
    //> sta Square2SoundQueue   ;play the power-up sound
    square2SoundQueue = Sfx_PowerUpGrab
    //> lda PowerUpType         ;check power-up type
    //> cmp #$02
    //> bcc Shroom_Flower_PUp   ;if mushroom or fire flower, branch
    temp0 = powerUpType
    if (powerUpType >= 0x02) {
        //> cmp #$03
        //> beq SetFor1Up           ;if 1-up mushroom, branch
        if (temp0 != 0x03) {
            //> lda #$23                ;otherwise set star mario invincibility
            temp0 = 0x23
            //> sta StarInvincibleTimer ;timer, and load the star mario music
            starInvincibleTimer = temp0
            //> lda #StarPowerMusic     ;into the area music queue, then leave
            temp0 = StarPowerMusic
            //> sta AreaMusicQueue
            areaMusicQueue = temp0
            //> rts
            return
        } else {
            //> SetFor1Up:
            //> lda #$0b                 ;change 1000 points into 1-up instead
            temp0 = 0x0B
            //> sta FloateyNum_Control,x ;and then leave
            floateynumControl[X] = temp0
            //> rts
            return
        }
    }
    //> Shroom_Flower_PUp:
    //> lda PlayerStatus    ;if player status = small, branch
    temp0 = playerStatus
    //> beq UpToSuper
    if (temp0 != 0) {
        //> cmp #$01            ;if player status not super, leave
        //> bne NoPUp
        if (temp0 == 0x01) {
            //> ldx ObjectOffset    ;get enemy offset, not necessary
            //> lda #$02            ;set player status to fiery
            temp0 = 0x02
            //> sta PlayerStatus
            playerStatus = temp0
            //> jsr GetPlayerColors ;run sub to change colors of player
            getPlayerColors()
            //> ldx ObjectOffset    ;get enemy offset again, and again not necessary
            //> lda #$0c            ;set value to be used by subroutine tree (fiery)
            temp0 = 0x0C
            //> jmp UpToFiery       ;jump to set values accordingly
        } else {
            //> NoPUp: rts
            return
        }
    }
    //> UpToSuper:
    //> lda #$01         ;set player status to super
    temp0 = 0x01
    //> sta PlayerStatus
    playerStatus = temp0
    //> lda #$09         ;set value to be used by subroutine tree (super)
    temp0 = 0x09
    //> UpToFiery:
    //> ldy #$00         ;set value to be used as new player state
    //> jsr SetPRout     ;set values to stop certain things in motion
    setPRout(temp0, 0x00)
    temp1 = 0x00
    do {
        //> PlayerEnemyCollision:
        //> lda FrameCounter            ;check counter for d0 set
        temp0 = frameCounter
        //> lsr
        temp0 = temp0 shr 1
        //> bcs NoPUp                   ;if set, branch to leave
    } while ((temp0 and 0x01) != 0)
    //> jsr CheckPlayerVertical     ;if player object is completely offscreen or
    checkPlayerVertical()
    //> bcs NoPECol                 ;if down past 224th pixel row, branch to leave
    if ((temp0 and 0x01) == 0) {
        //> lda EnemyOffscrBitsMasked,x ;if current enemy is offscreen by any amount,
        temp0 = enemyOffscrBitsMasked[X]
        //> bne NoPECol                 ;go ahead and branch to leave
        if (temp0 == 0) {
            //> lda GameEngineSubroutine
            temp0 = gameEngineSubroutine
            //> cmp #$08                    ;if not set to run player control routine
            //> bne NoPECol                 ;on next frame, branch to leave
            if (temp0 == 0x08) {
                //> lda Enemy_State,x
                temp0 = enemyState[X]
                //> and #%00100000              ;if enemy state has d5 set, branch to leave
                temp2 = temp0 and 0x20
                //> bne NoPECol
                temp0 = temp2
                if (temp2 == 0) {
                    //> jsr GetEnemyBoundBoxOfs     ;get bounding box offset for current enemy object
                    getEnemyBoundBoxOfs()
                    //> jsr PlayerCollisionCore     ;do collision detection on player vs. enemy
                    playerCollisionCore()
                    //> ldx ObjectOffset            ;get enemy object buffer offset
                    //> bcs CheckForPUpCollision    ;if collision, branch past this part here
                    temp3 = objectOffset
                    if (!(temp0 >= 0x08)) {
                        //> lda Enemy_CollisionBits,x
                        temp0 = enemyCollisionbits[temp3]
                        //> and #%11111110              ;otherwise, clear d0 of current enemy object's
                        temp4 = temp0 and 0xFE
                        //> sta Enemy_CollisionBits,x   ;collision bit
                        enemyCollisionbits[temp3] = temp4
                    }
                }
            }
        }
    }
    //> NoPECol: rts
    return
}

// Decompiled from InjurePlayer
fun injurePlayer() {
    var temp0: Int = 0
    var injuryTimer by MemoryByte(InjuryTimer)
    var objectOffset by MemoryByte(ObjectOffset)
    //> InjurePlayer:
    //> lda InjuryTimer          ;check again to see if injured invincibility timer is
    //> bne ExInjColRoutines     ;at zero, and branch to leave if so
    temp0 = injuryTimer
    if (injuryTimer == 0) {
    }
    //> ExInjColRoutines:
    //> ldx ObjectOffset              ;get enemy offset and leave
    //> rts
    return
}

// Decompiled from ForceInjury
fun forceInjury(A: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var eventMusicQueue by MemoryByte(EventMusicQueue)
    var injuryTimer by MemoryByte(InjuryTimer)
    var playerStatus by MemoryByte(PlayerStatus)
    var playerXSpeed by MemoryByte(Player_X_Speed)
    var playerYSpeed by MemoryByte(Player_Y_Speed)
    var square1SoundQueue by MemoryByte(Square1SoundQueue)
    val enemyId by MemoryByteIndexed(Enemy_ID)
    val enemyMovingdir by MemoryByteIndexed(Enemy_MovingDir)
    val enemyState by MemoryByteIndexed(Enemy_State)
    val enemyXSpeed by MemoryByteIndexed(Enemy_X_Speed)
    val stompedEnemyPtsData by MemoryByteIndexed(StompedEnemyPtsData)
    //> ForceInjury:
    //> ldx PlayerStatus          ;check player's status
    //> beq KillPlayer            ;branch if small
    temp0 = playerStatus
    if (playerStatus != 0) {
        //> sta PlayerStatus          ;otherwise set player's status to small
        playerStatus = A
        //> lda #$08
        //> sta InjuryTimer           ;set injured invincibility timer
        injuryTimer = 0x08
        //> asl
        //> sta Square1SoundQueue     ;play pipedown/injury sound
        square1SoundQueue = (0x08 shl 1) and 0xFF
        //> jsr GetPlayerColors       ;change player's palette if necessary
        getPlayerColors()
        //> lda #$0a                  ;set subroutine to run on next frame
        //> SetKRout: ldy #$01                  ;set new player state
        // SetKRout code would set GameEngineSubroutine = 0x0a and Player_State = 1
        // For now, just return since this path shrinks the player
        return
    } else {
        // KillPlayer: (playerStatus == 0, small Mario)
        //> stx Player_X_Speed   ;halt player's horizontal movement by initializing speed
        playerXSpeed = temp0  // temp0 is playerStatus = 0
        //> inx
        temp0 = (temp0 + 1) and 0xFF  // temp0 = 1
        //> stx EventMusicQueue  ;set event music queue to death music
        eventMusicQueue = temp0  // death music = 1
        //> lda #$fc
        //> sta Player_Y_Speed   ;set new vertical speed
        playerYSpeed = 0xFC
        //> lda #$0b             ;set subroutine to run on next frame
        //> bne SetKRout         ;unconditional branch to SetKRout
        // SetKRout code would set GameEngineSubroutine = 0x0b and Player_State = 1
        return
    }
    //> StompedEnemyPtsData:
    //> .db $02, $06, $05, $06
    //> EnemyStomped:
    //> lda Enemy_ID,x             ;check for spiny, branch to hurt player
    //> cmp #Spiny                 ;if found
    //> beq InjurePlayer
    if (enemyId[temp0] - Spiny == 0) {
        //  goto InjurePlayer
        return
    } else {
        //> lda #Sfx_EnemyStomp        ;otherwise play stomp/swim sound
        //> sta Square1SoundQueue
        square1SoundQueue = Sfx_EnemyStomp
        //> lda Enemy_ID,x
        //> ldy #$00                   ;initialize points data offset for stomped enemies
        //> cmp #FlyingCheepCheep      ;branch for cheep-cheep
        //> beq EnemyStompedPts
        temp1 = enemyId[temp0]
        temp2 = 0x00
        if (enemyId[temp0] != FlyingCheepCheep) {
            //> cmp #BulletBill_FrenzyVar  ;branch for either bullet bill object
            //> beq EnemyStompedPts
            if (temp1 != BulletBill_FrenzyVar) {
                //> cmp #BulletBill_CannonVar
                //> beq EnemyStompedPts
                if (temp1 != BulletBill_CannonVar) {
                    //> cmp #Podoboo               ;branch for podoboo (this branch is logically impossible
                    //> beq EnemyStompedPts        ;for cpu to take due to earlier checking of podoboo)
                    if (temp1 != Podoboo) {
                        //> iny                        ;increment points data offset
                        temp2 = (temp2 + 1) and 0xFF
                        //> cmp #HammerBro             ;branch for hammer bro
                        //> beq EnemyStompedPts
                        if (temp1 != HammerBro) {
                            //> iny                        ;increment points data offset
                            temp2 = (temp2 + 1) and 0xFF
                            //> cmp #Lakitu                ;branch for lakitu
                            //> beq EnemyStompedPts
                            if (temp1 != Lakitu) {
                                //> iny                        ;increment points data offset
                                temp2 = (temp2 + 1) and 0xFF
                                //> cmp #Bloober               ;branch if NOT bloober
                                //> bne ChkForDemoteKoopa
                                if (temp1 == Bloober) {
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    //> EnemyStompedPts:
    //> lda StompedEnemyPtsData,y  ;load points data using offset in Y
    temp1 = stompedEnemyPtsData[temp2]
    //> jsr SetupFloateyNumber     ;run sub to set floatey number controls
    setupFloateyNumber(temp1, temp0)
    //> lda Enemy_MovingDir,x
    temp1 = enemyMovingdir[temp0]
    //> pha                        ;save enemy movement direction to stack
    push(temp1)
    //> jsr SetStun                ;run sub to kill enemy
    setStun(temp0)
    //> pla
    temp1 = pull()
    //> sta Enemy_MovingDir,x      ;return enemy movement direction from stack
    enemyMovingdir[temp0] = temp1
    //> lda #%00100000
    temp1 = 0x20
    //> sta Enemy_State,x          ;set d5 in enemy state
    enemyState[temp0] = temp1
    //> jsr InitVStf               ;nullify vertical speed, physics-related thing,
    initVStf(temp0)
    //> sta Enemy_X_Speed,x        ;and horizontal speed
    enemyXSpeed[temp0] = temp1
    //> lda #$fd                   ;set player's vertical speed, to give bounce
    temp1 = 0xFD
    //> sta Player_Y_Speed
    playerYSpeed = temp1
    //> rts
    return
}

// Decompiled from SetPRout
fun setPRout(A: Int, Y: Int) {
    var temp0: Int = 0
    var gameEngineSubroutine by MemoryByte(GameEngineSubroutine)
    var objectOffset by MemoryByte(ObjectOffset)
    var playerState by MemoryByte(Player_State)
    var scrollAmount by MemoryByte(ScrollAmount)
    var timerControl by MemoryByte(TimerControl)
    //> SetPRout: sta GameEngineSubroutine  ;load new value to run subroutine on next frame
    gameEngineSubroutine = A
    //> sty Player_State          ;store new player state
    playerState = Y
    //> ldy #$ff
    //> sty TimerControl          ;set master timer control flag to halt timers
    timerControl = 0xFF
    //> iny
    temp0 = 0xFF
    temp0 = (temp0 + 1) and 0xFF
    //> sty ScrollAmount          ;initialize scroll speed
    scrollAmount = temp0
    //> ExInjColRoutines:
    //> ldx ObjectOffset              ;get enemy offset and leave
    //> rts
    return
}

// Decompiled from EnemyFacePlayer
fun enemyFacePlayer(X: Int) {
    var temp0: Int = 0
    val enemyMovingdir by MemoryByteIndexed(Enemy_MovingDir)
    //> EnemyFacePlayer:
    //> ldy #$01               ;set to move right by default
    //> jsr PlayerEnemyDiff    ;get horizontal difference between player and enemy
    playerEnemyDiff(X)
    //> bpl SFcRt              ;if enemy is to the right of player, do not increment
    temp0 = 0x01
    if ((0x01 and 0x80) != 0) {
        //> iny                    ;otherwise, increment to set to move to the left
        temp0 = (temp0 + 1) and 0xFF
    }
    //> SFcRt: sty Enemy_MovingDir,x  ;set moving direction here
    enemyMovingdir[X] = temp0
    //> dey                    ;then decrement to use as a proper offset
    temp0 = (temp0 - 1) and 0xFF
    //> rts
    return
}

// Decompiled from SetupFloateyNumber
fun setupFloateyNumber(A: Int, X: Int) {
    var enemyRelXpos by MemoryByte(Enemy_Rel_XPos)
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    val floateynumControl by MemoryByteIndexed(FloateyNum_Control)
    val floateynumTimer by MemoryByteIndexed(FloateyNum_Timer)
    val floateynumXPos by MemoryByteIndexed(FloateyNum_X_Pos)
    val floateynumYPos by MemoryByteIndexed(FloateyNum_Y_Pos)
    //> SetupFloateyNumber:
    //> sta FloateyNum_Control,x ;set number of points control for floatey numbers
    floateynumControl[X] = A
    //> lda #$30
    //> sta FloateyNum_Timer,x   ;set timer for floatey numbers
    floateynumTimer[X] = 0x30
    //> lda Enemy_Y_Position,x
    //> sta FloateyNum_Y_Pos,x   ;set vertical coordinate
    floateynumYPos[X] = enemyYPosition[X]
    //> lda Enemy_Rel_XPos
    //> sta FloateyNum_X_Pos,x   ;set horizontal coordinate and leave
    floateynumXPos[X] = enemyRelXpos
    //> ExSFN: rts
    return
}

// Decompiled from EnemiesCollision
fun enemiesCollision(X: Int, Y: Int) {
    //> ExSFN: rts
    return
}

// Decompiled from ProcEnemyCollisions
fun procEnemyCollisions(X: Int, Y: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    val enemyId by MemoryByteIndexed(Enemy_ID)
    val enemyState by MemoryByteIndexed(Enemy_State)
    val shellChainCounter by MemoryByteIndexed(ShellChainCounter)
    //> ProcEnemyCollisions:
    //> lda Enemy_State,y        ;check both enemy states for d5 set
    //> ora Enemy_State,x
    temp0 = enemyState[Y] or enemyState[X]
    //> and #%00100000           ;if d5 is set in either state, or both, branch
    temp1 = temp0 and 0x20
    //> bne ExitProcessEColl     ;to leave and do nothing else at this point
    temp2 = temp1
    if (temp1 == 0) {
        //> lda Enemy_State,x
        temp2 = enemyState[X]
        //> cmp #$06                 ;if second enemy state < $06, branch elsewhere
        //> bcc ProcSecondEnemyColl
        if (temp2 >= 0x06) {
            //> lda Enemy_ID,x           ;check second enemy identifier for hammer bro
            temp2 = enemyId[X]
            //> cmp #HammerBro           ;if hammer bro found in alt state, branch to leave
            //> beq ExitProcessEColl
            if (temp2 != HammerBro) {
                //> lda Enemy_State,y        ;check first enemy state for d7 set
                temp2 = enemyState[Y]
                //> asl
                temp2 = (temp2 shl 1) and 0xFF
                //> bcc ShellCollisions      ;branch if d7 is clear
                if ((temp2 and 0x80) != 0) {
                    //> lda #$06
                    temp2 = 0x06
                    //> jsr SetupFloateyNumber   ;award 1000 points for killing enemy
                    setupFloateyNumber(temp2, X)
                    //> jsr ShellOrBlockDefeat   ;then kill enemy, then load
                    shellOrBlockDefeat(X)
                    //> ldy $01                  ;original offset of second enemy
                }
                //> ShellCollisions:
                //> tya                      ;move Y to X
                //> tax
                //> jsr ShellOrBlockDefeat   ;kill second enemy
                shellOrBlockDefeat(Y)
                //> ldx ObjectOffset
                //> lda ShellChainCounter,x  ;get chain counter for shell
                temp2 = shellChainCounter[objectOffset]
                //> clc
                //> adc #$04                 ;add four to get appropriate point offset
                //> ldx $01
                //> jsr SetupFloateyNumber   ;award appropriate number of points for second enemy
                setupFloateyNumber((temp2 + 0x04) and 0xFF, memory[0x1].toInt())
                //> ldx ObjectOffset         ;load original offset of first enemy
                //> inc ShellChainCounter,x  ;increment chain counter for additional enemies
                shellChainCounter[objectOffset] = (shellChainCounter[objectOffset] + 1) and 0xFF
            }
        }
    }
    //> ExitProcessEColl:
    //> rts                      ;leave!!!
    return
}

// Decompiled from EnemyTurnAround
fun enemyTurnAround(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    val enemyId by MemoryByteIndexed(Enemy_ID)
    val enemyMovingdir by MemoryByteIndexed(Enemy_MovingDir)
    val enemyXSpeed by MemoryByteIndexed(Enemy_X_Speed)
    //> EnemyTurnAround:
    //> lda Enemy_ID,x           ;check for specific enemies
    //> cmp #PiranhaPlant
    //> beq ExTA                 ;if piranha plant, leave
    temp0 = enemyId[X]
    if (enemyId[X] != PiranhaPlant) {
        //> cmp #Lakitu
        //> beq ExTA                 ;if lakitu, leave
        if (temp0 != Lakitu) {
            //> cmp #HammerBro
            //> beq ExTA                 ;if hammer bro, leave
            if (temp0 != HammerBro) {
                //> cmp #Spiny
                //> beq RXSpd                ;if spiny, turn it around
                if (temp0 != Spiny) {
                    //> cmp #GreenParatroopaJump
                    //> beq RXSpd                ;if green paratroopa, turn it around
                    if (temp0 != GreenParatroopaJump) {
                        //> cmp #$07
                        //> bcs ExTA                 ;if any OTHER enemy object => $07, leave
                        if (!(temp0 >= 0x07)) {
                        } else {
                            //> ExTA:  rts                      ;leave!!!
                            return
                        }
                    }
                }
                //> RXSpd: lda Enemy_X_Speed,x      ;load horizontal speed
                temp0 = enemyXSpeed[X]
                //> eor #$ff                 ;get two's compliment for horizontal speed
                temp1 = temp0 xor 0xFF
                //> tay
                //> iny
                temp1 = (temp1 + 1) and 0xFF
                //> sty Enemy_X_Speed,x      ;store as new horizontal speed
                enemyXSpeed[X] = temp1
                //> lda Enemy_MovingDir,x
                temp0 = enemyMovingdir[X]
                //> eor #%00000011           ;invert moving direction and store, then leave
                temp2 = temp0 xor 0x03
                //> sta Enemy_MovingDir,x    ;thus effectively turning the enemy around
                enemyMovingdir[X] = temp2
            }
        }
    }
}

// Decompiled from LargePlatformCollision
fun largePlatformCollision(X: Int) {
    var temp0: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    var timerControl by MemoryByte(TimerControl)
    val enemyId by MemoryByteIndexed(Enemy_ID)
    val enemyState by MemoryByteIndexed(Enemy_State)
    val platformCollisionFlag by MemoryByteIndexed(PlatformCollisionFlag)
    //> LargePlatformCollision:
    //> lda #$ff                     ;save value here
    //> sta PlatformCollisionFlag,x
    platformCollisionFlag[X] = 0xFF
    //> lda TimerControl             ;check master timer control
    //> bne ExLPC                    ;if set, branch to leave
    temp0 = timerControl
    if (timerControl == 0) {
        //> lda Enemy_State,x            ;if d7 set in object state,
        temp0 = enemyState[X]
        //> bmi ExLPC                    ;branch to leave
        if ((temp0 and 0x80) == 0) {
            //> lda Enemy_ID,x
            temp0 = enemyId[X]
            //> cmp #$24                     ;check enemy object identifier for
            //> bne ChkForPlayerC_LargeP     ;balance platform, branch if not found
            if (!(temp0 - 0x24 == 0)) {
                //  goto ChkForPlayerC_LargeP
                return
            }
            //> lda Enemy_State,x
            temp0 = enemyState[X]
            //> tax                          ;set state as enemy offset here
            //> jsr ChkForPlayerC_LargeP     ;perform code with state offset, then original offset, in X
            chkforplayercLargep(temp0)
        }
    }
    //> ExLPC: ldx ObjectOffset             ;get enemy object buffer offset and leave
    //> rts
    return
}

// Decompiled from ChkForPlayerC_LargeP
fun chkforplayercLargep(X: Int) {
    var Y: Int = 0
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    //> ChkForPlayerC_LargeP:
    //> jsr CheckPlayerVertical      ;figure out if player is below a certain point
    checkPlayerVertical()
    //> bcs ExLPC                    ;or offscreen, branch to leave if true
    if (flagC) {
        //  goto ExLPC
        return
    }
    if (!flagC) {
        //> txa
        //> jsr GetEnemyBoundBoxOfsArg   ;get bounding box offset in Y
        getEnemyBoundBoxOfsArg(X)
        //> lda Enemy_Y_Position,x       ;store vertical coordinate in
        //> sta $00                      ;temp variable for now
        memory[0x0] = (enemyYPosition[X] and 0xFF).toUByte()
        //> txa                          ;send offset we're on to the stack
        //> pha
        push(X)
        //> jsr PlayerCollisionCore      ;do player-to-platform collision detection
        playerCollisionCore()
        //> pla                          ;retrieve offset from the stack
        temp0 = pull()
        //> tax
        //> bcc ExLPC                    ;if no collision, branch to leave
        if (!(flagC)) {
            //  goto ExLPC
            return
        }
        temp1 = temp0
        temp2 = temp0
        if (flagC) {
            //> jsr ProcLPlatCollisions      ;otherwise collision, perform sub
            procLPlatCollisions(temp2, Y)
        }
    }
    //> ExLPC: ldx ObjectOffset             ;get enemy object buffer offset and leave
    temp2 = objectOffset
    //> rts
    return
}

// Decompiled from SmallPlatformCollision
fun smallPlatformCollision(X: Int, Y: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    var timerControl by MemoryByte(TimerControl)
    val boundingboxDrYpos by MemoryByteIndexed(BoundingBox_DR_YPos)
    val boundingboxUlYpos by MemoryByteIndexed(BoundingBox_UL_YPos)
    val platformCollisionFlag by MemoryByteIndexed(PlatformCollisionFlag)
    //> SmallPlatformCollision:
    //> lda TimerControl             ;if master timer control set,
    //> bne ExSPC                    ;branch to leave
    temp0 = timerControl
    if (timerControl == 0) {
        //> sta PlatformCollisionFlag,x  ;otherwise initialize collision flag
        platformCollisionFlag[X] = temp0
        //> jsr CheckPlayerVertical      ;do a sub to see if player is below a certain point
        checkPlayerVertical()
        //> bcs ExSPC                    ;or entirely offscreen, and branch to leave if true
        if (!flagC) {
            //> lda #$02
            temp0 = 0x02
            //> sta $00                      ;load counter here for 2 bounding boxes
            memory[0x0] = temp0.toUByte()
            while (((memory[0x0].toInt() - 1) and 0xFF) != 0) {
                //> lda BoundingBox_UL_YPos,y  ;check top of platform's bounding box for being
                temp0 = boundingboxUlYpos[Y]
                //> cmp #$20                   ;above a specific point
                //> bcc MoveBoundBox           ;if so, branch, don't do collision detection
                if (temp0 >= 0x20) {
                    //> jsr PlayerCollisionCore    ;otherwise, perform player-to-platform collision detection
                    playerCollisionCore()
                    //> bcs ProcSPlatCollisions    ;skip ahead if collision
                    if (!(temp0 >= 0x20)) {
                    }
                }
                do {
                    //> ChkSmallPlatLoop:
                    //> ldx ObjectOffset           ;get enemy object offset
                    //> jsr GetEnemyBoundBoxOfs    ;get bounding box offset in Y
                    getEnemyBoundBoxOfs()
                    //> and #%00000010             ;if d1 of offscreen lower nybble bits was set
                    temp1 = temp0 and 0x02
                    //> bne ExSPC                  ;then branch to leave
                    //> MoveBoundBox:
                    //> lda BoundingBox_UL_YPos,y  ;move bounding box vertical coordinates
                    temp0 = boundingboxUlYpos[Y]
                    //> clc                        ;128 pixels downwards
                    //> adc #$80
                    //> sta BoundingBox_UL_YPos,y
                    boundingboxUlYpos[Y] = (temp0 + 0x80) and 0xFF
                    //> lda BoundingBox_DR_YPos,y
                    temp0 = boundingboxDrYpos[Y]
                    //> clc
                    //> adc #$80
                    //> sta BoundingBox_DR_YPos,y
                    boundingboxDrYpos[Y] = (temp0 + 0x80) and 0xFF
                    //> dec $00                    ;decrement counter we set earlier
                    memory[0x0] = ((memory[0x0].toInt() - 1) and 0xFF).toUByte()
                    //> bne ChkSmallPlatLoop       ;loop back until both bounding boxes are checked
                } while (((memory[0x0].toInt() - 1) and 0xFF) != 0)
            }
        }
    }
    //> ExSPC: ldx ObjectOffset           ;get enemy object buffer offset, then leave
    //> rts
    return
}

// Decompiled from ProcLPlatCollisions
fun procLPlatCollisions(X: Int, Y: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    var playerState by MemoryByte(Player_State)
    var playerYSpeed by MemoryByte(Player_Y_Speed)
    val boundingboxDrXpos by MemoryByteIndexed(BoundingBox_DR_XPos)
    val boundingboxDrYpos by MemoryByteIndexed(BoundingBox_DR_YPos)
    val boundingboxUlXpos by MemoryByteIndexed(BoundingBox_UL_XPos)
    val boundingboxUlYpos by MemoryByteIndexed(BoundingBox_UL_YPos)
    val enemyId by MemoryByteIndexed(Enemy_ID)
    val platformCollisionFlag by MemoryByteIndexed(PlatformCollisionFlag)
    //> ProcLPlatCollisions:
    //> lda BoundingBox_DR_YPos,y    ;get difference by subtracting the top
    //> sec                          ;of the player's bounding box from the bottom
    //> sbc BoundingBox_UL_YPos      ;of the platform's bounding box
    //> cmp #$04                     ;if difference too large or negative,
    //> bcs ChkForTopCollision       ;branch, do not alter vertical speed of player
    temp0 = (boundingboxDrYpos[Y] - boundingboxUlYpos) and 0xFF
    if (!(((boundingboxDrYpos[Y] - boundingboxUlYpos) and 0xFF) >= 0x04)) {
        //> lda Player_Y_Speed           ;check to see if player's vertical speed is moving down
        temp0 = playerYSpeed
        //> bpl ChkForTopCollision       ;if so, don't mess with it
        if ((temp0 and 0x80) != 0) {
            //> lda #$01                     ;otherwise, set vertical
            temp0 = 0x01
            //> sta Player_Y_Speed           ;speed of player to kill jump
            playerYSpeed = temp0
        }
    }
    //> ChkForTopCollision:
    //> lda BoundingBox_DR_YPos      ;get difference by subtracting the top
    temp0 = boundingboxDrYpos[0]
    //> sec                          ;of the platform's bounding box from the bottom
    //> sbc BoundingBox_UL_YPos,y    ;of the player's bounding box
    //> cmp #$06
    //> bcs PlatformSideCollisions   ;if difference not close enough, skip all of this
    temp0 = (temp0 - boundingboxUlYpos[Y]) and 0xFF
    if (!(((temp0 - boundingboxUlYpos[Y]) and 0xFF) >= 0x06)) {
        //> lda Player_Y_Speed
        temp0 = playerYSpeed
        //> bmi PlatformSideCollisions   ;if player's vertical speed moving upwards, skip this
        if ((temp0 and 0x80) == 0) {
            //> lda $00                      ;get saved bounding box counter from earlier
            temp0 = memory[0x0].toInt()
            //> ldy Enemy_ID,x
            //> cpy #$2b                     ;if either of the two small platform objects are found,
            //> beq SetCollisionFlag         ;regardless of which one, branch to use bounding box counter
            temp1 = enemyId[X]
            if (enemyId[X] != 0x2B) {
                //> cpy #$2c                     ;as contents of collision flag
                //> beq SetCollisionFlag
                if (temp1 != 0x2C) {
                    //> txa                          ;otherwise use enemy object buffer offset
                }
            }
            //> SetCollisionFlag:
            //> ldx ObjectOffset             ;get enemy object buffer offset
            //> sta PlatformCollisionFlag,x  ;save either bounding box counter or enemy offset here
            platformCollisionFlag[objectOffset] = temp0
            //> lda #$00
            temp0 = 0x00
            //> sta Player_State             ;set player state to normal then leave
            playerState = temp0
            //> rts
            return
        }
    }
    //> PlatformSideCollisions:
    //> lda #$01                   ;set value here to indicate possible horizontal
    temp0 = 0x01
    //> sta $00                    ;collision on left side of platform
    memory[0x0] = temp0.toUByte()
    //> lda BoundingBox_DR_XPos    ;get difference by subtracting platform's left edge
    temp0 = boundingboxDrXpos[0]
    //> sec                        ;from player's right edge
    //> sbc BoundingBox_UL_XPos,y
    //> cmp #$08                   ;if difference close enough, skip all of this
    //> bcc SideC
    temp0 = (temp0 - boundingboxUlXpos[temp1]) and 0xFF
    if (((temp0 - boundingboxUlXpos[temp1]) and 0xFF) >= 0x08) {
        //> inc $00                    ;otherwise increment value set here for right side collision
        memory[0x0] = ((memory[0x0].toInt() + 1) and 0xFF).toUByte()
        //> lda BoundingBox_DR_XPos,y  ;get difference by subtracting player's left edge
        temp0 = boundingboxDrXpos[temp1]
        //> clc                        ;from platform's right edge
        //> sbc BoundingBox_UL_XPos
        //> cmp #$09                   ;if difference not close enough, skip subroutine
        //> bcs NoSideC                ;and instead branch to leave (no collision)
        temp0 = (temp0 - boundingboxUlXpos[0] - 1) and 0xFF
        if (!(((temp0 - boundingboxUlXpos[0] - 1) and 0xFF) >= 0x09)) {
        } else {
            //> NoSideC: ldx ObjectOffset           ;return with enemy object buffer offset
            //> rts
            return
        }
    }
    //> SideC:   jsr ImpedePlayerMove       ;deal with horizontal collision
    impedePlayerMove()
}

// Decompiled from PositionPlayerOnS_Plat
fun positionplayeronsPlat(A: Int, X: Int) {
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    val playerPosSPlatData by MemoryByteIndexed(PlayerPosSPlatData)
    //> PositionPlayerOnS_Plat:
    //> tay                        ;use bounding box counter saved in collision flag
    //> lda Enemy_Y_Position,x     ;for offset
    //> clc                        ;add positioning data using offset to the vertical
    //> adc PlayerPosSPlatData-1,y ;coordinate
    //> .db $2c                    ;BIT instruction opcode
}

// Decompiled from PositionPlayerOnVPlat
fun positionPlayerOnVPlat(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var gameEngineSubroutine by MemoryByte(GameEngineSubroutine)
    var playerYHighpos by MemoryByte(Player_Y_HighPos)
    var playerYMoveforce by MemoryByte(Player_Y_MoveForce)
    var playerYPosition by MemoryByte(Player_Y_Position)
    var playerYSpeed by MemoryByte(Player_Y_Speed)
    val enemyYHighpos by MemoryByteIndexed(Enemy_Y_HighPos)
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    //> PositionPlayerOnVPlat:
    //> lda Enemy_Y_Position,x    ;get vertical coordinate
    //> ldy GameEngineSubroutine
    //> cpy #$0b                  ;if certain routine being executed on this frame,
    //> beq ExPlPos               ;skip all of this
    temp0 = enemyYPosition[X]
    temp1 = gameEngineSubroutine
    if (gameEngineSubroutine != 0x0B) {
        //> ldy Enemy_Y_HighPos,x
        temp1 = enemyYHighpos[X]
        //> cpy #$01                  ;if vertical high byte offscreen, skip this
        //> bne ExPlPos
        if (temp1 == 0x01) {
            //> sec                       ;subtract 32 pixels from vertical coordinate
            //> sbc #$20                  ;for the player object's height
            //> sta Player_Y_Position     ;save as player's new vertical coordinate
            playerYPosition = (temp0 - 0x20) and 0xFF
            //> tya
            //> sbc #$00                  ;subtract borrow and store as player's
            //> sta Player_Y_HighPos      ;new vertical high byte
            playerYHighpos = (temp1 - (if (temp0 - 0x20 >= 0) 0 else 1)) and 0xFF
            //> lda #$00
            temp0 = 0x00
            //> sta Player_Y_Speed        ;initialize vertical speed and low byte of force
            playerYSpeed = temp0
            //> sta Player_Y_MoveForce    ;and then leave
            playerYMoveforce = temp0
        }
    }
    //> ExPlPos: rts
    return
}

// Decompiled from CheckPlayerVertical
fun checkPlayerVertical() {
    var temp0: Int = 0
    var temp1: Int = 0
    var playerOffscreenbits by MemoryByte(Player_OffscreenBits)
    var playerYHighpos by MemoryByte(Player_Y_HighPos)
    var playerYPosition by MemoryByte(Player_Y_Position)
    //> CheckPlayerVertical:
    //> lda Player_OffscreenBits  ;if player object is completely offscreen
    //> cmp #$f0                  ;vertically, leave this routine
    //> bcs ExCPV
    temp0 = playerOffscreenbits
    if (!(playerOffscreenbits >= 0xF0)) {
        //> ldy Player_Y_HighPos      ;if player high vertical byte is not
        //> dey                       ;within the screen, leave this routine
        playerYHighpos = (playerYHighpos - 1) and 0xFF
        //> bne ExCPV
        temp1 = playerYHighpos
        if (playerYHighpos == 0) {
            //> lda Player_Y_Position     ;if on the screen, check to see how far down
            temp0 = playerYPosition
            //> cmp #$d0                  ;the player is vertically
        }
    }
    //> ExCPV: rts
    return
}

// Decompiled from GetEnemyBoundBoxOfs
fun getEnemyBoundBoxOfs(): Int {
    var objectOffset by MemoryByte(ObjectOffset)
    //> GetEnemyBoundBoxOfs:
    //> lda ObjectOffset         ;get enemy object buffer offset
    return objectOffset  // FIX: Return the loaded value
}

// Decompiled from GetEnemyBoundBoxOfsArg
fun getEnemyBoundBoxOfsArg(A: Int) {
    var A: Int = A
    var temp0: Int = 0
    var enemyOffscreenbits by MemoryByte(Enemy_OffscreenBits)
    //> GetEnemyBoundBoxOfsArg:
    //> asl                      ;multiply A by four, then add four
    A = (A shl 1) and 0xFF
    //> asl                      ;to skip player's bounding box
    A = (A shl 1) and 0xFF
    //> clc
    //> adc #$04
    //> tay                      ;send to Y
    //> lda Enemy_OffscreenBits  ;get offscreen bits for enemy object
    //> and #%00001111           ;save low nybble
    temp0 = enemyOffscreenbits and 0x0F
    //> cmp #%00001111           ;check for all bits set
    //> rts
    return
}

// Decompiled from PlayerBGCollision
fun playerBGCollision() {
    var temp0: Int = 0
    var temp1: Int = 0
    var disableCollisionDet by MemoryByte(DisableCollisionDet)
    var gameEngineSubroutine by MemoryByte(GameEngineSubroutine)
    var playerCollisionbits by MemoryByte(Player_CollisionBits)
    var playerState by MemoryByte(Player_State)
    var playerYHighpos by MemoryByte(Player_Y_HighPos)
    var playerYPosition by MemoryByte(Player_Y_Position)
    var swimmingFlag by MemoryByte(SwimmingFlag)
    //> PlayerBGCollision:
    //> lda DisableCollisionDet   ;if collision detection disabled flag set,
    //> bne ExPBGCol              ;branch to leave
    temp0 = disableCollisionDet
    if (disableCollisionDet == 0) {
        //> lda GameEngineSubroutine
        temp0 = gameEngineSubroutine
        //> cmp #$0b                  ;if running routine #11 or $0b
        //> beq ExPBGCol              ;branch to leave
        if (temp0 != 0x0B) {
            //> cmp #$04
            //> bcc ExPBGCol              ;if running routines $00-$03 branch to leave
            if (temp0 >= 0x04) {
                //> lda #$01                  ;load default player state for swimming
                temp0 = 0x01
                //> ldy SwimmingFlag          ;if swimming flag set,
                //> bne SetPSte               ;branch ahead to set default state
                temp1 = swimmingFlag
                if (swimmingFlag == 0) {
                    //> lda Player_State          ;if player in normal state,
                    temp0 = playerState
                    //> beq SetFallS              ;branch to set default state for falling
                    if (temp0 != 0) {
                        //> cmp #$03
                        //> bne ChkOnScr              ;if in any other state besides climbing, skip to next part
                        if (temp0 == 0x03) {
                        }
                    }
                    //> SetFallS: lda #$02                  ;load default player state for falling
                    temp0 = 0x02
                }
                //> SetPSte:  sta Player_State          ;set whatever player state is appropriate
                playerState = temp0
                //> ChkOnScr: lda Player_Y_HighPos
                temp0 = playerYHighpos
                //> cmp #$01                  ;check player's vertical high byte for still on the screen
                //> bne ExPBGCol              ;branch to leave if not
                if (temp0 == 0x01) {
                    //> lda #$ff
                    temp0 = 0xFF
                    //> sta Player_CollisionBits  ;initialize player's collision flag
                    playerCollisionbits = temp0
                    //> lda Player_Y_Position
                    temp0 = playerYPosition
                    //> cmp #$cf                  ;check player's vertical coordinate
                    //> bcc ChkCollSize           ;if not too close to the bottom of screen, continue
                    if (temp0 >= 0xCF) {
                    }
                }
            }
        }
    }
    //> ExPBGCol: rts                       ;otherwise leave
    return
}

// Decompiled from ErACM
fun erACM() {
    //> ErACM: ldy $02             ;load vertical high nybble offset for block buffer
    //> lda #$00            ;load blank metatile
    //> sta ($06),y         ;store to remove old contents from block buffer
    memory[readWord(0x6) + memory[0x2].toInt()] = 0x00.toUByte()
    //> jmp RemoveCoin_Axe  ;update the screen accordingly
}

// Decompiled from ChkInvisibleMTiles
fun chkInvisibleMTiles(A: Int) {
    //> ChkInvisibleMTiles:
    //> cmp #$5f       ;check for hidden coin block
    //> beq ExCInvT    ;branch to leave if found
    if (A != 0x5F) {
        //> cmp #$60       ;check for hidden 1-up block
    }
    //> ExCInvT: rts            ;leave with zero flag set if either found
    return
}

// Decompiled from ChkForLandJumpSpring
fun chkForLandJumpSpring() {
    var A: Int = 0
    var jumpspringAnimCtrl by MemoryByte(JumpspringAnimCtrl)
    var jumpspringForce by MemoryByte(JumpspringForce)
    var jumpspringTimer by MemoryByte(JumpspringTimer)
    var verticalForce by MemoryByte(VerticalForce)
    //> ChkForLandJumpSpring:
    //> jsr ChkJumpspringMetatiles  ;do sub to check if player landed on jumpspring
    chkJumpspringMetatiles(A)
    //> bcc ExCJSp                  ;if carry not set, jumpspring not found, therefore leave
    if (flagC) {
        //> lda #$70
        //> sta VerticalForce           ;otherwise set vertical movement force for player
        verticalForce = 0x70
        //> lda #$f9
        //> sta JumpspringForce         ;set default jumpspring force
        jumpspringForce = 0xF9
        //> lda #$03
        //> sta JumpspringTimer         ;set jumpspring timer to be used later
        jumpspringTimer = 0x03
        //> lsr
        //> sta JumpspringAnimCtrl      ;set jumpspring animation control to start animating
        jumpspringAnimCtrl = 0x03 shr 1
    }
    //> ExCJSp: rts                         ;and leave
    return
}

// Decompiled from ChkJumpspringMetatiles
fun chkJumpspringMetatiles(A: Int) {
    //> ChkJumpspringMetatiles:
    //> cmp #$67      ;check for top jumpspring metatile
    //> beq JSFnd     ;branch to set carry if found
    if (A != 0x67) {
        //> cmp #$68      ;check for bottom jumpspring metatile
        //> clc           ;clear carry flag
        //> bne NoJSFnd   ;branch to use cleared carry if not found
        if (A == 0x68) {
        } else {
            //> NoJSFnd: rts           ;leave
            return
        }
    }
    //> JSFnd:   sec           ;set carry if found
}

// Decompiled from HandlePipeEntry
fun handlePipeEntry() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var altEntranceControl by MemoryByte(AltEntranceControl)
    var areaNumber by MemoryByte(AreaNumber)
    var areaPointer by MemoryByte(AreaPointer)
    var changeAreaTimer by MemoryByte(ChangeAreaTimer)
    var entrancePage by MemoryByte(EntrancePage)
    var eventMusicQueue by MemoryByte(EventMusicQueue)
    var fetchNewGameTimerFlag by MemoryByte(FetchNewGameTimerFlag)
    var gameEngineSubroutine by MemoryByte(GameEngineSubroutine)
    var hidden1UpFlag by MemoryByte(Hidden1UpFlag)
    var levelNumber by MemoryByte(LevelNumber)
    var playerSprattrib by MemoryByte(Player_SprAttrib)
    var playerXPosition by MemoryByte(Player_X_Position)
    var square1SoundQueue by MemoryByte(Square1SoundQueue)
    var upDownButtons by MemoryByte(Up_Down_Buttons)
    var warpZoneControl by MemoryByte(WarpZoneControl)
    var worldNumber by MemoryByte(WorldNumber)
    val areaAddrOffsets by MemoryByteIndexed(AreaAddrOffsets)
    val warpZoneNumbers by MemoryByteIndexed(WarpZoneNumbers)
    val worldAddrOffsets by MemoryByteIndexed(WorldAddrOffsets)
    //> HandlePipeEntry:
    //> lda Up_Down_Buttons       ;check saved controller bits from earlier
    //> and #%00000100            ;for pressing down
    temp0 = upDownButtons and 0x04
    //> beq ExPipeE               ;if not pressing down, branch to leave
    temp1 = temp0
    if (temp0 != 0) {
        //> lda $00
        temp1 = memory[0x0].toInt()
        //> cmp #$11                  ;check right foot metatile for warp pipe right metatile
        //> bne ExPipeE               ;branch to leave if not found
        if (temp1 == 0x11) {
            //> lda $01
            temp1 = memory[0x1].toInt()
            //> cmp #$10                  ;check left foot metatile for warp pipe left metatile
            //> bne ExPipeE               ;branch to leave if not found
            if (temp1 == 0x10) {
                //> lda #$30
                temp1 = 0x30
                //> sta ChangeAreaTimer       ;set timer for change of area
                changeAreaTimer = temp1
                //> lda #$03
                temp1 = 0x03
                //> sta GameEngineSubroutine  ;set to run vertical pipe entry routine on next frame
                gameEngineSubroutine = temp1
                //> lda #Sfx_PipeDown_Injury
                temp1 = Sfx_PipeDown_Injury
                //> sta Square1SoundQueue     ;load pipedown/injury sound
                square1SoundQueue = temp1
                //> lda #%00100000
                temp1 = 0x20
                //> sta Player_SprAttrib      ;set background priority bit in player's attributes
                playerSprattrib = temp1
                //> lda WarpZoneControl       ;check warp zone control
                temp1 = warpZoneControl
                //> beq ExPipeE               ;branch to leave if none found
                if (temp1 != 0) {
                    //> and #%00000011            ;mask out all but 2 LSB
                    temp2 = temp1 and 0x03
                    //> asl
                    temp2 = (temp2 shl 1) and 0xFF
                    //> asl                       ;multiply by four
                    temp2 = (temp2 shl 1) and 0xFF
                    //> tax                       ;save as offset to warp zone numbers (starts at left pipe)
                    //> lda Player_X_Position     ;get player's horizontal position
                    temp1 = playerXPosition
                    //> cmp #$60
                    //> bcc GetWNum               ;if player at left, not near middle, use offset and skip ahead
                    temp3 = temp2
                    if (temp1 >= 0x60) {
                        //> inx                       ;otherwise increment for middle pipe
                        temp3 = (temp3 + 1) and 0xFF
                        //> cmp #$a0
                        //> bcc GetWNum               ;if player at middle, but not too far right, use offset and skip
                        if (temp1 >= 0xA0) {
                            //> inx                       ;otherwise increment for last pipe
                            temp3 = (temp3 + 1) and 0xFF
                        }
                    }
                    //> GetWNum: ldy WarpZoneNumbers,x     ;get warp zone numbers
                    //> dey                       ;decrement for use as world number
                    temp4 = warpZoneNumbers[temp3]
                    temp4 = (temp4 - 1) and 0xFF
                    //> sty WorldNumber           ;store as world number and offset
                    worldNumber = temp4
                    //> ldx WorldAddrOffsets,y    ;get offset to where this world's area offsets are
                    temp3 = worldAddrOffsets[temp4]
                    //> lda AreaAddrOffsets,x     ;get area offset based on world offset
                    temp1 = areaAddrOffsets[temp3]
                    //> sta AreaPointer           ;store area offset here to be used to change areas
                    areaPointer = temp1
                    //> lda #Silence
                    temp1 = Silence
                    //> sta EventMusicQueue       ;silence music
                    eventMusicQueue = temp1
                    //> lda #$00
                    temp1 = 0x00
                    //> sta EntrancePage          ;initialize starting page number
                    entrancePage = temp1
                    //> sta AreaNumber            ;initialize area number used for area address offset
                    areaNumber = temp1
                    //> sta LevelNumber           ;initialize level number used for world display
                    levelNumber = temp1
                    //> sta AltEntranceControl    ;initialize mode of entry
                    altEntranceControl = temp1
                    //> inc Hidden1UpFlag         ;set flag for hidden 1-up blocks
                    hidden1UpFlag = (hidden1UpFlag + 1) and 0xFF
                    //> inc FetchNewGameTimerFlag ;set flag to load new game timer
                    fetchNewGameTimerFlag = (fetchNewGameTimerFlag + 1) and 0xFF
                }
            }
        }
    }
    //> ExPipeE: rts                       ;leave!!!
    return
}

// Decompiled from ImpedePlayerMove
fun impedePlayerMove() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var playerCollisionbits by MemoryByte(Player_CollisionBits)
    var playerPageloc by MemoryByte(Player_PageLoc)
    var playerXPosition by MemoryByte(Player_X_Position)
    var playerXSpeed by MemoryByte(Player_X_Speed)
    var sideCollisionTimer by MemoryByte(SideCollisionTimer)
    //> ImpedePlayerMove:
    //> lda #$00                  ;initialize value here
    //> ldy Player_X_Speed        ;get player's horizontal speed
    //> ldx $00                   ;check value set earlier for
    //> dex                       ;left side collision
    temp0 = memory[0x0].toInt()
    temp0 = (temp0 - 1) and 0xFF
    //> bne RImpd                 ;if right side collision, skip this part
    temp1 = 0x00
    temp2 = playerXSpeed
    if (temp0 == 0) {
        //> inx                       ;return value to X
        temp0 = (temp0 + 1) and 0xFF
        //> cpy #$00                  ;if player moving to the left,
        //> bmi ExIPM                 ;branch to invert bit and leave
        if ((temp0 and 0x80) == 0) {
            //> lda #$ff                  ;otherwise load A with value to be used later
            temp1 = 0xFF
            //> jmp NXSpd                 ;and jump to affect movement
        } else {
            //> ExIPM: txa                       ;invert contents of X
            //> eor #$ff
            temp3 = temp0 xor 0xFF
            //> and Player_CollisionBits  ;mask out bit that was set here
            temp4 = temp3 and playerCollisionbits
            //> sta Player_CollisionBits  ;store to clear bit
            playerCollisionbits = temp4
            //> rts
            return
        }
    } else {
        //> RImpd: ldx #$02                  ;return $02 to X
        temp0 = 0x02
        //> cpy #$01                  ;if player moving to the right,
        //> bpl ExIPM                 ;branch to invert bit and leave
        if ((temp0 and 0x80) != 0) {
            //> lda #$01                  ;otherwise load A with value to be used here
            temp1 = 0x01
        }
    }
    //> NXSpd: ldy #$10
    temp2 = 0x10
    //> sty SideCollisionTimer    ;set timer of some sort
    sideCollisionTimer = temp2
    //> ldy #$00
    temp2 = 0x00
    //> sty Player_X_Speed        ;nullify player's horizontal speed
    playerXSpeed = temp2
    //> cmp #$00                  ;if value set in A not set to $ff,
    //> bpl PlatF                 ;branch ahead, do not decrement Y
    if (temp1 < 0) {
        //> dey                       ;otherwise decrement Y now
        temp2 = (temp2 - 1) and 0xFF
    }
    //> PlatF: sty $00                   ;store Y as high bits of horizontal adder
    memory[0x0] = temp2.toUByte()
    //> clc
    //> adc Player_X_Position     ;add contents of A to player's horizontal
    //> sta Player_X_Position     ;position to move player left or right
    playerXPosition = (temp1 + playerXPosition) and 0xFF
    //> lda Player_PageLoc
    temp1 = playerPageloc
    //> adc $00                   ;add high bits and carry to
    //> sta Player_PageLoc        ;page location if necessary
    playerPageloc = (temp1 + memory[0x0].toInt() + (if (temp1 + playerXPosition > 0xFF) 1 else 0)) and 0xFF
}

// Decompiled from CheckForSolidMTiles
fun checkForSolidMTiles(A: Int, X: Int) {
    val solidMTileUpperExt by MemoryByteIndexed(SolidMTileUpperExt)
    //> CheckForSolidMTiles:
    //> jsr GetMTileAttrib        ;find appropriate offset based on metatile's 2 MSB
    getMTileAttrib(A)
    //> cmp SolidMTileUpperExt,x  ;compare current metatile with solid metatiles
    //> rts
    return
}

// Decompiled from CheckForClimbMTiles
fun checkForClimbMTiles(A: Int, X: Int) {
    val climbMTileUpperExt by MemoryByteIndexed(ClimbMTileUpperExt)
    //> CheckForClimbMTiles:
    //> jsr GetMTileAttrib        ;find appropriate offset based on metatile's 2 MSB
    getMTileAttrib(A)
    //> cmp ClimbMTileUpperExt,x  ;compare current metatile with climbable metatiles
    //> rts
    return
}

// Decompiled from CheckForCoinMTiles
fun checkForCoinMTiles(A: Int) {
    var square2SoundQueue by MemoryByte(Square2SoundQueue)
    //> CheckForCoinMTiles:
    //> cmp #$c2              ;check for regular coin
    //> beq CoinSd            ;branch if found
    if (A != 0xC2) {
        //> cmp #$c3              ;check for underwater coin
        //> beq CoinSd            ;branch if found
        if (A != 0xC3) {
            //> clc                   ;otherwise clear carry and leave
            //> rts
            return
        }
    }
    //> CoinSd:  lda #Sfx_CoinGrab
    //> sta Square2SoundQueue ;load coin grab sound and leave
    square2SoundQueue = Sfx_CoinGrab
    //> rts
    return
}

// Decompiled from GetMTileAttrib
fun getMTileAttrib(A: Int): Int {
    var temp0: Int = 0
    //> GetMTileAttrib:
    //> tay            ;save metatile value into Y
    //> and #%11000000 ;mask out all but 2 MSB
    temp0 = A and 0xC0
    //> asl
    temp0 = (temp0 shl 1) and 0xFF
    //> rol            ;shift and rotate d7-d6 to d1-d0
    temp0 = (temp0 shl 1) and 0xFE or if ((temp0 and 0x80) != 0) 1 else 0
    //> rol
    temp0 = (temp0 shl 1) and 0xFE or if ((temp0 and 0x80) != 0) 1 else 0
    //> tax            ;use as offset for metatile data
    //> tya            ;get original metatile value back
    //> ExEBG: rts            ;leave
    return A
}

// Decompiled from EnemyToBGCollisionDet
fun enemyToBGCollisionDet(X: Int) {
    //> ExEBG: rts            ;leave
    return
}

// Decompiled from ChkToStunEnemies
fun chkToStunEnemies(A: Int, X: Int) {
    var temp0: Int = 0
    val enemyId by MemoryByteIndexed(Enemy_ID)
    //> ChkToStunEnemies:
    //> cmp #$09                   ;perform many comparisons on enemy object identifier
    //> bcc SetStun
    if (!(A >= 0x09)) {
        //  goto SetStun
        return
    } else {
        //> cmp #$11                   ;if the enemy object identifier is equal to the values
        //> bcs SetStun                ;$09, $0e, $0f or $10, it will be modified, and not
        if (A >= 0x11) {
            //  goto SetStun
            return
        }
    }
    //> cmp #$0a                   ;modified if not any of those values, note that piranha plant will
    //> bcc Demote                 ;always fail this test because A will still have vertical
    if (A >= 0x0A) {
        //> cmp #PiranhaPlant          ;coordinate from previous addition, also these comparisons
        //> bcc SetStun                ;are only necessary if branching from $d7a1
        if (!(A >= PiranhaPlant)) {
            //  goto SetStun
            return
        }
    }
    //> Demote:   and #%00000001             ;erase all but LSB, essentially turning enemy object
    temp0 = A and 0x01
    //> sta Enemy_ID,x             ;into green or red koopa troopa to demote them
    enemyId[X] = temp0
}

// Decompiled from SetStun
fun setStun(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var areaType by MemoryByte(AreaType)
    val enemyBGCXSpdData by MemoryByteIndexed(EnemyBGCXSpdData)
    val enemyId by MemoryByteIndexed(Enemy_ID)
    val enemyMovingdir by MemoryByteIndexed(Enemy_MovingDir)
    val enemyState by MemoryByteIndexed(Enemy_State)
    val enemyXSpeed by MemoryByteIndexed(Enemy_X_Speed)
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    val enemyYSpeed by MemoryByteIndexed(Enemy_Y_Speed)
    //> SetStun:  lda Enemy_State,x          ;load enemy state
    //> and #%11110000             ;save high nybble
    temp0 = enemyState[X] and 0xF0
    //> ora #%00000010
    temp1 = temp0 or 0x02
    //> sta Enemy_State,x          ;set d1 of enemy state
    enemyState[X] = temp1
    //> dec Enemy_Y_Position,x
    enemyYPosition[X] = (enemyYPosition[X] - 1) and 0xFF
    //> dec Enemy_Y_Position,x     ;subtract two pixels from enemy's vertical position
    enemyYPosition[X] = (enemyYPosition[X] - 1) and 0xFF
    //> lda Enemy_ID,x
    //> cmp #Bloober               ;check for bloober object
    //> beq SetWYSpd
    temp2 = enemyId[X]
    if (enemyId[X] != Bloober) {
        //> lda #$fd                   ;set default vertical speed
        temp2 = 0xFD
        //> ldy AreaType
        //> bne SetNotW                ;if area type not water, set as speed, otherwise
        temp3 = areaType
        if (areaType == 0) {
        }
    }
    //> SetWYSpd: lda #$ff                   ;change the vertical speed
    temp2 = 0xFF
    //> SetNotW:  sta Enemy_Y_Speed,x        ;set vertical speed now
    enemyYSpeed[X] = temp2
    //> ldy #$01
    temp3 = 0x01
    //> jsr PlayerEnemyDiff        ;get horizontal difference between player and enemy object
    playerEnemyDiff(X)
    //> bpl ChkBBill               ;branch if enemy is to the right of player
    if ((temp3 and 0x80) != 0) {
        //> iny                        ;increment Y if not
        temp3 = (temp3 + 1) and 0xFF
    }
    //> ChkBBill: lda Enemy_ID,x
    temp2 = enemyId[X]
    //> cmp #BulletBill_CannonVar  ;check for bullet bill (cannon variant)
    //> beq NoCDirF
    if (temp2 != BulletBill_CannonVar) {
        //> cmp #BulletBill_FrenzyVar  ;check for bullet bill (frenzy variant)
        //> beq NoCDirF                ;branch if either found, direction does not change
        if (temp2 != BulletBill_FrenzyVar) {
            //> sty Enemy_MovingDir,x      ;store as moving direction
            enemyMovingdir[X] = temp3
        }
    }
    //> NoCDirF:  dey                        ;decrement and use as offset
    temp3 = (temp3 - 1) and 0xFF
    //> lda EnemyBGCXSpdData,y     ;get proper horizontal speed
    temp2 = enemyBGCXSpdData[temp3]
    //> sta Enemy_X_Speed,x        ;and store, then leave
    enemyXSpeed[X] = temp2
    //> ExEBGChk: rts
    return
}

// Decompiled from ChkForBump_HammerBroJ
fun chkforbumpHammerbroj(X: Int) {
    var Y: Int = 0
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var temp5: Int = 0
    var temp6: Int = 0
    var temp7: Int = 0
    var frameCounter by MemoryByte(FrameCounter)
    var secondaryHardMode by MemoryByte(SecondaryHardMode)
    val enemyFrameTimer by MemoryByteIndexed(EnemyFrameTimer)
    val enemyIntervalTimer by MemoryByteIndexed(EnemyIntervalTimer)
    val enemyMovingdir by MemoryByteIndexed(Enemy_MovingDir)
    val enemyState by MemoryByteIndexed(Enemy_State)
    val enemyXSpeed by MemoryByteIndexed(Enemy_X_Speed)
    val enemyYSpeed by MemoryByteIndexed(Enemy_Y_Speed)
    val hammerBroJumpLData by MemoryByteIndexed(HammerBroJumpLData)
    val hammerBroJumpTimer by MemoryByteIndexed(HammerBroJumpTimer)
    val pseudoRandomBitReg by MemoryByteIndexed(PseudoRandomBitReg)
    //> SetHJ: sty Enemy_Y_Speed,x         ;set vertical speed for jumping
    enemyYSpeed[X] = Y
    //> lda Enemy_State,x           ;set d0 in enemy state for jumping
    //> ora #$01
    temp0 = enemyState[X] or 0x01
    //> sta Enemy_State,x
    enemyState[X] = temp0
    //> lda $00                     ;load preset value here to use as bitmask
    //> and PseudoRandomBitReg+2,x  ;and do bit-wise comparison with part of LSFR
    temp1 = memory[0x0].toInt() and pseudoRandomBitReg[2 + X]
    //> tay                         ;then use as offset
    //> lda SecondaryHardMode       ;check secondary hard mode flag
    //> bne HJump
    temp2 = secondaryHardMode
    temp3 = temp1
    if (secondaryHardMode == 0) {
        //> tay                         ;if secondary hard mode flag clear, set offset to 0
    }
    //> HJump: lda HammerBroJumpLData,y    ;get jump length timer data using offset from before
    temp2 = hammerBroJumpLData[temp3]
    //> sta EnemyFrameTimer,x       ;save in enemy timer
    enemyFrameTimer[X] = temp2
    //> lda PseudoRandomBitReg+1,x
    temp2 = pseudoRandomBitReg[1 + X]
    //> ora #%11000000              ;get contents of part of LSFR, set d7 and d6, then
    temp4 = temp2 or 0xC0
    //> sta HammerBroJumpTimer,x    ;store in jump timer
    hammerBroJumpTimer[X] = temp4
    //> MoveHammerBroXDir:
    //> ldy #$fc                  ;move hammer bro a little to the left
    temp3 = 0xFC
    //> lda FrameCounter
    temp2 = frameCounter
    //> and #%01000000            ;change hammer bro's direction every 64 frames
    temp5 = temp2 and 0x40
    //> bne Shimmy
    temp2 = temp5
    if (temp5 == 0) {
        //> ldy #$04                  ;if d6 set in counter, move him a little to the right
        temp3 = 0x04
    }
    //> Shimmy:  sty Enemy_X_Speed,x       ;store horizontal speed
    enemyXSpeed[X] = temp3
    //> ldy #$01                  ;set to face right by default
    temp3 = 0x01
    //> jsr PlayerEnemyDiff       ;get horizontal difference between player and hammer bro
    playerEnemyDiff(X)
    //> bmi SetShim               ;if enemy to the left of player, skip this part
    if ((temp3 and 0x80) == 0) {
        //> iny                       ;set to face left
        temp3 = (temp3 + 1) and 0xFF
        //> lda EnemyIntervalTimer,x  ;check walking timer
        temp2 = enemyIntervalTimer[X]
        //> bne SetShim               ;if not yet expired, skip to set moving direction
        if (temp2 == 0) {
            //> lda #$f8
            temp2 = 0xF8
            //> sta Enemy_X_Speed,x       ;otherwise, make the hammer bro walk left towards player
            enemyXSpeed[X] = temp2
        }
    }
    //> SetShim: sty Enemy_MovingDir,x     ;set moving direction
    enemyMovingdir[X] = temp3
    //> RXSpd: lda Enemy_X_Speed,x      ;load horizontal speed
    temp2 = enemyXSpeed[X]
    //> eor #$ff                 ;get two's compliment for horizontal speed
    temp6 = temp2 xor 0xFF
    //> tay
    //> iny
    temp6 = (temp6 + 1) and 0xFF
    //> sty Enemy_X_Speed,x      ;store as new horizontal speed
    enemyXSpeed[X] = temp6
    //> lda Enemy_MovingDir,x
    temp2 = enemyMovingdir[X]
    //> eor #%00000011           ;invert moving direction and store, then leave
    temp7 = temp2 xor 0x03
    //> sta Enemy_MovingDir,x    ;thus effectively turning the enemy around
    enemyMovingdir[X] = temp7
    //> ExTA:  rts                      ;leave!!!
    return
}

// Decompiled from PlayerEnemyDiff
fun playerEnemyDiff(X: Int) {
    var playerPageloc by MemoryByte(Player_PageLoc)
    var playerXPosition by MemoryByte(Player_X_Position)
    val enemyPageloc by MemoryByteIndexed(Enemy_PageLoc)
    val enemyXPosition by MemoryByteIndexed(Enemy_X_Position)
    //> PlayerEnemyDiff:
    //> lda Enemy_X_Position,x  ;get distance between enemy object's
    //> sec                     ;horizontal coordinate and the player's
    //> sbc Player_X_Position   ;horizontal coordinate
    //> sta $00                 ;and store here
    memory[0x0] = ((enemyXPosition[X] - playerXPosition) and 0xFF).toUByte()
    //> lda Enemy_PageLoc,x
    //> sbc Player_PageLoc      ;subtract borrow, then leave
    //> rts
    return
}

// Decompiled from EnemyLanding
fun enemyLanding(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    //> EnemyLanding:
    //> jsr InitVStf            ;do something here to vertical speed and something else
    initVStf(X)
    //> lda Enemy_Y_Position,x
    //> and #%11110000          ;save high nybble of vertical coordinate, and
    temp0 = enemyYPosition[X] and 0xF0
    //> ora #%00001000          ;set d3, then store, probably used to set enemy object
    temp1 = temp0 or 0x08
    //> sta Enemy_Y_Position,x  ;neatly on whatever it's landing on
    enemyYPosition[X] = temp1
    //> rts
    return
}

// Decompiled from SubtEnemyYPos
fun subtEnemyYPos(X: Int) {
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    //> SubtEnemyYPos:
    //> lda Enemy_Y_Position,x  ;add 62 pixels to enemy object's
    //> clc                     ;vertical coordinate
    //> adc #$3e
    //> cmp #$44                ;compare against a certain range
    //> rts                     ;and leave with flags set for conditional branch
    return
}

// Decompiled from EnemyJump
fun enemyJump(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    val enemyMovingdir by MemoryByteIndexed(Enemy_MovingDir)
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    //> DoEnemySideCheck:
    //> lda Enemy_Y_Position,x     ;if enemy within status bar, branch to leave
    //> cmp #$20                   ;because there's nothing there that impedes movement
    //> bcc ExESdeC
    temp0 = enemyYPosition[X]
    if (enemyYPosition[X] >= 0x20) {
        //> ldy #$16                   ;start by finding block to the left of enemy ($00,$14)
        //> lda #$02                   ;set value here in what is also used as
        temp0 = 0x02
        //> sta $eb                    ;OAM data offset
        memory[0xEB] = temp0.toUByte()
        temp1 = 0x16
        while (!(temp1 >= 0x18)) {
            //> lda #$01                   ;set flag in A for save horizontal coordinate
            temp0 = 0x01
            //> jsr BlockBufferChk_Enemy   ;find block to left or right of enemy object
            blockbufferchkEnemy(temp0, X)
            //> beq NextSdeC               ;if nothing found, branch
            if (temp0 != 0) {
                //> jsr ChkForNonSolids        ;check for non-solid blocks
                chkForNonSolids(temp0)
                //> bne ChkForBump_HammerBroJ  ;branch if not found
                if (!(temp0 == 0)) {
                    //  goto ChkForBump_HammerBroJ
                    return
                }
            }
            do {
                //> SdeCLoop: lda $eb                    ;check value
                temp0 = memory[0xEB].toInt()
                //> cmp Enemy_MovingDir,x      ;compare value against moving direction
                //> bne NextSdeC               ;branch if different and do not seek block there
                //> NextSdeC: dec $eb                    ;move to the next direction
                memory[0xEB] = ((memory[0xEB].toInt() - 1) and 0xFF).toUByte()
                //> iny
                temp1 = (temp1 + 1) and 0xFF
                //> cpy #$18                   ;increment Y, loop only if Y < $18, thus we check
                //> bcc SdeCLoop               ;enemy ($00, $14) and ($10, $14) pixel coordinates
            } while (!(temp1 >= 0x18))
        }
    }
    //> ExESdeC:  rts
    return
}

// Decompiled from KillEnemyAboveBlock
fun killEnemyAboveBlock(X: Int) {
    val enemyYSpeed by MemoryByteIndexed(Enemy_Y_Speed)
    //> KillEnemyAboveBlock:
    //> jsr ShellOrBlockDefeat  ;do this sub to kill enemy
    shellOrBlockDefeat(X)
    //> lda #$fc                ;alter vertical speed of enemy and leave
    //> sta Enemy_Y_Speed,x
    enemyYSpeed[X] = 0xFC
    //> rts
    return
}

// Decompiled from ChkUnderEnemy
fun chkUnderEnemy() {
    //> ChkUnderEnemy:
    //> lda #$00                  ;set flag in A for save vertical coordinate
    //> ldy #$15                  ;set Y to check the bottom middle (8,18) of enemy object
    //> jmp BlockBufferChk_Enemy  ;hop to it!
}

// Decompiled from ChkForNonSolids
fun chkForNonSolids(A: Int) {
    //> ChkForNonSolids:
    //> cmp #$26       ;blank metatile used for vines?
    //> beq NSFnd
    if (A != 0x26) {
        //> cmp #$c2       ;regular coin?
        //> beq NSFnd
        if (A != 0xC2) {
            //> cmp #$c3       ;underwater coin?
            //> beq NSFnd
            if (A != 0xC3) {
                //> cmp #$5f       ;hidden coin block?
                //> beq NSFnd
                if (A != 0x5F) {
                    //> cmp #$60       ;hidden 1-up block?
                }
            }
        }
    }
    //> NSFnd: rts
    return
}

// Decompiled from FireballBGCollision
fun fireballBGCollision(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var square1SoundQueue by MemoryByte(Square1SoundQueue)
    val fireballBouncingFlag by MemoryByteIndexed(FireballBouncingFlag)
    val fireballState by MemoryByteIndexed(Fireball_State)
    val fireballYPosition by MemoryByteIndexed(Fireball_Y_Position)
    val fireballYSpeed by MemoryByteIndexed(Fireball_Y_Speed)
    //> FireballBGCollision:
    //> lda Fireball_Y_Position,x   ;check fireball's vertical coordinate
    //> cmp #$18
    //> bcc ClearBounceFlag         ;if within the status bar area of the screen, branch ahead
    temp0 = fireballYPosition[X]
    if (fireballYPosition[X] >= 0x18) {
        //> jsr BlockBufferChk_FBall    ;do fireball to background collision detection on bottom of it
        blockbufferchkFball(X)
        //> beq ClearBounceFlag         ;if nothing underneath fireball, branch
        if (fireballYPosition[X] != 0x18) {
            //> jsr ChkForNonSolids         ;check for non-solid metatiles
            chkForNonSolids(temp0)
            //> beq ClearBounceFlag         ;branch if any found
            if (fireballYPosition[X] != 0x18) {
                //> lda Fireball_Y_Speed,x      ;if fireball's vertical speed set to move upwards,
                temp0 = fireballYSpeed[X]
                //> bmi InitFireballExplode     ;branch to set exploding bit in fireball's state
                if ((temp0 and 0x80) == 0) {
                    //> lda FireballBouncingFlag,x  ;if bouncing flag already set,
                    temp0 = fireballBouncingFlag[X]
                    //> bne InitFireballExplode     ;branch to set exploding bit in fireball's state
                    if (temp0 == 0) {
                        //> lda #$fd
                        temp0 = 0xFD
                        //> sta Fireball_Y_Speed,x      ;otherwise set vertical speed to move upwards (give it bounce)
                        fireballYSpeed[X] = temp0
                        //> lda #$01
                        temp0 = 0x01
                        //> sta FireballBouncingFlag,x  ;set bouncing flag
                        fireballBouncingFlag[X] = temp0
                        //> lda Fireball_Y_Position,x
                        temp0 = fireballYPosition[X]
                        //> and #$f8                    ;modify vertical coordinate to land it properly
                        temp1 = temp0 and 0xF8
                        //> sta Fireball_Y_Position,x   ;store as new vertical coordinate
                        fireballYPosition[X] = temp1
                        //> rts                         ;leave
                        return
                    } else {
                        //> InitFireballExplode:
                        //> lda #$80
                        temp0 = 0x80
                        //> sta Fireball_State,x        ;set exploding flag in fireball's state
                        fireballState[X] = temp0
                        //> lda #Sfx_Bump
                        temp0 = Sfx_Bump
                        //> sta Square1SoundQueue       ;load bump sound
                        square1SoundQueue = temp0
                        //> rts                         ;leave
                        return
                    }
                }
            }
        }
    }
    //> ClearBounceFlag:
    //> lda #$00
    temp0 = 0x00
    //> sta FireballBouncingFlag,x  ;clear bouncing flag by default
    fireballBouncingFlag[X] = temp0
    //> rts                         ;leave
    return
}

// Decompiled from GetFireballBoundBox
fun getFireballBoundBox(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    var screenleftPageloc by MemoryByte(ScreenLeft_PageLoc)
    var screenleftXPos by MemoryByte(ScreenLeft_X_Pos)
    val boundingboxDrXpos by MemoryByteIndexed(BoundingBox_DR_XPos)
    val boundingboxUlXpos by MemoryByteIndexed(BoundingBox_UL_XPos)
    val sprobjectPageloc by MemoryByteIndexed(SprObject_PageLoc)
    val sprobjectXPosition by MemoryByteIndexed(SprObject_X_Position)
    //> GetFireballBoundBox:
    //> txa         ;add seven bytes to offset
    //> clc         ;to use in routines as offset for fireball
    //> adc #$07
    //> tax
    //> ldy #$02    ;set offset for relative coordinates
    //> bne FBallB  ;unconditional branch
    if (!(0x02 == 0)) {
        //  goto FBallB
        return
    }
    temp0 = (X + 0x07) and 0xFF
    temp1 = (X + 0x07) and 0xFF
    temp2 = 0x02
    if (0x02 == 0) {
    }
    //> FBallB: jsr BoundingBoxCore       ;get bounding box coordinates
    boundingBoxCore(temp1, temp2)
    //> jmp CheckRightScreenBBox  ;jump to handle any offscreen coordinates
    //> CheckRightScreenBBox:
    //> lda ScreenLeft_X_Pos       ;add 128 pixels to left side of screen
    temp0 = screenleftXPos
    //> clc                        ;and store as horizontal coordinate of middle
    //> adc #$80
    //> sta $02
    memory[0x2] = ((temp0 + 0x80) and 0xFF).toUByte()
    //> lda ScreenLeft_PageLoc     ;add carry to page location of left side of screen
    temp0 = screenleftPageloc
    //> adc #$00                   ;and store as page location of middle
    //> sta $01
    memory[0x1] = ((temp0 + (if (temp0 + 0x80 > 0xFF) 1 else 0)) and 0xFF).toUByte()
    //> lda SprObject_X_Position,x ;get horizontal coordinate
    temp0 = sprobjectXPosition[temp1]
    //> cmp $02                    ;compare against middle horizontal coordinate
    //> lda SprObject_PageLoc,x    ;get page location
    temp0 = sprobjectPageloc[temp1]
    //> sbc $01                    ;subtract from middle page location
    //> bcc CheckLeftScreenBBox    ;if object is on the left side of the screen, branch
    temp0 = (temp0 - memory[0x1].toInt() - (if (temp0 >= memory[0x2].toInt()) 0 else 1)) and 0xFF
    if ((temp0 - memory[0x1].toInt() - (if (temp0 >= memory[0x2].toInt()) 0 else 1)) >= 0) {
        //> lda BoundingBox_DR_XPos,y  ;check right-side edge of bounding box for offscreen
        temp0 = boundingboxDrXpos[temp2]
        //> bmi NoOfs                  ;coordinates, branch if still on the screen
        if ((temp0 and 0x80) == 0) {
            //> lda #$ff                   ;load offscreen value here to use on one or both horizontal sides
            temp0 = 0xFF
            //> ldx BoundingBox_UL_XPos,y  ;check left-side edge of bounding box for offscreen
            temp1 = boundingboxUlXpos[temp2]
            //> bmi SORte                  ;coordinates, and branch if still on the screen
            if ((temp1 and 0x80) == 0) {
                //> sta BoundingBox_UL_XPos,y  ;store offscreen value for left side
                boundingboxUlXpos[temp2] = temp0
            }
            //> SORte: sta BoundingBox_DR_XPos,y  ;store offscreen value for right side
            boundingboxDrXpos[temp2] = temp0
        }
        //> NoOfs: ldx ObjectOffset           ;get object offset and leave
        temp1 = objectOffset
        //> rts
        return
    } else {
        //> CheckLeftScreenBBox:
        //> lda BoundingBox_UL_XPos,y  ;check left-side edge of bounding box for offscreen
        temp0 = boundingboxUlXpos[temp2]
        //> bpl NoOfs2                 ;coordinates, and branch if still on the screen
        if ((temp0 and 0x80) != 0) {
            //> cmp #$a0                   ;check to see if left-side edge is in the middle of the
            //> bcc NoOfs2                 ;screen or really offscreen, and branch if still on
            if (temp0 >= 0xA0) {
                //> lda #$00
                temp0 = 0x00
                //> ldx BoundingBox_DR_XPos,y  ;check right-side edge of bounding box for offscreen
                temp1 = boundingboxDrXpos[temp2]
                //> bpl SOLft                  ;coordinates, branch if still onscreen
                if ((temp1 and 0x80) != 0) {
                    //> sta BoundingBox_DR_XPos,y  ;store offscreen value for right side
                    boundingboxDrXpos[temp2] = temp0
                }
                //> SOLft:  sta BoundingBox_UL_XPos,y  ;store offscreen value for left side
                boundingboxUlXpos[temp2] = temp0
            }
        }
    }
    //> NoOfs2: ldx ObjectOffset           ;get object offset and leave
    temp1 = objectOffset
    //> rts
    return
}

// Decompiled from GetMiscBoundBox
fun getMiscBoundBox(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    var screenleftPageloc by MemoryByte(ScreenLeft_PageLoc)
    var screenleftXPos by MemoryByte(ScreenLeft_X_Pos)
    val boundingboxDrXpos by MemoryByteIndexed(BoundingBox_DR_XPos)
    val boundingboxUlXpos by MemoryByteIndexed(BoundingBox_UL_XPos)
    val sprobjectPageloc by MemoryByteIndexed(SprObject_PageLoc)
    val sprobjectXPosition by MemoryByteIndexed(SprObject_X_Position)
    //> GetMiscBoundBox:
    //> txa                       ;add nine bytes to offset
    //> clc                       ;to use in routines as offset for misc object
    //> adc #$09
    //> tax
    //> ldy #$06                  ;set offset for relative coordinates
    //> FBallB: jsr BoundingBoxCore       ;get bounding box coordinates
    boundingBoxCore((X + 0x09) and 0xFF, 0x06)
    //> jmp CheckRightScreenBBox  ;jump to handle any offscreen coordinates
    //> CheckRightScreenBBox:
    //> lda ScreenLeft_X_Pos       ;add 128 pixels to left side of screen
    //> clc                        ;and store as horizontal coordinate of middle
    //> adc #$80
    //> sta $02
    memory[0x2] = ((screenleftXPos + 0x80) and 0xFF).toUByte()
    //> lda ScreenLeft_PageLoc     ;add carry to page location of left side of screen
    //> adc #$00                   ;and store as page location of middle
    //> sta $01
    memory[0x1] = ((screenleftPageloc + (if (screenleftXPos + 0x80 > 0xFF) 1 else 0)) and 0xFF).toUByte()
    //> lda SprObject_X_Position,x ;get horizontal coordinate
    //> cmp $02                    ;compare against middle horizontal coordinate
    //> lda SprObject_PageLoc,x    ;get page location
    //> sbc $01                    ;subtract from middle page location
    //> bcc CheckLeftScreenBBox    ;if object is on the left side of the screen, branch
    temp0 = (sprobjectPageloc[(X + 0x09) and 0xFF] - memory[0x1].toInt() - (if (sprobjectXPosition[(X + 0x09) and 0xFF] >= memory[0x2].toInt()) 0 else 1)) and 0xFF
    temp1 = (X + 0x09) and 0xFF
    temp2 = 0x06
    if ((sprobjectPageloc[(X + 0x09) and 0xFF] - memory[0x1].toInt() - (if (sprobjectXPosition[(X + 0x09) and 0xFF] >= memory[0x2].toInt()) 0 else 1)) >= 0) {
        //> lda BoundingBox_DR_XPos,y  ;check right-side edge of bounding box for offscreen
        temp0 = boundingboxDrXpos[temp2]
        //> bmi NoOfs                  ;coordinates, branch if still on the screen
        if ((temp0 and 0x80) == 0) {
            //> lda #$ff                   ;load offscreen value here to use on one or both horizontal sides
            temp0 = 0xFF
            //> ldx BoundingBox_UL_XPos,y  ;check left-side edge of bounding box for offscreen
            temp1 = boundingboxUlXpos[temp2]
            //> bmi SORte                  ;coordinates, and branch if still on the screen
            if ((temp1 and 0x80) == 0) {
                //> sta BoundingBox_UL_XPos,y  ;store offscreen value for left side
                boundingboxUlXpos[temp2] = temp0
            }
            //> SORte: sta BoundingBox_DR_XPos,y  ;store offscreen value for right side
            boundingboxDrXpos[temp2] = temp0
        }
        //> NoOfs: ldx ObjectOffset           ;get object offset and leave
        temp1 = objectOffset
        //> rts
        return
    } else {
        //> CheckLeftScreenBBox:
        //> lda BoundingBox_UL_XPos,y  ;check left-side edge of bounding box for offscreen
        temp0 = boundingboxUlXpos[temp2]
        //> bpl NoOfs2                 ;coordinates, and branch if still on the screen
        if ((temp0 and 0x80) != 0) {
            //> cmp #$a0                   ;check to see if left-side edge is in the middle of the
            //> bcc NoOfs2                 ;screen or really offscreen, and branch if still on
            if (temp0 >= 0xA0) {
                //> lda #$00
                temp0 = 0x00
                //> ldx BoundingBox_DR_XPos,y  ;check right-side edge of bounding box for offscreen
                temp1 = boundingboxDrXpos[temp2]
                //> bpl SOLft                  ;coordinates, branch if still onscreen
                if ((temp1 and 0x80) != 0) {
                    //> sta BoundingBox_DR_XPos,y  ;store offscreen value for right side
                    boundingboxDrXpos[temp2] = temp0
                }
                //> SOLft:  sta BoundingBox_UL_XPos,y  ;store offscreen value for left side
                boundingboxUlXpos[temp2] = temp0
            }
        }
    }
    //> NoOfs2: ldx ObjectOffset           ;get object offset and leave
    temp1 = objectOffset
    //> rts
    return
}

// Decompiled from GetEnemyBoundBox
fun getEnemyBoundBox(X: Int) {
    var X: Int = X
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var enemyOffscreenbits by MemoryByte(Enemy_OffscreenBits)
    var objectOffset by MemoryByte(ObjectOffset)
    var screenleftPageloc by MemoryByte(ScreenLeft_PageLoc)
    var screenleftXPos by MemoryByte(ScreenLeft_X_Pos)
    val boundingboxDrXpos by MemoryByteIndexed(BoundingBox_DR_XPos)
    val boundingboxUlXpos by MemoryByteIndexed(BoundingBox_UL_XPos)
    val enemyBoundingBoxCoord by MemoryByteIndexed(EnemyBoundingBoxCoord)
    val enemyOffscrBitsMasked by MemoryByteIndexed(EnemyOffscrBitsMasked)
    val enemyPageloc by MemoryByteIndexed(Enemy_PageLoc)
    val enemyXPosition by MemoryByteIndexed(Enemy_X_Position)
    val sprobjectPageloc by MemoryByteIndexed(SprObject_PageLoc)
    val sprobjectXPosition by MemoryByteIndexed(SprObject_X_Position)
    //> GetEnemyBoundBox:
    //> ldy #$48                 ;store bitmask here for now
    //> sty $00
    memory[0x0] = 0x48.toUByte()
    //> ldy #$44                 ;store another bitmask here for now and jump
    //> jmp GetMaskedOffScrBits
    //> GetMaskedOffScrBits:
    //> lda Enemy_X_Position,x      ;get enemy object position relative
    //> sec                         ;to the left side of the screen
    //> sbc ScreenLeft_X_Pos
    //> sta $01                     ;store here
    memory[0x1] = ((enemyXPosition[X] - screenleftXPos) and 0xFF).toUByte()
    //> lda Enemy_PageLoc,x         ;subtract borrow from current page location
    //> sbc ScreenLeft_PageLoc      ;of left side
    //> bmi CMBits                  ;if enemy object is beyond left edge, branch
    temp0 = (enemyPageloc[X] - screenleftPageloc - (if (enemyXPosition[X] - screenleftXPos >= 0) 0 else 1)) and 0xFF
    temp1 = 0x44
    if (((enemyPageloc[X] - screenleftPageloc - if (enemyXPosition[X] - screenleftXPos >= 0) 0 else 1) and 0xFF and 0x80) == 0) {
        //> ora $01
        temp2 = temp0 or memory[0x1].toInt()
        //> beq CMBits                  ;if precisely at the left edge, branch
        temp0 = temp2
        if (temp2 != 0) {
            //> ldy $00                     ;if to the right of left edge, use value in $00 for A
            temp1 = memory[0x0].toInt()
        }
    }
    //> CMBits: tya                         ;otherwise use contents of Y
    //> and Enemy_OffscreenBits     ;preserve bitwise whatever's in here
    temp3 = temp1 and enemyOffscreenbits
    //> sta EnemyOffscrBitsMasked,x ;save masked offscreen bits here
    enemyOffscrBitsMasked[X] = temp3
    //> bne MoveBoundBoxOffscreen   ;if anything set here, branch
    temp0 = temp3
    if (temp3 == 0) {
        //> jmp SetupEOffsetFBBox       ;otherwise, do something else
        //> SetupEOffsetFBBox:
        //> txa                        ;add 1 to offset to properly address
        //> clc                        ;the enemy object memory locations
        //> adc #$01
        //> tax
        //> ldy #$01                   ;load 1 as offset here, same reason
        temp1 = 0x01
        //> jsr BoundingBoxCore        ;do a sub to get the coordinates of the bounding box
        boundingBoxCore((X + 0x01) and 0xFF, temp1)
        //> jmp CheckRightScreenBBox   ;jump to handle offscreen coordinates of bounding box
    } else {
        //> MoveBoundBoxOffscreen:
        //> txa                            ;multiply offset by 4
        //> asl
        X = (X shl 1) and 0xFF
        //> asl
        X = (X shl 1) and 0xFF
        //> tay                            ;use as offset here
        //> lda #$ff
        temp0 = 0xFF
        //> sta EnemyBoundingBoxCoord,y    ;load value into four locations here and leave
        enemyBoundingBoxCoord[X] = temp0
        //> sta EnemyBoundingBoxCoord+1,y
        enemyBoundingBoxCoord[1 + X] = temp0
        //> sta EnemyBoundingBoxCoord+2,y
        enemyBoundingBoxCoord[2 + X] = temp0
        //> sta EnemyBoundingBoxCoord+3,y
        enemyBoundingBoxCoord[3 + X] = temp0
        //> rts
        return
    }
    //> CheckRightScreenBBox:
    //> lda ScreenLeft_X_Pos       ;add 128 pixels to left side of screen
    temp0 = screenleftXPos
    //> clc                        ;and store as horizontal coordinate of middle
    //> adc #$80
    //> sta $02
    memory[0x2] = ((temp0 + 0x80) and 0xFF).toUByte()
    //> lda ScreenLeft_PageLoc     ;add carry to page location of left side of screen
    temp0 = screenleftPageloc
    //> adc #$00                   ;and store as page location of middle
    //> sta $01
    memory[0x1] = ((temp0 + (if (temp0 + 0x80 > 0xFF) 1 else 0)) and 0xFF).toUByte()
    //> lda SprObject_X_Position,x ;get horizontal coordinate
    temp0 = sprobjectXPosition[X]
    //> cmp $02                    ;compare against middle horizontal coordinate
    //> lda SprObject_PageLoc,x    ;get page location
    temp0 = sprobjectPageloc[X]
    //> sbc $01                    ;subtract from middle page location
    //> bcc CheckLeftScreenBBox    ;if object is on the left side of the screen, branch
    temp0 = (temp0 - memory[0x1].toInt() - (if (temp0 >= memory[0x2].toInt()) 0 else 1)) and 0xFF
    if ((temp0 - memory[0x1].toInt() - (if (temp0 >= memory[0x2].toInt()) 0 else 1)) >= 0) {
        //> lda BoundingBox_DR_XPos,y  ;check right-side edge of bounding box for offscreen
        temp0 = boundingboxDrXpos[temp1]
        //> bmi NoOfs                  ;coordinates, branch if still on the screen
        if ((temp0 and 0x80) == 0) {
            //> lda #$ff                   ;load offscreen value here to use on one or both horizontal sides
            temp0 = 0xFF
            //> ldx BoundingBox_UL_XPos,y  ;check left-side edge of bounding box for offscreen
            //> bmi SORte                  ;coordinates, and branch if still on the screen
            temp4 = boundingboxUlXpos[temp1]
            if ((boundingboxUlXpos[temp1] and 0x80) == 0) {
                //> sta BoundingBox_UL_XPos,y  ;store offscreen value for left side
                boundingboxUlXpos[temp1] = temp0
            }
            //> SORte: sta BoundingBox_DR_XPos,y  ;store offscreen value for right side
            boundingboxDrXpos[temp1] = temp0
        }
        //> NoOfs: ldx ObjectOffset           ;get object offset and leave
        temp4 = objectOffset
        //> rts
        return
    } else {
        //> CheckLeftScreenBBox:
        //> lda BoundingBox_UL_XPos,y  ;check left-side edge of bounding box for offscreen
        temp0 = boundingboxUlXpos[temp1]
        //> bpl NoOfs2                 ;coordinates, and branch if still on the screen
        if ((temp0 and 0x80) != 0) {
            //> cmp #$a0                   ;check to see if left-side edge is in the middle of the
            //> bcc NoOfs2                 ;screen or really offscreen, and branch if still on
            if (temp0 >= 0xA0) {
                //> lda #$00
                temp0 = 0x00
                //> ldx BoundingBox_DR_XPos,y  ;check right-side edge of bounding box for offscreen
                temp4 = boundingboxDrXpos[temp1]
                //> bpl SOLft                  ;coordinates, branch if still onscreen
                if ((temp4 and 0x80) != 0) {
                    //> sta BoundingBox_DR_XPos,y  ;store offscreen value for right side
                    boundingboxDrXpos[temp1] = temp0
                }
                //> SOLft:  sta BoundingBox_UL_XPos,y  ;store offscreen value for left side
                boundingboxUlXpos[temp1] = temp0
            }
        }
    }
    //> NoOfs2: ldx ObjectOffset           ;get object offset and leave
    temp4 = objectOffset
    //> rts
    return
}

// Decompiled from SmallPlatformBoundBox
fun smallPlatformBoundBox(X: Int) {
    var X: Int = X
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var enemyOffscreenbits by MemoryByte(Enemy_OffscreenBits)
    var objectOffset by MemoryByte(ObjectOffset)
    var screenleftPageloc by MemoryByte(ScreenLeft_PageLoc)
    var screenleftXPos by MemoryByte(ScreenLeft_X_Pos)
    val boundingboxDrXpos by MemoryByteIndexed(BoundingBox_DR_XPos)
    val boundingboxUlXpos by MemoryByteIndexed(BoundingBox_UL_XPos)
    val enemyBoundingBoxCoord by MemoryByteIndexed(EnemyBoundingBoxCoord)
    val enemyOffscrBitsMasked by MemoryByteIndexed(EnemyOffscrBitsMasked)
    val enemyPageloc by MemoryByteIndexed(Enemy_PageLoc)
    val enemyXPosition by MemoryByteIndexed(Enemy_X_Position)
    val sprobjectPageloc by MemoryByteIndexed(SprObject_PageLoc)
    val sprobjectXPosition by MemoryByteIndexed(SprObject_X_Position)
    //> SmallPlatformBoundBox:
    //> ldy #$08                 ;store bitmask here for now
    //> sty $00
    memory[0x0] = 0x08.toUByte()
    //> ldy #$04                 ;store another bitmask here for now
    //> GetMaskedOffScrBits:
    //> lda Enemy_X_Position,x      ;get enemy object position relative
    //> sec                         ;to the left side of the screen
    //> sbc ScreenLeft_X_Pos
    //> sta $01                     ;store here
    memory[0x1] = ((enemyXPosition[X] - screenleftXPos) and 0xFF).toUByte()
    //> lda Enemy_PageLoc,x         ;subtract borrow from current page location
    //> sbc ScreenLeft_PageLoc      ;of left side
    //> bmi CMBits                  ;if enemy object is beyond left edge, branch
    temp0 = (enemyPageloc[X] - screenleftPageloc - (if (enemyXPosition[X] - screenleftXPos >= 0) 0 else 1)) and 0xFF
    temp1 = 0x04
    if (((enemyPageloc[X] - screenleftPageloc - if (enemyXPosition[X] - screenleftXPos >= 0) 0 else 1) and 0xFF and 0x80) == 0) {
        //> ora $01
        temp2 = temp0 or memory[0x1].toInt()
        //> beq CMBits                  ;if precisely at the left edge, branch
        temp0 = temp2
        if (temp2 != 0) {
            //> ldy $00                     ;if to the right of left edge, use value in $00 for A
            temp1 = memory[0x0].toInt()
        }
    }
    //> CMBits: tya                         ;otherwise use contents of Y
    //> and Enemy_OffscreenBits     ;preserve bitwise whatever's in here
    temp3 = temp1 and enemyOffscreenbits
    //> sta EnemyOffscrBitsMasked,x ;save masked offscreen bits here
    enemyOffscrBitsMasked[X] = temp3
    //> bne MoveBoundBoxOffscreen   ;if anything set here, branch
    temp0 = temp3
    if (temp3 == 0) {
        //> jmp SetupEOffsetFBBox       ;otherwise, do something else
        //> SetupEOffsetFBBox:
        //> txa                        ;add 1 to offset to properly address
        //> clc                        ;the enemy object memory locations
        //> adc #$01
        //> tax
        //> ldy #$01                   ;load 1 as offset here, same reason
        temp1 = 0x01
        //> jsr BoundingBoxCore        ;do a sub to get the coordinates of the bounding box
        boundingBoxCore((X + 0x01) and 0xFF, temp1)
        //> jmp CheckRightScreenBBox   ;jump to handle offscreen coordinates of bounding box
    } else {
        //> MoveBoundBoxOffscreen:
        //> txa                            ;multiply offset by 4
        //> asl
        X = (X shl 1) and 0xFF
        //> asl
        X = (X shl 1) and 0xFF
        //> tay                            ;use as offset here
        //> lda #$ff
        temp0 = 0xFF
        //> sta EnemyBoundingBoxCoord,y    ;load value into four locations here and leave
        enemyBoundingBoxCoord[X] = temp0
        //> sta EnemyBoundingBoxCoord+1,y
        enemyBoundingBoxCoord[1 + X] = temp0
        //> sta EnemyBoundingBoxCoord+2,y
        enemyBoundingBoxCoord[2 + X] = temp0
        //> sta EnemyBoundingBoxCoord+3,y
        enemyBoundingBoxCoord[3 + X] = temp0
        //> rts
        return
    }
    //> CheckRightScreenBBox:
    //> lda ScreenLeft_X_Pos       ;add 128 pixels to left side of screen
    temp0 = screenleftXPos
    //> clc                        ;and store as horizontal coordinate of middle
    //> adc #$80
    //> sta $02
    memory[0x2] = ((temp0 + 0x80) and 0xFF).toUByte()
    //> lda ScreenLeft_PageLoc     ;add carry to page location of left side of screen
    temp0 = screenleftPageloc
    //> adc #$00                   ;and store as page location of middle
    //> sta $01
    memory[0x1] = ((temp0 + (if (temp0 + 0x80 > 0xFF) 1 else 0)) and 0xFF).toUByte()
    //> lda SprObject_X_Position,x ;get horizontal coordinate
    temp0 = sprobjectXPosition[X]
    //> cmp $02                    ;compare against middle horizontal coordinate
    //> lda SprObject_PageLoc,x    ;get page location
    temp0 = sprobjectPageloc[X]
    //> sbc $01                    ;subtract from middle page location
    //> bcc CheckLeftScreenBBox    ;if object is on the left side of the screen, branch
    temp0 = (temp0 - memory[0x1].toInt() - (if (temp0 >= memory[0x2].toInt()) 0 else 1)) and 0xFF
    if ((temp0 - memory[0x1].toInt() - (if (temp0 >= memory[0x2].toInt()) 0 else 1)) >= 0) {
        //> lda BoundingBox_DR_XPos,y  ;check right-side edge of bounding box for offscreen
        temp0 = boundingboxDrXpos[temp1]
        //> bmi NoOfs                  ;coordinates, branch if still on the screen
        if ((temp0 and 0x80) == 0) {
            //> lda #$ff                   ;load offscreen value here to use on one or both horizontal sides
            temp0 = 0xFF
            //> ldx BoundingBox_UL_XPos,y  ;check left-side edge of bounding box for offscreen
            //> bmi SORte                  ;coordinates, and branch if still on the screen
            temp4 = boundingboxUlXpos[temp1]
            if ((boundingboxUlXpos[temp1] and 0x80) == 0) {
                //> sta BoundingBox_UL_XPos,y  ;store offscreen value for left side
                boundingboxUlXpos[temp1] = temp0
            }
            //> SORte: sta BoundingBox_DR_XPos,y  ;store offscreen value for right side
            boundingboxDrXpos[temp1] = temp0
        }
        //> NoOfs: ldx ObjectOffset           ;get object offset and leave
        temp4 = objectOffset
        //> rts
        return
    } else {
        //> CheckLeftScreenBBox:
        //> lda BoundingBox_UL_XPos,y  ;check left-side edge of bounding box for offscreen
        temp0 = boundingboxUlXpos[temp1]
        //> bpl NoOfs2                 ;coordinates, and branch if still on the screen
        if ((temp0 and 0x80) != 0) {
            //> cmp #$a0                   ;check to see if left-side edge is in the middle of the
            //> bcc NoOfs2                 ;screen or really offscreen, and branch if still on
            if (temp0 >= 0xA0) {
                //> lda #$00
                temp0 = 0x00
                //> ldx BoundingBox_DR_XPos,y  ;check right-side edge of bounding box for offscreen
                temp4 = boundingboxDrXpos[temp1]
                //> bpl SOLft                  ;coordinates, branch if still onscreen
                if ((temp4 and 0x80) != 0) {
                    //> sta BoundingBox_DR_XPos,y  ;store offscreen value for right side
                    boundingboxDrXpos[temp1] = temp0
                }
                //> SOLft:  sta BoundingBox_UL_XPos,y  ;store offscreen value for left side
                boundingboxUlXpos[temp1] = temp0
            }
        }
    }
    //> NoOfs2: ldx ObjectOffset           ;get object offset and leave
    temp4 = objectOffset
    //> rts
    return
}

// Decompiled from LargePlatformBoundBox
fun largePlatformBoundBox(A: Int, X: Int) {
    var X: Int = X
    var Y: Int = 0
    var temp0: Int = 0
    var temp1: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    var screenleftPageloc by MemoryByte(ScreenLeft_PageLoc)
    var screenleftXPos by MemoryByte(ScreenLeft_X_Pos)
    val boundingboxDrXpos by MemoryByteIndexed(BoundingBox_DR_XPos)
    val boundingboxUlXpos by MemoryByteIndexed(BoundingBox_UL_XPos)
    val enemyBoundingBoxCoord by MemoryByteIndexed(EnemyBoundingBoxCoord)
    val sprobjectPageloc by MemoryByteIndexed(SprObject_PageLoc)
    val sprobjectXPosition by MemoryByteIndexed(SprObject_X_Position)
    //> LargePlatformBoundBox:
    //> inx                        ;increment X to get the proper offset
    X = (X + 1) and 0xFF
    //> jsr GetXOffscreenBits      ;then jump directly to the sub for horizontal offscreen bits
    getXOffscreenBits(X)
    //> dex                        ;decrement to return to original offset
    X = (X - 1) and 0xFF
    //> cmp #$fe                   ;if completely offscreen, branch to put entire bounding
    //> bcs MoveBoundBoxOffscreen  ;box offscreen, otherwise start getting coordinates
    if (A >= 0xFE) {
        //  goto MoveBoundBoxOffscreen
        return
    }
    if (!(A >= 0xFE)) {
        //> SetupEOffsetFBBox:
        //> txa                        ;add 1 to offset to properly address
        //> clc                        ;the enemy object memory locations
        //> adc #$01
        //> tax
        //> ldy #$01                   ;load 1 as offset here, same reason
        //> jsr BoundingBoxCore        ;do a sub to get the coordinates of the bounding box
        boundingBoxCore((X + 0x01) and 0xFF, 0x01)
        //> jmp CheckRightScreenBBox   ;jump to handle offscreen coordinates of bounding box
    } else {
        //> MoveBoundBoxOffscreen:
        //> txa                            ;multiply offset by 4
        //> asl
        X = (X shl 1) and 0xFF
        //> asl
        X = (X shl 1) and 0xFF
        //> tay                            ;use as offset here
        //> lda #$ff
        //> sta EnemyBoundingBoxCoord,y    ;load value into four locations here and leave
        enemyBoundingBoxCoord[X] = 0xFF
        //> sta EnemyBoundingBoxCoord+1,y
        enemyBoundingBoxCoord[1 + X] = 0xFF
        //> sta EnemyBoundingBoxCoord+2,y
        enemyBoundingBoxCoord[2 + X] = 0xFF
        //> sta EnemyBoundingBoxCoord+3,y
        enemyBoundingBoxCoord[3 + X] = 0xFF
        //> rts
        return
    }
    //> CheckRightScreenBBox:
    //> lda ScreenLeft_X_Pos       ;add 128 pixels to left side of screen
    //> clc                        ;and store as horizontal coordinate of middle
    //> adc #$80
    //> sta $02
    memory[0x2] = ((screenleftXPos + 0x80) and 0xFF).toUByte()
    //> lda ScreenLeft_PageLoc     ;add carry to page location of left side of screen
    //> adc #$00                   ;and store as page location of middle
    //> sta $01
    memory[0x1] = ((screenleftPageloc + (if (screenleftXPos + 0x80 > 0xFF) 1 else 0)) and 0xFF).toUByte()
    //> lda SprObject_X_Position,x ;get horizontal coordinate
    //> cmp $02                    ;compare against middle horizontal coordinate
    //> lda SprObject_PageLoc,x    ;get page location
    //> sbc $01                    ;subtract from middle page location
    //> bcc CheckLeftScreenBBox    ;if object is on the left side of the screen, branch
    temp0 = (sprobjectPageloc[X] - memory[0x1].toInt() - (if (sprobjectXPosition[X] >= memory[0x2].toInt()) 0 else 1)) and 0xFF
    if ((sprobjectPageloc[X] - memory[0x1].toInt() - (if (sprobjectXPosition[X] >= memory[0x2].toInt()) 0 else 1)) >= 0) {
        //> lda BoundingBox_DR_XPos,y  ;check right-side edge of bounding box for offscreen
        temp0 = boundingboxDrXpos[Y]
        //> bmi NoOfs                  ;coordinates, branch if still on the screen
        if ((temp0 and 0x80) == 0) {
            //> lda #$ff                   ;load offscreen value here to use on one or both horizontal sides
            temp0 = 0xFF
            //> ldx BoundingBox_UL_XPos,y  ;check left-side edge of bounding box for offscreen
            //> bmi SORte                  ;coordinates, and branch if still on the screen
            temp1 = boundingboxUlXpos[Y]
            if ((boundingboxUlXpos[Y] and 0x80) == 0) {
                //> sta BoundingBox_UL_XPos,y  ;store offscreen value for left side
                boundingboxUlXpos[Y] = temp0
            }
            //> SORte: sta BoundingBox_DR_XPos,y  ;store offscreen value for right side
            boundingboxDrXpos[Y] = temp0
        }
        //> NoOfs: ldx ObjectOffset           ;get object offset and leave
        temp1 = objectOffset
        //> rts
        return
    } else {
        //> CheckLeftScreenBBox:
        //> lda BoundingBox_UL_XPos,y  ;check left-side edge of bounding box for offscreen
        temp0 = boundingboxUlXpos[Y]
        //> bpl NoOfs2                 ;coordinates, and branch if still on the screen
        if ((temp0 and 0x80) != 0) {
            //> cmp #$a0                   ;check to see if left-side edge is in the middle of the
            //> bcc NoOfs2                 ;screen or really offscreen, and branch if still on
            if (temp0 >= 0xA0) {
                //> lda #$00
                temp0 = 0x00
                //> ldx BoundingBox_DR_XPos,y  ;check right-side edge of bounding box for offscreen
                temp1 = boundingboxDrXpos[Y]
                //> bpl SOLft                  ;coordinates, branch if still onscreen
                if ((temp1 and 0x80) != 0) {
                    //> sta BoundingBox_DR_XPos,y  ;store offscreen value for right side
                    boundingboxDrXpos[Y] = temp0
                }
                //> SOLft:  sta BoundingBox_UL_XPos,y  ;store offscreen value for left side
                boundingboxUlXpos[Y] = temp0
            }
        }
    }
    //> NoOfs2: ldx ObjectOffset           ;get object offset and leave
    temp1 = objectOffset
    //> rts
    return
}

// Decompiled from BoundingBoxCore
fun boundingBoxCore(X: Int, Y: Int) {
    var X: Int = X
    var temp0: Int = 0
    var temp1: Int = 0
    val boundBoxCtrlData by MemoryByteIndexed(BoundBoxCtrlData)
    val boundingboxLrCorner by MemoryByteIndexed(BoundingBox_LR_Corner)
    val boundingboxUlCorner by MemoryByteIndexed(BoundingBox_UL_Corner)
    val sprobjBoundboxctrl by MemoryByteIndexed(SprObj_BoundBoxCtrl)
    val sprobjectRelXpos by MemoryByteIndexed(SprObject_Rel_XPos)
    val sprobjectRelYpos by MemoryByteIndexed(SprObject_Rel_YPos)
    //> BoundingBoxCore:
    //> stx $00                     ;save offset here
    memory[0x0] = X.toUByte()
    //> lda SprObject_Rel_YPos,y    ;store object coordinates relative to screen
    //> sta $02                     ;vertically and horizontally, respectively
    memory[0x2] = (sprobjectRelYpos[Y] and 0xFF).toUByte()
    //> lda SprObject_Rel_XPos,y
    //> sta $01
    memory[0x1] = (sprobjectRelXpos[Y] and 0xFF).toUByte()
    //> txa                         ;multiply offset by four and save to stack
    //> asl
    X = (X shl 1) and 0xFF
    //> asl
    X = (X shl 1) and 0xFF
    //> pha
    push(X)
    //> tay                         ;use as offset for Y, X is left alone
    //> lda SprObj_BoundBoxCtrl,x   ;load value here to be used as offset for X
    //> asl                         ;multiply that by four and use as X
    //> asl
    //> tax
    //> lda $01                     ;add the first number in the bounding box data to the
    //> clc                         ;relative horizontal coordinate using enemy object offset
    //> adc BoundBoxCtrlData,x      ;and store somewhere using same offset * 4
    //> sta BoundingBox_UL_Corner,y ;store here
    boundingboxUlCorner[X] = (memory[0x1].toInt() + boundBoxCtrlData[(((sprobjBoundboxctrl[X] shl 1) and 0xFF) shl 1) and 0xFF]) and 0xFF
    //> lda $01
    //> clc
    //> adc BoundBoxCtrlData+2,x    ;add the third number in the bounding box data to the
    //> sta BoundingBox_LR_Corner,y ;relative horizontal coordinate and store
    boundingboxLrCorner[X] = (memory[0x1].toInt() + boundBoxCtrlData[2 + ((((sprobjBoundboxctrl[X] shl 1) and 0xFF) shl 1) and 0xFF)]) and 0xFF
    //> inx                         ;increment both offsets
    temp0 = (((sprobjBoundboxctrl[X] shl 1) and 0xFF) shl 1) and 0xFF
    temp0 = (temp0 + 1) and 0xFF
    //> iny
    X = (X + 1) and 0xFF
    //> lda $02                     ;add the second number to the relative vertical coordinate
    //> clc                         ;using incremented offset and store using the other
    //> adc BoundBoxCtrlData,x      ;incremented offset
    //> sta BoundingBox_UL_Corner,y
    boundingboxUlCorner[X] = (memory[0x2].toInt() + boundBoxCtrlData[temp0]) and 0xFF
    //> lda $02
    //> clc
    //> adc BoundBoxCtrlData+2,x    ;add the fourth number to the relative vertical coordinate
    //> sta BoundingBox_LR_Corner,y ;and store
    boundingboxLrCorner[X] = (memory[0x2].toInt() + boundBoxCtrlData[2 + temp0]) and 0xFF
    //> pla                         ;get original offset loaded into $00 * y from stack
    temp1 = pull()
    //> tay                         ;use as Y
    //> ldx $00                     ;get original offset and use as X again
    temp0 = memory[0x0].toInt()
    //> rts
    return
}

// Decompiled from PlayerCollisionCore
fun playerCollisionCore() {
    //> PlayerCollisionCore:
    //> ldx #$00     ;initialize X to use player's bounding box for comparison
}

// Decompiled from SprObjectCollisionCore
fun sprObjectCollisionCore(X: Int, Y: Int) {
    var X: Int = X
    var Y: Int = Y
    var temp0: Int = 0
    val boundingboxLrCorner by MemoryByteIndexed(BoundingBox_LR_Corner)
    val boundingboxUlCorner by MemoryByteIndexed(BoundingBox_UL_Corner)
    //> SprObjectCollisionCore:
    //> sty $06      ;save contents of Y here
    memory[0x6] = Y.toUByte()
    //> lda #$01
    //> sta $07      ;save value 1 here as counter, compare horizontal coordinates first
    memory[0x7] = 0x01.toUByte()
    //> CollisionCoreLoop:
    //> lda BoundingBox_UL_Corner,y  ;compare left/top coordinates
    //> cmp BoundingBox_UL_Corner,x  ;of first and second objects' bounding boxes
    //> bcs FirstBoxGreater          ;if first left/top => second, branch
    temp0 = boundingboxUlCorner[Y]
    if (!(boundingboxUlCorner[Y] >= boundingboxUlCorner[X])) {
        //> cmp BoundingBox_LR_Corner,x  ;otherwise compare to right/bottom of second
        //> bcc SecondBoxVerticalChk     ;if first left/top < second right/bottom, branch elsewhere
        if (temp0 >= boundingboxLrCorner[X]) {
            //> beq CollisionFound           ;if somehow equal, collision, thus branch
            if (temp0 != boundingboxLrCorner[X]) {
                //> lda BoundingBox_LR_Corner,y  ;if somehow greater, check to see if bottom of
                temp0 = boundingboxLrCorner[Y]
                //> cmp BoundingBox_UL_Corner,y  ;first object's bounding box is greater than its top
                //> bcc CollisionFound           ;if somehow less, vertical wrap collision, thus branch
                if (temp0 >= boundingboxUlCorner[Y]) {
                    //> cmp BoundingBox_UL_Corner,x  ;otherwise compare bottom of first bounding box to the top
                    //> bcs CollisionFound           ;of second box, and if equal or greater, collision, thus branch
                    if (!(temp0 >= boundingboxUlCorner[X])) {
                        //> ldy $06                      ;otherwise return with carry clear and Y = $0006
                        //> rts                          ;note horizontal wrapping never occurs
                        return
                    }
                }
            }
        }
        //> SecondBoxVerticalChk:
        //> lda BoundingBox_LR_Corner,x  ;check to see if the vertical bottom of the box
        temp0 = boundingboxLrCorner[X]
        //> cmp BoundingBox_UL_Corner,x  ;is greater than the vertical top
        //> bcc CollisionFound           ;if somehow less, vertical wrap collision, thus branch
        if (temp0 >= boundingboxUlCorner[X]) {
            //> lda BoundingBox_LR_Corner,y  ;otherwise compare horizontal right or vertical bottom
            temp0 = boundingboxLrCorner[Y]
            //> cmp BoundingBox_UL_Corner,x  ;of first box with horizontal left or vertical top of second box
            //> bcs CollisionFound           ;if equal or greater, collision, thus branch
            if (!(temp0 >= boundingboxUlCorner[X])) {
                //> ldy $06                      ;otherwise return with carry clear and Y = $0006
                //> rts
                return
            }
        }
    }
    //> FirstBoxGreater:
    //> cmp BoundingBox_UL_Corner,x  ;compare first and second box horizontal left/vertical top again
    //> beq CollisionFound           ;if first coordinate = second, collision, thus branch
    if (temp0 != boundingboxUlCorner[X]) {
        //> cmp BoundingBox_LR_Corner,x  ;if not, compare with second object right or bottom edge
        //> bcc CollisionFound           ;if left/top of first less than or equal to right/bottom of second
        if (temp0 >= boundingboxLrCorner[X]) {
            //> beq CollisionFound           ;then collision, thus branch
            if (temp0 != boundingboxLrCorner[X]) {
                //> cmp BoundingBox_LR_Corner,y  ;otherwise check to see if top of first box is greater than bottom
                //> bcc NoCollisionFound         ;if less than or equal, no collision, branch to end
                if (temp0 >= boundingboxLrCorner[Y]) {
                    //> beq NoCollisionFound
                    if (temp0 != boundingboxLrCorner[Y]) {
                        //> lda BoundingBox_LR_Corner,y  ;otherwise compare bottom of first to top of second
                        temp0 = boundingboxLrCorner[Y]
                        //> cmp BoundingBox_UL_Corner,x  ;if bottom of first is greater than top of second, vertical wrap
                        //> bcs CollisionFound           ;collision, and branch, otherwise, proceed onwards here
                        if (!(temp0 >= boundingboxUlCorner[X])) {
                        }
                    }
                }
                //> NoCollisionFound:
                //> clc          ;clear carry, then load value set earlier, then leave
                //> ldy $06      ;like previous ones, if horizontal coordinates do not collide, we do
                //> rts          ;not bother checking vertical ones, because what's the point?
                return
            }
        }
    }
    //> CollisionFound:
    //> inx                    ;increment offsets on both objects to check
    X = (X + 1) and 0xFF
    //> iny                    ;the vertical coordinates
    Y = (Y + 1) and 0xFF
    //> dec $07                ;decrement counter to reflect this
    memory[0x7] = ((memory[0x7].toInt() - 1) and 0xFF).toUByte()
    //> bpl CollisionCoreLoop  ;if counter not expired, branch to loop
    //> sec                    ;otherwise we already did both sets, therefore collision, so set carry
    //> ldy $06                ;load original value set here earlier, then leave
    //> rts
    return
}

// Decompiled from BlockBufferChk_Enemy
fun blockbufferchkEnemy(A: Int, X: Int) {
    var Y: Int = 0
    var temp0: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    //> BlockBufferChk_Enemy:
    //> pha        ;save contents of A to stack
    push(A)
    //> txa
    //> clc        ;add 1 to X to run sub with enemy offset in mind
    //> adc #$01
    //> tax
    //> pla        ;pull A from stack and jump elsewhere
    temp0 = pull()
    //> jmp BBChk_E
    //> BBChk_E: jsr BlockBufferCollision  ;do collision detection subroutine for sprite object
    blockBufferCollision(temp0, (X + 0x01) and 0xFF, Y)
    //> ldx ObjectOffset          ;get object offset
    //> cmp #$00                  ;check to see if object bumped into anything
    //> rts
    return
}

// Decompiled from BlockBufferChk_FBall
fun blockbufferchkFball(X: Int) {
    var objectOffset by MemoryByte(ObjectOffset)
    //> BlockBufferChk_FBall:
    //> ldy #$1a                  ;set offset for block buffer adder data
    //> txa
    //> clc
    //> adc #$07                  ;add seven bytes to use
    //> tax
    //> ResJmpM: lda #$00                  ;set A to return vertical coordinate
    //> BBChk_E: jsr BlockBufferCollision  ;do collision detection subroutine for sprite object
    blockBufferCollision(0x00, (X + 0x07) and 0xFF, 0x1A)
    //> ldx ObjectOffset          ;get object offset
    //> cmp #$00                  ;check to see if object bumped into anything
    //> rts
    return
}

// Decompiled from BlockBufferColli_Feet
fun blockbuffercolliFeet(Y: Int) {
    var Y: Int = Y
    //> BlockBufferColli_Feet:
    //> iny            ;if branched here, increment to next set of adders
    Y = (Y + 1) and 0xFF
}

// Decompiled from BlockBufferColli_Head
fun blockbuffercolliHead() {
    //> BlockBufferColli_Head:
    //> lda #$00       ;set flag to return vertical coordinate
    //> .db $2c        ;BIT instruction opcode
}

// Decompiled from BlockBufferColli_Side
fun blockbuffercolliSide() {
    //> BlockBufferColli_Side:
    //> lda #$01       ;set flag to return horizontal coordinate
    //> ldx #$00       ;set offset for player object
}

// Decompiled from BlockBufferCollision
fun blockBufferCollision(A: Int, X: Int, Y: Int): Int {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var temp5: Int = 0
    var temp6: Int = 0
    val blockbufferXAdder by MemoryByteIndexed(BlockBuffer_X_Adder)
    val blockbufferYAdder by MemoryByteIndexed(BlockBuffer_Y_Adder)
    val sprobjectPageloc by MemoryByteIndexed(SprObject_PageLoc)
    val sprobjectXPosition by MemoryByteIndexed(SprObject_X_Position)
    val sprobjectYPosition by MemoryByteIndexed(SprObject_Y_Position)
    //> BlockBufferCollision:
    //> pha                         ;save contents of A to stack
    push(A)
    //> sty $04                     ;save contents of Y here
    memory[0x4] = Y.toUByte()
    //> lda BlockBuffer_X_Adder,y   ;add horizontal coordinate
    //> clc                         ;of object to value obtained using Y as offset
    //> adc SprObject_X_Position,x
    //> sta $05                     ;store here
    memory[0x5] = ((blockbufferXAdder[Y] + sprobjectXPosition[X]) and 0xFF).toUByte()
    //> lda SprObject_PageLoc,x
    //> adc #$00                    ;add carry to page location
    //> and #$01                    ;get LSB, mask out all other bits
    temp0 = (sprobjectPageloc[X] + (if (blockbufferXAdder[Y] + sprobjectXPosition[X] > 0xFF) 1 else 0)) and 0xFF and 0x01
    //> lsr                         ;move to carry
    temp0 = temp0 shr 1
    //> ora $05                     ;get stored value
    temp1 = temp0 or memory[0x5].toInt()
    //> ror                         ;rotate carry to MSB of A
    temp1 = temp1 shr 1 or if ((temp0 and 0x01) != 0) 0x80 else 0
    //> lsr                         ;and effectively move high nybble to
    temp1 = temp1 shr 1
    //> lsr                         ;lower, LSB which became MSB will be
    temp1 = temp1 shr 1
    //> lsr                         ;d4 at this point
    temp1 = temp1 shr 1
    //> jsr GetBlockBufferAddr      ;get address of block buffer into $06, $07
    getBlockBufferAddr(temp1)
    //> ldy $04                     ;get old contents of Y
    //> lda SprObject_Y_Position,x  ;get vertical coordinate of object
    //> clc
    //> adc BlockBuffer_Y_Adder,y   ;add it to value obtained using Y as offset
    //> and #%11110000              ;mask out low nybble
    temp2 = (sprobjectYPosition[X] + blockbufferYAdder[memory[0x4].toInt()]) and 0xFF and 0xF0
    //> sec
    //> sbc #$20                    ;subtract 32 pixels for the status bar
    //> sta $02                     ;store result here
    memory[0x2] = ((temp2 - 0x20) and 0xFF).toUByte()
    //> tay                         ;use as offset for block buffer
    //> lda ($06),y                 ;check current content of block buffer
    //> sta $03                     ;and store here
    memory[0x3] = memory[readWord(0x6) + ((temp2 - 0x20) and 0xFF)].toInt().toUByte()
    //> ldy $04                     ;get old contents of Y again
    //> pla                         ;pull A from stack
    temp3 = pull()
    //> bne RetXC                   ;if A = 1, branch
    temp4 = temp3
    temp5 = memory[0x4].toInt()
    if (temp3 == 0) {
        //> lda SprObject_Y_Position,x  ;if A = 0, load vertical coordinate
        temp4 = sprobjectYPosition[X]
        //> jmp RetYC                   ;and jump
    } else {
        //> RetXC: lda SprObject_X_Position,x  ;otherwise load horizontal coordinate
        temp4 = sprobjectXPosition[X]
    }
    //> RetYC: and #%00001111              ;and mask out high nybble
    temp6 = temp4 and 0x0F
    //> sta $04                     ;store masked out result here
    memory[0x4] = temp6.toUByte()
    //> lda $03                     ;get saved content of block buffer
    temp4 = memory[0x3].toInt()
    //> rts                         ;and leave
    return A
}

// Decompiled from DrawVine
fun drawVine(Y: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var enemyRelXpos by MemoryByte(Enemy_Rel_XPos)
    var enemyRelYpos by MemoryByte(Enemy_Rel_YPos)
    var vinestartYPosition by MemoryByte(VineStart_Y_Position)
    val enemySprdataoffset by MemoryByteIndexed(Enemy_SprDataOffset)
    val spriteAttributes by MemoryByteIndexed(Sprite_Attributes)
    val spriteTilenumber by MemoryByteIndexed(Sprite_Tilenumber)
    val spriteXPosition by MemoryByteIndexed(Sprite_X_Position)
    val spriteYPosition by MemoryByteIndexed(Sprite_Y_Position)
    val vineObjOffset by MemoryByteIndexed(VineObjOffset)
    val vineYPosAdder by MemoryByteIndexed(VineYPosAdder)
    //> DrawVine:
    //> sty $00                    ;save offset here
    memory[0x0] = Y.toUByte()
    //> lda Enemy_Rel_YPos         ;get relative vertical coordinate
    //> clc
    //> adc VineYPosAdder,y        ;add value using offset in Y to get value
    //> ldx VineObjOffset,y        ;get offset to vine
    //> ldy Enemy_SprDataOffset,x  ;get sprite data offset
    //> sty $02                    ;store sprite data offset here
    memory[0x2] = (enemySprdataoffset[vineObjOffset[Y]] and 0xFF).toUByte()
    //> jsr SixSpriteStacker       ;stack six sprites on top of each other vertically
    sixSpriteStacker((enemyRelYpos + vineYPosAdder[Y]) and 0xFF, enemySprdataoffset[vineObjOffset[Y]])
    //> lda Enemy_Rel_XPos         ;get relative horizontal coordinate
    //> sta Sprite_X_Position,y    ;store in first, third and fifth sprites
    spriteXPosition[enemySprdataoffset[vineObjOffset[Y]]] = enemyRelXpos
    //> sta Sprite_X_Position+8,y
    spriteXPosition[8 + enemySprdataoffset[vineObjOffset[Y]]] = enemyRelXpos
    //> sta Sprite_X_Position+16,y
    spriteXPosition[16 + enemySprdataoffset[vineObjOffset[Y]]] = enemyRelXpos
    //> clc
    //> adc #$06                   ;add six pixels to second, fourth and sixth sprites
    //> sta Sprite_X_Position+4,y  ;to give characteristic staggered vine shape to
    spriteXPosition[4 + enemySprdataoffset[vineObjOffset[Y]]] = (enemyRelXpos + 0x06) and 0xFF
    //> sta Sprite_X_Position+12,y ;our vertical stack of sprites
    spriteXPosition[12 + enemySprdataoffset[vineObjOffset[Y]]] = (enemyRelXpos + 0x06) and 0xFF
    //> sta Sprite_X_Position+20,y
    spriteXPosition[20 + enemySprdataoffset[vineObjOffset[Y]]] = (enemyRelXpos + 0x06) and 0xFF
    //> lda #%00100001             ;set bg priority and palette attribute bits
    //> sta Sprite_Attributes,y    ;set in first, third and fifth sprites
    spriteAttributes[enemySprdataoffset[vineObjOffset[Y]]] = 0x21
    //> sta Sprite_Attributes+8,y
    spriteAttributes[8 + enemySprdataoffset[vineObjOffset[Y]]] = 0x21
    //> sta Sprite_Attributes+16,y
    spriteAttributes[16 + enemySprdataoffset[vineObjOffset[Y]]] = 0x21
    //> ora #%01000000             ;additionally, set horizontal flip bit
    temp0 = 0x21 or 0x40
    //> sta Sprite_Attributes+4,y  ;for second, fourth and sixth sprites
    spriteAttributes[4 + enemySprdataoffset[vineObjOffset[Y]]] = temp0
    //> sta Sprite_Attributes+12,y
    spriteAttributes[12 + enemySprdataoffset[vineObjOffset[Y]]] = temp0
    //> sta Sprite_Attributes+20,y
    spriteAttributes[20 + enemySprdataoffset[vineObjOffset[Y]]] = temp0
    //> ldx #$05                   ;set tiles for six sprites
    temp1 = 0x05
    temp2 = enemySprdataoffset[vineObjOffset[Y]]
    do {
        //> VineTL:  lda #$e1                   ;set tile number for sprite
        //> sta Sprite_Tilenumber,y
        spriteTilenumber[temp2] = 0xE1
        //> iny                        ;move offset to next sprite data
        temp2 = (temp2 + 1) and 0xFF
        //> iny
        temp2 = (temp2 + 1) and 0xFF
        //> iny
        temp2 = (temp2 + 1) and 0xFF
        //> iny
        temp2 = (temp2 + 1) and 0xFF
        //> dex                        ;move onto next sprite
        temp1 = (temp1 - 1) and 0xFF
        //> bpl VineTL                 ;loop until all sprites are done
    } while ((temp1 and 0x80) == 0)
    //> ldy $02                    ;get original offset
    temp2 = memory[0x2].toInt()
    //> lda $00                    ;get offset to vine adding data
    //> bne SkpVTop                ;if offset not zero, skip this part
    temp3 = memory[0x0].toInt()
    if (memory[0x0].toInt() == 0) {
        //> lda #$e0
        temp3 = 0xE0
        //> sta Sprite_Tilenumber,y    ;set other tile number for top of vine
        spriteTilenumber[temp2] = temp3
    }
    //> SkpVTop: ldx #$00                   ;start with the first sprite again
    temp1 = 0x00
    while (temp1 != 0x06) {
        //> lda #$f8
        temp3 = 0xF8
        //> sta Sprite_Y_Position,y    ;otherwise move sprite offscreen
        spriteYPosition[temp2] = temp3
        do {
            //> ChkFTop: lda VineStart_Y_Position   ;get original starting vertical coordinate
            temp3 = vinestartYPosition
            //> sec
            //> sbc Sprite_Y_Position,y    ;subtract top-most sprite's Y coordinate
            //> cmp #$64                   ;if two coordinates are less than 100/$64 pixels
            //> bcc NextVSp                ;apart, skip this to leave sprite alone
            //> NextVSp: iny                        ;move offset to next OAM data
            temp2 = (temp2 + 1) and 0xFF
            //> iny
            temp2 = (temp2 + 1) and 0xFF
            //> iny
            temp2 = (temp2 + 1) and 0xFF
            //> iny
            temp2 = (temp2 + 1) and 0xFF
            //> inx                        ;move onto next sprite
            temp1 = (temp1 + 1) and 0xFF
            //> cpx #$06                   ;do this until all sprites are checked
            //> bne ChkFTop
        } while (temp1 != 0x06)
    }
    //> ldy $00                    ;return offset set earlier
    temp2 = memory[0x0].toInt()
    //> rts
    return
}

// Decompiled from SixSpriteStacker
fun sixSpriteStacker(A: Int, Y: Int) {
    var Y: Int = Y
    var temp0: Int = 0
    val spriteData by MemoryByteIndexed(Sprite_Data)
    //> SixSpriteStacker:
    //> ldx #$06           ;do six sprites
    temp0 = 0x06
    do {
        //> StkLp: sta Sprite_Data,y  ;store X or Y coordinate into OAM data
        spriteData[Y] = A
        //> clc
        //> adc #$08           ;add eight pixels
        //> iny
        Y = (Y + 1) and 0xFF
        //> iny                ;move offset four bytes forward
        Y = (Y + 1) and 0xFF
        //> iny
        Y = (Y + 1) and 0xFF
        //> iny
        Y = (Y + 1) and 0xFF
        //> dex                ;do another sprite
        temp0 = (temp0 - 1) and 0xFF
        //> bne StkLp          ;do this until all sprites are done
    } while (temp0 != 0)
    //> ldy $02            ;get saved OAM data offset and leave
    //> rts
    return
}

// Decompiled from DrawHammer
fun drawHammer(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var temp5: Int = 0
    var frameCounter by MemoryByte(FrameCounter)
    var miscOffscreenbits by MemoryByte(Misc_OffscreenBits)
    var miscRelXpos by MemoryByte(Misc_Rel_XPos)
    var miscRelYpos by MemoryByte(Misc_Rel_YPos)
    var objectOffset by MemoryByte(ObjectOffset)
    var timerControl by MemoryByte(TimerControl)
    val firstSprTilenum by MemoryByteIndexed(FirstSprTilenum)
    val firstSprXPos by MemoryByteIndexed(FirstSprXPos)
    val firstSprYPos by MemoryByteIndexed(FirstSprYPos)
    val hammerSprAttrib by MemoryByteIndexed(HammerSprAttrib)
    val miscSprdataoffset by MemoryByteIndexed(Misc_SprDataOffset)
    val miscState by MemoryByteIndexed(Misc_State)
    val secondSprTilenum by MemoryByteIndexed(SecondSprTilenum)
    val secondSprXPos by MemoryByteIndexed(SecondSprXPos)
    val secondSprYPos by MemoryByteIndexed(SecondSprYPos)
    val spriteAttributes by MemoryByteIndexed(Sprite_Attributes)
    val spriteTilenumber by MemoryByteIndexed(Sprite_Tilenumber)
    val spriteXPosition by MemoryByteIndexed(Sprite_X_Position)
    val spriteYPosition by MemoryByteIndexed(Sprite_Y_Position)
    //> DrawHammer:
    //> ldy Misc_SprDataOffset,x    ;get misc object OAM data offset
    //> lda TimerControl
    //> bne ForceHPose              ;if master timer control set, skip this part
    temp0 = timerControl
    temp1 = miscSprdataoffset[X]
    if (timerControl == 0) {
        //> lda Misc_State,x            ;otherwise get hammer's state
        temp0 = miscState[X]
        //> and #%01111111              ;mask out d7
        temp2 = temp0 and 0x7F
        //> cmp #$01                    ;check to see if set to 1 yet
        //> beq GetHPose                ;if so, branch
        temp0 = temp2
        if (temp2 != 0x01) {
        }
    }
    //> ForceHPose: ldx #$00                    ;reset offset here
    //> beq RenderH                 ;do unconditional branch to rendering part
    temp3 = 0x00
    if (0x00 != 0) {
        //> GetHPose:   lda FrameCounter            ;get frame counter
        temp0 = frameCounter
        //> lsr                         ;move d3-d2 to d1-d0
        temp0 = temp0 shr 1
        //> lsr
        temp0 = temp0 shr 1
        //> and #%00000011              ;mask out all but d1-d0 (changes every four frames)
        temp4 = temp0 and 0x03
        //> tax                         ;use as timing offset
    }
    //> RenderH:    lda Misc_Rel_YPos           ;get relative vertical coordinate
    temp0 = miscRelYpos
    //> clc
    //> adc FirstSprYPos,x          ;add first sprite vertical adder based on offset
    //> sta Sprite_Y_Position,y     ;store as sprite Y coordinate for first sprite
    spriteYPosition[temp1] = (temp0 + firstSprYPos[temp3]) and 0xFF
    //> clc
    //> adc SecondSprYPos,x         ;add second sprite vertical adder based on offset
    //> sta Sprite_Y_Position+4,y   ;store as sprite Y coordinate for second sprite
    spriteYPosition[4 + temp1] = (((temp0 + firstSprYPos[temp3]) and 0xFF) + secondSprYPos[temp3]) and 0xFF
    //> lda Misc_Rel_XPos           ;get relative horizontal coordinate
    temp0 = miscRelXpos
    //> clc
    //> adc FirstSprXPos,x          ;add first sprite horizontal adder based on offset
    //> sta Sprite_X_Position,y     ;store as sprite X coordinate for first sprite
    spriteXPosition[temp1] = (temp0 + firstSprXPos[temp3]) and 0xFF
    //> clc
    //> adc SecondSprXPos,x         ;add second sprite horizontal adder based on offset
    //> sta Sprite_X_Position+4,y   ;store as sprite X coordinate for second sprite
    spriteXPosition[4 + temp1] = (((temp0 + firstSprXPos[temp3]) and 0xFF) + secondSprXPos[temp3]) and 0xFF
    //> lda FirstSprTilenum,x
    temp0 = firstSprTilenum[temp3]
    //> sta Sprite_Tilenumber,y     ;get and store tile number of first sprite
    spriteTilenumber[temp1] = temp0
    //> lda SecondSprTilenum,x
    temp0 = secondSprTilenum[temp3]
    //> sta Sprite_Tilenumber+4,y   ;get and store tile number of second sprite
    spriteTilenumber[4 + temp1] = temp0
    //> lda HammerSprAttrib,x
    temp0 = hammerSprAttrib[temp3]
    //> sta Sprite_Attributes,y     ;get and store attribute bytes for both
    spriteAttributes[temp1] = temp0
    //> sta Sprite_Attributes+4,y   ;note in this case they use the same data
    spriteAttributes[4 + temp1] = temp0
    //> ldx ObjectOffset            ;get misc object offset
    temp3 = objectOffset
    //> lda Misc_OffscreenBits
    temp0 = miscOffscreenbits
    //> and #%11111100              ;check offscreen bits
    temp5 = temp0 and 0xFC
    //> beq NoHOffscr               ;if all bits clear, leave object alone
    temp0 = temp5
    if (temp5 != 0) {
        //> lda #$00
        temp0 = 0x00
        //> sta Misc_State,x            ;otherwise nullify misc object state
        miscState[temp3] = temp0
        //> lda #$f8
        temp0 = 0xF8
        //> jsr DumpTwoSpr              ;do sub to move hammer sprites offscreen
        dumpTwoSpr(temp0, temp1)
    }
    //> NoHOffscr:  rts                         ;leave
    return
}

// Decompiled from FlagpoleGfxHandler
fun flagpoleGfxHandler(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var enemyOffscreenbits by MemoryByte(Enemy_OffscreenBits)
    var enemyRelXpos by MemoryByte(Enemy_Rel_XPos)
    var flagpoleCollisionYPos by MemoryByte(FlagpoleCollisionYPos)
    var flagpolefnumYPos by MemoryByte(FlagpoleFNum_Y_Pos)
    var flagpoleScore by MemoryByte(FlagpoleScore)
    var objectOffset by MemoryByte(ObjectOffset)
    val enemySprdataoffset by MemoryByteIndexed(Enemy_SprDataOffset)
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    val flagpoleScoreNumTiles by MemoryByteIndexed(FlagpoleScoreNumTiles)
    val spriteAttributes by MemoryByteIndexed(Sprite_Attributes)
    val spriteTilenumber by MemoryByteIndexed(Sprite_Tilenumber)
    val spriteXPosition by MemoryByteIndexed(Sprite_X_Position)
    val spriteYPosition by MemoryByteIndexed(Sprite_Y_Position)
    //> FlagpoleGfxHandler:
    //> ldy Enemy_SprDataOffset,x      ;get sprite data offset for flagpole flag
    //> lda Enemy_Rel_XPos             ;get relative horizontal coordinate
    //> sta Sprite_X_Position,y        ;store as X coordinate for first sprite
    spriteXPosition[enemySprdataoffset[X]] = enemyRelXpos
    //> clc
    //> adc #$08                       ;add eight pixels and store
    //> sta Sprite_X_Position+4,y      ;as X coordinate for second and third sprites
    spriteXPosition[4 + enemySprdataoffset[X]] = (enemyRelXpos + 0x08) and 0xFF
    //> sta Sprite_X_Position+8,y
    spriteXPosition[8 + enemySprdataoffset[X]] = (enemyRelXpos + 0x08) and 0xFF
    //> clc
    //> adc #$0c                       ;add twelve more pixels and
    //> sta $05                        ;store here to be used later by floatey number
    memory[0x5] = ((((enemyRelXpos + 0x08) and 0xFF) + 0x0C) and 0xFF).toUByte()
    //> lda Enemy_Y_Position,x         ;get vertical coordinate
    //> jsr DumpTwoSpr                 ;and do sub to dump into first and second sprites
    dumpTwoSpr(enemyYPosition[X], enemySprdataoffset[X])
    //> adc #$08                       ;add eight pixels
    //> sta Sprite_Y_Position+8,y      ;and store into third sprite
    spriteYPosition[8 + enemySprdataoffset[X]] = (enemyYPosition[X] + 0x08 + (if (((enemyRelXpos + 0x08) and 0xFF) + 0x0C > 0xFF) 1 else 0)) and 0xFF
    //> lda FlagpoleFNum_Y_Pos         ;get vertical coordinate for floatey number
    //> sta $02                        ;store it here
    memory[0x2] = flagpolefnumYPos.toUByte()
    //> lda #$01
    //> sta $03                        ;set value for flip which will not be used, and
    memory[0x3] = 0x01.toUByte()
    //> sta $04                        ;attribute byte for floatey number
    memory[0x4] = 0x01.toUByte()
    //> sta Sprite_Attributes,y        ;set attribute bytes for all three sprites
    spriteAttributes[enemySprdataoffset[X]] = 0x01
    //> sta Sprite_Attributes+4,y
    spriteAttributes[4 + enemySprdataoffset[X]] = 0x01
    //> sta Sprite_Attributes+8,y
    spriteAttributes[8 + enemySprdataoffset[X]] = 0x01
    //> lda #$7e
    //> sta Sprite_Tilenumber,y        ;put triangle shaped tile
    spriteTilenumber[enemySprdataoffset[X]] = 0x7E
    //> sta Sprite_Tilenumber+8,y      ;into first and third sprites
    spriteTilenumber[8 + enemySprdataoffset[X]] = 0x7E
    //> lda #$7f
    //> sta Sprite_Tilenumber+4,y      ;put skull tile into second sprite
    spriteTilenumber[4 + enemySprdataoffset[X]] = 0x7F
    //> lda FlagpoleCollisionYPos      ;get vertical coordinate at time of collision
    //> beq ChkFlagOffscreen           ;if zero, branch ahead
    temp0 = flagpoleCollisionYPos
    temp1 = enemySprdataoffset[X]
    if (flagpoleCollisionYPos != 0) {
        //> tya
        //> clc                            ;add 12 bytes to sprite data offset
        //> adc #$0c
        //> tay                            ;put back in Y
        //> lda FlagpoleScore              ;get offset used to award points for touching flagpole
        temp0 = flagpoleScore
        //> asl                            ;multiply by 2 to get proper offset here
        temp0 = (temp0 shl 1) and 0xFF
        //> tax
        //> lda FlagpoleScoreNumTiles,x    ;get appropriate tile data
        temp0 = flagpoleScoreNumTiles[temp0]
        //> sta $00
        memory[0x0] = temp0.toUByte()
        //> lda FlagpoleScoreNumTiles+1,x
        temp0 = flagpoleScoreNumTiles[1 + temp0]
        //> jsr DrawOneSpriteRow           ;use it to render floatey number
        drawOneSpriteRow(temp0, temp0, (temp1 + 0x0C) and 0xFF)
    }
    //> ChkFlagOffscreen:
    //> ldx ObjectOffset               ;get object offset for flag
    //> ldy Enemy_SprDataOffset,x      ;get OAM data offset
    temp1 = enemySprdataoffset[objectOffset]
    //> lda Enemy_OffscreenBits        ;get offscreen bits
    temp0 = enemyOffscreenbits
    //> and #%00001110                 ;mask out all but d3-d1
    temp2 = temp0 and 0x0E
    //> beq ExitDumpSpr                ;if none of these bits set, branch to leave
    temp0 = temp2
    temp3 = objectOffset
    if (temp2 != 0) {
        //> ;-------------------------------------------------------------------------------------
    }
    //> ExitDumpSpr:
    //> rts
    return
}

// Decompiled from MoveSixSpritesOffscreen
fun moveSixSpritesOffscreen() {
    //> MoveSixSpritesOffscreen:
    //> lda #$f8                  ;set offscreen coordinate if jumping here
}

// Decompiled from DumpSixSpr
fun dumpSixSpr(A: Int, Y: Int) {
    val spriteData by MemoryByteIndexed(Sprite_Data)
    //> DumpSixSpr:
    //> sta Sprite_Data+20,y      ;dump A contents
    spriteData[20 + Y] = A
    //> sta Sprite_Data+16,y      ;into third row sprites
    spriteData[16 + Y] = A
}

// Decompiled from DumpFourSpr
fun dumpFourSpr(A: Int, Y: Int) {
    val spriteData by MemoryByteIndexed(Sprite_Data)
    //> DumpFourSpr:
    //> sta Sprite_Data+12,y      ;into second row sprites
    spriteData[12 + Y] = A
}

// Decompiled from DumpThreeSpr
fun dumpThreeSpr(A: Int, Y: Int) {
    val spriteData by MemoryByteIndexed(Sprite_Data)
    //> DumpThreeSpr:
    //> sta Sprite_Data+8,y
    spriteData[8 + Y] = A
}

// Decompiled from DumpTwoSpr
fun dumpTwoSpr(A: Int, Y: Int) {
    val spriteData by MemoryByteIndexed(Sprite_Data)
    //> DumpTwoSpr:
    //> sta Sprite_Data+4,y       ;and into first row sprites
    spriteData[4 + Y] = A
    //> sta Sprite_Data,y
    spriteData[Y] = A
    //> ExitDumpSpr:
    //> rts
    return
}

// Decompiled from DrawLargePlatform
fun drawLargePlatform(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var areaType by MemoryByte(AreaType)
    var cloudTypeOverride by MemoryByte(CloudTypeOverride)
    var enemyOffscreenbits by MemoryByte(Enemy_OffscreenBits)
    var enemyRelXpos by MemoryByte(Enemy_Rel_XPos)
    var objectOffset by MemoryByte(ObjectOffset)
    var secondaryHardMode by MemoryByte(SecondaryHardMode)
    val enemySprdataoffset by MemoryByteIndexed(Enemy_SprDataOffset)
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    val spriteYPosition by MemoryByteIndexed(Sprite_Y_Position)
    //> DrawLargePlatform:
    //> ldy Enemy_SprDataOffset,x   ;get OAM data offset
    //> sty $02                     ;store here
    memory[0x2] = (enemySprdataoffset[X] and 0xFF).toUByte()
    //> iny                         ;add 3 to it for offset
    temp0 = enemySprdataoffset[X]
    temp0 = (temp0 + 1) and 0xFF
    //> iny                         ;to X coordinate
    temp0 = (temp0 + 1) and 0xFF
    //> iny
    temp0 = (temp0 + 1) and 0xFF
    //> lda Enemy_Rel_XPos          ;get horizontal relative coordinate
    //> jsr SixSpriteStacker        ;store X coordinates using A as base, stack horizontally
    sixSpriteStacker(enemyRelXpos, temp0)
    //> ldx ObjectOffset
    //> lda Enemy_Y_Position,x      ;get vertical coordinate
    //> jsr DumpFourSpr             ;dump into first four sprites as Y coordinate
    dumpFourSpr(enemyYPosition[objectOffset], temp0)
    //> ldy AreaType
    temp0 = areaType
    //> cpy #$03                    ;check for castle-type level
    //> beq ShrinkPlatform
    temp1 = enemyYPosition[objectOffset]
    temp2 = objectOffset
    if (temp0 != 0x03) {
        //> ldy SecondaryHardMode       ;check for secondary hard mode flag set
        temp0 = secondaryHardMode
        //> beq SetLast2Platform        ;branch if not set elsewhere
        if (temp0 != 0) {
        }
    }
    //> ShrinkPlatform:
    //> lda #$f8                    ;load offscreen coordinate if flag set or castle-type level
    temp1 = 0xF8
    //> SetLast2Platform:
    //> ldy Enemy_SprDataOffset,x   ;get OAM data offset
    temp0 = enemySprdataoffset[temp2]
    //> sta Sprite_Y_Position+16,y  ;store vertical coordinate or offscreen
    spriteYPosition[16 + temp0] = temp1
    //> sta Sprite_Y_Position+20,y  ;coordinate into last two sprites as Y coordinate
    spriteYPosition[20 + temp0] = temp1
    //> lda #$5b                    ;load default tile for platform (girder)
    temp1 = 0x5B
    //> ldx CloudTypeOverride
    temp2 = cloudTypeOverride
    //> beq SetPlatformTilenum      ;if cloud level override flag not set, use
    if (temp2 != 0) {
        //> lda #$75                    ;otherwise load other tile for platform (puff)
        temp1 = 0x75
    }
    //> SetPlatformTilenum:
    //> ldx ObjectOffset            ;get enemy object buffer offset
    temp2 = objectOffset
    //> iny                         ;increment Y for tile offset
    temp0 = (temp0 + 1) and 0xFF
    //> jsr DumpSixSpr              ;dump tile number into all six sprites
    dumpSixSpr(temp1, temp0)
    //> lda #$02                    ;set palette controls
    temp1 = 0x02
    //> iny                         ;increment Y for sprite attributes
    temp0 = (temp0 + 1) and 0xFF
    //> jsr DumpSixSpr              ;dump attributes into all six sprites
    dumpSixSpr(temp1, temp0)
    //> inx                         ;increment X for enemy objects
    temp2 = (temp2 + 1) and 0xFF
    //> jsr GetXOffscreenBits       ;get offscreen bits again
    getXOffscreenBits(temp2)
    //> dex
    temp2 = (temp2 - 1) and 0xFF
    //> ldy Enemy_SprDataOffset,x   ;get OAM data offset
    temp0 = enemySprdataoffset[temp2]
    //> asl                         ;rotate d7 into carry, save remaining
    temp1 = (temp1 shl 1) and 0xFF
    //> pha                         ;bits to the stack
    push(temp1)
    //> bcc SChk2
    if ((temp1 and 0x80) != 0) {
        //> lda #$f8                    ;if d7 was set, move first sprite offscreen
        temp1 = 0xF8
        //> sta Sprite_Y_Position,y
        spriteYPosition[temp0] = temp1
    }
    //> SChk2:  pla                         ;get bits from stack
    temp1 = pull()
    //> asl                         ;rotate d6 into carry
    temp1 = (temp1 shl 1) and 0xFF
    //> pha                         ;save to stack
    push(temp1)
    //> bcc SChk3
    if ((temp1 and 0x80) != 0) {
        //> lda #$f8                    ;if d6 was set, move second sprite offscreen
        temp1 = 0xF8
        //> sta Sprite_Y_Position+4,y
        spriteYPosition[4 + temp0] = temp1
    }
    //> SChk3:  pla                         ;get bits from stack
    temp1 = pull()
    //> asl                         ;rotate d5 into carry
    temp1 = (temp1 shl 1) and 0xFF
    //> pha                         ;save to stack
    push(temp1)
    //> bcc SChk4
    if ((temp1 and 0x80) != 0) {
        //> lda #$f8                    ;if d5 was set, move third sprite offscreen
        temp1 = 0xF8
        //> sta Sprite_Y_Position+8,y
        spriteYPosition[8 + temp0] = temp1
    }
    //> SChk4:  pla                         ;get bits from stack
    temp1 = pull()
    //> asl                         ;rotate d4 into carry
    temp1 = (temp1 shl 1) and 0xFF
    //> pha                         ;save to stack
    push(temp1)
    //> bcc SChk5
    if ((temp1 and 0x80) != 0) {
        //> lda #$f8                    ;if d4 was set, move fourth sprite offscreen
        temp1 = 0xF8
        //> sta Sprite_Y_Position+12,y
        spriteYPosition[12 + temp0] = temp1
    }
    //> SChk5:  pla                         ;get bits from stack
    temp1 = pull()
    //> asl                         ;rotate d3 into carry
    temp1 = (temp1 shl 1) and 0xFF
    //> pha                         ;save to stack
    push(temp1)
    //> bcc SChk6
    if ((temp1 and 0x80) != 0) {
        //> lda #$f8                    ;if d3 was set, move fifth sprite offscreen
        temp1 = 0xF8
        //> sta Sprite_Y_Position+16,y
        spriteYPosition[16 + temp0] = temp1
    }
    //> SChk6:  pla                         ;get bits from stack
    temp1 = pull()
    //> asl                         ;rotate d2 into carry
    temp1 = (temp1 shl 1) and 0xFF
    //> bcc SLChk                   ;save to stack
    if ((temp1 and 0x80) != 0) {
        //> lda #$f8
        temp1 = 0xF8
        //> sta Sprite_Y_Position+20,y  ;if d2 was set, move sixth sprite offscreen
        spriteYPosition[20 + temp0] = temp1
    }
    //> SLChk:  lda Enemy_OffscreenBits     ;check d7 of offscreen bits
    temp1 = enemyOffscreenbits
    //> asl                         ;and if d7 is not set, skip sub
    temp1 = (temp1 shl 1) and 0xFF
    //> bcc ExDLPl
    if ((temp1 and 0x80) != 0) {
        //> jsr MoveSixSpritesOffscreen ;otherwise branch to move all sprites offscreen
        moveSixSpritesOffscreen()
    }
    //> ExDLPl: rts
    return
}

// Decompiled from JCoinGfxHandler
fun jCoinGfxHandler(X: Int) {
    var Y: Int = 0
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var frameCounter by MemoryByte(FrameCounter)
    var miscRelXpos by MemoryByte(Misc_Rel_XPos)
    var objectOffset by MemoryByte(ObjectOffset)
    val jumpingCoinTiles by MemoryByteIndexed(JumpingCoinTiles)
    val miscSprdataoffset by MemoryByteIndexed(Misc_SprDataOffset)
    val miscState by MemoryByteIndexed(Misc_State)
    val miscYPosition by MemoryByteIndexed(Misc_Y_Position)
    val spriteAttributes by MemoryByteIndexed(Sprite_Attributes)
    val spriteTilenumber by MemoryByteIndexed(Sprite_Tilenumber)
    val spriteXPosition by MemoryByteIndexed(Sprite_X_Position)
    val spriteYPosition by MemoryByteIndexed(Sprite_Y_Position)
    //> DrawFloateyNumber_Coin:
    //> lda FrameCounter          ;get frame counter
    //> lsr                       ;divide by 2
    frameCounter = frameCounter shr 1
    //> bcs NotRsNum              ;branch if d0 not set to raise number every other frame
    if ((frameCounter and 0x01) != 0) {
        //  goto NotRsNum
        return
    }
    temp0 = frameCounter
    if ((frameCounter and 0x01) == 0) {
        //> dec Misc_Y_Position,x     ;otherwise, decrement vertical coordinate
        miscYPosition[X] = (miscYPosition[X] - 1) and 0xFF
    }
    //> NotRsNum: lda Misc_Y_Position,x     ;get vertical coordinate
    temp0 = miscYPosition[X]
    //> jsr DumpTwoSpr            ;dump into both sprites
    dumpTwoSpr(temp0, Y)
    //> lda Misc_Rel_XPos         ;get relative horizontal coordinate
    temp0 = miscRelXpos
    //> sta Sprite_X_Position,y   ;store as X coordinate for first sprite
    spriteXPosition[Y] = temp0
    //> clc
    //> adc #$08                  ;add eight pixels
    //> sta Sprite_X_Position+4,y ;store as X coordinate for second sprite
    spriteXPosition[4 + Y] = (temp0 + 0x08) and 0xFF
    //> lda #$02
    temp0 = 0x02
    //> sta Sprite_Attributes,y   ;store attribute byte in both sprites
    spriteAttributes[Y] = temp0
    //> sta Sprite_Attributes+4,y
    spriteAttributes[4 + Y] = temp0
    //> lda #$f7
    temp0 = 0xF7
    //> sta Sprite_Tilenumber,y   ;put tile numbers into both sprites
    spriteTilenumber[Y] = temp0
    //> lda #$fb                  ;that resemble "200"
    temp0 = 0xFB
    //> sta Sprite_Tilenumber+4,y
    spriteTilenumber[4 + Y] = temp0
    //> jmp ExJCGfx               ;then jump to leave (why not an rts here instead?)
    //> JCoinGfxHandler:
    //> ldy Misc_SprDataOffset,x    ;get coin/floatey number's OAM data offset
    //> lda Misc_State,x            ;get state of misc object
    temp0 = miscState[X]
    //> cmp #$02                    ;if 2 or greater,
    //> bcs DrawFloateyNumber_Coin  ;branch to draw floatey number
    //> lda Misc_Y_Position,x       ;store vertical coordinate as
    temp0 = miscYPosition[X]
    //> sta Sprite_Y_Position,y     ;Y coordinate for first sprite
    spriteYPosition[miscSprdataoffset[X]] = temp0
    //> clc
    //> adc #$08                    ;add eight pixels
    //> sta Sprite_Y_Position+4,y   ;store as Y coordinate for second sprite
    spriteYPosition[4 + miscSprdataoffset[X]] = (temp0 + 0x08) and 0xFF
    //> lda Misc_Rel_XPos           ;get relative horizontal coordinate
    temp0 = miscRelXpos
    //> sta Sprite_X_Position,y
    spriteXPosition[miscSprdataoffset[X]] = temp0
    //> sta Sprite_X_Position+4,y   ;store as X coordinate for first and second sprites
    spriteXPosition[4 + miscSprdataoffset[X]] = temp0
    //> lda FrameCounter            ;get frame counter
    temp0 = frameCounter
    //> lsr                         ;divide by 2 to alter every other frame
    temp0 = temp0 shr 1
    //> and #%00000011              ;mask out d2-d1
    temp1 = temp0 and 0x03
    //> tax                         ;use as graphical offset
    //> lda JumpingCoinTiles,x      ;load tile number
    temp0 = jumpingCoinTiles[temp1]
    //> iny                         ;increment OAM data offset to write tile numbers
    temp2 = miscSprdataoffset[X]
    temp2 = (temp2 + 1) and 0xFF
    //> jsr DumpTwoSpr              ;do sub to dump tile number into both sprites
    dumpTwoSpr(temp0, temp2)
    //> dey                         ;decrement to get old offset
    temp2 = (temp2 - 1) and 0xFF
    //> lda #$02
    temp0 = 0x02
    //> sta Sprite_Attributes,y     ;set attribute byte in first sprite
    spriteAttributes[temp2] = temp0
    //> lda #$82
    temp0 = 0x82
    //> sta Sprite_Attributes+4,y   ;set attribute byte with vertical flip in second sprite
    spriteAttributes[4 + temp2] = temp0
    //> ldx ObjectOffset            ;get misc object offset
    //> ExJCGfx: rts                         ;leave
    return
}

// Decompiled from DrawPowerUp
fun drawPowerUp() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var temp5: Int = 0
    var temp6: Int = 0
    var temp7: Int = 0
    var temp8: Int = 0
    var enemyOffscreenbits by MemoryByte(Enemy_OffscreenBits)
    var enemyRelXpos by MemoryByte(Enemy_Rel_XPos)
    var enemyRelYpos by MemoryByte(Enemy_Rel_YPos)
    var frameCounter by MemoryByte(FrameCounter)
    var objectOffset by MemoryByte(ObjectOffset)
    var powerUpType by MemoryByte(PowerUpType)
    val enemyId by MemoryByteIndexed(Enemy_ID)
    val enemySprattrib by MemoryByteIndexed(Enemy_SprAttrib)
    val enemySprdataoffset by MemoryByteIndexed(Enemy_SprDataOffset)
    val enemyYHighpos by MemoryByteIndexed(Enemy_Y_HighPos)
    val powerUpAttributes by MemoryByteIndexed(PowerUpAttributes)
    val powerUpGfxTable by MemoryByteIndexed(PowerUpGfxTable)
    val spriteAttributes by MemoryByteIndexed(Sprite_Attributes)
    //> DrawPowerUp:
    //> ldy Enemy_SprDataOffset+5  ;get power-up's sprite data offset
    //> lda Enemy_Rel_YPos         ;get relative vertical coordinate
    //> clc
    //> adc #$08                   ;add eight pixels
    //> sta $02                    ;store result here
    memory[0x2] = ((enemyRelYpos + 0x08) and 0xFF).toUByte()
    //> lda Enemy_Rel_XPos         ;get relative horizontal coordinate
    //> sta $05                    ;store here
    memory[0x5] = enemyRelXpos.toUByte()
    //> ldx PowerUpType            ;get power-up type
    //> lda PowerUpAttributes,x    ;get attribute data for power-up type
    //> ora Enemy_SprAttrib+5      ;add background priority bit if set
    temp0 = powerUpAttributes[powerUpType] or enemySprattrib[5]
    //> sta $04                    ;store attributes here
    memory[0x4] = temp0.toUByte()
    //> txa
    //> pha                        ;save power-up type to the stack
    push(powerUpType)
    //> asl
    powerUpType = (powerUpType shl 1) and 0xFF
    //> asl                        ;multiply by four to get proper offset
    powerUpType = (powerUpType shl 1) and 0xFF
    //> tax                        ;use as X
    //> lda #$01
    //> sta $07                    ;set counter here to draw two rows of sprite object
    memory[0x7] = 0x01.toUByte()
    //> sta $03                    ;init d1 of flip control
    memory[0x3] = 0x01.toUByte()
    temp1 = powerUpType
    temp2 = enemySprdataoffset[5]
    do {
        //> PUpDrawLoop:
        //> lda PowerUpGfxTable,x      ;load left tile of power-up object
        //> sta $00
        memory[0x0] = (powerUpGfxTable[temp1] and 0xFF).toUByte()
        //> lda PowerUpGfxTable+1,x    ;load right tile
        //> jsr DrawOneSpriteRow       ;branch to draw one row of our power-up object
        drawOneSpriteRow(powerUpGfxTable[1 + temp1], temp1, temp2)
        //> dec $07                    ;decrement counter
        memory[0x7] = ((memory[0x7].toInt() - 1) and 0xFF).toUByte()
        //> bpl PUpDrawLoop            ;branch until two rows are drawn
    } while (((memory[0x7].toInt() - 1) and 0xFF and 0x80) == 0)
    //> ldy Enemy_SprDataOffset+5  ;get sprite data offset again
    temp2 = enemySprdataoffset[5]
    //> pla                        ;pull saved power-up type from the stack
    temp3 = pull()
    //> beq PUpOfs                 ;if regular mushroom, branch, do not change colors or flip
    if (temp3 == 0) {
        //  goto PUpOfs
        return
    }
    temp4 = temp3
    if (temp3 != 0) {
        //> cmp #$03
        //> beq PUpOfs                 ;if 1-up mushroom, branch, do not change colors or flip
        if (temp4 - 0x03 == 0) {
            //  goto PUpOfs
            return
        }
        if (temp4 != 0x03) {
            //> sta $00                    ;store power-up type here now
            memory[0x0] = temp4.toUByte()
            //> lda FrameCounter           ;get frame counter
            temp4 = frameCounter
            //> lsr                        ;divide by 2 to change colors every two frames
            temp4 = temp4 shr 1
            //> and #%00000011             ;mask out all but d1 and d0 (previously d2 and d1)
            temp5 = temp4 and 0x03
            //> ora Enemy_SprAttrib+5      ;add background priority bit if any set
            temp6 = temp5 or enemySprattrib[5]
            //> sta Sprite_Attributes,y    ;set as new palette bits for top left and
            spriteAttributes[temp2] = temp6
            //> sta Sprite_Attributes+4,y  ;top right sprites for fire flower and star
            spriteAttributes[4 + temp2] = temp6
            //> ldx $00
            temp1 = memory[0x0].toInt()
            //> dex                        ;check power-up type for fire flower
            temp1 = (temp1 - 1) and 0xFF
            //> beq FlipPUpRightSide       ;if found, skip this part
            temp4 = temp6
            if (temp1 != 0) {
                //> sta Sprite_Attributes+8,y  ;otherwise set new palette bits  for bottom left
                spriteAttributes[8 + temp2] = temp4
                //> sta Sprite_Attributes+12,y ;and bottom right sprites as well for star only
                spriteAttributes[12 + temp2] = temp4
            }
            //> FlipPUpRightSide:
            //> lda Sprite_Attributes+4,y
            temp4 = spriteAttributes[4 + temp2]
            //> ora #%01000000             ;set horizontal flip bit for top right sprite
            temp7 = temp4 or 0x40
            //> sta Sprite_Attributes+4,y
            spriteAttributes[4 + temp2] = temp7
            //> lda Sprite_Attributes+12,y
            temp4 = spriteAttributes[12 + temp2]
            //> ora #%01000000             ;set horizontal flip bit for bottom right sprite
            temp8 = temp4 or 0x40
            //> sta Sprite_Attributes+12,y ;note these are only done for fire flower and star power-ups
            spriteAttributes[12 + temp2] = temp8
        }
    }
    //> PUpOfs: jmp SprObjectOffscrChk     ;jump to check to see if power-up is offscreen at all, then leave
    //> SprObjectOffscrChk:
    //> ldx ObjectOffset          ;get enemy buffer offset
    temp1 = objectOffset
    //> lda Enemy_OffscreenBits   ;check offscreen information
    temp4 = enemyOffscreenbits
    //> lsr
    temp4 = temp4 shr 1
    //> lsr                       ;shift three times to the right
    temp4 = temp4 shr 1
    //> lsr                       ;which puts d2 into carry
    temp4 = temp4 shr 1
    //> pha                       ;save to stack
    push(temp4)
    //> bcc LcChk                 ;branch if not set
    if ((temp4 and 0x01) != 0) {
        //> lda #$04                  ;set for right column sprites
        temp4 = 0x04
        //> jsr MoveESprColOffscreen  ;and move them offscreen
        moveESprColOffscreen(temp4, temp1)
    }
    //> LcChk:   pla                       ;get from stack
    temp4 = pull()
    //> lsr                       ;move d3 to carry
    temp4 = temp4 shr 1
    //> pha                       ;save to stack
    push(temp4)
    //> bcc Row3C                 ;branch if not set
    if ((temp4 and 0x01) != 0) {
        //> lda #$00                  ;set for left column sprites,
        temp4 = 0x00
        //> jsr MoveESprColOffscreen  ;move them offscreen
        moveESprColOffscreen(temp4, temp1)
    }
    //> Row3C:   pla                       ;get from stack again
    temp4 = pull()
    //> lsr                       ;move d5 to carry this time
    temp4 = temp4 shr 1
    //> lsr
    temp4 = temp4 shr 1
    //> pha                       ;save to stack again
    push(temp4)
    //> bcc Row23C                ;branch if carry not set
    if ((temp4 and 0x01) != 0) {
        //> lda #$10                  ;set for third row of sprites
        temp4 = 0x10
        //> jsr MoveESprRowOffscreen  ;and move them offscreen
        moveESprRowOffscreen(temp4, temp1)
    }
    //> Row23C:  pla                       ;get from stack
    temp4 = pull()
    //> lsr                       ;move d6 into carry
    temp4 = temp4 shr 1
    //> pha                       ;save to stack
    push(temp4)
    //> bcc AllRowC
    if ((temp4 and 0x01) != 0) {
        //> lda #$08                  ;set for second and third rows
        temp4 = 0x08
        //> jsr MoveESprRowOffscreen  ;move them offscreen
        moveESprRowOffscreen(temp4, temp1)
    }
    //> AllRowC: pla                       ;get from stack once more
    temp4 = pull()
    //> lsr                       ;move d7 into carry
    temp4 = temp4 shr 1
    //> bcc ExEGHandler
    if ((temp4 and 0x01) != 0) {
        //> jsr MoveESprRowOffscreen  ;move all sprites offscreen (A should be 0 by now)
        moveESprRowOffscreen(temp4, temp1)
        //> lda Enemy_ID,x
        temp4 = enemyId[temp1]
        //> cmp #Podoboo              ;check enemy identifier for podoboo
        //> beq ExEGHandler           ;skip this part if found, we do not want to erase podoboo!
        if (temp4 != Podoboo) {
            //> lda Enemy_Y_HighPos,x     ;check high byte of vertical position
            temp4 = enemyYHighpos[temp1]
            //> cmp #$02                  ;if not yet past the bottom of the screen, branch
            //> bne ExEGHandler
            if (temp4 == 0x02) {
                //> jsr EraseEnemyObject      ;what it says
                eraseEnemyObject(temp1)
            }
        }
    }
    //> ExEGHandler:
    //> rts
    return
}

// Decompiled from EnemyGfxHandler
fun enemyGfxHandler(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp10: Int = 0
    var temp11: Int = 0
    var temp12: Int = 0
    var temp13: Int = 0
    var temp14: Int = 0
    var temp15: Int = 0
    var temp16: Int = 0
    var temp17: Int = 0
    var temp18: Int = 0
    var temp19: Int = 0
    var temp2: Int = 0
    var temp20: Int = 0
    var temp21: Int = 0
    var temp22: Int = 0
    var temp23: Int = 0
    var temp24: Int = 0
    var temp25: Int = 0
    var temp26: Int = 0
    var temp27: Int = 0
    var temp28: Int = 0
    var temp29: Int = 0
    var temp3: Int = 0
    var temp30: Int = 0
    var temp31: Int = 0
    var temp4: Int = 0
    var temp5: Int = 0
    var temp6: Int = 0
    var temp7: Int = 0
    var temp8: Int = 0
    var temp9: Int = 0
    var bowserBodyControls by MemoryByte(BowserBodyControls)
    var bowserGfxFlag by MemoryByte(BowserGfxFlag)
    var enemyOffscreenbits by MemoryByte(Enemy_OffscreenBits)
    var enemyRelXpos by MemoryByte(Enemy_Rel_XPos)
    var frameCounter by MemoryByte(FrameCounter)
    var frenzyEnemyTimer by MemoryByte(FrenzyEnemyTimer)
    var jumpspringAnimCtrl by MemoryByte(JumpspringAnimCtrl)
    var objectOffset by MemoryByte(ObjectOffset)
    var timerControl by MemoryByte(TimerControl)
    var verticalFlipFlag by MemoryByte(VerticalFlipFlag)
    var worldNumber by MemoryByte(WorldNumber)
    val enemyAnimTimingBMask by MemoryByteIndexed(EnemyAnimTimingBMask)
    val enemyAttributeData by MemoryByteIndexed(EnemyAttributeData)
    val enemyFrameTimer by MemoryByteIndexed(EnemyFrameTimer)
    val enemyGfxTableOffsets by MemoryByteIndexed(EnemyGfxTableOffsets)
    val enemyIntervalTimer by MemoryByteIndexed(EnemyIntervalTimer)
    val enemyId by MemoryByteIndexed(Enemy_ID)
    val enemyMovingdir by MemoryByteIndexed(Enemy_MovingDir)
    val enemySprattrib by MemoryByteIndexed(Enemy_SprAttrib)
    val enemySprdataoffset by MemoryByteIndexed(Enemy_SprDataOffset)
    val enemyState by MemoryByteIndexed(Enemy_State)
    val enemyYHighpos by MemoryByteIndexed(Enemy_Y_HighPos)
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    val enemyYSpeed by MemoryByteIndexed(Enemy_Y_Speed)
    val jumpspringFrameOffsets by MemoryByteIndexed(JumpspringFrameOffsets)
    val piranhaplantYSpeed by MemoryByteIndexed(PiranhaPlant_Y_Speed)
    val spriteAttributes by MemoryByteIndexed(Sprite_Attributes)
    val spriteTilenumber by MemoryByteIndexed(Sprite_Tilenumber)
    //> EnemyGfxHandler:
    //> lda Enemy_Y_Position,x      ;get enemy object vertical position
    //> sta $02
    memory[0x2] = (enemyYPosition[X] and 0xFF).toUByte()
    //> lda Enemy_Rel_XPos          ;get enemy object horizontal position
    //> sta $05                     ;relative to screen
    memory[0x5] = enemyRelXpos.toUByte()
    //> ldy Enemy_SprDataOffset,x
    //> sty $eb                     ;get sprite data offset
    memory[0xEB] = (enemySprdataoffset[X] and 0xFF).toUByte()
    //> lda #$00
    //> sta VerticalFlipFlag        ;initialize vertical flip flag by default
    verticalFlipFlag = 0x00
    //> lda Enemy_MovingDir,x
    //> sta $03                     ;get enemy object moving direction
    memory[0x3] = (enemyMovingdir[X] and 0xFF).toUByte()
    //> lda Enemy_SprAttrib,x
    //> sta $04                     ;get enemy object sprite attributes
    memory[0x4] = (enemySprattrib[X] and 0xFF).toUByte()
    //> lda Enemy_ID,x
    //> cmp #PiranhaPlant           ;is enemy object piranha plant?
    //> bne CheckForRetainerObj     ;if not, branch
    temp0 = enemyId[X]
    temp1 = enemySprdataoffset[X]
    if (enemyId[X] == PiranhaPlant) {
        //> ldy PiranhaPlant_Y_Speed,x
        temp1 = piranhaplantYSpeed[X]
        //> bmi CheckForRetainerObj     ;if piranha plant moving upwards, branch
        if ((temp1 and 0x80) == 0) {
            //> ldy EnemyFrameTimer,x
            temp1 = enemyFrameTimer[X]
            //> beq CheckForRetainerObj     ;if timer for movement expired, branch
            if (temp1 != 0) {
                //> rts                         ;if all conditions fail, leave
                return
            }
        }
    }
    //> CheckForRetainerObj:
    //> lda Enemy_State,x           ;store enemy state
    temp0 = enemyState[X]
    //> sta $ed
    memory[0xED] = temp0.toUByte()
    //> and #%00011111              ;nullify all but 5 LSB and use as Y
    temp2 = temp0 and 0x1F
    //> tay
    //> lda Enemy_ID,x              ;check for mushroom retainer/princess object
    temp0 = enemyId[X]
    //> cmp #RetainerObject
    //> bne CheckForBulletBillCV    ;if not found, branch
    temp1 = temp2
    if (temp0 == RetainerObject) {
        //> ldy #$00                    ;if found, nullify saved state in Y
        temp1 = 0x00
        //> lda #$01                    ;set value that will not be used
        temp0 = 0x01
        //> sta $03
        memory[0x3] = temp0.toUByte()
        //> lda #$15                    ;set value $15 as code for mushroom retainer/princess object
        temp0 = 0x15
    }
    //> CheckForBulletBillCV:
    //> cmp #BulletBill_CannonVar   ;otherwise check for bullet bill object
    //> bne CheckForJumpspring      ;if not found, branch again
    if (temp0 == BulletBill_CannonVar) {
        //> dec $02                     ;decrement saved vertical position
        memory[0x2] = ((memory[0x2].toInt() - 1) and 0xFF).toUByte()
        //> lda #$03
        temp0 = 0x03
        //> ldy EnemyFrameTimer,x       ;get timer for enemy object
        temp1 = enemyFrameTimer[X]
        //> beq SBBAt                   ;if expired, do not set priority bit
        if (temp1 != 0) {
            //> ora #%00100000              ;otherwise do so
            temp3 = temp0 or 0x20
        }
        //> SBBAt: sta $04                     ;set new sprite attributes
        memory[0x4] = temp0.toUByte()
        //> ldy #$00                    ;nullify saved enemy state both in Y and in
        temp1 = 0x00
        //> sty $ed                     ;memory location here
        memory[0xED] = temp1.toUByte()
        //> lda #$08                    ;set specific value to unconditionally branch once
        temp0 = 0x08
    }
    //> CheckForJumpspring:
    //> cmp #JumpspringObject        ;check for jumpspring object
    //> bne CheckForPodoboo
    if (temp0 == JumpspringObject) {
        //> ldy #$03                     ;set enemy state -2 MSB here for jumpspring object
        temp1 = 0x03
        //> ldx JumpspringAnimCtrl       ;get current frame number for jumpspring object
        //> lda JumpspringFrameOffsets,x ;load data using frame number as offset
        temp0 = jumpspringFrameOffsets[jumpspringAnimCtrl]
    }
    //> CheckForPodoboo:
    //> sta $ef                 ;store saved enemy object value here
    memory[0xEF] = temp0.toUByte()
    //> sty $ec                 ;and Y here (enemy state -2 MSB if not changed)
    memory[0xEC] = temp1.toUByte()
    //> ldx ObjectOffset        ;get enemy object offset
    //> cmp #$0c                ;check for podoboo object
    //> bne CheckBowserGfxFlag  ;branch if not found
    temp4 = objectOffset
    if (temp0 == 0x0C) {
        //> lda Enemy_Y_Speed,x     ;if moving upwards, branch
        temp0 = enemyYSpeed[temp4]
        //> bmi CheckBowserGfxFlag
        if ((temp0 and 0x80) == 0) {
            //> inc VerticalFlipFlag    ;otherwise, set flag for vertical flip
            verticalFlipFlag = (verticalFlipFlag + 1) and 0xFF
        }
    }
    //> CheckBowserGfxFlag:
    //> lda BowserGfxFlag   ;if not drawing bowser at all, skip to something else
    temp0 = bowserGfxFlag
    //> beq CheckForGoomba
    if (temp0 != 0) {
        //> ldy #$16            ;if set to 1, draw bowser's front
        temp1 = 0x16
        //> cmp #$01
        //> beq SBwsrGfxOfs
        if (temp0 != 0x01) {
            //> iny                 ;otherwise draw bowser's rear
            temp1 = (temp1 + 1) and 0xFF
        }
        //> SBwsrGfxOfs: sty $ef
        memory[0xEF] = temp1.toUByte()
    }
    //> CheckForGoomba:
    //> ldy $ef               ;check value for goomba object
    temp1 = memory[0xEF].toInt()
    //> cpy #Goomba
    //> bne CheckBowserFront  ;branch if not found
    if (temp1 == Goomba) {
        //> lda Enemy_State,x
        temp0 = enemyState[temp4]
        //> cmp #$02              ;check for defeated state
        //> bcc GmbaAnim          ;if not defeated, go ahead and animate
        if (temp0 >= 0x02) {
            //> ldx #$04              ;if defeated, write new value here
            temp4 = 0x04
            //> stx $ec
            memory[0xEC] = temp4.toUByte()
        }
        //> GmbaAnim: and #%00100000        ;check for d5 set in enemy object state
        temp5 = temp0 and 0x20
        //> ora TimerControl      ;or timer disable flag set
        temp6 = temp5 or timerControl
        //> bne CheckBowserFront  ;if either condition true, do not animate goomba
        temp0 = temp6
        if (temp6 == 0) {
            //> lda FrameCounter
            temp0 = frameCounter
            //> and #%00001000        ;check for every eighth frame
            temp7 = temp0 and 0x08
            //> bne CheckBowserFront
            temp0 = temp7
            if (temp7 == 0) {
                //> lda $03
                temp0 = memory[0x3].toInt()
                //> eor #%00000011        ;invert bits to flip horizontally every eight frames
                temp8 = temp0 xor 0x03
                //> sta $03               ;leave alone otherwise
                memory[0x3] = temp8.toUByte()
            }
        }
    }
    //> CheckBowserFront:
    //> lda EnemyAttributeData,y    ;load sprite attribute using enemy object
    temp0 = enemyAttributeData[temp1]
    //> ora $04                     ;as offset, and add to bits already loaded
    temp9 = temp0 or memory[0x4].toInt()
    //> sta $04
    memory[0x4] = temp9.toUByte()
    //> lda EnemyGfxTableOffsets,y  ;load value based on enemy object as offset
    temp0 = enemyGfxTableOffsets[temp1]
    //> tax                         ;save as X
    //> ldy $ec                     ;get previously saved value
    temp1 = memory[0xEC].toInt()
    //> lda BowserGfxFlag
    temp0 = bowserGfxFlag
    //> beq CheckForSpiny           ;if not drawing bowser object at all, skip all of this
    temp4 = temp0
    if (temp0 != 0) {
        //> cmp #$01
        //> bne CheckBowserRear         ;if not drawing front part, branch to draw the rear part
        if (temp0 == 0x01) {
            //> lda BowserBodyControls      ;check bowser's body control bits
            temp0 = bowserBodyControls
            //> bpl ChkFrontSte             ;branch if d7 not set (control's bowser's mouth)
            if ((temp0 and 0x80) != 0) {
                //> ldx #$de                    ;otherwise load offset for second frame
                temp4 = 0xDE
            }
            //> ChkFrontSte: lda $ed                     ;check saved enemy state
            temp0 = memory[0xED].toInt()
            //> and #%00100000              ;if bowser not defeated, do not set flag
            temp10 = temp0 and 0x20
            //> beq DrawBowser
            if (temp10 == 0) {
                //  goto DrawBowser
                return
            }
            temp0 = temp10
            if (temp10 != 0) {
                //> FlipBowserOver:
                //> stx VerticalFlipFlag  ;set vertical flip flag to nonzero
                verticalFlipFlag = temp4
            }
            //> DrawBowser:
            //> jmp DrawEnemyObject   ;draw bowser's graphics now
        }
        //> CheckBowserRear:
        //> lda BowserBodyControls  ;check bowser's body control bits
        temp0 = bowserBodyControls
        //> and #$01
        temp11 = temp0 and 0x01
        //> beq ChkRearSte          ;branch if d0 not set (control's bowser's feet)
        temp0 = temp11
        if (temp11 != 0) {
            //> ldx #$e4                ;otherwise load offset for second frame
            temp4 = 0xE4
        }
        //> ChkRearSte: lda $ed                 ;check saved enemy state
        temp0 = memory[0xED].toInt()
        //> and #%00100000          ;if bowser not defeated, do not set flag
        temp12 = temp0 and 0x20
        //> beq DrawBowser
        if (temp12 == 0) {
            //  goto DrawBowser
            return
        }
        // Bowser defeated - subtract 16 from vertical coordinate and flip
        //> lda $02                 ;subtract 16 pixels from
        temp0 = memory[0x2].toInt()
        //> sec                     ;saved vertical coordinate
        //> sbc #$10
        //> sta $02
        memory[0x2] = ((temp0 - 0x10) and 0xFF).toUByte()
        //> jmp FlipBowserOver      ;jump to set vertical flip flag
        // FlipBowserOver handled elsewhere - return for now
        return
    }
    //> CheckForSpiny:
    //> cpx #$24               ;check if value loaded is for spiny
    //> bne CheckForLakitu     ;if not found, branch
    if (temp4 == 0x24) {
        //> cpy #$05               ;if enemy state set to $05, do this,
        //> bne NotEgg             ;otherwise branch
        if (!(temp1 == 0x05)) {
            //  goto NotEgg
            return
        }
        if (temp1 == 0x05) {
            //> ldx #$30               ;set to spiny egg offset
            temp4 = 0x30
            //> lda #$02
            temp0 = 0x02
            //> sta $03                ;set enemy direction to reverse sprites horizontally
            memory[0x3] = temp0.toUByte()
            //> lda #$05
            temp0 = 0x05
            //> sta $ec                ;set enemy state
            memory[0xEC] = temp0.toUByte()
        }
    } else {
        //> CheckForLakitu:
        //> cpx #$90                  ;check value for lakitu's offset loaded
        //> bne CheckUpsideDownShell  ;branch if not loaded
        if (temp4 == 0x90) {
            //> lda $ed
            temp0 = memory[0xED].toInt()
            //> and #%00100000            ;check for d5 set in enemy state
            temp13 = temp0 and 0x20
            //> bne NoLAFr                ;branch if set
            if (!(temp13 == 0)) {
                //  goto NoLAFr
                return
            }
            temp0 = temp13
            if (temp13 == 0) {
                //> lda FrenzyEnemyTimer
                temp0 = frenzyEnemyTimer
                //> cmp #$10                  ;check timer to see if we've reached a certain range
                //> bcs NoLAFr                ;branch if not
                if (temp0 >= 0x10) {
                    //  goto NoLAFr
                    return
                }
                if (!(temp0 >= 0x10)) {
                    //> ldx #$96                  ;if d6 not set and timer in range, load alt frame for lakitu
                    temp4 = 0x96
                }
            }
            //> NoLAFr: jmp CheckDefeatedState    ;skip this next part if we found lakitu but alt frame not needed
        }
        //> CheckUpsideDownShell:
        //> lda $ef                    ;check for enemy object => $04
        temp0 = memory[0xEF].toInt()
        //> cmp #$04
        //> bcs CheckRightSideUpShell  ;branch if true
        if (!(temp0 >= 0x04)) {
            //> cpy #$02
            //> bcc CheckRightSideUpShell  ;branch if enemy state < $02
            if (temp1 >= 0x02) {
                //> ldx #$5a                   ;set for upside-down koopa shell by default
                temp4 = 0x5A
                //> ldy $ef
                temp1 = memory[0xEF].toInt()
                //> cpy #BuzzyBeetle           ;check for buzzy beetle object
                //> bne CheckRightSideUpShell
                if (temp1 == BuzzyBeetle) {
                    //> ldx #$7e                   ;set for upside-down buzzy beetle shell if found
                    temp4 = 0x7E
                    //> inc $02                    ;increment vertical position by one pixel
                    memory[0x2] = ((memory[0x2].toInt() + 1) and 0xFF).toUByte()
                }
            }
        }
        //> CheckRightSideUpShell:
        //> lda $ec                ;check for value set here
        temp0 = memory[0xEC].toInt()
        //> cmp #$04               ;if enemy state < $02, do not change to shell, if
        //> bne CheckForHammerBro  ;enemy state => $02 but not = $04, leave shell upside-down
        if (temp0 == 0x04) {
            //> ldx #$72               ;set right-side up buzzy beetle shell by default
            temp4 = 0x72
            //> inc $02                ;increment saved vertical position by one pixel
            memory[0x2] = ((memory[0x2].toInt() + 1) and 0xFF).toUByte()
            //> ldy $ef
            temp1 = memory[0xEF].toInt()
            //> cpy #BuzzyBeetle       ;check for buzzy beetle object
            //> beq CheckForDefdGoomba ;branch if found
            if (temp1 != BuzzyBeetle) {
                //> ldx #$66               ;change to right-side up koopa shell if not found
                temp4 = 0x66
                //> inc $02                ;and increment saved vertical position again
                memory[0x2] = ((memory[0x2].toInt() + 1) and 0xFF).toUByte()
            }
            //> CheckForDefdGoomba:
            //> cpy #Goomba            ;check for goomba object (necessary if previously
            //> bne CheckForHammerBro  ;failed buzzy beetle object test)
            if (temp1 == Goomba) {
                //> ldx #$54               ;load for regular goomba
                temp4 = 0x54
                //> lda $ed                ;note that this only gets performed if enemy state => $02
                temp0 = memory[0xED].toInt()
                //> and #%00100000         ;check saved enemy state for d5 set
                temp14 = temp0 and 0x20
                //> bne CheckForHammerBro  ;branch if set
                temp0 = temp14
                if (temp14 == 0) {
                    //> ldx #$8a               ;load offset for defeated goomba
                    temp4 = 0x8A
                    //> dec $02                ;set different value and decrement saved vertical position
                    memory[0x2] = ((memory[0x2].toInt() - 1) and 0xFF).toUByte()
                }
            }
        }
    }
    //> CheckForHammerBro:
    //> ldy ObjectOffset
    temp1 = objectOffset
    //> lda $ef                  ;check for hammer bro object
    temp0 = memory[0xEF].toInt()
    //> cmp #HammerBro
    //> bne CheckForBloober      ;branch if not found
    if (temp0 == HammerBro) {
        //> lda $ed
        temp0 = memory[0xED].toInt()
        //> beq CheckToAnimateEnemy  ;branch if not in normal enemy state
        if (temp0 != 0) {
            //> and #%00001000
            temp15 = temp0 and 0x08
            //> beq CheckDefeatedState   ;if d3 not set, branch further away
            temp0 = temp15
            if (temp15 != 0) {
                //> ldx #$b4                 ;otherwise load offset for different frame
                temp4 = 0xB4
                //> bne CheckToAnimateEnemy  ;unconditional branch
                if (temp4 == 0) {
                }
            }
        }
    }
    //> CheckForBloober:
    //> cpx #$48                 ;check for cheep-cheep offset loaded
    //> beq CheckToAnimateEnemy  ;branch if found
    if (temp4 != 0x48) {
        //> lda EnemyIntervalTimer,y
        temp0 = enemyIntervalTimer[temp1]
        //> cmp #$05
        //> bcs CheckDefeatedState   ;branch if some timer is above a certain point
        if (!(temp0 >= 0x05)) {
            //> cpx #$3c                 ;check for bloober offset loaded
            //> bne CheckToAnimateEnemy  ;branch if not found this time
            if (temp4 == 0x3C) {
                //> cmp #$01
                //> beq CheckDefeatedState   ;branch if timer is set to certain point
                if (temp0 != 0x01) {
                    //> inc $02                  ;increment saved vertical coordinate three pixels
                    memory[0x2] = ((memory[0x2].toInt() + 1) and 0xFF).toUByte()
                    //> inc $02
                    memory[0x2] = ((memory[0x2].toInt() + 1) and 0xFF).toUByte()
                    //> inc $02
                    memory[0x2] = ((memory[0x2].toInt() + 1) and 0xFF).toUByte()
                    //> jmp CheckAnimationStop   ;and do something else
                }
            }
        }
    } else {
        //> CheckToAnimateEnemy:
        //> lda $ef                  ;check for specific enemy objects
        temp0 = memory[0xEF].toInt()
        //> cmp #Goomba
        //> beq CheckDefeatedState   ;branch if goomba
        if (temp0 != Goomba) {
            //> cmp #$08
            //> beq CheckDefeatedState   ;branch if bullet bill (note both variants use $08 here)
            if (temp0 != 0x08) {
                //> cmp #Podoboo
                //> beq CheckDefeatedState   ;branch if podoboo
                if (temp0 != Podoboo) {
                    //> cmp #$18                 ;branch if => $18
                    //> bcs CheckDefeatedState
                    if (!(temp0 >= 0x18)) {
                        //> ldy #$00
                        temp1 = 0x00
                        //> cmp #$15                 ;check for mushroom retainer/princess object
                        //> bne CheckForSecondFrame  ;which uses different code here, branch if not found
                        if (temp0 == 0x15) {
                            //> iny                      ;residual instruction
                            temp1 = (temp1 + 1) and 0xFF
                            //> lda WorldNumber          ;are we on world 8?
                            temp0 = worldNumber
                            //> cmp #World8
                            //> bcs CheckDefeatedState   ;if so, leave the offset alone (use princess)
                            if (!(temp0 >= World8)) {
                                //> ldx #$a2                 ;otherwise, set for mushroom retainer object instead
                                temp4 = 0xA2
                                //> lda #$03                 ;set alternate state here
                                temp0 = 0x03
                                //> sta $ec
                                memory[0xEC] = temp0.toUByte()
                                //> bne CheckDefeatedState   ;unconditional branch
                                if (temp0 == 0) {
                                }
                            }
                        }
                        //> CheckForSecondFrame:
                        //> lda FrameCounter            ;load frame counter
                        temp0 = frameCounter
                        //> and EnemyAnimTimingBMask,y  ;mask it (partly residual, one byte not ever used)
                        temp16 = temp0 and enemyAnimTimingBMask[temp1]
                        //> bne CheckDefeatedState      ;branch if timing is off
                        temp0 = temp16
                        if (temp16 == 0) {
                        }
                    }
                }
            }
        }
    }
    //> CheckAnimationStop:
    //> lda $ed                 ;check saved enemy state
    temp0 = memory[0xED].toInt()
    //> and #%10100000          ;for d7 or d5, or check for timers stopped
    temp17 = temp0 and 0xA0
    //> ora TimerControl
    temp18 = temp17 or timerControl
    //> bne CheckDefeatedState  ;if either condition true, branch
    temp0 = temp18
    if (temp18 == 0) {
        //> txa
        //> clc
        //> adc #$06                ;add $06 to current enemy offset
        //> tax                     ;to animate various enemy objects
    }
    //> CheckDefeatedState:
    //> lda $ed               ;check saved enemy state
    temp0 = memory[0xED].toInt()
    //> and #%00100000        ;for d5 set
    temp19 = temp0 and 0x20
    //> beq DrawEnemyObject   ;branch if not set
    temp0 = temp19
    if (temp19 != 0) {
        //> lda $ef
        temp0 = memory[0xEF].toInt()
        //> cmp #$04              ;check for saved enemy object => $04
        //> bcc DrawEnemyObject   ;branch if less
        if (temp0 >= 0x04) {
            //> ldy #$01
            temp1 = 0x01
            //> sty VerticalFlipFlag  ;set vertical flip flag
            verticalFlipFlag = temp1
            //> dey
            temp1 = (temp1 - 1) and 0xFF
            //> sty $ec               ;init saved value here
            memory[0xEC] = temp1.toUByte()
        }
    }
    //> DrawEnemyObject:
    //> ldy $eb                    ;load sprite data offset
    temp1 = memory[0xEB].toInt()
    //> jsr DrawEnemyObjRow        ;draw six tiles of data
    drawEnemyObjRow(temp4)
    //> jsr DrawEnemyObjRow        ;into sprite data
    drawEnemyObjRow(temp4)
    //> jsr DrawEnemyObjRow
    drawEnemyObjRow(temp4)
    //> ldx ObjectOffset           ;get enemy object offset
    temp4 = objectOffset
    //> ldy Enemy_SprDataOffset,x  ;get sprite data offset
    temp1 = enemySprdataoffset[temp4]
    //> lda $ef
    temp0 = memory[0xEF].toInt()
    //> cmp #$08                   ;get saved enemy object and check
    //> bne CheckForVerticalFlip   ;for bullet bill, branch if not found
    if (temp0 == 0x08) {
    } else {
        //> CheckForVerticalFlip:
        //> lda VerticalFlipFlag       ;check if vertical flip flag is set here
        temp0 = verticalFlipFlag
        //> beq CheckForESymmetry      ;branch if not
        if (temp0 != 0) {
            //> lda Sprite_Attributes,y    ;get attributes of first sprite we dealt with
            temp0 = spriteAttributes[temp1]
            //> ora #%10000000             ;set bit for vertical flip
            temp20 = temp0 or 0x80
            //> iny
            temp1 = (temp1 + 1) and 0xFF
            //> iny                        ;increment two bytes so that we store the vertical flip
            temp1 = (temp1 + 1) and 0xFF
            //> jsr DumpSixSpr             ;in attribute bytes of enemy obj sprite data
            dumpSixSpr(temp20, temp1)
            //> dey
            temp1 = (temp1 - 1) and 0xFF
            //> dey                        ;now go back to the Y coordinate offset
            temp1 = (temp1 - 1) and 0xFF
            //> tya
            //> tax                        ;give offset to X
            //> lda $ef
            temp0 = memory[0xEF].toInt()
            //> cmp #HammerBro             ;check saved enemy object for hammer bro
            //> beq FlipEnemyVertically
            temp4 = temp1
            if (temp0 != HammerBro) {
                //> cmp #Lakitu                ;check saved enemy object for lakitu
                //> beq FlipEnemyVertically    ;branch for hammer bro or lakitu
                if (temp0 != Lakitu) {
                    //> cmp #$15
                    //> bcs FlipEnemyVertically    ;also branch if enemy object => $15
                    if (!(temp0 >= 0x15)) {
                        //> txa
                        //> clc
                        //> adc #$08                   ;if not selected objects or => $15, set
                        //> tax                        ;offset in X for next row
                    }
                }
            }
            //> FlipEnemyVertically:
            //> lda Sprite_Tilenumber,x     ;load first or second row tiles
            temp0 = spriteTilenumber[temp4]
            //> pha                         ;and save tiles to the stack
            push(temp0)
            //> lda Sprite_Tilenumber+4,x
            temp0 = spriteTilenumber[4 + temp4]
            //> pha
            push(temp0)
            //> lda Sprite_Tilenumber+16,y  ;exchange third row tiles
            temp0 = spriteTilenumber[16 + temp1]
            //> sta Sprite_Tilenumber,x     ;with first or second row tiles
            spriteTilenumber[temp4] = temp0
            //> lda Sprite_Tilenumber+20,y
            temp0 = spriteTilenumber[20 + temp1]
            //> sta Sprite_Tilenumber+4,x
            spriteTilenumber[4 + temp4] = temp0
            //> pla                         ;pull first or second row tiles from stack
            temp0 = pull()
            //> sta Sprite_Tilenumber+20,y  ;and save in third row
            spriteTilenumber[20 + temp1] = temp0
            //> pla
            temp0 = pull()
            //> sta Sprite_Tilenumber+16,y
            spriteTilenumber[16 + temp1] = temp0
        }
        //> CheckForESymmetry:
        //> lda BowserGfxFlag           ;are we drawing bowser at all?
        temp0 = bowserGfxFlag
        //> bne SkipToOffScrChk         ;branch if so
        if (!(temp0 == 0)) {
            //  goto SkipToOffScrChk
            return
        }
        //> lda $ef
        temp0 = memory[0xEF].toInt()
        //> ldx $ec                     ;get alternate enemy state
        temp4 = memory[0xEC].toInt()
        //> cmp #$05                    ;check for hammer bro object
        //> bne ContES
        if (temp0 == 0x05) {
        } else {
            //> ContES: cmp #Bloober                ;check for bloober object
            //> beq MirrorEnemyGfx
            if (temp0 != Bloober) {
                //> cmp #PiranhaPlant           ;check for piranha plant object
                //> beq MirrorEnemyGfx
                if (temp0 != PiranhaPlant) {
                    //> cmp #Podoboo                ;check for podoboo object
                    //> beq MirrorEnemyGfx          ;branch if either of three are found
                    if (temp0 != Podoboo) {
                        //> cmp #Spiny                  ;check for spiny object
                        //> bne ESRtnr                  ;branch closer if not found
                        if (temp0 == Spiny) {
                            //> cpx #$05                    ;check spiny's state
                            //> bne CheckToMirrorLakitu     ;branch if not an egg, otherwise
                            if (temp4 == 0x05) {
                            }
                        }
                        //> ESRtnr: cmp #$15                    ;check for princess/mushroom retainer object
                        //> bne SpnySC
                        if (temp0 == 0x15) {
                            //> lda #$42                    ;set horizontal flip on bottom right sprite
                            temp0 = 0x42
                            //> sta Sprite_Attributes+20,y  ;note that palette bits were already set earlier
                            spriteAttributes[20 + temp1] = temp0
                        }
                        //> SpnySC: cpx #$02                    ;if alternate enemy state set to 1 or 0, branch
                        //> bcc CheckToMirrorLakitu
                        if (temp4 >= 0x02) {
                        }
                    }
                }
            }
            //> MirrorEnemyGfx:
            //> lda BowserGfxFlag           ;if enemy object is bowser, skip all of this
            temp0 = bowserGfxFlag
            //> bne CheckToMirrorLakitu
            if (temp0 == 0) {
                //> lda Sprite_Attributes,y     ;load attribute bits of first sprite
                temp0 = spriteAttributes[temp1]
                //> and #%10100011
                temp21 = temp0 and 0xA3
                //> sta Sprite_Attributes,y     ;save vertical flip, priority, and palette bits
                spriteAttributes[temp1] = temp21
                //> sta Sprite_Attributes+8,y   ;in left sprite column of enemy object OAM data
                spriteAttributes[8 + temp1] = temp21
                //> sta Sprite_Attributes+16,y
                spriteAttributes[16 + temp1] = temp21
                //> ora #%01000000              ;set horizontal flip
                temp22 = temp21 or 0x40
                //> cpx #$05                    ;check for state used by spiny's egg
                //> bne EggExc                  ;if alternate state not set to $05, branch
                temp0 = temp22
                if (temp4 == 0x05) {
                    //> ora #%10000000              ;otherwise set vertical flip
                    temp23 = temp0 or 0x80
                }
                //> EggExc: sta Sprite_Attributes+4,y   ;set bits of right sprite column
                spriteAttributes[4 + temp1] = temp0
                //> sta Sprite_Attributes+12,y  ;of enemy object sprite data
                spriteAttributes[12 + temp1] = temp0
                //> sta Sprite_Attributes+20,y
                spriteAttributes[20 + temp1] = temp0
                //> cpx #$04                    ;check alternate enemy state
                //> bne CheckToMirrorLakitu     ;branch if not $04
                if (temp4 == 0x04) {
                    //> lda Sprite_Attributes+8,y   ;get second row left sprite attributes
                    temp0 = spriteAttributes[8 + temp1]
                    //> ora #%10000000
                    temp24 = temp0 or 0x80
                    //> sta Sprite_Attributes+8,y   ;store bits with vertical flip in
                    spriteAttributes[8 + temp1] = temp24
                    //> sta Sprite_Attributes+16,y  ;second and third row left sprites
                    spriteAttributes[16 + temp1] = temp24
                    //> ora #%01000000
                    temp25 = temp24 or 0x40
                    //> sta Sprite_Attributes+12,y  ;store with horizontal and vertical flip in
                    spriteAttributes[12 + temp1] = temp25
                    //> sta Sprite_Attributes+20,y  ;second and third row right sprites
                    spriteAttributes[20 + temp1] = temp25
                }
            }
            //> CheckToMirrorLakitu:
            //> lda $ef                     ;check for lakitu enemy object
            temp0 = memory[0xEF].toInt()
            //> cmp #Lakitu
            //> bne CheckToMirrorJSpring    ;branch if not found
            if (temp0 == Lakitu) {
                //> lda VerticalFlipFlag
                temp0 = verticalFlipFlag
                //> bne NVFLak                  ;branch if vertical flip flag not set
                if (temp0 == 0) {
                    //> lda Sprite_Attributes+16,y  ;save vertical flip and palette bits
                    temp0 = spriteAttributes[16 + temp1]
                    //> and #%10000001              ;in third row left sprite
                    temp26 = temp0 and 0x81
                    //> sta Sprite_Attributes+16,y
                    spriteAttributes[16 + temp1] = temp26
                    //> lda Sprite_Attributes+20,y  ;set horizontal flip and palette bits
                    temp0 = spriteAttributes[20 + temp1]
                    //> ora #%01000001              ;in third row right sprite
                    temp27 = temp0 or 0x41
                    //> sta Sprite_Attributes+20,y
                    spriteAttributes[20 + temp1] = temp27
                    //> ldx FrenzyEnemyTimer        ;check timer
                    temp4 = frenzyEnemyTimer
                    //> cpx #$10
                    //> bcs SprObjectOffscrChk      ;branch if timer has not reached a certain range
                    if (temp4 >= 0x10) {
                        //  goto SprObjectOffscrChk
                        return
                    }
                    temp0 = temp27
                    if (!(temp4 >= 0x10)) {
                        //> sta Sprite_Attributes+12,y  ;otherwise set same for second row right sprite
                        spriteAttributes[12 + temp1] = temp0
                        //> and #%10000001
                        temp28 = temp0 and 0x81
                        //> sta Sprite_Attributes+8,y   ;preserve vertical flip and palette bits for left sprite
                        spriteAttributes[8 + temp1] = temp28
                        //> bcc SprObjectOffscrChk      ;unconditional branch
                        if (!(temp4 >= 0x10)) {
                            //  goto SprObjectOffscrChk
                            return
                        }
                        temp0 = temp28
                        if (temp4 >= 0x10) {
                        }
                    }
                }
                //> NVFLak: lda Sprite_Attributes,y     ;get first row left sprite attributes
                temp0 = spriteAttributes[temp1]
                //> and #%10000001
                temp29 = temp0 and 0x81
                //> sta Sprite_Attributes,y     ;save vertical flip and palette bits
                spriteAttributes[temp1] = temp29
                //> lda Sprite_Attributes+4,y   ;get first row right sprite attributes
                temp0 = spriteAttributes[4 + temp1]
                //> ora #%01000001              ;set horizontal flip and palette bits
                temp30 = temp0 or 0x41
                //> sta Sprite_Attributes+4,y   ;note that vertical flip is left as-is
                spriteAttributes[4 + temp1] = temp30
            }
            //> CheckToMirrorJSpring:
            //> lda $ef                     ;check for jumpspring object (any frame)
            temp0 = memory[0xEF].toInt()
            //> cmp #$18
            //> bcc SprObjectOffscrChk      ;branch if not jumpspring object at all
            if (!(temp0 >= 0x18)) {
                //  goto SprObjectOffscrChk
                return
            }
            if (temp0 >= 0x18) {
                //> lda #$82
                temp0 = 0x82
                //> sta Sprite_Attributes+8,y   ;set vertical flip and palette bits of
                spriteAttributes[8 + temp1] = temp0
                //> sta Sprite_Attributes+16,y  ;second and third row left sprites
                spriteAttributes[16 + temp1] = temp0
                //> ora #%01000000
                temp31 = temp0 or 0x40
                //> sta Sprite_Attributes+12,y  ;set, in addition to those, horizontal flip
                spriteAttributes[12 + temp1] = temp31
                //> sta Sprite_Attributes+20,y  ;for second and third row right sprites
                spriteAttributes[20 + temp1] = temp31
            }
        }
    }
    //> SprObjectOffscrChk:
    //> ldx ObjectOffset          ;get enemy buffer offset
    temp4 = objectOffset
    //> lda Enemy_OffscreenBits   ;check offscreen information
    temp0 = enemyOffscreenbits
    //> lsr
    temp0 = temp0 shr 1
    //> lsr                       ;shift three times to the right
    temp0 = temp0 shr 1
    //> lsr                       ;which puts d2 into carry
    temp0 = temp0 shr 1
    //> pha                       ;save to stack
    push(temp0)
    //> bcc LcChk                 ;branch if not set
    if ((temp0 and 0x01) != 0) {
        //> lda #$04                  ;set for right column sprites
        temp0 = 0x04
        //> jsr MoveESprColOffscreen  ;and move them offscreen
        moveESprColOffscreen(temp0, temp4)
    }
    //> LcChk:   pla                       ;get from stack
    temp0 = pull()
    //> lsr                       ;move d3 to carry
    temp0 = temp0 shr 1
    //> pha                       ;save to stack
    push(temp0)
    //> bcc Row3C                 ;branch if not set
    if ((temp0 and 0x01) != 0) {
        //> lda #$00                  ;set for left column sprites,
        temp0 = 0x00
        //> jsr MoveESprColOffscreen  ;move them offscreen
        moveESprColOffscreen(temp0, temp4)
    }
    //> Row3C:   pla                       ;get from stack again
    temp0 = pull()
    //> lsr                       ;move d5 to carry this time
    temp0 = temp0 shr 1
    //> lsr
    temp0 = temp0 shr 1
    //> pha                       ;save to stack again
    push(temp0)
    //> bcc Row23C                ;branch if carry not set
    if ((temp0 and 0x01) != 0) {
        //> lda #$10                  ;set for third row of sprites
        temp0 = 0x10
        //> jsr MoveESprRowOffscreen  ;and move them offscreen
        moveESprRowOffscreen(temp0, temp4)
    }
    //> Row23C:  pla                       ;get from stack
    temp0 = pull()
    //> lsr                       ;move d6 into carry
    temp0 = temp0 shr 1
    //> pha                       ;save to stack
    push(temp0)
    //> bcc AllRowC
    if ((temp0 and 0x01) != 0) {
        //> lda #$08                  ;set for second and third rows
        temp0 = 0x08
        //> jsr MoveESprRowOffscreen  ;move them offscreen
        moveESprRowOffscreen(temp0, temp4)
    }
    //> AllRowC: pla                       ;get from stack once more
    temp0 = pull()
    //> lsr                       ;move d7 into carry
    temp0 = temp0 shr 1
    //> bcc ExEGHandler
    if ((temp0 and 0x01) != 0) {
        //> jsr MoveESprRowOffscreen  ;move all sprites offscreen (A should be 0 by now)
        moveESprRowOffscreen(temp0, temp4)
        //> lda Enemy_ID,x
        temp0 = enemyId[temp4]
        //> cmp #Podoboo              ;check enemy identifier for podoboo
        //> beq ExEGHandler           ;skip this part if found, we do not want to erase podoboo!
        if (temp0 != Podoboo) {
            //> lda Enemy_Y_HighPos,x     ;check high byte of vertical position
            temp0 = enemyYHighpos[temp4]
            //> cmp #$02                  ;if not yet past the bottom of the screen, branch
            //> bne ExEGHandler
            if (temp0 == 0x02) {
                //> jsr EraseEnemyObject      ;what it says
                eraseEnemyObject(temp4)
            }
        }
    }
    //> ExEGHandler:
    //> rts
    return
}

// Decompiled from DrawEnemyObjRow
fun drawEnemyObjRow(X: Int) {
    val enemyGraphicsTable by MemoryByteIndexed(EnemyGraphicsTable)
    //> DrawEnemyObjRow:
    //> lda EnemyGraphicsTable,x    ;load two tiles of enemy graphics
    //> sta $00
    memory[0x0] = (enemyGraphicsTable[X] and 0xFF).toUByte()
    //> lda EnemyGraphicsTable+1,x
}

// Decompiled from DrawOneSpriteRow
fun drawOneSpriteRow(A: Int, X: Int, Y: Int) {
    var X: Int = X
    var temp0: Int = 0
    var temp1: Int = 0
    val spriteAttributes by MemoryByteIndexed(Sprite_Attributes)
    val spriteTilenumber by MemoryByteIndexed(Sprite_Tilenumber)
    val spriteXPosition by MemoryByteIndexed(Sprite_X_Position)
    val spriteYPosition by MemoryByteIndexed(Sprite_Y_Position)
    //> DrawOneSpriteRow:
    //> sta $01
    memory[0x1] = A.toUByte()
    //> jmp DrawSpriteObject        ;draw them
    //> DrawSpriteObject:
    //> lda $03                    ;get saved flip control bits
    //> lsr
    //> lsr                        ;move d1 into carry
    //> lda $00
    //> bcc NoHFlip                ;if d1 not set, branch
    temp0 = memory[0x0].toInt()
    if ((memory[0x3].toInt() shr 1 and 0x01) != 0) {
        //> sta Sprite_Tilenumber+4,y  ;store first tile into second sprite
        spriteTilenumber[4 + Y] = temp0
        //> lda $01                    ;and second into first sprite
        temp0 = memory[0x1].toInt()
        //> sta Sprite_Tilenumber,y
        spriteTilenumber[Y] = temp0
        //> lda #$40                   ;activate horizontal flip OAM attribute
        temp0 = 0x40
        //> bne SetHFAt                ;and unconditionally branch
        if (temp0 == 0) {
        } else {
            //> SetHFAt: ora $04                    ;add other OAM attributes if necessary
            temp1 = temp0 or memory[0x4].toInt()
            //> sta Sprite_Attributes,y    ;store sprite attributes
            spriteAttributes[Y] = temp1
            //> sta Sprite_Attributes+4,y
            spriteAttributes[4 + Y] = temp1
            //> lda $02                    ;now the y coordinates
            temp0 = memory[0x2].toInt()
            //> sta Sprite_Y_Position,y    ;note because they are
            spriteYPosition[Y] = temp0
            //> sta Sprite_Y_Position+4,y  ;side by side, they are the same
            spriteYPosition[4 + Y] = temp0
            //> lda $05
            temp0 = memory[0x5].toInt()
            //> sta Sprite_X_Position,y    ;store x coordinate, then
            spriteXPosition[Y] = temp0
            //> clc                        ;add 8 pixels and store another to
            //> adc #$08                   ;put them side by side
            //> sta Sprite_X_Position+4,y
            spriteXPosition[4 + Y] = (temp0 + 0x08) and 0xFF
            //> lda $02                    ;add eight pixels to the next y
            temp0 = memory[0x2].toInt()
            //> clc                        ;coordinate
            //> adc #$08
            //> sta $02
            memory[0x2] = ((temp0 + 0x08) and 0xFF).toUByte()
            //> tya                        ;add eight to the offset in Y to
            //> clc                        ;move to the next two sprites
            //> adc #$08
            //> tay
            //> inx                        ;increment offset to return it to the
            X = (X + 1) and 0xFF
            //> inx                        ;routine that called this subroutine
            X = (X + 1) and 0xFF
            //> rts
            return
        }
    }
    //> NoHFlip: sta Sprite_Tilenumber,y    ;store first tile into first sprite
    spriteTilenumber[Y] = temp0
    //> lda $01                    ;and second into second sprite
    temp0 = memory[0x1].toInt()
    //> sta Sprite_Tilenumber+4,y
    spriteTilenumber[4 + Y] = temp0
    //> lda #$00                   ;clear bit for horizontal flip
    temp0 = 0x00
}

// Decompiled from MoveESprRowOffscreen
fun moveESprRowOffscreen(A: Int, X: Int) {
    val enemySprdataoffset by MemoryByteIndexed(Enemy_SprDataOffset)
    //> MoveESprRowOffscreen:
    //> clc                         ;add A to enemy object OAM data offset
    //> adc Enemy_SprDataOffset,x
    //> tay                         ;use as offset
    //> lda #$f8
    //> jmp DumpTwoSpr              ;move first row of sprites offscreen
}

// Decompiled from MoveESprColOffscreen
fun moveESprColOffscreen(A: Int, X: Int) {
    val enemySprdataoffset by MemoryByteIndexed(Enemy_SprDataOffset)
    val spriteData by MemoryByteIndexed(Sprite_Data)
    //> MoveESprColOffscreen:
    //> clc                         ;add A to enemy object OAM data offset
    //> adc Enemy_SprDataOffset,x
    //> tay                         ;use as offset
    //> jsr MoveColOffscreen        ;move first and second row sprites in column offscreen
    moveColOffscreen((A + enemySprdataoffset[X]) and 0xFF)
    //> sta Sprite_Data+16,y        ;move third row sprite in column offscreen
    spriteData[16 + ((A + enemySprdataoffset[X]) and 0xFF)] = (A + enemySprdataoffset[X]) and 0xFF
    //> rts
    return
}

// Decompiled from DrawBlock
fun drawBlock(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var temp5: Int = 0
    var temp6: Int = 0
    var areaType by MemoryByte(AreaType)
    var blockOffscreenbits by MemoryByte(Block_OffscreenBits)
    var blockRelXpos by MemoryByte(Block_Rel_XPos)
    var blockRelYpos by MemoryByte(Block_Rel_YPos)
    var objectOffset by MemoryByte(ObjectOffset)
    val blockMetatile by MemoryByteIndexed(Block_Metatile)
    val blockSprdataoffset by MemoryByteIndexed(Block_SprDataOffset)
    val defaultBlockObjTiles by MemoryByteIndexed(DefaultBlockObjTiles)
    val spriteAttributes by MemoryByteIndexed(Sprite_Attributes)
    val spriteTilenumber by MemoryByteIndexed(Sprite_Tilenumber)
    val spriteYPosition by MemoryByteIndexed(Sprite_Y_Position)
    //> DrawBlock:
    //> lda Block_Rel_YPos            ;get relative vertical coordinate of block object
    //> sta $02                       ;store here
    memory[0x2] = blockRelYpos.toUByte()
    //> lda Block_Rel_XPos            ;get relative horizontal coordinate of block object
    //> sta $05                       ;store here
    memory[0x5] = blockRelXpos.toUByte()
    //> lda #$03
    //> sta $04                       ;set attribute byte here
    memory[0x4] = 0x03.toUByte()
    //> lsr
    //> sta $03                       ;set horizontal flip bit here (will not be used)
    memory[0x3] = ((0x03 shr 1) and 0xFF).toUByte()
    //> ldy Block_SprDataOffset,x     ;get sprite data offset
    //> ldx #$00                      ;reset X for use as offset to tile data
    temp0 = 0x00
    temp1 = blockSprdataoffset[X]
    do {
        //> DBlkLoop:  lda DefaultBlockObjTiles,x    ;get left tile number
        //> sta $00                       ;set here
        memory[0x0] = (defaultBlockObjTiles[temp0] and 0xFF).toUByte()
        //> lda DefaultBlockObjTiles+1,x  ;get right tile number
        //> jsr DrawOneSpriteRow          ;do sub to write tile numbers to first row of sprites
        drawOneSpriteRow(defaultBlockObjTiles[1 + temp0], temp0, temp1)
        //> cpx #$04                      ;check incremented offset
        //> bne DBlkLoop                  ;and loop back until all four sprites are done
    } while (temp0 != 0x04)
    //> ldx ObjectOffset              ;get block object offset
    temp0 = objectOffset
    //> ldy Block_SprDataOffset,x     ;get sprite data offset
    temp1 = blockSprdataoffset[temp0]
    //> lda AreaType
    //> cmp #$01                      ;check for ground level type area
    //> beq ChkRep                    ;if found, branch to next part
    temp2 = areaType
    if (areaType != 0x01) {
        //> lda #$86
        temp2 = 0x86
        //> sta Sprite_Tilenumber,y       ;otherwise remove brick tiles with lines
        spriteTilenumber[temp1] = temp2
        //> sta Sprite_Tilenumber+4,y     ;and replace then with lineless brick tiles
        spriteTilenumber[4 + temp1] = temp2
    }
    //> ChkRep:    lda Block_Metatile,x          ;check replacement metatile
    temp2 = blockMetatile[temp0]
    //> cmp #$c4                      ;if not used block metatile, then
    //> bne BlkOffscr                 ;branch ahead to use current graphics
    if (temp2 == 0xC4) {
        //> lda #$87                      ;set A for used block tile
        temp2 = 0x87
        //> iny                           ;increment Y to write to tile bytes
        temp1 = (temp1 + 1) and 0xFF
        //> jsr DumpFourSpr               ;do sub to dump into all four sprites
        dumpFourSpr(temp2, temp1)
        //> dey                           ;return Y to original offset
        temp1 = (temp1 - 1) and 0xFF
        //> lda #$03                      ;set palette bits
        temp2 = 0x03
        //> ldx AreaType
        temp0 = areaType
        //> dex                           ;check for ground level type area again
        temp0 = (temp0 - 1) and 0xFF
        //> beq SetBFlip                  ;if found, use current palette bits
        if (temp0 != 0) {
            //> lsr                           ;otherwise set to $01
            temp2 = temp2 shr 1
        }
        //> SetBFlip:  ldx ObjectOffset              ;put block object offset back in X
        temp0 = objectOffset
        //> sta Sprite_Attributes,y       ;store attribute byte as-is in first sprite
        spriteAttributes[temp1] = temp2
        //> ora #%01000000
        temp3 = temp2 or 0x40
        //> sta Sprite_Attributes+4,y     ;set horizontal flip bit for second sprite
        spriteAttributes[4 + temp1] = temp3
        //> ora #%10000000
        temp4 = temp3 or 0x80
        //> sta Sprite_Attributes+12,y    ;set both flip bits for fourth sprite
        spriteAttributes[12 + temp1] = temp4
        //> and #%10000011
        temp5 = temp4 and 0x83
        //> sta Sprite_Attributes+8,y     ;set vertical flip bit for third sprite
        spriteAttributes[8 + temp1] = temp5
    }
    //> BlkOffscr: lda Block_OffscreenBits       ;get offscreen bits for block object
    temp2 = blockOffscreenbits
    //> pha                           ;save to stack
    push(temp2)
    //> and #%00000100                ;check to see if d2 in offscreen bits are set
    temp6 = temp2 and 0x04
    //> beq PullOfsB                  ;if not set, branch, otherwise move sprites offscreen
    temp2 = temp6
    if (temp6 != 0) {
        //> lda #$f8                      ;move offscreen two OAMs
        temp2 = 0xF8
        //> sta Sprite_Y_Position+4,y     ;on the right side
        spriteYPosition[4 + temp1] = temp2
        //> sta Sprite_Y_Position+12,y
        spriteYPosition[12 + temp1] = temp2
    }
    //> PullOfsB:  pla                           ;pull offscreen bits from stack
    temp2 = pull()
}

// Decompiled from ChkLeftCo
fun chkLeftCo(A: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    //> ChkLeftCo: and #%00001000                ;check to see if d3 in offscreen bits are set
    temp0 = A and 0x08
    //> beq ExDBlk                    ;if not set, branch, otherwise move sprites offscreen
    if (temp0 == 0) {
        //  goto ExDBlk
        return
    }
    temp1 = temp0
    if (temp0 != 0) {
    }
    //> ExDBlk: rts
    return
}

// Decompiled from MoveColOffscreen
fun moveColOffscreen(Y: Int): Int {
    val spriteYPosition by MemoryByteIndexed(Sprite_Y_Position)
    //> MoveColOffscreen:
    //> lda #$f8                   ;move offscreen two OAMs
    //> sta Sprite_Y_Position,y    ;on the left side (or two rows of enemy on either side
    spriteYPosition[Y] = 0xF8
    //> sta Sprite_Y_Position+8,y  ;if branched here from enemy graphics handler)
    spriteYPosition[8 + Y] = 0xF8
    //> ExDBlk: rts
    return A
}

// Decompiled from DrawBrickChunks
fun drawBrickChunks(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var blockOffscreenbits by MemoryByte(Block_OffscreenBits)
    var frameCounter by MemoryByte(FrameCounter)
    var gameEngineSubroutine by MemoryByte(GameEngineSubroutine)
    var screenleftXPos by MemoryByte(ScreenLeft_X_Pos)
    val blockOrigXpos by MemoryByteIndexed(Block_Orig_XPos)
    val blockRelXpos by MemoryByteIndexed(Block_Rel_XPos)
    val blockRelYpos by MemoryByteIndexed(Block_Rel_YPos)
    val blockSprdataoffset by MemoryByteIndexed(Block_SprDataOffset)
    val spriteXPosition by MemoryByteIndexed(Sprite_X_Position)
    val spriteYPosition by MemoryByteIndexed(Sprite_Y_Position)
    //> DrawBrickChunks:
    //> lda #$02                   ;set palette bits here
    //> sta $00
    memory[0x0] = 0x02.toUByte()
    //> lda #$75                   ;set tile number for ball (something residual, likely)
    //> ldy GameEngineSubroutine
    //> cpy #$05                   ;if end-of-level routine running,
    //> beq DChunks                ;use palette and tile number assigned
    temp0 = 0x75
    temp1 = gameEngineSubroutine
    if (gameEngineSubroutine != 0x05) {
        //> lda #$03                   ;otherwise set different palette bits
        temp0 = 0x03
        //> sta $00
        memory[0x0] = temp0.toUByte()
        //> lda #$84                   ;and set tile number for brick chunks
        temp0 = 0x84
    }
    //> DChunks: ldy Block_SprDataOffset,x  ;get OAM data offset
    temp1 = blockSprdataoffset[X]
    //> iny                        ;increment to start with tile bytes in OAM
    temp1 = (temp1 + 1) and 0xFF
    //> jsr DumpFourSpr            ;do sub to dump tile number into all four sprites
    dumpFourSpr(temp0, temp1)
    //> lda FrameCounter           ;get frame counter
    temp0 = frameCounter
    //> asl
    temp0 = (temp0 shl 1) and 0xFF
    //> asl
    temp0 = (temp0 shl 1) and 0xFF
    //> asl                        ;move low nybble to high
    temp0 = (temp0 shl 1) and 0xFF
    //> asl
    temp0 = (temp0 shl 1) and 0xFF
    //> and #$c0                   ;get what was originally d3-d2 of low nybble
    temp2 = temp0 and 0xC0
    //> ora $00                    ;add palette bits
    temp3 = temp2 or memory[0x0].toInt()
    //> iny                        ;increment offset for attribute bytes
    temp1 = (temp1 + 1) and 0xFF
    //> jsr DumpFourSpr            ;do sub to dump attribute data into all four sprites
    dumpFourSpr(temp3, temp1)
    //> dey
    temp1 = (temp1 - 1) and 0xFF
    //> dey                        ;decrement offset to Y coordinate
    temp1 = (temp1 - 1) and 0xFF
    //> lda Block_Rel_YPos         ;get first block object's relative vertical coordinate
    temp0 = blockRelYpos[0]
    //> jsr DumpTwoSpr             ;do sub to dump current Y coordinate into two sprites
    dumpTwoSpr(temp0, temp1)
    //> lda Block_Rel_XPos         ;get first block object's relative horizontal coordinate
    temp0 = blockRelXpos[0]
    //> sta Sprite_X_Position,y    ;save into X coordinate of first sprite
    spriteXPosition[temp1] = temp0
    //> lda Block_Orig_XPos,x      ;get original horizontal coordinate
    temp0 = blockOrigXpos[X]
    //> sec
    //> sbc ScreenLeft_X_Pos       ;subtract coordinate of left side from original coordinate
    //> sta $00                    ;store result as relative horizontal coordinate of original
    memory[0x0] = ((temp0 - screenleftXPos) and 0xFF).toUByte()
    //> sec
    //> sbc Block_Rel_XPos         ;get difference of relative positions of original - current
    //> adc $00                    ;add original relative position to result
    //> adc #$06                   ;plus 6 pixels to position second brick chunk correctly
    //> sta Sprite_X_Position+4,y  ;save into X coordinate of second sprite
    spriteXPosition[4 + temp1] = (((((((temp0 - screenleftXPos) and 0xFF) - blockRelXpos) and 0xFF) + memory[0x0].toInt() + (if (((temp0 - screenleftXPos) and 0xFF) - blockRelXpos >= 0) 1 else 0)) and 0xFF) + 0x06 + (if (((((temp0 - screenleftXPos) and 0xFF) - blockRelXpos) and 0xFF) + memory[0x0].toInt() + (if (((temp0 - screenleftXPos) and 0xFF) - blockRelXpos >= 0) 1 else 0) > 0xFF) 1 else 0)) and 0xFF
    //> lda Block_Rel_YPos+1       ;get second block object's relative vertical coordinate
    temp0 = blockRelYpos[1]
    //> sta Sprite_Y_Position+8,y
    spriteYPosition[8 + temp1] = temp0
    //> sta Sprite_Y_Position+12,y ;dump into Y coordinates of third and fourth sprites
    spriteYPosition[12 + temp1] = temp0
    //> lda Block_Rel_XPos+1       ;get second block object's relative horizontal coordinate
    temp0 = blockRelXpos[1]
    //> sta Sprite_X_Position+8,y  ;save into X coordinate of third sprite
    spriteXPosition[8 + temp1] = temp0
    //> lda $00                    ;use original relative horizontal position
    temp0 = memory[0x0].toInt()
    //> sec
    //> sbc Block_Rel_XPos+1       ;get difference of relative positions of original - current
    //> adc $00                    ;add original relative position to result
    //> adc #$06                   ;plus 6 pixels to position fourth brick chunk correctly
    //> sta Sprite_X_Position+12,y ;save into X coordinate of fourth sprite
    spriteXPosition[12 + temp1] = (((((temp0 - blockRelXpos[1]) and 0xFF) + memory[0x0].toInt() + (if (temp0 - blockRelXpos[1] >= 0) 1 else 0)) and 0xFF) + 0x06 + (if (((temp0 - blockRelXpos[1]) and 0xFF) + memory[0x0].toInt() + (if (temp0 - blockRelXpos[1] >= 0) 1 else 0) > 0xFF) 1 else 0)) and 0xFF
    //> lda Block_OffscreenBits    ;get offscreen bits for block object
    temp0 = blockOffscreenbits
    //> jsr ChkLeftCo              ;do sub to move left half of sprites offscreen if necessary
    chkLeftCo(temp0)
    //> lda Block_OffscreenBits    ;get offscreen bits again
    temp0 = blockOffscreenbits
    //> asl                        ;shift d7 into carry
    temp0 = (temp0 shl 1) and 0xFF
    //> bcc ChnkOfs                ;if d7 not set, branch to last part
    if ((temp0 and 0x80) != 0) {
        //> lda #$f8
        temp0 = 0xF8
        //> jsr DumpTwoSpr             ;otherwise move top sprites offscreen
        dumpTwoSpr(temp0, temp1)
    }
    //> ChnkOfs: lda $00                    ;if relative position on left side of screen,
    temp0 = memory[0x0].toInt()
    //> bpl ExBCDr                 ;go ahead and leave
    if ((temp0 and 0x80) != 0) {
        //> lda Sprite_X_Position,y    ;otherwise compare left-side X coordinate
        temp0 = spriteXPosition[temp1]
        //> cmp Sprite_X_Position+4,y  ;to right-side X coordinate
        //> bcc ExBCDr                 ;branch to leave if less
        if (temp0 >= spriteXPosition[4 + temp1]) {
            //> lda #$f8                   ;otherwise move right half of sprites offscreen
            temp0 = 0xF8
            //> sta Sprite_Y_Position+4,y
            spriteYPosition[4 + temp1] = temp0
            //> sta Sprite_Y_Position+12,y
            spriteYPosition[12 + temp1] = temp0
        }
    }
    //> ExBCDr:  rts                        ;leave
    return
}

// Decompiled from DrawFirebar
fun drawFirebar(Y: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var frameCounter by MemoryByte(FrameCounter)
    val spriteAttributes by MemoryByteIndexed(Sprite_Attributes)
    val spriteTilenumber by MemoryByteIndexed(Sprite_Tilenumber)
    //> DrawFirebar:
    //> lda FrameCounter         ;get frame counter
    //> lsr                      ;divide by four
    frameCounter = frameCounter shr 1
    //> lsr
    frameCounter = frameCounter shr 1
    //> pha                      ;save result to stack
    push(frameCounter)
    //> and #$01                 ;mask out all but last bit
    temp0 = frameCounter and 0x01
    //> eor #$64                 ;set either tile $64 or $65 as fireball tile
    temp1 = temp0 xor 0x64
    //> sta Sprite_Tilenumber,y  ;thus tile changes every four frames
    spriteTilenumber[Y] = temp1
    //> pla                      ;get from stack
    temp2 = pull()
    //> lsr                      ;divide by four again
    temp2 = temp2 shr 1
    //> lsr
    temp2 = temp2 shr 1
    //> lda #$02                 ;load value $02 to set palette in attrib byte
    //> bcc FireA                ;if last bit shifted out was not set, skip this
    temp3 = 0x02
    if ((temp2 and 0x01) != 0) {
        //> ora #%11000000           ;otherwise flip both ways every eight frames
        temp4 = temp3 or 0xC0
    }
    //> FireA: sta Sprite_Attributes,y  ;store attribute byte and leave
    spriteAttributes[Y] = temp3
    //> rts
    return
}

// Decompiled from DrawExplosion_Fireworks
fun drawexplosionFireworks(A: Int, Y: Int) {
    var Y: Int = Y
    var fireballRelXpos by MemoryByte(Fireball_Rel_XPos)
    var fireballRelYpos by MemoryByte(Fireball_Rel_YPos)
    var objectOffset by MemoryByte(ObjectOffset)
    val explosionTiles by MemoryByteIndexed(ExplosionTiles)
    val spriteAttributes by MemoryByteIndexed(Sprite_Attributes)
    val spriteXPosition by MemoryByteIndexed(Sprite_X_Position)
    val spriteYPosition by MemoryByteIndexed(Sprite_Y_Position)
    //> DrawExplosion_Fireworks:
    //> tax                         ;use whatever's in A for offset
    //> lda ExplosionTiles,x        ;get tile number using offset
    //> iny                         ;increment Y (contains sprite data offset)
    Y = (Y + 1) and 0xFF
    //> jsr DumpFourSpr             ;and dump into tile number part of sprite data
    dumpFourSpr(explosionTiles[A], Y)
    //> dey                         ;decrement Y so we have the proper offset again
    Y = (Y - 1) and 0xFF
    //> ldx ObjectOffset            ;return enemy object buffer offset to X
    //> lda Fireball_Rel_YPos       ;get relative vertical coordinate
    //> sec                         ;subtract four pixels vertically
    //> sbc #$04                    ;for first and third sprites
    //> sta Sprite_Y_Position,y
    spriteYPosition[Y] = (fireballRelYpos - 0x04) and 0xFF
    //> sta Sprite_Y_Position+8,y
    spriteYPosition[8 + Y] = (fireballRelYpos - 0x04) and 0xFF
    //> clc                         ;add eight pixels vertically
    //> adc #$08                    ;for second and fourth sprites
    //> sta Sprite_Y_Position+4,y
    spriteYPosition[4 + Y] = (((fireballRelYpos - 0x04) and 0xFF) + 0x08) and 0xFF
    //> sta Sprite_Y_Position+12,y
    spriteYPosition[12 + Y] = (((fireballRelYpos - 0x04) and 0xFF) + 0x08) and 0xFF
    //> lda Fireball_Rel_XPos       ;get relative horizontal coordinate
    //> sec                         ;subtract four pixels horizontally
    //> sbc #$04                    ;for first and second sprites
    //> sta Sprite_X_Position,y
    spriteXPosition[Y] = (fireballRelXpos - 0x04) and 0xFF
    //> sta Sprite_X_Position+4,y
    spriteXPosition[4 + Y] = (fireballRelXpos - 0x04) and 0xFF
    //> clc                         ;add eight pixels horizontally
    //> adc #$08                    ;for third and fourth sprites
    //> sta Sprite_X_Position+8,y
    spriteXPosition[8 + Y] = (((fireballRelXpos - 0x04) and 0xFF) + 0x08) and 0xFF
    //> sta Sprite_X_Position+12,y
    spriteXPosition[12 + Y] = (((fireballRelXpos - 0x04) and 0xFF) + 0x08) and 0xFF
    //> lda #$02                    ;set palette attributes for all sprites, but
    //> sta Sprite_Attributes,y     ;set no flip at all for first sprite
    spriteAttributes[Y] = 0x02
    //> lda #$82
    //> sta Sprite_Attributes+4,y   ;set vertical flip for second sprite
    spriteAttributes[4 + Y] = 0x82
    //> lda #$42
    //> sta Sprite_Attributes+8,y   ;set horizontal flip for third sprite
    spriteAttributes[8 + Y] = 0x42
    //> lda #$c2
    //> sta Sprite_Attributes+12,y  ;set both flips for fourth sprite
    spriteAttributes[12 + Y] = 0xC2
    //> rts                         ;we are done
    return
}

// Decompiled from DrawSmallPlatform
fun drawSmallPlatform(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var temp5: Int = 0
    var enemyOffscreenbits by MemoryByte(Enemy_OffscreenBits)
    var enemyRelXpos by MemoryByte(Enemy_Rel_XPos)
    var objectOffset by MemoryByte(ObjectOffset)
    val enemySprdataoffset by MemoryByteIndexed(Enemy_SprDataOffset)
    val enemyYPosition by MemoryByteIndexed(Enemy_Y_Position)
    val spriteXPosition by MemoryByteIndexed(Sprite_X_Position)
    val spriteYPosition by MemoryByteIndexed(Sprite_Y_Position)
    //> DrawSmallPlatform:
    //> ldy Enemy_SprDataOffset,x   ;get OAM data offset
    //> lda #$5b                    ;load tile number for small platforms
    //> iny                         ;increment offset for tile numbers
    temp0 = enemySprdataoffset[X]
    temp0 = (temp0 + 1) and 0xFF
    //> jsr DumpSixSpr              ;dump tile number into all six sprites
    dumpSixSpr(0x5B, temp0)
    //> iny                         ;increment offset for attributes
    temp0 = (temp0 + 1) and 0xFF
    //> lda #$02                    ;load palette controls
    //> jsr DumpSixSpr              ;dump attributes into all six sprites
    dumpSixSpr(0x02, temp0)
    //> dey                         ;decrement for original offset
    temp0 = (temp0 - 1) and 0xFF
    //> dey
    temp0 = (temp0 - 1) and 0xFF
    //> lda Enemy_Rel_XPos          ;get relative horizontal coordinate
    //> sta Sprite_X_Position,y
    spriteXPosition[temp0] = enemyRelXpos
    //> sta Sprite_X_Position+12,y  ;dump as X coordinate into first and fourth sprites
    spriteXPosition[12 + temp0] = enemyRelXpos
    //> clc
    //> adc #$08                    ;add eight pixels
    //> sta Sprite_X_Position+4,y   ;dump into second and fifth sprites
    spriteXPosition[4 + temp0] = (enemyRelXpos + 0x08) and 0xFF
    //> sta Sprite_X_Position+16,y
    spriteXPosition[16 + temp0] = (enemyRelXpos + 0x08) and 0xFF
    //> clc
    //> adc #$08                    ;add eight more pixels
    //> sta Sprite_X_Position+8,y   ;dump into third and sixth sprites
    spriteXPosition[8 + temp0] = (((enemyRelXpos + 0x08) and 0xFF) + 0x08) and 0xFF
    //> sta Sprite_X_Position+20,y
    spriteXPosition[20 + temp0] = (((enemyRelXpos + 0x08) and 0xFF) + 0x08) and 0xFF
    //> lda Enemy_Y_Position,x      ;get vertical coordinate
    //> tax
    //> pha                         ;save to stack
    push(enemyYPosition[X])
    //> cpx #$20                    ;if vertical coordinate below status bar,
    //> bcs TopSP                   ;do not mess with it
    temp1 = enemyYPosition[X]
    temp2 = enemyYPosition[X]
    if (!(enemyYPosition[X] >= 0x20)) {
        //> lda #$f8                    ;otherwise move first three sprites offscreen
        temp1 = 0xF8
    }
    //> TopSP: jsr DumpThreeSpr            ;dump vertical coordinate into Y coordinates
    dumpThreeSpr(temp1, temp0)
    //> pla                         ;pull from stack
    temp1 = pull()
    //> clc
    //> adc #$80                    ;add 128 pixels
    //> tax
    //> cpx #$20                    ;if below status bar (taking wrap into account)
    //> bcs BotSP                   ;then do not change altered coordinate
    temp1 = (temp1 + 0x80) and 0xFF
    temp2 = (temp1 + 0x80) and 0xFF
    if (!(((temp1 + 0x80) and 0xFF) >= 0x20)) {
        //> lda #$f8                    ;otherwise move last three sprites offscreen
        temp1 = 0xF8
    }
    //> BotSP: sta Sprite_Y_Position+12,y  ;dump vertical coordinate + 128 pixels
    spriteYPosition[12 + temp0] = temp1
    //> sta Sprite_Y_Position+16,y  ;into Y coordinates
    spriteYPosition[16 + temp0] = temp1
    //> sta Sprite_Y_Position+20,y
    spriteYPosition[20 + temp0] = temp1
    //> lda Enemy_OffscreenBits     ;get offscreen bits
    temp1 = enemyOffscreenbits
    //> pha                         ;save to stack
    push(temp1)
    //> and #%00001000              ;check d3
    temp3 = temp1 and 0x08
    //> beq SOfs
    temp1 = temp3
    if (temp3 != 0) {
        //> lda #$f8                    ;if d3 was set, move first and
        temp1 = 0xF8
        //> sta Sprite_Y_Position,y     ;fourth sprites offscreen
        spriteYPosition[temp0] = temp1
        //> sta Sprite_Y_Position+12,y
        spriteYPosition[12 + temp0] = temp1
    }
    //> SOfs:  pla                         ;move out and back into stack
    temp1 = pull()
    //> pha
    push(temp1)
    //> and #%00000100              ;check d2
    temp4 = temp1 and 0x04
    //> beq SOfs2
    temp1 = temp4
    if (temp4 != 0) {
        //> lda #$f8                    ;if d2 was set, move second and
        temp1 = 0xF8
        //> sta Sprite_Y_Position+4,y   ;fifth sprites offscreen
        spriteYPosition[4 + temp0] = temp1
        //> sta Sprite_Y_Position+16,y
        spriteYPosition[16 + temp0] = temp1
    }
    //> SOfs2: pla                         ;get from stack
    temp1 = pull()
    //> and #%00000010              ;check d1
    temp5 = temp1 and 0x02
    //> beq ExSPl
    temp1 = temp5
    if (temp5 != 0) {
        //> lda #$f8                    ;if d1 was set, move third and
        temp1 = 0xF8
        //> sta Sprite_Y_Position+8,y   ;sixth sprites offscreen
        spriteYPosition[8 + temp0] = temp1
        //> sta Sprite_Y_Position+20,y
        spriteYPosition[20 + temp0] = temp1
    }
    //> ExSPl: ldx ObjectOffset            ;get enemy object offset and leave
    temp2 = objectOffset
    //> rts
    return
}

// Decompiled from DrawBubble
fun drawBubble(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var bubbleOffscreenbits by MemoryByte(Bubble_OffscreenBits)
    var bubbleRelXpos by MemoryByte(Bubble_Rel_XPos)
    var bubbleRelYpos by MemoryByte(Bubble_Rel_YPos)
    var playerYHighpos by MemoryByte(Player_Y_HighPos)
    val bubbleSprdataoffset by MemoryByteIndexed(Bubble_SprDataOffset)
    val spriteAttributes by MemoryByteIndexed(Sprite_Attributes)
    val spriteTilenumber by MemoryByteIndexed(Sprite_Tilenumber)
    val spriteXPosition by MemoryByteIndexed(Sprite_X_Position)
    val spriteYPosition by MemoryByteIndexed(Sprite_Y_Position)
    //> DrawBubble:
    //> ldy Player_Y_HighPos        ;if player's vertical high position
    //> dey                         ;not within screen, skip all of this
    playerYHighpos = (playerYHighpos - 1) and 0xFF
    //> bne ExDBub
    temp0 = playerYHighpos
    if (playerYHighpos == 0) {
        //> lda Bubble_OffscreenBits    ;check air bubble's offscreen bits
        //> and #%00001000
        temp1 = bubbleOffscreenbits and 0x08
        //> bne ExDBub                  ;if bit set, branch to leave
        temp2 = temp1
        if (temp1 == 0) {
            //> ldy Bubble_SprDataOffset,x  ;get air bubble's OAM data offset
            temp0 = bubbleSprdataoffset[X]
            //> lda Bubble_Rel_XPos         ;get relative horizontal coordinate
            temp2 = bubbleRelXpos
            //> sta Sprite_X_Position,y     ;store as X coordinate here
            spriteXPosition[temp0] = temp2
            //> lda Bubble_Rel_YPos         ;get relative vertical coordinate
            temp2 = bubbleRelYpos
            //> sta Sprite_Y_Position,y     ;store as Y coordinate here
            spriteYPosition[temp0] = temp2
            //> lda #$74
            temp2 = 0x74
            //> sta Sprite_Tilenumber,y     ;put air bubble tile into OAM data
            spriteTilenumber[temp0] = temp2
            //> lda #$02
            temp2 = 0x02
            //> sta Sprite_Attributes,y     ;set attribute byte
            spriteAttributes[temp0] = temp2
        }
    }
    //> ExDBub: rts                         ;leave
    return
}

// Decompiled from PlayerGfxHandler
fun playerGfxHandler() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var fireballThrowingTimer by MemoryByte(FireballThrowingTimer)
    var frameCounter by MemoryByte(FrameCounter)
    var gameEngineSubroutine by MemoryByte(GameEngineSubroutine)
    var injuryTimer by MemoryByte(InjuryTimer)
    var leftRightButtons by MemoryByte(Left_Right_Buttons)
    var playerAnimTimer by MemoryByte(PlayerAnimTimer)
    var playerChangeSizeFlag by MemoryByte(PlayerChangeSizeFlag)
    var playerFacingDir by MemoryByte(PlayerFacingDir)
    var playerGfxOffset by MemoryByte(PlayerGfxOffset)
    var playerSize by MemoryByte(PlayerSize)
    var playerOffscreenbits by MemoryByte(Player_OffscreenBits)
    var playerSprdataoffset by MemoryByte(Player_SprDataOffset)
    var playerState by MemoryByte(Player_State)
    var playerXSpeed by MemoryByte(Player_X_Speed)
    var swimTileRepOffset by MemoryByte(SwimTileRepOffset)
    var swimmingFlag by MemoryByte(SwimmingFlag)
    val playerGfxTblOffsets by MemoryByteIndexed(PlayerGfxTblOffsets)
    val spriteTilenumber by MemoryByteIndexed(Sprite_Tilenumber)
    val swimKickTileNum by MemoryByteIndexed(SwimKickTileNum)
    //> PlayerGfxHandler:
    //> lda InjuryTimer             ;if player's injured invincibility timer
    //> beq CntPl                   ;not set, skip checkpoint and continue code
    temp0 = injuryTimer
    if (injuryTimer != 0) {
        //> lda FrameCounter
        temp0 = frameCounter
        //> lsr                         ;otherwise check frame counter and branch
        temp0 = temp0 shr 1
        //> bcs ExPGH                   ;to leave on every other frame (when d0 is set)
        if ((temp0 and 0x01) == 0) {
        } else {
            //> ExPGH:  rts                         ;then leave
            return
        }
    }
    //> CntPl:  lda GameEngineSubroutine    ;if executing specific game engine routine,
    temp0 = gameEngineSubroutine
    //> cmp #$0b                    ;branch ahead to some other part
    //> beq PlayerKilled
    if (temp0 != 0x0B) {
        //> lda PlayerChangeSizeFlag    ;if grow/shrink flag set
        temp0 = playerChangeSizeFlag
        //> bne DoChangeSize            ;then branch to some other code
        if (!(temp0 == 0)) {
            //  goto DoChangeSize
            return
        }
        if (temp0 == 0) {
            //> ldy SwimmingFlag            ;if swimming flag set, branch to
            //> beq FindPlayerAction        ;different part, do not return
            if (swimmingFlag == 0) {
                //  goto FindPlayerAction
                return
            }
            //> lda Player_State
            temp0 = playerState
            //> cmp #$00                    ;if player status normal,
            //> beq FindPlayerAction        ;branch and do not return
            if (temp0 == 0) {
                //  goto FindPlayerAction
                return
            }
            //> jsr FindPlayerAction        ;otherwise jump and return
            findPlayerAction(temp0)
            //> lda FrameCounter
            temp0 = frameCounter
            //> and #%00000100              ;check frame counter for d2 set (8 frames every
            temp1 = temp0 and 0x04
            //> bne ExPGH                   ;eighth frame), and branch if set to leave
            temp0 = temp1
            temp2 = swimmingFlag
            if (temp1 == 0) {
                //> tax                         ;initialize X to zero
                //> ldy Player_SprDataOffset    ;get player sprite data offset
                temp2 = playerSprdataoffset
                //> lda PlayerFacingDir         ;get player's facing direction
                temp0 = playerFacingDir
                //> lsr
                temp0 = temp0 shr 1
                //> bcs SwimKT                  ;if player facing to the right, use current offset
                temp3 = temp0
                if ((temp0 and 0x01) == 0) {
                    //> iny
                    temp2 = (temp2 + 1) and 0xFF
                    //> iny                         ;otherwise move to next OAM data
                    temp2 = (temp2 + 1) and 0xFF
                    //> iny
                    temp2 = (temp2 + 1) and 0xFF
                    //> iny
                    temp2 = (temp2 + 1) and 0xFF
                }
                //> SwimKT: lda PlayerSize              ;check player's size
                temp0 = playerSize
                //> beq BigKTS                  ;if big, use first tile
                if (temp0 != 0) {
                    //> lda Sprite_Tilenumber+24,y  ;check tile number of seventh/eighth sprite
                    temp0 = spriteTilenumber[24 + temp2]
                    //> cmp SwimTileRepOffset       ;against tile number in player graphics table
                    //> beq ExPGH                   ;if spr7/spr8 tile number = value, branch to leave
                    if (temp0 != swimTileRepOffset) {
                        //> inx                         ;otherwise increment X for second tile
                        temp3 = (temp3 + 1) and 0xFF
                    }
                }
                //> BigKTS: lda SwimKickTileNum,x       ;overwrite tile number in sprite 7/8
                temp0 = swimKickTileNum[temp3]
                //> sta Sprite_Tilenumber+24,y  ;to animate player's feet when swimming
                spriteTilenumber[24 + temp2] = temp0
            }
        }
        //> DoChangeSize:
        //> jsr HandleChangeSize          ;find proper offset to graphics table for grow/shrink
        handleChangeSize()
        //> jmp PlayerGfxProcessing       ;draw player, then process for fireball throwing
    } else {
        //> PlayerKilled:
        //> ldy #$0e                      ;load offset for player killed
        temp2 = 0x0E
        //> lda PlayerGfxTblOffsets,y     ;get offset to graphics table
        temp0 = playerGfxTblOffsets[temp2]
    }
    //> PlayerGfxProcessing:
    //> sta PlayerGfxOffset           ;store offset to graphics table here
    playerGfxOffset = temp0
    //> lda #$04
    temp0 = 0x04
    //> jsr RenderPlayerSub           ;draw player based on offset loaded
    renderPlayerSub(temp0)
    //> jsr ChkForPlayerAttrib        ;set horizontal flip bits as necessary
    chkForPlayerAttrib()
    //> lda FireballThrowingTimer
    temp0 = fireballThrowingTimer
    //> beq PlayerOffscreenChk        ;if fireball throw timer not set, skip to the end
    if (temp0 != 0) {
        //> ldy #$00                      ;set value to initialize by default
        temp2 = 0x00
        //> lda PlayerAnimTimer           ;get animation frame timer
        temp0 = playerAnimTimer
        //> cmp FireballThrowingTimer     ;compare to fireball throw timer
        //> sty FireballThrowingTimer     ;initialize fireball throw timer
        fireballThrowingTimer = temp2
        //> bcs PlayerOffscreenChk        ;if animation frame timer => fireball throw timer skip to end
        if (!(temp0 >= fireballThrowingTimer)) {
            //> sta FireballThrowingTimer     ;otherwise store animation timer into fireball throw timer
            fireballThrowingTimer = temp0
            //> ldy #$07                      ;load offset for throwing
            temp2 = 0x07
            //> lda PlayerGfxTblOffsets,y     ;get offset to graphics table
            temp0 = playerGfxTblOffsets[temp2]
            //> sta PlayerGfxOffset           ;store it for use later
            playerGfxOffset = temp0
            //> ldy #$04                      ;set to update four sprite rows by default
            temp2 = 0x04
            //> lda Player_X_Speed
            temp0 = playerXSpeed
            //> ora Left_Right_Buttons        ;check for horizontal speed or left/right button press
            temp4 = temp0 or leftRightButtons
            //> beq SUpdR                     ;if no speed or button press, branch using set value in Y
            temp0 = temp4
            if (temp4 != 0) {
                //> dey                           ;otherwise set to update only three sprite rows
                temp2 = (temp2 - 1) and 0xFF
            }
            //> SUpdR: tya                           ;save in A for use
            //> jsr RenderPlayerSub           ;in sub, draw player object again
            renderPlayerSub(temp2)
        }
    }
    //> PlayerOffscreenChk:
    //> lda Player_OffscreenBits      ;get player's offscreen bits
    temp0 = playerOffscreenbits
    //> lsr
    temp0 = temp0 shr 1
    //> lsr                           ;move vertical bits to low nybble
    temp0 = temp0 shr 1
    //> lsr
    temp0 = temp0 shr 1
    //> lsr
    temp0 = temp0 shr 1
    //> sta $00                       ;store here
    memory[0x0] = temp0.toUByte()
    //> ldx #$03                      ;check all four rows of player sprites
    temp3 = 0x03
    //> lda Player_SprDataOffset      ;get player's sprite data offset
    temp0 = playerSprdataoffset
    //> clc
    //> adc #$18                      ;add 24 bytes to start at bottom row
    //> tay                           ;set as offset here
    temp2 = (temp0 + 0x18) and 0xFF
    while ((temp3 and 0x80) == 0) {
        //> jsr DumpTwoSpr                ;otherwise dump offscreen Y coordinate into sprite data
        dumpTwoSpr((temp0 + 0x18) and 0xFF, temp2)
        do {
            //> PROfsLoop: lda #$f8                      ;load offscreen Y coordinate just in case
            temp0 = 0xF8
            //> lsr $00                       ;shift bit into carry
            memory[0x0] = ((memory[0x0].toInt() shr 1) and 0xFF).toUByte()
            //> bcc NPROffscr                 ;if bit not set, skip, do not move sprites
            //> NPROffscr: tya
            //> sec                           ;subtract eight bytes to do
            //> sbc #$08                      ;next row up
            //> tay
            //> dex                           ;decrement row counter
            temp3 = (temp3 - 1) and 0xFF
            //> bpl PROfsLoop                 ;do this until all sprite rows are checked
        } while ((temp3 and 0x80) == 0)
    }
    //> rts                           ;then we are done!
    return
}

// Decompiled from FindPlayerAction
fun findPlayerAction(A: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var fireballThrowingTimer by MemoryByte(FireballThrowingTimer)
    var leftRightButtons by MemoryByte(Left_Right_Buttons)
    var playerAnimTimer by MemoryByte(PlayerAnimTimer)
    var playerGfxOffset by MemoryByte(PlayerGfxOffset)
    var playerOffscreenbits by MemoryByte(Player_OffscreenBits)
    var playerSprdataoffset by MemoryByte(Player_SprDataOffset)
    var playerXSpeed by MemoryByte(Player_X_Speed)
    val playerGfxTblOffsets by MemoryByteIndexed(PlayerGfxTblOffsets)
    //> FindPlayerAction:
    //> jsr ProcessPlayerAction       ;find proper offset to graphics table by player's actions
    processPlayerAction()
    //> jmp PlayerGfxProcessing       ;draw player, then process for fireball throwing
    //> PlayerGfxProcessing:
    //> sta PlayerGfxOffset           ;store offset to graphics table here
    playerGfxOffset = A
    //> lda #$04
    //> jsr RenderPlayerSub           ;draw player based on offset loaded
    renderPlayerSub(0x04)
    //> jsr ChkForPlayerAttrib        ;set horizontal flip bits as necessary
    chkForPlayerAttrib()
    //> lda FireballThrowingTimer
    //> beq PlayerOffscreenChk        ;if fireball throw timer not set, skip to the end
    temp0 = fireballThrowingTimer
    if (fireballThrowingTimer != 0) {
        //> ldy #$00                      ;set value to initialize by default
        //> lda PlayerAnimTimer           ;get animation frame timer
        temp0 = playerAnimTimer
        //> cmp FireballThrowingTimer     ;compare to fireball throw timer
        //> sty FireballThrowingTimer     ;initialize fireball throw timer
        fireballThrowingTimer = 0x00
        //> bcs PlayerOffscreenChk        ;if animation frame timer => fireball throw timer skip to end
        temp1 = 0x00
        if (!(temp0 >= fireballThrowingTimer)) {
            //> sta FireballThrowingTimer     ;otherwise store animation timer into fireball throw timer
            fireballThrowingTimer = temp0
            //> ldy #$07                      ;load offset for throwing
            temp1 = 0x07
            //> lda PlayerGfxTblOffsets,y     ;get offset to graphics table
            temp0 = playerGfxTblOffsets[temp1]
            //> sta PlayerGfxOffset           ;store it for use later
            playerGfxOffset = temp0
            //> ldy #$04                      ;set to update four sprite rows by default
            temp1 = 0x04
            //> lda Player_X_Speed
            temp0 = playerXSpeed
            //> ora Left_Right_Buttons        ;check for horizontal speed or left/right button press
            temp2 = temp0 or leftRightButtons
            //> beq SUpdR                     ;if no speed or button press, branch using set value in Y
            temp0 = temp2
            if (temp2 != 0) {
                //> dey                           ;otherwise set to update only three sprite rows
                temp1 = (temp1 - 1) and 0xFF
            }
            //> SUpdR: tya                           ;save in A for use
            //> jsr RenderPlayerSub           ;in sub, draw player object again
            renderPlayerSub(temp1)
        }
    }
    //> PlayerOffscreenChk:
    //> lda Player_OffscreenBits      ;get player's offscreen bits
    temp0 = playerOffscreenbits
    //> lsr
    temp0 = temp0 shr 1
    //> lsr                           ;move vertical bits to low nybble
    temp0 = temp0 shr 1
    //> lsr
    temp0 = temp0 shr 1
    //> lsr
    temp0 = temp0 shr 1
    //> sta $00                       ;store here
    memory[0x0] = temp0.toUByte()
    //> ldx #$03                      ;check all four rows of player sprites
    //> lda Player_SprDataOffset      ;get player's sprite data offset
    temp0 = playerSprdataoffset
    //> clc
    //> adc #$18                      ;add 24 bytes to start at bottom row
    //> tay                           ;set as offset here
    temp3 = 0x03
    temp1 = (temp0 + 0x18) and 0xFF
    while ((temp3 and 0x80) == 0) {
        //> jsr DumpTwoSpr                ;otherwise dump offscreen Y coordinate into sprite data
        dumpTwoSpr((temp0 + 0x18) and 0xFF, temp1)
        do {
            //> PROfsLoop: lda #$f8                      ;load offscreen Y coordinate just in case
            temp0 = 0xF8
            //> lsr $00                       ;shift bit into carry
            memory[0x0] = ((memory[0x0].toInt() shr 1) and 0xFF).toUByte()
            //> bcc NPROffscr                 ;if bit not set, skip, do not move sprites
            //> NPROffscr: tya
            //> sec                           ;subtract eight bytes to do
            //> sbc #$08                      ;next row up
            //> tay
            //> dex                           ;decrement row counter
            temp3 = (temp3 - 1) and 0xFF
            //> bpl PROfsLoop                 ;do this until all sprite rows are checked
        } while ((temp3 and 0x80) == 0)
    }
    //> rts                           ;then we are done!
    return
}

// Decompiled from DrawPlayer_Intermediate
fun drawplayerIntermediate() {
    var temp0: Int = 0
    var temp1: Int = 0
    val intermediatePlayerData by MemoryByteIndexed(IntermediatePlayerData)
    val spriteAttributes by MemoryByteIndexed(Sprite_Attributes)
    //> DrawPlayer_Intermediate:
    //> ldx #$05                       ;store data into zero page memory
    temp0 = 0x05
    do {
        //> PIntLoop: lda IntermediatePlayerData,x   ;load data to display player as he always
        //> sta $02,x                      ;appears on world/lives display
        memory[0x2 + temp0] = (intermediatePlayerData[temp0] and 0xFF).toUByte()
        //> dex
        temp0 = (temp0 - 1) and 0xFF
        //> bpl PIntLoop                   ;do this until all data is loaded
    } while ((temp0 and 0x80) == 0)
    //> ldx #$b8                       ;load offset for small standing
    temp0 = 0xB8
    //> ldy #$04                       ;load sprite data offset
    //> jsr DrawPlayerLoop             ;draw player accordingly
    drawPlayerLoop(temp0)
    //> lda Sprite_Attributes+36       ;get empty sprite attributes
    //> ora #%01000000                 ;set horizontal flip bit for bottom-right sprite
    temp1 = spriteAttributes[36] or 0x40
    //> sta Sprite_Attributes+32       ;store and leave
    spriteAttributes[32] = temp1
    //> rts
    return
}

// Decompiled from RenderPlayerSub
fun renderPlayerSub(A: Int) {
    var playerFacingDir by MemoryByte(PlayerFacingDir)
    var playerGfxOffset by MemoryByte(PlayerGfxOffset)
    var playerPosForscroll by MemoryByte(Player_Pos_ForScroll)
    var playerRelXpos by MemoryByte(Player_Rel_XPos)
    var playerRelYpos by MemoryByte(Player_Rel_YPos)
    var playerSprattrib by MemoryByte(Player_SprAttrib)
    var playerSprdataoffset by MemoryByte(Player_SprDataOffset)
    //> RenderPlayerSub:
    //> sta $07                      ;store number of rows of sprites to draw
    memory[0x7] = A.toUByte()
    //> lda Player_Rel_XPos
    //> sta Player_Pos_ForScroll     ;store player's relative horizontal position
    playerPosForscroll = playerRelXpos
    //> sta $05                      ;store it here also
    memory[0x5] = playerRelXpos.toUByte()
    //> lda Player_Rel_YPos
    //> sta $02                      ;store player's vertical position
    memory[0x2] = playerRelYpos.toUByte()
    //> lda PlayerFacingDir
    //> sta $03                      ;store player's facing direction
    memory[0x3] = playerFacingDir.toUByte()
    //> lda Player_SprAttrib
    //> sta $04                      ;store player's sprite attributes
    memory[0x4] = playerSprattrib.toUByte()
    //> ldx PlayerGfxOffset          ;load graphics table offset
    //> ldy Player_SprDataOffset     ;get player's sprite data offset
}

// Decompiled from DrawPlayerLoop
fun drawPlayerLoop(X: Int) {
    var Y: Int = 0
    val playerGraphicsTable by MemoryByteIndexed(PlayerGraphicsTable)
    do {
        //> DrawPlayerLoop:
        //> lda PlayerGraphicsTable,x    ;load player's left side
        //> sta $00
        memory[0x0] = (playerGraphicsTable[X] and 0xFF).toUByte()
        //> lda PlayerGraphicsTable+1,x  ;now load right side
        //> jsr DrawOneSpriteRow
        drawOneSpriteRow(playerGraphicsTable[1 + X], X, Y)
        //> dec $07                      ;decrement rows of sprites to draw
        memory[0x7] = ((memory[0x7].toInt() - 1) and 0xFF).toUByte()
        //> bne DrawPlayerLoop           ;do this until all rows are drawn
    } while (((memory[0x7].toInt() - 1) and 0xFF) != 0)
    //> rts
    return
}

// Decompiled from ProcessPlayerAction
fun processPlayerAction() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var aBButtons by MemoryByte(A_B_Buttons)
    var crouchingFlag by MemoryByte(CrouchingFlag)
    var jumpSwimTimer by MemoryByte(JumpSwimTimer)
    var leftRightButtons by MemoryByte(Left_Right_Buttons)
    var playerAnimCtrl by MemoryByte(PlayerAnimCtrl)
    var playerAnimTimer by MemoryByte(PlayerAnimTimer)
    var playerAnimTimerSet by MemoryByte(PlayerAnimTimerSet)
    var playerFacingDir by MemoryByte(PlayerFacingDir)
    var playerMovingdir by MemoryByte(Player_MovingDir)
    var playerState by MemoryByte(Player_State)
    var playerXspeedabsolute by MemoryByte(Player_XSpeedAbsolute)
    var playerXSpeed by MemoryByte(Player_X_Speed)
    var playerYSpeed by MemoryByte(Player_Y_Speed)
    var swimmingFlag by MemoryByte(SwimmingFlag)
    val playerGfxTblOffsets by MemoryByteIndexed(PlayerGfxTblOffsets)
    //> ProcessPlayerAction:
    //> lda Player_State      ;get player's state
    //> cmp #$03
    //> beq ActionClimbing    ;if climbing, branch here
    temp0 = playerState
    if (playerState != 0x03) {
        //> cmp #$02
        //> beq ActionFalling     ;if falling, branch here
        if (temp0 - 0x02 == 0) {
            //  goto ActionFalling
            return
        }
        if (temp0 != 0x02) {
            //> cmp #$01
            //> bne ProcOnGroundActs  ;if not jumping, branch here
            if (temp0 == 0x01) {
                //> lda SwimmingFlag
                temp0 = swimmingFlag
                //> bne ActionSwimming    ;if swimming flag set, branch elsewhere
                if (temp0 == 0) {
                    //> ldy #$06              ;load offset for crouching
                    //> lda CrouchingFlag     ;get crouching flag
                    temp0 = crouchingFlag
                    //> bne NonAnimatedActs   ;if set, branch to get offset for graphics table
                    temp1 = 0x06
                    if (temp0 == 0) {
                        //> ldy #$00              ;otherwise load offset for jumping
                        temp1 = 0x00
                        //> jmp NonAnimatedActs   ;go to get offset to graphics table
                    } else {
                        //> NonAnimatedActs:
                        //> jsr GetGfxOffsetAdder      ;do a sub here to get offset adder for graphics table
                        getGfxOffsetAdder(temp1)
                        //> lda #$00
                        temp0 = 0x00
                        //> sta PlayerAnimCtrl         ;initialize animation frame control
                        playerAnimCtrl = temp0
                        //> lda PlayerGfxTblOffsets,y  ;load offset to graphics table using size as offset
                        temp0 = playerGfxTblOffsets[temp1]
                        //> rts
                        return
                    }
                }
            } else {
                //> ProcOnGroundActs:
                //> ldy #$06                   ;load offset for crouching
                temp1 = 0x06
                //> lda CrouchingFlag          ;get crouching flag
                temp0 = crouchingFlag
                //> bne NonAnimatedActs        ;if set, branch to get offset for graphics table
                if (temp0 == 0) {
                    //> ldy #$02                   ;load offset for standing
                    temp1 = 0x02
                    //> lda Player_X_Speed         ;check player's horizontal speed
                    temp0 = playerXSpeed
                    //> ora Left_Right_Buttons     ;and left/right controller bits
                    temp2 = temp0 or leftRightButtons
                    //> beq NonAnimatedActs        ;if no speed or buttons pressed, use standing offset
                    temp0 = temp2
                    if (temp2 != 0) {
                        //> lda Player_XSpeedAbsolute  ;load walking/running speed
                        temp0 = playerXspeedabsolute
                        //> cmp #$09
                        //> bcc ActionWalkRun          ;if less than a certain amount, branch, too slow to skid
                        if (!(temp0 >= 0x09)) {
                            //  goto ActionWalkRun
                            return
                        }
                        if (temp0 >= 0x09) {
                            //> lda Player_MovingDir       ;otherwise check to see if moving direction
                            temp0 = playerMovingdir
                            //> and PlayerFacingDir        ;and facing direction are the same
                            temp3 = temp0 and playerFacingDir
                            //> bne ActionWalkRun          ;if moving direction = facing direction, branch, don't skid
                            if (!(temp3 == 0)) {
                                //  goto ActionWalkRun
                                return
                            }
                            temp0 = temp3
                            if (temp3 == 0) {
                                //> iny                        ;otherwise increment to skid offset ($03)
                                temp1 = (temp1 + 1) and 0xFF
                            }
                        }
                    }
                }
            }
        }
        //> ActionFalling:
        //> ldy #$04                  ;load offset for walking/running
        temp1 = 0x04
        //> jsr GetGfxOffsetAdder     ;get offset to graphics table
        getGfxOffsetAdder(temp1)
        //> jmp GetCurrentAnimOffset  ;execute instructions for falling state
        //> ActionWalkRun:
        //> ldy #$04               ;load offset for walking/running
        temp1 = 0x04
        //> jsr GetGfxOffsetAdder  ;get offset to graphics table
        getGfxOffsetAdder(temp1)
        //> jmp FourFrameExtent    ;execute instructions for normal state
    } else {
        do {
            //> ActionClimbing:
            //> ldy #$05               ;load offset for climbing
            temp1 = 0x05
            //> lda Player_Y_Speed     ;check player's vertical speed
            temp0 = playerYSpeed
            //> beq NonAnimatedActs    ;if no speed, branch, use offset as-is
        } while (temp0 == 0)
        //> jsr GetGfxOffsetAdder  ;otherwise get offset for graphics table
        getGfxOffsetAdder(temp1)
        //> jmp ThreeFrameExtent   ;then skip ahead to more code
        //> ActionSwimming:
        //> ldy #$01               ;load offset for swimming
        temp1 = 0x01
        //> jsr GetGfxOffsetAdder
        getGfxOffsetAdder(temp1)
        //> lda JumpSwimTimer      ;check jump/swim timer
        temp0 = jumpSwimTimer
        //> ora PlayerAnimCtrl     ;and animation frame control
        temp4 = temp0 or playerAnimCtrl
        //> bne FourFrameExtent    ;if any one of these set, branch ahead
        if (!(temp4 == 0)) {
            //  goto FourFrameExtent
            return
        }
        temp0 = temp4
        if (temp4 == 0) {
            //> lda A_B_Buttons
            temp0 = aBButtons
            //> asl                    ;check for A button pressed
            temp0 = (temp0 shl 1) and 0xFF
            //> bcs FourFrameExtent    ;branch to same place if A button pressed
            if ((temp0 and 0x80) != 0) {
                //  goto FourFrameExtent
                return
            }
            if ((temp0 and 0x80) == 0) {
            }
        }
    }
    //> FourFrameExtent:
    //> lda #$03              ;load upper extent for frame control
    temp0 = 0x03
    //> jmp AnimationControl  ;jump to get offset and animate player object
    //> ThreeFrameExtent:
    //> lda #$02              ;load upper extent for frame control for climbing
    temp0 = 0x02
    //> AnimationControl:
    //> sta $00                   ;store upper extent here
    memory[0x0] = temp0.toUByte()
    //> jsr GetCurrentAnimOffset  ;get proper offset to graphics table
    getCurrentAnimOffset(temp1)
    //> pha                       ;save offset to stack
    push(temp0)
    //> lda PlayerAnimTimer       ;load animation frame timer
    temp0 = playerAnimTimer
    //> bne ExAnimC               ;branch if not expired
    if (temp0 == 0) {
        //> lda PlayerAnimTimerSet    ;get animation frame timer amount
        temp0 = playerAnimTimerSet
        //> sta PlayerAnimTimer       ;and set timer accordingly
        playerAnimTimer = temp0
        //> lda PlayerAnimCtrl
        temp0 = playerAnimCtrl
        //> clc                       ;add one to animation frame control
        //> adc #$01
        //> cmp $00                   ;compare to upper extent
        //> bcc SetAnimC              ;if frame control + 1 < upper extent, use as next
        temp0 = (temp0 + 0x01) and 0xFF
        if (((temp0 + 0x01) and 0xFF) >= memory[0x0].toInt()) {
            //> lda #$00                  ;otherwise initialize frame control
            temp0 = 0x00
        }
        //> SetAnimC: sta PlayerAnimCtrl        ;store as new animation frame control
        playerAnimCtrl = temp0
    }
    //> ExAnimC:  pla                       ;get offset to graphics table from stack and leave
    temp0 = pull()
    //> rts
    return
}

// Decompiled from GetCurrentAnimOffset
fun getCurrentAnimOffset(Y: Int): Int {
    var playerAnimCtrl by MemoryByte(PlayerAnimCtrl)
    val playerGfxTblOffsets by MemoryByteIndexed(PlayerGfxTblOffsets)
    //> GetCurrentAnimOffset:
    //> lda PlayerAnimCtrl         ;get animation frame control
    //> jmp GetOffsetFromAnimCtrl  ;jump to get proper offset to graphics table
    //> GetOffsetFromAnimCtrl:
    //> asl                        ;multiply animation frame control
    playerAnimCtrl = (playerAnimCtrl shl 1) and 0xFF
    //> asl                        ;by eight to get proper amount
    playerAnimCtrl = (playerAnimCtrl shl 1) and 0xFF
    //> asl                        ;to add to our offset
    playerAnimCtrl = (playerAnimCtrl shl 1) and 0xFF
    //> adc PlayerGfxTblOffsets,y  ;add to offset to graphics table
    //> rts                        ;and return with result in A
    return A
}

// Decompiled from GetGfxOffsetAdder
fun getGfxOffsetAdder(Y: Int) {
    var temp0: Int = 0
    var playerSize by MemoryByte(PlayerSize)
    //> GetGfxOffsetAdder:
    //> lda PlayerSize  ;get player's size
    //> beq SzOfs       ;if player big, use current offset as-is
    temp0 = playerSize
    if (playerSize != 0) {
        //> tya             ;for big player
        //> clc             ;otherwise add eight bytes to offset
        //> adc #$08        ;for small player
        //> tay
    }
    //> SzOfs:  rts             ;go back
    return
}

// Decompiled from HandleChangeSize
fun handleChangeSize() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var frameCounter by MemoryByte(FrameCounter)
    var playerAnimCtrl by MemoryByte(PlayerAnimCtrl)
    var playerChangeSizeFlag by MemoryByte(PlayerChangeSizeFlag)
    var playerSize by MemoryByte(PlayerSize)
    val changeSizeOffsetAdder by MemoryByteIndexed(ChangeSizeOffsetAdder)
    val playerGfxTblOffsets by MemoryByteIndexed(PlayerGfxTblOffsets)
    //> HandleChangeSize:
    //> ldy PlayerAnimCtrl           ;get animation frame control
    //> lda FrameCounter
    //> and #%00000011               ;get frame counter and execute this code every
    temp0 = frameCounter and 0x03
    //> bne GorSLog                  ;fourth frame, otherwise branch ahead
    temp1 = temp0
    temp2 = playerAnimCtrl
    if (temp0 == 0) {
        //> iny                          ;increment frame control
        temp2 = (temp2 + 1) and 0xFF
        //> cpy #$0a                     ;check for preset upper extent
        //> bcc CSzNext                  ;if not there yet, skip ahead to use
        if (temp2 >= 0x0A) {
            //> ldy #$00                     ;otherwise initialize both grow/shrink flag
            temp2 = 0x00
            //> sty PlayerChangeSizeFlag     ;and animation frame control
            playerChangeSizeFlag = temp2
        }
        //> CSzNext: sty PlayerAnimCtrl           ;store proper frame control
        playerAnimCtrl = temp2
    }
    //> GorSLog: lda PlayerSize               ;get player's size
    temp1 = playerSize
    //> bne ShrinkPlayer             ;if player small, skip ahead to next part
    if (temp1 == 0) {
        //> lda ChangeSizeOffsetAdder,y  ;get offset adder based on frame control as offset
        temp1 = changeSizeOffsetAdder[temp2]
        //> ldy #$0f                     ;load offset for player growing
        temp2 = 0x0F
        //> GetOffsetFromAnimCtrl:
        //> asl                        ;multiply animation frame control
        temp1 = (temp1 shl 1) and 0xFF
        //> asl                        ;by eight to get proper amount
        temp1 = (temp1 shl 1) and 0xFF
        //> asl                        ;to add to our offset
        temp1 = (temp1 shl 1) and 0xFF
        //> adc PlayerGfxTblOffsets,y  ;add to offset to graphics table
        //> rts                        ;and return with result in A
        return
    } else {
        //> ShrinkPlayer:
        //> tya                          ;add ten bytes to frame control as offset
        //> clc
        //> adc #$0a                     ;this thing apparently uses two of the swimming frames
        //> tax                          ;to draw the player shrinking
        //> ldy #$09                     ;load offset for small player swimming
        temp2 = 0x09
        //> lda ChangeSizeOffsetAdder,x  ;get what would normally be offset adder
        temp1 = changeSizeOffsetAdder[(temp2 + 0x0A) and 0xFF]
        //> bne ShrPlF                   ;and branch to use offset if nonzero
        temp3 = (temp2 + 0x0A) and 0xFF
        if (temp1 == 0) {
            //> ldy #$01                     ;otherwise load offset for big player swimming
            temp2 = 0x01
        }
    }
    //> ShrPlF: lda PlayerGfxTblOffsets,y    ;get offset to graphics table based on offset loaded
    temp1 = playerGfxTblOffsets[temp2]
    //> rts                          ;and leave
    return
}

// Decompiled from ChkForPlayerAttrib
fun chkForPlayerAttrib() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var temp5: Int = 0
    var temp6: Int = 0
    var temp7: Int = 0
    var gameEngineSubroutine by MemoryByte(GameEngineSubroutine)
    var playerGfxOffset by MemoryByte(PlayerGfxOffset)
    var playerSprdataoffset by MemoryByte(Player_SprDataOffset)
    val spriteAttributes by MemoryByteIndexed(Sprite_Attributes)
    //> ChkForPlayerAttrib:
    //> ldy Player_SprDataOffset    ;get sprite data offset
    //> lda GameEngineSubroutine
    //> cmp #$0b                    ;if executing specific game engine routine,
    //> beq KilledAtt               ;branch to change third and fourth row OAM attributes
    temp0 = gameEngineSubroutine
    temp1 = playerSprdataoffset
    if (gameEngineSubroutine != 0x0B) {
        //> lda PlayerGfxOffset         ;get graphics table offset
        temp0 = playerGfxOffset
        //> cmp #$50
        //> beq C_S_IGAtt               ;if crouch offset, either standing offset,
        if (temp0 != 0x50) {
            //> cmp #$b8                    ;or intermediate growing offset,
            //> beq C_S_IGAtt               ;go ahead and execute code to change
            if (temp0 != 0xB8) {
                //> cmp #$c0                    ;fourth row OAM attributes only
                //> beq C_S_IGAtt
                if (temp0 != 0xC0) {
                    //> cmp #$c8
                    //> bne ExPlyrAt                ;if none of these, branch to leave
                    if (temp0 == 0xC8) {
                    } else {
                        //> ExPlyrAt:  rts                         ;leave
                        return
                    }
                }
            }
        }
    }
    //> KilledAtt: lda Sprite_Attributes+16,y
    temp0 = spriteAttributes[16 + temp1]
    //> and #%00111111              ;mask out horizontal and vertical flip bits
    temp2 = temp0 and 0x3F
    //> sta Sprite_Attributes+16,y  ;for third row sprites and save
    spriteAttributes[16 + temp1] = temp2
    //> lda Sprite_Attributes+20,y
    temp0 = spriteAttributes[20 + temp1]
    //> and #%00111111
    temp3 = temp0 and 0x3F
    //> ora #%01000000              ;set horizontal flip bit for second
    temp4 = temp3 or 0x40
    //> sta Sprite_Attributes+20,y  ;sprite in the third row
    spriteAttributes[20 + temp1] = temp4
    //> C_S_IGAtt: lda Sprite_Attributes+24,y
    temp0 = spriteAttributes[24 + temp1]
    //> and #%00111111              ;mask out horizontal and vertical flip bits
    temp5 = temp0 and 0x3F
    //> sta Sprite_Attributes+24,y  ;for fourth row sprites and save
    spriteAttributes[24 + temp1] = temp5
    //> lda Sprite_Attributes+28,y
    temp0 = spriteAttributes[28 + temp1]
    //> and #%00111111
    temp6 = temp0 and 0x3F
    //> ora #%01000000              ;set horizontal flip bit for second
    temp7 = temp6 or 0x40
    //> sta Sprite_Attributes+28,y  ;sprite in the fourth row
    spriteAttributes[28 + temp1] = temp7
}

// Decompiled from RelativePlayerPosition
fun relativePlayerPosition() {
    var objectOffset by MemoryByte(ObjectOffset)
    //> RelativePlayerPosition:
    //> ldx #$00      ;set offsets for relative cooordinates
    //> ldy #$00      ;routine to correspond to player object
    //> jmp RelWOfs   ;get the coordinates
    //> RelWOfs: jsr GetObjRelativePosition  ;get the coordinates
    getObjRelativePosition(0x00, 0x00)
    //> ldx ObjectOffset            ;return original offset
    //> rts                         ;leave
    return
}

// Decompiled from RelativeBubblePosition
fun relativeBubblePosition() {
    var X: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    //> RelativeBubblePosition:
    //> ldy #$01                ;set for air bubble offsets
    //> jsr GetProperObjOffset  ;modify X to get proper air bubble offset
    getProperObjOffset(X, 0x01)
    //> ldy #$03
    //> jmp RelWOfs             ;get the coordinates
    //> RelWOfs: jsr GetObjRelativePosition  ;get the coordinates
    getObjRelativePosition(X, 0x03)
    //> ldx ObjectOffset            ;return original offset
    //> rts                         ;leave
    return
}

// Decompiled from RelativeFireballPosition
fun relativeFireballPosition() {
    var X: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    //> RelativeFireballPosition:
    //> ldy #$00                    ;set for fireball offsets
    //> jsr GetProperObjOffset      ;modify X to get proper fireball offset
    getProperObjOffset(X, 0x00)
    //> ldy #$02
    //> RelWOfs: jsr GetObjRelativePosition  ;get the coordinates
    getObjRelativePosition(X, 0x02)
    //> ldx ObjectOffset            ;return original offset
    //> rts                         ;leave
    return
}

// Decompiled from RelativeMiscPosition
fun relativeMiscPosition() {
    var X: Int = 0
    var Y: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    //> RelWOfs: jsr GetObjRelativePosition  ;get the coordinates
    getObjRelativePosition(X, Y)
    //> ldx ObjectOffset            ;return original offset
    //> rts                         ;leave
    return
}

// Decompiled from RelativeEnemyPosition
fun relativeEnemyPosition() {
    //> RelativeEnemyPosition:
    //> lda #$01                     ;get coordinates of enemy object
    //> ldy #$01                     ;relative to the screen
    //> jmp VariableObjOfsRelPos
}

// Decompiled from RelativeBlockPosition
fun relativeBlockPosition(X: Int) {
    var X: Int = X
    var temp0: Int = 0
    //> RelativeBlockPosition:
    //> lda #$09                     ;get coordinates of one block object
    //> ldy #$04                     ;relative to the screen
    //> jsr VariableObjOfsRelPos
    variableObjOfsRelPos(0x09, X)
    //> inx                          ;adjust offset for other block object if any
    X = (X + 1) and 0xFF
    //> inx
    X = (X + 1) and 0xFF
    //> lda #$09
    //> iny                          ;adjust other and get coordinates for other one
    temp0 = 0x04
    temp0 = (temp0 + 1) and 0xFF
}

// Decompiled from VariableObjOfsRelPos
fun variableObjOfsRelPos(A: Int, X: Int) {
    var Y: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    //> VariableObjOfsRelPos:
    //> stx $00                     ;store value to add to A here
    memory[0x0] = X.toUByte()
    //> clc
    //> adc $00                     ;add A to value stored
    //> tax                         ;use as enemy offset
    //> jsr GetObjRelativePosition
    getObjRelativePosition((A + memory[0x0].toInt()) and 0xFF, Y)
    //> ldx ObjectOffset            ;reload old object offset and leave
    //> rts
    return
}

// Decompiled from GetObjRelativePosition
fun getObjRelativePosition(X: Int, Y: Int) {
    var screenleftXPos by MemoryByte(ScreenLeft_X_Pos)
    val sprobjectRelXpos by MemoryByteIndexed(SprObject_Rel_XPos)
    val sprobjectRelYpos by MemoryByteIndexed(SprObject_Rel_YPos)
    val sprobjectXPosition by MemoryByteIndexed(SprObject_X_Position)
    val sprobjectYPosition by MemoryByteIndexed(SprObject_Y_Position)
    //> GetObjRelativePosition:
    //> lda SprObject_Y_Position,x  ;load vertical coordinate low
    //> sta SprObject_Rel_YPos,y    ;store here
    sprobjectRelYpos[Y] = sprobjectYPosition[X]
    //> lda SprObject_X_Position,x  ;load horizontal coordinate
    //> sec                         ;subtract left edge coordinate
    //> sbc ScreenLeft_X_Pos
    //> sta SprObject_Rel_XPos,y    ;store result here
    sprobjectRelXpos[Y] = (sprobjectXPosition[X] - screenleftXPos) and 0xFF
    //> rts
    return
}

// Decompiled from GetPlayerOffscreenBits
fun getPlayerOffscreenBits() {
    var temp0: Int = 0
    var temp1: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    val sprobjectOffscrbits by MemoryByteIndexed(SprObject_OffscrBits)
    //> GetPlayerOffscreenBits:
    //> ldx #$00                 ;set offsets for player-specific variables
    //> ldy #$00                 ;and get offscreen information about player
    //> jmp GetOffScreenBitsSet
    //> GetOffScreenBitsSet:
    //> tya                         ;save offscreen bits offset to stack for now
    //> pha
    push(0x00)
    //> jsr RunOffscrBitsSubs
    runOffscrBitsSubs(0x00, 0x00)
    //> asl                         ;move low nybble to high nybble
    //> asl
    //> asl
    //> asl
    //> ora $00                     ;mask together with previously saved low nybble
    temp0 = (((((((0x00 shl 1) and 0xFF) shl 1) and 0xFF) shl 1) and 0xFF) shl 1) and 0xFF or memory[0x0].toInt()
    //> sta $00                     ;store both here
    memory[0x0] = temp0.toUByte()
    //> pla                         ;get offscreen bits offset from stack
    temp1 = pull()
    //> tay
    //> lda $00                     ;get value here and store elsewhere
    //> sta SprObject_OffscrBits,y
    sprobjectOffscrbits[temp1] = memory[0x0].toInt()
    //> ldx ObjectOffset
    //> rts
    return
}

// Decompiled from GetFireballOffscreenBits
fun getFireballOffscreenBits() {
    var X: Int = 0
    var temp0: Int = 0
    var temp1: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    val sprobjectOffscrbits by MemoryByteIndexed(SprObject_OffscrBits)
    //> GetFireballOffscreenBits:
    //> ldy #$00                 ;set for fireball offsets
    //> jsr GetProperObjOffset   ;modify X to get proper fireball offset
    getProperObjOffset(X, 0x00)
    //> ldy #$02                 ;set other offset for fireball's offscreen bits
    //> jmp GetOffScreenBitsSet  ;and get offscreen information about fireball
    //> GetOffScreenBitsSet:
    //> tya                         ;save offscreen bits offset to stack for now
    //> pha
    push(0x02)
    //> jsr RunOffscrBitsSubs
    runOffscrBitsSubs(0x02, X)
    //> asl                         ;move low nybble to high nybble
    //> asl
    //> asl
    //> asl
    //> ora $00                     ;mask together with previously saved low nybble
    temp0 = (((((((0x02 shl 1) and 0xFF) shl 1) and 0xFF) shl 1) and 0xFF) shl 1) and 0xFF or memory[0x0].toInt()
    //> sta $00                     ;store both here
    memory[0x0] = temp0.toUByte()
    //> pla                         ;get offscreen bits offset from stack
    temp1 = pull()
    //> tay
    //> lda $00                     ;get value here and store elsewhere
    //> sta SprObject_OffscrBits,y
    sprobjectOffscrbits[temp1] = memory[0x0].toInt()
    //> ldx ObjectOffset
    //> rts
    return
}

// Decompiled from GetBubbleOffscreenBits
fun getBubbleOffscreenBits() {
    var X: Int = 0
    var temp0: Int = 0
    var temp1: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    val sprobjectOffscrbits by MemoryByteIndexed(SprObject_OffscrBits)
    //> GetBubbleOffscreenBits:
    //> ldy #$01                 ;set for air bubble offsets
    //> jsr GetProperObjOffset   ;modify X to get proper air bubble offset
    getProperObjOffset(X, 0x01)
    //> ldy #$03                 ;set other offset for airbubble's offscreen bits
    //> jmp GetOffScreenBitsSet  ;and get offscreen information about air bubble
    //> GetOffScreenBitsSet:
    //> tya                         ;save offscreen bits offset to stack for now
    //> pha
    push(0x03)
    //> jsr RunOffscrBitsSubs
    runOffscrBitsSubs(0x03, X)
    //> asl                         ;move low nybble to high nybble
    //> asl
    //> asl
    //> asl
    //> ora $00                     ;mask together with previously saved low nybble
    temp0 = (((((((0x03 shl 1) and 0xFF) shl 1) and 0xFF) shl 1) and 0xFF) shl 1) and 0xFF or memory[0x0].toInt()
    //> sta $00                     ;store both here
    memory[0x0] = temp0.toUByte()
    //> pla                         ;get offscreen bits offset from stack
    temp1 = pull()
    //> tay
    //> lda $00                     ;get value here and store elsewhere
    //> sta SprObject_OffscrBits,y
    sprobjectOffscrbits[temp1] = memory[0x0].toInt()
    //> ldx ObjectOffset
    //> rts
    return
}

// Decompiled from GetMiscOffscreenBits
fun getMiscOffscreenBits() {
    var X: Int = 0
    var temp0: Int = 0
    var temp1: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    val sprobjectOffscrbits by MemoryByteIndexed(SprObject_OffscrBits)
    //> GetMiscOffscreenBits:
    //> ldy #$02                 ;set for misc object offsets
    //> jsr GetProperObjOffset   ;modify X to get proper misc object offset
    getProperObjOffset(X, 0x02)
    //> ldy #$06                 ;set other offset for misc object's offscreen bits
    //> jmp GetOffScreenBitsSet  ;and get offscreen information about misc object
    //> GetOffScreenBitsSet:
    //> tya                         ;save offscreen bits offset to stack for now
    //> pha
    push(0x06)
    //> jsr RunOffscrBitsSubs
    runOffscrBitsSubs(0x06, X)
    //> asl                         ;move low nybble to high nybble
    //> asl
    //> asl
    //> asl
    //> ora $00                     ;mask together with previously saved low nybble
    temp0 = (((((((0x06 shl 1) and 0xFF) shl 1) and 0xFF) shl 1) and 0xFF) shl 1) and 0xFF or memory[0x0].toInt()
    //> sta $00                     ;store both here
    memory[0x0] = temp0.toUByte()
    //> pla                         ;get offscreen bits offset from stack
    temp1 = pull()
    //> tay
    //> lda $00                     ;get value here and store elsewhere
    //> sta SprObject_OffscrBits,y
    sprobjectOffscrbits[temp1] = memory[0x0].toInt()
    //> ldx ObjectOffset
    //> rts
    return
}

// Decompiled from GetProperObjOffset
fun getProperObjOffset(X: Int, Y: Int) {
    val objOffsetData by MemoryByteIndexed(ObjOffsetData)
    //> GetProperObjOffset:
    //> txa                  ;move offset to A
    //> clc
    //> adc ObjOffsetData,y  ;add amount of bytes to offset depending on setting in Y
    //> tax                  ;put back in X and leave
    //> rts
    return
}

// Decompiled from GetEnemyOffscreenBits
fun getEnemyOffscreenBits(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    val sprobjectOffscrbits by MemoryByteIndexed(SprObject_OffscrBits)
    //> GetEnemyOffscreenBits:
    //> lda #$01                 ;set A to add 1 byte in order to get enemy offset
    //> ldy #$01                 ;set Y to put offscreen bits in Enemy_OffscreenBits
    //> jmp SetOffscrBitsOffset
    //> SetOffscrBitsOffset:
    //> stx $00
    memory[0x0] = X.toUByte()
    //> clc           ;add contents of X to A to get
    //> adc $00       ;appropriate offset, then give back to X
    //> tax
    //> GetOffScreenBitsSet:
    //> tya                         ;save offscreen bits offset to stack for now
    //> pha
    push(0x01)
    //> jsr RunOffscrBitsSubs
    runOffscrBitsSubs(0x01, (0x01 + memory[0x0].toInt()) and 0xFF)
    //> asl                         ;move low nybble to high nybble
    //> asl
    //> asl
    //> asl
    //> ora $00                     ;mask together with previously saved low nybble
    temp0 = (((((((0x01 shl 1) and 0xFF) shl 1) and 0xFF) shl 1) and 0xFF) shl 1) and 0xFF or memory[0x0].toInt()
    //> sta $00                     ;store both here
    memory[0x0] = temp0.toUByte()
    //> pla                         ;get offscreen bits offset from stack
    temp1 = pull()
    //> tay
    //> lda $00                     ;get value here and store elsewhere
    //> sta SprObject_OffscrBits,y
    sprobjectOffscrbits[temp1] = memory[0x0].toInt()
    //> ldx ObjectOffset
    //> rts
    return
}

// Decompiled from GetBlockOffscreenBits
fun getBlockOffscreenBits(X: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var objectOffset by MemoryByte(ObjectOffset)
    val sprobjectOffscrbits by MemoryByteIndexed(SprObject_OffscrBits)
    //> GetBlockOffscreenBits:
    //> lda #$09       ;set A to add 9 bytes in order to get block obj offset
    //> ldy #$04       ;set Y to put offscreen bits in Block_OffscreenBits
    //> SetOffscrBitsOffset:
    //> stx $00
    memory[0x0] = X.toUByte()
    //> clc           ;add contents of X to A to get
    //> adc $00       ;appropriate offset, then give back to X
    //> tax
    //> GetOffScreenBitsSet:
    //> tya                         ;save offscreen bits offset to stack for now
    //> pha
    push(0x04)
    //> jsr RunOffscrBitsSubs
    runOffscrBitsSubs(0x04, (0x09 + memory[0x0].toInt()) and 0xFF)
    //> asl                         ;move low nybble to high nybble
    //> asl
    //> asl
    //> asl
    //> ora $00                     ;mask together with previously saved low nybble
    temp0 = (((((((0x04 shl 1) and 0xFF) shl 1) and 0xFF) shl 1) and 0xFF) shl 1) and 0xFF or memory[0x0].toInt()
    //> sta $00                     ;store both here
    memory[0x0] = temp0.toUByte()
    //> pla                         ;get offscreen bits offset from stack
    temp1 = pull()
    //> tay
    //> lda $00                     ;get value here and store elsewhere
    //> sta SprObject_OffscrBits,y
    sprobjectOffscrbits[temp1] = memory[0x0].toInt()
    //> ldx ObjectOffset
    //> rts
    return
}

// Decompiled from RunOffscrBitsSubs
fun runOffscrBitsSubs(A: Int, X: Int): Int {
    var A: Int = A
    var temp0: Int = 0  // Y register
    var temp1: Int = 0  // X register (offset)
    var bufX: Int = X
    val defaultYOnscreenOfs by MemoryByteIndexed(DefaultYOnscreenOfs)
    val highPosUnitData by MemoryByteIndexed(HighPosUnitData)
    val sprobjectYHighpos by MemoryByteIndexed(SprObject_Y_HighPos)
    val sprobjectYPosition by MemoryByteIndexed(SprObject_Y_Position)
    val yOffscreenBitsData by MemoryByteIndexed(YOffscreenBitsData)
    //> RunOffscrBitsSubs:
    //> jsr GetXOffscreenBits  ;do subroutine here
    A = getXOffscreenBits(X)
    //> lsr                    ;move high nybble to low
    A = A shr 1
    //> lsr
    A = A shr 1
    //> lsr
    A = A shr 1
    //> lsr
    A = A shr 1
    //> sta $00                ;store here
    memory[0x0] = A.toUByte()
    //> jmp GetYOffscreenBits
    //> GetYOffscreenBits:
    //> stx $04                      ;save position in buffer to here
    memory[0x4] = X.toUByte()
    //> ldy #$01                     ;start with top of screen
    temp0 = 0x01
    bufX = X

    // YOfsLoop: Main loop - Y goes from 1 to 0, then exits
    YOfsLoop@while (true) {
        //> YOfsLoop: lda HighPosUnitData,y        ;load coordinate for edge of vertical unit
        A = highPosUnitData[temp0]
        //> sec
        //> sbc SprObject_Y_Position,x   ;subtract from vertical coordinate of object
        val diff = A - sprobjectYPosition[bufX]
        //> sta $07                      ;store here
        memory[0x7] = (diff and 0xFF).toUByte()
        //> lda #$01                     ;subtract one from vertical high byte of object
        //> sbc SprObject_Y_HighPos,x
        val cmpResult = 0x01 - sprobjectYHighpos[bufX] - (if (diff < 0) 1 else 0)
        A = cmpResult and 0xFF
        //> ldx DefaultYOnscreenOfs,y    ;load offset value here
        temp1 = defaultYOnscreenOfs[temp0]
        //> cmp #$00
        //> bmi YLdBData                 ;if under top of the screen or beyond bottom, branch
        if ((cmpResult and 0x80) != 0) {
            // YLdBData: Branch taken when comparison is negative
            //> ldx DefaultYOnscreenOfs+1,y  ;if not, load alternate offset value here
            temp1 = defaultYOnscreenOfs[1 + temp0]
            //> cmp #$01
            //> bpl YLdBData2                 ;if one vertical unit or more above the screen, branch
            if (cmpResult >= 1) {
                // Skip DividePDiff - go directly to load bits
            } else {
                //> lda #$20                     ;if no branching, load value here and store
                //> sta $06
                memory[0x6] = 0x20.toUByte()
                //> lda #$04                     ;load some other value and execute subroutine
                //> jsr DividePDiff
                dividePDiff(0x04, temp0)
            }
            //> YLdBData2: lda YOffscreenBitsData,x     ;get offscreen data bits using offset
            A = yOffscreenBitsData[temp1]
            //> ldx $04                      ;reobtain position in buffer
            bufX = memory[0x4].toInt()
            //> cmp #$00
            //> bne ExYOfsBS                 ;if bits not zero, branch to leave
            if (A != 0) {
                return A  // ExYOfsBS - bits not zero, exit
            }
            // If bits are zero, fall through and continue with YOfsLoop
        }
        //> dey                          ;otherwise, do bottom of the screen now
        temp0 = (temp0 - 1) and 0xFF
        //> bpl YOfsLoop
        if ((temp0 and 0x80) != 0) {
            // Y went negative, exit
            break
        }
        // Continue YOfsLoop
    }
    //> ExYOfsBS: rts
    return A
}

// Decompiled from GetXOffscreenBits
fun getXOffscreenBits(X: Int): Int {
    var A: Int = 0
    var temp0: Int = 0  // Y register
    var temp1: Int = 0  // X register (offset)
    var bufX: Int = X
    val defaultXOnscreenOfs by MemoryByteIndexed(DefaultXOnscreenOfs)
    val screenedgePageloc by MemoryByteIndexed(ScreenEdge_PageLoc)
    val screenedgeXPos by MemoryByteIndexed(ScreenEdge_X_Pos)
    val sprobjectPageloc by MemoryByteIndexed(SprObject_PageLoc)
    val sprobjectXPosition by MemoryByteIndexed(SprObject_X_Position)
    val xOffscreenBitsData by MemoryByteIndexed(XOffscreenBitsData)
    //> GetXOffscreenBits:
    //> stx $04                     ;save position in buffer to here
    memory[0x4] = X.toUByte()
    //> ldy #$01                    ;start with right side of screen
    temp0 = 0x01
    bufX = X

    // XOfsLoop: Main loop - Y goes from 1 to 0, then exits
    XOfsLoop@while (true) {
        //> XOfsLoop: lda ScreenEdge_X_Pos,y      ;get pixel coordinate of edge
        A = screenedgeXPos[temp0]
        //> sec                         ;get difference between pixel coordinate of edge
        //> sbc SprObject_X_Position,x  ;and pixel coordinate of object position
        val diff = A - sprobjectXPosition[bufX]
        //> sta $07                     ;store here
        memory[0x7] = (diff and 0xFF).toUByte()
        //> lda ScreenEdge_PageLoc,y    ;get page location of edge
        A = screenedgePageloc[temp0]
        //> sbc SprObject_PageLoc,x     ;subtract from page location of object position
        val cmpResult = A - sprobjectPageloc[bufX] - (if (diff < 0) 1 else 0)
        A = cmpResult and 0xFF
        //> ldx DefaultXOnscreenOfs,y   ;load offset value here
        temp1 = defaultXOnscreenOfs[temp0]
        //> cmp #$00
        //> bmi XLdBData                ;if beyond right edge or in front of left edge, branch
        if ((cmpResult and 0x80) != 0) {
            // XLdBData: Branch taken when comparison is negative
            //> ldx DefaultXOnscreenOfs+1,y ;if not, load alternate offset value here
            temp1 = defaultXOnscreenOfs[1 + temp0]
            //> cmp #$01
            //> bpl XLdBData2                ;if one page or more to the left of either edge, branch
            if (cmpResult >= 1) {
                // Skip DividePDiff - go directly to load bits
            } else {
                //> lda #$38                    ;if no branching, load value here and store
                //> sta $06
                memory[0x6] = 0x38.toUByte()
                //> lda #$08                    ;load some other value and execute subroutine
                //> jsr DividePDiff
                dividePDiff(0x08, temp0)
            }
            //> XLdBData2: lda XOffscreenBitsData,x    ;get bits here
            A = xOffscreenBitsData[temp1]
            //> ldx $04                     ;reobtain position in buffer
            bufX = memory[0x4].toInt()
            //> cmp #$00                    ;if bits not zero, branch to leave
            //> bne ExXOfsBS
            if (A != 0) {
                return A  // ExXOfsBS - bits not zero, exit
            }
            // If bits are zero, fall through and continue with XOfsLoop (after this we continue the loop)
        }
        //> dey                         ;otherwise, do left side of screen now
        temp0 = (temp0 - 1) and 0xFF
        //> bpl XOfsLoop                ;branch if not already done with left side
        if ((temp0 and 0x80) != 0) {
            // Y went negative, exit
            break
        }
        // Continue XOfsLoop
    }
    //> ExXOfsBS: rts
    return A
}

// Decompiled from DividePDiff
fun dividePDiff(A: Int, Y: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    //> DividePDiff:
    //> sta $05       ;store current value in A here
    memory[0x5] = A.toUByte()
    //> lda $07       ;get pixel difference
    //> cmp $06       ;compare to preset value
    //> bcs ExDivPD   ;if pixel difference >= preset value, branch
    temp0 = memory[0x7].toInt()
    if (!(memory[0x7].toInt() >= memory[0x6].toInt())) {
        //> lsr           ;divide by eight
        temp0 = temp0 shr 1
        //> lsr
        temp0 = temp0 shr 1
        //> lsr
        temp0 = temp0 shr 1
        //> and #$07      ;mask out all but 3 LSB
        temp1 = temp0 and 0x07
        //> cpy #$01      ;right side of the screen or top?
        //> bcs SetOscrO  ;if so, branch, use difference / 8 as offset
        temp0 = temp1
        if (!(Y >= 0x01)) {
            //> adc $05       ;if not, add value to difference / 8
        }
        //> SetOscrO: tax           ;use as offset
    }
    //> ExDivPD:  rts           ;leave
    return
}

// Decompiled from SoundEngine
fun soundEngine() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var areaMusicBuffer by MemoryByte(AreaMusicBuffer)
    var areaMusicQueue by MemoryByte(AreaMusicQueue)
    var dacCounter by MemoryByte(DAC_Counter)
    var eventMusicQueue by MemoryByte(EventMusicQueue)
    var joypadPort2 by MemoryByte(JOYPAD_PORT2)
    var noiseSoundBuffer by MemoryByte(NoiseSoundBuffer)
    var noiseSoundQueue by MemoryByte(NoiseSoundQueue)
    var operMode by MemoryByte(OperMode)
    var pauseModeFlag by MemoryByte(PauseModeFlag)
    var pauseSoundBuffer by MemoryByte(PauseSoundBuffer)
    var pauseSoundQueue by MemoryByte(PauseSoundQueue)
    var sndMasterctrlReg by MemoryByte(SND_MASTERCTRL_REG)
    var squ1Sfxlencounter by MemoryByte(Squ1_SfxLenCounter)
    var square1SoundBuffer by MemoryByte(Square1SoundBuffer)
    var square1SoundQueue by MemoryByte(Square1SoundQueue)
    var square2SoundBuffer by MemoryByte(Square2SoundBuffer)
    var square2SoundQueue by MemoryByte(Square2SoundQueue)
    val sndDeltaReg by MemoryByteIndexed(SND_DELTA_REG)
    //> SoundEngine:
    //> lda OperMode              ;are we in title screen mode?
    //> bne SndOn
    temp0 = operMode
    if (operMode == 0) {
        //> sta SND_MASTERCTRL_REG    ;if so, disable sound and leave
        sndMasterctrlReg = temp0
        //> rts
        return
    } else {
        //> SndOn:   lda #$ff
        temp0 = 0xFF
        //> sta JOYPAD_PORT2          ;disable irqs and set frame counter mode???
        joypadPort2 = temp0
        //> lda #$0f
        temp0 = 0x0F
        //> sta SND_MASTERCTRL_REG    ;enable first four channels
        sndMasterctrlReg = temp0
        //> lda PauseModeFlag         ;is sound already in pause mode?
        temp0 = pauseModeFlag
        //> bne InPause
        if (temp0 == 0) {
            //> lda PauseSoundQueue       ;if not, check pause sfx queue
            temp0 = pauseSoundQueue
            //> cmp #$01
            //> bne RunSoundSubroutines   ;if queue is empty, skip pause mode routine
            if (temp0 == 0x01) {
            }
        }
    }
    //> InPause: lda PauseSoundBuffer      ;check pause sfx buffer
    temp0 = pauseSoundBuffer
    //> bne ContPau
    if (temp0 == 0) {
        //> lda PauseSoundQueue       ;check pause queue
        temp0 = pauseSoundQueue
        //> beq SkipSoundSubroutines
        if (temp0 != 0) {
            //> sta PauseSoundBuffer      ;if queue full, store in buffer and activate
            pauseSoundBuffer = temp0
            //> sta PauseModeFlag         ;pause mode to interrupt game sounds
            pauseModeFlag = temp0
            //> lda #$00                  ;disable sound and clear sfx buffers
            temp0 = 0x00
            //> sta SND_MASTERCTRL_REG
            sndMasterctrlReg = temp0
            //> sta Square1SoundBuffer
            square1SoundBuffer = temp0
            //> sta Square2SoundBuffer
            square2SoundBuffer = temp0
            //> sta NoiseSoundBuffer
            noiseSoundBuffer = temp0
            //> lda #$0f
            temp0 = 0x0F
            //> sta SND_MASTERCTRL_REG    ;enable sound again
            sndMasterctrlReg = temp0
            //> lda #$2a                  ;store length of sound in pause counter
            temp0 = 0x2A
            //> sta Squ1_SfxLenCounter
            squ1Sfxlencounter = temp0
            //> PTone1F: lda #$44                  ;play first tone
            temp0 = 0x44
            //> bne PTRegC                ;unconditional branch
            if (temp0 == 0) {
            }
        }
    }
    //> ContPau: lda Squ1_SfxLenCounter    ;check pause length left
    temp0 = squ1Sfxlencounter
    //> cmp #$24                  ;time to play second?
    //> beq PTone2F
    if (temp0 != 0x24) {
        do {
            //> cmp #$1e                  ;time to play first again?
            //> beq PTone1F
        } while (temp0 == 0x1E)
        //> cmp #$18                  ;time to play second again?
        //> bne DecPauC               ;only load regs during times, otherwise skip
        if (temp0 == 0x18) {
        }
    }
    //> PTone2F: lda #$64                  ;store reg contents and play the pause sfx
    temp0 = 0x64
    //> PTRegC:  ldx #$84
    //> ldy #$7f
    //> jsr PlaySqu1Sfx
    playSqu1Sfx()
    //> DecPauC: dec Squ1_SfxLenCounter    ;decrement pause sfx counter
    squ1Sfxlencounter = (squ1Sfxlencounter - 1) and 0xFF
    //> bne SkipSoundSubroutines
    temp1 = 0x84
    temp2 = 0x7F
    if (((squ1Sfxlencounter - 1) and 0xFF) == 0) {
        //> lda #$00                  ;disable sound if in pause mode and
        temp0 = 0x00
        //> sta SND_MASTERCTRL_REG    ;not currently playing the pause sfx
        sndMasterctrlReg = temp0
        //> lda PauseSoundBuffer      ;if no longer playing pause sfx, check to see
        temp0 = pauseSoundBuffer
        //> cmp #$02                  ;if we need to be playing sound again
        //> bne SkipPIn
        if (temp0 == 0x02) {
            //> lda #$00                  ;clear pause mode to allow game sounds again
            temp0 = 0x00
            //> sta PauseModeFlag
            pauseModeFlag = temp0
        }
        //> SkipPIn: lda #$00                  ;clear pause sfx buffer
        temp0 = 0x00
        //> sta PauseSoundBuffer
        pauseSoundBuffer = temp0
        //> beq SkipSoundSubroutines
        if (temp0 != 0) {
            //> RunSoundSubroutines:
            //> jsr Square1SfxHandler  ;play sfx on square channel 1
            square1SfxHandler()
            //> jsr Square2SfxHandler  ; ''  ''  '' square channel 2
            square2SfxHandler()
            //> jsr NoiseSfxHandler    ; ''  ''  '' noise channel
            noiseSfxHandler()
            //> jsr MusicHandler       ;play music on all channels
            musicHandler()
            //> lda #$00               ;clear the music queues
            temp0 = 0x00
            //> sta AreaMusicQueue
            areaMusicQueue = temp0
            //> sta EventMusicQueue
            eventMusicQueue = temp0
        }
    }
    //> SkipSoundSubroutines:
    //> lda #$00               ;clear the sound effects queues
    temp0 = 0x00
    //> sta Square1SoundQueue
    square1SoundQueue = temp0
    //> sta Square2SoundQueue
    square2SoundQueue = temp0
    //> sta NoiseSoundQueue
    noiseSoundQueue = temp0
    //> sta PauseSoundQueue
    pauseSoundQueue = temp0
    //> ldy DAC_Counter        ;load some sort of counter
    temp2 = dacCounter
    //> lda AreaMusicBuffer
    temp0 = areaMusicBuffer
    //> and #%00000011         ;check for specific music
    temp3 = temp0 and 0x03
    //> beq NoIncDAC
    temp0 = temp3
    if (temp3 != 0) {
        //> inc DAC_Counter        ;increment and check counter
        dacCounter = (dacCounter + 1) and 0xFF
        //> cpy #$30
        //> bcc StrWave            ;if not there yet, just store it
        if (temp2 >= 0x30) {
        } else {
            //> StrWave:  sty SND_DELTA_REG+1    ;store into DMC load register (??)
            sndDeltaReg[1] = temp2
            //> rts                    ;we are done here
            return
        }
    }
    //> NoIncDAC: tya
    //> beq StrWave            ;if we are at zero, do not decrement
    temp0 = temp2
    if (temp2 != 0) {
        //> dec DAC_Counter        ;decrement counter
        dacCounter = (dacCounter - 1) and 0xFF
    }
}

// Decompiled from Dump_Squ1_Regs
fun dumpSqu1Regs(X: Int, Y: Int) {
    val sndSquare1Reg by MemoryByteIndexed(SND_SQUARE1_REG)
    //> Dump_Squ1_Regs:
    //> sty SND_SQUARE1_REG+1  ;dump the contents of X and Y into square 1's control regs
    sndSquare1Reg[1] = Y
    //> stx SND_SQUARE1_REG
    sndSquare1Reg[0] = X
    //> rts
    return
}

// Decompiled from PlaySqu1Sfx
fun playSqu1Sfx() {
    var X: Int = 0
    var Y: Int = 0
    //> PlaySqu1Sfx:
    //> jsr Dump_Squ1_Regs     ;do sub to set ctrl regs for square 1, then set frequency regs
    dumpSqu1Regs(X, Y)
}

// Decompiled from SetFreq_Squ1
fun setfreqSqu1(A: Int) {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    val freqRegLookupTbl by MemoryByteIndexed(FreqRegLookupTbl)
    val sndRegister by MemoryByteIndexed(SND_REGISTER)
    //> SetFreq_Squ1:
    //> ldx #$00               ;set frequency reg offset for square 1 sound channel
    //> Dump_Freq_Regs:
    //> tay
    //> lda FreqRegLookupTbl+1,y  ;use previous contents of A for sound reg offset
    //> beq NoTone                ;if zero, then do not load
    temp0 = freqRegLookupTbl[1 + A]
    temp1 = 0x00
    temp2 = A
    if (freqRegLookupTbl[1 + A] != 0) {
        //> sta SND_REGISTER+2,x      ;first byte goes into LSB of frequency divider
        sndRegister[2 + temp1] = temp0
        //> lda FreqRegLookupTbl,y    ;second byte goes into 3 MSB plus extra bit for
        temp0 = freqRegLookupTbl[temp2]
        //> ora #%00001000            ;length counter
        temp3 = temp0 or 0x08
        //> sta SND_REGISTER+3,x
        sndRegister[3 + temp1] = temp3
    }
    //> NoTone: rts
    return
}

// Decompiled from Dump_Sq2_Regs
fun dumpSq2Regs(X: Int, Y: Int) {
    val sndSquare2Reg by MemoryByteIndexed(SND_SQUARE2_REG)
    //> Dump_Sq2_Regs:
    //> stx SND_SQUARE2_REG    ;dump the contents of X and Y into square 2's control regs
    sndSquare2Reg[0] = X
    //> sty SND_SQUARE2_REG+1
    sndSquare2Reg[1] = Y
    //> rts
    return
}

// Decompiled from PlaySqu2Sfx
fun playSqu2Sfx() {
    var X: Int = 0
    var Y: Int = 0
    //> PlaySqu2Sfx:
    //> jsr Dump_Sq2_Regs      ;do sub to set ctrl regs for square 2, then set frequency regs
    dumpSq2Regs(X, Y)
}

// Decompiled from SetFreq_Squ2
fun setfreqSqu2(A: Int) {
    var X: Int = 0
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    val freqRegLookupTbl by MemoryByteIndexed(FreqRegLookupTbl)
    val sndRegister by MemoryByteIndexed(SND_REGISTER)
    //> Dump_Freq_Regs:
    //> tay
    //> lda FreqRegLookupTbl+1,y  ;use previous contents of A for sound reg offset
    //> beq NoTone                ;if zero, then do not load
    temp0 = freqRegLookupTbl[1 + A]
    temp1 = A
    if (freqRegLookupTbl[1 + A] != 0) {
        //> sta SND_REGISTER+2,x      ;first byte goes into LSB of frequency divider
        sndRegister[2 + X] = temp0
        //> lda FreqRegLookupTbl,y    ;second byte goes into 3 MSB plus extra bit for
        temp0 = freqRegLookupTbl[temp1]
        //> ora #%00001000            ;length counter
        temp2 = temp0 or 0x08
        //> sta SND_REGISTER+3,x
        sndRegister[3 + X] = temp2
    }
    //> NoTone: rts
    return
}

// Decompiled from SetFreq_Tri
fun setfreqTri(A: Int) {
    var X: Int = 0
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    val freqRegLookupTbl by MemoryByteIndexed(FreqRegLookupTbl)
    val sndRegister by MemoryByteIndexed(SND_REGISTER)
    //> Dump_Freq_Regs:
    //> tay
    //> lda FreqRegLookupTbl+1,y  ;use previous contents of A for sound reg offset
    //> beq NoTone                ;if zero, then do not load
    temp0 = freqRegLookupTbl[1 + A]
    temp1 = A
    if (freqRegLookupTbl[1 + A] != 0) {
        //> sta SND_REGISTER+2,x      ;first byte goes into LSB of frequency divider
        sndRegister[2 + X] = temp0
        //> lda FreqRegLookupTbl,y    ;second byte goes into 3 MSB plus extra bit for
        temp0 = freqRegLookupTbl[temp1]
        //> ora #%00001000            ;length counter
        temp2 = temp0 or 0x08
        //> sta SND_REGISTER+3,x
        sndRegister[3 + X] = temp2
    }
    //> NoTone: rts
    return
}

// Decompiled from Square1SfxHandler
fun square1SfxHandler() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var squ1Sfxlencounter by MemoryByte(Squ1_SfxLenCounter)
    var square1SoundBuffer by MemoryByte(Square1SoundBuffer)
    var square1SoundQueue by MemoryByte(Square1SoundQueue)
    val sndSquare1Reg by MemoryByteIndexed(SND_SQUARE1_REG)
    val swimStompEnvelopeData by MemoryByteIndexed(SwimStompEnvelopeData)
    while (flagC) {
        //> PlaySmallJump:
        //> lda #$26               ;branch here for small mario jumping sound
        //> bne JumpRegContents
        temp0 = 0x26
        if (0x26 == 0) {
            //> PlayBigJump:
            //> lda #$18               ;branch here for big mario jumping sound
            temp0 = 0x18
        }
        //> JumpRegContents:
        //> ldx #$82               ;note that small and big jump borrow each others' reg contents
        //> ldy #$a7               ;anyway, this loads the first part of mario's jumping sound
        //> jsr PlaySqu1Sfx
        playSqu1Sfx()
        //> lda #$28               ;store length of sfx for both jumping sounds
        temp0 = 0x28
        //> sta Squ1_SfxLenCounter ;then continue on here
        squ1Sfxlencounter = temp0
        //> ContinueSndJump:
        //> lda Squ1_SfxLenCounter ;jumping sounds seem to be composed of three parts
        temp0 = squ1Sfxlencounter
        //> cmp #$25               ;check for time to play second part yet
        //> bne N2Prt
        temp1 = 0x82
        temp2 = 0xA7
        if (temp0 == 0x25) {
            //> ldx #$5f               ;load second part
            temp1 = 0x5F
            //> ldy #$f6
            temp2 = 0xF6
            //> bne DmpJpFPS           ;unconditional branch
            if (temp2 == 0) {
            }
        }
        //> N2Prt:    cmp #$20               ;check for third part
        //> bne DecJpFPS
        if (temp0 == 0x20) {
            //> ldx #$48               ;load third part
            temp1 = 0x48
            //> FPS2nd:   ldy #$bc               ;the flagpole slide sound shares part of third part
            temp2 = 0xBC
            //> DmpJpFPS: jsr Dump_Squ1_Regs
            dumpSqu1Regs(temp1, temp2)
            //> bne DecJpFPS           ;unconditional branch outta here
            if (temp2 == 0) {
                //> PlayFireballThrow:
                //> lda #$05
                temp0 = 0x05
                //> ldy #$99                 ;load reg contents for fireball throw sound
                temp2 = 0x99
                //> bne Fthrow               ;unconditional branch
                if (temp2 == 0) {
                    //> PlayBump:
                    //> lda #$0a                ;load length of sfx and reg contents for bump sound
                    temp0 = 0x0A
                    //> ldy #$93
                    temp2 = 0x93
                }
                //> Fthrow:   ldx #$9e                ;the fireball sound shares reg contents with the bump sound
                temp1 = 0x9E
                //> sta Squ1_SfxLenCounter
                squ1Sfxlencounter = temp0
                //> lda #$0c                ;load offset for bump sound
                temp0 = 0x0C
                //> jsr PlaySqu1Sfx
                playSqu1Sfx()
                //> ContinueBumpThrow:
                //> lda Squ1_SfxLenCounter  ;check for second part of bump sound
                temp0 = squ1Sfxlencounter
                //> cmp #$06
                //> bne DecJpFPS
                if (temp0 == 0x06) {
                    //> lda #$bb                ;load second part directly
                    temp0 = 0xBB
                    //> sta SND_SQUARE1_REG+1
                    sndSquare1Reg[1] = temp0
                }
            }
        }
        //> DecJpFPS: bne BranchToDecLength1  ;unconditional branch
        if (flagZ) {
            //> Square1SfxHandler:
            //> ldy Square1SoundQueue   ;check for sfx in queue
            temp2 = square1SoundQueue
            //> beq CheckSfx1Buffer
            if (temp2 != 0) {
                //> sty Square1SoundBuffer  ;if found, put in buffer
                square1SoundBuffer = temp2
                //> bmi PlaySmallJump       ;small jump
                //> lsr Square1SoundQueue
                square1SoundQueue = square1SoundQueue shr 1
                //> bcs PlayBigJump         ;big jump
                do {
                    //> lsr Square1SoundQueue
                    square1SoundQueue = square1SoundQueue shr 1
                    //> bcs PlayBump            ;bump
                } while ((square1SoundQueue and 0x01) != 0)
                //> lsr Square1SoundQueue
                square1SoundQueue = square1SoundQueue shr 1
                //> bcs PlaySwimStomp       ;swim/stomp
                if ((square1SoundQueue and 0x01) == 0) {
                    //> lsr Square1SoundQueue
                    square1SoundQueue = square1SoundQueue shr 1
                    //> bcs PlaySmackEnemy      ;smack enemy
                    if ((square1SoundQueue and 0x01) == 0) {
                        //> lsr Square1SoundQueue
                        square1SoundQueue = square1SoundQueue shr 1
                        //> bcs PlayPipeDownInj     ;pipedown/injury
                        if ((square1SoundQueue and 0x01) == 0) {
                            //> lsr Square1SoundQueue
                            square1SoundQueue = square1SoundQueue shr 1
                            //> bcs PlayFireballThrow   ;fireball throw
                            //> lsr Square1SoundQueue
                            square1SoundQueue = square1SoundQueue shr 1
                            //> bcs PlayFlagpoleSlide   ;slide flagpole
                        }
                    }
                }
            }
            //> CheckSfx1Buffer:
            //> lda Square1SoundBuffer   ;check for sfx in buffer
            temp0 = square1SoundBuffer
            //> beq ExS1H                ;if not found, exit sub
            if (temp0 != 0) {
                //> bmi ContinueSndJump      ;small mario jump
                //> lsr
                temp0 = temp0 shr 1
                //> bcs ContinueSndJump      ;big mario jump
                //> lsr
                temp0 = temp0 shr 1
                //> bcs ContinueBumpThrow    ;bump
                //> lsr
                temp0 = temp0 shr 1
                //> bcs ContinueSwimStomp    ;swim/stomp
                if ((temp0 and 0x01) == 0) {
                    //> lsr
                    temp0 = temp0 shr 1
                    //> bcs ContinueSmackEnemy   ;smack enemy
                    if ((temp0 and 0x01) == 0) {
                        //> lsr
                        temp0 = temp0 shr 1
                        //> bcs ContinuePipeDownInj  ;pipedown/injury
                        if ((temp0 and 0x01) == 0) {
                            //> lsr
                            temp0 = temp0 shr 1
                            //> bcs ContinueBumpThrow    ;fireball throw
                        }
                    }
                }
            } else {
                //> ExS1H: rts
                return
            }
        }
    }
    //> lsr
    temp0 = temp0 shr 1
    //> bcs DecrementSfx1Length  ;slide flagpole
    if ((temp0 and 0x01) == 0) {
        //> PlaySwimStomp:
        //> lda #$0e               ;store length of swim/stomp sound
        temp0 = 0x0E
        //> sta Squ1_SfxLenCounter
        squ1Sfxlencounter = temp0
        //> ldy #$9c               ;store reg contents for swim/stomp sound
        temp2 = 0x9C
        //> ldx #$9e
        temp1 = 0x9E
        //> lda #$26
        temp0 = 0x26
        //> jsr PlaySqu1Sfx
        playSqu1Sfx()
        //> ContinueSwimStomp:
        //> ldy Squ1_SfxLenCounter        ;look up reg contents in data section based on
        temp2 = squ1Sfxlencounter
        //> lda SwimStompEnvelopeData-1,y ;length of sound left, used to control sound's
        temp0 = swimStompEnvelopeData[-1 + temp2]
        //> sta SND_SQUARE1_REG           ;envelope
        sndSquare1Reg[0] = temp0
        //> cpy #$06
        //> bne BranchToDecLength1
        if (temp2 == 0x06) {
            //> lda #$9e                      ;when the length counts down to a certain point, put this
            temp0 = 0x9E
            //> sta SND_SQUARE1_REG+2         ;directly into the LSB of square 1's frequency divider
            sndSquare1Reg[2] = temp0
        }
        //> BranchToDecLength1:
        //> bne DecrementSfx1Length  ;unconditional branch (regardless of how we got here)
        if (flagZ) {
            //> PlaySmackEnemy:
            //> lda #$0e                 ;store length of smack enemy sound
            temp0 = 0x0E
            //> ldy #$cb
            temp2 = 0xCB
            //> ldx #$9f
            temp1 = 0x9F
            //> sta Squ1_SfxLenCounter
            squ1Sfxlencounter = temp0
            //> lda #$28                 ;store reg contents for smack enemy sound
            temp0 = 0x28
            //> jsr PlaySqu1Sfx
            playSqu1Sfx()
            //> bne DecrementSfx1Length  ;unconditional branch
            if (temp0 == 0) {
                //> ContinueSmackEnemy:
                //> ldy Squ1_SfxLenCounter  ;check about halfway through
                temp2 = squ1Sfxlencounter
                //> cpy #$08
                //> bne SmSpc
                if (temp2 == 0x08) {
                    //> lda #$a0                ;if we're at the about-halfway point, make the second tone
                    temp0 = 0xA0
                    //> sta SND_SQUARE1_REG+2   ;in the smack enemy sound
                    sndSquare1Reg[2] = temp0
                    //> lda #$9f
                    temp0 = 0x9F
                    //> bne SmTick
                    if (temp0 == 0) {
                    }
                }
                //> SmSpc:  lda #$90                ;this creates spaces in the sound, giving it its distinct noise
                temp0 = 0x90
                //> SmTick: sta SND_SQUARE1_REG
                sndSquare1Reg[0] = temp0
            }
        }
    }
    //> DecrementSfx1Length:
    //> dec Squ1_SfxLenCounter    ;decrement length of sfx
    squ1Sfxlencounter = (squ1Sfxlencounter - 1) and 0xFF
    //> bne ExSfx1
    if (((squ1Sfxlencounter - 1) and 0xFF) == 0) {
    }
    //> ExSfx1: rts
    return
}

// Decompiled from StopSquare1Sfx
fun stopSquare1Sfx() {
    var sndMasterctrlReg by MemoryByte(SND_MASTERCTRL_REG)
    //> StopSquare1Sfx:
    //> ldx #$00                ;if end of sfx reached, clear buffer
    //> stx $f1                 ;and stop making the sfx
    memory[0xF1] = 0x00.toUByte()
    //> ldx #$0e
    //> stx SND_MASTERCTRL_REG
    sndMasterctrlReg = 0x0E
    //> ldx #$0f
    //> stx SND_MASTERCTRL_REG
    sndMasterctrlReg = 0x0F
    //> ExSfx1: rts
    return
}

// Decompiled from StopSquare2Sfx
fun stopSquare2Sfx() {
    var sndMasterctrlReg by MemoryByte(SND_MASTERCTRL_REG)
    //> StopSquare2Sfx:
    //> ldx #$0d                ;stop playing the sfx
    //> stx SND_MASTERCTRL_REG
    sndMasterctrlReg = 0x0D
    //> ldx #$0f
    //> stx SND_MASTERCTRL_REG
    sndMasterctrlReg = 0x0F
    //> ExSfx2: rts
    return
}

// Decompiled from Square2SfxHandler
fun square2SfxHandler() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var squ2Sfxlencounter by MemoryByte(Squ2_SfxLenCounter)
    var square2SoundBuffer by MemoryByte(Square2SoundBuffer)
    val powerUpGrabFreqData by MemoryByteIndexed(PowerUpGrabFreqData)
    val sndSquare2Reg by MemoryByteIndexed(SND_SQUARE2_REG)
    //> PlayCoinGrab:
    //> lda #$35             ;load length of coin grab sound
    //> ldx #$8d             ;and part of reg contents
    //> bne CGrab_TTickRegL
    temp0 = 0x35
    temp1 = 0x8D
    if (0x8D == 0) {
        //> PlayTimerTick:
        //> lda #$06             ;load length of timer tick sound
        temp0 = 0x06
        //> ldx #$98             ;and part of reg contents
        temp1 = 0x98
    }
    //> CGrab_TTickRegL:
    //> sta Squ2_SfxLenCounter
    squ2Sfxlencounter = temp0
    //> ldy #$7f                ;load the rest of reg contents
    //> lda #$42                ;of coin grab and timer tick sound
    temp0 = 0x42
    //> jsr PlaySqu2Sfx
    playSqu2Sfx()
    //> ContinueCGrabTTick:
    //> lda Squ2_SfxLenCounter  ;check for time to play second tone yet
    temp0 = squ2Sfxlencounter
    //> cmp #$30                ;timer tick sound also executes this, not sure why
    //> bne N2Tone
    temp2 = 0x7F
    if (temp0 == 0x30) {
        //> lda #$54                ;if so, load the tone directly into the reg
        temp0 = 0x54
        //> sta SND_SQUARE2_REG+2
        sndSquare2Reg[2] = temp0
    }
    //> N2Tone: bne DecrementSfx2Length
    if (flagZ) {
        //> PlayBlast:
        //> lda #$20                ;load length of fireworks/gunfire sound
        temp0 = 0x20
        //> sta Squ2_SfxLenCounter
        squ2Sfxlencounter = temp0
        //> ldy #$94                ;load reg contents of fireworks/gunfire sound
        temp2 = 0x94
        //> lda #$5e
        temp0 = 0x5E
        //> bne SBlasJ
        if (temp0 == 0) {
            //> ContinueBlast:
            //> lda Squ2_SfxLenCounter  ;check for time to play second part
            temp0 = squ2Sfxlencounter
            //> cmp #$18
            //> bne DecrementSfx2Length
            if (temp0 == 0x18) {
                //> ldy #$93                ;load second part reg contents then
                temp2 = 0x93
                //> lda #$18
                temp0 = 0x18
            }
        }
        //> SBlasJ: bne BlstSJp             ;unconditional branch to load rest of reg contents
        if (flagZ) {
            //> PlayPowerUpGrab:
            //> lda #$36                    ;load length of power-up grab sound
            temp0 = 0x36
            //> sta Squ2_SfxLenCounter
            squ2Sfxlencounter = temp0
            //> ContinuePowerUpGrab:
            //> lda Squ2_SfxLenCounter      ;load frequency reg based on length left over
            temp0 = squ2Sfxlencounter
            //> lsr                         ;divide by 2
            temp0 = temp0 shr 1
            //> bcs DecrementSfx2Length     ;alter frequency every other frame
            if ((temp0 and 0x01) == 0) {
                //> tay
                //> lda PowerUpGrabFreqData-1,y ;use length left over / 2 for frequency offset
                temp0 = powerUpGrabFreqData[-1 + temp0]
                //> ldx #$5d                    ;store reg contents of power-up grab sound
                temp1 = 0x5D
                //> ldy #$7f
                temp2 = 0x7F
                //> LoadSqu2Regs:
                //> jsr PlaySqu2Sfx
                playSqu2Sfx()
            }
        }
    }
    //> DecrementSfx2Length:
    //> dec Squ2_SfxLenCounter   ;decrement length of sfx
    squ2Sfxlencounter = (squ2Sfxlencounter - 1) and 0xFF
    //> bne ExSfx2
    if (!(((squ2Sfxlencounter - 1) and 0xFF) == 0)) {
        //  goto ExSfx2
        return
    }
    if (((squ2Sfxlencounter - 1) and 0xFF) == 0) {
        //> EmptySfx2Buffer:
        //> ldx #$00                ;initialize square 2's sound effects buffer
        temp1 = 0x00
        //> stx Square2SoundBuffer
        square2SoundBuffer = temp1
    }
    //> ExSfx2: rts
    return
}

// Decompiled from NoiseSfxHandler
fun noiseSfxHandler() {
    var temp0: Int = 0
    var noiseSoundBuffer by MemoryByte(NoiseSoundBuffer)
    var noiseSfxlencounter by MemoryByte(Noise_SfxLenCounter)
    val brickShatterEnvData by MemoryByteIndexed(BrickShatterEnvData)
    val brickShatterFreqData by MemoryByteIndexed(BrickShatterFreqData)
    val sndNoiseReg by MemoryByteIndexed(SND_NOISE_REG)
    //> PlayBrickShatter:
    //> lda #$20                 ;load length of brick shatter sound
    //> sta Noise_SfxLenCounter
    noiseSfxlencounter = 0x20
    //> ContinueBrickShatter:
    //> lda Noise_SfxLenCounter
    //> lsr                         ;divide by 2 and check for bit set to use offset
    noiseSfxlencounter = noiseSfxlencounter shr 1
    //> bcc DecrementSfx3Length
    temp0 = noiseSfxlencounter
    if ((noiseSfxlencounter and 0x01) != 0) {
        //> tay
        //> ldx BrickShatterFreqData,y  ;load reg contents of brick shatter sound
        //> lda BrickShatterEnvData,y
        temp0 = brickShatterEnvData[temp0]
        //> PlayNoiseSfx:
        //> sta SND_NOISE_REG        ;play the sfx
        sndNoiseReg[0] = temp0
        //> stx SND_NOISE_REG+2
        sndNoiseReg[2] = brickShatterFreqData[temp0]
        //> lda #$18
        temp0 = 0x18
        //> sta SND_NOISE_REG+3
        sndNoiseReg[3] = temp0
    }
    //> DecrementSfx3Length:
    //> dec Noise_SfxLenCounter  ;decrement length of sfx
    noiseSfxlencounter = (noiseSfxlencounter - 1) and 0xFF
    //> bne ExSfx3
    if (((noiseSfxlencounter - 1) and 0xFF) == 0) {
        //> lda #$f0                 ;if done, stop playing the sfx
        temp0 = 0xF0
        //> sta SND_NOISE_REG
        sndNoiseReg[0] = temp0
        //> lda #$00
        temp0 = 0x00
        //> sta NoiseSoundBuffer
        noiseSoundBuffer = temp0
    }
    //> ExSfx3: rts
    return
}

// Decompiled from MusicHandler
fun musicHandler() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp10: Int = 0
    var temp11: Int = 0
    var temp12: Int = 0
    var temp13: Int = 0
    var temp14: Int = 0
    var temp2: Int = 0
    var temp3: Int = 0
    var temp4: Int = 0
    var temp5: Int = 0
    var temp6: Int = 0
    var temp7: Int = 0
    var temp8: Int = 0
    var temp9: Int = 0
    var altRegContentFlag by MemoryByte(AltRegContentFlag)
    var areaMusicBuffer by MemoryByte(AreaMusicBuffer)
    var areamusicbufferAlt by MemoryByte(AreaMusicBuffer_Alt)
    var areaMusicQueue by MemoryByte(AreaMusicQueue)
    var eventMusicBuffer by MemoryByte(EventMusicBuffer)
    var eventMusicQueue by MemoryByte(EventMusicQueue)
    var groundMusicHeaderOfs by MemoryByte(GroundMusicHeaderOfs)
    var musicDataHigh by MemoryByte(MusicDataHigh)
    var musicDataLow by MemoryByte(MusicDataLow)
    var musicoffsetNoise by MemoryByte(MusicOffset_Noise)
    var musicoffsetSquare1 by MemoryByte(MusicOffset_Square1)
    var musicoffsetSquare2 by MemoryByte(MusicOffset_Square2)
    var musicoffsetTriangle by MemoryByte(MusicOffset_Triangle)
    var noiseDataLoopbackOfs by MemoryByte(NoiseDataLoopbackOfs)
    var noiseBeatlencounter by MemoryByte(Noise_BeatLenCounter)
    var noteLenLookupTblOfs by MemoryByte(NoteLenLookupTblOfs)
    var noteLengthTblAdder by MemoryByte(NoteLengthTblAdder)
    var sndMasterctrlReg by MemoryByte(SND_MASTERCTRL_REG)
    var sndTriangleReg by MemoryByte(SND_TRIANGLE_REG)
    var squ1Envelopedatactrl by MemoryByte(Squ1_EnvelopeDataCtrl)
    var squ1Notelencounter by MemoryByte(Squ1_NoteLenCounter)
    var squ2Envelopedatactrl by MemoryByte(Squ2_EnvelopeDataCtrl)
    var squ2Notelenbuffer by MemoryByte(Squ2_NoteLenBuffer)
    var squ2Notelencounter by MemoryByte(Squ2_NoteLenCounter)
    var square1SoundBuffer by MemoryByte(Square1SoundBuffer)
    var square2SoundBuffer by MemoryByte(Square2SoundBuffer)
    var triNotelenbuffer by MemoryByte(Tri_NoteLenBuffer)
    var triNotelencounter by MemoryByte(Tri_NoteLenCounter)
    val musicHeaderData by MemoryByteIndexed(MusicHeaderData)
    val musicHeaderOffsetData by MemoryByteIndexed(MusicHeaderOffsetData)
    val sndNoiseReg by MemoryByteIndexed(SND_NOISE_REG)
    val sndSquare1Reg by MemoryByteIndexed(SND_SQUARE1_REG)
    val sndSquare2Reg by MemoryByteIndexed(SND_SQUARE2_REG)
    //> ContinueMusic:
    //> jmp HandleSquare2Music  ;if we have music, start with square 2 channel
    //> MusicHandler:
    //> lda EventMusicQueue     ;check event music queue
    //> bne LoadEventMusic
    if (!(eventMusicQueue == 0)) {
        //  goto LoadEventMusic
        return
    }
    temp0 = eventMusicQueue
    if (eventMusicQueue == 0) {
        //> lda AreaMusicQueue      ;check area music queue
        temp0 = areaMusicQueue
        //> bne LoadAreaMusic
        if (!(temp0 == 0)) {
            //  goto LoadAreaMusic
            return
        }
        if (temp0 == 0) {
            //> lda EventMusicBuffer    ;check both buffers
            temp0 = eventMusicBuffer
            //> ora AreaMusicBuffer
            temp1 = temp0 or areaMusicBuffer
            //> bne ContinueMusic
            if (!(temp1 == 0)) {
                //  goto ContinueMusic
                return
            }
            //> rts                     ;no music, then leave
            return
        }
    }
    //> LoadEventMusic:
    //> sta EventMusicBuffer      ;copy event music queue contents to buffer
    eventMusicBuffer = temp0
    //> cmp #DeathMusic           ;is it death music?
    //> bne NoStopSfx             ;if not, jump elsewhere
    if (temp0 == DeathMusic) {
        //> jsr StopSquare1Sfx        ;stop sfx in square 1 and 2
        stopSquare1Sfx()
        //> jsr StopSquare2Sfx        ;but clear only square 1's sfx buffer
        stopSquare2Sfx()
    }
    //> NoStopSfx: ldx AreaMusicBuffer
    //> stx AreaMusicBuffer_Alt   ;save current area music buffer to be re-obtained later
    areamusicbufferAlt = areaMusicBuffer
    //> ldy #$00
    //> sty NoteLengthTblAdder    ;default value for additional length byte offset
    noteLengthTblAdder = 0x00
    //> sty AreaMusicBuffer       ;clear area music buffer
    areaMusicBuffer = 0x00
    //> cmp #TimeRunningOutMusic  ;is it time running out music?
    //> bne FindEventMusicHeader
    temp2 = areaMusicBuffer
    temp3 = 0x00
    if (temp0 == TimeRunningOutMusic) {
        //> ldx #$08                  ;load offset to be added to length byte of header
        temp2 = 0x08
        //> stx NoteLengthTblAdder
        noteLengthTblAdder = temp2
        //> bne FindEventMusicHeader  ;unconditional branch
        if (temp2 == 0) {
            //> LoadAreaMusic:
            //> cmp #$04                  ;is it underground music?
            //> bne NoStop1               ;no, do not stop square 1 sfx
            if (temp0 == 0x04) {
                //> jsr StopSquare1Sfx
                stopSquare1Sfx()
            }
            //> NoStop1: ldy #$10                  ;start counter used only by ground level music
            temp3 = 0x10
            while (temp3 != 0) {
                //> GMLoopB: sty GroundMusicHeaderOfs
                groundMusicHeaderOfs = temp3
                //> HandleAreaMusicLoopB:
                //> ldy #$00                  ;clear event music buffer
                temp3 = 0x00
                //> sty EventMusicBuffer
                eventMusicBuffer = temp3
                //> sta AreaMusicBuffer       ;copy area music queue contents to buffer
                areaMusicBuffer = temp0
                //> cmp #$01                  ;is it ground level music?
                //> bne FindAreaMusicHeader
                //> inc GroundMusicHeaderOfs  ;increment but only if playing ground level music
                groundMusicHeaderOfs = (groundMusicHeaderOfs + 1) and 0xFF
                //> ldy GroundMusicHeaderOfs  ;is it time to loopback ground level music?
                temp3 = groundMusicHeaderOfs
                //> cpy #$32
                //> bne LoadHeader            ;branch ahead with alternate offset
                //> ldy #$11
                temp3 = 0x11
                //> bne GMLoopB               ;unconditional branch
            }
            //> FindAreaMusicHeader:
            //> ldy #$08                   ;load Y for offset of area music
            temp3 = 0x08
            //> sty MusicOffset_Square2    ;residual instruction here
            musicoffsetSquare2 = temp3
        }
    }
    do {
        //> FindEventMusicHeader:
        //> iny                       ;increment Y pointer based on previously loaded queue contents
        temp3 = (temp3 + 1) and 0xFF
        //> lsr                       ;bit shift and increment until we find a set bit for music
        temp0 = temp0 shr 1
        //> bcc FindEventMusicHeader
    } while ((temp0 and 0x01) == 0)
    //> LoadHeader:
    //> lda MusicHeaderOffsetData,y  ;load offset for header
    temp0 = musicHeaderOffsetData[temp3]
    //> tay
    //> lda MusicHeaderData,y        ;now load the header
    temp0 = musicHeaderData[temp0]
    //> sta NoteLenLookupTblOfs
    noteLenLookupTblOfs = temp0
    //> lda MusicHeaderData+1,y
    temp0 = musicHeaderData[1 + temp0]
    //> sta MusicDataLow
    musicDataLow = temp0
    //> lda MusicHeaderData+2,y
    temp0 = musicHeaderData[2 + temp0]
    //> sta MusicDataHigh
    musicDataHigh = temp0
    //> lda MusicHeaderData+3,y
    temp0 = musicHeaderData[3 + temp0]
    //> sta MusicOffset_Triangle
    musicoffsetTriangle = temp0
    //> lda MusicHeaderData+4,y
    temp0 = musicHeaderData[4 + temp0]
    //> sta MusicOffset_Square1
    musicoffsetSquare1 = temp0
    //> lda MusicHeaderData+5,y
    temp0 = musicHeaderData[5 + temp0]
    //> sta MusicOffset_Noise
    musicoffsetNoise = temp0
    //> sta NoiseDataLoopbackOfs
    noiseDataLoopbackOfs = temp0
    //> lda #$01                     ;initialize music note counters
    temp0 = 0x01
    //> sta Squ2_NoteLenCounter
    squ2Notelencounter = temp0
    //> sta Squ1_NoteLenCounter
    squ1Notelencounter = temp0
    //> sta Tri_NoteLenCounter
    triNotelencounter = temp0
    //> sta Noise_BeatLenCounter
    noiseBeatlencounter = temp0
    //> lda #$00                     ;initialize music data offset for square 2
    temp0 = 0x00
    //> sta MusicOffset_Square2
    musicoffsetSquare2 = temp0
    //> sta AltRegContentFlag        ;initialize alternate control reg data used by square 1
    altRegContentFlag = temp0
    //> lda #$0b                     ;disable triangle channel and reenable it
    temp0 = 0x0B
    //> sta SND_MASTERCTRL_REG
    sndMasterctrlReg = temp0
    //> lda #$0f
    temp0 = 0x0F
    //> sta SND_MASTERCTRL_REG
    sndMasterctrlReg = temp0
    //> HandleSquare2Music:
    //> dec Squ2_NoteLenCounter  ;decrement square 2 note length
    squ2Notelencounter = (squ2Notelencounter - 1) and 0xFF
    //> bne MiscSqu2MusicTasks   ;is it time for more data?  if not, branch to end tasks
    temp3 = temp0
    if (((squ2Notelencounter - 1) and 0xFF) == 0) {
        //> ldy MusicOffset_Square2  ;increment square 2 music offset and fetch data
        temp3 = musicoffsetSquare2
        //> inc MusicOffset_Square2
        musicoffsetSquare2 = (musicoffsetSquare2 + 1) and 0xFF
        //> lda (MusicData),y
        temp0 = memory[readWord(MusicData) + temp3].toInt()
        //> beq EndOfMusicData       ;if zero, the data is a null terminator
        if (temp0 != 0) {
            //> bpl Squ2NoteHandler      ;if non-negative, data is a note
            if ((temp0 and 0x80) != 0) {
                //> bne Squ2LengthHandler    ;otherwise it is length data
                if (temp0 == 0) {
                }
            }
        }
        //> EndOfMusicData:
        //> lda EventMusicBuffer     ;check secondary buffer for time running out music
        temp0 = eventMusicBuffer
        //> cmp #TimeRunningOutMusic
        //> bne NotTRO
        if (temp0 == TimeRunningOutMusic) {
            //> lda AreaMusicBuffer_Alt  ;load previously saved contents of primary buffer
            temp0 = areamusicbufferAlt
            //> bne MusicLoopBack        ;and start playing the song again if there is one
            if (!(temp0 == 0)) {
                //  goto MusicLoopBack
                return
            }
            if (temp0 == 0) {
            }
        }
        //> NotTRO: and #VictoryMusic        ;check for victory music (the only secondary that loops)
        temp4 = temp0 and VictoryMusic
        //> bne VictoryMLoopBack
        if (!(temp4 == 0)) {
            //  goto VictoryMLoopBack
            return
        }
        temp0 = temp4
        if (temp4 == 0) {
            //> lda AreaMusicBuffer      ;check primary buffer for any music except pipe intro
            temp0 = areaMusicBuffer
            //> and #%01011111
            temp5 = temp0 and 0x5F
            //> bne MusicLoopBack        ;if any area music except pipe intro, music loops
            if (!(temp5 == 0)) {
                //  goto MusicLoopBack
                return
            }
            temp0 = temp5
            if (temp5 == 0) {
                //> lda #$00                 ;clear primary and secondary buffers and initialize
                temp0 = 0x00
                //> sta AreaMusicBuffer      ;control regs of square and triangle channels
                areaMusicBuffer = temp0
                //> sta EventMusicBuffer
                eventMusicBuffer = temp0
                //> sta SND_TRIANGLE_REG
                sndTriangleReg = temp0
                //> lda #$90
                temp0 = 0x90
                //> sta SND_SQUARE1_REG
                sndSquare1Reg[0] = temp0
                //> sta SND_SQUARE2_REG
                sndSquare2Reg[0] = temp0
                //> rts
                return
            }
            // MusicLoopBack: jmp HandleAreaMusicLoopB
            // Music loop - return for now, handled per frame
            return
        }
        // VictoryMLoopBack: jmp LoadEventMusic
        // Victory music loop - return for now, handled per frame
        return
        //> Squ2LengthHandler:
        //> jsr ProcessLengthData    ;store length of note
        processLengthData(temp0)
        //> sta Squ2_NoteLenBuffer
        squ2Notelenbuffer = temp0
        //> ldy MusicOffset_Square2  ;fetch another byte (MUST NOT BE LENGTH BYTE!)
        temp3 = musicoffsetSquare2
        //> inc MusicOffset_Square2
        musicoffsetSquare2 = (musicoffsetSquare2 + 1) and 0xFF
        //> lda (MusicData),y
        temp0 = memory[readWord(MusicData) + temp3].toInt()
        //> Squ2NoteHandler:
        //> ldx Square2SoundBuffer     ;is there a sound playing on this channel?
        temp2 = square2SoundBuffer
        //> bne SkipFqL1
        if (temp2 == 0) {
            //> jsr SetFreq_Squ2           ;no, then play the note
            setfreqSqu2(temp0)
            //> beq Rest                   ;check to see if note is rest
            if (temp2 != 0) {
                //> jsr LoadControlRegs        ;if not, load control regs for square 2
                loadControlRegs()
            }
            //> Rest:     sta Squ2_EnvelopeDataCtrl  ;save contents of A
            squ2Envelopedatactrl = temp0
            //> jsr Dump_Sq2_Regs          ;dump X and Y into square 2 control regs
            dumpSq2Regs(temp2, temp3)
        }
        //> SkipFqL1: lda Squ2_NoteLenBuffer     ;save length in square 2 note counter
        temp0 = squ2Notelenbuffer
        //> sta Squ2_NoteLenCounter
        squ2Notelencounter = temp0
    }
    //> MiscSqu2MusicTasks:
    //> lda Square2SoundBuffer     ;is there a sound playing on square 2?
    temp0 = square2SoundBuffer
    //> bne HandleSquare1Music
    if (temp0 == 0) {
        //> lda EventMusicBuffer       ;check for death music or d4 set on secondary buffer
        temp0 = eventMusicBuffer
        //> and #%10010001             ;note that regs for death music or d4 are loaded by default
        temp6 = temp0 and 0x91
        //> bne HandleSquare1Music
        temp0 = temp6
        if (temp6 == 0) {
            //> ldy Squ2_EnvelopeDataCtrl  ;check for contents saved from LoadControlRegs
            temp3 = squ2Envelopedatactrl
            //> beq NoDecEnv1
            if (temp3 != 0) {
                //> dec Squ2_EnvelopeDataCtrl  ;decrement unless already zero
                squ2Envelopedatactrl = (squ2Envelopedatactrl - 1) and 0xFF
            }
            //> NoDecEnv1: jsr LoadEnvelopeData       ;do a load of envelope data to replace default
            loadEnvelopeData(temp3)
            //> sta SND_SQUARE2_REG        ;based on offset set by first load unless playing
            sndSquare2Reg[0] = temp0
            //> ldx #$7f                   ;death music or d4 set on secondary buffer
            temp2 = 0x7F
            //> stx SND_SQUARE2_REG+1
            sndSquare2Reg[1] = temp2
        }
    }
    //> HandleSquare1Music:
    //> ldy MusicOffset_Square1    ;is there a nonzero offset here?
    temp3 = musicoffsetSquare1
    //> beq HandleTriangleMusic    ;if not, skip ahead to the triangle channel
    if (temp3 != 0) {
        //> dec Squ1_NoteLenCounter    ;decrement square 1 note length
        squ1Notelencounter = (squ1Notelencounter - 1) and 0xFF
        //> bne MiscSqu1MusicTasks     ;is it time for more data?
        if (((squ1Notelencounter - 1) and 0xFF) == 0) {
            while (temp0 != 0) {
                do {
                    //> FetchSqu1MusicData:
                    //> ldy MusicOffset_Square1    ;increment square 1 music offset and fetch data
                    temp3 = musicoffsetSquare1
                    //> inc MusicOffset_Square1
                    musicoffsetSquare1 = (musicoffsetSquare1 + 1) and 0xFF
                    //> lda (MusicData),y
                    temp0 = memory[readWord(MusicData) + temp3].toInt()
                    //> bne Squ1NoteHandler        ;if nonzero, then skip this part
                    //> lda #$83
                    temp0 = 0x83
                    //> sta SND_SQUARE1_REG        ;store some data into control regs for square 1
                    sndSquare1Reg[0] = temp0
                    //> lda #$94                   ;and fetch another byte of data, used to give
                    temp0 = 0x94
                    //> sta SND_SQUARE1_REG+1      ;death music its unique sound
                    sndSquare1Reg[1] = temp0
                    //> sta AltRegContentFlag
                    altRegContentFlag = temp0
                    //> bne FetchSqu1MusicData     ;unconditional branch
                } while (temp0 != 0)
            }
            //> Squ1NoteHandler:
            //> jsr AlternateLengthHandler
            alternateLengthHandler(temp0)
            //> sta Squ1_NoteLenCounter    ;save contents of A in square 1 note counter
            squ1Notelencounter = temp0
            //> ldy Square1SoundBuffer     ;is there a sound playing on square 1?
            temp3 = square1SoundBuffer
            //> bne HandleTriangleMusic
            if (temp3 == 0) {
                //> txa
                //> and #%00111110             ;change saved data to appropriate note format
                temp7 = temp2 and 0x3E
                //> jsr SetFreq_Squ1           ;play the note
                setfreqSqu1(temp7)
                //> beq SkipCtrlL
                temp0 = temp7
                if (temp7 != 0) {
                    //> jsr LoadControlRegs
                    loadControlRegs()
                }
                //> SkipCtrlL: sta Squ1_EnvelopeDataCtrl  ;save envelope offset
                squ1Envelopedatactrl = temp0
                //> jsr Dump_Squ1_Regs
                dumpSqu1Regs(temp2, temp3)
            }
        }
        //> MiscSqu1MusicTasks:
        //> lda Square1SoundBuffer     ;is there a sound playing on square 1?
        temp0 = square1SoundBuffer
        //> bne HandleTriangleMusic
        if (temp0 == 0) {
            //> lda EventMusicBuffer       ;check for death music or d4 set on secondary buffer
            temp0 = eventMusicBuffer
            //> and #%10010001
            temp8 = temp0 and 0x91
            //> bne DeathMAltReg
            temp0 = temp8
            if (temp8 == 0) {
                //> ldy Squ1_EnvelopeDataCtrl  ;check saved envelope offset
                temp3 = squ1Envelopedatactrl
                //> beq NoDecEnv2
                if (temp3 != 0) {
                    //> dec Squ1_EnvelopeDataCtrl  ;decrement unless already zero
                    squ1Envelopedatactrl = (squ1Envelopedatactrl - 1) and 0xFF
                }
                //> NoDecEnv2:    jsr LoadEnvelopeData       ;do a load of envelope data
                loadEnvelopeData(temp3)
                //> sta SND_SQUARE1_REG        ;based on offset set by first load
                sndSquare1Reg[0] = temp0
            }
            //> DeathMAltReg: lda AltRegContentFlag      ;check for alternate control reg data
            temp0 = altRegContentFlag
            //> bne DoAltLoad
            if (temp0 == 0) {
                //> lda #$7f                   ;load this value if zero, the alternate value
                temp0 = 0x7F
            }
            //> DoAltLoad:    sta SND_SQUARE1_REG+1      ;if nonzero, and let's move on
            sndSquare1Reg[1] = temp0
        }
    }
    //> HandleTriangleMusic:
    //> lda MusicOffset_Triangle
    temp0 = musicoffsetTriangle
    //> dec Tri_NoteLenCounter    ;decrement triangle note length
    triNotelencounter = (triNotelencounter - 1) and 0xFF
    //> bne HandleNoiseMusic      ;is it time for more data?
    if (((triNotelencounter - 1) and 0xFF) == 0) {
        //> ldy MusicOffset_Triangle  ;increment square 1 music offset and fetch data
        temp3 = musicoffsetTriangle
        //> inc MusicOffset_Triangle
        musicoffsetTriangle = (musicoffsetTriangle + 1) and 0xFF
        //> lda (MusicData),y
        temp0 = memory[readWord(MusicData) + temp3].toInt()
        //> beq LoadTriCtrlReg        ;if zero, skip all this and move on to noise
        if (temp0 != 0) {
            //> bpl TriNoteHandler        ;if non-negative, data is note
            if ((temp0 and 0x80) != 0) {
                //> jsr ProcessLengthData     ;otherwise, it is length data
                processLengthData(temp0)
                //> sta Tri_NoteLenBuffer     ;save contents of A
                triNotelenbuffer = temp0
                //> lda #$1f
                temp0 = 0x1F
                //> sta SND_TRIANGLE_REG      ;load some default data for triangle control reg
                sndTriangleReg = temp0
                //> ldy MusicOffset_Triangle  ;fetch another byte
                temp3 = musicoffsetTriangle
                //> inc MusicOffset_Triangle
                musicoffsetTriangle = (musicoffsetTriangle + 1) and 0xFF
                //> lda (MusicData),y
                temp0 = memory[readWord(MusicData) + temp3].toInt()
                //> beq LoadTriCtrlReg        ;check once more for nonzero data
                if (temp0 != 0) {
                }
            }
            //> TriNoteHandler:
            //> jsr SetFreq_Tri
            setfreqTri(temp0)
            //> ldx Tri_NoteLenBuffer   ;save length in triangle note counter
            temp2 = triNotelenbuffer
            //> stx Tri_NoteLenCounter
            triNotelencounter = temp2
            //> lda EventMusicBuffer
            temp0 = eventMusicBuffer
            //> and #%01101110          ;check for death music or d4 set on secondary buffer
            temp9 = temp0 and 0x6E
            //> bne NotDOrD4            ;if playing any other secondary, skip primary buffer check
            temp0 = temp9
            if (temp9 == 0) {
                //> lda AreaMusicBuffer     ;check primary buffer for water or castle level music
                temp0 = areaMusicBuffer
                //> and #%00001010
                temp10 = temp0 and 0x0A
                //> beq HandleNoiseMusic    ;if playing any other primary, or death or d4, go on to noise routine
                temp0 = temp10
                if (temp10 != 0) {
                }
            }
            //> NotDOrD4: txa                     ;if playing water or castle music or any secondary
            //> cmp #$12                ;besides death music or d4 set, check length of note
            //> bcs LongN
            temp0 = temp2
            if (!(temp2 >= 0x12)) {
                //> lda EventMusicBuffer    ;check for win castle music again if not playing a long note
                temp0 = eventMusicBuffer
                //> and #EndOfCastleMusic
                temp11 = temp0 and EndOfCastleMusic
                //> beq MediN
                temp0 = temp11
                if (temp11 != 0) {
                    //> lda #$0f                ;load value $0f if playing the win castle music and playing a short
                    temp0 = 0x0F
                    //> bne LoadTriCtrlReg      ;note, load value $1f if playing water or castle level music or any
                    if (temp0 == 0) {
                    }
                }
                //> MediN:    lda #$1f                ;secondary besides death and d4 except win castle or win castle and playing
                temp0 = 0x1F
                //> bne LoadTriCtrlReg      ;a short note, and load value $ff if playing a long note on water, castle
                if (temp0 == 0) {
                }
            }
            //> LongN:    lda #$ff                ;or any secondary (including win castle) except death and d4
            temp0 = 0xFF
        }
        //> LoadTriCtrlReg:
        //> sta SND_TRIANGLE_REG      ;save final contents of A into control reg for triangle
        sndTriangleReg = temp0
    }
    //> HandleNoiseMusic:
    //> lda AreaMusicBuffer       ;check if playing underground or castle music
    temp0 = areaMusicBuffer
    //> and #%11110011
    temp12 = temp0 and 0xF3
    //> beq ExitMusicHandler      ;if so, skip the noise routine
    temp0 = temp12
    if (temp12 != 0) {
        //> dec Noise_BeatLenCounter  ;decrement noise beat length
        noiseBeatlencounter = (noiseBeatlencounter - 1) and 0xFF
        //> bne ExitMusicHandler      ;is it time for more data?
        if (((noiseBeatlencounter - 1) and 0xFF) == 0) {
            while (temp0 != 0) {
                do {
                    //> FetchNoiseBeatData:
                    //> ldy MusicOffset_Noise       ;increment noise beat offset and fetch data
                    temp3 = musicoffsetNoise
                    //> inc MusicOffset_Noise
                    musicoffsetNoise = (musicoffsetNoise + 1) and 0xFF
                    //> lda (MusicData),y           ;get noise beat data, if nonzero, branch to handle
                    temp0 = memory[readWord(MusicData) + temp3].toInt()
                    //> bne NoiseBeatHandler
                    //> lda NoiseDataLoopbackOfs    ;if data is zero, reload original noise beat offset
                    temp0 = noiseDataLoopbackOfs
                    //> sta MusicOffset_Noise       ;and loopback next time around
                    musicoffsetNoise = temp0
                    //> bne FetchNoiseBeatData      ;unconditional branch
                } while (temp0 != 0)
            }
            //> NoiseBeatHandler:
            //> jsr AlternateLengthHandler
            alternateLengthHandler(temp0)
            //> sta Noise_BeatLenCounter    ;store length in noise beat counter
            noiseBeatlencounter = temp0
            //> txa
            //> and #%00111110              ;reload data and erase length bits
            temp13 = temp2 and 0x3E
            //> beq SilentBeat              ;if no beat data, silence
            temp0 = temp13
            if (temp13 != 0) {
                //> cmp #$30                    ;check the beat data and play the appropriate
                //> beq LongBeat                ;noise accordingly
                if (temp0 != 0x30) {
                    //> cmp #$20
                    //> beq StrongBeat
                    if (temp0 != 0x20) {
                        //> and #%00010000
                        temp14 = temp0 and 0x10
                        //> beq SilentBeat
                        temp0 = temp14
                        if (temp14 != 0) {
                            //> lda #$1c        ;short beat data
                            temp0 = 0x1C
                            //> ldx #$03
                            temp2 = 0x03
                            //> ldy #$18
                            temp3 = 0x18
                            //> bne PlayBeat
                            if (temp3 == 0) {
                            }
                        }
                    }
                    //> StrongBeat:
                    //> lda #$1c        ;strong beat data
                    temp0 = 0x1C
                    //> ldx #$0c
                    temp2 = 0x0C
                    //> ldy #$18
                    temp3 = 0x18
                    //> bne PlayBeat
                    if (temp3 == 0) {
                    }
                }
                //> LongBeat:
                //> lda #$1c        ;long beat data
                temp0 = 0x1C
                //> ldx #$03
                temp2 = 0x03
                //> ldy #$58
                temp3 = 0x58
                //> bne PlayBeat
                if (temp3 == 0) {
                }
            }
            //> SilentBeat:
            //> lda #$10        ;silence
            temp0 = 0x10
            //> PlayBeat:
            //> sta SND_NOISE_REG    ;load beat data into noise regs
            sndNoiseReg[0] = temp0
            //> stx SND_NOISE_REG+2
            sndNoiseReg[2] = temp2
            //> sty SND_NOISE_REG+3
            sndNoiseReg[3] = temp3
        }
    }
    //> ExitMusicHandler:
    //> rts
    return
}

// Decompiled from AlternateLengthHandler
fun alternateLengthHandler(A: Int): Int {
    var A: Int = A
    //> AlternateLengthHandler:
    //> tax            ;save a copy of original byte into X
    //> ror            ;save LSB from original byte into carry
    A = A shr 1 or if (flagC) 0x80 else 0
    //> txa            ;reload original byte and rotate three times
    //> rol            ;turning xx00000x into 00000xxx, with the
    A = (A shl 1) and 0xFE or if ((A and 0x01) != 0) 1 else 0
    //> rol            ;bit in carry as the MSB here
    A = (A shl 1) and 0xFE or if ((A and 0x80) != 0) 1 else 0
    //> rol
    A = (A shl 1) and 0xFE or if ((A and 0x80) != 0) 1 else 0
    return A  // FIX: Return the modified value
}

// Decompiled from ProcessLengthData
fun processLengthData(A: Int): Int {
    var temp0: Int = 0
    var noteLengthTblAdder by MemoryByte(NoteLengthTblAdder)
    val musicLengthLookupTbl by MemoryByteIndexed(MusicLengthLookupTbl)
    //> ProcessLengthData:
    //> and #%00000111              ;clear all but the three LSBs
    temp0 = A and 0x07
    //> clc
    //> adc $f0                     ;add offset loaded from first header byte
    //> adc NoteLengthTblAdder      ;add extra if time running out music
    //> tay
    //> lda MusicLengthLookupTbl,y  ;load length
    //> rts
    return A
}

// Decompiled from LoadControlRegs
fun loadControlRegs() {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var areaMusicBuffer by MemoryByte(AreaMusicBuffer)
    var eventMusicBuffer by MemoryByte(EventMusicBuffer)
    //> LoadControlRegs:
    //> lda EventMusicBuffer  ;check secondary buffer for win castle music
    //> and #EndOfCastleMusic
    temp0 = eventMusicBuffer and EndOfCastleMusic
    //> beq NotECstlM
    temp1 = temp0
    if (temp0 != 0) {
        //> lda #$04              ;this value is only used for win castle music
        temp1 = 0x04
        //> bne AllMus            ;unconditional branch
        if (temp1 == 0) {
        } else {
            //> AllMus:    ldx #$82              ;load contents of other sound regs for square 2
            //> ldy #$7f
            //> rts
            return
        }
    }
    //> NotECstlM: lda AreaMusicBuffer
    temp1 = areaMusicBuffer
    //> and #%01111101        ;check primary buffer for water music
    temp2 = temp1 and 0x7D
    //> beq WaterMus
    temp1 = temp2
    if (temp2 != 0) {
        //> lda #$08              ;this is the default value for all other music
        temp1 = 0x08
        //> bne AllMus
        if (temp1 == 0) {
        }
    }
    //> WaterMus:  lda #$28              ;this value is used for water music and all other event music
    temp1 = 0x28
}

// Decompiled from LoadEnvelopeData
fun loadEnvelopeData(Y: Int): Int {
    var temp0: Int = 0
    var temp1: Int = 0
    var temp2: Int = 0
    var areaMusicBuffer by MemoryByte(AreaMusicBuffer)
    var eventMusicBuffer by MemoryByte(EventMusicBuffer)
    val areaMusicEnvData by MemoryByteIndexed(AreaMusicEnvData)
    val endOfCastleMusicEnvData by MemoryByteIndexed(EndOfCastleMusicEnvData)
    val waterEventMusEnvData by MemoryByteIndexed(WaterEventMusEnvData)
    //> LoadEnvelopeData:
    //> lda EventMusicBuffer           ;check secondary buffer for win castle music
    //> and #EndOfCastleMusic
    temp0 = eventMusicBuffer and EndOfCastleMusic
    //> beq LoadUsualEnvData
    temp1 = temp0
    if (temp0 != 0) {
        //> lda EndOfCastleMusicEnvData,y  ;load data from offset for win castle music
        temp1 = endOfCastleMusicEnvData[Y]
        //> rts
        return A
    } else {
        //> LoadUsualEnvData:
        //> lda AreaMusicBuffer            ;check primary buffer for water music
        temp1 = areaMusicBuffer
        //> and #%01111101
        temp2 = temp1 and 0x7D
        //> beq LoadWaterEventMusEnvData
        temp1 = temp2
        if (temp2 != 0) {
            //> lda AreaMusicEnvData,y         ;load default data from offset for all other music
            temp1 = areaMusicEnvData[Y]
            //> rts
            return A
        }
    }
    //> LoadWaterEventMusEnvData:
    //> lda WaterEventMusEnvData,y     ;load data from offset for water music and all other event music
    temp1 = waterEventMusEnvData[Y]
    //> rts
    return A
}

