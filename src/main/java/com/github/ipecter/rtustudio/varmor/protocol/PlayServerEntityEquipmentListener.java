package com.github.ipecter.rtustudio.varmor.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.github.ipecter.rtustudio.varmor.VanishArmor;
import com.github.ipecter.rtustudio.varmor.configuration.VanishConfig;
import com.github.ipecter.rtustudio.varmor.manager.ToggleManager;
import com.github.ipecter.rtustudio.varmor.protocol.wrapper.WrapperPlayServerEntityEquipment;
import kr.rtuserver.framework.bukkit.api.dependencies.RSPacketListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayServerEntityEquipmentListener extends RSPacketListener<VanishArmor> {

    private final VanishConfig config;
    private final ToggleManager manager;
    private final ItemStack empty = new ItemStack(Material.AIR);

    public PlayServerEntityEquipmentListener(VanishArmor plugin) {
        super(plugin, new AdapterParameteters()
                .listenerPriority(ListenerPriority.HIGHEST)
                .types(PacketType.Play.Server.ENTITY_EQUIPMENT)
                .optionAsync());
        this.config = plugin.getVanishConfig();
        this.manager = plugin.getToggleManager();
    }

    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        WrapperPlayServerEntityEquipment p = new WrapperPlayServerEntityEquipment(packet);
        if (config.isHideOther()) {
            if (check(p.getEntityID())) {
                Player player = event.getPlayer();
                if (!manager.getMap().getOrDefault(player.getUniqueId(), false)) return;
                if (!player.hasPermission(getPlugin().getName() + ".vanish")) return;
                p.setSlotStackPair(EnumWrappers.ItemSlot.HEAD, empty);
                p.setSlotStackPair(EnumWrappers.ItemSlot.CHEST, empty);
                p.setSlotStackPair(EnumWrappers.ItemSlot.LEGS, empty);
                p.setSlotStackPair(EnumWrappers.ItemSlot.FEET, empty);
            }
        }
        if (config.isHideFromOther()) {
            if (check(p.getEntityID())) {
                if (p.getEntity(event) instanceof Player player) {
                    if (!manager.getMap().getOrDefault(player.getUniqueId(), false)) return;
                    if (!player.hasPermission(getPlugin().getName() + ".vanish")) return;
                    p.setSlotStackPair(EnumWrappers.ItemSlot.HEAD, empty);
                    p.setSlotStackPair(EnumWrappers.ItemSlot.CHEST, empty);
                    p.setSlotStackPair(EnumWrappers.ItemSlot.LEGS, empty);
                    p.setSlotStackPair(EnumWrappers.ItemSlot.FEET, empty);
                }
            }
        }
    }

    private boolean check(int id) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (id == player.getEntityId()) return true;
        }
        return false;
    }
}
