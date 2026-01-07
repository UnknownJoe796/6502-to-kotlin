#!/bin/bash
echo "Quick pass/fail check:"
for func in pauseRoutine soundEngine processWhirlpools spriteShuffler updateScreen getPlayerColors findAreaPointer; do
  result=$(./gradlew :smb:test --tests "GeneratedFunctionTests.${func}*" 2>&1)
  if echo "$result" | grep -q "BUILD SUCCESSFUL"; then
    echo "PASS: $func"
  else
    fails=$(echo "$result" | grep -c "FAILED")
    echo "FAIL: $func ($fails failed)"
  fi
done
