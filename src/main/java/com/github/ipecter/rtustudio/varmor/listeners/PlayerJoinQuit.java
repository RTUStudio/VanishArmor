package com.github.ipecter.rtustudio.varmor.listeners;

import kr.rtuserver.framework.bukkit.api.listener.RSListener;
import com.github.ipecter.rtustudio.varmor.VanishArmor;
import com.github.ipecter.rtustudio.varmor.manager.ToggleManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        manager.removePlayer(e.getPlayer().getUniqueId());
    }

}
