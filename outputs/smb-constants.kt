@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.smb

import com.ivieleague.decompiler6502tokotlin.hand.MemoryByte

// Memory address variables from smbdism.asm
// Each variable delegates to a specific memory location using property delegates
// Access: `operMode.toInt()` reads from memory[0x0770]
// Store: `operMode = 5u` writes to memory[0x0770]

const val A_B_Buttons = 0x0A
var aBButtons by MemoryByte(A_B_Buttons)
const val A_Button = 0x80
var aButton by MemoryByte(A_Button)
const val AirBubbleTimer = 0x0792
var airBubbleTimer by MemoryByte(AirBubbleTimer)
const val AltEntranceControl = 0x0752
var altEntranceControl by MemoryByte(AltEntranceControl)
const val AltGameOverMusic = 0x10
var altGameOverMusic by MemoryByte(AltGameOverMusic)
const val AltRegContentFlag = 0x07CA
var altRegContentFlag by MemoryByte(AltRegContentFlag)
const val Alt_SprDataOffset = 0x06EC
var altSprDataOffset by MemoryByte(Alt_SprDataOffset)
const val AreaAddrsLOffset = 0x074F
var areaAddrsLOffset by MemoryByte(AreaAddrsLOffset)
const val AreaData = 0xE7
var areaData by MemoryByte(AreaData)
const val AreaDataHigh = 0xE8
var areaDataHigh by MemoryByte(AreaDataHigh)
const val AreaDataLow = 0xE7
var areaDataLow by MemoryByte(AreaDataLow)
const val AreaDataOffset = 0x072C
var areaDataOffset by MemoryByte(AreaDataOffset)
const val AreaMusicBuffer = 0xF4
var areaMusicBuffer by MemoryByte(AreaMusicBuffer)
const val AreaMusicBuffer_Alt = 0x07C5
var areaMusicBufferAlt by MemoryByte(AreaMusicBuffer_Alt)
const val AreaMusicQueue = 0xFB
var areaMusicQueue by MemoryByte(AreaMusicQueue)
const val AreaNumber = 0x0760
var areaNumber by MemoryByte(AreaNumber)
const val AreaObjOffsetBuffer = 0x072D
var areaObjOffsetBuffer by MemoryByte(AreaObjOffsetBuffer)
const val AreaObjectHeight = 0x0735
var areaObjectHeight by MemoryByte(AreaObjectHeight)
const val AreaObjectLength = 0x0730
var areaObjectLength by MemoryByte(AreaObjectLength)
const val AreaObjectPageLoc = 0x072A
var areaObjectPageLoc by MemoryByte(AreaObjectPageLoc)
const val AreaObjectPageSel = 0x072B
var areaObjectPageSel by MemoryByte(AreaObjectPageSel)
const val AreaParserTaskNum = 0x071F
var areaParserTaskNum by MemoryByte(AreaParserTaskNum)
const val AreaPointer = 0x0750
var areaPointer by MemoryByte(AreaPointer)
const val AreaStyle = 0x0733
var areaStyle by MemoryByte(AreaStyle)
const val AreaType = 0x074E
var areaType by MemoryByte(AreaType)
const val AttributeBuffer = 0x03F9
var attributeBuffer by MemoryByte(AttributeBuffer)
const val BBill_CCheep_Frenzy = 0x17
var bBillCCheepFrenzy by MemoryByte(BBill_CCheep_Frenzy)
const val B_Button = 0x40
var bButton by MemoryByte(B_Button)
const val BackgroundColorCtrl = 0x0744
var backgroundColorCtrl by MemoryByte(BackgroundColorCtrl)
const val BackgroundScenery = 0x0742
var backgroundScenery by MemoryByte(BackgroundScenery)
const val BackloadingFlag = 0x0728
var backloadingFlag by MemoryByte(BackloadingFlag)
const val BalPlatformAlignment = 0x03A0
var balPlatformAlignment by MemoryByte(BalPlatformAlignment)
const val BehindAreaParserFlag = 0x0729
var behindAreaParserFlag by MemoryByte(BehindAreaParserFlag)
const val BitMFilter = 0x06DD
var bitMFilter by MemoryByte(BitMFilter)
const val BlockBounceTimer = 0x0784
var blockBounceTimer by MemoryByte(BlockBounceTimer)
const val BlockBufferColumnPos = 0x06A0
var blockBufferColumnPos by MemoryByte(BlockBufferColumnPos)
const val Block_BBuf_Low = 0x03E6
var blockBBufLow by MemoryByte(Block_BBuf_Low)
const val Block_Buffer_1 = 0x0500
var blockBuffer1 by MemoryByte(Block_Buffer_1)
const val Block_Buffer_2 = 0x05D0
var blockBuffer2 by MemoryByte(Block_Buffer_2)
const val Block_Metatile = 0x03E8
var blockMetatile by MemoryByte(Block_Metatile)
const val Block_OffscreenBits = 0x03D4
var blockOffscreenBits by MemoryByte(Block_OffscreenBits)
const val Block_Orig_XPos = 0x03F1
var blockOrigXPos by MemoryByte(Block_Orig_XPos)
const val Block_Orig_YPos = 0x03E4
var blockOrigYPos by MemoryByte(Block_Orig_YPos)
const val Block_PageLoc = 0x76
var blockPageLoc by MemoryByte(Block_PageLoc)
const val Block_PageLoc2 = 0x03EA
var blockPageLoc2 by MemoryByte(Block_PageLoc2)
const val Block_Rel_XPos = 0x03B1
var blockRelXPos by MemoryByte(Block_Rel_XPos)
const val Block_Rel_YPos = 0x03BC
var blockRelYPos by MemoryByte(Block_Rel_YPos)
const val Block_RepFlag = 0x03EC
var blockRepFlag by MemoryByte(Block_RepFlag)
const val Block_ResidualCounter = 0x03F0
var blockResidualCounter by MemoryByte(Block_ResidualCounter)
const val Block_SprDataOffset = 0x06EC
var blockSprDataOffset by MemoryByte(Block_SprDataOffset)
const val Block_State = 0x26
var blockState by MemoryByte(Block_State)
const val Block_X_Position = 0x8F
var blockXPosition by MemoryByte(Block_X_Position)
const val Block_X_Speed = 0x60
var blockXSpeed by MemoryByte(Block_X_Speed)
const val Block_Y_HighPos = 0xBE
var blockYHighPos by MemoryByte(Block_Y_HighPos)
const val Block_Y_MoveForce = 0x043C
var blockYMoveForce by MemoryByte(Block_Y_MoveForce)
const val Block_Y_Position = 0xD7
var blockYPosition by MemoryByte(Block_Y_Position)
const val Block_Y_Speed = 0xA8
var blockYSpeed by MemoryByte(Block_Y_Speed)
const val Bloober = 0x07
var bloober by MemoryByte(Bloober)
const val BlooperMoveCounter = 0xA0
var blooperMoveCounter by MemoryByte(BlooperMoveCounter)
const val BlooperMoveSpeed = 0x58
var blooperMoveSpeed by MemoryByte(BlooperMoveSpeed)
const val BoundingBox_DR_XPos = 0x04AE
var boundingBoxDRXPos by MemoryByte(BoundingBox_DR_XPos)
const val BoundingBox_DR_YPos = 0x04AF
var boundingBoxDRYPos by MemoryByte(BoundingBox_DR_YPos)
const val BoundingBox_LR_Corner = 0x04AE
var boundingBoxLRCorner by MemoryByte(BoundingBox_LR_Corner)
const val BoundingBox_UL_Corner = 0x04AC
var boundingBoxULCorner by MemoryByte(BoundingBox_UL_Corner)
const val BoundingBox_UL_XPos = 0x04AC
var boundingBoxULXPos by MemoryByte(BoundingBox_UL_XPos)
const val BoundingBox_UL_YPos = 0x04AD
var boundingBoxULYPos by MemoryByte(BoundingBox_UL_YPos)
const val Bowser = 0x2D
var bowser by MemoryByte(Bowser)
const val BowserBodyControls = 0x0363
var bowserBodyControls by MemoryByte(BowserBodyControls)
const val BowserFeetCounter = 0x0364
var bowserFeetCounter by MemoryByte(BowserFeetCounter)
const val BowserFireBreathTimer = 0x0790
var bowserFireBreathTimer by MemoryByte(BowserFireBreathTimer)
const val BowserFlame = 0x15
var bowserFlame by MemoryByte(BowserFlame)
const val BowserFlamePRandomOfs = 0x0417
var bowserFlamePRandomOfs by MemoryByte(BowserFlamePRandomOfs)
const val BowserFlameTimerCtrl = 0x0367
var bowserFlameTimerCtrl by MemoryByte(BowserFlameTimerCtrl)
const val BowserFront_Offset = 0x0368
var bowserFrontOffset by MemoryByte(BowserFront_Offset)
const val BowserGfxFlag = 0x036A
var bowserGfxFlag by MemoryByte(BowserGfxFlag)
const val BowserHitPoints = 0x0483
var bowserHitPoints by MemoryByte(BowserHitPoints)
const val BowserMovementSpeed = 0x0365
var bowserMovementSpeed by MemoryByte(BowserMovementSpeed)
const val BowserOrigXPos = 0x0366
var bowserOrigXPos by MemoryByte(BowserOrigXPos)
const val BrickCoinTimer = 0x079D
var brickCoinTimer by MemoryByte(BrickCoinTimer)
const val BrickCoinTimerFlag = 0x06BC
var brickCoinTimerFlag by MemoryByte(BrickCoinTimerFlag)
const val BridgeCollapseOffset = 0x0369
var bridgeCollapseOffset by MemoryByte(BridgeCollapseOffset)
const val Bubble_OffscreenBits = 0x03D3
var bubbleOffscreenBits by MemoryByte(Bubble_OffscreenBits)
const val Bubble_PageLoc = 0x83
var bubblePageLoc by MemoryByte(Bubble_PageLoc)
const val Bubble_Rel_XPos = 0x03B0
var bubbleRelXPos by MemoryByte(Bubble_Rel_XPos)
const val Bubble_Rel_YPos = 0x03BB
var bubbleRelYPos by MemoryByte(Bubble_Rel_YPos)
const val Bubble_SprDataOffset = 0x06EE
var bubbleSprDataOffset by MemoryByte(Bubble_SprDataOffset)
const val Bubble_X_Position = 0x9C
var bubbleXPosition by MemoryByte(Bubble_X_Position)
const val Bubble_YMF_Dummy = 0x042C
var bubbleYMFDummy by MemoryByte(Bubble_YMF_Dummy)
const val Bubble_Y_HighPos = 0xCB
var bubbleYHighPos by MemoryByte(Bubble_Y_HighPos)
const val Bubble_Y_Position = 0xE4
var bubbleYPosition by MemoryByte(Bubble_Y_Position)
const val BulletBill_CannonVar = 0x33
var bulletBillCannonVar by MemoryByte(BulletBill_CannonVar)
const val BulletBill_FrenzyVar = 0x08
var bulletBillFrenzyVar by MemoryByte(BulletBill_FrenzyVar)
const val BuzzyBeetle = 0x02
var buzzyBeetle by MemoryByte(BuzzyBeetle)
const val Cannon_Offset = 0x046A
var cannonOffset by MemoryByte(Cannon_Offset)
const val Cannon_PageLoc = 0x046B
var cannonPageLoc by MemoryByte(Cannon_PageLoc)
const val Cannon_Timer = 0x047D
var cannonTimer by MemoryByte(Cannon_Timer)
const val Cannon_X_Position = 0x0471
var cannonXPosition by MemoryByte(Cannon_X_Position)
const val Cannon_Y_Position = 0x0477
var cannonYPosition by MemoryByte(Cannon_Y_Position)
const val CastleMusic = 0x08
var castleMusic by MemoryByte(CastleMusic)
const val ChangeAreaTimer = 0x06DE
var changeAreaTimer by MemoryByte(ChangeAreaTimer)
const val CheepCheepMoveMFlag = 0x58
var cheepCheepMoveMFlag by MemoryByte(CheepCheepMoveMFlag)
const val CheepCheepOrigYPos = 0x0434
var cheepCheepOrigYPos by MemoryByte(CheepCheepOrigYPos)
const val ClimbSideTimer = 0x0789
var climbSideTimer by MemoryByte(ClimbSideTimer)
const val CloudMusic = 0x10
var cloudMusic by MemoryByte(CloudMusic)
const val CloudTypeOverride = 0x0743
var cloudTypeOverride by MemoryByte(CloudTypeOverride)
const val CoinTally = 0x075E
var coinTally by MemoryByte(CoinTally)
const val CoinTallyFor1Ups = 0x0748
var coinTallyFor1Ups by MemoryByte(CoinTallyFor1Ups)
const val ColorRotateOffset = 0x06D4
var colorRotateOffset by MemoryByte(ColorRotateOffset)
const val ColumnSets = 0x071E
var columnSets by MemoryByte(ColumnSets)
const val ContinueWorld = 0x07FD
var continueWorld by MemoryByte(ContinueWorld)
const val CrouchingFlag = 0x0714
var crouchingFlag by MemoryByte(CrouchingFlag)
const val CurrentColumnPos = 0x0726
var currentColumnPos by MemoryByte(CurrentColumnPos)
const val CurrentNTAddr_High = 0x0720
var currentNTAddrHigh by MemoryByte(CurrentNTAddr_High)
const val CurrentNTAddr_Low = 0x0721
var currentNTAddrLow by MemoryByte(CurrentNTAddr_Low)
const val CurrentPageLoc = 0x0725
var currentPageLoc by MemoryByte(CurrentPageLoc)
const val CurrentPlayer = 0x0753
var currentPlayer by MemoryByte(CurrentPlayer)
const val DAC_Counter = 0x07C0
var dACCounter by MemoryByte(DAC_Counter)
const val DeathMusic = 0x01
var deathMusic by MemoryByte(DeathMusic)
const val DeathMusicLoaded = 0x0712
var deathMusicLoaded by MemoryByte(DeathMusicLoaded)
const val DemoAction = 0x0717
var demoAction by MemoryByte(DemoAction)
const val DemoActionTimer = 0x0718
var demoActionTimer by MemoryByte(DemoActionTimer)
const val DemoTimer = 0x07A2
var demoTimer by MemoryByte(DemoTimer)
const val DestinationPageLoc = 0x34
var destinationPageLoc by MemoryByte(DestinationPageLoc)
const val DiffToHaltJump = 0x0706
var diffToHaltJump by MemoryByte(DiffToHaltJump)
const val DigitModifier = 0x0134
var digitModifier by MemoryByte(DigitModifier)
const val DisableCollisionDet = 0x0716
var disableCollisionDet by MemoryByte(DisableCollisionDet)
const val DisableIntermediate = 0x0769
var disableIntermediate by MemoryByte(DisableIntermediate)
const val DisableScreenFlag = 0x0774
var disableScreenFlag by MemoryByte(DisableScreenFlag)
const val DisplayDigits = 0x07D7
var displayDigits by MemoryByte(DisplayDigits)
const val Down_Dir = 0x04
var downDir by MemoryByte(Down_Dir)
const val DuplicateObj_Offset = 0x06CF
var duplicateObjOffset by MemoryByte(DuplicateObj_Offset)
const val EndOfCastleMusic = 0x08
var endOfCastleMusic by MemoryByte(EndOfCastleMusic)
const val EndOfLevelMusic = 0x20
var endOfLevelMusic by MemoryByte(EndOfLevelMusic)
const val EnemyBoundingBoxCoord = 0x04B0
var enemyBoundingBoxCoord by MemoryByte(EnemyBoundingBoxCoord)
const val EnemyData = 0xE9
var enemyData by MemoryByte(EnemyData)
const val EnemyDataHigh = 0xEA
var enemyDataHigh by MemoryByte(EnemyDataHigh)
const val EnemyDataLow = 0xE9
var enemyDataLow by MemoryByte(EnemyDataLow)
const val EnemyDataOffset = 0x0739
var enemyDataOffset by MemoryByte(EnemyDataOffset)
const val EnemyFrameTimer = 0x078A
var enemyFrameTimer by MemoryByte(EnemyFrameTimer)
const val EnemyFrenzyBuffer = 0x06CB
var enemyFrenzyBuffer by MemoryByte(EnemyFrenzyBuffer)
const val EnemyFrenzyQueue = 0x06CD
var enemyFrenzyQueue by MemoryByte(EnemyFrenzyQueue)
const val EnemyIntervalTimer = 0x0796
var enemyIntervalTimer by MemoryByte(EnemyIntervalTimer)
const val EnemyObjectPageLoc = 0x073A
var enemyObjectPageLoc by MemoryByte(EnemyObjectPageLoc)
const val EnemyObjectPageSel = 0x073B
var enemyObjectPageSel by MemoryByte(EnemyObjectPageSel)
const val EnemyOffscrBitsMasked = 0x03D8
var enemyOffscrBitsMasked by MemoryByte(EnemyOffscrBitsMasked)
const val Enemy_BoundBoxCtrl = 0x049A
var enemyBoundBoxCtrl by MemoryByte(Enemy_BoundBoxCtrl)
const val Enemy_CollisionBits = 0x0491
var enemyCollisionBits by MemoryByte(Enemy_CollisionBits)
const val Enemy_Flag = 0x0F
var enemyFlag by MemoryByte(Enemy_Flag)
const val Enemy_ID = 0x16
var enemyID by MemoryByte(Enemy_ID)
const val Enemy_MovingDir = 0x46
var enemyMovingDir by MemoryByte(Enemy_MovingDir)
const val Enemy_OffscreenBits = 0x03D1
var enemyOffscreenBits by MemoryByte(Enemy_OffscreenBits)
const val Enemy_PageLoc = 0x6E
var enemyPageLoc by MemoryByte(Enemy_PageLoc)
const val Enemy_Rel_XPos = 0x03AE
var enemyRelXPos by MemoryByte(Enemy_Rel_XPos)
const val Enemy_Rel_YPos = 0x03B9
var enemyRelYPos by MemoryByte(Enemy_Rel_YPos)
const val Enemy_SprAttrib = 0x03C5
var enemySprAttrib by MemoryByte(Enemy_SprAttrib)
const val Enemy_SprDataOffset = 0x06E5
var enemySprDataOffset by MemoryByte(Enemy_SprDataOffset)
const val Enemy_State = 0x1E
var enemyState by MemoryByte(Enemy_State)
const val Enemy_X_MoveForce = 0x0401
var enemyXMoveForce by MemoryByte(Enemy_X_MoveForce)
const val Enemy_X_Position = 0x87
var enemyXPosition by MemoryByte(Enemy_X_Position)
const val Enemy_X_Speed = 0x58
var enemyXSpeed by MemoryByte(Enemy_X_Speed)
const val Enemy_YMF_Dummy = 0x0417
var enemyYMFDummy by MemoryByte(Enemy_YMF_Dummy)
const val Enemy_Y_HighPos = 0xB6
var enemyYHighPos by MemoryByte(Enemy_Y_HighPos)
const val Enemy_Y_MoveForce = 0x0434
var enemyYMoveForce by MemoryByte(Enemy_Y_MoveForce)
const val Enemy_Y_Position = 0xCF
var enemyYPosition by MemoryByte(Enemy_Y_Position)
const val Enemy_Y_Speed = 0xA0
var enemyYSpeed by MemoryByte(Enemy_Y_Speed)
const val EntrancePage = 0x0751
var entrancePage by MemoryByte(EntrancePage)
const val EventMusicBuffer = 0x07B1
var eventMusicBuffer by MemoryByte(EventMusicBuffer)
const val EventMusicQueue = 0xFC
var eventMusicQueue by MemoryByte(EventMusicQueue)
const val ExplosionGfxCounter = 0x58
var explosionGfxCounter by MemoryByte(ExplosionGfxCounter)
const val ExplosionTimerCounter = 0xA0
var explosionTimerCounter by MemoryByte(ExplosionTimerCounter)
const val FBall_OffscreenBits = 0x03D2
var fBallOffscreenBits by MemoryByte(FBall_OffscreenBits)
const val FBall_SprDataOffset = 0x06F1
var fBallSprDataOffset by MemoryByte(FBall_SprDataOffset)
const val FetchNewGameTimerFlag = 0x0757
var fetchNewGameTimerFlag by MemoryByte(FetchNewGameTimerFlag)
const val FireballBouncingFlag = 0x3A
var fireballBouncingFlag by MemoryByte(FireballBouncingFlag)
const val FireballCounter = 0x06CE
var fireballCounter by MemoryByte(FireballCounter)
const val FireballThrowingTimer = 0x0711
var fireballThrowingTimer by MemoryByte(FireballThrowingTimer)
const val Fireball_BoundBoxCtrl = 0x04A0
var fireballBoundBoxCtrl by MemoryByte(Fireball_BoundBoxCtrl)
const val Fireball_PageLoc = 0x74
var fireballPageLoc by MemoryByte(Fireball_PageLoc)
const val Fireball_Rel_XPos = 0x03AF
var fireballRelXPos by MemoryByte(Fireball_Rel_XPos)
const val Fireball_Rel_YPos = 0x03BA
var fireballRelYPos by MemoryByte(Fireball_Rel_YPos)
const val Fireball_State = 0x24
var fireballState by MemoryByte(Fireball_State)
const val Fireball_X_Position = 0x8D
var fireballXPosition by MemoryByte(Fireball_X_Position)
const val Fireball_X_Speed = 0x5E
var fireballXSpeed by MemoryByte(Fireball_X_Speed)
const val Fireball_Y_HighPos = 0xBC
var fireballYHighPos by MemoryByte(Fireball_Y_HighPos)
const val Fireball_Y_Position = 0xD5
var fireballYPosition by MemoryByte(Fireball_Y_Position)
const val Fireball_Y_Speed = 0xA6
var fireballYSpeed by MemoryByte(Fireball_Y_Speed)
const val FirebarSpinDirection = 0x34
var firebarSpinDirection by MemoryByte(FirebarSpinDirection)
const val FirebarSpinSpeed = 0x0388
var firebarSpinSpeed by MemoryByte(FirebarSpinSpeed)
const val FirebarSpinState_High = 0xA0
var firebarSpinStateHigh by MemoryByte(FirebarSpinState_High)
const val FirebarSpinState_Low = 0x58
var firebarSpinStateLow by MemoryByte(FirebarSpinState_Low)
const val Fireworks = 0x16
var fireworks by MemoryByte(Fireworks)
const val FireworksCounter = 0x06D7
var fireworksCounter by MemoryByte(FireworksCounter)
const val FlagpoleCollisionYPos = 0x070F
var flagpoleCollisionYPos by MemoryByte(FlagpoleCollisionYPos)
const val FlagpoleFNum_YMFDummy = 0x010E
var flagpoleFNumYMFDummy by MemoryByte(FlagpoleFNum_YMFDummy)
const val FlagpoleFNum_Y_Pos = 0x010D
var flagpoleFNumYPos by MemoryByte(FlagpoleFNum_Y_Pos)
const val FlagpoleFlagObject = 0x30
var flagpoleFlagObject by MemoryByte(FlagpoleFlagObject)
const val FlagpoleScore = 0x010F
var flagpoleScore by MemoryByte(FlagpoleScore)
const val FlagpoleSoundQueue = 0x0713
var flagpoleSoundQueue by MemoryByte(FlagpoleSoundQueue)
const val FloateyNum_Control = 0x0110
var floateyNumControl by MemoryByte(FloateyNum_Control)
const val FloateyNum_Timer = 0x012C
var floateyNumTimer by MemoryByte(FloateyNum_Timer)
const val FloateyNum_X_Pos = 0x0117
var floateyNumXPos by MemoryByte(FloateyNum_X_Pos)
const val FloateyNum_Y_Pos = 0x011E
var floateyNumYPos by MemoryByte(FloateyNum_Y_Pos)
const val FlyCheepCheepFrenzy = 0x14
var flyCheepCheepFrenzy by MemoryByte(FlyCheepCheepFrenzy)
const val FlyingCheepCheep = 0x14
var flyingCheepCheep by MemoryByte(FlyingCheepCheep)
const val ForegroundScenery = 0x0741
var foregroundScenery by MemoryByte(ForegroundScenery)
const val FrameCounter = 0x09
var frameCounter by MemoryByte(FrameCounter)
const val FrenzyEnemyTimer = 0x078F
var frenzyEnemyTimer by MemoryByte(FrenzyEnemyTimer)
const val FrictionAdderHigh = 0x0701
var frictionAdderHigh by MemoryByte(FrictionAdderHigh)
const val FrictionAdderLow = 0x0702
var frictionAdderLow by MemoryByte(FrictionAdderLow)
const val GameEngineSubroutine = 0x0E
var gameEngineSubroutine by MemoryByte(GameEngineSubroutine)
const val GameModeValue = 0x01
var gameModeValue by MemoryByte(GameModeValue)
const val GameOverModeValue = 0x03
var gameOverModeValue by MemoryByte(GameOverModeValue)
const val GameOverMusic = 0x02
var gameOverMusic by MemoryByte(GameOverMusic)
const val GamePauseStatus = 0x0776
var gamePauseStatus by MemoryByte(GamePauseStatus)
const val GamePauseTimer = 0x0777
var gamePauseTimer by MemoryByte(GamePauseTimer)
const val GameTimerCtrlTimer = 0x0787
var gameTimerCtrlTimer by MemoryByte(GameTimerCtrlTimer)
const val GameTimerDisplay = 0x07F8
var gameTimerDisplay by MemoryByte(GameTimerDisplay)
const val GameTimerExpiredFlag = 0x0759
var gameTimerExpiredFlag by MemoryByte(GameTimerExpiredFlag)
const val GameTimerSetting = 0x0715
var gameTimerSetting by MemoryByte(GameTimerSetting)
const val Goomba = 0x06
var goomba by MemoryByte(Goomba)
const val GreenKoopa = 0x00
var greenKoopa by MemoryByte(GreenKoopa)
const val GreenParatroopaFly = 0x10
var greenParatroopaFly by MemoryByte(GreenParatroopaFly)
const val GreenParatroopaJump = 0x0E
var greenParatroopaJump by MemoryByte(GreenParatroopaJump)
const val GreyCheepCheep = 0x0A
var greyCheepCheep by MemoryByte(GreyCheepCheep)
const val GroundMusic = 0x01
var groundMusic by MemoryByte(GroundMusic)
const val GroundMusicHeaderOfs = 0x07C7
var groundMusicHeaderOfs by MemoryByte(GroundMusicHeaderOfs)
const val HalfwayPage = 0x075B
var halfwayPage by MemoryByte(HalfwayPage)
const val HammerBro = 0x05
var hammerBro by MemoryByte(HammerBro)
const val HammerBroJumpTimer = 0x3C
var hammerBroJumpTimer by MemoryByte(HammerBroJumpTimer)
const val HammerEnemyOffset = 0x06AE
var hammerEnemyOffset by MemoryByte(HammerEnemyOffset)
const val HammerThrowingTimer = 0x03A2
var hammerThrowingTimer by MemoryByte(HammerThrowingTimer)
const val Hidden1UpFlag = 0x075D
var hidden1UpFlag by MemoryByte(Hidden1UpFlag)
const val HorizontalScroll = 0x073F
var horizontalScroll by MemoryByte(HorizontalScroll)
const val InjuryTimer = 0x079E
var injuryTimer by MemoryByte(InjuryTimer)
const val IntervalTimerControl = 0x077F
var intervalTimerControl by MemoryByte(IntervalTimerControl)
const val JOYPAD_PORT = 0x4016
var jOYPADPORT by MemoryByte(JOYPAD_PORT)
const val JOYPAD_PORT1 = 0x4016
var jOYPADPORT1 by MemoryByte(JOYPAD_PORT1)
const val JOYPAD_PORT2 = 0x4017
var jOYPADPORT2 by MemoryByte(JOYPAD_PORT2)
const val JoypadBitMask = 0x074A
var joypadBitMask by MemoryByte(JoypadBitMask)
const val JoypadOverride = 0x0758
var joypadOverride by MemoryByte(JoypadOverride)
const val JumpCoinMiscOffset = 0x06B7
var jumpCoinMiscOffset by MemoryByte(JumpCoinMiscOffset)
const val JumpOrigin_Y_HighPos = 0x0707
var jumpOriginYHighPos by MemoryByte(JumpOrigin_Y_HighPos)
const val JumpOrigin_Y_Position = 0x0708
var jumpOriginYPosition by MemoryByte(JumpOrigin_Y_Position)
const val JumpSwimTimer = 0x0782
var jumpSwimTimer by MemoryByte(JumpSwimTimer)
const val JumpspringAnimCtrl = 0x070E
var jumpspringAnimCtrl by MemoryByte(JumpspringAnimCtrl)
const val JumpspringForce = 0x06DB
var jumpspringForce by MemoryByte(JumpspringForce)
const val JumpspringObject = 0x32
var jumpspringObject by MemoryByte(JumpspringObject)
const val JumpspringTimer = 0x0786
var jumpspringTimer by MemoryByte(JumpspringTimer)
const val Jumpspring_FixedYPos = 0x58
var jumpspringFixedYPos by MemoryByte(Jumpspring_FixedYPos)
const val Lakitu = 0x11
var lakitu by MemoryByte(Lakitu)
const val LakituMoveDirection = 0xA0
var lakituMoveDirection by MemoryByte(LakituMoveDirection)
const val LakituMoveSpeed = 0x58
var lakituMoveSpeed by MemoryByte(LakituMoveSpeed)
const val LakituReappearTimer = 0x06D1
var lakituReappearTimer by MemoryByte(LakituReappearTimer)
const val Left_Dir = 0x02
var leftDir by MemoryByte(Left_Dir)
const val Left_Right_Buttons = 0x0C
var leftRightButtons by MemoryByte(Left_Right_Buttons)
const val Level1 = 0x00
var level1 by MemoryByte(Level1)
const val Level2 = 0x01
var level2 by MemoryByte(Level2)
const val Level3 = 0x02
var level3 by MemoryByte(Level3)
const val Level4 = 0x03
var level4 by MemoryByte(Level4)
const val LevelNumber = 0x075C
var levelNumber by MemoryByte(LevelNumber)
const val LoopCommand = 0x0745
var loopCommand by MemoryByte(LoopCommand)
const val MaxRangeFromOrigin = 0x06DC
var maxRangeFromOrigin by MemoryByte(MaxRangeFromOrigin)
const val MaximumLeftSpeed = 0x0450
var maximumLeftSpeed by MemoryByte(MaximumLeftSpeed)
const val MaximumRightSpeed = 0x0456
var maximumRightSpeed by MemoryByte(MaximumRightSpeed)
const val MetatileBuffer = 0x06A1
var metatileBuffer by MemoryByte(MetatileBuffer)
const val Mirror_PPU_CTRL_REG1 = 0x0778
var mirrorPPUCTRLREG1 by MemoryByte(Mirror_PPU_CTRL_REG1)
const val Mirror_PPU_CTRL_REG2 = 0x0779
var mirrorPPUCTRLREG2 by MemoryByte(Mirror_PPU_CTRL_REG2)
const val Misc_BoundBoxCtrl = 0x04A2
var miscBoundBoxCtrl by MemoryByte(Misc_BoundBoxCtrl)
const val Misc_Collision_Flag = 0x06BE
var miscCollisionFlag by MemoryByte(Misc_Collision_Flag)
const val Misc_OffscreenBits = 0x03D6
var miscOffscreenBits by MemoryByte(Misc_OffscreenBits)
const val Misc_PageLoc = 0x7A
var miscPageLoc by MemoryByte(Misc_PageLoc)
const val Misc_Rel_XPos = 0x03B3
var miscRelXPos by MemoryByte(Misc_Rel_XPos)
const val Misc_Rel_YPos = 0x03BE
var miscRelYPos by MemoryByte(Misc_Rel_YPos)
const val Misc_SprDataOffset = 0x06F3
var miscSprDataOffset by MemoryByte(Misc_SprDataOffset)
const val Misc_State = 0x2A
var miscState by MemoryByte(Misc_State)
const val Misc_X_Position = 0x93
var miscXPosition by MemoryByte(Misc_X_Position)
const val Misc_X_Speed = 0x64
var miscXSpeed by MemoryByte(Misc_X_Speed)
const val Misc_Y_HighPos = 0xC2
var miscYHighPos by MemoryByte(Misc_Y_HighPos)
const val Misc_Y_Position = 0xDB
var miscYPosition by MemoryByte(Misc_Y_Position)
const val Misc_Y_Speed = 0xAC
var miscYSpeed by MemoryByte(Misc_Y_Speed)
const val MultiLoopCorrectCntr = 0x06D9
var multiLoopCorrectCntr by MemoryByte(MultiLoopCorrectCntr)
const val MultiLoopPassCntr = 0x06DA
var multiLoopPassCntr by MemoryByte(MultiLoopPassCntr)
const val MushroomLedgeHalfLen = 0x0736
var mushroomLedgeHalfLen by MemoryByte(MushroomLedgeHalfLen)
const val MusicData = 0xF5
var musicData by MemoryByte(MusicData)
const val MusicDataHigh = 0xF6
var musicDataHigh by MemoryByte(MusicDataHigh)
const val MusicDataLow = 0xF5
var musicDataLow by MemoryByte(MusicDataLow)
const val MusicOffset_Noise = 0x07B0
var musicOffsetNoise by MemoryByte(MusicOffset_Noise)
const val MusicOffset_Square1 = 0xF8
var musicOffsetSquare1 by MemoryByte(MusicOffset_Square1)
const val MusicOffset_Square2 = 0xF7
var musicOffsetSquare2 by MemoryByte(MusicOffset_Square2)
const val MusicOffset_Triangle = 0xF9
var musicOffsetTriangle by MemoryByte(MusicOffset_Triangle)
const val NoiseDataLoopbackOfs = 0x07C1
var noiseDataLoopbackOfs by MemoryByte(NoiseDataLoopbackOfs)
const val NoiseSoundBuffer = 0xF3
var noiseSoundBuffer by MemoryByte(NoiseSoundBuffer)
const val NoiseSoundQueue = 0xFD
var noiseSoundQueue by MemoryByte(NoiseSoundQueue)
const val Noise_BeatLenCounter = 0x07BA
var noiseBeatLenCounter by MemoryByte(Noise_BeatLenCounter)
const val Noise_SfxLenCounter = 0x07BF
var noiseSfxLenCounter by MemoryByte(Noise_SfxLenCounter)
const val NoteLenLookupTblOfs = 0xF0
var noteLenLookupTblOfs by MemoryByte(NoteLenLookupTblOfs)
const val NoteLengthTblAdder = 0x07C4
var noteLengthTblAdder by MemoryByte(NoteLengthTblAdder)
const val NumberOfPlayers = 0x077A
var numberOfPlayers by MemoryByte(NumberOfPlayers)
const val NumberofGroupEnemies = 0x06D3
var numberofGroupEnemies by MemoryByte(NumberofGroupEnemies)
const val NumberofLives = 0x075A
var numberofLives by MemoryByte(NumberofLives)
const val ObjectOffset = 0x08
var objectOffset by MemoryByte(ObjectOffset)
const val OffScr_AreaNumber = 0x0767
var offScrAreaNumber by MemoryByte(OffScr_AreaNumber)
const val OffScr_CoinTally = 0x0765
var offScrCoinTally by MemoryByte(OffScr_CoinTally)
const val OffScr_HalfwayPage = 0x0762
var offScrHalfwayPage by MemoryByte(OffScr_HalfwayPage)
const val OffScr_Hidden1UpFlag = 0x0764
var offScrHidden1UpFlag by MemoryByte(OffScr_Hidden1UpFlag)
const val OffScr_LevelNumber = 0x0763
var offScrLevelNumber by MemoryByte(OffScr_LevelNumber)
const val OffScr_NumberofLives = 0x0761
var offScrNumberofLives by MemoryByte(OffScr_NumberofLives)
const val OffScr_WorldNumber = 0x0766
var offScrWorldNumber by MemoryByte(OffScr_WorldNumber)
const val OffscreenPlayerInfo = 0x0761
var offscreenPlayerInfo by MemoryByte(OffscreenPlayerInfo)
const val OnscreenPlayerInfo = 0x075A
var onscreenPlayerInfo by MemoryByte(OnscreenPlayerInfo)
const val OperMode = 0x0770
var operMode by MemoryByte(OperMode)
const val OperMode_Task = 0x0772
var operModeTask by MemoryByte(OperMode_Task)
const val PPU_ADDRESS = 0x2006
var pPUADDRESS by MemoryByte(PPU_ADDRESS)
const val PPU_CTRL_REG1 = 0x2000
var pPUCTRLREG1 by MemoryByte(PPU_CTRL_REG1)
const val PPU_CTRL_REG2 = 0x2001
var pPUCTRLREG2 by MemoryByte(PPU_CTRL_REG2)
const val PPU_DATA = 0x2007
var pPUDATA by MemoryByte(PPU_DATA)
const val PPU_SCROLL_REG = 0x2005
var pPUSCROLLREG by MemoryByte(PPU_SCROLL_REG)
const val PPU_SPR_ADDR = 0x2003
var pPUSPRADDR by MemoryByte(PPU_SPR_ADDR)
const val PPU_SPR_DATA = 0x2004
var pPUSPRDATA by MemoryByte(PPU_SPR_DATA)
const val PPU_STATUS = 0x2002
var pPUSTATUS by MemoryByte(PPU_STATUS)
const val PauseModeFlag = 0x07C6
var pauseModeFlag by MemoryByte(PauseModeFlag)
const val PauseSoundBuffer = 0x07B2
var pauseSoundBuffer by MemoryByte(PauseSoundBuffer)
const val PauseSoundQueue = 0xFA
var pauseSoundQueue by MemoryByte(PauseSoundQueue)
const val PipeIntroMusic = 0x20
var pipeIntroMusic by MemoryByte(PipeIntroMusic)
const val PiranhaPlant = 0x0D
var piranhaPlant by MemoryByte(PiranhaPlant)
const val PiranhaPlantDownYPos = 0x0434
var piranhaPlantDownYPos by MemoryByte(PiranhaPlantDownYPos)
const val PiranhaPlantUpYPos = 0x0417
var piranhaPlantUpYPos by MemoryByte(PiranhaPlantUpYPos)
const val PiranhaPlant_MoveFlag = 0xA0
var piranhaPlantMoveFlag by MemoryByte(PiranhaPlant_MoveFlag)
const val PiranhaPlant_Y_Speed = 0x58
var piranhaPlantYSpeed by MemoryByte(PiranhaPlant_Y_Speed)
const val PlatformCollisionFlag = 0x03A2
var platformCollisionFlag by MemoryByte(PlatformCollisionFlag)
const val Platform_X_Scroll = 0x03A1
var platformXScroll by MemoryByte(Platform_X_Scroll)
const val PlayerAnimCtrl = 0x070D
var playerAnimCtrl by MemoryByte(PlayerAnimCtrl)
const val PlayerAnimTimer = 0x0781
var playerAnimTimer by MemoryByte(PlayerAnimTimer)
const val PlayerAnimTimerSet = 0x070C
var playerAnimTimerSet by MemoryByte(PlayerAnimTimerSet)
const val PlayerChangeSizeFlag = 0x070B
var playerChangeSizeFlag by MemoryByte(PlayerChangeSizeFlag)
const val PlayerEntranceCtrl = 0x0710
var playerEntranceCtrl by MemoryByte(PlayerEntranceCtrl)
const val PlayerFacingDir = 0x33
var playerFacingDir by MemoryByte(PlayerFacingDir)
const val PlayerGfxOffset = 0x06D5
var playerGfxOffset by MemoryByte(PlayerGfxOffset)
const val PlayerScoreDisplay = 0x07DD
var playerScoreDisplay by MemoryByte(PlayerScoreDisplay)
const val PlayerSize = 0x0754
var playerSize by MemoryByte(PlayerSize)
const val PlayerStatus = 0x0756
var playerStatus by MemoryByte(PlayerStatus)
const val Player_BoundBoxCtrl = 0x0499
var playerBoundBoxCtrl by MemoryByte(Player_BoundBoxCtrl)
const val Player_CollisionBits = 0x0490
var playerCollisionBits by MemoryByte(Player_CollisionBits)
const val Player_MovingDir = 0x45
var playerMovingDir by MemoryByte(Player_MovingDir)
const val Player_OffscreenBits = 0x03D0
var playerOffscreenBits by MemoryByte(Player_OffscreenBits)
const val Player_PageLoc = 0x6D
var playerPageLoc by MemoryByte(Player_PageLoc)
const val Player_Pos_ForScroll = 0x0755
var playerPosForScroll by MemoryByte(Player_Pos_ForScroll)
const val Player_Rel_XPos = 0x03AD
var playerRelXPos by MemoryByte(Player_Rel_XPos)
const val Player_Rel_YPos = 0x03B8
var playerRelYPos by MemoryByte(Player_Rel_YPos)
const val Player_SprAttrib = 0x03C4
var playerSprAttrib by MemoryByte(Player_SprAttrib)
const val Player_SprDataOffset = 0x06E4
var playerSprDataOffset by MemoryByte(Player_SprDataOffset)
const val Player_State = 0x1D
var playerState by MemoryByte(Player_State)
const val Player_XSpeedAbsolute = 0x0700
var playerXSpeedAbsolute by MemoryByte(Player_XSpeedAbsolute)
const val Player_X_MoveForce = 0x0705
var playerXMoveForce by MemoryByte(Player_X_MoveForce)
const val Player_X_Position = 0x86
var playerXPosition by MemoryByte(Player_X_Position)
const val Player_X_Scroll = 0x06FF
var playerXScroll by MemoryByte(Player_X_Scroll)
const val Player_X_Speed = 0x57
var playerXSpeed by MemoryByte(Player_X_Speed)
const val Player_YMF_Dummy = 0x0416
var playerYMFDummy by MemoryByte(Player_YMF_Dummy)
const val Player_Y_HighPos = 0xB5
var playerYHighPos by MemoryByte(Player_Y_HighPos)
const val Player_Y_MoveForce = 0x0433
var playerYMoveForce by MemoryByte(Player_Y_MoveForce)
const val Player_Y_Position = 0xCE
var playerYPosition by MemoryByte(Player_Y_Position)
const val Player_Y_Speed = 0x9F
var playerYSpeed by MemoryByte(Player_Y_Speed)
const val Podoboo = 0x0C
var podoboo by MemoryByte(Podoboo)
const val PowerUpObject = 0x2E
var powerUpObject by MemoryByte(PowerUpObject)
const val PowerUpType = 0x39
var powerUpType by MemoryByte(PowerUpType)
const val PreviousA_B_Buttons = 0x0D
var previousABButtons by MemoryByte(PreviousA_B_Buttons)
const val PrimaryHardMode = 0x076A
var primaryHardMode by MemoryByte(PrimaryHardMode)
const val PrimaryMsgCounter = 0x0719
var primaryMsgCounter by MemoryByte(PrimaryMsgCounter)
const val PseudoRandomBitReg = 0x07A7
var pseudoRandomBitReg by MemoryByte(PseudoRandomBitReg)
const val RedCheepCheep = 0x0B
var redCheepCheep by MemoryByte(RedCheepCheep)
const val RedKoopa = 0x03
var redKoopa by MemoryByte(RedKoopa)
const val RedPTroopaCenterYPos = 0x58
var redPTroopaCenterYPos by MemoryByte(RedPTroopaCenterYPos)
const val RedPTroopaOrigXPos = 0x0401
var redPTroopaOrigXPos by MemoryByte(RedPTroopaOrigXPos)
const val RedParatroopa = 0x0F
var redParatroopa by MemoryByte(RedParatroopa)
const val RetainerObject = 0x35
var retainerObject by MemoryByte(RetainerObject)
const val Right_Dir = 0x01
var rightDir by MemoryByte(Right_Dir)
const val RunningSpeed = 0x0703
var runningSpeed by MemoryByte(RunningSpeed)
const val RunningTimer = 0x0783
var runningTimer by MemoryByte(RunningTimer)
const val SND_DELTA_REG = 0x4010
var sNDDELTAREG by MemoryByte(SND_DELTA_REG)
const val SND_MASTERCTRL_REG = 0x4015
var sNDMASTERCTRLREG by MemoryByte(SND_MASTERCTRL_REG)
const val SND_NOISE_REG = 0x400C
var sNDNOISEREG by MemoryByte(SND_NOISE_REG)
const val SND_REGISTER = 0x4000
var sNDREGISTER by MemoryByte(SND_REGISTER)
const val SND_SQUARE1_REG = 0x4000
var sNDSQUARE1REG by MemoryByte(SND_SQUARE1_REG)
const val SND_SQUARE2_REG = 0x4004
var sNDSQUARE2REG by MemoryByte(SND_SQUARE2_REG)
const val SND_TRIANGLE_REG = 0x4008
var sNDTRIANGLEREG by MemoryByte(SND_TRIANGLE_REG)
const val SPR_DMA = 0x4014
var sPRDMA by MemoryByte(SPR_DMA)
const val SavedJoypad1Bits = 0x06FC
var savedJoypad1Bits by MemoryByte(SavedJoypad1Bits)
const val SavedJoypad2Bits = 0x06FD
var savedJoypad2Bits by MemoryByte(SavedJoypad2Bits)
const val SavedJoypadBits = 0x06FC
var savedJoypadBits by MemoryByte(SavedJoypadBits)
const val ScoreAndCoinDisplay = 0x07DD
var scoreAndCoinDisplay by MemoryByte(ScoreAndCoinDisplay)
const val ScreenEdge_PageLoc = 0x071A
var screenEdgePageLoc by MemoryByte(ScreenEdge_PageLoc)
const val ScreenEdge_X_Pos = 0x071C
var screenEdgeXPos by MemoryByte(ScreenEdge_X_Pos)
const val ScreenLeft_PageLoc = 0x071A
var screenLeftPageLoc by MemoryByte(ScreenLeft_PageLoc)
const val ScreenLeft_X_Pos = 0x071C
var screenLeftXPos by MemoryByte(ScreenLeft_X_Pos)
const val ScreenRight_PageLoc = 0x071B
var screenRightPageLoc by MemoryByte(ScreenRight_PageLoc)
const val ScreenRight_X_Pos = 0x071D
var screenRightXPos by MemoryByte(ScreenRight_X_Pos)
const val ScreenRoutineTask = 0x073C
var screenRoutineTask by MemoryByte(ScreenRoutineTask)
const val ScreenTimer = 0x07A0
var screenTimer by MemoryByte(ScreenTimer)
const val ScrollAmount = 0x0775
var scrollAmount by MemoryByte(ScrollAmount)
const val ScrollFractional = 0x0768
var scrollFractional by MemoryByte(ScrollFractional)
const val ScrollIntervalTimer = 0x0795
var scrollIntervalTimer by MemoryByte(ScrollIntervalTimer)
const val ScrollLock = 0x0723
var scrollLock by MemoryByte(ScrollLock)
const val ScrollThirtyTwo = 0x073D
var scrollThirtyTwo by MemoryByte(ScrollThirtyTwo)
const val SecondaryHardMode = 0x06CC
var secondaryHardMode by MemoryByte(SecondaryHardMode)
const val SecondaryMsgCounter = 0x0749
var secondaryMsgCounter by MemoryByte(SecondaryMsgCounter)
const val SelectTimer = 0x0780
var selectTimer by MemoryByte(SelectTimer)
const val Select_Button = 0x20
var selectButton by MemoryByte(Select_Button)
const val Sfx_BigJump = 0x01
var sfxBigJump by MemoryByte(Sfx_BigJump)
const val Sfx_Blast = 0x08
var sfxBlast by MemoryByte(Sfx_Blast)
const val Sfx_BowserFall = 0x80
var sfxBowserFall by MemoryByte(Sfx_BowserFall)
const val Sfx_BowserFlame = 0x02
var sfxBowserFlame by MemoryByte(Sfx_BowserFlame)
const val Sfx_BrickShatter = 0x01
var sfxBrickShatter by MemoryByte(Sfx_BrickShatter)
const val Sfx_Bump = 0x02
var sfxBump by MemoryByte(Sfx_Bump)
const val Sfx_CoinGrab = 0x01
var sfxCoinGrab by MemoryByte(Sfx_CoinGrab)
const val Sfx_EnemySmack = 0x08
var sfxEnemySmack by MemoryByte(Sfx_EnemySmack)
const val Sfx_EnemyStomp = 0x04
var sfxEnemyStomp by MemoryByte(Sfx_EnemyStomp)
const val Sfx_ExtraLife = 0x40
var sfxExtraLife by MemoryByte(Sfx_ExtraLife)
const val Sfx_Fireball = 0x20
var sfxFireball by MemoryByte(Sfx_Fireball)
const val Sfx_Flagpole = 0x40
var sfxFlagpole by MemoryByte(Sfx_Flagpole)
const val Sfx_GrowPowerUp = 0x02
var sfxGrowPowerUp by MemoryByte(Sfx_GrowPowerUp)
const val Sfx_GrowVine = 0x04
var sfxGrowVine by MemoryByte(Sfx_GrowVine)
const val Sfx_PipeDown_Injury = 0x10
var sfxPipeDownInjury by MemoryByte(Sfx_PipeDown_Injury)
const val Sfx_PowerUpGrab = 0x20
var sfxPowerUpGrab by MemoryByte(Sfx_PowerUpGrab)
const val Sfx_SecondaryCounter = 0x07BE
var sfxSecondaryCounter by MemoryByte(Sfx_SecondaryCounter)
const val Sfx_SmallJump = 0x80
var sfxSmallJump by MemoryByte(Sfx_SmallJump)
const val Sfx_TimerTick = 0x10
var sfxTimerTick by MemoryByte(Sfx_TimerTick)
const val ShellChainCounter = 0x0125
var shellChainCounter by MemoryByte(ShellChainCounter)
const val SideCollisionTimer = 0x0785
var sideCollisionTimer by MemoryByte(SideCollisionTimer)
const val Silence = 0x80
var silence by MemoryByte(Silence)
const val SoundMemory = 0x07B0
var soundMemory by MemoryByte(SoundMemory)
const val Spiny = 0x12
var spiny by MemoryByte(Spiny)
const val SprDataOffset = 0x06E4
var sprDataOffset by MemoryByte(SprDataOffset)
const val SprDataOffset_Ctrl = 0x03EE
var sprDataOffsetCtrl by MemoryByte(SprDataOffset_Ctrl)
const val SprObj_BoundBoxCtrl = 0x0499
var sprObjBoundBoxCtrl by MemoryByte(SprObj_BoundBoxCtrl)
const val SprObject_OffscrBits = 0x03D0
var sprObjectOffscrBits by MemoryByte(SprObject_OffscrBits)
const val SprObject_PageLoc = 0x6D
var sprObjectPageLoc by MemoryByte(SprObject_PageLoc)
const val SprObject_Rel_XPos = 0x03AD
var sprObjectRelXPos by MemoryByte(SprObject_Rel_XPos)
const val SprObject_Rel_YPos = 0x03B8
var sprObjectRelYPos by MemoryByte(SprObject_Rel_YPos)
const val SprObject_SprAttrib = 0x03C4
var sprObjectSprAttrib by MemoryByte(SprObject_SprAttrib)
const val SprObject_X_MoveForce = 0x0400
var sprObjectXMoveForce by MemoryByte(SprObject_X_MoveForce)
const val SprObject_X_Position = 0x86
var sprObjectXPosition by MemoryByte(SprObject_X_Position)
const val SprObject_X_Speed = 0x57
var sprObjectXSpeed by MemoryByte(SprObject_X_Speed)
const val SprObject_YMF_Dummy = 0x0416
var sprObjectYMFDummy by MemoryByte(SprObject_YMF_Dummy)
const val SprObject_Y_HighPos = 0xB5
var sprObjectYHighPos by MemoryByte(SprObject_Y_HighPos)
const val SprObject_Y_MoveForce = 0x0433
var sprObjectYMoveForce by MemoryByte(SprObject_Y_MoveForce)
const val SprObject_Y_Position = 0xCE
var sprObjectYPosition by MemoryByte(SprObject_Y_Position)
const val SprObject_Y_Speed = 0x9F
var sprObjectYSpeed by MemoryByte(SprObject_Y_Speed)
const val SprShuffleAmt = 0x06E1
var sprShuffleAmt by MemoryByte(SprShuffleAmt)
const val SprShuffleAmtOffset = 0x06E0
var sprShuffleAmtOffset by MemoryByte(SprShuffleAmtOffset)
const val Sprite0HitDetectFlag = 0x0722
var sprite0HitDetectFlag by MemoryByte(Sprite0HitDetectFlag)
const val Sprite_Attributes = 0x0202
var spriteAttributes by MemoryByte(Sprite_Attributes)
const val Sprite_Data = 0x0200
var spriteData by MemoryByte(Sprite_Data)
const val Sprite_Tilenumber = 0x0201
var spriteTilenumber by MemoryByte(Sprite_Tilenumber)
const val Sprite_X_Position = 0x0203
var spriteXPosition by MemoryByte(Sprite_X_Position)
const val Sprite_Y_Position = 0x0200
var spriteYPosition by MemoryByte(Sprite_Y_Position)
const val Squ1_EnvelopeDataCtrl = 0x07B7
var squ1EnvelopeDataCtrl by MemoryByte(Squ1_EnvelopeDataCtrl)
const val Squ1_NoteLenCounter = 0x07B6
var squ1NoteLenCounter by MemoryByte(Squ1_NoteLenCounter)
const val Squ1_SfxLenCounter = 0x07BB
var squ1SfxLenCounter by MemoryByte(Squ1_SfxLenCounter)
const val Squ2_EnvelopeDataCtrl = 0x07B5
var squ2EnvelopeDataCtrl by MemoryByte(Squ2_EnvelopeDataCtrl)
const val Squ2_NoteLenBuffer = 0x07B3
var squ2NoteLenBuffer by MemoryByte(Squ2_NoteLenBuffer)
const val Squ2_NoteLenCounter = 0x07B4
var squ2NoteLenCounter by MemoryByte(Squ2_NoteLenCounter)
const val Squ2_SfxLenCounter = 0x07BD
var squ2SfxLenCounter by MemoryByte(Squ2_SfxLenCounter)
const val Square1SoundBuffer = 0xF1
var square1SoundBuffer by MemoryByte(Square1SoundBuffer)
const val Square1SoundQueue = 0xFF
var square1SoundQueue by MemoryByte(Square1SoundQueue)
const val Square2SoundBuffer = 0xF2
var square2SoundBuffer by MemoryByte(Square2SoundBuffer)
const val Square2SoundQueue = 0xFE
var square2SoundQueue by MemoryByte(Square2SoundQueue)
const val StaircaseControl = 0x0734
var staircaseControl by MemoryByte(StaircaseControl)
const val StarFlagObject = 0x31
var starFlagObject by MemoryByte(StarFlagObject)
const val StarFlagTaskControl = 0x0746
var starFlagTaskControl by MemoryByte(StarFlagTaskControl)
const val StarInvincibleTimer = 0x079F
var starInvincibleTimer by MemoryByte(StarInvincibleTimer)
const val StarPowerMusic = 0x40
var starPowerMusic by MemoryByte(StarPowerMusic)
const val Start_Button = 0x10
var startButton by MemoryByte(Start_Button)
const val StompChainCounter = 0x0484
var stompChainCounter by MemoryByte(StompChainCounter)
const val StompTimer = 0x0791
var stompTimer by MemoryByte(StompTimer)
const val Stop_Frenzy = 0x18
var stopFrenzy by MemoryByte(Stop_Frenzy)
const val SwimmingFlag = 0x0704
var swimmingFlag by MemoryByte(SwimmingFlag)
const val TallEnemy = 0x09
var tallEnemy by MemoryByte(TallEnemy)
const val TerrainControl = 0x0727
var terrainControl by MemoryByte(TerrainControl)
const val TimeRunningOutMusic = 0x40
var timeRunningOutMusic by MemoryByte(TimeRunningOutMusic)
const val TimerControl = 0x0747
var timerControl by MemoryByte(TimerControl)
const val Timers = 0x0780
var timers by MemoryByte(Timers)
const val TitleScreenDataOffset = 0x1EC0
var titleScreenDataOffset by MemoryByte(TitleScreenDataOffset)
const val TitleScreenModeValue = 0x00
var titleScreenModeValue by MemoryByte(TitleScreenModeValue)
const val TopScoreDisplay = 0x07D7
var topScoreDisplay by MemoryByte(TopScoreDisplay)
const val Tri_NoteLenBuffer = 0x07B8
var triNoteLenBuffer by MemoryByte(Tri_NoteLenBuffer)
const val Tri_NoteLenCounter = 0x07B9
var triNoteLenCounter by MemoryByte(Tri_NoteLenCounter)
const val UndergroundMusic = 0x04
var undergroundMusic by MemoryByte(UndergroundMusic)
const val Up_Dir = 0x08
var upDir by MemoryByte(Up_Dir)
const val Up_Down_Buttons = 0x0B
var upDownButtons by MemoryByte(Up_Down_Buttons)
const val VRAM_Buffer1 = 0x0301
var vRAMBuffer1 by MemoryByte(VRAM_Buffer1)
const val VRAM_Buffer1_Offset = 0x0300
var vRAMBuffer1Offset by MemoryByte(VRAM_Buffer1_Offset)
const val VRAM_Buffer2 = 0x0341
var vRAMBuffer2 by MemoryByte(VRAM_Buffer2)
const val VRAM_Buffer2_Offset = 0x0340
var vRAMBuffer2Offset by MemoryByte(VRAM_Buffer2_Offset)
const val VRAM_Buffer_AddrCtrl = 0x0773
var vRAMBufferAddrCtrl by MemoryByte(VRAM_Buffer_AddrCtrl)
const val VerticalFlipFlag = 0x0109
var verticalFlipFlag by MemoryByte(VerticalFlipFlag)
const val VerticalForce = 0x0709
var verticalForce by MemoryByte(VerticalForce)
const val VerticalForceDown = 0x070A
var verticalForceDown by MemoryByte(VerticalForceDown)
const val VerticalScroll = 0x0740
var verticalScroll by MemoryByte(VerticalScroll)
const val VictoryModeValue = 0x02
var victoryModeValue by MemoryByte(VictoryModeValue)
const val VictoryMusic = 0x04
var victoryMusic by MemoryByte(VictoryMusic)
const val VictoryWalkControl = 0x35
var victoryWalkControl by MemoryByte(VictoryWalkControl)
const val VineFlagOffset = 0x0398
var vineFlagOffset by MemoryByte(VineFlagOffset)
const val VineHeight = 0x0399
var vineHeight by MemoryByte(VineHeight)
const val VineObjOffset = 0x039A
var vineObjOffset by MemoryByte(VineObjOffset)
const val VineObject = 0x2F
var vineObject by MemoryByte(VineObject)
const val VineStart_Y_Position = 0x039D
var vineStartYPosition by MemoryByte(VineStart_Y_Position)
const val WarmBootValidation = 0x07FF
var warmBootValidation by MemoryByte(WarmBootValidation)
const val WarpZoneControl = 0x06D6
var warpZoneControl by MemoryByte(WarpZoneControl)
const val WaterMusic = 0x02
var waterMusic by MemoryByte(WaterMusic)
const val Whirlpool_Flag = 0x047D
var whirlpoolFlag by MemoryByte(Whirlpool_Flag)
const val Whirlpool_LeftExtent = 0x0471
var whirlpoolLeftExtent by MemoryByte(Whirlpool_LeftExtent)
const val Whirlpool_Length = 0x0477
var whirlpoolLength by MemoryByte(Whirlpool_Length)
const val Whirlpool_Offset = 0x046A
var whirlpoolOffset by MemoryByte(Whirlpool_Offset)
const val Whirlpool_PageLoc = 0x046B
var whirlpoolPageLoc by MemoryByte(Whirlpool_PageLoc)
const val World1 = 0x00
var world1 by MemoryByte(World1)
const val World2 = 0x01
var world2 by MemoryByte(World2)
const val World3 = 0x02
var world3 by MemoryByte(World3)
const val World4 = 0x03
var world4 by MemoryByte(World4)
const val World5 = 0x04
var world5 by MemoryByte(World5)
const val World6 = 0x05
var world6 by MemoryByte(World6)
const val World7 = 0x06
var world7 by MemoryByte(World7)
const val World8 = 0x07
var world8 by MemoryByte(World8)
const val WorldEndTimer = 0x07A1
var worldEndTimer by MemoryByte(WorldEndTimer)
const val WorldNumber = 0x075F
var worldNumber by MemoryByte(WorldNumber)
const val WorldSelectEnableFlag = 0x07FC
var worldSelectEnableFlag by MemoryByte(WorldSelectEnableFlag)
const val WorldSelectNumber = 0x076B
var worldSelectNumber by MemoryByte(WorldSelectNumber)
const val XMovePrimaryCounter = 0xA0
var xMovePrimaryCounter by MemoryByte(XMovePrimaryCounter)
const val XMoveSecondaryCounter = 0x58
var xMoveSecondaryCounter by MemoryByte(XMoveSecondaryCounter)
const val YPlatformCenterYPos = 0x58
var yPlatformCenterYPos by MemoryByte(YPlatformCenterYPos)
const val YPlatformTopYPos = 0x0401
var yPlatformTopYPos by MemoryByte(YPlatformTopYPos)

// ROM code label constants (data table addresses)
// These are addresses in ROM where data tables and code are located

const val ActionClimbing = 0xFC52
const val ActionFalling = 0xFC42
const val ActionSwimming = 0xFC60
const val ActionWalkRun = 0xFC4A
const val AddCCF = 0xD62E
const val AddFBit = 0xCCBA
const val AddHA = 0xD4B5
const val AddHS = 0xD109
const val AddModLoop = 0x9073
const val AddToScore = 0xC065
const val AddVA = 0xD4F2
const val AdjSm = 0xD523
const val AlignP = 0xCDED
const val AllMus = 0x10633
const val AllRowC = 0xF73D
const val AllUnder = 0x9977
const val AltYPosOffset = 0x9247
const val Alter2 = 0x9898
const val AlterAreaAttributes = 0x9879
const val AlterYP = 0xC4C6
const val AlternateLengthHandler = 0x10603
const val AnimationControl = 0xFC82
const val AreaAddrOffsets = 0x9EF4
const val AreaChangeTimerData = 0xE7C8
const val AreaDataAddrHigh = 0x9F86
const val AreaDataAddrLow = 0x9F64
const val AreaDataHOffsets = 0x9F60
const val AreaDataOfsLoopback = 0x9E24
const val AreaFrenzy = 0x98EA
const val AreaMusicEnvData = 0x10CE1
const val AreaPalette = 0x862F
const val AreaParserCore = 0x954B
const val AreaParserTaskControl = 0x8768
const val AreaParserTaskHandler = 0x93FC
const val AreaParserTasks = 0x9416
const val AreaStyleObject = 0x9902
const val AttribLoop = 0x8A52
const val AutoClimb = 0xB46C
const val AutoControlPlayer = 0xB351
const val AutoPlayer = 0x83DB
const val AwardGameTimerPoints = 0xDAD1
const val AwardTouchedCoin = 0xE6AC
const val AxeObj = 0x9C0B
const val BBChk_E = 0xEE6A
const val BBFly = 0xBE78
const val BB_SLoop = 0xCCD3
const val BGColorCtrl_Addr = 0x863F
const val BHalf = 0xE72F
const val BPGet = 0xD641
const val BSceneDataOffsets = 0x9446
const val BSwimE = 0xD2A2
const val BWithL = 0x9D3F
const val B_FaceP = 0xD81E
const val BackColC = 0x97F6
const val BackSceneryData = 0x9449
const val BackSceneryMetatiles = 0x94D9
const val BackgroundColors = 0x8643
const val BalancePlatRope = 0x9BD9
const val BalancePlatform = 0xDC25
const val BigBP = 0xC1D8
const val BigJp = 0xD52E
const val BigKTS = 0xFB19
const val Bitmasks = 0xCC56
const val BlankPalette = 0x8A8F
const val BlkOffscr = 0xF7F1
const val BlockBuffLowBounds = 0x967B
const val BlockBufferAdderData = 0xEE73
const val BlockBufferAddr = 0x9E07
const val BlockBufferChk_Enemy = 0xEE4D
const val BlockBufferChk_FBall = 0xEE61
const val BlockBufferColli_Feet = 0xEEAE
const val BlockBufferColli_Head = 0xEEAF
const val BlockBufferColli_Side = 0xEEB2
const val BlockBufferCollision = 0xEEB6
const val BlockBuffer_X_Adder = 0xEE76
const val BlockBuffer_Y_Adder = 0xEE92
const val BlockBumpedChk = 0xC27E
const val BlockCode = 0xC244
const val BlockGfxData = 0x8B07
const val BlockObjMT_Updater = 0xC382
const val BlockObjectsCore = 0xC30E
const val BlockYPosAdderData = 0xC14E
const val BlooberBitmasks = 0xD206
const val BlooberSwim = 0xD233
const val BlstSJp = 0x1029A
const val BorrowOne = 0x9096
const val BotSP = 0xF980
const val BounceJS = 0xBCBC
const val BouncingBlockHandler = 0xC35D
const val BoundBoxCtrlData = 0xEC8B
const val BoundingBoxCore = 0xED36
const val BowserControl = 0xD7E7
const val BowserFlameEnvData = 0x10D11
const val BowserGfxHandler = 0xD906
const val BowserIdentities = 0xDFB0
const val BowserPaletteData = 0x8E33
const val BranchToDecLength1 = 0x10114
const val BrickMetatiles = 0x9C2D
const val BrickQBlockMetatiles = 0xC270
const val BrickShatter = 0xC28C
const val BrickShatterEnvData = 0x10D31
const val BrickShatterFreqData = 0x102FC
const val BrickWithCoins = 0x9D29
const val BrickWithItem = 0x9D2E
const val BridgeCollapse = 0xD73C
const val BridgeCollapseData = 0xD72D
const val Bridge_High = 0x9B76
const val Bridge_Low = 0x9B7C
const val Bridge_Middle = 0x9B79
const val BubbleCheck = 0xBA61
const val BubbleTimerData = 0xBAC7
const val Bubble_MForceData = 0xBAC5
const val BublExit = 0xB9D8
const val BublLoop = 0xB9C5
const val BulletBillCannon = 0x9C70
const val BulletBillCheepCheep = 0xCC68
const val BulletBillHandler = 0xBE2B
const val BulletBillXSpdData = 0xBE29
const val BumpBlock = 0xC21B
const val BumpChkLoop = 0xC280
const val BuzzyBeetleMutate = 0xC732
const val CCSwim = 0xD303
const val CCSwimUpwards = 0xD35A
const val CGrab_TTickRegL = 0x101C5
const val CInvu = 0xE9F6
const val CMBits = 0xECFF
const val CNwCDir = 0xEB0A
const val CRendLoop = 0x99EE
const val CSetFDir = 0xB6F4
const val CSzNext = 0xFCDE
const val C_ObjectMetatile = 0x9C00
const val C_ObjectRow = 0x9BFD
const val C_S_IGAtt = 0xFD3A
const val CannonBitmasks = 0xBD9E
const val CarryOne = 0x909E
const val CasPBB = 0xCE45
const val CastleBridgeObj = 0x9C03
const val CastleMetatiles = 0x999F
const val CastleMusData = 0x108EB
const val CastleMusHdr = 0x106A3
const val CastleObject = 0x99D6
const val CastlePaletteData = 0x8DF7
const val ChainObj = 0x9C10
const val ChangeSizeOffsetAdder = 0xFCB4
const val CheckAnimationStop = 0xF5A9
const val CheckBalPlatform = 0xDC37
const val CheckBowserFront = 0xF465
const val CheckBowserGfxFlag = 0xF427
const val CheckBowserRear = 0xF496
const val CheckDefeatedState = 0xF5B9
const val CheckEndofBuffer = 0xC662
const val CheckForBloober = 0xF54C
const val CheckForBulletBillCV = 0xF3E7
const val CheckForClimbMTiles = 0xE99E
const val CheckForCoinMTiles = 0xE9A5
const val CheckForDefdGoomba = 0xF51D
const val CheckForESymmetry = 0xF636
const val CheckForEnemyGroup = 0xC723
const val CheckForGoomba = 0xF438
const val CheckForHammerBro = 0xF531
const val CheckForJumping = 0xB770
const val CheckForJumpspring = 0xF403
const val CheckForLakitu = 0xF4CD
const val CheckForPUpCollision = 0xE136
const val CheckForPodoboo = 0xF410
const val CheckForRetainerObj = 0xF3CD
const val CheckForSecondFrame = 0xF5A0
const val CheckForSolidMTiles = 0xE993
const val CheckForSpiny = 0xF4B4
const val CheckForVerticalFlip = 0xF5EF
const val CheckFrenzyBuffer = 0xC751
const val CheckHalfway = 0x9165
const val CheckLeftScreenBBox = 0xEDB8
const val CheckNoiseBuffer = 0x10350
const val CheckPageCtrlRow = 0xC6A4
const val CheckPlayerName = 0x88F1
const val CheckPlayerVertical = 0xE5A4
const val CheckRear = 0x96E3
const val CheckRightBounds = 0xC67A
const val CheckRightExtBounds = 0xC6F1
const val CheckRightScreenBBox = 0xED80
const val CheckRightSideUpShell = 0xF503
const val CheckSfx1Buffer = 0x100CD
const val CheckSfx2Buffer = 0x10265
const val CheckSideMTiles = 0xE74D
const val CheckThreeBytes = 0xC793
const val CheckToAnimateEnemy = 0xF56F
const val CheckToMirrorJSpring = 0xF6F1
const val CheckToMirrorLakitu = 0xF6B0
const val CheckTopOfBlock = 0xC2AB
const val CheckUpsideDownShell = 0xF4E7
const val CheckpointEnemyID = 0xC7B2
const val ChgAreaMode = 0xB4B4
const val ChgAreaPipe = 0xB4AB
const val ChgSDir = 0xD57A
const val Chk1Row13 = 0x96AE
const val Chk1Row14 = 0x96D8
const val Chk1stB = 0x972A
const val Chk2MSBSt = 0xEB3C
const val Chk2Ofs = 0xD562
const val Chk2Players = 0x88A1
const val ChkAreaTsk = 0xC54C
const val ChkAreaType = 0x9231
const val ChkBBill = 0xEA7D
const val ChkBehPipe = 0xB2E4
const val ChkBowserF = 0xC559
const val ChkBrick = 0xC184
const val ChkBuzzyBeetle = 0xDFD2
const val ChkCFloor = 0x9A03
const val ChkCollSize = 0xE60D
const val ChkContinue = 0x8310
const val ChkDSte = 0xBE6D
const val ChkETmrs = 0xE1E0
const val ChkEmySpd = 0xD717
const val ChkEnemyFaceRight = 0xE2ED
const val ChkEnemyFrenzy = 0xC63B
const val ChkFBCl = 0xD55D
const val ChkFOfs = 0xD4D3
const val ChkFTop = 0xEF6E
const val ChkFiery = 0x8673
const val ChkFireB = 0xD8CF
const val ChkFlagOffscreen = 0xF097
const val ChkFlagpoleYPosLoop = 0xE83C
const val ChkFootMTile = 0xE6AF
const val ChkForBump_HammerBroJ = 0xEB82
const val ChkForDemoteKoopa = 0xE2A4
const val ChkForFall = 0xDC47
const val ChkForFlagpole = 0xE804
const val ChkForFloatdown = 0xD2BF
const val ChkForLandJumpSpring = 0xE8AB
const val ChkForNonSolids = 0xEC30
const val ChkForPlayerAttrib = 0xFD06
const val ChkForPlayerC_LargeP = 0xE497
const val ChkForPlayerInjury = 0xE1C3
const val ChkForRedKoopa = 0xEB2E
const val ChkForTopCollision = 0xE51B
const val ChkFrontSte = 0xF488
const val ChkGERtn = 0xE7B1
const val ChkHiByte = 0x87AB
const val ChkHoleX = 0xB43C
const val ChkInj = 0xE1CC
const val ChkInvisibleMTiles = 0xE8A3
const val ChkJH = 0xCFFF
const val ChkJumpspringMetatiles = 0xE8C5
const val ChkKillGoomba = 0xD141
const val ChkLS = 0xD656
const val ChkLak = 0xC914
const val ChkLakDif = 0xD6B0
const val ChkLandedEnemyState = 0xEAB2
const val ChkLeftCo = 0xF803
const val ChkLength = 0x96F8
const val ChkLrgObjFixedLength = 0x9DD4
const val ChkLrgObjLength = 0x9DD1
const val ChkLuigi = 0x8908
const val ChkMTLow = 0x9652
const val ChkMouth = 0xD7F5
const val ChkMoveDir = 0xB3BC
const val ChkNearMid = 0xB209
const val ChkNearPlayer = 0xD2D0
const val ChkNoEn = 0xC92D
const val ChkNumTimer = 0x8534
const val ChkOnScr = 0xE5F7
const val ChkOtherEnemies = 0xE015
const val ChkOtherForFall = 0xDC61
const val ChkOverR = 0x92CF
const val ChkPBtm = 0xE771
const val ChkPOffscr = 0xB252
const val ChkPSpeed = 0xD6DF
const val ChkPUSte = 0xC133
const val ChkPauseTimer = 0x81B1
const val ChkPlayerNearPipe = 0xDBA6
const val ChkRBit = 0xCCA9
const val ChkRFast = 0xB864
const val ChkRearSte = 0xF4A0
const val ChkRep = 0xF7C3
const val ChkRow13 = 0x9759
const val ChkRow14 = 0x9744
const val ChkSRows = 0x977E
const val ChkSelect = 0x8285
const val ChkSkid = 0xB8C9
const val ChkSmallPlatCollision = 0xDEB9
const val ChkSmallPlatLoop = 0xE4CC
const val ChkSpinyO = 0xD709
const val ChkStPos = 0x9285
const val ChkStart = 0x81BB
const val ChkStop = 0xB59E
const val ChkSwimE = 0x92EB
const val ChkSwimYPos = 0xD372
const val ChkTallEnemy = 0x8567
const val ChkToMoveBalPlat = 0xDC76
const val ChkToStunEnemies = 0xEA36
const val ChkTop = 0xC351
const val ChkUnderEnemy = 0xEC29
const val ChkUpM = 0xC503
const val ChkVFBD = 0xD53B
const val ChkW2 = 0xCC84
const val ChkWorldSel = 0x829C
const val ChkWtr = 0xB7DB
const val ChkYCenterPos = 0xDE23
const val ChkYPCollision = 0xDE35
const val Chk_BB = 0xBE0D
const val ChnkOfs = 0xF892
const val ChpChpEx = 0xC9CB
const val ClHCol = 0xE09C
const val ClearBitsMask = 0xE328
const val ClearBounceFlag = 0xEC7A
const val ClearBuffersDrawIcon = 0x87BD
const val ClearVRLoop = 0x9194
const val ClimbAdderHigh = 0xB6A7
const val ClimbAdderLow = 0xB6A3
const val ClimbFD = 0xB6EF
const val ClimbMTileUpperExt = 0xE99A
const val ClimbPLocAdder = 0xE7EF
const val ClimbXPosAdder = 0xE7ED
const val Climb_Y_MForceData = 0xB73D
const val Climb_Y_SpeedData = 0xB73A
const val ClimbingSub = 0xB6AB
const val CloudExit = 0xB452
const val ClrGetLoop = 0x8682
const val ClrMTBuf = 0x9558
const val ClrPauseTimer = 0x81DF
const val ClrPlrPal = 0xB1A8
const val ClrSndLoop = 0x90E9
const val ClrTimersLoop = 0x9101
const val CntGrp = 0xCD2F
const val CntPl = 0xFAD3
const val CoinBlock = 0xBF55
const val CoinMetatileData = 0x9BF0
const val CoinPoints = 0xC060
const val CoinSd = 0xE9B1
const val CoinTallyOffsets = 0xC034
const val ColFlg = 0xDC9F
const val ColObj = 0x9C24
const val ColdBoot = 0x8030
const val CollisionCoreLoop = 0xEDDF
const val CollisionFound = 0xEE40
const val ColorRotatePalette = 0x8A89
const val ColorRotation = 0x8AA7
const val ColumnOfBricks = 0x9C56
const val ColumnOfSolidBlocks = 0x9C5F
const val CommonPlatCode = 0xCE30
const val CommonSmallLift = 0xCE6C
const val CompDToO = 0xD883
const val ContBTmr = 0xC1A9
const val ContChk = 0xE6C3
const val ContES = 0xF64A
const val ContPau = 0xFF76
const val ContSChk = 0xE75C
const val ContVMove = 0xC439
const val Cont_CGrab_TTick = 0x1028B
const val ContinueBlast = 0x101EB
const val ContinueBowserFall = 0x1029D
const val ContinueBowserFlame = 0x10364
const val ContinueBrickShatter = 0x10311
const val ContinueBumpThrow = 0x10087
const val ContinueCGrabTTick = 0x101CF
const val ContinueExtraLife = 0x102B3
const val ContinueGame = 0x93AC
const val ContinueGrowItems = 0x102DF
const val ContinueMusic = 0x10371
const val ContinuePipeDownInj = 0x1015A
const val ContinuePowerUpGrab = 0x101FF
const val ContinueSmackEnemy = 0x10128
const val ContinueSndJump = 0x10054
const val ContinueSwimStomp = 0x10101
const val CopyFToR = 0xD914
const val CopyScore = 0x90C1
const val CreateL = 0xC93A
const val CreateSpiny = 0xC950
const val CyclePlayerPalette = 0xB537
const val CycleTwo = 0xB1A1
const val D2XPos1 = 0xCABB
const val D2XPos2 = 0xCAD3
const val DBlkLoop = 0xF79C
const val DBlockSte = 0xC15E
const val DChunks = 0xF827
const val DSFLoop = 0xDB36
const val DaySnowPaletteData = 0x8E1B
const val DeathMAltReg = 0x1052E
const val DeathMusData = 0x108B9
const val DeathMusHdr = 0x106F9
const val DecHT = 0xD030
const val DecJpFPS = 0x10094
const val DecNumTimer = 0x853F
const val DecPauC = 0xFF91
const val DecSeXM = 0xD1D5
const val DecTimers = 0x810C
const val DecTimersLoop = 0x811B
const val DecodeAreaData = 0x9721
const val DecrementSfx1Length = 0x1013F
const val DecrementSfx2Length = 0x10211
const val DecrementSfx3Length = 0x1032A
const val DefaultBlockObjTiles = 0xF77E
const val DefaultSprOffsets = 0x90CF
const val DefaultXOnscreenOfs = 0xFE23
const val DefaultYOnscreenOfs = 0xFE6E
const val DelayToAreaEnd = 0xDB6D
const val DemoActionData = 0x837D
const val DemoEngine = 0x83A8
const val DemoOver = 0x83C9
const val DemoTimingData = 0x8392
const val Demote = 0xEA4A
const val DemotedKoopaXSpdData = 0xE0FC
const val DestroyBlockMetatile = 0x8B3A
const val DifLoop = 0xC980
const val DigitPLoop = 0x904F
const val DigitsMathRoutine = 0x9069
const val DisJoyp = 0xB371
const val DisplayIntermediate = 0x8725
const val DisplayTimeUp = 0x870F
const val DivLLoop = 0x102B8
const val DividePDiff = 0xFEB1
const val DmpJpFPS = 0x1006C
const val DoAPTasks = 0x9407
const val DoAction = 0x83BF
const val DoAltLoad = 0x10536
const val DoBPl = 0xDC30
const val DoBulletBills = 0xCCD1
const val DoChangeSize = 0xFB26
const val DoEnemySideCheck = 0xEB52
const val DoFootCheck = 0xE67E
const val DoGroup = 0xC76D
const val DoIDCheckBGColl = 0xE9E6
const val DoLpBack = 0xC628
const val DoNothing1 = 0x93F6
const val DoNothing2 = 0x93FB
const val DoOtherPlatform = 0xDCB4
const val DoPlayerSideCheck = 0xE6FE
const val DoSide = 0xEBF5
const val DoneInitArea = 0x9170
const val DonePlayerTask = 0xB51F
const val DontWalk = 0x841A
const val DownJSpr = 0xBC94
const val DrawBlock = 0xF782
const val DrawBowser = 0xF493
const val DrawBrickChunks = 0xF811
const val DrawBricks = 0x9C3D
const val DrawBubble = 0xF9BB
const val DrawEnemyObjRow = 0xF759
const val DrawEnemyObject = 0xF5D2
const val DrawEraseRope = 0xDCCD
const val DrawExplosion_Fireball = 0xF8D7
const val DrawExplosion_Fireworks = 0xF8E8
const val DrawFbar = 0xD478
const val DrawFireball = 0xF8AA
const val DrawFirebar = 0xF8B9
const val DrawFirebar_Collision = 0xD49F
const val DrawFlagSetTimer = 0xDB61
const val DrawFlameLoop = 0xD9E4
const val DrawFloateyNumber_Coin = 0xF155
const val DrawHammer = 0xEFBD
const val DrawJSpr = 0xBCCC
const val DrawLargePlatform = 0xF0BA
const val DrawMTLoop = 0x896C
const val DrawMushroomIcon = 0x8360
const val DrawOneSpriteRow = 0xF762
const val DrawPipe = 0x9B1C
const val DrawPlayerLoop = 0xFBD9
const val DrawPlayer_Intermediate = 0xFB99
const val DrawPowerUp = 0xF1DB
const val DrawQBlk = 0x9D44
const val DrawRope = 0x9BEB
const val DrawRow = 0x9C4D
const val DrawSidePart = 0x9AB9
const val DrawSmallPlatform = 0xF939
const val DrawSpriteObject = 0xFECC
const val DrawStarFlag = 0xDB2E
const val DrawThisRow = 0x9DC0
const val DrawTitleScreen = 0x8783
const val DrawVine = 0xEF0B
const val DropPlatform = 0xDE71
const val DumpFall = 0xB65F
const val DumpFourSpr = 0xF0AD
const val DumpSixSpr = 0xF0A7
const val DumpThreeSpr = 0xF0B0
const val DumpTwoSpr = 0xF0B3
const val Dump_Freq_Regs = 0xFFFD
const val Dump_Sq2_Regs = 0x10010
const val Dump_Squ1_Regs = 0xFFF1
const val DuplicateEnemyObj = 0xCB21
const val ECLoop = 0xE35B
const val EColl = 0xE141
const val ELPGive = 0xDB03
const val EL_LRegs = 0x102AB
const val ESRtnr = 0xF663
const val E_CastleArea1 = 0x9FA8
const val E_CastleArea2 = 0x9FCF
const val E_CastleArea3 = 0x9FE8
const val E_CastleArea4 = 0xA017
const val E_CastleArea5 = 0xA042
const val E_CastleArea6 = 0xA057
const val E_GroundArea1 = 0xA091
const val E_GroundArea10 = 0xA1B3
const val E_GroundArea11 = 0xA1B4
const val E_GroundArea12 = 0xA1D8
const val E_GroundArea13 = 0xA1E1
const val E_GroundArea14 = 0xA206
const val E_GroundArea15 = 0xA229
const val E_GroundArea16 = 0xA232
const val E_GroundArea17 = 0xA233
const val E_GroundArea18 = 0xA26D
const val E_GroundArea19 = 0xA298
const val E_GroundArea2 = 0xA0B6
const val E_GroundArea20 = 0xA2C6
const val E_GroundArea21 = 0xA2E2
const val E_GroundArea22 = 0xA2EB
const val E_GroundArea3 = 0xA0D3
const val E_GroundArea4 = 0xA0E1
const val E_GroundArea5 = 0xA108
const val E_GroundArea6 = 0xA139
const val E_GroundArea7 = 0xA157
const val E_GroundArea8 = 0xA174
const val E_GroundArea9 = 0xA189
const val E_UndergroundArea1 = 0xA310
const val E_UndergroundArea2 = 0xA33D
const val E_UndergroundArea3 = 0xA36B
const val E_WaterArea1 = 0xA398
const val E_WaterArea2 = 0xA3A9
const val E_WaterArea3 = 0xA3D3
const val EggExc = 0xF68F
const val EmptyBlock = 0x9C1C
const val EmptyChkLoop = 0x9B46
const val EmptySfx2Buffer = 0x10217
const val EndAParse = 0x9714
const val EndAreaPoints = 0xDAF9
const val EndChgSize = 0xB4E3
const val EndChkBButton = 0x84E7
const val EndExitOne = 0x84E6
const val EndExitTwo = 0x84FF
const val EndFrenzy = 0xCDB0
const val EndGameText = 0x88BD
const val EndMushL = 0x9953
const val EndOfCastleMusData = 0x10B98
const val EndOfCastleMusicEnvData = 0x10CDD
const val EndOfEnemyInitCode = 0xCE91
const val EndOfLevelMusHdr = 0x10690
const val EndOfMusicData = 0x1044A
const val EndRp = 0xDD49
const val EndTreeL = 0x9939
const val EndUChk = 0x9639
const val EndlessLoop = 0x805C
const val EndlessRope = 0x9BD2
const val EnemiesAndLoopsCore = 0xC53D
const val EnemiesCollision = 0xE32F
const val Enemy17YPosData = 0xCC5E
const val EnemyAddrHOffsets = 0x9F18
const val EnemyAnimTimingBMask = 0xF38E
const val EnemyAttributeData = 0xF373
const val EnemyBGCStateData = 0xE9C0
const val EnemyBGCXSpdData = 0xE9C6
const val EnemyDataAddrHigh = 0x9F3E
const val EnemyDataAddrLow = 0x9F1C
const val EnemyFacePlayer = 0xE2FE
const val EnemyGfxHandler = 0xF395
const val EnemyGfxTableOffsets = 0xF358
const val EnemyGraphicsTable = 0xF256
const val EnemyJump = 0xEBD0
const val EnemyLanding = 0xEBB9
const val EnemyMovementSubs = 0xCF19
const val EnemySmackScore = 0xE054
const val EnemyStomped = 0xE24A
const val EnemyStompedPts = 0xE282
const val EnemyToBGCollisionDet = 0xE9C8
const val EnemyTurnAround = 0xE443
const val EnterSidePipe = 0xB4C0
const val EntrMode2 = 0xB2FE
const val Entrance_GameTimerSetup = 0x925E
const val ErACM = 0xE7E2
const val EraseDMods = 0x908A
const val EraseEnemyObject = 0xCFAF
const val EraseFB = 0xBA55
const val EraseMLoop = 0x908E
const val EraseR1 = 0xDD0D
const val EraseR2 = 0xDD41
const val EvalForMusic = 0x848F
const val ExAnimC = 0xFCA6
const val ExBCDr = 0xF8A9
const val ExBGfxH = 0xD950
const val ExCInvT = 0xE8AA
const val ExCJSp = 0xE8C4
const val ExCPV = 0xE5B8
const val ExCSM = 0xE7C7
const val ExCannon = 0xBE28
const val ExDBlk = 0xF810
const val ExDBub = 0xF9E3
const val ExDLPl = 0xF154
const val ExDPl = 0xDE7D
const val ExDivPD = 0xFECB
const val ExEBG = 0xE9BF
const val ExEBGChk = 0xEA94
const val ExEGHandler = 0xF758
const val ExEPar = 0xC76C
const val ExESdeC = 0xEB81
const val ExF17 = 0xCCE7
const val ExFl = 0xD981
const val ExFlmeD = 0xDA45
const val ExGTimer = 0xBB28
const val ExHC = 0xE803
const val ExHCF = 0xE05C
const val ExIPM = 0xE985
const val ExInjColRoutines = 0xE231
const val ExJCGfx = 0xF1C6
const val ExJSpring = 0xBCE9
const val ExLPC = 0xE4B4
const val ExLSHand = 0xC94F
const val ExLiftP = 0xDEC2
const val ExMoveLak = 0xD72C
const val ExNH = 0x1035E
const val ExPBGCol = 0xE60C
const val ExPEC = 0xE1C2
const val ExPF = 0xDDFC
const val ExPGH = 0xFB1F
const val ExPHC = 0xE0A1
const val ExPRp = 0xDDBF
const val ExPVne = 0xE8A2
const val ExPipeE = 0xE941
const val ExPlPos = 0xE5A3
const val ExPlyrAt = 0xFD4C
const val ExRPl = 0xDE92
const val ExS1H = 0x100F2
const val ExS2H = 0x1028A
const val ExSCH = 0xE74C
const val ExSFN = 0xE320
const val ExSPC = 0xE4FD
const val ExSPl = 0xF9B7
const val ExScrnBd = 0xDF37
const val ExSfx1 = 0x10154
const val ExSfx2 = 0x10226
const val ExSfx3 = 0x1033A
const val ExSteChk = 0xEAE1
const val ExSwCC = 0xD391
const val ExTA = 0xE476
const val ExTrans = 0x93F5
const val ExVMove = 0xC53C
const val ExXMP = 0xDE70
const val ExXMove = 0xC413
const val ExXOfsBS = 0xFE64
const val ExYOfsBS = 0xFEB0
const val ExYPl = 0xDE3E
const val ExecGameLoopback = 0xC588
const val ExitAFrenzy = 0x98FE
const val ExitBlink = 0xB4FC
const val ExitBlockChk = 0xC26F
const val ExitBoth = 0xB513
const val ExitBubl = 0xBAC4
const val ExitCAPipe = 0xB4BF
const val ExitCSub = 0xB70F
const val ExitCastle = 0x9A51
const val ExitChgSize = 0xB4EB
const val ExitChkName = 0x8918
const val ExitColorRot = 0x8B06
const val ExitCtrl = 0xB451
const val ExitDeath = 0xB554
const val ExitDecBlock = 0x9D55
const val ExitDrawM = 0x8A24
const val ExitDumpSpr = 0xF0B9
const val ExitECRoutine = 0xE3C6
const val ExitELCore = 0xC566
const val ExitEmptyChk = 0x9B53
const val ExitEng = 0xB1DC
const val ExitEntr = 0xB350
const val ExitFBallEnemy = 0xDFAC
const val ExitFWk = 0xCC55
const val ExitFlagP = 0xBC6F
const val ExitGetM = 0x9242
const val ExitIcon = 0x837C
const val ExitMenu = 0x8348
const val ExitMov1 = 0xB6A0
const val ExitMsgs = 0x84BE
const val ExitMusicHandler = 0x10602
const val ExitNA = 0xB5E9
const val ExitOutputN = 0x9068
const val ExitPUp = 0xC14D
const val ExitPause = 0x81E7
const val ExitPhy = 0xB8B4
const val ExitPipe = 0x9A91
const val ExitProcessEColl = 0xE411
const val ExitRp = 0xDD57
const val ExitUPartR = 0x9DD0
const val ExitVH = 0xBD9A
const val ExitVWalk = 0x843E
const val ExitWh = 0xBB8B
const val ExplosionTiles = 0xF8D4
const val ExtendLB = 0xDEDD
const val ExtraLifeFreqData = 0x10176
const val ExtraLifeMushBlock = 0xC25F
const val FBCLoop = 0xD52F
const val FBLeft = 0xD227
const val FBallB = 0xECCC
const val FMiscLoop = 0xBFAA
const val FPGfx = 0xBC66
const val FPS2nd = 0x1006A
const val FSLoop = 0xCB23
const val FSceneDataOffsets = 0x94FD
const val FallE = 0xD0DE
const val FallMForceData = 0xB71B
const val FallingSub = 0xB638
const val FastXSp = 0xB876
const val FeetTmr = 0xD7FE
const val FetchNoiseBeatData = 0x105AC
const val FetchSqu1MusicData = 0x104D7
const val FinCCSt = 0xCAE2
const val FindAreaMusicHeader = 0x103DD
const val FindAreaPointer = 0x9E3F
const val FindEmptyEnemySlot = 0x9B44
const val FindEmptyMiscSlot = 0xBFA8
const val FindEventMusicHeader = 0x103E2
const val FindLoop = 0xC5D8
const val FindPlayerAction = 0xFB20
const val FinishFlame = 0xCBDD
const val FireA = 0xF8D0
const val FireBulletBill = 0xCCE8
const val FireCannon = 0xBDD4
const val FireballBGCollision = 0xEC47
const val FireballEnemyCDLoop = 0xDF55
const val FireballEnemyCollision = 0xDF3B
const val FireballExplosion = 0xBA5B
const val FireballObjCore = 0xB9DB
const val FireballXSpdData = 0xB9D9
const val FirebarCollision = 0xD4FC
const val FirebarMirrorData = 0xD3F5
const val FirebarPosLookupTbl = 0xD392
const val FirebarSpin = 0xDBF8
const val FirebarSpinDirData = 0xC9D1
const val FirebarSpinSpdData = 0xC9CC
const val FirebarTblOffsets = 0xD3F9
const val FirebarYPos = 0xD405
const val FireworksSoundScore = 0xDA75
const val FireworksXPosData = 0xCBF2
const val FireworksYPosData = 0xCBF8
const val FirstBoxGreater = 0xEE1A
const val FirstSprTilenum = 0xEFB1
const val FirstSprXPos = 0xEFA1
const val FirstSprYPos = 0xEFA5
const val FlagBalls_Residual = 0x9B91
const val FlagpoleCollision = 0xE80E
const val FlagpoleGfxHandler = 0xF034
const val FlagpoleObject = 0x9B9B
const val FlagpoleRoutine = 0xBC01
const val FlagpoleScoreDigits = 0xBBFC
const val FlagpoleScoreMods = 0xBBF7
const val FlagpoleScoreNumTiles = 0xF02A
const val FlagpoleSlide = 0xB555
const val FlagpoleYPosData = 0xE7F1
const val FlameTimerData = 0xD968
const val FlameYMFAdderData = 0xCB52
const val FlameYPosData = 0xCB4E
const val FlipBowserOver = 0xF490
const val FlipEnemyVertically = 0xF61A
const val FlipPUpRightSide = 0xF243
const val FlmEx = 0xCB4D
const val FlmeAt = 0xD9DC
const val Floatdown = 0xD2C5
const val FloateyNumTileData = 0x8500
const val FloateyNumbersRoutine = 0x8524
const val FloateyPart = 0x859C
const val FlyCC = 0xD60D
const val FlyCCBPriority = 0xD5F8
const val FlyCCTimerData = 0xCA2A
const val FlyCCXPositionData = 0xCA0E
const val FlyCCXSpeedData = 0xCA1E
const val ForceHPose = 0xEFD0
const val ForceInjury = 0xE209
const val ForeSceneryData = 0x9500
const val FourFrameExtent = 0xFC7B
const val Fr12S = 0xD669
const val FreCompLoop = 0x98F2
const val FrenzyIDData = 0x98E7
const val FreqRegLookupTbl = 0x10C47
const val FrictionData = 0xB737
const val Fthrow = 0x1007D
const val GBBAdr = 0xE623
const val GMLoopB = 0x103BD
const val GSeed = 0xCA75
const val GSltLp = 0xCD34
const val GameCoreRoutine = 0xB122
const val GameEngine = 0xB137
const val GameIsOn = 0x93CA
const val GameMenuRoutine = 0x8270
const val GameMode = 0xB114
const val GameOverInter = 0x8755
const val GameOverMode = 0x935B
const val GameOverMusData = 0x1098C
const val GameOverMusHdr = 0x106AD
const val GameRoutines = 0xB2A5
const val GameText = 0x87DC
const val GameTextLoop = 0x88AD
const val GameTextOffsets = 0x8888
const val GameTimerData = 0x925A
const val GameTimerFireworks = 0xDAAD
const val Get17ID = 0xCC8D
const val GetAltOffset = 0x8593
const val GetAlternatePalette1 = 0x86BE
const val GetAreaDataAddrs = 0x9E4E
const val GetAreaMusic = 0x9214
const val GetAreaObjXPosition = 0x9DF4
const val GetAreaObjYPosition = 0x9DFC
const val GetAreaObjectID = 0x9D4E
const val GetAreaPal = 0x8AD3
const val GetAreaPalette = 0x8633
const val GetAreaType = 0x9E35
const val GetBackgroundColor = 0x8657
const val GetBlankPal = 0x8AB8
const val GetBlockBufferAddr = 0x9E0B
const val GetBlockOffscreenBits = 0xFDDF
const val GetBubbleOffscreenBits = 0xFDBA
const val GetCent = 0xC8AB
const val GetCurrentAnimOffset = 0xFC75
const val GetDToO = 0xD861
const val GetESpd = 0xC865
const val GetEnemyBoundBox = 0xECD2
const val GetEnemyBoundBoxOfs = 0xE5B9
const val GetEnemyBoundBoxOfsArg = 0xE5BC
const val GetEnemyOffscreenBits = 0xFDD8
const val GetFireballBoundBox = 0xECBB
const val GetFireballOffscreenBits = 0xFDB0
const val GetFirebarPosition = 0xD5A1
const val GetGfxOffsetAdder = 0xFCA8
const val GetHAdder = 0xD5AE
const val GetHPose = 0xEFD5
const val GetHRp = 0xDD89
const val GetHalfway = 0x9336
const val GetLRp = 0xDD6B
const val GetLength = 0x8FAB
const val GetLrgObjAttrib = 0x9DE1
const val GetMTileAttrib = 0xE9B7
const val GetMaskedOffScrBits = 0xECE3
const val GetMiscBoundBox = 0xECC5
const val GetMiscOffscreenBits = 0xFDC4
const val GetObjRelativePosition = 0xFD98
const val GetOffScreenBitsSet = 0xFDEB
const val GetOffsetFromAnimCtrl = 0xFCEC
const val GetPRCmp = 0xD844
const val GetPipeHeight = 0x9B32
const val GetPlayerAnimSpeed = 0xB8B8
const val GetPlayerColors = 0x8666
const val GetPlayerOffscreenBits = 0xFDA9
const val GetProperObjOffset = 0xFDD1
const val GetRBit = 0xCCA4
const val GetRow = 0x9C49
const val GetRow2 = 0x9C65
const val GetSBNybbles = 0xC06E
const val GetScoreDiff = 0x90B1
const val GetScreenPosition = 0xB293
const val GetSteFromD = 0xEB4C
const val GetVAdder = 0xD5D3
const val GetWNum = 0xE918
const val GetXOffscreenBits = 0xFE26
const val GetXPhy = 0xB881
const val GetXPhy2 = 0xB891
const val GetYOffscreenBits = 0xFE73
const val GetYPhy = 0xB7EF
const val GiveFPScr = 0xBC52
const val GiveOEPoints = 0xEA31
const val GiveOneCoin = 0xC03A
const val GmbaAnim = 0xF44D
const val GndMove = 0xB62E
const val GoContinue = 0x8349
const val GoombaDie = 0xDF75
const val GoombaPoints = 0xE04D
const val GorSLog = 0xFCE1
const val GrLoop = 0xCD32
const val GroundLevelLeadInHdr = 0x106E1
const val GroundLevelPart1Hdr = 0x106BD
const val GroundLevelPart2AHdr = 0x106C3
const val GroundLevelPart2BHdr = 0x106C9
const val GroundLevelPart2CHdr = 0x106CF
const val GroundLevelPart3AHdr = 0x106D5
const val GroundLevelPart3BHdr = 0x106DB
const val GroundLevelPart4AHdr = 0x106E7
const val GroundLevelPart4BHdr = 0x106ED
const val GroundLevelPart4CHdr = 0x106F3
const val GroundMLdInData = 0x10840
const val GroundM_P1Data = 0x10748
const val GroundM_P2AData = 0x10790
const val GroundM_P2BData = 0x107BC
const val GroundM_P2CData = 0x107E4
const val GroundM_P3AData = 0x10809
const val GroundM_P3BData = 0x10822
const val GroundM_P4AData = 0x1086C
const val GroundM_P4BData = 0x10892
const val GroundM_P4CData = 0x108BB
const val GroundPaletteData = 0x8DAF
const val GrowItemRegs = 0x102D2
const val GrowThePowerUp = 0xC10B
const val HBChk = 0xE9EE
const val HBlankDelay = 0x8171
const val HBroWalkingTimerData = 0xC877
const val HJump = 0xD07F
const val HalfwayPageNybbles = 0x92FA
const val HammerBroBGColl = 0xEBF8
const val HammerBroJumpCode = 0xD038
const val HammerBroJumpLData = 0xD036
const val HammerChk = 0xD88C
const val HammerEnemyOfsData = 0xBE8E
const val HammerSprAttrib = 0xEFB9
const val HammerThrowTmrData = 0xCFEA
const val HammerXSpdData = 0xBE97
const val HandleAreaMusicLoopB = 0x103C0
const val HandleAxeMetatile = 0xE7D3
const val HandleChangeSize = 0xFCC8
const val HandleClimbing = 0xE7F6
const val HandleCoinMetatile = 0xE7CA
const val HandleEToBGCollision = 0xEA0E
const val HandleEnemyFBallCol = 0xDFB8
const val HandleGroupEnemies = 0xCCF5
const val HandleNoiseMusic = 0x1059E
const val HandlePECollisions = 0xE14D
const val HandlePipeEntry = 0xE8D2
const val HandlePowerUpCollision = 0xE0A2
const val HandleSquare1Music = 0x104CB
const val HandleSquare2Music = 0x10432
const val HandleStompedShellE = 0xE2C9
const val HandleTriangleMusic = 0x10539
const val HeadChk = 0xE634
const val Hidden1UpBlock = 0x9D15
const val Hidden1UpCoinAmts = 0xB579
const val HighPosUnitData = 0xFE71
const val HoleBottom = 0xB437
const val HoleDie = 0xB421
const val HoleMetatiles = 0x9D56
const val Hole_Empty = 0x9D5A
const val Hole_Water = 0x9B54
const val HurtBowser = 0xDFDF
const val ISpr0Loop = 0x91D5
const val IconDataRead = 0x8362
const val ImpedePlayerMove = 0xE942
const val ImposeFriction = 0xB8FE
const val ImposeGravity = 0xC4B2
const val ImposeGravityBlock = 0xC477
const val ImposeGravitySprObj = 0xC481
const val InCastle = 0xB5AE
const val InPause = 0xFF47
const val Inc2B = 0xC7A3
const val Inc3B = 0xC7A0
const val IncAreaObjOffset = 0x9715
const val IncMLoop = 0xC60A
const val IncModeTask_A = 0x84BB
const val IncModeTask_B = 0x87D8
const val IncMsgCounter = 0x84A0
const val IncPXM = 0xD1D1
const val IncSubtask = 0x87CF
const val IncWorldSel = 0x82D1
const val IncrementColumnPos = 0x9429
const val IncrementSFTask1 = 0xDACD
const val IncrementSFTask2 = 0xDB69
const val InitATLoop = 0x8F3C
const val InitBalPlatform = 0xCDDC
const val InitBlock_XY_Pos = 0xC1FE
const val InitBloober = 0xC894
const val InitBowser = 0xCAF3
const val InitBowserFlame = 0xCB54
const val InitBuffer = 0x80D9
const val InitBulletBill = 0xC8C5
const val InitByte = 0x9200
const val InitByteLoop = 0x91F6
const val InitCSTimer = 0xB710
const val InitChangeSize = 0xB4FF
const val InitCheepCheep = 0xC8D0
const val InitDropPlatform = 0xCE06
const val InitEnemyFrenzy = 0xCD97
const val InitEnemyObject = 0xC764
const val InitEnemyRoutines = 0xC7C9
const val InitFireballExplode = 0xEC80
const val InitFireworks = 0xCBFE
const val InitFlyingCheepCheep = 0xCA2E
const val InitGoomba = 0xC83B
const val InitHammerBro = 0xC879
const val InitHoriPlatform = 0xCE0E
const val InitHorizFlySwimEnemy = 0xC88F
const val InitJS = 0xB7A2
const val InitJumpGPTroopa = 0xCDCC
const val InitLCmd = 0xC636
const val InitLakitu = 0xC8E2
const val InitLongFirebar = 0xC9D6
const val InitMForceData = 0xB729
const val InitMLp = 0xC62E
const val InitNTLoop = 0x8F28
const val InitNormalEnemy = 0xC85C
const val InitPageLoop = 0x91F3
const val InitPiranhaPlant = 0xCD7A
const val InitPlatScrl = 0xB289
const val InitPlatformFall = 0xDDC0
const val InitPodoboo = 0xC841
const val InitRear = 0x97E4
const val InitRedKoopa = 0xC86E
const val InitRedPTroopa = 0xC89E
const val InitRetainerObj = 0xC854
const val InitScores = 0x8341
const val InitScreen = 0x85FE
const val InitScrlAmt = 0xB24D
const val InitScroll = 0x8FE7
const val InitShortFirebar = 0xC9D9
const val InitSteP = 0xE6F9
const val InitVStf = 0xC8BC
const val InitVertPlatform = 0xCE16
const val InitializeArea = 0x90F8
const val InitializeGame = 0x90E2
const val InitializeMemory = 0x91EC
const val InitializeNameTables = 0x8F06
const val InjurePlayer = 0xE203
const val IntermediatePlayerData = 0xFB93
const val IntroEntr = 0xB2EF
const val IntroPipe = 0x9A66
const val InvEnemyDir = 0xEBA5
const val InvOBit = 0xC1F5
const val InvtD = 0xEB01
const val JCoinC = 0xBF8E
const val JCoinGfxHandler = 0xF18A
const val JCoinRun = 0xBFFD
const val JSFnd = 0xE8D0
const val JSMove = 0xB68D
const val JmpEO = 0xCEA2
const val JoypFrict = 0xB912
const val JumpEngine = 0x8EEB
const val JumpMForceData = 0xB714
const val JumpRegContents = 0x10048
const val JumpSwimSub = 0xB641
const val JumpToDecLength2 = 0x1028E
const val JumpingCoinTiles = 0xF186
const val Jumpspring = 0x9CDF
const val JumpspringFrameOffsets = 0xF390
const val JumpspringHandler = 0xBC74
const val Jumpspring_Y_PosData = 0xBC70
const val KSPts = 0xE1BF
const val KeepOnscr = 0xB269
const val KickedShellPtsData = 0xE14A
const val KickedShellXSpdData = 0xE0FA
const val KillAllEnemies = 0xD7D7
const val KillBB = 0xBE8A
const val KillBlock = 0xC37C
const val KillELoop = 0x98D6
const val KillEnemies = 0x98CF
const val KillEnemyAboveBlock = 0xEC03
const val KillFireBall = 0xF933
const val KillLakitu = 0xC8F3
const val KillLoop = 0xD7D9
const val KillPlayer = 0xE235
const val KillVine = 0xBD66
const val KilledAtt = 0xFD28
const val LInj = 0xE2F8
const val LLeft = 0x89DD
const val LRAir = 0xB684
const val LRWater = 0xB67B
const val L_CastleArea1 = 0xA3E7
const val L_CastleArea2 = 0xA448
const val L_CastleArea3 = 0xA4C7
const val L_CastleArea4 = 0xA53A
const val L_CastleArea5 = 0xA5A7
const val L_CastleArea6 = 0xA632
const val L_GroundArea1 = 0xA6A3
const val L_GroundArea10 = 0xAA6A
const val L_GroundArea11 = 0xAA73
const val L_GroundArea12 = 0xAAB2
const val L_GroundArea13 = 0xAAC7
const val L_GroundArea14 = 0xAB2E
const val L_GroundArea15 = 0xAB93
const val L_GroundArea16 = 0xAC06
const val L_GroundArea17 = 0xAC37
const val L_GroundArea18 = 0xACCA
const val L_GroundArea19 = 0xAD3D
const val L_GroundArea2 = 0xA706
const val L_GroundArea20 = 0xADB6
const val L_GroundArea21 = 0xAE0F
const val L_GroundArea22 = 0xAE3A
const val L_GroundArea3 = 0xA76F
const val L_GroundArea4 = 0xA7C2
const val L_GroundArea5 = 0xA851
const val L_GroundArea6 = 0xA8C6
const val L_GroundArea7 = 0xA92B
const val L_GroundArea8 = 0xA980
const val L_GroundArea9 = 0xAA05
const val L_UndergroundArea1 = 0xAE6D
const val L_UndergroundArea2 = 0xAF10
const val L_UndergroundArea3 = 0xAFB1
const val L_WaterArea1 = 0xB03E
const val L_WaterArea2 = 0xB07D
const val L_WaterArea3 = 0xB0F8
const val LakituAndSpinyHandler = 0xC902
const val LakituChk = 0xCDB2
const val LakituDiffAdj = 0xD648
const val LandEnemyInitState = 0xEB14
const val LandEnemyProperly = 0xEA95
const val LandPlyr = 0xE6E0
const val LargeLiftBBox = 0xCE52
const val LargeLiftDown = 0xCE4F
const val LargeLiftUp = 0xCE49
const val LargePlatformBoundBox = 0xED0C
const val LargePlatformCollision = 0xE477
const val LargePlatformSubroutines = 0xCF98
const val LcChk = 0xF71B
const val LdGameText = 0x88A8
const val LdLDa = 0xD670
const val LeavePar = 0x97E3
const val LeftFrict = 0xB916
const val LeftSwim = 0xD25F
const val LeftWh = 0xBBCD
const val LenSet = 0x9DE0
const val LimitB = 0xDEDB
const val LoadAreaMusic = 0x103B3
const val LoadAreaPointer = 0x9E2F
const val LoadControlRegs = 0x10617
const val LoadEnvelopeData = 0x10638
const val LoadEventMusic = 0x1038A
const val LoadHeader = 0x103E7
const val LoadNumTiles = 0x8554
const val LoadSqu2Regs = 0x1020E
const val LoadTriCtrlReg = 0x1059B
const val LoadUsualEnvData = 0x10644
const val LoadWaterEventMusEnvData = 0x10650
const val LongBeat = 0x105EE
const val LongN = 0x10599
const val LoopCmdE = 0x97F5
const val LoopCmdPageNumber = 0xC572
const val LoopCmdWorldNumber = 0xC567
const val LoopCmdYPosition = 0xC57D
const val LrgObj = 0x9799
const val LuigiName = 0x8877
const val LuigiThanksMessage = 0x8E4F
const val M1FOfs = 0xDA3B
const val M2FOfs = 0xDA30
const val M3FOfs = 0xDA25
const val MEHor = 0xD0FB
const val MRetainerMsg = 0x8468
const val MakeBJump = 0xD8BF
const val MakePlatformFall = 0xDC5E
const val MarioThanksMessage = 0x8E3B
const val Mask2MSB = 0x9779
const val MaskHPNyb = 0x9345
const val MatchBump = 0xC28B
const val MaxCC = 0xCA4C
const val MaxLeftXSpdData = 0xB730
const val MaxRightXSpdData = 0xB733
const val MaxSpdBlockData = 0xC472
const val MediN = 0x10594
const val MetatileGraphics_High = 0x8BF3
const val MetatileGraphics_Low = 0x8BEF
const val MidTreeL = 0x992C
const val MirrorEnemyGfx = 0xF672
const val MiscLoop = 0xBFBE
const val MiscLoopBack = 0xC02F
const val MiscObjectsCore = 0xBFBC
const val MiscSqu1MusicTasks = 0x10511
const val MiscSqu2MusicTasks = 0x104A9
const val MovPTDwn = 0xD185
const val MoveAOId = 0x97BA
const val MoveAllSpritesOffscreen = 0x824A
const val MoveBloober = 0xD208
const val MoveBoundBox = 0xE4E5
const val MoveBoundBoxOffscreen = 0xED23
const val MoveBubl = 0xBAA8
const val MoveBulletBill = 0xD2E1
const val MoveColOffscreen = 0xF808
const val MoveD_Bowser = 0xD768
const val MoveD_EnemyVertically = 0xC42D
const val MoveDefeatedBloober = 0xD272
const val MoveDefeatedEnemy = 0xD13B
const val MoveDropPlatform = 0xC457
const val MoveEOfs = 0xE43B
const val MoveESprColOffscreen = 0xF772
const val MoveESprRowOffscreen = 0xF768
const val MoveEnemyHorizontally = 0xC3B8
const val MoveEnemySlowVert = 0xC45C
const val MoveFallingPlatform = 0xC437
const val MoveFlyGreenPTroopa = 0xD188
const val MoveFlyingCheepCheep = 0xD5FD
const val MoveHammerBroXDir = 0xD08D
const val MoveJ_EnemyVertically = 0xC463
const val MoveJumpingEnemy = 0xD152
const val MoveLakitu = 0xD64B
const val MoveLargeLiftPlat = 0xDE93
const val MoveLiftPlatforms = 0xDE9F
const val MoveNormalEnemy = 0xD0B3
const val MoveObjectHorizontally = 0xC3C7
const val MoveOnVine = 0xB6BE
const val MovePiranhaPlant = 0xDB7D
const val MovePlatformDown = 0xC489
const val MovePlatformUp = 0xC48C
const val MovePlayerHorizontally = 0xC3C0
const val MovePlayerVertically = 0xC414
const val MovePlayerYAxis = 0xB49E
const val MovePodoboo = 0xCFCA
const val MoveRedPTUpOrDown = 0xD179
const val MoveRedPTroopa = 0xC443
const val MoveRedPTroopaDown = 0xC43C
const val MoveRedPTroopaUp = 0xC441
const val MoveSixSpritesOffscreen = 0xF0A5
const val MoveSmallPlatform = 0xDE99
const val MoveSpritesOffscreen = 0x824D
const val MoveSubs = 0xB616
const val MoveSwimmingCheepCheep = 0xD2F8
const val MoveVOffset = 0x8B63
const val MoveWithXMCntrs = 0xD1DD
const val MtchF = 0xE846
const val MushFlowerBlock = 0xC259
const val MushLExit = 0x999E
const val MushroomIconData = 0x8358
const val MushroomLedge = 0x993E
const val MushroomPaletteData = 0x8E2B
const val MushroomRetainerSaved = 0x8E63
const val MusicHandler = 0x10374
const val MusicHeaderData = 0x10654
const val MusicLengthLookupTbl = 0x10CAD
const val MusicLoopBack = 0x10479
const val MusicSelectData = 0x920E
const val N2Prt = 0x10063
const val N2Tone = 0x101DC
const val NKGmba = 0xD151
const val NMovShellFallBit = 0xEB25
const val NPROffscr = 0xFB89
const val NSFnd = 0xEC46
const val NVFLak = 0xF6E1
const val NXSpd = 0xE962
const val NYSpd = 0xE679
const val NameLoop = 0x890E
const val Next3Slt = 0xBE24
const val NextAObj = 0x96F5
const val NextArea = 0xB5D5
const val NextBUpd = 0xC3B3
const val NextED = 0xCD77
const val NextFSlot = 0xCDBF
const val NextFbar = 0xD492
const val NextMTRow = 0x89E3
const val NextSdeC = 0xEB78
const val NextSprOffset = 0x820C
const val NextStair = 0x9CCD
const val NextSubtask = 0x863C
const val NextTBit = 0x9621
const val NextVO = 0xBD0F
const val NextVSp = 0xEF7F
const val NextWh = 0xBB87
const val NightSnowPaletteData = 0x8E23
const val NoAltPal = 0x86CB
const val NoBFall = 0xD7C0
const val NoBGColor = 0x8663
const val NoBlankP = 0x9A84
const val NoBump = 0xEB93
const val NoCDirF = 0xEA8D
const val NoChgMus = 0xB194
const val NoCloud2 = 0x9610
const val NoColFB = 0xD596
const val NoColWrap = 0x943A
const val NoCollisionFound = 0xEE3B
const val NoDecEnv1 = 0x104C0
const val NoDecEnv2 = 0x10528
const val NoDecTimers = 0x8128
const val NoEToBGCollision = 0xEA0B
const val NoEnemyCollision = 0xE3B4
const val NoFBall = 0xBA5A
const val NoFD = 0xD2CF
const val NoFPObj = 0xB575
const val NoFToECol = 0xDFA3
const val NoFore = 0x95C4
const val NoFrenzyCode = 0xCDAF
const val NoHFlip = 0xFEE5
const val NoHOffscr = 0xF029
const val NoHammer = 0xBEC9
const val NoIncDAC = 0xFFE6
const val NoIncPT = 0xD178
const val NoIncXM = 0xD1D0
const val NoInitCode = 0xC83A
const val NoInter = 0x8762
const val NoJSChk = 0xC422
const val NoJSFnd = 0xE8D1
const val NoJump = 0xB784
const val NoKillE = 0x98E2
const val NoLAFr = 0xF4E4
const val NoMGPT = 0xD1AF
const val NoMoveCode = 0xCF49
const val NoMoveSub = 0xB621
const val NoOfs = 0xEDB4
const val NoOfs2 = 0xEDD1
const val NoPDwnL = 0x10173
const val NoPECol = 0xE135
const val NoPUp = 0xE0F7
const val NoReset = 0x8946
const val NoRunCode = 0xCEE9
const val NoSSw = 0xD2BE
const val NoSideC = 0xE56D
const val NoStop1 = 0x103BB
const val NoStopSfx = 0x10398
const val NoTTick = 0xDAEA
const val NoTimeUp = 0x871F
const val NoTone = 0x1000F
const val NoTopSc = 0x90CE
const val NoUnder = 0x997D
const val NoUnderHammerBro = 0xEC20
const val NoWhirlP = 0x9D8F
const val NoZSup = 0xC085
const val NoiseBeatHandler = 0x105C1
const val NoiseSfxHandler = 0x1033B
const val NonAnimatedActs = 0xFC36
const val NonMaskableInterrupt = 0x8087
const val NormObj = 0x97BE
const val NormalXSpdData = 0xC85A
const val NotDOrD4 = 0x10581
const val NotDefB = 0xD2EC
const val NotECstlM = 0x10624
const val NotEgg = 0xF4CA
const val NotGoomba = 0xDF82
const val NotRsNum = 0xF15F
const val NotTRO = 0x10458
const val NotTall = 0x9A23
const val NotUse = 0xC790
const val NotWPipe = 0x97AE
const val NullJoypad = 0x82F1
const val ObjOffsetData = 0xFDCE
const val OffVine = 0xB32E
const val OffscrJoypadBitsData = 0xB291
const val OffscreenBoundsCheck = 0xDEC3
const val OnGroundStateSub = 0xB622
const val OnePlayerGameOver = 0x883D
const val OnePlayerTimeUp = 0x882A
const val OperModeExecutionTree = 0x823C
const val OtherRope = 0xDD15
const val OutputCol = 0x877D
const val OutputInter = 0x8749
const val OutputNumbers = 0x9014
const val OutputTScr = 0x879E
const val OutputToVRAM = 0x8FAE
const val PBFRegs = 0x102A9
const val PIntLoop = 0xFB9B
const val PJumpSnd = 0xB822
const val PPHSubt = 0xDE65
const val PRDiffAdjustData = 0xC8F6
const val PROfsLoop = 0xFB7E
const val PRandomRange = 0xD7C3
const val PRandomSubtracter = 0xD5F3
const val PTRegC = 0xFF8A
const val PTone1F = 0xFF71
const val PTone2F = 0xFF88
const val PUpDrawLoop = 0xF206
const val PUpOfs = 0xF253
const val PUp_VGrow_FreqData = 0x1019A
const val Palette0_MTiles = 0x8BF7
const val Palette1_MTiles = 0x8C93
const val Palette2_MTiles = 0x8D4B
const val Palette3Data = 0x8A97
const val Palette3_MTiles = 0x8D73
const val ParseRow0e = 0xC770
const val PauseRoutine = 0x819C
const val PauseSkip = 0x812B
const val PdbM = 0xCFE7
const val PerformWalk = 0x8416
const val PipeDwnS = 0xE78A
const val PlatDn = 0xDCB1
const val PlatF = 0xE972
const val PlatLiftDown = 0xCE62
const val PlatLiftUp = 0xCE55
const val PlatPosDataHigh = 0xCE7A
const val PlatPosDataLow = 0xCE77
const val PlatSt = 0xDCAB
const val PlatUp = 0xDCA5
const val PlatformFall = 0xDDE5
const val PlatformSideCollisions = 0xE54A
const val PlayBeat = 0x105F9
const val PlayBigJump = 0x10046
const val PlayBlast = 0x101DF
const val PlayBowserFall = 0x10291
const val PlayBowserFlame = 0x1035F
const val PlayBrickShatter = 0x1030C
const val PlayBump = 0x10079
const val PlayCoinGrab = 0x101BA
const val PlayExtraLife = 0x102AE
const val PlayFireballThrow = 0x10072
const val PlayFlagpoleSlide = 0x10032
const val PlayGrowPowerUp = 0x102CB
const val PlayGrowVine = 0x102D0
const val PlayNoiseSfx = 0x1031F
const val PlayPipeDownInj = 0x10155
const val PlayPowerUpGrab = 0x101FA
const val PlaySmackEnemy = 0x10117
const val PlaySmallJump = 0x10041
const val PlaySqu1Sfx = 0xFFF8
const val PlaySqu2Sfx = 0x10017
const val PlaySwimStomp = 0x100F3
const val PlayTimerTick = 0x101C1
const val PlayerAnimTmrData = 0xB8B5
const val PlayerBGCollision = 0xE5CC
const val PlayerBGPriorityData = 0x9252
const val PlayerBGUpperExtent = 0xE5CA
const val PlayerChangeSize = 0xB4D8
const val PlayerCollisionCore = 0xEDD5
const val PlayerColors = 0x864B
const val PlayerCtrlRoutine = 0xB354
const val PlayerDeath = 0xB514
const val PlayerEndLevel = 0xB581
const val PlayerEndWorld = 0x84BF
const val PlayerEnemyCollision = 0xE0FE
const val PlayerEnemyDiff = 0xEBA8
const val PlayerEntrance = 0xB2C5
const val PlayerFireFlower = 0xB52A
const val PlayerGfxHandler = 0xFAC6
const val PlayerGfxProcessing = 0xFB31
const val PlayerGfxTblOffsets = 0xF9E4
const val PlayerGraphicsTable = 0xF9F4
const val PlayerHammerCollision = 0xE05D
const val PlayerHeadCollision = 0xC150
const val PlayerHole = 0xB401
const val PlayerInjuryBlink = 0xB4EC
const val PlayerInter = 0x8744
const val PlayerKilled = 0xFB2C
const val PlayerLakituDiff = 0xD69C
const val PlayerLoseLife = 0x930A
const val PlayerMovementSubs = 0xB5EA
const val PlayerOffscreenChk = 0xFB6B
const val PlayerPhysicsSub = 0xB740
const val PlayerPosSPlatData = 0xE571
const val PlayerRdy = 0xB33C
const val PlayerStarting_X_Pos = 0x9243
const val PlayerStarting_Y_Pos = 0x9249
const val PlayerStop = 0x9A4C
const val PlayerSubs = 0xB3CE
const val PlayerVictoryWalk = 0x8400
const val PlayerYSpdData = 0xB722
const val PlyrPipe = 0xE795
const val PortLoop = 0x8F5C
const val PosBubl = 0xBA82
const val PosJSpr = 0xBC9A
const val PosPlatform = 0xCE7D
const val PositionEnemyObj = 0xC6CA
const val PositionPlayerOnHPlat = 0xDE4D
const val PositionPlayerOnS_Plat = 0xE573
const val PositionPlayerOnVPlat = 0xE57C
const val PowerUpAttributes = 0xF1D7
const val PowerUpGfxTable = 0xF1C7
const val PowerUpGrabFreqData = 0x1017C
const val PowerUpObjHandler = 0xC0D4
const val PrimaryGameSetup = 0x917E
const val PrincessSaved1 = 0x8E8F
const val PrincessSaved2 = 0x8EA6
const val PrintMsg = 0x8499
const val PrintStatusBarNumbers = 0x9007
const val PrintVictoryMessages = 0x8445
const val PrintWarpZoneNumbers = 0x8919
const val ProcADLoop = 0x9681
const val ProcAirBubbles = 0xB9BD
const val ProcBowserFlame = 0xD982
const val ProcClimb = 0xB75A
const val ProcELoop = 0xB13C
const val ProcEnemyCollisions = 0xE3CA
const val ProcEnemyDirection = 0xEAE2
const val ProcFireball_Bubble = 0xB965
const val ProcFireballs = 0xB9B3
const val ProcFirebar = 0xD407
const val ProcHammerBro = 0xCFF4
const val ProcHammerObj = 0xBECE
const val ProcJumpCoin = 0xBFD1
const val ProcJumping = 0xB787
const val ProcLPlatCollisions = 0xE504
const val ProcLoopCommand = 0xC5CA
const val ProcLoopb = 0x9704
const val ProcMove = 0xB600
const val ProcMoveRedPTroopa = 0xD158
const val ProcOnGroundActs = 0xFC11
const val ProcPRun = 0xB845
const val ProcSPlatCollisions = 0xE501
const val ProcSecondEnemyColl = 0xE412
const val ProcSkid = 0xB8E1
const val ProcSwim = 0xB665
const val ProcSwimmingB = 0xD275
const val ProcessAreaData = 0x967F
const val ProcessBowserHalf = 0xD951
const val ProcessCannons = 0xBDA0
const val ProcessEnemyData = 0xC654
const val ProcessLengthData = 0x10609
const val ProcessPlayerAction = 0xFBEC
const val ProcessWhirlpools = 0xBB41
const val PullID = 0xCD0B
const val PullOfsB = 0xF802
const val PulleyRopeMetatiles = 0x9985
const val PulleyRopeObject = 0x9988
const val PutAtRightExtent = 0xCB8E
const val PutBehind = 0xC0C9
const val PutBlockMetatile = 0x8B6B
const val PutLives = 0x88DF
const val PutMTileB = 0xC1B2
const val PutOldMT = 0xC1B1
const val PutPlayerOnVine = 0xE863
const val PutinPipe = 0xDBF2
const val PwrUpJmp = 0xC0A8
const val QuestionBlock = 0x9D23
const val QuestionBlockRow_High = 0x9B65
const val QuestionBlockRow_Low = 0x9B68
const val RImpd = 0xE959
const val RSeed = 0xCA8D
const val RXSpd = 0xE464
const val RaiseFlagSetoffFWorks = 0xDB12
const val RdyDecode = 0x96EC
const val RdyNextA = 0xB5B3
const val ReadJoypads = 0x8F4C
const val ReadPortBits = 0x8F5A
const val ReadyNextEnemy = 0xE3BD
const val RedPTroopaGrav = 0xC4AB
const val RelWOfs = 0xFD65
const val RelativeBlockPosition = 0xFD7D
const val RelativeBubblePosition = 0xFD54
const val RelativeEnemyPosition = 0xFD76
const val RelativeFireballPosition = 0xFD5E
const val RelativeMiscPosition = 0xFD6C
const val RelativePlayerPosition = 0xFD4D
const val RemBridge = 0x8BB1
const val RemoveBridge = 0xD76E
const val RemoveCoin_Axe = 0x8B1B
const val RendBBuf = 0x9645
const val RendBack = 0x9573
const val RendFore = 0x95B0
const val RendTerr = 0x95CB
const val RenderAreaGraphics = 0x8947
const val RenderAttributeTables = 0x8A27
const val RenderH = 0xEFDD
const val RenderPlayerSub = 0xFBB5
const val RenderPul = 0x9998
const val RenderSceneryTerrain = 0x9554
const val RenderSidewaysPipe = 0x9A99
const val RenderUnderPart = 0x9D99
const val RepeatByte = 0x8FB2
const val ReplaceBlockMetatile = 0x8B30
const val ResGTCtrl = 0xBB0B
const val ResJmpM = 0xEE68
const val ResetMDr = 0xD811
const val ResetPalFireFlower = 0xB548
const val ResetPalStar = 0xB54B
const val ResetScreenTimer = 0x893E
const val ResetSpritesAndScreenTimer = 0x8935
const val ResetTitle = 0x8301
const val ResidualGravityCode = 0xC474
const val ResidualHeaderData = 0x10695
const val ResidualMiscObjectCode = 0xEE57
const val ResidualXSpdData = 0xE0F8
const val Rest = 0x1049D
const val RetEOfs = 0xC94C
const val RetXC = 0xEEFC
const val RetYC = 0xEEFF
const val ReversePlantSpeed = 0xDBAE
const val RevivalRateData = 0xE2C7
const val ReviveStunned = 0xD118
const val RevivedXSpeed = 0xCFF0
const val RghtFrict = 0xB938
const val RightCheck = 0x89C7
const val RightPipe = 0xB4D3
const val RightPlatform = 0xDE7E
const val RiseFallPiranhaPlant = 0xDBC8
const val RotPRandomBit = 0x8144
const val Row23C = 0xF732
const val Row3C = 0xF726
const val RowOfBricks = 0x9C32
const val RowOfCoins = 0x9BF4
const val RowOfSolidBlocks = 0x9C43
const val RunAObj = 0x9811
const val RunAllH = 0xBF45
const val RunBBSubs = 0xBE7B
const val RunBowser = 0xD7C7
const val RunBowserFlame = 0xCF4A
const val RunDemo = 0x82F6
const val RunEnemyObjectsCore = 0xCE92
const val RunFB = 0xBA21
const val RunFR = 0xE849
const val RunFirebarObj = 0xCF5C
const val RunFireworks = 0xDA46
const val RunGameOver = 0x937B
const val RunGameTimer = 0xBAC9
const val RunHSubs = 0xBF48
const val RunJCSubs = 0xC023
const val RunLargePlatform = 0xCF7A
const val RunNormalEnemies = 0xCEF3
const val RunOffscrBitsSubs = 0xFE06
const val RunPUSubs = 0xC13B
const val RunParser = 0xB1D9
const val RunRetainerObj = 0xCEEA
const val RunSmallPlatform = 0xCF62
const val RunSoundSubroutines = 0xFFB1
const val RunStarFlagObj = 0xDA93
const val RunVSubs = 0xBD43
const val SBBAt = 0xF3F9
const val SBMDir = 0xD230
const val SBlasJ = 0x101F7
const val SBnce = 0xE2E7
const val SBwsrGfxOfs = 0xF435
const val SChk2 = 0xF114
const val SChk3 = 0xF11F
const val SChk4 = 0xF12A
const val SChk5 = 0xF135
const val SChk6 = 0xF140
const val SChkA = 0xEAAF
const val SFcRt = 0xE307
const val SFlmX = 0xD992
const val SJumpSnd = 0xB82C
const val SLChk = 0xF14A
const val SOLft = 0xEDCE
const val SORte = 0xEDB1
const val SOfs = 0xF99A
const val SOfs2 = 0xF9A9
const val SPBBox = 0xCE33
const val SPixelLak = 0xD725
const val SUpdR = 0xFB67
const val Save8Bits = 0x8F81
const val SaveAB = 0xB1AB
const val SaveHAdder = 0x8B80
const val SaveJoyp = 0xB376
const val SaveXSpd = 0xC3DF
const val SceLoop1 = 0x959D
const val SceLoop2 = 0x95BB
const val ScoreOffsets = 0xC036
const val ScoreUpdateData = 0x8518
const val ScreenOff = 0x80A4
const val ScreenRoutines = 0x85DA
const val ScrollHandler = 0xB1DD
const val ScrollLockObject = 0x98C6
const val ScrollLockObject_Warp = 0x98A9
const val ScrollScreen = 0xB214
const val SdeCLoop = 0xEB61
const val SecondBoxVerticalChk = 0xEE04
const val SecondPartMsg = 0x847B
const val SecondSprTilenum = 0xEFB5
const val SecondSprXPos = 0xEFA9
const val SecondSprYPos = 0xEFAD
const val SecondaryGameSetup = 0x918E
const val SelectBLogic = 0x82A8
const val Set17ID = 0xCC94
const val SetATHigh = 0x8A3C
const val SetAbsSpd = 0xB961
const val SetAmtOffset = 0x821B
const val SetAnimC = 0xFCA3
const val SetAnimSpd = 0xB8F7
const val SetAttrib = 0x89E6
const val SetBBox = 0xC8B4
const val SetBBox2 = 0xCDD8
const val SetBFlip = 0xF7DC
const val SetBGColor = 0x869C
const val SetBPA = 0xCDFA
const val SetBehind = 0x96F2
const val SetBitsMask = 0xE321
const val SetCATmr = 0xE7AB
const val SetCAnim = 0xB76C
const val SetCollisionFlag = 0xE53E
const val SetCrouch = 0xB5FD
const val SetD6Ste = 0xEB4F
const val SetDBSte = 0xE005
const val SetDplSpd = 0xC49C
const val SetESpd = 0xC868
const val SetEndTimer = 0x84B3
const val SetEntr = 0xB479
const val SetFBTmr = 0xD8FE
const val SetFWC = 0xDAC7
const val SetFallS = 0xE5F2
const val SetFlameTimer = 0xD970
const val SetFor1Up = 0xE0E5
const val SetForStn = 0xEAD6
const val SetFore = 0x98A5
const val SetFrT = 0xCB7F
const val SetFreq_Squ1 = 0xFFFB
const val SetFreq_Squ2 = 0x1001A
const val SetFreq_Tri = 0x1001F
const val SetGfxF = 0xD9C2
const val SetHFAt = 0xFEF0
const val SetHJ = 0xD066
const val SetHPos = 0xBF20
const val SetHSpd = 0xBF06
const val SetHalfway = 0x9352
const val SetHiMax = 0xC465
const val SetHmrTmr = 0xD8A8
const val SetInitNTHigh = 0x9129
const val SetKRout = 0xE220
const val SetLMov = 0xD696
const val SetLMovD = 0xD6DB
const val SetLSpd = 0xD67D
const val SetLast2Platform = 0xF0E2
const val SetM2 = 0xD75D
const val SetMF = 0xCBD2
const val SetMFbar = 0xD470
const val SetMOfs = 0xBEA6
const val SetMdMax = 0xC45E
const val SetMiscOffset = 0x8222
const val SetMoveDir = 0xB3CB
const val SetNotW = 0xEA71
const val SetOffscrBitsOffset = 0xFDE3
const val SetOscrO = 0xFECA
const val SetPESub = 0x92F4
const val SetPRout = 0xE222
const val SetPSte = 0xE5F4
const val SetPVar = 0xDE67
const val SetPWh = 0xBBE2
const val SetPause = 0x81E4
const val SetPlatformTilenum = 0xF0F5
const val SetRSpd = 0xD134
const val SetRTmr = 0xB87C
const val SetRunSpd = 0xB8DB
const val SetSDir = 0xD586
const val SetSecHard = 0x9162
const val SetShim = 0xD0B0
const val SetSpSpd = 0xC9AB
const val SetStPos = 0x9299
const val SetStun = 0xEA4F
const val SetVFbr = 0xD4F6
const val SetVRAMAddr_A = 0x8639
const val SetVRAMAddr_B = 0x86C8
const val SetVRAMCtrl = 0x8A83
const val SetVRAMOffset = 0x86BA
const val SetVXPl = 0xE881
const val SetWYSpd = 0xEA6F
const val SetXMoveAmt = 0xC467
const val SetYGp = 0xCD18
const val SetYO = 0xCE25
const val SetoffF = 0xDB20
const val SetupBB = 0xBE4A
const val SetupBubble = 0xBA77
const val SetupCannon = 0x9C8F
const val SetupEOffsetFBBox = 0xED16
const val SetupExpl = 0xDA5C
const val SetupFloateyNumber = 0xE30C
const val SetupGFB = 0xD43E
const val SetupGameOver = 0x9367
const val SetupIntermediate = 0x860F
const val SetupJumpCoin = 0xBF71
const val SetupLakitu = 0xC8E8
const val SetupNumSpr = 0x85A9
const val SetupNums = 0x902D
const val SetupPlatformRope = 0xDD5B
const val SetupPowerUp = 0xC089
const val SetupToMovePPlant = 0xDBBC
const val SetupVictoryMode = 0x83F1
const val SetupWrites = 0x8FA0
const val Setup_Vine = 0xBCEA
const val ShellCollisions = 0xE3F7
const val ShellOrBlockDefeat = 0xE024
const val Shimmy = 0xD099
const val ShrPlF = 0xFD02
const val ShrinkPlatform = 0xF0E0
const val ShrinkPlayer = 0xFCF3
const val ShroomM = 0xC102
const val Shroom_Flower_PUp = 0xE0C7
const val ShufAmtLoop = 0x91C9
const val ShuffleLoop = 0x81F2
const val SideC = 0xE56A
const val SideCheckLoop = 0xE708
const val SideExitPipeEntry = 0xB4A6
const val SidePipeBottomPart = 0x9A8D
const val SidePipeShaftData = 0x9A85
const val SidePipeTopPart = 0x9A89
const val SilenceData = 0x10763
const val SilenceHdr = 0x1069F
const val SilentBeat = 0x105F7
const val SixSpriteStacker = 0xEF8D
const val SizeChk = 0xB3A7
const val SkipATRender = 0x9415
const val SkipByte = 0x9203
const val SkipCtrlL = 0x1050B
const val SkipExpTimer = 0x8124
const val SkipFBar = 0xD49E
const val SkipFqL1 = 0x104A3
const val SkipIY = 0xDE20
const val SkipMainOper = 0x8192
const val SkipMove = 0xCF16
const val SkipPIn = 0xFFA9
const val SkipPT = 0xCF8F
const val SkipScore = 0xBC4F
const val SkipSoundSubroutines = 0xFFC5
const val SkipSprite0 = 0x8175
const val SkipToFB = 0xD8BC
const val SkipToOffScrChk = 0xF5EC
const val SkpFSte = 0xD438
const val SkpVTop = 0xEF6C
const val SlidePlayer = 0xB572
const val SlowM = 0xD0FE
const val SlowSwim = 0xD2A3
const val SmSpc = 0x1013A
const val SmTick = 0x1013C
const val SmallBBox = 0xC899
const val SmallBP = 0xC1D7
const val SmallPlatformBoundBox = 0xECDC
const val SmallPlatformCollision = 0xE4B8
const val SndOn = 0xFF2F
const val SnglID = 0xCD0C
const val SolidBlockMetatiles = 0x9C29
const val SolidMTileUpperExt = 0xE98F
const val SolidOrClimb = 0xE66F
const val SoundEngine = 0xFF25
const val SpawnBrickChunks = 0xC2D3
const val SpawnFBr = 0xD8DC
const val SpawnFromMouth = 0xCBA5
const val SpawnHammerObj = 0xBE99
const val SpecObj = 0x97B4
const val SpinCounterClockwise = 0xDC13
const val SpinyRte = 0xC9B9
const val SpnySC = 0xF66D
const val SprInitLoop = 0x8251
const val SprObjectCollisionCore = 0xEDD7
const val SprObjectOffscrChk = 0xF709
const val Sprite0Clr = 0x8152
const val Sprite0Data = 0x90DE
const val Sprite0Hit = 0x8167
const val SpriteShuffler = 0x81E8
const val Squ1NoteHandler = 0x104F3
const val Squ2LengthHandler = 0x1047F
const val Squ2NoteHandler = 0x1048E
const val Square1SfxHandler = 0x10097
const val Square2SfxHandler = 0x10227
const val StaircaseHeightData = 0x9CB0
const val StaircaseObject = 0x9CC2
const val StaircaseRowData = 0x9CB9
const val StarBlock = 0xC25C
const val StarFChk = 0xCC0E
const val StarFlagExit = 0xDAD0
const val StarFlagExit2 = 0xDB7C
const val StarFlagTileData = 0xDA8F
const val StarFlagXPosAdder = 0xDA8B
const val StarFlagYPosAdder = 0xDA87
const val Star_CloudHdr = 0x1068A
const val Star_CloudMData = 0x106FF
const val Start = 0x8000
const val StartBTmr = 0xC19B
const val StartClrGet = 0x867D
const val StartGame = 0x8282
const val StartPage = 0x9114
const val StartWorld1 = 0x8320
const val StatusBarData = 0x8FF5
const val StatusBarNybbles = 0xC038
const val StatusBarOffset = 0x9001
const val SteadM = 0xD100
const val StillInGame = 0x9328
const val StkLp = 0xEF8F
const val StnE = 0xE034
const val StompedEnemyPtsData = 0xE246
const val StopGrowItems = 0x102F9
const val StopPlatforms = 0xDDDB
const val StopPlayerMove = 0xE7C4
const val StopSquare1Sfx = 0x10145
const val StopSquare2Sfx = 0x1021C
const val StoreFore = 0x9E9A
const val StoreMT = 0x95E9
const val StoreMusic = 0x923C
const val StoreNewD = 0x9082
const val StoreStyle = 0x9ED7
const val StrAObj = 0x9808
const val StrBlock = 0x9669
const val StrCOffset = 0x9CAC
const val StrFre = 0xC761
const val StrID = 0xC73F
const val StrSprOffset = 0x8209
const val StrType = 0xC0C6
const val StrWOffset = 0x9D8C
const val StrWave = 0xFFED
const val StrongBeat = 0x105E5
const val SubDifAdj = 0xD71F
const val SubtEnemyYPos = 0xEBC7
const val SubtR1 = 0xD4CF
const val SusFbar = 0xD423
const val SwimCCXMoveData = 0xD2F4
const val SwimCC_IDData = 0xCC66
const val SwimKT = 0xFB09
const val SwimKickTileNum = 0xFAC4
const val SwimStompEnvelopeData = 0x10024
const val SwimX = 0xD245
const val SzOfs = 0xFCB3
const val TInjE = 0xE1F8
const val TScrClear = 0x87C5
const val TallBBox = 0xC8B2
const val TallBBox2 = 0xCDD6
const val TaskLoop = 0x876B
const val TerMTile = 0x95DE
const val TerminateGame = 0x938E
const val TerrBChk = 0x9612
const val TerrLoop = 0x95F3
const val TerrainMetatiles = 0x9527
const val TerrainRenderBits = 0x952B
const val ThankPlayer = 0x846D
const val ThirdP = 0x9568
const val ThreeFrameExtent = 0xFC80
const val ThreeSChk = 0xBDA8
const val TimeRunOutMusData = 0x109B9
const val TimeRunningOutHdr = 0x10685
const val TimeUpOn = 0xBB1F
const val TitleScreenMode = 0x825C
const val TooFar = 0xDF34
const val TopEx = 0xC2D2
const val TopSP = 0xF971
const val TopScoreCheck = 0x90AE
const val TopStatusBarLine = 0x87DC
const val TransLoop = 0x93E2
const val TransposePlayers = 0x93CB
const val TreeLedge = 0x990E
const val TriNoteHandler = 0x10568
const val TwoPlayerGameOver = 0x8835
const val TwoPlayerTimeUp = 0x8822
const val Unbreak = 0xC1F2
const val UnderHammerBro = 0xEC0C
const val UndergroundMusData = 0x10A58
const val UndergroundMusHdr = 0x1069A
const val UndergroundPaletteData = 0x8DD3
const val UpToFiery = 0xE0F2
const val UpToSuper = 0xE0EB
const val UpdScrollVar = 0xB1B6
const val UpdSte = 0xC37E
const val UpdateLoop = 0xC384
const val UpdateNumber = 0xC074
const val UpdateScreen = 0x8FDC
const val UpdateShroom = 0x82DE
const val UpdateTopScore = 0x90A7
const val UseAdder = 0xC3EA
const val UseBOffset = 0x8B5B
const val UseMiscS = 0xBFB8
const val UsePosv = 0xC9AA
const val VAHandl = 0xD4DD
const val VBlank1 = 0x800A
const val VBlank2 = 0x8010
const val VDrawLoop = 0xBD53
const val VPipeSectLoop = 0x9A75
const val VRAM_AddrTable_High = 0x8072
const val VRAM_AddrTable_Low = 0x805F
const val VRAM_Buffer_Offset = 0x8085
const val VariableObjOfsRelPos = 0xFD89
const val VerticalPipe = 0x9AD1
const val VerticalPipeData = 0x9AC9
const val VerticalPipeEntry = 0xB481
const val VictoryMLoopBack = 0x1047C
const val VictoryMode = 0x83CA
const val VictoryModeSubroutines = 0x83E1
const val VictoryMusData = 0x10C0F
const val VictoryMusHdr = 0x106A8
const val VineBlock = 0xC267
const val VineCollision = 0xE851
const val VineEntr = 0xB312
const val VineHeightData = 0xBD1C
const val VineObjectHandler = 0xBD1E
const val VineTL = 0xEF51
const val VineYPosAdder = 0xEF09
const val Vine_AutoClimb = 0xB45E
const val WBootCheck = 0x801A
const val WSelectBufferTemplate = 0x826A
const val WaitOneRow = 0x9DC3
const val WarpNum = 0x98BA
const val WarpNumLoop = 0x8920
const val WarpPipe = 0x9ADE
const val WarpZoneNumbers = 0x887C
const val WarpZoneObject = 0xBB29
const val WarpZoneWelcome = 0x884A
const val WaterEventMusEnvData = 0x10CE9
const val WaterMus = 0x10631
const val WaterMusData = 0x10A99
const val WaterMusHdr = 0x106B2
const val WaterPaletteData = 0x8D8B
const val WaterPipe = 0x9A52
const val WhLoop = 0xBB52
const val WhPull = 0xBBE5
const val WhirlpoolActivate = 0xBB8C
const val WinCastleMusHdr = 0x106B8
const val WinLevelMusData = 0x109F7
const val World1Areas = 0x9EF4
const val World2Areas = 0x9EF9
const val World3Areas = 0x9EFE
const val World4Areas = 0x9F02
const val World5Areas = 0x9F07
const val World6Areas = 0x9F0B
const val World7Areas = 0x9F0F
const val World8Areas = 0x9F14
const val WorldAddrOffsets = 0x9EEC
const val WorldLivesDisplay = 0x8803
const val WorldSelectMessage1 = 0x8EC5
const val WorldSelectMessage2 = 0x8ED6
const val WrCMTile = 0xBD76
const val WriteBlankMT = 0x8B27
const val WriteBlockMetatile = 0x8B3C
const val WriteBottomStatusLine = 0x86D6
const val WriteBufferToScreen = 0x8F86
const val WriteGameText = 0x8892
const val WriteNTAddr = 0x8F1A
const val WritePPUReg1 = 0x8FEE
const val WriteTopScore = 0x87D3
const val WriteTopStatusLine = 0x86CE
const val WrongChk = 0xC620
const val XLdBData = 0xFE55
const val XMRight = 0xD1F8
const val XMoveCntr_GreenPTroopa = 0xD1B0
const val XMoveCntr_Platform = 0xD1B2
const val XMovingPlatform = 0xDE3F
const val XOffscreenBitsData = 0xFE13
const val XOfsLoop = 0xFE2B
const val XSpdSign = 0xB957
const val XSpeedAdderData = 0xCFEC
const val X_Physics = 0xB82F
const val X_SubtracterData = 0xB28F
const val YLdBData = 0xFEA1
const val YMDown = 0xDE32
const val YMovingPlatform = 0xDE00
const val YOffscreenBitsData = 0xFE65
const val YOfsLoop = 0xFE78
const val YPDiff = 0xD388
const val YSway = 0xD1A2
const val Y_Bubl = 0xBAC1
const val YesEC = 0xE3AE
const val YesIn = 0xEA05
