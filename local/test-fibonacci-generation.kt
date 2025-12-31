import com.ivieleague.decompiler6502tokotlin.hand.*
import com.ivieleague.decompiler6502tokotlin.validation.*

fun main() {
    // Generate code for Fibonacci initialization
    val instructions = listOf(
        // LDX #$01 - x = 1
        AssemblyInstruction(
            AssemblyOp.LDX,
            AssemblyAddressing.ByteValue(0x01u, AssemblyAddressing.Radix.Hex)
        ),
        // STX $00 - stores x
        AssemblyInstruction(
            AssemblyOp.STX,
            AssemblyAddressing.Direct("\$00")
        ),
        // SEC - clean carry for subtraction
        AssemblyInstruction(AssemblyOp.SEC),
        // LDY #$07 - calculates 7th fibonacci number
        AssemblyInstruction(
            AssemblyOp.LDY,
            AssemblyAddressing.ByteValue(0x07u, AssemblyAddressing.Radix.Hex)
        ),
        // TYA - transfer y register to accumulator
        AssemblyInstruction(AssemblyOp.TYA),
        // SBC #$03 - handles the algorithm iteration counting (7-3=4 iterations)
        AssemblyInstruction(
            AssemblyOp.SBC,
            AssemblyAddressing.ByteValue(0x03u, AssemblyAddressing.Radix.Hex)
        ),
        // TAY - transfer the accumulator back to y register
        AssemblyInstruction(AssemblyOp.TAY),
        // CLC - clean carry for addition
        AssemblyInstruction(AssemblyOp.CLC),
        // LDA #$02 - a = 2 (second Fibonacci number)
        AssemblyInstruction(
            AssemblyOp.LDA,
            AssemblyAddressing.ByteValue(0x02u, AssemblyAddressing.Radix.Hex)
        ),
        // STA $01 - stores a
        AssemblyInstruction(
            AssemblyOp.STA,
            AssemblyAddressing.Direct("\$01")
        )
    )

    val decompiler = SimpleDecompiler()
    val kotlinCode = decompiler.generateKotlinCode(instructions)

    println("=".repeat(60))
    println("GENERATED KOTLIN CODE FOR FIBONACCI INITIALIZATION")
    println("=".repeat(60))
    println(kotlinCode)
    println("=".repeat(60))
}
