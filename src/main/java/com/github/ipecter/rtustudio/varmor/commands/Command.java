package com.github.ipecter.rtustudio.varmor.commands;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.github.ipecter.rtustudio.varmor.VanishArmor;
import com.github.ipecter.rtustudio.varmor.config.VanishConfig;
import com.github.ipecter.rtustudio.varmor.manager.ToggleManager;
import com.github.ipecter.rtustudio.varmor.protocol.wrapper.WrapperPlayServerEntityEquipment;
import kr.rtuserver.framework.bukkit.api.command.RSCommand;
import kr.rtuserver.framework.bukkit.api.command.RSCommandData;
import kr.rtuserver.framework.bukkit.api.scheduler.BukkitScheduler;
import kr.rtuserver.framework.bukkit.api.utility.player.PlayerChat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Command extends RSCommand<VanishArmor> {

    private final VanishConfig config;
    private final ToggleManager manager;

    private final ItemStack empty = new ItemStack(Material.AIR);
    private final List<EnumWrappers.ItemSlot> slots = List.of(EnumWrappers.ItemSlot.HEAD, EnumWrappers.ItemSlot.CHEST, EnumWrappers.ItemSlot.LEGS, EnumWrappers.ItemSlot.FEET);

    public Command(VanishArmor plugin) {
        super(plugin, "varmor");
        this.config = plugin.getVanishConfig();
        this.manager = plugin.getToggleManager();
    }

    @Override
    public boolean execute(RSCommandData data) {
        if (!data.length(0)) return false;
        PlayerChat chat = PlayerChat.of(getPlugin());
        if (getSender() instanceof Player player) {
            if (hasPermission(getPlugin().getName() + ".vanish")) {
                boolean isVanished = manager.getMap().getOrDefault(player.getUniqueId(), false);
                if (isVanished) {
                    manager.off(player.getUniqueId());
                    chat.announce(getSender(), getMessage().get(getSender(), "disable"));
                } else {
                    manager.on(player.getUniqueId());
                    chat.announce(getSender(), getMessage().get(getSender(), "enable"));
                }
                BukkitScheduler.runLaterAsync(getPlugin(), player::updateInventory, 1);
                if (config.isHideOther()) {
                    ProtocolManager pm = ProtocolLibrary.getProtocolManager();
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.getUniqueId().equals(player.getUniqueId())) continue;
                        WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment();
                        packet.setEntityID(p.getEntityId());
                        if (isVanished) {
                            EntityEquipment eq = p.getEquipment();
                            packet.setSlotStackPair(EnumWrappers.ItemSlot.HEAD, eq.getHelmet());
                            packet.setSlotStackPair(EnumWrappers.ItemSlot.CHEST, eq.getChestplate());
                            packet.setSlotStackPair(EnumWrappers.ItemSlot.LEGS, eq.getLeggings());
                            packet.setSlotStackPair(EnumWrappers.ItemSlot.FEET, eq.getBoots());
                        } else for (EnumWrappers.ItemSlot slot : slots) packet.setSlotStackPair(slot, empty);
                        pm.sendServerPacket(player, packet.getHandle());
                    }
                }
                if (config.isHideFromOther()) {
                    ProtocolManager pm = ProtocolLibrary.getProtocolManager();
                    WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment();
                    packet.setEntityID(player.getEntityId());
                    if (isVanished) {
                        EntityEquipment eq = player.getEquipment();
                        packet.setSlotStackPair(EnumWrappers.ItemSlot.HEAD, eq.getHelmet());
                        packet.setSlotStackPair(EnumWrappers.ItemSlot.CHEST, eq.getChestplate());
                        packet.setSlotStackPair(EnumWrappers.ItemSlot.LEGS, eq.getLeggings());
                        packet.setSlotStackPair(EnumWrappers.ItemSlot.FEET, eq.getBoots());
                    } else for (EnumWrappers.ItemSlot slot : slots) packet.setSlotStackPair(slot, empty);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.getUniqueId().equals(player.getUniqueId())) continue;
                        pm.sendServerPacket(p, packet.getHandle());
                    }
                }
                return true;
            } else chat.announce(getSender(), getCommon().getMessage("noPermission"));
        } else chat.announce(getSender(), getCommon().getMessage(getSender(), "onlyPlayer"));
        return true;
    }

    @Override
    public void reload(RSCommandData data) {
        config.reload();
    }

}
