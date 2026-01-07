#!/bin/bash
# Test each function group and report pass/fail

FUNCS="pauseRoutine soundEngine updateScreen renderAttributeTables relativePlayerPosition processWhirlpools processAreaData playSqu2Sfx jumpSwimSub initializeNameTables incAreaObjOffset getMiscOffscreenBits getAreaDataAddrs findAreaPointer demoEngine colorRotation chkForPlayerAttrib moveEnemySlowVert drawPowerUp spriteShuffler getPlayerColors"

for func in $FUNCS; do
  result=$(./gradlew :smb:test --tests "GeneratedFunctionTests.${func}*" 2>&1)
  if echo "$result" | grep -q "BUILD SUCCESSFUL"; then
    echo "PASS: $func"
  else
    echo "FAIL: $func"
  fi
done
