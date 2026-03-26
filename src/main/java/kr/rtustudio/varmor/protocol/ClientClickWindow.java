package kr.rtustudio.varmor.protocol;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import kr.rtustudio.framework.bukkit.api.integration.wrapper.PacketWrapper;
import kr.rtustudio.framework.bukkit.api.scheduler.CraftScheduler;
import kr.rtustudio.varmor.VanishArmor;
import kr.rtustudio.varmor.configuration.VanishConfig;
import kr.rtustudio.varmor.manager.ToggleManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.List;

public class ClientClickWindow extends PacketWrapper<VanishArmor> {

    private static final List<Integer> ARMOR_SLOTS = List.of(5, 6, 7, 8);

    private final VanishConfig config;
    private final ToggleManager manager;

    public ClientClickWindow(VanishArmor plugin) {
        super(plugin, Priority.HIGHEST);
        this.config = plugin.getVanishConfig();
        this.manager = plugin.getToggleManager();
    }

    @Override
    public void onReceive(PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.CLICK_WINDOW) return;

        if (!config.isHideSelf()) return;
        Player player = event.getPlayer();
        if (!plugin.hasPermission(player, "vanish")) return;
        if (!manager.get(player.getUniqueId())) return;

        WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);
        if (player.getOpenInventory().getType() != InventoryType.CRAFTING) return;
        
        if (ARMOR_SLOTS.contains(packet.getSlot())) {
            CraftScheduler.delay(plugin, player::updateInventory, 1, true);
        }
    }
}
