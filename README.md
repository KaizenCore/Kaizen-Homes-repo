# KaizenHomes

A modern Minecraft Paper plugin for advanced home management with beautiful GUIs, sharing systems, and teleportation features.

## Features

### Core Functionality
- **Home Management**: Set, delete, rename, and update homes with ease
- **Interactive GUIs**: Beautiful inventory-based menus for all operations
- **Privacy Modes**: Private, Shared, and Public home visibility options
- **Sharing System**: Share homes with specific players
- **Teleportation**: Smart teleportation with warmup timers and cooldown periods
- **Safety Checks**: Automatic safe location detection before teleporting

### Advanced Features
- **Death Homes**: Automatically save your location on death
- **Bed Sync**: Sync homes with bed respawn points
- **Custom Icons**: Choose from 20+ materials for home icons
- **Categories**: Organize homes with visual categories
- **Public Browser**: Browse and visit public homes from other players
- **Admin Panel**: Comprehensive GUI for managing all plugin settings

### Visual Excellence
- **RGB Gradients**: Beautiful gradient text throughout the plugin
- **Preset Themes**: Kaizen, Fire, Ocean, Nature, Sunset, Purple, Gold, and more
- **Action Bar**: Real-time feedback with gradient messages
- **Titles**: Stunning title displays for teleportation
- **Sound Effects**: Modern Adventure API sound system

## Requirements

- **Minecraft**: 1.21+
- **Server**: Paper or Paper-based server (Purpur, Pufferfish, etc.)
- **Java**: 21+

## Installation

1. Download the latest release from the [Releases](https://github.com/KaizenCore/Kaizen-Homes-repo/releases) page
2. Place `KaizenHomes-1.0.jar` in your server's `plugins/` folder
3. Restart or reload your server
4. Configure settings in `plugins/kaizen/kaizenhomes/config.yml`

## Building from Source

```bash
# Clone the repository
git clone https://github.com/KaizenCore/Kaizen-Homes-repo.git
cd Kaizen-Homes-repo

# Build with Gradle
./gradlew build

# The compiled JAR will be in build/libs/
```

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/home <name>` | Teleport to a home | `khomes.home` |
| `/home set <name>` | Create a new home | `khomes.sethome` |
| `/home delete <name>` | Delete a home | `khomes.delhome` |
| `/homes` | Open homes GUI | `khomes.homes` |
| `/publichomes` | Browse public homes | `khomes.publichomes` |
| `/khomes admin` | Open admin panel | `khomes.admin` |
| `/khomes reload` | Reload configuration | `khomes.reload` |

## Permissions

### Player Permissions
- `khomes.home` - Use /home command
- `khomes.sethome` - Create homes
- `khomes.delhome` - Delete homes
- `khomes.homes` - Access homes GUI
- `khomes.publichomes` - Browse public homes

### Home Limits
- `khomes.limit.1` - 1 home (default)
- `khomes.limit.3` - 3 homes
- `khomes.limit.5` - 5 homes
- `khomes.limit.10` - 10 homes
- `khomes.limit.unlimited` - Unlimited homes

### Admin Permissions
- `khomes.admin` - Access admin panel
- `khomes.reload` - Reload configuration
- `khomes.bypass.cooldown` - Bypass teleport cooldowns
- `khomes.bypass.warmup` - Instant teleportation

## Configuration

The plugin stores data in `plugins/kaizen/kaizenhomes/`:
- `config.yml` - Main configuration file
- `homes/` - Player home data (per-player YAML files)

### Key Settings
- Teleport warmup duration (default: 3 seconds)
- Cooldown period (default: 60 seconds)
- Safety check toggle
- Death home auto-save
- Bed sync enable/disable
- GUI customization options

## Tech Stack

- **Java 21**
- **Paper API 1.21**
- **Adventure API** - Modern text components and sounds
- **Gradle** - Build system
- **YAML Storage** - Async per-player storage

## Architecture

```
kaizen.core.kaizenHomes/
├── commands/          # Command handlers
├── config/            # Configuration management
├── gui/               # Inventory-based GUIs
├── listeners/         # Event listeners
├── managers/          # Business logic
├── models/            # Data models
├── storage/           # Data persistence
└── utils/             # Helper utilities
```

## Support

For issues, feature requests, or contributions, please visit the [GitHub Issues](https://github.com/KaizenCore/Kaizen-Homes-repo/issues) page.

## License

This project is part of the Kaizen plugin suite.
