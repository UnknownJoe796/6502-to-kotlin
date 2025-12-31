-- Dump full RAM ($0000-$07FF) at specific frames for comparison with interpreter
-- Run in FCEUX: load ROM, load movie (happylee-warps.fm2), then run this script
-- Or: fceux smb.nes --playmov happylee-warps.fm2 --loadlua dump-full-ram.lua

local dumpFrames = {187, 192, 200, 210, 220, 250, 300}  -- FC values to dump (matching interpreter)
local dumpSet = {}
for _, f in ipairs(dumpFrames) do
    dumpSet[f] = true
end

local outFile = io.open("fceux-ram-dump.txt", "w")

function dumpRAM(frame)
    outFile:write(string.format("\n=== FRAME %d ===\n", frame))

    -- Key game state first
    local operMode = memory.readbyte(0x0770)
    local frameCounter = memory.readbyte(0x09)
    local playerX = memory.readbyte(0x86)
    local playerY = memory.readbyte(0xCE)
    local playerPage = memory.readbyte(0x6D)
    local speed = memory.readbyte(0x57)
    local playerState = memory.readbyte(0x1D)

    outFile:write(string.format("OperMode=%d FC=%d X=%d Y=%d Page=%d Speed=%d State=%d\n",
        operMode, frameCounter, playerX, playerY, playerPage, speed, playerState))

    -- Dump zero page ($00-$FF)
    outFile:write("\nZero Page ($00-$FF):\n")
    for row = 0, 15 do
        outFile:write(string.format("$%02X: ", row * 16))
        for col = 0, 15 do
            local addr = row * 16 + col
            outFile:write(string.format("%02X ", memory.readbyte(addr)))
        end
        outFile:write("\n")
    end

    -- Dump pages $01-$07 (stack and RAM)
    for page = 1, 7 do
        outFile:write(string.format("\nPage $%02X00-$%02XFF:\n", page, page))
        for row = 0, 15 do
            local baseAddr = page * 256 + row * 16
            outFile:write(string.format("$%04X: ", baseAddr))
            for col = 0, 15 do
                local addr = baseAddr + col
                outFile:write(string.format("%02X ", memory.readbyte(addr)))
            end
            outFile:write("\n")
        end
    end

    outFile:flush()
end

emu.registerafter(function()
    local frame = emu.framecount()
    if dumpSet[frame] then
        dumpRAM(frame)
        print("Dumped RAM at frame " .. frame)
    end

    -- Stop after last dump frame (don't pause, just close file)
    if frame > 500 then
        outFile:write("\nDump complete at frame " .. frame .. "\n")
        outFile:close()
        print("RAM dump complete - file saved")
        -- Don't pause, let movie continue
    end
end)

print("RAM dump script loaded - will dump at frames: " .. table.concat(dumpFrames, ", "))
