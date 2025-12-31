-- Dump full RAM ($0000-$07FF) every frame for TAS comparison
-- Output format: one line per frame with hex bytes
-- Run in FCEUX: load ROM, load movie, then run this script
-- Or: fceux smb.nes --playmov happylee-warps.fm2 --loadlua dump-all-ram.lua

local outFile = io.open("fceux-full-ram.bin", "wb")
local indexFile = io.open("fceux-frame-index.txt", "w")
local maxFrames = 18000  -- Full TAS length (17867 frames + buffer)

indexFile:write("# Frame index for RAM dump\n")
indexFile:write("# Format: FRAME_NUM BYTE_OFFSET NMI_ENABLED\n")

local byteOffset = 0
local nmiCount = 0

emu.registerafter(function()
    local frame = emu.framecount()

    -- Check if NMI is enabled (PPUCTRL bit 7)
    local ppuctrl = memory.readbyte(0x2000)
    local nmiEnabled = (ppuctrl >= 128) and 1 or 0
    if nmiEnabled == 1 then
        nmiCount = nmiCount + 1
    end

    -- Write frame index entry
    indexFile:write(string.format("%d %d %d\n", frame, byteOffset, nmiEnabled))

    -- Dump all RAM ($0000-$07FF = 2048 bytes)
    for addr = 0, 0x7FF do
        outFile:write(string.char(memory.readbyte(addr)))
    end
    byteOffset = byteOffset + 2048

    -- Progress output every 500 frames
    if frame % 500 == 0 then
        print(string.format("Frame %d: %d NMIs, %d bytes written", frame, nmiCount, byteOffset))
    end

    -- Stop after maxFrames
    if frame >= maxFrames then
        outFile:close()
        indexFile:close()
        print(string.format("Done! %d frames, %d NMIs, %d bytes total", frame, nmiCount, byteOffset))
        emu.pause()
    end
end)

print("Full RAM dump script loaded")
print(string.format("Will dump %d frames of RAM ($0000-$07FF)", maxFrames))
