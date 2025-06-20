package com.github.ipecter.rtustudio.varmor.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.github.ipecter.rtustudio.varmor.VanishArmor;
import com.github.ipecter.rtustudio.varmor.configuration.VanishConfig;
import com.github.ipecter.rtustudio.varmor.manager.ToggleManager;
import com.github.ipecter.rtustudio.varmor.protocol.wrapper.WrapperPlayServerWindowItems;
import kr.rtuserver.framework.bukkit.api.integration.RSPacketListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayServerWindowItemsListener extends RSPacketListener<VanishArmor> {

    private final VanishConfig config;
    private final ToggleManager manager;
    private final ItemStack empty = new ItemStack(Material.AIR);

    public PlayServerWindowItemsListener(VanishArmor plugin) {
        super(plugin, new AdapterParameteters()
                .listenerPriority(ListenerPriority.HIGHEST)
                .types(PacketType.Play.Server.WINDOW_ITEMS)
                .optionAsync());
        this.config = plugin.getVanishConfig();
        this.manager = plugin.getToggleManager();
    }

    public void send(PacketEvent event) {
        Player player = event.getPlayer();
        if (!manager.get(player.getUniqueId())) return;
        if (!player.hasPermission(getPlugin().getName() + ".vanish")) return;
        if (!config.isHideSelf()) return;
        PacketContainer packet = event.getPacket();
        WrapperPlayServerWindowItems p = new WrapperPlayServerWindowItems(packet);
        if (player.getOpenInventory().getType() == InventoryType.CRAFTING) {
            List<ItemStack> list = p.getSlotData();
            for (int slot = 5; slot < 9; slot++) {
                ItemStack itemStack = list.get(slot);
                if (itemStack == null) continue;
                if (isCosmetic(itemStack)) continue;
                list.set(slot, empty);
            }
            p.setSlotData(list);
        }

    }

    private boolean isCosmetic(ItemStack itemStack) {
        return itemStack.hasItemMeta() && itemStack.getItemMeta().hasCustomModelData();
    }

}
