package kr.rtustudio.varmor.protocol;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import java.util.List;
import kr.rtustudio.framework.bukkit.api.integration.wrapper.PacketWrapper;
import kr.rtustudio.varmor.VanishArmor;
import kr.rtustudio.varmor.configuration.VanishConfig;
import kr.rtustudio.varmor.manager.ToggleManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class ServerWindowItems extends PacketWrapper<VanishArmor> {

    private final VanishConfig config;
    private final ToggleManager manager;
    private final ItemStack empty = ItemStack.EMPTY;

    public ServerWindowItems(VanishArmor plugin) {
        super(plugin, Priority.HIGHEST);
        this.config = plugin.getVanishConfig();
        this.manager = plugin.getToggleManager();
    }

    @Override
    public void onSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.WINDOW_ITEMS) return;

        if (!config.isHideSelf()) return;
        Player player = event.getPlayer();
        if (!getPlugin().hasPermission(player, "vanish")) return;
        if (!manager.get(player.getUniqueId())) return;

        WrapperPlayServerWindowItems packet = new WrapperPlayServerWindowItems(event);
        if (player.getOpenInventory().getType() == InventoryType.CRAFTING) {
            List<ItemStack> items = packet.getItems();
            if (items.size() >= 9) {
                for (int slot = 5; slot < 9; slot++) {
                    ItemStack itemStack = items.get(slot);
                    if (itemStack == null || itemStack.isEmpty()) continue;
                    if (isCosmetic(itemStack)) continue;
                    items.set(slot, empty);
                }
                packet.setItems(items);
            }
        }
    }

    private boolean isCosmetic(ItemStack itemStack) {
        org.bukkit.inventory.ItemStack bukkitStack = SpigotConversionUtil.toBukkitItemStack(itemStack);
        return bukkitStack != null
                && bukkitStack.hasItemMeta()
                && bukkitStack.getItemMeta().hasCustomModelData();
    }
}
