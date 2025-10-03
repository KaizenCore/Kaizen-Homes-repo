package kaizen.core.kaizenHomes.commands;

import kaizen.core.kaizenHomes.KaizenHomes;
import kaizen.core.kaizenHomes.gui.HomeMenuGUI;
import kaizen.core.kaizenHomes.managers.HomeManager;
import kaizen.core.kaizenHomes.utils.MessageUtil;
import kaizen.core.kaizenHomes.utils.PermissionUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomesCommand implements CommandExecutor {

    private final KaizenHomes plugin;
    private final HomeManager homeManager;

    public HomesCommand(KaizenHomes plugin, HomeManager homeManager) {
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

        if (!PermissionUtil.hasPermission(player, PermissionUtil.PERMISSION_LIST)) {
            MessageUtil.sendNoPermission(player);
            return true;
        }

        // Open the homes GUI
        HomeMenuGUI gui = new HomeMenuGUI(plugin, homeManager, player);
        gui.open();

        return true;
    }
}
