package kr.rtustudio.varmor.listener;

import kr.rtustudio.framework.bukkit.api.listener.RSListener;
import kr.rtustudio.framework.bukkit.api.scheduler.CraftScheduler;
import kr.rtustudio.varmor.VanishArmor;
import kr.rtustudio.varmor.manager.ToggleManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@SuppressWarnings("unused")
public class PlayerItemDamage extends RSListener<VanishArmor> {

    private final ToggleManager manager;

    public PlayerItemDamage(VanishArmor plugin) {
        super(plugin);
        this.manager = plugin.getToggleManager();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onArmorDamage(PlayerItemDamageEvent e) {
        Player player = e.getPlayer();
        if (!manager.get(player.getUniqueId())) return;
        if (!getPlugin().hasPermission(player, "vanish")) return;
        ItemStack itemStack = e.getItem();
        if (check(player, itemStack)) CraftScheduler.delay(getPlugin(), player::updateInventory, 1, true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onArmorMend(PlayerItemMendEvent e) {
        Player player = e.getPlayer();
        if (!manager.get(player.getUniqueId())) return;
        if (!getPlugin().hasPermission(player, "vanish")) return;
        ItemStack itemStack = e.getItem();
        if (check(player, itemStack)) CraftScheduler.delay(getPlugin(), player::updateInventory, 1, true);
    }

    private boolean check(Player player, ItemStack itemStack) {
        EntityEquipment equipment = player.getEquipment();
        for (EquipmentSlot slot : List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)) {
            ItemStack target = equipment.getItem(slot);
            return target.isSimilar(itemStack);
        }
        return false;
    }

}
