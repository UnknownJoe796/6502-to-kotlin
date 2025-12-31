-- FCEUX Lua script to dump game state during TAS playback
-- Run with: fceux --loadlua dump-state.lua --playmov happylee-warps.fm2 smb.nes

local output_file = io.open("fceux-state-dump.txt", "w")
local frame_count = 0
local max_frames = 6000

-- SMB memory addresses
local ADDR = {
    Player_X = 0x0086,
    Player_Y = 0x00CE,
    Player_Page = 0x006D,
    Screen_Page = 0x071A,
    Player_Speed = 0x0057,
    Player_State = 0x001D,
    Player_Y_Speed = 0x009F,
    OperMode = 0x0770,
    FrameCounter = 0x0009,
    WorldNumber = 0x075F,
    LevelNumber = 0x0760,
    NumberofLives = 0x075A,
    SavedJoypad1Bits = 0x06FC,
}

function dump_state()
    local x = memory.readbyte(ADDR.Player_X)
    local y = memory.readbyte(ADDR.Player_Y)
    local pg = memory.readbyte(ADDR.Player_Page)
    local scr = memory.readbyte(ADDR.Screen_Page)
    local spd = memory.readbytesigned(ADDR.Player_Speed)
    local st = memory.readbyte(ADDR.Player_State)
    local yspd = memory.readbytesigned(ADDR.Player_Y_Speed)
    local mode = memory.readbyte(ADDR.OperMode)
    local fc = memory.readbyte(ADDR.FrameCounter)
    local world = memory.readbyte(ADDR.WorldNumber)
    local level = memory.readbyte(ADDR.LevelNumber)
    local lives = memory.readbyte(ADDR.NumberofLives)
    local joy = memory.readbyte(ADDR.SavedJoypad1Bits)

    local line = string.format("F%d X=%d Y=%d Pg=%d/%d Spd=%d St=%d YSpd=%d Mode=%d FC=%d W=%d-%d Lives=%d Joy=0x%x",
        frame_count, x, y, pg, scr, spd, st, yspd, mode, fc, world+1, level+1, lives, joy)

    output_file:write(line .. "\n")

    -- Also print to console every 100 frames
    if frame_count % 100 == 0 then
        print(line)
    end
end

function on_frame()
    dump_state()
    frame_count = frame_count + 1

    if frame_count >= max_frames then
        output_file:close()
        print("Dump complete: " .. frame_count .. " frames")
        emu.pause()
    end
end

-- Register the frame callback
emu.registerafter(on_frame)

print("State dump script loaded. Will dump " .. max_frames .. " frames.")
