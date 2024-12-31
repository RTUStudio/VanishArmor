package com.github.ipecter.rtustudio.varmor.commands;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.github.ipecter.rtustudio.varmor.manager.ToggleManager;
import com.github.ipecter.rtustudio.varmor.protocol.wrapper.WrapperPlayServerEntityEquipment;
import kr.rtuserver.framework.bukkit.api.command.RSCommand;
import kr.rtuserver.framework.bukkit.api.command.RSCommandData;
import com.github.ipecter.rtustudio.varmor.VanishArmor;
import com.github.ipecter.rtustudio.varmor.config.VanishConfig;
import kr.rtuserver.framework.bukkit.api.utility.player.PlayerChat;
import kr.rtuserver.framework.bukkit.api.utility.scheduler.CraftScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Command extends RSCommand<VanishArmor> {

    private final VanishConfig config;
    private final ToggleManager manager;

    private final ItemStack empty = new ItemStack(Material.AIR);

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
            if (config.isHideSelf()) CraftScheduler.runLaterAsync(getPlugin(), player::updateInventory, 1);
            if (config.isHideOther()) {
                ProtocolManager pm = ProtocolLibrary.getProtocolManager();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment();
                    packet.setEntityID(p.getEntityId());
                    packet.setSlotStackPair(EnumWrappers.ItemSlot.HEAD, empty);
                    packet.setSlotStackPair(EnumWrappers.ItemSlot.CHEST, empty);
                    packet.setSlotStackPair(EnumWrappers.ItemSlot.LEGS, empty);
                    packet.setSlotStackPair(EnumWrappers.ItemSlot.FEET, empty);
                    pm.sendServerPacket(player, packet.getHandle());
                }
            }
            if (config.isHideFromOther()) {
                ProtocolManager pm = ProtocolLibrary.getProtocolManager();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment();
                    packet.setEntityID(player.getEntityId());
                    packet.setSlotStackPair(EnumWrappers.ItemSlot.HEAD, empty);
                    packet.setSlotStackPair(EnumWrappers.ItemSlot.CHEST, empty);
                    packet.setSlotStackPair(EnumWrappers.ItemSlot.LEGS, empty);
                    packet.setSlotStackPair(EnumWrappers.ItemSlot.FEET, empty);
                    pm.sendServerPacket(p, packet.getHandle());
                }
            }
            return true;
        } else chat.announce(getSender(), getCommon().getMessage(getSender(), "onlyPlayer"));
        return false;
    }

    @Override
    public void reload(RSCommandData data) {
        config.reload();
    }

}
