@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.smb

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

const val ActionClimbing = 0xFB5B
const val ActionFalling = 0xFB4B
const val ActionSwimming = 0xFB69
const val ActionWalkRun = 0xFB53
const val AddCCF = 0xD53C
const val AddFBit = 0xCC0C
const val AddHA = 0xD3C3
const val AddHS = 0xD017
const val AddModLoop = 0x9057
const val AddToScore = 0xBFF7
const val AddVA = 0xD400
const val AdjSm = 0xD431
const val AlignP = 0xCD39
const val AllMus = 0x1053C
const val AllRowC = 0xF646
const val AllUnder = 0x991E
const val AltYPosOffset = 0x922B
const val Alter2 = 0x9842
const val AlterAreaAttributes = 0x9823
const val AlterYP = 0xC44F
const val AlternateLengthHandler = 0x1050C
const val AnimationControl = 0xFB8B
const val AreaAddrOffsets = 0x9E9B
const val AreaChangeTimerData = 0xE6D1
const val AreaDataAddrHigh = 0x9F2D
const val AreaDataAddrLow = 0x9F0B
const val AreaDataHOffsets = 0x9F07
const val AreaDataOfsLoopback = 0x9DCB
const val AreaFrenzy = 0x9894
const val AreaMusicEnvData = 0x10BEA
const val AreaPalette = 0x8613
const val AreaParserCore = 0x9524
const val AreaParserTaskControl = 0x874C
const val AreaParserTaskHandler = 0x93DD
const val AreaParserTasks = 0x93F7
const val AreaStyleObject = 0x98AC
const val AttribLoop = 0x8A36
const val AutoClimb = 0xB402
const val AutoControlPlayer = 0xB2E7
const val AutoPlayer = 0x83D3
const val AwardGameTimerPoints = 0xD9DA
const val AwardTouchedCoin = 0xE5B5
const val AxeObj = 0x9BB2
const val BBChk_E = 0xED73
const val BBFly = 0xBE0A
const val BB_SLoop = 0xCC25
const val BGColorCtrl_Addr = 0x8623
const val BHalf = 0xE638
const val BPGet = 0xD54F
const val BSceneDataOffsets = 0x941F
const val BSwimE = 0xD1B0
const val BWithL = 0x9CE6
const val B_FaceP = 0xD72C
const val BackColC = 0x97CF
const val BackSceneryData = 0x9422
const val BackSceneryMetatiles = 0x94B2
const val BackgroundColors = 0x8627
const val BalancePlatRope = 0x9B80
const val BalancePlatform = 0xDB2E
const val BigBP = 0xC16A
const val BigJp = 0xD43C
const val BigKTS = 0xFA22
const val Bitmasks = 0xCBA8
const val BlankPalette = 0x8A73
const val BlkOffscr = 0xF6FA
const val BlockBuffLowBounds = 0x9654
const val BlockBufferAdderData = 0xED7C
const val BlockBufferAddr = 0x9DAE
const val BlockBufferChk_Enemy = 0xED56
const val BlockBufferChk_FBall = 0xED6A
const val BlockBufferColli_Feet = 0xEDB7
const val BlockBufferColli_Head = 0xEDB8
const val BlockBufferColli_Side = 0xEDBB
const val BlockBufferCollision = 0xEDBF
const val BlockBuffer_X_Adder = 0xED7F
const val BlockBuffer_Y_Adder = 0xED9B
const val BlockBumpedChk = 0xC207
const val BlockCode = 0xC1D6
const val BlockGfxData = 0x8AEB
const val BlockObjMT_Updater = 0xC30B
const val BlockObjectsCore = 0xC297
const val BlockYPosAdderData = 0xC0E0
const val BlooberBitmasks = 0xD114
const val BlooberSwim = 0xD141
const val BlstSJp = 0x101A3
const val BorrowOne = 0x907A
const val BotSP = 0xF889
const val BounceJS = 0xBC4E
const val BouncingBlockHandler = 0xC2E6
const val BoundBoxCtrlData = 0xEB94
const val BoundingBoxCore = 0xEC3F
const val BowserControl = 0xD6F5
const val BowserFlameEnvData = 0x10C1A
const val BowserGfxHandler = 0xD814
const val BowserIdentities = 0xDEB9
const val BowserPaletteData = 0x8E17
const val BranchToDecLength1 = 0x1001D
const val BrickMetatiles = 0x9BD4
const val BrickQBlockMetatiles = 0xC1F9
const val BrickShatter = 0xC215
const val BrickShatterEnvData = 0x10C3A
const val BrickShatterFreqData = 0x10205
const val BrickWithCoins = 0x9CD0
const val BrickWithItem = 0x9CD5
const val BridgeCollapse = 0xD64A
const val BridgeCollapseData = 0xD63B
const val Bridge_High = 0x9B1D
const val Bridge_Low = 0x9B23
const val Bridge_Middle = 0x9B20
const val BubbleCheck = 0xB9F3
const val BubbleTimerData = 0xBA59
const val Bubble_MForceData = 0xBA57
const val BublExit = 0xB96A
const val BublLoop = 0xB957
const val BulletBillCannon = 0x9C17
const val BulletBillCheepCheep = 0xCBBA
const val BulletBillHandler = 0xBDBD
const val BulletBillXSpdData = 0xBDBB
const val BumpBlock = 0xC1AD
const val BumpChkLoop = 0xC209
const val BuzzyBeetleMutate = 0xC6BB
const val CCSwim = 0xD211
const val CCSwimUpwards = 0xD268
const val CGrab_TTickRegL = 0x100CE
const val CInvu = 0xE8FF
const val CMBits = 0xEC08
const val CNwCDir = 0xEA13
const val CRendLoop = 0x9995
const val CSetFDir = 0xB686
const val CSzNext = 0xFBE7
const val C_ObjectMetatile = 0x9BA7
const val C_ObjectRow = 0x9BA4
const val C_S_IGAtt = 0xFC43
const val CannonBitmasks = 0xBD30
const val CarryOne = 0x9082
const val CasPBB = 0xCD91
const val CastleBridgeObj = 0x9BAA
const val CastleMetatiles = 0x9946
const val CastleMusData = 0x107F4
const val CastleMusHdr = 0x105AC
const val CastleObject = 0x997D
const val CastlePaletteData = 0x8DDB
const val ChainObj = 0x9BB7
const val ChangeSizeOffsetAdder = 0xFBBD
const val CheckAnimationStop = 0xF4B2
const val CheckBalPlatform = 0xDB40
const val CheckBowserFront = 0xF36E
const val CheckBowserGfxFlag = 0xF330
const val CheckBowserRear = 0xF39F
const val CheckDefeatedState = 0xF4C2
const val CheckEndofBuffer = 0xC5EB
const val CheckForBloober = 0xF455
const val CheckForBulletBillCV = 0xF2F0
const val CheckForClimbMTiles = 0xE8A7
const val CheckForCoinMTiles = 0xE8AE
const val CheckForDefdGoomba = 0xF426
const val CheckForESymmetry = 0xF53F
const val CheckForEnemyGroup = 0xC6AC
const val CheckForGoomba = 0xF341
const val CheckForHammerBro = 0xF43A
const val CheckForJumping = 0xB702
const val CheckForJumpspring = 0xF30C
const val CheckForLakitu = 0xF3D6
const val CheckForPUpCollision = 0xE03F
const val CheckForPodoboo = 0xF319
const val CheckForRetainerObj = 0xF2D6
const val CheckForSecondFrame = 0xF4A9
const val CheckForSolidMTiles = 0xE89C
const val CheckForSpiny = 0xF3BD
const val CheckForVerticalFlip = 0xF4F8
const val CheckFrenzyBuffer = 0xC6DA
const val CheckHalfway = 0x9149
const val CheckLeftScreenBBox = 0xECC1
const val CheckNoiseBuffer = 0x10259
const val CheckPageCtrlRow = 0xC62D
const val CheckPlayerName = 0x88D5
const val CheckPlayerVertical = 0xE4AD
const val CheckRear = 0x96BC
const val CheckRightBounds = 0xC603
const val CheckRightExtBounds = 0xC67A
const val CheckRightScreenBBox = 0xEC89
const val CheckRightSideUpShell = 0xF40C
const val CheckSfx1Buffer = 0xFFD6
const val CheckSfx2Buffer = 0x1016E
const val CheckSideMTiles = 0xE656
const val CheckThreeBytes = 0xC71C
const val CheckToAnimateEnemy = 0xF478
const val CheckToMirrorJSpring = 0xF5FA
const val CheckToMirrorLakitu = 0xF5B9
const val CheckTopOfBlock = 0xC234
const val CheckUpsideDownShell = 0xF3F0
const val CheckpointEnemyID = 0xC73B
const val ChgAreaMode = 0xB44A
const val ChgAreaPipe = 0xB441
const val ChgSDir = 0xD488
const val Chk1Row13 = 0x9687
const val Chk1Row14 = 0x96B1
const val Chk1stB = 0x9703
const val Chk2MSBSt = 0xEA45
const val Chk2Ofs = 0xD470
const val Chk2Players = 0x8885
const val ChkAreaTsk = 0xC4D5
const val ChkAreaType = 0x9215
const val ChkBBill = 0xE986
const val ChkBehPipe = 0xB27A
const val ChkBowserF = 0xC4E2
const val ChkBrick = 0xC116
const val ChkBuzzyBeetle = 0xDEDB
const val ChkCFloor = 0x99AA
const val ChkCollSize = 0xE516
const val ChkContinue = 0x8308
const val ChkDSte = 0xBDFF
const val ChkETmrs = 0xE0E9
const val ChkEmySpd = 0xD625
const val ChkEnemyFaceRight = 0xE1F6
const val ChkEnemyFrenzy = 0xC5C4
const val ChkFBCl = 0xD46B
const val ChkFOfs = 0xD3E1
const val ChkFTop = 0xEE77
const val ChkFiery = 0x8657
const val ChkFireB = 0xD7DD
const val ChkFlagOffscreen = 0xEFA0
const val ChkFlagpoleYPosLoop = 0xE745
const val ChkFootMTile = 0xE5B8
const val ChkForBump_HammerBroJ = 0xEA8B
const val ChkForDemoteKoopa = 0xE1AD
const val ChkForFall = 0xDB50
const val ChkForFlagpole = 0xE70D
const val ChkForFloatdown = 0xD1CD
const val ChkForLandJumpSpring = 0xE7B4
const val ChkForNonSolids = 0xEB39
const val ChkForPlayerAttrib = 0xFC0F
const val ChkForPlayerC_LargeP = 0xE3A0
const val ChkForPlayerInjury = 0xE0CC
const val ChkForRedKoopa = 0xEA37
const val ChkForTopCollision = 0xE424
const val ChkFrontSte = 0xF391
const val ChkGERtn = 0xE6BA
const val ChkHiByte = 0x878F
const val ChkHoleX = 0xB3D2
const val ChkInj = 0xE0D5
const val ChkInvisibleMTiles = 0xE7AC
const val ChkJH = 0xCF0D
const val ChkJumpspringMetatiles = 0xE7CE
const val ChkKillGoomba = 0xD04F
const val ChkLS = 0xD564
const val ChkLak = 0xC866
const val ChkLakDif = 0xD5BE
const val ChkLandedEnemyState = 0xE9BB
const val ChkLeftCo = 0xF70C
const val ChkLength = 0x96D1
const val ChkLrgObjFixedLength = 0x9D7B
const val ChkLrgObjLength = 0x9D78
const val ChkLuigi = 0x88EC
const val ChkMTLow = 0x962B
const val ChkMouth = 0xD703
const val ChkMoveDir = 0xB352
const val ChkNearMid = 0xB1AC
const val ChkNearPlayer = 0xD1DE
const val ChkNoEn = 0xC87F
const val ChkNumTimer = 0x8527
const val ChkOnScr = 0xE500
const val ChkOtherEnemies = 0xDF1E
const val ChkOtherForFall = 0xDB6A
const val ChkOverR = 0x92B3
const val ChkPBtm = 0xE67A
const val ChkPOffscr = 0xB1F5
const val ChkPSpeed = 0xD5ED
const val ChkPUSte = 0xC0C5
const val ChkPauseTimer = 0x81B1
const val ChkPlayerNearPipe = 0xDAAF
const val ChkRBit = 0xCBFB
const val ChkRFast = 0xB7F6
const val ChkRearSte = 0xF3A9
const val ChkRep = 0xF6CC
const val ChkRow13 = 0x9732
const val ChkRow14 = 0x971D
const val ChkSRows = 0x9757
const val ChkSelect = 0x827D
const val ChkSkid = 0xB85B
const val ChkSmallPlatCollision = 0xDDC2
const val ChkSmallPlatLoop = 0xE3D5
const val ChkSpinyO = 0xD617
const val ChkStPos = 0x9269
const val ChkStart = 0x81BB
const val ChkStop = 0xB534
const val ChkSwimE = 0x92CF
const val ChkSwimYPos = 0xD280
const val ChkTallEnemy = 0x855A
const val ChkToMoveBalPlat = 0xDB7F
const val ChkToStunEnemies = 0xE93F
const val ChkTop = 0xC2DA
const val ChkUnderEnemy = 0xEB32
const val ChkUpM = 0xC48C
const val ChkVFBD = 0xD449
const val ChkW2 = 0xCBD6
const val ChkWorldSel = 0x8294
const val ChkWtr = 0xB76D
const val ChkYCenterPos = 0xDD2C
const val ChkYPCollision = 0xDD3E
const val Chk_BB = 0xBD9F
const val ChnkOfs = 0xF79B
const val ChpChpEx = 0xC91D
const val ClHCol = 0xDFA5
const val ClearBitsMask = 0xE231
const val ClearBounceFlag = 0xEB83
const val ClearBuffersDrawIcon = 0x87A1
const val ClearVRLoop = 0x9178
const val ClimbAdderHigh = 0xB639
const val ClimbAdderLow = 0xB635
const val ClimbFD = 0xB681
const val ClimbMTileUpperExt = 0xE8A3
const val ClimbPLocAdder = 0xE6F8
const val ClimbXPosAdder = 0xE6F6
const val Climb_Y_MForceData = 0xB6CF
const val Climb_Y_SpeedData = 0xB6CC
const val ClimbingSub = 0xB63D
const val CloudExit = 0xB3E8
const val ClrGetLoop = 0x8666
const val ClrMTBuf = 0x9531
const val ClrPauseTimer = 0x81DF
const val ClrPlrPal = 0xB14B
const val ClrSndLoop = 0x90CD
const val ClrTimersLoop = 0x90E5
const val CntGrp = 0xCC81
const val CntPl = 0xF9DC
const val CoinBlock = 0xBEE7
const val CoinMetatileData = 0x9B97
const val CoinPoints = 0xBFF2
const val CoinSd = 0xE8BA
const val CoinTallyOffsets = 0xBFC6
const val ColFlg = 0xDBA8
const val ColObj = 0x9BCB
const val ColdBoot = 0x8030
const val CollisionCoreLoop = 0xECE8
const val CollisionFound = 0xED49
const val ColorRotatePalette = 0x8A6D
const val ColorRotation = 0x8A8B
const val ColumnOfBricks = 0x9BFD
const val ColumnOfSolidBlocks = 0x9C06
const val CommonPlatCode = 0xCD7C
const val CommonSmallLift = 0xCDB8
const val CompDToO = 0xD791
const val ContBTmr = 0xC13B
const val ContChk = 0xE5CC
const val ContES = 0xF553
const val ContPau = 0xFE7F
const val ContSChk = 0xE665
const val ContVMove = 0xC3C2
const val Cont_CGrab_TTick = 0x10194
const val ContinueBlast = 0x100F4
const val ContinueBowserFall = 0x101A6
const val ContinueBowserFlame = 0x1026D
const val ContinueBrickShatter = 0x1021A
const val ContinueBumpThrow = 0xFF90
const val ContinueCGrabTTick = 0x100D8
const val ContinueExtraLife = 0x101BC
const val ContinueGame = 0x938D
const val ContinueGrowItems = 0x101E8
const val ContinueMusic = 0x1027A
const val ContinuePipeDownInj = 0x10063
const val ContinuePowerUpGrab = 0x10108
const val ContinueSmackEnemy = 0x10031
const val ContinueSndJump = 0xFF5D
const val ContinueSwimStomp = 0x1000A
const val CopyFToR = 0xD822
const val CopyScore = 0x90A5
const val CreateL = 0xC88C
const val CreateSpiny = 0xC8A2
const val CyclePlayerPalette = 0xB4CD
const val CycleTwo = 0xB144
const val D2XPos1 = 0xCA0D
const val D2XPos2 = 0xCA25
const val DBlkLoop = 0xF6A5
const val DBlockSte = 0xC0F0
const val DChunks = 0xF730
const val DSFLoop = 0xDA3F
const val DaySnowPaletteData = 0x8DFF
const val DeathMAltReg = 0x10437
const val DeathMusData = 0x107C2
const val DeathMusHdr = 0x10602
const val DecHT = 0xCF3E
const val DecJpFPS = 0xFF9D
const val DecNumTimer = 0x8532
const val DecPauC = 0xFE9A
const val DecSeXM = 0xD0E3
const val DecTimers = 0x810C
const val DecTimersLoop = 0x811B
const val DecodeAreaData = 0x96FA
const val DecrementSfx1Length = 0x10048
const val DecrementSfx2Length = 0x1011A
const val DecrementSfx3Length = 0x10233
const val DefaultBlockObjTiles = 0xF687
const val DefaultSprOffsets = 0x90B3
const val DefaultXOnscreenOfs = 0xFD2C
const val DefaultYOnscreenOfs = 0xFD77
const val DelayToAreaEnd = 0xDA76
const val DemoActionData = 0x8375
const val DemoEngine = 0x83A0
const val DemoOver = 0x83C1
const val DemoTimingData = 0x838A
const val Demote = 0xE953
const val DemotedKoopaXSpdData = 0xE005
const val DestroyBlockMetatile = 0x8B1E
const val DifLoop = 0xC8D2
const val DigitPLoop = 0x9033
const val DigitsMathRoutine = 0x904D
const val DisJoyp = 0xB307
const val DisplayIntermediate = 0x8709
const val DisplayTimeUp = 0x86F3
const val DivLLoop = 0x101C1
const val DividePDiff = 0xFDBA
const val DmpJpFPS = 0xFF75
const val DoAPTasks = 0x93E8
const val DoAction = 0x83B7
const val DoAltLoad = 0x1043F
const val DoBPl = 0xDB39
const val DoBulletBills = 0xCC23
const val DoChangeSize = 0xFA2F
const val DoEnemySideCheck = 0xEA5B
const val DoFootCheck = 0xE587
const val DoGroup = 0xC6F6
const val DoIDCheckBGColl = 0xE8EF
const val DoLpBack = 0xC5B1
const val DoNothing1 = 0x93D7
const val DoNothing2 = 0x93DC
const val DoOtherPlatform = 0xDBBD
const val DoPlayerSideCheck = 0xE607
const val DoSide = 0xEAFE
const val DoneInitArea = 0x9154
const val DonePlayerTask = 0xB4B5
const val DontWalk = 0x840D
const val DownJSpr = 0xBC26
const val DrawBlock = 0xF68B
const val DrawBowser = 0xF39C
const val DrawBrickChunks = 0xF71A
const val DrawBricks = 0x9BE4
const val DrawBubble = 0xF8C4
const val DrawEnemyObjRow = 0xF662
const val DrawEnemyObject = 0xF4DB
const val DrawEraseRope = 0xDBD6
const val DrawExplosion_Fireball = 0xF7E0
const val DrawExplosion_Fireworks = 0xF7F1
const val DrawFbar = 0xD386
const val DrawFireball = 0xF7B3
const val DrawFirebar = 0xF7C2
const val DrawFirebar_Collision = 0xD3AD
const val DrawFlagSetTimer = 0xDA6A
const val DrawFlameLoop = 0xD8F2
const val DrawFloateyNumber_Coin = 0xF05E
const val DrawHammer = 0xEEC6
const val DrawJSpr = 0xBC5E
const val DrawLargePlatform = 0xEFC3
const val DrawMTLoop = 0x8950
const val DrawMushroomIcon = 0x8358
const val DrawOneSpriteRow = 0xF66B
const val DrawPipe = 0x9AC3
const val DrawPlayerLoop = 0xFAE2
const val DrawPlayer_Intermediate = 0xFAA2
const val DrawPowerUp = 0xF0E4
const val DrawQBlk = 0x9CEB
const val DrawRope = 0x9B92
const val DrawRow = 0x9BF4
const val DrawSidePart = 0x9A60
const val DrawSmallPlatform = 0xF842
const val DrawSpriteObject = 0xFDD5
const val DrawStarFlag = 0xDA37
const val DrawThisRow = 0x9D67
const val DrawTitleScreen = 0x8767
const val DrawVine = 0xEE14
const val DropPlatform = 0xDD7A
const val DumpFall = 0xB5F1
const val DumpFourSpr = 0xEFB6
const val DumpSixSpr = 0xEFB0
const val DumpThreeSpr = 0xEFB9
const val DumpTwoSpr = 0xEFBC
const val Dump_Freq_Regs = 0xFF06
const val Dump_Sq2_Regs = 0xFF19
const val Dump_Squ1_Regs = 0xFEFA
const val DuplicateEnemyObj = 0xCA73
const val ECLoop = 0xE264
const val EColl = 0xE04A
const val ELPGive = 0xDA0C
const val EL_LRegs = 0x101B4
const val ESRtnr = 0xF56C
const val E_CastleArea1 = 0x9F4F
const val E_CastleArea2 = 0x9F76
const val E_CastleArea3 = 0x9F8F
const val E_CastleArea4 = 0x9FBE
const val E_CastleArea5 = 0x9FE9
const val E_CastleArea6 = 0x9FFE
const val E_GroundArea1 = 0xA038
const val E_GroundArea10 = 0xA15A
const val E_GroundArea11 = 0xA15B
const val E_GroundArea12 = 0xA17F
const val E_GroundArea13 = 0xA188
const val E_GroundArea14 = 0xA1AD
const val E_GroundArea15 = 0xA1D0
const val E_GroundArea16 = 0xA1D9
const val E_GroundArea17 = 0xA1DA
const val E_GroundArea18 = 0xA214
const val E_GroundArea19 = 0xA23F
const val E_GroundArea2 = 0xA05D
const val E_GroundArea20 = 0xA26D
const val E_GroundArea21 = 0xA289
const val E_GroundArea22 = 0xA292
const val E_GroundArea3 = 0xA07A
const val E_GroundArea4 = 0xA088
const val E_GroundArea5 = 0xA0AF
const val E_GroundArea6 = 0xA0E0
const val E_GroundArea7 = 0xA0FE
const val E_GroundArea8 = 0xA11B
const val E_GroundArea9 = 0xA130
const val E_UndergroundArea1 = 0xA2B7
const val E_UndergroundArea2 = 0xA2E4
const val E_UndergroundArea3 = 0xA312
const val E_WaterArea1 = 0xA33F
const val E_WaterArea2 = 0xA350
const val E_WaterArea3 = 0xA37A
const val EggExc = 0xF598
const val EmptyBlock = 0x9BC3
const val EmptyChkLoop = 0x9AED
const val EmptySfx2Buffer = 0x10120
const val EndAParse = 0x96ED
const val EndAreaPoints = 0xDA02
const val EndChgSize = 0xB479
const val EndChkBButton = 0x84DA
const val EndExitOne = 0x84D9
const val EndExitTwo = 0x84F2
const val EndFrenzy = 0xCCFC
const val EndGameText = 0x88A1
const val EndMushL = 0x98FA
const val EndOfCastleMusData = 0x10AA1
const val EndOfCastleMusicEnvData = 0x10BE6
const val EndOfEnemyInitCode = 0xCDDD
const val EndOfLevelMusHdr = 0x10599
const val EndOfMusicData = 0x10353
const val EndRp = 0xDC52
const val EndTreeL = 0x98E0
const val EndUChk = 0x9612
const val EndlessLoop = 0x805C
const val EndlessRope = 0x9B79
const val EnemiesAndLoopsCore = 0xC4C6
const val EnemiesCollision = 0xE238
const val Enemy17YPosData = 0xCBB0
const val EnemyAddrHOffsets = 0x9EBF
const val EnemyAnimTimingBMask = 0xF297
const val EnemyAttributeData = 0xF27C
const val EnemyBGCStateData = 0xE8C9
const val EnemyBGCXSpdData = 0xE8CF
const val EnemyDataAddrHigh = 0x9EE5
const val EnemyDataAddrLow = 0x9EC3
const val EnemyFacePlayer = 0xE207
const val EnemyGfxHandler = 0xF29E
const val EnemyGfxTableOffsets = 0xF261
const val EnemyGraphicsTable = 0xF15F
const val EnemyJump = 0xEAD9
const val EnemyLanding = 0xEAC2
const val EnemyMovementSubs = 0xCE43
const val EnemySmackScore = 0xDF5D
const val EnemyStomped = 0xE153
const val EnemyStompedPts = 0xE18B
const val EnemyToBGCollisionDet = 0xE8D1
const val EnemyTurnAround = 0xE34C
const val EnterSidePipe = 0xB456
const val EntrMode2 = 0xB294
const val Entrance_GameTimerSetup = 0x9242
const val ErACM = 0xE6EB
const val EraseDMods = 0x906E
const val EraseEnemyObject = 0xCEBD
const val EraseFB = 0xB9E7
const val EraseMLoop = 0x9072
const val EraseR1 = 0xDC16
const val EraseR2 = 0xDC4A
const val EvalForMusic = 0x8482
const val ExAnimC = 0xFBAF
const val ExBCDr = 0xF7B2
const val ExBGfxH = 0xD85E
const val ExCInvT = 0xE7B3
const val ExCJSp = 0xE7CD
const val ExCPV = 0xE4C1
const val ExCSM = 0xE6D0
const val ExCannon = 0xBDBA
const val ExDBlk = 0xF719
const val ExDBub = 0xF8EC
const val ExDLPl = 0xF05D
const val ExDPl = 0xDD86
const val ExDivPD = 0xFDD4
const val ExEBG = 0xE8C8
const val ExEBGChk = 0xE99D
const val ExEGHandler = 0xF661
const val ExEPar = 0xC6F5
const val ExESdeC = 0xEA8A
const val ExF17 = 0xCC39
const val ExFl = 0xD88F
const val ExFlmeD = 0xD953
const val ExGTimer = 0xBABA
const val ExHC = 0xE70C
const val ExHCF = 0xDF65
const val ExIPM = 0xE88E
const val ExInjColRoutines = 0xE13A
const val ExJCGfx = 0xF0CF
const val ExJSpring = 0xBC7B
const val ExLPC = 0xE3BD
const val ExLSHand = 0xC8A1
const val ExLiftP = 0xDDCB
const val ExMoveLak = 0xD63A
const val ExNH = 0x10267
const val ExPBGCol = 0xE515
const val ExPEC = 0xE0CB
const val ExPF = 0xDD05
const val ExPGH = 0xFA28
const val ExPHC = 0xDFAA
const val ExPRp = 0xDCC8
const val ExPVne = 0xE7AB
const val ExPipeE = 0xE84A
const val ExPlPos = 0xE4AC
const val ExPlyrAt = 0xFC55
const val ExRPl = 0xDD9B
const val ExS1H = 0xFFFB
const val ExS2H = 0x10193
const val ExSCH = 0xE655
const val ExSFN = 0xE229
const val ExSPC = 0xE406
const val ExSPl = 0xF8C0
const val ExScrnBd = 0xDE40
const val ExSfx1 = 0x1005D
const val ExSfx2 = 0x1012F
const val ExSfx3 = 0x10243
const val ExSteChk = 0xE9EA
const val ExSwCC = 0xD29F
const val ExTA = 0xE37F
const val ExTrans = 0x93D6
const val ExVMove = 0xC4C5
const val ExXMP = 0xDD79
const val ExXMove = 0xC39C
const val ExXOfsBS = 0xFD6D
const val ExYOfsBS = 0xFDB9
const val ExYPl = 0xDD47
const val ExecGameLoopback = 0xC511
const val ExitAFrenzy = 0x98A8
const val ExitBlink = 0xB492
const val ExitBlockChk = 0xC1F8
const val ExitBoth = 0xB4A9
const val ExitBubl = 0xBA56
const val ExitCAPipe = 0xB455
const val ExitCSub = 0xB6A1
const val ExitCastle = 0x99F8
const val ExitChgSize = 0xB481
const val ExitChkName = 0x88FC
const val ExitColorRot = 0x8AEA
const val ExitCtrl = 0xB3E7
const val ExitDeath = 0xB4EA
const val ExitDecBlock = 0x9CFC
const val ExitDrawM = 0x8A08
const val ExitDumpSpr = 0xEFC2
const val ExitECRoutine = 0xE2CF
const val ExitELCore = 0xC4EF
const val ExitEmptyChk = 0x9AFA
const val ExitEng = 0xB17F
const val ExitEntr = 0xB2E6
const val ExitFBallEnemy = 0xDEB5
const val ExitFWk = 0xCBA7
const val ExitFlagP = 0xBC01
const val ExitGetM = 0x9226
const val ExitIcon = 0x8374
const val ExitMenu = 0x8340
const val ExitMov1 = 0xB632
const val ExitMsgs = 0x84B1
const val ExitMusicHandler = 0x1050B
const val ExitNA = 0xB57F
const val ExitOutputN = 0x904C
const val ExitPUp = 0xC0DF
const val ExitPause = 0x81E7
const val ExitPhy = 0xB846
const val ExitPipe = 0x9A38
const val ExitProcessEColl = 0xE31A
const val ExitRp = 0xDC60
const val ExitUPartR = 0x9D77
const val ExitVH = 0xBD2C
const val ExitVWalk = 0x8431
const val ExitWh = 0xBB1D
const val ExplosionTiles = 0xF7DD
const val ExtendLB = 0xDDE6
const val ExtraLifeFreqData = 0x1007F
const val ExtraLifeMushBlock = 0xC1E8
const val FBCLoop = 0xD43D
const val FBLeft = 0xD135
const val FBallB = 0xEBD5
const val FMiscLoop = 0xBF3C
const val FPGfx = 0xBBF8
const val FPS2nd = 0xFF73
const val FSLoop = 0xCA75
const val FSceneDataOffsets = 0x94D6
const val FallE = 0xCFEC
const val FallMForceData = 0xB6AD
const val FallingSub = 0xB5CA
const val FastXSp = 0xB808
const val FeetTmr = 0xD70C
const val FetchNoiseBeatData = 0x104B5
const val FetchSqu1MusicData = 0x103E0
const val FinCCSt = 0xCA34
const val FindAreaMusicHeader = 0x102E6
const val FindAreaPointer = 0x9DE6
const val FindEmptyEnemySlot = 0x9AEB
const val FindEmptyMiscSlot = 0xBF3A
const val FindEventMusicHeader = 0x102EB
const val FindLoop = 0xC561
const val FindPlayerAction = 0xFA29
const val FinishFlame = 0xCB2F
const val FireA = 0xF7D9
const val FireBulletBill = 0xCC3A
const val FireCannon = 0xBD66
const val FireballBGCollision = 0xEB50
const val FireballEnemyCDLoop = 0xDE5E
const val FireballEnemyCollision = 0xDE44
const val FireballExplosion = 0xB9ED
const val FireballObjCore = 0xB96D
const val FireballXSpdData = 0xB96B
const val FirebarCollision = 0xD40A
const val FirebarMirrorData = 0xD303
const val FirebarPosLookupTbl = 0xD2A0
const val FirebarSpin = 0xDB01
const val FirebarSpinDirData = 0xC923
const val FirebarSpinSpdData = 0xC91E
const val FirebarTblOffsets = 0xD307
const val FirebarYPos = 0xD313
const val FireworksSoundScore = 0xD983
const val FireworksXPosData = 0xCB44
const val FireworksYPosData = 0xCB4A
const val FirstBoxGreater = 0xED23
const val FirstSprTilenum = 0xEEBA
const val FirstSprXPos = 0xEEAA
const val FirstSprYPos = 0xEEAE
const val FlagBalls_Residual = 0x9B38
const val FlagpoleCollision = 0xE717
const val FlagpoleGfxHandler = 0xEF3D
const val FlagpoleObject = 0x9B42
const val FlagpoleRoutine = 0xBB93
const val FlagpoleScoreDigits = 0xBB8E
const val FlagpoleScoreMods = 0xBB89
const val FlagpoleScoreNumTiles = 0xEF33
const val FlagpoleSlide = 0xB4EB
const val FlagpoleYPosData = 0xE6FA
const val FlameTimerData = 0xD876
const val FlameYMFAdderData = 0xCAA4
const val FlameYPosData = 0xCAA0
const val FlipBowserOver = 0xF399
const val FlipEnemyVertically = 0xF523
const val FlipPUpRightSide = 0xF14C
const val FlmEx = 0xCA9F
const val FlmeAt = 0xD8EA
const val Floatdown = 0xD1D3
const val FloateyNumTileData = 0x84F3
const val FloateyNumbersRoutine = 0x8517
const val FloateyPart = 0x858F
const val FlyCC = 0xD51B
const val FlyCCBPriority = 0xD506
const val FlyCCTimerData = 0xC97C
const val FlyCCXPositionData = 0xC960
const val FlyCCXSpeedData = 0xC970
const val ForceHPose = 0xEED9
const val ForceInjury = 0xE112
const val ForeSceneryData = 0x94D9
const val FourFrameExtent = 0xFB84
const val Fr12S = 0xD577
const val FreCompLoop = 0x989C
const val FrenzyIDData = 0x9891
const val FreqRegLookupTbl = 0x10B50
const val FrictionData = 0xB6C9
const val Fthrow = 0xFF86
const val GBBAdr = 0xE52C
const val GMLoopB = 0x102C6
const val GSeed = 0xC9C7
const val GSltLp = 0xCC86
const val GameCoreRoutine = 0xB0C5
const val GameEngine = 0xB0DA
const val GameIsOn = 0x93AB
const val GameMenuRoutine = 0x8268
const val GameMode = 0xB0BB
const val GameOverInter = 0x8739
const val GameOverMode = 0x933F
const val GameOverMusData = 0x10895
const val GameOverMusHdr = 0x105B6
const val GameRoutines = 0xB248
const val GameText = 0x87C0
const val GameTextLoop = 0x8891
const val GameTextOffsets = 0x886C
const val GameTimerData = 0x923E
const val GameTimerFireworks = 0xD9B6
const val Get17ID = 0xCBDF
const val GetAltOffset = 0x8586
const val GetAlternatePalette1 = 0x86A2
const val GetAreaDataAddrs = 0x9DF5
const val GetAreaMusic = 0x91F8
const val GetAreaObjXPosition = 0x9D9B
const val GetAreaObjYPosition = 0x9DA3
const val GetAreaObjectID = 0x9CF5
const val GetAreaPal = 0x8AB7
const val GetAreaPalette = 0x8617
const val GetAreaType = 0x9DDC
const val GetBackgroundColor = 0x863B
const val GetBlankPal = 0x8A9C
const val GetBlockBufferAddr = 0x9DB2
const val GetBlockOffscreenBits = 0xFCE8
const val GetBubbleOffscreenBits = 0xFCC3
const val GetCent = 0xC7FD
const val GetCurrentAnimOffset = 0xFB7E
const val GetDToO = 0xD76F
const val GetESpd = 0xC7B7
const val GetEnemyBoundBox = 0xEBDB
const val GetEnemyBoundBoxOfs = 0xE4C2
const val GetEnemyBoundBoxOfsArg = 0xE4C5
const val GetEnemyOffscreenBits = 0xFCE1
const val GetFireballBoundBox = 0xEBC4
const val GetFireballOffscreenBits = 0xFCB9
const val GetFirebarPosition = 0xD4AF
const val GetGfxOffsetAdder = 0xFBB1
const val GetHAdder = 0xD4BC
const val GetHPose = 0xEEDE
const val GetHRp = 0xDC92
const val GetHalfway = 0x931A
const val GetLRp = 0xDC74
const val GetLength = 0x8F8F
const val GetLrgObjAttrib = 0x9D88
const val GetMTileAttrib = 0xE8C0
const val GetMaskedOffScrBits = 0xEBEC
const val GetMiscBoundBox = 0xEBCE
const val GetMiscOffscreenBits = 0xFCCD
const val GetObjRelativePosition = 0xFCA1
const val GetOffScreenBitsSet = 0xFCF4
const val GetOffsetFromAnimCtrl = 0xFBF5
const val GetPRCmp = 0xD752
const val GetPipeHeight = 0x9AD9
const val GetPlayerAnimSpeed = 0xB84A
const val GetPlayerColors = 0x864A
const val GetPlayerOffscreenBits = 0xFCB2
const val GetProperObjOffset = 0xFCDA
const val GetRBit = 0xCBF6
const val GetRow = 0x9BF0
const val GetRow2 = 0x9C0C
const val GetSBNybbles = 0xC000
const val GetScoreDiff = 0x9095
const val GetScreenPosition = 0xB236
const val GetSteFromD = 0xEA55
const val GetVAdder = 0xD4E1
const val GetWNum = 0xE821
const val GetXOffscreenBits = 0xFD2F
const val GetXPhy = 0xB813
const val GetXPhy2 = 0xB823
const val GetYOffscreenBits = 0xFD7C
const val GetYPhy = 0xB781
const val GiveFPScr = 0xBBE4
const val GiveOEPoints = 0xE93A
const val GiveOneCoin = 0xBFCC
const val GmbaAnim = 0xF356
const val GndMove = 0xB5C0
const val GoContinue = 0x8341
const val GoombaDie = 0xDE7E
const val GoombaPoints = 0xDF56
const val GorSLog = 0xFBEA
const val GrLoop = 0xCC84
const val GroundLevelLeadInHdr = 0x105EA
const val GroundLevelPart1Hdr = 0x105C6
const val GroundLevelPart2AHdr = 0x105CC
const val GroundLevelPart2BHdr = 0x105D2
const val GroundLevelPart2CHdr = 0x105D8
const val GroundLevelPart3AHdr = 0x105DE
const val GroundLevelPart3BHdr = 0x105E4
const val GroundLevelPart4AHdr = 0x105F0
const val GroundLevelPart4BHdr = 0x105F6
const val GroundLevelPart4CHdr = 0x105FC
const val GroundMLdInData = 0x10749
const val GroundM_P1Data = 0x10651
const val GroundM_P2AData = 0x10699
const val GroundM_P2BData = 0x106C5
const val GroundM_P2CData = 0x106ED
const val GroundM_P3AData = 0x10712
const val GroundM_P3BData = 0x1072B
const val GroundM_P4AData = 0x10775
const val GroundM_P4BData = 0x1079B
const val GroundM_P4CData = 0x107C4
const val GroundPaletteData = 0x8D93
const val GrowItemRegs = 0x101DB
const val GrowThePowerUp = 0xC09D
const val HBChk = 0xE8F7
const val HBlankDelay = 0x8171
const val HBroWalkingTimerData = 0xC7C9
const val HJump = 0xCF8D
const val HalfwayPageNybbles = 0x92DE
const val HammerBroBGColl = 0xEB01
const val HammerBroJumpCode = 0xCF46
const val HammerBroJumpLData = 0xCF44
const val HammerChk = 0xD79A
const val HammerEnemyOfsData = 0xBE20
const val HammerSprAttrib = 0xEEC2
const val HammerThrowTmrData = 0xCEF8
const val HammerXSpdData = 0xBE29
const val HandleAreaMusicLoopB = 0x102C9
const val HandleAxeMetatile = 0xE6DC
const val HandleChangeSize = 0xFBD1
const val HandleClimbing = 0xE6FF
const val HandleCoinMetatile = 0xE6D3
const val HandleEToBGCollision = 0xE917
const val HandleEnemyFBallCol = 0xDEC1
const val HandleGroupEnemies = 0xCC47
const val HandleNoiseMusic = 0x104A7
const val HandlePECollisions = 0xE056
const val HandlePipeEntry = 0xE7DB
const val HandlePowerUpCollision = 0xDFAB
const val HandleSquare1Music = 0x103D4
const val HandleSquare2Music = 0x1033B
const val HandleStompedShellE = 0xE1D2
const val HandleTriangleMusic = 0x10442
const val HeadChk = 0xE53D
const val Hidden1UpBlock = 0x9CBC
const val Hidden1UpCoinAmts = 0xB50F
const val HighPosUnitData = 0xFD7A
const val HoleBottom = 0xB3CD
const val HoleDie = 0xB3B7
const val HoleMetatiles = 0x9CFD
const val Hole_Empty = 0x9D01
const val Hole_Water = 0x9AFB
const val HurtBowser = 0xDEE8
const val ISpr0Loop = 0x91B9
const val IconDataRead = 0x835A
const val ImpedePlayerMove = 0xE84B
const val ImposeFriction = 0xB890
const val ImposeGravity = 0xC43B
const val ImposeGravityBlock = 0xC400
const val ImposeGravitySprObj = 0xC40A
const val InCastle = 0xB544
const val InPause = 0xFE50
const val Inc2B = 0xC72C
const val Inc3B = 0xC729
const val IncAreaObjOffset = 0x96EE
const val IncMLoop = 0xC593
const val IncModeTask_A = 0x84AE
const val IncModeTask_B = 0x87BC
const val IncMsgCounter = 0x8493
const val IncPXM = 0xD0DF
const val IncSubtask = 0x87B3
const val IncWorldSel = 0x82C9
const val IncrementColumnPos = 0x9402
const val IncrementSFTask1 = 0xD9D6
const val IncrementSFTask2 = 0xDA72
const val InitATLoop = 0x8F20
const val InitBalPlatform = 0xCD28
const val InitBlock_XY_Pos = 0xC190
const val InitBloober = 0xC7E6
const val InitBowser = 0xCA45
const val InitBowserFlame = 0xCAA6
const val InitBuffer = 0x80D9
const val InitBulletBill = 0xC817
const val InitByte = 0x91E4
const val InitByteLoop = 0x91DA
const val InitCSTimer = 0xB6A2
const val InitChangeSize = 0xB495
const val InitCheepCheep = 0xC822
const val InitDropPlatform = 0xCD52
const val InitEnemyFrenzy = 0xCCE9
const val InitEnemyObject = 0xC6ED
const val InitEnemyRoutines = 0xC752
const val InitFireballExplode = 0xEB89
const val InitFireworks = 0xCB50
const val InitFlyingCheepCheep = 0xC980
const val InitGoomba = 0xC78D
const val InitHammerBro = 0xC7CB
const val InitHoriPlatform = 0xCD5A
const val InitHorizFlySwimEnemy = 0xC7E1
const val InitJS = 0xB734
const val InitJumpGPTroopa = 0xCD18
const val InitLCmd = 0xC5BF
const val InitLakitu = 0xC834
const val InitLongFirebar = 0xC928
const val InitMForceData = 0xB6BB
const val InitMLp = 0xC5B7
const val InitNTLoop = 0x8F0C
const val InitNormalEnemy = 0xC7AE
const val InitPageLoop = 0x91D7
const val InitPiranhaPlant = 0xCCCC
const val InitPlatScrl = 0xB22C
const val InitPlatformFall = 0xDCC9
const val InitPodoboo = 0xC793
const val InitRear = 0x97BD
const val InitRedKoopa = 0xC7C0
const val InitRedPTroopa = 0xC7F0
const val InitRetainerObj = 0xC7A6
const val InitScores = 0x8339
const val InitScreen = 0x85E2
const val InitScrlAmt = 0xB1F0
const val InitScroll = 0x8FCB
const val InitShortFirebar = 0xC92B
const val InitSteP = 0xE602
const val InitVStf = 0xC80E
const val InitVertPlatform = 0xCD62
const val InitializeArea = 0x90DC
const val InitializeGame = 0x90C6
const val InitializeMemory = 0x91D0
const val InitializeNameTables = 0x8EEA
const val InjurePlayer = 0xE10C
const val IntermediatePlayerData = 0xFA9C
const val IntroEntr = 0xB285
const val IntroPipe = 0x9A0D
const val InvEnemyDir = 0xEAAE
const val InvOBit = 0xC187
const val InvtD = 0xEA0A
const val JCoinC = 0xBF20
const val JCoinGfxHandler = 0xF093
const val JCoinRun = 0xBF8F
const val JSFnd = 0xE7D9
const val JSMove = 0xB61F
const val JmpEO = 0xCDEE
const val JoypFrict = 0xB8A4
const val JumpEngine = 0x8ECF
const val JumpMForceData = 0xB6A6
const val JumpRegContents = 0xFF51
const val JumpSwimSub = 0xB5D3
const val JumpToDecLength2 = 0x10197
const val JumpingCoinTiles = 0xF08F
const val Jumpspring = 0x9C86
const val JumpspringFrameOffsets = 0xF299
const val JumpspringHandler = 0xBC06
const val Jumpspring_Y_PosData = 0xBC02
const val KSPts = 0xE0C8
const val KeepOnscr = 0xB20C
const val KickedShellPtsData = 0xE053
const val KickedShellXSpdData = 0xE003
const val KillAllEnemies = 0xD6E5
const val KillBB = 0xBE1C
const val KillBlock = 0xC305
const val KillELoop = 0x9880
const val KillEnemies = 0x9879
const val KillEnemyAboveBlock = 0xEB0C
const val KillFireBall = 0xF83C
const val KillLakitu = 0xC845
const val KillLoop = 0xD6E7
const val KillPlayer = 0xE13E
const val KillVine = 0xBCF8
const val KilledAtt = 0xFC31
const val LInj = 0xE201
const val LLeft = 0x89C1
const val LRAir = 0xB616
const val LRWater = 0xB60D
const val L_CastleArea1 = 0xA38E
const val L_CastleArea2 = 0xA3EF
const val L_CastleArea3 = 0xA46E
const val L_CastleArea4 = 0xA4E1
const val L_CastleArea5 = 0xA54E
const val L_CastleArea6 = 0xA5D9
const val L_GroundArea1 = 0xA64A
const val L_GroundArea10 = 0xAA11
const val L_GroundArea11 = 0xAA1A
const val L_GroundArea12 = 0xAA59
const val L_GroundArea13 = 0xAA6E
const val L_GroundArea14 = 0xAAD5
const val L_GroundArea15 = 0xAB3A
const val L_GroundArea16 = 0xABAD
const val L_GroundArea17 = 0xABDE
const val L_GroundArea18 = 0xAC71
const val L_GroundArea19 = 0xACE4
const val L_GroundArea2 = 0xA6AD
const val L_GroundArea20 = 0xAD5D
const val L_GroundArea21 = 0xADB6
const val L_GroundArea22 = 0xADE1
const val L_GroundArea3 = 0xA716
const val L_GroundArea4 = 0xA769
const val L_GroundArea5 = 0xA7F8
const val L_GroundArea6 = 0xA86D
const val L_GroundArea7 = 0xA8D2
const val L_GroundArea8 = 0xA927
const val L_GroundArea9 = 0xA9AC
const val L_UndergroundArea1 = 0xAE14
const val L_UndergroundArea2 = 0xAEB7
const val L_UndergroundArea3 = 0xAF58
const val L_WaterArea1 = 0xAFE5
const val L_WaterArea2 = 0xB024
const val L_WaterArea3 = 0xB09F
const val LakituAndSpinyHandler = 0xC854
const val LakituChk = 0xCCFE
const val LakituDiffAdj = 0xD556
const val LandEnemyInitState = 0xEA1D
const val LandEnemyProperly = 0xE99E
const val LandPlyr = 0xE5E9
const val LargeLiftBBox = 0xCD9E
const val LargeLiftDown = 0xCD9B
const val LargeLiftUp = 0xCD95
const val LargePlatformBoundBox = 0xEC15
const val LargePlatformCollision = 0xE380
const val LargePlatformSubroutines = 0xCEAD
const val LcChk = 0xF624
const val LdGameText = 0x888C
const val LdLDa = 0xD57E
const val LeavePar = 0x97BC
const val LeftFrict = 0xB8A8
const val LeftSwim = 0xD16D
const val LeftWh = 0xBB5F
const val LenSet = 0x9D87
const val LimitB = 0xDDE4
const val LoadAreaMusic = 0x102BC
const val LoadAreaPointer = 0x9DD6
const val LoadControlRegs = 0x10520
const val LoadEnvelopeData = 0x10541
const val LoadEventMusic = 0x10293
const val LoadHeader = 0x102F0
const val LoadNumTiles = 0x8547
const val LoadSqu2Regs = 0x10117
const val LoadTriCtrlReg = 0x104A4
const val LoadUsualEnvData = 0x1054D
const val LoadWaterEventMusEnvData = 0x10559
const val LongBeat = 0x104F7
const val LongN = 0x104A2
const val LoopCmdE = 0x97CE
const val LoopCmdPageNumber = 0xC4FB
const val LoopCmdWorldNumber = 0xC4F0
const val LoopCmdYPosition = 0xC506
const val LrgObj = 0x9772
const val LuigiName = 0x885B
const val LuigiThanksMessage = 0x8E33
const val M1FOfs = 0xD949
const val M2FOfs = 0xD93E
const val M3FOfs = 0xD933
const val MEHor = 0xD009
const val MRetainerMsg = 0x845B
const val MakeBJump = 0xD7CD
const val MakePlatformFall = 0xDB67
const val MarioThanksMessage = 0x8E1F
const val Mask2MSB = 0x9752
const val MaskHPNyb = 0x9329
const val MatchBump = 0xC214
const val MaxCC = 0xC99E
const val MaxLeftXSpdData = 0xB6C2
const val MaxRightXSpdData = 0xB6C5
const val MaxSpdBlockData = 0xC3FB
const val MediN = 0x1049D
const val MetatileGraphics_High = 0x8BD7
const val MetatileGraphics_Low = 0x8BD3
const val MidTreeL = 0x98D3
const val MirrorEnemyGfx = 0xF57B
const val MiscLoop = 0xBF50
const val MiscLoopBack = 0xBFC1
const val MiscObjectsCore = 0xBF4E
const val MiscSqu1MusicTasks = 0x1041A
const val MiscSqu2MusicTasks = 0x103B2
const val MovPTDwn = 0xD093
const val MoveAOId = 0x9793
const val MoveAllSpritesOffscreen = 0x8246
const val MoveBloober = 0xD116
const val MoveBoundBox = 0xE3EE
const val MoveBoundBoxOffscreen = 0xEC2C
const val MoveBubl = 0xBA3A
const val MoveBulletBill = 0xD1EF
const val MoveColOffscreen = 0xF711
const val MoveD_Bowser = 0xD676
const val MoveD_EnemyVertically = 0xC3B6
const val MoveDefeatedBloober = 0xD180
const val MoveDefeatedEnemy = 0xD049
const val MoveDropPlatform = 0xC3E0
const val MoveEOfs = 0xE344
const val MoveESprColOffscreen = 0xF67B
const val MoveESprRowOffscreen = 0xF671
const val MoveEnemyHorizontally = 0xC341
const val MoveEnemySlowVert = 0xC3E5
const val MoveFallingPlatform = 0xC3C0
const val MoveFlyGreenPTroopa = 0xD096
const val MoveFlyingCheepCheep = 0xD50B
const val MoveHammerBroXDir = 0xCF9B
const val MoveJ_EnemyVertically = 0xC3EC
const val MoveJumpingEnemy = 0xD060
const val MoveLakitu = 0xD559
const val MoveLargeLiftPlat = 0xDD9C
const val MoveLiftPlatforms = 0xDDA8
const val MoveNormalEnemy = 0xCFC1
const val MoveObjectHorizontally = 0xC350
const val MoveOnVine = 0xB650
const val MovePiranhaPlant = 0xDA86
const val MovePlatformDown = 0xC412
const val MovePlatformUp = 0xC415
const val MovePlayerHorizontally = 0xC349
const val MovePlayerVertically = 0xC39D
const val MovePlayerYAxis = 0xB434
const val MovePodoboo = 0xCED8
const val MoveRedPTUpOrDown = 0xD087
const val MoveRedPTroopa = 0xC3CC
const val MoveRedPTroopaDown = 0xC3C5
const val MoveRedPTroopaUp = 0xC3CA
const val MoveSixSpritesOffscreen = 0xEFAE
const val MoveSmallPlatform = 0xDDA2
const val MoveSpritesOffscreen = 0x8249
const val MoveSubs = 0xB5AC
const val MoveSwimmingCheepCheep = 0xD206
const val MoveVOffset = 0x8B47
const val MoveWithXMCntrs = 0xD0EB
const val MtchF = 0xE74F
const val MushFlowerBlock = 0xC1E2
const val MushLExit = 0x9945
const val MushroomIconData = 0x8350
const val MushroomLedge = 0x98E5
const val MushroomPaletteData = 0x8E0F
const val MushroomRetainerSaved = 0x8E47
const val MusicHandler = 0x1027D
const val MusicHeaderData = 0x1055D
const val MusicLengthLookupTbl = 0x10BB6
const val MusicLoopBack = 0x10382
const val MusicSelectData = 0x91F2
const val N2Prt = 0xFF6C
const val N2Tone = 0x100E5
const val NKGmba = 0xD05F
const val NMovShellFallBit = 0xEA2E
const val NPROffscr = 0xFA92
const val NSFnd = 0xEB4F
const val NVFLak = 0xF5EA
const val NXSpd = 0xE86B
const val NYSpd = 0xE582
const val NameLoop = 0x88F2
const val Next3Slt = 0xBDB6
const val NextAObj = 0x96CE
const val NextArea = 0xB56B
const val NextBUpd = 0xC33C
const val NextED = 0xCCC9
const val NextFSlot = 0xCD0B
const val NextFbar = 0xD3A0
const val NextMTRow = 0x89C7
const val NextSdeC = 0xEA81
const val NextSprOffset = 0x820C
const val NextStair = 0x9C74
const val NextSubtask = 0x8620
const val NextTBit = 0x95FA
const val NextVO = 0xBCA1
const val NextVSp = 0xEE88
const val NextWh = 0xBB19
const val NightSnowPaletteData = 0x8E07
const val NoAltPal = 0x86AF
const val NoBFall = 0xD6CE
const val NoBGColor = 0x8647
const val NoBlankP = 0x9A2B
const val NoBump = 0xEA9C
const val NoCDirF = 0xE996
const val NoChgMus = 0xB137
const val NoCloud2 = 0x95E9
const val NoColFB = 0xD4A4
const val NoColWrap = 0x9413
const val NoCollisionFound = 0xED44
const val NoDecEnv1 = 0x103C9
const val NoDecEnv2 = 0x10431
const val NoDecTimers = 0x8128
const val NoEToBGCollision = 0xE914
const val NoEnemyCollision = 0xE2BD
const val NoFBall = 0xB9EC
const val NoFD = 0xD1DD
const val NoFPObj = 0xB50B
const val NoFToECol = 0xDEAC
const val NoFore = 0x959D
const val NoFrenzyCode = 0xCCFB
const val NoHFlip = 0xFDEE
const val NoHOffscr = 0xEF32
const val NoHammer = 0xBE5B
const val NoIncDAC = 0xFEEF
const val NoIncPT = 0xD086
const val NoIncXM = 0xD0DE
const val NoInitCode = 0xC78C
const val NoInter = 0x8746
const val NoJSChk = 0xC3AB
const val NoJSFnd = 0xE7DA
const val NoJump = 0xB716
const val NoKillE = 0x988C
const val NoLAFr = 0xF3ED
const val NoMGPT = 0xD0BD
const val NoMoveCode = 0xCE5E
const val NoMoveSub = 0xB5B3
const val NoOfs = 0xECBD
const val NoOfs2 = 0xECDA
const val NoPDwnL = 0x1007C
const val NoPECol = 0xE03E
const val NoPUp = 0xE000
const val NoReset = 0x892A
const val NoRunCode = 0xCE13
const val NoSSw = 0xD1CC
const val NoSideC = 0xE476
const val NoStop1 = 0x102C4
const val NoStopSfx = 0x102A1
const val NoTTick = 0xD9F3
const val NoTimeUp = 0x8703
const val NoTone = 0xFF18
const val NoTopSc = 0x90B2
const val NoUnder = 0x9924
const val NoUnderHammerBro = 0xEB29
const val NoWhirlP = 0x9D36
const val NoZSup = 0xC017
const val NoiseBeatHandler = 0x104CA
const val NoiseSfxHandler = 0x10244
const val NonAnimatedActs = 0xFB3F
const val NonMaskableInterrupt = 0x8087
const val NormObj = 0x9797
const val NormalXSpdData = 0xC7AC
const val NotDOrD4 = 0x1048A
const val NotDefB = 0xD1FA
const val NotECstlM = 0x1052D
const val NotEgg = 0xF3D3
const val NotGoomba = 0xDE8B
const val NotRsNum = 0xF068
const val NotTRO = 0x10361
const val NotTall = 0x99CA
const val NotUse = 0xC719
const val NotWPipe = 0x9787
const val NullJoypad = 0x82E9
const val ObjOffsetData = 0xFCD7
const val OffVine = 0xB2C4
const val OffscrJoypadBitsData = 0xB234
const val OffscreenBoundsCheck = 0xDDCC
const val OnGroundStateSub = 0xB5B4
const val OnePlayerGameOver = 0x8821
const val OnePlayerTimeUp = 0x880E
const val OperModeExecutionTree = 0x823C
const val OtherRope = 0xDC1E
const val OutputCol = 0x8761
const val OutputInter = 0x872D
const val OutputNumbers = 0x8FF8
const val OutputTScr = 0x8782
const val OutputToVRAM = 0x8F92
const val PBFRegs = 0x101B2
const val PIntLoop = 0xFAA4
const val PJumpSnd = 0xB7B4
const val PPHSubt = 0xDD6E
const val PRDiffAdjustData = 0xC848
const val PROfsLoop = 0xFA87
const val PRandomRange = 0xD6D1
const val PRandomSubtracter = 0xD501
const val PTRegC = 0xFE93
const val PTone1F = 0xFE7A
const val PTone2F = 0xFE91
const val PUpDrawLoop = 0xF10F
const val PUpOfs = 0xF15C
const val PUp_VGrow_FreqData = 0x100A3
const val Palette0_MTiles = 0x8BDB
const val Palette1_MTiles = 0x8C77
const val Palette2_MTiles = 0x8D2F
const val Palette3Data = 0x8A7B
const val Palette3_MTiles = 0x8D57
const val ParseRow0e = 0xC6F9
const val PauseRoutine = 0x819C
const val PauseSkip = 0x812B
const val PdbM = 0xCEF5
const val PerformWalk = 0x8409
const val PipeDwnS = 0xE693
const val PlatDn = 0xDBBA
const val PlatF = 0xE87B
const val PlatLiftDown = 0xCDAE
const val PlatLiftUp = 0xCDA1
const val PlatPosDataHigh = 0xCDC6
const val PlatPosDataLow = 0xCDC3
const val PlatSt = 0xDBB4
const val PlatUp = 0xDBAE
const val PlatformFall = 0xDCEE
const val PlatformSideCollisions = 0xE453
const val PlayBeat = 0x10502
const val PlayBigJump = 0xFF4F
const val PlayBlast = 0x100E8
const val PlayBowserFall = 0x1019A
const val PlayBowserFlame = 0x10268
const val PlayBrickShatter = 0x10215
const val PlayBump = 0xFF82
const val PlayCoinGrab = 0x100C3
const val PlayExtraLife = 0x101B7
const val PlayFireballThrow = 0xFF7B
const val PlayFlagpoleSlide = 0xFF3B
const val PlayGrowPowerUp = 0x101D4
const val PlayGrowVine = 0x101D9
const val PlayNoiseSfx = 0x10228
const val PlayPipeDownInj = 0x1005E
const val PlayPowerUpGrab = 0x10103
const val PlaySmackEnemy = 0x10020
const val PlaySmallJump = 0xFF4A
const val PlaySqu1Sfx = 0xFF01
const val PlaySqu2Sfx = 0xFF20
const val PlaySwimStomp = 0xFFFC
const val PlayTimerTick = 0x100CA
const val PlayerAnimTmrData = 0xB847
const val PlayerBGCollision = 0xE4D5
const val PlayerBGPriorityData = 0x9236
const val PlayerBGUpperExtent = 0xE4D3
const val PlayerChangeSize = 0xB46E
const val PlayerCollisionCore = 0xECDE
const val PlayerColors = 0x862F
const val PlayerCtrlRoutine = 0xB2EA
const val PlayerDeath = 0xB4AA
const val PlayerEndLevel = 0xB517
const val PlayerEndWorld = 0x84B2
const val PlayerEnemyCollision = 0xE007
const val PlayerEnemyDiff = 0xEAB1
const val PlayerEntrance = 0xB25B
const val PlayerFireFlower = 0xB4C0
const val PlayerGfxHandler = 0xF9CF
const val PlayerGfxProcessing = 0xFA3A
const val PlayerGfxTblOffsets = 0xF8ED
const val PlayerGraphicsTable = 0xF8FD
const val PlayerHammerCollision = 0xDF66
const val PlayerHeadCollision = 0xC0E2
const val PlayerHole = 0xB397
const val PlayerInjuryBlink = 0xB482
const val PlayerInter = 0x8728
const val PlayerKilled = 0xFA35
const val PlayerLakituDiff = 0xD5AA
const val PlayerLoseLife = 0x92EE
const val PlayerMovementSubs = 0xB580
const val PlayerOffscreenChk = 0xFA74
const val PlayerPhysicsSub = 0xB6D2
const val PlayerPosSPlatData = 0xE47A
const val PlayerRdy = 0xB2D2
const val PlayerStarting_X_Pos = 0x9227
const val PlayerStarting_Y_Pos = 0x922D
const val PlayerStop = 0x99F3
const val PlayerSubs = 0xB364
const val PlayerVictoryWalk = 0x83F3
const val PlayerYSpdData = 0xB6B4
const val PlyrPipe = 0xE69E
const val PortLoop = 0x8F40
const val PosBubl = 0xBA14
const val PosJSpr = 0xBC2C
const val PosPlatform = 0xCDC9
const val PositionEnemyObj = 0xC653
const val PositionPlayerOnHPlat = 0xDD56
const val PositionPlayerOnS_Plat = 0xE47C
const val PositionPlayerOnVPlat = 0xE485
const val PowerUpAttributes = 0xF0E0
const val PowerUpGfxTable = 0xF0D0
const val PowerUpGrabFreqData = 0x10085
const val PowerUpObjHandler = 0xC066
const val PrimaryGameSetup = 0x9162
const val PrincessSaved1 = 0x8E73
const val PrincessSaved2 = 0x8E8A
const val PrintMsg = 0x848C
const val PrintStatusBarNumbers = 0x8FEB
const val PrintVictoryMessages = 0x8438
const val PrintWarpZoneNumbers = 0x88FD
const val ProcADLoop = 0x965A
const val ProcAirBubbles = 0xB94F
const val ProcBowserFlame = 0xD890
const val ProcClimb = 0xB6EC
const val ProcELoop = 0xB0DF
const val ProcEnemyCollisions = 0xE2D3
const val ProcEnemyDirection = 0xE9EB
const val ProcFireball_Bubble = 0xB8F7
const val ProcFireballs = 0xB945
const val ProcFirebar = 0xD315
const val ProcHammerBro = 0xCF02
const val ProcHammerObj = 0xBE60
const val ProcJumpCoin = 0xBF63
const val ProcJumping = 0xB719
const val ProcLPlatCollisions = 0xE40D
const val ProcLoopCommand = 0xC553
const val ProcLoopb = 0x96DD
const val ProcMove = 0xB596
const val ProcMoveRedPTroopa = 0xD066
const val ProcOnGroundActs = 0xFB1A
const val ProcPRun = 0xB7D7
const val ProcSPlatCollisions = 0xE40A
const val ProcSecondEnemyColl = 0xE31B
const val ProcSkid = 0xB873
const val ProcSwim = 0xB5F7
const val ProcSwimmingB = 0xD183
const val ProcessAreaData = 0x9658
const val ProcessBowserHalf = 0xD85F
const val ProcessCannons = 0xBD32
const val ProcessEnemyData = 0xC5DD
const val ProcessLengthData = 0x10512
const val ProcessPlayerAction = 0xFAF5
const val ProcessWhirlpools = 0xBAD3
const val PullID = 0xCC5D
const val PullOfsB = 0xF70B
const val PulleyRopeMetatiles = 0x992C
const val PulleyRopeObject = 0x992F
const val PutAtRightExtent = 0xCAE0
const val PutBehind = 0xC05B
const val PutBlockMetatile = 0x8B4F
const val PutLives = 0x88C3
const val PutMTileB = 0xC144
const val PutOldMT = 0xC143
const val PutPlayerOnVine = 0xE76C
const val PutinPipe = 0xDAFB
const val PwrUpJmp = 0xC03A
const val QuestionBlock = 0x9CCA
const val QuestionBlockRow_High = 0x9B0C
const val QuestionBlockRow_Low = 0x9B0F
const val RImpd = 0xE862
const val RSeed = 0xC9DF
const val RXSpd = 0xE36D
const val RaiseFlagSetoffFWorks = 0xDA1B
const val RdyDecode = 0x96C5
const val RdyNextA = 0xB549
const val ReadJoypads = 0x8F30
const val ReadPortBits = 0x8F3E
const val ReadyNextEnemy = 0xE2C6
const val RedPTroopaGrav = 0xC434
const val RelWOfs = 0xFC6E
const val RelativeBlockPosition = 0xFC86
const val RelativeBubblePosition = 0xFC5D
const val RelativeEnemyPosition = 0xFC7F
const val RelativeFireballPosition = 0xFC67
const val RelativeMiscPosition = 0xFC75
const val RelativePlayerPosition = 0xFC56
const val RemBridge = 0x8B95
const val RemoveBridge = 0xD67C
const val RemoveCoin_Axe = 0x8AFF
const val RendBBuf = 0x961E
const val RendBack = 0x954C
const val RendFore = 0x9589
const val RendTerr = 0x95A4
const val RenderAreaGraphics = 0x892B
const val RenderAttributeTables = 0x8A0B
const val RenderH = 0xEEE6
const val RenderPlayerSub = 0xFABE
const val RenderPul = 0x993F
const val RenderSceneryTerrain = 0x952D
const val RenderSidewaysPipe = 0x9A40
const val RenderUnderPart = 0x9D40
const val RepeatByte = 0x8F96
const val ReplaceBlockMetatile = 0x8B14
const val ResGTCtrl = 0xBA9D
const val ResJmpM = 0xED71
const val ResetMDr = 0xD71F
const val ResetPalFireFlower = 0xB4DE
const val ResetPalStar = 0xB4E1
const val ResetScreenTimer = 0x8922
const val ResetSpritesAndScreenTimer = 0x8919
const val ResetTitle = 0x82F9
const val ResidualGravityCode = 0xC3FD
const val ResidualHeaderData = 0x1059E
const val ResidualMiscObjectCode = 0xED60
const val ResidualXSpdData = 0xE001
const val Rest = 0x103A6
const val RetEOfs = 0xC89E
const val RetXC = 0xEE05
const val RetYC = 0xEE08
const val ReversePlantSpeed = 0xDAB7
const val RevivalRateData = 0xE1D0
const val ReviveStunned = 0xD026
const val RevivedXSpeed = 0xCEFE
const val RghtFrict = 0xB8CA
const val RightCheck = 0x89AB
const val RightPipe = 0xB469
const val RightPlatform = 0xDD87
const val RiseFallPiranhaPlant = 0xDAD1
const val RotPRandomBit = 0x8144
const val Row23C = 0xF63B
const val Row3C = 0xF62F
const val RowOfBricks = 0x9BD9
const val RowOfCoins = 0x9B9B
const val RowOfSolidBlocks = 0x9BEA
const val RunAObj = 0x97EA
const val RunAllH = 0xBED7
const val RunBBSubs = 0xBE0D
const val RunBowser = 0xD6D5
const val RunBowserFlame = 0xCE5F
const val RunDemo = 0x82EE
const val RunEnemyObjectsCore = 0xCDDE
const val RunFB = 0xB9B3
const val RunFR = 0xE752
const val RunFirebarObj = 0xCE71
const val RunFireworks = 0xD954
const val RunGameOver = 0x935C
const val RunGameTimer = 0xBA5B
const val RunHSubs = 0xBEDA
const val RunJCSubs = 0xBFB5
const val RunLargePlatform = 0xCE8F
const val RunNormalEnemies = 0xCE1D
const val RunOffscrBitsSubs = 0xFD0F
const val RunPUSubs = 0xC0CD
const val RunParser = 0xB17C
const val RunRetainerObj = 0xCE14
const val RunSmallPlatform = 0xCE77
const val RunSoundSubroutines = 0xFEBA
const val RunStarFlagObj = 0xD9A1
const val RunVSubs = 0xBCD5
const val SBBAt = 0xF302
const val SBMDir = 0xD13E
const val SBlasJ = 0x10100
const val SBnce = 0xE1F0
const val SBwsrGfxOfs = 0xF33E
const val SChk2 = 0xF01D
const val SChk3 = 0xF028
const val SChk4 = 0xF033
const val SChk5 = 0xF03E
const val SChk6 = 0xF049
const val SChkA = 0xE9B8
const val SFcRt = 0xE210
const val SFlmX = 0xD8A0
const val SJumpSnd = 0xB7BE
const val SLChk = 0xF053
const val SOLft = 0xECD7
const val SORte = 0xECBA
const val SOfs = 0xF8A3
const val SOfs2 = 0xF8B2
const val SPBBox = 0xCD7F
const val SPixelLak = 0xD633
const val SUpdR = 0xFA70
const val Save8Bits = 0x8F65
const val SaveAB = 0xB14E
const val SaveHAdder = 0x8B64
const val SaveJoyp = 0xB30C
const val SaveXSpd = 0xC368
const val SceLoop1 = 0x9576
const val SceLoop2 = 0x9594
const val ScoreOffsets = 0xBFC8
const val ScoreUpdateData = 0x850B
const val ScreenOff = 0x80A4
const val ScreenRoutines = 0x85CD
const val ScrollHandler = 0xB180
const val ScrollLockObject = 0x9870
const val ScrollLockObject_Warp = 0x9853
const val ScrollScreen = 0xB1B7
const val SdeCLoop = 0xEA6A
const val SecondBoxVerticalChk = 0xED0D
const val SecondPartMsg = 0x846E
const val SecondSprTilenum = 0xEEBE
const val SecondSprXPos = 0xEEB2
const val SecondSprYPos = 0xEEB6
const val SecondaryGameSetup = 0x9172
const val SelectBLogic = 0x82A0
const val Set17ID = 0xCBE6
const val SetATHigh = 0x8A20
const val SetAbsSpd = 0xB8F3
const val SetAmtOffset = 0x821B
const val SetAnimC = 0xFBAC
const val SetAnimSpd = 0xB889
const val SetAttrib = 0x89CA
const val SetBBox = 0xC806
const val SetBBox2 = 0xCD24
const val SetBFlip = 0xF6E5
const val SetBGColor = 0x8680
const val SetBPA = 0xCD46
const val SetBehind = 0x96CB
const val SetBitsMask = 0xE22A
const val SetCATmr = 0xE6B4
const val SetCAnim = 0xB6FE
const val SetCollisionFlag = 0xE447
const val SetCrouch = 0xB593
const val SetD6Ste = 0xEA58
const val SetDBSte = 0xDF0E
const val SetDplSpd = 0xC425
const val SetESpd = 0xC7BA
const val SetEndTimer = 0x84A6
const val SetEntr = 0xB40F
const val SetFBTmr = 0xD80C
const val SetFWC = 0xD9D0
const val SetFallS = 0xE4FB
const val SetFlameTimer = 0xD87E
const val SetFor1Up = 0xDFEE
const val SetForStn = 0xE9DF
const val SetFore = 0x984F
const val SetFrT = 0xCAD1
const val SetFreq_Squ1 = 0xFF04
const val SetFreq_Squ2 = 0xFF23
const val SetFreq_Tri = 0xFF28
const val SetGfxF = 0xD8D0
const val SetHFAt = 0xFDF9
const val SetHJ = 0xCF74
const val SetHPos = 0xBEB2
const val SetHSpd = 0xBE98
const val SetHalfway = 0x9336
const val SetHiMax = 0xC3EE
const val SetHmrTmr = 0xD7B6
const val SetInitNTHigh = 0x910D
const val SetKRout = 0xE129
const val SetLMov = 0xD5A4
const val SetLMovD = 0xD5E9
const val SetLSpd = 0xD58B
const val SetLast2Platform = 0xEFEB
const val SetM2 = 0xD66B
const val SetMF = 0xCB24
const val SetMFbar = 0xD37E
const val SetMOfs = 0xBE38
const val SetMdMax = 0xC3E7
const val SetMiscOffset = 0x8222
const val SetMoveDir = 0xB361
const val SetNotW = 0xE97A
const val SetOffscrBitsOffset = 0xFCEC
const val SetOscrO = 0xFDD3
const val SetPESub = 0x92D8
const val SetPRout = 0xE12B
const val SetPSte = 0xE4FD
const val SetPVar = 0xDD70
const val SetPWh = 0xBB74
const val SetPause = 0x81E4
const val SetPlatformTilenum = 0xEFFE
const val SetRSpd = 0xD042
const val SetRTmr = 0xB80E
const val SetRunSpd = 0xB86D
const val SetSDir = 0xD494
const val SetSecHard = 0x9146
const val SetShim = 0xCFBE
const val SetSpSpd = 0xC8FD
const val SetStPos = 0x927D
const val SetStun = 0xE958
const val SetVFbr = 0xD404
const val SetVRAMAddr_A = 0x861D
const val SetVRAMAddr_B = 0x86AC
const val SetVRAMCtrl = 0x8A67
const val SetVRAMOffset = 0x869E
const val SetVXPl = 0xE78A
const val SetWYSpd = 0xE978
const val SetXMoveAmt = 0xC3F0
const val SetYGp = 0xCC6A
const val SetYO = 0xCD71
const val SetoffF = 0xDA29
const val SetupBB = 0xBDDC
const val SetupBubble = 0xBA09
const val SetupCannon = 0x9C36
const val SetupEOffsetFBBox = 0xEC1F
const val SetupExpl = 0xD96A
const val SetupFloateyNumber = 0xE215
const val SetupGFB = 0xD34C
const val SetupGameOver = 0x9348
const val SetupIntermediate = 0x85F3
const val SetupJumpCoin = 0xBF03
const val SetupLakitu = 0xC83A
const val SetupNumSpr = 0x859C
const val SetupNums = 0x9011
const val SetupPlatformRope = 0xDC64
const val SetupPowerUp = 0xC01B
const val SetupToMovePPlant = 0xDAC5
const val SetupVictoryMode = 0x83E4
const val SetupWrites = 0x8F84
const val Setup_Vine = 0xBC7C
const val ShellCollisions = 0xE300
const val ShellOrBlockDefeat = 0xDF2D
const val Shimmy = 0xCFA7
const val ShrPlF = 0xFC0B
const val ShrinkPlatform = 0xEFE9
const val ShrinkPlayer = 0xFBFC
const val ShroomM = 0xC094
const val Shroom_Flower_PUp = 0xDFD0
const val ShufAmtLoop = 0x91AD
const val ShuffleLoop = 0x81F2
const val SideC = 0xE473
const val SideCheckLoop = 0xE611
const val SideExitPipeEntry = 0xB43C
const val SidePipeBottomPart = 0x9A34
const val SidePipeShaftData = 0x9A2C
const val SidePipeTopPart = 0x9A30
const val SilenceData = 0x1066C
const val SilenceHdr = 0x105A8
const val SilentBeat = 0x10500
const val SixSpriteStacker = 0xEE96
const val SizeChk = 0xB33D
const val SkipATRender = 0x93F6
const val SkipByte = 0x91E7
const val SkipCtrlL = 0x10414
const val SkipExpTimer = 0x8124
const val SkipFBar = 0xD3AC
const val SkipFqL1 = 0x103AC
const val SkipIY = 0xDD29
const val SkipMainOper = 0x8192
const val SkipMove = 0xCE40
const val SkipPIn = 0xFEB2
const val SkipPT = 0xCEA4
const val SkipScore = 0xBBE1
const val SkipSoundSubroutines = 0xFECE
const val SkipSprite0 = 0x8175
const val SkipToFB = 0xD7CA
const val SkipToOffScrChk = 0xF4F5
const val SkpFSte = 0xD346
const val SkpVTop = 0xEE75
const val SlidePlayer = 0xB508
const val SlowM = 0xD00C
const val SlowSwim = 0xD1B1
const val SmSpc = 0x10043
const val SmTick = 0x10045
const val SmallBBox = 0xC7EB
const val SmallBP = 0xC169
const val SmallPlatformBoundBox = 0xEBE5
const val SmallPlatformCollision = 0xE3C1
const val SndOn = 0xFE38
const val SnglID = 0xCC5E
const val SolidBlockMetatiles = 0x9BD0
const val SolidMTileUpperExt = 0xE898
const val SolidOrClimb = 0xE578
const val SoundEngine = 0xFE2E
const val SpawnBrickChunks = 0xC25C
const val SpawnFBr = 0xD7EA
const val SpawnFromMouth = 0xCAF7
const val SpawnHammerObj = 0xBE2B
const val SpecObj = 0x978D
const val SpinCounterClockwise = 0xDB1C
const val SpinyRte = 0xC90B
const val SpnySC = 0xF576
const val SprInitLoop = 0x824D
const val SprObjectCollisionCore = 0xECE0
const val SprObjectOffscrChk = 0xF612
const val Sprite0Clr = 0x8152
const val Sprite0Data = 0x90C2
const val Sprite0Hit = 0x8167
const val SpriteShuffler = 0x81E8
const val Squ1NoteHandler = 0x103FC
const val Squ2LengthHandler = 0x10388
const val Squ2NoteHandler = 0x10397
const val Square1SfxHandler = 0xFFA0
const val Square2SfxHandler = 0x10130
const val StaircaseHeightData = 0x9C57
const val StaircaseObject = 0x9C69
const val StaircaseRowData = 0x9C60
const val StarBlock = 0xC1E5
const val StarFChk = 0xCB60
const val StarFlagExit = 0xD9D9
const val StarFlagExit2 = 0xDA85
const val StarFlagTileData = 0xD99D
const val StarFlagXPosAdder = 0xD999
const val StarFlagYPosAdder = 0xD995
const val Star_CloudHdr = 0x10593
const val Star_CloudMData = 0x10608
const val Start = 0x8000
const val StartBTmr = 0xC12D
const val StartClrGet = 0x8661
const val StartGame = 0x827A
const val StartPage = 0x90F8
const val StartWorld1 = 0x8318
const val StatusBarData = 0x8FD9
const val StatusBarNybbles = 0xBFCA
const val StatusBarOffset = 0x8FE5
const val SteadM = 0xD00E
const val StillInGame = 0x930C
const val StkLp = 0xEE98
const val StnE = 0xDF3D
const val StompedEnemyPtsData = 0xE14F
const val StopGrowItems = 0x10202
const val StopPlatforms = 0xDCE4
const val StopPlayerMove = 0xE6CD
const val StopSquare1Sfx = 0x1004E
const val StopSquare2Sfx = 0x10125
const val StoreFore = 0x9E41
const val StoreMT = 0x95C2
const val StoreMusic = 0x9220
const val StoreNewD = 0x9066
const val StoreStyle = 0x9E7E
const val StrAObj = 0x97E1
const val StrBlock = 0x9642
const val StrCOffset = 0x9C53
const val StrFre = 0xC6EA
const val StrID = 0xC6C8
const val StrSprOffset = 0x8209
const val StrType = 0xC058
const val StrWOffset = 0x9D33
const val StrWave = 0xFEF6
const val StrongBeat = 0x104EE
const val SubDifAdj = 0xD62D
const val SubtEnemyYPos = 0xEAD0
const val SubtR1 = 0xD3DD
const val SusFbar = 0xD331
const val SwimCCXMoveData = 0xD202
const val SwimCC_IDData = 0xCBB8
const val SwimKT = 0xFA12
const val SwimKickTileNum = 0xF9CD
const val SwimStompEnvelopeData = 0xFF2D
const val SwimX = 0xD153
const val SzOfs = 0xFBBC
const val TInjE = 0xE101
const val TScrClear = 0x87A9
const val TallBBox = 0xC804
const val TallBBox2 = 0xCD22
const val TaskLoop = 0x874F
const val TerMTile = 0x95B7
const val TerminateGame = 0x936F
const val TerrBChk = 0x95EB
const val TerrLoop = 0x95CC
const val TerrainMetatiles = 0x9500
const val TerrainRenderBits = 0x9504
const val ThankPlayer = 0x8460
const val ThirdP = 0x9541
const val ThreeFrameExtent = 0xFB89
const val ThreeSChk = 0xBD3A
const val TimeRunOutMusData = 0x108C2
const val TimeRunningOutHdr = 0x1058E
const val TimeUpOn = 0xBAB1
const val TitleScreenMode = 0x8258
const val TooFar = 0xDE3D
const val TopEx = 0xC25B
const val TopSP = 0xF87A
const val TopScoreCheck = 0x9092
const val TopStatusBarLine = 0x87C0
const val TransLoop = 0x93C3
const val TransposePlayers = 0x93AC
const val TreeLedge = 0x98B5
const val TriNoteHandler = 0x10471
const val TwoPlayerGameOver = 0x8819
const val TwoPlayerTimeUp = 0x8806
const val Unbreak = 0xC184
const val UnderHammerBro = 0xEB15
const val UndergroundMusData = 0x10961
const val UndergroundMusHdr = 0x105A3
const val UndergroundPaletteData = 0x8DB7
const val UpToFiery = 0xDFFB
const val UpToSuper = 0xDFF4
const val UpdScrollVar = 0xB159
const val UpdSte = 0xC307
const val UpdateLoop = 0xC30D
const val UpdateNumber = 0xC006
const val UpdateScreen = 0x8FC0
const val UpdateShroom = 0x82D6
const val UpdateTopScore = 0x908B
const val UseAdder = 0xC373
const val UseBOffset = 0x8B3F
const val UseMiscS = 0xBF4A
const val UsePosv = 0xC8FC
const val VAHandl = 0xD3EB
const val VBlank1 = 0x800A
const val VBlank2 = 0x8010
const val VDrawLoop = 0xBCE5
const val VPipeSectLoop = 0x9A1C
const val VRAM_AddrTable_High = 0x8072
const val VRAM_AddrTable_Low = 0x805F
const val VRAM_Buffer_Offset = 0x8085
const val VariableObjOfsRelPos = 0xFC92
const val VerticalPipe = 0x9A78
const val VerticalPipeData = 0x9A70
const val VerticalPipeEntry = 0xB417
const val VictoryMLoopBack = 0x10385
const val VictoryMode = 0x83C2
const val VictoryModeSubroutines = 0x83D9
const val VictoryMusData = 0x10B18
const val VictoryMusHdr = 0x105B1
const val VineBlock = 0xC1F0
const val VineCollision = 0xE75A
const val VineEntr = 0xB2A8
const val VineHeightData = 0xBCAE
const val VineObjectHandler = 0xBCB0
const val VineTL = 0xEE5A
const val VineYPosAdder = 0xEE12
const val Vine_AutoClimb = 0xB3F4
const val WBootCheck = 0x801A
const val WSelectBufferTemplate = 0x8262
const val WaitOneRow = 0x9D6A
const val WarpNum = 0x9864
const val WarpNumLoop = 0x8904
const val WarpPipe = 0x9A85
const val WarpZoneNumbers = 0x8860
const val WarpZoneObject = 0xBABB
const val WarpZoneWelcome = 0x882E
const val WaterEventMusEnvData = 0x10BF2
const val WaterMus = 0x1053A
const val WaterMusData = 0x109A2
const val WaterMusHdr = 0x105BB
const val WaterPaletteData = 0x8D6F
const val WaterPipe = 0x99F9
const val WhLoop = 0xBAE4
const val WhPull = 0xBB77
const val WhirlpoolActivate = 0xBB1E
const val WinCastleMusHdr = 0x105C1
const val WinLevelMusData = 0x10900
const val World1Areas = 0x9E9B
const val World2Areas = 0x9EA0
const val World3Areas = 0x9EA5
const val World4Areas = 0x9EA9
const val World5Areas = 0x9EAE
const val World6Areas = 0x9EB2
const val World7Areas = 0x9EB6
const val World8Areas = 0x9EBB
const val WorldAddrOffsets = 0x9E93
const val WorldLivesDisplay = 0x87E7
const val WorldSelectMessage1 = 0x8EA9
const val WorldSelectMessage2 = 0x8EBA
const val WrCMTile = 0xBD08
const val WriteBlankMT = 0x8B0B
const val WriteBlockMetatile = 0x8B20
const val WriteBottomStatusLine = 0x86BA
const val WriteBufferToScreen = 0x8F6A
const val WriteGameText = 0x8876
const val WriteNTAddr = 0x8EFE
const val WritePPUReg1 = 0x8FD2
const val WriteTopScore = 0x87B7
const val WriteTopStatusLine = 0x86B2
const val WrongChk = 0xC5A9
const val XLdBData = 0xFD5E
const val XMRight = 0xD106
const val XMoveCntr_GreenPTroopa = 0xD0BE
const val XMoveCntr_Platform = 0xD0C0
const val XMovingPlatform = 0xDD48
const val XOffscreenBitsData = 0xFD1C
const val XOfsLoop = 0xFD34
const val XSpdSign = 0xB8E9
const val XSpeedAdderData = 0xCEFA
const val X_Physics = 0xB7C1
const val X_SubtracterData = 0xB232
const val YLdBData = 0xFDAA
const val YMDown = 0xDD3B
const val YMovingPlatform = 0xDD09
const val YOffscreenBitsData = 0xFD6E
const val YOfsLoop = 0xFD81
const val YPDiff = 0xD296
const val YSway = 0xD0B0
const val Y_Bubl = 0xBA53
const val YesEC = 0xE2B7
const val YesIn = 0xE90E
