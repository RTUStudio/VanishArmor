package kr.rtustudio.varmor.listener;

import kr.rtustudio.framework.bukkit.api.listener.RSListener;
import kr.rtustudio.framework.bukkit.api.scheduler.CraftScheduler;
import kr.rtustudio.varmor.VanishArmor;
import kr.rtustudio.varmor.manager.ToggleManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

@SuppressWarnings("unused")
public class PlayerJoinQuit extends RSListener<VanishArmor> {

    private final ToggleManager manager;

    public PlayerJoinQuit(VanishArmor plugin) {
        super(plugin);
        this.manager = plugin.getToggleManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        manager.addPlayer(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPostJoin(PlayerSpawnLocationEvent e) {
        CraftScheduler.delay(getPlugin(), e.getPlayer()::updateInventory, 1, true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        manager.removePlayer(e.getPlayer().getUniqueId());
    }

}
