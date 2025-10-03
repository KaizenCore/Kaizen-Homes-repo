# Admin Panel Guide

The KaizenHomes Admin Panel provides a comprehensive GUI for managing plugin settings and all player homes.

## Access

**Command:** `/khomes admin`
**Permission:** `kaizenhomes.admin` (default: op)

## Features

### 1. Settings Overview

The admin panel displays all current plugin settings in an easy-to-read GUI format:

#### General Settings
- **Default Home Limit** - Shows the base home limit for players
- **Death Home** - Toggle status (saves death location)
- **Bed Sync** - Toggle status (sync home with bed)

#### Teleportation Settings
- **Safety Check** - Whether unsafe locations are checked
- **Warmup Timer** - Seconds before teleport
- **Cooldown Timer** - Seconds between teleports
- **Cancel on Move** - Whether movement cancels teleport

#### Sharing Settings
- **Sharing System** - Enable/disable home sharing
- **Max Shared Players** - Maximum players per home
- **Public Homes** - Enable/disable public homes

#### Effects Settings
- **Particles** - Toggle particle effects (shows type)
- **Sounds** - Toggle sound effects
- **Titles** - Toggle title messages

#### Economy Settings
- **Economy** - Vault integration status
- Shows set home cost and teleport cost

### 2. Configuration Management

**Reload Config Button** - Reload the config.yml file without restarting the server
- Located at the bottom of the panel
- Refreshes the GUI to show updated values
- Useful after manually editing config.yml

### 3. All Homes Browser

**View All Homes Button** - Opens a GUI showing all homes from all players

Features:
- See every home on the server
- Shows owner, location, privacy mode, category
- **Left Click** - Teleport to the home
- **Right Click** - Delete the home (admin action)
- Total homes count displayed
- Back button to return to main panel

### 4. Editing Settings

All settings are **fully editable** directly in the GUI:

#### Toggle Settings (Click to Toggle)
- Death Home
- Bed Sync
- Safety Check
- Cancel on Move
- Sharing System
- Public Homes
- Particles
- Sounds
- Titles
- Economy

#### Numeric Settings (Click to Edit)
- **Default Home Limit** - Enter value (1-100)
- **Warmup Timer** - Enter seconds (0-30)
- **Cooldown Timer** - Enter seconds (0-300)
- **Max Shared Players** - Enter value (1-50)

When editing numeric values:
1. Click the setting item
2. Enter the new value in chat
3. Type 'cancel' to cancel the edit
4. GUI reopens automatically with updated value
5. Changes are saved instantly to config.yml

All changes take effect immediately and are automatically saved to the config file.

## GUI Layout

```
╔═══════════════════════════════════════════════════════╗
║              KAIZEN HOMES ADMIN PANEL                 ║
╠═══════════════════════════════════════════════════════╣
║                                                       ║
║  [Home Limit]  [Death Home]  [Bed Sync]             ║
║                                                       ║
║  [Safety]  [Warmup]  [Cooldown]  [Cancel Move]      ║
║                                                       ║
║  [Sharing]  [Max Shared]  [Public Homes]            ║
║                                                       ║
║  [Particles]  [Sounds]  [Titles]    [Economy]       ║
║                                                       ║
╠═══════════════════════════════════════════════════════╣
║  [View All Homes]  [Reload Config]  [Close]         ║
╚═══════════════════════════════════════════════════════╝
```

## All Homes GUI

```
╔═══════════════════════════════════════════════════════╗
║              ALL HOMES (ADMIN)                        ║
╠═══════════════════════════════════════════════════════╣
║                                                       ║
║  [Home1 - Player1]  [Home2 - Player2]  [...]        ║
║  [Home3 - Player3]  [Home4 - Player4]  [...]        ║
║  [Home5 - Player5]  [Home6 - Player6]  [...]        ║
║                      ...                             ║
║                                                       ║
╠═══════════════════════════════════════════════════════╣
║               [Info]                [Back]           ║
╚═══════════════════════════════════════════════════════╝
```

## Use Cases

### View Current Settings
```
/khomes admin
```
Opens the panel to view all current settings at a glance.

### Edit a Toggle Setting
```
/khomes admin
→ Click any toggle item (Death Home, Bed Sync, etc.)
→ Setting instantly toggles and saves
```

### Edit a Numeric Setting
```
/khomes admin
→ Click a numeric item (Home Limit, Warmup, etc.)
→ Enter new value in chat
→ GUI reopens with updated value
```

### Reload Configuration
Use this after manually editing config.yml:
1. `/khomes admin`
2. Click "Reload Config" button
3. Settings reloaded from file

### Browse All Homes
```
/khomes admin
→ Click "View All Homes"
```
See all homes from all players on the server.

### Teleport to Any Home (Admin)
```
/khomes admin
→ Click "View All Homes"
→ Left click any home
```
Useful for checking reported home locations or testing.

### Delete a Home (Admin)
```
/khomes admin
→ Click "View All Homes"
→ Right click the home to delete
```
Permanently removes a home (use carefully!).

## Color Coding

- **Gold/Orange Gradients** - Item names and important values
- **Green ✓** - Feature enabled
- **Red ✗** - Feature disabled
- **Gray** - Descriptive text
- **Dark Gray Italic** - Hints and instructions

## Tips

1. **Quick Edits** - Use the GUI for quick setting changes without editing files
2. **Chat Input** - When editing numbers, type 'cancel' if you change your mind
3. **Instant Save** - All changes are saved immediately to config.yml
4. **Testing** - Toggle settings on/off to test different configurations quickly
5. **Home Management** - Browse all homes to identify and remove outdated/unused homes
6. **File Reload** - Use "Reload Config" button if you manually edit config.yml

## Permissions

Only players with `kaizenhomes.admin` permission (or OP status) can:
- Open the admin panel
- Edit plugin settings via GUI
- Toggle features on/off
- Change numeric settings
- View all homes
- Teleport to any home
- Delete any home

Regular players cannot access these features.
