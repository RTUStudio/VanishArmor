package com.github.ipecter.rtustudio.varmor.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.github.ipecter.rtustudio.varmor.VanishArmor;
import com.github.ipecter.rtustudio.varmor.config.VanishConfig;
import com.github.ipecter.rtustudio.varmor.manager.ToggleManager;
import com.github.ipecter.rtustudio.varmor.protocol.wrapper.WrapperPlayClientWindowClick;
import kr.rtuserver.framework.bukkit.api.dependencies.RSPacketListener;
import kr.rtuserver.framework.bukkit.api.utility.scheduler.CraftScheduler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

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

    private final ItemStack empty = new ItemStack(Material.AIR);

    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();
        if (!manager.getMap().getOrDefault(player.getUniqueId(), false)) return;
        if (!player.hasPermission(getPlugin().getName() + ".vanish")) return;
        if (!config.isHideSelf()) return;
        PacketContainer packet = event.getPacket();
        WrapperPlayClientWindowClick p = new WrapperPlayClientWindowClick(packet);
        if (player.getOpenInventory().getType() == InventoryType.CRAFTING) {
            List<Integer> slots = List.of(5, 6, 7, 8);
            if (slots.contains(p.getSlot())) {
                CraftScheduler.runLaterAsync(getPlugin(), player::updateInventory, 1);
            }
        }
    }
}
