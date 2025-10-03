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

public class HomeMenuGUI implements Listener {

    private final KaizenHomes plugin;
    private final HomeManager homeManager;
    private final Player player;
    private Inventory inventory;
    private Component inventoryTitle;

    public HomeMenuGUI(KaizenHomes plugin, HomeManager homeManager, Player player) {
        this.plugin = plugin;
        this.homeManager = homeManager;
        this.player = player;

        // Register listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void open() {
        String title = plugin.getConfigManager().getGUITitle();
        int size = plugin.getConfigManager().getGUISize();

        inventoryTitle = Component.text(title);
        inventory = Bukkit.createInventory(null, size, inventoryTitle);

        // Load homes and populate GUI
        homeManager.getHomes(player.getUniqueId()).thenAccept(homes -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                populateInventory(homes);
                player.openInventory(inventory);
            });
        });
    }

    private void populateInventory(List<Home> homes) {
        // Clear inventory
        inventory.clear();

        int slot = 0;
        for (Home home : homes) {
            if (slot >= inventory.getSize() - 9) break; // Leave space for controls

            ItemStack item = createHomeItem(home);
            inventory.setItem(slot, item);
            slot++;
        }

        // Fill empty slots if configured
        if (plugin.getConfigManager().isGUIFillEmpty()) {
            Material fillMaterial = Material.valueOf(plugin.getConfigManager().getGUIFillMaterial());
            ItemStack filler = new ItemStack(fillMaterial);
            ItemMeta meta = filler.getItemMeta();
            meta.displayName(Component.text(" "));
            filler.setItemMeta(meta);

            for (int i = 0; i < inventory.getSize(); i++) {
                if (inventory.getItem(i) == null) {
                    inventory.setItem(i, filler);
                }
            }
        }
    }

    private ItemStack createHomeItem(Home home) {
        ItemStack item = new ItemStack(home.getIcon());
        ItemMeta meta = item.getItemMeta();

        // Display name with gradient
        Component name = GradientUtil.gradient(home.getName(), "#FFD700", "#FFA500", true);

        if (home.isDefault()) {
            name = name.append(Component.text(" ★").color(NamedTextColor.YELLOW));
        }

        meta.displayName(name);

        // Lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(""));

        if (!home.getDescription().isEmpty()) {
            lore.add(Component.text(home.getDescription()).color(NamedTextColor.GRAY));
            lore.add(Component.text(""));
        }

        lore.add(Component.text("Location: " + home.getFormattedLocation()).color(NamedTextColor.AQUA));
        lore.add(Component.text("Category: " + home.getCategory()).color(NamedTextColor.YELLOW));
        lore.add(Component.text("Privacy: " + home.getPrivacyMode()).color(NamedTextColor.GREEN));

        if (!home.getSharedWith().isEmpty()) {
            lore.add(Component.text("Shared with: " + home.getSharedWith().size() + " players").color(NamedTextColor.LIGHT_PURPLE));
        }

        lore.add(Component.text(""));
        lore.add(Component.text("Left Click").color(NamedTextColor.GREEN)
                .append(Component.text(" - Teleport").color(NamedTextColor.GRAY)));
        lore.add(Component.text("Right Click").color(NamedTextColor.YELLOW)
                .append(Component.text(" - Manage").color(NamedTextColor.GRAY)));

        meta.lore(lore);
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

        // Get home name from item
        ItemMeta meta = clicked.getItemMeta();
        if (meta == null || meta.displayName() == null) return;

        String homeName = extractHomeName(meta.displayName());

        homeManager.getHome(player.getUniqueId(), homeName).thenAccept(home -> {
            if (home == null) return;

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (event.isLeftClick()) {
                    // Teleport
                    unregister();
                    player.closeInventory();
                    plugin.getTeleportManager().teleportToHome(player, home);
                } else if (event.isRightClick()) {
                    // Open manage GUI
                    unregister();
                    HomeManageGUI manageGUI = new HomeManageGUI(plugin, homeManager, player, home);
                    manageGUI.open();
                }
            });
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

    private String extractHomeName(Component displayName) {
        // Extract plain text from gradient component and remove the star if present
        String plainText = PlainTextComponentSerializer.plainText().serialize(displayName);
        return plainText.replace(" ★", "").trim();
    }
}
