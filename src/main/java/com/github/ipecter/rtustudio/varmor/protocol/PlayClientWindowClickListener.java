package com.github.ipecter.rtustudio.varmor.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.github.ipecter.rtustudio.varmor.VanishArmor;
import com.github.ipecter.rtustudio.varmor.configuration.VanishConfig;
import com.github.ipecter.rtustudio.varmor.manager.ToggleManager;
import com.github.ipecter.rtustudio.varmor.protocol.wrapper.WrapperPlayClientWindowClick;
import kr.rtuserver.framework.bukkit.api.integration.RSPacketListener;
import kr.rtuserver.framework.bukkit.api.scheduler.CraftScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.List;

public class PlayClientWindowClickListener extends RSPacketListener<VanishArmor> {

    private final VanishConfig config;
    private final ToggleManager manager;

    public PlayClientWindowClickListener(VanishArmor plugin) {
        super(plugin, new AdapterParameteters()
                .listenerPriority(ListenerPriority.HIGHEST)
                .types(PacketType.Play.Client.WINDOW_CLICK)
                .optionAsync());
        this.config = plugin.getVanishConfig();
        this.manager = plugin.getToggleManager();
    }

    @Override
    public void receive(PacketEvent event) {
        Player player = event.getPlayer();
        if (!manager.get(player.getUniqueId())) return;
        if (!player.hasPermission(getPlugin().getName() + ".vanish")) return;
        if (!config.isHideSelf()) return;
        PacketContainer packet = event.getPacket();
        WrapperPlayClientWindowClick p = new WrapperPlayClientWindowClick(packet);
        if (player.getOpenInventory().getType() == InventoryType.CRAFTING) {
            List<Integer> slots = List.of(5, 6, 7, 8);
            player.sendMessage(p.getSlot() + "");
            if (slots.contains(p.getSlot())) {
                CraftScheduler.runLaterAsync(getPlugin(), player::updateInventory, 1);
            }
        }
    }
}
