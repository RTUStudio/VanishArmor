package com.github.ipecter.rtustudio.varmor.command;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.github.ipecter.rtustudio.varmor.VanishArmor;
import com.github.ipecter.rtustudio.varmor.configuration.VanishConfig;
import com.github.ipecter.rtustudio.varmor.manager.ToggleManager;
import com.github.ipecter.rtustudio.varmor.protocol.wrapper.WrapperPlayServerEntityEquipment;
import kr.rtuserver.framework.bukkit.api.command.RSCommand;
import kr.rtuserver.framework.bukkit.api.command.RSCommandData;
import kr.rtuserver.framework.bukkit.api.scheduler.CraftScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MainCommand extends RSCommand<VanishArmor> {

    private final VanishConfig config;
    private final ToggleManager manager;

    private final ItemStack empty = new ItemStack(Material.AIR);
    private final List<EnumWrappers.ItemSlot> slots = List.of(EnumWrappers.ItemSlot.HEAD, EnumWrappers.ItemSlot.CHEST, EnumWrappers.ItemSlot.LEGS, EnumWrappers.ItemSlot.FEET);

    public MainCommand(VanishArmor plugin) {
        super(plugin, "varmor");
        this.config = plugin.getVanishConfig();
        this.manager = plugin.getToggleManager();
    }

    @Override
    public boolean execute(RSCommandData data) {
        if (!data.length(0)) return false;
        Player player = player();
        if (player == null) {
            chat().announce(audience(), message().getCommon(player(), "onlyPlayer"));
            return true;
        }
        if (hasPermission(getPlugin().getName() + ".vanish")) {
            boolean isVanished = manager.get(player.getUniqueId());
            if (isVanished) {
                manager.off(player.getUniqueId());
                chat().announce(audience(), message().get(player(), "disable"));
            } else {
                manager.on(player.getUniqueId());
                chat().announce(audience(), message().get(player(), "enable"));
            }
            CraftScheduler.runLaterAsync(getPlugin(), player::updateInventory, 1);
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
        } else chat().announce(audience(), message().getCommon("noPermission"));
        return true;
    }

    @Override
    public void reload(RSCommandData data) {
        config.reload();
    }

}
