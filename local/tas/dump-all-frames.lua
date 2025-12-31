-- FCEUX Lua script to dump ALL frames from 170-220 to find Mario spawn
local output_file = io.open("fceux-all-frames.txt", "w")
local frame_count = 0

function on_frame()
    frame_count = frame_count + 1

    -- Dump every frame from 170-220 to find exact Mario spawn
    if frame_count >= 170 and frame_count <= 320 then
        local player_x = memory.readbyte(0x0086)
        local player_y = memory.readbyte(0x00CE)
        local player_page = memory.readbyte(0x006D)
        local screen_page = memory.readbyte(0x071A)
        local player_speed = memory.readbytesigned(0x0057)
        local side_coll = memory.readbyte(0x0785)
        local opermode = memory.readbyte(0x0770)
        local player_state = memory.readbyte(0x001D)
        local opermode_task = memory.readbyte(0x0772)
        local frame_counter = memory.readbyte(0x09)
        local saved_joypad = memory.readbyte(0x06FC)
        local y_speed = memory.readbytesigned(0x009F)

        output_file:write(string.format("F%d: X=%d Y=%d Pg=%d/%d Spd=%d YSpd=%d St=%d Mode=%d Task=%d FC=%d Joy=0x%02X\n",
            frame_count, player_x, player_y, player_page, screen_page, player_speed, y_speed, player_state, opermode, opermode_task, frame_counter, saved_joypad))
        output_file:flush()
    end

    if frame_count >= 330 then
        output_file:write("\nDump complete at frame " .. frame_count .. "\n")
        output_file:close()
        emu.pause()
    end
end

emu.registerafter(on_frame)
print("All frames dump script loaded - dumping frames 170-320")
