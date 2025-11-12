package com.ivieleague.smb

// Decompiled Super Mario Bros. NES ROM
// Generated from smbdism.asm

// Decompiled from @0
fun func_0() {
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
    PPU_CTRL_REG1 = 0x10
    //> ldx #$ff                     ;reset stack pointer
    //> txs
    do {
        //> VBlank1:     lda PPU_STATUS               ;wait two frames
        //> bpl VBlank1
    } while (negativeFlag)
    do {
        //> VBlank2:     lda PPU_STATUS
        //> bpl VBlank2
    } while (negativeFlag)
    //> ldy #ColdBootOffset          ;load default cold boot pointer
    //> ldx #$05                     ;this is where we check for a warm boot
    while (negativeFlag) {
        do {
            //> WBootCheck:  lda TopScoreDisplay,x        ;check each score digit in the top score
            //> cmp #10                      ;to see if we have a valid digit
            //> bcs ColdBoot                 ;if not, give up and proceed with cold boot
            //> dex
            //> bpl WBootCheck
        } while (!negativeFlag)
    }
    //> lda WarmBootValidation       ;second checkpoint, check to see if
    //> cmp #$a5                     ;another location has a specific value
    //> bne ColdBoot
    if (WarmBootValidation - 0xA5 == 0) {
        //> ldy #WarmBootOffset          ;if passed both, load warm boot pointer
    }
    //> ColdBoot:    jsr InitializeMemory         ;clear memory using pointer in Y
    initializeMemory(WarmBootOffset)
    //> sta SND_DELTA_REG+1          ;reset delta counter load register
    SND_DELTA_REG = WarmBootValidation
    //> sta OperMode                 ;reset primary mode of operation
    OperMode = WarmBootValidation
    //> lda #$a5                     ;set warm boot flag
    //> sta WarmBootValidation
    WarmBootValidation = 0xA5
    //> sta PseudoRandomBitReg       ;set seed for pseudorandom register
    PseudoRandomBitReg = 0xA5
    //> lda #%00001111
    //> sta SND_MASTERCTRL_REG       ;enable all sound channels except dmc
    SND_MASTERCTRL_REG = 0x0F
    //> lda #%00000110
    //> sta PPU_CTRL_REG2            ;turn off clipping for OAM and background
    PPU_CTRL_REG2 = 0x06
    //> jsr MoveAllSpritesOffscreen
    moveAllSpritesOffscreen()
    //> jsr InitializeNameTables     ;initialize both name tables
    initializeNameTables()
    //> inc DisableScreenFlag        ;set flag to disable screen output
    DisableScreenFlag = (DisableScreenFlag + 1) and 0xFF
    //> lda Mirror_PPU_CTRL_REG1
    //> ora #%10000000               ;enable NMIs
    //> jsr WritePPUReg1
    writePPUReg1(Mirror_PPU_CTRL_REG1 or 0x80)
    while (true) {
        //> EndlessLoop: jmp EndlessLoop              ;endless loop, need I say more?
    }
}

// Decompiled from PauseRoutine
fun pauseRoutine() {
    var A: Int = 0
    //> PauseRoutine:
    //> lda OperMode           ;are we in victory mode?
    //> cmp #VictoryModeValue  ;if so, go ahead
    //> beq ChkPauseTimer
    if (!(OperMode - VictoryModeValue == 0)) {
        //> cmp #GameModeValue     ;are we in game mode?
        //> bne ExitPause          ;if not, leave
        if (A - GameModeValue == 0) {
            //> lda OperMode_Task      ;if we are in game mode, are we running game engine?
            //> cmp #$03
            //> bne ExitPause          ;if not, leave
            if (OperMode_Task - 0x03 == 0) {
                //> ChkPauseTimer: lda GamePauseTimer     ;check if pause timer is still counting down
                //> beq ChkStart
                if (!zeroFlag) {
                    //> dec GamePauseTimer     ;if so, decrement and leave
                    GamePauseTimer = (GamePauseTimer - 1) and 0xFF
                    //> rts
                    return
                }
                //> ChkStart:      lda SavedJoypad1Bits   ;check to see if start is pressed
                //> and #Start_Button      ;on controller 1
                //> beq ClrPauseTimer
                if (!zeroFlag) {
                    //> lda GamePauseStatus    ;check to see if timer flag is set
                    //> and #%10000000         ;and if so, do not reset timer (residual,
                    //> bne ExitPause          ;joypad reading routine makes this unnecessary)
                    if (zeroFlag) {
                        //> lda #$2b               ;set pause timer
                        //> sta GamePauseTimer
                        GamePauseTimer = 0x2B
                        //> lda GamePauseStatus
                        //> tay
                        //> iny                    ;set pause sfx queue for next pause mode
                        //> sty PauseSoundQueue
                        PauseSoundQueue = (GamePauseStatus + 1) and 0xFF
                        //> eor #%00000001         ;invert d0 and set d7
                        //> ora #%10000000
                        //> bne SetPause           ;unconditional branch
                        if (zeroFlag) {
                            //> ClrPauseTimer: lda GamePauseStatus    ;clear timer flag if timer is at zero and start button
                            //> and #%01111111         ;is not pressed
                        }
                        //> SetPause:      sta GamePauseStatus
                        GamePauseStatus = GamePauseStatus and 0x7F
                    }
                }
            }
        }
    }
    if (!zeroFlag) {
    }
    if (!zeroFlag) {
        if (zeroFlag) {
            if (zeroFlag) {
            }
        }
    }
    //> ExitPause:     rts
    return
}

// Decompiled from SpriteShuffler
fun spriteShuffler() {
    //> SpriteShuffler:
    //> ldy AreaType                ;load level type, likely residual code
    //> lda #$28                    ;load preset value which will put it at
    //> sta $00                     ;sprite #10
    zp_00 = 0x28
    //> ldx #$0e                    ;start at the end of OAM data offsets
    while (negativeFlag) {
        //> ldy SprShuffleAmtOffset     ;get current offset to preset value we want to add
        //> clc
        //> adc SprShuffleAmt,y         ;get shuffle amount, add to current sprite offset
        //> bcc StrSprOffset            ;if not exceeded $ff, skip second add
        if (0) {
            //> clc
            //> adc $00                     ;otherwise add preset value $28 to offset
        }
        //> StrSprOffset:  sta SprDataOffset,x         ;store new offset here or old one if branched to here
        SprDataOffset[0x0E] = ((0x28 + SprShuffleAmt[SprShuffleAmtOffset] + 0) and 0xFF + zp_00 + 0) and 0xFF
        do {
            //> ShuffleLoop:   lda SprDataOffset,x         ;check for offset value against
            //> cmp $00                     ;the preset value
            //> bcc NextSprOffset           ;if less, skip this part
            //> NextSprOffset: dex                         ;move backwards to next one
            //> bpl ShuffleLoop
        } while (!negativeFlag)
    }
    //> ldx SprShuffleAmtOffset     ;load offset
    //> inx
    //> cpx #$03                    ;check if offset + 1 goes to 3
    //> bne SetAmtOffset            ;if offset + 1 not 3, store
    if ((SprShuffleAmtOffset + 1) and 0xFF == 0x03) {
        //> ldx #$00                    ;otherwise, init to 0
    }
    //> SetAmtOffset:  stx SprShuffleAmtOffset
    SprShuffleAmtOffset = 0x00
    //> ldx #$08                    ;load offsets for values and storage
    //> ldy #$02
    do {
        //> SetMiscOffset: lda SprDataOffset+5,y       ;load one of three OAM data offsets
        //> sta Misc_SprDataOffset-2,x  ;store first one unmodified, but
        Misc_SprDataOffset[0x08] = SprDataOffset[0x02]
        //> clc                         ;add eight to the second and eight
        //> adc #$08                    ;more to the third one
        //> sta Misc_SprDataOffset-1,x  ;note that due to the way X is set up,
        Misc_SprDataOffset[0x08] = (SprDataOffset[0x02] + 0x08 + 0) and 0xFF
        //> clc                         ;this code loads into the misc sprite offsets
        //> adc #$08
        //> sta Misc_SprDataOffset,x
        Misc_SprDataOffset[0x08] = ((SprDataOffset[0x02] + 0x08 + 0) and 0xFF + 0x08 + 0) and 0xFF
        //> dex
        //> dex
        //> dex
        //> dey
        //> bpl SetMiscOffset           ;do this until all misc spr offsets are loaded
    } while (negativeFlag)
    //> rts
    return
}

// Decompiled from OperModeExecutionTree
fun operModeExecutionTree() {
    //> OperModeExecutionTree:
    //> lda OperMode     ;this is the heart of the entire program,
    //> jsr JumpEngine   ;most of what goes on starts here
    jumpEngine(OperMode)
    //> .dw TitleScreenMode
    //> .dw GameMode
    //> .dw VictoryMode
    //> .dw GameOverMode
    //> ;-------------------------------------------------------------------------------------
}

// Decompiled from MoveAllSpritesOffscreen
fun moveAllSpritesOffscreen() {
    //> MoveAllSpritesOffscreen:
    //> ldy #$00                ;this routine moves all sprites off the screen
    //> .db $2c                 ;BIT instruction opcode
}

// Decompiled from MoveSpritesOffscreen
fun moveSpritesOffscreen() {
    //> MoveSpritesOffscreen:
    //> ldy #$04                ;this routine moves all but sprite 0
    //> lda #$f8                ;off the screen
    do {
        //> SprInitLoop:  sta Sprite_Y_Position,y ;write 248 into OAM data's Y coordinate
        Sprite_Y_Position[0x04] = 0xF8
        //> iny                     ;which will move it off the screen
        //> iny
        //> iny
        //> iny
        //> bne SprInitLoop
    } while (zeroFlag)
    //> rts
    return
}

// Decompiled from GoContinue
fun goContinue(A: Int) {
    //> GoContinue:   sta WorldNumber             ;start both players at the first area
    WorldNumber = A
    //> sta OffScr_WorldNumber      ;of the previously saved world number
    OffScr_WorldNumber = A
    //> ldx #$00                    ;note that on power-up using this function
    //> stx AreaNumber              ;will make no difference
    AreaNumber = 0x00
    //> stx OffScr_AreaNumber
    OffScr_AreaNumber = 0x00
    //> rts
    return
}

// Decompiled from DrawMushroomIcon
fun drawMushroomIcon() {
    //> DrawMushroomIcon:
    //> ldy #$07                ;read eight bytes to be read by transfer routine
    do {
        //> IconDataRead: lda MushroomIconData,y  ;note that the default position is set for a
        //> sta VRAM_Buffer1-1,y    ;1-player game
        VRAM_Buffer1[0x07] = MushroomIconData[0x07]
        //> dey
        //> bpl IconDataRead
    } while (negativeFlag)
    //> lda NumberOfPlayers     ;check number of players
    //> beq ExitIcon            ;if set to 1-player game, we're done
    if (!zeroFlag) {
        //> lda #$24                ;otherwise, load blank tile in 1-player position
        //> sta VRAM_Buffer1+3
        VRAM_Buffer1 = 0x24
        //> lda #$ce                ;then load shroom icon tile in 2-player position
        //> sta VRAM_Buffer1+5
        VRAM_Buffer1 = 0xCE
    }
    //> ExitIcon:     rts
    return
}

// Decompiled from DemoEngine
fun demoEngine() {
    //> DemoEngine:
    //> ldx DemoAction         ;load current demo action
    //> lda DemoActionTimer    ;load current action timer
    //> bne DoAction           ;if timer still counting down, skip
    if (zeroFlag) {
        //> inx
        //> inc DemoAction         ;if expired, increment action, X, and
        DemoAction = (DemoAction + 1) and 0xFF
        //> sec                    ;set carry by default for demo over
        //> lda DemoTimingData-1,x ;get next timer
        //> sta DemoActionTimer    ;store as current timer
        DemoActionTimer = DemoTimingData[(DemoAction + 1) and 0xFF]
        //> beq DemoOver           ;if timer already at zero, skip
        if (!zeroFlag) {
            //> DoAction: lda DemoActionData-1,x ;get and perform action (current or next)
            //> sta SavedJoypad1Bits
            SavedJoypad1Bits = DemoActionData[(DemoAction + 1) and 0xFF]
            //> dec DemoActionTimer    ;decrement action timer
            DemoActionTimer = (DemoActionTimer - 1) and 0xFF
            //> clc                    ;clear carry if demo still going
        }
    }
    //> DemoOver: rts
    return
}

// Decompiled from VictoryModeSubroutines
fun victoryModeSubroutines() {
    //> VictoryModeSubroutines:
    //> lda OperMode_Task
    //> jsr JumpEngine
    jumpEngine(OperMode_Task)
    //> .dw BridgeCollapse
    //> .dw SetupVictoryMode
    //> .dw PlayerVictoryWalk
    //> .dw PrintVictoryMessages
    //> .dw PlayerEndWorld
    //> ;-------------------------------------------------------------------------------------
    //> SetupVictoryMode:
    //> ldx ScreenRight_PageLoc  ;get page location of right side of screen
    //> inx                      ;increment to next page
    //> stx DestinationPageLoc   ;store here
    DestinationPageLoc = (ScreenRight_PageLoc + 1) and 0xFF
    //> lda #EndOfCastleMusic
    //> sta EventMusicQueue      ;play win castle music
    EventMusicQueue = EndOfCastleMusic
    //> jmp IncModeTask_B        ;jump to set next major task in victory mode
    //> IncModeTask_B: inc OperMode_Task  ;move onto next mode
    OperMode_Task = (OperMode_Task + 1) and 0xFF
    //> rts
    return
}

// Decompiled from FloateyNumbersRoutine
fun floateyNumbersRoutine(X: Int) {
    //> EndExitOne:    rts                        ;and leave
    return
}

// Decompiled from GetPlayerColors
fun getPlayerColors() {
    //> GetPlayerColors:
    //> ldx VRAM_Buffer1_Offset  ;get current buffer offset
    //> ldy #$00
    //> lda CurrentPlayer        ;check which player is on the screen
    //> beq ChkFiery
    if (!zeroFlag) {
        //> ldy #$04                 ;load offset for luigi
    }
    //> ChkFiery:      lda PlayerStatus         ;check player status
    //> cmp #$02
    //> bne StartClrGet          ;if fiery, load alternate offset for fiery player
    if (PlayerStatus - 0x02 == 0) {
        //> ldy #$08
    }
    //> StartClrGet:   lda #$03                 ;do four colors
    //> sta $00
    zp_00 = 0x03
    do {
        //> ClrGetLoop:    lda PlayerColors,y       ;fetch player colors and store them
        //> sta VRAM_Buffer1+3,x     ;in the buffer
        VRAM_Buffer1[VRAM_Buffer1_Offset] = PlayerColors[0x08]
        //> iny
        //> inx
        //> dec $00
        zp_00 = (zp_00 - 1) and 0xFF
        //> bpl ClrGetLoop
    } while (negativeFlag)
    //> ldx VRAM_Buffer1_Offset  ;load original offset from before
    //> ldy BackgroundColorCtrl  ;if this value is four or greater, it will be set
    //> bne SetBGColor           ;therefore use it as offset to background color
    if (zeroFlag) {
        //> ldy AreaType             ;otherwise use area type bits from area offset as offset
    }
    //> SetBGColor:    lda BackgroundColors,y   ;to background color instead
    //> sta VRAM_Buffer1+3,x
    VRAM_Buffer1[VRAM_Buffer1_Offset] = BackgroundColors[AreaType]
    //> lda #$3f                 ;set for sprite palette address
    //> sta VRAM_Buffer1,x       ;save to buffer
    VRAM_Buffer1[VRAM_Buffer1_Offset] = 0x3F
    //> lda #$10
    //> sta VRAM_Buffer1+1,x
    VRAM_Buffer1[VRAM_Buffer1_Offset] = 0x10
    //> lda #$04                 ;write length byte to buffer
    //> sta VRAM_Buffer1+2,x
    VRAM_Buffer1[VRAM_Buffer1_Offset] = 0x04
    //> lda #$00                 ;now the null terminator
    //> sta VRAM_Buffer1+7,x
    VRAM_Buffer1[VRAM_Buffer1_Offset] = 0x00
    //> txa                      ;move the buffer pointer ahead 7 bytes
    //> clc                      ;in case we want to write anything else later
    //> adc #$07
    //> SetVRAMOffset: sta VRAM_Buffer1_Offset  ;store as new vram buffer offset
    VRAM_Buffer1_Offset = (VRAM_Buffer1_Offset + 0x07 + 0) and 0xFF
    //> rts
    return
}

// Decompiled from WriteGameText
fun writeGameText(A: Int) {
    //> SetVRAMOffset: sta VRAM_Buffer1_Offset  ;store as new vram buffer offset
    VRAM_Buffer1_Offset = A
    //> rts
    return
}

// Decompiled from ResetScreenTimer
fun resetScreenTimer() {
    //> ResetScreenTimer:
    //> lda #$07                    ;reset timer again
    //> sta ScreenTimer
    ScreenTimer = 0x07
    //> inc ScreenRoutineTask       ;move onto next task
    ScreenRoutineTask = (ScreenRoutineTask + 1) and 0xFF
    //> NoReset: rts
    return
}

// Decompiled from RenderAttributeTables
fun renderAttributeTables() {
    var X: Int = 0
    //> RenderAttributeTables:
    //> lda CurrentNTAddr_Low    ;get low byte of next name table address
    //> and #%00011111           ;to be written to, mask out all but 5 LSB,
    //> sec                      ;subtract four
    //> sbc #$04
    //> and #%00011111           ;mask out bits again and store
    //> sta $01
    zp_01 = (CurrentNTAddr_Low and 0x1F - 0x04 - (1 - 1)) and 0xFF and 0x1F
    //> lda CurrentNTAddr_High   ;get high byte and branch if borrow not set
    //> bcs SetATHigh
    if (!1) {
        //> eor #%00000100           ;otherwise invert d2
    }
    //> SetATHigh:   and #%00000100           ;mask out all other bits
    //> ora #$23                 ;add $2300 to the high byte and store
    //> sta $00
    zp_00 = CurrentNTAddr_High xor 0x04 and 0x04 or 0x23
    //> lda $01                  ;get low byte - 4, divide by 4, add offset for
    //> lsr                      ;attribute table and store
    //> lsr
    //> adc #$c0                 ;we should now have the appropriate block of
    //> sta $01                  ;attribute table in our temp address
    zp_01 = (zp_01 shr 1 shr 1 + 0xC0 + 1) and 0xFF
    //> ldx #$00
    //> ldy VRAM_Buffer2_Offset  ;get buffer offset
    do {
        //> AttribLoop:  lda $00
        //> sta VRAM_Buffer2,y       ;store high byte of attribute table address
        VRAM_Buffer2[VRAM_Buffer2_Offset] = zp_00
        //> lda $01
        //> clc                      ;get low byte, add 8 because we want to start
        //> adc #$08                 ;below the status bar, and store
        //> sta VRAM_Buffer2+1,y
        VRAM_Buffer2[VRAM_Buffer2_Offset] = (zp_01 + 0x08 + 0) and 0xFF
        //> sta $01                  ;also store in temp again
        zp_01 = (zp_01 + 0x08 + 0) and 0xFF
        //> lda AttributeBuffer,x    ;fetch current attribute table byte and store
        //> sta VRAM_Buffer2+3,y     ;in the buffer
        VRAM_Buffer2[VRAM_Buffer2_Offset] = AttributeBuffer[0x00]
        //> lda #$01
        //> sta VRAM_Buffer2+2,y     ;store length of 1 in buffer
        VRAM_Buffer2[VRAM_Buffer2_Offset] = 0x01
        //> lsr
        //> sta AttributeBuffer,x    ;clear current byte in attribute buffer
        AttributeBuffer[0x00] = 0x01 shr 1
        //> iny                      ;increment buffer offset by 4 bytes
        //> iny
        //> iny
        //> iny
        //> inx                      ;increment attribute offset and check to see
        //> cpx #$07                 ;if we're at the end yet
        //> bcc AttribLoop
    } while ((X + 1) and 0xFF >= 0x07)
    //> sta VRAM_Buffer2,y       ;put null terminator at the end
    VRAM_Buffer2[((((VRAM_Buffer2_Offset + 1) and 0xFF + 1) and 0xFF + 1) and 0xFF + 1) and 0xFF] = 0x01 shr 1
    //> sty VRAM_Buffer2_Offset  ;store offset in case we want to do any more
    VRAM_Buffer2_Offset = ((((VRAM_Buffer2_Offset + 1) and 0xFF + 1) and 0xFF + 1) and 0xFF + 1) and 0xFF
    //> SetVRAMCtrl: lda #$06
    //> sta VRAM_Buffer_AddrCtrl ;set buffer to $0341 and leave
    VRAM_Buffer_AddrCtrl = 0x06
    //> rts
    return
}

// Decompiled from ColorRotation
fun colorRotation() {
    var Y: Int = 0
    //> ColorRotation:
    //> lda FrameCounter         ;get frame counter
    //> and #$07                 ;mask out all but three LSB
    //> bne ExitColorRot         ;branch if not set to zero to do this every eighth frame
    if (zeroFlag) {
        //> ldx VRAM_Buffer1_Offset  ;check vram buffer offset
        //> cpx #$31
        //> bcs ExitColorRot         ;if offset over 48 bytes, branch to leave
        if (!(VRAM_Buffer1_Offset >= 0x31)) {
            //> tay                      ;otherwise use frame counter's 3 LSB as offset here
            do {
                //> GetBlankPal:  lda BlankPalette,y       ;get blank palette for palette 3
                //> sta VRAM_Buffer1,x       ;store it in the vram buffer
                VRAM_Buffer1[VRAM_Buffer1_Offset] = BlankPalette[FrameCounter and 0x07]
                //> inx                      ;increment offsets
                //> iny
                //> cpy #$08
                //> bcc GetBlankPal          ;do this until all bytes are copied
            } while ((Y + 1) and 0xFF >= 0x08)
            //> ldx VRAM_Buffer1_Offset  ;get current vram buffer offset
            //> lda #$03
            //> sta $00                  ;set counter here
            zp_00 = 0x03
            //> lda AreaType             ;get area type
            //> asl                      ;multiply by 4 to get proper offset
            //> asl
            //> tay                      ;save as offset here
            do {
                //> GetAreaPal:   lda Palette3Data,y       ;fetch palette to be written based on area type
                //> sta VRAM_Buffer1+3,x     ;store it to overwrite blank palette in vram buffer
                VRAM_Buffer1[VRAM_Buffer1_Offset] = Palette3Data[((AreaType shl 1) and 0xFF shl 1) and 0xFF]
                //> iny
                //> inx
                //> dec $00                  ;decrement counter
                zp_00 = (zp_00 - 1) and 0xFF
                //> bpl GetAreaPal           ;do this until the palette is all copied
            } while (negativeFlag)
            //> ldx VRAM_Buffer1_Offset  ;get current vram buffer offset
            //> ldy ColorRotateOffset    ;get color cycling offset
            //> lda ColorRotatePalette,y
            //> sta VRAM_Buffer1+4,x     ;get and store current color in second slot of palette
            VRAM_Buffer1[VRAM_Buffer1_Offset] = ColorRotatePalette[ColorRotateOffset]
            //> lda VRAM_Buffer1_Offset
            //> clc                      ;add seven bytes to vram buffer offset
            //> adc #$07
            //> sta VRAM_Buffer1_Offset
            VRAM_Buffer1_Offset = (VRAM_Buffer1_Offset + 0x07 + 0) and 0xFF
            //> inc ColorRotateOffset    ;increment color cycling offset
            ColorRotateOffset = (ColorRotateOffset + 1) and 0xFF
            //> lda ColorRotateOffset
            //> cmp #$06                 ;check to see if it's still in range
            //> bcc ExitColorRot         ;if so, branch to leave
            if (ColorRotateOffset >= 0x06) {
                //> lda #$00
                //> sta ColorRotateOffset    ;otherwise, init to keep it in range
                ColorRotateOffset = 0x00
            }
        }
    }
    //> ExitColorRot: rts                      ;leave
    return
}

// Decompiled from RemoveCoin_Axe
fun removecoinAxe() {
    //> RemoveCoin_Axe:
    //> ldy #$41                 ;set low byte so offset points to $0341
    //> lda #$03                 ;load offset for default blank metatile
    //> ldx AreaType             ;check area type
    //> bne WriteBlankMT         ;if not water type, use offset
    if (zeroFlag) {
        //> lda #$04                 ;otherwise load offset for blank metatile used in water
    }
    //> WriteBlankMT: jsr PutBlockMetatile     ;do a sub to write blank metatile to vram buffer
    putBlockMetatile(0x04, AreaType, 0x41)
    //> lda #$06
    //> sta VRAM_Buffer_AddrCtrl ;set vram address controller to $0341 and leave
    VRAM_Buffer_AddrCtrl = 0x06
    //> rts
    return
}

// Decompiled from ReplaceBlockMetatile
fun replaceBlockMetatile(X: Int) {
    var A: Int = 0
    //> ReplaceBlockMetatile:
    //> jsr WriteBlockMetatile    ;write metatile to vram buffer to replace block object
    writeBlockMetatile(A)
    //> inc Block_ResidualCounter ;increment unused counter (residual code)
    Block_ResidualCounter = (Block_ResidualCounter + 1) and 0xFF
    //> dec Block_RepFlag,x       ;decrement flag (residual code)
    Block_RepFlag[X] = (Block_RepFlag[X] - 1) and 0xFF
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
    //> WriteBlockMetatile:
    //> ldy #$03                ;load offset for blank metatile
    //> cmp #$00                ;check contents of A for blank metatile
    //> beq UseBOffset          ;branch if found (unconditional if branched from 8a6b)
    if (!(A - 0x00 == 0)) {
        //> ldy #$00                ;load offset for brick metatile w/ line
        //> cmp #$58
        //> beq UseBOffset          ;use offset if metatile is brick with coins (w/ line)
        if (!(A - 0x58 == 0)) {
            //> cmp #$51
            //> beq UseBOffset          ;use offset if metatile is breakable brick w/ line
            if (!(A - 0x51 == 0)) {
                //> iny                     ;increment offset for brick metatile w/o line
                //> cmp #$5d
                //> beq UseBOffset          ;use offset if metatile is brick with coins (w/o line)
                if (!(A - 0x5D == 0)) {
                    //> cmp #$52
                    //> beq UseBOffset          ;use offset if metatile is breakable brick w/o line
                    if (!(A - 0x52 == 0)) {
                        //> iny                     ;if any other metatile, increment offset for empty block
                    }
                }
            }
        }
    }
    //> UseBOffset:  tya                     ;put Y in A
    //> ldy VRAM_Buffer1_Offset ;get vram buffer offset
    //> iny                     ;move onto next byte
    //> jsr PutBlockMetatile    ;get appropriate block data and write to vram buffer
    putBlockMetatile(((0x00 + 1) and 0xFF + 1) and 0xFF, X, (VRAM_Buffer1_Offset + 1) and 0xFF)
}

// Decompiled from MoveVOffset
fun moveVOffset(Y: Int) {
    var A: Int = 0
    //> SetVRAMOffset: sta VRAM_Buffer1_Offset  ;store as new vram buffer offset
    VRAM_Buffer1_Offset = A
    //> rts
    return
}

// Decompiled from PutBlockMetatile
fun putBlockMetatile(A: Int, X: Int, Y: Int) {
    //> PutBlockMetatile:
    //> stx $00               ;store control bit from SprDataOffset_Ctrl
    zp_00 = X
    //> sty $01               ;store vram buffer offset for next byte
    zp_01 = Y
    //> asl
    //> asl                   ;multiply A by four and use as X
    //> tax
    //> ldy #$20              ;load high byte for name table 0
    //> lda $06               ;get low byte of block buffer pointer
    //> cmp #$d0              ;check to see if we're on odd-page block buffer
    //> bcc SaveHAdder        ;if not, use current high byte
    if (zp_06 >= 0xD0) {
        //> ldy #$24              ;otherwise load high byte for name table 1
    }
    //> SaveHAdder: sty $03               ;save high byte here
    zp_03 = 0x24
    //> and #$0f              ;mask out high nybble of block buffer pointer
    //> asl                   ;multiply by 2 to get appropriate name table low byte
    //> sta $04               ;and then store it here
    zp_04 = (zp_06 and 0x0F shl 1) and 0xFF
    //> lda #$00
    //> sta $05               ;initialize temp high byte
    zp_05 = 0x00
    //> lda $02               ;get vertical high nybble offset used in block buffer routine
    //> clc
    //> adc #$20              ;add 32 pixels for the status bar
    //> asl
    //> rol $05               ;shift and rotate d7 onto d0 and d6 into carry
    //> asl
    //> rol $05               ;shift and rotate d6 onto d0 and d5 into carry
    //> adc $04               ;add low byte of name table and carry to vertical high nybble
    //> sta $04               ;and store here
    zp_04 = ((((zp_02 + 0x20 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF + zp_04 + 0) and 0xFF
    //> lda $05               ;get whatever was in d7 and d6 of vertical high nybble
    //> adc #$00              ;add carry
    //> clc
    //> adc $03               ;then add high byte of name table
    //> sta $05               ;store here
    zp_05 = ((zp_05 + 0x00 + 0) and 0xFF + zp_03 + 0) and 0xFF
    //> ldy $01               ;get vram buffer offset to be used
}

// Decompiled from RemBridge
fun remBridge(X: Int, Y: Int) {
    //> RemBridge:  lda BlockGfxData,x    ;write top left and top right
    //> sta VRAM_Buffer1+2,y  ;tile numbers into first spot
    VRAM_Buffer1[Y] = BlockGfxData[X]
    //> lda BlockGfxData+1,x
    //> sta VRAM_Buffer1+3,y
    VRAM_Buffer1[Y] = BlockGfxData[X]
    //> lda BlockGfxData+2,x  ;write bottom left and bottom
    //> sta VRAM_Buffer1+7,y  ;right tiles numbers into
    VRAM_Buffer1[Y] = BlockGfxData[X]
    //> lda BlockGfxData+3,x  ;second spot
    //> sta VRAM_Buffer1+8,y
    VRAM_Buffer1[Y] = BlockGfxData[X]
    //> lda $04
    //> sta VRAM_Buffer1,y    ;write low byte of name table
    VRAM_Buffer1[Y] = zp_04
    //> clc                   ;into first slot as read
    //> adc #$20              ;add 32 bytes to value
    //> sta VRAM_Buffer1+5,y  ;write low byte of name table
    VRAM_Buffer1[Y] = (zp_04 + 0x20 + 0) and 0xFF
    //> lda $05               ;plus 32 bytes into second slot
    //> sta VRAM_Buffer1-1,y  ;write high byte of name
    VRAM_Buffer1[Y] = zp_05
    //> sta VRAM_Buffer1+4,y  ;table address to both slots
    VRAM_Buffer1[Y] = zp_05
    //> lda #$02
    //> sta VRAM_Buffer1+1,y  ;put length of 2 in
    VRAM_Buffer1[Y] = 0x02
    //> sta VRAM_Buffer1+6,y  ;both slots
    VRAM_Buffer1[Y] = 0x02
    //> lda #$00
    //> sta VRAM_Buffer1+9,y  ;put null terminator at end
    VRAM_Buffer1[Y] = 0x00
    //> ldx $00               ;get offset control bit here
    //> rts                   ;and leave
    return
}

// Decompiled from JumpEngine
fun jumpEngine(A: Int) {
    //> JumpEngine:
    //> asl          ;shift bit from contents of A
    //> tay
    //> pla          ;pull saved return address from stack
    //> sta $04      ;save to indirect
    zp_04 = (A shl 1) and 0xFF
    //> pla
    //> sta $05
    zp_05 = (A shl 1) and 0xFF
    //> iny
    //> lda ($04),y  ;load pointer from indirect
    //> sta $06      ;note that if an RTS is performed in next routine
    zp_06 = TODO
    //> iny          ;it will return to the execution before the sub
    //> lda ($04),y  ;that called this routine
    //> sta $07
    zp_07 = TODO
    //> jmp ($06)    ;jump to the address we loaded
}

// Decompiled from InitializeNameTables
fun initializeNameTables() {
    //> InitializeNameTables:
    //> lda PPU_STATUS            ;reset flip-flop
    //> lda Mirror_PPU_CTRL_REG1  ;load mirror of ppu reg $2000
    //> ora #%00010000            ;set sprites for first 4k and background for second 4k
    //> and #%11110000            ;clear rest of lower nybble, leave higher alone
    //> jsr WritePPUReg1
    writePPUReg1(Mirror_PPU_CTRL_REG1 or 0x10 and 0xF0)
    //> lda #$24                  ;set vram address to start of name table 1
    //> jsr WriteNTAddr
    writeNTAddr(0x24)
    //> lda #$20                  ;and then set it to name table 0
}

// Decompiled from WriteNTAddr
fun writeNTAddr(A: Int) {
    //> WriteNTAddr:  sta PPU_ADDRESS
    PPU_ADDRESS = A
    //> lda #$00
    //> sta PPU_ADDRESS
    PPU_ADDRESS = 0x00
    //> ldx #$04                  ;clear name table with blank tile #24
    //> ldy #$c0
    //> lda #$24
    while (zeroFlag) {
        do {
            //> InitNTLoop:   sta PPU_DATA              ;count out exactly 768 tiles
            PPU_DATA = 0x24
            //> dey
            //> bne InitNTLoop
            //> dex
            //> bne InitNTLoop
        } while (!zeroFlag)
    }
    //> ldy #64                   ;now to clear the attribute table (with zero this time)
    //> txa
    //> sta VRAM_Buffer1_Offset   ;init vram buffer 1 offset
    VRAM_Buffer1_Offset = (0x04 - 1) and 0xFF
    //> sta VRAM_Buffer1          ;init vram buffer 1
    VRAM_Buffer1 = (0x04 - 1) and 0xFF
    do {
        //> InitATLoop:   sta PPU_DATA
        PPU_DATA = (0x04 - 1) and 0xFF
        //> dey
        //> bne InitATLoop
    } while (zeroFlag)
    //> sta HorizontalScroll      ;reset scroll variables
    HorizontalScroll = (0x04 - 1) and 0xFF
    //> sta VerticalScroll
    VerticalScroll = (0x04 - 1) and 0xFF
    //> jmp InitScroll            ;initialize scroll registers to zero
}

// Decompiled from ReadJoypads
fun readJoypads() {
    //> ReadJoypads:
    //> lda #$01               ;reset and clear strobe of joypad ports
    //> sta JOYPAD_PORT
    JOYPAD_PORT = 0x01
    //> lsr
    //> tax                    ;start with joypad 1's port
    //> sta JOYPAD_PORT
    JOYPAD_PORT = 0x01 shr 1
    //> jsr ReadPortBits
    readPortBits(0x01 shr 1, 0x01 shr 1)
    //> inx                    ;increment for joypad 2's port
}

// Decompiled from ReadPortBits
fun readPortBits(A: Int, X: Int) {
    //> ReadPortBits: ldy #$08
    do {
        //> PortLoop:     pha                    ;push previous bit onto stack
        //> lda JOYPAD_PORT,x      ;read current bit on joypad port
        //> sta $00                ;check d1 and d0 of port output
        zp_00 = JOYPAD_PORT[X]
        //> lsr                    ;this is necessary on the old
        //> ora $00                ;famicom systems in japan
        //> lsr
        //> pla                    ;read bits from stack
        //> rol                    ;rotate bit from carry flag
        //> dey
        //> bne PortLoop           ;count down bits left
    } while (zeroFlag)
    //> sta SavedJoypadBits,x  ;save controller status here always
    SavedJoypadBits[X] = JOYPAD_PORT[X] shr 1 or zp_00 shr 1
    //> pha
    //> and #%00110000         ;check for select or start
    //> and JoypadBitMask,x    ;if neither saved state nor current state
    //> beq Save8Bits          ;have any of these two set, branch
    if (!zeroFlag) {
        //> pla
        //> and #%11001111         ;otherwise store without select
        //> sta SavedJoypadBits,x  ;or start bits and leave
        SavedJoypadBits[X] = JOYPAD_PORT[X] shr 1 or zp_00 shr 1 and 0x30 and JoypadBitMask[X] and 0xCF
        //> rts
        return
    } else {
        //> Save8Bits:    pla
        //> sta JoypadBitMask,x    ;save with all bits in another place and leave
        JoypadBitMask[X] = JOYPAD_PORT[X] shr 1 or zp_00 shr 1 and 0x30 and JoypadBitMask[X] and 0xCF
        //> rts
        return
    }
}

// Decompiled from UpdateScreen
fun updateScreen() {
    var A: Int = 0
    var Y: Int = 0
    //> WriteBufferToScreen:
    //> sta PPU_ADDRESS           ;store high byte of vram address
    PPU_ADDRESS = A
    //> iny
    //> lda ($00),y               ;load next byte (second)
    //> sta PPU_ADDRESS           ;store low byte of vram address
    PPU_ADDRESS = TODO
    //> iny
    //> lda ($00),y               ;load next byte (third)
    //> asl                       ;shift to left and save in stack
    //> pha
    //> lda Mirror_PPU_CTRL_REG1  ;load mirror of $2000,
    //> ora #%00000100            ;set ppu to increment by 32 by default
    //> bcs SetupWrites           ;if d7 of third byte was clear, ppu will
    if (!carryFlag) {
        //> and #%11111011            ;only increment by 1
    }
    //> SetupWrites:   jsr WritePPUReg1          ;write to register
    writePPUReg1(Mirror_PPU_CTRL_REG1 or 0x04 and 0xFB)
    //> pla                       ;pull from stack and shift to left again
    //> asl
    //> bcc GetLength             ;if d6 of third byte was clear, do not repeat byte
    if (carryFlag) {
        //> ora #%00000010            ;otherwise set d1 and increment Y
        //> iny
    }
    //> GetLength:     lsr                       ;shift back to the right to get proper length
    //> lsr                       ;note that d1 will now be in carry
    //> tax
    while (zeroFlag) {
        //> iny                       ;otherwise increment Y to load next byte
        do {
            //> OutputToVRAM:  bcs RepeatByte            ;if carry set, repeat loading the same byte
            //> RepeatByte:    lda ($00),y               ;load more data from buffer and write to vram
            //> sta PPU_DATA
            PPU_DATA = TODO
            //> dex                       ;done writing?
            //> bne OutputToVRAM
        } while (!zeroFlag)
    }
    //> sec
    //> tya
    //> adc $00                   ;add end length plus one to the indirect at $00
    //> sta $00                   ;to allow this routine to read another set of updates
    zp_00 = (((((Y + 1) and 0xFF + 1) and 0xFF + 1) and 0xFF + 1) and 0xFF + zp_00 + 1) and 0xFF
    //> lda #$00
    //> adc $01
    //> sta $01
    zp_01 = (0x00 + zp_01 + 1) and 0xFF
    //> lda #$3f                  ;sets vram address to $3f00
    //> sta PPU_ADDRESS
    PPU_ADDRESS = 0x3F
    //> lda #$00
    //> sta PPU_ADDRESS
    PPU_ADDRESS = 0x00
    //> sta PPU_ADDRESS           ;then reinitializes it for some reason
    PPU_ADDRESS = 0x00
    //> sta PPU_ADDRESS
    PPU_ADDRESS = 0x00
    //> UpdateScreen:  ldx PPU_STATUS            ;reset flip-flop
    //> ldy #$00                  ;load first byte from indirect as a pointer
    //> lda ($00),y
    //> bne WriteBufferToScreen   ;if byte is zero we have no further updates to make here
}

// Decompiled from InitScroll
fun initScroll(A: Int) {
    //> InitScroll:    sta PPU_SCROLL_REG        ;store contents of A into scroll registers
    PPU_SCROLL_REG = A
    //> sta PPU_SCROLL_REG        ;and end whatever subroutine led us here
    PPU_SCROLL_REG = A
    //> rts
    return
}

// Decompiled from WritePPUReg1
fun writePPUReg1(A: Int) {
    //> WritePPUReg1:
    //> sta PPU_CTRL_REG1         ;write contents of A to PPU register 1
    PPU_CTRL_REG1 = A
    //> sta Mirror_PPU_CTRL_REG1  ;and its mirror
    Mirror_PPU_CTRL_REG1 = A
    //> rts
    return
}

// Decompiled from PrintStatusBarNumbers
fun printStatusBarNumbers(A: Int) {
    //> PrintStatusBarNumbers:
    //> sta $00            ;store player-specific offset
    zp_00 = A
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
    //> OutputNumbers:
    //> clc                      ;add 1 to low nybble
    //> adc #$01
    //> and #%00001111           ;mask out high nybble
    //> cmp #$06
    //> bcs ExitOutputN
    if (!((A + 0x01 + 0) and 0xFF and 0x0F >= 0x06)) {
        //> pha                      ;save incremented value to stack for now and
        //> asl                      ;shift to left and use as offset
        //> tay
        //> ldx VRAM_Buffer1_Offset  ;get current buffer pointer
        //> lda #$20                 ;put at top of screen by default
        //> cpy #$00                 ;are we writing top score on title screen?
        //> bne SetupNums
        if ((A shl 1) and 0xFF == 0x00) {
            //> lda #$22                 ;if so, put further down on the screen
        }
        //> SetupNums:   sta VRAM_Buffer1,x
        VRAM_Buffer1[VRAM_Buffer1_Offset] = 0x22
        //> lda StatusBarData,y      ;write low vram address and length of thing
        //> sta VRAM_Buffer1+1,x     ;we're printing to the buffer
        VRAM_Buffer1[VRAM_Buffer1_Offset] = StatusBarData[((A + 0x01 + 0) and 0xFF and 0x0F shl 1) and 0xFF]
        //> lda StatusBarData+1,y
        //> sta VRAM_Buffer1+2,x
        VRAM_Buffer1[VRAM_Buffer1_Offset] = StatusBarData[((A + 0x01 + 0) and 0xFF and 0x0F shl 1) and 0xFF]
        //> sta $03                  ;save length byte in counter
        zp_03 = StatusBarData[((A + 0x01 + 0) and 0xFF and 0x0F shl 1) and 0xFF]
        //> stx $02                  ;and buffer pointer elsewhere for now
        zp_02 = VRAM_Buffer1_Offset
        //> pla                      ;pull original incremented value from stack
        //> tax
        //> lda StatusBarOffset,x    ;load offset to value we want to write
        //> sec
        //> sbc StatusBarData+1,y    ;subtract from length byte we read before
        //> tay                      ;use value as offset to display digits
        //> ldx $02
        do {
            //> DigitPLoop:  lda DisplayDigits,y      ;write digits to the buffer
            //> sta VRAM_Buffer1+3,x
            VRAM_Buffer1[zp_02] = DisplayDigits[(StatusBarOffset[StatusBarData[((A + 0x01 + 0) and 0xFF and 0x0F shl 1) and 0xFF]] - StatusBarData[((A + 0x01 + 0) and 0xFF and 0x0F shl 1) and 0xFF] - (1 - 1)) and 0xFF]
            //> inx
            //> iny
            //> dec $03                  ;do this until all the digits are written
            zp_03 = (zp_03 - 1) and 0xFF
            //> bne DigitPLoop
        } while (zeroFlag)
        //> lda #$00                 ;put null terminator at end
        //> sta VRAM_Buffer1+3,x
        VRAM_Buffer1[(zp_02 + 1) and 0xFF] = 0x00
        //> inx                      ;increment buffer pointer by 3
        //> inx
        //> inx
        //> stx VRAM_Buffer1_Offset  ;store it in case we want to use it again
        VRAM_Buffer1_Offset = ((((zp_02 + 1) and 0xFF + 1) and 0xFF + 1) and 0xFF + 1) and 0xFF
    }
    //> ExitOutputN: rts
    return
}

// Decompiled from DigitsMathRoutine
fun digitsMathRoutine(Y: Int) {
    var A: Int = 0
    //> DigitsMathRoutine:
    //> lda OperMode              ;check mode of operation
    //> cmp #TitleScreenModeValue
    //> beq EraseDMods            ;if in title screen mode, branch to lock score
    if (!(OperMode - TitleScreenModeValue == 0)) {
        //> ldx #$05
        while (!negativeFlag) {
            //> cmp #10
            //> bcs CarryOne              ;if digit greater than $09, branch to add
            if (!(A >= 0x0A)) {
                while (negativeFlag) {
                    //> EraseDMods: lda #$00                  ;store zero here
                    //> ldx #$06                  ;start with the last digit
                    do {
                        //> EraseMLoop: sta DigitModifier-1,x     ;initialize the digit amounts to increment
                        DigitModifier[0x06] = 0x00
                        //> dex
                        //> bpl EraseMLoop            ;do this until they're all reset, then leave
                    } while (negativeFlag)
                    //> rts
                    return
                    do {
                        //> StoreNewD:  sta DisplayDigits,y       ;store as new score or game timer digit
                        DisplayDigits[Y] = 0x00
                        //> dey                       ;move onto next digits in score or game timer
                        //> dex                       ;and digit amounts to increment
                        //> bpl AddModLoop            ;loop back if we're not done yet
                        //> BorrowOne:  dec DigitModifier-1,x     ;decrement the previous digit, then put $09 in
                        DigitModifier[((0x06 - 1) and 0xFF - 1) and 0xFF] = (DigitModifier[((0x06 - 1) and 0xFF - 1) and 0xFF] - 1) and 0xFF
                        //> lda #$09                  ;the game timer digit we're currently on to "borrow
                        //> bne StoreNewD             ;the one", then do an unconditional branch back
                    } while (!zeroFlag)
                }
            }
        }
    }
    do {
    } while (negativeFlag)
    do {
    } while (!zeroFlag)
    while (true) {
        //> CarryOne:   sec                       ;subtract ten from our digit to make it a
        //> sbc #10                   ;proper BCD number, then increment the digit
        //> inc DigitModifier-1,x     ;preceding current digit to "carry the one" properly
        DigitModifier[((0x06 - 1) and 0xFF - 1) and 0xFF] = (DigitModifier[((0x06 - 1) and 0xFF - 1) and 0xFF] + 1) and 0xFF
        //> jmp StoreNewD             ;go back to just after we branched here
    }
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
    var Y: Int = 0
    //> TopScoreCheck:
    //> ldy #$05                 ;start with the lowest digit
    //> sec
    do {
        //> GetScoreDiff: lda PlayerScoreDisplay,x ;subtract each player digit from each high score digit
        //> sbc TopScoreDisplay,y    ;from lowest to highest, if any top score digit exceeds
        //> dex                      ;any player digit, borrow will be set until a subsequent
        //> dey                      ;subtraction clears it (player digit is higher than top)
        //> bpl GetScoreDiff
    } while (negativeFlag)
    //> bcc NoTopSc              ;check to see if borrow is still set, if so, no new high score
    if (carryFlag) {
        //> inx                      ;increment X and Y once to the start of the score
        //> iny
        do {
            //> CopyScore:    lda PlayerScoreDisplay,x ;store player's score digits into high score memory area
            //> sta TopScoreDisplay,y
            TopScoreDisplay[((0x05 - 1) and 0xFF + 1) and 0xFF] = PlayerScoreDisplay[((X - 1) and 0xFF + 1) and 0xFF]
            //> inx
            //> iny
            //> cpy #$06                 ;do this until we have stored them all
            //> bcc CopyScore
        } while ((Y + 1) and 0xFF >= 0x06)
    }
    //> NoTopSc:      rts
    return
}

// Decompiled from InitializeMemory
fun initializeMemory(Y: Int): Int {
    //> InitializeMemory:
    //> ldx #$07          ;set initial high byte to $0700-$07ff
    //> lda #$00          ;set initial low byte to start of page (at $00 of page)
    //> sta $06
    zp_06 = 0x00
    while (!negativeFlag) {
        //> InitPageLoop: stx $07
        zp_07 = 0x07
        //> InitByteLoop: cpx #$01          ;check to see if we're on the stack ($0100-$01ff)
        //> bne InitByte      ;if not, go ahead anyway
        //> cpy #$60          ;otherwise, check to see if we're at $0160-$01ff
        //> bcs SkipByte      ;if so, skip write
        //> InitByte:     sta ($06),y       ;otherwise, initialize byte with current low byte in Y
        TODO = 0x00
        //> SkipByte:     dey
        //> cpy #$ff          ;do this until all bytes in page have been erased
        //> bne InitByteLoop
        //> dex               ;go onto the next page
        //> bpl InitPageLoop  ;do this until all pages of memory have been erased
    }
    //> rts
    return A
}

// Decompiled from GetAreaMusic
fun getAreaMusic() {
    var A: Int = 0
    //> GetAreaMusic:
    //> lda OperMode           ;if in title screen mode, leave
    //> beq ExitGetM
    if (!zeroFlag) {
        //> lda AltEntranceControl ;check for specific alternate mode of entry
        //> cmp #$02               ;if found, branch without checking starting position
        //> beq ChkAreaType        ;from area object data header
        if (!(AltEntranceControl - 0x02 == 0)) {
            //> ldy #$05               ;select music for pipe intro scene by default
            //> lda PlayerEntranceCtrl ;check value from level header for certain values
            //> cmp #$06
            //> beq StoreMusic         ;load music for pipe intro scene if header
            if (!(PlayerEntranceCtrl - 0x06 == 0)) {
                //> cmp #$07               ;start position either value $06 or $07
                //> beq StoreMusic
                if (!(A - 0x07 == 0)) {
                    //> ChkAreaType: ldy AreaType           ;load area type as offset for music bit
                    //> lda CloudTypeOverride
                    //> beq StoreMusic         ;check for cloud type override
                    if (!zeroFlag) {
                        //> ldy #$04               ;select music for cloud type level if found
                    }
                }
            }
        }
        if (!zeroFlag) {
        }
        //> StoreMusic:  lda MusicSelectData,y  ;otherwise select appropriate music for level type
        //> sta AreaMusicQueue     ;store in queue and leave
        AreaMusicQueue = MusicSelectData[0x04]
    }
    //> ExitGetM:    rts
    return
}

// Decompiled from TerminateGame
fun terminateGame() {
    //> TerminateGame:
    //> lda #Silence          ;silence music
    //> sta EventMusicQueue
    EventMusicQueue = Silence
    //> jsr TransposePlayers  ;check if other player can keep
    transposePlayers()
    //> bcc ContinueGame      ;going, and do so if possible
    if (carryFlag) {
        //> lda WorldNumber       ;otherwise put world number of current
        //> sta ContinueWorld     ;player into secret continue function variable
        ContinueWorld = WorldNumber
        //> lda #$00
        //> asl                   ;residual ASL instruction
        //> sta OperMode_Task     ;reset all modes to title screen and
        OperMode_Task = (0x00 shl 1) and 0xFF
        //> sta ScreenTimer       ;leave
        ScreenTimer = (0x00 shl 1) and 0xFF
        //> sta OperMode
        OperMode = (0x00 shl 1) and 0xFF
        //> rts
        return
    } else {
        //> ContinueGame:
        //> jsr LoadAreaPointer       ;update level pointer with
        loadAreaPointer((0x00 shl 1) and 0xFF)
        //> lda #$01                  ;actual world and area numbers, then
        //> sta PlayerSize            ;reset player's size, status, and
        PlayerSize = 0x01
        //> inc FetchNewGameTimerFlag ;set game timer flag to reload
        FetchNewGameTimerFlag = (FetchNewGameTimerFlag + 1) and 0xFF
        //> lda #$00                  ;game timer from header
        //> sta TimerControl          ;also set flag for timers to count again
        TimerControl = 0x00
        //> sta PlayerStatus
        PlayerStatus = 0x00
        //> sta GameEngineSubroutine  ;reset task for game core
        GameEngineSubroutine = 0x00
        //> sta OperMode_Task         ;set modes and leave
        OperMode_Task = 0x00
        //> lda #$01                  ;if in game over mode, switch back to
        //> sta OperMode              ;game mode, because game is still on
        OperMode = 0x01
    }
    //> GameIsOn:  rts
    return
}

// Decompiled from TransposePlayers
fun transposePlayers() {
    //> TransposePlayers:
    //> sec                       ;set carry flag by default to end game
    //> lda NumberOfPlayers       ;if only a 1 player game, leave
    //> beq ExTrans
    if (!zeroFlag) {
        //> lda OffScr_NumberofLives  ;does offscreen player have any lives left?
        //> bmi ExTrans               ;branch if not
        if (!negativeFlag) {
            //> lda CurrentPlayer         ;invert bit to update
            //> eor #%00000001            ;which player is on the screen
            //> sta CurrentPlayer
            CurrentPlayer = CurrentPlayer xor 0x01
            //> ldx #$06
            do {
                //> TransLoop: lda OnscreenPlayerInfo,x    ;transpose the information
                //> pha                         ;of the onscreen player
                //> lda OffscreenPlayerInfo,x   ;with that of the offscreen player
                //> sta OnscreenPlayerInfo,x
                OnscreenPlayerInfo[0x06] = OffscreenPlayerInfo[0x06]
                //> pla
                //> sta OffscreenPlayerInfo,x
                OffscreenPlayerInfo[0x06] = OffscreenPlayerInfo[0x06]
                //> dex
                //> bpl TransLoop
            } while (negativeFlag)
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
    zp_06c9 = 0xFF
}

// Decompiled from DoNothing2
fun doNothing2() {
    //> DoNothing2:
    //> rts
    return
}

// Decompiled from AreaParserTaskHandler
fun areaParserTaskHandler() {
    //> AreaParserTaskHandler:
    //> ldy AreaParserTaskNum     ;check number of tasks here
    //> bne DoAPTasks             ;if already set, go ahead
    if (zeroFlag) {
        //> ldy #$08
        //> sty AreaParserTaskNum     ;otherwise, set eight by default
        AreaParserTaskNum = 0x08
    }
    //> DoAPTasks:    dey
    //> tya
    //> jsr AreaParserTasks
    areaParserTasks()
    //> dec AreaParserTaskNum     ;if all tasks not complete do not
    AreaParserTaskNum = (AreaParserTaskNum - 1) and 0xFF
    //> bne SkipATRender          ;render attribute table yet
    if (zeroFlag) {
        //> jsr RenderAttributeTables
        renderAttributeTables()
    }
    //> SkipATRender: rts
    return
}

// Decompiled from AreaParserTasks
fun areaParserTasks() {
    var A: Int = 0
    //> AreaParserTasks:
    //> jsr JumpEngine
    jumpEngine(A)
    //> .dw IncrementColumnPos
    //> .dw RenderAreaGraphics
    //> .dw RenderAreaGraphics
    //> .dw AreaParserCore
    //> .dw IncrementColumnPos
    //> .dw RenderAreaGraphics
    //> .dw RenderAreaGraphics
    //> .dw AreaParserCore
    //> ;-------------------------------------------------------------------------------------
    //> IncrementColumnPos:
    //> inc CurrentColumnPos     ;increment column where we're at
    CurrentColumnPos = (CurrentColumnPos + 1) and 0xFF
    //> lda CurrentColumnPos
    //> and #%00001111           ;mask out higher nybble
    //> bne NoColWrap
    if (zeroFlag) {
        //> sta CurrentColumnPos     ;if no bits left set, wrap back to zero (0-f)
        CurrentColumnPos = CurrentColumnPos and 0x0F
        //> inc CurrentPageLoc       ;and increment page number where we're at
        CurrentPageLoc = (CurrentPageLoc + 1) and 0xFF
    }
    //> NoColWrap: inc BlockBufferColumnPos ;increment column offset where we're at
    BlockBufferColumnPos = (BlockBufferColumnPos + 1) and 0xFF
    //> lda BlockBufferColumnPos
    //> and #%00011111           ;mask out all but 5 LSB (0-1f)
    //> sta BlockBufferColumnPos ;and save
    BlockBufferColumnPos = BlockBufferColumnPos and 0x1F
    //> rts
    return
}

// Decompiled from ProcessAreaData
fun processAreaData() {
    var A: Int = 0
    //> ProcessAreaData:
    //> ldx #$02                 ;start at the end of area object buffer
    while (negativeFlag) {
        //> lda AreaObjectLength,x   ;check area object buffer flag
        //> bpl RdyDecode            ;if buffer not negative, branch, otherwise
        if (negativeFlag) {
            //> iny
            //> lda (AreaData),y         ;get second byte of area object
            //> asl                      ;check for page select bit (d7), branch if not set
            //> bcc Chk1Row13
            if (carryFlag) {
                //> lda AreaObjectPageSel    ;check page select
                //> bne Chk1Row13
                if (zeroFlag) {
                    //> inc AreaObjectPageSel    ;if not already set, set it now
                    AreaObjectPageSel = (AreaObjectPageSel + 1) and 0xFF
                    //> inc AreaObjectPageLoc    ;and increment page location
                    AreaObjectPageLoc = (AreaObjectPageLoc + 1) and 0xFF
                }
            }
            //> Chk1Row13:  dey
            //> lda (AreaData),y         ;reread first byte of level object
            //> and #$0f                 ;mask out high nybble
            //> cmp #$0d                 ;row 13?
            //> bne Chk1Row14
            if (TODO and 0x0F - 0x0D == 0) {
                //> iny                      ;if so, reread second byte of level object
                //> lda (AreaData),y
                //> dey                      ;decrement to get ready to read first byte
                //> and #%01000000           ;check for d6 set (if not, object is page control)
                //> bne CheckRear
                if (zeroFlag) {
                    //> lda AreaObjectPageSel    ;if page select is set, do not reread
                    //> bne CheckRear
                    if (zeroFlag) {
                        //> iny                      ;if d6 not set, reread second byte
                        //> lda (AreaData),y
                        //> and #%00011111           ;mask out all but 5 LSB and store in page control
                        //> sta AreaObjectPageLoc
                        AreaObjectPageLoc = TODO and 0x1F
                        //> inc AreaObjectPageSel    ;increment page select
                        AreaObjectPageSel = (AreaObjectPageSel + 1) and 0xFF
                        //> jmp NextAObj
                        //> Chk1Row14:  cmp #$0e                 ;row 14?
                        //> bne CheckRear
                        if (A - 0x0E == 0) {
                            //> lda BackloadingFlag      ;check flag for saved page number and branch if set
                            //> bne RdyDecode            ;to render the object (otherwise bg might not look right)
                            if (zeroFlag) {
                                //> CheckRear:  lda AreaObjectPageLoc    ;check to see if current page of level object is
                                //> cmp CurrentPageLoc       ;behind current page of renderer
                                //> bcc SetBehind            ;if so branch
                                if (AreaObjectPageLoc >= CurrentPageLoc) {
                                    //> RdyDecode:  jsr DecodeAreaData       ;do sub and do not turn on flag
                                    decodeAreaData(0x02)
                                    //> jmp ChkLength
                                }
                            }
                        }
                    }
                }
            }
            if (A - 0x0E == 0) {
                if (zeroFlag) {
                    if (AreaObjectPageLoc >= CurrentPageLoc) {
                    }
                }
            }
            if (AreaObjectPageLoc >= CurrentPageLoc) {
            }
        }
        //> SetBehind:  inc BehindAreaParserFlag ;turn on flag if object is behind renderer
        BehindAreaParserFlag = (BehindAreaParserFlag + 1) and 0xFF
        //> NextAObj:   jsr IncAreaObjOffset     ;increment buffer offset and move on
        incAreaObjOffset()
        //> ChkLength:  ldx ObjectOffset         ;get buffer offset
        //> lda AreaObjectLength,x   ;check object length for anything stored here
        //> bmi ProcLoopb            ;if not, branch to handle loopback
        if (!negativeFlag) {
            //> dec AreaObjectLength,x   ;otherwise decrement length or get rid of it
            AreaObjectLength[ObjectOffset] = (AreaObjectLength[ObjectOffset] - 1) and 0xFF
        }
        //> ProcLoopb:  dex                      ;decrement buffer offset
        //> bpl ProcADLoop           ;and loopback unless exceeded buffer
    }
    //> lda BehindAreaParserFlag ;check for flag set if objects were behind renderer
    //> bne ProcessAreaData      ;branch if true to load more level data, otherwise
    //> lda BackloadingFlag      ;check for flag set if starting right of page $00
    //> bne ProcessAreaData      ;branch if true to load more level data, otherwise leave
    //> EndAParse:  rts
    return
}

// Decompiled from IncAreaObjOffset
fun incAreaObjOffset() {
    //> IncAreaObjOffset:
    //> inc AreaDataOffset    ;increment offset of level pointer
    AreaDataOffset = (AreaDataOffset + 1) and 0xFF
    //> inc AreaDataOffset
    AreaDataOffset = (AreaDataOffset + 1) and 0xFF
    //> lda #$00              ;reset page select
    //> sta AreaObjectPageSel
    AreaObjectPageSel = 0x00
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
    //> KillEnemies:
    //> sta $00           ;store identifier here
    zp_00 = A
    //> lda #$00
    //> ldx #$04          ;check for identifier in enemy object buffer
    while (negativeFlag) {
        //> sta Enemy_Flag,x  ;if found, deactivate enemy object flag
        Enemy_Flag[0x04] = 0x00
        do {
            //> KillELoop: ldy Enemy_ID,x
            //> cpy $00           ;if not found, branch
            //> bne NoKillE
            //> NoKillE:   dex               ;do this until all slots are checked
            //> bpl KillELoop
        } while (!negativeFlag)
    }
    //> rts
    return
}

// Decompiled from RenderSidewaysPipe
fun renderSidewaysPipe(X: Int, Y: Int) {
    //> RenderSidewaysPipe:
    //> dey                       ;decrement twice to make room for shaft at bottom
    //> dey                       ;and store here for now as vertical length
    //> sty $05
    zp_05 = ((Y - 1) and 0xFF - 1) and 0xFF
    //> ldy AreaObjectLength,x    ;get length left over and store here
    //> sty $06
    zp_06 = AreaObjectLength[X]
    //> ldx $05                   ;get vertical length plus one, use as buffer offset
    //> inx
    //> lda SidePipeShaftData,y   ;check for value $00 based on horizontal offset
    //> cmp #$00
    //> beq DrawSidePart          ;if found, do not draw the vertical pipe shaft
    if (!(SidePipeShaftData[AreaObjectLength[X]] - 0x00 == 0)) {
        //> ldx #$00
        //> ldy $05                   ;init buffer offset and get vertical length
        //> jsr RenderUnderPart       ;and render vertical shaft using tile number in A
        renderUnderPart(SidePipeShaftData[AreaObjectLength[X]], 0x00, zp_05)
        //> clc                       ;clear carry flag to be used by IntroPipe
    }
    //> DrawSidePart: ldy $06                   ;render side pipe part at the bottom
    //> lda SidePipeTopPart,y
    //> sta MetatileBuffer,x      ;note that the pipe parts are stored
    MetatileBuffer[0x00] = SidePipeTopPart[zp_06]
    //> lda SidePipeBottomPart,y  ;backwards horizontally
    //> sta MetatileBuffer+1,x
    MetatileBuffer[0x00] = SidePipeBottomPart[zp_06]
    //> rts
    return
}

// Decompiled from GetPipeHeight
fun getPipeHeight(X: Int) {
    //> GetPipeHeight:
    //> ldy #$01       ;check for length loaded, if not, load
    //> jsr ChkLrgObjFixedLength ;pipe length of 2 (horizontal)
    chkLrgObjFixedLength(X, 0x01)
    //> jsr GetLrgObjAttrib
    getLrgObjAttrib(X)
    //> tya            ;get saved lower nybble as height
    //> and #$07       ;save only the three lower bits as
    //> sta $06        ;vertical length, then load Y with
    zp_06 = 0x01 and 0x07
    //> ldy AreaObjectLength,x    ;length left over
    //> rts
    return
}

// Decompiled from FindEmptyEnemySlot
fun findEmptyEnemySlot(): Int {
    var X: Int = 0
    //> FindEmptyEnemySlot:
    //> ldx #$00          ;start at first enemy slot
    while ((X + 1) and 0xFF == 0x05) {
        do {
            //> EmptyChkLoop: clc               ;clear carry flag by default
            //> lda Enemy_Flag,x  ;check enemy buffer for nonzero
            //> beq ExitEmptyChk  ;if zero, leave
            //> inx
            //> cpx #$05          ;if nonzero, check next value
            //> bne EmptyChkLoop
        } while (!((X + 1) and 0xFF == 0x05))
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
    while (negativeFlag) {
        //> cpy #$17
        //> beq WaitOneRow        ;if middle part (tree ledge), wait until next row
        if (!(Y == 0x17)) {
            //> cpy #$1a
            //> beq WaitOneRow        ;if middle part (mushroom ledge), wait until next row
            if (!(Y == 0x1A)) {
                //> cpy #$c0
                //> beq DrawThisRow       ;if question block w/ coin, overwrite
                if (!(Y == 0xC0)) {
                    //> cpy #$c0
                    //> bcs WaitOneRow        ;if any other metatile with palette 3, wait until next row
                    if (!(Y >= 0xC0)) {
                        //> cpy #$54
                        //> bne DrawThisRow       ;if cracked rock terrain, overwrite
                        if (Y == 0x54) {
                            //> cmp #$50
                            //> beq WaitOneRow        ;if stem top of mushroom, wait until next row
                            if (!(A - 0x50 == 0)) {
                                //> DrawThisRow: sta MetatileBuffer,x  ;render contents of A from routine that called this
                                MetatileBuffer[X] = A
                            }
                        }
                    }
                }
            }
        }
        //> WaitOneRow:  inx
        //> cpx #$0d              ;stop rendering if we're at the bottom of the screen
        //> bcs ExitUPartR
        if (!((X + 1) and 0xFF >= 0x0D)) {
            do {
                //> RenderUnderPart:
                //> sty AreaObjectHeight  ;store vertical length to render
                AreaObjectHeight = Y
                //> ldy MetatileBuffer,x  ;check current spot to see if there's something
                //> beq DrawThisRow       ;we need to keep, if nothing, go ahead
                //> ldy AreaObjectHeight  ;decrement, and stop rendering if there is no more length
                //> dey
                //> bpl RenderUnderPart
            } while (!negativeFlag)
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
    //> ChkLrgObjFixedLength:
    //> lda AreaObjectLength,x  ;check for set length counter
    //> clc                     ;clear carry flag for not just starting
    //> bpl LenSet              ;if counter not set, load it, otherwise leave alone
    if (negativeFlag) {
        //> tya                     ;save length into length counter
        //> sta AreaObjectLength,x
        AreaObjectLength[X] = Y
        //> sec                     ;set carry flag if just starting
    }
    //> LenSet: rts
    return
}

// Decompiled from GetLrgObjAttrib
fun getLrgObjAttrib(X: Int): Int {
    //> GetLrgObjAttrib:
    //> ldy AreaObjOffsetBuffer,x ;get offset saved from area obj decoding routine
    //> lda (AreaData),y          ;get first byte of level object
    //> and #%00001111
    //> sta $07                   ;save row location
    zp_07 = TODO and 0x0F
    //> iny
    //> lda (AreaData),y          ;get next byte, save lower nybble (length or height)
    //> and #%00001111            ;as Y, then leave
    //> tay
    //> rts
    return A
}

// Decompiled from GetAreaObjXPosition
fun getAreaObjXPosition(): Int {
    //> GetAreaObjXPosition:
    //> lda CurrentColumnPos    ;multiply current offset where we're at by 16
    //> asl                     ;to obtain horizontal pixel coordinate
    //> asl
    //> asl
    //> asl
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
    //> GetBlockBufferAddr:
    //> pha                      ;take value of A, save
    //> lsr                      ;move high nybble to low
    //> lsr
    //> lsr
    //> lsr
    //> tay                      ;use nybble as pointer to high byte
    //> lda BlockBufferAddr+2,y  ;of indirect here
    //> sta $07
    zp_07 = BlockBufferAddr[A shr 1 shr 1 shr 1 shr 1]
    //> pla
    //> and #%00001111           ;pull from stack, mask out high nybble
    //> clc
    //> adc BlockBufferAddr,y    ;add to low byte
    //> sta $06                  ;store here and leave
    zp_06 = (BlockBufferAddr[A shr 1 shr 1 shr 1 shr 1] and 0x0F + BlockBufferAddr[A shr 1 shr 1 shr 1 shr 1] + 0) and 0xFF
    //> rts
    return
}

// Decompiled from LoadAreaPointer
fun loadAreaPointer(A: Int) {
    //> LoadAreaPointer:
    //> jsr FindAreaPointer  ;find it and store it here
    findAreaPointer()
    //> sta AreaPointer
    AreaPointer = A
}

// Decompiled from GetAreaType
fun getAreaType(A: Int): Int {
    //> GetAreaType: and #%01100000       ;mask out all but d6 and d5
    //> asl
    //> rol
    //> rol
    //> rol                  ;make %0xx00000 into %000000xx
    //> sta AreaType         ;save 2 MSB as area type
    AreaType = (A and 0x60 shl 1) and 0xFF
    //> rts
    return A
}

// Decompiled from FindAreaPointer
fun findAreaPointer(): Int {
    //> FindAreaPointer:
    //> ldy WorldNumber        ;load offset from world variable
    //> lda WorldAddrOffsets,y
    //> clc                    ;add area number used to find data
    //> adc AreaNumber
    //> tay
    //> lda AreaAddrOffsets,y  ;from there we have our area pointer
    //> rts
    return A
}

// Decompiled from GetAreaDataAddrs
fun getAreaDataAddrs() {
    var A: Int = 0
    //> GetAreaDataAddrs:
    //> lda AreaPointer          ;use 2 MSB for Y
    //> jsr GetAreaType
    getAreaType(AreaPointer)
    //> tay
    //> lda AreaPointer          ;mask out all but 5 LSB
    //> and #%00011111
    //> sta AreaAddrsLOffset     ;save as low offset
    AreaAddrsLOffset = AreaPointer and 0x1F
    //> lda EnemyAddrHOffsets,y  ;load base value with 2 altered MSB,
    //> clc                      ;then add base value to 5 LSB, result
    //> adc AreaAddrsLOffset     ;becomes offset for level data
    //> tay
    //> lda EnemyDataAddrLow,y   ;use offset to load pointer
    //> sta EnemyDataLow
    EnemyDataLow = EnemyDataAddrLow[(EnemyAddrHOffsets[AreaPointer] + AreaAddrsLOffset + 0) and 0xFF]
    //> lda EnemyDataAddrHigh,y
    //> sta EnemyDataHigh
    EnemyDataHigh = EnemyDataAddrHigh[(EnemyAddrHOffsets[AreaPointer] + AreaAddrsLOffset + 0) and 0xFF]
    //> ldy AreaType             ;use area type as offset
    //> lda AreaDataHOffsets,y   ;do the same thing but with different base value
    //> clc
    //> adc AreaAddrsLOffset
    //> tay
    //> lda AreaDataAddrLow,y    ;use this offset to load another pointer
    //> sta AreaDataLow
    AreaDataLow = AreaDataAddrLow[(AreaDataHOffsets[AreaType] + AreaAddrsLOffset + 0) and 0xFF]
    //> lda AreaDataAddrHigh,y
    //> sta AreaDataHigh
    AreaDataHigh = AreaDataAddrHigh[(AreaDataHOffsets[AreaType] + AreaAddrsLOffset + 0) and 0xFF]
    //> ldy #$00                 ;load first byte of header
    //> lda (AreaData),y
    //> pha                      ;save it to the stack for now
    //> and #%00000111           ;save 3 LSB for foreground scenery or bg color control
    //> cmp #$04
    //> bcc StoreFore
    if (TODO and 0x07 >= 0x04) {
        //> sta BackgroundColorCtrl  ;if 4 or greater, save value here as bg color control
        BackgroundColorCtrl = TODO and 0x07
        //> lda #$00
    }
    //> StoreFore:  sta ForegroundScenery    ;if less, save value here as foreground scenery
    ForegroundScenery = 0x00
    //> pla                      ;pull byte from stack and push it back
    //> pha
    //> and #%00111000           ;save player entrance control bits
    //> lsr                      ;shift bits over to LSBs
    //> lsr
    //> lsr
    //> sta PlayerEntranceCtrl       ;save value here as player entrance control
    PlayerEntranceCtrl = 0x00 and 0x38 shr 1 shr 1 shr 1
    //> pla                      ;pull byte again but do not push it back
    //> and #%11000000           ;save 2 MSB for game timer setting
    //> clc
    //> rol                      ;rotate bits over to LSBs
    //> rol
    //> rol
    //> sta GameTimerSetting     ;save value here as game timer setting
    GameTimerSetting = 0x00 and 0x38 shr 1 shr 1 shr 1 and 0xC0
    //> iny
    //> lda (AreaData),y         ;load second byte of header
    //> pha                      ;save to stack
    //> and #%00001111           ;mask out all but lower nybble
    //> sta TerrainControl
    TerrainControl = TODO and 0x0F
    //> pla                      ;pull and push byte to copy it to A
    //> pha
    //> and #%00110000           ;save 2 MSB for background scenery type
    //> lsr
    //> lsr                      ;shift bits to LSBs
    //> lsr
    //> lsr
    //> sta BackgroundScenery    ;save as background scenery
    BackgroundScenery = TODO and 0x0F and 0x30 shr 1 shr 1 shr 1 shr 1
    //> pla
    //> and #%11000000
    //> clc
    //> rol                      ;rotate bits over to LSBs
    //> rol
    //> rol
    //> cmp #%00000011           ;if set to 3, store here
    //> bne StoreStyle           ;and nullify other value
    if (A shr 1 shr 1 shr 1 and 0xC0 - 0x03 == 0) {
        //> sta CloudTypeOverride    ;otherwise store value in other place
        CloudTypeOverride = TODO and 0x0F and 0x30 shr 1 shr 1 shr 1 shr 1 and 0xC0
        //> lda #$00
    }
    //> StoreStyle: sta AreaStyle
    AreaStyle = 0x00
    //> lda AreaDataLow          ;increment area data address by 2 bytes
    //> clc
    //> adc #$02
    //> sta AreaDataLow
    AreaDataLow = (AreaDataLow + 0x02 + 0) and 0xFF
    //> lda AreaDataHigh
    //> adc #$00
    //> sta AreaDataHigh
    AreaDataHigh = (AreaDataHigh + 0x00 + 0) and 0xFF
    //> rts
    return
}

// Decompiled from GameCoreRoutine
fun gameCoreRoutine() {
    var A: Int = 0
    var X: Int = 0
    //> GameCoreRoutine:
    //> ldx CurrentPlayer          ;get which player is on the screen
    //> lda SavedJoypadBits,x      ;use appropriate player's controller bits
    //> sta SavedJoypadBits        ;as the master controller bits
    SavedJoypadBits = SavedJoypadBits[CurrentPlayer]
    //> jsr GameRoutines           ;execute one of many possible subs
    gameRoutines()
    //> lda OperMode_Task          ;check major task of operating mode
    //> cmp #$03                   ;if we are supposed to be here,
    //> bcs GameEngine             ;branch to the game engine itself
    if (!(OperMode_Task >= 0x03)) {
        //> rts
        return
    } else {
        //> GameEngine:
        //> jsr ProcFireball_Bubble    ;process fireballs and air bubbles
        procfireballBubble()
        //> ldx #$00
    }
    do {
        //> ProcELoop:    stx ObjectOffset           ;put incremented offset in X as enemy object offset
        ObjectOffset = 0x00
        //> jsr EnemiesAndLoopsCore    ;process enemy objects
        enemiesAndLoopsCore(0x00)
        //> jsr FloateyNumbersRoutine  ;process floatey numbers
        floateyNumbersRoutine(0x00)
        //> inx
        //> cpx #$06                   ;do these two subroutines until the whole buffer is done
        //> bne ProcELoop
    } while ((X + 1) and 0xFF == 0x06)
    //> jsr GetPlayerOffscreenBits ;get offscreen bits for player object
    getPlayerOffscreenBits()
    //> jsr RelativePlayerPosition ;get relative coordinates for player object
    relativePlayerPosition()
    //> jsr PlayerGfxHandler       ;draw the player
    playerGfxHandler()
    //> jsr BlockObjMT_Updater     ;replace block objects with metatiles if necessary
    blockobjmtUpdater()
    //> ldx #$01
    //> stx ObjectOffset           ;set offset for second
    ObjectOffset = 0x01
    //> jsr BlockObjectsCore       ;process second block object
    blockObjectsCore(0x01)
    //> dex
    //> stx ObjectOffset           ;set offset for first
    ObjectOffset = (0x01 - 1) and 0xFF
    //> jsr BlockObjectsCore       ;process first block object
    blockObjectsCore((0x01 - 1) and 0xFF)
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
    //> cmp #$02                   ;if player is below the screen, don't bother with the music
    //> bpl NoChgMus
    if (Player_Y_HighPos - 0x02 < 0) {
        //> lda StarInvincibleTimer    ;if star mario invincibility timer at zero,
        //> beq ClrPlrPal              ;skip this part
        if (!zeroFlag) {
            //> cmp #$04
            //> bne NoChgMus               ;if not yet at a certain point, continue
            if (A - 0x04 == 0) {
                //> lda IntervalTimerControl   ;if interval timer not yet expired,
                //> bne NoChgMus               ;branch ahead, don't bother with the music
                if (zeroFlag) {
                    //> jsr GetAreaMusic           ;to re-attain appropriate level music
                    getAreaMusic()
                }
            }
            //> NoChgMus:     ldy StarInvincibleTimer    ;get invincibility timer
            //> lda FrameCounter           ;get frame counter
            //> cpy #$08                   ;if timer still above certain point,
            //> bcs CycleTwo               ;branch to cycle player's palette quickly
            if (!(StarInvincibleTimer >= 0x08)) {
                //> lsr                        ;otherwise, divide by 8 to cycle every eighth frame
                //> lsr
            }
            //> CycleTwo:     lsr                        ;if branched here, divide by 2 to cycle every other frame
            //> jsr CyclePlayerPalette     ;do sub to cycle the palette (note: shares fire flower code)
            cyclePlayerPalette(FrameCounter shr 1 shr 1 shr 1)
            //> jmp SaveAB                 ;then skip this sub to finish up the game engine
        }
    }
    if (!(StarInvincibleTimer >= 0x08)) {
    }
    //> ClrPlrPal:    jsr ResetPalStar           ;do sub to clear player's palette bits in attributes
    resetPalStar()
    //> SaveAB:       lda A_B_Buttons            ;save current A and B button
    //> sta PreviousA_B_Buttons    ;into temp variable to be used on next frame
    PreviousA_B_Buttons = A_B_Buttons
    //> lda #$00
    //> sta Left_Right_Buttons     ;nullify left and right buttons temp variable
    Left_Right_Buttons = 0x00
}

// Decompiled from UpdScrollVar
fun updScrollVar() {
    //> UpdScrollVar: lda VRAM_Buffer_AddrCtrl
    //> cmp #$06                   ;if vram address controller set to 6 (one of two $0341s)
    //> beq ExitEng                ;then branch to leave
    if (!(VRAM_Buffer_AddrCtrl - 0x06 == 0)) {
        //> lda AreaParserTaskNum      ;otherwise check number of tasks
        //> bne RunParser
        if (zeroFlag) {
            //> lda ScrollThirtyTwo        ;get horizontal scroll in 0-31 or $00-$20 range
            //> cmp #$20                   ;check to see if exceeded $21
            //> bmi ExitEng                ;branch to leave if not
            if (!(ScrollThirtyTwo - 0x20 < 0)) {
                //> lda ScrollThirtyTwo
                //> sbc #$20                   ;otherwise subtract $20 to set appropriately
                //> sta ScrollThirtyTwo        ;and store
                ScrollThirtyTwo = (ScrollThirtyTwo - 0x20 - (1 - ScrollThirtyTwo >= 0x20)) and 0xFF
                //> lda #$00                   ;reset vram buffer offset used in conjunction with
                //> sta VRAM_Buffer2_Offset    ;level graphics buffer at $0341-$035f
                VRAM_Buffer2_Offset = 0x00
                //> RunParser:    jsr AreaParserTaskHandler  ;update the name table with more level graphics
                areaParserTaskHandler()
            }
        }
    }
    //> ExitEng:      rts                        ;and after all that, we're finally done!
    return
}

// Decompiled from ScrollHandler
fun scrollHandler() {
    var Y: Int = 0
    //> ScrollHandler:
    //> lda Player_X_Scroll       ;load value saved here
    //> clc
    //> adc Platform_X_Scroll     ;add value used by left/right platforms
    //> sta Player_X_Scroll       ;save as new value here to impose force on scroll
    Player_X_Scroll = (Player_X_Scroll + Platform_X_Scroll + 0) and 0xFF
    //> lda ScrollLock            ;check scroll lock flag
    //> bne InitScrlAmt           ;skip a bunch of code here if set
    if (zeroFlag) {
        //> lda Player_Pos_ForScroll
        //> cmp #$50                  ;check player's horizontal screen position
        //> bcc InitScrlAmt           ;if less than 80 pixels to the right, branch
        if (Player_Pos_ForScroll >= 0x50) {
            //> lda SideCollisionTimer    ;if timer related to player's side collision
            //> bne InitScrlAmt           ;not expired, branch
            if (zeroFlag) {
                //> ldy Player_X_Scroll       ;get value and decrement by one
                //> dey                       ;if value originally set to zero or otherwise
                //> bmi InitScrlAmt           ;negative for left movement, branch
                if (!negativeFlag) {
                    //> iny
                    //> cpy #$02                  ;if value $01, branch and do not decrement
                    //> bcc ChkNearMid
                    if ((Y + 1) and 0xFF >= 0x02) {
                        //> dey                       ;otherwise decrement by one
                    }
                    //> ChkNearMid: lda Player_Pos_ForScroll
                    //> cmp #$70                  ;check player's horizontal screen position
                    //> bcc ScrollScreen          ;if less than 112 pixels to the right, branch
                    //> ldy Player_X_Scroll       ;otherwise get original value undecremented
                }
            }
        }
    }
    //> InitScrlAmt:  lda #$00
    //> sta ScrollAmount          ;initialize value here
    ScrollAmount = 0x00
    //> ChkPOffscr:   ldx #$00                  ;set X for player offset
    //> jsr GetXOffscreenBits     ;get horizontal offscreen bits for player
    getXOffscreenBits(0x00)
    //> sta $00                   ;save them here
    zp_00 = 0x00
    //> ldy #$00                  ;load default offset (left side)
    //> asl                       ;if d7 of offscreen bits are set,
    //> bcs KeepOnscr             ;branch with default offset
    if (!carryFlag) {
        //> iny                         ;otherwise use different offset (right side)
        //> lda $00
        //> and #%00100000              ;check offscreen bits for d5 set
        //> beq InitPlatScrl            ;if not set, branch ahead of this part
        if (!zeroFlag) {
            //> KeepOnscr:    lda ScreenEdge_X_Pos,y      ;get left or right side coordinate based on offset
            //> sec
            //> sbc X_SubtracterData,y      ;subtract amount based on offset
            //> sta Player_X_Position       ;store as player position to prevent movement further
            Player_X_Position = (ScreenEdge_X_Pos[(0x00 + 1) and 0xFF] - X_SubtracterData[(0x00 + 1) and 0xFF] - (1 - 1)) and 0xFF
            //> lda ScreenEdge_PageLoc,y    ;get left or right page location based on offset
            //> sbc #$00                    ;subtract borrow
            //> sta Player_PageLoc          ;save as player's page location
            Player_PageLoc = (ScreenEdge_PageLoc[(0x00 + 1) and 0xFF] - 0x00 - (1 - 1)) and 0xFF
            //> lda Left_Right_Buttons      ;check saved controller bits
            //> cmp OffscrJoypadBitsData,y  ;against bits based on offset
            //> beq InitPlatScrl            ;if not equal, branch
            if (!(Left_Right_Buttons - OffscrJoypadBitsData[Y] == 0)) {
                //> lda #$00
                //> sta Player_X_Speed          ;otherwise nullify horizontal speed of player
                Player_X_Speed = 0x00
            }
        }
    }
    if (!(Left_Right_Buttons - OffscrJoypadBitsData[Y] == 0)) {
    }
    //> InitPlatScrl: lda #$00                    ;nullify platform force imposed on scroll
    //> sta Platform_X_Scroll
    Platform_X_Scroll = 0x00
    //> rts
    return
}

// Decompiled from ScrollScreen
fun scrollScreen(Y: Int) {
    //> ScrollScreen:
    //> tya
    //> sta ScrollAmount          ;save value here
    ScrollAmount = Y
    //> clc
    //> adc ScrollThirtyTwo       ;add to value already set here
    //> sta ScrollThirtyTwo       ;save as new value here
    ScrollThirtyTwo = (Y + ScrollThirtyTwo + 0) and 0xFF
    //> tya
    //> clc
    //> adc ScreenLeft_X_Pos      ;add to left side coordinate
    //> sta ScreenLeft_X_Pos      ;save as new left side coordinate
    ScreenLeft_X_Pos = (Y + ScreenLeft_X_Pos + 0) and 0xFF
    //> sta HorizontalScroll      ;save here also
    HorizontalScroll = (Y + ScreenLeft_X_Pos + 0) and 0xFF
    //> lda ScreenLeft_PageLoc
    //> adc #$00                  ;add carry to page location for left
    //> sta ScreenLeft_PageLoc    ;side of the screen
    ScreenLeft_PageLoc = (ScreenLeft_PageLoc + 0x00 + 0) and 0xFF
    //> and #$01                  ;get LSB of page location
    //> sta $00                   ;save as temp variable for PPU register 1 mirror
    zp_00 = (ScreenLeft_PageLoc + 0x00 + 0) and 0xFF and 0x01
    //> lda Mirror_PPU_CTRL_REG1  ;get PPU register 1 mirror
    //> and #%11111110            ;save all bits except d0
    //> ora $00                   ;get saved bit here and save in PPU register 1
    //> sta Mirror_PPU_CTRL_REG1  ;mirror to be used to set name table later
    Mirror_PPU_CTRL_REG1 = Mirror_PPU_CTRL_REG1 and 0xFE or zp_00
    //> jsr GetScreenPosition     ;figure out where the right side is
    getScreenPosition()
    //> lda #$08
    //> sta ScrollIntervalTimer   ;set scroll timer (residual, not used elsewhere)
    ScrollIntervalTimer = 0x08
    //> jmp ChkPOffscr            ;skip this part
    //> ChkPOffscr:   ldx #$00                  ;set X for player offset
    //> jsr GetXOffscreenBits     ;get horizontal offscreen bits for player
    getXOffscreenBits(0x00)
    //> sta $00                   ;save them here
    zp_00 = 0x08
    //> ldy #$00                  ;load default offset (left side)
    //> asl                       ;if d7 of offscreen bits are set,
    //> bcs KeepOnscr             ;branch with default offset
    if (!carryFlag) {
        //> iny                         ;otherwise use different offset (right side)
        //> lda $00
        //> and #%00100000              ;check offscreen bits for d5 set
        //> beq InitPlatScrl            ;if not set, branch ahead of this part
        if (!zeroFlag) {
            //> KeepOnscr:    lda ScreenEdge_X_Pos,y      ;get left or right side coordinate based on offset
            //> sec
            //> sbc X_SubtracterData,y      ;subtract amount based on offset
            //> sta Player_X_Position       ;store as player position to prevent movement further
            Player_X_Position = (ScreenEdge_X_Pos[(0x00 + 1) and 0xFF] - X_SubtracterData[(0x00 + 1) and 0xFF] - (1 - 1)) and 0xFF
            //> lda ScreenEdge_PageLoc,y    ;get left or right page location based on offset
            //> sbc #$00                    ;subtract borrow
            //> sta Player_PageLoc          ;save as player's page location
            Player_PageLoc = (ScreenEdge_PageLoc[(0x00 + 1) and 0xFF] - 0x00 - (1 - 1)) and 0xFF
            //> lda Left_Right_Buttons      ;check saved controller bits
            //> cmp OffscrJoypadBitsData,y  ;against bits based on offset
            //> beq InitPlatScrl            ;if not equal, branch
            if (!(Left_Right_Buttons - OffscrJoypadBitsData[Y] == 0)) {
                //> lda #$00
                //> sta Player_X_Speed          ;otherwise nullify horizontal speed of player
                Player_X_Speed = 0x00
            }
        }
    }
    if (!(Left_Right_Buttons - OffscrJoypadBitsData[Y] == 0)) {
    }
    //> InitPlatScrl: lda #$00                    ;nullify platform force imposed on scroll
    //> sta Platform_X_Scroll
    Platform_X_Scroll = 0x00
    //> rts
    return
}

// Decompiled from GetScreenPosition
fun getScreenPosition(): Int {
    //> GetScreenPosition:
    //> lda ScreenLeft_X_Pos    ;get coordinate of screen's left boundary
    //> clc
    //> adc #$ff                ;add 255 pixels
    //> sta ScreenRight_X_Pos   ;store as coordinate of screen's right boundary
    ScreenRight_X_Pos = (ScreenLeft_X_Pos + 0xFF + 0) and 0xFF
    //> lda ScreenLeft_PageLoc  ;get page number where left boundary is
    //> adc #$00                ;add carry from before
    //> sta ScreenRight_PageLoc ;store as page number where right boundary is
    ScreenRight_PageLoc = (ScreenLeft_PageLoc + 0x00 + 0) and 0xFF
    //> rts
    return A
}

// Decompiled from GameRoutines
fun gameRoutines() {
    var A: Int = 0
    //> GameRoutines:
    //> lda GameEngineSubroutine  ;run routine based on number (a few of these routines are
    //> jsr JumpEngine            ;merely placeholders as conditions for other routines)
    jumpEngine(GameEngineSubroutine)
    //> .dw Entrance_GameTimerSetup
    //> .dw Vine_AutoClimb
    //> .dw SideExitPipeEntry
    //> .dw VerticalPipeEntry
    //> .dw FlagpoleSlide
    //> .dw PlayerEndLevel
    //> .dw PlayerLoseLife
    //> .dw PlayerEntrance
    //> .dw PlayerCtrlRoutine
    //> .dw PlayerChangeSize
    //> .dw PlayerInjuryBlink
    //> .dw PlayerDeath
    //> .dw PlayerFireFlower
    //> ;-------------------------------------------------------------------------------------
    //> PlayerEntrance:
    //> lda AltEntranceControl    ;check for mode of alternate entry
    //> cmp #$02
    //> beq EntrMode2             ;if found, branch to enter from pipe or with vine
    if (!(AltEntranceControl - 0x02 == 0)) {
        //> lda #$00
        //> ldy Player_Y_Position     ;if vertical position above a certain
        //> cpy #$30                  ;point, nullify controller bits and continue
        //> bcc AutoControlPlayer     ;with player movement code, do not return
        //> lda PlayerEntranceCtrl    ;check player entry bits from header
        //> cmp #$06
        //> beq ChkBehPipe            ;if set to 6 or 7, execute pipe intro code
        if (!(PlayerEntranceCtrl - 0x06 == 0)) {
            //> cmp #$07                  ;otherwise branch to normal entry
            //> bne PlayerRdy
            if (A - 0x07 == 0) {
                //> ChkBehPipe: lda Player_SprAttrib      ;check for sprite attributes
                //> bne IntroEntr             ;branch if found
                if (zeroFlag) {
                    //> lda #$01
                    //> jmp AutoControlPlayer     ;force player to walk to the right
                }
                //> IntroEntr:  jsr EnterSidePipe         ;execute sub to move player to the right
                enterSidePipe()
                //> dec ChangeAreaTimer       ;decrement timer for change of area
                ChangeAreaTimer = (ChangeAreaTimer - 1) and 0xFF
                //> bne ExitEntr              ;branch to exit if not yet expired
                if (zeroFlag) {
                    //> inc DisableIntermediate   ;set flag to skip world and lives display
                    DisableIntermediate = (DisableIntermediate + 1) and 0xFF
                    //> jmp NextArea              ;jump to increment to next area and set modes
                    //> EntrMode2:  lda JoypadOverride        ;if controller override bits set here,
                    //> bne VineEntr              ;branch to enter with vine
                    if (zeroFlag) {
                        //> lda #$ff                  ;otherwise, set value here then execute sub
                        //> jsr MovePlayerYAxis       ;to move player upwards (note $ff = -1)
                        movePlayerYAxis(0xFF)
                        //> lda Player_Y_Position     ;check to see if player is at a specific coordinate
                        //> cmp #$91                  ;if player risen to a certain point (this requires pipes
                        //> bcc PlayerRdy             ;to be at specific height to look/function right) branch
                        if (Player_Y_Position >= 0x91) {
                            //> rts                       ;to the last part, otherwise leave
                            return
                            //> VineEntr:   lda VineHeight
                            //> cmp #$60                  ;check vine height
                            //> bne ExitEntr              ;if vine not yet reached maximum height, branch to leave
                            if (VineHeight - 0x60 == 0) {
                                //> lda Player_Y_Position     ;get player's vertical coordinate
                                //> cmp #$99                  ;check player's vertical coordinate against preset value
                                //> ldy #$00                  ;load default values to be written to
                                //> lda #$01                  ;this value moves player to the right off the vine
                                //> bcc OffVine               ;if vertical coordinate < preset value, use defaults
                                if (Player_Y_Position >= 0x99) {
                                    //> lda #$03
                                    //> sta Player_State          ;otherwise set player state to climbing
                                    Player_State = 0x03
                                    //> iny                       ;increment value in Y
                                    //> lda #$08                  ;set block in block buffer to cover hole, then
                                    //> sta Block_Buffer_1+$b4    ;use same value to force player to climb
                                }
                                //> OffVine:    sty DisableCollisionDet   ;set collision detection disable flag
                                DisableCollisionDet = (0x00 + 1) and 0xFF
                                //> jsr AutoControlPlayer     ;use contents of A to move player up or right, execute sub
                                autoControlPlayer(0x08)
                                //> lda Player_X_Position
                                //> cmp #$48                  ;check player's horizontal position
                                //> bcc ExitEntr              ;if not far enough to the right, branch to leave
                                if (Player_X_Position >= 0x48) {
                                    //> PlayerRdy:  lda #$08                  ;set routine to be executed by game engine next frame
                                    //> sta GameEngineSubroutine
                                    GameEngineSubroutine = 0x08
                                    //> lda #$01                  ;set to face player to the right
                                    //> sta PlayerFacingDir
                                    PlayerFacingDir = 0x01
                                    //> lsr                       ;init A
                                    //> sta AltEntranceControl    ;init mode of entry
                                    AltEntranceControl = 0x01 shr 1
                                    //> sta DisableCollisionDet   ;init collision detection disable flag
                                    DisableCollisionDet = 0x01 shr 1
                                    //> sta JoypadOverride        ;nullify controller override bits
                                    JoypadOverride = 0x01 shr 1
                                }
                            }
                        }
                    }
                    if (VineHeight - 0x60 == 0) {
                        if (Player_Y_Position >= 0x99) {
                        }
                        if (Player_X_Position >= 0x48) {
                        }
                    }
                }
            }
        }
        if (zeroFlag) {
        }
        if (zeroFlag) {
            if (zeroFlag) {
                if (Player_Y_Position >= 0x91) {
                    if (VineHeight - 0x60 == 0) {
                        if (Player_Y_Position >= 0x99) {
                        }
                        if (Player_X_Position >= 0x48) {
                        }
                    }
                }
            }
            if (VineHeight - 0x60 == 0) {
                if (Player_Y_Position >= 0x99) {
                }
                if (Player_X_Position >= 0x48) {
                }
            }
        }
    } else {
        if (zeroFlag) {
            if (Player_Y_Position >= 0x91) {
                if (VineHeight - 0x60 == 0) {
                    if (Player_Y_Position >= 0x99) {
                    }
                    if (Player_X_Position >= 0x48) {
                    }
                }
            }
        }
        if (VineHeight - 0x60 == 0) {
            if (Player_Y_Position >= 0x99) {
            }
            if (Player_X_Position >= 0x48) {
            }
        }
        //> ExitEntr:   rts                       ;leave!
        return
    }
    //> NextArea: inc AreaNumber            ;increment area number used for address loader
    AreaNumber = (AreaNumber + 1) and 0xFF
    //> jsr LoadAreaPointer       ;get new level pointer
    loadAreaPointer(0x01 shr 1)
    //> inc FetchNewGameTimerFlag ;set flag to load new game timer
    FetchNewGameTimerFlag = (FetchNewGameTimerFlag + 1) and 0xFF
    //> jsr ChgAreaMode           ;do sub to set secondary mode, disable screen and sprite 0
    chgAreaMode()
    //> sta HalfwayPage           ;reset halfway page to 0 (beginning)
    HalfwayPage = 0x01 shr 1
    //> lda #Silence
    //> sta EventMusicQueue       ;silence music and leave
    EventMusicQueue = Silence
    //> ExitNA:   rts
    return
}

// Decompiled from AutoControlPlayer
fun autoControlPlayer(A: Int) {
    //> AutoControlPlayer:
    //> sta SavedJoypadBits         ;override controller bits with contents of A if executing here
    SavedJoypadBits = A
    //> PlayerCtrlRoutine:
    //> lda GameEngineSubroutine    ;check task here
    //> cmp #$0b                    ;if certain value is set, branch to skip controller bit loading
    //> beq SizeChk
    if (!(GameEngineSubroutine - 0x0B == 0)) {
        //> lda AreaType                ;are we in a water type area?
        //> bne SaveJoyp                ;if not, branch
        if (zeroFlag) {
            //> ldy Player_Y_HighPos
            //> dey                         ;if not in vertical area between
            //> bne DisJoyp                 ;status bar and bottom, branch
            if (zeroFlag) {
                //> lda Player_Y_Position
                //> cmp #$d0                    ;if nearing the bottom of the screen or
                //> bcc SaveJoyp                ;not in the vertical area between status bar or bottom,
                if (Player_Y_Position >= 0xD0) {
                    //> DisJoyp:    lda #$00                    ;disable controller bits
                    //> sta SavedJoypadBits
                    SavedJoypadBits = 0x00
                }
            }
        }
        //> SaveJoyp:   lda SavedJoypadBits         ;otherwise store A and B buttons in $0a
        //> and #%11000000
        //> sta A_B_Buttons
        A_B_Buttons = SavedJoypadBits and 0xC0
        //> lda SavedJoypadBits         ;store left and right buttons in $0c
        //> and #%00000011
        //> sta Left_Right_Buttons
        Left_Right_Buttons = SavedJoypadBits and 0x03
        //> lda SavedJoypadBits         ;store up and down buttons in $0b
        //> and #%00001100
        //> sta Up_Down_Buttons
        Up_Down_Buttons = SavedJoypadBits and 0x0C
        //> and #%00000100              ;check for pressing down
        //> beq SizeChk                 ;if not, branch
        if (!zeroFlag) {
            //> lda Player_State            ;check player's state
            //> bne SizeChk                 ;if not on the ground, branch
            if (zeroFlag) {
                //> ldy Left_Right_Buttons      ;check left and right
                //> beq SizeChk                 ;if neither pressed, branch
                if (!zeroFlag) {
                    //> lda #$00
                    //> sta Left_Right_Buttons      ;if pressing down while on the ground,
                    Left_Right_Buttons = 0x00
                    //> sta Up_Down_Buttons         ;nullify directional bits
                    Up_Down_Buttons = 0x00
                }
            }
        }
    }
    //> SizeChk:    jsr PlayerMovementSubs      ;run movement subroutines
    playerMovementSubs()
    //> ldy #$01                    ;is player small?
    //> lda PlayerSize
    //> bne ChkMoveDir
    if (zeroFlag) {
        //> ldy #$00                    ;check for if crouching
        //> lda CrouchingFlag
        //> beq ChkMoveDir              ;if not, branch ahead
        if (!zeroFlag) {
            //> ldy #$02                    ;if big and crouching, load y with 2
        }
    }
    //> ChkMoveDir: sty Player_BoundBoxCtrl     ;set contents of Y as player's bounding box size control
    Player_BoundBoxCtrl = 0x02
    //> lda #$01                    ;set moving direction to right by default
    //> ldy Player_X_Speed          ;check player's horizontal speed
    //> beq PlayerSubs              ;if not moving at all horizontally, skip this part
    if (!zeroFlag) {
        //> bpl SetMoveDir              ;if moving to the right, use default moving direction
        if (negativeFlag) {
            //> asl                         ;otherwise change to move to the left
        }
        //> SetMoveDir: sta Player_MovingDir        ;set moving direction
        Player_MovingDir = (0x01 shl 1) and 0xFF
    }
    //> PlayerSubs: jsr ScrollHandler           ;move the screen if necessary
    scrollHandler()
    //> jsr GetPlayerOffscreenBits  ;get player's offscreen bits
    getPlayerOffscreenBits()
    //> jsr RelativePlayerPosition  ;get coordinates relative to the screen
    relativePlayerPosition()
    //> ldx #$00                    ;set offset for player object
    //> jsr BoundingBoxCore         ;get player's bounding box coordinates
    boundingBoxCore(0x00, Player_X_Speed)
    //> jsr PlayerBGCollision       ;do collision detection and process
    playerBGCollision()
    //> lda Player_Y_Position
    //> cmp #$40                    ;check to see if player is higher than 64th pixel
    //> bcc PlayerHole              ;if so, branch ahead
    if (Player_Y_Position >= 0x40) {
        //> lda GameEngineSubroutine
        //> cmp #$05                    ;if running end-of-level routine, branch ahead
        //> beq PlayerHole
        if (!(GameEngineSubroutine - 0x05 == 0)) {
            //> cmp #$07                    ;if running player entrance routine, branch ahead
            //> beq PlayerHole
            if (!(A - 0x07 == 0)) {
                //> cmp #$04                    ;if running routines $00-$03, branch ahead
                //> bcc PlayerHole
                if (A >= 0x04) {
                    //> lda Player_SprAttrib
                    //> and #%11011111              ;otherwise nullify player's
                    //> sta Player_SprAttrib        ;background priority flag
                    Player_SprAttrib = Player_SprAttrib and 0xDF
                }
            }
        }
    }
    //> PlayerHole: lda Player_Y_HighPos        ;check player's vertical high byte
    //> cmp #$02                    ;for below the screen
    //> bmi ExitCtrl                ;branch to leave if not that far down
    if (!(Player_Y_HighPos - 0x02 < 0)) {
        //> ldx #$01
        //> stx ScrollLock              ;set scroll lock
        ScrollLock = 0x01
        //> ldy #$04
        //> sty $07                     ;set value here
        zp_07 = 0x04
        //> ldx #$00                    ;use X as flag, and clear for cloud level
        //> ldy GameTimerExpiredFlag    ;check game timer expiration flag
        //> bne HoleDie                 ;if set, branch
        if (zeroFlag) {
            //> ldy CloudTypeOverride       ;check for cloud type override
            //> bne ChkHoleX                ;skip to last part if found
            if (zeroFlag) {
                //> HoleDie:    inx                         ;set flag in X for player death
                //> ldy GameEngineSubroutine
                //> cpy #$0b                    ;check for some other routine running
                //> beq ChkHoleX                ;if so, branch ahead
                if (!(GameEngineSubroutine == 0x0B)) {
                    //> ldy DeathMusicLoaded        ;check value here
                    //> bne HoleBottom              ;if already set, branch to next part
                    if (zeroFlag) {
                        //> iny
                        //> sty EventMusicQueue         ;otherwise play death music
                        EventMusicQueue = (DeathMusicLoaded + 1) and 0xFF
                        //> sty DeathMusicLoaded        ;and set value here
                        DeathMusicLoaded = (DeathMusicLoaded + 1) and 0xFF
                    }
                    //> HoleBottom: ldy #$06
                    //> sty $07                     ;change value here
                    zp_07 = 0x06
                }
            }
        }
        if (!(GameEngineSubroutine == 0x0B)) {
            if (zeroFlag) {
            }
        }
        //> ChkHoleX:   cmp $07                     ;compare vertical high byte with value set here
        //> bmi ExitCtrl                ;if less, branch to leave
        if (!(A - zp_07 < 0)) {
            //> dex                         ;otherwise decrement flag in X
            //> bmi CloudExit               ;if flag was clear, branch to set modes and other values
            if (!negativeFlag) {
                //> ldy EventMusicBuffer        ;check to see if music is still playing
                //> bne ExitCtrl                ;branch to leave if so
                if (zeroFlag) {
                    //> lda #$06                    ;otherwise set to run lose life routine
                    //> sta GameEngineSubroutine    ;on next frame
                    GameEngineSubroutine = 0x06
                }
                //> ExitCtrl:   rts                         ;leave
                return
            }
        }
    }
    //> CloudExit:
    //> lda #$00
    //> sta JoypadOverride      ;clear controller override bits if any are set
    JoypadOverride = 0x00
    //> jsr SetEntr             ;do sub to set secondary mode
    setEntr()
    //> inc AltEntranceControl  ;set mode of entry to 3
    AltEntranceControl = (AltEntranceControl + 1) and 0xFF
    //> rts
    return
}

// Decompiled from SetEntr
fun setEntr() {
    //> SetEntr:   lda #$02               ;set starting position to override
    //> sta AltEntranceControl
    AltEntranceControl = 0x02
    //> jmp ChgAreaMode        ;set modes
}

// Decompiled from MovePlayerYAxis
fun movePlayerYAxis(A: Int) {
    //> MovePlayerYAxis:
    //> clc
    //> adc Player_Y_Position ;add contents of A to player position
    //> sta Player_Y_Position
    Player_Y_Position = (A + Player_Y_Position + 0) and 0xFF
    //> rts
    return
}

// Decompiled from ChgAreaMode
fun chgAreaMode(): Int {
    //> ChgAreaMode: inc DisableScreenFlag     ;set flag to disable screen output
    DisableScreenFlag = (DisableScreenFlag + 1) and 0xFF
    //> lda #$00
    //> sta OperMode_Task         ;set secondary mode of operation
    OperMode_Task = 0x00
    //> sta Sprite0HitDetectFlag  ;disable sprite 0 check
    Sprite0HitDetectFlag = 0x00
    //> ExitCAPipe:  rts                       ;leave
    return A
}

// Decompiled from EnterSidePipe
fun enterSidePipe() {
    //> EnterSidePipe:
    //> lda #$08               ;set player's horizontal speed
    //> sta Player_X_Speed
    Player_X_Speed = 0x08
    //> ldy #$01               ;set controller right button by default
    //> lda Player_X_Position  ;mask out higher nybble of player's
    //> and #%00001111         ;horizontal position
    //> bne RightPipe
    if (zeroFlag) {
        //> sta Player_X_Speed     ;if lower nybble = 0, set as horizontal speed
        Player_X_Speed = Player_X_Position and 0x0F
        //> tay                    ;and nullify controller bit override here
    }
    //> RightPipe: tya                    ;use contents of Y to
    //> jsr AutoControlPlayer  ;execute player control routine with ctrl bits nulled
    autoControlPlayer(Player_X_Position and 0x0F)
    //> rts
    return
}

// Decompiled from DonePlayerTask
fun donePlayerTask() {
    //> DonePlayerTask:
    //> lda #$00
    //> sta TimerControl          ;initialize master timer control to continue timers
    TimerControl = 0x00
    //> lda #$08
    //> sta GameEngineSubroutine  ;set player control routine to run next frame
    GameEngineSubroutine = 0x08
    //> rts                       ;leave
    return
}

// Decompiled from CyclePlayerPalette
fun cyclePlayerPalette(A: Int) {
    //> CyclePlayerPalette:
    //> and #$03              ;mask out all but d1-d0 (previously d3-d2)
    //> sta $00               ;store result here to use as palette bits
    zp_00 = A and 0x03
    //> lda Player_SprAttrib  ;get player attributes
    //> and #%11111100        ;save any other bits but palette bits
    //> ora $00               ;add palette bits
    //> sta Player_SprAttrib  ;store as new player attributes
    Player_SprAttrib = Player_SprAttrib and 0xFC or zp_00
    //> rts                   ;and leave
    return
}

// Decompiled from ResetPalStar
fun resetPalStar() {
    //> ResetPalStar:
    //> lda Player_SprAttrib  ;get player attributes
    //> and #%11111100        ;mask out palette bits to force palette 0
    //> sta Player_SprAttrib  ;store as new player attributes
    Player_SprAttrib = Player_SprAttrib and 0xFC
    //> rts                   ;and leave
    return
}

// Decompiled from PlayerMovementSubs
fun playerMovementSubs() {
    //> PlayerMovementSubs:
    //> lda #$00                  ;set A to init crouch flag by default
    //> ldy PlayerSize            ;is player small?
    //> bne SetCrouch             ;if so, branch
    if (zeroFlag) {
        //> lda Player_State          ;check state of player
        //> bne ProcMove              ;if not on the ground, branch
        if (zeroFlag) {
            //> lda Up_Down_Buttons       ;load controller bits for up and down
            //> and #%00000100            ;single out bit for down button
            //> SetCrouch: sta CrouchingFlag         ;store value in crouch flag
            CrouchingFlag = Up_Down_Buttons and 0x04
        }
    }
    //> ProcMove:  jsr PlayerPhysicsSub      ;run sub related to jumping and swimming
    playerPhysicsSub()
    //> lda PlayerChangeSizeFlag  ;if growing/shrinking flag set,
    //> bne NoMoveSub             ;branch to leave
    if (zeroFlag) {
        //> lda Player_State
        //> cmp #$03                  ;get player state
        //> beq MoveSubs              ;if climbing, branch ahead, leave timer unset
        if (!(Player_State - 0x03 == 0)) {
            //> ldy #$18
            //> sty ClimbSideTimer        ;otherwise reset timer now
            ClimbSideTimer = 0x18
        }
        //> MoveSubs:  jsr JumpEngine
        jumpEngine(Player_State)
        //> .dw OnGroundStateSub
        //> .dw JumpSwimSub
        //> .dw FallingSub
        //> .dw ClimbingSub
    }
    //> NoMoveSub: rts
    return
}

// Decompiled from PlayerPhysicsSub
fun playerPhysicsSub() {
    var A: Int = 0
    //> PlayerPhysicsSub:
    //> lda Player_State          ;check player state
    //> cmp #$03
    //> bne CheckForJumping       ;if not climbing, branch
    if (Player_State - 0x03 == 0) {
        //> ldy #$00
        //> lda Up_Down_Buttons       ;get controller bits for up/down
        //> and Player_CollisionBits  ;check against player's collision detection bits
        //> beq ProcClimb             ;if not pressing up or down, branch
        if (!zeroFlag) {
            //> iny
            //> and #%00001000            ;check for pressing up
            //> bne ProcClimb
            if (zeroFlag) {
                //> iny
            }
        }
        //> ProcClimb: ldx Climb_Y_MForceData,y  ;load value here
        //> stx Player_Y_MoveForce    ;store as vertical movement force
        Player_Y_MoveForce = Climb_Y_MForceData[((0x00 + 1) and 0xFF + 1) and 0xFF]
        //> lda #$08                  ;load default animation timing
        //> ldx Climb_Y_SpeedData,y   ;load some other value here
        //> stx Player_Y_Speed        ;store as vertical speed
        Player_Y_Speed = Climb_Y_SpeedData[((0x00 + 1) and 0xFF + 1) and 0xFF]
        //> bmi SetCAnim              ;if climbing down, use default animation timing value
        if (!negativeFlag) {
            //> lsr                       ;otherwise divide timer setting by 2
        }
        //> SetCAnim:  sta PlayerAnimTimerSet    ;store animation timer setting and leave
        PlayerAnimTimerSet = 0x08 shr 1
        //> rts
        return
    } else {
        //> CheckForJumping:
        //> lda JumpspringAnimCtrl    ;if jumpspring animating,
        //> bne NoJump                ;skip ahead to something else
        if (zeroFlag) {
            //> lda A_B_Buttons           ;check for A button press
            //> and #A_Button
            //> beq NoJump                ;if not, branch to something else
            if (!zeroFlag) {
                //> and PreviousA_B_Buttons   ;if button not pressed in previous frame, branch
                //> beq ProcJumping
                if (!zeroFlag) {
                    //> NoJump: jmp X_Physics             ;otherwise, jump to something else
                }
            }
        }
    }
    //> ProcJumping:
    //> lda Player_State           ;check player state
    //> beq InitJS                 ;if on the ground, branch
    if (!zeroFlag) {
        do {
            //> lda SwimmingFlag           ;if swimming flag not set, jump to do something else
            //> beq NoJump                 ;to prevent midair jumping, otherwise continue
        } while (zeroFlag)
        //> lda JumpSwimTimer          ;if jump/swim timer nonzero, branch
        //> bne InitJS
        if (zeroFlag) {
            //> lda Player_Y_Speed         ;check player's vertical speed
            //> bpl InitJS                 ;if player's vertical speed motionless or down, branch
            if (negativeFlag) {
                //> jmp X_Physics              ;if timer at zero and player still rising, do not swim
            }
        }
    } else {
        //> InitJS:    lda #$20                   ;set jump/swim timer
        //> sta JumpSwimTimer
        JumpSwimTimer = 0x20
        //> ldy #$00                   ;initialize vertical force and dummy variable
        //> sty Player_YMF_Dummy
        Player_YMF_Dummy = 0x00
        //> sty Player_Y_MoveForce
        Player_Y_MoveForce = 0x00
        //> lda Player_Y_HighPos       ;get vertical high and low bytes of jump origin
        //> sta JumpOrigin_Y_HighPos   ;and store them next to each other here
        JumpOrigin_Y_HighPos = Player_Y_HighPos
        //> lda Player_Y_Position
        //> sta JumpOrigin_Y_Position
        JumpOrigin_Y_Position = Player_Y_Position
        //> lda #$01                   ;set player state to jumping/swimming
        //> sta Player_State
        Player_State = 0x01
        //> lda Player_XSpeedAbsolute  ;check value related to walking/running speed
        //> cmp #$09
        //> bcc ChkWtr                 ;branch if below certain values, increment Y
        if (Player_XSpeedAbsolute >= 0x09) {
            //> iny                        ;for each amount equal or exceeded
            //> cmp #$10
            //> bcc ChkWtr
            if (A >= 0x10) {
                //> iny
                //> cmp #$19
                //> bcc ChkWtr
                if (A >= 0x19) {
                    //> iny
                    //> cmp #$1c
                    //> bcc ChkWtr                 ;note that for jumping, range is 0-4 for Y
                    if (A >= 0x1C) {
                        //> iny
                    }
                }
            }
        }
        //> ChkWtr:    lda #$01                   ;set value here (apparently always set to 1)
        //> sta DiffToHaltJump
        DiffToHaltJump = 0x01
        //> lda SwimmingFlag           ;if swimming flag disabled, branch
        //> beq GetYPhy
        if (!zeroFlag) {
            //> ldy #$05                   ;otherwise set Y to 5, range is 5-6
            //> lda Whirlpool_Flag         ;if whirlpool flag not set, branch
            //> beq GetYPhy
            if (!zeroFlag) {
                //> iny                        ;otherwise increment to 6
            }
        }
        //> GetYPhy:   lda JumpMForceData,y       ;store appropriate jump/swim
        //> sta VerticalForce          ;data here
        VerticalForce = JumpMForceData[(0x05 + 1) and 0xFF]
        //> lda FallMForceData,y
        //> sta VerticalForceDown
        VerticalForceDown = FallMForceData[(0x05 + 1) and 0xFF]
        //> lda InitMForceData,y
        //> sta Player_Y_MoveForce
        Player_Y_MoveForce = InitMForceData[(0x05 + 1) and 0xFF]
        //> lda PlayerYSpdData,y
        //> sta Player_Y_Speed
        Player_Y_Speed = PlayerYSpdData[(0x05 + 1) and 0xFF]
        //> lda SwimmingFlag           ;if swimming flag disabled, branch
        //> beq PJumpSnd
        if (!zeroFlag) {
            //> lda #Sfx_EnemyStomp        ;load swim/goomba stomp sound into
            //> sta Square1SoundQueue      ;square 1's sfx queue
            Square1SoundQueue = Sfx_EnemyStomp
            //> lda Player_Y_Position
            //> cmp #$14                   ;check vertical low byte of player position
            //> bcs X_Physics              ;if below a certain point, branch
            if (!(Player_Y_Position >= 0x14)) {
                //> lda #$00                   ;otherwise reset player's vertical speed
                //> sta Player_Y_Speed         ;and jump to something else to keep player
                Player_Y_Speed = 0x00
                //> jmp X_Physics              ;from swimming above water level
                //> PJumpSnd:  lda #Sfx_BigJump           ;load big mario's jump sound by default
                //> ldy PlayerSize             ;is mario big?
                //> beq SJumpSnd
                if (!zeroFlag) {
                    //> lda #Sfx_SmallJump         ;if not, load small mario's jump sound
                }
                //> SJumpSnd:  sta Square1SoundQueue      ;store appropriate jump sound in square 1 sfx queue
                Square1SoundQueue = Sfx_SmallJump
            }
        } else {
            if (!zeroFlag) {
            }
        }
    }
    //> X_Physics: ldy #$00
    //> sty $00                    ;init value here
    zp_00 = 0x00
    //> lda Player_State           ;if mario is on the ground, branch
    //> beq ProcPRun
    if (!zeroFlag) {
        //> lda Player_XSpeedAbsolute  ;check something that seems to be related
        //> cmp #$19                   ;to mario's speed
        //> bcs GetXPhy                ;if =>$19 branch here
        if (!(Player_XSpeedAbsolute >= 0x19)) {
            //> bcc ChkRFast               ;if not branch elsewhere
            if (A >= 0x19) {
                //> ProcPRun:  iny                        ;if mario on the ground, increment Y
                //> lda AreaType               ;check area type
                //> beq ChkRFast               ;if water type, branch
                if (!zeroFlag) {
                    //> dey                        ;decrement Y by default for non-water type area
                    //> lda Left_Right_Buttons     ;get left/right controller bits
                    //> cmp Player_MovingDir       ;check against moving direction
                    //> bne ChkRFast               ;if controller bits <> moving direction, skip this part
                    if (Left_Right_Buttons - Player_MovingDir == 0) {
                        //> lda A_B_Buttons            ;check for b button pressed
                        //> and #B_Button
                        //> bne SetRTmr                ;if pressed, skip ahead to set timer
                        if (zeroFlag) {
                            //> lda RunningTimer           ;check for running timer set
                            //> bne GetXPhy                ;if set, branch
                            if (zeroFlag) {
                                //> ChkRFast:  iny                        ;if running timer not set or level type is water,
                                //> inc $00                    ;increment Y again and temp variable in memory
                                zp_00 = (zp_00 + 1) and 0xFF
                                //> lda RunningSpeed
                                //> bne FastXSp                ;if running speed set here, branch
                                if (zeroFlag) {
                                    //> lda Player_XSpeedAbsolute
                                    //> cmp #$21                   ;otherwise check player's walking/running speed
                                    //> bcc GetXPhy                ;if less than a certain amount, branch ahead
                                    if (Player_XSpeedAbsolute >= 0x21) {
                                        //> FastXSp:   inc $00                    ;if running speed set or speed => $21 increment $00
                                        zp_00 = (zp_00 + 1) and 0xFF
                                        //> jmp GetXPhy                ;and jump ahead
                                        //> SetRTmr:   lda #$0a                   ;if b button pressed, set running timer
                                        //> sta RunningTimer
                                        RunningTimer = 0x0A
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (zeroFlag) {
                if (Player_XSpeedAbsolute >= 0x21) {
                }
            }
        }
    }
    if (!zeroFlag) {
        if (Left_Right_Buttons - Player_MovingDir == 0) {
            if (zeroFlag) {
                if (zeroFlag) {
                    if (zeroFlag) {
                        if (Player_XSpeedAbsolute >= 0x21) {
                        }
                    }
                }
            }
        }
    }
    if (zeroFlag) {
        if (Player_XSpeedAbsolute >= 0x21) {
        }
    }
    //> GetXPhy:   lda MaxLeftXSpdData,y      ;get maximum speed to the left
    //> sta MaximumLeftSpeed
    MaximumLeftSpeed = MaxLeftXSpdData[(((0x00 + 1) and 0xFF - 1) and 0xFF + 1) and 0xFF]
    //> lda GameEngineSubroutine   ;check for specific routine running
    //> cmp #$07                   ;(player entrance)
    //> bne GetXPhy2               ;if not running, skip and use old value of Y
    if (GameEngineSubroutine - 0x07 == 0) {
        //> ldy #$03                   ;otherwise set Y to 3
    }
    //> GetXPhy2:  lda MaxRightXSpdData,y     ;get maximum speed to the right
    //> sta MaximumRightSpeed
    MaximumRightSpeed = MaxRightXSpdData[0x03]
    //> ldy $00                    ;get other value in memory
    //> lda FrictionData,y         ;get value using value in memory as offset
    //> sta FrictionAdderLow
    FrictionAdderLow = FrictionData[zp_00]
    //> lda #$00
    //> sta FrictionAdderHigh      ;init something here
    FrictionAdderHigh = 0x00
    //> lda PlayerFacingDir
    //> cmp Player_MovingDir       ;check facing direction against moving direction
    //> beq ExitPhy                ;if the same, branch to leave
    if (!(PlayerFacingDir - Player_MovingDir == 0)) {
        //> asl FrictionAdderLow       ;otherwise shift d7 of friction adder low into carry
        FrictionAdderLow = (FrictionAdderLow shl 1) and 0xFF
        //> rol FrictionAdderHigh      ;then rotate carry onto d0 of friction adder high
    }
    //> ExitPhy:   rts                        ;and then leave
    return
}

// Decompiled from GetPlayerAnimSpeed
fun getPlayerAnimSpeed() {
    var A: Int = 0
    //> GetPlayerAnimSpeed:
    //> ldy #$00                   ;initialize offset in Y
    //> lda Player_XSpeedAbsolute  ;check player's walking/running speed
    //> cmp #$1c                   ;against preset amount
    //> bcs SetRunSpd              ;if greater than a certain amount, branch ahead
    if (!(Player_XSpeedAbsolute >= 0x1C)) {
        //> iny                        ;otherwise increment Y
        //> cmp #$0e                   ;compare against lower amount
        //> bcs ChkSkid                ;if greater than this but not greater than first, skip increment
        if (!(A >= 0x0E)) {
            //> iny                        ;otherwise increment Y again
        }
        //> ChkSkid:    lda SavedJoypadBits        ;get controller bits
        //> and #%01111111             ;mask out A button
        //> beq SetAnimSpd             ;if no other buttons pressed, branch ahead of all this
        if (!zeroFlag) {
            //> and #$03                   ;mask out all others except left and right
            //> cmp Player_MovingDir       ;check against moving direction
            //> bne ProcSkid               ;if left/right controller bits <> moving direction, branch
            if (A and 0x03 - Player_MovingDir == 0) {
                //> lda #$00                   ;otherwise set zero value here
                //> SetRunSpd:  sta RunningSpeed           ;store zero or running speed here
                RunningSpeed = 0x00
                //> jmp SetAnimSpd
            } else {
                //> ProcSkid:   lda Player_XSpeedAbsolute  ;check player's walking/running speed
                //> cmp #$0b                   ;against one last amount
                //> bcs SetAnimSpd             ;if greater than this amount, branch
                if (!(Player_XSpeedAbsolute >= 0x0B)) {
                    //> lda PlayerFacingDir
                    //> sta Player_MovingDir       ;otherwise use facing direction to set moving direction
                    Player_MovingDir = PlayerFacingDir
                    //> lda #$00
                    //> sta Player_X_Speed         ;nullify player's horizontal speed
                    Player_X_Speed = 0x00
                    //> sta Player_X_MoveForce     ;and dummy variable for player
                    Player_X_MoveForce = 0x00
                }
            }
        }
    }
    if (!(Player_XSpeedAbsolute >= 0x0B)) {
    }
    //> SetAnimSpd: lda PlayerAnimTmrData,y    ;get animation timer setting using Y as offset
    //> sta PlayerAnimTimerSet
    PlayerAnimTimerSet = PlayerAnimTmrData[((0x00 + 1) and 0xFF + 1) and 0xFF]
    //> rts
    return
}

// Decompiled from ImposeFriction
fun imposeFriction(A: Int): Int {
    //> ImposeFriction:
    //> and Player_CollisionBits  ;perform AND between left/right controller bits and collision flag
    //> cmp #$00                  ;then compare to zero (this instruction is redundant)
    //> bne JoypFrict             ;if any bits set, branch to next part
    if (A and Player_CollisionBits - 0x00 == 0) {
        //> lda Player_X_Speed
        //> beq SetAbsSpd             ;if player has no horizontal speed, branch ahead to last part
        if (!zeroFlag) {
            //> bpl RghtFrict             ;if player moving to the right, branch to slow
            if (negativeFlag) {
                //> bmi LeftFrict             ;otherwise logic dictates player moving left, branch to slow
                if (!negativeFlag) {
                    //> JoypFrict: lsr                       ;put right controller bit into carry
                    //> bcc RghtFrict             ;if left button pressed, carry = 0, thus branch
                    if (carryFlag) {
                        //> LeftFrict: lda Player_X_MoveForce    ;load value set here
                        //> clc
                        //> adc FrictionAdderLow      ;add to it another value set here
                        //> sta Player_X_MoveForce    ;store here
                        Player_X_MoveForce = (Player_X_MoveForce + FrictionAdderLow + 0) and 0xFF
                        //> lda Player_X_Speed
                        //> adc FrictionAdderHigh     ;add value plus carry to horizontal speed
                        //> sta Player_X_Speed        ;set as new horizontal speed
                        Player_X_Speed = (Player_X_Speed + FrictionAdderHigh + 0) and 0xFF
                        //> cmp MaximumRightSpeed     ;compare against maximum value for right movement
                        //> bmi XSpdSign              ;if horizontal speed greater negatively, branch
                        if (!((Player_X_Speed + FrictionAdderHigh + 0) and 0xFF - MaximumRightSpeed < 0)) {
                            //> lda MaximumRightSpeed     ;otherwise set preset value as horizontal speed
                            //> sta Player_X_Speed        ;thus slowing the player's left movement down
                            Player_X_Speed = MaximumRightSpeed
                            //> jmp SetAbsSpd             ;skip to the end
                            //> RghtFrict: lda Player_X_MoveForce    ;load value set here
                            //> sec
                            //> sbc FrictionAdderLow      ;subtract from it another value set here
                            //> sta Player_X_MoveForce    ;store here
                            Player_X_MoveForce = (Player_X_MoveForce - FrictionAdderLow - (1 - 1)) and 0xFF
                            //> lda Player_X_Speed
                            //> sbc FrictionAdderHigh     ;subtract value plus borrow from horizontal speed
                            //> sta Player_X_Speed        ;set as new horizontal speed
                            Player_X_Speed = (Player_X_Speed - FrictionAdderHigh - (1 - 1)) and 0xFF
                            //> cmp MaximumLeftSpeed      ;compare against maximum value for left movement
                            //> bpl XSpdSign              ;if horizontal speed greater positively, branch
                            if ((Player_X_Speed - FrictionAdderHigh - (1 - 1)) and 0xFF - MaximumLeftSpeed < 0) {
                                //> lda MaximumLeftSpeed      ;otherwise set preset value as horizontal speed
                                //> sta Player_X_Speed        ;thus slowing the player's right movement down
                                Player_X_Speed = MaximumLeftSpeed
                            }
                        }
                    }
                }
                if (!((Player_X_Speed + FrictionAdderHigh + 0) and 0xFF - MaximumRightSpeed < 0)) {
                    if ((Player_X_Speed - FrictionAdderHigh - (1 - 1)) and 0xFF - MaximumLeftSpeed < 0) {
                    }
                }
            } else {
                if ((Player_X_Speed - FrictionAdderHigh - (1 - 1)) and 0xFF - MaximumLeftSpeed < 0) {
                }
                //> XSpdSign:  cmp #$00                  ;if player not moving or moving to the right,
                //> bpl SetAbsSpd             ;branch and leave horizontal speed value unmodified
                if (A - 0x00 < 0) {
                    //> eor #$ff
                    //> clc                       ;otherwise get two's compliment to get absolute
                    //> adc #$01                  ;unsigned walking/running speed
                }
            }
        }
    }
    if (carryFlag) {
        if (!((Player_X_Speed + FrictionAdderHigh + 0) and 0xFF - MaximumRightSpeed < 0)) {
            if ((Player_X_Speed - FrictionAdderHigh - (1 - 1)) and 0xFF - MaximumLeftSpeed < 0) {
            }
        }
    } else {
        if ((Player_X_Speed - FrictionAdderHigh - (1 - 1)) and 0xFF - MaximumLeftSpeed < 0) {
        }
        if (A - 0x00 < 0) {
        }
    }
    //> SetAbsSpd: sta Player_XSpeedAbsolute ;store walking/running speed here and leave
    Player_XSpeedAbsolute = (MaximumLeftSpeed xor 0xFF + 0x01 + 0) and 0xFF
    //> rts
    return A
}

// Decompiled from ProcFireball_Bubble
fun procfireballBubble() {
    //> ProcFireball_Bubble:
    //> lda PlayerStatus           ;check player's status
    //> cmp #$02
    //> bcc ProcAirBubbles         ;if not fiery, branch
    if (PlayerStatus >= 0x02) {
        //> lda A_B_Buttons
        //> and #B_Button              ;check for b button pressed
        //> beq ProcFireballs          ;branch if not pressed
        if (!zeroFlag) {
            //> and PreviousA_B_Buttons
            //> bne ProcFireballs          ;if button pressed in previous frame, branch
            if (zeroFlag) {
                //> lda FireballCounter        ;load fireball counter
                //> and #%00000001             ;get LSB and use as offset for buffer
                //> tax
                //> lda Fireball_State,x       ;load fireball state
                //> bne ProcFireballs          ;if not inactive, branch
                if (zeroFlag) {
                    //> ldy Player_Y_HighPos       ;if player too high or too low, branch
                    //> dey
                    //> bne ProcFireballs
                    if (zeroFlag) {
                        //> lda CrouchingFlag          ;if player crouching, branch
                        //> bne ProcFireballs
                        if (zeroFlag) {
                            //> lda Player_State           ;if player's state = climbing, branch
                            //> cmp #$03
                            //> beq ProcFireballs
                            if (!(Player_State - 0x03 == 0)) {
                                //> lda #Sfx_Fireball          ;play fireball sound effect
                                //> sta Square1SoundQueue
                                Square1SoundQueue = Sfx_Fireball
                                //> lda #$02                   ;load state
                                //> sta Fireball_State,x
                                Fireball_State[FireballCounter and 0x01] = 0x02
                                //> ldy PlayerAnimTimerSet     ;copy animation frame timer setting
                                //> sty FireballThrowingTimer  ;into fireball throwing timer
                                FireballThrowingTimer = PlayerAnimTimerSet
                                //> dey
                                //> sty PlayerAnimTimer        ;decrement and store in player's animation timer
                                PlayerAnimTimer = (PlayerAnimTimerSet - 1) and 0xFF
                                //> inc FireballCounter        ;increment fireball counter
                                FireballCounter = (FireballCounter + 1) and 0xFF
                            }
                        }
                    }
                }
            }
        }
        //> ProcFireballs:
        //> ldx #$00
        //> jsr FireballObjCore  ;process first fireball object
        fireballObjCore(0x00)
        //> ldx #$01
        //> jsr FireballObjCore  ;process second fireball object, then do air bubbles
        fireballObjCore(0x01)
    }
    //> ProcAirBubbles:
    //> lda AreaType                ;if not water type level, skip the rest of this
    //> bne BublExit
    if (zeroFlag) {
        //> ldx #$02                    ;otherwise load counter and use as offset
        do {
            //> BublLoop: stx ObjectOffset            ;store offset
            ObjectOffset = 0x02
            //> jsr BubbleCheck             ;check timers and coordinates, create air bubble
            bubbleCheck(0x02)
            //> jsr RelativeBubblePosition  ;get relative coordinates
            relativeBubblePosition()
            //> jsr GetBubbleOffscreenBits  ;get offscreen information
            getBubbleOffscreenBits()
            //> jsr DrawBubble              ;draw the air bubble
            drawBubble(0x02)
            //> dex
            //> bpl BublLoop                ;do this until all three are handled
        } while (negativeFlag)
    }
    //> BublExit: rts                         ;then leave
    return
}

// Decompiled from FireballObjCore
fun fireballObjCore(X: Int) {
    //> FireballObjCore:
    //> stx ObjectOffset             ;store offset as current object
    ObjectOffset = X
    //> lda Fireball_State,x         ;check for d7 = 1
    //> asl
    //> bcs FireballExplosion        ;if so, branch to get relative coordinates and draw explosion
    if (!carryFlag) {
        //> ldy Fireball_State,x         ;if fireball inactive, branch to leave
        //> beq NoFBall
        if (!zeroFlag) {
            //> dey                          ;if fireball state set to 1, skip this part and just run it
            //> beq RunFB
            if (!zeroFlag) {
                //> lda Player_X_Position        ;get player's horizontal position
                //> adc #$04                     ;add four pixels and store as fireball's horizontal position
                //> sta Fireball_X_Position,x
                Fireball_X_Position[X] = (Player_X_Position + 0x04 + 0) and 0xFF
                //> lda Player_PageLoc           ;get player's page location
                //> adc #$00                     ;add carry and store as fireball's page location
                //> sta Fireball_PageLoc,x
                Fireball_PageLoc[X] = (Player_PageLoc + 0x00 + 0) and 0xFF
                //> lda Player_Y_Position        ;get player's vertical position and store
                //> sta Fireball_Y_Position,x
                Fireball_Y_Position[X] = Player_Y_Position
                //> lda #$01                     ;set high byte of vertical position
                //> sta Fireball_Y_HighPos,x
                Fireball_Y_HighPos[X] = 0x01
                //> ldy PlayerFacingDir          ;get player's facing direction
                //> dey                          ;decrement to use as offset here
                //> lda FireballXSpdData,y       ;set horizontal speed of fireball accordingly
                //> sta Fireball_X_Speed,x
                Fireball_X_Speed[X] = FireballXSpdData[(PlayerFacingDir - 1) and 0xFF]
                //> lda #$04                     ;set vertical speed of fireball
                //> sta Fireball_Y_Speed,x
                Fireball_Y_Speed[X] = 0x04
                //> lda #$07
                //> sta Fireball_BoundBoxCtrl,x  ;set bounding box size control for fireball
                Fireball_BoundBoxCtrl[X] = 0x07
                //> dec Fireball_State,x         ;decrement state to 1 to skip this part from now on
                Fireball_State[X] = (Fireball_State[X] - 1) and 0xFF
            }
            //> RunFB:   txa                          ;add 7 to offset to use
            //> clc                          ;as fireball offset for next routines
            //> adc #$07
            //> tax
            //> lda #$50                     ;set downward movement force here
            //> sta $00
            zp_00 = 0x50
            //> lda #$03                     ;set maximum speed here
            //> sta $02
            zp_02 = 0x03
            //> lda #$00
            //> jsr ImposeGravity            ;do sub here to impose gravity on fireball and move vertically
            imposeGravity(0x00, (X + 0x07 + 0) and 0xFF)
            //> jsr MoveObjectHorizontally   ;do another sub to move it horizontally
            moveObjectHorizontally((X + 0x07 + 0) and 0xFF)
            //> ldx ObjectOffset             ;return fireball offset to X
            //> jsr RelativeFireballPosition ;get relative coordinates
            relativeFireballPosition()
            //> jsr GetFireballOffscreenBits ;get offscreen information
            getFireballOffscreenBits()
            //> jsr GetFireballBoundBox      ;get bounding box coordinates
            getFireballBoundBox(ObjectOffset)
            //> jsr FireballBGCollision      ;do fireball to background collision detection
            fireballBGCollision(ObjectOffset)
            //> lda FBall_OffscreenBits      ;get fireball offscreen bits
            //> and #%11001100               ;mask out certain bits
            //> bne EraseFB                  ;if any bits still set, branch to kill fireball
            if (zeroFlag) {
                //> jsr FireballEnemyCollision   ;do fireball to enemy collision detection and deal with collisions
                fireballEnemyCollision(ObjectOffset)
                //> jmp DrawFireball             ;draw fireball appropriately and leave
            }
            //> EraseFB: lda #$00                     ;erase fireball state
            //> sta Fireball_State,x
            Fireball_State[ObjectOffset] = 0x00
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
    //> lda Fireball_Rel_YPos      ;get relative vertical coordinate
    //> sta Sprite_Y_Position,y    ;store as sprite Y coordinate
    Sprite_Y_Position[FBall_SprDataOffset[ObjectOffset]] = Fireball_Rel_YPos
    //> lda Fireball_Rel_XPos      ;get relative horizontal coordinate
    //> sta Sprite_X_Position,y    ;store as sprite X coordinate, then do shared code
    Sprite_X_Position[FBall_SprDataOffset[ObjectOffset]] = Fireball_Rel_XPos
    //> DrawExplosion_Fireball:
    //> ldy Alt_SprDataOffset,x  ;get OAM data offset of alternate sort for fireball's explosion
    //> lda Fireball_State,x     ;load fireball state
    //> inc Fireball_State,x     ;increment state for next frame
    Fireball_State[ObjectOffset] = (Fireball_State[ObjectOffset] + 1) and 0xFF
    //> lsr                      ;divide by 2
    //> and #%00000111           ;mask out all but d3-d1
    //> cmp #$03                 ;check to see if time to kill fireball
    //> bcs KillFireBall         ;branch if so, otherwise continue to draw explosion
    if (!(Fireball_State[X] shr 1 and 0x07 >= 0x03)) {
    }
    //> KillFireBall:
    //> lda #$00                    ;clear fireball state to kill it
    //> sta Fireball_State,x
    Fireball_State[ObjectOffset] = 0x00
    //> rts
    return
}

// Decompiled from BubbleCheck
fun bubbleCheck(X: Int) {
    //> BubbleCheck:
    //> lda PseudoRandomBitReg+1,x  ;get part of LSFR
    //> and #$01
    //> sta $07                     ;store pseudorandom bit here
    zp_07 = PseudoRandomBitReg[X] and 0x01
    //> lda Bubble_Y_Position,x     ;get vertical coordinate for air bubble
    //> cmp #$f8                    ;if offscreen coordinate not set,
    //> bne MoveBubl                ;branch to move air bubble
    if (Bubble_Y_Position[X] - 0xF8 == 0) {
        //> lda AirBubbleTimer          ;if air bubble timer not expired,
        //> bne ExitBubl                ;branch to leave, otherwise create new air bubble
        if (zeroFlag) {
            //> MoveBubl: ldy $07                  ;get pseudorandom bit again, use as offset
            //> lda Bubble_YMF_Dummy,x
            //> sec                      ;subtract pseudorandom amount from dummy variable
            //> sbc Bubble_MForceData,y
            //> sta Bubble_YMF_Dummy,x   ;save dummy variable
            Bubble_YMF_Dummy[X] = (Bubble_YMF_Dummy[X] - Bubble_MForceData[zp_07] - (1 - 1)) and 0xFF
            //> lda Bubble_Y_Position,x
            //> sbc #$00                 ;subtract borrow from airbubble's vertical coordinate
            //> cmp #$20                 ;if below the status bar,
            //> bcs Y_Bubl               ;branch to go ahead and use to move air bubble upwards
            if (!((Bubble_Y_Position[X] - 0x00 - (1 - 1)) and 0xFF >= 0x20)) {
                //> lda #$f8                 ;otherwise set offscreen coordinate
            }
            //> Y_Bubl:   sta Bubble_Y_Position,x  ;store as new vertical coordinate for air bubble
            Bubble_Y_Position[X] = 0xF8
        }
    }
    if (!((Bubble_Y_Position[X] - 0x00 - (1 - 1)) and 0xFF >= 0x20)) {
    }
    //> ExitBubl: rts                      ;leave
    return
}

// Decompiled from SetupBubble
fun setupBubble(X: Int) {
    //> SetupBubble:
    //> ldy #$00                 ;load default value here
    //> lda PlayerFacingDir      ;get player's facing direction
    //> lsr                      ;move d0 to carry
    //> bcc PosBubl              ;branch to use default value if facing left
    if (carryFlag) {
        //> ldy #$08                 ;otherwise load alternate value here
    }
    //> PosBubl:  tya                      ;use value loaded as adder
    //> adc Player_X_Position    ;add to player's horizontal position
    //> sta Bubble_X_Position,x  ;save as horizontal position for airbubble
    Bubble_X_Position[X] = (0x08 + Player_X_Position + 0) and 0xFF
    //> lda Player_PageLoc
    //> adc #$00                 ;add carry to player's page location
    //> sta Bubble_PageLoc,x     ;save as page location for airbubble
    Bubble_PageLoc[X] = (Player_PageLoc + 0x00 + 0) and 0xFF
    //> lda Player_Y_Position
    //> clc                      ;add eight pixels to player's vertical position
    //> adc #$08
    //> sta Bubble_Y_Position,x  ;save as vertical position for air bubble
    Bubble_Y_Position[X] = (Player_Y_Position + 0x08 + 0) and 0xFF
    //> lda #$01
    //> sta Bubble_Y_HighPos,x   ;set vertical high byte for air bubble
    Bubble_Y_HighPos[X] = 0x01
    //> ldy $07                  ;get pseudorandom bit, use as offset
    //> lda BubbleTimerData,y    ;get data for air bubble timer
    //> sta AirBubbleTimer       ;set air bubble timer
    AirBubbleTimer = BubbleTimerData[zp_07]
    //> MoveBubl: ldy $07                  ;get pseudorandom bit again, use as offset
    //> lda Bubble_YMF_Dummy,x
    //> sec                      ;subtract pseudorandom amount from dummy variable
    //> sbc Bubble_MForceData,y
    //> sta Bubble_YMF_Dummy,x   ;save dummy variable
    Bubble_YMF_Dummy[X] = (Bubble_YMF_Dummy[X] - Bubble_MForceData[zp_07] - (1 - 1)) and 0xFF
    //> lda Bubble_Y_Position,x
    //> sbc #$00                 ;subtract borrow from airbubble's vertical coordinate
    //> cmp #$20                 ;if below the status bar,
    //> bcs Y_Bubl               ;branch to go ahead and use to move air bubble upwards
    if (!((Bubble_Y_Position[X] - 0x00 - (1 - 1)) and 0xFF >= 0x20)) {
        //> lda #$f8                 ;otherwise set offscreen coordinate
    }
    //> Y_Bubl:   sta Bubble_Y_Position,x  ;store as new vertical coordinate for air bubble
    Bubble_Y_Position[X] = 0xF8
    //> ExitBubl: rts                      ;leave
    return
}

// Decompiled from RunGameTimer
fun runGameTimer() {
    var A: Int = 0
    //> RunGameTimer:
    //> lda OperMode               ;get primary mode of operation
    //> beq ExGTimer               ;branch to leave if in title screen mode
    if (!zeroFlag) {
        //> lda GameEngineSubroutine
        //> cmp #$08                   ;if routine number less than eight running,
        //> bcc ExGTimer               ;branch to leave
        if (GameEngineSubroutine >= 0x08) {
            //> cmp #$0b                   ;if running death routine,
            //> beq ExGTimer               ;branch to leave
            if (!(A - 0x0B == 0)) {
                //> lda Player_Y_HighPos
                //> cmp #$02                   ;if player below the screen,
                //> bcs ExGTimer               ;branch to leave regardless of level type
                if (!(Player_Y_HighPos >= 0x02)) {
                    //> lda GameTimerCtrlTimer     ;if game timer control not yet expired,
                    //> bne ExGTimer               ;branch to leave
                    if (zeroFlag) {
                        //> lda GameTimerDisplay
                        //> ora GameTimerDisplay+1     ;otherwise check game timer digits
                        //> ora GameTimerDisplay+2
                        //> beq TimeUpOn               ;if game timer digits at 000, branch to time-up code
                        if (!zeroFlag) {
                            //> ldy GameTimerDisplay       ;otherwise check first digit
                            //> dey                        ;if first digit not on 1,
                            //> bne ResGTCtrl              ;branch to reset game timer control
                            if (zeroFlag) {
                                //> lda GameTimerDisplay+1     ;otherwise check second and third digits
                                //> ora GameTimerDisplay+2
                                //> bne ResGTCtrl              ;if timer not at 100, branch to reset game timer control
                                if (zeroFlag) {
                                    //> lda #TimeRunningOutMusic
                                    //> sta EventMusicQueue        ;otherwise load time running out music
                                    EventMusicQueue = TimeRunningOutMusic
                                }
                            }
                            //> ResGTCtrl: lda #$18                   ;reset game timer control
                            //> sta GameTimerCtrlTimer
                            GameTimerCtrlTimer = 0x18
                            //> ldy #$23                   ;set offset for last digit
                            //> lda #$ff                   ;set value to decrement game timer digit
                            //> sta DigitModifier+5
                            DigitModifier = 0xFF
                            //> jsr DigitsMathRoutine      ;do sub to decrement game timer slowly
                            digitsMathRoutine(0x23)
                            //> lda #$a4                   ;set status nybbles to update game timer display
                            //> jmp PrintStatusBarNumbers  ;do sub to update the display
                        }
                        //> TimeUpOn:  sta PlayerStatus           ;init player status (note A will always be zero here)
                        PlayerStatus = 0xA4
                        //> jsr ForceInjury            ;do sub to kill the player (note player is small here)
                        forceInjury(0xA4)
                        //> inc GameTimerExpiredFlag   ;set game timer expiration flag
                        GameTimerExpiredFlag = (GameTimerExpiredFlag + 1) and 0xFF
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
    //> ProcessWhirlpools:
    //> lda AreaType                ;check for water type level
    //> bne ExitWh                  ;branch to leave if not found
    if (zeroFlag) {
        //> sta Whirlpool_Flag          ;otherwise initialize whirlpool flag
        Whirlpool_Flag = AreaType
        //> lda TimerControl            ;if master timer control set,
        //> bne ExitWh                  ;branch to leave
        if (zeroFlag) {
            //> ldy #$04                    ;otherwise start with last whirlpool data
            while (negativeFlag) {
                //> adc #$00                    ;add carry
                //> sta $01                     ;store result as page location of right extent here
                zp_01 = (TimerControl + 0x00 + 0) and 0xFF
                //> lda Player_X_Position       ;get player's horizontal position
                //> sec
                //> sbc Whirlpool_LeftExtent,y  ;subtract left extent
                //> lda Player_PageLoc          ;get player's page location
                //> sbc Whirlpool_PageLoc,y     ;subtract borrow
                //> bmi NextWh                  ;if player too far left, branch to get next data
                if (!negativeFlag) {
                    //> lda $02                     ;otherwise get right extent
                    //> sec
                    //> sbc Player_X_Position       ;subtract player's horizontal coordinate
                    //> lda $01                     ;get right extent's page location
                    //> sbc Player_PageLoc          ;subtract borrow
                    //> bpl WhirlpoolActivate       ;if player within right extent, branch to whirlpool code
                    if (negativeFlag) {
                        do {
                            //> WhLoop: lda Whirlpool_LeftExtent,y  ;get left extent of whirlpool
                            //> clc
                            //> adc Whirlpool_Length,y      ;add length of whirlpool
                            //> sta $02                     ;store result as right extent here
                            zp_02 = (Whirlpool_LeftExtent[0x04] + Whirlpool_Length[0x04] + 0) and 0xFF
                            //> lda Whirlpool_PageLoc,y     ;get page location
                            //> beq NextWh                  ;if none or page 0, branch to get next data
                            //> NextWh: dey                         ;move onto next whirlpool data
                            //> bpl WhLoop                  ;do this until all whirlpools are checked
                        } while (!negativeFlag)
                        //> ExitWh: rts                         ;leave
                        return
                    }
                }
                do {
                } while (!negativeFlag)
            }
        }
    }
    //> WhirlpoolActivate:
    //> lda Whirlpool_Length,y      ;get length of whirlpool
    //> lsr                         ;divide by 2
    //> sta $00                     ;save here
    zp_00 = Whirlpool_Length[(0x04 - 1) and 0xFF] shr 1
    //> lda Whirlpool_LeftExtent,y  ;get left extent of whirlpool
    //> clc
    //> adc $00                     ;add length divided by 2
    //> sta $01                     ;save as center of whirlpool
    zp_01 = (Whirlpool_LeftExtent[(0x04 - 1) and 0xFF] + zp_00 + 0) and 0xFF
    //> lda Whirlpool_PageLoc,y     ;get page location
    //> adc #$00                    ;add carry
    //> sta $00                     ;save as page location of whirlpool center
    zp_00 = (Whirlpool_PageLoc[(0x04 - 1) and 0xFF] + 0x00 + 0) and 0xFF
    //> lda FrameCounter            ;get frame counter
    //> lsr                         ;shift d0 into carry (to run on every other frame)
    //> bcc WhPull                  ;if d0 not set, branch to last part of code
    if (0) {
        //> lda $01                     ;get center
        //> sec
        //> sbc Player_X_Position       ;subtract player's horizontal coordinate
        //> lda $00                     ;get page location of center
        //> sbc Player_PageLoc          ;subtract borrow
        //> bpl LeftWh                  ;if player to the left of center, branch
        if (negativeFlag) {
            //> lda Player_X_Position       ;otherwise slowly pull player left, towards the center
            //> sec
            //> sbc #$01                    ;subtract one pixel
            //> sta Player_X_Position       ;set player's new horizontal coordinate
            Player_X_Position = (Player_X_Position - 0x01 - (1 - 1)) and 0xFF
            //> lda Player_PageLoc
            //> sbc #$00                    ;subtract borrow
            //> jmp SetPWh                  ;jump to set player's new page location
        } else {
            //> LeftWh: lda Player_CollisionBits    ;get player's collision bits
            //> lsr                         ;shift d0 into carry
            //> bcc WhPull                  ;if d0 not set, branch
            if (carryFlag) {
                //> lda Player_X_Position       ;otherwise slowly pull player right, towards the center
                //> clc
                //> adc #$01                    ;add one pixel
                //> sta Player_X_Position       ;set player's new horizontal coordinate
                Player_X_Position = (Player_X_Position + 0x01 + 0) and 0xFF
                //> lda Player_PageLoc
                //> adc #$00                    ;add carry
                //> SetPWh: sta Player_PageLoc          ;set player's new page location
                Player_PageLoc = (Player_PageLoc + 0x00 + 0) and 0xFF
            }
        }
    }
    //> WhPull: lda #$10
    //> sta $00                     ;set vertical movement force
    zp_00 = 0x10
    //> lda #$01
    //> sta Whirlpool_Flag          ;set whirlpool flag to be used later
    Whirlpool_Flag = 0x01
    //> sta $02                     ;also set maximum vertical speed
    zp_02 = 0x01
    //> lsr
    //> tax                         ;set X for player offset
    //> jmp ImposeGravity           ;jump to put whirlpool effect on player vertically, do not return
}

// Decompiled from FlagpoleRoutine
fun flagpoleRoutine() {
    var X: Int = 0
    //> FlagpoleRoutine:
    //> ldx #$05                  ;set enemy object offset
    //> stx ObjectOffset          ;to special use slot
    ObjectOffset = 0x05
    //> lda Enemy_ID,x
    //> cmp #FlagpoleFlagObject   ;if flagpole flag not found,
    //> bne ExitFlagP             ;branch to leave
    if (Enemy_ID[0x05] - FlagpoleFlagObject == 0) {
        //> lda GameEngineSubroutine
        //> cmp #$04                  ;if flagpole slide routine not running,
        //> bne SkipScore             ;branch to near the end of code
        if (GameEngineSubroutine - 0x04 == 0) {
            //> lda Player_State
            //> cmp #$03                  ;if player state not climbing,
            //> bne SkipScore             ;branch to near the end of code
            if (Player_State - 0x03 == 0) {
                //> lda Enemy_Y_Position,x    ;check flagpole flag's vertical coordinate
                //> cmp #$aa                  ;if flagpole flag down to a certain point,
                //> bcs GiveFPScr             ;branch to end the level
                if (!(Enemy_Y_Position[X] >= 0xAA)) {
                    //> lda Player_Y_Position     ;check player's vertical coordinate
                    //> cmp #$a2                  ;if player down to a certain point,
                    //> bcs GiveFPScr             ;branch to end the level
                    if (!(Player_Y_Position >= 0xA2)) {
                        //> lda Enemy_YMF_Dummy,x
                        //> adc #$ff                  ;add movement amount to dummy variable
                        //> sta Enemy_YMF_Dummy,x     ;save dummy variable
                        Enemy_YMF_Dummy[0x05] = (Enemy_YMF_Dummy[0x05] + 0xFF + Player_Y_Position >= 0xA2) and 0xFF
                        //> lda Enemy_Y_Position,x    ;get flag's vertical coordinate
                        //> adc #$01                  ;add 1 plus carry to move flag, and
                        //> sta Enemy_Y_Position,x    ;store vertical coordinate
                        Enemy_Y_Position[0x05] = (Enemy_Y_Position[0x05] + 0x01 + Player_Y_Position >= 0xA2) and 0xFF
                        //> lda FlagpoleFNum_YMFDummy
                        //> sec                       ;subtract movement amount from dummy variable
                        //> sbc #$ff
                        //> sta FlagpoleFNum_YMFDummy ;save dummy variable
                        FlagpoleFNum_YMFDummy = (FlagpoleFNum_YMFDummy - 0xFF - (1 - 1)) and 0xFF
                        //> lda FlagpoleFNum_Y_Pos
                        //> sbc #$01                  ;subtract one plus borrow to move floatey number,
                        //> sta FlagpoleFNum_Y_Pos    ;and store vertical coordinate here
                        FlagpoleFNum_Y_Pos = (FlagpoleFNum_Y_Pos - 0x01 - (1 - 1)) and 0xFF
                        //> SkipScore: jmp FPGfx                 ;jump to skip ahead and draw flag and floatey number
                    }
                }
            }
        }
        //> GiveFPScr: ldy FlagpoleScore         ;get score offset from earlier (when player touched flagpole)
        //> lda FlagpoleScoreMods,y   ;get amount to award player points
        //> ldx FlagpoleScoreDigits,y ;get digit with which to award points
        //> sta DigitModifier,x       ;store in digit modifier
        DigitModifier[FlagpoleScoreDigits[FlagpoleScore]] = FlagpoleScoreMods[FlagpoleScore]
        //> jsr AddToScore            ;do sub to award player points depending on height of collision
        addToScore()
        //> lda #$05
        //> sta GameEngineSubroutine  ;set to run end-of-level subroutine on next frame
        GameEngineSubroutine = 0x05
        //> FPGfx:     jsr GetEnemyOffscreenBits ;get offscreen information
        getEnemyOffscreenBits(FlagpoleScoreDigits[FlagpoleScore])
        //> jsr RelativeEnemyPosition ;get relative coordinates
        relativeEnemyPosition()
        //> jsr FlagpoleGfxHandler    ;draw flagpole flag and floatey number
        flagpoleGfxHandler(FlagpoleScoreDigits[FlagpoleScore])
    }
    //> ExitFlagP: rts
    return
}

// Decompiled from Setup_Vine
fun setupVine(X: Int, Y: Int) {
    //> Setup_Vine:
    //> lda #VineObject          ;load identifier for vine object
    //> sta Enemy_ID,x           ;store in buffer
    Enemy_ID[X] = VineObject
    //> lda #$01
    //> sta Enemy_Flag,x         ;set flag for enemy object buffer
    Enemy_Flag[X] = 0x01
    //> lda Block_PageLoc,y
    //> sta Enemy_PageLoc,x      ;copy page location from previous object
    Enemy_PageLoc[X] = Block_PageLoc[Y]
    //> lda Block_X_Position,y
    //> sta Enemy_X_Position,x   ;copy horizontal coordinate from previous object
    Enemy_X_Position[X] = Block_X_Position[Y]
    //> lda Block_Y_Position,y
    //> sta Enemy_Y_Position,x   ;copy vertical coordinate from previous object
    Enemy_Y_Position[X] = Block_Y_Position[Y]
    //> ldy VineFlagOffset       ;load vine flag/offset to next available vine slot
    //> bne NextVO               ;if set at all, don't bother to store vertical
    if (zeroFlag) {
        //> sta VineStart_Y_Position ;otherwise store vertical coordinate here
        VineStart_Y_Position = Block_Y_Position[Y]
    }
    //> NextVO: txa                      ;store object offset to next available vine slot
    //> sta VineObjOffset,y      ;using vine flag as offset
    VineObjOffset[VineFlagOffset] = X
    //> inc VineFlagOffset       ;increment vine flag offset
    VineFlagOffset = (VineFlagOffset + 1) and 0xFF
    //> lda #Sfx_GrowVine
    //> sta Square2SoundQueue    ;load vine grow sound
    Square2SoundQueue = Sfx_GrowVine
    //> rts
    return
}

// Decompiled from ProcessCannons
fun processCannons() {
    var X: Int = 0
    //> ProcessCannons:
    //> lda AreaType                ;get area type
    //> beq ExCannon                ;if water type area, branch to leave
    if (!zeroFlag) {
        //> ldx #$02
        //> ThreeSChk: stx ObjectOffset            ;start at third enemy slot
        ObjectOffset = 0x02
        //> lda Enemy_Flag,x            ;check enemy buffer flag
        //> bne Chk_BB                  ;if set, branch to check enemy
        if (zeroFlag) {
            //> lda PseudoRandomBitReg+1,x  ;otherwise get part of LSFR
            //> ldy SecondaryHardMode       ;get secondary hard mode flag, use as offset
            //> and CannonBitmasks,y        ;mask out bits of LSFR as decided by flag
            //> cmp #$06                    ;check to see if lower nybble is above certain value
            //> bcs Chk_BB                  ;if so, branch to check enemy
            if (!(PseudoRandomBitReg[X] and CannonBitmasks[SecondaryHardMode] >= 0x06)) {
                //> tay                         ;transfer masked contents of LSFR to Y as pseudorandom offset
                //> lda Cannon_PageLoc,y        ;get page location
                //> beq Chk_BB                  ;if not set or on page 0, branch to check enemy
                if (!zeroFlag) {
                    //> lda Cannon_Timer,y          ;get cannon timer
                    //> beq FireCannon              ;if expired, branch to fire cannon
                    if (!zeroFlag) {
                        //> sbc #$00                    ;otherwise subtract borrow (note carry will always be clear here)
                        //> sta Cannon_Timer,y          ;to count timer down
                        Cannon_Timer[PseudoRandomBitReg[0x02] and CannonBitmasks[SecondaryHardMode]] = (Cannon_Timer[PseudoRandomBitReg[0x02] and CannonBitmasks[SecondaryHardMode]] - 0x00 - (1 - PseudoRandomBitReg[0x02] and CannonBitmasks[SecondaryHardMode] >= 0x06)) and 0xFF
                        //> jmp Chk_BB                  ;then jump ahead to check enemy
                    } else {
                        //> FireCannon:
                        //> lda TimerControl           ;if master timer control set,
                        //> bne Chk_BB                 ;branch to check enemy
                        if (zeroFlag) {
                            //> lda #$0e                   ;otherwise we start creating one
                            //> sta Cannon_Timer,y         ;first, reset cannon timer
                            Cannon_Timer[PseudoRandomBitReg[0x02] and CannonBitmasks[SecondaryHardMode]] = 0x0E
                            //> lda Cannon_PageLoc,y       ;get page location of cannon
                            //> sta Enemy_PageLoc,x        ;save as page location of bullet bill
                            Enemy_PageLoc[0x02] = Cannon_PageLoc[PseudoRandomBitReg[0x02] and CannonBitmasks[SecondaryHardMode]]
                            //> lda Cannon_X_Position,y    ;get horizontal coordinate of cannon
                            //> sta Enemy_X_Position,x     ;save as horizontal coordinate of bullet bill
                            Enemy_X_Position[0x02] = Cannon_X_Position[PseudoRandomBitReg[0x02] and CannonBitmasks[SecondaryHardMode]]
                            //> lda Cannon_Y_Position,y    ;get vertical coordinate of cannon
                            //> sec
                            //> sbc #$08                   ;subtract eight pixels (because enemies are 24 pixels tall)
                            //> sta Enemy_Y_Position,x     ;save as vertical coordinate of bullet bill
                            Enemy_Y_Position[0x02] = (Cannon_Y_Position[PseudoRandomBitReg[0x02] and CannonBitmasks[SecondaryHardMode]] - 0x08 - (1 - 1)) and 0xFF
                            //> lda #$01
                            //> sta Enemy_Y_HighPos,x      ;set vertical high byte of bullet bill
                            Enemy_Y_HighPos[0x02] = 0x01
                            //> sta Enemy_Flag,x           ;set buffer flag
                            Enemy_Flag[0x02] = 0x01
                            //> lsr                        ;shift right once to init A
                            //> sta Enemy_State,x          ;then initialize enemy's state
                            Enemy_State[0x02] = 0x01 shr 1
                            //> lda #$09
                            //> sta Enemy_BoundBoxCtrl,x   ;set bounding box size control for bullet bill
                            Enemy_BoundBoxCtrl[0x02] = 0x09
                            //> lda #BulletBill_CannonVar
                            //> sta Enemy_ID,x             ;load identifier for bullet bill (cannon variant)
                            Enemy_ID[0x02] = BulletBill_CannonVar
                            //> jmp Next3Slt               ;move onto next slot
                        }
                    }
                }
            }
        } else {
            //> Chk_BB:   lda Enemy_ID,x             ;check enemy identifier for bullet bill (cannon variant)
            //> cmp #BulletBill_CannonVar
            //> bne Next3Slt               ;if not found, branch to get next slot
            if (Enemy_ID[X] - BulletBill_CannonVar == 0) {
                //> jsr OffscreenBoundsCheck   ;otherwise, check to see if it went offscreen
                offscreenBoundsCheck(0x02)
                //> lda Enemy_Flag,x           ;check enemy buffer flag
                //> beq Next3Slt               ;if not set, branch to get next slot
                if (!zeroFlag) {
                    //> jsr GetEnemyOffscreenBits  ;otherwise, get offscreen information
                    getEnemyOffscreenBits(0x02)
                    //> jsr BulletBillHandler      ;then do sub to handle bullet bill
                    bulletBillHandler(0x02)
                }
            }
        }
        //> Next3Slt: dex                        ;move onto next slot
        //> bpl ThreeSChk              ;do this until first three slots are checked
    }
    //> ExCannon: rts                        ;then leave
    return
}

// Decompiled from BulletBillHandler
fun bulletBillHandler(X: Int) {
    //> BulletBillHandler:
    //> lda TimerControl          ;if master timer control set,
    //> bne RunBBSubs             ;branch to run subroutines except movement sub
    if (zeroFlag) {
        //> lda Enemy_State,x
        //> bne ChkDSte               ;if bullet bill's state set, branch to check defeated state
        if (zeroFlag) {
            //> lda Enemy_OffscreenBits   ;otherwise load offscreen bits
            //> and #%00001100            ;mask out bits
            //> cmp #%00001100            ;check to see if all bits are set
            //> beq KillBB                ;if so, branch to kill this object
            if (!(Enemy_OffscreenBits and 0x0C - 0x0C == 0)) {
                //> ldy #$01                  ;set to move right by default
                //> jsr PlayerEnemyDiff       ;get horizontal difference between player and bullet bill
                playerEnemyDiff(X)
                //> bmi SetupBB               ;if enemy to the left of player, branch
                if (!negativeFlag) {
                    //> iny                       ;otherwise increment to move left
                }
                //> SetupBB:   sty Enemy_MovingDir,x     ;set bullet bill's moving direction
                Enemy_MovingDir[X] = (0x01 + 1) and 0xFF
                //> dey                       ;decrement to use as offset
                //> lda BulletBillXSpdData,y  ;get horizontal speed based on moving direction
                //> sta Enemy_X_Speed,x       ;and store it
                Enemy_X_Speed[X] = BulletBillXSpdData[((0x01 + 1) and 0xFF - 1) and 0xFF]
                //> lda $00                   ;get horizontal difference
                //> adc #$28                  ;add 40 pixels
                //> cmp #$50                  ;if less than a certain amount, player is too close
                //> bcc KillBB                ;to cannon either on left or right side, thus branch
                if ((zp_00 + 0x28 + 0) and 0xFF >= 0x50) {
                    //> lda #$01
                    //> sta Enemy_State,x         ;otherwise set bullet bill's state
                    Enemy_State[X] = 0x01
                    //> lda #$0a
                    //> sta EnemyFrameTimer,x     ;set enemy frame timer
                    EnemyFrameTimer[X] = 0x0A
                    //> lda #Sfx_Blast
                    //> sta Square2SoundQueue     ;play fireworks/gunfire sound
                    Square2SoundQueue = Sfx_Blast
                    //> ChkDSte:   lda Enemy_State,x         ;check enemy state for d5 set
                    //> and #%00100000
                    //> beq BBFly                 ;if not set, skip to move horizontally
                    if (!zeroFlag) {
                        //> jsr MoveD_EnemyVertically ;otherwise do sub to move bullet bill vertically
                        movedEnemyvertically(X)
                    }
                    //> BBFly:     jsr MoveEnemyHorizontally ;do sub to move bullet bill horizontally
                    moveEnemyHorizontally(X)
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
            }
        }
        if (!zeroFlag) {
        }
    }
    //> KillBB:    jsr EraseEnemyObject      ;kill bullet bill and leave
    eraseEnemyObject(X)
    //> rts
    return
}

// Decompiled from SpawnHammerObj
fun spawnHammerObj() {
    //> SpawnHammerObj:
    //> lda PseudoRandomBitReg+1 ;get pseudorandom bits from
    //> and #%00000111           ;second part of LSFR
    //> bne SetMOfs              ;if any bits are set, branch and use as offset
    if (zeroFlag) {
        //> lda PseudoRandomBitReg+1
        //> and #%00001000           ;get d3 from same part of LSFR
    }
    //> SetMOfs:  tay                      ;use either d3 or d2-d0 for offset here
    //> lda Misc_State,y         ;if any values loaded in
    //> bne NoHammer             ;$2a-$32 where offset is then leave with carry clear
    if (zeroFlag) {
        //> ldx HammerEnemyOfsData,y ;get offset of enemy slot to check using Y as offset
        //> lda Enemy_Flag,x         ;check enemy buffer flag at offset
        //> bne NoHammer             ;if buffer flag set, branch to leave with carry clear
        if (zeroFlag) {
            //> ldx ObjectOffset         ;get original enemy object offset
            //> txa
            //> sta HammerEnemyOffset,y  ;save here
            HammerEnemyOffset[PseudoRandomBitReg and 0x08] = ObjectOffset
            //> lda #$90
            //> sta Misc_State,y         ;save hammer's state here
            Misc_State[PseudoRandomBitReg and 0x08] = 0x90
            //> lda #$07
            //> sta Misc_BoundBoxCtrl,y  ;set something else entirely, here
            Misc_BoundBoxCtrl[PseudoRandomBitReg and 0x08] = 0x07
            //> sec                      ;return with carry set
            //> rts
            return
        }
    }
    //> NoHammer: ldx ObjectOffset         ;get original enemy object offset
    //> clc                      ;return with carry clear
    //> rts
    return
}

// Decompiled from ProcHammerObj
fun procHammerObj(X: Int) {
    var A: Int = 0
    //> ProcHammerObj:
    //> lda TimerControl           ;if master timer control set
    //> bne RunHSubs               ;skip all of this code and go to last subs at the end
    if (zeroFlag) {
        //> lda Misc_State,x           ;otherwise get hammer's state
        //> and #%01111111             ;mask out d7
        //> ldy HammerEnemyOffset,x    ;get enemy object offset that spawned this hammer
        //> cmp #$02                   ;check hammer's state
        //> beq SetHSpd                ;if currently at 2, branch
        if (!(Misc_State[X] and 0x7F - 0x02 == 0)) {
            //> bcs SetHPos                ;if greater than 2, branch elsewhere
            if (!(A >= 0x02)) {
                //> txa
                //> clc                        ;add 13 bytes to use
                //> adc #$0d                   ;proper misc object
                //> tax                        ;return offset to X
                //> lda #$10
                //> sta $00                    ;set downward movement force
                zp_00 = 0x10
                //> lda #$0f
                //> sta $01                    ;set upward movement force (not used)
                zp_01 = 0x0F
                //> lda #$04
                //> sta $02                    ;set maximum vertical speed
                zp_02 = 0x04
                //> lda #$00                   ;set A to impose gravity on hammer
                //> jsr ImposeGravity          ;do sub to impose gravity on hammer and move vertically
                imposeGravity(0x00, (X + 0x0D + 0) and 0xFF)
                //> jsr MoveObjectHorizontally ;do sub to move it horizontally
                moveObjectHorizontally((X + 0x0D + 0) and 0xFF)
                //> ldx ObjectOffset           ;get original misc object offset
                //> jmp RunAllH                ;branch to essential subroutines
                //> SetHSpd:  lda #$fe
                //> sta Misc_Y_Speed,x         ;set hammer's vertical speed
                Misc_Y_Speed[ObjectOffset] = 0xFE
                //> lda Enemy_State,y          ;get enemy object state
                //> and #%11110111             ;mask out d3
                //> sta Enemy_State,y          ;store new state
                Enemy_State[HammerEnemyOffset[X]] = Enemy_State[HammerEnemyOffset[X]] and 0xF7
                //> ldx Enemy_MovingDir,y      ;get enemy's moving direction
                //> dex                        ;decrement to use as offset
                //> lda HammerXSpdData,x       ;get proper speed to use based on moving direction
                //> ldx ObjectOffset           ;reobtain hammer's buffer offset
                //> sta Misc_X_Speed,x         ;set hammer's horizontal speed
                Misc_X_Speed[ObjectOffset] = HammerXSpdData[(Enemy_MovingDir[HammerEnemyOffset[X]] - 1) and 0xFF]
            }
        } else {
            //> SetHPos:  dec Misc_State,x           ;decrement hammer's state
            Misc_State[ObjectOffset] = (Misc_State[ObjectOffset] - 1) and 0xFF
            //> lda Enemy_X_Position,y     ;get enemy's horizontal position
            //> clc
            //> adc #$02                   ;set position 2 pixels to the right
            //> sta Misc_X_Position,x      ;store as hammer's horizontal position
            Misc_X_Position[ObjectOffset] = (Enemy_X_Position[HammerEnemyOffset[X]] + 0x02 + 0) and 0xFF
            //> lda Enemy_PageLoc,y        ;get enemy's page location
            //> adc #$00                   ;add carry
            //> sta Misc_PageLoc,x         ;store as hammer's page location
            Misc_PageLoc[ObjectOffset] = (Enemy_PageLoc[HammerEnemyOffset[X]] + 0x00 + 0) and 0xFF
            //> lda Enemy_Y_Position,y     ;get enemy's vertical position
            //> sec
            //> sbc #$0a                   ;move position 10 pixels upward
            //> sta Misc_Y_Position,x      ;store as hammer's vertical position
            Misc_Y_Position[ObjectOffset] = (Enemy_Y_Position[HammerEnemyOffset[X]] - 0x0A - (1 - 1)) and 0xFF
            //> lda #$01
            //> sta Misc_Y_HighPos,x       ;set hammer's vertical high byte
            Misc_Y_HighPos[ObjectOffset] = 0x01
            //> bne RunHSubs               ;unconditional branch to skip first routine
            if (zeroFlag) {
                //> RunAllH:  jsr PlayerHammerCollision  ;handle collisions
                playerHammerCollision(ObjectOffset)
            }
        }
    }
    //> RunHSubs: jsr GetMiscOffscreenBits   ;get offscreen information
    getMiscOffscreenBits()
    //> jsr RelativeMiscPosition   ;get relative coordinates
    relativeMiscPosition()
    //> jsr GetMiscBoundBox        ;get bounding box coordinates
    getMiscBoundBox(ObjectOffset)
    //> jsr DrawHammer             ;draw the hammer
    drawHammer(ObjectOffset)
    //> rts                        ;and we are done here
    return
}

// Decompiled from SetupJumpCoin
fun setupJumpCoin(X: Int, Y: Int) {
    //> SetupJumpCoin:
    //> jsr FindEmptyMiscSlot  ;set offset for empty or last misc object buffer slot
    findEmptyMiscSlot()
    //> lda Block_PageLoc2,x   ;get page location saved earlier
    //> sta Misc_PageLoc,y     ;and save as page location for misc object
    Misc_PageLoc[Y] = Block_PageLoc2[X]
    //> lda $06                ;get low byte of block buffer offset
    //> asl
    //> asl                    ;multiply by 16 to use lower nybble
    //> asl
    //> asl
    //> ora #$05               ;add five pixels
    //> sta Misc_X_Position,y  ;save as horizontal coordinate for misc object
    Misc_X_Position[Y] = ((((zp_06 shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF or 0x05
    //> lda $02                ;get vertical high nybble offset from earlier
    //> adc #$20               ;add 32 pixels for the status bar
    //> sta Misc_Y_Position,y  ;store as vertical coordinate
    Misc_Y_Position[Y] = (zp_02 + 0x20 + 0) and 0xFF
    //> JCoinC: lda #$fb
    //> sta Misc_Y_Speed,y     ;set vertical speed
    Misc_Y_Speed[Y] = 0xFB
    //> lda #$01
    //> sta Misc_Y_HighPos,y   ;set vertical high byte
    Misc_Y_HighPos[Y] = 0x01
    //> sta Misc_State,y       ;set state for misc object
    Misc_State[Y] = 0x01
    //> sta Square2SoundQueue  ;load coin grab sound
    Square2SoundQueue = 0x01
    //> stx ObjectOffset       ;store current control bit as misc object offset
    ObjectOffset = X
    //> jsr GiveOneCoin        ;update coin tally on the screen and coin amount variable
    giveOneCoin()
    //> inc CoinTallyFor1Ups   ;increment coin tally used to activate 1-up block flag
    CoinTallyFor1Ups = (CoinTallyFor1Ups + 1) and 0xFF
    //> rts
    return
}

// Decompiled from FindEmptyMiscSlot
fun findEmptyMiscSlot() {
    var Y: Int = 0
    //> FindEmptyMiscSlot:
    //> ldy #$08                ;start at end of misc objects buffer
    while ((Y - 1) and 0xFF == 0x05) {
        do {
            //> FMiscLoop: lda Misc_State,y        ;get misc object state
            //> beq UseMiscS            ;branch if none found to use current offset
            //> dey                     ;decrement offset
            //> cpy #$05                ;do this for three slots
            //> bne FMiscLoop           ;do this until all slots are checked
        } while (!((Y - 1) and 0xFF == 0x05))
    }
    //> ldy #$08                ;if no empty slots found, use last slot
    //> UseMiscS:  sty JumpCoinMiscOffset  ;store offset of misc object buffer here (residual)
    JumpCoinMiscOffset = 0x08
    //> rts
    return
}

// Decompiled from MiscObjectsCore
fun miscObjectsCore() {
    var X: Int = 0
    //> MiscObjectsCore:
    //> ldx #$08          ;set at end of misc object buffer
    //> MiscLoop: stx ObjectOffset  ;store misc object offset here
    ObjectOffset = 0x08
    //> lda Misc_State,x  ;check misc object state
    //> beq MiscLoopBack  ;branch to check next slot
    if (!zeroFlag) {
        //> asl               ;otherwise shift d7 into carry
        //> bcc ProcJumpCoin  ;if d7 not set, jumping coin, thus skip to rest of code here
        if (carryFlag) {
            //> jsr ProcHammerObj ;otherwise go to process hammer,
            procHammerObj(0x08)
            //> jmp MiscLoopBack  ;then check next slot
        } else {
            //> ProcJumpCoin:
            //> ldy Misc_State,x          ;check misc object state
            //> dey                       ;decrement to see if it's set to 1
            //> beq JCoinRun              ;if so, branch to handle jumping coin
            if (!zeroFlag) {
                //> inc Misc_State,x          ;otherwise increment state to either start off or as timer
                Misc_State[0x08] = (Misc_State[0x08] + 1) and 0xFF
                //> lda Misc_X_Position,x     ;get horizontal coordinate for misc object
                //> clc                       ;whether its jumping coin (state 0 only) or floatey number
                //> adc ScrollAmount          ;add current scroll speed
                //> sta Misc_X_Position,x     ;store as new horizontal coordinate
                Misc_X_Position[0x08] = (Misc_X_Position[0x08] + ScrollAmount + 0) and 0xFF
                //> lda Misc_PageLoc,x        ;get page location
                //> adc #$00                  ;add carry
                //> sta Misc_PageLoc,x        ;store as new page location
                Misc_PageLoc[0x08] = (Misc_PageLoc[0x08] + 0x00 + 0) and 0xFF
                //> lda Misc_State,x
                //> cmp #$30                  ;check state of object for preset value
                //> bne RunJCSubs             ;if not yet reached, branch to subroutines
                if (Misc_State[X] - 0x30 == 0) {
                    //> lda #$00
                    //> sta Misc_State,x          ;otherwise nullify object state
                    Misc_State[0x08] = 0x00
                    //> jmp MiscLoopBack          ;and move onto next slot
                    //> JCoinRun:  txa
                    //> clc                       ;add 13 bytes to offset for next subroutine
                    //> adc #$0d
                    //> tax
                    //> lda #$50                  ;set downward movement amount
                    //> sta $00
                    zp_00 = 0x50
                    //> lda #$06                  ;set maximum vertical speed
                    //> sta $02
                    zp_02 = 0x06
                    //> lsr                       ;divide by 2 and set
                    //> sta $01                   ;as upward movement amount (apparently residual)
                    zp_01 = 0x06 shr 1
                    //> lda #$00                  ;set A to impose gravity on jumping coin
                    //> jsr ImposeGravity         ;do sub to move coin vertically and impose gravity on it
                    imposeGravity(0x00, (0x08 + 0x0D + 0) and 0xFF)
                    //> ldx ObjectOffset          ;get original misc object offset
                    //> lda Misc_Y_Speed,x        ;check vertical speed
                    //> cmp #$05
                    //> bne RunJCSubs             ;if not moving downward fast enough, keep state as-is
                    if (Misc_Y_Speed[ObjectOffset] - 0x05 == 0) {
                        //> inc Misc_State,x          ;otherwise increment state to change to floatey number
                        Misc_State[ObjectOffset] = (Misc_State[ObjectOffset] + 1) and 0xFF
                    }
                }
            } else {
                if (Misc_Y_Speed[ObjectOffset] - 0x05 == 0) {
                }
                //> RunJCSubs: jsr RelativeMiscPosition  ;get relative coordinates
                relativeMiscPosition()
                //> jsr GetMiscOffscreenBits  ;get offscreen information
                getMiscOffscreenBits()
                //> jsr GetMiscBoundBox       ;get bounding box coordinates (why?)
                getMiscBoundBox(ObjectOffset)
                //> jsr JCoinGfxHandler       ;draw the coin or floatey number
                jCoinGfxHandler(ObjectOffset)
            }
        }
    }
    do {
        //> MiscLoopBack:
        //> dex                       ;decrement misc object offset
        //> bpl MiscLoop              ;loop back until all misc objects handled
    } while (!negativeFlag)
    //> rts                       ;then leave
    return
}

// Decompiled from GiveOneCoin
fun giveOneCoin() {
    //> GiveOneCoin:
    //> lda #$01               ;set digit modifier to add 1 coin
    //> sta DigitModifier+5    ;to the current player's coin tally
    DigitModifier = 0x01
    //> ldx CurrentPlayer      ;get current player on the screen
    //> ldy CoinTallyOffsets,x ;get offset for player's coin tally
    //> jsr DigitsMathRoutine  ;update the coin tally
    digitsMathRoutine(CoinTallyOffsets[CurrentPlayer])
    //> inc CoinTally          ;increment onscreen player's coin amount
    CoinTally = (CoinTally + 1) and 0xFF
    //> lda CoinTally
    //> cmp #100               ;does player have 100 coins yet?
    //> bne CoinPoints         ;if not, skip all of this
    if (CoinTally - 0x64 == 0) {
        //> lda #$00
        //> sta CoinTally          ;otherwise, reinitialize coin amount
        CoinTally = 0x00
        //> inc NumberofLives      ;give the player an extra life
        NumberofLives = (NumberofLives + 1) and 0xFF
        //> lda #Sfx_ExtraLife
        //> sta Square2SoundQueue  ;play 1-up sound
        Square2SoundQueue = Sfx_ExtraLife
    }
    //> CoinPoints:
    //> lda #$02               ;set digit modifier to award
    //> sta DigitModifier+4    ;200 points to the player
    DigitModifier = 0x02
}

// Decompiled from AddToScore
fun addToScore() {
    //> AddToScore:
    //> ldx CurrentPlayer      ;get current player
    //> ldy ScoreOffsets,x     ;get offset for player's score
    //> jsr DigitsMathRoutine  ;update the score internally with value in digit modifier
    digitsMathRoutine(ScoreOffsets[CurrentPlayer])
}

// Decompiled from GetSBNybbles
fun getSBNybbles() {
    //> GetSBNybbles:
    //> ldy CurrentPlayer      ;get current player
    //> lda StatusBarNybbles,y ;get nybbles based on player, use to update score and coins
}

// Decompiled from UpdateNumber
fun updateNumber() {
    var A: Int = 0
    //> UpdateNumber:
    //> jsr PrintStatusBarNumbers ;print status bar numbers based on nybbles, whatever they be
    printStatusBarNumbers(A)
    //> ldy VRAM_Buffer1_Offset
    //> lda VRAM_Buffer1-6,y      ;check highest digit of score
    //> bne NoZSup                ;if zero, overwrite with space tile for zero suppression
    if (zeroFlag) {
        //> lda #$24
        //> sta VRAM_Buffer1-6,y
        VRAM_Buffer1[VRAM_Buffer1_Offset] = 0x24
    }
    //> NoZSup: ldx ObjectOffset          ;get enemy object buffer offset
    //> rts
    return
}

// Decompiled from PlayerHeadCollision
fun playerHeadCollision(A: Int) {
    var X: Int = 0
    var Y: Int = 0
    //> PlayerHeadCollision:
    //> pha                      ;store metatile number to stack
    //> lda #$11                 ;load unbreakable block object state by default
    //> ldx SprDataOffset_Ctrl   ;load offset control bit here
    //> ldy PlayerSize           ;check player's size
    //> bne DBlockSte            ;if small, branch
    if (zeroFlag) {
        //> lda #$12                 ;otherwise load breakable block object state
    }
    //> DBlockSte: sta Block_State,x        ;store into block object buffer
    Block_State[SprDataOffset_Ctrl] = 0x12
    //> jsr DestroyBlockMetatile ;store blank metatile in vram buffer to write to name table
    destroyBlockMetatile()
    //> ldx SprDataOffset_Ctrl   ;load offset control bit
    //> lda $02                  ;get vertical high nybble offset used in block buffer routine
    //> sta Block_Orig_YPos,x    ;set as vertical coordinate for block object
    Block_Orig_YPos[SprDataOffset_Ctrl] = zp_02
    //> tay
    //> lda $06                  ;get low byte of block buffer address used in same routine
    //> sta Block_BBuf_Low,x     ;save as offset here to be used later
    Block_BBuf_Low[SprDataOffset_Ctrl] = zp_06
    //> lda ($06),y              ;get contents of block buffer at old address at $06, $07
    //> jsr BlockBumpedChk       ;do a sub to check which block player bumped head on
    blockBumpedChk(TODO)
    //> sta $00                  ;store metatile here
    zp_00 = TODO
    //> ldy PlayerSize           ;check player's size
    //> bne ChkBrick             ;if small, use metatile itself as contents of A
    if (zeroFlag) {
        //> tya                      ;otherwise init A (note: big = 0)
    }
    //> ChkBrick:  bcc PutMTileB            ;if no match was found in previous sub, skip ahead
    if (carryFlag) {
        //> ldy #$11                 ;otherwise load unbreakable state into block object buffer
        //> sty Block_State,x        ;note this applies to both player sizes
        Block_State[SprDataOffset_Ctrl] = 0x11
        //> lda #$c4                 ;load empty block metatile into A for now
        //> ldy $00                  ;get metatile from before
        //> cpy #$58                 ;is it brick with coins (with line)?
        //> beq StartBTmr            ;if so, branch
        if (!(zp_00 == 0x58)) {
            //> cpy #$5d                 ;is it brick with coins (without line)?
            //> bne PutMTileB            ;if not, branch ahead to store empty block metatile
            if (Y == 0x5D) {
                //> StartBTmr: lda BrickCoinTimerFlag   ;check brick coin timer flag
                //> bne ContBTmr             ;if set, timer expired or counting down, thus branch
                if (zeroFlag) {
                    //> lda #$0b
                    //> sta BrickCoinTimer       ;if not set, set brick coin timer
                    BrickCoinTimer = 0x0B
                    //> inc BrickCoinTimerFlag   ;and set flag linked to it
                    BrickCoinTimerFlag = (BrickCoinTimerFlag + 1) and 0xFF
                }
                //> ContBTmr:  lda BrickCoinTimer       ;check brick coin timer
                //> bne PutOldMT             ;if not yet expired, branch to use current metatile
                if (zeroFlag) {
                    //> ldy #$c4                 ;otherwise use empty block metatile
                }
                //> PutOldMT:  tya                      ;put metatile into A
            }
        }
        if (zeroFlag) {
        }
        if (zeroFlag) {
        }
    }
    //> PutMTileB: sta Block_Metatile,x     ;store whatever metatile be appropriate here
    Block_Metatile[SprDataOffset_Ctrl] = 0xC4
    //> jsr InitBlock_XY_Pos     ;get block object horizontal coordinates saved
    initblockXyPos(SprDataOffset_Ctrl)
    //> ldy $02                  ;get vertical high nybble offset
    //> lda #$23
    //> sta ($06),y              ;write blank metatile $23 to block buffer
    TODO = 0x23
    //> lda #$10
    //> sta BlockBounceTimer     ;set block bounce timer
    BlockBounceTimer = 0x10
    //> pla                      ;pull original metatile from stack
    //> sta $05                  ;and save here
    zp_05 = 0x10
    //> ldy #$00                 ;set default offset
    //> lda CrouchingFlag        ;is player crouching?
    //> bne SmallBP              ;if so, branch to increment offset
    if (zeroFlag) {
        //> lda PlayerSize           ;is player big?
        //> beq BigBP                ;if so, branch to use default offset
        if (!zeroFlag) {
            //> SmallBP:   iny                      ;increment for small or big and crouching
        }
    }
    //> BigBP:     lda Player_Y_Position    ;get player's vertical coordinate
    //> clc
    //> adc BlockYPosAdderData,y ;add value determined by size
    //> and #$f0                 ;mask out low nybble to get 16-pixel correspondence
    //> sta Block_Y_Position,x   ;save as vertical coordinate for block object
    Block_Y_Position[SprDataOffset_Ctrl] = (Player_Y_Position + BlockYPosAdderData[(0x00 + 1) and 0xFF] + 0) and 0xFF and 0xF0
    //> ldy Block_State,x        ;get block object state
    //> cpy #$11
    //> beq Unbreak              ;if set to value loaded for unbreakable, branch
    if (!(Block_State[X] == 0x11)) {
        //> jsr BrickShatter         ;execute code for breakable brick
        brickShatter(SprDataOffset_Ctrl)
        //> jmp InvOBit              ;skip subroutine to do last part of code here
    } else {
        //> Unbreak:   jsr BumpBlock            ;execute code for unbreakable brick or question block
        bumpBlock(SprDataOffset_Ctrl, Block_State[SprDataOffset_Ctrl])
    }
    //> InvOBit:   lda SprDataOffset_Ctrl   ;invert control bit used by block objects
    //> eor #$01                 ;and floatey numbers
    //> sta SprDataOffset_Ctrl
    SprDataOffset_Ctrl = SprDataOffset_Ctrl xor 0x01
    //> rts                      ;leave!
    return
}

// Decompiled from InitBlock_XY_Pos
fun initblockXyPos(X: Int) {
    //> InitBlock_XY_Pos:
    //> lda Player_X_Position   ;get player's horizontal coordinate
    //> clc
    //> adc #$08                ;add eight pixels
    //> and #$f0                ;mask out low nybble to give 16-pixel correspondence
    //> sta Block_X_Position,x  ;save as horizontal coordinate for block object
    Block_X_Position[X] = (Player_X_Position + 0x08 + 0) and 0xFF and 0xF0
    //> lda Player_PageLoc
    //> adc #$00                ;add carry to page location of player
    //> sta Block_PageLoc,x     ;save as page location of block object
    Block_PageLoc[X] = (Player_PageLoc + 0x00 + 0) and 0xFF
    //> sta Block_PageLoc2,x    ;save elsewhere to be used later
    Block_PageLoc2[X] = (Player_PageLoc + 0x00 + 0) and 0xFF
    //> lda Player_Y_HighPos
    //> sta Block_Y_HighPos,x   ;save vertical high byte of player into
    Block_Y_HighPos[X] = Player_Y_HighPos
    //> rts                     ;vertical high byte of block object and leave
    return
}

// Decompiled from BumpBlock
fun bumpBlock(X: Int, Y: Int) {
    //> SetupPowerUp:
    //> lda #PowerUpObject        ;load power-up identifier into
    //> sta Enemy_ID+5            ;special use slot of enemy object buffer
    Enemy_ID = PowerUpObject
    //> lda Block_PageLoc,x       ;store page location of block object
    //> sta Enemy_PageLoc+5       ;as page location of power-up object
    Enemy_PageLoc = Block_PageLoc[X]
    //> lda Block_X_Position,x    ;store horizontal coordinate of block object
    //> sta Enemy_X_Position+5    ;as horizontal coordinate of power-up object
    Enemy_X_Position = Block_X_Position[X]
    //> lda #$01
    //> sta Enemy_Y_HighPos+5     ;set vertical high byte of power-up object
    Enemy_Y_HighPos = 0x01
    //> lda Block_Y_Position,x    ;get vertical coordinate of block object
    //> sec
    //> sbc #$08                  ;subtract 8 pixels
    //> sta Enemy_Y_Position+5    ;and use as vertical coordinate of power-up object
    Enemy_Y_Position = (Block_Y_Position[X] - 0x08 - (1 - 1)) and 0xFF
    //> PwrUpJmp:  lda #$01                  ;this is a residual jump point in enemy object jump table
    //> sta Enemy_State+5         ;set power-up object's state
    Enemy_State = 0x01
    //> sta Enemy_Flag+5          ;set buffer flag
    Enemy_Flag = 0x01
    //> lda #$03
    //> sta Enemy_BoundBoxCtrl+5  ;set bounding box size control for power-up object
    Enemy_BoundBoxCtrl = 0x03
    //> lda PowerUpType
    //> cmp #$02                  ;check currently loaded power-up type
    //> bcs PutBehind             ;if star or 1-up, branch ahead
    if (!(PowerUpType >= 0x02)) {
        //> lda PlayerStatus          ;otherwise check player's current status
        //> cmp #$02
        //> bcc StrType               ;if player not fiery, use status as power-up type
        if (PlayerStatus >= 0x02) {
            //> lsr                       ;otherwise shift right to force fire flower type
        }
        //> StrType:   sta PowerUpType           ;store type here
        PowerUpType = PlayerStatus shr 1
    }
    //> PutBehind: lda #%00100000
    //> sta Enemy_SprAttrib+5     ;set background priority bit
    Enemy_SprAttrib = 0x20
    //> lda #Sfx_GrowPowerUp
    //> sta Square2SoundQueue     ;load power-up reveal sound and leave
    Square2SoundQueue = Sfx_GrowPowerUp
    //> rts
    return
}

// Decompiled from BlockBumpedChk
fun blockBumpedChk(A: Int) {
    //> BlockBumpedChk:
    //> ldy #$0d                    ;start at end of metatile data
    while (negativeFlag) {
        do {
            //> BumpChkLoop: cmp BrickQBlockMetatiles,y  ;check to see if current metatile matches
            //> beq MatchBump               ;metatile found in block buffer, branch if so
            //> dey                         ;otherwise move onto next metatile
            //> bpl BumpChkLoop             ;do this until all metatiles are checked
        } while (!negativeFlag)
    }
    //> clc                         ;if none match, return with carry clear
    //> MatchBump:   rts                         ;note carry is set if found match
    return
}

// Decompiled from BrickShatter
fun brickShatter(X: Int) {
    //> BrickShatter:
    //> jsr CheckTopOfBlock    ;check to see if there's a coin directly above this block
    checkTopOfBlock()
    //> lda #Sfx_BrickShatter
    //> sta Block_RepFlag,x    ;set flag for block object to immediately replace metatile
    Block_RepFlag[X] = Sfx_BrickShatter
    //> sta NoiseSoundQueue    ;load brick shatter sound
    NoiseSoundQueue = Sfx_BrickShatter
    //> jsr SpawnBrickChunks   ;create brick chunk objects
    spawnBrickChunks(X)
    //> lda #$fe
    //> sta Player_Y_Speed     ;set vertical speed for player
    Player_Y_Speed = 0xFE
    //> lda #$05
    //> sta DigitModifier+5    ;set digit modifier to give player 50 points
    DigitModifier = 0x05
    //> jsr AddToScore         ;do sub to update the score
    addToScore()
    //> ldx SprDataOffset_Ctrl ;load control bit and leave
    //> rts
    return
}

// Decompiled from CheckTopOfBlock
fun checkTopOfBlock() {
    //> CheckTopOfBlock:
    //> ldx SprDataOffset_Ctrl  ;load control bit
    //> ldy $02                 ;get vertical high nybble offset used in block buffer
    //> beq TopEx               ;branch to leave if set to zero, because we're at the top
    if (!zeroFlag) {
        //> tya                     ;otherwise set to A
        //> sec
        //> sbc #$10                ;subtract $10 to move up one row in the block buffer
        //> sta $02                 ;store as new vertical high nybble offset
        zp_02 = (zp_02 - 0x10 - (1 - 1)) and 0xFF
        //> tay
        //> lda ($06),y             ;get contents of block buffer in same column, one row up
        //> cmp #$c2                ;is it a coin? (not underwater)
        //> bne TopEx               ;if not, branch to leave
        if (TODO - 0xC2 == 0) {
            //> lda #$00
            //> sta ($06),y             ;otherwise put blank metatile where coin was
            TODO = 0x00
            //> jsr RemoveCoin_Axe      ;write blank metatile to vram buffer
            removecoinAxe()
            //> ldx SprDataOffset_Ctrl  ;get control bit
            //> jsr SetupJumpCoin       ;create jumping coin object and update coin variables
            setupJumpCoin(SprDataOffset_Ctrl, (zp_02 - 0x10 - (1 - 1)) and 0xFF)
        }
    }
    //> TopEx: rts                     ;leave!
    return
}

// Decompiled from SpawnBrickChunks
fun spawnBrickChunks(X: Int) {
    //> SpawnBrickChunks:
    //> lda Block_X_Position,x     ;set horizontal coordinate of block object
    //> sta Block_Orig_XPos,x      ;as original horizontal coordinate here
    Block_Orig_XPos[X] = Block_X_Position[X]
    //> lda #$f0
    //> sta Block_X_Speed,x        ;set horizontal speed for brick chunk objects
    Block_X_Speed[X] = 0xF0
    //> sta Block_X_Speed+2,x
    Block_X_Speed[X] = 0xF0
    //> lda #$fa
    //> sta Block_Y_Speed,x        ;set vertical speed for one
    Block_Y_Speed[X] = 0xFA
    //> lda #$fc
    //> sta Block_Y_Speed+2,x      ;set lower vertical speed for the other
    Block_Y_Speed[X] = 0xFC
    //> lda #$00
    //> sta Block_Y_MoveForce,x    ;init fractional movement force for both
    Block_Y_MoveForce[X] = 0x00
    //> sta Block_Y_MoveForce+2,x
    Block_Y_MoveForce[X] = 0x00
    //> lda Block_PageLoc,x
    //> sta Block_PageLoc+2,x      ;copy page location
    Block_PageLoc[X] = Block_PageLoc[X]
    //> lda Block_X_Position,x
    //> sta Block_X_Position+2,x   ;copy horizontal coordinate
    Block_X_Position[X] = Block_X_Position[X]
    //> lda Block_Y_Position,x
    //> clc                        ;add 8 pixels to vertical coordinate
    //> adc #$08                   ;and save as vertical coordinate for one of them
    //> sta Block_Y_Position+2,x
    Block_Y_Position[X] = (Block_Y_Position[X] + 0x08 + 0) and 0xFF
    //> lda #$fa
    //> sta Block_Y_Speed,x        ;set vertical speed...again??? (redundant)
    Block_Y_Speed[X] = 0xFA
    //> rts
    return
}

// Decompiled from BlockObjectsCore
fun blockObjectsCore(X: Int) {
    //> BlockObjectsCore:
    //> lda Block_State,x           ;get state of block object
    //> beq UpdSte                  ;if not set, branch to leave
    if (!zeroFlag) {
        //> and #$0f                    ;mask out high nybble
        //> pha                         ;push to stack
        //> tay                         ;put in Y for now
        //> txa
        //> clc
        //> adc #$09                    ;add 9 bytes to offset (note two block objects are created
        //> tax                         ;when using brick chunks, but only one offset for both)
        //> dey                         ;decrement Y to check for solid block state
        //> beq BouncingBlockHandler    ;branch if found, otherwise continue for brick chunks
        if (!zeroFlag) {
            //> jsr ImposeGravityBlock      ;do sub to impose gravity on one block object object
            imposeGravityBlock()
            //> jsr MoveObjectHorizontally  ;do another sub to move horizontally
            moveObjectHorizontally((X + 0x09 + 0) and 0xFF)
            //> txa
            //> clc                         ;move onto next block object
            //> adc #$02
            //> tax
            //> jsr ImposeGravityBlock      ;do sub to impose gravity on other block object
            imposeGravityBlock()
            //> jsr MoveObjectHorizontally  ;do another sub to move horizontally
            moveObjectHorizontally(((X + 0x09 + 0) and 0xFF + 0x02 + 0) and 0xFF)
            //> ldx ObjectOffset            ;get block object offset used for both
            //> jsr RelativeBlockPosition   ;get relative coordinates
            relativeBlockPosition(ObjectOffset)
            //> jsr GetBlockOffscreenBits   ;get offscreen information
            getBlockOffscreenBits(ObjectOffset)
            //> jsr DrawBrickChunks         ;draw the brick chunks
            drawBrickChunks(ObjectOffset)
            //> pla                         ;get lower nybble of saved state
            //> ldy Block_Y_HighPos,x       ;check vertical high byte of block object
            //> beq UpdSte                  ;if above the screen, branch to kill it
            if (!zeroFlag) {
                //> pha                         ;otherwise save state back into stack
                //> lda #$f0
                //> cmp Block_Y_Position+2,x    ;check to see if bottom block object went
                //> bcs ChkTop                  ;to the bottom of the screen, and branch if not
                if (!(0xF0 >= Block_Y_Position[X])) {
                    //> sta Block_Y_Position+2,x    ;otherwise set offscreen coordinate
                    Block_Y_Position[ObjectOffset] = 0xF0
                }
                //> ChkTop: lda Block_Y_Position,x      ;get top block object's vertical coordinate
                //> cmp #$f0                    ;see if it went to the bottom of the screen
                //> pla                         ;pull block object state from stack
                //> bcc UpdSte                  ;if not, branch to save state
                if (Block_Y_Position[X] >= 0xF0) {
                    //> bcs KillBlock               ;otherwise do unconditional branch to kill it
                    if (!carryFlag) {
                        //> BouncingBlockHandler:
                        //> jsr ImposeGravityBlock     ;do sub to impose gravity on block object
                        imposeGravityBlock()
                        //> ldx ObjectOffset           ;get block object offset
                        //> jsr RelativeBlockPosition  ;get relative coordinates
                        relativeBlockPosition(ObjectOffset)
                        //> jsr GetBlockOffscreenBits  ;get offscreen information
                        getBlockOffscreenBits(ObjectOffset)
                        //> jsr DrawBlock              ;draw the block
                        drawBlock(ObjectOffset)
                        //> lda Block_Y_Position,x     ;get vertical coordinate
                        //> and #$0f                   ;mask out high nybble
                        //> cmp #$05                   ;check to see if low nybble wrapped around
                        //> pla                        ;pull state from stack
                        //> bcs UpdSte                 ;if still above amount, not time to kill block yet, thus branch
                        if (!(Block_Y_Position[ObjectOffset] and 0x0F >= 0x05)) {
                            //> lda #$01
                            //> sta Block_RepFlag,x        ;otherwise set flag to replace metatile
                            Block_RepFlag[ObjectOffset] = 0x01
                            //> KillBlock: lda #$00                   ;if branched here, nullify object state
                        }
                    }
                }
            }
        }
        if (!(Block_Y_Position[ObjectOffset] and 0x0F >= 0x05)) {
        }
    }
    //> UpdSte:    sta Block_State,x          ;store contents of A in block object state
    Block_State[ObjectOffset] = 0x00
    //> rts
    return
}

// Decompiled from BlockObjMT_Updater
fun blockobjmtUpdater() {
    //> BlockObjMT_Updater:
    //> ldx #$01                  ;set offset to start with second block object
    while (negativeFlag) {
        //> lda Block_RepFlag,x       ;if flag for block object already clear,
        //> beq NextBUpd              ;branch to move onto next block object
        if (!zeroFlag) {
            //> lda Block_BBuf_Low,x      ;get low byte of block buffer
            //> sta $06                   ;store into block buffer address
            zp_06 = Block_BBuf_Low[0x01]
            //> lda #$05
            //> sta $07                   ;set high byte of block buffer address
            zp_07 = 0x05
            //> lda Block_Orig_YPos,x     ;get original vertical coordinate of block object
            //> sta $02                   ;store here and use as offset to block buffer
            zp_02 = Block_Orig_YPos[0x01]
            //> tay
            //> lda Block_Metatile,x      ;get metatile to be written
            //> sta ($06),y               ;write it to the block buffer
            TODO = Block_Metatile[0x01]
            //> jsr ReplaceBlockMetatile  ;do sub to replace metatile where block object is
            replaceBlockMetatile(0x01)
            //> lda #$00
            //> sta Block_RepFlag,x       ;clear block object flag
            Block_RepFlag[0x01] = 0x00
        }
        do {
            //> UpdateLoop: stx ObjectOffset          ;set offset here
            ObjectOffset = 0x01
            //> lda VRAM_Buffer1          ;if vram buffer already being used here,
            //> bne NextBUpd              ;branch to move onto next block object
            //> NextBUpd:   dex                       ;decrement block object offset
            //> bpl UpdateLoop            ;do this until both block objects are dealt with
        } while (!negativeFlag)
    }
    //> rts                       ;then leave
    return
}

// Decompiled from MoveEnemyHorizontally
fun moveEnemyHorizontally(X: Int) {
    //> MoveEnemyHorizontally:
    //> inx                         ;increment offset for enemy offset
    //> jsr MoveObjectHorizontally  ;position object horizontally according to
    moveObjectHorizontally((X + 1) and 0xFF)
    //> ldx ObjectOffset            ;counters, return with saved value in A,
    //> rts                         ;put enemy offset back in X and leave
    return
}

// Decompiled from MovePlayerHorizontally
fun movePlayerHorizontally(): Int {
    //> MovePlayerHorizontally:
    //> lda JumpspringAnimCtrl  ;if jumpspring currently animating,
    //> bne ExXMove             ;branch to leave
    if (zeroFlag) {
        //> tax                     ;otherwise set zero for offset to use player's stuff
    }
    //> ExXMove:  rts                         ;and leave
    return A
}

// Decompiled from MoveObjectHorizontally
fun moveObjectHorizontally(X: Int) {
    var A: Int = 0
    //> MoveObjectHorizontally:
    //> lda SprObject_X_Speed,x     ;get currently saved value (horizontal
    //> asl                         ;speed, secondary counter, whatever)
    //> asl                         ;and move low nybble to high
    //> asl
    //> asl
    //> sta $01                     ;store result here
    zp_01 = ((((SprObject_X_Speed[X] shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF
    //> lda SprObject_X_Speed,x     ;get saved value again
    //> lsr                         ;move high nybble to low
    //> lsr
    //> lsr
    //> lsr
    //> cmp #$08                    ;if < 8, branch, do not change
    //> bcc SaveXSpd
    if (SprObject_X_Speed[X] shr 1 shr 1 shr 1 shr 1 >= 0x08) {
        //> ora #%11110000              ;otherwise alter high nybble
    }
    //> SaveXSpd: sta $00                     ;save result here
    zp_00 = SprObject_X_Speed[X] shr 1 shr 1 shr 1 shr 1 or 0xF0
    //> ldy #$00                    ;load default Y value here
    //> cmp #$00                    ;if result positive, leave Y alone
    //> bpl UseAdder
    if (A - 0x00 < 0) {
        //> dey                         ;otherwise decrement Y
    }
    //> UseAdder: sty $02                     ;save Y here
    zp_02 = (0x00 - 1) and 0xFF
    //> lda SprObject_X_MoveForce,x ;get whatever number's here
    //> clc
    //> adc $01                     ;add low nybble moved to high
    //> sta SprObject_X_MoveForce,x ;store result here
    SprObject_X_MoveForce[X] = (SprObject_X_MoveForce[X] + zp_01 + 0) and 0xFF
    //> lda #$00                    ;init A
    //> rol                         ;rotate carry into d0
    //> pha                         ;push onto stack
    //> ror                         ;rotate d0 back onto carry
    //> lda SprObject_X_Position,x
    //> adc $00                     ;add carry plus saved value (high nybble moved to low
    //> sta SprObject_X_Position,x  ;plus $f0 if necessary) to object's horizontal position
    SprObject_X_Position[X] = (SprObject_X_Position[X] + zp_00 + 0) and 0xFF
    //> lda SprObject_PageLoc,x
    //> adc $02                     ;add carry plus other saved value to the
    //> sta SprObject_PageLoc,x     ;object's page location and save
    SprObject_PageLoc[X] = (SprObject_PageLoc[X] + zp_02 + 0) and 0xFF
    //> pla
    //> clc                         ;pull old carry from stack and add
    //> adc $00                     ;to high nybble moved to low
    //> ExXMove:  rts                         ;and leave
    return
}

// Decompiled from MoveD_EnemyVertically
fun movedEnemyvertically(X: Int) {
    //> MoveD_EnemyVertically:
    //> ldy #$3d           ;set quick movement amount downwards
    //> lda Enemy_State,x  ;then check enemy state
    //> cmp #$05           ;if not set to unique state for spiny's egg, go ahead
    //> bne ContVMove      ;and use, otherwise set different movement amount, continue on
    if (Enemy_State[X] - 0x05 == 0) {
    }
    //> ContVMove: jmp SetHiMax   ;jump to skip the rest of this
    //> SetHiMax:    lda #$03                ;set maximum speed in A
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
    //> MoveDropPlatform:
    //> ldy #$7f      ;set movement amount for drop platform
    //> bne SetMdMax  ;skip ahead of other value set here
    if (zeroFlag) {
    }
    //> SetMdMax: lda #$02         ;set maximum speed in A
    //> bne SetXMoveAmt  ;unconditional branch
    //> ;--------------------------------
}

// Decompiled from MoveEnemySlowVert
fun moveEnemySlowVert() {
    //> MoveEnemySlowVert:
    //> ldy #$0f         ;set movement amount for bowser/other objects
    //> SetMdMax: lda #$02         ;set maximum speed in A
    //> bne SetXMoveAmt  ;unconditional branch
    //> ;--------------------------------
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
    //> SetXMoveAmt: sty $00                 ;set movement amount here
    zp_00 = Y
    //> inx                     ;increment X for enemy offset
    //> jsr ImposeGravitySprObj ;do a sub to move enemy object downwards
    imposeGravitySprObj(A)
    //> ldx ObjectOffset        ;get enemy object buffer offset and leave
    //> rts
    return
}

// Decompiled from ImposeGravityBlock
fun imposeGravityBlock() {
    //> ImposeGravityBlock:
    //> ldy #$01       ;set offset for maximum speed
    //> lda #$50       ;set movement amount here
    //> sta $00
    zp_00 = 0x50
    //> lda MaxSpdBlockData,y    ;get maximum speed
}

// Decompiled from ImposeGravitySprObj
fun imposeGravitySprObj(A: Int) {
    //> ImposeGravitySprObj:
    //> sta $02            ;set maximum speed here
    zp_02 = A
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
    //> MovePlatformUp:
    //> lda #$01        ;save value to stack
    //> pha
    //> ldy Enemy_ID,x  ;get enemy object identifier
    //> inx             ;increment offset for enemy object
    //> lda #$05        ;load default value here
    //> cpy #$29        ;residual comparison, object #29 never executes
    //> bne SetDplSpd   ;this code, thus unconditional branch here
    if (Enemy_ID[X] == 0x29) {
        //> lda #$09        ;residual code
    }
    //> SetDplSpd: sta $00         ;save downward movement amount here
    zp_00 = 0x09
    //> lda #$0a        ;save upward movement amount here
    //> sta $01
    zp_01 = 0x0A
    //> lda #$03        ;save maximum vertical speed here
    //> sta $02
    zp_02 = 0x03
    //> pla             ;get value from stack
    //> tay             ;use as Y, then move onto code shared by red koopa
    //> RedPTroopaGrav:
    //> jsr ImposeGravity  ;do a sub to move object gradually
    imposeGravity(0x03, (X + 1) and 0xFF)
    //> ldx ObjectOffset   ;get enemy object offset and leave
    //> rts
    return
}

// Decompiled from ImposeGravity
fun imposeGravity(A: Int, X: Int) {
    //> ImposeGravity:
    //> pha                          ;push value to stack
    //> lda SprObject_YMF_Dummy,x
    //> clc                          ;add value in movement force to contents of dummy variable
    //> adc SprObject_Y_MoveForce,x
    //> sta SprObject_YMF_Dummy,x
    SprObject_YMF_Dummy[X] = (SprObject_YMF_Dummy[X] + SprObject_Y_MoveForce[X] + 0) and 0xFF
    //> ldy #$00                     ;set Y to zero by default
    //> lda SprObject_Y_Speed,x      ;get current vertical speed
    //> bpl AlterYP                  ;if currently moving downwards, do not decrement Y
    if (negativeFlag) {
        //> dey                          ;otherwise decrement Y
    }
    //> AlterYP: sty $07                      ;store Y here
    zp_07 = (0x00 - 1) and 0xFF
    //> adc SprObject_Y_Position,x   ;add vertical position to vertical speed plus carry
    //> sta SprObject_Y_Position,x   ;store as new vertical position
    SprObject_Y_Position[X] = (SprObject_Y_Speed[X] + SprObject_Y_Position[X] + 0) and 0xFF
    //> lda SprObject_Y_HighPos,x
    //> adc $07                      ;add carry plus contents of $07 to vertical high byte
    //> sta SprObject_Y_HighPos,x    ;store as new vertical high byte
    SprObject_Y_HighPos[X] = (SprObject_Y_HighPos[X] + zp_07 + 0) and 0xFF
    //> lda SprObject_Y_MoveForce,x
    //> clc
    //> adc $00                      ;add downward movement amount to contents of $0433
    //> sta SprObject_Y_MoveForce,x
    SprObject_Y_MoveForce[X] = (SprObject_Y_MoveForce[X] + zp_00 + 0) and 0xFF
    //> lda SprObject_Y_Speed,x      ;add carry to vertical speed and store
    //> adc #$00
    //> sta SprObject_Y_Speed,x
    SprObject_Y_Speed[X] = (SprObject_Y_Speed[X] + 0x00 + 0) and 0xFF
    //> cmp $02                      ;compare to maximum speed
    //> bmi ChkUpM                   ;if less than preset value, skip this part
    if (!((SprObject_Y_Speed[X] + 0x00 + 0) and 0xFF - zp_02 < 0)) {
        //> lda SprObject_Y_MoveForce,x
        //> cmp #$80                     ;if less positively than preset maximum, skip this part
        //> bcc ChkUpM
        if (SprObject_Y_MoveForce[X] >= 0x80) {
            //> lda $02
            //> sta SprObject_Y_Speed,x      ;keep vertical speed within maximum value
            SprObject_Y_Speed[X] = zp_02
            //> lda #$00
            //> sta SprObject_Y_MoveForce,x  ;clear fractional
            SprObject_Y_MoveForce[X] = 0x00
        }
    }
    //> ChkUpM:  pla                          ;get value from stack
    //> beq ExVMove                  ;if set to zero, branch to leave
    if (!zeroFlag) {
        //> lda $02
        //> eor #%11111111               ;otherwise get two's compliment of maximum speed
        //> tay
        //> iny
        //> sty $07                      ;store two's compliment here
        zp_07 = (zp_02 xor 0xFF + 1) and 0xFF
        //> lda SprObject_Y_MoveForce,x
        //> sec                          ;subtract upward movement amount from contents
        //> sbc $01                      ;of movement force, note that $01 is twice as large as $00,
        //> sta SprObject_Y_MoveForce,x  ;thus it effectively undoes add we did earlier
        SprObject_Y_MoveForce[X] = (SprObject_Y_MoveForce[X] - zp_01 - (1 - 1)) and 0xFF
        //> lda SprObject_Y_Speed,x
        //> sbc #$00                     ;subtract borrow from vertical speed and store
        //> sta SprObject_Y_Speed,x
        SprObject_Y_Speed[X] = (SprObject_Y_Speed[X] - 0x00 - (1 - 1)) and 0xFF
        //> cmp $07                      ;compare vertical speed to two's compliment
        //> bpl ExVMove                  ;if less negatively than preset maximum, skip this part
        if ((SprObject_Y_Speed[X] - 0x00 - (1 - 1)) and 0xFF - zp_07 < 0) {
            //> lda SprObject_Y_MoveForce,x
            //> cmp #$80                     ;check if fractional part is above certain amount,
            //> bcs ExVMove                  ;and if so, branch to leave
            if (!(SprObject_Y_MoveForce[X] >= 0x80)) {
                //> lda $07
                //> sta SprObject_Y_Speed,x      ;keep vertical speed within maximum value
                SprObject_Y_Speed[X] = zp_07
                //> lda #$ff
                //> sta SprObject_Y_MoveForce,x  ;clear fractional
                SprObject_Y_MoveForce[X] = 0xFF
            }
        }
    }
    //> ExVMove: rts                          ;leave!
    return
}

// Decompiled from EnemiesAndLoopsCore
fun enemiesAndLoopsCore(X: Int) {
    var A: Int = 0
    var Y: Int = 0
    //> EnemiesAndLoopsCore:
    //> lda Enemy_Flag,x         ;check data here for MSB set
    //> pha                      ;save in stack
    //> asl
    //> bcs ChkBowserF           ;if MSB set in enemy flag, branch ahead of jumps
    if (!carryFlag) {
        //> pla                      ;get from stack
        //> beq ChkAreaTsk           ;if data zero, branch
        if (!zeroFlag) {
            //> jmp RunEnemyObjectsCore  ;otherwise, jump to run enemy subroutines
        }
        //> ChkAreaTsk: lda AreaParserTaskNum    ;check number of tasks to perform
        //> and #$07
        //> cmp #$07                 ;if at a specific task, jump and leave
        //> beq ExitELCore
        if (!(AreaParserTaskNum and 0x07 - 0x07 == 0)) {
            //> jmp ProcLoopCommand      ;otherwise, jump to process loop command/load enemies
            //> ChkBowserF: pla                      ;get data from stack
            //> and #%00001111           ;mask out high nybble
            //> tay
            //> lda Enemy_Flag,y         ;use as pointer and load same place with different offset
            //> bne ExitELCore
            if (zeroFlag) {
                //> sta Enemy_Flag,x         ;if second enemy flag not set, also clear first one
                Enemy_Flag[X] = Enemy_Flag[AreaParserTaskNum and 0x07 and 0x0F]
            }
        }
    } else {
        if (zeroFlag) {
        }
        //> ExitELCore: rts
        return
    }
    //> ProcLoopCommand:
    //> lda LoopCommand           ;check if loop command was found
    //> beq ChkEnemyFrenzy
    if (!zeroFlag) {
        //> lda CurrentColumnPos      ;check to see if we're still on the first page
        //> bne ChkEnemyFrenzy        ;if not, do not loop yet
        if (zeroFlag) {
            //> ldy #$0b                  ;start at the end of each set of loop data
            while (WorldNumber - LoopCmdWorldNumber[Y] == 0) {
                do {
                    //> FindLoop: dey
                    //> bmi ChkEnemyFrenzy        ;if all data is checked and not match, do not loop
                    //> lda WorldNumber           ;check to see if one of the world numbers
                    //> cmp LoopCmdWorldNumber,y  ;matches our current world number
                    //> bne FindLoop
                } while (!(WorldNumber - LoopCmdWorldNumber[Y] == 0))
                do {
                    //> lda CurrentPageLoc        ;check to see if one of the page numbers
                    //> cmp LoopCmdPageNumber,y   ;matches the page we're currently on
                    //> bne FindLoop
                } while (!(CurrentPageLoc - LoopCmdPageNumber[Y] == 0))
            }
            //> lda Player_Y_Position     ;check to see if the player is at the correct position
            //> cmp LoopCmdYPosition,y    ;if not, branch to check for world 7
            //> bne WrongChk
            if (Player_Y_Position - LoopCmdYPosition[Y] == 0) {
                //> lda Player_State          ;check to see if the player is
                //> cmp #$00                  ;on solid ground (i.e. not jumping or falling)
                //> bne WrongChk              ;if not, player fails to pass loop, and loopback
                if (Player_State - 0x00 == 0) {
                    //> lda WorldNumber           ;are we in world 7? (check performed on correct
                    //> cmp #World7               ;vertical position and on solid ground)
                    //> bne InitMLp               ;if not, initialize flags used there, otherwise
                    if (WorldNumber - World7 == 0) {
                        //> inc MultiLoopCorrectCntr  ;increment counter for correct progression
                        MultiLoopCorrectCntr = (MultiLoopCorrectCntr + 1) and 0xFF
                        while (MultiLoopPassCntr - 0x03 == 0) {
                            //> lda MultiLoopCorrectCntr  ;if so, have we done them all correctly?
                            //> cmp #$03
                            //> beq InitMLp               ;if so, branch past unnecessary check here
                            if (!(MultiLoopCorrectCntr - 0x03 == 0)) {
                                //> bne DoLpBack              ;unconditional branch if previous branch fails
                                if (A - 0x03 == 0) {
                                    do {
                                        //> IncMLoop: inc MultiLoopPassCntr     ;increment master multi-part counter
                                        MultiLoopPassCntr = (MultiLoopPassCntr + 1) and 0xFF
                                        //> lda MultiLoopPassCntr     ;have we done all three parts?
                                        //> cmp #$03
                                        //> bne InitLCmd              ;if not, skip this part
                                        //> WrongChk: lda WorldNumber           ;are we in world 7? (check performed on
                                        //> cmp #World7               ;incorrect vertical position or not on solid ground)
                                        //> beq IncMLoop
                                    } while (WorldNumber - World7 == 0)
                                }
                                //> DoLpBack: jsr ExecGameLoopback      ;if player is not in right place, loop back
                                execGameLoopback((0x0B - 1) and 0xFF)
                                //> jsr KillAllEnemies
                                killAllEnemies(WorldNumber)
                            }
                        }
                    }
                }
            }
            do {
            } while (WorldNumber - World7 == 0)
            //> InitMLp:  lda #$00                  ;initialize counters used for multi-part loop commands
            //> sta MultiLoopPassCntr
            MultiLoopPassCntr = 0x00
            //> sta MultiLoopCorrectCntr
            MultiLoopCorrectCntr = 0x00
            //> InitLCmd: lda #$00                  ;initialize loop command flag
            //> sta LoopCommand
            LoopCommand = 0x00
            //> ;--------------------------------
        }
    }
    //> ChkEnemyFrenzy:
    //> lda EnemyFrenzyQueue  ;check for enemy object in frenzy queue
    //> beq ProcessEnemyData  ;if not, skip this part
    if (!zeroFlag) {
        //> sta Enemy_ID,x        ;store as enemy object identifier here
        Enemy_ID[X] = EnemyFrenzyQueue
        //> lda #$01
        //> sta Enemy_Flag,x      ;activate enemy object flag
        Enemy_Flag[X] = 0x01
        //> lda #$00
        //> sta Enemy_State,x     ;initialize state and frenzy queue
        Enemy_State[X] = 0x00
        //> sta EnemyFrenzyQueue
        EnemyFrenzyQueue = 0x00
        //> jmp InitEnemyObject   ;and then jump to deal with this enemy
    }
    //> ProcessEnemyData:
    //> ldy EnemyDataOffset      ;get offset of enemy object data
    //> lda (EnemyData),y        ;load first byte
    //> cmp #$ff                 ;check for EOD terminator
    //> bne CheckEndofBuffer
    if (TODO - 0xFF == 0) {
    } else {
        //> CheckEndofBuffer:
        //> and #%00001111           ;check for special row $0e
        //> cmp #$0e
        //> beq CheckRightBounds     ;if found, branch, otherwise
        if (!(A and 0x0F - 0x0E == 0)) {
            //> cpx #$05                 ;check for end of buffer
            //> bcc CheckRightBounds     ;if not at end of buffer, branch
            if (X >= 0x05) {
                //> iny
                //> lda (EnemyData),y        ;check for specific value here
                //> and #%00111111           ;not sure what this was intended for, exactly
                //> cmp #$2e                 ;this part is quite possibly residual code
                //> beq CheckRightBounds     ;but it has the effect of keeping enemies out of
                if (!(TODO and 0x3F - 0x2E == 0)) {
                    //> rts                      ;the sixth slot
                    return
                }
            }
        }
        //> CheckRightBounds:
        //> lda ScreenRight_X_Pos    ;add 48 to pixel coordinate of right boundary
        //> clc
        //> adc #$30
        //> and #%11110000           ;store high nybble
        //> sta $07
        zp_07 = (ScreenRight_X_Pos + 0x30 + 0) and 0xFF and 0xF0
        //> lda ScreenRight_PageLoc  ;add carry to page location of right boundary
        //> adc #$00
        //> sta $06                  ;store page location + carry
        zp_06 = (ScreenRight_PageLoc + 0x00 + 0) and 0xFF
        //> ldy EnemyDataOffset
        //> iny
        //> lda (EnemyData),y        ;if MSB of enemy object is clear, branch to check for row $0f
        //> asl
        //> bcc CheckPageCtrlRow
        if (0) {
            //> lda EnemyObjectPageSel   ;if page select already set, do not set again
            //> bne CheckPageCtrlRow
            if (zeroFlag) {
                //> inc EnemyObjectPageSel   ;otherwise, if MSB is set, set page select
                EnemyObjectPageSel = (EnemyObjectPageSel + 1) and 0xFF
                //> inc EnemyObjectPageLoc   ;and increment page control
                EnemyObjectPageLoc = (EnemyObjectPageLoc + 1) and 0xFF
            }
        }
        //> CheckPageCtrlRow:
        //> dey
        //> lda (EnemyData),y        ;reread first byte
        //> and #$0f
        //> cmp #$0f                 ;check for special row $0f
        //> bne PositionEnemyObj     ;if not found, branch to position enemy object
        if (TODO and 0x0F - 0x0F == 0) {
            //> lda EnemyObjectPageSel   ;if page select set,
            //> bne PositionEnemyObj     ;branch without reading second byte
            if (zeroFlag) {
                while (true) {
                    //> jmp CheckFrenzyBuffer    ;if found, jump to check frenzy buffer, otherwise
                    //> iny
                    //> lda (EnemyData),y        ;otherwise, get second byte, mask out 2 MSB
                    //> and #%00111111
                    //> sta EnemyObjectPageLoc   ;store as page control for enemy object data
                    EnemyObjectPageLoc = TODO and 0x3F
                    //> inc EnemyDataOffset      ;increment enemy object data offset 2 bytes
                    EnemyDataOffset = (EnemyDataOffset + 1) and 0xFF
                    //> inc EnemyDataOffset
                    EnemyDataOffset = (EnemyDataOffset + 1) and 0xFF
                    //> inc EnemyObjectPageSel   ;set page select for enemy object data and
                    EnemyObjectPageSel = (EnemyObjectPageSel + 1) and 0xFF
                    //> jmp ProcLoopCommand      ;jump back to process loop commands again
                }
            }
        }
        //> PositionEnemyObj:
        //> lda EnemyObjectPageLoc   ;store page control as page location
        //> sta Enemy_PageLoc,x      ;for enemy object
        Enemy_PageLoc[X] = EnemyObjectPageLoc
        //> lda (EnemyData),y        ;get first byte of enemy object
        //> and #%11110000
        //> sta Enemy_X_Position,x   ;store column position
        Enemy_X_Position[X] = TODO and 0xF0
        //> cmp ScreenRight_X_Pos    ;check column position against right boundary
        //> lda Enemy_PageLoc,x      ;without subtracting, then subtract borrow
        //> sbc ScreenRight_PageLoc  ;from page location
        //> bcs CheckRightExtBounds  ;if enemy object beyond or at boundary, branch
        if (!(TODO and 0xF0 >= ScreenRight_X_Pos)) {
            //> lda (EnemyData),y
            //> and #%00001111           ;check for special row $0e
            //> cmp #$0e                 ;if found, jump elsewhere
            //> beq ParseRow0e
            if (!(TODO and 0x0F - 0x0E == 0)) {
                //> jmp CheckThreeBytes      ;if not found, unconditional jump
                //> CheckRightExtBounds:
                //> lda $07                  ;check right boundary + 48 against
                //> cmp Enemy_X_Position,x   ;column position without subtracting,
                //> lda $06                  ;then subtract borrow from page control temp
                //> sbc Enemy_PageLoc,x      ;plus carry
                //> bcc CheckFrenzyBuffer    ;if enemy object beyond extended boundary, branch
                if (zp_07 >= Enemy_X_Position[X]) {
                    //> lda #$01                 ;store value in vertical high byte
                    //> sta Enemy_Y_HighPos,x
                    Enemy_Y_HighPos[X] = 0x01
                    //> lda (EnemyData),y        ;get first byte again
                    //> asl                      ;multiply by four to get the vertical
                    //> asl                      ;coordinate
                    //> asl
                    //> asl
                    //> sta Enemy_Y_Position,x
                    Enemy_Y_Position[X] = ((((TODO shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF
                    //> cmp #$e0                 ;do one last check for special row $0e
                    //> beq ParseRow0e           ;(necessary if branched to $c1cb)
                    if (!(((((TODO shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF - 0xE0 == 0)) {
                        //> iny
                        //> lda (EnemyData),y        ;get second byte of object
                        //> and #%01000000           ;check to see if hard mode bit is set
                        //> beq CheckForEnemyGroup   ;if not, branch to check for group enemy objects
                        if (!zeroFlag) {
                            //> lda SecondaryHardMode    ;if set, check to see if secondary hard mode flag
                            //> beq Inc2B                ;is on, and if not, branch to skip this object completely
                            if (!zeroFlag) {
                                //> CheckForEnemyGroup:
                                //> lda (EnemyData),y      ;get second byte and mask out 2 MSB
                                //> and #%00111111
                                //> cmp #$37               ;check for value below $37
                                //> bcc BuzzyBeetleMutate
                                if (TODO and 0x3F >= 0x37) {
                                    //> cmp #$3f               ;if $37 or greater, check for value
                                    //> bcc DoGroup            ;below $3f, branch if below $3f
                                    if (A >= 0x3F) {
                                        //> BuzzyBeetleMutate:
                                        //> cmp #Goomba          ;if below $37, check for goomba
                                        //> bne StrID            ;value ($3f or more always fails)
                                        if (A - Goomba == 0) {
                                            //> ldy PrimaryHardMode  ;check if primary hard mode flag is set
                                            //> beq StrID            ;and if so, change goomba to buzzy beetle
                                            if (!zeroFlag) {
                                                //> lda #BuzzyBeetle
                                            }
                                        }
                                        //> StrID:  sta Enemy_ID,x       ;store enemy object number into buffer
                                        Enemy_ID[X] = BuzzyBeetle
                                        //> lda #$01
                                        //> sta Enemy_Flag,x     ;set flag for enemy in buffer
                                        Enemy_Flag[X] = 0x01
                                        //> jsr InitEnemyObject
                                        initEnemyObject(X)
                                        //> lda Enemy_Flag,x     ;check to see if flag is set
                                        //> bne Inc2B            ;if not, leave, otherwise branch
                                        if (zeroFlag) {
                                            //> rts
                                            return
                                            //> CheckFrenzyBuffer:
                                            //> lda EnemyFrenzyBuffer    ;if enemy object stored in frenzy buffer
                                            //> bne StrFre               ;then branch ahead to store in enemy object buffer
                                            if (zeroFlag) {
                                                //> lda VineFlagOffset       ;otherwise check vine flag offset
                                                //> cmp #$01
                                                //> bne ExEPar               ;if other value <> 1, leave
                                                if (VineFlagOffset - 0x01 == 0) {
                                                    //> lda #VineObject          ;otherwise put vine in enemy identifier
                                                    //> StrFre: sta Enemy_ID,x           ;store contents of frenzy buffer into enemy identifier value
                                                    Enemy_ID[X] = VineObject
                                                }
                                            }
                                            //> ExEPar: rts                      ;then leave
                                            return
                                            //> DoGroup:
                                            //> jmp HandleGroupEnemies   ;handle enemy group objects
                                            //> ParseRow0e:
                                            //> iny                      ;increment Y to load third byte of object
                                            //> iny
                                            //> lda (EnemyData),y
                                            //> lsr                      ;move 3 MSB to the bottom, effectively
                                            //> lsr                      ;making %xxx00000 into %00000xxx
                                            //> lsr
                                            //> lsr
                                            //> lsr
                                            //> cmp WorldNumber          ;is it the same world number as we're on?
                                            //> bne NotUse               ;if not, do not use (this allows multiple uses
                                            if (TODO shr 1 shr 1 shr 1 shr 1 shr 1 - WorldNumber == 0) {
                                                //> dey                      ;of the same area, like the underground bonus areas)
                                                //> lda (EnemyData),y        ;otherwise, get second byte and use as offset
                                                //> sta AreaPointer          ;to addresses for level and enemy object data
                                                AreaPointer = TODO
                                                //> iny
                                                //> lda (EnemyData),y        ;get third byte again, and this time mask out
                                                //> and #%00011111           ;the 3 MSB from before, save as page number to be
                                                //> sta EntrancePage         ;used upon entry to area, if area is entered
                                                EntrancePage = TODO and 0x1F
                                            }
                                            //> NotUse: jmp Inc3B
                                            //> CheckThreeBytes:
                                            //> ldy EnemyDataOffset      ;load current offset for enemy object data
                                            //> lda (EnemyData),y        ;get first byte
                                            //> and #%00001111           ;check for special row $0e
                                            //> cmp #$0e
                                            //> bne Inc2B
                                            if (TODO and 0x0F - 0x0E == 0) {
                                                //> Inc3B:  inc EnemyDataOffset      ;if row = $0e, increment three bytes
                                                EnemyDataOffset = (EnemyDataOffset + 1) and 0xFF
                                            }
                                        }
                                    }
                                }
                                if (A - Goomba == 0) {
                                    if (!zeroFlag) {
                                    }
                                }
                                if (zeroFlag) {
                                    if (zeroFlag) {
                                        if (VineFlagOffset - 0x01 == 0) {
                                        }
                                    }
                                    if (TODO shr 1 shr 1 shr 1 shr 1 shr 1 - WorldNumber == 0) {
                                    }
                                    if (TODO and 0x0F - 0x0E == 0) {
                                    }
                                }
                            }
                        }
                        if (TODO and 0x3F >= 0x37) {
                            if (A >= 0x3F) {
                                if (A - Goomba == 0) {
                                    if (!zeroFlag) {
                                    }
                                }
                                if (zeroFlag) {
                                    if (zeroFlag) {
                                        if (VineFlagOffset - 0x01 == 0) {
                                        }
                                    }
                                    if (TODO shr 1 shr 1 shr 1 shr 1 shr 1 - WorldNumber == 0) {
                                    }
                                    if (TODO and 0x0F - 0x0E == 0) {
                                    }
                                }
                            }
                        }
                        if (A - Goomba == 0) {
                            if (!zeroFlag) {
                            }
                        }
                        if (zeroFlag) {
                            if (zeroFlag) {
                                if (VineFlagOffset - 0x01 == 0) {
                                }
                            }
                            if (TODO shr 1 shr 1 shr 1 shr 1 shr 1 - WorldNumber == 0) {
                            }
                            if (TODO and 0x0F - 0x0E == 0) {
                            }
                        }
                    }
                }
                if (zeroFlag) {
                    if (VineFlagOffset - 0x01 == 0) {
                    }
                }
            }
        }
        if (zp_07 >= Enemy_X_Position[X]) {
            if (!(((((TODO shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF - 0xE0 == 0)) {
                if (!zeroFlag) {
                    if (!zeroFlag) {
                        if (TODO and 0x3F >= 0x37) {
                            if (A >= 0x3F) {
                                if (A - Goomba == 0) {
                                    if (!zeroFlag) {
                                    }
                                }
                                if (zeroFlag) {
                                    if (zeroFlag) {
                                        if (VineFlagOffset - 0x01 == 0) {
                                        }
                                    }
                                    if (TODO shr 1 shr 1 shr 1 shr 1 shr 1 - WorldNumber == 0) {
                                    }
                                    if (TODO and 0x0F - 0x0E == 0) {
                                    }
                                }
                            }
                        }
                        if (A - Goomba == 0) {
                            if (!zeroFlag) {
                            }
                        }
                        if (zeroFlag) {
                            if (zeroFlag) {
                                if (VineFlagOffset - 0x01 == 0) {
                                }
                            }
                            if (TODO shr 1 shr 1 shr 1 shr 1 shr 1 - WorldNumber == 0) {
                            }
                            if (TODO and 0x0F - 0x0E == 0) {
                            }
                        }
                    }
                }
                if (TODO and 0x3F >= 0x37) {
                    if (A >= 0x3F) {
                        if (A - Goomba == 0) {
                            if (!zeroFlag) {
                            }
                        }
                        if (zeroFlag) {
                            if (zeroFlag) {
                                if (VineFlagOffset - 0x01 == 0) {
                                }
                            }
                            if (TODO shr 1 shr 1 shr 1 shr 1 shr 1 - WorldNumber == 0) {
                            }
                            if (TODO and 0x0F - 0x0E == 0) {
                            }
                        }
                    }
                }
                if (A - Goomba == 0) {
                    if (!zeroFlag) {
                    }
                }
                if (zeroFlag) {
                    if (zeroFlag) {
                        if (VineFlagOffset - 0x01 == 0) {
                        }
                    }
                    if (TODO shr 1 shr 1 shr 1 shr 1 shr 1 - WorldNumber == 0) {
                    }
                    if (TODO and 0x0F - 0x0E == 0) {
                    }
                }
            }
        }
    }
    if (zeroFlag) {
        if (VineFlagOffset - 0x01 == 0) {
        }
    }
    if (TODO shr 1 shr 1 shr 1 shr 1 shr 1 - WorldNumber == 0) {
    }
    if (TODO and 0x0F - 0x0E == 0) {
    }
    //> Inc2B:  inc EnemyDataOffset      ;otherwise increment two bytes
    EnemyDataOffset = (EnemyDataOffset + 1) and 0xFF
    //> inc EnemyDataOffset
    EnemyDataOffset = (EnemyDataOffset + 1) and 0xFF
    //> lda #$00                 ;init page select for enemy objects
    //> sta EnemyObjectPageSel
    EnemyObjectPageSel = 0x00
    //> ldx ObjectOffset         ;reload current offset in enemy buffers
    //> rts                      ;and leave
    return
}

// Decompiled from ExecGameLoopback
fun execGameLoopback(Y: Int) {
    //> ExecGameLoopback:
    //> lda Player_PageLoc        ;send player back four pages
    //> sec
    //> sbc #$04
    //> sta Player_PageLoc
    Player_PageLoc = (Player_PageLoc - 0x04 - (1 - 1)) and 0xFF
    //> lda CurrentPageLoc        ;send current page back four pages
    //> sec
    //> sbc #$04
    //> sta CurrentPageLoc
    CurrentPageLoc = (CurrentPageLoc - 0x04 - (1 - 1)) and 0xFF
    //> lda ScreenLeft_PageLoc    ;subtract four from page location
    //> sec                       ;of screen's left border
    //> sbc #$04
    //> sta ScreenLeft_PageLoc
    ScreenLeft_PageLoc = (ScreenLeft_PageLoc - 0x04 - (1 - 1)) and 0xFF
    //> lda ScreenRight_PageLoc   ;do the same for the page location
    //> sec                       ;of screen's right border
    //> sbc #$04
    //> sta ScreenRight_PageLoc
    ScreenRight_PageLoc = (ScreenRight_PageLoc - 0x04 - (1 - 1)) and 0xFF
    //> lda AreaObjectPageLoc     ;subtract four from page control
    //> sec                       ;for area objects
    //> sbc #$04
    //> sta AreaObjectPageLoc
    AreaObjectPageLoc = (AreaObjectPageLoc - 0x04 - (1 - 1)) and 0xFF
    //> lda #$00                  ;initialize page select for both
    //> sta EnemyObjectPageSel    ;area and enemy objects
    EnemyObjectPageSel = 0x00
    //> sta AreaObjectPageSel
    AreaObjectPageSel = 0x00
    //> sta EnemyDataOffset       ;initialize enemy object data offset
    EnemyDataOffset = 0x00
    //> sta EnemyObjectPageLoc    ;and enemy object page control
    EnemyObjectPageLoc = 0x00
    //> lda AreaDataOfsLoopback,y ;adjust area object offset based on
    //> sta AreaDataOffset        ;which loop command we encountered
    AreaDataOffset = AreaDataOfsLoopback[Y]
    //> rts
    return
}

// Decompiled from InitEnemyObject
fun initEnemyObject(X: Int) {
    //> InitEnemyObject:
    //> lda #$00                 ;initialize enemy state
    //> sta Enemy_State,x
    Enemy_State[X] = 0x00
    //> jsr CheckpointEnemyID    ;jump ahead to run jump engine and subroutines
    checkpointEnemyID(X)
    //> ExEPar: rts                      ;then leave
    return
}

// Decompiled from CheckpointEnemyID
fun checkpointEnemyID(X: Int) {
    //> CheckpointEnemyID:
    //> lda Enemy_ID,x
    //> cmp #$15                     ;check enemy object identifier for $15 or greater
    //> bcs InitEnemyRoutines        ;and branch straight to the jump engine if found
    if (!(Enemy_ID[X] >= 0x15)) {
        //> tay                          ;save identifier in Y register for now
        //> lda Enemy_Y_Position,x
        //> adc #$08                     ;add eight pixels to what will eventually be the
        //> sta Enemy_Y_Position,x       ;enemy object's vertical coordinate ($00-$14 only)
        Enemy_Y_Position[X] = (Enemy_Y_Position[X] + 0x08 + Enemy_ID[X] >= 0x15) and 0xFF
        //> lda #$01
        //> sta EnemyOffscrBitsMasked,x  ;set offscreen masked bit
        EnemyOffscrBitsMasked[X] = 0x01
        //> tya                          ;get identifier back and use as offset for jump engine
    }
    //> InitEnemyRoutines:
    //> jsr JumpEngine
    jumpEngine(Enemy_ID[X])
    //> ;jump engine table for newly loaded enemy objects
    //> .dw InitNormalEnemy  ;for objects $00-$0f
    //> .dw InitNormalEnemy
    //> .dw InitNormalEnemy
    //> .dw InitRedKoopa
    //> .dw NoInitCode
    //> .dw InitHammerBro
    //> .dw InitGoomba
    //> .dw InitBloober
    //> .dw InitBulletBill
    //> .dw NoInitCode
    //> .dw InitCheepCheep
    //> .dw InitCheepCheep
    //> .dw InitPodoboo
    //> .dw InitPiranhaPlant
    //> .dw InitJumpGPTroopa
    //> .dw InitRedPTroopa
    //> .dw InitHorizFlySwimEnemy  ;for objects $10-$1f
    //> .dw InitLakitu
    //> .dw InitEnemyFrenzy
    //> .dw NoInitCode
    //> .dw InitEnemyFrenzy
    //> .dw InitEnemyFrenzy
    //> .dw InitEnemyFrenzy
    //> .dw InitEnemyFrenzy
    //> .dw EndFrenzy
    //> .dw NoInitCode
    //> .dw NoInitCode
    //> .dw InitShortFirebar
    //> .dw InitShortFirebar
    //> .dw InitShortFirebar
    //> .dw InitShortFirebar
    //> .dw InitLongFirebar
    //> .dw NoInitCode ;for objects $20-$2f
    //> .dw NoInitCode
    //> .dw NoInitCode
    //> .dw NoInitCode
    //> .dw InitBalPlatform
    //> .dw InitVertPlatform
    //> .dw LargeLiftUp
    //> .dw LargeLiftDown
    //> .dw InitHoriPlatform
    //> .dw InitDropPlatform
    //> .dw InitHoriPlatform
    //> .dw PlatLiftUp
    //> .dw PlatLiftDown
    //> .dw InitBowser
    //> .dw PwrUpJmp   ;possibly dummy value
    //> .dw Setup_Vine
    //> .dw NoInitCode ;for objects $30-$36
    //> .dw NoInitCode
    //> .dw NoInitCode
    //> .dw NoInitCode
    //> .dw NoInitCode
    //> .dw InitRetainerObj
    //> .dw EndOfEnemyInitCode
    //> ;-------------------------------------------------------------------------------------
    //> NoInitCode:
    //> rts               ;this executed when enemy object has no init code
    return
}

// Decompiled from InitPodoboo
fun initPodoboo(X: Int) {
    //> InitPodoboo:
    //> lda #$02                  ;set enemy position to below
    //> sta Enemy_Y_HighPos,x     ;the bottom of the screen
    Enemy_Y_HighPos[X] = 0x02
    //> sta Enemy_Y_Position,x
    Enemy_Y_Position[X] = 0x02
    //> lsr
    //> sta EnemyIntervalTimer,x  ;set timer for enemy
    EnemyIntervalTimer[X] = 0x02 shr 1
    //> lsr
    //> sta Enemy_State,x         ;initialize enemy state, then jump to use
    Enemy_State[X] = 0x02 shr 1 shr 1
    //> jmp SmallBBox             ;$09 as bounding box size and set other things
}

// Decompiled from InitNormalEnemy
fun initNormalEnemy(X: Int) {
    //> InitNormalEnemy:
    //> ldy #$01              ;load offset of 1 by default
    //> lda PrimaryHardMode   ;check for primary hard mode flag set
    //> bne GetESpd
    if (zeroFlag) {
        //> dey                   ;if not set, decrement offset
    }
    //> GetESpd: lda NormalXSpdData,y  ;get appropriate horizontal speed
    //> SetESpd: sta Enemy_X_Speed,x   ;store as speed for enemy object
    Enemy_X_Speed[X] = NormalXSpdData[(0x01 - 1) and 0xFF]
    //> jmp TallBBox          ;branch to set bounding box control and other data
    //> TallBBox: lda #$03                    ;set specific bounding box size control
    //> SetBBox:  sta Enemy_BoundBoxCtrl,x    ;set bounding box control here
    Enemy_BoundBoxCtrl[X] = 0x03
    //> lda #$02                    ;set moving direction for left
    //> sta Enemy_MovingDir,x
    Enemy_MovingDir[X] = 0x02
}

// Decompiled from InitHorizFlySwimEnemy
fun initHorizFlySwimEnemy(X: Int) {
    var A: Int = 0
    //> SetESpd: sta Enemy_X_Speed,x   ;store as speed for enemy object
    Enemy_X_Speed[X] = A
    //> jmp TallBBox          ;branch to set bounding box control and other data
    while (true) {
        //> InitHorizFlySwimEnemy:
        //> lda #$00        ;initialize horizontal speed
        //> jmp SetESpd
    }
    //> TallBBox: lda #$03                    ;set specific bounding box size control
    //> SetBBox:  sta Enemy_BoundBoxCtrl,x    ;set bounding box control here
    Enemy_BoundBoxCtrl[X] = 0x03
    //> lda #$02                    ;set moving direction for left
    //> sta Enemy_MovingDir,x
    Enemy_MovingDir[X] = 0x02
}

// Decompiled from SmallBBox
fun smallBBox(X: Int): Int {
    //> SmallBBox: lda #$09               ;set specific bounding box size control
    //> bne SetBBox            ;unconditional branch
    if (zeroFlag) {
        //> ;--------------------------------
        //> InitRedPTroopa:
        //> ldy #$30                    ;load central position adder for 48 pixels down
        //> lda Enemy_Y_Position,x      ;set vertical coordinate into location to
        //> sta RedPTroopaOrigXPos,x    ;be used as original vertical coordinate
        RedPTroopaOrigXPos[X] = Enemy_Y_Position[X]
        //> bpl GetCent                 ;if vertical coordinate < $80
        if (negativeFlag) {
            //> ldy #$e0                    ;if => $80, load position adder for 32 pixels up
        }
        //> GetCent:  tya                         ;send central position adder to A
        //> adc Enemy_Y_Position,x      ;add to current vertical coordinate
        //> sta RedPTroopaCenterYPos,x  ;store as central vertical coordinate
        RedPTroopaCenterYPos[X] = (0xE0 + Enemy_Y_Position[X] + 0) and 0xFF
        //> TallBBox: lda #$03                    ;set specific bounding box size control
    }
    //> SetBBox:  sta Enemy_BoundBoxCtrl,x    ;set bounding box control here
    Enemy_BoundBoxCtrl[X] = 0x03
    //> lda #$02                    ;set moving direction for left
    //> sta Enemy_MovingDir,x
    Enemy_MovingDir[X] = 0x02
}

// Decompiled from InitVStf
fun initVStf(X: Int): Int {
    //> InitVStf: lda #$00                    ;initialize vertical speed
    //> sta Enemy_Y_Speed,x         ;and movement force
    Enemy_Y_Speed[X] = 0x00
    //> sta Enemy_Y_MoveForce,x
    Enemy_Y_MoveForce[X] = 0x00
    //> rts
    return A
}

// Decompiled from SetupLakitu
fun setupLakitu(X: Int) {
    //> SetupLakitu:
    //> lda #$00                   ;erase counter for lakitu's reappearance
    //> sta LakituReappearTimer
    LakituReappearTimer = 0x00
    //> jsr InitHorizFlySwimEnemy  ;set $03 as bounding box, set other attributes
    initHorizFlySwimEnemy(X)
    //> jmp TallBBox2              ;set $03 as bounding box again (not necessary) and leave
    //> TallBBox2: lda #$03                  ;set specific value for bounding box control
    //> SetBBox2:  sta Enemy_BoundBoxCtrl,x  ;set bounding box control then leave
    Enemy_BoundBoxCtrl[X] = 0x03
    //> rts
    return
}

// Decompiled from DuplicateEnemyObj
fun duplicateEnemyObj(X: Int) {
    //> DuplicateEnemyObj:
    //> ldy #$ff                ;start at beginning of enemy slots
    do {
        //> FSLoop: iny                     ;increment one slot
        //> lda Enemy_Flag,y        ;check enemy buffer flag for empty slot
        //> bne FSLoop              ;if set, branch and keep checking
    } while (zeroFlag)
    //> sty DuplicateObj_Offset ;otherwise set offset here
    DuplicateObj_Offset = (0xFF + 1) and 0xFF
    //> txa                     ;transfer original enemy buffer offset
    //> ora #%10000000          ;store with d7 set as flag in new enemy
    //> sta Enemy_Flag,y        ;slot as well as enemy offset
    Enemy_Flag[(0xFF + 1) and 0xFF] = X or 0x80
    //> lda Enemy_PageLoc,x
    //> sta Enemy_PageLoc,y     ;copy page location and horizontal coordinates
    Enemy_PageLoc[(0xFF + 1) and 0xFF] = Enemy_PageLoc[X]
    //> lda Enemy_X_Position,x  ;from original enemy to new enemy
    //> sta Enemy_X_Position,y
    Enemy_X_Position[(0xFF + 1) and 0xFF] = Enemy_X_Position[X]
    //> lda #$01
    //> sta Enemy_Flag,x        ;set flag as normal for original enemy
    Enemy_Flag[X] = 0x01
    //> sta Enemy_Y_HighPos,y   ;set high vertical byte for new enemy
    Enemy_Y_HighPos[(0xFF + 1) and 0xFF] = 0x01
    //> lda Enemy_Y_Position,x
    //> sta Enemy_Y_Position,y  ;copy vertical coordinate from original to new
    Enemy_Y_Position[(0xFF + 1) and 0xFF] = Enemy_Y_Position[X]
    //> FlmEx:  rts                     ;and then leave
    return
}

// Decompiled from PutAtRightExtent
fun putAtRightExtent(A: Int, X: Int): Int {
    //> PutAtRightExtent:
    //> sta Enemy_Y_Position,x    ;set vertical position
    Enemy_Y_Position[X] = A
    //> lda ScreenRight_X_Pos
    //> clc
    //> adc #$20                  ;place enemy 32 pixels beyond right side of screen
    //> sta Enemy_X_Position,x
    Enemy_X_Position[X] = (ScreenRight_X_Pos + 0x20 + 0) and 0xFF
    //> lda ScreenRight_PageLoc
    //> adc #$00                  ;add carry
    //> sta Enemy_PageLoc,x
    Enemy_PageLoc[X] = (ScreenRight_PageLoc + 0x00 + 0) and 0xFF
    //> jmp FinishFlame           ;skip this part to finish setting values
    //> FinishFlame:
    //> lda #$08                 ;set $08 for bounding box control
    //> sta Enemy_BoundBoxCtrl,x
    Enemy_BoundBoxCtrl[X] = 0x08
    //> lda #$01                 ;set high byte of vertical and
    //> sta Enemy_Y_HighPos,x    ;enemy buffer flag
    Enemy_Y_HighPos[X] = 0x01
    //> sta Enemy_Flag,x
    Enemy_Flag[X] = 0x01
    //> lsr
    //> sta Enemy_X_MoveForce,x  ;initialize horizontal movement force, and
    Enemy_X_MoveForce[X] = 0x01 shr 1
    //> sta Enemy_State,x        ;enemy state
    Enemy_State[X] = 0x01 shr 1
    //> rts
    return A
}

// Decompiled from InitPiranhaPlant
fun initPiranhaPlant(X: Int) {
    //> InitPiranhaPlant:
    //> lda #$01                     ;set initial speed
    //> sta PiranhaPlant_Y_Speed,x
    PiranhaPlant_Y_Speed[X] = 0x01
    //> lsr
    //> sta Enemy_State,x            ;initialize enemy state and what would normally
    Enemy_State[X] = 0x01 shr 1
    //> sta PiranhaPlant_MoveFlag,x  ;be used as vertical speed, but not in this case
    PiranhaPlant_MoveFlag[X] = 0x01 shr 1
    //> lda Enemy_Y_Position,x
    //> sta PiranhaPlantDownYPos,x   ;save original vertical coordinate here
    PiranhaPlantDownYPos[X] = Enemy_Y_Position[X]
    //> sec
    //> sbc #$18
    //> sta PiranhaPlantUpYPos,x     ;save original vertical coordinate - 24 pixels here
    PiranhaPlantUpYPos[X] = (Enemy_Y_Position[X] - 0x18 - (1 - 1)) and 0xFF
    //> lda #$09
    //> jmp SetBBox2                 ;set specific value for bounding box control
    //> SetBBox2:  sta Enemy_BoundBoxCtrl,x  ;set bounding box control then leave
    Enemy_BoundBoxCtrl[X] = 0x09
    //> rts
    return
}

// Decompiled from PlatLiftUp
fun platLiftUp(X: Int) {
    //> PlatLiftUp:
    //> lda #$10                 ;set movement amount here
    //> sta Enemy_Y_MoveForce,x
    Enemy_Y_MoveForce[X] = 0x10
    //> lda #$ff                 ;set moving speed for platforms going up
    //> sta Enemy_Y_Speed,x
    Enemy_Y_Speed[X] = 0xFF
    //> jmp CommonSmallLift      ;skip ahead to part we should be executing
    //> CommonSmallLift:
    //> ldy #$01
    //> jsr PosPlatform           ;do a sub to add 12 pixels due to preset value
    posPlatform(X, 0x01)
    //> lda #$04
    //> sta Enemy_BoundBoxCtrl,x  ;set bounding box control for small platforms
    Enemy_BoundBoxCtrl[X] = 0x04
    //> rts
    return
}

// Decompiled from PlatLiftDown
fun platLiftDown(X: Int) {
    //> PlatLiftDown:
    //> lda #$f0                 ;set movement amount here
    //> sta Enemy_Y_MoveForce,x
    Enemy_Y_MoveForce[X] = 0xF0
    //> lda #$00                 ;set moving speed for platforms going down
    //> sta Enemy_Y_Speed,x
    Enemy_Y_Speed[X] = 0x00
    //> ;--------------------------------
    //> CommonSmallLift:
    //> ldy #$01
    //> jsr PosPlatform           ;do a sub to add 12 pixels due to preset value
    posPlatform(X, 0x01)
    //> lda #$04
    //> sta Enemy_BoundBoxCtrl,x  ;set bounding box control for small platforms
    Enemy_BoundBoxCtrl[X] = 0x04
    //> rts
    return
}

// Decompiled from PosPlatform
fun posPlatform(X: Int, Y: Int) {
    //> PosPlatform:
    //> lda Enemy_X_Position,x  ;get horizontal coordinate
    //> clc
    //> adc PlatPosDataLow,y    ;add or subtract pixels depending on offset
    //> sta Enemy_X_Position,x  ;store as new horizontal coordinate
    Enemy_X_Position[X] = (Enemy_X_Position[X] + PlatPosDataLow[Y] + 0) and 0xFF
    //> lda Enemy_PageLoc,x
    //> adc PlatPosDataHigh,y   ;add or subtract page location depending on offset
    //> sta Enemy_PageLoc,x     ;store as new page location
    Enemy_PageLoc[X] = (Enemy_PageLoc[X] + PlatPosDataHigh[Y] + 0) and 0xFF
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
    //> EnemyMovementSubs:
    //> lda Enemy_ID,x
    //> jsr JumpEngine
    jumpEngine(Enemy_ID[X])
    //> .dw MoveNormalEnemy      ;only objects $00-$14 use this table
    //> .dw MoveNormalEnemy
    //> .dw MoveNormalEnemy
    //> .dw MoveNormalEnemy
    //> .dw MoveNormalEnemy
    //> .dw ProcHammerBro
    //> .dw MoveNormalEnemy
    //> .dw MoveBloober
    //> .dw MoveBulletBill
    //> .dw NoMoveCode
    //> .dw MoveSwimmingCheepCheep
    //> .dw MoveSwimmingCheepCheep
    //> .dw MovePodoboo
    //> .dw MovePiranhaPlant
    //> .dw MoveJumpingEnemy
    //> .dw ProcMoveRedPTroopa
    //> .dw MoveFlyGreenPTroopa
    //> .dw MoveLakitu
    //> .dw MoveNormalEnemy
    //> .dw NoMoveCode   ;dummy
    //> .dw MoveFlyingCheepCheep
    //> ;--------------------------------
    //> NoMoveCode:
    //> rts
    return
}

// Decompiled from LargePlatformSubroutines
fun largePlatformSubroutines(X: Int) {
    //> LargePlatformSubroutines:
    //> lda Enemy_ID,x  ;subtract $24 to get proper offset for jump table
    //> sec
    //> sbc #$24
    //> jsr JumpEngine
    jumpEngine((Enemy_ID[X] - 0x24 - (1 - 1)) and 0xFF)
    //> .dw BalancePlatform   ;table used by objects $24-$2a
    //> .dw YMovingPlatform
    //> .dw MoveLargeLiftPlat
    //> .dw MoveLargeLiftPlat
    //> .dw XMovingPlatform
    //> .dw DropPlatform
    //> .dw RightPlatform
    //> ;-------------------------------------------------------------------------------------
}

// Decompiled from EraseEnemyObject
fun eraseEnemyObject(X: Int) {
    //> EraseEnemyObject:
    //> lda #$00                 ;clear all enemy object variables
    //> sta Enemy_Flag,x
    Enemy_Flag[X] = 0x00
    //> sta Enemy_ID,x
    Enemy_ID[X] = 0x00
    //> sta Enemy_State,x
    Enemy_State[X] = 0x00
    //> sta FloateyNum_Control,x
    FloateyNum_Control[X] = 0x00
    //> sta EnemyIntervalTimer,x
    EnemyIntervalTimer[X] = 0x00
    //> sta ShellChainCounter,x
    ShellChainCounter[X] = 0x00
    //> sta Enemy_SprAttrib,x
    Enemy_SprAttrib[X] = 0x00
    //> sta EnemyFrameTimer,x
    EnemyFrameTimer[X] = 0x00
    //> rts
    return
}

// Decompiled from MoveNormalEnemy
fun moveNormalEnemy(X: Int) {
    var A: Int = 0
    //> MoveNormalEnemy:
    //> ldy #$00                   ;init Y to leave horizontal movement as-is
    //> lda Enemy_State,x
    //> and #%01000000             ;check enemy state for d6 set, if set skip
    //> bne FallE                  ;to move enemy vertically, then horizontally if necessary
    if (zeroFlag) {
        //> lda Enemy_State,x
        //> asl                        ;check enemy state for d7 set
        //> bcs SteadM                 ;if set, branch to move enemy horizontally
        if (!carryFlag) {
            //> lda Enemy_State,x
            //> and #%00100000             ;check enemy state for d5 set
            //> bne MoveDefeatedEnemy      ;if set, branch to move defeated enemy object
            if (zeroFlag) {
                //> lda Enemy_State,x
                //> and #%00000111             ;check d2-d0 of enemy state for any set bits
                //> beq SteadM                 ;if enemy in normal state, branch to move enemy horizontally
                if (!zeroFlag) {
                    //> cmp #$05
                    //> beq FallE                  ;if enemy in state used by spiny's egg, go ahead here
                    if (!(A - 0x05 == 0)) {
                        //> cmp #$03
                        //> bcs ReviveStunned          ;if enemy in states $03 or $04, skip ahead to yet another part
                        if (!(A >= 0x03)) {
                            //> FallE: jsr MoveD_EnemyVertically  ;do a sub here to move enemy downwards
                            movedEnemyvertically(X)
                            //> ldy #$00
                            //> lda Enemy_State,x          ;check for enemy state $02
                            //> cmp #$02
                            //> beq MEHor                  ;if found, branch to move enemy horizontally
                            if (!(Enemy_State[X] - 0x02 == 0)) {
                                //> and #%01000000             ;check for d6 set
                                //> beq SteadM                 ;if not set, branch to something else
                                if (!zeroFlag) {
                                    //> lda Enemy_ID,x
                                    //> cmp #PowerUpObject         ;check for power-up object
                                    //> beq SteadM
                                    if (!(Enemy_ID[X] - PowerUpObject == 0)) {
                                        //> bne SlowM                  ;if any other object where d6 set, jump to set Y
                                        if (A - PowerUpObject == 0) {
                                            //> MEHor: jmp MoveEnemyHorizontally  ;jump here to move enemy horizontally for <> $2e and d6 set
                                        }
                                        //> SlowM:  ldy #$01                  ;if branched here, increment Y to slow horizontal movement
                                    }
                                }
                            }
                            //> SteadM: lda Enemy_X_Speed,x       ;get current horizontal speed
                            //> pha                       ;save to stack
                            //> bpl AddHS                 ;if not moving or moving right, skip, leave Y alone
                            if (negativeFlag) {
                                //> iny
                                //> iny                       ;otherwise increment Y to next data
                            }
                            //> AddHS:  clc
                            //> adc XSpeedAdderData,y     ;add value here to slow enemy down if necessary
                            //> sta Enemy_X_Speed,x       ;save as horizontal speed temporarily
                            Enemy_X_Speed[X] = (Enemy_X_Speed[X] + XSpeedAdderData[((0x01 + 1) and 0xFF + 1) and 0xFF] + 0) and 0xFF
                            //> jsr MoveEnemyHorizontally ;then do a sub to move horizontally
                            moveEnemyHorizontally(X)
                            //> pla
                            //> sta Enemy_X_Speed,x       ;get old horizontal speed from stack and return to
                            Enemy_X_Speed[X] = (Enemy_X_Speed[X] + XSpeedAdderData[((0x01 + 1) and 0xFF + 1) and 0xFF] + 0) and 0xFF
                            //> rts                       ;original memory location, then leave
                            return
                        }
                    }
                    if (!(Enemy_State[X] - 0x02 == 0)) {
                        if (!zeroFlag) {
                            if (!(Enemy_ID[X] - PowerUpObject == 0)) {
                                if (A - PowerUpObject == 0) {
                                }
                            }
                        }
                    }
                }
                if (negativeFlag) {
                }
                //> ReviveStunned:
                //> lda EnemyIntervalTimer,x  ;if enemy timer not expired yet,
                //> bne ChkKillGoomba         ;skip ahead to something else
                if (zeroFlag) {
                    //> sta Enemy_State,x         ;otherwise initialize enemy state to normal
                    Enemy_State[X] = EnemyIntervalTimer[X]
                    //> lda FrameCounter
                    //> and #$01                  ;get d0 of frame counter
                    //> tay                       ;use as Y and increment for movement direction
                    //> iny
                    //> sty Enemy_MovingDir,x     ;store as pseudorandom movement direction
                    Enemy_MovingDir[X] = (FrameCounter and 0x01 + 1) and 0xFF
                    //> dey                       ;decrement for use as pointer
                    //> lda PrimaryHardMode       ;check primary hard mode flag
                    //> beq SetRSpd               ;if not set, use pointer as-is
                    if (!zeroFlag) {
                        //> iny
                        //> iny                       ;otherwise increment 2 bytes to next data
                    }
                    //> SetRSpd: lda RevivedXSpeed,y       ;load and store new horizontal speed
                    //> sta Enemy_X_Speed,x       ;and leave
                    Enemy_X_Speed[X] = RevivedXSpeed[((((FrameCounter and 0x01 + 1) and 0xFF - 1) and 0xFF + 1) and 0xFF + 1) and 0xFF]
                    //> rts
                    return
                    //> MoveDefeatedEnemy:
                    //> jsr MoveD_EnemyVertically      ;execute sub to move defeated enemy downwards
                    movedEnemyvertically(X)
                    //> jmp MoveEnemyHorizontally      ;now move defeated enemy horizontally
                }
            }
        }
    }
    if (!(Enemy_State[X] - 0x02 == 0)) {
        if (!zeroFlag) {
            if (!(Enemy_ID[X] - PowerUpObject == 0)) {
                if (A - PowerUpObject == 0) {
                }
            }
        }
    }
    if (negativeFlag) {
    }
    if (zeroFlag) {
        if (!zeroFlag) {
        }
    }
    //> ChkKillGoomba:
    //> cmp #$0e              ;check to see if enemy timer has reached
    //> bne NKGmba            ;a certain point, and branch to leave if not
    if (A - 0x0E == 0) {
        //> lda Enemy_ID,x
        //> cmp #Goomba           ;check for goomba object
        //> bne NKGmba            ;branch if not found
        if (Enemy_ID[X] - Goomba == 0) {
            //> jsr EraseEnemyObject  ;otherwise, kill this goomba object
            eraseEnemyObject(X)
        }
    }
    //> NKGmba: rts                   ;leave!
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
    var Y: Int = 0
    //> XMoveCntr_Platform:
    //> sta $01                     ;store value here
    zp_01 = A
    //> lda FrameCounter
    //> and #%00000011              ;branch to leave if not on
    //> bne NoIncXM                 ;every fourth frame
    if (zeroFlag) {
        //> ldy XMoveSecondaryCounter,x ;get secondary counter
        //> lda XMovePrimaryCounter,x   ;get primary counter
        //> lsr
        //> bcs DecSeXM                 ;if d0 of primary counter set, branch elsewhere
        if (!carryFlag) {
            //> cpy $01                     ;compare secondary counter to preset maximum value
            //> beq IncPXM                  ;if equal, branch ahead of this part
            if (!(Y == zp_01)) {
                //> inc XMoveSecondaryCounter,x ;increment secondary counter and leave
                XMoveSecondaryCounter[X] = (XMoveSecondaryCounter[X] + 1) and 0xFF
                //> NoIncXM: rts
                return
            }
            //> IncPXM:  inc XMovePrimaryCounter,x   ;increment primary counter and leave
            XMovePrimaryCounter[X] = (XMovePrimaryCounter[X] + 1) and 0xFF
            //> rts
            return
        }
    }
    do {
        //> DecSeXM: tya                         ;put secondary counter in A
        //> beq IncPXM                  ;if secondary counter at zero, branch back
    } while (zeroFlag)
    //> dec XMoveSecondaryCounter,x ;otherwise decrement secondary counter and leave
    XMoveSecondaryCounter[X] = (XMoveSecondaryCounter[X] - 1) and 0xFF
    //> rts
    return
}

// Decompiled from MoveWithXMCntrs
fun moveWithXMCntrs(X: Int) {
    //> MoveWithXMCntrs:
    //> lda XMoveSecondaryCounter,x  ;save secondary counter to stack
    //> pha
    //> ldy #$01                     ;set value here by default
    //> lda XMovePrimaryCounter,x
    //> and #%00000010               ;if d1 of primary counter is
    //> bne XMRight                  ;set, branch ahead of this part here
    if (zeroFlag) {
        //> lda XMoveSecondaryCounter,x
        //> eor #$ff                     ;otherwise change secondary
        //> clc                          ;counter to two's compliment
        //> adc #$01
        //> sta XMoveSecondaryCounter,x
        XMoveSecondaryCounter[X] = (XMoveSecondaryCounter[X] xor 0xFF + 0x01 + 0) and 0xFF
        //> ldy #$02                     ;load alternate value here
    }
    //> XMRight: sty Enemy_MovingDir,x        ;store as moving direction
    Enemy_MovingDir[X] = 0x02
    //> jsr MoveEnemyHorizontally
    moveEnemyHorizontally(X)
    //> sta $00                      ;save value obtained from sub here
    zp_00 = (XMoveSecondaryCounter[X] xor 0xFF + 0x01 + 0) and 0xFF
    //> pla                          ;get secondary counter from stack
    //> sta XMoveSecondaryCounter,x  ;and return to original place
    XMoveSecondaryCounter[X] = (XMoveSecondaryCounter[X] xor 0xFF + 0x01 + 0) and 0xFF
    //> rts
    return
}

// Decompiled from ProcSwimmingB
fun procSwimmingB(X: Int) {
    //> ProcSwimmingB:
    //> lda BlooperMoveCounter,x  ;get enemy's movement counter
    //> and #%00000010            ;check for d1 set
    //> bne ChkForFloatdown       ;branch if set
    if (zeroFlag) {
        //> lda FrameCounter
        //> and #%00000111            ;get 3 LSB of frame counter
        //> pha                       ;and save it to the stack
        //> lda BlooperMoveCounter,x  ;get enemy's movement counter
        //> lsr                       ;check for d0 set
        //> bcs SlowSwim              ;branch if set
        if (!carryFlag) {
            //> pla                       ;pull 3 LSB of frame counter from the stack
            //> bne BSwimE                ;branch to leave, execute code only every eighth frame
            if (zeroFlag) {
                //> lda Enemy_Y_MoveForce,x
                //> clc                       ;add to movement force to speed up swim
                //> adc #$01
                //> sta Enemy_Y_MoveForce,x   ;set movement force
                Enemy_Y_MoveForce[X] = (Enemy_Y_MoveForce[X] + 0x01 + 0) and 0xFF
                //> sta BlooperMoveSpeed,x    ;set as movement speed
                BlooperMoveSpeed[X] = (Enemy_Y_MoveForce[X] + 0x01 + 0) and 0xFF
                //> cmp #$02
                //> bne BSwimE                ;if certain horizontal speed, branch to leave
                if ((Enemy_Y_MoveForce[X] + 0x01 + 0) and 0xFF - 0x02 == 0) {
                    //> inc BlooperMoveCounter,x  ;otherwise increment movement counter
                    BlooperMoveCounter[X] = (BlooperMoveCounter[X] + 1) and 0xFF
                }
            }
            //> BSwimE: rts
            return
        }
        //> SlowSwim:
        //> pla                      ;pull 3 LSB of frame counter from the stack
        //> bne NoSSw                ;branch to leave, execute code only every eighth frame
        if (zeroFlag) {
            //> lda Enemy_Y_MoveForce,x
            //> sec                      ;subtract from movement force to slow swim
            //> sbc #$01
            //> sta Enemy_Y_MoveForce,x  ;set movement force
            Enemy_Y_MoveForce[X] = (Enemy_Y_MoveForce[X] - 0x01 - (1 - 1)) and 0xFF
            //> sta BlooperMoveSpeed,x   ;set as movement speed
            BlooperMoveSpeed[X] = (Enemy_Y_MoveForce[X] - 0x01 - (1 - 1)) and 0xFF
            //> bne NoSSw                ;if any speed, branch to leave
            if (zeroFlag) {
                //> inc BlooperMoveCounter,x ;otherwise increment movement counter
                BlooperMoveCounter[X] = (BlooperMoveCounter[X] + 1) and 0xFF
                //> lda #$02
                //> sta EnemyIntervalTimer,x ;set enemy's timer
                EnemyIntervalTimer[X] = 0x02
            }
        }
        //> NoSSw: rts                      ;leave
        return
    } else {
        //> ChkForFloatdown:
        //> lda EnemyIntervalTimer,x ;get enemy timer
        //> beq ChkNearPlayer        ;branch if expired
        if (!zeroFlag) {
            //> Floatdown:
            //> lda FrameCounter        ;get frame counter
            //> lsr                     ;check for d0 set
            //> bcs NoFD                ;branch to leave on every other frame
            if (!carryFlag) {
                //> inc Enemy_Y_Position,x  ;otherwise increment vertical coordinate
                Enemy_Y_Position[X] = (Enemy_Y_Position[X] + 1) and 0xFF
            }
            //> NoFD: rts                     ;leave
            return
        }
    }
    do {
        //> ChkNearPlayer:
        //> lda Enemy_Y_Position,x    ;get vertical coordinate
        //> adc #$10                  ;add sixteen pixels
        //> cmp Player_Y_Position     ;compare result with player's vertical coordinate
        //> bcc Floatdown             ;if modified vertical less than player's, branch
    } while (!((Enemy_Y_Position[X] + 0x10 + 0) and 0xFF >= Player_Y_Position))
    //> lda #$00
    //> sta BlooperMoveCounter,x  ;otherwise nullify movement counter
    BlooperMoveCounter[X] = 0x00
    //> rts
    return
}

// Decompiled from ProcFirebar
fun procFirebar(X: Int) {
    var A: Int = 0
    //> ProcFirebar:
    //> jsr GetEnemyOffscreenBits   ;get offscreen information
    getEnemyOffscreenBits(X)
    //> lda Enemy_OffscreenBits     ;check for d3 set
    //> and #%00001000              ;if so, branch to leave
    //> bne SkipFBar
    if (zeroFlag) {
        //> lda TimerControl            ;if master timer control set, branch
        //> bne SusFbar                 ;ahead of this part
        if (zeroFlag) {
            //> lda FirebarSpinSpeed,x      ;load spinning speed of firebar
            //> jsr FirebarSpin             ;modify current spinstate
            firebarSpin(FirebarSpinSpeed[X], X)
            //> and #%00011111              ;mask out all but 5 LSB
            //> sta FirebarSpinState_High,x ;and store as new high byte of spinstate
            FirebarSpinState_High[X] = FirebarSpinSpeed[X] and 0x1F
        }
        //> SusFbar:  lda FirebarSpinState_High,x ;get high byte of spinstate
        //> ldy Enemy_ID,x              ;check enemy identifier
        //> cpy #$1f
        //> bcc SetupGFB                ;if < $1f (long firebar), branch
        if (Enemy_ID[X] >= 0x1F) {
            //> cmp #$08                    ;check high byte of spinstate
            //> beq SkpFSte                 ;if eight, branch to change
            if (!(A - 0x08 == 0)) {
                //> cmp #$18
                //> bne SetupGFB                ;if not at twenty-four branch to not change
                if (A - 0x18 == 0) {
                    //> SkpFSte:  clc
                    //> adc #$01                    ;add one to spinning thing to avoid horizontal state
                    //> sta FirebarSpinState_High,x
                    FirebarSpinState_High[X] = (FirebarSpinState_High[X] + 0x01 + 0) and 0xFF
                }
            }
        }
        //> SetupGFB: sta $ef                     ;save high byte of spinning thing, modified or otherwise
        zp_ef = (FirebarSpinState_High[X] + 0x01 + 0) and 0xFF
        //> jsr RelativeEnemyPosition   ;get relative coordinates to screen
        relativeEnemyPosition()
        //> jsr GetFirebarPosition      ;do a sub here (residual, too early to be used now)
        getFirebarPosition((FirebarSpinState_High[X] + 0x01 + 0) and 0xFF)
        //> ldy Enemy_SprDataOffset,x   ;get OAM data offset
        //> lda Enemy_Rel_YPos          ;get relative vertical coordinate
        //> sta Sprite_Y_Position,y     ;store as Y in OAM data
        Sprite_Y_Position[Enemy_SprDataOffset[X]] = Enemy_Rel_YPos
        //> sta $07                     ;also save here
        zp_07 = Enemy_Rel_YPos
        //> lda Enemy_Rel_XPos          ;get relative horizontal coordinate
        //> sta Sprite_X_Position,y     ;store as X in OAM data
        Sprite_X_Position[Enemy_SprDataOffset[X]] = Enemy_Rel_XPos
        //> sta $06                     ;also save here
        zp_06 = Enemy_Rel_XPos
        //> lda #$01
        //> sta $00                     ;set $01 value here (not necessary)
        zp_00 = 0x01
        //> jsr FirebarCollision        ;draw fireball part and do collision detection
        firebarCollision(Enemy_SprDataOffset[X])
        //> ldy #$05                    ;load value for short firebars by default
        //> lda Enemy_ID,x
        //> cmp #$1f                    ;are we doing a long firebar?
        //> bcc SetMFbar                ;no, branch then
        if (Enemy_ID[X] >= 0x1F) {
            //> ldy #$0b                    ;otherwise load value for long firebars
        }
        //> SetMFbar: sty $ed                     ;store maximum value for length of firebars
        zp_ed = 0x0B
        //> lda #$00
        //> sta $00                     ;initialize counter here
        zp_00 = 0x00
        while (zp_00 >= zp_ed) {
            //> ldy DuplicateObj_Offset     ;if we arrive at fifth firebar part,
            //> lda Enemy_SprDataOffset,y   ;get offset from long firebar and load OAM data offset
            //> sta $06                     ;using long firebar offset, then store as new one here
            zp_06 = Enemy_SprDataOffset[DuplicateObj_Offset]
            do {
                //> DrawFbar: lda $ef                     ;load high byte of spinstate
                //> jsr GetFirebarPosition      ;get fireball position data depending on firebar part
                getFirebarPosition(zp_ef)
                //> jsr DrawFirebar_Collision   ;position it properly, draw it and do collision detection
                drawfirebarCollision()
                //> lda $00                     ;check which firebar part
                //> cmp #$04
                //> bne NextFbar
                //> NextFbar: inc $00                     ;move onto the next firebar part
                zp_00 = (zp_00 + 1) and 0xFF
                //> lda $00
                //> cmp $ed                     ;if we end up at the maximum part, go on and leave
                //> bcc DrawFbar                ;otherwise go back and do another
            } while (!(zp_00 >= zp_ed))
        }
    }
    //> SkipFBar: rts
    return
}

// Decompiled from DrawFirebar_Collision
fun drawfirebarCollision() {
    var A: Int = 0
    //> DrawFirebar_Collision:
    //> lda $03                  ;store mirror data elsewhere
    //> sta $05
    zp_05 = zp_03
    //> ldy $06                  ;load OAM data offset for firebar
    //> lda $01                  ;load horizontal adder we got from position loader
    //> lsr $05                  ;shift LSB of mirror data
    zp_05 = zp_05 shr 1
    //> bcs AddHA                ;if carry was set, skip this part
    if (!carryFlag) {
        //> eor #$ff
        //> adc #$01                 ;otherwise get two's compliment of horizontal adder
    }
    //> AddHA:   clc                      ;add horizontal coordinate relative to screen to
    //> adc Enemy_Rel_XPos       ;horizontal adder, modified or otherwise
    //> sta Sprite_X_Position,y  ;store as X coordinate here
    Sprite_X_Position[zp_06] = ((zp_01 xor 0xFF + 0x01 + 0) and 0xFF + Enemy_Rel_XPos + 0) and 0xFF
    //> sta $06                  ;store here for now, note offset is saved in Y still
    zp_06 = ((zp_01 xor 0xFF + 0x01 + 0) and 0xFF + Enemy_Rel_XPos + 0) and 0xFF
    //> cmp Enemy_Rel_XPos       ;compare X coordinate of sprite to original X of firebar
    //> bcs SubtR1               ;if sprite coordinate => original coordinate, branch
    if (!((A + Enemy_Rel_XPos + 0) and 0xFF >= Enemy_Rel_XPos)) {
        //> lda Enemy_Rel_XPos
        //> sec                      ;otherwise subtract sprite X from the
        //> sbc $06                  ;original one and skip this part
        //> jmp ChkFOfs
    } else {
        //> SubtR1:  sec                      ;subtract original X from the
        //> sbc Enemy_Rel_XPos       ;current sprite X
    }
    //> ChkFOfs: cmp #$59                 ;if difference of coordinates within a certain range,
    //> bcc VAHandl              ;continue by handling vertical adder
    if (A >= 0x59) {
        //> lda #$f8                 ;otherwise, load offscreen Y coordinate
        //> bne SetVFbr              ;and unconditionally branch to move sprite offscreen
        if (zeroFlag) {
            //> VAHandl: lda Enemy_Rel_YPos       ;if vertical relative coordinate offscreen,
            //> cmp #$f8                 ;skip ahead of this part and write into sprite Y coordinate
            //> beq SetVFbr
            if (!(Enemy_Rel_YPos - 0xF8 == 0)) {
                //> lda $02                  ;load vertical adder we got from position loader
                //> lsr $05                  ;shift LSB of mirror data one more time
                zp_05 = zp_05 shr 1
                //> bcs AddVA                ;if carry was set, skip this part
                if (!carryFlag) {
                    //> eor #$ff
                    //> adc #$01                 ;otherwise get two's compliment of second part
                }
                //> AddVA:   clc                      ;add vertical coordinate relative to screen to
                //> adc Enemy_Rel_YPos       ;the second data, modified or otherwise
            }
        }
    }
    if (!(Enemy_Rel_YPos - 0xF8 == 0)) {
        if (!carryFlag) {
        }
    }
    //> SetVFbr: sta Sprite_Y_Position,y  ;store as Y coordinate here
    Sprite_Y_Position[zp_06] = ((zp_02 xor 0xFF + 0x01 + Enemy_Rel_YPos >= 0xF8) and 0xFF + Enemy_Rel_YPos + 0) and 0xFF
    //> sta $07                  ;also store here for now
    zp_07 = ((zp_02 xor 0xFF + 0x01 + Enemy_Rel_YPos >= 0xF8) and 0xFF + Enemy_Rel_YPos + 0) and 0xFF
}

// Decompiled from FirebarCollision
fun firebarCollision(Y: Int) {
    var A: Int = 0
    //> FirebarCollision:
    //> jsr DrawFirebar          ;run sub here to draw current tile of firebar
    drawFirebar(Y)
    //> tya                      ;return OAM data offset and save
    //> pha                      ;to the stack for now
    //> lda StarInvincibleTimer  ;if star mario invincibility timer
    //> ora TimerControl         ;or master timer controls set
    //> bne NoColFB              ;then skip all of this
    if (zeroFlag) {
        //> sta $05                  ;otherwise initialize counter
        zp_05 = StarInvincibleTimer or TimerControl
        //> ldy Player_Y_HighPos
        //> dey                      ;if player's vertical high byte offscreen,
        //> bne NoColFB              ;skip all of this
        if (zeroFlag) {
            //> ldy Player_Y_Position    ;get player's vertical position
            //> lda PlayerSize           ;get player's size
            //> bne AdjSm                ;if player small, branch to alter variables
            if (zeroFlag) {
                //> lda CrouchingFlag
                //> beq BigJp                ;if player big and not crouching, jump ahead
                if (!zeroFlag) {
                    //> AdjSm:   inc $05                  ;if small or big but crouching, execute this part
                    zp_05 = (zp_05 + 1) and 0xFF
                    //> inc $05                  ;first increment our counter twice (setting $02 as flag)
                    zp_05 = (zp_05 + 1) and 0xFF
                    //> tya
                    //> clc                      ;then add 24 pixels to the player's
                    //> adc #$18                 ;vertical coordinate
                    //> tay
                }
            }
            //> BigJp:   tya                      ;get vertical coordinate, altered or otherwise, from Y
            while (!/* unknown branch */) {
                //> eor #$ff                 ;skip two's compliment part
                //> clc                      ;otherwise get two's compliment
                //> adc #$01
                //> ChkVFBD: cmp #$08                 ;if difference => 8 pixels, skip ahead of this part
                //> bcs Chk2Ofs
                if (!(A >= 0x08)) {
                    //> lda $06                  ;if firebar on far right on the screen, skip this,
                    //> cmp #$f0                 ;because, really, what's the point?
                    //> bcs Chk2Ofs
                    if (!(zp_06 >= 0xF0)) {
                        //> lda Sprite_X_Position+4  ;get OAM X coordinate for sprite #1
                        //> clc
                        //> adc #$04                 ;add four pixels
                        //> sta $04                  ;store here
                        zp_04 = (Sprite_X_Position + 0x04 + 0) and 0xFF
                        //> sec                      ;subtract horizontal coordinate of firebar
                        //> sbc $06                  ;from the X coordinate of player's sprite 1
                        //> bpl ChkFBCl              ;if modded X coordinate to the right of firebar
                        if (negativeFlag) {
                            //> eor #$ff                 ;skip two's compliment part
                            //> clc                      ;otherwise get two's compliment
                            //> adc #$01
                        }
                        //> ChkFBCl: cmp #$08                 ;if difference < 8 pixels, collision, thus branch
                        //> bcc ChgSDir              ;to process
                        if (A >= 0x08) {
                            //> Chk2Ofs: lda $05                  ;if value of $02 was set earlier for whatever reason,
                            //> cmp #$02                 ;branch to increment OAM offset and leave, no collision
                            //> beq NoColFB
                            if (!(zp_05 - 0x02 == 0)) {
                                while (true) {
                                    //> FBCLoop: sec                      ;subtract vertical position of firebar
                                    //> sbc $07                  ;from the vertical coordinate of the player
                                    //> bpl ChkVFBD              ;if player lower on the screen than firebar,
                                    //> ldy $05                  ;otherwise get temp here and use as offset
                                    //> lda Player_Y_Position
                                    //> clc
                                    //> adc FirebarYPos,y        ;add value loaded with offset to player's vertical coordinate
                                    //> inc $05                  ;then increment temp and jump back
                                    zp_05 = (zp_05 + 1) and 0xFF
                                    //> jmp FBCLoop
                                }
                                //> ChgSDir: ldx #$01                 ;set movement direction by default
                                //> lda $04                  ;if OAM X coordinate of player's sprite 1
                                //> cmp $06                  ;is greater than horizontal coordinate of firebar
                                //> bcs SetSDir              ;then do not alter movement direction
                                if (!(zp_04 >= zp_06)) {
                                    //> inx                      ;otherwise increment it
                                }
                                //> SetSDir: stx Enemy_MovingDir      ;store movement direction here
                                Enemy_MovingDir = (0x01 + 1) and 0xFF
                                //> ldx #$00
                                //> lda $00                  ;save value written to $00 to stack
                                //> pha
                                //> jsr InjurePlayer         ;perform sub to hurt or kill player
                                injurePlayer()
                                //> pla
                                //> sta $00                  ;get value of $00 from stack
                                zp_00 = zp_00
                            }
                        }
                    }
                }
                if (!(zp_05 - 0x02 == 0)) {
                    while (true) {
                    }
                    if (!(zp_04 >= zp_06)) {
                    }
                }
            }
            if (!(zp_04 >= zp_06)) {
            }
        }
    }
    //> NoColFB: pla                      ;get OAM data offset
    //> clc                      ;add four to it and save
    //> adc #$04
    //> sta $06
    zp_06 = (zp_00 + 0x04 + 0) and 0xFF
    //> ldx ObjectOffset         ;get enemy object buffer offset and leave
    //> rts
    return
}

// Decompiled from GetFirebarPosition
fun getFirebarPosition(A: Int) {
    //> GetFirebarPosition:
    //> pha                        ;save high byte of spinstate to the stack
    //> and #%00001111             ;mask out low nybble
    //> cmp #$09
    //> bcc GetHAdder              ;if lower than $09, branch ahead
    if (A and 0x0F >= 0x09) {
        //> eor #%00001111             ;otherwise get two's compliment to oscillate
        //> clc
        //> adc #$01
    }
    //> GetHAdder: sta $01                    ;store result, modified or not, here
    zp_01 = (A and 0x0F xor 0x0F + 0x01 + 0) and 0xFF
    //> ldy $00                    ;load number of firebar ball where we're at
    //> lda FirebarTblOffsets,y    ;load offset to firebar position data
    //> clc
    //> adc $01                    ;add oscillated high byte of spinstate
    //> tay                        ;to offset here and use as new offset
    //> lda FirebarPosLookupTbl,y  ;get data here and store as horizontal adder
    //> sta $01
    zp_01 = FirebarPosLookupTbl[(FirebarTblOffsets[zp_00] + zp_01 + 0) and 0xFF]
    //> pla                        ;pull whatever was in A from the stack
    //> pha                        ;save it again because we still need it
    //> clc
    //> adc #$08                   ;add eight this time, to get vertical adder
    //> and #%00001111             ;mask out high nybble
    //> cmp #$09                   ;if lower than $09, branch ahead
    //> bcc GetVAdder
    if ((FirebarPosLookupTbl[(A + zp_01 + 0) and 0xFF] + 0x08 + 0) and 0xFF and 0x0F >= 0x09) {
        //> eor #%00001111             ;otherwise get two's compliment
        //> clc
        //> adc #$01
    }
    //> GetVAdder: sta $02                    ;store result here
    zp_02 = ((FirebarPosLookupTbl[(FirebarTblOffsets[zp_00] + zp_01 + 0) and 0xFF] + 0x08 + 0) and 0xFF and 0x0F xor 0x0F + 0x01 + 0) and 0xFF
    //> ldy $00
    //> lda FirebarTblOffsets,y    ;load offset to firebar position data again
    //> clc
    //> adc $02                    ;this time add value in $02 to offset here and use as offset
    //> tay
    //> lda FirebarPosLookupTbl,y  ;get data here and store as vertica adder
    //> sta $02
    zp_02 = FirebarPosLookupTbl[(FirebarTblOffsets[zp_00] + zp_02 + 0) and 0xFF]
    //> pla                        ;pull out whatever was in A one last time
    //> lsr                        ;divide by eight or shift three to the right
    //> lsr
    //> lsr
    //> tay                        ;use as offset
    //> lda FirebarMirrorData,y    ;load mirroring data here
    //> sta $03                    ;store
    zp_03 = FirebarMirrorData[FirebarPosLookupTbl[(FirebarTblOffsets[zp_00] + zp_02 + 0) and 0xFF] shr 1 shr 1 shr 1]
    //> rts
    return
}

// Decompiled from PlayerLakituDiff
fun playerLakituDiff(X: Int) {
    var Y: Int = 0
    //> PlayerLakituDiff:
    //> ldy #$00                   ;set Y for default value
    //> jsr PlayerEnemyDiff        ;get horizontal difference between enemy and player
    playerEnemyDiff(X)
    //> bpl ChkLakDif              ;branch if enemy is to the right of the player
    if (negativeFlag) {
        //> iny                        ;increment Y for left of player
        //> lda $00
        //> eor #$ff                   ;get two's compliment of low byte of horizontal difference
        //> clc
        //> adc #$01                   ;store two's compliment as horizontal difference
        //> sta $00
        zp_00 = (zp_00 xor 0xFF + 0x01 + 0) and 0xFF
    }
    //> ChkLakDif: lda $00                    ;get low byte of horizontal difference
    //> cmp #$3c                   ;if within a certain distance of player, branch
    //> bcc ChkPSpeed
    if (zp_00 >= 0x3C) {
        //> lda #$3c                   ;otherwise set maximum distance
        //> sta $00
        zp_00 = 0x3C
        //> lda Enemy_ID,x             ;check if lakitu is in our current enemy slot
        //> cmp #Lakitu
        //> bne ChkPSpeed              ;if not, branch elsewhere
        if (Enemy_ID[X] - Lakitu == 0) {
            //> tya                        ;compare contents of Y, now in A
            //> cmp LakituMoveDirection,x  ;to what is being used as horizontal movement direction
            //> beq ChkPSpeed              ;if moving toward the player, branch, do not alter
            if (!(Y - LakituMoveDirection[X] == 0)) {
                //> lda LakituMoveDirection,x  ;if moving to the left beyond maximum distance,
                //> beq SetLMovD               ;branch and alter without delay
                if (!zeroFlag) {
                    //> dec LakituMoveSpeed,x      ;decrement horizontal speed
                    LakituMoveSpeed[X] = (LakituMoveSpeed[X] - 1) and 0xFF
                    //> lda LakituMoveSpeed,x      ;if horizontal speed not yet at zero, branch to leave
                    //> bne ExMoveLak
                    if (zeroFlag) {
                        //> SetLMovD:  tya                        ;set horizontal direction depending on horizontal
                        //> sta LakituMoveDirection,x  ;difference between enemy and player if necessary
                        LakituMoveDirection[X] = (0x00 + 1) and 0xFF
                        //> ChkPSpeed: lda $00
                        //> and #%00111100             ;mask out all but four bits in the middle
                        //> lsr                        ;divide masked difference by four
                        //> lsr
                        //> sta $00                    ;store as new value
                        zp_00 = zp_00 and 0x3C shr 1 shr 1
                        //> ldy #$00                   ;init offset
                        //> lda Player_X_Speed
                        //> beq SubDifAdj              ;if player not moving horizontally, branch
                        if (!zeroFlag) {
                            //> lda ScrollAmount
                            //> beq SubDifAdj              ;if scroll speed not set, branch to same place
                            if (!zeroFlag) {
                                //> iny                        ;otherwise increment offset
                                //> lda Player_X_Speed
                                //> cmp #$19                   ;if player not running, branch
                                //> bcc ChkSpinyO
                                if (Player_X_Speed >= 0x19) {
                                    //> lda ScrollAmount
                                    //> cmp #$02                   ;if scroll speed below a certain amount, branch
                                    //> bcc ChkSpinyO              ;to same place
                                    if (ScrollAmount >= 0x02) {
                                        //> iny                        ;otherwise increment once more
                                    }
                                }
                                //> ChkSpinyO: lda Enemy_ID,x             ;check for spiny object
                                //> cmp #Spiny
                                //> bne ChkEmySpd              ;branch if not found
                                if (Enemy_ID[X] - Spiny == 0) {
                                    //> lda Player_X_Speed         ;if player not moving, skip this part
                                    //> bne SubDifAdj
                                    if (zeroFlag) {
                                        //> ChkEmySpd: lda Enemy_Y_Speed,x        ;check vertical speed
                                        //> bne SubDifAdj              ;branch if nonzero
                                        if (zeroFlag) {
                                            //> ldy #$00                   ;otherwise reinit offset
                                        }
                                    }
                                }
                                if (zeroFlag) {
                                }
                            }
                        }
                        //> SubDifAdj: lda $0001,y                ;get one of three saved values from earlier
                        //> ldy $00                    ;get saved horizontal difference
                        do {
                            //> SPixelLak: sec                        ;subtract one for each pixel of horizontal difference
                            //> sbc #$01                   ;from one of three saved values
                            //> dey
                            //> bpl SPixelLak              ;branch until all pixels are subtracted, to adjust difference
                        } while (negativeFlag)
                    }
                }
            }
        }
    }
    if (!zeroFlag) {
        if (!zeroFlag) {
            if (Player_X_Speed >= 0x19) {
                if (ScrollAmount >= 0x02) {
                }
            }
            if (Enemy_ID[X] - Spiny == 0) {
                if (zeroFlag) {
                    if (zeroFlag) {
                    }
                }
            }
            if (zeroFlag) {
            }
        }
    }
    do {
    } while (negativeFlag)
    //> ExMoveLak: rts                        ;leave!!!
    return
}

// Decompiled from KillAllEnemies
fun killAllEnemies(A: Int) {
    //> KillAllEnemies:
    //> ldx #$04              ;start with last enemy slot
    do {
        //> KillLoop: jsr EraseEnemyObject  ;branch to kill enemy objects
        eraseEnemyObject(0x04)
        //> dex                   ;move onto next enemy slot
        //> bpl KillLoop          ;do this until all slots are emptied
    } while (negativeFlag)
    //> sta EnemyFrenzyBuffer ;empty frenzy buffer
    EnemyFrenzyBuffer = A
    //> ldx ObjectOffset      ;get enemy object offset and leave
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
    //> SetFlameTimer:
    //> ldy BowserFlameTimerCtrl  ;load counter as offset
    //> inc BowserFlameTimerCtrl  ;increment
    BowserFlameTimerCtrl = (BowserFlameTimerCtrl + 1) and 0xFF
    //> lda BowserFlameTimerCtrl  ;mask out all but 3 LSB
    //> and #%00000111            ;to keep in range of 0-7
    //> sta BowserFlameTimerCtrl
    BowserFlameTimerCtrl = BowserFlameTimerCtrl and 0x07
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
    //> DrawStarFlag:
    //> jsr RelativeEnemyPosition  ;get relative coordinates of star flag
    relativeEnemyPosition()
    //> ldy Enemy_SprDataOffset,x  ;get OAM data offset
    //> ldx #$03                   ;do four sprites
    do {
        //> DSFLoop: lda Enemy_Rel_YPos         ;get relative vertical coordinate
        //> clc
        //> adc StarFlagYPosAdder,x    ;add Y coordinate adder data
        //> sta Sprite_Y_Position,y    ;store as Y coordinate
        Sprite_Y_Position[Enemy_SprDataOffset[X]] = (Enemy_Rel_YPos + StarFlagYPosAdder[0x03] + 0) and 0xFF
        //> lda StarFlagTileData,x     ;get tile number
        //> sta Sprite_Tilenumber,y    ;store as tile number
        Sprite_Tilenumber[Enemy_SprDataOffset[X]] = StarFlagTileData[0x03]
        //> lda #$22                   ;set palette and background priority bits
        //> sta Sprite_Attributes,y    ;store as attributes
        Sprite_Attributes[Enemy_SprDataOffset[X]] = 0x22
        //> lda Enemy_Rel_XPos         ;get relative horizontal coordinate
        //> clc
        //> adc StarFlagXPosAdder,x    ;add X coordinate adder data
        //> sta Sprite_X_Position,y    ;store as X coordinate
        Sprite_X_Position[Enemy_SprDataOffset[X]] = (Enemy_Rel_XPos + StarFlagXPosAdder[0x03] + 0) and 0xFF
        //> iny
        //> iny                        ;increment OAM data offset four bytes
        //> iny                        ;for next sprite
        //> iny
        //> dex                        ;move onto next sprite
        //> bpl DSFLoop                ;do this until all sprites are done
    } while (negativeFlag)
    //> ldx ObjectOffset           ;get enemy object offset and leave
    //> rts
    return
}

// Decompiled from FirebarSpin
fun firebarSpin(A: Int, X: Int): Int {
    //> FirebarSpin:
    //> sta $07                     ;save spinning speed here
    zp_07 = A
    //> lda FirebarSpinDirection,x  ;check spinning direction
    //> bne SpinCounterClockwise    ;if moving counter-clockwise, branch to other part
    if (zeroFlag) {
        //> ldy #$18                    ;possibly residual ldy
        //> lda FirebarSpinState_Low,x
        //> clc                         ;add spinning speed to what would normally be
        //> adc $07                     ;the horizontal speed
        //> sta FirebarSpinState_Low,x
        FirebarSpinState_Low[X] = (FirebarSpinState_Low[X] + zp_07 + 0) and 0xFF
        //> lda FirebarSpinState_High,x ;add carry to what would normally be the vertical speed
        //> adc #$00
        //> rts
        return A
    } else {
        //> SpinCounterClockwise:
        //> ldy #$08                    ;possibly residual ldy
        //> lda FirebarSpinState_Low,x
        //> sec                         ;subtract spinning speed to what would normally be
        //> sbc $07                     ;the horizontal speed
        //> sta FirebarSpinState_Low,x
        FirebarSpinState_Low[X] = (FirebarSpinState_Low[X] - zp_07 - (1 - 1)) and 0xFF
        //> lda FirebarSpinState_High,x ;add carry to what would normally be the vertical speed
        //> sbc #$00
        //> rts
        return A
    }
}

// Decompiled from SetupPlatformRope
fun setupPlatformRope(A: Int, Y: Int) {
    //> SetupPlatformRope:
    //> pha                     ;save second/third copy to stack
    //> lda Enemy_X_Position,y  ;get horizontal coordinate
    //> clc
    //> adc #$08                ;add eight pixels
    //> ldx SecondaryHardMode   ;if secondary hard mode flag set,
    //> bne GetLRp              ;use coordinate as-is
    if (zeroFlag) {
        //> clc
        //> adc #$10                ;otherwise add sixteen more pixels
    }
    //> GetLRp: pha                     ;save modified horizontal coordinate to stack
    //> lda Enemy_PageLoc,y
    //> adc #$00                ;add carry to page location
    //> sta $02                 ;and save here
    zp_02 = (Enemy_PageLoc[Y] + 0x00 + 0) and 0xFF
    //> pla                     ;pull modified horizontal coordinate
    //> and #%11110000          ;from the stack, mask out low nybble
    //> lsr                     ;and shift three bits to the right
    //> lsr
    //> lsr
    //> sta $00                 ;store result here as part of name table low byte
    zp_00 = (Enemy_PageLoc[Y] + 0x00 + 0) and 0xFF and 0xF0 shr 1 shr 1 shr 1
    //> ldx Enemy_Y_Position,y  ;get vertical coordinate
    //> pla                     ;get second/third copy of vertical speed from stack
    //> bpl GetHRp              ;skip this part if moving downwards or not at all
    if (negativeFlag) {
        //> txa
        //> clc
        //> adc #$08                ;add eight to vertical coordinate and
        //> tax                     ;save as X
    }
    //> GetHRp: txa                     ;move vertical coordinate to A
    //> ldx VRAM_Buffer1_Offset ;get vram buffer offset
    //> asl
    //> rol                     ;rotate d7 to d0 and d6 into carry
    //> pha                     ;save modified vertical coordinate to stack
    //> rol                     ;rotate carry to d0, thus d7 and d6 are at 2 LSB
    //> and #%00000011          ;mask out all bits but d7 and d6, then set
    //> ora #%00100000          ;d5 to get appropriate high byte of name table
    //> sta $01                 ;address, then store
    zp_01 = ((Enemy_Y_Position[Y] + 0x08 + 0) and 0xFF shl 1) and 0xFF and 0x03 or 0x20
    //> lda $02                 ;get saved page location from earlier
    //> and #$01                ;mask out all but LSB
    //> asl
    //> asl                     ;shift twice to the left and save with the
    //> ora $01                 ;rest of the bits of the high byte, to get
    //> sta $01                 ;the proper name table and the right place on it
    zp_01 = ((zp_02 and 0x01 shl 1) and 0xFF shl 1) and 0xFF or zp_01
    //> pla                     ;get modified vertical coordinate from stack
    //> and #%11100000          ;mask out low nybble and LSB of high nybble
    //> clc
    //> adc $00                 ;add to horizontal part saved here
    //> sta $00                 ;save as name table low byte
    zp_00 = (((zp_02 and 0x01 shl 1) and 0xFF shl 1) and 0xFF or zp_01 and 0xE0 + zp_00 + 0) and 0xFF
    //> lda Enemy_Y_Position,y
    //> cmp #$e8                ;if vertical position not below the
    //> bcc ExPRp               ;bottom of the screen, we're done, branch to leave
    if (Enemy_Y_Position[Y] >= 0xE8) {
        //> lda $00
        //> and #%10111111          ;mask out d6 of low byte of name table address
        //> sta $00
        zp_00 = zp_00 and 0xBF
    }
    //> ExPRp:  rts                     ;leave!
    return
}

// Decompiled from StopPlatforms
fun stopPlatforms(A: Int, Y: Int) {
    var X: Int = 0
    //> StopPlatforms:
    //> jsr InitVStf             ;initialize vertical speed and low byte
    initVStf(X)
    //> sta Enemy_Y_Speed,y      ;for both platforms and leave
    Enemy_Y_Speed[Y] = A
    //> sta Enemy_Y_MoveForce,y
    Enemy_Y_MoveForce[Y] = A
    //> rts
    return
}

// Decompiled from PositionPlayerOnHPlat
fun positionPlayerOnHPlat() {
    var X: Int = 0
    //> PositionPlayerOnHPlat:
    //> lda Player_X_Position
    //> clc                       ;add saved value from second subroutine to
    //> adc $00                   ;current player's position to position
    //> sta Player_X_Position     ;player accordingly in horizontal position
    Player_X_Position = (Player_X_Position + zp_00 + 0) and 0xFF
    //> lda Player_PageLoc        ;get player's page location
    //> ldy $00                   ;check to see if saved value here is positive or negative
    //> bmi PPHSubt               ;if negative, branch to subtract
    if (!negativeFlag) {
        //> adc #$00                  ;otherwise add carry to page location
        //> jmp SetPVar               ;jump to skip subtraction
    } else {
        //> PPHSubt: sbc #$00                  ;subtract borrow from page location
    }
    //> SetPVar: sta Player_PageLoc        ;save result to player's page location
    Player_PageLoc = ((Player_PageLoc + 0x00 + 0) and 0xFF - 0x00 - (1 - 0)) and 0xFF
    //> sty Platform_X_Scroll     ;put saved value from second sub here to be used later
    Platform_X_Scroll = zp_00
    //> jsr PositionPlayerOnVPlat ;position player vertically and appropriately
    positionPlayerOnVPlat(X)
    //> ExXMP:   rts                       ;and we are done here
    return
}

// Decompiled from MoveSmallPlatform
fun moveSmallPlatform(X: Int) {
    //> MoveSmallPlatform:
    //> jsr MoveLiftPlatforms      ;execute common to all large and small lift platforms
    moveLiftPlatforms(X)
    //> jmp ChkSmallPlatCollision  ;branch to position player correctly
    //> ChkSmallPlatCollision:
    //> lda PlatformCollisionFlag,x ;get bounding box counter saved in collision flag
    //> beq ExLiftP                 ;if none found, leave player position alone
    if (!zeroFlag) {
        //> jsr PositionPlayerOnS_Plat  ;use to position player correctly
        positionplayeronsPlat(PlatformCollisionFlag[X], X)
    }
    //> ExLiftP: rts                         ;then leave
    return
}

// Decompiled from MoveLiftPlatforms
fun moveLiftPlatforms(X: Int) {
    //> MoveLiftPlatforms:
    //> lda TimerControl         ;if master timer control set, skip all of this
    //> bne ExLiftP              ;and branch to leave
    if (zeroFlag) {
        //> lda Enemy_YMF_Dummy,x
        //> clc                      ;add contents of movement amount to whatever's here
        //> adc Enemy_Y_MoveForce,x
        //> sta Enemy_YMF_Dummy,x
        Enemy_YMF_Dummy[X] = (Enemy_YMF_Dummy[X] + Enemy_Y_MoveForce[X] + 0) and 0xFF
        //> lda Enemy_Y_Position,x   ;add whatever vertical speed is set to current
        //> adc Enemy_Y_Speed,x      ;vertical position plus carry to move up or down
        //> sta Enemy_Y_Position,x   ;and then leave
        Enemy_Y_Position[X] = (Enemy_Y_Position[X] + Enemy_Y_Speed[X] + 0) and 0xFF
        //> rts
        return
    } else {
        //> ExLiftP: rts                         ;then leave
        return
    }
}

// Decompiled from OffscreenBoundsCheck
fun offscreenBoundsCheck(X: Int) {
    var Y: Int = 0
    //> OffscreenBoundsCheck:
    //> lda Enemy_ID,x          ;check for cheep-cheep object
    //> cmp #FlyingCheepCheep   ;branch to leave if found
    //> beq ExScrnBd
    if (!(Enemy_ID[X] - FlyingCheepCheep == 0)) {
        //> lda ScreenLeft_X_Pos    ;get horizontal coordinate for left side of screen
        //> ldy Enemy_ID,x
        //> cpy #HammerBro          ;check for hammer bro object
        //> beq LimitB
        if (!(Enemy_ID[X] == HammerBro)) {
            //> cpy #PiranhaPlant       ;check for piranha plant object
            //> bne ExtendLB            ;these two will be erased sooner than others if too far left
            if (Y == PiranhaPlant) {
                //> LimitB:   adc #$38                ;add 56 pixels to coordinate if hammer bro or piranha plant
            }
        }
        //> ExtendLB: sbc #$48                ;subtract 72 pixels regardless of enemy object
        //> sta $01                 ;store result here
        zp_01 = ((ScreenLeft_X_Pos + 0x38 + Enemy_ID[X] >= PiranhaPlant) and 0xFF - 0x48 - (1 - Enemy_ID[X] >= PiranhaPlant)) and 0xFF
        //> lda ScreenLeft_PageLoc
        //> sbc #$00                ;subtract borrow from page location of left side
        //> sta $00                 ;store result here
        zp_00 = (ScreenLeft_PageLoc - 0x00 - (1 - Enemy_ID[X] >= PiranhaPlant)) and 0xFF
        //> lda ScreenRight_X_Pos   ;add 72 pixels to the right side horizontal coordinate
        //> adc #$48
        //> sta $03                 ;store result here
        zp_03 = (ScreenRight_X_Pos + 0x48 + Enemy_ID[X] >= PiranhaPlant) and 0xFF
        //> lda ScreenRight_PageLoc
        //> adc #$00                ;then add the carry to the page location
        //> sta $02                 ;and store result here
        zp_02 = (ScreenRight_PageLoc + 0x00 + Enemy_ID[X] >= PiranhaPlant) and 0xFF
        //> lda Enemy_X_Position,x  ;compare horizontal coordinate of the enemy object
        //> cmp $01                 ;to modified horizontal left edge coordinate to get carry
        //> lda Enemy_PageLoc,x
        //> sbc $00                 ;then subtract it from the page coordinate of the enemy object
        //> bmi TooFar              ;if enemy object is too far left, branch to erase it
        if (!(Enemy_X_Position[X] - zp_01 < 0)) {
            //> lda Enemy_X_Position,x  ;compare horizontal coordinate of the enemy object
            //> cmp $03                 ;to modified horizontal right edge coordinate to get carry
            //> lda Enemy_PageLoc,x
            //> sbc $02                 ;then subtract it from the page coordinate of the enemy object
            //> bmi ExScrnBd            ;if enemy object is on the screen, leave, do not erase enemy
            if (!(Enemy_X_Position[X] - zp_03 < 0)) {
                //> lda Enemy_State,x       ;if at this point, enemy is offscreen to the right, so check
                //> cmp #HammerBro          ;if in state used by spiny's egg, do not erase
                //> beq ExScrnBd
                if (!(Enemy_State[X] - HammerBro == 0)) {
                    //> cpy #PiranhaPlant       ;if piranha plant, do not erase
                    //> beq ExScrnBd
                    if (!(Y == PiranhaPlant)) {
                        //> cpy #FlagpoleFlagObject ;if flagpole flag, do not erase
                        //> beq ExScrnBd
                        if (!(Y == FlagpoleFlagObject)) {
                            //> cpy #StarFlagObject     ;if star flag, do not erase
                            //> beq ExScrnBd
                            if (!(Y == StarFlagObject)) {
                                //> cpy #JumpspringObject   ;if jumpspring, do not erase
                                //> beq ExScrnBd            ;erase all others too far to the right
                                if (!(Y == JumpspringObject)) {
                                    //> TooFar:   jsr EraseEnemyObject    ;erase object if necessary
                                    eraseEnemyObject(X)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    //> ExScrnBd: rts                     ;leave
    return
}

// Decompiled from FireballEnemyCollision
fun fireballEnemyCollision(X: Int) {
    var A: Int = 0
    //> FireballEnemyCollision:
    //> lda Fireball_State,x  ;check to see if fireball state is set at all
    //> beq ExitFBallEnemy    ;branch to leave if not
    if (!zeroFlag) {
        //> asl
        //> bcs ExitFBallEnemy    ;branch to leave also if d7 in state is set
        if (!carryFlag) {
            //> lda FrameCounter
            //> lsr                   ;get LSB of frame counter
            //> bcs ExitFBallEnemy    ;branch to leave if set (do routine every other frame)
            if (!carryFlag) {
                //> txa
                //> asl                   ;multiply fireball offset by four
                //> asl
                //> clc
                //> adc #$1c              ;then add $1c or 28 bytes to it
                //> tay                   ;to use fireball's bounding box coordinates
                //> ldx #$04
                while (negativeFlag) {
                    //> lda Enemy_Flag,x            ;check to see if buffer flag is set
                    //> beq NoFToECol               ;if not, skip to next enemy slot
                    if (!zeroFlag) {
                        //> lda Enemy_ID,x              ;check enemy identifier
                        //> cmp #$24
                        //> bcc GoombaDie               ;if < $24, branch to check further
                        if (Enemy_ID[X] >= 0x24) {
                            //> cmp #$2b
                            //> bcc NoFToECol               ;if in range $24-$2a, skip to next enemy slot
                            if (A >= 0x2B) {
                                //> GoombaDie: cmp #Goomba                 ;check for goomba identifier
                                //> bne NotGoomba               ;if not found, continue with code
                                if (A - Goomba == 0) {
                                    //> lda Enemy_State,x           ;otherwise check for defeated state
                                    //> cmp #$02                    ;if stomped or otherwise defeated,
                                    //> bcs NoFToECol               ;skip to next enemy slot
                                    if (!(Enemy_State[X] >= 0x02)) {
                                        //> NotGoomba: lda EnemyOffscrBitsMasked,x ;if any masked offscreen bits set,
                                        //> bne NoFToECol               ;skip to next enemy slot
                                        if (zeroFlag) {
                                            //> txa
                                            //> asl                         ;otherwise multiply enemy offset by four
                                            //> asl
                                            //> clc
                                            //> adc #$04                    ;add 4 bytes to it
                                            //> tax                         ;to use enemy's bounding box coordinates
                                            //> jsr SprObjectCollisionCore  ;do fireball-to-enemy collision detection
                                            sprObjectCollisionCore((((0x04 shl 1) and 0xFF shl 1) and 0xFF + 0x04 + 0) and 0xFF, (((X shl 1) and 0xFF shl 1) and 0xFF + 0x1C + 0) and 0xFF)
                                            //> ldx ObjectOffset            ;return fireball's original offset
                                            //> bcc NoFToECol               ;if carry clear, no collision, thus do next enemy slot
                                            if (0) {
                                                //> lda #%10000000
                                                //> sta Fireball_State,x        ;set d7 in enemy state
                                                Fireball_State[ObjectOffset] = 0x80
                                                //> ldx $01                     ;get enemy offset
                                                //> jsr HandleEnemyFBallCol     ;jump to handle fireball to enemy collision
                                                handleEnemyFBallCol()
                                            }
                                        }
                                    }
                                }
                                if (zeroFlag) {
                                    if (0) {
                                    }
                                }
                            }
                        }
                        if (A - Goomba == 0) {
                            if (!(Enemy_State[X] >= 0x02)) {
                                if (zeroFlag) {
                                    if (0) {
                                    }
                                }
                            }
                        }
                        if (zeroFlag) {
                            if (0) {
                            }
                        }
                    }
                    do {
                        //> FireballEnemyCDLoop:
                        //> stx $01                     ;store enemy object offset here
                        zp_01 = zp_01
                        //> tya
                        //> pha                         ;push fireball offset to the stack
                        //> lda Enemy_State,x
                        //> and #%00100000              ;check to see if d5 is set in enemy state
                        //> bne NoFToECol               ;if so, skip to next enemy slot
                        //> NoFToECol: pla                         ;pull fireball offset from stack
                        //> tay                         ;put it in Y
                        //> ldx $01                     ;get enemy object offset
                        //> dex                         ;decrement it
                        //> bpl FireballEnemyCDLoop     ;loop back until collision detection done on all enemies
                    } while (!negativeFlag)
                }
            }
        }
    }
    //> ExitFBallEnemy:
    //> ldx ObjectOffset                 ;get original fireball offset and leave
    //> rts
    return
}

// Decompiled from HandleEnemyFBallCol
fun handleEnemyFBallCol() {
    var A: Int = 0
    var X: Int = 0
    //> HandleEnemyFBallCol:
    //> jsr RelativeEnemyPosition  ;get relative coordinate of enemy
    relativeEnemyPosition()
    //> ldx $01                    ;get current enemy object offset
    //> lda Enemy_Flag,x           ;check buffer flag for d7 set
    //> bpl ChkBuzzyBeetle         ;branch if not set to continue
    if (negativeFlag) {
        //> and #%00001111             ;otherwise mask out high nybble and
        //> tax                        ;use low nybble as enemy offset
        //> lda Enemy_ID,x
        //> cmp #Bowser                ;check enemy identifier for bowser
        //> beq HurtBowser             ;branch if found
        if (!(Enemy_ID[A and 0x0F] - Bowser == 0)) {
            //> ldx $01                    ;otherwise retrieve current enemy offset
            //> ChkBuzzyBeetle:
            //> lda Enemy_ID,x
            //> cmp #BuzzyBeetle           ;check for buzzy beetle
            //> beq ExHCF                  ;branch if found to leave (buzzy beetles fireproof)
            if (!(Enemy_ID[X] - BuzzyBeetle == 0)) {
                //> cmp #Bowser                ;check for bowser one more time (necessary if d7 of flag was clear)
                //> bne ChkOtherEnemies        ;if not found, branch to check other enemies
                if (A - Bowser == 0) {
                    //> HurtBowser:
                    //> dec BowserHitPoints        ;decrement bowser's hit points
                    BowserHitPoints = (BowserHitPoints - 1) and 0xFF
                    //> bne ExHCF                  ;if bowser still has hit points, branch to leave
                    if (zeroFlag) {
                        //> jsr InitVStf               ;otherwise do sub to init vertical speed and movement force
                        initVStf(zp_01)
                        //> sta Enemy_X_Speed,x        ;initialize horizontal speed
                        Enemy_X_Speed[zp_01] = Enemy_ID[zp_01]
                        //> sta EnemyFrenzyBuffer      ;init enemy frenzy buffer
                        EnemyFrenzyBuffer = Enemy_ID[zp_01]
                        //> lda #$fe
                        //> sta Enemy_Y_Speed,x        ;set vertical speed to make defeated bowser jump a little
                        Enemy_Y_Speed[zp_01] = 0xFE
                        //> ldy WorldNumber            ;use world number as offset
                        //> lda BowserIdentities,y     ;get enemy identifier to replace bowser with
                        //> sta Enemy_ID,x             ;set as new enemy identifier
                        Enemy_ID[zp_01] = BowserIdentities[WorldNumber]
                        //> lda #$20                   ;set A to use starting value for state
                        //> cpy #$03                   ;check to see if using offset of 3 or more
                        //> bcs SetDBSte               ;branch if so
                        if (!(WorldNumber >= 0x03)) {
                            //> ora #$03                   ;otherwise add 3 to enemy state
                        }
                        //> SetDBSte: sta Enemy_State,x          ;set defeated enemy state
                        Enemy_State[zp_01] = 0x20 or 0x03
                        //> lda #Sfx_BowserFall
                        //> sta Square2SoundQueue      ;load bowser defeat sound
                        Square2SoundQueue = Sfx_BowserFall
                        //> ldx $01                    ;get enemy offset
                        //> lda #$09                   ;award 5000 points to player for defeating bowser
                        //> bne EnemySmackScore        ;unconditional branch to award points
                        if (zeroFlag) {
                            //> ChkOtherEnemies:
                            //> cmp #BulletBill_FrenzyVar
                            //> beq ExHCF                 ;branch to leave if bullet bill (frenzy variant)
                            if (!(A - BulletBill_FrenzyVar == 0)) {
                                //> cmp #Podoboo
                                //> beq ExHCF                 ;branch to leave if podoboo
                                if (!(A - Podoboo == 0)) {
                                    //> cmp #$15
                                    //> bcs ExHCF                 ;branch to leave if identifier => $15
                                    if (!(A >= 0x15)) {
                                        //> EnemySmackScore:
                                        //> jsr SetupFloateyNumber   ;update necessary score variables
                                        setupFloateyNumber(0x09, zp_01)
                                        //> lda #Sfx_EnemySmack      ;play smack enemy sound
                                        //> sta Square1SoundQueue
                                        Square1SoundQueue = Sfx_EnemySmack
                                    }
                                }
                            }
                        }
                    }
                }
                if (!(A - BulletBill_FrenzyVar == 0)) {
                    if (!(A - Podoboo == 0)) {
                        if (!(A >= 0x15)) {
                        }
                    }
                }
            }
        }
    }
    if (!(Enemy_ID[X] - BuzzyBeetle == 0)) {
        if (A - Bowser == 0) {
            if (zeroFlag) {
                if (!(WorldNumber >= 0x03)) {
                }
                if (zeroFlag) {
                    if (!(A - BulletBill_FrenzyVar == 0)) {
                        if (!(A - Podoboo == 0)) {
                            if (!(A >= 0x15)) {
                            }
                        }
                    }
                }
            }
        }
        if (!(A - BulletBill_FrenzyVar == 0)) {
            if (!(A - Podoboo == 0)) {
                if (!(A >= 0x15)) {
                }
            }
        }
    }
    //> ExHCF: rts                      ;and now let's leave
    return
}

// Decompiled from ShellOrBlockDefeat
fun shellOrBlockDefeat(X: Int) {
    var Y: Int = 0
    //> ShellOrBlockDefeat:
    //> lda Enemy_ID,x            ;check for piranha plant
    //> cmp #PiranhaPlant
    //> bne StnE                  ;branch if not found
    if (Enemy_ID[X] - PiranhaPlant == 0) {
        //> lda Enemy_Y_Position,x
        //> adc #$18                  ;add 24 pixels to enemy object's vertical position
        //> sta Enemy_Y_Position,x
        Enemy_Y_Position[X] = (Enemy_Y_Position[X] + 0x18 + Enemy_ID[X] >= PiranhaPlant) and 0xFF
    }
    //> StnE: jsr ChkToStunEnemies      ;do yet another sub
    chkToStunEnemies((Enemy_Y_Position[X] + 0x18 + Enemy_ID[X] >= PiranhaPlant) and 0xFF, X)
    //> lda Enemy_State,x
    //> and #%00011111            ;mask out 2 MSB of enemy object's state
    //> ora #%00100000            ;set d5 to defeat enemy and save as new state
    //> sta Enemy_State,x
    Enemy_State[X] = Enemy_State[X] and 0x1F or 0x20
    //> lda #$02                  ;award 200 points by default
    //> ldy Enemy_ID,x            ;check for hammer bro
    //> cpy #HammerBro
    //> bne GoombaPoints          ;branch if not found
    if (Enemy_ID[X] == HammerBro) {
        //> lda #$06                  ;award 1000 points for hammer bro
    }
    //> GoombaPoints:
    //> cpy #Goomba               ;check for goomba
    //> bne EnemySmackScore       ;branch if not found
    if (Y == Goomba) {
        //> lda #$01                  ;award 100 points for goomba
    }
    //> EnemySmackScore:
    //> jsr SetupFloateyNumber   ;update necessary score variables
    setupFloateyNumber(0x01, X)
    //> lda #Sfx_EnemySmack      ;play smack enemy sound
    //> sta Square1SoundQueue
    Square1SoundQueue = Sfx_EnemySmack
    //> ExHCF: rts                      ;and now let's leave
    return
}

// Decompiled from PlayerHammerCollision
fun playerHammerCollision(X: Int) {
    //> PlayerHammerCollision:
    //> lda FrameCounter          ;get frame counter
    //> lsr                       ;shift d0 into carry
    //> bcc ExPHC                 ;branch to leave if d0 not set to execute every other frame
    if (carryFlag) {
        //> lda TimerControl          ;if either master timer control
        //> ora Misc_OffscreenBits    ;or any offscreen bits for hammer are set,
        //> bne ExPHC                 ;branch to leave
        if (zeroFlag) {
            //> txa
            //> asl                       ;multiply misc object offset by four
            //> asl
            //> clc
            //> adc #$24                  ;add 36 or $24 bytes to get proper offset
            //> tay                       ;for misc object bounding box coordinates
            //> jsr PlayerCollisionCore   ;do player-to-hammer collision detection
            playerCollisionCore()
            //> ldx ObjectOffset          ;get misc object offset
            //> bcc ClHCol                ;if no collision, then branch
            if (0) {
                //> lda Misc_Collision_Flag,x ;otherwise read collision flag
                //> bne ExPHC                 ;if collision flag already set, branch to leave
                if (zeroFlag) {
                    //> lda #$01
                    //> sta Misc_Collision_Flag,x ;otherwise set collision flag now
                    Misc_Collision_Flag[ObjectOffset] = 0x01
                    //> lda Misc_X_Speed,x
                    //> eor #$ff                  ;get two's compliment of
                    //> clc                       ;hammer's horizontal speed
                    //> adc #$01
                    //> sta Misc_X_Speed,x        ;set to send hammer flying the opposite direction
                    Misc_X_Speed[ObjectOffset] = (Misc_X_Speed[ObjectOffset] xor 0xFF + 0x01 + 0) and 0xFF
                    //> lda StarInvincibleTimer   ;if star mario invincibility timer set,
                    //> bne ExPHC                 ;branch to leave
                    if (zeroFlag) {
                        //> jmp InjurePlayer          ;otherwise jump to hurt player, do not return
                        //> ClHCol: lda #$00                  ;clear collision flag
                        //> sta Misc_Collision_Flag,x
                        Misc_Collision_Flag[ObjectOffset] = 0x00
                    }
                }
            }
        }
    }
    //> ExPHC:  rts
    return
}

// Decompiled from PlayerEnemyCollision
fun playerEnemyCollision(X: Int) {
    var A: Int = 0
    //> HandlePowerUpCollision:
    //> jsr EraseEnemyObject    ;erase the power-up object
    eraseEnemyObject(X)
    //> lda #$06
    //> jsr SetupFloateyNumber  ;award 1000 points to player by default
    setupFloateyNumber(0x06, X)
    //> lda #Sfx_PowerUpGrab
    //> sta Square2SoundQueue   ;play the power-up sound
    Square2SoundQueue = Sfx_PowerUpGrab
    //> lda PowerUpType         ;check power-up type
    //> cmp #$02
    //> bcc Shroom_Flower_PUp   ;if mushroom or fire flower, branch
    if (PowerUpType >= 0x02) {
        //> cmp #$03
        //> beq SetFor1Up           ;if 1-up mushroom, branch
        if (!(A - 0x03 == 0)) {
            //> lda #$23                ;otherwise set star mario invincibility
            //> sta StarInvincibleTimer ;timer, and load the star mario music
            StarInvincibleTimer = 0x23
            //> lda #StarPowerMusic     ;into the area music queue, then leave
            //> sta AreaMusicQueue
            AreaMusicQueue = StarPowerMusic
            //> rts
            return
            //> Shroom_Flower_PUp:
            //> lda PlayerStatus    ;if player status = small, branch
            //> beq UpToSuper
            if (!zeroFlag) {
                //> cmp #$01            ;if player status not super, leave
                //> bne NoPUp
                if (A - 0x01 == 0) {
                    //> ldx ObjectOffset    ;get enemy offset, not necessary
                    //> lda #$02            ;set player status to fiery
                    //> sta PlayerStatus
                    PlayerStatus = 0x02
                    //> jsr GetPlayerColors ;run sub to change colors of player
                    getPlayerColors()
                    //> ldx ObjectOffset    ;get enemy offset again, and again not necessary
                    //> lda #$0c            ;set value to be used by subroutine tree (fiery)
                    //> jmp UpToFiery       ;jump to set values accordingly
                    //> SetFor1Up:
                    //> lda #$0b                 ;change 1000 points into 1-up instead
                    //> sta FloateyNum_Control,x ;and then leave
                    FloateyNum_Control[ObjectOffset] = 0x0B
                    //> rts
                    return
                    //> UpToSuper:
                    //> lda #$01         ;set player status to super
                    //> sta PlayerStatus
                    PlayerStatus = 0x01
                    //> lda #$09         ;set value to be used by subroutine tree (super)
                    //> UpToFiery:
                    //> ldy #$00         ;set value to be used as new player state
                    //> jsr SetPRout     ;set values to stop certain things in motion
                    setPRout(0x09, 0x00)
                }
            }
        }
    }
    if (!zeroFlag) {
        if (A - 0x01 == 0) {
        }
    }
    //> NoPUp: rts
    return
}

// Decompiled from InjurePlayer
fun injurePlayer() {
    //> InjurePlayer:
    //> lda InjuryTimer          ;check again to see if injured invincibility timer is
    //> bne ExInjColRoutines     ;at zero, and branch to leave if so
    if (zeroFlag) {
    }
    //> ExInjColRoutines:
    //> ldx ObjectOffset              ;get enemy offset and leave
    //> rts
    return
}

// Decompiled from ForceInjury
fun forceInjury(A: Int) {
    var X: Int = 0
    //> ForceInjury:
    //> ldx PlayerStatus          ;check player's status
    //> beq KillPlayer            ;branch if small
    if (!zeroFlag) {
        //> sta PlayerStatus          ;otherwise set player's status to small
        PlayerStatus = A
        //> lda #$08
        //> sta InjuryTimer           ;set injured invincibility timer
        InjuryTimer = 0x08
        //> asl
        //> sta Square1SoundQueue     ;play pipedown/injury sound
        Square1SoundQueue = (0x08 shl 1) and 0xFF
        //> jsr GetPlayerColors       ;change player's palette if necessary
        getPlayerColors()
        //> lda #$0a                  ;set subroutine to run on next frame
        //> SetKRout: ldy #$01                  ;set new player state
    }
    do {
        //> KillPlayer:
        //> stx Player_X_Speed   ;halt player's horizontal movement by initializing speed
        Player_X_Speed = PlayerStatus
        //> inx
        //> stx EventMusicQueue  ;set event music queue to death music
        EventMusicQueue = (PlayerStatus + 1) and 0xFF
        //> lda #$fc
        //> sta Player_Y_Speed   ;set new vertical speed
        Player_Y_Speed = 0xFC
        //> lda #$0b             ;set subroutine to run on next frame
        //> bne SetKRout         ;branch to set player's state and other things
    } while (!zeroFlag)
    //> StompedEnemyPtsData:
    //> .db $02, $06, $05, $06
    //> EnemyStomped:
    //> lda Enemy_ID,x             ;check for spiny, branch to hurt player
    //> cmp #Spiny                 ;if found
    //> beq InjurePlayer
    //> lda #Sfx_EnemyStomp        ;otherwise play stomp/swim sound
    //> sta Square1SoundQueue
    Square1SoundQueue = Sfx_EnemyStomp
    //> lda Enemy_ID,x
    //> ldy #$00                   ;initialize points data offset for stomped enemies
    //> cmp #FlyingCheepCheep      ;branch for cheep-cheep
    //> beq EnemyStompedPts
    if (!(Enemy_ID[X] - FlyingCheepCheep == 0)) {
        //> cmp #BulletBill_FrenzyVar  ;branch for either bullet bill object
        //> beq EnemyStompedPts
        if (!(A - BulletBill_FrenzyVar == 0)) {
            //> cmp #BulletBill_CannonVar
            //> beq EnemyStompedPts
            if (!(A - BulletBill_CannonVar == 0)) {
                //> cmp #Podoboo               ;branch for podoboo (this branch is logically impossible
                //> beq EnemyStompedPts        ;for cpu to take due to earlier checking of podoboo)
                if (!(A - Podoboo == 0)) {
                    //> iny                        ;increment points data offset
                    //> cmp #HammerBro             ;branch for hammer bro
                    //> beq EnemyStompedPts
                    if (!(A - HammerBro == 0)) {
                        //> iny                        ;increment points data offset
                        //> cmp #Lakitu                ;branch for lakitu
                        //> beq EnemyStompedPts
                        if (!(A - Lakitu == 0)) {
                            //> iny                        ;increment points data offset
                            //> cmp #Bloober               ;branch if NOT bloober
                            //> bne ChkForDemoteKoopa
                            if (A - Bloober == 0) {
                                //> EnemyStompedPts:
                                //> lda StompedEnemyPtsData,y  ;load points data using offset in Y
                                //> jsr SetupFloateyNumber     ;run sub to set floatey number controls
                                setupFloateyNumber(StompedEnemyPtsData[(((0x00 + 1) and 0xFF + 1) and 0xFF + 1) and 0xFF], (PlayerStatus + 1) and 0xFF)
                                //> lda Enemy_MovingDir,x
                                //> pha                        ;save enemy movement direction to stack
                                //> jsr SetStun                ;run sub to kill enemy
                                setStun((PlayerStatus + 1) and 0xFF)
                                //> pla
                                //> sta Enemy_MovingDir,x      ;return enemy movement direction from stack
                                Enemy_MovingDir[(PlayerStatus + 1) and 0xFF] = Enemy_MovingDir[(PlayerStatus + 1) and 0xFF]
                                //> lda #%00100000
                                //> sta Enemy_State,x          ;set d5 in enemy state
                                Enemy_State[(PlayerStatus + 1) and 0xFF] = 0x20
                                //> jsr InitVStf               ;nullify vertical speed, physics-related thing,
                                initVStf((PlayerStatus + 1) and 0xFF)
                                //> sta Enemy_X_Speed,x        ;and horizontal speed
                                Enemy_X_Speed[(PlayerStatus + 1) and 0xFF] = 0x20
                                //> lda #$fd                   ;set player's vertical speed, to give bounce
                                //> sta Player_Y_Speed
                                Player_Y_Speed = 0xFD
                                //> rts
                                return
                            }
                        }
                    }
                }
            }
        }
    }
    //> ChkForDemoteKoopa:
    //> cmp #$09                   ;branch elsewhere if enemy object < $09
    //> bcc HandleStompedShellE
    if (A >= 0x09) {
        //> and #%00000001             ;demote koopa paratroopas to ordinary troopas
        //> sta Enemy_ID,x
        Enemy_ID[(PlayerStatus + 1) and 0xFF] = 0xFD and 0x01
        //> ldy #$00                   ;return enemy to normal state
        //> sty Enemy_State,x
        Enemy_State[(PlayerStatus + 1) and 0xFF] = 0x00
        //> lda #$03                   ;award 400 points to the player
        //> jsr SetupFloateyNumber
        setupFloateyNumber(0x03, (PlayerStatus + 1) and 0xFF)
        //> jsr InitVStf               ;nullify physics-related thing and vertical speed
        initVStf((PlayerStatus + 1) and 0xFF)
        //> jsr EnemyFacePlayer        ;turn enemy around if necessary
        enemyFacePlayer((PlayerStatus + 1) and 0xFF)
        //> lda DemotedKoopaXSpdData,y
        //> sta Enemy_X_Speed,x        ;set appropriate moving speed based on direction
        Enemy_X_Speed[(PlayerStatus + 1) and 0xFF] = DemotedKoopaXSpdData[0x00]
        //> jmp SBnce                  ;then move onto something else
    } else {
        //> HandleStompedShellE:
        //> lda #$04                   ;set defeated state for enemy
        //> sta Enemy_State,x
        Enemy_State[(PlayerStatus + 1) and 0xFF] = 0x04
        //> inc StompChainCounter      ;increment the stomp counter
        StompChainCounter = (StompChainCounter + 1) and 0xFF
        //> lda StompChainCounter      ;add whatever is in the stomp counter
        //> clc                        ;to whatever is in the stomp timer
        //> adc StompTimer
        //> jsr SetupFloateyNumber     ;award points accordingly
        setupFloateyNumber((StompChainCounter + StompTimer + 0) and 0xFF, (PlayerStatus + 1) and 0xFF)
        //> inc StompTimer             ;increment stomp timer of some sort
        StompTimer = (StompTimer + 1) and 0xFF
        //> ldy PrimaryHardMode        ;check primary hard mode flag
        //> lda RevivalRateData,y      ;load timer setting according to flag
        //> sta EnemyIntervalTimer,x   ;set as enemy timer to revive stomped enemy
        EnemyIntervalTimer[(PlayerStatus + 1) and 0xFF] = RevivalRateData[PrimaryHardMode]
    }
    //> SBnce: lda #$fc                   ;set player's vertical speed for bounce
    //> sta Player_Y_Speed         ;and then leave!!!
    Player_Y_Speed = 0xFC
    //> rts
    return
}

// Decompiled from SetPRout
fun setPRout(A: Int, Y: Int) {
    //> SetPRout: sta GameEngineSubroutine  ;load new value to run subroutine on next frame
    GameEngineSubroutine = A
    //> sty Player_State          ;store new player state
    Player_State = Y
    //> ldy #$ff
    //> sty TimerControl          ;set master timer control flag to halt timers
    TimerControl = 0xFF
    //> iny
    //> sty ScrollAmount          ;initialize scroll speed
    ScrollAmount = (0xFF + 1) and 0xFF
    //> ExInjColRoutines:
    //> ldx ObjectOffset              ;get enemy offset and leave
    //> rts
    return
}

// Decompiled from EnemyFacePlayer
fun enemyFacePlayer(X: Int) {
    //> EnemyFacePlayer:
    //> ldy #$01               ;set to move right by default
    //> jsr PlayerEnemyDiff    ;get horizontal difference between player and enemy
    playerEnemyDiff(X)
    //> bpl SFcRt              ;if enemy is to the right of player, do not increment
    if (negativeFlag) {
        //> iny                    ;otherwise, increment to set to move to the left
    }
    //> SFcRt: sty Enemy_MovingDir,x  ;set moving direction here
    Enemy_MovingDir[X] = (0x01 + 1) and 0xFF
    //> dey                    ;then decrement to use as a proper offset
    //> rts
    return
}

// Decompiled from SetupFloateyNumber
fun setupFloateyNumber(A: Int, X: Int) {
    //> SetupFloateyNumber:
    //> sta FloateyNum_Control,x ;set number of points control for floatey numbers
    FloateyNum_Control[X] = A
    //> lda #$30
    //> sta FloateyNum_Timer,x   ;set timer for floatey numbers
    FloateyNum_Timer[X] = 0x30
    //> lda Enemy_Y_Position,x
    //> sta FloateyNum_Y_Pos,x   ;set vertical coordinate
    FloateyNum_Y_Pos[X] = Enemy_Y_Position[X]
    //> lda Enemy_Rel_XPos
    //> sta FloateyNum_X_Pos,x   ;set horizontal coordinate and leave
    FloateyNum_X_Pos[X] = Enemy_Rel_XPos
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
    //> ProcEnemyCollisions:
    //> lda Enemy_State,y        ;check both enemy states for d5 set
    //> ora Enemy_State,x
    //> and #%00100000           ;if d5 is set in either state, or both, branch
    //> bne ExitProcessEColl     ;to leave and do nothing else at this point
    if (zeroFlag) {
        //> lda Enemy_State,x
        //> cmp #$06                 ;if second enemy state < $06, branch elsewhere
        //> bcc ProcSecondEnemyColl
        if (Enemy_State[X] >= 0x06) {
            //> lda Enemy_ID,x           ;check second enemy identifier for hammer bro
            //> cmp #HammerBro           ;if hammer bro found in alt state, branch to leave
            //> beq ExitProcessEColl
            if (!(Enemy_ID[X] - HammerBro == 0)) {
                //> lda Enemy_State,y        ;check first enemy state for d7 set
                //> asl
                //> bcc ShellCollisions      ;branch if d7 is clear
                if (carryFlag) {
                    //> lda #$06
                    //> jsr SetupFloateyNumber   ;award 1000 points for killing enemy
                    setupFloateyNumber(0x06, X)
                    //> jsr ShellOrBlockDefeat   ;then kill enemy, then load
                    shellOrBlockDefeat(X)
                    //> ldy $01                  ;original offset of second enemy
                }
                //> ShellCollisions:
                //> tya                      ;move Y to X
                //> tax
                //> jsr ShellOrBlockDefeat   ;kill second enemy
                shellOrBlockDefeat(zp_01)
                //> ldx ObjectOffset
                //> lda ShellChainCounter,x  ;get chain counter for shell
                //> clc
                //> adc #$04                 ;add four to get appropriate point offset
                //> ldx $01
                //> jsr SetupFloateyNumber   ;award appropriate number of points for second enemy
                setupFloateyNumber((ShellChainCounter[ObjectOffset] + 0x04 + 0) and 0xFF, zp_01)
                //> ldx ObjectOffset         ;load original offset of first enemy
                //> inc ShellChainCounter,x  ;increment chain counter for additional enemies
                ShellChainCounter[ObjectOffset] = (ShellChainCounter[ObjectOffset] + 1) and 0xFF
            }
            //> ExitProcessEColl:
            //> rts                      ;leave!!!
            return
        }
    }
    //> ProcSecondEnemyColl:
    //> lda Enemy_State,y        ;if first enemy state < $06, branch elsewhere
    //> cmp #$06
    //> bcc MoveEOfs
    if (Enemy_State[Y] >= 0x06) {
        do {
            //> lda Enemy_ID,y           ;check first enemy identifier for hammer bro
            //> cmp #HammerBro           ;if hammer bro found in alt state, branch to leave
            //> beq ExitProcessEColl
        } while (Enemy_ID[Y] - HammerBro == 0)
        //> jsr ShellOrBlockDefeat   ;otherwise, kill first enemy
        shellOrBlockDefeat(ObjectOffset)
        //> ldy $01
        //> lda ShellChainCounter,y  ;get chain counter for shell
        //> clc
        //> adc #$04                 ;add four to get appropriate point offset
        //> ldx ObjectOffset
        //> jsr SetupFloateyNumber   ;award appropriate number of points for first enemy
        setupFloateyNumber((ShellChainCounter[zp_01] + 0x04 + 0) and 0xFF, ObjectOffset)
        //> ldx $01                  ;load original offset of second enemy
        //> inc ShellChainCounter,x  ;increment chain counter for additional enemies
        ShellChainCounter[zp_01] = (ShellChainCounter[zp_01] + 1) and 0xFF
        //> rts                      ;leave!!!
        return
    } else {
        //> MoveEOfs:
        //> tya                      ;move Y ($01) to X
        //> tax
        //> jsr EnemyTurnAround      ;do the sub here using value from $01
        enemyTurnAround(zp_01)
        //> ldx ObjectOffset         ;then do it again using value from $08
    }
}

// Decompiled from EnemyTurnAround
fun enemyTurnAround(X: Int) {
    var A: Int = 0
    //> EnemyTurnAround:
    //> lda Enemy_ID,x           ;check for specific enemies
    //> cmp #PiranhaPlant
    //> beq ExTA                 ;if piranha plant, leave
    if (!(Enemy_ID[X] - PiranhaPlant == 0)) {
        //> cmp #Lakitu
        //> beq ExTA                 ;if lakitu, leave
        if (!(A - Lakitu == 0)) {
            //> cmp #HammerBro
            //> beq ExTA                 ;if hammer bro, leave
            if (!(A - HammerBro == 0)) {
                //> cmp #Spiny
                //> beq RXSpd                ;if spiny, turn it around
                if (!(A - Spiny == 0)) {
                    //> cmp #GreenParatroopaJump
                    //> beq RXSpd                ;if green paratroopa, turn it around
                    if (!(A - GreenParatroopaJump == 0)) {
                        //> cmp #$07
                        //> bcs ExTA                 ;if any OTHER enemy object => $07, leave
                        if (!(A >= 0x07)) {
                            //> RXSpd: lda Enemy_X_Speed,x      ;load horizontal speed
                            //> eor #$ff                 ;get two's compliment for horizontal speed
                            //> tay
                            //> iny
                            //> sty Enemy_X_Speed,x      ;store as new horizontal speed
                            Enemy_X_Speed[X] = (Enemy_X_Speed[X] xor 0xFF + 1) and 0xFF
                            //> lda Enemy_MovingDir,x
                            //> eor #%00000011           ;invert moving direction and store, then leave
                            //> sta Enemy_MovingDir,x    ;thus effectively turning the enemy around
                            Enemy_MovingDir[X] = Enemy_MovingDir[X] xor 0x03
                        }
                    }
                }
            }
        }
    }
    //> ExTA:  rts                      ;leave!!!
    return
}

// Decompiled from LargePlatformCollision
fun largePlatformCollision(X: Int) {
    //> LargePlatformCollision:
    //> lda #$ff                     ;save value here
    //> sta PlatformCollisionFlag,x
    PlatformCollisionFlag[X] = 0xFF
    //> lda TimerControl             ;check master timer control
    //> bne ExLPC                    ;if set, branch to leave
    if (zeroFlag) {
        //> lda Enemy_State,x            ;if d7 set in object state,
        //> bmi ExLPC                    ;branch to leave
        if (!negativeFlag) {
            //> lda Enemy_ID,x
            //> cmp #$24                     ;check enemy object identifier for
            //> bne ChkForPlayerC_LargeP     ;balance platform, branch if not found
            //> lda Enemy_State,x
            //> tax                          ;set state as enemy offset here
            //> jsr ChkForPlayerC_LargeP     ;perform code with state offset, then original offset, in X
            chkforplayercLargep(Enemy_State[X])
        }
    }
    //> ExLPC: ldx ObjectOffset             ;get enemy object buffer offset and leave
    //> rts
    return
}

// Decompiled from ChkForPlayerC_LargeP
fun chkforplayercLargep(X: Int) {
    var Y: Int = 0
    //> ChkForPlayerC_LargeP:
    //> jsr CheckPlayerVertical      ;figure out if player is below a certain point
    checkPlayerVertical()
    //> bcs ExLPC                    ;or offscreen, branch to leave if true
    if (!carryFlag) {
        //> txa
        //> jsr GetEnemyBoundBoxOfsArg   ;get bounding box offset in Y
        getEnemyBoundBoxOfsArg(X)
        //> lda Enemy_Y_Position,x       ;store vertical coordinate in
        //> sta $00                      ;temp variable for now
        zp_00 = Enemy_Y_Position[X]
        //> txa                          ;send offset we're on to the stack
        //> pha
        //> jsr PlayerCollisionCore      ;do player-to-platform collision detection
        playerCollisionCore()
        //> pla                          ;retrieve offset from the stack
        //> tax
        //> bcc ExLPC                    ;if no collision, branch to leave
        if (carryFlag) {
            //> jsr ProcLPlatCollisions      ;otherwise collision, perform sub
            procLPlatCollisions(X, Y)
        }
    }
    //> ExLPC: ldx ObjectOffset             ;get enemy object buffer offset and leave
    //> rts
    return
}

// Decompiled from SmallPlatformCollision
fun smallPlatformCollision(X: Int, Y: Int) {
    var A: Int = 0
    //> SmallPlatformCollision:
    //> lda TimerControl             ;if master timer control set,
    //> bne ExSPC                    ;branch to leave
    if (zeroFlag) {
        //> sta PlatformCollisionFlag,x  ;otherwise initialize collision flag
        PlatformCollisionFlag[X] = TimerControl
        //> jsr CheckPlayerVertical      ;do a sub to see if player is below a certain point
        checkPlayerVertical()
        //> bcs ExSPC                    ;or entirely offscreen, and branch to leave if true
        if (!carryFlag) {
            //> lda #$02
            //> sta $00                      ;load counter here for 2 bounding boxes
            zp_00 = 0x02
            while (zeroFlag) {
                //> lda BoundingBox_UL_YPos,y  ;check top of platform's bounding box for being
                //> cmp #$20                   ;above a specific point
                //> bcc MoveBoundBox           ;if so, branch, don't do collision detection
                if (BoundingBox_UL_YPos[Y] >= 0x20) {
                    //> jsr PlayerCollisionCore    ;otherwise, perform player-to-platform collision detection
                    playerCollisionCore()
                    //> bcs ProcSPlatCollisions    ;skip ahead if collision
                    if (!(A >= 0x20)) {
                        do {
                            //> ChkSmallPlatLoop:
                            //> ldx ObjectOffset           ;get enemy object offset
                            //> jsr GetEnemyBoundBoxOfs    ;get bounding box offset in Y
                            getEnemyBoundBoxOfs()
                            //> and #%00000010             ;if d1 of offscreen lower nybble bits was set
                            //> bne ExSPC                  ;then branch to leave
                            //> MoveBoundBox:
                            //> lda BoundingBox_UL_YPos,y  ;move bounding box vertical coordinates
                            //> clc                        ;128 pixels downwards
                            //> adc #$80
                            //> sta BoundingBox_UL_YPos,y
                            BoundingBox_UL_YPos[Y] = (BoundingBox_UL_YPos[Y] + 0x80 + 0) and 0xFF
                            //> lda BoundingBox_DR_YPos,y
                            //> clc
                            //> adc #$80
                            //> sta BoundingBox_DR_YPos,y
                            BoundingBox_DR_YPos[Y] = (BoundingBox_DR_YPos[Y] + 0x80 + 0) and 0xFF
                            //> dec $00                    ;decrement counter we set earlier
                            zp_00 = (zp_00 - 1) and 0xFF
                            //> bne ChkSmallPlatLoop       ;loop back until both bounding boxes are checked
                        } while (!zeroFlag)
                        //> ExSPC: ldx ObjectOffset           ;get enemy object buffer offset, then leave
                        //> rts
                        return
                    }
                }
                do {
                } while (!zeroFlag)
            }
        }
    }
    //> ProcSPlatCollisions:
    //> ldx ObjectOffset             ;return enemy object buffer offset to X, then continue
}

// Decompiled from ProcLPlatCollisions
fun procLPlatCollisions(X: Int, Y: Int) {
    //> ProcLPlatCollisions:
    //> lda BoundingBox_DR_YPos,y    ;get difference by subtracting the top
    //> sec                          ;of the player's bounding box from the bottom
    //> sbc BoundingBox_UL_YPos      ;of the platform's bounding box
    //> cmp #$04                     ;if difference too large or negative,
    //> bcs ChkForTopCollision       ;branch, do not alter vertical speed of player
    if (!((BoundingBox_DR_YPos[Y] - BoundingBox_UL_YPos - (1 - 1)) and 0xFF >= 0x04)) {
        //> lda Player_Y_Speed           ;check to see if player's vertical speed is moving down
        //> bpl ChkForTopCollision       ;if so, don't mess with it
        if (negativeFlag) {
            //> lda #$01                     ;otherwise, set vertical
            //> sta Player_Y_Speed           ;speed of player to kill jump
            Player_Y_Speed = 0x01
        }
    }
    //> ChkForTopCollision:
    //> lda BoundingBox_DR_YPos      ;get difference by subtracting the top
    //> sec                          ;of the platform's bounding box from the bottom
    //> sbc BoundingBox_UL_YPos,y    ;of the player's bounding box
    //> cmp #$06
    //> bcs PlatformSideCollisions   ;if difference not close enough, skip all of this
    if (!((BoundingBox_DR_YPos - BoundingBox_UL_YPos[Y] - (1 - 1)) and 0xFF >= 0x06)) {
        //> lda Player_Y_Speed
        //> bmi PlatformSideCollisions   ;if player's vertical speed moving upwards, skip this
        if (!negativeFlag) {
            //> lda $00                      ;get saved bounding box counter from earlier
            //> ldy Enemy_ID,x
            //> cpy #$2b                     ;if either of the two small platform objects are found,
            //> beq SetCollisionFlag         ;regardless of which one, branch to use bounding box counter
            if (!(Enemy_ID[X] == 0x2B)) {
                //> cpy #$2c                     ;as contents of collision flag
                //> beq SetCollisionFlag
                if (!(Y == 0x2C)) {
                    //> txa                          ;otherwise use enemy object buffer offset
                }
            }
            //> SetCollisionFlag:
            //> ldx ObjectOffset             ;get enemy object buffer offset
            //> sta PlatformCollisionFlag,x  ;save either bounding box counter or enemy offset here
            PlatformCollisionFlag[ObjectOffset] = X
            //> lda #$00
            //> sta Player_State             ;set player state to normal then leave
            Player_State = 0x00
            //> rts
            return
        }
    }
    //> PlatformSideCollisions:
    //> lda #$01                   ;set value here to indicate possible horizontal
    //> sta $00                    ;collision on left side of platform
    zp_00 = 0x01
    //> lda BoundingBox_DR_XPos    ;get difference by subtracting platform's left edge
    //> sec                        ;from player's right edge
    //> sbc BoundingBox_UL_XPos,y
    //> cmp #$08                   ;if difference close enough, skip all of this
    //> bcc SideC
    if ((BoundingBox_DR_XPos - BoundingBox_UL_XPos[Y] - (1 - 1)) and 0xFF >= 0x08) {
        //> inc $00                    ;otherwise increment value set here for right side collision
        zp_00 = (zp_00 + 1) and 0xFF
        //> lda BoundingBox_DR_XPos,y  ;get difference by subtracting player's left edge
        //> clc                        ;from platform's right edge
        //> sbc BoundingBox_UL_XPos
        //> cmp #$09                   ;if difference not close enough, skip subroutine
        //> bcs NoSideC                ;and instead branch to leave (no collision)
        if (!((BoundingBox_DR_XPos[Y] - BoundingBox_UL_XPos - (1 - 0)) and 0xFF >= 0x09)) {
            //> SideC:   jsr ImpedePlayerMove       ;deal with horizontal collision
            impedePlayerMove()
        }
    }
    //> NoSideC: ldx ObjectOffset           ;return with enemy object buffer offset
    //> rts
    return
}

// Decompiled from PositionPlayerOnS_Plat
fun positionplayeronsPlat(A: Int, X: Int) {
    //> PositionPlayerOnS_Plat:
    //> tay                        ;use bounding box counter saved in collision flag
    //> lda Enemy_Y_Position,x     ;for offset
    //> clc                        ;add positioning data using offset to the vertical
    //> adc PlayerPosSPlatData-1,y ;coordinate
    //> .db $2c                    ;BIT instruction opcode
}

// Decompiled from PositionPlayerOnVPlat
fun positionPlayerOnVPlat(X: Int) {
    //> PositionPlayerOnVPlat:
    //> lda Enemy_Y_Position,x    ;get vertical coordinate
    //> ldy GameEngineSubroutine
    //> cpy #$0b                  ;if certain routine being executed on this frame,
    //> beq ExPlPos               ;skip all of this
    if (!(GameEngineSubroutine == 0x0B)) {
        //> ldy Enemy_Y_HighPos,x
        //> cpy #$01                  ;if vertical high byte offscreen, skip this
        //> bne ExPlPos
        if (Enemy_Y_HighPos[X] == 0x01) {
            //> sec                       ;subtract 32 pixels from vertical coordinate
            //> sbc #$20                  ;for the player object's height
            //> sta Player_Y_Position     ;save as player's new vertical coordinate
            Player_Y_Position = (Enemy_Y_Position[X] - 0x20 - (1 - 1)) and 0xFF
            //> tya
            //> sbc #$00                  ;subtract borrow and store as player's
            //> sta Player_Y_HighPos      ;new vertical high byte
            Player_Y_HighPos = (Enemy_Y_HighPos[X] - 0x00 - (1 - 1)) and 0xFF
            //> lda #$00
            //> sta Player_Y_Speed        ;initialize vertical speed and low byte of force
            Player_Y_Speed = 0x00
            //> sta Player_Y_MoveForce    ;and then leave
            Player_Y_MoveForce = 0x00
        }
    }
    //> ExPlPos: rts
    return
}

// Decompiled from CheckPlayerVertical
fun checkPlayerVertical() {
    //> CheckPlayerVertical:
    //> lda Player_OffscreenBits  ;if player object is completely offscreen
    //> cmp #$f0                  ;vertically, leave this routine
    //> bcs ExCPV
    if (!(Player_OffscreenBits >= 0xF0)) {
        //> ldy Player_Y_HighPos      ;if player high vertical byte is not
        //> dey                       ;within the screen, leave this routine
        //> bne ExCPV
        if (zeroFlag) {
            //> lda Player_Y_Position     ;if on the screen, check to see how far down
            //> cmp #$d0                  ;the player is vertically
        }
    }
    //> ExCPV: rts
    return
}

// Decompiled from GetEnemyBoundBoxOfs
fun getEnemyBoundBoxOfs(): Int {
    //> GetEnemyBoundBoxOfs:
    //> lda ObjectOffset         ;get enemy object buffer offset
}

// Decompiled from GetEnemyBoundBoxOfsArg
fun getEnemyBoundBoxOfsArg(A: Int) {
    //> GetEnemyBoundBoxOfsArg:
    //> asl                      ;multiply A by four, then add four
    //> asl                      ;to skip player's bounding box
    //> clc
    //> adc #$04
    //> tay                      ;send to Y
    //> lda Enemy_OffscreenBits  ;get offscreen bits for enemy object
    //> and #%00001111           ;save low nybble
    //> cmp #%00001111           ;check for all bits set
    //> rts
    return
}

// Decompiled from PlayerBGCollision
fun playerBGCollision() {
    var A: Int = 0
    var X: Int = 0
    var Y: Int = 0
    //> PlayerBGCollision:
    //> lda DisableCollisionDet   ;if collision detection disabled flag set,
    //> bne ExPBGCol              ;branch to leave
    if (zeroFlag) {
        //> lda GameEngineSubroutine
        //> cmp #$0b                  ;if running routine #11 or $0b
        //> beq ExPBGCol              ;branch to leave
        if (!(GameEngineSubroutine - 0x0B == 0)) {
            //> cmp #$04
            //> bcc ExPBGCol              ;if running routines $00-$03 branch to leave
            if (A >= 0x04) {
                //> lda #$01                  ;load default player state for swimming
                //> ldy SwimmingFlag          ;if swimming flag set,
                //> bne SetPSte               ;branch ahead to set default state
                if (zeroFlag) {
                    //> lda Player_State          ;if player in normal state,
                    //> beq SetFallS              ;branch to set default state for falling
                    if (!zeroFlag) {
                        //> cmp #$03
                        //> bne ChkOnScr              ;if in any other state besides climbing, skip to next part
                        if (A - 0x03 == 0) {
                            //> SetFallS: lda #$02                  ;load default player state for falling
                            //> SetPSte:  sta Player_State          ;set whatever player state is appropriate
                            Player_State = 0x02
                        }
                    }
                }
                //> ChkOnScr: lda Player_Y_HighPos
                //> cmp #$01                  ;check player's vertical high byte for still on the screen
                //> bne ExPBGCol              ;branch to leave if not
                if (Player_Y_HighPos - 0x01 == 0) {
                    //> lda #$ff
                    //> sta Player_CollisionBits  ;initialize player's collision flag
                    Player_CollisionBits = 0xFF
                    //> lda Player_Y_Position
                    //> cmp #$cf                  ;check player's vertical coordinate
                    //> bcc ChkCollSize           ;if not too close to the bottom of screen, continue
                    if (Player_Y_Position >= 0xCF) {
                        //> ExPBGCol: rts                       ;otherwise leave
                        return
                    }
                }
            }
        }
    }
    //> ChkCollSize:
    //> ldy #$02                    ;load default offset
    //> lda CrouchingFlag
    //> bne GBBAdr                  ;if player crouching, skip ahead
    if (zeroFlag) {
        //> lda PlayerSize
        //> bne GBBAdr                  ;if player small, skip ahead
        if (zeroFlag) {
            //> dey                         ;otherwise decrement offset for big player not crouching
            //> lda SwimmingFlag
            //> bne GBBAdr                  ;if swimming flag set, skip ahead
            if (zeroFlag) {
                //> dey                         ;otherwise decrement offset
            }
        }
    }
    //> GBBAdr:  lda BlockBufferAdderData,y  ;get value using offset
    //> sta $eb                     ;store value here
    zp_eb = BlockBufferAdderData[((0x02 - 1) and 0xFF - 1) and 0xFF]
    //> tay                         ;put value into Y, as offset for block buffer routine
    //> ldx PlayerSize              ;get player's size as offset
    //> lda CrouchingFlag
    //> beq HeadChk                 ;if player not crouching, branch ahead
    if (!zeroFlag) {
        //> inx                         ;otherwise increment size as offset
    }
    //> HeadChk: lda Player_Y_Position       ;get player's vertical coordinate
    //> cmp PlayerBGUpperExtent,x   ;compare with upper extent value based on offset
    //> bcc DoFootCheck             ;if player is too high, skip this part
    if (Player_Y_Position >= PlayerBGUpperExtent[X]) {
        //> jsr BlockBufferColli_Head   ;do player-to-bg collision detection on top of
        blockbuffercolliHead()
        //> beq DoFootCheck             ;player, and branch if nothing above player's head
        if (!(A - PlayerBGUpperExtent[X] == 0)) {
            //> jsr CheckForCoinMTiles      ;check to see if player touched coin with their head
            checkForCoinMTiles(Player_Y_Position)
            //> bcs AwardTouchedCoin        ;if so, branch to some other part of code
            if (!(A >= PlayerBGUpperExtent[X])) {
                //> ldy Player_Y_Speed          ;check player's vertical speed
                //> bpl DoFootCheck             ;if player not moving upwards, branch elsewhere
                if (negativeFlag) {
                    //> ldy $04                     ;check lower nybble of vertical coordinate returned
                    //> cpy #$04                    ;from collision detection routine
                    //> bcc DoFootCheck             ;if low nybble < 4, branch
                    if (zp_04 >= 0x04) {
                        //> jsr CheckForSolidMTiles     ;check to see what player's head bumped on
                        checkForSolidMTiles(Player_Y_Position, (PlayerSize + 1) and 0xFF)
                        //> bcs SolidOrClimb            ;if player collided with solid metatile, branch
                        if (!(Y >= 0x04)) {
                            //> ldy AreaType                ;otherwise check area type
                            //> beq NYSpd                   ;if water level, branch ahead
                            if (!zeroFlag) {
                                //> ldy BlockBounceTimer        ;if block bounce timer not expired,
                                //> bne NYSpd                   ;branch ahead, do not process collision
                                if (zeroFlag) {
                                    //> jsr PlayerHeadCollision     ;otherwise do a sub to process collision
                                    playerHeadCollision(Player_Y_Position)
                                    //> jmp DoFootCheck             ;jump ahead to skip these other parts here
                                    //> SolidOrClimb:
                                    //> cmp #$26               ;if climbing metatile,
                                    //> beq NYSpd              ;branch ahead and do not play sound
                                    if (!(A - 0x26 == 0)) {
                                        //> lda #Sfx_Bump
                                        //> sta Square1SoundQueue  ;otherwise load bump sound
                                        Square1SoundQueue = Sfx_Bump
                                    }
                                }
                            }
                        } else {
                            if (!(A - 0x26 == 0)) {
                            }
                            //> NYSpd: lda #$01               ;set player's vertical speed to nullify
                            //> sta Player_Y_Speed     ;jump or swim
                            Player_Y_Speed = 0x01
                        }
                    }
                }
                //> DoFootCheck:
                //> ldy $eb                    ;get block buffer adder offset
                //> lda Player_Y_Position
                //> cmp #$cf                   ;check to see how low player is
                //> bcs DoPlayerSideCheck      ;if player is too far down on screen, skip all of this
                if (!(Player_Y_Position >= 0xCF)) {
                    //> jsr BlockBufferColli_Feet  ;do player-to-bg collision detection on bottom left of player
                    blockbuffercolliFeet(zp_eb)
                    //> jsr CheckForCoinMTiles     ;check to see if player touched coin with their left foot
                    checkForCoinMTiles(Player_Y_Position)
                    //> bcs AwardTouchedCoin       ;if so, branch to some other part of code
                    if (!(A >= 0xCF)) {
                        //> pha                        ;save bottom left metatile to stack
                        //> jsr BlockBufferColli_Feet  ;do player-to-bg collision detection on bottom right of player
                        blockbuffercolliFeet(zp_eb)
                        //> sta $00                    ;save bottom right metatile here
                        zp_00 = Player_Y_Position
                        //> pla
                        //> sta $01                    ;pull bottom left metatile and save here
                        zp_01 = Player_Y_Position
                        //> bne ChkFootMTile           ;if anything here, skip this part
                        if (zeroFlag) {
                            //> lda $00                    ;otherwise check for anything in bottom right metatile
                            //> beq DoPlayerSideCheck      ;and skip ahead if not
                            if (!zeroFlag) {
                                //> jsr CheckForCoinMTiles     ;check to see if player touched coin with their right foot
                                checkForCoinMTiles(zp_00)
                                //> bcc ChkFootMTile           ;if not, skip unconditional jump and continue code
                                if (carryFlag) {
                                    //> AwardTouchedCoin:
                                    //> jmp HandleCoinMetatile     ;follow the code to erase coin and award to player 1 coin
                                }
                                //> ChkFootMTile:
                                //> jsr CheckForClimbMTiles    ;check to see if player landed on climbable metatiles
                                checkForClimbMTiles(zp_00, (PlayerSize + 1) and 0xFF)
                                //> bcs DoPlayerSideCheck      ;if so, branch
                                if (!carryFlag) {
                                    //> ldy Player_Y_Speed         ;check player's vertical speed
                                    //> bmi DoPlayerSideCheck      ;if player moving upwards, branch
                                    if (!negativeFlag) {
                                        //> cmp #$c5
                                        //> bne ContChk                ;if player did not touch axe, skip ahead
                                        if (A - 0xC5 == 0) {
                                            //> jmp HandleAxeMetatile      ;otherwise jump to set modes of operation
                                        }
                                        //> ContChk:  jsr ChkInvisibleMTiles     ;do sub to check for hidden coin or 1-up blocks
                                        chkInvisibleMTiles(zp_00)
                                        //> beq DoPlayerSideCheck      ;if either found, branch
                                        if (!(A - 0xC5 == 0)) {
                                            //> ldy JumpspringAnimCtrl     ;if jumpspring animating right now,
                                            //> bne InitSteP               ;branch ahead
                                            if (zeroFlag) {
                                                //> ldy $04                    ;check lower nybble of vertical coordinate returned
                                                //> cpy #$05                   ;from collision detection routine
                                                //> bcc LandPlyr               ;if lower nybble < 5, branch
                                                if (zp_04 >= 0x05) {
                                                    //> lda Player_MovingDir
                                                    //> sta $00                    ;use player's moving direction as temp variable
                                                    zp_00 = Player_MovingDir
                                                    //> jmp ImpedePlayerMove       ;jump to impede player's movement in that direction
                                                }
                                                //> LandPlyr: jsr ChkForLandJumpSpring   ;do sub to check for jumpspring metatiles and deal with it
                                                chkForLandJumpSpring()
                                                //> lda #$f0
                                                //> and Player_Y_Position      ;mask out lower nybble of player's vertical position
                                                //> sta Player_Y_Position      ;and store as new vertical position to land player properly
                                                Player_Y_Position = 0xF0 and Player_Y_Position
                                                //> jsr HandlePipeEntry        ;do sub to process potential pipe entry
                                                handlePipeEntry()
                                                //> lda #$00
                                                //> sta Player_Y_Speed         ;initialize vertical speed and fractional
                                                Player_Y_Speed = 0x00
                                                //> sta Player_Y_MoveForce     ;movement force to stop player's vertical movement
                                                Player_Y_MoveForce = 0x00
                                                //> sta StompChainCounter      ;initialize enemy stomp counter
                                                StompChainCounter = 0x00
                                            }
                                            //> InitSteP: lda #$00
                                            //> sta Player_State           ;set player's state to normal
                                            Player_State = 0x00
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!carryFlag) {
                        if (!negativeFlag) {
                            if (A - 0xC5 == 0) {
                            }
                            if (!(A - 0xC5 == 0)) {
                                if (zeroFlag) {
                                    if (zp_04 >= 0x05) {
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (!(Player_Y_Position >= 0xCF)) {
        if (!(A >= 0xCF)) {
            if (zeroFlag) {
                if (!zeroFlag) {
                    if (carryFlag) {
                    }
                    if (!carryFlag) {
                        if (!negativeFlag) {
                            if (A - 0xC5 == 0) {
                            }
                            if (!(A - 0xC5 == 0)) {
                                if (zeroFlag) {
                                    if (zp_04 >= 0x05) {
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!carryFlag) {
            if (!negativeFlag) {
                if (A - 0xC5 == 0) {
                }
                if (!(A - 0xC5 == 0)) {
                    if (zeroFlag) {
                        if (zp_04 >= 0x05) {
                        }
                    }
                }
            }
        }
    }
    //> DoPlayerSideCheck:
    //> ldy $eb       ;get block buffer adder offset
    //> iny
    //> iny           ;increment offset 2 bytes to use adders for side collisions
    //> lda #$02      ;set value here to be used as counter
    //> sta $00
    zp_00 = 0x02
    while (zeroFlag) {
        //> cmp #$e4
        //> bcs ExSCH                 ;branch to leave if player is too far down
        if (!(A >= 0xE4)) {
            //> jsr BlockBufferColli_Side ;do player-to-bg collision detection on one half of player
            blockbuffercolliSide()
            //> beq BHalf                 ;branch ahead if nothing found
            if (!(A - 0xE4 == 0)) {
                //> cmp #$1c                  ;otherwise check for pipe metatiles
                //> beq BHalf                 ;if collided with sideways pipe (top), branch ahead
                if (!(A - 0x1C == 0)) {
                    //> cmp #$6b
                    //> beq BHalf                 ;if collided with water pipe (top), branch ahead
                    if (!(A - 0x6B == 0)) {
                        //> jsr CheckForClimbMTiles   ;do sub to see if player bumped into anything climbable
                        checkForClimbMTiles(0x02, (PlayerSize + 1) and 0xFF)
                        //> bcc CheckSideMTiles       ;if not, branch to alternate section of code
                        if (A >= 0x6B) {
                            //> BHalf: ldy $eb                   ;load block adder offset
                            //> iny                       ;increment it
                            //> lda Player_Y_Position     ;get player's vertical position
                            //> cmp #$08
                            //> bcc ExSCH                 ;if too high, branch to leave
                            if (Player_Y_Position >= 0x08) {
                                //> cmp #$d0
                                //> bcs ExSCH                 ;if too low, branch to leave
                                if (!(A >= 0xD0)) {
                                    //> jsr BlockBufferColli_Side ;do player-to-bg collision detection on other half of player
                                    blockbuffercolliSide()
                                    //> bne CheckSideMTiles       ;if something found, branch
                                    if (A - 0xD0 == 0) {
                                        do {
                                            //> SideCheckLoop:
                                            //> iny                       ;move onto the next one
                                            //> sty $eb                   ;store it
                                            zp_eb = ((zp_eb + 1) and 0xFF + 1) and 0xFF
                                            //> lda Player_Y_Position
                                            //> cmp #$20                  ;check player's vertical position
                                            //> bcc BHalf                 ;if player is in status bar area, branch ahead to skip this part
                                            //> dec $00                   ;otherwise decrement counter
                                            zp_00 = (zp_00 - 1) and 0xFF
                                            //> bne SideCheckLoop         ;run code until both sides of player are checked
                                        } while (!zeroFlag)
                                        //> ExSCH: rts                       ;leave
                                        return
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (Player_Y_Position >= 0x08) {
                if (!(A >= 0xD0)) {
                    if (A - 0xD0 == 0) {
                        do {
                        } while (!zeroFlag)
                    }
                }
            }
        }
    }
    //> CheckSideMTiles:
    //> jsr ChkInvisibleMTiles     ;check for hidden or coin 1-up blocks
    chkInvisibleMTiles(Player_Y_Position)
    //> beq ExCSM                  ;branch to leave if either found
    if (!(A - 0x6B == 0)) {
        //> jsr CheckForClimbMTiles    ;check for climbable metatiles
        checkForClimbMTiles(Player_Y_Position, (PlayerSize + 1) and 0xFF)
        //> bcc ContSChk               ;if not found, skip and continue with code
        if (A >= 0x6B) {
            //> jmp HandleClimbing         ;otherwise jump to handle climbing
        }
        //> ContSChk: jsr CheckForCoinMTiles     ;check to see if player touched coin
        checkForCoinMTiles(Player_Y_Position)
        //> bcs HandleCoinMetatile     ;if so, execute code to erase coin and award to player 1 coin
        if (!(A >= 0x6B)) {
            //> jsr ChkJumpspringMetatiles ;check for jumpspring metatiles
            chkJumpspringMetatiles(Player_Y_Position)
            //> bcc ChkPBtm                ;if not found, branch ahead to continue cude
            if (A >= 0x6B) {
                //> lda JumpspringAnimCtrl     ;otherwise check jumpspring animation control
                //> bne ExCSM                  ;branch to leave if set
                if (zeroFlag) {
                    //> jmp StopPlayerMove         ;otherwise jump to impede player's movement
                    //> ChkPBtm:  ldy Player_State           ;get player's state
                    //> cpy #$00                   ;check for player's state set to normal
                    //> bne StopPlayerMove         ;if not, branch to impede player's movement
                    if (Player_State == 0x00) {
                        //> ldy PlayerFacingDir        ;get player's facing direction
                        //> dey
                        //> bne StopPlayerMove         ;if facing left, branch to impede movement
                        if (zeroFlag) {
                            //> cmp #$6c                   ;otherwise check for pipe metatiles
                            //> beq PipeDwnS               ;if collided with sideways pipe (bottom), branch
                            if (!(A - 0x6C == 0)) {
                                //> cmp #$1f                   ;if collided with water pipe (bottom), continue
                                //> bne StopPlayerMove         ;otherwise branch to impede player's movement
                                if (A - 0x1F == 0) {
                                    //> PipeDwnS: lda Player_SprAttrib       ;check player's attributes
                                    //> bne PlyrPipe               ;if already set, branch, do not play sound again
                                    if (zeroFlag) {
                                        //> ldy #Sfx_PipeDown_Injury
                                        //> sty Square1SoundQueue      ;otherwise load pipedown/injury sound
                                        Square1SoundQueue = Sfx_PipeDown_Injury
                                    }
                                    //> PlyrPipe: ora #%00100000
                                    //> sta Player_SprAttrib       ;set background priority bit in player attributes
                                    Player_SprAttrib = Player_SprAttrib or 0x20
                                    //> lda Player_X_Position
                                    //> and #%00001111             ;get lower nybble of player's horizontal coordinate
                                    //> beq ChkGERtn               ;if at zero, branch ahead to skip this part
                                    if (!zeroFlag) {
                                        //> ldy #$00                   ;set default offset for timer setting data
                                        //> lda ScreenLeft_PageLoc     ;load page location for left side of screen
                                        //> beq SetCATmr               ;if at page zero, use default offset
                                        if (!zeroFlag) {
                                            //> iny                        ;otherwise increment offset
                                        }
                                        //> SetCATmr: lda AreaChangeTimerData,y  ;set timer for change of area as appropriate
                                        //> sta ChangeAreaTimer
                                        ChangeAreaTimer = AreaChangeTimerData[(0x00 + 1) and 0xFF]
                                    }
                                    //> ChkGERtn: lda GameEngineSubroutine   ;get number of game engine routine running
                                    //> cmp #$07
                                    //> beq ExCSM                  ;if running player entrance routine or
                                    if (!(GameEngineSubroutine - 0x07 == 0)) {
                                        //> cmp #$08                   ;player control routine, go ahead and branch to leave
                                        //> bne ExCSM
                                        if (A - 0x08 == 0) {
                                            //> lda #$02
                                            //> sta GameEngineSubroutine   ;otherwise set sideways pipe entry routine to run
                                            GameEngineSubroutine = 0x02
                                            //> rts                        ;and leave
                                            return
                                            //> StopPlayerMove:
                                            //> jsr ImpedePlayerMove      ;stop player's movement
                                            impedePlayerMove()
                                        }
                                    }
                                }
                            }
                            if (zeroFlag) {
                            }
                            if (!zeroFlag) {
                                if (!zeroFlag) {
                                }
                            }
                            if (!(GameEngineSubroutine - 0x07 == 0)) {
                                if (A - 0x08 == 0) {
                                }
                            }
                        }
                    }
                }
            } else {
                if (Player_State == 0x00) {
                    if (zeroFlag) {
                        if (!(A - 0x6C == 0)) {
                            if (A - 0x1F == 0) {
                                if (zeroFlag) {
                                }
                                if (!zeroFlag) {
                                    if (!zeroFlag) {
                                    }
                                }
                                if (!(GameEngineSubroutine - 0x07 == 0)) {
                                    if (A - 0x08 == 0) {
                                    }
                                }
                            }
                        }
                        if (zeroFlag) {
                        }
                        if (!zeroFlag) {
                            if (!zeroFlag) {
                            }
                        }
                        if (!(GameEngineSubroutine - 0x07 == 0)) {
                            if (A - 0x08 == 0) {
                            }
                        }
                    }
                }
            }
            //> ExCSM: rts                       ;leave
            return
        }
    }
    //> HandleCoinMetatile:
    //> jsr ErACM             ;do sub to erase coin metatile from block buffer
    erACM()
    //> inc CoinTallyFor1Ups  ;increment coin tally used for 1-up blocks
    CoinTallyFor1Ups = (CoinTallyFor1Ups + 1) and 0xFF
    //> jmp GiveOneCoin       ;update coin amount and tally on the screen
    //> HandleAxeMetatile:
    //> lda #$00
    //> sta OperMode_Task   ;reset secondary mode
    OperMode_Task = 0x00
    //> lda #$02
    //> sta OperMode        ;set primary mode to autoctrl mode
    OperMode = 0x02
    //> lda #$18
    //> sta Player_X_Speed  ;set horizontal speed and continue to erase axe metatile
    Player_X_Speed = 0x18
    //> HandleClimbing:
    //> ldy $04            ;check low nybble of horizontal coordinate returned from
    //> cpy #$06           ;collision detection routine against certain values, this
    //> bcc ExHC           ;makes actual physical part of vine or flagpole thinner
    if (zp_04 >= 0x06) {
        //> cpy #$0a           ;than 16 pixels
        //> bcc ChkForFlagpole
        if (Y >= 0x0A) {
            //> ExHC: rts                ;leave if too far left or too far right
            return
        }
    }
    //> ChkForFlagpole:
    //> cmp #$24               ;check climbing metatiles
    //> beq FlagpoleCollision  ;branch if flagpole ball found
    if (!(A - 0x24 == 0)) {
        //> cmp #$25
        //> bne VineCollision      ;branch to alternate code if flagpole shaft not found
        if (A - 0x25 == 0) {
            //> FlagpoleCollision:
            //> lda GameEngineSubroutine
            //> cmp #$05                  ;check for end-of-level routine running
            //> beq PutPlayerOnVine       ;if running, branch to end of climbing code
            if (!(GameEngineSubroutine - 0x05 == 0)) {
                //> lda #$01
                //> sta PlayerFacingDir       ;set player's facing direction to right
                PlayerFacingDir = 0x01
                //> inc ScrollLock            ;set scroll lock flag
                ScrollLock = (ScrollLock + 1) and 0xFF
                //> lda GameEngineSubroutine
                //> cmp #$04                  ;check for flagpole slide routine running
                //> beq RunFR                 ;if running, branch to end of flagpole code here
                if (!(GameEngineSubroutine - 0x04 == 0)) {
                    //> lda #BulletBill_CannonVar ;load identifier for bullet bills (cannon variant)
                    //> jsr KillEnemies           ;get rid of them
                    killEnemies(BulletBill_CannonVar)
                    //> lda #Silence
                    //> sta EventMusicQueue       ;silence music
                    EventMusicQueue = Silence
                    //> lsr
                    //> sta FlagpoleSoundQueue    ;load flagpole sound into flagpole sound queue
                    FlagpoleSoundQueue = Silence shr 1
                    //> ldx #$04                  ;start at end of vertical coordinate data
                    //> lda Player_Y_Position
                    //> sta FlagpoleCollisionYPos ;store player's vertical coordinate here to be used later
                    FlagpoleCollisionYPos = Player_Y_Position
                    while (zeroFlag) {
                        do {
                            //> ChkFlagpoleYPosLoop:
                            //> cmp FlagpoleYPosData,x    ;compare with current vertical coordinate data
                            //> bcs MtchF                 ;if player's => current, branch to use current offset
                            //> dex                       ;otherwise decrement offset to use
                            //> bne ChkFlagpoleYPosLoop   ;do this until all data is checked (use last one if all checked)
                        } while (!zeroFlag)
                    }
                    //> MtchF: stx FlagpoleScore         ;store offset here to be used later
                    FlagpoleScore = (0x04 - 1) and 0xFF
                }
                //> RunFR: lda #$04
                //> sta GameEngineSubroutine  ;set value to run flagpole slide routine
                GameEngineSubroutine = 0x04
                //> jmp PutPlayerOnVine       ;jump to end of climbing code
                //> VineCollision:
                //> cmp #$26                  ;check for climbing metatile used on vines
                //> bne PutPlayerOnVine
                if (A - 0x26 == 0) {
                    //> lda Player_Y_Position     ;check player's vertical coordinate
                    //> cmp #$20                  ;for being in status bar area
                    //> bcs PutPlayerOnVine       ;branch if not that far up
                    if (!(Player_Y_Position >= 0x20)) {
                        //> lda #$01
                        //> sta GameEngineSubroutine  ;otherwise set to run autoclimb routine next frame
                        GameEngineSubroutine = 0x01
                    }
                }
            }
        }
    }
    if (!(GameEngineSubroutine - 0x05 == 0)) {
        if (!(GameEngineSubroutine - 0x04 == 0)) {
            while (zeroFlag) {
                do {
                } while (!zeroFlag)
            }
        }
        if (A - 0x26 == 0) {
            if (!(Player_Y_Position >= 0x20)) {
            }
        }
    }
    //> PutPlayerOnVine:
    //> lda #$03                ;set player state to climbing
    //> sta Player_State
    Player_State = 0x03
    //> lda #$00                ;nullify player's horizontal speed
    //> sta Player_X_Speed      ;and fractional horizontal movement force
    Player_X_Speed = 0x00
    //> sta Player_X_MoveForce
    Player_X_MoveForce = 0x00
    //> lda Player_X_Position   ;get player's horizontal coordinate
    //> sec
    //> sbc ScreenLeft_X_Pos    ;subtract from left side horizontal coordinate
    //> cmp #$10
    //> bcs SetVXPl             ;if 16 or more pixels difference, do not alter facing direction
    if (!((Player_X_Position - ScreenLeft_X_Pos - (1 - 1)) and 0xFF >= 0x10)) {
        //> lda #$02
        //> sta PlayerFacingDir     ;otherwise force player to face left
        PlayerFacingDir = 0x02
    }
    //> SetVXPl: ldy PlayerFacingDir     ;get current facing direction, use as offset
    //> lda $06                 ;get low byte of block buffer address
    //> asl
    //> asl                     ;move low nybble to high
    //> asl
    //> asl
    //> clc
    //> adc ClimbXPosAdder-1,y  ;add pixels depending on facing direction
    //> sta Player_X_Position   ;store as player's horizontal coordinate
    Player_X_Position = (((((zp_06 shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF + ClimbXPosAdder[PlayerFacingDir] + 0) and 0xFF
    //> lda $06                 ;get low byte of block buffer address again
    //> bne ExPVne              ;if not zero, branch
    if (zeroFlag) {
        //> lda ScreenRight_PageLoc ;load page location of right side of screen
        //> clc
        //> adc ClimbPLocAdder-1,y  ;add depending on facing location
        //> sta Player_PageLoc      ;store as player's page location
        Player_PageLoc = (ScreenRight_PageLoc + ClimbPLocAdder[PlayerFacingDir] + 0) and 0xFF
    }
    //> ExPVne:  rts                     ;finally, we're done!
    return
}

// Decompiled from ErACM
fun erACM() {
    //> ErACM: ldy $02             ;load vertical high nybble offset for block buffer
    //> lda #$00            ;load blank metatile
    //> sta ($06),y         ;store to remove old contents from block buffer
    TODO = 0x00
    //> jmp RemoveCoin_Axe  ;update the screen accordingly
}

// Decompiled from ChkInvisibleMTiles
fun chkInvisibleMTiles(A: Int) {
    //> ChkInvisibleMTiles:
    //> cmp #$5f       ;check for hidden coin block
    //> beq ExCInvT    ;branch to leave if found
    if (!(A - 0x5F == 0)) {
        //> cmp #$60       ;check for hidden 1-up block
    }
    //> ExCInvT: rts            ;leave with zero flag set if either found
    return
}

// Decompiled from ChkForLandJumpSpring
fun chkForLandJumpSpring() {
    var A: Int = 0
    //> ChkForLandJumpSpring:
    //> jsr ChkJumpspringMetatiles  ;do sub to check if player landed on jumpspring
    chkJumpspringMetatiles(A)
    //> bcc ExCJSp                  ;if carry not set, jumpspring not found, therefore leave
    if (carryFlag) {
        //> lda #$70
        //> sta VerticalForce           ;otherwise set vertical movement force for player
        VerticalForce = 0x70
        //> lda #$f9
        //> sta JumpspringForce         ;set default jumpspring force
        JumpspringForce = 0xF9
        //> lda #$03
        //> sta JumpspringTimer         ;set jumpspring timer to be used later
        JumpspringTimer = 0x03
        //> lsr
        //> sta JumpspringAnimCtrl      ;set jumpspring animation control to start animating
        JumpspringAnimCtrl = 0x03 shr 1
    }
    //> ExCJSp: rts                         ;and leave
    return
}

// Decompiled from ChkJumpspringMetatiles
fun chkJumpspringMetatiles(A: Int) {
    //> ChkJumpspringMetatiles:
    //> cmp #$67      ;check for top jumpspring metatile
    //> beq JSFnd     ;branch to set carry if found
    if (!(A - 0x67 == 0)) {
        //> cmp #$68      ;check for bottom jumpspring metatile
        //> clc           ;clear carry flag
        //> bne NoJSFnd   ;branch to use cleared carry if not found
        if (A - 0x68 == 0) {
            //> JSFnd:   sec           ;set carry if found
        }
    }
    //> NoJSFnd: rts           ;leave
    return
}

// Decompiled from HandlePipeEntry
fun handlePipeEntry() {
    var A: Int = 0
    //> HandlePipeEntry:
    //> lda Up_Down_Buttons       ;check saved controller bits from earlier
    //> and #%00000100            ;for pressing down
    //> beq ExPipeE               ;if not pressing down, branch to leave
    if (!zeroFlag) {
        //> lda $00
        //> cmp #$11                  ;check right foot metatile for warp pipe right metatile
        //> bne ExPipeE               ;branch to leave if not found
        if (zp_00 - 0x11 == 0) {
            //> lda $01
            //> cmp #$10                  ;check left foot metatile for warp pipe left metatile
            //> bne ExPipeE               ;branch to leave if not found
            if (zp_01 - 0x10 == 0) {
                //> lda #$30
                //> sta ChangeAreaTimer       ;set timer for change of area
                ChangeAreaTimer = 0x30
                //> lda #$03
                //> sta GameEngineSubroutine  ;set to run vertical pipe entry routine on next frame
                GameEngineSubroutine = 0x03
                //> lda #Sfx_PipeDown_Injury
                //> sta Square1SoundQueue     ;load pipedown/injury sound
                Square1SoundQueue = Sfx_PipeDown_Injury
                //> lda #%00100000
                //> sta Player_SprAttrib      ;set background priority bit in player's attributes
                Player_SprAttrib = 0x20
                //> lda WarpZoneControl       ;check warp zone control
                //> beq ExPipeE               ;branch to leave if none found
                if (!zeroFlag) {
                    //> and #%00000011            ;mask out all but 2 LSB
                    //> asl
                    //> asl                       ;multiply by four
                    //> tax                       ;save as offset to warp zone numbers (starts at left pipe)
                    //> lda Player_X_Position     ;get player's horizontal position
                    //> cmp #$60
                    //> bcc GetWNum               ;if player at left, not near middle, use offset and skip ahead
                    if (Player_X_Position >= 0x60) {
                        //> inx                       ;otherwise increment for middle pipe
                        //> cmp #$a0
                        //> bcc GetWNum               ;if player at middle, but not too far right, use offset and skip
                        if (A >= 0xA0) {
                            //> inx                       ;otherwise increment for last pipe
                        }
                    }
                    //> GetWNum: ldy WarpZoneNumbers,x     ;get warp zone numbers
                    //> dey                       ;decrement for use as world number
                    //> sty WorldNumber           ;store as world number and offset
                    WorldNumber = (WarpZoneNumbers[((((WarpZoneControl and 0x03 shl 1) and 0xFF shl 1) and 0xFF + 1) and 0xFF + 1) and 0xFF] - 1) and 0xFF
                    //> ldx WorldAddrOffsets,y    ;get offset to where this world's area offsets are
                    //> lda AreaAddrOffsets,x     ;get area offset based on world offset
                    //> sta AreaPointer           ;store area offset here to be used to change areas
                    AreaPointer = AreaAddrOffsets[WorldAddrOffsets[(WarpZoneNumbers[((((WarpZoneControl and 0x03 shl 1) and 0xFF shl 1) and 0xFF + 1) and 0xFF + 1) and 0xFF] - 1) and 0xFF]]
                    //> lda #Silence
                    //> sta EventMusicQueue       ;silence music
                    EventMusicQueue = Silence
                    //> lda #$00
                    //> sta EntrancePage          ;initialize starting page number
                    EntrancePage = 0x00
                    //> sta AreaNumber            ;initialize area number used for area address offset
                    AreaNumber = 0x00
                    //> sta LevelNumber           ;initialize level number used for world display
                    LevelNumber = 0x00
                    //> sta AltEntranceControl    ;initialize mode of entry
                    AltEntranceControl = 0x00
                    //> inc Hidden1UpFlag         ;set flag for hidden 1-up blocks
                    Hidden1UpFlag = (Hidden1UpFlag + 1) and 0xFF
                    //> inc FetchNewGameTimerFlag ;set flag to load new game timer
                    FetchNewGameTimerFlag = (FetchNewGameTimerFlag + 1) and 0xFF
                }
            }
        }
    }
    //> ExPipeE: rts                       ;leave!!!
    return
}

// Decompiled from ImpedePlayerMove
fun impedePlayerMove() {
    var A: Int = 0
    //> ImpedePlayerMove:
    //> lda #$00                  ;initialize value here
    //> ldy Player_X_Speed        ;get player's horizontal speed
    //> ldx $00                   ;check value set earlier for
    //> dex                       ;left side collision
    //> bne RImpd                 ;if right side collision, skip this part
    if (zeroFlag) {
        //> inx                       ;return value to X
        //> cpy #$00                  ;if player moving to the left,
        //> bmi ExIPM                 ;branch to invert bit and leave
        if (!negativeFlag) {
            //> lda #$ff                  ;otherwise load A with value to be used later
            //> jmp NXSpd                 ;and jump to affect movement
            //> RImpd: ldx #$02                  ;return $02 to X
            //> cpy #$01                  ;if player moving to the right,
            //> bpl ExIPM                 ;branch to invert bit and leave
            if (negativeFlag) {
                //> lda #$01                  ;otherwise load A with value to be used here
                //> NXSpd: ldy #$10
                //> sty SideCollisionTimer    ;set timer of some sort
                SideCollisionTimer = 0x10
                //> ldy #$00
                //> sty Player_X_Speed        ;nullify player's horizontal speed
                Player_X_Speed = 0x00
                //> cmp #$00                  ;if value set in A not set to $ff,
                //> bpl PlatF                 ;branch ahead, do not decrement Y
                if (A - 0x00 < 0) {
                    //> dey                       ;otherwise decrement Y now
                }
                //> PlatF: sty $00                   ;store Y as high bits of horizontal adder
                zp_00 = (0x00 - 1) and 0xFF
                //> clc
                //> adc Player_X_Position     ;add contents of A to player's horizontal
                //> sta Player_X_Position     ;position to move player left or right
                Player_X_Position = (0x01 + Player_X_Position + 0) and 0xFF
                //> lda Player_PageLoc
                //> adc $00                   ;add high bits and carry to
                //> sta Player_PageLoc        ;page location if necessary
                Player_PageLoc = (Player_PageLoc + zp_00 + 0) and 0xFF
            }
        }
    } else {
        if (negativeFlag) {
            if (A - 0x00 < 0) {
            }
        }
    }
    if (A - 0x00 < 0) {
    }
    //> ExIPM: txa                       ;invert contents of X
    //> eor #$ff
    //> and Player_CollisionBits  ;mask out bit that was set here
    //> sta Player_CollisionBits  ;store to clear bit
    Player_CollisionBits = 0x02 xor 0xFF and Player_CollisionBits
    //> rts
    return
}

// Decompiled from CheckForSolidMTiles
fun checkForSolidMTiles(A: Int, X: Int) {
    //> CheckForSolidMTiles:
    //> jsr GetMTileAttrib        ;find appropriate offset based on metatile's 2 MSB
    getMTileAttrib(A)
    //> cmp SolidMTileUpperExt,x  ;compare current metatile with solid metatiles
    //> rts
    return
}

// Decompiled from CheckForClimbMTiles
fun checkForClimbMTiles(A: Int, X: Int) {
    //> CheckForClimbMTiles:
    //> jsr GetMTileAttrib        ;find appropriate offset based on metatile's 2 MSB
    getMTileAttrib(A)
    //> cmp ClimbMTileUpperExt,x  ;compare current metatile with climbable metatiles
    //> rts
    return
}

// Decompiled from CheckForCoinMTiles
fun checkForCoinMTiles(A: Int) {
    //> CheckForCoinMTiles:
    //> cmp #$c2              ;check for regular coin
    //> beq CoinSd            ;branch if found
    if (!(A - 0xC2 == 0)) {
        //> cmp #$c3              ;check for underwater coin
        //> beq CoinSd            ;branch if found
        if (!(A - 0xC3 == 0)) {
            //> clc                   ;otherwise clear carry and leave
            //> rts
            return
        }
    }
    //> CoinSd:  lda #Sfx_CoinGrab
    //> sta Square2SoundQueue ;load coin grab sound and leave
    Square2SoundQueue = Sfx_CoinGrab
    //> rts
    return
}

// Decompiled from GetMTileAttrib
fun getMTileAttrib(A: Int): Int {
    //> GetMTileAttrib:
    //> tay            ;save metatile value into Y
    //> and #%11000000 ;mask out all but 2 MSB
    //> asl
    //> rol            ;shift and rotate d7-d6 to d1-d0
    //> rol
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
    //> ChkToStunEnemies:
    //> cmp #$09                   ;perform many comparisons on enemy object identifier
    //> bcc SetStun
    //> cmp #$11                   ;if the enemy object identifier is equal to the values
    //> bcs SetStun                ;$09, $0e, $0f or $10, it will be modified, and not
    //> cmp #$0a                   ;modified if not any of those values, note that piranha plant will
    //> bcc Demote                 ;always fail this test because A will still have vertical
    if (A >= 0x0A) {
        //> cmp #PiranhaPlant          ;coordinate from previous addition, also these comparisons
        //> bcc SetStun                ;are only necessary if branching from $d7a1
    }
    //> Demote:   and #%00000001             ;erase all but LSB, essentially turning enemy object
    //> sta Enemy_ID,x             ;into green or red koopa troopa to demote them
    Enemy_ID[X] = A and 0x01
}

// Decompiled from SetStun
fun setStun(X: Int) {
    var A: Int = 0
    //> SetStun:  lda Enemy_State,x          ;load enemy state
    //> and #%11110000             ;save high nybble
    //> ora #%00000010
    //> sta Enemy_State,x          ;set d1 of enemy state
    Enemy_State[X] = Enemy_State[X] and 0xF0 or 0x02
    //> dec Enemy_Y_Position,x
    Enemy_Y_Position[X] = (Enemy_Y_Position[X] - 1) and 0xFF
    //> dec Enemy_Y_Position,x     ;subtract two pixels from enemy's vertical position
    Enemy_Y_Position[X] = (Enemy_Y_Position[X] - 1) and 0xFF
    //> lda Enemy_ID,x
    //> cmp #Bloober               ;check for bloober object
    //> beq SetWYSpd
    if (!(Enemy_ID[X] - Bloober == 0)) {
        //> lda #$fd                   ;set default vertical speed
        //> ldy AreaType
        //> bne SetNotW                ;if area type not water, set as speed, otherwise
        if (zeroFlag) {
            //> SetWYSpd: lda #$ff                   ;change the vertical speed
        }
    }
    //> SetNotW:  sta Enemy_Y_Speed,x        ;set vertical speed now
    Enemy_Y_Speed[X] = 0xFF
    //> ldy #$01
    //> jsr PlayerEnemyDiff        ;get horizontal difference between player and enemy object
    playerEnemyDiff(X)
    //> bpl ChkBBill               ;branch if enemy is to the right of player
    if (negativeFlag) {
        //> iny                        ;increment Y if not
    }
    //> ChkBBill: lda Enemy_ID,x
    //> cmp #BulletBill_CannonVar  ;check for bullet bill (cannon variant)
    //> beq NoCDirF
    if (!(Enemy_ID[X] - BulletBill_CannonVar == 0)) {
        //> cmp #BulletBill_FrenzyVar  ;check for bullet bill (frenzy variant)
        //> beq NoCDirF                ;branch if either found, direction does not change
        if (!(A - BulletBill_FrenzyVar == 0)) {
            //> sty Enemy_MovingDir,x      ;store as moving direction
            Enemy_MovingDir[X] = (0x01 + 1) and 0xFF
        }
    }
    //> NoCDirF:  dey                        ;decrement and use as offset
    //> lda EnemyBGCXSpdData,y     ;get proper horizontal speed
    //> sta Enemy_X_Speed,x        ;and store, then leave
    Enemy_X_Speed[X] = EnemyBGCXSpdData[((0x01 + 1) and 0xFF - 1) and 0xFF]
    //> ExEBGChk: rts
    return
}

// Decompiled from ChkForBump_HammerBroJ
fun chkforbumpHammerbroj(X: Int) {
    var Y: Int = 0
    //> SetHJ: sty Enemy_Y_Speed,x         ;set vertical speed for jumping
    Enemy_Y_Speed[X] = Y
    //> lda Enemy_State,x           ;set d0 in enemy state for jumping
    //> ora #$01
    //> sta Enemy_State,x
    Enemy_State[X] = Enemy_State[X] or 0x01
    //> lda $00                     ;load preset value here to use as bitmask
    //> and PseudoRandomBitReg+2,x  ;and do bit-wise comparison with part of LSFR
    //> tay                         ;then use as offset
    //> lda SecondaryHardMode       ;check secondary hard mode flag
    //> bne HJump
    if (zeroFlag) {
        //> tay                         ;if secondary hard mode flag clear, set offset to 0
    }
    //> HJump: lda HammerBroJumpLData,y    ;get jump length timer data using offset from before
    //> sta EnemyFrameTimer,x       ;save in enemy timer
    EnemyFrameTimer[X] = HammerBroJumpLData[SecondaryHardMode]
    //> lda PseudoRandomBitReg+1,x
    //> ora #%11000000              ;get contents of part of LSFR, set d7 and d6, then
    //> sta HammerBroJumpTimer,x    ;store in jump timer
    HammerBroJumpTimer[X] = PseudoRandomBitReg[X] or 0xC0
    //> MoveHammerBroXDir:
    //> ldy #$fc                  ;move hammer bro a little to the left
    //> lda FrameCounter
    //> and #%01000000            ;change hammer bro's direction every 64 frames
    //> bne Shimmy
    if (zeroFlag) {
        //> ldy #$04                  ;if d6 set in counter, move him a little to the right
    }
    //> Shimmy:  sty Enemy_X_Speed,x       ;store horizontal speed
    Enemy_X_Speed[X] = 0x04
    //> ldy #$01                  ;set to face right by default
    //> jsr PlayerEnemyDiff       ;get horizontal difference between player and hammer bro
    playerEnemyDiff(X)
    //> bmi SetShim               ;if enemy to the left of player, skip this part
    if (!negativeFlag) {
        //> iny                       ;set to face left
        //> lda EnemyIntervalTimer,x  ;check walking timer
        //> bne SetShim               ;if not yet expired, skip to set moving direction
        if (zeroFlag) {
            //> lda #$f8
            //> sta Enemy_X_Speed,x       ;otherwise, make the hammer bro walk left towards player
            Enemy_X_Speed[X] = 0xF8
        }
    }
    //> SetShim: sty Enemy_MovingDir,x     ;set moving direction
    Enemy_MovingDir[X] = (0x01 + 1) and 0xFF
    //> RXSpd: lda Enemy_X_Speed,x      ;load horizontal speed
    //> eor #$ff                 ;get two's compliment for horizontal speed
    //> tay
    //> iny
    //> sty Enemy_X_Speed,x      ;store as new horizontal speed
    Enemy_X_Speed[X] = (Enemy_X_Speed[X] xor 0xFF + 1) and 0xFF
    //> lda Enemy_MovingDir,x
    //> eor #%00000011           ;invert moving direction and store, then leave
    //> sta Enemy_MovingDir,x    ;thus effectively turning the enemy around
    Enemy_MovingDir[X] = Enemy_MovingDir[X] xor 0x03
    //> ExTA:  rts                      ;leave!!!
    return
}

// Decompiled from PlayerEnemyDiff
fun playerEnemyDiff(X: Int) {
    //> PlayerEnemyDiff:
    //> lda Enemy_X_Position,x  ;get distance between enemy object's
    //> sec                     ;horizontal coordinate and the player's
    //> sbc Player_X_Position   ;horizontal coordinate
    //> sta $00                 ;and store here
    zp_00 = (Enemy_X_Position[X] - Player_X_Position - (1 - 1)) and 0xFF
    //> lda Enemy_PageLoc,x
    //> sbc Player_PageLoc      ;subtract borrow, then leave
    //> rts
    return
}

// Decompiled from EnemyLanding
fun enemyLanding(X: Int) {
    //> EnemyLanding:
    //> jsr InitVStf            ;do something here to vertical speed and something else
    initVStf(X)
    //> lda Enemy_Y_Position,x
    //> and #%11110000          ;save high nybble of vertical coordinate, and
    //> ora #%00001000          ;set d3, then store, probably used to set enemy object
    //> sta Enemy_Y_Position,x  ;neatly on whatever it's landing on
    Enemy_Y_Position[X] = Enemy_Y_Position[X] and 0xF0 or 0x08
    //> rts
    return
}

// Decompiled from SubtEnemyYPos
fun subtEnemyYPos(X: Int) {
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
    var Y: Int = 0
    //> DoEnemySideCheck:
    //> lda Enemy_Y_Position,x     ;if enemy within status bar, branch to leave
    //> cmp #$20                   ;because there's nothing there that impedes movement
    //> bcc ExESdeC
    if (Enemy_Y_Position[X] >= 0x20) {
        //> ldy #$16                   ;start by finding block to the left of enemy ($00,$14)
        //> lda #$02                   ;set value here in what is also used as
        //> sta $eb                    ;OAM data offset
        zp_eb = 0x02
        while ((Y + 1) and 0xFF >= 0x18) {
            //> lda #$01                   ;set flag in A for save horizontal coordinate
            //> jsr BlockBufferChk_Enemy   ;find block to left or right of enemy object
            blockbufferchkEnemy(0x01, X)
            //> beq NextSdeC               ;if nothing found, branch
            if (!zeroFlag) {
                //> jsr ChkForNonSolids        ;check for non-solid blocks
                chkForNonSolids(0x01)
                //> bne ChkForBump_HammerBroJ  ;branch if not found
            }
            do {
                //> SdeCLoop: lda $eb                    ;check value
                //> cmp Enemy_MovingDir,x      ;compare value against moving direction
                //> bne NextSdeC               ;branch if different and do not seek block there
                //> NextSdeC: dec $eb                    ;move to the next direction
                zp_eb = (zp_eb - 1) and 0xFF
                //> iny
                //> cpy #$18                   ;increment Y, loop only if Y < $18, thus we check
                //> bcc SdeCLoop               ;enemy ($00, $14) and ($10, $14) pixel coordinates
            } while (!((Y + 1) and 0xFF >= 0x18))
        }
    }
    //> ExESdeC:  rts
    return
}

// Decompiled from KillEnemyAboveBlock
fun killEnemyAboveBlock(X: Int) {
    //> KillEnemyAboveBlock:
    //> jsr ShellOrBlockDefeat  ;do this sub to kill enemy
    shellOrBlockDefeat(X)
    //> lda #$fc                ;alter vertical speed of enemy and leave
    //> sta Enemy_Y_Speed,x
    Enemy_Y_Speed[X] = 0xFC
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
    if (!(A - 0x26 == 0)) {
        //> cmp #$c2       ;regular coin?
        //> beq NSFnd
        if (!(A - 0xC2 == 0)) {
            //> cmp #$c3       ;underwater coin?
            //> beq NSFnd
            if (!(A - 0xC3 == 0)) {
                //> cmp #$5f       ;hidden coin block?
                //> beq NSFnd
                if (!(A - 0x5F == 0)) {
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
    var A: Int = 0
    //> FireballBGCollision:
    //> lda Fireball_Y_Position,x   ;check fireball's vertical coordinate
    //> cmp #$18
    //> bcc ClearBounceFlag         ;if within the status bar area of the screen, branch ahead
    if (Fireball_Y_Position[X] >= 0x18) {
        //> jsr BlockBufferChk_FBall    ;do fireball to background collision detection on bottom of it
        blockbufferchkFball(X)
        //> beq ClearBounceFlag         ;if nothing underneath fireball, branch
        if (!(A - 0x18 == 0)) {
            //> jsr ChkForNonSolids         ;check for non-solid metatiles
            chkForNonSolids(Fireball_Y_Position[X])
            //> beq ClearBounceFlag         ;branch if any found
            if (!(A - 0x18 == 0)) {
                //> lda Fireball_Y_Speed,x      ;if fireball's vertical speed set to move upwards,
                //> bmi InitFireballExplode     ;branch to set exploding bit in fireball's state
                if (!negativeFlag) {
                    //> lda FireballBouncingFlag,x  ;if bouncing flag already set,
                    //> bne InitFireballExplode     ;branch to set exploding bit in fireball's state
                    if (zeroFlag) {
                        //> lda #$fd
                        //> sta Fireball_Y_Speed,x      ;otherwise set vertical speed to move upwards (give it bounce)
                        Fireball_Y_Speed[X] = 0xFD
                        //> lda #$01
                        //> sta FireballBouncingFlag,x  ;set bouncing flag
                        FireballBouncingFlag[X] = 0x01
                        //> lda Fireball_Y_Position,x
                        //> and #$f8                    ;modify vertical coordinate to land it properly
                        //> sta Fireball_Y_Position,x   ;store as new vertical coordinate
                        Fireball_Y_Position[X] = Fireball_Y_Position[X] and 0xF8
                        //> rts                         ;leave
                        return
                        //> ClearBounceFlag:
                        //> lda #$00
                        //> sta FireballBouncingFlag,x  ;clear bouncing flag by default
                        FireballBouncingFlag[X] = 0x00
                        //> rts                         ;leave
                        return
                    }
                }
            }
        }
    }
    //> InitFireballExplode:
    //> lda #$80
    //> sta Fireball_State,x        ;set exploding flag in fireball's state
    Fireball_State[X] = 0x80
    //> lda #Sfx_Bump
    //> sta Square1SoundQueue       ;load bump sound
    Square1SoundQueue = Sfx_Bump
    //> rts                         ;leave
    return
}

// Decompiled from GetFireballBoundBox
fun getFireballBoundBox(X: Int) {
    var A: Int = 0
    //> GetFireballBoundBox:
    //> txa         ;add seven bytes to offset
    //> clc         ;to use in routines as offset for fireball
    //> adc #$07
    //> tax
    //> ldy #$02    ;set offset for relative coordinates
    //> bne FBallB  ;unconditional branch
    if (zeroFlag) {
    }
    //> FBallB: jsr BoundingBoxCore       ;get bounding box coordinates
    boundingBoxCore((X + 0x07 + 0) and 0xFF, 0x02)
    //> jmp CheckRightScreenBBox  ;jump to handle any offscreen coordinates
    //> CheckRightScreenBBox:
    //> lda ScreenLeft_X_Pos       ;add 128 pixels to left side of screen
    //> clc                        ;and store as horizontal coordinate of middle
    //> adc #$80
    //> sta $02
    zp_02 = (ScreenLeft_X_Pos + 0x80 + 0) and 0xFF
    //> lda ScreenLeft_PageLoc     ;add carry to page location of left side of screen
    //> adc #$00                   ;and store as page location of middle
    //> sta $01
    zp_01 = (ScreenLeft_PageLoc + 0x00 + 0) and 0xFF
    //> lda SprObject_X_Position,x ;get horizontal coordinate
    //> cmp $02                    ;compare against middle horizontal coordinate
    //> lda SprObject_PageLoc,x    ;get page location
    //> sbc $01                    ;subtract from middle page location
    //> bcc CheckLeftScreenBBox    ;if object is on the left side of the screen, branch
    if (SprObject_X_Position[X] >= zp_02) {
        //> lda BoundingBox_DR_XPos,y  ;check right-side edge of bounding box for offscreen
        //> bmi NoOfs                  ;coordinates, branch if still on the screen
        if (!negativeFlag) {
            //> lda #$ff                   ;load offscreen value here to use on one or both horizontal sides
            //> ldx BoundingBox_UL_XPos,y  ;check left-side edge of bounding box for offscreen
            //> bmi SORte                  ;coordinates, and branch if still on the screen
            if (!negativeFlag) {
                //> sta BoundingBox_UL_XPos,y  ;store offscreen value for left side
                BoundingBox_UL_XPos[0x02] = 0xFF
            }
            //> SORte: sta BoundingBox_DR_XPos,y  ;store offscreen value for right side
            BoundingBox_DR_XPos[0x02] = 0xFF
        }
        //> NoOfs: ldx ObjectOffset           ;get object offset and leave
        //> rts
        return
    } else {
        //> CheckLeftScreenBBox:
        //> lda BoundingBox_UL_XPos,y  ;check left-side edge of bounding box for offscreen
        //> bpl NoOfs2                 ;coordinates, and branch if still on the screen
        if (negativeFlag) {
            //> cmp #$a0                   ;check to see if left-side edge is in the middle of the
            //> bcc NoOfs2                 ;screen or really offscreen, and branch if still on
            if (A >= 0xA0) {
                //> lda #$00
                //> ldx BoundingBox_DR_XPos,y  ;check right-side edge of bounding box for offscreen
                //> bpl SOLft                  ;coordinates, branch if still onscreen
                if (negativeFlag) {
                    //> sta BoundingBox_DR_XPos,y  ;store offscreen value for right side
                    BoundingBox_DR_XPos[0x02] = 0x00
                }
                //> SOLft:  sta BoundingBox_UL_XPos,y  ;store offscreen value for left side
                BoundingBox_UL_XPos[0x02] = 0x00
            }
        }
    }
    //> NoOfs2: ldx ObjectOffset           ;get object offset and leave
    //> rts
    return
}

// Decompiled from GetMiscBoundBox
fun getMiscBoundBox(X: Int) {
    var A: Int = 0
    //> GetMiscBoundBox:
    //> txa                       ;add nine bytes to offset
    //> clc                       ;to use in routines as offset for misc object
    //> adc #$09
    //> tax
    //> ldy #$06                  ;set offset for relative coordinates
    //> FBallB: jsr BoundingBoxCore       ;get bounding box coordinates
    boundingBoxCore((X + 0x09 + 0) and 0xFF, 0x06)
    //> jmp CheckRightScreenBBox  ;jump to handle any offscreen coordinates
    //> CheckRightScreenBBox:
    //> lda ScreenLeft_X_Pos       ;add 128 pixels to left side of screen
    //> clc                        ;and store as horizontal coordinate of middle
    //> adc #$80
    //> sta $02
    zp_02 = (ScreenLeft_X_Pos + 0x80 + 0) and 0xFF
    //> lda ScreenLeft_PageLoc     ;add carry to page location of left side of screen
    //> adc #$00                   ;and store as page location of middle
    //> sta $01
    zp_01 = (ScreenLeft_PageLoc + 0x00 + 0) and 0xFF
    //> lda SprObject_X_Position,x ;get horizontal coordinate
    //> cmp $02                    ;compare against middle horizontal coordinate
    //> lda SprObject_PageLoc,x    ;get page location
    //> sbc $01                    ;subtract from middle page location
    //> bcc CheckLeftScreenBBox    ;if object is on the left side of the screen, branch
    if (SprObject_X_Position[X] >= zp_02) {
        //> lda BoundingBox_DR_XPos,y  ;check right-side edge of bounding box for offscreen
        //> bmi NoOfs                  ;coordinates, branch if still on the screen
        if (!negativeFlag) {
            //> lda #$ff                   ;load offscreen value here to use on one or both horizontal sides
            //> ldx BoundingBox_UL_XPos,y  ;check left-side edge of bounding box for offscreen
            //> bmi SORte                  ;coordinates, and branch if still on the screen
            if (!negativeFlag) {
                //> sta BoundingBox_UL_XPos,y  ;store offscreen value for left side
                BoundingBox_UL_XPos[0x06] = 0xFF
            }
            //> SORte: sta BoundingBox_DR_XPos,y  ;store offscreen value for right side
            BoundingBox_DR_XPos[0x06] = 0xFF
        }
        //> NoOfs: ldx ObjectOffset           ;get object offset and leave
        //> rts
        return
    } else {
        //> CheckLeftScreenBBox:
        //> lda BoundingBox_UL_XPos,y  ;check left-side edge of bounding box for offscreen
        //> bpl NoOfs2                 ;coordinates, and branch if still on the screen
        if (negativeFlag) {
            //> cmp #$a0                   ;check to see if left-side edge is in the middle of the
            //> bcc NoOfs2                 ;screen or really offscreen, and branch if still on
            if (A >= 0xA0) {
                //> lda #$00
                //> ldx BoundingBox_DR_XPos,y  ;check right-side edge of bounding box for offscreen
                //> bpl SOLft                  ;coordinates, branch if still onscreen
                if (negativeFlag) {
                    //> sta BoundingBox_DR_XPos,y  ;store offscreen value for right side
                    BoundingBox_DR_XPos[0x06] = 0x00
                }
                //> SOLft:  sta BoundingBox_UL_XPos,y  ;store offscreen value for left side
                BoundingBox_UL_XPos[0x06] = 0x00
            }
        }
    }
    //> NoOfs2: ldx ObjectOffset           ;get object offset and leave
    //> rts
    return
}

// Decompiled from GetEnemyBoundBox
fun getEnemyBoundBox(X: Int) {
    var A: Int = 0
    //> GetEnemyBoundBox:
    //> ldy #$48                 ;store bitmask here for now
    //> sty $00
    zp_00 = 0x48
    //> ldy #$44                 ;store another bitmask here for now and jump
    //> jmp GetMaskedOffScrBits
    //> GetMaskedOffScrBits:
    //> lda Enemy_X_Position,x      ;get enemy object position relative
    //> sec                         ;to the left side of the screen
    //> sbc ScreenLeft_X_Pos
    //> sta $01                     ;store here
    zp_01 = (Enemy_X_Position[X] - ScreenLeft_X_Pos - (1 - 1)) and 0xFF
    //> lda Enemy_PageLoc,x         ;subtract borrow from current page location
    //> sbc ScreenLeft_PageLoc      ;of left side
    //> bmi CMBits                  ;if enemy object is beyond left edge, branch
    if (!negativeFlag) {
        //> ora $01
        //> beq CMBits                  ;if precisely at the left edge, branch
        if (!zeroFlag) {
            //> ldy $00                     ;if to the right of left edge, use value in $00 for A
        }
    }
    //> CMBits: tya                         ;otherwise use contents of Y
    //> and Enemy_OffscreenBits     ;preserve bitwise whatever's in here
    //> sta EnemyOffscrBitsMasked,x ;save masked offscreen bits here
    EnemyOffscrBitsMasked[X] = zp_00 and Enemy_OffscreenBits
    //> bne MoveBoundBoxOffscreen   ;if anything set here, branch
    if (zeroFlag) {
        //> jmp SetupEOffsetFBBox       ;otherwise, do something else
        //> SetupEOffsetFBBox:
        //> txa                        ;add 1 to offset to properly address
        //> clc                        ;the enemy object memory locations
        //> adc #$01
        //> tax
        //> ldy #$01                   ;load 1 as offset here, same reason
        //> jsr BoundingBoxCore        ;do a sub to get the coordinates of the bounding box
        boundingBoxCore((X + 0x01 + 0) and 0xFF, 0x01)
        //> jmp CheckRightScreenBBox   ;jump to handle offscreen coordinates of bounding box
    } else {
        //> MoveBoundBoxOffscreen:
        //> txa                            ;multiply offset by 4
        //> asl
        //> asl
        //> tay                            ;use as offset here
        //> lda #$ff
        //> sta EnemyBoundingBoxCoord,y    ;load value into four locations here and leave
        EnemyBoundingBoxCoord[(((X + 0x01 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF] = 0xFF
        //> sta EnemyBoundingBoxCoord+1,y
        EnemyBoundingBoxCoord[(((X + 0x01 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF] = 0xFF
        //> sta EnemyBoundingBoxCoord+2,y
        EnemyBoundingBoxCoord[(((X + 0x01 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF] = 0xFF
        //> sta EnemyBoundingBoxCoord+3,y
        EnemyBoundingBoxCoord[(((X + 0x01 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF] = 0xFF
        //> rts
        return
    }
    //> CheckRightScreenBBox:
    //> lda ScreenLeft_X_Pos       ;add 128 pixels to left side of screen
    //> clc                        ;and store as horizontal coordinate of middle
    //> adc #$80
    //> sta $02
    zp_02 = (ScreenLeft_X_Pos + 0x80 + 0) and 0xFF
    //> lda ScreenLeft_PageLoc     ;add carry to page location of left side of screen
    //> adc #$00                   ;and store as page location of middle
    //> sta $01
    zp_01 = (ScreenLeft_PageLoc + 0x00 + 0) and 0xFF
    //> lda SprObject_X_Position,x ;get horizontal coordinate
    //> cmp $02                    ;compare against middle horizontal coordinate
    //> lda SprObject_PageLoc,x    ;get page location
    //> sbc $01                    ;subtract from middle page location
    //> bcc CheckLeftScreenBBox    ;if object is on the left side of the screen, branch
    if (SprObject_X_Position[X] >= zp_02) {
        //> lda BoundingBox_DR_XPos,y  ;check right-side edge of bounding box for offscreen
        //> bmi NoOfs                  ;coordinates, branch if still on the screen
        if (!negativeFlag) {
            //> lda #$ff                   ;load offscreen value here to use on one or both horizontal sides
            //> ldx BoundingBox_UL_XPos,y  ;check left-side edge of bounding box for offscreen
            //> bmi SORte                  ;coordinates, and branch if still on the screen
            if (!negativeFlag) {
                //> sta BoundingBox_UL_XPos,y  ;store offscreen value for left side
                BoundingBox_UL_XPos[(((X + 0x01 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF] = 0xFF
            }
            //> SORte: sta BoundingBox_DR_XPos,y  ;store offscreen value for right side
            BoundingBox_DR_XPos[(((X + 0x01 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF] = 0xFF
        }
        //> NoOfs: ldx ObjectOffset           ;get object offset and leave
        //> rts
        return
    } else {
        //> CheckLeftScreenBBox:
        //> lda BoundingBox_UL_XPos,y  ;check left-side edge of bounding box for offscreen
        //> bpl NoOfs2                 ;coordinates, and branch if still on the screen
        if (negativeFlag) {
            //> cmp #$a0                   ;check to see if left-side edge is in the middle of the
            //> bcc NoOfs2                 ;screen or really offscreen, and branch if still on
            if (A >= 0xA0) {
                //> lda #$00
                //> ldx BoundingBox_DR_XPos,y  ;check right-side edge of bounding box for offscreen
                //> bpl SOLft                  ;coordinates, branch if still onscreen
                if (negativeFlag) {
                    //> sta BoundingBox_DR_XPos,y  ;store offscreen value for right side
                    BoundingBox_DR_XPos[(((X + 0x01 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF] = 0x00
                }
                //> SOLft:  sta BoundingBox_UL_XPos,y  ;store offscreen value for left side
                BoundingBox_UL_XPos[(((X + 0x01 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF] = 0x00
            }
        }
    }
    //> NoOfs2: ldx ObjectOffset           ;get object offset and leave
    //> rts
    return
}

// Decompiled from SmallPlatformBoundBox
fun smallPlatformBoundBox(X: Int) {
    var A: Int = 0
    //> SmallPlatformBoundBox:
    //> ldy #$08                 ;store bitmask here for now
    //> sty $00
    zp_00 = 0x08
    //> ldy #$04                 ;store another bitmask here for now
    //> GetMaskedOffScrBits:
    //> lda Enemy_X_Position,x      ;get enemy object position relative
    //> sec                         ;to the left side of the screen
    //> sbc ScreenLeft_X_Pos
    //> sta $01                     ;store here
    zp_01 = (Enemy_X_Position[X] - ScreenLeft_X_Pos - (1 - 1)) and 0xFF
    //> lda Enemy_PageLoc,x         ;subtract borrow from current page location
    //> sbc ScreenLeft_PageLoc      ;of left side
    //> bmi CMBits                  ;if enemy object is beyond left edge, branch
    if (!negativeFlag) {
        //> ora $01
        //> beq CMBits                  ;if precisely at the left edge, branch
        if (!zeroFlag) {
            //> ldy $00                     ;if to the right of left edge, use value in $00 for A
        }
    }
    //> CMBits: tya                         ;otherwise use contents of Y
    //> and Enemy_OffscreenBits     ;preserve bitwise whatever's in here
    //> sta EnemyOffscrBitsMasked,x ;save masked offscreen bits here
    EnemyOffscrBitsMasked[X] = zp_00 and Enemy_OffscreenBits
    //> bne MoveBoundBoxOffscreen   ;if anything set here, branch
    if (zeroFlag) {
        //> jmp SetupEOffsetFBBox       ;otherwise, do something else
        //> SetupEOffsetFBBox:
        //> txa                        ;add 1 to offset to properly address
        //> clc                        ;the enemy object memory locations
        //> adc #$01
        //> tax
        //> ldy #$01                   ;load 1 as offset here, same reason
        //> jsr BoundingBoxCore        ;do a sub to get the coordinates of the bounding box
        boundingBoxCore((X + 0x01 + 0) and 0xFF, 0x01)
        //> jmp CheckRightScreenBBox   ;jump to handle offscreen coordinates of bounding box
    } else {
        //> MoveBoundBoxOffscreen:
        //> txa                            ;multiply offset by 4
        //> asl
        //> asl
        //> tay                            ;use as offset here
        //> lda #$ff
        //> sta EnemyBoundingBoxCoord,y    ;load value into four locations here and leave
        EnemyBoundingBoxCoord[(((X + 0x01 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF] = 0xFF
        //> sta EnemyBoundingBoxCoord+1,y
        EnemyBoundingBoxCoord[(((X + 0x01 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF] = 0xFF
        //> sta EnemyBoundingBoxCoord+2,y
        EnemyBoundingBoxCoord[(((X + 0x01 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF] = 0xFF
        //> sta EnemyBoundingBoxCoord+3,y
        EnemyBoundingBoxCoord[(((X + 0x01 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF] = 0xFF
        //> rts
        return
    }
    //> CheckRightScreenBBox:
    //> lda ScreenLeft_X_Pos       ;add 128 pixels to left side of screen
    //> clc                        ;and store as horizontal coordinate of middle
    //> adc #$80
    //> sta $02
    zp_02 = (ScreenLeft_X_Pos + 0x80 + 0) and 0xFF
    //> lda ScreenLeft_PageLoc     ;add carry to page location of left side of screen
    //> adc #$00                   ;and store as page location of middle
    //> sta $01
    zp_01 = (ScreenLeft_PageLoc + 0x00 + 0) and 0xFF
    //> lda SprObject_X_Position,x ;get horizontal coordinate
    //> cmp $02                    ;compare against middle horizontal coordinate
    //> lda SprObject_PageLoc,x    ;get page location
    //> sbc $01                    ;subtract from middle page location
    //> bcc CheckLeftScreenBBox    ;if object is on the left side of the screen, branch
    if (SprObject_X_Position[X] >= zp_02) {
        //> lda BoundingBox_DR_XPos,y  ;check right-side edge of bounding box for offscreen
        //> bmi NoOfs                  ;coordinates, branch if still on the screen
        if (!negativeFlag) {
            //> lda #$ff                   ;load offscreen value here to use on one or both horizontal sides
            //> ldx BoundingBox_UL_XPos,y  ;check left-side edge of bounding box for offscreen
            //> bmi SORte                  ;coordinates, and branch if still on the screen
            if (!negativeFlag) {
                //> sta BoundingBox_UL_XPos,y  ;store offscreen value for left side
                BoundingBox_UL_XPos[(((X + 0x01 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF] = 0xFF
            }
            //> SORte: sta BoundingBox_DR_XPos,y  ;store offscreen value for right side
            BoundingBox_DR_XPos[(((X + 0x01 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF] = 0xFF
        }
        //> NoOfs: ldx ObjectOffset           ;get object offset and leave
        //> rts
        return
    } else {
        //> CheckLeftScreenBBox:
        //> lda BoundingBox_UL_XPos,y  ;check left-side edge of bounding box for offscreen
        //> bpl NoOfs2                 ;coordinates, and branch if still on the screen
        if (negativeFlag) {
            //> cmp #$a0                   ;check to see if left-side edge is in the middle of the
            //> bcc NoOfs2                 ;screen or really offscreen, and branch if still on
            if (A >= 0xA0) {
                //> lda #$00
                //> ldx BoundingBox_DR_XPos,y  ;check right-side edge of bounding box for offscreen
                //> bpl SOLft                  ;coordinates, branch if still onscreen
                if (negativeFlag) {
                    //> sta BoundingBox_DR_XPos,y  ;store offscreen value for right side
                    BoundingBox_DR_XPos[(((X + 0x01 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF] = 0x00
                }
                //> SOLft:  sta BoundingBox_UL_XPos,y  ;store offscreen value for left side
                BoundingBox_UL_XPos[(((X + 0x01 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF] = 0x00
            }
        }
    }
    //> NoOfs2: ldx ObjectOffset           ;get object offset and leave
    //> rts
    return
}

// Decompiled from LargePlatformBoundBox
fun largePlatformBoundBox(A: Int, X: Int) {
    //> LargePlatformBoundBox:
    //> inx                        ;increment X to get the proper offset
    //> jsr GetXOffscreenBits      ;then jump directly to the sub for horizontal offscreen bits
    getXOffscreenBits((X + 1) and 0xFF)
    //> dex                        ;decrement to return to original offset
    //> cmp #$fe                   ;if completely offscreen, branch to put entire bounding
    //> bcs MoveBoundBoxOffscreen  ;box offscreen, otherwise start getting coordinates
    if (!(A >= 0xFE)) {
        //> SetupEOffsetFBBox:
        //> txa                        ;add 1 to offset to properly address
        //> clc                        ;the enemy object memory locations
        //> adc #$01
        //> tax
        //> ldy #$01                   ;load 1 as offset here, same reason
        //> jsr BoundingBoxCore        ;do a sub to get the coordinates of the bounding box
        boundingBoxCore((((X + 1) and 0xFF - 1) and 0xFF + 0x01 + 0) and 0xFF, 0x01)
        //> jmp CheckRightScreenBBox   ;jump to handle offscreen coordinates of bounding box
    } else {
        //> MoveBoundBoxOffscreen:
        //> txa                            ;multiply offset by 4
        //> asl
        //> asl
        //> tay                            ;use as offset here
        //> lda #$ff
        //> sta EnemyBoundingBoxCoord,y    ;load value into four locations here and leave
        EnemyBoundingBoxCoord[(((((X + 1) and 0xFF - 1) and 0xFF + 0x01 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF] = 0xFF
        //> sta EnemyBoundingBoxCoord+1,y
        EnemyBoundingBoxCoord[(((((X + 1) and 0xFF - 1) and 0xFF + 0x01 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF] = 0xFF
        //> sta EnemyBoundingBoxCoord+2,y
        EnemyBoundingBoxCoord[(((((X + 1) and 0xFF - 1) and 0xFF + 0x01 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF] = 0xFF
        //> sta EnemyBoundingBoxCoord+3,y
        EnemyBoundingBoxCoord[(((((X + 1) and 0xFF - 1) and 0xFF + 0x01 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF] = 0xFF
        //> rts
        return
    }
    //> CheckRightScreenBBox:
    //> lda ScreenLeft_X_Pos       ;add 128 pixels to left side of screen
    //> clc                        ;and store as horizontal coordinate of middle
    //> adc #$80
    //> sta $02
    zp_02 = (ScreenLeft_X_Pos + 0x80 + 0) and 0xFF
    //> lda ScreenLeft_PageLoc     ;add carry to page location of left side of screen
    //> adc #$00                   ;and store as page location of middle
    //> sta $01
    zp_01 = (ScreenLeft_PageLoc + 0x00 + 0) and 0xFF
    //> lda SprObject_X_Position,x ;get horizontal coordinate
    //> cmp $02                    ;compare against middle horizontal coordinate
    //> lda SprObject_PageLoc,x    ;get page location
    //> sbc $01                    ;subtract from middle page location
    //> bcc CheckLeftScreenBBox    ;if object is on the left side of the screen, branch
    if (SprObject_X_Position[X] >= zp_02) {
        //> lda BoundingBox_DR_XPos,y  ;check right-side edge of bounding box for offscreen
        //> bmi NoOfs                  ;coordinates, branch if still on the screen
        if (!negativeFlag) {
            //> lda #$ff                   ;load offscreen value here to use on one or both horizontal sides
            //> ldx BoundingBox_UL_XPos,y  ;check left-side edge of bounding box for offscreen
            //> bmi SORte                  ;coordinates, and branch if still on the screen
            if (!negativeFlag) {
                //> sta BoundingBox_UL_XPos,y  ;store offscreen value for left side
                BoundingBox_UL_XPos[(((((X + 1) and 0xFF - 1) and 0xFF + 0x01 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF] = 0xFF
            }
            //> SORte: sta BoundingBox_DR_XPos,y  ;store offscreen value for right side
            BoundingBox_DR_XPos[(((((X + 1) and 0xFF - 1) and 0xFF + 0x01 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF] = 0xFF
        }
        //> NoOfs: ldx ObjectOffset           ;get object offset and leave
        //> rts
        return
    } else {
        //> CheckLeftScreenBBox:
        //> lda BoundingBox_UL_XPos,y  ;check left-side edge of bounding box for offscreen
        //> bpl NoOfs2                 ;coordinates, and branch if still on the screen
        if (negativeFlag) {
            //> cmp #$a0                   ;check to see if left-side edge is in the middle of the
            //> bcc NoOfs2                 ;screen or really offscreen, and branch if still on
            if (A >= 0xA0) {
                //> lda #$00
                //> ldx BoundingBox_DR_XPos,y  ;check right-side edge of bounding box for offscreen
                //> bpl SOLft                  ;coordinates, branch if still onscreen
                if (negativeFlag) {
                    //> sta BoundingBox_DR_XPos,y  ;store offscreen value for right side
                    BoundingBox_DR_XPos[(((((X + 1) and 0xFF - 1) and 0xFF + 0x01 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF] = 0x00
                }
                //> SOLft:  sta BoundingBox_UL_XPos,y  ;store offscreen value for left side
                BoundingBox_UL_XPos[(((((X + 1) and 0xFF - 1) and 0xFF + 0x01 + 0) and 0xFF shl 1) and 0xFF shl 1) and 0xFF] = 0x00
            }
        }
    }
    //> NoOfs2: ldx ObjectOffset           ;get object offset and leave
    //> rts
    return
}

// Decompiled from BoundingBoxCore
fun boundingBoxCore(X: Int, Y: Int) {
    //> BoundingBoxCore:
    //> stx $00                     ;save offset here
    zp_00 = X
    //> lda SprObject_Rel_YPos,y    ;store object coordinates relative to screen
    //> sta $02                     ;vertically and horizontally, respectively
    zp_02 = SprObject_Rel_YPos[Y]
    //> lda SprObject_Rel_XPos,y
    //> sta $01
    zp_01 = SprObject_Rel_XPos[Y]
    //> txa                         ;multiply offset by four and save to stack
    //> asl
    //> asl
    //> pha
    //> tay                         ;use as offset for Y, X is left alone
    //> lda SprObj_BoundBoxCtrl,x   ;load value here to be used as offset for X
    //> asl                         ;multiply that by four and use as X
    //> asl
    //> tax
    //> lda $01                     ;add the first number in the bounding box data to the
    //> clc                         ;relative horizontal coordinate using enemy object offset
    //> adc BoundBoxCtrlData,x      ;and store somewhere using same offset * 4
    //> sta BoundingBox_UL_Corner,y ;store here
    BoundingBox_UL_Corner[((X shl 1) and 0xFF shl 1) and 0xFF] = (zp_01 + BoundBoxCtrlData[((SprObj_BoundBoxCtrl[X] shl 1) and 0xFF shl 1) and 0xFF] + 0) and 0xFF
    //> lda $01
    //> clc
    //> adc BoundBoxCtrlData+2,x    ;add the third number in the bounding box data to the
    //> sta BoundingBox_LR_Corner,y ;relative horizontal coordinate and store
    BoundingBox_LR_Corner[((X shl 1) and 0xFF shl 1) and 0xFF] = (zp_01 + BoundBoxCtrlData[((SprObj_BoundBoxCtrl[X] shl 1) and 0xFF shl 1) and 0xFF] + 0) and 0xFF
    //> inx                         ;increment both offsets
    //> iny
    //> lda $02                     ;add the second number to the relative vertical coordinate
    //> clc                         ;using incremented offset and store using the other
    //> adc BoundBoxCtrlData,x      ;incremented offset
    //> sta BoundingBox_UL_Corner,y
    BoundingBox_UL_Corner[(((X shl 1) and 0xFF shl 1) and 0xFF + 1) and 0xFF] = (zp_02 + BoundBoxCtrlData[(((SprObj_BoundBoxCtrl[X] shl 1) and 0xFF shl 1) and 0xFF + 1) and 0xFF] + 0) and 0xFF
    //> lda $02
    //> clc
    //> adc BoundBoxCtrlData+2,x    ;add the fourth number to the relative vertical coordinate
    //> sta BoundingBox_LR_Corner,y ;and store
    BoundingBox_LR_Corner[(((X shl 1) and 0xFF shl 1) and 0xFF + 1) and 0xFF] = (zp_02 + BoundBoxCtrlData[(((SprObj_BoundBoxCtrl[X] shl 1) and 0xFF shl 1) and 0xFF + 1) and 0xFF] + 0) and 0xFF
    //> pla                         ;get original offset loaded into $00 * y from stack
    //> tay                         ;use as Y
    //> ldx $00                     ;get original offset and use as X again
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
    var A: Int = 0
    //> SprObjectCollisionCore:
    //> sty $06      ;save contents of Y here
    zp_06 = Y
    //> lda #$01
    //> sta $07      ;save value 1 here as counter, compare horizontal coordinates first
    zp_07 = 0x01
    //> CollisionCoreLoop:
    //> lda BoundingBox_UL_Corner,y  ;compare left/top coordinates
    //> cmp BoundingBox_UL_Corner,x  ;of first and second objects' bounding boxes
    //> bcs FirstBoxGreater          ;if first left/top => second, branch
    if (!(BoundingBox_UL_Corner[Y] >= BoundingBox_UL_Corner[X])) {
        //> cmp BoundingBox_LR_Corner,x  ;otherwise compare to right/bottom of second
        //> bcc SecondBoxVerticalChk     ;if first left/top < second right/bottom, branch elsewhere
        if (A >= BoundingBox_LR_Corner[X]) {
            //> beq CollisionFound           ;if somehow equal, collision, thus branch
            if (!(A - BoundingBox_LR_Corner[X] == 0)) {
                //> lda BoundingBox_LR_Corner,y  ;if somehow greater, check to see if bottom of
                //> cmp BoundingBox_UL_Corner,y  ;first object's bounding box is greater than its top
                //> bcc CollisionFound           ;if somehow less, vertical wrap collision, thus branch
                if (BoundingBox_LR_Corner[Y] >= BoundingBox_UL_Corner[Y]) {
                    //> cmp BoundingBox_UL_Corner,x  ;otherwise compare bottom of first bounding box to the top
                    //> bcs CollisionFound           ;of second box, and if equal or greater, collision, thus branch
                    if (!(A >= BoundingBox_UL_Corner[X])) {
                        //> ldy $06                      ;otherwise return with carry clear and Y = $0006
                        //> rts                          ;note horizontal wrapping never occurs
                        return
                        //> SecondBoxVerticalChk:
                        //> lda BoundingBox_LR_Corner,x  ;check to see if the vertical bottom of the box
                        //> cmp BoundingBox_UL_Corner,x  ;is greater than the vertical top
                        //> bcc CollisionFound           ;if somehow less, vertical wrap collision, thus branch
                        if (BoundingBox_LR_Corner[X] >= BoundingBox_UL_Corner[X]) {
                            //> lda BoundingBox_LR_Corner,y  ;otherwise compare horizontal right or vertical bottom
                            //> cmp BoundingBox_UL_Corner,x  ;of first box with horizontal left or vertical top of second box
                            //> bcs CollisionFound           ;if equal or greater, collision, thus branch
                            if (!(BoundingBox_LR_Corner[Y] >= BoundingBox_UL_Corner[X])) {
                                //> ldy $06                      ;otherwise return with carry clear and Y = $0006
                                //> rts
                                return
                                //> FirstBoxGreater:
                                //> cmp BoundingBox_UL_Corner,x  ;compare first and second box horizontal left/vertical top again
                                //> beq CollisionFound           ;if first coordinate = second, collision, thus branch
                                if (!(A - BoundingBox_UL_Corner[X] == 0)) {
                                    //> cmp BoundingBox_LR_Corner,x  ;if not, compare with second object right or bottom edge
                                    //> bcc CollisionFound           ;if left/top of first less than or equal to right/bottom of second
                                    if (A >= BoundingBox_LR_Corner[X]) {
                                        //> beq CollisionFound           ;then collision, thus branch
                                        if (!(A - BoundingBox_LR_Corner[X] == 0)) {
                                            //> cmp BoundingBox_LR_Corner,y  ;otherwise check to see if top of first box is greater than bottom
                                            //> bcc NoCollisionFound         ;if less than or equal, no collision, branch to end
                                            if (A >= BoundingBox_LR_Corner[Y]) {
                                                //> beq NoCollisionFound
                                                if (!(A - BoundingBox_LR_Corner[Y] == 0)) {
                                                    //> lda BoundingBox_LR_Corner,y  ;otherwise compare bottom of first to top of second
                                                    //> cmp BoundingBox_UL_Corner,x  ;if bottom of first is greater than top of second, vertical wrap
                                                    //> bcs CollisionFound           ;collision, and branch, otherwise, proceed onwards here
                                                    if (!(BoundingBox_LR_Corner[Y] >= BoundingBox_UL_Corner[X])) {
                                                        //> NoCollisionFound:
                                                        //> clc          ;clear carry, then load value set earlier, then leave
                                                        //> ldy $06      ;like previous ones, if horizontal coordinates do not collide, we do
                                                        //> rts          ;not bother checking vertical ones, because what's the point?
                                                        return
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (BoundingBox_LR_Corner[X] >= BoundingBox_UL_Corner[X]) {
            if (!(BoundingBox_LR_Corner[Y] >= BoundingBox_UL_Corner[X])) {
                if (!(A - BoundingBox_UL_Corner[X] == 0)) {
                    if (A >= BoundingBox_LR_Corner[X]) {
                        if (!(A - BoundingBox_LR_Corner[X] == 0)) {
                            if (A >= BoundingBox_LR_Corner[Y]) {
                                if (!(A - BoundingBox_LR_Corner[Y] == 0)) {
                                    if (!(BoundingBox_LR_Corner[Y] >= BoundingBox_UL_Corner[X])) {
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (!(A - BoundingBox_UL_Corner[X] == 0)) {
        if (A >= BoundingBox_LR_Corner[X]) {
            if (!(A - BoundingBox_LR_Corner[X] == 0)) {
                if (A >= BoundingBox_LR_Corner[Y]) {
                    if (!(A - BoundingBox_LR_Corner[Y] == 0)) {
                        if (!(BoundingBox_LR_Corner[Y] >= BoundingBox_UL_Corner[X])) {
                        }
                    }
                }
            }
        }
    }
    //> CollisionFound:
    //> inx                    ;increment offsets on both objects to check
    //> iny                    ;the vertical coordinates
    //> dec $07                ;decrement counter to reflect this
    zp_07 = (zp_07 - 1) and 0xFF
    //> bpl CollisionCoreLoop  ;if counter not expired, branch to loop
    //> sec                    ;otherwise we already did both sets, therefore collision, so set carry
    //> ldy $06                ;load original value set here earlier, then leave
    //> rts
    return
}

// Decompiled from BlockBufferChk_Enemy
fun blockbufferchkEnemy(A: Int, X: Int) {
    var Y: Int = 0
    //> BlockBufferChk_Enemy:
    //> pha        ;save contents of A to stack
    //> txa
    //> clc        ;add 1 to X to run sub with enemy offset in mind
    //> adc #$01
    //> tax
    //> pla        ;pull A from stack and jump elsewhere
    //> jmp BBChk_E
    //> BBChk_E: jsr BlockBufferCollision  ;do collision detection subroutine for sprite object
    blockBufferCollision((X + 0x01 + 0) and 0xFF, (X + 0x01 + 0) and 0xFF, Y)
    //> ldx ObjectOffset          ;get object offset
    //> cmp #$00                  ;check to see if object bumped into anything
    //> rts
    return
}

// Decompiled from BlockBufferChk_FBall
fun blockbufferchkFball(X: Int) {
    //> BlockBufferChk_FBall:
    //> ldy #$1a                  ;set offset for block buffer adder data
    //> txa
    //> clc
    //> adc #$07                  ;add seven bytes to use
    //> tax
    //> ResJmpM: lda #$00                  ;set A to return vertical coordinate
    //> BBChk_E: jsr BlockBufferCollision  ;do collision detection subroutine for sprite object
    blockBufferCollision(0x00, (X + 0x07 + 0) and 0xFF, 0x1A)
    //> ldx ObjectOffset          ;get object offset
    //> cmp #$00                  ;check to see if object bumped into anything
    //> rts
    return
}

// Decompiled from BlockBufferColli_Feet
fun blockbuffercolliFeet(Y: Int) {
    //> BlockBufferColli_Feet:
    //> iny            ;if branched here, increment to next set of adders
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
    //> BlockBufferCollision:
    //> pha                         ;save contents of A to stack
    //> sty $04                     ;save contents of Y here
    zp_04 = Y
    //> lda BlockBuffer_X_Adder,y   ;add horizontal coordinate
    //> clc                         ;of object to value obtained using Y as offset
    //> adc SprObject_X_Position,x
    //> sta $05                     ;store here
    zp_05 = (BlockBuffer_X_Adder[Y] + SprObject_X_Position[X] + 0) and 0xFF
    //> lda SprObject_PageLoc,x
    //> adc #$00                    ;add carry to page location
    //> and #$01                    ;get LSB, mask out all other bits
    //> lsr                         ;move to carry
    //> ora $05                     ;get stored value
    //> ror                         ;rotate carry to MSB of A
    //> lsr                         ;and effectively move high nybble to
    //> lsr                         ;lower, LSB which became MSB will be
    //> lsr                         ;d4 at this point
    //> jsr GetBlockBufferAddr      ;get address of block buffer into $06, $07
    getBlockBufferAddr((SprObject_PageLoc[X] + 0x00 + 0) and 0xFF and 0x01 shr 1 or zp_05 shr 1 shr 1 shr 1)
    //> ldy $04                     ;get old contents of Y
    //> lda SprObject_Y_Position,x  ;get vertical coordinate of object
    //> clc
    //> adc BlockBuffer_Y_Adder,y   ;add it to value obtained using Y as offset
    //> and #%11110000              ;mask out low nybble
    //> sec
    //> sbc #$20                    ;subtract 32 pixels for the status bar
    //> sta $02                     ;store result here
    zp_02 = ((SprObject_Y_Position[X] + BlockBuffer_Y_Adder[zp_04] + 0) and 0xFF and 0xF0 - 0x20 - (1 - 1)) and 0xFF
    //> tay                         ;use as offset for block buffer
    //> lda ($06),y                 ;check current content of block buffer
    //> sta $03                     ;and store here
    zp_03 = TODO
    //> ldy $04                     ;get old contents of Y again
    //> pla                         ;pull A from stack
    //> bne RetXC                   ;if A = 1, branch
    if (zeroFlag) {
        //> lda SprObject_Y_Position,x  ;if A = 0, load vertical coordinate
        //> jmp RetYC                   ;and jump
    } else {
        //> RetXC: lda SprObject_X_Position,x  ;otherwise load horizontal coordinate
    }
    //> RetYC: and #%00001111              ;and mask out high nybble
    //> sta $04                     ;store masked out result here
    zp_04 = SprObject_X_Position[X] and 0x0F
    //> lda $03                     ;get saved content of block buffer
    //> rts                         ;and leave
    return A
}

// Decompiled from DrawVine
fun drawVine(Y: Int) {
    var X: Int = 0
    //> DrawVine:
    //> sty $00                    ;save offset here
    zp_00 = Y
    //> lda Enemy_Rel_YPos         ;get relative vertical coordinate
    //> clc
    //> adc VineYPosAdder,y        ;add value using offset in Y to get value
    //> ldx VineObjOffset,y        ;get offset to vine
    //> ldy Enemy_SprDataOffset,x  ;get sprite data offset
    //> sty $02                    ;store sprite data offset here
    zp_02 = Enemy_SprDataOffset[VineObjOffset[Y]]
    //> jsr SixSpriteStacker       ;stack six sprites on top of each other vertically
    sixSpriteStacker((Enemy_Rel_YPos + VineYPosAdder[Y] + 0) and 0xFF, Enemy_SprDataOffset[VineObjOffset[Y]])
    //> lda Enemy_Rel_XPos         ;get relative horizontal coordinate
    //> sta Sprite_X_Position,y    ;store in first, third and fifth sprites
    Sprite_X_Position[Enemy_SprDataOffset[VineObjOffset[Y]]] = Enemy_Rel_XPos
    //> sta Sprite_X_Position+8,y
    Sprite_X_Position[Enemy_SprDataOffset[VineObjOffset[Y]]] = Enemy_Rel_XPos
    //> sta Sprite_X_Position+16,y
    Sprite_X_Position[Enemy_SprDataOffset[VineObjOffset[Y]]] = Enemy_Rel_XPos
    //> clc
    //> adc #$06                   ;add six pixels to second, fourth and sixth sprites
    //> sta Sprite_X_Position+4,y  ;to give characteristic staggered vine shape to
    Sprite_X_Position[Enemy_SprDataOffset[VineObjOffset[Y]]] = (Enemy_Rel_XPos + 0x06 + 0) and 0xFF
    //> sta Sprite_X_Position+12,y ;our vertical stack of sprites
    Sprite_X_Position[Enemy_SprDataOffset[VineObjOffset[Y]]] = (Enemy_Rel_XPos + 0x06 + 0) and 0xFF
    //> sta Sprite_X_Position+20,y
    Sprite_X_Position[Enemy_SprDataOffset[VineObjOffset[Y]]] = (Enemy_Rel_XPos + 0x06 + 0) and 0xFF
    //> lda #%00100001             ;set bg priority and palette attribute bits
    //> sta Sprite_Attributes,y    ;set in first, third and fifth sprites
    Sprite_Attributes[Enemy_SprDataOffset[VineObjOffset[Y]]] = 0x21
    //> sta Sprite_Attributes+8,y
    Sprite_Attributes[Enemy_SprDataOffset[VineObjOffset[Y]]] = 0x21
    //> sta Sprite_Attributes+16,y
    Sprite_Attributes[Enemy_SprDataOffset[VineObjOffset[Y]]] = 0x21
    //> ora #%01000000             ;additionally, set horizontal flip bit
    //> sta Sprite_Attributes+4,y  ;for second, fourth and sixth sprites
    Sprite_Attributes[Enemy_SprDataOffset[VineObjOffset[Y]]] = 0x21 or 0x40
    //> sta Sprite_Attributes+12,y
    Sprite_Attributes[Enemy_SprDataOffset[VineObjOffset[Y]]] = 0x21 or 0x40
    //> sta Sprite_Attributes+20,y
    Sprite_Attributes[Enemy_SprDataOffset[VineObjOffset[Y]]] = 0x21 or 0x40
    //> ldx #$05                   ;set tiles for six sprites
    do {
        //> VineTL:  lda #$e1                   ;set tile number for sprite
        //> sta Sprite_Tilenumber,y
        Sprite_Tilenumber[Enemy_SprDataOffset[VineObjOffset[Y]]] = 0xE1
        //> iny                        ;move offset to next sprite data
        //> iny
        //> iny
        //> iny
        //> dex                        ;move onto next sprite
        //> bpl VineTL                 ;loop until all sprites are done
    } while (negativeFlag)
    //> ldy $02                    ;get original offset
    //> lda $00                    ;get offset to vine adding data
    //> bne SkpVTop                ;if offset not zero, skip this part
    if (zeroFlag) {
        //> lda #$e0
        //> sta Sprite_Tilenumber,y    ;set other tile number for top of vine
        Sprite_Tilenumber[zp_02] = 0xE0
    }
    //> SkpVTop: ldx #$00                   ;start with the first sprite again
    while ((X + 1) and 0xFF == 0x06) {
        //> lda #$f8
        //> sta Sprite_Y_Position,y    ;otherwise move sprite offscreen
        Sprite_Y_Position[zp_02] = 0xF8
        do {
            //> ChkFTop: lda VineStart_Y_Position   ;get original starting vertical coordinate
            //> sec
            //> sbc Sprite_Y_Position,y    ;subtract top-most sprite's Y coordinate
            //> cmp #$64                   ;if two coordinates are less than 100/$64 pixels
            //> bcc NextVSp                ;apart, skip this to leave sprite alone
            //> NextVSp: iny                        ;move offset to next OAM data
            //> iny
            //> iny
            //> iny
            //> inx                        ;move onto next sprite
            //> cpx #$06                   ;do this until all sprites are checked
            //> bne ChkFTop
        } while (!((X + 1) and 0xFF == 0x06))
    }
    //> ldy $00                    ;return offset set earlier
    //> rts
    return
}

// Decompiled from SixSpriteStacker
fun sixSpriteStacker(A: Int, Y: Int) {
    //> SixSpriteStacker:
    //> ldx #$06           ;do six sprites
    do {
        //> StkLp: sta Sprite_Data,y  ;store X or Y coordinate into OAM data
        Sprite_Data[Y] = A
        //> clc
        //> adc #$08           ;add eight pixels
        //> iny
        //> iny                ;move offset four bytes forward
        //> iny
        //> iny
        //> dex                ;do another sprite
        //> bne StkLp          ;do this until all sprites are done
    } while (zeroFlag)
    //> ldy $02            ;get saved OAM data offset and leave
    //> rts
    return
}

// Decompiled from DrawHammer
fun drawHammer(X: Int) {
    //> DrawHammer:
    //> ldy Misc_SprDataOffset,x    ;get misc object OAM data offset
    //> lda TimerControl
    //> bne ForceHPose              ;if master timer control set, skip this part
    if (zeroFlag) {
        //> lda Misc_State,x            ;otherwise get hammer's state
        //> and #%01111111              ;mask out d7
        //> cmp #$01                    ;check to see if set to 1 yet
        //> beq GetHPose                ;if so, branch
        if (!(Misc_State[X] and 0x7F - 0x01 == 0)) {
            //> ForceHPose: ldx #$00                    ;reset offset here
            //> beq RenderH                 ;do unconditional branch to rendering part
            if (!zeroFlag) {
                //> GetHPose:   lda FrameCounter            ;get frame counter
                //> lsr                         ;move d3-d2 to d1-d0
                //> lsr
                //> and #%00000011              ;mask out all but d1-d0 (changes every four frames)
                //> tax                         ;use as timing offset
            }
        }
    }
    if (!zeroFlag) {
    }
    //> RenderH:    lda Misc_Rel_YPos           ;get relative vertical coordinate
    //> clc
    //> adc FirstSprYPos,x          ;add first sprite vertical adder based on offset
    //> sta Sprite_Y_Position,y     ;store as sprite Y coordinate for first sprite
    Sprite_Y_Position[Misc_SprDataOffset[X]] = (Misc_Rel_YPos + FirstSprYPos[FrameCounter shr 1 shr 1 and 0x03] + 0) and 0xFF
    //> clc
    //> adc SecondSprYPos,x         ;add second sprite vertical adder based on offset
    //> sta Sprite_Y_Position+4,y   ;store as sprite Y coordinate for second sprite
    Sprite_Y_Position[Misc_SprDataOffset[X]] = ((Misc_Rel_YPos + FirstSprYPos[FrameCounter shr 1 shr 1 and 0x03] + 0) and 0xFF + SecondSprYPos[FrameCounter shr 1 shr 1 and 0x03] + 0) and 0xFF
    //> lda Misc_Rel_XPos           ;get relative horizontal coordinate
    //> clc
    //> adc FirstSprXPos,x          ;add first sprite horizontal adder based on offset
    //> sta Sprite_X_Position,y     ;store as sprite X coordinate for first sprite
    Sprite_X_Position[Misc_SprDataOffset[X]] = (Misc_Rel_XPos + FirstSprXPos[FrameCounter shr 1 shr 1 and 0x03] + 0) and 0xFF
    //> clc
    //> adc SecondSprXPos,x         ;add second sprite horizontal adder based on offset
    //> sta Sprite_X_Position+4,y   ;store as sprite X coordinate for second sprite
    Sprite_X_Position[Misc_SprDataOffset[X]] = ((Misc_Rel_XPos + FirstSprXPos[FrameCounter shr 1 shr 1 and 0x03] + 0) and 0xFF + SecondSprXPos[FrameCounter shr 1 shr 1 and 0x03] + 0) and 0xFF
    //> lda FirstSprTilenum,x
    //> sta Sprite_Tilenumber,y     ;get and store tile number of first sprite
    Sprite_Tilenumber[Misc_SprDataOffset[X]] = FirstSprTilenum[FrameCounter shr 1 shr 1 and 0x03]
    //> lda SecondSprTilenum,x
    //> sta Sprite_Tilenumber+4,y   ;get and store tile number of second sprite
    Sprite_Tilenumber[Misc_SprDataOffset[X]] = SecondSprTilenum[FrameCounter shr 1 shr 1 and 0x03]
    //> lda HammerSprAttrib,x
    //> sta Sprite_Attributes,y     ;get and store attribute bytes for both
    Sprite_Attributes[Misc_SprDataOffset[X]] = HammerSprAttrib[FrameCounter shr 1 shr 1 and 0x03]
    //> sta Sprite_Attributes+4,y   ;note in this case they use the same data
    Sprite_Attributes[Misc_SprDataOffset[X]] = HammerSprAttrib[FrameCounter shr 1 shr 1 and 0x03]
    //> ldx ObjectOffset            ;get misc object offset
    //> lda Misc_OffscreenBits
    //> and #%11111100              ;check offscreen bits
    //> beq NoHOffscr               ;if all bits clear, leave object alone
    if (!zeroFlag) {
        //> lda #$00
        //> sta Misc_State,x            ;otherwise nullify misc object state
        Misc_State[ObjectOffset] = 0x00
        //> lda #$f8
        //> jsr DumpTwoSpr              ;do sub to move hammer sprites offscreen
        dumpTwoSpr(0xF8, Misc_SprDataOffset[X])
    }
    //> NoHOffscr:  rts                         ;leave
    return
}

// Decompiled from FlagpoleGfxHandler
fun flagpoleGfxHandler(X: Int) {
    //> FlagpoleGfxHandler:
    //> ldy Enemy_SprDataOffset,x      ;get sprite data offset for flagpole flag
    //> lda Enemy_Rel_XPos             ;get relative horizontal coordinate
    //> sta Sprite_X_Position,y        ;store as X coordinate for first sprite
    Sprite_X_Position[Enemy_SprDataOffset[X]] = Enemy_Rel_XPos
    //> clc
    //> adc #$08                       ;add eight pixels and store
    //> sta Sprite_X_Position+4,y      ;as X coordinate for second and third sprites
    Sprite_X_Position[Enemy_SprDataOffset[X]] = (Enemy_Rel_XPos + 0x08 + 0) and 0xFF
    //> sta Sprite_X_Position+8,y
    Sprite_X_Position[Enemy_SprDataOffset[X]] = (Enemy_Rel_XPos + 0x08 + 0) and 0xFF
    //> clc
    //> adc #$0c                       ;add twelve more pixels and
    //> sta $05                        ;store here to be used later by floatey number
    zp_05 = ((Enemy_Rel_XPos + 0x08 + 0) and 0xFF + 0x0C + 0) and 0xFF
    //> lda Enemy_Y_Position,x         ;get vertical coordinate
    //> jsr DumpTwoSpr                 ;and do sub to dump into first and second sprites
    dumpTwoSpr(Enemy_Y_Position[X], Enemy_SprDataOffset[X])
    //> adc #$08                       ;add eight pixels
    //> sta Sprite_Y_Position+8,y      ;and store into third sprite
    Sprite_Y_Position[Enemy_SprDataOffset[X]] = (Enemy_Y_Position[X] + 0x08 + 0) and 0xFF
    //> lda FlagpoleFNum_Y_Pos         ;get vertical coordinate for floatey number
    //> sta $02                        ;store it here
    zp_02 = FlagpoleFNum_Y_Pos
    //> lda #$01
    //> sta $03                        ;set value for flip which will not be used, and
    zp_03 = 0x01
    //> sta $04                        ;attribute byte for floatey number
    zp_04 = 0x01
    //> sta Sprite_Attributes,y        ;set attribute bytes for all three sprites
    Sprite_Attributes[Enemy_SprDataOffset[X]] = 0x01
    //> sta Sprite_Attributes+4,y
    Sprite_Attributes[Enemy_SprDataOffset[X]] = 0x01
    //> sta Sprite_Attributes+8,y
    Sprite_Attributes[Enemy_SprDataOffset[X]] = 0x01
    //> lda #$7e
    //> sta Sprite_Tilenumber,y        ;put triangle shaped tile
    Sprite_Tilenumber[Enemy_SprDataOffset[X]] = 0x7E
    //> sta Sprite_Tilenumber+8,y      ;into first and third sprites
    Sprite_Tilenumber[Enemy_SprDataOffset[X]] = 0x7E
    //> lda #$7f
    //> sta Sprite_Tilenumber+4,y      ;put skull tile into second sprite
    Sprite_Tilenumber[Enemy_SprDataOffset[X]] = 0x7F
    //> lda FlagpoleCollisionYPos      ;get vertical coordinate at time of collision
    //> beq ChkFlagOffscreen           ;if zero, branch ahead
    if (!zeroFlag) {
        //> tya
        //> clc                            ;add 12 bytes to sprite data offset
        //> adc #$0c
        //> tay                            ;put back in Y
        //> lda FlagpoleScore              ;get offset used to award points for touching flagpole
        //> asl                            ;multiply by 2 to get proper offset here
        //> tax
        //> lda FlagpoleScoreNumTiles,x    ;get appropriate tile data
        //> sta $00
        zp_00 = FlagpoleScoreNumTiles[(FlagpoleScore shl 1) and 0xFF]
        //> lda FlagpoleScoreNumTiles+1,x
        //> jsr DrawOneSpriteRow           ;use it to render floatey number
        drawOneSpriteRow(FlagpoleScoreNumTiles[(FlagpoleScore shl 1) and 0xFF], (FlagpoleScore shl 1) and 0xFF, (Enemy_SprDataOffset[X] + 0x0C + 0) and 0xFF)
    }
    //> ChkFlagOffscreen:
    //> ldx ObjectOffset               ;get object offset for flag
    //> ldy Enemy_SprDataOffset,x      ;get OAM data offset
    //> lda Enemy_OffscreenBits        ;get offscreen bits
    //> and #%00001110                 ;mask out all but d3-d1
    //> beq ExitDumpSpr                ;if none of these bits set, branch to leave
    if (!zeroFlag) {
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
    //> DumpSixSpr:
    //> sta Sprite_Data+20,y      ;dump A contents
    Sprite_Data[Y] = A
    //> sta Sprite_Data+16,y      ;into third row sprites
    Sprite_Data[Y] = A
}

// Decompiled from DumpFourSpr
fun dumpFourSpr(A: Int, Y: Int) {
    //> DumpFourSpr:
    //> sta Sprite_Data+12,y      ;into second row sprites
    Sprite_Data[Y] = A
}

// Decompiled from DumpThreeSpr
fun dumpThreeSpr(A: Int, Y: Int) {
    //> DumpThreeSpr:
    //> sta Sprite_Data+8,y
    Sprite_Data[Y] = A
}

// Decompiled from DumpTwoSpr
fun dumpTwoSpr(A: Int, Y: Int) {
    //> DumpTwoSpr:
    //> sta Sprite_Data+4,y       ;and into first row sprites
    Sprite_Data[Y] = A
    //> sta Sprite_Data,y
    Sprite_Data[Y] = A
    //> ExitDumpSpr:
    //> rts
    return
}

// Decompiled from DrawLargePlatform
fun drawLargePlatform(X: Int) {
    //> DrawLargePlatform:
    //> ldy Enemy_SprDataOffset,x   ;get OAM data offset
    //> sty $02                     ;store here
    zp_02 = Enemy_SprDataOffset[X]
    //> iny                         ;add 3 to it for offset
    //> iny                         ;to X coordinate
    //> iny
    //> lda Enemy_Rel_XPos          ;get horizontal relative coordinate
    //> jsr SixSpriteStacker        ;store X coordinates using A as base, stack horizontally
    sixSpriteStacker(Enemy_Rel_XPos, (((Enemy_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF + 1) and 0xFF)
    //> ldx ObjectOffset
    //> lda Enemy_Y_Position,x      ;get vertical coordinate
    //> jsr DumpFourSpr             ;dump into first four sprites as Y coordinate
    dumpFourSpr(Enemy_Y_Position[ObjectOffset], (((Enemy_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF + 1) and 0xFF)
    //> ldy AreaType
    //> cpy #$03                    ;check for castle-type level
    //> beq ShrinkPlatform
    if (!(AreaType == 0x03)) {
        //> ldy SecondaryHardMode       ;check for secondary hard mode flag set
        //> beq SetLast2Platform        ;branch if not set elsewhere
        if (!zeroFlag) {
            //> ShrinkPlatform:
            //> lda #$f8                    ;load offscreen coordinate if flag set or castle-type level
        }
    }
    //> SetLast2Platform:
    //> ldy Enemy_SprDataOffset,x   ;get OAM data offset
    //> sta Sprite_Y_Position+16,y  ;store vertical coordinate or offscreen
    Sprite_Y_Position[Enemy_SprDataOffset[ObjectOffset]] = 0xF8
    //> sta Sprite_Y_Position+20,y  ;coordinate into last two sprites as Y coordinate
    Sprite_Y_Position[Enemy_SprDataOffset[ObjectOffset]] = 0xF8
    //> lda #$5b                    ;load default tile for platform (girder)
    //> ldx CloudTypeOverride
    //> beq SetPlatformTilenum      ;if cloud level override flag not set, use
    if (!zeroFlag) {
        //> lda #$75                    ;otherwise load other tile for platform (puff)
    }
    //> SetPlatformTilenum:
    //> ldx ObjectOffset            ;get enemy object buffer offset
    //> iny                         ;increment Y for tile offset
    //> jsr DumpSixSpr              ;dump tile number into all six sprites
    dumpSixSpr(0x75, (Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF)
    //> lda #$02                    ;set palette controls
    //> iny                         ;increment Y for sprite attributes
    //> jsr DumpSixSpr              ;dump attributes into all six sprites
    dumpSixSpr(0x02, ((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF)
    //> inx                         ;increment X for enemy objects
    //> jsr GetXOffscreenBits       ;get offscreen bits again
    getXOffscreenBits((ObjectOffset + 1) and 0xFF)
    //> dex
    //> ldy Enemy_SprDataOffset,x   ;get OAM data offset
    //> asl                         ;rotate d7 into carry, save remaining
    //> pha                         ;bits to the stack
    //> bcc SChk2
    if (carryFlag) {
        //> lda #$f8                    ;if d7 was set, move first sprite offscreen
        //> sta Sprite_Y_Position,y
        Sprite_Y_Position[Enemy_SprDataOffset[((ObjectOffset + 1) and 0xFF - 1) and 0xFF]] = 0xF8
    }
    //> SChk2:  pla                         ;get bits from stack
    //> asl                         ;rotate d6 into carry
    //> pha                         ;save to stack
    //> bcc SChk3
    if (carryFlag) {
        //> lda #$f8                    ;if d6 was set, move second sprite offscreen
        //> sta Sprite_Y_Position+4,y
        Sprite_Y_Position[Enemy_SprDataOffset[((ObjectOffset + 1) and 0xFF - 1) and 0xFF]] = 0xF8
    }
    //> SChk3:  pla                         ;get bits from stack
    //> asl                         ;rotate d5 into carry
    //> pha                         ;save to stack
    //> bcc SChk4
    if (carryFlag) {
        //> lda #$f8                    ;if d5 was set, move third sprite offscreen
        //> sta Sprite_Y_Position+8,y
        Sprite_Y_Position[Enemy_SprDataOffset[((ObjectOffset + 1) and 0xFF - 1) and 0xFF]] = 0xF8
    }
    //> SChk4:  pla                         ;get bits from stack
    //> asl                         ;rotate d4 into carry
    //> pha                         ;save to stack
    //> bcc SChk5
    if (carryFlag) {
        //> lda #$f8                    ;if d4 was set, move fourth sprite offscreen
        //> sta Sprite_Y_Position+12,y
        Sprite_Y_Position[Enemy_SprDataOffset[((ObjectOffset + 1) and 0xFF - 1) and 0xFF]] = 0xF8
    }
    //> SChk5:  pla                         ;get bits from stack
    //> asl                         ;rotate d3 into carry
    //> pha                         ;save to stack
    //> bcc SChk6
    if (carryFlag) {
        //> lda #$f8                    ;if d3 was set, move fifth sprite offscreen
        //> sta Sprite_Y_Position+16,y
        Sprite_Y_Position[Enemy_SprDataOffset[((ObjectOffset + 1) and 0xFF - 1) and 0xFF]] = 0xF8
    }
    //> SChk6:  pla                         ;get bits from stack
    //> asl                         ;rotate d2 into carry
    //> bcc SLChk                   ;save to stack
    if (carryFlag) {
        //> lda #$f8
        //> sta Sprite_Y_Position+20,y  ;if d2 was set, move sixth sprite offscreen
        Sprite_Y_Position[Enemy_SprDataOffset[((ObjectOffset + 1) and 0xFF - 1) and 0xFF]] = 0xF8
    }
    //> SLChk:  lda Enemy_OffscreenBits     ;check d7 of offscreen bits
    //> asl                         ;and if d7 is not set, skip sub
    //> bcc ExDLPl
    if (carryFlag) {
        //> jsr MoveSixSpritesOffscreen ;otherwise branch to move all sprites offscreen
        moveSixSpritesOffscreen()
    }
    //> ExDLPl: rts
    return
}

// Decompiled from JCoinGfxHandler
fun jCoinGfxHandler(X: Int) {
    var Y: Int = 0
    //> DrawFloateyNumber_Coin:
    //> lda FrameCounter          ;get frame counter
    //> lsr                       ;divide by 2
    //> bcs NotRsNum              ;branch if d0 not set to raise number every other frame
    if (!carryFlag) {
        //> dec Misc_Y_Position,x     ;otherwise, decrement vertical coordinate
        Misc_Y_Position[X] = (Misc_Y_Position[X] - 1) and 0xFF
    }
    //> NotRsNum: lda Misc_Y_Position,x     ;get vertical coordinate
    //> jsr DumpTwoSpr            ;dump into both sprites
    dumpTwoSpr(Misc_Y_Position[X], Y)
    //> lda Misc_Rel_XPos         ;get relative horizontal coordinate
    //> sta Sprite_X_Position,y   ;store as X coordinate for first sprite
    Sprite_X_Position[Y] = Misc_Rel_XPos
    //> clc
    //> adc #$08                  ;add eight pixels
    //> sta Sprite_X_Position+4,y ;store as X coordinate for second sprite
    Sprite_X_Position[Y] = (Misc_Rel_XPos + 0x08 + 0) and 0xFF
    //> lda #$02
    //> sta Sprite_Attributes,y   ;store attribute byte in both sprites
    Sprite_Attributes[Y] = 0x02
    //> sta Sprite_Attributes+4,y
    Sprite_Attributes[Y] = 0x02
    //> lda #$f7
    //> sta Sprite_Tilenumber,y   ;put tile numbers into both sprites
    Sprite_Tilenumber[Y] = 0xF7
    //> lda #$fb                  ;that resemble "200"
    //> sta Sprite_Tilenumber+4,y
    Sprite_Tilenumber[Y] = 0xFB
    //> jmp ExJCGfx               ;then jump to leave (why not an rts here instead?)
    //> JCoinGfxHandler:
    //> ldy Misc_SprDataOffset,x    ;get coin/floatey number's OAM data offset
    //> lda Misc_State,x            ;get state of misc object
    //> cmp #$02                    ;if 2 or greater,
    //> bcs DrawFloateyNumber_Coin  ;branch to draw floatey number
    //> lda Misc_Y_Position,x       ;store vertical coordinate as
    //> sta Sprite_Y_Position,y     ;Y coordinate for first sprite
    Sprite_Y_Position[Misc_SprDataOffset[X]] = Misc_Y_Position[X]
    //> clc
    //> adc #$08                    ;add eight pixels
    //> sta Sprite_Y_Position+4,y   ;store as Y coordinate for second sprite
    Sprite_Y_Position[Misc_SprDataOffset[X]] = (Misc_Y_Position[X] + 0x08 + 0) and 0xFF
    //> lda Misc_Rel_XPos           ;get relative horizontal coordinate
    //> sta Sprite_X_Position,y
    Sprite_X_Position[Misc_SprDataOffset[X]] = Misc_Rel_XPos
    //> sta Sprite_X_Position+4,y   ;store as X coordinate for first and second sprites
    Sprite_X_Position[Misc_SprDataOffset[X]] = Misc_Rel_XPos
    //> lda FrameCounter            ;get frame counter
    //> lsr                         ;divide by 2 to alter every other frame
    //> and #%00000011              ;mask out d2-d1
    //> tax                         ;use as graphical offset
    //> lda JumpingCoinTiles,x      ;load tile number
    //> iny                         ;increment OAM data offset to write tile numbers
    //> jsr DumpTwoSpr              ;do sub to dump tile number into both sprites
    dumpTwoSpr(JumpingCoinTiles[FrameCounter shr 1 and 0x03], (Misc_SprDataOffset[X] + 1) and 0xFF)
    //> dey                         ;decrement to get old offset
    //> lda #$02
    //> sta Sprite_Attributes,y     ;set attribute byte in first sprite
    Sprite_Attributes[((Misc_SprDataOffset[X] + 1) and 0xFF - 1) and 0xFF] = 0x02
    //> lda #$82
    //> sta Sprite_Attributes+4,y   ;set attribute byte with vertical flip in second sprite
    Sprite_Attributes[((Misc_SprDataOffset[X] + 1) and 0xFF - 1) and 0xFF] = 0x82
    //> ldx ObjectOffset            ;get misc object offset
    //> ExJCGfx: rts                         ;leave
    return
}

// Decompiled from DrawPowerUp
fun drawPowerUp() {
    var A: Int = 0
    var X: Int = 0
    //> DrawPowerUp:
    //> ldy Enemy_SprDataOffset+5  ;get power-up's sprite data offset
    //> lda Enemy_Rel_YPos         ;get relative vertical coordinate
    //> clc
    //> adc #$08                   ;add eight pixels
    //> sta $02                    ;store result here
    zp_02 = (Enemy_Rel_YPos + 0x08 + 0) and 0xFF
    //> lda Enemy_Rel_XPos         ;get relative horizontal coordinate
    //> sta $05                    ;store here
    zp_05 = Enemy_Rel_XPos
    //> ldx PowerUpType            ;get power-up type
    //> lda PowerUpAttributes,x    ;get attribute data for power-up type
    //> ora Enemy_SprAttrib+5      ;add background priority bit if set
    //> sta $04                    ;store attributes here
    zp_04 = PowerUpAttributes[PowerUpType] or Enemy_SprAttrib
    //> txa
    //> pha                        ;save power-up type to the stack
    //> asl
    //> asl                        ;multiply by four to get proper offset
    //> tax                        ;use as X
    //> lda #$01
    //> sta $07                    ;set counter here to draw two rows of sprite object
    zp_07 = 0x01
    //> sta $03                    ;init d1 of flip control
    zp_03 = 0x01
    do {
        //> PUpDrawLoop:
        //> lda PowerUpGfxTable,x      ;load left tile of power-up object
        //> sta $00
        zp_00 = PowerUpGfxTable[((PowerUpType shl 1) and 0xFF shl 1) and 0xFF]
        //> lda PowerUpGfxTable+1,x    ;load right tile
        //> jsr DrawOneSpriteRow       ;branch to draw one row of our power-up object
        drawOneSpriteRow(PowerUpGfxTable[((PowerUpType shl 1) and 0xFF shl 1) and 0xFF], ((PowerUpType shl 1) and 0xFF shl 1) and 0xFF, Enemy_SprDataOffset)
        //> dec $07                    ;decrement counter
        zp_07 = (zp_07 - 1) and 0xFF
        //> bpl PUpDrawLoop            ;branch until two rows are drawn
    } while (negativeFlag)
    //> ldy Enemy_SprDataOffset+5  ;get sprite data offset again
    //> pla                        ;pull saved power-up type from the stack
    //> beq PUpOfs                 ;if regular mushroom, branch, do not change colors or flip
    if (!zeroFlag) {
        //> cmp #$03
        //> beq PUpOfs                 ;if 1-up mushroom, branch, do not change colors or flip
        if (!(A - 0x03 == 0)) {
            //> sta $00                    ;store power-up type here now
            zp_00 = PowerUpGfxTable[((PowerUpType shl 1) and 0xFF shl 1) and 0xFF]
            //> lda FrameCounter           ;get frame counter
            //> lsr                        ;divide by 2 to change colors every two frames
            //> and #%00000011             ;mask out all but d1 and d0 (previously d2 and d1)
            //> ora Enemy_SprAttrib+5      ;add background priority bit if any set
            //> sta Sprite_Attributes,y    ;set as new palette bits for top left and
            Sprite_Attributes[Enemy_SprDataOffset] = FrameCounter shr 1 and 0x03 or Enemy_SprAttrib
            //> sta Sprite_Attributes+4,y  ;top right sprites for fire flower and star
            Sprite_Attributes[Enemy_SprDataOffset] = FrameCounter shr 1 and 0x03 or Enemy_SprAttrib
            //> ldx $00
            //> dex                        ;check power-up type for fire flower
            //> beq FlipPUpRightSide       ;if found, skip this part
            if (!zeroFlag) {
                //> sta Sprite_Attributes+8,y  ;otherwise set new palette bits  for bottom left
                Sprite_Attributes[Enemy_SprDataOffset] = FrameCounter shr 1 and 0x03 or Enemy_SprAttrib
                //> sta Sprite_Attributes+12,y ;and bottom right sprites as well for star only
                Sprite_Attributes[Enemy_SprDataOffset] = FrameCounter shr 1 and 0x03 or Enemy_SprAttrib
            }
            //> FlipPUpRightSide:
            //> lda Sprite_Attributes+4,y
            //> ora #%01000000             ;set horizontal flip bit for top right sprite
            //> sta Sprite_Attributes+4,y
            Sprite_Attributes[Enemy_SprDataOffset] = Sprite_Attributes[Enemy_SprDataOffset] or 0x40
            //> lda Sprite_Attributes+12,y
            //> ora #%01000000             ;set horizontal flip bit for bottom right sprite
            //> sta Sprite_Attributes+12,y ;note these are only done for fire flower and star power-ups
            Sprite_Attributes[Enemy_SprDataOffset] = Sprite_Attributes[Enemy_SprDataOffset] or 0x40
        }
    }
    //> PUpOfs: jmp SprObjectOffscrChk     ;jump to check to see if power-up is offscreen at all, then leave
    //> SprObjectOffscrChk:
    //> ldx ObjectOffset          ;get enemy buffer offset
    //> lda Enemy_OffscreenBits   ;check offscreen information
    //> lsr
    //> lsr                       ;shift three times to the right
    //> lsr                       ;which puts d2 into carry
    //> pha                       ;save to stack
    //> bcc LcChk                 ;branch if not set
    if (carryFlag) {
        //> lda #$04                  ;set for right column sprites
        //> jsr MoveESprColOffscreen  ;and move them offscreen
        moveESprColOffscreen(0x04, ObjectOffset)
    }
    //> LcChk:   pla                       ;get from stack
    //> lsr                       ;move d3 to carry
    //> pha                       ;save to stack
    //> bcc Row3C                 ;branch if not set
    if (carryFlag) {
        //> lda #$00                  ;set for left column sprites,
        //> jsr MoveESprColOffscreen  ;move them offscreen
        moveESprColOffscreen(0x00, ObjectOffset)
    }
    //> Row3C:   pla                       ;get from stack again
    //> lsr                       ;move d5 to carry this time
    //> lsr
    //> pha                       ;save to stack again
    //> bcc Row23C                ;branch if carry not set
    if (carryFlag) {
        //> lda #$10                  ;set for third row of sprites
        //> jsr MoveESprRowOffscreen  ;and move them offscreen
        moveESprRowOffscreen(0x10, ObjectOffset)
    }
    //> Row23C:  pla                       ;get from stack
    //> lsr                       ;move d6 into carry
    //> pha                       ;save to stack
    //> bcc AllRowC
    if (carryFlag) {
        //> lda #$08                  ;set for second and third rows
        //> jsr MoveESprRowOffscreen  ;move them offscreen
        moveESprRowOffscreen(0x08, ObjectOffset)
    }
    //> AllRowC: pla                       ;get from stack once more
    //> lsr                       ;move d7 into carry
    //> bcc ExEGHandler
    if (carryFlag) {
        //> jsr MoveESprRowOffscreen  ;move all sprites offscreen (A should be 0 by now)
        moveESprRowOffscreen(0x08 shr 1, ObjectOffset)
        //> lda Enemy_ID,x
        //> cmp #Podoboo              ;check enemy identifier for podoboo
        //> beq ExEGHandler           ;skip this part if found, we do not want to erase podoboo!
        if (!(Enemy_ID[X] - Podoboo == 0)) {
            //> lda Enemy_Y_HighPos,x     ;check high byte of vertical position
            //> cmp #$02                  ;if not yet past the bottom of the screen, branch
            //> bne ExEGHandler
            if (Enemy_Y_HighPos[X] - 0x02 == 0) {
                //> jsr EraseEnemyObject      ;what it says
                eraseEnemyObject(ObjectOffset)
            }
        }
    }
    //> ExEGHandler:
    //> rts
    return
}

// Decompiled from EnemyGfxHandler
fun enemyGfxHandler(X: Int) {
    var A: Int = 0
    var Y: Int = 0
    //> EnemyGfxHandler:
    //> lda Enemy_Y_Position,x      ;get enemy object vertical position
    //> sta $02
    zp_02 = Enemy_Y_Position[X]
    //> lda Enemy_Rel_XPos          ;get enemy object horizontal position
    //> sta $05                     ;relative to screen
    zp_05 = Enemy_Rel_XPos
    //> ldy Enemy_SprDataOffset,x
    //> sty $eb                     ;get sprite data offset
    zp_eb = Enemy_SprDataOffset[X]
    //> lda #$00
    //> sta VerticalFlipFlag        ;initialize vertical flip flag by default
    VerticalFlipFlag = 0x00
    //> lda Enemy_MovingDir,x
    //> sta $03                     ;get enemy object moving direction
    zp_03 = Enemy_MovingDir[X]
    //> lda Enemy_SprAttrib,x
    //> sta $04                     ;get enemy object sprite attributes
    zp_04 = Enemy_SprAttrib[X]
    //> lda Enemy_ID,x
    //> cmp #PiranhaPlant           ;is enemy object piranha plant?
    //> bne CheckForRetainerObj     ;if not, branch
    if (Enemy_ID[X] - PiranhaPlant == 0) {
        //> ldy PiranhaPlant_Y_Speed,x
        //> bmi CheckForRetainerObj     ;if piranha plant moving upwards, branch
        if (!negativeFlag) {
            //> ldy EnemyFrameTimer,x
            //> beq CheckForRetainerObj     ;if timer for movement expired, branch
            if (!zeroFlag) {
                //> rts                         ;if all conditions fail, leave
                return
            }
        }
    }
    //> CheckForRetainerObj:
    //> lda Enemy_State,x           ;store enemy state
    //> sta $ed
    zp_ed = Enemy_State[X]
    //> and #%00011111              ;nullify all but 5 LSB and use as Y
    //> tay
    //> lda Enemy_ID,x              ;check for mushroom retainer/princess object
    //> cmp #RetainerObject
    //> bne CheckForBulletBillCV    ;if not found, branch
    if (Enemy_ID[X] - RetainerObject == 0) {
        //> ldy #$00                    ;if found, nullify saved state in Y
        //> lda #$01                    ;set value that will not be used
        //> sta $03
        zp_03 = 0x01
        //> lda #$15                    ;set value $15 as code for mushroom retainer/princess object
    }
    //> CheckForBulletBillCV:
    //> cmp #BulletBill_CannonVar   ;otherwise check for bullet bill object
    //> bne CheckForJumpspring      ;if not found, branch again
    if (A - BulletBill_CannonVar == 0) {
        //> dec $02                     ;decrement saved vertical position
        zp_02 = (zp_02 - 1) and 0xFF
        //> lda #$03
        //> ldy EnemyFrameTimer,x       ;get timer for enemy object
        //> beq SBBAt                   ;if expired, do not set priority bit
        if (!zeroFlag) {
            //> ora #%00100000              ;otherwise do so
        }
        //> SBBAt: sta $04                     ;set new sprite attributes
        zp_04 = 0x03 or 0x20
        //> ldy #$00                    ;nullify saved enemy state both in Y and in
        //> sty $ed                     ;memory location here
        zp_ed = 0x00
        //> lda #$08                    ;set specific value to unconditionally branch once
    }
    //> CheckForJumpspring:
    //> cmp #JumpspringObject        ;check for jumpspring object
    //> bne CheckForPodoboo
    if (A - JumpspringObject == 0) {
        //> ldy #$03                     ;set enemy state -2 MSB here for jumpspring object
        //> ldx JumpspringAnimCtrl       ;get current frame number for jumpspring object
        //> lda JumpspringFrameOffsets,x ;load data using frame number as offset
    }
    //> CheckForPodoboo:
    //> sta $ef                 ;store saved enemy object value here
    zp_ef = JumpspringFrameOffsets[JumpspringAnimCtrl]
    //> sty $ec                 ;and Y here (enemy state -2 MSB if not changed)
    zp_ec = 0x03
    //> ldx ObjectOffset        ;get enemy object offset
    //> cmp #$0c                ;check for podoboo object
    //> bne CheckBowserGfxFlag  ;branch if not found
    if (A - 0x0C == 0) {
        //> lda Enemy_Y_Speed,x     ;if moving upwards, branch
        //> bmi CheckBowserGfxFlag
        if (!negativeFlag) {
            //> inc VerticalFlipFlag    ;otherwise, set flag for vertical flip
            VerticalFlipFlag = (VerticalFlipFlag + 1) and 0xFF
        }
    }
    //> CheckBowserGfxFlag:
    //> lda BowserGfxFlag   ;if not drawing bowser at all, skip to something else
    //> beq CheckForGoomba
    if (!zeroFlag) {
        //> ldy #$16            ;if set to 1, draw bowser's front
        //> cmp #$01
        //> beq SBwsrGfxOfs
        if (!(A - 0x01 == 0)) {
            //> iny                 ;otherwise draw bowser's rear
        }
        //> SBwsrGfxOfs: sty $ef
        zp_ef = (0x16 + 1) and 0xFF
    }
    //> CheckForGoomba:
    //> ldy $ef               ;check value for goomba object
    //> cpy #Goomba
    //> bne CheckBowserFront  ;branch if not found
    if (zp_ef == Goomba) {
        //> lda Enemy_State,x
        //> cmp #$02              ;check for defeated state
        //> bcc GmbaAnim          ;if not defeated, go ahead and animate
        if (Enemy_State[X] >= 0x02) {
            //> ldx #$04              ;if defeated, write new value here
            //> stx $ec
            zp_ec = 0x04
        }
        //> GmbaAnim: and #%00100000        ;check for d5 set in enemy object state
        //> ora TimerControl      ;or timer disable flag set
        //> bne CheckBowserFront  ;if either condition true, do not animate goomba
        if (zeroFlag) {
            //> lda FrameCounter
            //> and #%00001000        ;check for every eighth frame
            //> bne CheckBowserFront
            if (zeroFlag) {
                //> lda $03
                //> eor #%00000011        ;invert bits to flip horizontally every eight frames
                //> sta $03               ;leave alone otherwise
                zp_03 = zp_03 xor 0x03
            }
        }
    }
    //> CheckBowserFront:
    //> lda EnemyAttributeData,y    ;load sprite attribute using enemy object
    //> ora $04                     ;as offset, and add to bits already loaded
    //> sta $04
    zp_04 = EnemyAttributeData[zp_ef] or zp_04
    //> lda EnemyGfxTableOffsets,y  ;load value based on enemy object as offset
    //> tax                         ;save as X
    //> ldy $ec                     ;get previously saved value
    //> lda BowserGfxFlag
    //> beq CheckForSpiny           ;if not drawing bowser object at all, skip all of this
    if (!zeroFlag) {
        //> cmp #$01
        //> bne CheckBowserRear         ;if not drawing front part, branch to draw the rear part
        if (A - 0x01 == 0) {
            //> lda BowserBodyControls      ;check bowser's body control bits
            //> bpl ChkFrontSte             ;branch if d7 not set (control's bowser's mouth)
            if (negativeFlag) {
                //> ldx #$de                    ;otherwise load offset for second frame
            }
            //> ChkFrontSte: lda $ed                     ;check saved enemy state
            //> and #%00100000              ;if bowser not defeated, do not set flag
            //> beq DrawBowser
            if (!zeroFlag) {
                //> FlipBowserOver:
                //> stx VerticalFlipFlag  ;set vertical flip flag to nonzero
                VerticalFlipFlag = 0xDE
            }
            //> DrawBowser:
            //> jmp DrawEnemyObject   ;draw bowser's graphics now
        }
        //> CheckBowserRear:
        //> lda BowserBodyControls  ;check bowser's body control bits
        //> and #$01
        //> beq ChkRearSte          ;branch if d0 not set (control's bowser's feet)
        if (!zeroFlag) {
            //> ldx #$e4                ;otherwise load offset for second frame
        }
        do {
            //> ChkRearSte: lda $ed                 ;check saved enemy state
            //> and #%00100000          ;if bowser not defeated, do not set flag
            //> beq DrawBowser
        } while (zeroFlag)
        while (true) {
            //> lda $02                 ;subtract 16 pixels from
            //> sec                     ;saved vertical coordinate
            //> sbc #$10
            //> sta $02
            zp_02 = (zp_02 - 0x10 - (1 - 1)) and 0xFF
            //> jmp FlipBowserOver      ;jump to set vertical flip flag
        }
    }
    //> CheckForSpiny:
    //> cpx #$24               ;check if value loaded is for spiny
    //> bne CheckForLakitu     ;if not found, branch
    if (X == 0x24) {
        //> cpy #$05               ;if enemy state set to $05, do this,
        //> bne NotEgg             ;otherwise branch
        if (Y == 0x05) {
            //> ldx #$30               ;set to spiny egg offset
            //> lda #$02
            //> sta $03                ;set enemy direction to reverse sprites horizontally
            zp_03 = 0x02
            //> lda #$05
            //> sta $ec                ;set enemy state
            zp_ec = 0x05
        }
    } else {
        //> CheckForLakitu:
        //> cpx #$90                  ;check value for lakitu's offset loaded
        //> bne CheckUpsideDownShell  ;branch if not loaded
        if (X == 0x90) {
            //> lda $ed
            //> and #%00100000            ;check for d5 set in enemy state
            //> bne NoLAFr                ;branch if set
            if (zeroFlag) {
                //> lda FrenzyEnemyTimer
                //> cmp #$10                  ;check timer to see if we've reached a certain range
                //> bcs NoLAFr                ;branch if not
                if (!(FrenzyEnemyTimer >= 0x10)) {
                    //> ldx #$96                  ;if d6 not set and timer in range, load alt frame for lakitu
                }
            }
            //> NoLAFr: jmp CheckDefeatedState    ;skip this next part if we found lakitu but alt frame not needed
        }
        //> CheckUpsideDownShell:
        //> lda $ef                    ;check for enemy object => $04
        //> cmp #$04
        //> bcs CheckRightSideUpShell  ;branch if true
        if (!(zp_ef >= 0x04)) {
            //> cpy #$02
            //> bcc CheckRightSideUpShell  ;branch if enemy state < $02
            if (Y >= 0x02) {
                //> ldx #$5a                   ;set for upside-down koopa shell by default
                //> ldy $ef
                //> cpy #BuzzyBeetle           ;check for buzzy beetle object
                //> bne CheckRightSideUpShell
                if (zp_ef == BuzzyBeetle) {
                    //> ldx #$7e                   ;set for upside-down buzzy beetle shell if found
                    //> inc $02                    ;increment vertical position by one pixel
                    zp_02 = (zp_02 + 1) and 0xFF
                }
            }
        }
        //> CheckRightSideUpShell:
        //> lda $ec                ;check for value set here
        //> cmp #$04               ;if enemy state < $02, do not change to shell, if
        //> bne CheckForHammerBro  ;enemy state => $02 but not = $04, leave shell upside-down
        if (zp_ec - 0x04 == 0) {
            //> ldx #$72               ;set right-side up buzzy beetle shell by default
            //> inc $02                ;increment saved vertical position by one pixel
            zp_02 = (zp_02 + 1) and 0xFF
            //> ldy $ef
            //> cpy #BuzzyBeetle       ;check for buzzy beetle object
            //> beq CheckForDefdGoomba ;branch if found
            if (!(zp_ef == BuzzyBeetle)) {
                //> ldx #$66               ;change to right-side up koopa shell if not found
                //> inc $02                ;and increment saved vertical position again
                zp_02 = (zp_02 + 1) and 0xFF
            }
            //> CheckForDefdGoomba:
            //> cpy #Goomba            ;check for goomba object (necessary if previously
            //> bne CheckForHammerBro  ;failed buzzy beetle object test)
            if (Y == Goomba) {
                //> ldx #$54               ;load for regular goomba
                //> lda $ed                ;note that this only gets performed if enemy state => $02
                //> and #%00100000         ;check saved enemy state for d5 set
                //> bne CheckForHammerBro  ;branch if set
                if (zeroFlag) {
                    //> ldx #$8a               ;load offset for defeated goomba
                    //> dec $02                ;set different value and decrement saved vertical position
                    zp_02 = (zp_02 - 1) and 0xFF
                }
            }
        }
    }
    //> CheckForHammerBro:
    //> ldy ObjectOffset
    //> lda $ef                  ;check for hammer bro object
    //> cmp #HammerBro
    //> bne CheckForBloober      ;branch if not found
    if (zp_ef - HammerBro == 0) {
        //> lda $ed
        //> beq CheckToAnimateEnemy  ;branch if not in normal enemy state
        if (!zeroFlag) {
            //> and #%00001000
            //> beq CheckDefeatedState   ;if d3 not set, branch further away
            if (!zeroFlag) {
                //> ldx #$b4                 ;otherwise load offset for different frame
                //> bne CheckToAnimateEnemy  ;unconditional branch
                if (zeroFlag) {
                    //> CheckForBloober:
                    //> cpx #$48                 ;check for cheep-cheep offset loaded
                    //> beq CheckToAnimateEnemy  ;branch if found
                    if (!(X == 0x48)) {
                        //> lda EnemyIntervalTimer,y
                        //> cmp #$05
                        //> bcs CheckDefeatedState   ;branch if some timer is above a certain point
                        if (!(EnemyIntervalTimer[Y] >= 0x05)) {
                            //> cpx #$3c                 ;check for bloober offset loaded
                            //> bne CheckToAnimateEnemy  ;branch if not found this time
                            if (X == 0x3C) {
                                //> cmp #$01
                                //> beq CheckDefeatedState   ;branch if timer is set to certain point
                                if (!(A - 0x01 == 0)) {
                                    //> inc $02                  ;increment saved vertical coordinate three pixels
                                    zp_02 = (zp_02 + 1) and 0xFF
                                    //> inc $02
                                    zp_02 = (zp_02 + 1) and 0xFF
                                    //> inc $02
                                    zp_02 = (zp_02 + 1) and 0xFF
                                    //> jmp CheckAnimationStop   ;and do something else
                                    //> CheckToAnimateEnemy:
                                    //> lda $ef                  ;check for specific enemy objects
                                    //> cmp #Goomba
                                    //> beq CheckDefeatedState   ;branch if goomba
                                    if (!(zp_ef - Goomba == 0)) {
                                        //> cmp #$08
                                        //> beq CheckDefeatedState   ;branch if bullet bill (note both variants use $08 here)
                                        if (!(A - 0x08 == 0)) {
                                            //> cmp #Podoboo
                                            //> beq CheckDefeatedState   ;branch if podoboo
                                            if (!(A - Podoboo == 0)) {
                                                //> cmp #$18                 ;branch if => $18
                                                //> bcs CheckDefeatedState
                                                if (!(A >= 0x18)) {
                                                    //> ldy #$00
                                                    //> cmp #$15                 ;check for mushroom retainer/princess object
                                                    //> bne CheckForSecondFrame  ;which uses different code here, branch if not found
                                                    if (A - 0x15 == 0) {
                                                        //> iny                      ;residual instruction
                                                        //> lda WorldNumber          ;are we on world 8?
                                                        //> cmp #World8
                                                        //> bcs CheckDefeatedState   ;if so, leave the offset alone (use princess)
                                                        if (!(WorldNumber >= World8)) {
                                                            //> ldx #$a2                 ;otherwise, set for mushroom retainer object instead
                                                            //> lda #$03                 ;set alternate state here
                                                            //> sta $ec
                                                            zp_ec = 0x03
                                                            //> bne CheckDefeatedState   ;unconditional branch
                                                            if (zeroFlag) {
                                                                //> CheckForSecondFrame:
                                                                //> lda FrameCounter            ;load frame counter
                                                                //> and EnemyAnimTimingBMask,y  ;mask it (partly residual, one byte not ever used)
                                                                //> bne CheckDefeatedState      ;branch if timing is off
                                                                if (zeroFlag) {
                                                                    //> CheckAnimationStop:
                                                                    //> lda $ed                 ;check saved enemy state
                                                                    //> and #%10100000          ;for d7 or d5, or check for timers stopped
                                                                    //> ora TimerControl
                                                                    //> bne CheckDefeatedState  ;if either condition true, branch
                                                                    if (zeroFlag) {
                                                                        //> txa
                                                                        //> clc
                                                                        //> adc #$06                ;add $06 to current enemy offset
                                                                        //> tax                     ;to animate various enemy objects
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if (zeroFlag) {
                                                        if (zeroFlag) {
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (!(zp_ef - Goomba == 0)) {
                                    if (!(A - 0x08 == 0)) {
                                        if (!(A - Podoboo == 0)) {
                                            if (!(A >= 0x18)) {
                                                if (A - 0x15 == 0) {
                                                    if (!(WorldNumber >= World8)) {
                                                        if (zeroFlag) {
                                                            if (zeroFlag) {
                                                                if (zeroFlag) {
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                if (zeroFlag) {
                                                    if (zeroFlag) {
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (zeroFlag) {
                            }
                        }
                    }
                } else {
                    if (!(zp_ef - Goomba == 0)) {
                        if (!(A - 0x08 == 0)) {
                            if (!(A - Podoboo == 0)) {
                                if (!(A >= 0x18)) {
                                    if (A - 0x15 == 0) {
                                        if (!(WorldNumber >= World8)) {
                                            if (zeroFlag) {
                                                if (zeroFlag) {
                                                    if (zeroFlag) {
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (zeroFlag) {
                                        if (zeroFlag) {
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (zeroFlag) {
                }
            }
        }
    }
    if (!(X == 0x48)) {
        if (!(EnemyIntervalTimer[Y] >= 0x05)) {
            if (X == 0x3C) {
                if (!(A - 0x01 == 0)) {
                    if (!(zp_ef - Goomba == 0)) {
                        if (!(A - 0x08 == 0)) {
                            if (!(A - Podoboo == 0)) {
                                if (!(A >= 0x18)) {
                                    if (A - 0x15 == 0) {
                                        if (!(WorldNumber >= World8)) {
                                            if (zeroFlag) {
                                                if (zeroFlag) {
                                                    if (zeroFlag) {
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (zeroFlag) {
                                        if (zeroFlag) {
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                if (!(zp_ef - Goomba == 0)) {
                    if (!(A - 0x08 == 0)) {
                        if (!(A - Podoboo == 0)) {
                            if (!(A >= 0x18)) {
                                if (A - 0x15 == 0) {
                                    if (!(WorldNumber >= World8)) {
                                        if (zeroFlag) {
                                            if (zeroFlag) {
                                                if (zeroFlag) {
                                                }
                                            }
                                        }
                                    }
                                }
                                if (zeroFlag) {
                                    if (zeroFlag) {
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (zeroFlag) {
            }
        }
    } else {
        if (!(zp_ef - Goomba == 0)) {
            if (!(A - 0x08 == 0)) {
                if (!(A - Podoboo == 0)) {
                    if (!(A >= 0x18)) {
                        if (A - 0x15 == 0) {
                            if (!(WorldNumber >= World8)) {
                                if (zeroFlag) {
                                    if (zeroFlag) {
                                        if (zeroFlag) {
                                        }
                                    }
                                }
                            }
                        }
                        if (zeroFlag) {
                            if (zeroFlag) {
                            }
                        }
                    }
                }
            }
        }
    }
    if (zeroFlag) {
    }
    //> CheckDefeatedState:
    //> lda $ed               ;check saved enemy state
    //> and #%00100000        ;for d5 set
    //> beq DrawEnemyObject   ;branch if not set
    if (!zeroFlag) {
        //> lda $ef
        //> cmp #$04              ;check for saved enemy object => $04
        //> bcc DrawEnemyObject   ;branch if less
        if (zp_ef >= 0x04) {
            //> ldy #$01
            //> sty VerticalFlipFlag  ;set vertical flip flag
            VerticalFlipFlag = 0x01
            //> dey
            //> sty $ec               ;init saved value here
            zp_ec = (0x01 - 1) and 0xFF
        }
    }
    //> DrawEnemyObject:
    //> ldy $eb                    ;load sprite data offset
    //> jsr DrawEnemyObjRow        ;draw six tiles of data
    drawEnemyObjRow((0xA2 + 0x06 + 0) and 0xFF)
    //> jsr DrawEnemyObjRow        ;into sprite data
    drawEnemyObjRow((0xA2 + 0x06 + 0) and 0xFF)
    //> jsr DrawEnemyObjRow
    drawEnemyObjRow((0xA2 + 0x06 + 0) and 0xFF)
    //> ldx ObjectOffset           ;get enemy object offset
    //> ldy Enemy_SprDataOffset,x  ;get sprite data offset
    //> lda $ef
    //> cmp #$08                   ;get saved enemy object and check
    //> bne CheckForVerticalFlip   ;for bullet bill, branch if not found
    if (zp_ef - 0x08 == 0) {
    } else {
        //> CheckForVerticalFlip:
        //> lda VerticalFlipFlag       ;check if vertical flip flag is set here
        //> beq CheckForESymmetry      ;branch if not
        if (!zeroFlag) {
            //> lda Sprite_Attributes,y    ;get attributes of first sprite we dealt with
            //> ora #%10000000             ;set bit for vertical flip
            //> iny
            //> iny                        ;increment two bytes so that we store the vertical flip
            //> jsr DumpSixSpr             ;in attribute bytes of enemy obj sprite data
            dumpSixSpr(Sprite_Attributes[Enemy_SprDataOffset[ObjectOffset]] or 0x80, ((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF)
            //> dey
            //> dey                        ;now go back to the Y coordinate offset
            //> tya
            //> tax                        ;give offset to X
            //> lda $ef
            //> cmp #HammerBro             ;check saved enemy object for hammer bro
            //> beq FlipEnemyVertically
            if (!(zp_ef - HammerBro == 0)) {
                //> cmp #Lakitu                ;check saved enemy object for lakitu
                //> beq FlipEnemyVertically    ;branch for hammer bro or lakitu
                if (!(A - Lakitu == 0)) {
                    //> cmp #$15
                    //> bcs FlipEnemyVertically    ;also branch if enemy object => $15
                    if (!(A >= 0x15)) {
                        //> txa
                        //> clc
                        //> adc #$08                   ;if not selected objects or => $15, set
                        //> tax                        ;offset in X for next row
                    }
                }
            }
            //> FlipEnemyVertically:
            //> lda Sprite_Tilenumber,x     ;load first or second row tiles
            //> pha                         ;and save tiles to the stack
            //> lda Sprite_Tilenumber+4,x
            //> pha
            //> lda Sprite_Tilenumber+16,y  ;exchange third row tiles
            //> sta Sprite_Tilenumber,x     ;with first or second row tiles
            Sprite_Tilenumber[(((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF + 0x08 + 0) and 0xFF] = Sprite_Tilenumber[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF]
            //> lda Sprite_Tilenumber+20,y
            //> sta Sprite_Tilenumber+4,x
            Sprite_Tilenumber[(((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF + 0x08 + 0) and 0xFF] = Sprite_Tilenumber[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF]
            //> pla                         ;pull first or second row tiles from stack
            //> sta Sprite_Tilenumber+20,y  ;and save in third row
            Sprite_Tilenumber[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = Sprite_Tilenumber[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF]
            //> pla
            //> sta Sprite_Tilenumber+16,y
            Sprite_Tilenumber[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = Sprite_Tilenumber[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF]
        }
        do {
            //> SkipToOffScrChk:
            //> jmp SprObjectOffscrChk     ;jump if found
            //> CheckForESymmetry:
            //> lda BowserGfxFlag           ;are we drawing bowser at all?
            //> bne SkipToOffScrChk         ;branch if so
        } while (!zeroFlag)
        //> lda $ef
        //> ldx $ec                     ;get alternate enemy state
        //> cmp #$05                    ;check for hammer bro object
        //> bne ContES
        if (zp_ef - 0x05 == 0) {
        } else {
            //> ContES: cmp #Bloober                ;check for bloober object
            //> beq MirrorEnemyGfx
            if (!(A - Bloober == 0)) {
                //> cmp #PiranhaPlant           ;check for piranha plant object
                //> beq MirrorEnemyGfx
                if (!(A - PiranhaPlant == 0)) {
                    //> cmp #Podoboo                ;check for podoboo object
                    //> beq MirrorEnemyGfx          ;branch if either of three are found
                    if (!(A - Podoboo == 0)) {
                        //> cmp #Spiny                  ;check for spiny object
                        //> bne ESRtnr                  ;branch closer if not found
                        if (A - Spiny == 0) {
                            //> cpx #$05                    ;check spiny's state
                            //> bne CheckToMirrorLakitu     ;branch if not an egg, otherwise
                            if (X == 0x05) {
                                //> ESRtnr: cmp #$15                    ;check for princess/mushroom retainer object
                                //> bne SpnySC
                                if (A - 0x15 == 0) {
                                    //> lda #$42                    ;set horizontal flip on bottom right sprite
                                    //> sta Sprite_Attributes+20,y  ;note that palette bits were already set earlier
                                    Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = 0x42
                                }
                                //> SpnySC: cpx #$02                    ;if alternate enemy state set to 1 or 0, branch
                                //> bcc CheckToMirrorLakitu
                                if (X >= 0x02) {
                                    //> MirrorEnemyGfx:
                                    //> lda BowserGfxFlag           ;if enemy object is bowser, skip all of this
                                    //> bne CheckToMirrorLakitu
                                    if (zeroFlag) {
                                        //> lda Sprite_Attributes,y     ;load attribute bits of first sprite
                                        //> and #%10100011
                                        //> sta Sprite_Attributes,y     ;save vertical flip, priority, and palette bits
                                        Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] and 0xA3
                                        //> sta Sprite_Attributes+8,y   ;in left sprite column of enemy object OAM data
                                        Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] and 0xA3
                                        //> sta Sprite_Attributes+16,y
                                        Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] and 0xA3
                                        //> ora #%01000000              ;set horizontal flip
                                        //> cpx #$05                    ;check for state used by spiny's egg
                                        //> bne EggExc                  ;if alternate state not set to $05, branch
                                        if (X == 0x05) {
                                            //> ora #%10000000              ;otherwise set vertical flip
                                        }
                                        //> EggExc: sta Sprite_Attributes+4,y   ;set bits of right sprite column
                                        Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] and 0xA3 or 0x40 or 0x80
                                        //> sta Sprite_Attributes+12,y  ;of enemy object sprite data
                                        Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] and 0xA3 or 0x40 or 0x80
                                        //> sta Sprite_Attributes+20,y
                                        Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] and 0xA3 or 0x40 or 0x80
                                        //> cpx #$04                    ;check alternate enemy state
                                        //> bne CheckToMirrorLakitu     ;branch if not $04
                                        if (X == 0x04) {
                                            //> lda Sprite_Attributes+8,y   ;get second row left sprite attributes
                                            //> ora #%10000000
                                            //> sta Sprite_Attributes+8,y   ;store bits with vertical flip in
                                            Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] or 0x80
                                            //> sta Sprite_Attributes+16,y  ;second and third row left sprites
                                            Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] or 0x80
                                            //> ora #%01000000
                                            //> sta Sprite_Attributes+12,y  ;store with horizontal and vertical flip in
                                            Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] or 0x80 or 0x40
                                            //> sta Sprite_Attributes+20,y  ;second and third row right sprites
                                            Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] or 0x80 or 0x40
                                        }
                                    }
                                }
                            }
                        }
                        if (A - 0x15 == 0) {
                        }
                        if (X >= 0x02) {
                            if (zeroFlag) {
                                if (X == 0x05) {
                                }
                                if (X == 0x04) {
                                }
                            }
                        }
                    }
                }
            }
            if (zeroFlag) {
                if (X == 0x05) {
                }
                if (X == 0x04) {
                }
            }
            //> CheckToMirrorLakitu:
            //> lda $ef                     ;check for lakitu enemy object
            //> cmp #Lakitu
            //> bne CheckToMirrorJSpring    ;branch if not found
            if (zp_ef - Lakitu == 0) {
                //> lda VerticalFlipFlag
                //> bne NVFLak                  ;branch if vertical flip flag not set
                if (zeroFlag) {
                    //> lda Sprite_Attributes+16,y  ;save vertical flip and palette bits
                    //> and #%10000001              ;in third row left sprite
                    //> sta Sprite_Attributes+16,y
                    Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] and 0x81
                    //> lda Sprite_Attributes+20,y  ;set horizontal flip and palette bits
                    //> ora #%01000001              ;in third row right sprite
                    //> sta Sprite_Attributes+20,y
                    Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] or 0x41
                    //> ldx FrenzyEnemyTimer        ;check timer
                    //> cpx #$10
                    //> bcs SprObjectOffscrChk      ;branch if timer has not reached a certain range
                    if (!(FrenzyEnemyTimer >= 0x10)) {
                        //> sta Sprite_Attributes+12,y  ;otherwise set same for second row right sprite
                        Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] or 0x41
                        //> and #%10000001
                        //> sta Sprite_Attributes+8,y   ;preserve vertical flip and palette bits for left sprite
                        Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] or 0x41 and 0x81
                        //> bcc SprObjectOffscrChk      ;unconditional branch
                        if (carryFlag) {
                            //> NVFLak: lda Sprite_Attributes,y     ;get first row left sprite attributes
                            //> and #%10000001
                            //> sta Sprite_Attributes,y     ;save vertical flip and palette bits
                            Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] and 0x81
                            //> lda Sprite_Attributes+4,y   ;get first row right sprite attributes
                            //> ora #%01000001              ;set horizontal flip and palette bits
                            //> sta Sprite_Attributes+4,y   ;note that vertical flip is left as-is
                            Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] or 0x41
                            //> CheckToMirrorJSpring:
                            //> lda $ef                     ;check for jumpspring object (any frame)
                            //> cmp #$18
                            //> bcc SprObjectOffscrChk      ;branch if not jumpspring object at all
                            if (zp_ef >= 0x18) {
                                //> lda #$82
                                //> sta Sprite_Attributes+8,y   ;set vertical flip and palette bits of
                                Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = 0x82
                                //> sta Sprite_Attributes+16,y  ;second and third row left sprites
                                Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = 0x82
                                //> ora #%01000000
                                //> sta Sprite_Attributes+12,y  ;set, in addition to those, horizontal flip
                                Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = 0x82 or 0x40
                                //> sta Sprite_Attributes+20,y  ;for second and third row right sprites
                                Sprite_Attributes[((((Enemy_SprDataOffset[ObjectOffset] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = 0x82 or 0x40
                            }
                        }
                    }
                }
            }
            if (zp_ef >= 0x18) {
            }
        }
    }
    //> SprObjectOffscrChk:
    //> ldx ObjectOffset          ;get enemy buffer offset
    //> lda Enemy_OffscreenBits   ;check offscreen information
    //> lsr
    //> lsr                       ;shift three times to the right
    //> lsr                       ;which puts d2 into carry
    //> pha                       ;save to stack
    //> bcc LcChk                 ;branch if not set
    if (carryFlag) {
        //> lda #$04                  ;set for right column sprites
        //> jsr MoveESprColOffscreen  ;and move them offscreen
        moveESprColOffscreen(0x04, ObjectOffset)
    }
    //> LcChk:   pla                       ;get from stack
    //> lsr                       ;move d3 to carry
    //> pha                       ;save to stack
    //> bcc Row3C                 ;branch if not set
    if (carryFlag) {
        //> lda #$00                  ;set for left column sprites,
        //> jsr MoveESprColOffscreen  ;move them offscreen
        moveESprColOffscreen(0x00, ObjectOffset)
    }
    //> Row3C:   pla                       ;get from stack again
    //> lsr                       ;move d5 to carry this time
    //> lsr
    //> pha                       ;save to stack again
    //> bcc Row23C                ;branch if carry not set
    if (carryFlag) {
        //> lda #$10                  ;set for third row of sprites
        //> jsr MoveESprRowOffscreen  ;and move them offscreen
        moveESprRowOffscreen(0x10, ObjectOffset)
    }
    //> Row23C:  pla                       ;get from stack
    //> lsr                       ;move d6 into carry
    //> pha                       ;save to stack
    //> bcc AllRowC
    if (carryFlag) {
        //> lda #$08                  ;set for second and third rows
        //> jsr MoveESprRowOffscreen  ;move them offscreen
        moveESprRowOffscreen(0x08, ObjectOffset)
    }
    //> AllRowC: pla                       ;get from stack once more
    //> lsr                       ;move d7 into carry
    //> bcc ExEGHandler
    if (carryFlag) {
        //> jsr MoveESprRowOffscreen  ;move all sprites offscreen (A should be 0 by now)
        moveESprRowOffscreen(0x08 shr 1, ObjectOffset)
        //> lda Enemy_ID,x
        //> cmp #Podoboo              ;check enemy identifier for podoboo
        //> beq ExEGHandler           ;skip this part if found, we do not want to erase podoboo!
        if (!(Enemy_ID[X] - Podoboo == 0)) {
            //> lda Enemy_Y_HighPos,x     ;check high byte of vertical position
            //> cmp #$02                  ;if not yet past the bottom of the screen, branch
            //> bne ExEGHandler
            if (Enemy_Y_HighPos[X] - 0x02 == 0) {
                //> jsr EraseEnemyObject      ;what it says
                eraseEnemyObject(ObjectOffset)
            }
        }
    }
    //> ExEGHandler:
    //> rts
    return
}

// Decompiled from DrawEnemyObjRow
fun drawEnemyObjRow(X: Int) {
    //> DrawEnemyObjRow:
    //> lda EnemyGraphicsTable,x    ;load two tiles of enemy graphics
    //> sta $00
    zp_00 = EnemyGraphicsTable[X]
    //> lda EnemyGraphicsTable+1,x
}

// Decompiled from DrawOneSpriteRow
fun drawOneSpriteRow(A: Int, X: Int, Y: Int) {
    //> DrawOneSpriteRow:
    //> sta $01
    zp_01 = A
    //> jmp DrawSpriteObject        ;draw them
    //> DrawSpriteObject:
    //> lda $03                    ;get saved flip control bits
    //> lsr
    //> lsr                        ;move d1 into carry
    //> lda $00
    //> bcc NoHFlip                ;if d1 not set, branch
    if (carryFlag) {
        //> sta Sprite_Tilenumber+4,y  ;store first tile into second sprite
        Sprite_Tilenumber[Y] = zp_00
        //> lda $01                    ;and second into first sprite
        //> sta Sprite_Tilenumber,y
        Sprite_Tilenumber[Y] = zp_01
        //> lda #$40                   ;activate horizontal flip OAM attribute
        //> bne SetHFAt                ;and unconditionally branch
        if (zeroFlag) {
            //> NoHFlip: sta Sprite_Tilenumber,y    ;store first tile into first sprite
            Sprite_Tilenumber[Y] = 0x40
            //> lda $01                    ;and second into second sprite
            //> sta Sprite_Tilenumber+4,y
            Sprite_Tilenumber[Y] = zp_01
            //> lda #$00                   ;clear bit for horizontal flip
        }
    }
    //> SetHFAt: ora $04                    ;add other OAM attributes if necessary
    //> sta Sprite_Attributes,y    ;store sprite attributes
    Sprite_Attributes[Y] = 0x00 or zp_04
    //> sta Sprite_Attributes+4,y
    Sprite_Attributes[Y] = 0x00 or zp_04
    //> lda $02                    ;now the y coordinates
    //> sta Sprite_Y_Position,y    ;note because they are
    Sprite_Y_Position[Y] = zp_02
    //> sta Sprite_Y_Position+4,y  ;side by side, they are the same
    Sprite_Y_Position[Y] = zp_02
    //> lda $05
    //> sta Sprite_X_Position,y    ;store x coordinate, then
    Sprite_X_Position[Y] = zp_05
    //> clc                        ;add 8 pixels and store another to
    //> adc #$08                   ;put them side by side
    //> sta Sprite_X_Position+4,y
    Sprite_X_Position[Y] = (zp_05 + 0x08 + 0) and 0xFF
    //> lda $02                    ;add eight pixels to the next y
    //> clc                        ;coordinate
    //> adc #$08
    //> sta $02
    zp_02 = (zp_02 + 0x08 + 0) and 0xFF
    //> tya                        ;add eight to the offset in Y to
    //> clc                        ;move to the next two sprites
    //> adc #$08
    //> tay
    //> inx                        ;increment offset to return it to the
    //> inx                        ;routine that called this subroutine
    //> rts
    return
}

// Decompiled from MoveESprRowOffscreen
fun moveESprRowOffscreen(A: Int, X: Int) {
    //> MoveESprRowOffscreen:
    //> clc                         ;add A to enemy object OAM data offset
    //> adc Enemy_SprDataOffset,x
    //> tay                         ;use as offset
    //> lda #$f8
    //> jmp DumpTwoSpr              ;move first row of sprites offscreen
}

// Decompiled from MoveESprColOffscreen
fun moveESprColOffscreen(A: Int, X: Int) {
    //> MoveESprColOffscreen:
    //> clc                         ;add A to enemy object OAM data offset
    //> adc Enemy_SprDataOffset,x
    //> tay                         ;use as offset
    //> jsr MoveColOffscreen        ;move first and second row sprites in column offscreen
    moveColOffscreen((A + Enemy_SprDataOffset[X] + 0) and 0xFF)
    //> sta Sprite_Data+16,y        ;move third row sprite in column offscreen
    Sprite_Data[(A + Enemy_SprDataOffset[X] + 0) and 0xFF] = (A + Enemy_SprDataOffset[X] + 0) and 0xFF
    //> rts
    return
}

// Decompiled from DrawBlock
fun drawBlock(X: Int) {
    //> DrawBlock:
    //> lda Block_Rel_YPos            ;get relative vertical coordinate of block object
    //> sta $02                       ;store here
    zp_02 = Block_Rel_YPos
    //> lda Block_Rel_XPos            ;get relative horizontal coordinate of block object
    //> sta $05                       ;store here
    zp_05 = Block_Rel_XPos
    //> lda #$03
    //> sta $04                       ;set attribute byte here
    zp_04 = 0x03
    //> lsr
    //> sta $03                       ;set horizontal flip bit here (will not be used)
    zp_03 = 0x03 shr 1
    //> ldy Block_SprDataOffset,x     ;get sprite data offset
    //> ldx #$00                      ;reset X for use as offset to tile data
    do {
        //> DBlkLoop:  lda DefaultBlockObjTiles,x    ;get left tile number
        //> sta $00                       ;set here
        zp_00 = DefaultBlockObjTiles[0x00]
        //> lda DefaultBlockObjTiles+1,x  ;get right tile number
        //> jsr DrawOneSpriteRow          ;do sub to write tile numbers to first row of sprites
        drawOneSpriteRow(DefaultBlockObjTiles[0x00], 0x00, Block_SprDataOffset[X])
        //> cpx #$04                      ;check incremented offset
        //> bne DBlkLoop                  ;and loop back until all four sprites are done
    } while (X == 0x04)
    //> ldx ObjectOffset              ;get block object offset
    //> ldy Block_SprDataOffset,x     ;get sprite data offset
    //> lda AreaType
    //> cmp #$01                      ;check for ground level type area
    //> beq ChkRep                    ;if found, branch to next part
    if (!(AreaType - 0x01 == 0)) {
        //> lda #$86
        //> sta Sprite_Tilenumber,y       ;otherwise remove brick tiles with lines
        Sprite_Tilenumber[Block_SprDataOffset[ObjectOffset]] = 0x86
        //> sta Sprite_Tilenumber+4,y     ;and replace then with lineless brick tiles
        Sprite_Tilenumber[Block_SprDataOffset[ObjectOffset]] = 0x86
    }
    //> ChkRep:    lda Block_Metatile,x          ;check replacement metatile
    //> cmp #$c4                      ;if not used block metatile, then
    //> bne BlkOffscr                 ;branch ahead to use current graphics
    if (Block_Metatile[X] - 0xC4 == 0) {
        //> lda #$87                      ;set A for used block tile
        //> iny                           ;increment Y to write to tile bytes
        //> jsr DumpFourSpr               ;do sub to dump into all four sprites
        dumpFourSpr(0x87, (Block_SprDataOffset[ObjectOffset] + 1) and 0xFF)
        //> dey                           ;return Y to original offset
        //> lda #$03                      ;set palette bits
        //> ldx AreaType
        //> dex                           ;check for ground level type area again
        //> beq SetBFlip                  ;if found, use current palette bits
        if (!zeroFlag) {
            //> lsr                           ;otherwise set to $01
        }
        //> SetBFlip:  ldx ObjectOffset              ;put block object offset back in X
        //> sta Sprite_Attributes,y       ;store attribute byte as-is in first sprite
        Sprite_Attributes[((Block_SprDataOffset[ObjectOffset] + 1) and 0xFF - 1) and 0xFF] = 0x03 shr 1
        //> ora #%01000000
        //> sta Sprite_Attributes+4,y     ;set horizontal flip bit for second sprite
        Sprite_Attributes[((Block_SprDataOffset[ObjectOffset] + 1) and 0xFF - 1) and 0xFF] = 0x03 shr 1 or 0x40
        //> ora #%10000000
        //> sta Sprite_Attributes+12,y    ;set both flip bits for fourth sprite
        Sprite_Attributes[((Block_SprDataOffset[ObjectOffset] + 1) and 0xFF - 1) and 0xFF] = 0x03 shr 1 or 0x40 or 0x80
        //> and #%10000011
        //> sta Sprite_Attributes+8,y     ;set vertical flip bit for third sprite
        Sprite_Attributes[((Block_SprDataOffset[ObjectOffset] + 1) and 0xFF - 1) and 0xFF] = 0x03 shr 1 or 0x40 or 0x80 and 0x83
    }
    //> BlkOffscr: lda Block_OffscreenBits       ;get offscreen bits for block object
    //> pha                           ;save to stack
    //> and #%00000100                ;check to see if d2 in offscreen bits are set
    //> beq PullOfsB                  ;if not set, branch, otherwise move sprites offscreen
    if (!zeroFlag) {
        //> lda #$f8                      ;move offscreen two OAMs
        //> sta Sprite_Y_Position+4,y     ;on the right side
        Sprite_Y_Position[((Block_SprDataOffset[ObjectOffset] + 1) and 0xFF - 1) and 0xFF] = 0xF8
        //> sta Sprite_Y_Position+12,y
        Sprite_Y_Position[((Block_SprDataOffset[ObjectOffset] + 1) and 0xFF - 1) and 0xFF] = 0xF8
    }
    //> PullOfsB:  pla                           ;pull offscreen bits from stack
}

// Decompiled from ChkLeftCo
fun chkLeftCo(A: Int) {
    //> ChkLeftCo: and #%00001000                ;check to see if d3 in offscreen bits are set
    //> beq ExDBlk                    ;if not set, branch, otherwise move sprites offscreen
    if (!zeroFlag) {
    }
    //> ExDBlk: rts
    return
}

// Decompiled from MoveColOffscreen
fun moveColOffscreen(Y: Int): Int {
    //> MoveColOffscreen:
    //> lda #$f8                   ;move offscreen two OAMs
    //> sta Sprite_Y_Position,y    ;on the left side (or two rows of enemy on either side
    Sprite_Y_Position[Y] = 0xF8
    //> sta Sprite_Y_Position+8,y  ;if branched here from enemy graphics handler)
    Sprite_Y_Position[Y] = 0xF8
    //> ExDBlk: rts
    return A
}

// Decompiled from DrawBrickChunks
fun drawBrickChunks(X: Int) {
    var Y: Int = 0
    //> DrawBrickChunks:
    //> lda #$02                   ;set palette bits here
    //> sta $00
    zp_00 = 0x02
    //> lda #$75                   ;set tile number for ball (something residual, likely)
    //> ldy GameEngineSubroutine
    //> cpy #$05                   ;if end-of-level routine running,
    //> beq DChunks                ;use palette and tile number assigned
    if (!(GameEngineSubroutine == 0x05)) {
        //> lda #$03                   ;otherwise set different palette bits
        //> sta $00
        zp_00 = 0x03
        //> lda #$84                   ;and set tile number for brick chunks
    }
    //> DChunks: ldy Block_SprDataOffset,x  ;get OAM data offset
    //> iny                        ;increment to start with tile bytes in OAM
    //> jsr DumpFourSpr            ;do sub to dump tile number into all four sprites
    dumpFourSpr(0x84, (Block_SprDataOffset[X] + 1) and 0xFF)
    //> lda FrameCounter           ;get frame counter
    //> asl
    //> asl
    //> asl                        ;move low nybble to high
    //> asl
    //> and #$c0                   ;get what was originally d3-d2 of low nybble
    //> ora $00                    ;add palette bits
    //> iny                        ;increment offset for attribute bytes
    //> jsr DumpFourSpr            ;do sub to dump attribute data into all four sprites
    dumpFourSpr(((((FrameCounter shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF and 0xC0 or zp_00, ((Block_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF)
    //> dey
    //> dey                        ;decrement offset to Y coordinate
    //> lda Block_Rel_YPos         ;get first block object's relative vertical coordinate
    //> jsr DumpTwoSpr             ;do sub to dump current Y coordinate into two sprites
    dumpTwoSpr(Block_Rel_YPos, ((((Block_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF)
    //> lda Block_Rel_XPos         ;get first block object's relative horizontal coordinate
    //> sta Sprite_X_Position,y    ;save into X coordinate of first sprite
    Sprite_X_Position[((((Block_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = Block_Rel_XPos
    //> lda Block_Orig_XPos,x      ;get original horizontal coordinate
    //> sec
    //> sbc ScreenLeft_X_Pos       ;subtract coordinate of left side from original coordinate
    //> sta $00                    ;store result as relative horizontal coordinate of original
    zp_00 = (Block_Orig_XPos[X] - ScreenLeft_X_Pos - (1 - 1)) and 0xFF
    //> sec
    //> sbc Block_Rel_XPos         ;get difference of relative positions of original - current
    //> adc $00                    ;add original relative position to result
    //> adc #$06                   ;plus 6 pixels to position second brick chunk correctly
    //> sta Sprite_X_Position+4,y  ;save into X coordinate of second sprite
    Sprite_X_Position[((((Block_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = ((((Block_Orig_XPos[X] - ScreenLeft_X_Pos - (1 - 1)) and 0xFF - Block_Rel_XPos - (1 - 1)) and 0xFF + zp_00 + 1) and 0xFF + 0x06 + 1) and 0xFF
    //> lda Block_Rel_YPos+1       ;get second block object's relative vertical coordinate
    //> sta Sprite_Y_Position+8,y
    Sprite_Y_Position[((((Block_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = Block_Rel_YPos
    //> sta Sprite_Y_Position+12,y ;dump into Y coordinates of third and fourth sprites
    Sprite_Y_Position[((((Block_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = Block_Rel_YPos
    //> lda Block_Rel_XPos+1       ;get second block object's relative horizontal coordinate
    //> sta Sprite_X_Position+8,y  ;save into X coordinate of third sprite
    Sprite_X_Position[((((Block_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = Block_Rel_XPos
    //> lda $00                    ;use original relative horizontal position
    //> sec
    //> sbc Block_Rel_XPos+1       ;get difference of relative positions of original - current
    //> adc $00                    ;add original relative position to result
    //> adc #$06                   ;plus 6 pixels to position fourth brick chunk correctly
    //> sta Sprite_X_Position+12,y ;save into X coordinate of fourth sprite
    Sprite_X_Position[((((Block_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = (((zp_00 - Block_Rel_XPos - (1 - 1)) and 0xFF + zp_00 + 1) and 0xFF + 0x06 + 1) and 0xFF
    //> lda Block_OffscreenBits    ;get offscreen bits for block object
    //> jsr ChkLeftCo              ;do sub to move left half of sprites offscreen if necessary
    chkLeftCo(Block_OffscreenBits)
    //> lda Block_OffscreenBits    ;get offscreen bits again
    //> asl                        ;shift d7 into carry
    //> bcc ChnkOfs                ;if d7 not set, branch to last part
    if (1) {
        //> lda #$f8
        //> jsr DumpTwoSpr             ;otherwise move top sprites offscreen
        dumpTwoSpr(0xF8, ((((Block_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF)
    }
    //> ChnkOfs: lda $00                    ;if relative position on left side of screen,
    //> bpl ExBCDr                 ;go ahead and leave
    if (negativeFlag) {
        //> lda Sprite_X_Position,y    ;otherwise compare left-side X coordinate
        //> cmp Sprite_X_Position+4,y  ;to right-side X coordinate
        //> bcc ExBCDr                 ;branch to leave if less
        if (Sprite_X_Position[Y] >= Sprite_X_Position[Y]) {
            //> lda #$f8                   ;otherwise move right half of sprites offscreen
            //> sta Sprite_Y_Position+4,y
            Sprite_Y_Position[((((Block_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = 0xF8
            //> sta Sprite_Y_Position+12,y
            Sprite_Y_Position[((((Block_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = 0xF8
        }
    }
    //> ExBCDr:  rts                        ;leave
    return
}

// Decompiled from DrawFirebar
fun drawFirebar(Y: Int) {
    //> DrawFirebar:
    //> lda FrameCounter         ;get frame counter
    //> lsr                      ;divide by four
    //> lsr
    //> pha                      ;save result to stack
    //> and #$01                 ;mask out all but last bit
    //> eor #$64                 ;set either tile $64 or $65 as fireball tile
    //> sta Sprite_Tilenumber,y  ;thus tile changes every four frames
    Sprite_Tilenumber[Y] = FrameCounter shr 1 shr 1 and 0x01 xor 0x64
    //> pla                      ;get from stack
    //> lsr                      ;divide by four again
    //> lsr
    //> lda #$02                 ;load value $02 to set palette in attrib byte
    //> bcc FireA                ;if last bit shifted out was not set, skip this
    if (carryFlag) {
        //> ora #%11000000           ;otherwise flip both ways every eight frames
    }
    //> FireA: sta Sprite_Attributes,y  ;store attribute byte and leave
    Sprite_Attributes[Y] = 0x02 or 0xC0
    //> rts
    return
}

// Decompiled from DrawExplosion_Fireworks
fun drawexplosionFireworks(A: Int, Y: Int) {
    //> DrawExplosion_Fireworks:
    //> tax                         ;use whatever's in A for offset
    //> lda ExplosionTiles,x        ;get tile number using offset
    //> iny                         ;increment Y (contains sprite data offset)
    //> jsr DumpFourSpr             ;and dump into tile number part of sprite data
    dumpFourSpr(ExplosionTiles[A], (Y + 1) and 0xFF)
    //> dey                         ;decrement Y so we have the proper offset again
    //> ldx ObjectOffset            ;return enemy object buffer offset to X
    //> lda Fireball_Rel_YPos       ;get relative vertical coordinate
    //> sec                         ;subtract four pixels vertically
    //> sbc #$04                    ;for first and third sprites
    //> sta Sprite_Y_Position,y
    Sprite_Y_Position[((Y + 1) and 0xFF - 1) and 0xFF] = (Fireball_Rel_YPos - 0x04 - (1 - 1)) and 0xFF
    //> sta Sprite_Y_Position+8,y
    Sprite_Y_Position[((Y + 1) and 0xFF - 1) and 0xFF] = (Fireball_Rel_YPos - 0x04 - (1 - 1)) and 0xFF
    //> clc                         ;add eight pixels vertically
    //> adc #$08                    ;for second and fourth sprites
    //> sta Sprite_Y_Position+4,y
    Sprite_Y_Position[((Y + 1) and 0xFF - 1) and 0xFF] = ((Fireball_Rel_YPos - 0x04 - (1 - 1)) and 0xFF + 0x08 + 0) and 0xFF
    //> sta Sprite_Y_Position+12,y
    Sprite_Y_Position[((Y + 1) and 0xFF - 1) and 0xFF] = ((Fireball_Rel_YPos - 0x04 - (1 - 1)) and 0xFF + 0x08 + 0) and 0xFF
    //> lda Fireball_Rel_XPos       ;get relative horizontal coordinate
    //> sec                         ;subtract four pixels horizontally
    //> sbc #$04                    ;for first and second sprites
    //> sta Sprite_X_Position,y
    Sprite_X_Position[((Y + 1) and 0xFF - 1) and 0xFF] = (Fireball_Rel_XPos - 0x04 - (1 - 1)) and 0xFF
    //> sta Sprite_X_Position+4,y
    Sprite_X_Position[((Y + 1) and 0xFF - 1) and 0xFF] = (Fireball_Rel_XPos - 0x04 - (1 - 1)) and 0xFF
    //> clc                         ;add eight pixels horizontally
    //> adc #$08                    ;for third and fourth sprites
    //> sta Sprite_X_Position+8,y
    Sprite_X_Position[((Y + 1) and 0xFF - 1) and 0xFF] = ((Fireball_Rel_XPos - 0x04 - (1 - 1)) and 0xFF + 0x08 + 0) and 0xFF
    //> sta Sprite_X_Position+12,y
    Sprite_X_Position[((Y + 1) and 0xFF - 1) and 0xFF] = ((Fireball_Rel_XPos - 0x04 - (1 - 1)) and 0xFF + 0x08 + 0) and 0xFF
    //> lda #$02                    ;set palette attributes for all sprites, but
    //> sta Sprite_Attributes,y     ;set no flip at all for first sprite
    Sprite_Attributes[((Y + 1) and 0xFF - 1) and 0xFF] = 0x02
    //> lda #$82
    //> sta Sprite_Attributes+4,y   ;set vertical flip for second sprite
    Sprite_Attributes[((Y + 1) and 0xFF - 1) and 0xFF] = 0x82
    //> lda #$42
    //> sta Sprite_Attributes+8,y   ;set horizontal flip for third sprite
    Sprite_Attributes[((Y + 1) and 0xFF - 1) and 0xFF] = 0x42
    //> lda #$c2
    //> sta Sprite_Attributes+12,y  ;set both flips for fourth sprite
    Sprite_Attributes[((Y + 1) and 0xFF - 1) and 0xFF] = 0xC2
    //> rts                         ;we are done
    return
}

// Decompiled from DrawSmallPlatform
fun drawSmallPlatform(X: Int) {
    var A: Int = 0
    //> DrawSmallPlatform:
    //> ldy Enemy_SprDataOffset,x   ;get OAM data offset
    //> lda #$5b                    ;load tile number for small platforms
    //> iny                         ;increment offset for tile numbers
    //> jsr DumpSixSpr              ;dump tile number into all six sprites
    dumpSixSpr(0x5B, (Enemy_SprDataOffset[X] + 1) and 0xFF)
    //> iny                         ;increment offset for attributes
    //> lda #$02                    ;load palette controls
    //> jsr DumpSixSpr              ;dump attributes into all six sprites
    dumpSixSpr(0x02, ((Enemy_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF)
    //> dey                         ;decrement for original offset
    //> dey
    //> lda Enemy_Rel_XPos          ;get relative horizontal coordinate
    //> sta Sprite_X_Position,y
    Sprite_X_Position[((((Enemy_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = Enemy_Rel_XPos
    //> sta Sprite_X_Position+12,y  ;dump as X coordinate into first and fourth sprites
    Sprite_X_Position[((((Enemy_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = Enemy_Rel_XPos
    //> clc
    //> adc #$08                    ;add eight pixels
    //> sta Sprite_X_Position+4,y   ;dump into second and fifth sprites
    Sprite_X_Position[((((Enemy_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = (Enemy_Rel_XPos + 0x08 + 0) and 0xFF
    //> sta Sprite_X_Position+16,y
    Sprite_X_Position[((((Enemy_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = (Enemy_Rel_XPos + 0x08 + 0) and 0xFF
    //> clc
    //> adc #$08                    ;add eight more pixels
    //> sta Sprite_X_Position+8,y   ;dump into third and sixth sprites
    Sprite_X_Position[((((Enemy_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = ((Enemy_Rel_XPos + 0x08 + 0) and 0xFF + 0x08 + 0) and 0xFF
    //> sta Sprite_X_Position+20,y
    Sprite_X_Position[((((Enemy_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = ((Enemy_Rel_XPos + 0x08 + 0) and 0xFF + 0x08 + 0) and 0xFF
    //> lda Enemy_Y_Position,x      ;get vertical coordinate
    //> tax
    //> pha                         ;save to stack
    //> cpx #$20                    ;if vertical coordinate below status bar,
    //> bcs TopSP                   ;do not mess with it
    if (!(Enemy_Y_Position[X] >= 0x20)) {
        //> lda #$f8                    ;otherwise move first three sprites offscreen
    }
    //> TopSP: jsr DumpThreeSpr            ;dump vertical coordinate into Y coordinates
    dumpThreeSpr(0xF8, ((((Enemy_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF)
    //> pla                         ;pull from stack
    //> clc
    //> adc #$80                    ;add 128 pixels
    //> tax
    //> cpx #$20                    ;if below status bar (taking wrap into account)
    //> bcs BotSP                   ;then do not change altered coordinate
    if (!((A + 0x80 + 0) and 0xFF >= 0x20)) {
        //> lda #$f8                    ;otherwise move last three sprites offscreen
    }
    //> BotSP: sta Sprite_Y_Position+12,y  ;dump vertical coordinate + 128 pixels
    Sprite_Y_Position[((((Enemy_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = 0xF8
    //> sta Sprite_Y_Position+16,y  ;into Y coordinates
    Sprite_Y_Position[((((Enemy_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = 0xF8
    //> sta Sprite_Y_Position+20,y
    Sprite_Y_Position[((((Enemy_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = 0xF8
    //> lda Enemy_OffscreenBits     ;get offscreen bits
    //> pha                         ;save to stack
    //> and #%00001000              ;check d3
    //> beq SOfs
    if (!zeroFlag) {
        //> lda #$f8                    ;if d3 was set, move first and
        //> sta Sprite_Y_Position,y     ;fourth sprites offscreen
        Sprite_Y_Position[((((Enemy_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = 0xF8
        //> sta Sprite_Y_Position+12,y
        Sprite_Y_Position[((((Enemy_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = 0xF8
    }
    //> SOfs:  pla                         ;move out and back into stack
    //> pha
    //> and #%00000100              ;check d2
    //> beq SOfs2
    if (!zeroFlag) {
        //> lda #$f8                    ;if d2 was set, move second and
        //> sta Sprite_Y_Position+4,y   ;fifth sprites offscreen
        Sprite_Y_Position[((((Enemy_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = 0xF8
        //> sta Sprite_Y_Position+16,y
        Sprite_Y_Position[((((Enemy_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = 0xF8
    }
    //> SOfs2: pla                         ;get from stack
    //> and #%00000010              ;check d1
    //> beq ExSPl
    if (!zeroFlag) {
        //> lda #$f8                    ;if d1 was set, move third and
        //> sta Sprite_Y_Position+8,y   ;sixth sprites offscreen
        Sprite_Y_Position[((((Enemy_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = 0xF8
        //> sta Sprite_Y_Position+20,y
        Sprite_Y_Position[((((Enemy_SprDataOffset[X] + 1) and 0xFF + 1) and 0xFF - 1) and 0xFF - 1) and 0xFF] = 0xF8
    }
    //> ExSPl: ldx ObjectOffset            ;get enemy object offset and leave
    //> rts
    return
}

// Decompiled from DrawBubble
fun drawBubble(X: Int) {
    //> DrawBubble:
    //> ldy Player_Y_HighPos        ;if player's vertical high position
    //> dey                         ;not within screen, skip all of this
    //> bne ExDBub
    if (zeroFlag) {
        //> lda Bubble_OffscreenBits    ;check air bubble's offscreen bits
        //> and #%00001000
        //> bne ExDBub                  ;if bit set, branch to leave
        if (zeroFlag) {
            //> ldy Bubble_SprDataOffset,x  ;get air bubble's OAM data offset
            //> lda Bubble_Rel_XPos         ;get relative horizontal coordinate
            //> sta Sprite_X_Position,y     ;store as X coordinate here
            Sprite_X_Position[Bubble_SprDataOffset[X]] = Bubble_Rel_XPos
            //> lda Bubble_Rel_YPos         ;get relative vertical coordinate
            //> sta Sprite_Y_Position,y     ;store as Y coordinate here
            Sprite_Y_Position[Bubble_SprDataOffset[X]] = Bubble_Rel_YPos
            //> lda #$74
            //> sta Sprite_Tilenumber,y     ;put air bubble tile into OAM data
            Sprite_Tilenumber[Bubble_SprDataOffset[X]] = 0x74
            //> lda #$02
            //> sta Sprite_Attributes,y     ;set attribute byte
            Sprite_Attributes[Bubble_SprDataOffset[X]] = 0x02
        }
    }
    //> ExDBub: rts                         ;leave
    return
}

// Decompiled from PlayerGfxHandler
fun playerGfxHandler() {
    var Y: Int = 0
    //> PlayerGfxHandler:
    //> lda InjuryTimer             ;if player's injured invincibility timer
    //> beq CntPl                   ;not set, skip checkpoint and continue code
    if (!zeroFlag) {
        //> lda FrameCounter
        //> lsr                         ;otherwise check frame counter and branch
        //> bcs ExPGH                   ;to leave on every other frame (when d0 is set)
        if (!carryFlag) {
            //> CntPl:  lda GameEngineSubroutine    ;if executing specific game engine routine,
            //> cmp #$0b                    ;branch ahead to some other part
            //> beq PlayerKilled
            if (!(GameEngineSubroutine - 0x0B == 0)) {
                //> lda PlayerChangeSizeFlag    ;if grow/shrink flag set
                //> bne DoChangeSize            ;then branch to some other code
                if (zeroFlag) {
                    //> ldy SwimmingFlag            ;if swimming flag set, branch to
                    //> beq FindPlayerAction        ;different part, do not return
                    //> lda Player_State
                    //> cmp #$00                    ;if player status normal,
                    //> beq FindPlayerAction        ;branch and do not return
                    //> jsr FindPlayerAction        ;otherwise jump and return
                    findPlayerAction(Player_State)
                    //> lda FrameCounter
                    //> and #%00000100              ;check frame counter for d2 set (8 frames every
                    //> bne ExPGH                   ;eighth frame), and branch if set to leave
                    if (zeroFlag) {
                        //> tax                         ;initialize X to zero
                        //> ldy Player_SprDataOffset    ;get player sprite data offset
                        //> lda PlayerFacingDir         ;get player's facing direction
                        //> lsr
                        //> bcs SwimKT                  ;if player facing to the right, use current offset
                        if (!carryFlag) {
                            //> iny
                            //> iny                         ;otherwise move to next OAM data
                            //> iny
                            //> iny
                        }
                        //> SwimKT: lda PlayerSize              ;check player's size
                        //> beq BigKTS                  ;if big, use first tile
                        if (!zeroFlag) {
                            //> lda Sprite_Tilenumber+24,y  ;check tile number of seventh/eighth sprite
                            //> cmp SwimTileRepOffset       ;against tile number in player graphics table
                            //> beq ExPGH                   ;if spr7/spr8 tile number = value, branch to leave
                            if (!(Sprite_Tilenumber[Y] - SwimTileRepOffset == 0)) {
                                //> inx                         ;otherwise increment X for second tile
                                //> BigKTS: lda SwimKickTileNum,x       ;overwrite tile number in sprite 7/8
                                //> sta Sprite_Tilenumber+24,y  ;to animate player's feet when swimming
                                Sprite_Tilenumber[((((Player_SprDataOffset + 1) and 0xFF + 1) and 0xFF + 1) and 0xFF + 1) and 0xFF] = SwimKickTileNum[(FrameCounter and 0x04 + 1) and 0xFF]
                            }
                        }
                    }
                    //> ExPGH:  rts                         ;then leave
                    return
                }
                //> DoChangeSize:
                //> jsr HandleChangeSize          ;find proper offset to graphics table for grow/shrink
                handleChangeSize()
                //> jmp PlayerGfxProcessing       ;draw player, then process for fireball throwing
            }
        }
    }
    if (!(GameEngineSubroutine - 0x0B == 0)) {
        if (zeroFlag) {
            if (zeroFlag) {
                if (!carryFlag) {
                }
                if (!zeroFlag) {
                    if (!(Sprite_Tilenumber[Y] - SwimTileRepOffset == 0)) {
                    }
                }
            }
        }
    } else {
        //> PlayerKilled:
        //> ldy #$0e                      ;load offset for player killed
        //> lda PlayerGfxTblOffsets,y     ;get offset to graphics table
    }
    //> PlayerGfxProcessing:
    //> sta PlayerGfxOffset           ;store offset to graphics table here
    PlayerGfxOffset = PlayerGfxTblOffsets[0x0E]
    //> lda #$04
    //> jsr RenderPlayerSub           ;draw player based on offset loaded
    renderPlayerSub(0x04)
    //> jsr ChkForPlayerAttrib        ;set horizontal flip bits as necessary
    chkForPlayerAttrib()
    //> lda FireballThrowingTimer
    //> beq PlayerOffscreenChk        ;if fireball throw timer not set, skip to the end
    if (!zeroFlag) {
        //> ldy #$00                      ;set value to initialize by default
        //> lda PlayerAnimTimer           ;get animation frame timer
        //> cmp FireballThrowingTimer     ;compare to fireball throw timer
        //> sty FireballThrowingTimer     ;initialize fireball throw timer
        FireballThrowingTimer = 0x00
        //> bcs PlayerOffscreenChk        ;if animation frame timer => fireball throw timer skip to end
        if (!(PlayerAnimTimer >= FireballThrowingTimer)) {
            //> sta FireballThrowingTimer     ;otherwise store animation timer into fireball throw timer
            FireballThrowingTimer = PlayerAnimTimer
            //> ldy #$07                      ;load offset for throwing
            //> lda PlayerGfxTblOffsets,y     ;get offset to graphics table
            //> sta PlayerGfxOffset           ;store it for use later
            PlayerGfxOffset = PlayerGfxTblOffsets[0x07]
            //> ldy #$04                      ;set to update four sprite rows by default
            //> lda Player_X_Speed
            //> ora Left_Right_Buttons        ;check for horizontal speed or left/right button press
            //> beq SUpdR                     ;if no speed or button press, branch using set value in Y
            if (!zeroFlag) {
                //> dey                           ;otherwise set to update only three sprite rows
            }
            //> SUpdR: tya                           ;save in A for use
            //> jsr RenderPlayerSub           ;in sub, draw player object again
            renderPlayerSub((0x04 - 1) and 0xFF)
        }
    }
    //> PlayerOffscreenChk:
    //> lda Player_OffscreenBits      ;get player's offscreen bits
    //> lsr
    //> lsr                           ;move vertical bits to low nybble
    //> lsr
    //> lsr
    //> sta $00                       ;store here
    zp_00 = Player_OffscreenBits shr 1 shr 1 shr 1 shr 1
    //> ldx #$03                      ;check all four rows of player sprites
    //> lda Player_SprDataOffset      ;get player's sprite data offset
    //> clc
    //> adc #$18                      ;add 24 bytes to start at bottom row
    //> tay                           ;set as offset here
    while (negativeFlag) {
        //> jsr DumpTwoSpr                ;otherwise dump offscreen Y coordinate into sprite data
        dumpTwoSpr((Player_SprDataOffset + 0x18 + 0) and 0xFF, (Player_SprDataOffset + 0x18 + 0) and 0xFF)
        do {
            //> PROfsLoop: lda #$f8                      ;load offscreen Y coordinate just in case
            //> lsr $00                       ;shift bit into carry
            zp_00 = zp_00 shr 1
            //> bcc NPROffscr                 ;if bit not set, skip, do not move sprites
            //> NPROffscr: tya
            //> sec                           ;subtract eight bytes to do
            //> sbc #$08                      ;next row up
            //> tay
            //> dex                           ;decrement row counter
            //> bpl PROfsLoop                 ;do this until all sprite rows are checked
        } while (!negativeFlag)
    }
    //> rts                           ;then we are done!
    return
}

// Decompiled from FindPlayerAction
fun findPlayerAction(A: Int) {
    //> FindPlayerAction:
    //> jsr ProcessPlayerAction       ;find proper offset to graphics table by player's actions
    processPlayerAction()
    //> jmp PlayerGfxProcessing       ;draw player, then process for fireball throwing
    //> PlayerGfxProcessing:
    //> sta PlayerGfxOffset           ;store offset to graphics table here
    PlayerGfxOffset = A
    //> lda #$04
    //> jsr RenderPlayerSub           ;draw player based on offset loaded
    renderPlayerSub(0x04)
    //> jsr ChkForPlayerAttrib        ;set horizontal flip bits as necessary
    chkForPlayerAttrib()
    //> lda FireballThrowingTimer
    //> beq PlayerOffscreenChk        ;if fireball throw timer not set, skip to the end
    if (!zeroFlag) {
        //> ldy #$00                      ;set value to initialize by default
        //> lda PlayerAnimTimer           ;get animation frame timer
        //> cmp FireballThrowingTimer     ;compare to fireball throw timer
        //> sty FireballThrowingTimer     ;initialize fireball throw timer
        FireballThrowingTimer = 0x00
        //> bcs PlayerOffscreenChk        ;if animation frame timer => fireball throw timer skip to end
        if (!(PlayerAnimTimer >= FireballThrowingTimer)) {
            //> sta FireballThrowingTimer     ;otherwise store animation timer into fireball throw timer
            FireballThrowingTimer = PlayerAnimTimer
            //> ldy #$07                      ;load offset for throwing
            //> lda PlayerGfxTblOffsets,y     ;get offset to graphics table
            //> sta PlayerGfxOffset           ;store it for use later
            PlayerGfxOffset = PlayerGfxTblOffsets[0x07]
            //> ldy #$04                      ;set to update four sprite rows by default
            //> lda Player_X_Speed
            //> ora Left_Right_Buttons        ;check for horizontal speed or left/right button press
            //> beq SUpdR                     ;if no speed or button press, branch using set value in Y
            if (!zeroFlag) {
                //> dey                           ;otherwise set to update only three sprite rows
            }
            //> SUpdR: tya                           ;save in A for use
            //> jsr RenderPlayerSub           ;in sub, draw player object again
            renderPlayerSub((0x04 - 1) and 0xFF)
        }
    }
    //> PlayerOffscreenChk:
    //> lda Player_OffscreenBits      ;get player's offscreen bits
    //> lsr
    //> lsr                           ;move vertical bits to low nybble
    //> lsr
    //> lsr
    //> sta $00                       ;store here
    zp_00 = Player_OffscreenBits shr 1 shr 1 shr 1 shr 1
    //> ldx #$03                      ;check all four rows of player sprites
    //> lda Player_SprDataOffset      ;get player's sprite data offset
    //> clc
    //> adc #$18                      ;add 24 bytes to start at bottom row
    //> tay                           ;set as offset here
    while (negativeFlag) {
        //> jsr DumpTwoSpr                ;otherwise dump offscreen Y coordinate into sprite data
        dumpTwoSpr((Player_SprDataOffset + 0x18 + 0) and 0xFF, (Player_SprDataOffset + 0x18 + 0) and 0xFF)
        do {
            //> PROfsLoop: lda #$f8                      ;load offscreen Y coordinate just in case
            //> lsr $00                       ;shift bit into carry
            zp_00 = zp_00 shr 1
            //> bcc NPROffscr                 ;if bit not set, skip, do not move sprites
            //> NPROffscr: tya
            //> sec                           ;subtract eight bytes to do
            //> sbc #$08                      ;next row up
            //> tay
            //> dex                           ;decrement row counter
            //> bpl PROfsLoop                 ;do this until all sprite rows are checked
        } while (!negativeFlag)
    }
    //> rts                           ;then we are done!
    return
}

// Decompiled from DrawPlayer_Intermediate
fun drawplayerIntermediate() {
    //> DrawPlayer_Intermediate:
    //> ldx #$05                       ;store data into zero page memory
    do {
        //> PIntLoop: lda IntermediatePlayerData,x   ;load data to display player as he always
        //> sta $02,x                      ;appears on world/lives display
        zp_02[0x05] = IntermediatePlayerData[0x05]
        //> dex
        //> bpl PIntLoop                   ;do this until all data is loaded
    } while (negativeFlag)
    //> ldx #$b8                       ;load offset for small standing
    //> ldy #$04                       ;load sprite data offset
    //> jsr DrawPlayerLoop             ;draw player accordingly
    drawPlayerLoop(0xB8)
    //> lda Sprite_Attributes+36       ;get empty sprite attributes
    //> ora #%01000000                 ;set horizontal flip bit for bottom-right sprite
    //> sta Sprite_Attributes+32       ;store and leave
    Sprite_Attributes = Sprite_Attributes or 0x40
    //> rts
    return
}

// Decompiled from RenderPlayerSub
fun renderPlayerSub(A: Int) {
    //> RenderPlayerSub:
    //> sta $07                      ;store number of rows of sprites to draw
    zp_07 = A
    //> lda Player_Rel_XPos
    //> sta Player_Pos_ForScroll     ;store player's relative horizontal position
    Player_Pos_ForScroll = Player_Rel_XPos
    //> sta $05                      ;store it here also
    zp_05 = Player_Rel_XPos
    //> lda Player_Rel_YPos
    //> sta $02                      ;store player's vertical position
    zp_02 = Player_Rel_YPos
    //> lda PlayerFacingDir
    //> sta $03                      ;store player's facing direction
    zp_03 = PlayerFacingDir
    //> lda Player_SprAttrib
    //> sta $04                      ;store player's sprite attributes
    zp_04 = Player_SprAttrib
    //> ldx PlayerGfxOffset          ;load graphics table offset
    //> ldy Player_SprDataOffset     ;get player's sprite data offset
}

// Decompiled from DrawPlayerLoop
fun drawPlayerLoop(X: Int) {
    var Y: Int = 0
    do {
        //> DrawPlayerLoop:
        //> lda PlayerGraphicsTable,x    ;load player's left side
        //> sta $00
        zp_00 = PlayerGraphicsTable[X]
        //> lda PlayerGraphicsTable+1,x  ;now load right side
        //> jsr DrawOneSpriteRow
        drawOneSpriteRow(PlayerGraphicsTable[X], X, Y)
        //> dec $07                      ;decrement rows of sprites to draw
        zp_07 = (zp_07 - 1) and 0xFF
        //> bne DrawPlayerLoop           ;do this until all rows are drawn
    } while (zeroFlag)
    //> rts
    return
}

// Decompiled from ProcessPlayerAction
fun processPlayerAction() {
    var A: Int = 0
    //> ProcessPlayerAction:
    //> lda Player_State      ;get player's state
    //> cmp #$03
    //> beq ActionClimbing    ;if climbing, branch here
    if (!(Player_State - 0x03 == 0)) {
        //> cmp #$02
        //> beq ActionFalling     ;if falling, branch here
        if (!(A - 0x02 == 0)) {
            //> cmp #$01
            //> bne ProcOnGroundActs  ;if not jumping, branch here
            if (A - 0x01 == 0) {
                //> lda SwimmingFlag
                //> bne ActionSwimming    ;if swimming flag set, branch elsewhere
                if (zeroFlag) {
                    //> ldy #$06              ;load offset for crouching
                    //> lda CrouchingFlag     ;get crouching flag
                    //> bne NonAnimatedActs   ;if set, branch to get offset for graphics table
                    if (zeroFlag) {
                        //> ldy #$00              ;otherwise load offset for jumping
                        //> jmp NonAnimatedActs   ;go to get offset to graphics table
                        //> ProcOnGroundActs:
                        //> ldy #$06                   ;load offset for crouching
                        //> lda CrouchingFlag          ;get crouching flag
                        //> bne NonAnimatedActs        ;if set, branch to get offset for graphics table
                        if (zeroFlag) {
                            //> ldy #$02                   ;load offset for standing
                            //> lda Player_X_Speed         ;check player's horizontal speed
                            //> ora Left_Right_Buttons     ;and left/right controller bits
                            //> beq NonAnimatedActs        ;if no speed or buttons pressed, use standing offset
                            if (!zeroFlag) {
                                //> lda Player_XSpeedAbsolute  ;load walking/running speed
                                //> cmp #$09
                                //> bcc ActionWalkRun          ;if less than a certain amount, branch, too slow to skid
                                if (Player_XSpeedAbsolute >= 0x09) {
                                    //> lda Player_MovingDir       ;otherwise check to see if moving direction
                                    //> and PlayerFacingDir        ;and facing direction are the same
                                    //> bne ActionWalkRun          ;if moving direction = facing direction, branch, don't skid
                                    if (zeroFlag) {
                                        //> iny                        ;otherwise increment to skid offset ($03)
                                        //> NonAnimatedActs:
                                        //> jsr GetGfxOffsetAdder      ;do a sub here to get offset adder for graphics table
                                        getGfxOffsetAdder((0x02 + 1) and 0xFF)
                                        //> lda #$00
                                        //> sta PlayerAnimCtrl         ;initialize animation frame control
                                        PlayerAnimCtrl = 0x00
                                        //> lda PlayerGfxTblOffsets,y  ;load offset to graphics table using size as offset
                                        //> rts
                                        return
                                        //> ActionFalling:
                                        //> ldy #$04                  ;load offset for walking/running
                                        //> jsr GetGfxOffsetAdder     ;get offset to graphics table
                                        getGfxOffsetAdder(0x04)
                                        //> jmp GetCurrentAnimOffset  ;execute instructions for falling state
                                    }
                                }
                            }
                        }
                    }
                    //> ActionWalkRun:
                    //> ldy #$04               ;load offset for walking/running
                    //> jsr GetGfxOffsetAdder  ;get offset to graphics table
                    getGfxOffsetAdder(0x04)
                    //> jmp FourFrameExtent    ;execute instructions for normal state
                    do {
                        //> ActionClimbing:
                        //> ldy #$05               ;load offset for climbing
                        //> lda Player_Y_Speed     ;check player's vertical speed
                        //> beq NonAnimatedActs    ;if no speed, branch, use offset as-is
                    } while (zeroFlag)
                    //> jsr GetGfxOffsetAdder  ;otherwise get offset for graphics table
                    getGfxOffsetAdder(0x05)
                    //> jmp ThreeFrameExtent   ;then skip ahead to more code
                }
            } else {
                if (zeroFlag) {
                    if (!zeroFlag) {
                        if (Player_XSpeedAbsolute >= 0x09) {
                            if (zeroFlag) {
                            }
                        }
                    }
                }
            }
        }
    } else {
        do {
        } while (zeroFlag)
        //> ActionSwimming:
        //> ldy #$01               ;load offset for swimming
        //> jsr GetGfxOffsetAdder
        getGfxOffsetAdder(0x01)
        //> lda JumpSwimTimer      ;check jump/swim timer
        //> ora PlayerAnimCtrl     ;and animation frame control
        //> bne FourFrameExtent    ;if any one of these set, branch ahead
        if (zeroFlag) {
            //> lda A_B_Buttons
            //> asl                    ;check for A button pressed
            //> bcs FourFrameExtent    ;branch to same place if A button pressed
            if (!carryFlag) {
            }
        }
    }
    //> FourFrameExtent:
    //> lda #$03              ;load upper extent for frame control
    //> jmp AnimationControl  ;jump to get offset and animate player object
    //> ThreeFrameExtent:
    //> lda #$02              ;load upper extent for frame control for climbing
    //> AnimationControl:
    //> sta $00                   ;store upper extent here
    zp_00 = 0x02
    //> jsr GetCurrentAnimOffset  ;get proper offset to graphics table
    getCurrentAnimOffset(0x01)
    //> pha                       ;save offset to stack
    //> lda PlayerAnimTimer       ;load animation frame timer
    //> bne ExAnimC               ;branch if not expired
    if (zeroFlag) {
        //> lda PlayerAnimTimerSet    ;get animation frame timer amount
        //> sta PlayerAnimTimer       ;and set timer accordingly
        PlayerAnimTimer = PlayerAnimTimerSet
        //> lda PlayerAnimCtrl
        //> clc                       ;add one to animation frame control
        //> adc #$01
        //> cmp $00                   ;compare to upper extent
        //> bcc SetAnimC              ;if frame control + 1 < upper extent, use as next
        if ((PlayerAnimCtrl + 0x01 + 0) and 0xFF >= zp_00) {
            //> lda #$00                  ;otherwise initialize frame control
        }
        //> SetAnimC: sta PlayerAnimCtrl        ;store as new animation frame control
        PlayerAnimCtrl = 0x00
    }
    //> ExAnimC:  pla                       ;get offset to graphics table from stack and leave
    //> rts
    return
}

// Decompiled from GetCurrentAnimOffset
fun getCurrentAnimOffset(Y: Int): Int {
    //> GetCurrentAnimOffset:
    //> lda PlayerAnimCtrl         ;get animation frame control
    //> jmp GetOffsetFromAnimCtrl  ;jump to get proper offset to graphics table
    //> GetOffsetFromAnimCtrl:
    //> asl                        ;multiply animation frame control
    //> asl                        ;by eight to get proper amount
    //> asl                        ;to add to our offset
    //> adc PlayerGfxTblOffsets,y  ;add to offset to graphics table
    //> rts                        ;and return with result in A
    return A
}

// Decompiled from GetGfxOffsetAdder
fun getGfxOffsetAdder(Y: Int) {
    //> GetGfxOffsetAdder:
    //> lda PlayerSize  ;get player's size
    //> beq SzOfs       ;if player big, use current offset as-is
    if (!zeroFlag) {
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
    var Y: Int = 0
    //> HandleChangeSize:
    //> ldy PlayerAnimCtrl           ;get animation frame control
    //> lda FrameCounter
    //> and #%00000011               ;get frame counter and execute this code every
    //> bne GorSLog                  ;fourth frame, otherwise branch ahead
    if (zeroFlag) {
        //> iny                          ;increment frame control
        //> cpy #$0a                     ;check for preset upper extent
        //> bcc CSzNext                  ;if not there yet, skip ahead to use
        if ((Y + 1) and 0xFF >= 0x0A) {
            //> ldy #$00                     ;otherwise initialize both grow/shrink flag
            //> sty PlayerChangeSizeFlag     ;and animation frame control
            PlayerChangeSizeFlag = 0x00
        }
        //> CSzNext: sty PlayerAnimCtrl           ;store proper frame control
        PlayerAnimCtrl = 0x00
    }
    //> GorSLog: lda PlayerSize               ;get player's size
    //> bne ShrinkPlayer             ;if player small, skip ahead to next part
    if (zeroFlag) {
        //> lda ChangeSizeOffsetAdder,y  ;get offset adder based on frame control as offset
        //> ldy #$0f                     ;load offset for player growing
        //> GetOffsetFromAnimCtrl:
        //> asl                        ;multiply animation frame control
        //> asl                        ;by eight to get proper amount
        //> asl                        ;to add to our offset
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
        //> lda ChangeSizeOffsetAdder,x  ;get what would normally be offset adder
        //> bne ShrPlF                   ;and branch to use offset if nonzero
        if (zeroFlag) {
            //> ldy #$01                     ;otherwise load offset for big player swimming
        }
    }
    //> ShrPlF: lda PlayerGfxTblOffsets,y    ;get offset to graphics table based on offset loaded
    //> rts                          ;and leave
    return
}

// Decompiled from ChkForPlayerAttrib
fun chkForPlayerAttrib() {
    var A: Int = 0
    //> ChkForPlayerAttrib:
    //> ldy Player_SprDataOffset    ;get sprite data offset
    //> lda GameEngineSubroutine
    //> cmp #$0b                    ;if executing specific game engine routine,
    //> beq KilledAtt               ;branch to change third and fourth row OAM attributes
    if (!(GameEngineSubroutine - 0x0B == 0)) {
        //> lda PlayerGfxOffset         ;get graphics table offset
        //> cmp #$50
        //> beq C_S_IGAtt               ;if crouch offset, either standing offset,
        if (!(PlayerGfxOffset - 0x50 == 0)) {
            //> cmp #$b8                    ;or intermediate growing offset,
            //> beq C_S_IGAtt               ;go ahead and execute code to change
            if (!(A - 0xB8 == 0)) {
                //> cmp #$c0                    ;fourth row OAM attributes only
                //> beq C_S_IGAtt
                if (!(A - 0xC0 == 0)) {
                    //> cmp #$c8
                    //> bne ExPlyrAt                ;if none of these, branch to leave
                    if (A - 0xC8 == 0) {
                        //> KilledAtt: lda Sprite_Attributes+16,y
                        //> and #%00111111              ;mask out horizontal and vertical flip bits
                        //> sta Sprite_Attributes+16,y  ;for third row sprites and save
                        Sprite_Attributes[Player_SprDataOffset] = Sprite_Attributes[Player_SprDataOffset] and 0x3F
                        //> lda Sprite_Attributes+20,y
                        //> and #%00111111
                        //> ora #%01000000              ;set horizontal flip bit for second
                        //> sta Sprite_Attributes+20,y  ;sprite in the third row
                        Sprite_Attributes[Player_SprDataOffset] = Sprite_Attributes[Player_SprDataOffset] and 0x3F or 0x40
                        //> C_S_IGAtt: lda Sprite_Attributes+24,y
                        //> and #%00111111              ;mask out horizontal and vertical flip bits
                        //> sta Sprite_Attributes+24,y  ;for fourth row sprites and save
                        Sprite_Attributes[Player_SprDataOffset] = Sprite_Attributes[Player_SprDataOffset] and 0x3F
                        //> lda Sprite_Attributes+28,y
                        //> and #%00111111
                        //> ora #%01000000              ;set horizontal flip bit for second
                        //> sta Sprite_Attributes+28,y  ;sprite in the fourth row
                        Sprite_Attributes[Player_SprDataOffset] = Sprite_Attributes[Player_SprDataOffset] and 0x3F or 0x40
                    }
                }
            }
        }
    }
    //> ExPlyrAt:  rts                         ;leave
    return
}

// Decompiled from RelativePlayerPosition
fun relativePlayerPosition() {
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
    //> RelativeBlockPosition:
    //> lda #$09                     ;get coordinates of one block object
    //> ldy #$04                     ;relative to the screen
    //> jsr VariableObjOfsRelPos
    variableObjOfsRelPos(0x09, X)
    //> inx                          ;adjust offset for other block object if any
    //> inx
    //> lda #$09
    //> iny                          ;adjust other and get coordinates for other one
}

// Decompiled from VariableObjOfsRelPos
fun variableObjOfsRelPos(A: Int, X: Int) {
    var Y: Int = 0
    //> VariableObjOfsRelPos:
    //> stx $00                     ;store value to add to A here
    zp_00 = X
    //> clc
    //> adc $00                     ;add A to value stored
    //> tax                         ;use as enemy offset
    //> jsr GetObjRelativePosition
    getObjRelativePosition((A + zp_00 + 0) and 0xFF, Y)
    //> ldx ObjectOffset            ;reload old object offset and leave
    //> rts
    return
}

// Decompiled from GetObjRelativePosition
fun getObjRelativePosition(X: Int, Y: Int) {
    //> GetObjRelativePosition:
    //> lda SprObject_Y_Position,x  ;load vertical coordinate low
    //> sta SprObject_Rel_YPos,y    ;store here
    SprObject_Rel_YPos[Y] = SprObject_Y_Position[X]
    //> lda SprObject_X_Position,x  ;load horizontal coordinate
    //> sec                         ;subtract left edge coordinate
    //> sbc ScreenLeft_X_Pos
    //> sta SprObject_Rel_XPos,y    ;store result here
    SprObject_Rel_XPos[Y] = (SprObject_X_Position[X] - ScreenLeft_X_Pos - (1 - 1)) and 0xFF
    //> rts
    return
}

// Decompiled from GetPlayerOffscreenBits
fun getPlayerOffscreenBits() {
    //> GetPlayerOffscreenBits:
    //> ldx #$00                 ;set offsets for player-specific variables
    //> ldy #$00                 ;and get offscreen information about player
    //> jmp GetOffScreenBitsSet
    //> GetOffScreenBitsSet:
    //> tya                         ;save offscreen bits offset to stack for now
    //> pha
    //> jsr RunOffscrBitsSubs
    runOffscrBitsSubs(0x00, 0x00)
    //> asl                         ;move low nybble to high nybble
    //> asl
    //> asl
    //> asl
    //> ora $00                     ;mask together with previously saved low nybble
    //> sta $00                     ;store both here
    zp_00 = ((((0x00 shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF or zp_00
    //> pla                         ;get offscreen bits offset from stack
    //> tay
    //> lda $00                     ;get value here and store elsewhere
    //> sta SprObject_OffscrBits,y
    SprObject_OffscrBits[((((0x00 shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF or zp_00] = zp_00
    //> ldx ObjectOffset
    //> rts
    return
}

// Decompiled from GetFireballOffscreenBits
fun getFireballOffscreenBits() {
    var X: Int = 0
    //> GetFireballOffscreenBits:
    //> ldy #$00                 ;set for fireball offsets
    //> jsr GetProperObjOffset   ;modify X to get proper fireball offset
    getProperObjOffset(X, 0x00)
    //> ldy #$02                 ;set other offset for fireball's offscreen bits
    //> jmp GetOffScreenBitsSet  ;and get offscreen information about fireball
    //> GetOffScreenBitsSet:
    //> tya                         ;save offscreen bits offset to stack for now
    //> pha
    //> jsr RunOffscrBitsSubs
    runOffscrBitsSubs(0x02, X)
    //> asl                         ;move low nybble to high nybble
    //> asl
    //> asl
    //> asl
    //> ora $00                     ;mask together with previously saved low nybble
    //> sta $00                     ;store both here
    zp_00 = ((((0x02 shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF or zp_00
    //> pla                         ;get offscreen bits offset from stack
    //> tay
    //> lda $00                     ;get value here and store elsewhere
    //> sta SprObject_OffscrBits,y
    SprObject_OffscrBits[((((0x02 shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF or zp_00] = zp_00
    //> ldx ObjectOffset
    //> rts
    return
}

// Decompiled from GetBubbleOffscreenBits
fun getBubbleOffscreenBits() {
    var X: Int = 0
    //> GetBubbleOffscreenBits:
    //> ldy #$01                 ;set for air bubble offsets
    //> jsr GetProperObjOffset   ;modify X to get proper air bubble offset
    getProperObjOffset(X, 0x01)
    //> ldy #$03                 ;set other offset for airbubble's offscreen bits
    //> jmp GetOffScreenBitsSet  ;and get offscreen information about air bubble
    //> GetOffScreenBitsSet:
    //> tya                         ;save offscreen bits offset to stack for now
    //> pha
    //> jsr RunOffscrBitsSubs
    runOffscrBitsSubs(0x03, X)
    //> asl                         ;move low nybble to high nybble
    //> asl
    //> asl
    //> asl
    //> ora $00                     ;mask together with previously saved low nybble
    //> sta $00                     ;store both here
    zp_00 = ((((0x03 shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF or zp_00
    //> pla                         ;get offscreen bits offset from stack
    //> tay
    //> lda $00                     ;get value here and store elsewhere
    //> sta SprObject_OffscrBits,y
    SprObject_OffscrBits[((((0x03 shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF or zp_00] = zp_00
    //> ldx ObjectOffset
    //> rts
    return
}

// Decompiled from GetMiscOffscreenBits
fun getMiscOffscreenBits() {
    var X: Int = 0
    //> GetMiscOffscreenBits:
    //> ldy #$02                 ;set for misc object offsets
    //> jsr GetProperObjOffset   ;modify X to get proper misc object offset
    getProperObjOffset(X, 0x02)
    //> ldy #$06                 ;set other offset for misc object's offscreen bits
    //> jmp GetOffScreenBitsSet  ;and get offscreen information about misc object
    //> GetOffScreenBitsSet:
    //> tya                         ;save offscreen bits offset to stack for now
    //> pha
    //> jsr RunOffscrBitsSubs
    runOffscrBitsSubs(0x06, X)
    //> asl                         ;move low nybble to high nybble
    //> asl
    //> asl
    //> asl
    //> ora $00                     ;mask together with previously saved low nybble
    //> sta $00                     ;store both here
    zp_00 = ((((0x06 shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF or zp_00
    //> pla                         ;get offscreen bits offset from stack
    //> tay
    //> lda $00                     ;get value here and store elsewhere
    //> sta SprObject_OffscrBits,y
    SprObject_OffscrBits[((((0x06 shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF or zp_00] = zp_00
    //> ldx ObjectOffset
    //> rts
    return
}

// Decompiled from GetProperObjOffset
fun getProperObjOffset(X: Int, Y: Int) {
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
    //> GetEnemyOffscreenBits:
    //> lda #$01                 ;set A to add 1 byte in order to get enemy offset
    //> ldy #$01                 ;set Y to put offscreen bits in Enemy_OffscreenBits
    //> jmp SetOffscrBitsOffset
    //> SetOffscrBitsOffset:
    //> stx $00
    zp_00 = X
    //> clc           ;add contents of X to A to get
    //> adc $00       ;appropriate offset, then give back to X
    //> tax
    //> GetOffScreenBitsSet:
    //> tya                         ;save offscreen bits offset to stack for now
    //> pha
    //> jsr RunOffscrBitsSubs
    runOffscrBitsSubs(0x01, (0x01 + zp_00 + 0) and 0xFF)
    //> asl                         ;move low nybble to high nybble
    //> asl
    //> asl
    //> asl
    //> ora $00                     ;mask together with previously saved low nybble
    //> sta $00                     ;store both here
    zp_00 = ((((0x01 shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF or zp_00
    //> pla                         ;get offscreen bits offset from stack
    //> tay
    //> lda $00                     ;get value here and store elsewhere
    //> sta SprObject_OffscrBits,y
    SprObject_OffscrBits[((((0x01 shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF or zp_00] = zp_00
    //> ldx ObjectOffset
    //> rts
    return
}

// Decompiled from GetBlockOffscreenBits
fun getBlockOffscreenBits(X: Int) {
    //> GetBlockOffscreenBits:
    //> lda #$09       ;set A to add 9 bytes in order to get block obj offset
    //> ldy #$04       ;set Y to put offscreen bits in Block_OffscreenBits
    //> SetOffscrBitsOffset:
    //> stx $00
    zp_00 = X
    //> clc           ;add contents of X to A to get
    //> adc $00       ;appropriate offset, then give back to X
    //> tax
    //> GetOffScreenBitsSet:
    //> tya                         ;save offscreen bits offset to stack for now
    //> pha
    //> jsr RunOffscrBitsSubs
    runOffscrBitsSubs(0x04, (0x09 + zp_00 + 0) and 0xFF)
    //> asl                         ;move low nybble to high nybble
    //> asl
    //> asl
    //> asl
    //> ora $00                     ;mask together with previously saved low nybble
    //> sta $00                     ;store both here
    zp_00 = ((((0x04 shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF or zp_00
    //> pla                         ;get offscreen bits offset from stack
    //> tay
    //> lda $00                     ;get value here and store elsewhere
    //> sta SprObject_OffscrBits,y
    SprObject_OffscrBits[((((0x04 shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF shl 1) and 0xFF or zp_00] = zp_00
    //> ldx ObjectOffset
    //> rts
    return
}

// Decompiled from RunOffscrBitsSubs
fun runOffscrBitsSubs(A: Int, X: Int): Int {
    //> RunOffscrBitsSubs:
    //> jsr GetXOffscreenBits  ;do subroutine here
    getXOffscreenBits(X)
    //> lsr                    ;move high nybble to low
    //> lsr
    //> lsr
    //> lsr
    //> sta $00                ;store here
    zp_00 = A shr 1 shr 1 shr 1 shr 1
    //> jmp GetYOffscreenBits
    //> GetYOffscreenBits:
    //> stx $04                      ;save position in buffer to here
    zp_04 = X
    //> ldy #$01                     ;start with top of screen
    while (negativeFlag) {
        //> ldx DefaultYOnscreenOfs+1,y  ;if not, load alternate offset value here
        //> cmp #$01
        //> bpl YLdBData                 ;if one vertical unit or more above the screen, branch
        if (A - 0x01 < 0) {
            //> lda #$20                     ;if no branching, load value here and store
            //> sta $06
            zp_06 = 0x20
            //> lda #$04                     ;load some other value and execute subroutine
            //> jsr DividePDiff
            dividePDiff(0x04, 0x01)
        }
        //> YLdBData: lda YOffscreenBitsData,x     ;get offscreen data bits using offset
        //> ldx $04                      ;reobtain position in buffer
        //> cmp #$00
        //> bne ExYOfsBS                 ;if bits not zero, branch to leave
        if (YOffscreenBitsData[X] - 0x00 == 0) {
            do {
                //> YOfsLoop: lda HighPosUnitData,y        ;load coordinate for edge of vertical unit
                //> sec
                //> sbc SprObject_Y_Position,x   ;subtract from vertical coordinate of object
                //> sta $07                      ;store here
                zp_07 = (HighPosUnitData[0x01] - SprObject_Y_Position[zp_04] - (1 - 1)) and 0xFF
                //> lda #$01                     ;subtract one from vertical high byte of object
                //> sbc SprObject_Y_HighPos,x
                //> ldx DefaultYOnscreenOfs,y    ;load offset value here
                //> cmp #$00
                //> bmi YLdBData                 ;if under top of the screen or beyond bottom, branch
                //> dey                          ;otherwise, do bottom of the screen now
                //> bpl YOfsLoop
            } while (!negativeFlag)
        }
    }
    //> ExYOfsBS: rts
    return A
}

// Decompiled from GetXOffscreenBits
fun getXOffscreenBits(X: Int): Int {
    var A: Int = 0
    //> GetXOffscreenBits:
    //> stx $04                     ;save position in buffer to here
    zp_04 = X
    //> ldy #$01                    ;start with right side of screen
    while (negativeFlag) {
        //> ldx DefaultXOnscreenOfs+1,y ;if not, load alternate offset value here
        //> cmp #$01
        //> bpl XLdBData                ;if one page or more to the left of either edge, branch
        if (A - 0x01 < 0) {
            //> lda #$38                    ;if no branching, load value here and store
            //> sta $06
            zp_06 = 0x38
            //> lda #$08                    ;load some other value and execute subroutine
            //> jsr DividePDiff
            dividePDiff(0x08, 0x01)
        }
        //> XLdBData: lda XOffscreenBitsData,x    ;get bits here
        //> ldx $04                     ;reobtain position in buffer
        //> cmp #$00                    ;if bits not zero, branch to leave
        //> bne ExXOfsBS
        if (XOffscreenBitsData[X] - 0x00 == 0) {
            do {
                //> XOfsLoop: lda ScreenEdge_X_Pos,y      ;get pixel coordinate of edge
                //> sec                         ;get difference between pixel coordinate of edge
                //> sbc SprObject_X_Position,x  ;and pixel coordinate of object position
                //> sta $07                     ;store here
                zp_07 = (ScreenEdge_X_Pos[0x01] - SprObject_X_Position[zp_04] - (1 - 1)) and 0xFF
                //> lda ScreenEdge_PageLoc,y    ;get page location of edge
                //> sbc SprObject_PageLoc,x     ;subtract from page location of object position
                //> ldx DefaultXOnscreenOfs,y   ;load offset value here
                //> cmp #$00
                //> bmi XLdBData                ;if beyond right edge or in front of left edge, branch
                //> dey                         ;otherwise, do left side of screen now
                //> bpl XOfsLoop                ;branch if not already done with left side
            } while (!negativeFlag)
        }
    }
    //> ExXOfsBS: rts
    return A
}

// Decompiled from DividePDiff
fun dividePDiff(A: Int, Y: Int) {
    //> DividePDiff:
    //> sta $05       ;store current value in A here
    zp_05 = A
    //> lda $07       ;get pixel difference
    //> cmp $06       ;compare to preset value
    //> bcs ExDivPD   ;if pixel difference >= preset value, branch
    if (!(zp_07 >= zp_06)) {
        //> lsr           ;divide by eight
        //> lsr
        //> lsr
        //> and #$07      ;mask out all but 3 LSB
        //> cpy #$01      ;right side of the screen or top?
        //> bcs SetOscrO  ;if so, branch, use difference / 8 as offset
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
    var A: Int = 0
    var Y: Int = 0
    //> SoundEngine:
    //> lda OperMode              ;are we in title screen mode?
    //> bne SndOn
    if (zeroFlag) {
        //> sta SND_MASTERCTRL_REG    ;if so, disable sound and leave
        SND_MASTERCTRL_REG = OperMode
        //> rts
        return
    } else {
        //> SndOn:   lda #$ff
        //> sta JOYPAD_PORT2          ;disable irqs and set frame counter mode???
        JOYPAD_PORT2 = 0xFF
        //> lda #$0f
        //> sta SND_MASTERCTRL_REG    ;enable first four channels
        SND_MASTERCTRL_REG = 0x0F
        //> lda PauseModeFlag         ;is sound already in pause mode?
        //> bne InPause
        if (zeroFlag) {
            //> lda PauseSoundQueue       ;if not, check pause sfx queue
            //> cmp #$01
            //> bne RunSoundSubroutines   ;if queue is empty, skip pause mode routine
            if (PauseSoundQueue - 0x01 == 0) {
                //> InPause: lda PauseSoundBuffer      ;check pause sfx buffer
                //> bne ContPau
                if (zeroFlag) {
                    //> lda PauseSoundQueue       ;check pause queue
                    //> beq SkipSoundSubroutines
                    if (!zeroFlag) {
                        //> sta PauseSoundBuffer      ;if queue full, store in buffer and activate
                        PauseSoundBuffer = PauseSoundQueue
                        //> sta PauseModeFlag         ;pause mode to interrupt game sounds
                        PauseModeFlag = PauseSoundQueue
                        //> lda #$00                  ;disable sound and clear sfx buffers
                        //> sta SND_MASTERCTRL_REG
                        SND_MASTERCTRL_REG = 0x00
                        //> sta Square1SoundBuffer
                        Square1SoundBuffer = 0x00
                        //> sta Square2SoundBuffer
                        Square2SoundBuffer = 0x00
                        //> sta NoiseSoundBuffer
                        NoiseSoundBuffer = 0x00
                        //> lda #$0f
                        //> sta SND_MASTERCTRL_REG    ;enable sound again
                        SND_MASTERCTRL_REG = 0x0F
                        //> lda #$2a                  ;store length of sound in pause counter
                        //> sta Squ1_SfxLenCounter
                        Squ1_SfxLenCounter = 0x2A
                        while (zeroFlag) {
                            //> ContPau: lda Squ1_SfxLenCounter    ;check pause length left
                            //> cmp #$24                  ;time to play second?
                            //> beq PTone2F
                            if (!(Squ1_SfxLenCounter - 0x24 == 0)) {
                                do {
                                    //> PTone1F: lda #$44                  ;play first tone
                                    //> bne PTRegC                ;unconditional branch
                                    //> cmp #$1e                  ;time to play first again?
                                    //> beq PTone1F
                                } while (A - 0x1E == 0)
                                //> cmp #$18                  ;time to play second again?
                                //> bne DecPauC               ;only load regs during times, otherwise skip
                                if (A - 0x18 == 0) {
                                    //> PTone2F: lda #$64                  ;store reg contents and play the pause sfx
                                    //> PTRegC:  ldx #$84
                                    //> ldy #$7f
                                    //> jsr PlaySqu1Sfx
                                    playSqu1Sfx()
                                }
                            }
                        }
                        //> DecPauC: dec Squ1_SfxLenCounter    ;decrement pause sfx counter
                        Squ1_SfxLenCounter = (Squ1_SfxLenCounter - 1) and 0xFF
                        //> bne SkipSoundSubroutines
                        if (zeroFlag) {
                            //> lda #$00                  ;disable sound if in pause mode and
                            //> sta SND_MASTERCTRL_REG    ;not currently playing the pause sfx
                            SND_MASTERCTRL_REG = 0x00
                            //> lda PauseSoundBuffer      ;if no longer playing pause sfx, check to see
                            //> cmp #$02                  ;if we need to be playing sound again
                            //> bne SkipPIn
                            if (PauseSoundBuffer - 0x02 == 0) {
                                //> lda #$00                  ;clear pause mode to allow game sounds again
                                //> sta PauseModeFlag
                                PauseModeFlag = 0x00
                            }
                            //> SkipPIn: lda #$00                  ;clear pause sfx buffer
                            //> sta PauseSoundBuffer
                            PauseSoundBuffer = 0x00
                            //> beq SkipSoundSubroutines
                            if (!zeroFlag) {
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
                                //> sta AreaMusicQueue
                                AreaMusicQueue = 0x00
                                //> sta EventMusicQueue
                                EventMusicQueue = 0x00
                            }
                        }
                    }
                }
                if (!(Squ1_SfxLenCounter - 0x24 == 0)) {
                    do {
                    } while (A - 0x1E == 0)
                    if (A - 0x18 == 0) {
                    }
                }
                if (zeroFlag) {
                    if (PauseSoundBuffer - 0x02 == 0) {
                    }
                    if (!zeroFlag) {
                    }
                }
            }
        }
    }
    if (zeroFlag) {
        if (!zeroFlag) {
            while (zeroFlag) {
                if (!(Squ1_SfxLenCounter - 0x24 == 0)) {
                    do {
                    } while (A - 0x1E == 0)
                    if (A - 0x18 == 0) {
                    }
                }
            }
            if (zeroFlag) {
                if (PauseSoundBuffer - 0x02 == 0) {
                }
                if (!zeroFlag) {
                }
            }
        }
    }
    if (!(Squ1_SfxLenCounter - 0x24 == 0)) {
        do {
        } while (A - 0x1E == 0)
        if (A - 0x18 == 0) {
        }
    }
    if (zeroFlag) {
        if (PauseSoundBuffer - 0x02 == 0) {
        }
        if (!zeroFlag) {
        }
    }
    //> SkipSoundSubroutines:
    //> lda #$00               ;clear the sound effects queues
    //> sta Square1SoundQueue
    Square1SoundQueue = 0x00
    //> sta Square2SoundQueue
    Square2SoundQueue = 0x00
    //> sta NoiseSoundQueue
    NoiseSoundQueue = 0x00
    //> sta PauseSoundQueue
    PauseSoundQueue = 0x00
    //> ldy DAC_Counter        ;load some sort of counter
    //> lda AreaMusicBuffer
    //> and #%00000011         ;check for specific music
    //> beq NoIncDAC
    if (!zeroFlag) {
        //> inc DAC_Counter        ;increment and check counter
        DAC_Counter = (DAC_Counter + 1) and 0xFF
        //> cpy #$30
        //> bcc StrWave            ;if not there yet, just store it
        if (Y >= 0x30) {
            //> NoIncDAC: tya
            //> beq StrWave            ;if we are at zero, do not decrement
            if (!zeroFlag) {
                //> dec DAC_Counter        ;decrement counter
                DAC_Counter = (DAC_Counter - 1) and 0xFF
            }
        }
    }
    if (!zeroFlag) {
    }
    //> StrWave:  sty SND_DELTA_REG+1    ;store into DMC load register (??)
    SND_DELTA_REG = DAC_Counter
    //> rts                    ;we are done here
    return
}

// Decompiled from Dump_Squ1_Regs
fun dumpSqu1Regs(X: Int, Y: Int) {
    //> Dump_Squ1_Regs:
    //> sty SND_SQUARE1_REG+1  ;dump the contents of X and Y into square 1's control regs
    SND_SQUARE1_REG = Y
    //> stx SND_SQUARE1_REG
    SND_SQUARE1_REG = X
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
    //> SetFreq_Squ1:
    //> ldx #$00               ;set frequency reg offset for square 1 sound channel
    //> Dump_Freq_Regs:
    //> tay
    //> lda FreqRegLookupTbl+1,y  ;use previous contents of A for sound reg offset
    //> beq NoTone                ;if zero, then do not load
    if (!zeroFlag) {
        //> sta SND_REGISTER+2,x      ;first byte goes into LSB of frequency divider
        SND_REGISTER[0x00] = FreqRegLookupTbl[A]
        //> lda FreqRegLookupTbl,y    ;second byte goes into 3 MSB plus extra bit for
        //> ora #%00001000            ;length counter
        //> sta SND_REGISTER+3,x
        SND_REGISTER[0x00] = FreqRegLookupTbl[A] or 0x08
    }
    //> NoTone: rts
    return
}

// Decompiled from Dump_Sq2_Regs
fun dumpSq2Regs(X: Int, Y: Int) {
    //> Dump_Sq2_Regs:
    //> stx SND_SQUARE2_REG    ;dump the contents of X and Y into square 2's control regs
    SND_SQUARE2_REG = X
    //> sty SND_SQUARE2_REG+1
    SND_SQUARE2_REG = Y
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
    //> Dump_Freq_Regs:
    //> tay
    //> lda FreqRegLookupTbl+1,y  ;use previous contents of A for sound reg offset
    //> beq NoTone                ;if zero, then do not load
    if (!zeroFlag) {
        //> sta SND_REGISTER+2,x      ;first byte goes into LSB of frequency divider
        SND_REGISTER[X] = FreqRegLookupTbl[A]
        //> lda FreqRegLookupTbl,y    ;second byte goes into 3 MSB plus extra bit for
        //> ora #%00001000            ;length counter
        //> sta SND_REGISTER+3,x
        SND_REGISTER[X] = FreqRegLookupTbl[A] or 0x08
    }
    //> NoTone: rts
    return
}

// Decompiled from SetFreq_Tri
fun setfreqTri(A: Int) {
    var X: Int = 0
    //> Dump_Freq_Regs:
    //> tay
    //> lda FreqRegLookupTbl+1,y  ;use previous contents of A for sound reg offset
    //> beq NoTone                ;if zero, then do not load
    if (!zeroFlag) {
        //> sta SND_REGISTER+2,x      ;first byte goes into LSB of frequency divider
        SND_REGISTER[X] = FreqRegLookupTbl[A]
        //> lda FreqRegLookupTbl,y    ;second byte goes into 3 MSB plus extra bit for
        //> ora #%00001000            ;length counter
        //> sta SND_REGISTER+3,x
        SND_REGISTER[X] = FreqRegLookupTbl[A] or 0x08
    }
    //> NoTone: rts
    return
}

// Decompiled from Square1SfxHandler
fun square1SfxHandler() {
    var A: Int = 0
    while (!carryFlag) {
        //> PlaySmallJump:
        //> lda #$26               ;branch here for small mario jumping sound
        //> bne JumpRegContents
        if (zeroFlag) {
            //> PlayBigJump:
            //> lda #$18               ;branch here for big mario jumping sound
        }
        //> JumpRegContents:
        //> ldx #$82               ;note that small and big jump borrow each others' reg contents
        //> ldy #$a7               ;anyway, this loads the first part of mario's jumping sound
        //> jsr PlaySqu1Sfx
        playSqu1Sfx()
        //> lda #$28               ;store length of sfx for both jumping sounds
        //> sta Squ1_SfxLenCounter ;then continue on here
        Squ1_SfxLenCounter = 0x28
        //> ContinueSndJump:
        //> lda Squ1_SfxLenCounter ;jumping sounds seem to be composed of three parts
        //> cmp #$25               ;check for time to play second part yet
        //> bne N2Prt
        if (Squ1_SfxLenCounter - 0x25 == 0) {
            //> ldx #$5f               ;load second part
            //> ldy #$f6
            //> bne DmpJpFPS           ;unconditional branch
            if (zeroFlag) {
                //> N2Prt:    cmp #$20               ;check for third part
                //> bne DecJpFPS
                if (A - 0x20 == 0) {
                    //> ldx #$48               ;load third part
                    //> FPS2nd:   ldy #$bc               ;the flagpole slide sound shares part of third part
                    //> DmpJpFPS: jsr Dump_Squ1_Regs
                    dumpSqu1Regs(0x48, 0xBC)
                    //> bne DecJpFPS           ;unconditional branch outta here
                    if (zeroFlag) {
                        //> PlayFireballThrow:
                        //> lda #$05
                        //> ldy #$99                 ;load reg contents for fireball throw sound
                        //> bne Fthrow               ;unconditional branch
                        if (zeroFlag) {
                            //> PlayBump:
                            //> lda #$0a                ;load length of sfx and reg contents for bump sound
                            //> ldy #$93
                        }
                        //> Fthrow:   ldx #$9e                ;the fireball sound shares reg contents with the bump sound
                        //> sta Squ1_SfxLenCounter
                        Squ1_SfxLenCounter = 0x0A
                        //> lda #$0c                ;load offset for bump sound
                        //> jsr PlaySqu1Sfx
                        playSqu1Sfx()
                        //> ContinueBumpThrow:
                        //> lda Squ1_SfxLenCounter  ;check for second part of bump sound
                        //> cmp #$06
                        //> bne DecJpFPS
                        if (Squ1_SfxLenCounter - 0x06 == 0) {
                            //> lda #$bb                ;load second part directly
                            //> sta SND_SQUARE1_REG+1
                            SND_SQUARE1_REG = 0xBB
                        }
                    }
                }
            }
        }
        if (A - 0x20 == 0) {
            if (zeroFlag) {
                if (zeroFlag) {
                }
                if (Squ1_SfxLenCounter - 0x06 == 0) {
                }
            }
        }
        //> DecJpFPS: bne BranchToDecLength1  ;unconditional branch
        if (zeroFlag) {
            //> Square1SfxHandler:
            //> ldy Square1SoundQueue   ;check for sfx in queue
            //> beq CheckSfx1Buffer
            if (!zeroFlag) {
                //> sty Square1SoundBuffer  ;if found, put in buffer
                Square1SoundBuffer = Square1SoundQueue
                //> bmi PlaySmallJump       ;small jump
                //> lsr Square1SoundQueue
                Square1SoundQueue = Square1SoundQueue shr 1
                //> bcs PlayBigJump         ;big jump
                do {
                    //> lsr Square1SoundQueue
                    Square1SoundQueue = Square1SoundQueue shr 1
                    //> bcs PlayBump            ;bump
                } while (carryFlag)
                //> lsr Square1SoundQueue
                Square1SoundQueue = Square1SoundQueue shr 1
                //> bcs PlaySwimStomp       ;swim/stomp
                if (!carryFlag) {
                    //> lsr Square1SoundQueue
                    Square1SoundQueue = Square1SoundQueue shr 1
                    //> bcs PlaySmackEnemy      ;smack enemy
                    if (!carryFlag) {
                        //> lsr Square1SoundQueue
                        Square1SoundQueue = Square1SoundQueue shr 1
                        //> bcs PlayPipeDownInj     ;pipedown/injury
                        if (!carryFlag) {
                            //> lsr Square1SoundQueue
                            Square1SoundQueue = Square1SoundQueue shr 1
                            //> bcs PlayFireballThrow   ;fireball throw
                            //> lsr Square1SoundQueue
                            Square1SoundQueue = Square1SoundQueue shr 1
                            //> bcs PlayFlagpoleSlide   ;slide flagpole
                            //> CheckSfx1Buffer:
                            //> lda Square1SoundBuffer   ;check for sfx in buffer
                            //> beq ExS1H                ;if not found, exit sub
                            if (!zeroFlag) {
                                //> bmi ContinueSndJump      ;small mario jump
                                //> lsr
                                //> bcs ContinueSndJump      ;big mario jump
                                //> lsr
                                //> bcs ContinueBumpThrow    ;bump
                                //> lsr
                                //> bcs ContinueSwimStomp    ;swim/stomp
                                if (!carryFlag) {
                                    //> lsr
                                    //> bcs ContinueSmackEnemy   ;smack enemy
                                    if (!carryFlag) {
                                        //> lsr
                                        //> bcs ContinuePipeDownInj  ;pipedown/injury
                                        if (!carryFlag) {
                                            //> lsr
                                            //> bcs ContinueBumpThrow    ;fireball throw
                                            //> lsr
                                            //> bcs DecrementSfx1Length  ;slide flagpole
                                            if (!carryFlag) {
                                                //> ExS1H: rts
                                                return
                                                //> PlaySwimStomp:
                                                //> lda #$0e               ;store length of swim/stomp sound
                                                //> sta Squ1_SfxLenCounter
                                                Squ1_SfxLenCounter = 0x0E
                                                //> ldy #$9c               ;store reg contents for swim/stomp sound
                                                //> ldx #$9e
                                                //> lda #$26
                                                //> jsr PlaySqu1Sfx
                                                playSqu1Sfx()
                                                //> ContinueSwimStomp:
                                                //> ldy Squ1_SfxLenCounter        ;look up reg contents in data section based on
                                                //> lda SwimStompEnvelopeData-1,y ;length of sound left, used to control sound's
                                                //> sta SND_SQUARE1_REG           ;envelope
                                                SND_SQUARE1_REG = SwimStompEnvelopeData[Squ1_SfxLenCounter]
                                                //> cpy #$06
                                                //> bne BranchToDecLength1
                                                if (Squ1_SfxLenCounter == 0x06) {
                                                    //> lda #$9e                      ;when the length counts down to a certain point, put this
                                                    //> sta SND_SQUARE1_REG+2         ;directly into the LSB of square 1's frequency divider
                                                    SND_SQUARE1_REG = 0x9E
                                                }
                                                //> BranchToDecLength1:
                                                //> bne DecrementSfx1Length  ;unconditional branch (regardless of how we got here)
                                                if (zeroFlag) {
                                                    //> PlaySmackEnemy:
                                                    //> lda #$0e                 ;store length of smack enemy sound
                                                    //> ldy #$cb
                                                    //> ldx #$9f
                                                    //> sta Squ1_SfxLenCounter
                                                    Squ1_SfxLenCounter = 0x0E
                                                    //> lda #$28                 ;store reg contents for smack enemy sound
                                                    //> jsr PlaySqu1Sfx
                                                    playSqu1Sfx()
                                                    //> bne DecrementSfx1Length  ;unconditional branch
                                                    if (zeroFlag) {
                                                        //> ContinueSmackEnemy:
                                                        //> ldy Squ1_SfxLenCounter  ;check about halfway through
                                                        //> cpy #$08
                                                        //> bne SmSpc
                                                        if (Squ1_SfxLenCounter == 0x08) {
                                                            //> lda #$a0                ;if we're at the about-halfway point, make the second tone
                                                            //> sta SND_SQUARE1_REG+2   ;in the smack enemy sound
                                                            SND_SQUARE1_REG = 0xA0
                                                            //> lda #$9f
                                                            //> bne SmTick
                                                            if (zeroFlag) {
                                                                //> SmSpc:  lda #$90                ;this creates spaces in the sound, giving it its distinct noise
                                                            }
                                                        }
                                                        //> SmTick: sta SND_SQUARE1_REG
                                                        SND_SQUARE1_REG = 0x90
                                                    }
                                                }
                                            }
                                            //> DecrementSfx1Length:
                                            //> dec Squ1_SfxLenCounter    ;decrement length of sfx
                                            Squ1_SfxLenCounter = (Squ1_SfxLenCounter - 1) and 0xFF
                                            //> bne ExSfx1
                                            if (zeroFlag) {
                                            }
                                            //> ExSfx1: rts
                                            return
                                            //> PlayPipeDownInj:
                                            //> lda #$2f                ;load length of pipedown sound
                                            //> sta Squ1_SfxLenCounter
                                            Squ1_SfxLenCounter = 0x2F
                                        }
                                    }
                                }
                            }
                            if (Squ1_SfxLenCounter == 0x06) {
                            }
                            if (zeroFlag) {
                                if (zeroFlag) {
                                    if (Squ1_SfxLenCounter == 0x08) {
                                        if (zeroFlag) {
                                        }
                                    }
                                }
                            }
                            if (zeroFlag) {
                            }
                        }
                    }
                }
            }
            if (!zeroFlag) {
                if (!carryFlag) {
                    if (!carryFlag) {
                        if (!carryFlag) {
                            if (!carryFlag) {
                                if (Squ1_SfxLenCounter == 0x06) {
                                }
                                if (zeroFlag) {
                                    if (zeroFlag) {
                                        if (Squ1_SfxLenCounter == 0x08) {
                                            if (zeroFlag) {
                                            }
                                        }
                                    }
                                }
                            }
                            if (zeroFlag) {
                            }
                        }
                    }
                }
            }
            if (Squ1_SfxLenCounter == 0x06) {
            }
        }
    }
    if (!carryFlag) {
        if (Squ1_SfxLenCounter == 0x06) {
        }
        if (zeroFlag) {
            if (zeroFlag) {
                if (Squ1_SfxLenCounter == 0x08) {
                    if (zeroFlag) {
                    }
                }
            }
        }
    }
    if (zeroFlag) {
    }
    //> ContinuePipeDownInj:
    //> lda Squ1_SfxLenCounter  ;some bitwise logic, forces the regs
    //> lsr                     ;to be written to only during six specific times
    //> bcs NoPDwnL             ;during which d3 must be set and d1-0 must be clear
    if (!carryFlag) {
        //> lsr
        //> bcs NoPDwnL
        if (!carryFlag) {
            //> and #%00000010
            //> beq NoPDwnL
            if (!zeroFlag) {
                //> ldy #$91                ;and this is where it actually gets written in
                //> ldx #$9a
                //> lda #$44
                //> jsr PlaySqu1Sfx
                playSqu1Sfx()
            }
        }
    }
    while (true) {
        //> NoPDwnL: jmp DecrementSfx1Length
    }
}

// Decompiled from StopSquare1Sfx
fun stopSquare1Sfx() {
    //> StopSquare1Sfx:
    //> ldx #$00                ;if end of sfx reached, clear buffer
    //> stx $f1                 ;and stop making the sfx
    zp_f1 = 0x00
    //> ldx #$0e
    //> stx SND_MASTERCTRL_REG
    SND_MASTERCTRL_REG = 0x0E
    //> ldx #$0f
    //> stx SND_MASTERCTRL_REG
    SND_MASTERCTRL_REG = 0x0F
    //> ExSfx1: rts
    return
}

// Decompiled from StopSquare2Sfx
fun stopSquare2Sfx() {
    //> StopSquare2Sfx:
    //> ldx #$0d                ;stop playing the sfx
    //> stx SND_MASTERCTRL_REG
    SND_MASTERCTRL_REG = 0x0D
    //> ldx #$0f
    //> stx SND_MASTERCTRL_REG
    SND_MASTERCTRL_REG = 0x0F
    //> ExSfx2: rts
    return
}

// Decompiled from Square2SfxHandler
fun square2SfxHandler() {
    //> PlayCoinGrab:
    //> lda #$35             ;load length of coin grab sound
    //> ldx #$8d             ;and part of reg contents
    //> bne CGrab_TTickRegL
    if (zeroFlag) {
        //> PlayTimerTick:
        //> lda #$06             ;load length of timer tick sound
        //> ldx #$98             ;and part of reg contents
    }
    //> CGrab_TTickRegL:
    //> sta Squ2_SfxLenCounter
    Squ2_SfxLenCounter = 0x06
    //> ldy #$7f                ;load the rest of reg contents
    //> lda #$42                ;of coin grab and timer tick sound
    //> jsr PlaySqu2Sfx
    playSqu2Sfx()
    //> ContinueCGrabTTick:
    //> lda Squ2_SfxLenCounter  ;check for time to play second tone yet
    //> cmp #$30                ;timer tick sound also executes this, not sure why
    //> bne N2Tone
    if (Squ2_SfxLenCounter - 0x30 == 0) {
        //> lda #$54                ;if so, load the tone directly into the reg
        //> sta SND_SQUARE2_REG+2
        SND_SQUARE2_REG = 0x54
    }
    //> N2Tone: bne DecrementSfx2Length
    if (zeroFlag) {
        //> PlayBlast:
        //> lda #$20                ;load length of fireworks/gunfire sound
        //> sta Squ2_SfxLenCounter
        Squ2_SfxLenCounter = 0x20
        //> ldy #$94                ;load reg contents of fireworks/gunfire sound
        //> lda #$5e
        //> bne SBlasJ
        if (zeroFlag) {
            //> ContinueBlast:
            //> lda Squ2_SfxLenCounter  ;check for time to play second part
            //> cmp #$18
            //> bne DecrementSfx2Length
            if (Squ2_SfxLenCounter - 0x18 == 0) {
                //> ldy #$93                ;load second part reg contents then
                //> lda #$18
                //> SBlasJ: bne BlstSJp             ;unconditional branch to load rest of reg contents
                if (zeroFlag) {
                    //> PlayPowerUpGrab:
                    //> lda #$36                    ;load length of power-up grab sound
                    //> sta Squ2_SfxLenCounter
                    Squ2_SfxLenCounter = 0x36
                    //> ContinuePowerUpGrab:
                    //> lda Squ2_SfxLenCounter      ;load frequency reg based on length left over
                    //> lsr                         ;divide by 2
                    //> bcs DecrementSfx2Length     ;alter frequency every other frame
                    if (!carryFlag) {
                        //> tay
                        //> lda PowerUpGrabFreqData-1,y ;use length left over / 2 for frequency offset
                        //> ldx #$5d                    ;store reg contents of power-up grab sound
                        //> ldy #$7f
                        //> LoadSqu2Regs:
                        //> jsr PlaySqu2Sfx
                        playSqu2Sfx()
                    }
                    //> DecrementSfx2Length:
                    //> dec Squ2_SfxLenCounter   ;decrement length of sfx
                    Squ2_SfxLenCounter = (Squ2_SfxLenCounter - 1) and 0xFF
                    //> bne ExSfx2
                    if (zeroFlag) {
                        //> EmptySfx2Buffer:
                        //> ldx #$00                ;initialize square 2's sound effects buffer
                        //> stx Square2SoundBuffer
                        Square2SoundBuffer = 0x00
                    }
                    //> ExSfx2: rts
                    return
                    //> Square2SfxHandler:
                    //> lda Square2SoundBuffer ;special handling for the 1-up sound to keep it
                    //> and #Sfx_ExtraLife     ;from being interrupted by other sounds on square 2
                    //> bne ContinueExtraLife
                    if (zeroFlag) {
                        //> ldy Square2SoundQueue  ;check for sfx in queue
                        //> beq CheckSfx2Buffer
                        if (!zeroFlag) {
                            //> sty Square2SoundBuffer ;if found, put in buffer and check for the following
                            Square2SoundBuffer = Square2SoundQueue
                            //> bmi PlayBowserFall     ;bowser fall
                            if (!negativeFlag) {
                                //> lsr Square2SoundQueue
                                Square2SoundQueue = Square2SoundQueue shr 1
                                //> bcs PlayCoinGrab       ;coin grab
                                //> lsr Square2SoundQueue
                                Square2SoundQueue = Square2SoundQueue shr 1
                                //> bcs PlayGrowPowerUp    ;power-up reveal
                                if (!carryFlag) {
                                    //> lsr Square2SoundQueue
                                    Square2SoundQueue = Square2SoundQueue shr 1
                                    //> bcs PlayGrowVine       ;vine grow
                                    if (!carryFlag) {
                                        //> lsr Square2SoundQueue
                                        Square2SoundQueue = Square2SoundQueue shr 1
                                        //> bcs PlayBlast          ;fireworks/gunfire
                                        //> lsr Square2SoundQueue
                                        Square2SoundQueue = Square2SoundQueue shr 1
                                        //> bcs PlayTimerTick      ;timer tick
                                        //> lsr Square2SoundQueue
                                        Square2SoundQueue = Square2SoundQueue shr 1
                                        //> bcs PlayPowerUpGrab    ;power-up grab
                                        //> lsr Square2SoundQueue
                                        Square2SoundQueue = Square2SoundQueue shr 1
                                        //> bcs PlayExtraLife      ;1-up
                                        if (!carryFlag) {
                                            //> CheckSfx2Buffer:
                                            //> lda Square2SoundBuffer   ;check for sfx in buffer
                                            //> beq ExS2H                ;if not found, exit sub
                                            if (!zeroFlag) {
                                                //> bmi ContinueBowserFall   ;bowser fall
                                                if (!negativeFlag) {
                                                    //> lsr
                                                    //> bcs Cont_CGrab_TTick     ;coin grab
                                                    if (!carryFlag) {
                                                        //> lsr
                                                        //> bcs ContinueGrowItems    ;power-up reveal
                                                        if (!carryFlag) {
                                                            //> lsr
                                                            //> bcs ContinueGrowItems    ;vine grow
                                                            if (!carryFlag) {
                                                                //> lsr
                                                                //> bcs ContinueBlast        ;fireworks/gunfire
                                                                //> lsr
                                                                //> bcs Cont_CGrab_TTick     ;timer tick
                                                                if (!carryFlag) {
                                                                    //> lsr
                                                                    //> bcs ContinuePowerUpGrab  ;power-up grab
                                                                    //> lsr
                                                                    //> bcs ContinueExtraLife    ;1-up
                                                                    if (!carryFlag) {
                                                                        //> ExS2H:  rts
                                                                        return
                                                                        while (true) {
                                                                            //> Cont_CGrab_TTick:
                                                                            //> jmp ContinueCGrabTTick
                                                                        }
                                                                        while (true) {
                                                                            //> JumpToDecLength2:
                                                                            //> jmp DecrementSfx2Length
                                                                        }
                                                                        //> PlayBowserFall:
                                                                        //> lda #$38                ;load length of bowser defeat sound
                                                                        //> sta Squ2_SfxLenCounter
                                                                        Squ2_SfxLenCounter = 0x38
                                                                        //> ldy #$c4                ;load contents of reg for bowser defeat sound
                                                                        //> lda #$18
                                                                        //> BlstSJp: bne PBFRegs
                                                                        if (zeroFlag) {
                                                                            //> ContinueBowserFall:
                                                                            //> lda Squ2_SfxLenCounter   ;check for almost near the end
                                                                            //> cmp #$08
                                                                            //> bne DecrementSfx2Length
                                                                            //> ldy #$a4                 ;if so, load the rest of reg contents for bowser defeat sound
                                                                            //> lda #$5a
                                                                        }
                                                                        //> PBFRegs:  ldx #$9f                 ;the fireworks/gunfire sound shares part of reg contents here
                                                                        //> EL_LRegs: bne LoadSqu2Regs         ;this is an unconditional branch outta here
                                                                        //> PlayExtraLife:
                                                                        //> lda #$30                  ;load length of 1-up sound
                                                                        //> sta Squ2_SfxLenCounter
                                                                        Squ2_SfxLenCounter = 0x30
                                                                    }
                                                                }
                                                                while (true) {
                                                                }
                                                                while (true) {
                                                                }
                                                                if (zeroFlag) {
                                                                }
                                                                while (zeroFlag) {
                                                                    //> ContinueExtraLife:
                                                                    //> lda Squ2_SfxLenCounter
                                                                    //> ldx #$03                  ;load new tones only every eight frames
                                                                    while (zeroFlag) {
                                                                        do {
                                                                            //> DivLLoop: lsr
                                                                            //> bcs JumpToDecLength2      ;if any bits set here, branch to dec the length
                                                                            //> dex
                                                                            //> bne DivLLoop              ;do this until all bits checked, if none set, continue
                                                                        } while (!zeroFlag)
                                                                    }
                                                                    do {
                                                                        //> tay
                                                                        //> lda ExtraLifeFreqData-1,y ;load our reg contents
                                                                        //> ldx #$82
                                                                        //> ldy #$7f
                                                                        //> bne EL_LRegs              ;unconditional branch
                                                                    } while (!zeroFlag)
                                                                }
                                                                //> PlayGrowPowerUp:
                                                                //> lda #$10                ;load length of power-up reveal sound
                                                                //> bne GrowItemRegs
                                                                if (zeroFlag) {
                                                                    //> PlayGrowVine:
                                                                    //> lda #$20                ;load length of vine grow sound
                                                                }
                                                                //> GrowItemRegs:
                                                                //> sta Squ2_SfxLenCounter
                                                                Squ2_SfxLenCounter = 0x20
                                                                //> lda #$7f                  ;load contents of reg for both sounds directly
                                                                //> sta SND_SQUARE2_REG+1
                                                                SND_SQUARE2_REG = 0x7F
                                                                //> lda #$00                  ;start secondary counter for both sounds
                                                                //> sta Sfx_SecondaryCounter
                                                                Sfx_SecondaryCounter = 0x00
                                                            }
                                                        }
                                                    }
                                                    while (true) {
                                                    }
                                                    while (true) {
                                                    }
                                                    if (zeroFlag) {
                                                    }
                                                }
                                            }
                                            while (true) {
                                            }
                                            while (true) {
                                            }
                                            if (zeroFlag) {
                                            }
                                        }
                                        while (zeroFlag) {
                                            do {
                                            } while (!zeroFlag)
                                        }
                                        do {
                                        } while (!zeroFlag)
                                        if (zeroFlag) {
                                        }
                                    }
                                }
                            }
                        }
                        if (!zeroFlag) {
                            if (!negativeFlag) {
                                if (!carryFlag) {
                                    if (!carryFlag) {
                                        if (!carryFlag) {
                                            if (!carryFlag) {
                                                if (!carryFlag) {
                                                    while (true) {
                                                    }
                                                    while (true) {
                                                    }
                                                    if (zeroFlag) {
                                                    }
                                                }
                                            }
                                            while (true) {
                                            }
                                            while (true) {
                                            }
                                            if (zeroFlag) {
                                            }
                                            while (zeroFlag) {
                                                while (zeroFlag) {
                                                    do {
                                                    } while (!zeroFlag)
                                                }
                                                do {
                                                } while (!zeroFlag)
                                            }
                                            if (zeroFlag) {
                                            }
                                        }
                                    }
                                }
                                while (true) {
                                }
                                while (true) {
                                }
                                if (zeroFlag) {
                                }
                            }
                        }
                        while (true) {
                        }
                        while (true) {
                        }
                        if (zeroFlag) {
                        }
                    }
                }
            }
        }
        if (zeroFlag) {
            if (!carryFlag) {
            }
            if (zeroFlag) {
            }
            if (zeroFlag) {
                if (!zeroFlag) {
                    if (!negativeFlag) {
                        if (!carryFlag) {
                            if (!carryFlag) {
                                if (!carryFlag) {
                                    if (!zeroFlag) {
                                        if (!negativeFlag) {
                                            if (!carryFlag) {
                                                if (!carryFlag) {
                                                    if (!carryFlag) {
                                                        if (!carryFlag) {
                                                            if (!carryFlag) {
                                                                while (true) {
                                                                }
                                                                while (true) {
                                                                }
                                                                if (zeroFlag) {
                                                                }
                                                            }
                                                        }
                                                        while (true) {
                                                        }
                                                        while (true) {
                                                        }
                                                        if (zeroFlag) {
                                                        }
                                                        while (zeroFlag) {
                                                            while (zeroFlag) {
                                                                do {
                                                                } while (!zeroFlag)
                                                            }
                                                            do {
                                                            } while (!zeroFlag)
                                                        }
                                                        if (zeroFlag) {
                                                        }
                                                    }
                                                }
                                            }
                                            while (true) {
                                            }
                                            while (true) {
                                            }
                                            if (zeroFlag) {
                                            }
                                        }
                                    }
                                    while (true) {
                                    }
                                    while (true) {
                                    }
                                    if (zeroFlag) {
                                    }
                                }
                                while (zeroFlag) {
                                    do {
                                    } while (!zeroFlag)
                                }
                                do {
                                } while (!zeroFlag)
                                if (zeroFlag) {
                                }
                            }
                        }
                    }
                }
                if (!zeroFlag) {
                    if (!negativeFlag) {
                        if (!carryFlag) {
                            if (!carryFlag) {
                                if (!carryFlag) {
                                    if (!carryFlag) {
                                        if (!carryFlag) {
                                            while (true) {
                                            }
                                            while (true) {
                                            }
                                            if (zeroFlag) {
                                            }
                                        }
                                    }
                                    while (true) {
                                    }
                                    while (true) {
                                    }
                                    if (zeroFlag) {
                                    }
                                    while (zeroFlag) {
                                        while (zeroFlag) {
                                            do {
                                            } while (!zeroFlag)
                                        }
                                        do {
                                        } while (!zeroFlag)
                                    }
                                    if (zeroFlag) {
                                    }
                                }
                            }
                        }
                        while (true) {
                        }
                        while (true) {
                        }
                        if (zeroFlag) {
                        }
                    }
                }
                while (true) {
                }
                while (true) {
                }
                if (zeroFlag) {
                }
            }
        }
    }
    if (zeroFlag) {
    }
    if (zeroFlag) {
        if (!zeroFlag) {
            if (!negativeFlag) {
                if (!carryFlag) {
                    if (!carryFlag) {
                        if (!carryFlag) {
                            if (!zeroFlag) {
                                if (!negativeFlag) {
                                    if (!carryFlag) {
                                        if (!carryFlag) {
                                            if (!carryFlag) {
                                                if (!carryFlag) {
                                                    if (!carryFlag) {
                                                        while (true) {
                                                        }
                                                        while (true) {
                                                        }
                                                        if (zeroFlag) {
                                                        }
                                                    }
                                                }
                                                while (true) {
                                                }
                                                while (true) {
                                                }
                                                if (zeroFlag) {
                                                }
                                                while (zeroFlag) {
                                                    while (zeroFlag) {
                                                        do {
                                                        } while (!zeroFlag)
                                                    }
                                                    do {
                                                    } while (!zeroFlag)
                                                }
                                                if (zeroFlag) {
                                                }
                                            }
                                        }
                                    }
                                    while (true) {
                                    }
                                    while (true) {
                                    }
                                    if (zeroFlag) {
                                    }
                                }
                            }
                            while (true) {
                            }
                            while (true) {
                            }
                            if (zeroFlag) {
                            }
                        }
                        while (zeroFlag) {
                            do {
                            } while (!zeroFlag)
                        }
                        do {
                        } while (!zeroFlag)
                        if (zeroFlag) {
                        }
                    }
                }
            }
        }
        if (!zeroFlag) {
            if (!negativeFlag) {
                if (!carryFlag) {
                    if (!carryFlag) {
                        if (!carryFlag) {
                            if (!carryFlag) {
                                if (!carryFlag) {
                                    while (true) {
                                    }
                                    while (true) {
                                    }
                                    if (zeroFlag) {
                                    }
                                }
                            }
                            while (true) {
                            }
                            while (true) {
                            }
                            if (zeroFlag) {
                            }
                            while (zeroFlag) {
                                while (zeroFlag) {
                                    do {
                                    } while (!zeroFlag)
                                }
                                do {
                                } while (!zeroFlag)
                            }
                            if (zeroFlag) {
                            }
                        }
                    }
                }
                while (true) {
                }
                while (true) {
                }
                if (zeroFlag) {
                }
            }
        }
        while (true) {
        }
        while (true) {
        }
        if (zeroFlag) {
        }
    }
    while (zeroFlag) {
        do {
        } while (!zeroFlag)
    }
    do {
    } while (!zeroFlag)
    if (zeroFlag) {
    }
    //> ContinueGrowItems:
    //> inc Sfx_SecondaryCounter  ;increment secondary counter for both sounds
    Sfx_SecondaryCounter = (Sfx_SecondaryCounter + 1) and 0xFF
    //> lda Sfx_SecondaryCounter  ;this sound doesn't decrement the usual counter
    //> lsr                       ;divide by 2 to get the offset
    //> tay
    //> cpy Squ2_SfxLenCounter    ;have we reached the end yet?
    //> beq StopGrowItems         ;if so, branch to jump, and stop playing sounds
    if (!(Sfx_SecondaryCounter shr 1 == Squ2_SfxLenCounter)) {
        //> lda #$9d                  ;load contents of other reg directly
        //> sta SND_SQUARE2_REG
        SND_SQUARE2_REG = 0x9D
        //> lda PUp_VGrow_FreqData,y  ;use secondary counter / 2 as offset for frequency regs
        //> jsr SetFreq_Squ2
        setfreqSqu2(PUp_VGrow_FreqData[Sfx_SecondaryCounter shr 1])
        //> rts
        return
    } else {
        while (true) {
            //> StopGrowItems:
            //> jmp EmptySfx2Buffer       ;branch to stop playing sounds
        }
    }
}

// Decompiled from NoiseSfxHandler
fun noiseSfxHandler() {
    //> PlayBrickShatter:
    //> lda #$20                 ;load length of brick shatter sound
    //> sta Noise_SfxLenCounter
    Noise_SfxLenCounter = 0x20
    //> ContinueBrickShatter:
    //> lda Noise_SfxLenCounter
    //> lsr                         ;divide by 2 and check for bit set to use offset
    //> bcc DecrementSfx3Length
    if (carryFlag) {
        //> tay
        //> ldx BrickShatterFreqData,y  ;load reg contents of brick shatter sound
        //> lda BrickShatterEnvData,y
        //> PlayNoiseSfx:
        //> sta SND_NOISE_REG        ;play the sfx
        SND_NOISE_REG = BrickShatterEnvData[Noise_SfxLenCounter shr 1]
        //> stx SND_NOISE_REG+2
        SND_NOISE_REG = BrickShatterFreqData[Noise_SfxLenCounter shr 1]
        //> lda #$18
        //> sta SND_NOISE_REG+3
        SND_NOISE_REG = 0x18
    }
    //> DecrementSfx3Length:
    //> dec Noise_SfxLenCounter  ;decrement length of sfx
    Noise_SfxLenCounter = (Noise_SfxLenCounter - 1) and 0xFF
    //> bne ExSfx3
    if (zeroFlag) {
        //> lda #$f0                 ;if done, stop playing the sfx
        //> sta SND_NOISE_REG
        SND_NOISE_REG = 0xF0
        //> lda #$00
        //> sta NoiseSoundBuffer
        NoiseSoundBuffer = 0x00
    }
    //> ExSfx3: rts
    return
}

// Decompiled from MusicHandler
fun musicHandler() {
    var A: Int = 0
    var X: Int = 0
    //> ContinueMusic:
    //> jmp HandleSquare2Music  ;if we have music, start with square 2 channel
    //> MusicHandler:
    //> lda EventMusicQueue     ;check event music queue
    //> bne LoadEventMusic
    if (zeroFlag) {
        //> lda AreaMusicQueue      ;check area music queue
        //> bne LoadAreaMusic
        if (zeroFlag) {
            do {
                //> lda EventMusicBuffer    ;check both buffers
                //> ora AreaMusicBuffer
                //> bne ContinueMusic
            } while (!zeroFlag)
            //> rts                     ;no music, then leave
            return
            //> LoadEventMusic:
            //> sta EventMusicBuffer      ;copy event music queue contents to buffer
            EventMusicBuffer = EventMusicBuffer or AreaMusicBuffer
            //> cmp #DeathMusic           ;is it death music?
            //> bne NoStopSfx             ;if not, jump elsewhere
            if (A - DeathMusic == 0) {
                //> jsr StopSquare1Sfx        ;stop sfx in square 1 and 2
                stopSquare1Sfx()
                //> jsr StopSquare2Sfx        ;but clear only square 1's sfx buffer
                stopSquare2Sfx()
            }
            //> NoStopSfx: ldx AreaMusicBuffer
            //> stx AreaMusicBuffer_Alt   ;save current area music buffer to be re-obtained later
            AreaMusicBuffer_Alt = AreaMusicBuffer
            //> ldy #$00
            //> sty NoteLengthTblAdder    ;default value for additional length byte offset
            NoteLengthTblAdder = 0x00
            //> sty AreaMusicBuffer       ;clear area music buffer
            AreaMusicBuffer = 0x00
            //> cmp #TimeRunningOutMusic  ;is it time running out music?
            //> bne FindEventMusicHeader
            if (A - TimeRunningOutMusic == 0) {
                //> ldx #$08                  ;load offset to be added to length byte of header
                //> stx NoteLengthTblAdder
                NoteLengthTblAdder = 0x08
                //> bne FindEventMusicHeader  ;unconditional branch
                if (zeroFlag) {
                    //> LoadAreaMusic:
                    //> cmp #$04                  ;is it underground music?
                    //> bne NoStop1               ;no, do not stop square 1 sfx
                    if (A - 0x04 == 0) {
                        //> jsr StopSquare1Sfx
                        stopSquare1Sfx()
                    }
                    //> NoStop1: ldy #$10                  ;start counter used only by ground level music
                    while (!zeroFlag) {
                        //> GMLoopB: sty GroundMusicHeaderOfs
                        GroundMusicHeaderOfs = 0x10
                        //> HandleAreaMusicLoopB:
                        //> ldy #$00                  ;clear event music buffer
                        //> sty EventMusicBuffer
                        EventMusicBuffer = 0x00
                        //> sta AreaMusicBuffer       ;copy area music queue contents to buffer
                        AreaMusicBuffer = EventMusicBuffer or AreaMusicBuffer
                        //> cmp #$01                  ;is it ground level music?
                        //> bne FindAreaMusicHeader
                        //> inc GroundMusicHeaderOfs  ;increment but only if playing ground level music
                        GroundMusicHeaderOfs = (GroundMusicHeaderOfs + 1) and 0xFF
                        //> ldy GroundMusicHeaderOfs  ;is it time to loopback ground level music?
                        //> cpy #$32
                        //> bne LoadHeader            ;branch ahead with alternate offset
                        //> ldy #$11
                        //> bne GMLoopB               ;unconditional branch
                    }
                    //> FindAreaMusicHeader:
                    //> ldy #$08                   ;load Y for offset of area music
                    //> sty MusicOffset_Square2    ;residual instruction here
                    MusicOffset_Square2 = 0x08
                }
            }
        }
    }
    if (A - DeathMusic == 0) {
    }
    if (A - TimeRunningOutMusic == 0) {
        if (zeroFlag) {
            if (A - 0x04 == 0) {
            }
            while (!zeroFlag) {
            }
        }
    }
    do {
        //> FindEventMusicHeader:
        //> iny                       ;increment Y pointer based on previously loaded queue contents
        //> lsr                       ;bit shift and increment until we find a set bit for music
        //> bcc FindEventMusicHeader
    } while (carryFlag)
    //> LoadHeader:
    //> lda MusicHeaderOffsetData,y  ;load offset for header
    //> tay
    //> lda MusicHeaderData,y        ;now load the header
    //> sta NoteLenLookupTblOfs
    NoteLenLookupTblOfs = MusicHeaderData[MusicHeaderOffsetData[(0x08 + 1) and 0xFF]]
    //> lda MusicHeaderData+1,y
    //> sta MusicDataLow
    MusicDataLow = MusicHeaderData[MusicHeaderOffsetData[(0x08 + 1) and 0xFF]]
    //> lda MusicHeaderData+2,y
    //> sta MusicDataHigh
    MusicDataHigh = MusicHeaderData[MusicHeaderOffsetData[(0x08 + 1) and 0xFF]]
    //> lda MusicHeaderData+3,y
    //> sta MusicOffset_Triangle
    MusicOffset_Triangle = MusicHeaderData[MusicHeaderOffsetData[(0x08 + 1) and 0xFF]]
    //> lda MusicHeaderData+4,y
    //> sta MusicOffset_Square1
    MusicOffset_Square1 = MusicHeaderData[MusicHeaderOffsetData[(0x08 + 1) and 0xFF]]
    //> lda MusicHeaderData+5,y
    //> sta MusicOffset_Noise
    MusicOffset_Noise = MusicHeaderData[MusicHeaderOffsetData[(0x08 + 1) and 0xFF]]
    //> sta NoiseDataLoopbackOfs
    NoiseDataLoopbackOfs = MusicHeaderData[MusicHeaderOffsetData[(0x08 + 1) and 0xFF]]
    //> lda #$01                     ;initialize music note counters
    //> sta Squ2_NoteLenCounter
    Squ2_NoteLenCounter = 0x01
    //> sta Squ1_NoteLenCounter
    Squ1_NoteLenCounter = 0x01
    //> sta Tri_NoteLenCounter
    Tri_NoteLenCounter = 0x01
    //> sta Noise_BeatLenCounter
    Noise_BeatLenCounter = 0x01
    //> lda #$00                     ;initialize music data offset for square 2
    //> sta MusicOffset_Square2
    MusicOffset_Square2 = 0x00
    //> sta AltRegContentFlag        ;initialize alternate control reg data used by square 1
    AltRegContentFlag = 0x00
    //> lda #$0b                     ;disable triangle channel and reenable it
    //> sta SND_MASTERCTRL_REG
    SND_MASTERCTRL_REG = 0x0B
    //> lda #$0f
    //> sta SND_MASTERCTRL_REG
    SND_MASTERCTRL_REG = 0x0F
    //> HandleSquare2Music:
    //> dec Squ2_NoteLenCounter  ;decrement square 2 note length
    Squ2_NoteLenCounter = (Squ2_NoteLenCounter - 1) and 0xFF
    //> bne MiscSqu2MusicTasks   ;is it time for more data?  if not, branch to end tasks
    if (zeroFlag) {
        //> ldy MusicOffset_Square2  ;increment square 2 music offset and fetch data
        //> inc MusicOffset_Square2
        MusicOffset_Square2 = (MusicOffset_Square2 + 1) and 0xFF
        //> lda (MusicData),y
        //> beq EndOfMusicData       ;if zero, the data is a null terminator
        if (!zeroFlag) {
            //> bpl Squ2NoteHandler      ;if non-negative, data is a note
            if (negativeFlag) {
                //> bne Squ2LengthHandler    ;otherwise it is length data
                if (zeroFlag) {
                    //> EndOfMusicData:
                    //> lda EventMusicBuffer     ;check secondary buffer for time running out music
                    //> cmp #TimeRunningOutMusic
                    //> bne NotTRO
                    if (EventMusicBuffer - TimeRunningOutMusic == 0) {
                        //> lda AreaMusicBuffer_Alt  ;load previously saved contents of primary buffer
                        //> bne MusicLoopBack        ;and start playing the song again if there is one
                        if (zeroFlag) {
                            //> NotTRO: and #VictoryMusic        ;check for victory music (the only secondary that loops)
                            //> bne VictoryMLoopBack
                            if (zeroFlag) {
                                //> lda AreaMusicBuffer      ;check primary buffer for any music except pipe intro
                                //> and #%01011111
                                //> bne MusicLoopBack        ;if any area music except pipe intro, music loops
                                if (zeroFlag) {
                                    //> lda #$00                 ;clear primary and secondary buffers and initialize
                                    //> sta AreaMusicBuffer      ;control regs of square and triangle channels
                                    AreaMusicBuffer = 0x00
                                    //> sta EventMusicBuffer
                                    EventMusicBuffer = 0x00
                                    //> sta SND_TRIANGLE_REG
                                    SND_TRIANGLE_REG = 0x00
                                    //> lda #$90
                                    //> sta SND_SQUARE1_REG
                                    SND_SQUARE1_REG = 0x90
                                    //> sta SND_SQUARE2_REG
                                    SND_SQUARE2_REG = 0x90
                                    //> rts
                                    return
                                }
                                while (true) {
                                    //> MusicLoopBack:
                                    //> jmp HandleAreaMusicLoopB
                                }
                            }
                        }
                    }
                    if (zeroFlag) {
                        if (zeroFlag) {
                        }
                        while (true) {
                        }
                    }
                    while (true) {
                        //> VictoryMLoopBack:
                        //> jmp LoadEventMusic
                    }
                }
                //> Squ2LengthHandler:
                //> jsr ProcessLengthData    ;store length of note
                processLengthData(0x90)
                //> sta Squ2_NoteLenBuffer
                Squ2_NoteLenBuffer = 0x90
                //> ldy MusicOffset_Square2  ;fetch another byte (MUST NOT BE LENGTH BYTE!)
                //> inc MusicOffset_Square2
                MusicOffset_Square2 = (MusicOffset_Square2 + 1) and 0xFF
                //> lda (MusicData),y
            }
        }
        if (EventMusicBuffer - TimeRunningOutMusic == 0) {
            if (zeroFlag) {
                if (zeroFlag) {
                    if (zeroFlag) {
                    }
                    while (true) {
                    }
                }
            }
        }
        if (zeroFlag) {
            if (zeroFlag) {
            }
            while (true) {
            }
        }
        while (true) {
        }
        //> Squ2NoteHandler:
        //> ldx Square2SoundBuffer     ;is there a sound playing on this channel?
        //> bne SkipFqL1
        if (zeroFlag) {
            //> jsr SetFreq_Squ2           ;no, then play the note
            setfreqSqu2(TODO)
            //> beq Rest                   ;check to see if note is rest
            if (!zeroFlag) {
                //> jsr LoadControlRegs        ;if not, load control regs for square 2
                loadControlRegs()
            }
            //> Rest:     sta Squ2_EnvelopeDataCtrl  ;save contents of A
            Squ2_EnvelopeDataCtrl = TODO
            //> jsr Dump_Sq2_Regs          ;dump X and Y into square 2 control regs
            dumpSq2Regs(Square2SoundBuffer, MusicOffset_Square2)
        }
        //> SkipFqL1: lda Squ2_NoteLenBuffer     ;save length in square 2 note counter
        //> sta Squ2_NoteLenCounter
        Squ2_NoteLenCounter = Squ2_NoteLenBuffer
    }
    //> MiscSqu2MusicTasks:
    //> lda Square2SoundBuffer     ;is there a sound playing on square 2?
    //> bne HandleSquare1Music
    if (zeroFlag) {
        //> lda EventMusicBuffer       ;check for death music or d4 set on secondary buffer
        //> and #%10010001             ;note that regs for death music or d4 are loaded by default
        //> bne HandleSquare1Music
        if (zeroFlag) {
            //> ldy Squ2_EnvelopeDataCtrl  ;check for contents saved from LoadControlRegs
            //> beq NoDecEnv1
            if (!zeroFlag) {
                //> dec Squ2_EnvelopeDataCtrl  ;decrement unless already zero
                Squ2_EnvelopeDataCtrl = (Squ2_EnvelopeDataCtrl - 1) and 0xFF
            }
            //> NoDecEnv1: jsr LoadEnvelopeData       ;do a load of envelope data to replace default
            loadEnvelopeData(Squ2_EnvelopeDataCtrl)
            //> sta SND_SQUARE2_REG        ;based on offset set by first load unless playing
            SND_SQUARE2_REG = EventMusicBuffer and 0x91
            //> ldx #$7f                   ;death music or d4 set on secondary buffer
            //> stx SND_SQUARE2_REG+1
            SND_SQUARE2_REG = 0x7F
        }
    }
    //> HandleSquare1Music:
    //> ldy MusicOffset_Square1    ;is there a nonzero offset here?
    //> beq HandleTriangleMusic    ;if not, skip ahead to the triangle channel
    if (!zeroFlag) {
        //> dec Squ1_NoteLenCounter    ;decrement square 1 note length
        Squ1_NoteLenCounter = (Squ1_NoteLenCounter - 1) and 0xFF
        //> bne MiscSqu1MusicTasks     ;is it time for more data?
        if (zeroFlag) {
            while (zeroFlag) {
                do {
                    //> FetchSqu1MusicData:
                    //> ldy MusicOffset_Square1    ;increment square 1 music offset and fetch data
                    //> inc MusicOffset_Square1
                    MusicOffset_Square1 = (MusicOffset_Square1 + 1) and 0xFF
                    //> lda (MusicData),y
                    //> bne Squ1NoteHandler        ;if nonzero, then skip this part
                    //> lda #$83
                    //> sta SND_SQUARE1_REG        ;store some data into control regs for square 1
                    SND_SQUARE1_REG = 0x83
                    //> lda #$94                   ;and fetch another byte of data, used to give
                    //> sta SND_SQUARE1_REG+1      ;death music its unique sound
                    SND_SQUARE1_REG = 0x94
                    //> sta AltRegContentFlag
                    AltRegContentFlag = 0x94
                    //> bne FetchSqu1MusicData     ;unconditional branch
                } while (!zeroFlag)
            }
            //> Squ1NoteHandler:
            //> jsr AlternateLengthHandler
            alternateLengthHandler(0x94)
            //> sta Squ1_NoteLenCounter    ;save contents of A in square 1 note counter
            Squ1_NoteLenCounter = 0x94
            //> ldy Square1SoundBuffer     ;is there a sound playing on square 1?
            //> bne HandleTriangleMusic
            if (zeroFlag) {
                //> txa
                //> and #%00111110             ;change saved data to appropriate note format
                //> jsr SetFreq_Squ1           ;play the note
                setfreqSqu1(0x7F and 0x3E)
                //> beq SkipCtrlL
                if (!zeroFlag) {
                    //> jsr LoadControlRegs
                    loadControlRegs()
                }
                //> SkipCtrlL: sta Squ1_EnvelopeDataCtrl  ;save envelope offset
                Squ1_EnvelopeDataCtrl = 0x7F and 0x3E
                //> jsr Dump_Squ1_Regs
                dumpSqu1Regs(0x7F, Square1SoundBuffer)
                //> MiscSqu1MusicTasks:
                //> lda Square1SoundBuffer     ;is there a sound playing on square 1?
                //> bne HandleTriangleMusic
                if (zeroFlag) {
                    //> lda EventMusicBuffer       ;check for death music or d4 set on secondary buffer
                    //> and #%10010001
                    //> bne DeathMAltReg
                    if (zeroFlag) {
                        //> ldy Squ1_EnvelopeDataCtrl  ;check saved envelope offset
                        //> beq NoDecEnv2
                        if (!zeroFlag) {
                            //> dec Squ1_EnvelopeDataCtrl  ;decrement unless already zero
                            Squ1_EnvelopeDataCtrl = (Squ1_EnvelopeDataCtrl - 1) and 0xFF
                        }
                        //> NoDecEnv2:    jsr LoadEnvelopeData       ;do a load of envelope data
                        loadEnvelopeData(Squ1_EnvelopeDataCtrl)
                        //> sta SND_SQUARE1_REG        ;based on offset set by first load
                        SND_SQUARE1_REG = EventMusicBuffer and 0x91
                    }
                    //> DeathMAltReg: lda AltRegContentFlag      ;check for alternate control reg data
                    //> bne DoAltLoad
                    if (zeroFlag) {
                        //> lda #$7f                   ;load this value if zero, the alternate value
                    }
                    //> DoAltLoad:    sta SND_SQUARE1_REG+1      ;if nonzero, and let's move on
                    SND_SQUARE1_REG = 0x7F
                }
            }
        }
        if (zeroFlag) {
            if (zeroFlag) {
                if (!zeroFlag) {
                }
            }
            if (zeroFlag) {
            }
        }
    }
    //> HandleTriangleMusic:
    //> lda MusicOffset_Triangle
    //> dec Tri_NoteLenCounter    ;decrement triangle note length
    Tri_NoteLenCounter = (Tri_NoteLenCounter - 1) and 0xFF
    //> bne HandleNoiseMusic      ;is it time for more data?
    if (zeroFlag) {
        //> ldy MusicOffset_Triangle  ;increment square 1 music offset and fetch data
        //> inc MusicOffset_Triangle
        MusicOffset_Triangle = (MusicOffset_Triangle + 1) and 0xFF
        //> lda (MusicData),y
        //> beq LoadTriCtrlReg        ;if zero, skip all this and move on to noise
        if (!zeroFlag) {
            //> bpl TriNoteHandler        ;if non-negative, data is note
            if (negativeFlag) {
                //> jsr ProcessLengthData     ;otherwise, it is length data
                processLengthData(TODO)
                //> sta Tri_NoteLenBuffer     ;save contents of A
                Tri_NoteLenBuffer = TODO
                //> lda #$1f
                //> sta SND_TRIANGLE_REG      ;load some default data for triangle control reg
                SND_TRIANGLE_REG = 0x1F
                //> ldy MusicOffset_Triangle  ;fetch another byte
                //> inc MusicOffset_Triangle
                MusicOffset_Triangle = (MusicOffset_Triangle + 1) and 0xFF
                //> lda (MusicData),y
                //> beq LoadTriCtrlReg        ;check once more for nonzero data
                if (!zeroFlag) {
                    //> TriNoteHandler:
                    //> jsr SetFreq_Tri
                    setfreqTri(TODO)
                    //> ldx Tri_NoteLenBuffer   ;save length in triangle note counter
                    //> stx Tri_NoteLenCounter
                    Tri_NoteLenCounter = Tri_NoteLenBuffer
                    //> lda EventMusicBuffer
                    //> and #%01101110          ;check for death music or d4 set on secondary buffer
                    //> bne NotDOrD4            ;if playing any other secondary, skip primary buffer check
                    if (zeroFlag) {
                        //> lda AreaMusicBuffer     ;check primary buffer for water or castle level music
                        //> and #%00001010
                        //> beq HandleNoiseMusic    ;if playing any other primary, or death or d4, go on to noise routine
                        if (!zeroFlag) {
                            //> NotDOrD4: txa                     ;if playing water or castle music or any secondary
                            //> cmp #$12                ;besides death music or d4 set, check length of note
                            //> bcs LongN
                            if (!(X >= 0x12)) {
                                //> lda EventMusicBuffer    ;check for win castle music again if not playing a long note
                                //> and #EndOfCastleMusic
                                //> beq MediN
                                if (!zeroFlag) {
                                    //> lda #$0f                ;load value $0f if playing the win castle music and playing a short
                                    //> bne LoadTriCtrlReg      ;note, load value $1f if playing water or castle level music or any
                                    if (zeroFlag) {
                                        //> MediN:    lda #$1f                ;secondary besides death and d4 except win castle or win castle and playing
                                        //> bne LoadTriCtrlReg      ;a short note, and load value $ff if playing a long note on water, castle
                                        if (zeroFlag) {
                                            //> LongN:    lda #$ff                ;or any secondary (including win castle) except death and d4
                                        }
                                    }
                                }
                                if (zeroFlag) {
                                }
                            }
                            //> LoadTriCtrlReg:
                            //> sta SND_TRIANGLE_REG      ;save final contents of A into control reg for triangle
                            SND_TRIANGLE_REG = 0xFF
                        }
                    }
                    if (!(X >= 0x12)) {
                        if (!zeroFlag) {
                            if (zeroFlag) {
                                if (zeroFlag) {
                                }
                            }
                        }
                        if (zeroFlag) {
                        }
                    }
                }
            }
            if (zeroFlag) {
                if (!zeroFlag) {
                    if (!(X >= 0x12)) {
                        if (!zeroFlag) {
                            if (zeroFlag) {
                                if (zeroFlag) {
                                }
                            }
                        }
                        if (zeroFlag) {
                        }
                    }
                }
            }
            if (!(X >= 0x12)) {
                if (!zeroFlag) {
                    if (zeroFlag) {
                        if (zeroFlag) {
                        }
                    }
                }
                if (zeroFlag) {
                }
            }
        }
    }
    //> HandleNoiseMusic:
    //> lda AreaMusicBuffer       ;check if playing underground or castle music
    //> and #%11110011
    //> beq ExitMusicHandler      ;if so, skip the noise routine
    if (!zeroFlag) {
        //> dec Noise_BeatLenCounter  ;decrement noise beat length
        Noise_BeatLenCounter = (Noise_BeatLenCounter - 1) and 0xFF
        //> bne ExitMusicHandler      ;is it time for more data?
        if (zeroFlag) {
            while (zeroFlag) {
                do {
                    //> FetchNoiseBeatData:
                    //> ldy MusicOffset_Noise       ;increment noise beat offset and fetch data
                    //> inc MusicOffset_Noise
                    MusicOffset_Noise = (MusicOffset_Noise + 1) and 0xFF
                    //> lda (MusicData),y           ;get noise beat data, if nonzero, branch to handle
                    //> bne NoiseBeatHandler
                    //> lda NoiseDataLoopbackOfs    ;if data is zero, reload original noise beat offset
                    //> sta MusicOffset_Noise       ;and loopback next time around
                    MusicOffset_Noise = NoiseDataLoopbackOfs
                    //> bne FetchNoiseBeatData      ;unconditional branch
                } while (!zeroFlag)
            }
            //> NoiseBeatHandler:
            //> jsr AlternateLengthHandler
            alternateLengthHandler(NoiseDataLoopbackOfs)
            //> sta Noise_BeatLenCounter    ;store length in noise beat counter
            Noise_BeatLenCounter = NoiseDataLoopbackOfs
            //> txa
            //> and #%00111110              ;reload data and erase length bits
            //> beq SilentBeat              ;if no beat data, silence
            if (!zeroFlag) {
                //> cmp #$30                    ;check the beat data and play the appropriate
                //> beq LongBeat                ;noise accordingly
                if (!(A - 0x30 == 0)) {
                    //> cmp #$20
                    //> beq StrongBeat
                    if (!(A - 0x20 == 0)) {
                        //> and #%00010000
                        //> beq SilentBeat
                        if (!zeroFlag) {
                            //> lda #$1c        ;short beat data
                            //> ldx #$03
                            //> ldy #$18
                            //> bne PlayBeat
                            if (zeroFlag) {
                                //> StrongBeat:
                                //> lda #$1c        ;strong beat data
                                //> ldx #$0c
                                //> ldy #$18
                                //> bne PlayBeat
                                if (zeroFlag) {
                                    //> LongBeat:
                                    //> lda #$1c        ;long beat data
                                    //> ldx #$03
                                    //> ldy #$58
                                    //> bne PlayBeat
                                    if (zeroFlag) {
                                        //> SilentBeat:
                                        //> lda #$10        ;silence
                                    }
                                }
                            }
                        }
                    }
                    if (zeroFlag) {
                        if (zeroFlag) {
                        }
                    }
                }
                if (zeroFlag) {
                }
            }
            //> PlayBeat:
            //> sta SND_NOISE_REG    ;load beat data into noise regs
            SND_NOISE_REG = 0x10
            //> stx SND_NOISE_REG+2
            SND_NOISE_REG = 0x03
            //> sty SND_NOISE_REG+3
            SND_NOISE_REG = 0x58
        }
    }
    //> ExitMusicHandler:
    //> rts
    return
}

// Decompiled from AlternateLengthHandler
fun alternateLengthHandler(A: Int): Int {
    //> AlternateLengthHandler:
    //> tax            ;save a copy of original byte into X
    //> ror            ;save LSB from original byte into carry
    //> txa            ;reload original byte and rotate three times
    //> rol            ;turning xx00000x into 00000xxx, with the
    //> rol            ;bit in carry as the MSB here
    //> rol
}

// Decompiled from ProcessLengthData
fun processLengthData(A: Int): Int {
    //> ProcessLengthData:
    //> and #%00000111              ;clear all but the three LSBs
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
    //> LoadControlRegs:
    //> lda EventMusicBuffer  ;check secondary buffer for win castle music
    //> and #EndOfCastleMusic
    //> beq NotECstlM
    if (!zeroFlag) {
        //> lda #$04              ;this value is only used for win castle music
        //> bne AllMus            ;unconditional branch
        if (zeroFlag) {
            //> NotECstlM: lda AreaMusicBuffer
            //> and #%01111101        ;check primary buffer for water music
            //> beq WaterMus
            if (!zeroFlag) {
                //> lda #$08              ;this is the default value for all other music
                //> bne AllMus
                if (zeroFlag) {
                    //> WaterMus:  lda #$28              ;this value is used for water music and all other event music
                }
            }
        }
    }
    if (!zeroFlag) {
        if (zeroFlag) {
        }
    }
    //> AllMus:    ldx #$82              ;load contents of other sound regs for square 2
    //> ldy #$7f
    //> rts
    return
}

// Decompiled from LoadEnvelopeData
fun loadEnvelopeData(Y: Int): Int {
    //> LoadEnvelopeData:
    //> lda EventMusicBuffer           ;check secondary buffer for win castle music
    //> and #EndOfCastleMusic
    //> beq LoadUsualEnvData
    if (!zeroFlag) {
        //> lda EndOfCastleMusicEnvData,y  ;load data from offset for win castle music
        //> rts
        return A
    } else {
        //> LoadUsualEnvData:
        //> lda AreaMusicBuffer            ;check primary buffer for water music
        //> and #%01111101
        //> beq LoadWaterEventMusEnvData
        if (!zeroFlag) {
            //> lda AreaMusicEnvData,y         ;load default data from offset for all other music
            //> rts
            return A
        }
    }
    //> LoadWaterEventMusEnvData:
    //> lda WaterEventMusEnvData,y     ;load data from offset for water music and all other event music
    //> rts
    return A
}

