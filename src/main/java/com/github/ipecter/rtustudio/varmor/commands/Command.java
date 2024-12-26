package com.github.ipecter.rtustudio.varmor.commands;

import com.github.ipecter.rtustudio.varmor.manager.ToggleManager;
import kr.rtuserver.framework.bukkit.api.command.RSCommand;
import kr.rtuserver.framework.bukkit.api.command.RSCommandData;
import com.github.ipecter.rtustudio.varmor.VanishArmor;
import com.github.ipecter.rtustudio.varmor.config.VanishConfig;
import kr.rtuserver.framework.bukkit.api.utility.player.PlayerChat;
import kr.rtuserver.framework.bukkit.api.utility.scheduler.CraftScheduler;
import org.bukkit.entity.Player;

public class Command extends RSCommand<VanishArmor> {

    private final VanishConfig config;
    private final ToggleManager manager;

    public Command(VanishArmor plugin) {
        super(plugin, "varmor", true);
        this.config = plugin.getVanishConfig();
        this.manager = plugin.getToggleManager();
    }

    @Override
    public boolean execute(RSCommandData data) {
        if (!data.length(0)) return false;
        PlayerChat chat = PlayerChat.of(getPlugin());
        if (getSender() instanceof Player player) {
            boolean isVanished = manager.getMap().getOrDefault(player.getUniqueId(), false);
            if (isVanished) {
                manager.off(player.getUniqueId());
                chat.announce(getSender(), getMessage().get(getSender(), "disable"));
            } else {
                manager.on(player.getUniqueId());
                chat.announce(getSender(), getMessage().get(getSender(), "enable"));
            }
            CraftScheduler.runLaterAsync(getPlugin(), player::updateInventory, 1);
            return true;
        } else chat.announce(getSender(), getCommon().getMessage(getSender(), "onlyPlayer"));
        return false;
    }

    @Override
    public void reload(RSCommandData data) {
        config.reload();
    }

}
