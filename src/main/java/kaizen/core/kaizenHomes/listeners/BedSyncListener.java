package kaizen.core.kaizenHomes.listeners;

import kaizen.core.kaizenHomes.KaizenHomes;
import kaizen.core.kaizenHomes.managers.HomeManager;
import kaizen.core.kaizenHomes.utils.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class BedSyncListener implements Listener {

    private final KaizenHomes plugin;
    private final HomeManager homeManager;

    public BedSyncListener(KaizenHomes plugin, HomeManager homeManager) {
        this.plugin = plugin;
        this.homeManager = homeManager;
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        if (!plugin.getConfigManager().isBedSyncEnabled()) {
            return;
        }

        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }

        Player player = event.getPlayer();
        String bedHomeName = plugin.getConfigManager().getBedSyncHomeName();

        homeManager.homeExists(player.getUniqueId(), bedHomeName).thenAccept(exists -> {
            if (exists) {
                // Update existing bed home
                homeManager.updateHomeLocation(player.getUniqueId(), bedHomeName, event.getBed().getLocation())
                        .thenRun(() -> MessageUtil.sendInfo(player, "Bed home updated!"));
            } else {
                // Create new bed home
                homeManager.createHome(player, bedHomeName, event.getBed().getLocation())
                        .thenAccept(success -> {
                            if (success) {
                                MessageUtil.sendInfo(player, "Bed home created!");
                            }
                        });
            }
        });
    }
}
