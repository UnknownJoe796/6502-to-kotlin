# Assembly-Time Constants Support Plan

## Problem Statement

The parser currently only tracks **labels** (symbols that appear before instructions/data), but doesn't handle **assembly-time constant definitions** using the `=` directive. This means symbols like:

```assembly
Sprite0HitDetectFlag  = $0722
Player_X_Position     = $86
Enemy_Y_Speed         = $0705,X
```

Are not included in the symbol table, preventing memory pattern analysis and type inference from understanding these important variable names.

## Current Behavior

**What works:**
```assembly
MyLabel:
    LDA $0722    ; This address is tracked
```

**What doesn't work:**
```assembly
MyVariable = $0722
    LDA MyVariable    ; Symbol not resolved, memory location not tracked
```

## Impact

- **Super Mario Bros** has 100+ assembly constants defined with `=`
- These represent the most important game variables (player position, enemy states, timers, flags)
- Without resolving these, the memory layout analysis misses critical semantic information
- Type inference can't assign meaningful names to these variables

## Solution Architecture

### Phase 1: Parser Enhancement (Pass 1)

**Objective:** Recognize and parse assembly-time constant definitions

**Changes to `pass-1-parsing.kt`:**

1. **Add new data structure for constant definitions:**
```kotlin
/**
 * Assembly-time constant definition (using = directive)
 */
data class AssemblyConstant(
    val name: String,
    val value: AssemblyAddressing  // Can be hex, decimal, or expression
)
```

2. **Update AssemblyLine to include constants:**
```kotlin
data class AssemblyLine(
    val label: String?,
    val instruction: AssemblyInstruction?,
    val data: AssemblyData?,
    val constant: AssemblyConstant?,  // NEW
    val comment: String?,
    val originalLine: String
)
```

3. **Add constant definition regex:**
```kotlin
private val constantPattern = Regex(
    """^\s*([A-Za-z_][A-Za-z0-9_]*)\s*=\s*(.+?)(?:\s*;.*)?$"""
)
```

4. **Parse constant definitions:**
```kotlin
fun String.parseToAssemblyLine(): AssemblyLine {
    // ... existing label parsing ...

    // Check for constant definition
    val constantMatch = constantPattern.matchEntire(this)
    if (constantMatch != null) {
        val (name, valueStr) = constantMatch.destructured
        val value = parseAddressing(valueStr.trim())
        return AssemblyLine(
            label = null,
            instruction = null,
            data = null,
            constant = AssemblyConstant(name, value),
            comment = extractComment(this),
            originalLine = this
        )
    }

    // ... rest of existing parsing ...
}
```

### Phase 2: Symbol Table Enhancement (Pass 2)

**Objective:** Add constants to the symbol table during address resolution

**Changes to `pass-2-resolution.kt`:**

1. **Collect constants before processing lines:**
```kotlin
fun AssemblyCodeFile.resolveAddresses(baseAddress: Int = 0): AddressResolution {
    val resolved = mutableListOf<ResolvedLine>()
    val labelToAddress = mutableMapOf<String, Int>()
    var pc = baseAddress

    // PHASE 1: Collect all assembly-time constants
    this.lines.forEach { line ->
        line.constant?.let { constant ->
            val address = resolveConstantValue(constant.value, labelToAddress)
            if (address != null) {
                labelToAddress[constant.name] = address
            }
        }
    }

    // PHASE 2: Process lines and labels (existing code)
    this.lines.forEach { line ->
        // ... existing label and instruction processing ...
    }

    return AddressResolution(resolved = resolved, labelToAddress = labelToAddress)
}
```

2. **Add function to resolve constant values:**
```kotlin
/**
 * Resolve a constant definition to an integer address
 * Supports: $XXXX (hex), decimal, and references to other constants
 */
private fun resolveConstantValue(
    addressing: AssemblyAddressing,
    existingConstants: Map<String, Int>
): Int? {
    return when (addressing) {
        is AssemblyAddressing.ValueHex ->
            addressing.value.toInt() and 0xFF

        is AssemblyAddressing.ValueDecimal ->
            addressing.value

        is AssemblyAddressing.ValueReference ->
            existingConstants[addressing.label]

        is AssemblyAddressing.Label -> {
            // Try parsing as hex or decimal first
            val parsed = parseHexAddr(addressing.label) ?: addressing.label.toIntOrNull()
            parsed ?: existingConstants[addressing.label]
        }

        is AssemblyAddressing.DirectX -> {
            // Handle indexed constants like "Enemy_Y_Speed = $0705,X"
            // Just resolve base address; indexing info preserved elsewhere
            val baseAddr = parseHexAddr(addressing.label) ?: existingConstants[addressing.label]
            baseAddr
        }

        is AssemblyAddressing.DirectY -> {
            val baseAddr = parseHexAddr(addressing.label) ?: existingConstants[addressing.label]
            baseAddr
        }

        else -> null
    }
}
```

3. **Handle forward references:**
```kotlin
/**
 * Resolve constants in multiple passes to handle forward references
 */
private fun resolveAllConstants(
    lines: List<AssemblyLine>
): Map<String, Int> {
    val constants = mutableMapOf<String, Int>()
    val unresolved = mutableListOf<AssemblyConstant>()

    // Collect all constant definitions
    lines.forEach { line ->
        line.constant?.let { unresolved.add(it) }
    }

    // Resolve in multiple passes (max 10 to handle chains)
    var changed = true
    var passCount = 0
    while (changed && passCount < 10) {
        changed = false
        passCount++

        val stillUnresolved = mutableListOf<AssemblyConstant>()
        unresolved.forEach { constant ->
            val value = resolveConstantValue(constant.value, constants)
            if (value != null) {
                constants[constant.name] = value
                changed = true
            } else {
                stillUnresolved.add(constant)
            }
        }
        unresolved.clear()
        unresolved.addAll(stillUnresolved)
    }

    return constants
}
```

### Phase 3: Testing

**Test Cases to Add:**

1. **Simple hex constant:**
```kotlin
@Test
fun testSimpleHexConstant() {
    val assembly = """
        MyVar = ${'$'}0722
        LDA MyVar
    """.trimIndent()

    val lines = assembly.parseToAssemblyCodeFile()
    val resolution = lines.resolveAddresses(0x8000)

    // Verify constant is in symbol table
    assertEquals(0x0722, resolution.labelToAddress["MyVar"])

    // Verify memory pattern analysis finds it
    val cfg = lines.constructCfg(resolution, ...)
    val patterns = lines.analyzeMemoryPatterns(cfg, constants, resolution)

    assertTrue(patterns.globalMemoryMap.containsKey(0x0722))
}
```

2. **Indexed constant:**
```kotlin
@Test
fun testIndexedConstant() {
    val assembly = """
        EnemyArray = ${'$'}0200,X
        LDA EnemyArray
    """.trimIndent()
    // Verify both constant resolution and indexed pattern detection
}
```

3. **Forward reference:**
```kotlin
@Test
fun testForwardReference() {
    val assembly = """
        FirstVar = SecondVar
        SecondVar = ${'$'}0100
    """.trimIndent()
    // Verify both resolve to $0100
}
```

4. **Super Mario Bros integration test:**
```kotlin
@Test
fun testSMBConstantsResolved() {
    val smbFile = File("smbdism.asm")
    val assembly = smbFile.readText()
    val lines = assembly.parseToAssemblyCodeFile()
    val resolution = lines.resolveAddresses(0x8000)

    // Check specific SMB constants
    assertEquals(0x0722, resolution.labelToAddress["Sprite0HitDetectFlag"])
    assertEquals(0x0086, resolution.labelToAddress["Player_X_Position"])

    // Verify memory patterns analysis includes them
    val patterns = lines.analyzeMemoryPatterns(...)
    assertTrue(patterns.globalMemoryMap.containsKey(0x0722))
}
```

### Phase 4: Update Downstream Passes

**Passes that benefit from constant resolution:**

1. **Pass 19 (Memory Patterns):** ✓ Already updated to use symbol table
2. **Pass 20 (Type Inference):** Will automatically benefit from better memory location tracking
3. **Pass 27-29 (Variable Naming):** Can use constant names as default variable names

**Future enhancement - Pass 27:**
```kotlin
/**
 * When assigning names to variables, prefer assembly constant names
 */
fun inferVariableName(address: Int, resolution: AddressResolution): String {
    // Find constant name for this address
    val constantName = resolution.labelToAddress.entries
        .find { it.value == address }
        ?.key

    if (constantName != null && !constantName.startsWith("L_")) {
        return constantName  // Use the original assembly constant name
    }

    // Fall back to auto-generated name
    return "var_${address.toString(16).uppercase()}"
}
```

## Implementation Order

1. ✅ **Pass 19 Memory Patterns:** Already updated to use symbol table for resolution
2. **Phase 1:** Parser enhancement (~2 hours)
   - Add AssemblyConstant data structure
   - Update parsing logic
   - Add tests for constant parsing
3. **Phase 2:** Symbol table enhancement (~2 hours)
   - Implement multi-pass constant resolution
   - Handle forward references
   - Add tests for resolution
4. **Phase 3:** Integration testing (~1 hour)
   - Test on Super Mario Bros
   - Verify all constants resolve correctly
   - Update SMBMemoryLayoutDemo to show named variables
5. **Phase 4:** Documentation update (~30 minutes)
   - Update CLAUDE.md with constant handling info
   - Document supported constant syntax

## Expected Results After Implementation

### Current Output:
```
HIGH RAM ($0800-$1FFF): 0 total locations
```

### Expected Output After Fix:
```
HIGH RAM ($0800-$1FFF): 47 total locations
Showing top 40 by usage:

  $0722 (Sprite0HitDetectFlag): Scalar, R/W, accesses: 6, reads: 1, writes: 5
  $086 (Player_X_Position): Scalar, R/W, accesses: 125, reads: 78, writes: 47
  $0705 (Enemy_Y_Speed): Array[X], R/W, accesses: 34, reads: 20, writes: 14
  ...
```

### Memory Analysis Improvements:
- **Before:** 1,197 tracked locations (mostly anonymous)
- **After:** 1,197+ tracked locations with **semantic names**
- **Type inference quality:** Names help identify variable purposes
- **Decompilation readability:** Output uses original variable names

## Risks and Considerations

1. **Circular References:** Handle with max iteration count (10 passes)
2. **Expression Support:** Current plan only handles simple values; complex expressions like `BaseAddr + 5` would need expression evaluator
3. **Macro Expansion:** Don't confuse macros with constants
4. **Case Sensitivity:** Assembly is typically case-sensitive for labels
5. **Performance:** Multi-pass resolution could be slow on very large files (mitigate with iteration limit)

## Success Metrics

- [ ] Parse all `=` directives in smbdism.asm without errors
- [ ] Resolve 100+ SMB constants to correct addresses
- [ ] Memory pattern analysis tracks `Sprite0HitDetectFlag` at $0722
- [ ] SMBMemoryLayoutDemo shows named variables
- [ ] No regressions in existing tests
- [ ] All Phase 5 tests still pass

## Future Enhancements

1. **Expression Evaluation:** Support `CONSTANT = $0700 + $22` syntax
2. **Macro Support:** Handle `.define` and `.macro` directives
3. **Export Metadata:** Generate variable name mapping file for decompilation
4. **Cross-Reference Analysis:** Track which constants are used where
5. **Unused Constant Detection:** Warn about defined but unused constants
