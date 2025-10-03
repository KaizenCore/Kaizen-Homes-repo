package kaizen.core.kaizenHomes.commands;

import kaizen.core.kaizenHomes.KaizenHomes;
import kaizen.core.kaizenHomes.gui.PublicHomesGUI;
import kaizen.core.kaizenHomes.managers.HomeManager;
import kaizen.core.kaizenHomes.utils.MessageUtil;
import kaizen.core.kaizenHomes.utils.PermissionUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PublicHomesCommand implements CommandExecutor {

    private final KaizenHomes plugin;
    private final HomeManager homeManager;

    public PublicHomesCommand(KaizenHomes plugin, HomeManager homeManager) {
        this.plugin = plugin;
        this.homeManager = homeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (!plugin.getConfigManager().isPublicHomesEnabled()) {
            MessageUtil.sendError(player, "Public homes are disabled!");
            return true;
        }

        if (!PermissionUtil.hasPermission(player, PermissionUtil.PERMISSION_PUBLIC)) {
            MessageUtil.sendNoPermission(player);
            return true;
        }

        // Open the public homes GUI
        PublicHomesGUI gui = new PublicHomesGUI(plugin, homeManager, player);
        gui.open();

        return true;
    }
}
