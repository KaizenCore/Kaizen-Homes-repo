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

public class PublicHomesGUI implements Listener {

    private final KaizenHomes plugin;
    private final HomeManager homeManager;
    private final Player player;
    private Inventory inventory;
    private Component inventoryTitle;

    public PublicHomesGUI(KaizenHomes plugin, HomeManager homeManager, Player player) {
        this.plugin = plugin;
        this.homeManager = homeManager;
        this.player = player;

        // Register listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void open() {
        String title = "Public Homes";
        int size = 54;

        inventoryTitle = Component.text(title);
        inventory = Bukkit.createInventory(null, size, inventoryTitle);

        // Load public homes and populate GUI
        homeManager.getPublicHomes().thenAccept(homes -> {
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

        // Fill empty slots
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

    private ItemStack createHomeItem(Home home) {
        ItemStack item = new ItemStack(home.getIcon());
        ItemMeta meta = item.getItemMeta();

        // Get owner name
        OfflinePlayer owner = Bukkit.getOfflinePlayer(home.getOwner());
        String ownerName = owner.getName() != null ? owner.getName() : "Unknown";

        // Display name with gradient
        Component name = GradientUtil.gradient(home.getName(), "#00FFFF", "#0088FF", true)
                .append(Component.text(" - " + ownerName).color(NamedTextColor.GRAY));

        meta.displayName(name);

        // Lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(""));
        lore.add(Component.text("Owner: " + ownerName).color(NamedTextColor.YELLOW));

        if (!home.getDescription().isEmpty()) {
            lore.add(Component.text(home.getDescription()).color(NamedTextColor.GRAY));
        }

        lore.add(Component.text(""));
        lore.add(Component.text("Location: " + home.getFormattedLocation()).color(NamedTextColor.AQUA));
        lore.add(Component.text("Category: " + home.getCategory()).color(NamedTextColor.GREEN));
        lore.add(Component.text(""));
        lore.add(Component.text("Click to visit").color(NamedTextColor.GREEN));

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

        // Get home name and owner from item
        ItemMeta meta = clicked.getItemMeta();
        if (meta == null || meta.displayName() == null) return;

        // Extract plain text from gradient component
        String fullName = PlainTextComponentSerializer.plainText().serialize(meta.displayName());
        String[] parts = fullName.split(" - ");
        if (parts.length < 2) return;

        String homeName = parts[0].trim();
        String ownerName = parts[1].trim();

        OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerName);

        homeManager.getHome(owner.getUniqueId(), homeName).thenAccept(home -> {
            if (home == null) return;

            Bukkit.getScheduler().runTask(plugin, () -> {
                unregister();
                player.closeInventory();
                plugin.getTeleportManager().teleportToHome(player, home);
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
}
