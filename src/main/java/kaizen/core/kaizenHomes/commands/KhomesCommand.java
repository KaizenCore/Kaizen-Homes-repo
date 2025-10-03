package kaizen.core.kaizenHomes.commands;

import kaizen.core.kaizenHomes.KaizenHomes;
import kaizen.core.kaizenHomes.gui.AdminPanelGUI;
import kaizen.core.kaizenHomes.utils.MessageUtil;
import kaizen.core.kaizenHomes.utils.PermissionUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KhomesCommand implements CommandExecutor, TabCompleter {

    private final KaizenHomes plugin;

    public KhomesCommand(KaizenHomes plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (!PermissionUtil.isAdmin(player)) {
            MessageUtil.sendNoPermission(player);
            return true;
        }

        // Check subcommand
        if (args.length == 0) {
            MessageUtil.sendError(player, "Usage: /khomes admin");
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "admin":
                // Open admin panel
                AdminPanelGUI adminPanel = new AdminPanelGUI(plugin, player);
                adminPanel.open();
                break;

            default:
                MessageUtil.sendError(player, "Unknown subcommand. Use: /khomes admin");
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender instanceof Player && PermissionUtil.isAdmin((Player) sender)) {
                completions.add("admin");
            }
        }

        return completions;
    }
}
