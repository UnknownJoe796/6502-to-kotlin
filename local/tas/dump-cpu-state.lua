-- FCEUX Lua script to dump CPU state and RAM every frame
-- This enables true supervised execution by providing:
-- 1. CPU registers (A, X, Y, SP, P, PC)
-- 2. Full RAM ($0000-$07FF)
-- 3. Controller input state

local outputFile = io.open("fceux-cpu-state.bin", "wb")
local indexFile = io.open("fceux-cpu-index.txt", "w")

indexFile:write("# Frame index for CPU state dump\n")
indexFile:write("# Format: FRAME_NUM BYTE_OFFSET NMI_TRIGGERED PC A X Y SP P BUTTONS\n")

local frameCount = 0
local maxFrames = 6000
local byteOffset = 0

-- Track if NMI occurred this frame
local nmiThisFrame = 0

memory.registerexecute(0xFFFA, function()
    -- NMI vector read - NMI is happening
    nmiThisFrame = 1
end)

local function dumpState()
    if frameCount >= maxFrames then return end

    local R = memory.getregister
    local pc = R("pc")
    local a = R("a")
    local x = R("x")
    local y = R("y")
    local sp = R("s")
    local p = R("p")
    local buttons = joypad.get(1)

    -- Convert button table to byte
    local btnByte = 0
    if buttons.A then btnByte = btnByte + 0x01 end
    if buttons.B then btnByte = btnByte + 0x02 end
    if buttons.select then btnByte = btnByte + 0x04 end
    if buttons.start then btnByte = btnByte + 0x08 end
    if buttons.up then btnByte = btnByte + 0x10 end
    if buttons.down then btnByte = btnByte + 0x20 end
    if buttons.left then btnByte = btnByte + 0x40 end
    if buttons.right then btnByte = btnByte + 0x80 end

    -- Write index entry
    indexFile:write(string.format("%d %d %d %04X %02X %02X %02X %02X %02X %02X\n",
        frameCount, byteOffset, nmiThisFrame, pc, a, x, y, sp, p, btnByte))

    -- Write CPU registers to binary file (8 bytes)
    -- Format: PC_lo, PC_hi, A, X, Y, SP, P, buttons
    outputFile:write(string.char(pc % 256))
    outputFile:write(string.char(math.floor(pc / 256)))
    outputFile:write(string.char(a))
    outputFile:write(string.char(x))
    outputFile:write(string.char(y))
    outputFile:write(string.char(sp))
    outputFile:write(string.char(p))
    outputFile:write(string.char(btnByte))

    -- Write full RAM (2KB)
    for addr = 0, 0x7FF do
        outputFile:write(string.char(memory.readbyte(addr)))
    end

    byteOffset = byteOffset + 8 + 2048  -- 8 bytes CPU + 2KB RAM
    frameCount = frameCount + 1
    nmiThisFrame = 0

    if frameCount % 500 == 0 then
        print("Dumped frame " .. frameCount)
    end

    if frameCount >= maxFrames then
        print("Dump complete: " .. frameCount .. " frames")
        outputFile:close()
        indexFile:close()
    end
end

emu.registerafter(dumpState)
print("CPU state dumper started. Will dump " .. maxFrames .. " frames.")
print("Output: fceux-cpu-state.bin (8 + 2048 bytes per frame)")
print("Index: fceux-cpu-index.txt")
