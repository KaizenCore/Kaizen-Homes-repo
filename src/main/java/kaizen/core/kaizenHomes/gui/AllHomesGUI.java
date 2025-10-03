package kaizen.core.kaizenHomes.gui;

import kaizen.core.kaizenHomes.KaizenHomes;
import kaizen.core.kaizenHomes.managers.HomeManager;
import kaizen.core.kaizenHomes.models.Home;
import kaizen.core.kaizenHomes.utils.GradientUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
import java.util.UUID;

public class AllHomesGUI implements Listener {

    private final KaizenHomes plugin;
    private final HomeManager homeManager;
    private final Player player;
    private Inventory inventory;
    private Component inventoryTitle;
    private List<Home> allHomes;

    public AllHomesGUI(KaizenHomes plugin, HomeManager homeManager, Player player) {
        this.plugin = plugin;
        this.homeManager = homeManager;
        this.player = player;
        this.allHomes = new ArrayList<>();

        // Register listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void open() {
        inventoryTitle = GradientUtil.gradient("All Homes (Admin)", "#FF6B9D", "#C44569", true);
        inventory = Bukkit.createInventory(null, 54, inventoryTitle);

        // Load all homes from all players
        loadAllHomes();
    }

    private void loadAllHomes() {
        allHomes.clear();

        // Get all online and offline players
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                homeManager.getHomes(offlinePlayer.getUniqueId()).thenAccept(homes -> {
                    if (!homes.isEmpty()) {
                        allHomes.addAll(homes);
                    }
                }).join();
            }

            // Update GUI on main thread
            Bukkit.getScheduler().runTask(plugin, () -> {
                populateInventory();
                player.openInventory(inventory);
            });
        });
    }

    private void populateInventory() {
        inventory.clear();

        int slot = 0;
        for (Home home : allHomes) {
            if (slot >= 45) break; // Leave space for controls

            ItemStack item = createHomeItem(home);
            inventory.setItem(slot, item);
            slot++;
        }

        // Info button
        inventory.setItem(49, createInfoButton());

        // Back button
        inventory.setItem(53, createBackButton());

        // Fill empty slots
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.displayName(Component.text(" "));
        filler.setItemMeta(fillerMeta);

        for (int i = 45; i < 54; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }

    private ItemStack createHomeItem(Home home) {
        ItemStack item = new ItemStack(home.getIcon());
        ItemMeta meta = item.getItemMeta();

        // Get owner name
        OfflinePlayer owner = Bukkit.getOfflinePlayer(home.getOwner());
        String ownerName = owner.getName() != null ? owner.getName() : "Unknown";

        // Display name
        Component name = GradientUtil.gradient(home.getName(), "#00FFFF", "#0088FF", true)
                .append(Component.text(" - " + ownerName).color(NamedTextColor.GRAY));

        meta.displayName(name);

        // Lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text("Owner: " + ownerName).color(NamedTextColor.YELLOW));
        lore.add(Component.text("Location: " + home.getFormattedLocation()).color(NamedTextColor.AQUA));
        lore.add(Component.text("Privacy: " + home.getPrivacyMode()).color(NamedTextColor.GREEN));
        lore.add(Component.text("Category: " + home.getCategory()).color(NamedTextColor.GOLD));

        if (home.isDefault()) {
            lore.add(Component.text("Default Home: ✓").color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true));
        }

        if (!home.getSharedWith().isEmpty()) {
            lore.add(Component.text("Shared with: " + home.getSharedWith().size() + " players").color(NamedTextColor.LIGHT_PURPLE));
        }

        lore.add(Component.empty());
        lore.add(Component.text("Left Click").color(NamedTextColor.GREEN)
                .append(Component.text(" - Teleport").color(NamedTextColor.GRAY)));
        lore.add(Component.text("Right Click").color(NamedTextColor.RED)
                .append(Component.text(" - Delete (Admin)").color(NamedTextColor.GRAY)));

        meta.lore(lore);
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createInfoButton() {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(GradientUtil.gradient("Total Homes", "#FFD700", "#FFA500", true));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text("Total: " + allHomes.size() + " homes").color(NamedTextColor.AQUA));
        lore.add(Component.empty());
        lore.add(Component.text("This shows all homes").color(NamedTextColor.GRAY));
        lore.add(Component.text("from all players").color(NamedTextColor.GRAY));

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createBackButton() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("Back to Admin Panel").color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true));

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

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) return;

        int slot = event.getSlot();

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (slot == 53) {
                // Back button
                unregister();
                AdminPanelGUI adminPanel = new AdminPanelGUI(plugin, player);
                adminPanel.open();
                return;
            }

            if (slot >= 45) return; // Ignore bottom row except back button

            // Get home from slot
            if (slot < allHomes.size()) {
                Home home = allHomes.get(slot);

                if (event.isLeftClick()) {
                    // Teleport to home
                    unregister();
                    player.closeInventory();
                    plugin.getTeleportManager().teleportToHome(player, home);
                } else if (event.isRightClick()) {
                    // Delete home (admin action)
                    homeManager.deleteHome(home.getOwner(), home.getName()).thenAccept(success -> {
                        if (success) {
                            OfflinePlayer owner = Bukkit.getOfflinePlayer(home.getOwner());
                            String ownerName = owner.getName() != null ? owner.getName() : "Unknown";

                            Bukkit.getScheduler().runTask(plugin, () -> {
                                player.sendMessage(GradientUtil.Presets.kaizen("Admin")
                                        .append(Component.text(" » Deleted home '").color(NamedTextColor.GRAY))
                                        .append(Component.text(home.getName()).color(NamedTextColor.RED))
                                        .append(Component.text("' from " + ownerName).color(NamedTextColor.GRAY)));

                                // Refresh GUI
                                unregister();
                                player.closeInventory();
                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                    AllHomesGUI newGUI = new AllHomesGUI(plugin, homeManager, player);
                                    newGUI.open();
                                }, 5L);
                            });
                        }
                    });
                }
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
}
