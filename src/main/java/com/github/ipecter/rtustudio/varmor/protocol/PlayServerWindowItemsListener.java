package com.github.ipecter.rtustudio.varmor.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.github.ipecter.rtustudio.varmor.VanishArmor;
import com.github.ipecter.rtustudio.varmor.configuration.VanishConfig;
import com.github.ipecter.rtustudio.varmor.manager.ToggleManager;
import com.github.ipecter.rtustudio.varmor.protocol.wrapper.WrapperPlayServerWindowItems;
import kr.rtuserver.framework.bukkit.api.dependencies.RSPacketListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class PlayServerWindowItemsListener extends RSPacketListener<VanishArmor> {

    private final VanishConfig config;
    private final ToggleManager manager;

    public PlayServerWindowItemsListener(VanishArmor plugin) {
        super(plugin, new AdapterParameteters()
                .listenerPriority(ListenerPriority.HIGHEST)
                .types(PacketType.Play.Server.WINDOW_ITEMS)
                .optionAsync());
        this.config = plugin.getVanishConfig();
        this.manager = plugin.getToggleManager();
    }

    public void onPacketSending(PacketEvent event) {
        Player player = event.getPlayer();
        if (!manager.getMap().getOrDefault(player.getUniqueId(), false)) return;
        if (!player.hasPermission(getPlugin().getName() + ".vanish")) return;
        if (!config.isHideSelf()) return;
        PacketContainer packet = event.getPacket();
        WrapperPlayServerWindowItems p = new WrapperPlayServerWindowItems(packet);
        if (player.getOpenInventory().getType() == InventoryType.CRAFTING) {
            List<ItemStack> list = p.getSlotData();
            ItemStack itemStack = new ItemStack(Material.AIR);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemStack.setItemMeta(itemMeta);
            list.set(5, itemStack);
            list.set(6, itemStack);
            list.set(7, itemStack);
            list.set(8, itemStack);
            p.setSlotData(list);
        }

    }

}
