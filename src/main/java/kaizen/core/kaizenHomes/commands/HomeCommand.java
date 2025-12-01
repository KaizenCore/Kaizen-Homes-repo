package kaizen.core.kaizenHomes.commands;

import kaizen.core.kaizenHomes.KaizenHomes;
import kaizen.core.kaizenHomes.managers.HomeManager;
import kaizen.core.kaizenHomes.managers.TeleportManager;
import kaizen.core.kaizenHomes.models.Home;
import kaizen.core.kaizenHomes.utils.MessageUtil;
import kaizen.core.kaizenHomes.utils.PermissionUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class HomeCommand implements CommandExecutor, TabCompleter {

    private static final int HOME_NAME_MIN_LENGTH = 1;
    private static final int HOME_NAME_MAX_LENGTH = 32;
    private static final String HOME_NAME_PATTERN = "^[a-zA-Z0-9_-]+$";

    private final KaizenHomes plugin;
    private final HomeManager homeManager;
    private final TeleportManager teleportManager;

    public HomeCommand(KaizenHomes plugin, HomeManager homeManager, TeleportManager teleportManager) {
        this.plugin = plugin;
        this.homeManager = homeManager;
        this.teleportManager = teleportManager;
    }

    /**
     * Validate a home name for security and consistency
     * @return error message if invalid, null if valid
     */
    private String validateHomeName(String name) {
        if (name == null || name.length() < HOME_NAME_MIN_LENGTH) {
            return "Home name must be at least " + HOME_NAME_MIN_LENGTH + " character(s)!";
        }
        if (name.length() > HOME_NAME_MAX_LENGTH) {
            return "Home name must be at most " + HOME_NAME_MAX_LENGTH + " characters!";
        }
        if (!name.matches(HOME_NAME_PATTERN)) {
            return "Home name can only contain letters, numbers, dashes, and underscores!";
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        // Check base permission
        if (!PermissionUtil.hasPermission(player, PermissionUtil.PERMISSION_TELEPORT)) {
            MessageUtil.sendNoPermission(player);
            return true;
        }

        // Handle subcommands
        if (args.length == 0) {
            // Teleport to default home
            teleportToDefaultHome(player);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "set":
            case "create":
                handleSetHome(player, args);
                break;
            case "delete":
            case "del":
            case "remove":
                handleDeleteHome(player, args);
                break;
            case "list":
                handleListHomes(player);
                break;
            case "info":
                handleHomeInfo(player, args);
                break;
            case "rename":
                handleRenameHome(player, args);
                break;
            case "setdefault":
            case "default":
                handleSetDefault(player, args);
                break;
            case "share":
                handleShareHome(player, args);
                break;
            case "unshare":
                handleUnshareHome(player, args);
                break;
            case "public":
                handlePublicHome(player, args);
                break;
            case "private":
                handlePrivateHome(player, args);
                break;
            default:
                // Try to teleport to home with that name
                teleportToHome(player, args[0]);
                break;
        }

        return true;
    }

    private void teleportToDefaultHome(Player player) {
        homeManager.getDefaultHome(player.getUniqueId()).thenAccept(home -> {
            if (home == null) {
                MessageUtil.sendError(player, "You don't have any homes set!");
                return;
            }
            teleportManager.teleportToHome(player, home);
        }).exceptionally(ex -> {
            plugin.getLogger().warning("Error getting default home for " + player.getName() + ": " + ex.getMessage());
            MessageUtil.sendError(player, "An error occurred while teleporting!");
            return null;
        });
    }

    private void teleportToHome(Player player, String homeName) {
        homeManager.getHome(player.getUniqueId(), homeName).thenAccept(home -> {
            if (home == null) {
                MessageUtil.sendHomeNotFound(player, homeName);
                return;
            }
            teleportManager.teleportToHome(player, home);
        }).exceptionally(ex -> {
            plugin.getLogger().warning("Error getting home '" + homeName + "' for " + player.getName() + ": " + ex.getMessage());
            MessageUtil.sendError(player, "An error occurred while teleporting!");
            return null;
        });
    }

    private void handleSetHome(Player player, String[] args) {
        if (!PermissionUtil.hasPermission(player, PermissionUtil.PERMISSION_SET)) {
            MessageUtil.sendNoPermission(player);
            return;
        }

        if (args.length < 2) {
            MessageUtil.sendError(player, "Usage: /home set <name>");
            return;
        }

        String homeName = args[1].toLowerCase();

        // Validate home name
        String validationError = validateHomeName(homeName);
        if (validationError != null) {
            MessageUtil.sendError(player, validationError);
            return;
        }

        homeManager.createHome(player, homeName, player.getLocation()).thenAccept(success -> {
            if (success) {
                MessageUtil.sendHomeSet(player, homeName);
            } else {
                homeManager.homeExists(player.getUniqueId(), homeName).thenAccept(exists -> {
                    if (exists) {
                        MessageUtil.sendHomeAlreadyExists(player, homeName);
                    } else {
                        int limit = PermissionUtil.getHomeLimit(player, plugin.getConfigManager().getDefaultHomeLimit());
                        MessageUtil.sendHomeLimitReached(player, limit);
                    }
                });
            }
        }).exceptionally(ex -> {
            plugin.getLogger().warning("Error creating home '" + homeName + "' for " + player.getName() + ": " + ex.getMessage());
            MessageUtil.sendError(player, "An error occurred while creating your home!");
            return null;
        });
    }

    private void handleDeleteHome(Player player, String[] args) {
        if (!PermissionUtil.hasPermission(player, PermissionUtil.PERMISSION_DELETE)) {
            MessageUtil.sendNoPermission(player);
            return;
        }

        if (args.length < 2) {
            MessageUtil.sendError(player, "Usage: /home delete <name>");
            return;
        }

        String homeName = args[1].toLowerCase();

        homeManager.deleteHome(player.getUniqueId(), homeName).thenAccept(success -> {
            if (success) {
                MessageUtil.sendHomeDeleted(player, homeName);
            } else {
                MessageUtil.sendHomeNotFound(player, homeName);
            }
        }).exceptionally(ex -> {
            plugin.getLogger().warning("Error deleting home '" + homeName + "' for " + player.getName() + ": " + ex.getMessage());
            MessageUtil.sendError(player, "An error occurred while deleting your home!");
            return null;
        });
    }

    private void handleListHomes(Player player) {
        if (!PermissionUtil.hasPermission(player, PermissionUtil.PERMISSION_LIST)) {
            MessageUtil.sendNoPermission(player);
            return;
        }

        homeManager.getHomes(player.getUniqueId()).thenAccept(homes -> {
            if (homes.isEmpty()) {
                MessageUtil.sendInfo(player, "You don't have any homes set!");
                return;
            }

            MessageUtil.sendInfo(player, "Your homes (" + homes.size() + "):");
            for (Home home : homes) {
                String defaultTag = home.isDefault() ? " [DEFAULT]" : "";
                String privacy = " [" + home.getPrivacyMode() + "]";
                MessageUtil.sendMessage(player, "  - " + home.getName() + defaultTag + privacy + " @ " + home.getFormattedLocation());
            }
        }).exceptionally(ex -> {
            plugin.getLogger().warning("Error listing homes for " + player.getName() + ": " + ex.getMessage());
            MessageUtil.sendError(player, "An error occurred while listing your homes!");
            return null;
        });
    }

    private void handleHomeInfo(Player player, String[] args) {
        if (args.length < 2) {
            MessageUtil.sendError(player, "Usage: /home info <name>");
            return;
        }

        String homeName = args[1].toLowerCase();

        homeManager.getHome(player.getUniqueId(), homeName).thenAccept(home -> {
            if (home == null) {
                MessageUtil.sendHomeNotFound(player, homeName);
                return;
            }

            MessageUtil.sendInfo(player, "Home Info - " + home.getName());
            MessageUtil.sendMessage(player, "  Location: " + home.getFormattedLocation());
            MessageUtil.sendMessage(player, "  Privacy: " + home.getPrivacyMode());
            MessageUtil.sendMessage(player, "  Category: " + home.getCategory());
            if (!home.getDescription().isEmpty()) {
                MessageUtil.sendMessage(player, "  Description: " + home.getDescription());
            }
            if (!home.getSharedWith().isEmpty()) {
                MessageUtil.sendMessage(player, "  Shared with " + home.getSharedWith().size() + " players");
            }
        }).exceptionally(ex -> {
            plugin.getLogger().warning("Error getting home info for " + player.getName() + ": " + ex.getMessage());
            MessageUtil.sendError(player, "An error occurred while getting home info!");
            return null;
        });
    }

    private void handleRenameHome(Player player, String[] args) {
        if (args.length < 3) {
            MessageUtil.sendError(player, "Usage: /home rename <old name> <new name>");
            return;
        }

        String oldName = args[1].toLowerCase();
        String newName = args[2].toLowerCase();

        // Validate new home name
        String validationError = validateHomeName(newName);
        if (validationError != null) {
            MessageUtil.sendError(player, validationError);
            return;
        }

        homeManager.renameHome(player.getUniqueId(), oldName, newName).thenAccept(success -> {
            if (success) {
                MessageUtil.sendSuccess(player, "Home renamed from '" + oldName + "' to '" + newName + "'!");
            } else {
                MessageUtil.sendError(player, "Failed to rename home. Check that the old name exists and the new name is not already used.");
            }
        }).exceptionally(ex -> {
            plugin.getLogger().warning("Error renaming home for " + player.getName() + ": " + ex.getMessage());
            MessageUtil.sendError(player, "An error occurred while renaming your home!");
            return null;
        });
    }

    private void handleSetDefault(Player player, String[] args) {
        if (args.length < 2) {
            MessageUtil.sendError(player, "Usage: /home setdefault <name>");
            return;
        }

        String homeName = args[1].toLowerCase();

        homeManager.homeExists(player.getUniqueId(), homeName).thenAccept(exists -> {
            if (!exists) {
                MessageUtil.sendHomeNotFound(player, homeName);
                return;
            }

            homeManager.setDefaultHome(player.getUniqueId(), homeName);
            MessageUtil.sendSuccess(player, "Set '" + homeName + "' as your default home!");
        }).exceptionally(ex -> {
            plugin.getLogger().warning("Error setting default home for " + player.getName() + ": " + ex.getMessage());
            MessageUtil.sendError(player, "An error occurred while setting default home!");
            return null;
        });
    }

    private void handleShareHome(Player player, String[] args) {
        if (!PermissionUtil.hasPermission(player, PermissionUtil.PERMISSION_SHARE)) {
            MessageUtil.sendNoPermission(player);
            return;
        }

        if (args.length < 3) {
            MessageUtil.sendError(player, "Usage: /home share <home> <player>");
            return;
        }

        String homeName = args[1].toLowerCase();
        String targetName = args[2];

        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            MessageUtil.sendError(player, "Player '" + targetName + "' not found!");
            return;
        }

        homeManager.getHome(player.getUniqueId(), homeName).thenAccept(home -> {
            if (home == null) {
                MessageUtil.sendHomeNotFound(player, homeName);
                return;
            }

            homeManager.shareHome(home, target.getUniqueId()).thenAccept(success -> {
                if (success) {
                    MessageUtil.sendHomeShared(player, homeName, targetName);
                } else {
                    MessageUtil.sendError(player, "Failed to share home. Player may already have access or you've reached the sharing limit.");
                }
            }).exceptionally(ex -> {
                plugin.getLogger().warning("Error sharing home for " + player.getName() + ": " + ex.getMessage());
                MessageUtil.sendError(player, "An error occurred while sharing your home!");
                return null;
            });
        }).exceptionally(ex -> {
            plugin.getLogger().warning("Error getting home for sharing: " + ex.getMessage());
            MessageUtil.sendError(player, "An error occurred while sharing your home!");
            return null;
        });
    }

    private void handleUnshareHome(Player player, String[] args) {
        if (!PermissionUtil.hasPermission(player, PermissionUtil.PERMISSION_SHARE)) {
            MessageUtil.sendNoPermission(player);
            return;
        }

        if (args.length < 3) {
            MessageUtil.sendError(player, "Usage: /home unshare <home> <player>");
            return;
        }

        String homeName = args[1].toLowerCase();
        String targetName = args[2];

        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        homeManager.getHome(player.getUniqueId(), homeName).thenAccept(home -> {
            if (home == null) {
                MessageUtil.sendHomeNotFound(player, homeName);
                return;
            }

            homeManager.unshareHome(home, target.getUniqueId()).thenAccept(success -> {
                if (success) {
                    MessageUtil.sendHomeUnshared(player, homeName, targetName);
                } else {
                    MessageUtil.sendError(player, "Player doesn't have access to this home!");
                }
            }).exceptionally(ex -> {
                plugin.getLogger().warning("Error unsharing home for " + player.getName() + ": " + ex.getMessage());
                MessageUtil.sendError(player, "An error occurred while unsharing your home!");
                return null;
            });
        }).exceptionally(ex -> {
            plugin.getLogger().warning("Error getting home for unsharing: " + ex.getMessage());
            MessageUtil.sendError(player, "An error occurred while unsharing your home!");
            return null;
        });
    }

    private void handlePublicHome(Player player, String[] args) {
        if (!PermissionUtil.hasPermission(player, PermissionUtil.PERMISSION_PUBLIC)) {
            MessageUtil.sendNoPermission(player);
            return;
        }

        if (args.length < 2) {
            MessageUtil.sendError(player, "Usage: /home public <name>");
            return;
        }

        String homeName = args[1].toLowerCase();

        homeManager.getHome(player.getUniqueId(), homeName).thenAccept(home -> {
            if (home == null) {
                MessageUtil.sendHomeNotFound(player, homeName);
                return;
            }

            homeManager.updateHomePrivacy(home, Home.PrivacyMode.PUBLIC).exceptionally(ex -> {
                plugin.getLogger().warning("Error setting home public for " + player.getName() + ": " + ex.getMessage());
                MessageUtil.sendError(player, "An error occurred while updating home privacy!");
                return null;
            });
            MessageUtil.sendSuccess(player, "Home '" + homeName + "' is now public!");
        }).exceptionally(ex -> {
            plugin.getLogger().warning("Error getting home for privacy update: " + ex.getMessage());
            MessageUtil.sendError(player, "An error occurred while updating home privacy!");
            return null;
        });
    }

    private void handlePrivateHome(Player player, String[] args) {
        if (args.length < 2) {
            MessageUtil.sendError(player, "Usage: /home private <name>");
            return;
        }

        String homeName = args[1].toLowerCase();

        homeManager.getHome(player.getUniqueId(), homeName).thenAccept(home -> {
            if (home == null) {
                MessageUtil.sendHomeNotFound(player, homeName);
                return;
            }

            homeManager.updateHomePrivacy(home, Home.PrivacyMode.PRIVATE).exceptionally(ex -> {
                plugin.getLogger().warning("Error setting home private for " + player.getName() + ": " + ex.getMessage());
                MessageUtil.sendError(player, "An error occurred while updating home privacy!");
                return null;
            });
            MessageUtil.sendSuccess(player, "Home '" + homeName + "' is now private!");
        }).exceptionally(ex -> {
            plugin.getLogger().warning("Error getting home for privacy update: " + ex.getMessage());
            MessageUtil.sendError(player, "An error occurred while updating home privacy!");
            return null;
        });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }

        Player player = (Player) sender;
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Subcommands
            completions.addAll(Arrays.asList("set", "delete", "list", "info", "rename", "setdefault", "share", "unshare", "public", "private"));

            // Add player's home names from cache (non-blocking)
            List<Home> homes = homeManager.getHomesCached(player.getUniqueId());
            completions.addAll(homes.stream().map(Home::getName).collect(Collectors.toList()));

            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            String subcommand = args[0].toLowerCase();
            if (subcommand.equals("delete") || subcommand.equals("info") || subcommand.equals("rename") ||
                    subcommand.equals("setdefault") || subcommand.equals("share") || subcommand.equals("unshare") ||
                    subcommand.equals("public") || subcommand.equals("private")) {

                // Get homes from cache (non-blocking)
                List<Home> homes = homeManager.getHomesCached(player.getUniqueId());
                completions.addAll(homes.stream().map(Home::getName).collect(Collectors.toList()));
            }
        }

        if (args.length == 3) {
            String subcommand = args[0].toLowerCase();
            if (subcommand.equals("share") || subcommand.equals("unshare")) {
                // Complete with online player names
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }

        return completions;
    }
}
