package com.github.ipecter.rtustudio.varmor.listener;

import com.github.ipecter.rtustudio.varmor.VanishArmor;
import com.github.ipecter.rtustudio.varmor.manager.ToggleManager;
import kr.rtuserver.framework.bukkit.api.listener.RSListener;
import kr.rtuserver.framework.bukkit.api.scheduler.CraftScheduler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

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
        CraftScheduler.runLaterAsync(getPlugin(), e.getPlayer()::updateInventory, 1);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        manager.removePlayer(e.getPlayer().getUniqueId());
    }

}
