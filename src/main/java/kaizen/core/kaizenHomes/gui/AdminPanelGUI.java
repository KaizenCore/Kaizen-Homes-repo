package kaizen.core.kaizenHomes.gui;

import kaizen.core.kaizenHomes.KaizenHomes;
import kaizen.core.kaizenHomes.config.ConfigManager;
import kaizen.core.kaizenHomes.utils.GradientUtil;
import kaizen.core.kaizenHomes.utils.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdminPanelGUI implements Listener {

    private final KaizenHomes plugin;
    private final ConfigManager configManager;
    private final Player player;
    private Inventory inventory;
    private Component inventoryTitle;

    // Track players waiting for chat input
    private static final Map<UUID, String> waitingForInput = new HashMap<>();

    public AdminPanelGUI(KaizenHomes plugin, Player player) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.player = player;

        // Register listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void open() {
        inventoryTitle = GradientUtil.Presets.kaizen("KHomes Admin Panel");
        inventory = Bukkit.createInventory(null, 54, inventoryTitle);

        populateInventory();
        player.openInventory(inventory);
    }

    private void populateInventory() {
        // Clear inventory
        inventory.clear();

        // === GENERAL SETTINGS (Row 1) ===
        inventory.setItem(10, createEditableItem(
                Material.BOOK,
                "Default Home Limit",
                NamedTextColor.GOLD,
                "Current: " + configManager.getDefaultHomeLimit(),
                "",
                "Click to change value"
        ));

        inventory.setItem(11, createToggleItem(
                Material.SKELETON_SKULL,
                "Death Home",
                configManager.isDeathHomeEnabled(),
                "Auto-saves death location"
        ));

        inventory.setItem(12, createToggleItem(
                Material.RED_BED,
                "Bed Sync",
                configManager.isBedSyncEnabled(),
                "Sync home with bed location"
        ));

        // === TELEPORT SETTINGS (Row 2) ===
        inventory.setItem(19, createToggleItem(
                Material.SHIELD,
                "Safety Check",
                configManager.isSafetyCheckEnabled(),
                "Check for unsafe locations"
        ));

        inventory.setItem(20, createEditableItem(
                Material.CLOCK,
                "Warmup Timer",
                NamedTextColor.AQUA,
                "Current: " + configManager.getTeleportWarmup() + " seconds",
                "",
                "Click to change value"
        ));

        inventory.setItem(21, createEditableItem(
                Material.HOPPER,
                "Cooldown Timer",
                NamedTextColor.AQUA,
                "Current: " + configManager.getTeleportCooldown() + " seconds",
                "",
                "Click to change value"
        ));

        inventory.setItem(22, createToggleItem(
                Material.FEATHER,
                "Cancel on Move",
                configManager.isCancelOnMove(),
                "Cancel teleport if player moves"
        ));

        // === SHARING SETTINGS (Row 3) ===
        inventory.setItem(28, createToggleItem(
                Material.PLAYER_HEAD,
                "Sharing System",
                configManager.isSharingEnabled(),
                "Allow home sharing"
        ));

        inventory.setItem(29, createEditableItem(
                Material.PAPER,
                "Max Shared Players",
                NamedTextColor.GREEN,
                "Current: " + configManager.getMaxSharedPlayers(),
                "",
                "Click to change value"
        ));

        inventory.setItem(30, createToggleItem(
                Material.ENDER_EYE,
                "Public Homes",
                configManager.isPublicHomesEnabled(),
                "Allow public homes"
        ));

        // === EFFECTS SETTINGS (Row 4) ===
        inventory.setItem(37, createToggleItem(
                Material.REDSTONE,
                "Particles",
                configManager.isParticlesEnabled(),
                "Teleport particles: " + configManager.getParticleType()
        ));

        inventory.setItem(38, createToggleItem(
                Material.NOTE_BLOCK,
                "Sounds",
                configManager.isSoundsEnabled(),
                "Teleport sound effects"
        ));

        inventory.setItem(39, createToggleItem(
                Material.OAK_SIGN,
                "Titles",
                configManager.isTitlesEnabled(),
                "Teleport title messages"
        ));

        // === ECONOMY SETTINGS (Row 4 continued) ===
        inventory.setItem(41, createToggleItem(
                Material.GOLD_INGOT,
                "Economy",
                configManager.isEconomyEnabled(),
                "Vault integration",
                "Set home cost: $" + configManager.getSetHomeCost(),
                "Teleport cost: $" + configManager.getTeleportCost()
        ));

        // === MANAGEMENT BUTTONS (Bottom row) ===
        inventory.setItem(48, createButton(
                Material.COMPASS,
                "View All Homes",
                NamedTextColor.AQUA,
                "Browse all player homes"
        ));

        inventory.setItem(49, createButton(
                Material.WRITABLE_BOOK,
                "Reload Config",
                NamedTextColor.YELLOW,
                "Reload configuration file"
        ));

        inventory.setItem(50, createButton(
                Material.BARRIER,
                "Close",
                NamedTextColor.RED,
                "Close admin panel"
        ));

        // Fill empty slots
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.displayName(Component.text(" "));
        filler.setItemMeta(fillerMeta);

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }

    private ItemStack createEditableItem(Material material, String name, NamedTextColor color, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(GradientUtil.gradient(name, "#FFD700", "#FFA500", true));

        List<Component> loreList = new ArrayList<>();
        loreList.add(Component.empty());
        for (String line : lore) {
            loreList.add(Component.text(line).color(NamedTextColor.GRAY));
        }
        loreList.add(Component.empty());
        loreList.add(Component.text("✎ Click to edit").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, true));
        meta.lore(loreList);

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createToggleItem(Material material, String name, boolean enabled, String... description) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(GradientUtil.gradient(name, "#FFD700", "#FFA500", true));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());

        // Status
        if (enabled) {
            lore.add(Component.text("Status: ").color(NamedTextColor.GRAY)
                    .append(Component.text("✓ ENABLED").color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true)));
        } else {
            lore.add(Component.text("Status: ").color(NamedTextColor.GRAY)
                    .append(Component.text("✗ DISABLED").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, true)));
        }

        lore.add(Component.empty());

        // Description
        for (String desc : description) {
            lore.add(Component.text(desc).color(NamedTextColor.GRAY));
        }

        lore.add(Component.empty());
        lore.add(Component.text("⇄ Click to toggle").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, true));

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createButton(Material material, String name, NamedTextColor color, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(GradientUtil.gradient(name, "#FFD700", "#FFA500", true));

        List<Component> loreList = new ArrayList<>();
        loreList.add(Component.empty());
        for (String line : lore) {
            loreList.add(Component.text(line).color(NamedTextColor.GRAY));
        }
        meta.lore(loreList);

        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().equals(inventory)) return;
        if (!event.getView().title().equals(inventoryTitle)) return;
        if (!(event.getWhoClicked() instanceof Player)) return;

        event.setCancelled(true);

        Player clicker = (Player) event.getWhoClicked();
        if (!clicker.equals(player)) return;

        int slot = event.getSlot();

        Bukkit.getScheduler().runTask(plugin, () -> {
            switch (slot) {
                // === GENERAL SETTINGS ===
                case 10: // Default Home Limit
                    promptForNumber(player, "home-limit", "Enter new default home limit (1-100):");
                    break;

                case 11: // Death Home Toggle
                    configManager.setDeathHomeEnabled(!configManager.isDeathHomeEnabled());
                    MessageUtil.sendSuccess(player, "Death Home " + (configManager.isDeathHomeEnabled() ? "enabled" : "disabled"));
                    refreshGUI();
                    break;

                case 12: // Bed Sync Toggle
                    configManager.setBedSyncEnabled(!configManager.isBedSyncEnabled());
                    MessageUtil.sendSuccess(player, "Bed Sync " + (configManager.isBedSyncEnabled() ? "enabled" : "disabled"));
                    refreshGUI();
                    break;

                // === TELEPORT SETTINGS ===
                case 19: // Safety Check Toggle
                    configManager.setSafetyCheckEnabled(!configManager.isSafetyCheckEnabled());
                    MessageUtil.sendSuccess(player, "Safety Check " + (configManager.isSafetyCheckEnabled() ? "enabled" : "disabled"));
                    refreshGUI();
                    break;

                case 20: // Warmup Timer
                    promptForNumber(player, "warmup", "Enter warmup seconds (0-30):");
                    break;

                case 21: // Cooldown Timer
                    promptForNumber(player, "cooldown", "Enter cooldown seconds (0-300):");
                    break;

                case 22: // Cancel on Move Toggle
                    configManager.setCancelOnMove(!configManager.isCancelOnMove());
                    MessageUtil.sendSuccess(player, "Cancel on Move " + (configManager.isCancelOnMove() ? "enabled" : "disabled"));
                    refreshGUI();
                    break;

                // === SHARING SETTINGS ===
                case 28: // Sharing System Toggle
                    configManager.setSharingEnabled(!configManager.isSharingEnabled());
                    MessageUtil.sendSuccess(player, "Sharing System " + (configManager.isSharingEnabled() ? "enabled" : "disabled"));
                    refreshGUI();
                    break;

                case 29: // Max Shared Players
                    promptForNumber(player, "max-shared", "Enter max shared players (1-50):");
                    break;

                case 30: // Public Homes Toggle
                    configManager.setPublicHomesEnabled(!configManager.isPublicHomesEnabled());
                    MessageUtil.sendSuccess(player, "Public Homes " + (configManager.isPublicHomesEnabled() ? "enabled" : "disabled"));
                    refreshGUI();
                    break;

                // === EFFECTS SETTINGS ===
                case 37: // Particles Toggle
                    configManager.setParticlesEnabled(!configManager.isParticlesEnabled());
                    MessageUtil.sendSuccess(player, "Particles " + (configManager.isParticlesEnabled() ? "enabled" : "disabled"));
                    refreshGUI();
                    break;

                case 38: // Sounds Toggle
                    configManager.setSoundsEnabled(!configManager.isSoundsEnabled());
                    MessageUtil.sendSuccess(player, "Sounds " + (configManager.isSoundsEnabled() ? "enabled" : "disabled"));
                    refreshGUI();
                    break;

                case 39: // Titles Toggle
                    configManager.setTitlesEnabled(!configManager.isTitlesEnabled());
                    MessageUtil.sendSuccess(player, "Titles " + (configManager.isTitlesEnabled() ? "enabled" : "disabled"));
                    refreshGUI();
                    break;

                // === ECONOMY SETTINGS ===
                case 41: // Economy Toggle
                    configManager.setEconomyEnabled(!configManager.isEconomyEnabled());
                    MessageUtil.sendSuccess(player, "Economy " + (configManager.isEconomyEnabled() ? "enabled" : "disabled"));
                    refreshGUI();
                    break;

                // === MANAGEMENT BUTTONS ===
                case 48: // View All Homes
                    unregister();
                    AllHomesGUI allHomesGUI = new AllHomesGUI(plugin, plugin.getHomeManager(), player);
                    allHomesGUI.open();
                    break;

                case 49: // Reload Config
                    configManager.reloadConfig();
                    MessageUtil.sendSuccess(player, "Configuration reloaded!");
                    refreshGUI();
                    break;

                case 50: // Close
                    unregister();
                    player.closeInventory();
                    break;
            }
        });
    }

    private void promptForNumber(Player player, String setting, String prompt) {
        unregister();
        player.closeInventory();
        player.sendMessage(GradientUtil.Presets.kaizen("Admin")
                .append(Component.text(" » " + prompt).color(NamedTextColor.GRAY)));
        player.sendMessage(Component.text("Type 'cancel' to cancel.").color(NamedTextColor.DARK_GRAY));
        waitingForInput.put(player.getUniqueId(), setting);
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        if (!waitingForInput.containsKey(player.getUniqueId())) return;

        event.setCancelled(true);
        String setting = waitingForInput.remove(player.getUniqueId());
        String input = PlainTextComponentSerializer.plainText().serialize(event.message()).trim();

        if (input.equalsIgnoreCase("cancel")) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                MessageUtil.sendInfo(player, "Cancelled.");
                new AdminPanelGUI(plugin, player).open();
            });
            return;
        }

        try {
            int value = Integer.parseInt(input);

            Bukkit.getScheduler().runTask(plugin, () -> {
                switch (setting) {
                    case "home-limit":
                        if (value < 1 || value > 100) {
                            MessageUtil.sendError(player, "Value must be between 1 and 100!");
                            new AdminPanelGUI(plugin, player).open();
                            return;
                        }
                        configManager.setDefaultHomeLimit(value);
                        MessageUtil.sendSuccess(player, "Default home limit set to " + value);
                        break;

                    case "warmup":
                        if (value < 0 || value > 30) {
                            MessageUtil.sendError(player, "Value must be between 0 and 30!");
                            new AdminPanelGUI(plugin, player).open();
                            return;
                        }
                        configManager.setTeleportWarmup(value);
                        MessageUtil.sendSuccess(player, "Warmup timer set to " + value + " seconds");
                        break;

                    case "cooldown":
                        if (value < 0 || value > 300) {
                            MessageUtil.sendError(player, "Value must be between 0 and 300!");
                            new AdminPanelGUI(plugin, player).open();
                            return;
                        }
                        configManager.setTeleportCooldown(value);
                        MessageUtil.sendSuccess(player, "Cooldown timer set to " + value + " seconds");
                        break;

                    case "max-shared":
                        if (value < 1 || value > 50) {
                            MessageUtil.sendError(player, "Value must be between 1 and 50!");
                            new AdminPanelGUI(plugin, player).open();
                            return;
                        }
                        configManager.setMaxSharedPlayers(value);
                        MessageUtil.sendSuccess(player, "Max shared players set to " + value);
                        break;
                }

                new AdminPanelGUI(plugin, player).open();
            });

        } catch (NumberFormatException e) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                MessageUtil.sendError(player, "Invalid number! Please enter a valid number.");
                new AdminPanelGUI(plugin, player).open();
            });
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getInventory().equals(inventory)) return;
        if (!(event.getPlayer() instanceof Player)) return;
        if (!event.getPlayer().equals(player)) return;

        // Don't unregister if waiting for chat input
        if (waitingForInput.containsKey(player.getUniqueId())) return;

        // Unregister listener when inventory closes
        Bukkit.getScheduler().runTaskLater(plugin, this::unregister, 1L);
    }

    private void unregister() {
        HandlerList.unregisterAll(this);
    }

    private void refreshGUI() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            populateInventory();
        }, 2L);
    }
}
