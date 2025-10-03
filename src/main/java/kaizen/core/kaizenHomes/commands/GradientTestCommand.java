package kaizen.core.kaizenHomes.commands;

import kaizen.core.kaizenHomes.utils.GradientUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GradientTestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        // Show gradient examples
        player.sendMessage(Component.text("═══════════════════════════════════════").color(NamedTextColor.DARK_GRAY));
        player.sendMessage(GradientUtil.Presets.kaizen("Kaizen Gradient Showcase"));
        player.sendMessage(Component.text("═══════════════════════════════════════").color(NamedTextColor.DARK_GRAY));
        player.sendMessage(Component.empty());

        player.sendMessage(Component.text("Preset Gradients:").color(NamedTextColor.WHITE));
        player.sendMessage(GradientUtil.Presets.kaizen("  • Kaizen (Pink to Dark Pink)"));
        player.sendMessage(GradientUtil.Presets.fire("  • Fire (Red to Orange to Yellow)"));
        player.sendMessage(GradientUtil.Presets.ocean("  • Ocean (Cyan to Blue)"));
        player.sendMessage(GradientUtil.Presets.nature("  • Nature (Lime to Dark Green)"));
        player.sendMessage(GradientUtil.Presets.sunset("  • Sunset (Red to Orange to Gold)"));
        player.sendMessage(GradientUtil.Presets.purple("  • Purple Gradient"));
        player.sendMessage(GradientUtil.Presets.gold("  • Gold (Gold to Orange)"));
        player.sendMessage(Component.empty());

        player.sendMessage(Component.text("Special Effects:").color(NamedTextColor.WHITE));
        player.sendMessage(GradientUtil.rainbow("  • Rainbow Gradient Text!"));
        player.sendMessage(GradientUtil.multiGradient("  • Multi-Color Gradient", "#FF0000", "#00FF00", "#0000FF"));
        player.sendMessage(Component.empty());

        player.sendMessage(Component.text("Custom Gradients:").color(NamedTextColor.WHITE));
        player.sendMessage(GradientUtil.gradient("  • Custom Pink to Blue", "#FF69B4", "#4169E1"));
        player.sendMessage(GradientUtil.gradient("  • Custom Green to Yellow", "#00FF00", "#FFFF00"));
        player.sendMessage(Component.empty());

        player.sendMessage(Component.text("═══════════════════════════════════════").color(NamedTextColor.DARK_GRAY));

        return true;
    }
}
