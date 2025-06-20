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
import kr.rtuserver.framework.bukkit.api.integration.RSPacketListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayServerEntityEquipmentListener extends RSPacketListener<VanishArmor> {

    private final VanishConfig config;
    private final ToggleManager manager;
    private final ItemStack empty = new ItemStack(Material.AIR);
    private final List<EnumWrappers.ItemSlot> slots;

    public PlayServerEntityEquipmentListener(VanishArmor plugin) {
        super(plugin, new AdapterParameteters()
                .listenerPriority(ListenerPriority.HIGHEST)
                .types(PacketType.Play.Server.ENTITY_EQUIPMENT)
                .optionAsync());
        this.config = plugin.getVanishConfig();
        this.manager = plugin.getToggleManager();
        this.slots = List.of(EnumWrappers.ItemSlot.HEAD, EnumWrappers.ItemSlot.CHEST, EnumWrappers.ItemSlot.LEGS, EnumWrappers.ItemSlot.FEET);

    }

    public void send(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        WrapperPlayServerEntityEquipment p = new WrapperPlayServerEntityEquipment(packet);
        if (config.isHideOther()) {
            if (check(p.getEntityID())) {
                Player player = event.getPlayer();
                if (!manager.get(player.getUniqueId())) return;
                if (!player.hasPermission(getPlugin().getName() + ".vanish")) return;
                for (EnumWrappers.ItemSlot slot : slots) {
                    ItemStack itemStack = p.getItem(slot);
                    if (itemStack == null) continue;
                    if (config.isBypassCosmetics() && isCosmetic(itemStack)) continue;
                    p.setSlotStackPair(slot, empty);
                }
            }
        }
        if (config.isHideFromOther()) {
            if (check(p.getEntityID())) {
                if (p.getEntity(event) instanceof Player player) {
                    if (!manager.get(player.getUniqueId())) return;
                    if (!player.hasPermission(getPlugin().getName() + ".vanish")) return;
                    for (EnumWrappers.ItemSlot slot : slots) {
                        ItemStack itemStack = p.getItem(slot);
                        if (itemStack == null) continue;
                        if (config.isBypassCosmetics() && isCosmetic(itemStack)) continue;
                        p.setSlotStackPair(slot, empty);
                    }
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

    private boolean isCosmetic(ItemStack itemStack) {
        return itemStack.hasItemMeta() && itemStack.getItemMeta().hasCustomModelData();
    }
}
