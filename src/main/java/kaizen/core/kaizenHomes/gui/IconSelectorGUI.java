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

public class IconSelectorGUI implements Listener {

    private final KaizenHomes plugin;
    private final HomeManager homeManager;
    private final Player player;
    private final Home home;
    private Inventory inventory;
    private Component inventoryTitle;

    // Available icon materials
    private static final Material[] ICON_MATERIALS = {
            // Buildings & Structures
            Material.RED_BED, Material.WHITE_BED, Material.ORANGE_BED, Material.YELLOW_BED,
            Material.OAK_DOOR, Material.BIRCH_DOOR, Material.SPRUCE_DOOR, Material.DARK_OAK_DOOR,
            Material.CAMPFIRE, Material.LANTERN, Material.TORCH, Material.SOUL_LANTERN,

            // Nature
            Material.OAK_SAPLING, Material.BIRCH_SAPLING, Material.SPRUCE_SAPLING, Material.JUNGLE_SAPLING,
            Material.CHERRY_SAPLING, Material.DARK_OAK_SAPLING, Material.ACACIA_SAPLING,
            Material.GRASS_BLOCK, Material.FLOWER_POT, Material.ROSE_BUSH, Material.SUNFLOWER,

            // Tools & Items
            Material.DIAMOND_SWORD, Material.IRON_PICKAXE, Material.GOLDEN_AXE, Material.SHIELD,
            Material.COMPASS, Material.CLOCK, Material.ENDER_PEARL, Material.NETHER_STAR,

            // Blocks
            Material.DIAMOND_BLOCK, Material.GOLD_BLOCK, Material.IRON_BLOCK, Material.EMERALD_BLOCK,
            Material.BEACON, Material.ENCHANTING_TABLE, Material.ANVIL, Material.CRAFTING_TABLE,

            // Food & Nature
            Material.APPLE, Material.GOLDEN_APPLE, Material.BREAD, Material.CAKE,
            Material.HONEY_BLOCK, Material.HONEYCOMB, Material.WHEAT, Material.CARROT,

            // Special
            Material.DRAGON_HEAD, Material.CREEPER_HEAD, Material.PLAYER_HEAD, Material.SKELETON_SKULL,
            Material.TOTEM_OF_UNDYING, Material.ELYTRA, Material.TRIDENT, Material.BOW,

            // Minerals & Gems
            Material.DIAMOND, Material.EMERALD, Material.GOLD_INGOT, Material.IRON_INGOT,
            Material.NETHERITE_INGOT, Material.AMETHYST_SHARD, Material.PRISMARINE_SHARD,

            // Misc
            Material.BELL, Material.BOOK, Material.WRITABLE_BOOK, Material.ENCHANTED_BOOK,
            Material.MAP, Material.PAINTING, Material.MUSIC_DISC_CAT, Material.JUKEBOX
    };

    public IconSelectorGUI(KaizenHomes plugin, HomeManager homeManager, Player player, Home home) {
        this.plugin = plugin;
        this.homeManager = homeManager;
        this.player = player;
        this.home = home;

        // Register listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void open() {
        inventoryTitle = GradientUtil.gradient("Select Home Icon", "#FFD700", "#FFA500", true);
        inventory = Bukkit.createInventory(null, 54, inventoryTitle);

        populateInventory();
        player.openInventory(inventory);
    }

    private void populateInventory() {
        inventory.clear();

        // Add all icon options
        int slot = 0;
        for (Material material : ICON_MATERIALS) {
            if (slot >= 45) break; // Leave space for bottom row

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();

            String materialName = formatMaterialName(material);
            meta.displayName(Component.text(materialName).color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true));

            List<Component> lore = new ArrayList<>();
            lore.add(Component.empty());

            if (material == home.getIcon()) {
                lore.add(Component.text("✓ Currently selected").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, true));
            } else {
                lore.add(Component.text("Click to select").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true));
            }

            meta.lore(lore);
            item.setItemMeta(meta);

            inventory.setItem(slot, item);
            slot++;
        }

        // Back button
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.displayName(Component.text("Back").color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true));
        backButton.setItemMeta(backMeta);
        inventory.setItem(49, backButton);

        // Fill empty slots in bottom row
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
            if (slot == 49) {
                // Back button - return to home manage GUI
                unregister();
                HomeManageGUI manageGUI = new HomeManageGUI(plugin, homeManager, player, home);
                manageGUI.open();
                return;
            }

            if (slot >= 45) return; // Ignore bottom row clicks

            // Get the clicked material
            Material selectedMaterial = clicked.getType();

            // Update home icon
            homeManager.updateHomeIcon(home, selectedMaterial).thenAccept(success -> {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (success) {
                        MessageUtil.sendSuccess(player, "Changed home icon to " + formatMaterialName(selectedMaterial));
                        unregister();
                        player.closeInventory();

                        // Reopen manage GUI after short delay
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            HomeManageGUI manageGUI = new HomeManageGUI(plugin, homeManager, player, home);
                            manageGUI.open();
                        }, 5L);
                    } else {
                        MessageUtil.sendError(player, "Failed to update home icon!");
                    }
                });
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
