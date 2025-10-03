package kaizen.core.kaizenHomes.gui;

import kaizen.core.kaizenHomes.KaizenHomes;
import kaizen.core.kaizenHomes.managers.HomeManager;
import kaizen.core.kaizenHomes.models.Home;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class HomeManageGUI implements Listener {

    private final KaizenHomes plugin;
    private final HomeManager homeManager;
    private final Player player;
    private final Home home;
    private Inventory inventory;
    private Component inventoryTitle;

    public HomeManageGUI(KaizenHomes plugin, HomeManager homeManager, Player player, Home home) {
        this.plugin = plugin;
        this.homeManager = homeManager;
        this.player = player;
        this.home = home;

        // Register listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void open() {
        String title = "Manage: " + home.getName();
        inventoryTitle = Component.text(title);
        inventory = Bukkit.createInventory(null, 27, inventoryTitle);

        populateInventory();
        player.openInventory(inventory);
    }

    private void populateInventory() {
        // Teleport button
        inventory.setItem(10, createButton(
                Material.ENDER_PEARL,
                "Teleport to Home",
                NamedTextColor.GREEN,
                "Click to teleport to this home"
        ));

        // Update location button
        inventory.setItem(11, createButton(
                Material.COMPASS,
                "Update Location",
                NamedTextColor.YELLOW,
                "Set home location to your current position"
        ));

        // Set as default button
        inventory.setItem(12, createButton(
                Material.NETHER_STAR,
                "Set as Default",
                NamedTextColor.GOLD,
                "Make this your default home"
        ));

        // Privacy settings
        Material privacyIcon = home.getPrivacyMode() == Home.PrivacyMode.PUBLIC ? Material.LIME_DYE :
                (home.getPrivacyMode() == Home.PrivacyMode.SHARED ? Material.YELLOW_DYE : Material.RED_DYE);
        inventory.setItem(13, createButton(
                privacyIcon,
                "Privacy: " + home.getPrivacyMode(),
                NamedTextColor.AQUA,
                "Click to toggle privacy mode"
        ));

        // Share button
        inventory.setItem(14, createButton(
                Material.PLAYER_HEAD,
                "Manage Sharing",
                NamedTextColor.LIGHT_PURPLE,
                "Shared with " + home.getSharedWith().size() + " players",
                "Use /home share <home> <player> to share"
        ));

        // Change Icon button
        inventory.setItem(15, createButton(
                home.getIcon(),
                "Change Icon",
                NamedTextColor.YELLOW,
                "Current: " + formatMaterialName(home.getIcon()),
                "Click to select a new icon"
        ));

        // Delete button
        inventory.setItem(16, createButton(
                Material.BARRIER,
                "Delete Home",
                NamedTextColor.RED,
                "Permanently delete this home"
        ));

        // Back button
        inventory.setItem(22, createButton(
                Material.ARROW,
                "Back",
                NamedTextColor.GRAY,
                "Return to homes list"
        ));

        // Fill empty slots
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        meta.displayName(Component.text(" "));
        filler.setItemMeta(meta);

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }

    private ItemStack createButton(Material material, String name, NamedTextColor color, String... loreLines) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        // Use gradient for button names
        Component displayName;
        if (color == NamedTextColor.GREEN || color == NamedTextColor.GOLD || color == NamedTextColor.YELLOW) {
            displayName = GradientUtil.gradient(name, "#FFD700", "#FFA500", true);
        } else if (color == NamedTextColor.RED) {
            displayName = GradientUtil.gradient(name, "#FF4444", "#AA0000", true);
        } else if (color == NamedTextColor.AQUA) {
            displayName = GradientUtil.gradient(name, "#00FFFF", "#0088FF", true);
        } else {
            displayName = Component.text(name).color(color).decoration(TextDecoration.BOLD, true);
        }

        meta.displayName(displayName);

        if (loreLines.length > 0) {
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text(""));
            for (String line : loreLines) {
                lore.add(Component.text(line).color(NamedTextColor.GRAY));
            }
            meta.lore(lore);
        }

        item.setItemMeta(meta);
        return item;
    }

    private String formatMaterialName(Material material) {
        String name = material.name().replace("_", " ");
        String[] words = name.split(" ");
        StringBuilder formatted = new StringBuilder();

        for (String word : words) {
            if (formatted.length() > 0) {
                formatted.append(" ");
            }
            formatted.append(word.charAt(0)).append(word.substring(1).toLowerCase());
        }

        return formatted.toString();
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

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) return;

        int slot = event.getSlot();

        Bukkit.getScheduler().runTask(plugin, () -> {
            switch (slot) {
                case 10: // Teleport
                    unregister();
                    player.closeInventory();
                    plugin.getTeleportManager().teleportToHome(player, home);
                    break;

                case 11: // Update location
                    homeManager.updateHomeLocation(player.getUniqueId(), home.getName(), player.getLocation());
                    MessageUtil.sendSuccess(player, "Updated location for home '" + home.getName() + "'!");
                    unregister();
                    player.closeInventory();
                    break;

                case 12: // Set as default
                    homeManager.setDefaultHome(player.getUniqueId(), home.getName());
                    MessageUtil.sendSuccess(player, "Set '" + home.getName() + "' as your default home!");
                    unregister();
                    player.closeInventory();
                    break;

                case 13: // Toggle privacy
                    togglePrivacy();
                    break;

                case 14: // Manage sharing
                    MessageUtil.sendInfo(player, "Use /home share <home> <player> to share this home");
                    MessageUtil.sendInfo(player, "Use /home unshare <home> <player> to remove access");
                    unregister();
                    player.closeInventory();
                    break;

                case 15: // Change Icon
                    unregister();
                    IconSelectorGUI iconSelector = new IconSelectorGUI(plugin, homeManager, player, home);
                    iconSelector.open();
                    break;

                case 16: // Delete home
                    homeManager.deleteHome(player.getUniqueId(), home.getName());
                    MessageUtil.sendHomeDeleted(player, home.getName());
                    unregister();
                    player.closeInventory();
                    break;

                case 22: // Back
                    unregister();
                    HomeMenuGUI menuGUI = new HomeMenuGUI(plugin, homeManager, player);
                    menuGUI.open();
                    break;
            }
        });
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getInventory().equals(inventory)) return;
        if (!(event.getPlayer() instanceof Player)) return;
        if (!event.getPlayer().equals(player)) return;

        // Unregister listener when inventory closes
        Bukkit.getScheduler().runTaskLater(plugin, this::unregister, 1L);
    }

    private void unregister() {
        HandlerList.unregisterAll(this);
    }

    private void togglePrivacy() {
        Home.PrivacyMode currentMode = home.getPrivacyMode();
        Home.PrivacyMode newMode;

        switch (currentMode) {
            case PRIVATE:
                newMode = Home.PrivacyMode.SHARED;
                break;
            case SHARED:
                newMode = Home.PrivacyMode.PUBLIC;
                break;
            case PUBLIC:
            default:
                newMode = Home.PrivacyMode.PRIVATE;
                break;
        }

        homeManager.updateHomePrivacy(home, newMode);
        MessageUtil.sendSuccess(player, "Changed privacy to " + newMode);

        // Refresh GUI
        unregister();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.closeInventory();
            HomeManageGUI newGUI = new HomeManageGUI(plugin, homeManager, player, home);
            newGUI.open();
        }, 5L);
    }
}
