package kaizen.core.kaizenHomes.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtil {

    // Gradient prefix for Kaizen branding
    private static Component getPrefix() {
        return GradientUtil.Presets.kaizen("Khomes")
                .append(Component.text(" » ").color(NamedTextColor.DARK_GRAY));
    }

    public static void sendSuccess(CommandSender sender, String message) {
        Component component = getPrefix()
                .append(Component.text(message).color(NamedTextColor.WHITE));
        sender.sendMessage(component);
    }

    public static void sendError(CommandSender sender, String message) {
        Component component = getPrefix()
                .append(Component.text(message).color(NamedTextColor.RED));
        sender.sendMessage(component);
    }

    public static void sendInfo(CommandSender sender, String message) {
        Component component = getPrefix()
                .append(Component.text(message).color(NamedTextColor.YELLOW));
        sender.sendMessage(component);
    }

    public static void sendMessage(CommandSender sender, String message) {
        Component component = getPrefix()
                .append(Component.text(message).color(NamedTextColor.WHITE));
        sender.sendMessage(component);
    }

    public static void sendActionBar(Player player, String message) {
        Component component = GradientUtil.gradient(message, "#FFD700", "#FFA500");
        player.sendActionBar(component);
    }

    public static void sendTitle(Player player, String title, String subtitle) {
        Component titleComponent = GradientUtil.Presets.kaizen(title);
        Component subtitleComponent = Component.text(subtitle).color(NamedTextColor.YELLOW);

        player.showTitle(net.kyori.adventure.title.Title.title(titleComponent, subtitleComponent));
    }

    public static void sendGradientTitle(Player player, String title, String subtitle) {
        Component titleComponent = GradientUtil.Presets.kaizen(title);
        Component subtitleComponent = GradientUtil.gradient(subtitle, "#FFD700", "#FFA500");

        player.showTitle(net.kyori.adventure.title.Title.title(titleComponent, subtitleComponent));
    }

    public static Component format(String message, NamedTextColor color) {
        return Component.text(message).color(color);
    }

    public static Component formatHighlight(String text) {
        return Component.text(text).color(NamedTextColor.AQUA).decoration(TextDecoration.BOLD, true);
    }

    // Specific home-related messages with gradients
    public static void sendHomeSet(Player player, String homeName) {
        Component message = getPrefix()
                .append(Component.text("Home ").color(NamedTextColor.GRAY))
                .append(GradientUtil.gradient(homeName, "#AAAAAA", "#FFFFFF"))
                .append(Component.text(" has been set!").color(NamedTextColor.GRAY));
        player.sendMessage(message);
    }

    public static void sendHomeDeleted(Player player, String homeName) {
        Component message = getPrefix()
                .append(Component.text("Home ").color(NamedTextColor.GRAY))
                .append(GradientUtil.gradient(homeName, "#AAAAAA", "#FFFFFF"))
                .append(Component.text(" has been deleted!").color(NamedTextColor.GRAY));
        player.sendMessage(message);
    }

    public static void sendHomeTeleport(Player player, String homeName) {
        Component message = getPrefix()
                .append(Component.text("Teleporting to ").color(NamedTextColor.GRAY))
                .append(GradientUtil.gradient(homeName, "#00FFFF", "#0088FF"))
                .append(Component.text("...").color(NamedTextColor.GRAY));
        player.sendMessage(message);
    }

    public static void sendHomeNotFound(Player player, String homeName) {
        sendError(player, "Home '" + homeName + "' not found!");
    }

    public static void sendHomeAlreadyExists(Player player, String homeName) {
        sendError(player, "Home '" + homeName + "' already exists!");
    }

    public static void sendHomeLimitReached(Player player, int limit) {
        sendError(player, "You have reached your home limit of " + limit + "!");
    }

    public static void sendNoPermission(Player player) {
        sendError(player, "You don't have permission to do that!");
    }

    public static void sendCooldownActive(Player player, int seconds) {
        sendError(player, "You must wait " + seconds + " seconds before teleporting again!");
    }

    public static void sendWarmupStarted(Player player, int seconds) {
        sendInfo(player, "Teleporting in " + seconds + " seconds. Don't move!");
    }

    public static void sendWarmupCancelled(Player player) {
        sendError(player, "Teleportation cancelled because you moved!");
    }

    public static void sendUnsafeLocation(Player player) {
        sendError(player, "The home location is not safe! Please update it.");
    }

    public static void sendHomeShared(Player player, String homeName, String targetPlayer) {
        sendSuccess(player, "Shared home '" + homeName + "' with " + targetPlayer + "!");
    }

    public static void sendHomeUnshared(Player player, String homeName, String targetPlayer) {
        sendSuccess(player, "Removed " + targetPlayer + "'s access to home '" + homeName + "'!");
    }
}
