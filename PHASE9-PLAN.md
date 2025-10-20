# Phase 9: Code Generation - Implementation Plan

## Overview

Phase 9 is the **final phase** that transforms the optimized, analyzed code from Phases 1-8 into clean, idiomatic Kotlin. This phase focuses on **human readability**, **maintainability**, and **Kotlin best practices** while preserving the original assembly's semantics and behavior.

**Status**: We have a working quick-and-dirty emitter (`quick-kotlin-emit.kt`). This plan outlines the path to production-quality code generation.

## Goals

1. **Generate idiomatic Kotlin** - Use Kotlin features (data classes, when expressions, ranges, etc.)
2. **Eliminate register variables** - Use expression trees to avoid intermediate assignments
3. **Recover control structures** - Convert gotos to while/for/if/when
4. **Name variables semantically** - Use Pass 27-28 naming analysis
5. **Format and document** - Add KDoc comments, format code, preserve assembly context
6. **Ensure correctness** - Generated code must be semantically equivalent to assembly

## Phase Dependencies

```
Phases 1-8: Complete Analysis Pipeline
         ↓
Phase 9: Code Generation
    ├── Pass 35: AST Construction (from analysis results)
    ├── Pass 36: Expression Emission (use Pass 22 trees)
    ├── Pass 37: Control Flow Emission (use Pass 14-15 structures)
    ├── Pass 38: Variable & Function Emission
    └── Pass 39: Code Formatting & Documentation
```

## Current State: Quick Emitter

**File**: `src/main/kotlin/quick-kotlin-emit.kt`

**What it does well**:
- ✅ Preserves assembly as comments
- ✅ Generates functional Kotlin
- ✅ Handles all 6502 instructions
- ✅ Multi-function support

**Limitations** (what Phase 9 will fix):
- ❌ Heavy register usage (A, X, Y everywhere)
- ❌ No control structure recovery (uses labels and gotos)
- ❌ No variable naming (just memory arrays)
- ❌ No expression simplification
- ❌ Not idiomatic Kotlin

## Pass Breakdown

---

### **Pass 35: AST Construction**

**Purpose**: Build a Kotlin-specific AST from all Phase 1-8 analysis results

**Key Insight**: We've analyzed the assembly extensively. Now we need to organize that information into a structure suitable for Kotlin emission.

#### Data Structures

```kotlin
/**
 * Kotlin AST - represents the structure of generated code
 */
sealed class KotlinAst {
    // Top-level declarations
    data class File(
        val packageName: String,
        val imports: List<Import>,
        val declarations: List<Declaration>,
        val header: FileHeader
    ) : KotlinAst()

    data class FileHeader(
        val comment: String,
        val originalSource: String,
        val suppressions: List<String>
    )

    // Declarations
    sealed class Declaration : KotlinAst() {
        data class Function(
            val name: String,
            val parameters: List<Parameter>,
            val returnType: KotlinType?,
            val body: Block,
            val kdoc: String?,
            val annotations: List<Annotation>
        ) : Declaration()

        data class Property(
            val name: String,
            val type: KotlinType,
            val initializer: Expression?,
            val isVar: Boolean,
            val kdoc: String?
        ) : Declaration()

        data class DataClass(
            val name: String,
            val properties: List<Property>,
            val kdoc: String?
        ) : Declaration()
    }

    // Statements
    sealed class Statement : KotlinAst() {
        data class Assignment(
            val target: Expression,
            val value: Expression
        ) : Statement()

        data class If(
            val condition: Expression,
            val thenBranch: Block,
            val elseBranch: Block?
        ) : Statement()

        data class When(
            val subject: Expression?,
            val branches: List<WhenBranch>
        ) : Statement()

        data class While(
            val condition: Expression,
            val body: Block,
            val label: String?
        ) : Statement()

        data class For(
            val variable: String,
            val range: Expression,
            val body: Block
        ) : Statement()

        data class Return(
            val value: Expression?
        ) : Statement()

        data class ExpressionStatement(
            val expression: Expression
        ) : Statement()

        data class Comment(
            val text: String,
            val originalAssembly: List<AssemblyLine>?
        ) : Statement()
    }

    // Expressions (reuse Pass 22's Expression trees)
    // These map directly from Pass 22 Expression to Kotlin Expression

    // Types
    sealed class KotlinType {
        object Int : KotlinType()
        object UByte : KotlinType()
        object UShort : KotlinType()
        object Boolean : KotlinType()
        object Unit : KotlinType()
        data class Array(val elementType: KotlinType) : KotlinType()
        data class Custom(val name: String) : KotlinType()
    }

    // Other constructs
    data class Block(val statements: List<Statement>)
    data class Parameter(val name: String, val type: KotlinType)
    data class Annotation(val name: String, val args: Map<String, String> = emptyMap())
}
```

#### Algorithm

1. **Create file structure**:
   ```kotlin
   fun createKotlinFile(
       cfg: CfgConstruction,
       allAnalysis: AllAnalysisResults
   ): KotlinAst.File
   ```

2. **For each function in CFG**:
   - Extract function metadata (name from Pass 28, parameters from Pass 29)
   - Build function signature
   - Construct function body (handled by Pass 36-37)

3. **Create supporting declarations**:
   - Memory-mapped structures (from Pass 19)
   - Enums for states (from Pass 20)
   - Constants (from Pass 18)

4. **Organize imports**:
   - Add necessary Kotlin stdlib imports
   - Add custom runtime support if needed

**Output**: Complete Kotlin AST ready for emission

**Estimated**: ~300 lines

---

### **Pass 36: Expression Emission**

**Purpose**: Convert Pass 22 expression trees directly to Kotlin expressions, eliminating register shuffling

**Key Insight**: We have expression trees! Use them to emit clean, single-statement code.

#### Key Transformations

**Before (current quick emitter)**:
```kotlin
// LDA #$05
A = 0x05
// STA $0200
memory[0x0200] = A
```

**After (with expression trees)**:
```kotlin
// LDA #$05 ; STA $0200
memory[0x0200] = 0x05
```

**Even better (with variable naming)**:
```kotlin
// LDA #$05 ; STA $0200
playerX = 5
```

#### Algorithm

```kotlin
fun emitExpression(expr: com.ivieleague.decompiler6502tokotlin.Expression): KotlinAst.Expression {
    return when (expr) {
        is Expression.Assignment -> {
            // If target is a named variable, use it
            // If target is memory, emit memory access
            KotlinAst.Statement.Assignment(
                target = emitExpression(expr.target),
                value = emitExpression(expr.value)
            )
        }

        is Expression.BinaryOp -> {
            // Emit clean binary operations
            // Apply precedence rules
            KotlinAst.Expression.Binary(...)
        }

        is Expression.MemoryAccess -> {
            // Check if this memory location has a variable name (from Pass 27)
            // If yes, emit variable name
            // If no, emit memory array access
            val varName = lookupVariableName(expr.address)
            if (varName != null) {
                KotlinAst.Expression.Variable(varName)
            } else {
                KotlinAst.Expression.ArrayAccess(
                    array = "memory",
                    index = emitExpression(expr.address)
                )
            }
        }

        is Expression.Literal -> {
            KotlinAst.Expression.Literal(expr.value, inferKotlinType(expr.type))
        }

        is Expression.FunctionCall -> {
            // Emit actual function calls (from JSR analysis)
            KotlinAst.Expression.FunctionCall(
                name = resolveFunctionName(expr.target),
                args = expr.arguments.map { emitExpression(it) }
            )
        }

        // ... etc
    }
}
```

**Features**:
- Direct translation from Pass 22 expression trees
- Variable name resolution from Pass 27
- Type-aware emission using Pass 20 inference
- Parenthesis minimization based on precedence
- Constant folding (from Pass 31)

**Output**: Clean Kotlin expressions with minimal intermediate variables

**Estimated**: ~250 lines

---

### **Pass 37: Control Flow Emission**

**Purpose**: Convert CFG structures into idiomatic Kotlin control flow (if/while/for/when)

**Key Insight**: Use Pass 14-15 structural analysis to recover high-level control structures instead of labels/gotos.

#### Transformations

##### 1. If-Then-Else (from Pass 15)

**Assembly**:
```assembly
CMP #$00
BEQ IsZero
LDA #$FF
JMP Done
IsZero:
LDA #$00
Done:
STA $0200
```

**Generated Kotlin**:
```kotlin
val value = if (accumulator == 0) {
    0x00
} else {
    0xFF
}
memory[0x0200] = value
```

##### 2. While Loops (from Pass 14)

**Assembly**:
```assembly
LDX #$00
Loop:
LDA $0200,X
STA $0300,X
INX
CPX #$10
BNE Loop
```

**Generated Kotlin**:
```kotlin
var index = 0
while (index < 0x10) {
    memory[0x0300 + index] = memory[0x0200 + index]
    index++
}
```

**Even better (with variable naming)**:
```kotlin
for (i in 0 until 16) {
    destArray[i] = sourceArray[i]
}
```

##### 3. Switch/When (from Pass 15)

**Assembly** (jump table):
```assembly
LDA State
ASL A
TAX
LDA JumpTable,X
STA $00
LDA JumpTable+1,X
STA $01
JMP ($00)
```

**Generated Kotlin**:
```kotlin
when (state) {
    0 -> handleState0()
    1 -> handleState1()
    2 -> handleState2()
    else -> handleDefault()
}
```

#### Algorithm

```kotlin
fun emitControlFlow(
    region: StructuralRegion,  // From Pass 15
    cfg: FunctionCfg,
    expressions: FunctionExpressions
): List<KotlinAst.Statement> {

    return when (region) {
        is StructuralRegion.Sequence -> {
            // Emit statements sequentially
            region.regions.flatMap { emitControlFlow(it, cfg, expressions) }
        }

        is StructuralRegion.IfThenElse -> {
            // Emit if statement
            val condition = emitBranchCondition(region.condition)
            val thenBranch = KotlinAst.Block(emitControlFlow(region.thenRegion, cfg, expressions))
            val elseBranch = region.elseRegion?.let {
                KotlinAst.Block(emitControlFlow(it, cfg, expressions))
            }

            listOf(KotlinAst.Statement.If(condition, thenBranch, elseBranch))
        }

        is StructuralRegion.WhileLoop -> {
            // Try to convert to for loop if it's a counting pattern
            val forLoop = tryConvertToForLoop(region)
            if (forLoop != null) {
                listOf(forLoop)
            } else {
                // Emit while loop
                val condition = emitLoopCondition(region)
                val body = KotlinAst.Block(emitControlFlow(region.body, cfg, expressions))
                listOf(KotlinAst.Statement.While(condition, body))
            }
        }

        is StructuralRegion.Switch -> {
            // Emit when expression
            val subject = emitExpression(region.switchValue)
            val branches = region.cases.map { case ->
                KotlinAst.WhenBranch(
                    condition = KotlinAst.Expression.Literal(case.value),
                    body = KotlinAst.Block(emitControlFlow(case.region, cfg, expressions))
                )
            }
            listOf(KotlinAst.Statement.When(subject, branches))
        }

        is StructuralRegion.BasicBlock -> {
            // Emit expressions from this block
            emitBlockStatements(region.block, expressions)
        }
    }
}
```

**Features**:
- Natural loops → while/for
- If-then-else → if expressions when possible
- Jump tables → when expressions
- Break/continue for loop control
- Labeled loops only when necessary

**Output**: Idiomatic Kotlin control flow without gotos

**Estimated**: ~400 lines

---

### **Pass 38: Variable & Function Emission**

**Purpose**: Generate proper variable declarations, function signatures, and supporting code

**Key Insight**: Use Pass 27 variable identification and Pass 28 naming to create clean variable declarations.

#### Variable Declaration

**From Pass 27 analysis**:
```kotlin
IdentifiedVariable(
    id = Memory(0x0086),
    scope = Global,
    type = UInt8,
    accessPattern = Scalar,
    usages = [...]
)
```

**Generated Kotlin**:
```kotlin
// Player X position ($0086)
var playerX: UByte = 0u
```

#### Function Signatures

**From Passes 27-29 analysis**:
```kotlin
FunctionCfg(
    entryLabel = "UpdatePlayer",
    parameters = [Variable(Memory(0x0090), UInt8)],  // From Pass 29
    returnValue = Variable(A, UInt8)
)
```

**Generated Kotlin**:
```kotlin
/**
 * Update player state
 * Original: UpdatePlayer @ $9500
 */
fun updatePlayer(entityIndex: UByte): UByte {
    // function body
}
```

#### Algorithm

```kotlin
// 1. Collect all variables
fun collectVariables(
    varAnalysis: VariableIdentification,
    typeAnalysis: TypeInferenceAnalysis
): List<VariableDeclaration> {

    val globals = varAnalysis.globals.map { variable ->
        VariableDeclaration(
            name = variable.name ?: generateName(variable),
            type = mapToKotlinType(variable.inferredType),
            scope = Scope.Global,
            initializer = inferInitializer(variable),
            kdoc = generateVariableDoc(variable)
        )
    }

    // Similar for locals, parameters, etc.
    return globals + /* locals + params */
}

// 2. Emit function signature
fun emitFunctionSignature(
    function: FunctionCfg,
    params: List<IdentifiedVariable>,
    returnType: InferredType?
): KotlinAst.Declaration.Function {

    val kotlinParams = params.map { param ->
        KotlinAst.Parameter(
            name = param.name ?: "param${param.id}",
            type = mapToKotlinType(param.inferredType)
        )
    }

    return KotlinAst.Declaration.Function(
        name = camelCase(function.entryLabel ?: "func_${function.entryAddress.toHex()}"),
        parameters = kotlinParams,
        returnType = returnType?.let { mapToKotlinType(it) },
        body = /* from Pass 37 */,
        kdoc = generateFunctionDoc(function),
        annotations = listOf(/* @JvmStatic, etc. */)
    )
}

// 3. Emit variable declarations
fun emitVariableDeclaration(variable: VariableDeclaration): String {
    val kdoc = if (variable.kdoc != null) "/** ${variable.kdoc} */\n" else ""
    val keyword = if (variable.isMutable) "var" else "val"
    val init = variable.initializer?.let { " = $it" } ?: ""

    return "${kdoc}${keyword} ${variable.name}: ${variable.type}${init}"
}
```

**Features**:
- Global variables emitted at file level
- Local variables scoped to functions/blocks
- Type annotations from Pass 20
- Meaningful names from Pass 28
- KDoc comments with assembly context
- Proper var/val distinction (immutability analysis)

**Output**: Clean variable and function declarations

**Estimated**: ~350 lines

---

### **Pass 39: Code Formatting & Documentation**

**Purpose**: Final polish - format code, add documentation, ensure correctness

**Key Insight**: The code is semantically correct but needs formatting and documentation for human readers.

#### Features

1. **KDoc Generation**:
   ```kotlin
   /**
    * Initialize game state
    *
    * Sets up player position, enemy states, and level data.
    *
    * Original assembly: GameInit @ $8000
    * Called from: NMI handler, Reset vector
    *
    * @see updatePlayer
    * @see initEnemies
    */
   fun gameInit() { ... }
   ```

2. **Assembly Context Comments**:
   ```kotlin
   // Original assembly (8 instructions compressed to 1 line):
   // LDA #$05 ; LDX #$00 ; LDY #$10 ; STA $0200,X ; STX $0201 ; STY $0202 ; RTS
   playerPosition = Point(x = 5, y = 16, sprite = 0)
   ```

3. **Code Formatting**:
   - Indentation (4 spaces)
   - Line wrapping (120 chars)
   - Blank lines between functions
   - Consistent spacing

4. **Suppression Annotations**:
   ```kotlin
   @file:Suppress(
       "UNUSED_VARIABLE",  // Some decompiled vars may appear unused
       "MagicNumber"       // Decompiled code has many magic numbers
   )
   ```

5. **Import Organization**:
   ```kotlin
   import kotlin.experimental.and
   import kotlin.experimental.or
   import kotlin.experimental.xor
   ```

#### Algorithm

```kotlin
fun formatAndDocument(ast: KotlinAst.File): String {
    val sb = StringBuilder()

    // 1. File header
    sb.appendLine(formatFileHeader(ast.header))
    sb.appendLine()

    // 2. Package and imports
    sb.appendLine("package ${ast.packageName}")
    sb.appendLine()
    ast.imports.forEach { sb.appendLine(formatImport(it)) }
    sb.appendLine()

    // 3. Top-level declarations
    ast.declarations.forEach { decl ->
        sb.appendLine(formatDeclaration(decl))
        sb.appendLine()
    }

    return sb.toString()
}

fun formatDeclaration(decl: KotlinAst.Declaration): String {
    return when (decl) {
        is KotlinAst.Declaration.Function -> {
            buildString {
                // KDoc
                decl.kdoc?.let { appendLine(formatKDoc(it)) }

                // Annotations
                decl.annotations.forEach { appendLine(formatAnnotation(it)) }

                // Signature
                append("fun ${decl.name}(")
                append(decl.parameters.joinToString { "${it.name}: ${it.type}" })
                append(")")
                decl.returnType?.let { append(": $it") }
                appendLine(" {")

                // Body
                appendLine(formatBlock(decl.body, indent = 1))

                append("}")
            }
        }

        is KotlinAst.Declaration.Property -> {
            formatProperty(decl)
        }

        is KotlinAst.Declaration.DataClass -> {
            formatDataClass(decl)
        }
    }
}
```

**Features**:
- Consistent formatting following Kotlin conventions
- Rich KDoc with cross-references
- Assembly context preserved in comments
- Readable line lengths
- Proper indentation

**Output**: Formatted, documented Kotlin source ready for developers

**Estimated**: ~200 lines

---

## Complete Pass Structure

```
Pass 35: AST Construction (~300 lines)
    ↓
Pass 36: Expression Emission (~250 lines)
    ↓
Pass 37: Control Flow Emission (~400 lines)
    ↓
Pass 38: Variable & Function Emission (~350 lines)
    ↓
Pass 39: Code Formatting & Documentation (~200 lines)
    ↓
Final Kotlin Source Code ✨
```

**Total estimated**: ~1,500 lines of implementation

---

## Example: Complete Transformation

### Input Assembly (SMB fragment)

```assembly
; Initialize player position
InitPlayer:
    LDA #$05        ; X = 5
    STA Player_X
    LDA #$10        ; Y = 16
    STA Player_Y
    LDA #$00        ; State = idle
    STA Player_State
    RTS

; Update player position
UpdatePlayer:
    LDA Player_State
    BEQ IdleState
    CMP #$01
    BEQ MovingState
    RTS

IdleState:
    ; Check input
    LDA Controller
    AND #$01
    BEQ UpdateDone
    LDA #$01
    STA Player_State
UpdateDone:
    RTS

MovingState:
    LDA Player_X
    CLC
    ADC Player_VelX
    STA Player_X
    RTS
```

### Output Kotlin (Phase 9)

```kotlin
package game.smb

/**
 * Super Mario Bros. decompiled code
 * Source: smbdism.asm
 */

// Player state
var playerX: UByte = 0u        // $0086
var playerY: UByte = 0u        // $0087
var playerState: UByte = 0u    // $0088
var playerVelX: Byte = 0       // $0089

// Input
var controller: UByte = 0u     // $4016

/**
 * Initialize player position
 *
 * Original: InitPlayer @ $8000
 */
fun initPlayer() {
    // LDA #$05 ; STA Player_X
    playerX = 5u

    // LDA #$10 ; STA Player_Y
    playerY = 16u

    // LDA #$00 ; STA Player_State
    playerState = 0u
}

/**
 * Update player position based on state
 *
 * Original: UpdatePlayer @ $8010
 */
fun updatePlayer() {
    when (playerState.toInt()) {
        0 -> {
            // Idle state - check for input
            // LDA Controller ; AND #$01 ; BEQ UpdateDone
            if ((controller and 0x01u) != 0u.toUByte()) {
                // LDA #$01 ; STA Player_State
                playerState = 1u
            }
        }

        1 -> {
            // Moving state - update position
            // LDA Player_X ; CLC ; ADC Player_VelX ; STA Player_X
            playerX = (playerX.toInt() + playerVelX).toUByte()
        }
    }
}
```

---

## Testing Strategy

### Unit Tests (per pass)

```kotlin
class Pass35AstConstructionTest {
    @Test fun `build file structure`()
    @Test fun `create function declarations`()
    @Test fun `map expression trees to AST`()
    // ... 8-10 tests per pass
}
```

### Integration Tests

```kotlin
class Phase9IntegrationTest {
    @Test fun `complete pipeline simple program`()
    @Test fun `complete pipeline with loops`()
    @Test fun `complete pipeline with functions`()
    @Test fun `generated code compiles`()
    @Test fun `generated code executes correctly`()
}
```

### Validation

1. **Syntactic**: Generated Kotlin must compile
2. **Semantic**: Execute tests comparing assembly vs. Kotlin behavior
3. **Readability**: Manual review of generated code quality

---

## Implementation Priority

### Phase 9A: MVP (Week 1-2)

Focus on correctness over beauty:

1. ✅ Pass 35: Basic AST construction
2. ✅ Pass 36: Expression emission (simple, all exprs)
3. ✅ Pass 37: Control flow (if/while, defer for loops)
4. ✅ Pass 38: Variables (basic naming)
5. ✅ Pass 39: Minimal formatting

**Goal**: Generate compilable, correct Kotlin

### Phase 9B: Quality (Week 3-4)

Improve readability:

1. Enhanced expression emission (minimize parentheses)
2. For loop conversion from while
3. Variable name improvements
4. KDoc generation
5. When expression generation

**Goal**: Generate readable, maintainable Kotlin

### Phase 9C: Polish (Week 5+)

Make it beautiful:

1. Data class generation for structs
2. Enum class generation for states
3. Extension functions for common patterns
4. Inlining of trivial functions
5. DSL-style builders where applicable

**Goal**: Generate idiomatic, elegant Kotlin

---

## Success Metrics

**MVP (Phase 9A)**:
- [ ] All generated code compiles without errors
- [ ] 100% of assembly instructions have Kotlin equivalents
- [ ] Generated code preserves semantics (test suite passes)
- [ ] Can decompile full SMB ROM

**Quality (Phase 9B)**:
- [ ] <10% of generated code uses goto/labels
- [ ] 80%+ of variables have semantic names
- [ ] Functions have KDoc comments
- [ ] Generated code follows Kotlin style guide

**Polish (Phase 9C)**:
- [ ] 0% goto usage (all structured control flow)
- [ ] 95%+ variables semantically named
- [ ] Data classes for multi-byte structures
- [ ] Generated code is maintainable by humans

---

## Files to Create

### Implementation
- `src/main/kotlin/pass-35-ast-construction.kt` (~300 lines)
- `src/main/kotlin/pass-36-expression-emit.kt` (~250 lines)
- `src/main/kotlin/pass-37-control-flow-emit.kt` (~400 lines)
- `src/main/kotlin/pass-38-variable-function-emit.kt` (~350 lines)
- `src/main/kotlin/pass-39-formatting.kt` (~200 lines)

### Tests
- `src/test/kotlin/Pass35AstConstructionTest.kt`
- `src/test/kotlin/Pass36ExpressionEmitTest.kt`
- `src/test/kotlin/Pass37ControlFlowEmitTest.kt`
- `src/test/kotlin/Pass38VariableFunctionEmitTest.kt`
- `src/test/kotlin/Pass39FormattingTest.kt`
- `src/test/kotlin/Phase9IntegrationTest.kt`

### Documentation
- `PHASE9-IMPLEMENTATION-STATUS.md` (track progress)
- Update `README.md` with Phase 9 commands

---

## Dependencies on Previous Phases

| Pass | Depends On | Why |
|------|------------|-----|
| Pass 35 | All Phases 1-8 | Needs complete analysis |
| Pass 36 | Pass 22 (Expression Trees) | Direct mapping |
| Pass 36 | Pass 27 (Variables) | Variable name lookup |
| Pass 36 | Pass 20 (Types) | Type-aware emission |
| Pass 37 | Pass 14-15 (Structures) | Control flow recovery |
| Pass 37 | Pass 32 (CFG Simplification) | Clean control flow |
| Pass 38 | Pass 27-28 (Variable Naming) | Variable declarations |
| Pass 38 | Pass 29 (Parameters) | Function signatures |
| Pass 39 | All above | Final formatting |

---

## Next Steps

1. **Review this plan** - Ensure it covers all requirements
2. **Create Pass 35** - Start with AST construction
3. **Implement incrementally** - One pass at a time with tests
4. **Iterate** - Improve quality with each pass
5. **Integrate** - Connect all passes into pipeline
6. **Validate** - Test on real 6502 code (SMB, etc.)

---

*This plan represents the **final phase** of the decompiler. After Phase 9, we'll have a complete, working 6502-to-Kotlin decompiler!*
