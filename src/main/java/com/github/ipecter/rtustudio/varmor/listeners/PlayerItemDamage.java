package com.github.ipecter.rtustudio.varmor.listeners;

import com.github.ipecter.rtustudio.varmor.VanishArmor;
import com.github.ipecter.rtustudio.varmor.manager.ToggleManager;
import kr.rtuserver.framework.bukkit.api.listener.RSListener;
import kr.rtuserver.framework.bukkit.api.scheduler.BukkitScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerItemDamage extends RSListener<VanishArmor> {

    private final ToggleManager manager;

    public PlayerItemDamage(VanishArmor plugin) {
        super(plugin);
        this.manager = plugin.getToggleManager();
    }

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent e) {
        Player player = e.getPlayer();
        if (!manager.getMap().getOrDefault(player.getUniqueId(), false)) return;
        if (!player.hasPermission(getPlugin().getName() + ".vanish")) return;
        ItemStack itemStack = e.getItem();
        if (check(player, itemStack)) BukkitScheduler.runLaterAsync(getPlugin(), player::updateInventory, 1);
    }

    @EventHandler
    public void onPlayerItemDamage(PlayerItemMendEvent e) {
        Player player = e.getPlayer();
        if (!manager.getMap().getOrDefault(player.getUniqueId(), false)) return;
        if (!player.hasPermission(getPlugin().getName() + ".vanish")) return;
        ItemStack itemStack = e.getItem();
        if (check(player, itemStack)) BukkitScheduler.runLaterAsync(getPlugin(), player::updateInventory, 1);
    }

    private boolean check(Player player, ItemStack itemStack) {
        EntityEquipment equipment = player.getEquipment();
        if (equipment == null) return false;
        for (EquipmentSlot slot : List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)) {
            ItemStack target = equipment.getItem(slot);
            return target.isSimilar(itemStack);
        }
        return false;
    }

}
