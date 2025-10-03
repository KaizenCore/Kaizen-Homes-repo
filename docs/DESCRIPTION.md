# KaizenHomes

![Version](https://img.shields.io/badge/version-1.0--beta-blue)
![Minecraft](https://img.shields.io/badge/minecraft-1.21+-green)
![Paper](https://img.shields.io/badge/paper-required-orange)
![Java](https://img.shields.io/badge/java-21-red)
![License](https://img.shields.io/badge/license-All%20Rights%20Reserved-lightgrey)
[![GitHub](https://img.shields.io/badge/github-KaizenCore-black?logo=github)](https://github.com/KaizenCore/Kaizen-Homes-repo)

> **A modern, feature-rich home management plugin with stunning GUIs and RGB gradient text**

Transform your server's home system with KaizenHomes - the ultimate home management solution featuring beautiful interactive menus, advanced sharing capabilities, and eye-catching RGB gradients throughout.

---

## ✨ Key Features

### 🏠 Advanced Home Management
- **Unlimited Homes** (permission-based limits)
- **Interactive GUI** for all home operations
- **Custom Icons** - Choose from 20+ materials
- **Categories** - Organize homes visually
- **Rename & Update** homes anytime
- **Death Homes** - Auto-save location on death
- **Bed Sync** - Sync homes with bed respawn points

### 🔒 Privacy & Sharing System
- **Three Privacy Modes**:
  - **Private** - Only you can access
  - **Shared** - Share with specific players
  - **Public** - Anyone can visit
- **Per-Player Sharing** - Control who can visit each home
- **Public Browser** - Discover and visit public homes from other players

### 🚀 Smart Teleportation
- **Warmup System** - Configurable delay before teleportation
- **Cooldown Management** - Prevent spam with configurable cooldowns
- **Safety Checks** - Automatic safe location detection
- **Visual Feedback** - Beautiful titles and action bars
- **Sound Effects** - Modern Adventure API sounds
- **Particle Effects** - Stunning visual effects on teleport

### 🎨 Beautiful Visuals
- **RGB Gradients** everywhere - Messages, GUIs, titles
- **7 Preset Themes**:
  - 🌸 Kaizen (Pink to Purple)
  - 🔥 Fire (Red to Orange)
  - 🌊 Ocean (Blue to Cyan)
  - 🌿 Nature (Green to Lime)
  - 🌅 Sunset (Orange to Pink)
  - 💜 Purple (Purple to Magenta)
  - 🏆 Gold (Yellow to Orange)
- **Rainbow Gradients** - Multi-color effects
- **Custom Gradients** - Create your own color schemes

### ⚙️ Admin Features
- **In-Game Admin Panel** - Edit all settings via GUI
- **Live Config Editor** - Changes save instantly
- **All Homes Browser** - View and manage any player's homes
- **Direct Teleportation** - Jump to any home as admin
- **Bulk Management** - Delete homes, manage limits
- **Reload Command** - Apply config changes without restart

---

## 📋 Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/home <name>` | Teleport to your home | `khomes.home` |
| `/home set <name>` | Create a new home at your location | `khomes.sethome` |
| `/home delete <name>` | Delete one of your homes | `khomes.delhome` |
| `/homes` | Open the homes menu GUI | `khomes.homes` |
| `/publichomes` | Browse public homes from all players | `khomes.publichomes` |
| `/khomes admin` | Open the admin configuration panel | `khomes.admin` |
| `/khomes reload` | Reload the configuration | `khomes.reload` |

---

## 🔑 Permissions

### Player Permissions
```yaml
khomes.home          # Use /home command
khomes.sethome       # Create homes
khomes.delhome       # Delete homes
khomes.homes         # Access homes GUI
khomes.publichomes   # Browse public homes
```

### Home Limit Permissions
```yaml
khomes.limit.1         # 1 home (default)
khomes.limit.3         # 3 homes
khomes.limit.5         # 5 homes
khomes.limit.10        # 10 homes
khomes.limit.unlimited # Unlimited homes
```

### Admin Permissions
```yaml
khomes.admin              # Full admin access
khomes.reload             # Reload configuration
khomes.bypass.cooldown    # Bypass cooldowns
khomes.bypass.warmup      # Instant teleportation
```

---

## 📦 Installation

1. **Download** the latest version from the releases page
2. **Place** `KaizenHomes-1.0.jar` in your server's `plugins/` folder
3. **Restart** your server (or use a plugin manager)
4. **Configure** settings in `plugins/kaizen/kaizenhomes/config.yml`
5. **Enjoy!** Start setting homes with `/home set <name>`

---

## ⚙️ Configuration

The plugin creates its data folder at `plugins/kaizen/kaizenhomes/`:

```
plugins/kaizen/kaizenhomes/
├── config.yml          # Main configuration
└── homes/              # Player home data
    ├── player1.yml
    ├── player2.yml
    └── ...
```

### Key Configuration Options

```yaml
# Teleportation Settings
teleport:
  warmup: 3              # Seconds before teleport
  cooldown: 60           # Seconds between teleports
  safety-check: true     # Check if location is safe

# Feature Toggles
features:
  death-homes: true      # Save location on death
  bed-sync: true         # Sync with bed respawn
  public-homes: true     # Enable public home browser

# GUI Settings
gui:
  gradient-theme: "kaizen"  # Default gradient preset
  sound-effects: true       # Play sounds on actions
```

---

## 🎯 Use Cases

### For Survival Servers
- Players can set multiple homes across the world
- Death homes help players recover their items
- Bed sync keeps spawn points organized

### For Creative Servers
- Quick teleportation between builds
- Share builds with friends via shared homes
- Public homes for showcasing creations

### For Multiplayer Networks
- Town/faction homes with sharing
- Public warp-like homes for community areas
- Admin tools for managing player homes

---

## 🛠️ Technical Details

- **Platform**: Paper 1.21+ (Purpur, Pufferfish compatible)
- **Java Version**: 21
- **API**: Paper API + Adventure API
- **Storage**: Async YAML per-player files
- **Performance**: All storage operations are non-blocking
- **Modern**: Uses Adventure API for text components and sounds

---

## 📸 Screenshots

> *Coming soon! Check the GitHub repository for screenshots and demos.*

---

## 🐛 Support & Issues

Found a bug or have a feature request?

- **GitHub Issues**: [Report Here](https://github.com/KaizenCore/Kaizen-Homes-repo/issues)
- **Discord**: Coming soon!

---

## 📄 License

© 2024 KaizenCore. All rights reserved.

---

## 🌟 Why Choose KaizenHomes?

✅ **Modern & Beautiful** - RGB gradients and polished GUIs
✅ **Feature-Rich** - Everything you need for home management
✅ **Performant** - Async operations, optimized code
✅ **Configurable** - Every feature can be customized
✅ **Admin-Friendly** - In-game configuration editor
✅ **User-Friendly** - Intuitive commands and GUIs
✅ **Active Development** - Regular updates and improvements

---

**Made with ❤️ by KaizenCore**
