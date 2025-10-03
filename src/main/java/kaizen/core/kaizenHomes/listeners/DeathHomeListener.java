package kaizen.core.kaizenHomes.listeners;

import kaizen.core.kaizenHomes.KaizenHomes;
import kaizen.core.kaizenHomes.managers.HomeManager;
import kaizen.core.kaizenHomes.models.Home;
import kaizen.core.kaizenHomes.utils.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathHomeListener implements Listener {

    private final KaizenHomes plugin;
    private final HomeManager homeManager;
    private static final String DEATH_HOME_NAME = "death";

    public DeathHomeListener(KaizenHomes plugin, HomeManager homeManager) {
        this.plugin = plugin;
        this.homeManager = homeManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!plugin.getConfigManager().isDeathHomeEnabled()) {
            return;
        }

        Player player = event.getPlayer();

        homeManager.homeExists(player.getUniqueId(), DEATH_HOME_NAME).thenAccept(exists -> {
            if (exists && !plugin.getConfigManager().isDeathHomeOverwrite()) {
                // Death home exists and overwrite is disabled
                return;
            }

            if (exists) {
                // Update existing death home
                homeManager.updateHomeLocation(player.getUniqueId(), DEATH_HOME_NAME, player.getLocation())
                        .thenRun(() -> MessageUtil.sendInfo(player, "Death location saved! Use /home death to return."));
            } else {
                // Create new death home
                homeManager.createHome(player, DEATH_HOME_NAME, player.getLocation())
                        .thenAccept(success -> {
                            if (success) {
                                MessageUtil.sendInfo(player, "Death location saved! Use /home death to return.");
                            }
                        });
            }
        });
    }
}
