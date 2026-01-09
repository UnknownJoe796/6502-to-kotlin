# ROM Files

Place NES ROM files here for testing. These files are not included in the repository.

## Required Files

### For Binary Interpreter TAS Tests

1. **smb.nes** - Super Mario Bros. (World) or (JU) ROM
   - Must be NROM (mapper 0)
   - 32KB PRG-ROM + 8KB CHR-ROM
   - MD5: `811b027eaf99c2def7b933c5208636de` (expected)

## How to Obtain

You need to legally own these games to use their ROMs. Options include:
- Dump from your own cartridge
- Purchase from Nintendo's Virtual Console (and extract)
- Other legal means

## Directory Structure

```
local/
├── roms/
│   ├── README.md (this file)
│   └── smb.nes
└── tas/
    └── smb-any%.fm2 (optional TAS movie)
```
