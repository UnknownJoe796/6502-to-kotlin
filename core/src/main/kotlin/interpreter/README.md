# 6502 Interpreter

A complete 6502 CPU interpreter implementation using the existing 6502 constructs from the decompiler project.

## Architecture

### Core Components

1. **CPU6502** - CPU state management
   - Registers: A, X, Y, SP, PC
   - Status flags: N, V, B, D, I, Z, C
   - Cycle counting
   - Status byte conversion

2. **Memory6502** - 64KB memory management
   - Byte and word read/write operations
   - Program loading
   - Memory dumping for debugging
   - Address wraparound handling

3. **Interpreter6502** - Instruction execution engine
   - All 6502 instructions implemented
   - All addressing modes supported
   - Stack operations
   - Branch condition evaluation
   - Label resolution support

## Supported Instructions

### Load/Store Operations
- LDA, LDX, LDY - Load accumulator/X/Y register
- STA, STX, STY - Store accumulator/X/Y register

### Transfer Operations
- TAX, TAY, TXA, TYA - Transfer between A and X/Y
- TSX, TXS - Transfer between stack pointer and X

### Stack Operations
- PHA, PHP - Push accumulator/processor status
- PLA, PLP - Pull accumulator/processor status

### Logical Operations
- AND, EOR, ORA - Bitwise operations
- BIT - Bit test

### Arithmetic Operations
- ADC - Add with carry
- SBC - Subtract with carry
- CMP, CPX, CPY - Compare operations

### Increment/Decrement
- INC, DEC - Memory increment/decrement
- INX, INY, DEX, DEY - Register increment/decrement

### Shift/Rotate Operations
- ASL, LSR - Arithmetic/logical shift
- ROL, ROR - Rotate left/right

### Branch Operations
- BCC, BCS - Branch on carry
- BEQ, BNE - Branch on zero
- BMI, BPL - Branch on negative
- BVC, BVS - Branch on overflow

### Flag Operations
- CLC, CLD, CLI, CLV - Clear flags
- SEC, SED, SEI - Set flags

### Control Flow
- JMP - Jump
- JSR, RTS - Subroutine call/return
- BRK - Break
- NOP - No operation
- RTI - Return from interrupt

## Addressing Modes

All 6502 addressing modes are supported:
- Immediate - `#$42`
- Absolute - `$1000`
- Zero Page - `$10`
- Indexed (X/Y) - `$1000,X` or `$1000,Y`
- Indirect X - `($10,X)`
- Indirect Y - `($10),Y`
- Indirect Absolute - `($1000)` (for JMP)
- Accumulator - `ASL A`
- Implied - `INX`

## Usage Example

```kotlin
// Create interpreter
val interp = Interpreter6502()

// Set up label resolver (optional)
interp.labelResolver = { label ->
    when (label) {
        "start" -> 0x8000
        "data" -> 0x2000
        else -> label.removePrefix("$").toInt(16)
    }
}

// Load a program into memory
val program = listOf<UByte>(
    0xA9u, 0x42u,  // LDA #$42
    0x85u, 0x10u   // STA $10
)
interp.memory.loadProgram(0x8000, program)

// Execute instructions
val lda = AssemblyInstruction(
    AssemblyOp.LDA,
    AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
)
interp.executeInstruction(lda)

// Check result
println(interp.cpu) // CPU state
println("A = ${interp.cpu.A}") // A = 0x42
```

## Testing

Comprehensive unit tests are provided in:
- `Memory6502Test.kt` - Memory operations
- `CPU6502Test.kt` - CPU state management
- `Interpreter6502Test.kt` - Instruction execution
- `Interpreter6502IntegrationTest.kt` - Complex scenarios

Run tests with:
```bash
./gradlew test --tests "com.ivieleague.decompiler6502tokotlin.interpreter.*"
```

## Features

- Accurate flag behavior (N, V, Z, C)
- Proper overflow detection
- Stack operations with wraparound
- Address wraparound in zero page
- Support for all standard 6502 instructions
- Cycle counting support (can be extended)
- Comprehensive test coverage

## Integration with Decompiler

This interpreter uses the same data structures as the decompiler:
- `AssemblyOp` - Operation enum
- `AssemblyInstruction` - Instruction representation
- `AssemblyAddressing` - Addressing mode hierarchy

This allows for seamless integration and testing of decompiled code.
