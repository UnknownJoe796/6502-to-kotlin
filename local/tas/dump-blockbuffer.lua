-- FCEUX Lua script to dump game state for comparison
local output_file = io.open("fceux-blockbuffer.txt", "w")
local frame_count = 0

function on_frame()
    frame_count = frame_count + 1

    -- Dump state at key comparison frames
    if (frame_count >= 40 and frame_count <= 50) or
       (frame_count >= 100 and frame_count <= 105) or
       (frame_count >= 200 and frame_count <= 205) or
       (frame_count >= 295 and frame_count <= 310) then
        local player_x = memory.readbyte(0x0086)
        local player_y = memory.readbyte(0x00CE)
        local player_page = memory.readbyte(0x006D)
        local screen_page = memory.readbyte(0x071A)
        local player_speed = memory.readbytesigned(0x0057)
        local side_coll = memory.readbyte(0x0785)
        local opermode = memory.readbyte(0x0770)
        local player_state = memory.readbyte(0x001D)

        output_file:write(string.format("F%d: X=%d Y=%d Pg=%d/%d Spd=%d St=%d SideColl=%d Mode=%d\n",
            frame_count, player_x, player_y, player_page, screen_page, player_speed, player_state, side_coll, opermode))
        output_file:flush()
    end

    if frame_count >= 320 then
        output_file:write("\nDump complete at frame " .. frame_count .. "\n")
        output_file:close()
        emu.pause()
    end
end

emu.registerafter(on_frame)
print("State comparison script loaded")
