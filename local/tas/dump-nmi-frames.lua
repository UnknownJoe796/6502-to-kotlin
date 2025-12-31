-- Dump TAS inputs only for frames where NMI is enabled
-- This filters out inputs during NMI-disabled periods (blacked-out screens)
--
-- Run in FCEUX: load ROM, load movie, then run this script
-- Output: nmi-filtered-inputs.txt with format "NMI_INDEX ORIGINAL_FRAME JOY_VALUE"

local outFile = io.open("nmi-filtered-inputs.txt", "w")
local nmiIndex = 0
local totalFrames = 0
local skippedFrames = 0

-- Write header
outFile:write("# NMI-filtered TAS inputs\n")
outFile:write("# Format: NMI_INDEX ORIGINAL_FRAME JOY_HEX\n")
outFile:write("# NMI_INDEX = sequential count of NMI-enabled frames\n")
outFile:write("# ORIGINAL_FRAME = wall clock frame in FCEUX\n")
outFile:write("# JOY_HEX = joypad 1 input value\n")
outFile:write("#\n")

emu.registerafter(function()
    local frame = emu.framecount()
    totalFrames = frame

    -- Read PPUCTRL ($2000) to check if NMI is enabled (bit 7)
    local ppuctrl = memory.readbyte(0x2000)
    local nmiEnabled = (ppuctrl >= 128)  -- bit 7 set

    -- Read joypad 1 input
    local joy = joypad.read(1)
    local joyValue = 0

    -- Convert joypad table to byte value
    -- NES controller bit order: A B Select Start Up Down Left Right (bits 0-7)
    if joy.A then joyValue = joyValue + 0x80 end
    if joy.B then joyValue = joyValue + 0x40 end
    if joy.select then joyValue = joyValue + 0x20 end
    if joy.start then joyValue = joyValue + 0x10 end
    if joy.up then joyValue = joyValue + 0x08 end
    if joy.down then joyValue = joyValue + 0x04 end
    if joy.left then joyValue = joyValue + 0x02 end
    if joy.right then joyValue = joyValue + 0x01 end

    if nmiEnabled then
        outFile:write(string.format("%d %d 0x%02X\n", nmiIndex, frame, joyValue))
        nmiIndex = nmiIndex + 1
    else
        skippedFrames = skippedFrames + 1
    end

    -- Progress output every 1000 frames
    if frame % 1000 == 0 then
        print(string.format("Frame %d: %d NMIs, %d skipped", frame, nmiIndex, skippedFrames))
    end

    -- Stop after enough frames (adjust as needed)
    if frame > 6000 then
        outFile:write(string.format("# Total: %d NMI frames, %d skipped, %d total\n",
            nmiIndex, skippedFrames, totalFrames))
        outFile:close()
        print(string.format("Done! %d NMI frames, %d skipped", nmiIndex, skippedFrames))
        emu.pause()
    end
end)

print("NMI-filtered dump script loaded")
print("Will track NMI-enabled frames and dump inputs")
