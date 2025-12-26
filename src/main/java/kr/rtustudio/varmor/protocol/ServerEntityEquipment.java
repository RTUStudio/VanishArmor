package kr.rtustudio.varmor.protocol;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import kr.rtustudio.configurate.objectmapping.meta.Comment;
import kr.rtustudio.framework.bukkit.api.integration.wrapper.PacketWrapper;
import kr.rtustudio.varmor.VanishArmor;
import kr.rtustudio.varmor.configuration.VanishConfig;
import kr.rtustudio.varmor.manager.ToggleManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class ServerEntityEquipment extends PacketWrapper<VanishArmor> {

    private static final List<EquipmentSlot> EQUIP_SLOTS =
            List.of(EquipmentSlot.HELMET, EquipmentSlot.CHEST_PLATE, EquipmentSlot.LEGGINGS, EquipmentSlot.BOOTS);

    private final VanishConfig config;
    private final ToggleManager manager;
    private final ItemStack empty = ItemStack.EMPTY;

    public ServerEntityEquipment(VanishArmor plugin) {
        super(plugin, Priority.HIGHEST);
        this.config = plugin.getVanishConfig();
        this.manager = plugin.getToggleManager();
    }

    @Override
    public void onSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.ENTITY_EQUIPMENT) return;

        WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(event);
        int targetId = packet.getEntityId();

        Player target = getPlayer(targetId);
        if (target == null) return;

        if (config.isHideOther()) {
            Player viewer = event.getPlayer();
            if (!manager.get(viewer.getUniqueId())) return;
            if (!getPlugin().hasPermission(viewer, "vanish")) return;
            stripEquipment(packet);
        }

        if (config.isHideFromOther()) {
            if (!manager.get(target.getUniqueId())) return;
            if (!getPlugin().hasPermission(target, "vanish")) return;
            stripEquipment(packet);
        }
    }

    private void stripEquipment(WrapperPlayServerEntityEquipment packet) {
        List<Equipment> equipment = packet.getEquipment();
        for (int i = 0; i < equipment.size(); i++) {
            Equipment eq = equipment.get(i);
            if (!EQUIP_SLOTS.contains(eq.getSlot())) continue;
            ItemStack item = eq.getItem();
            if (item.isEmpty()) continue;
            if (config.isBypassCosmetics() && isCosmetic(item)) continue;
            equipment.set(i, new Equipment(eq.getSlot(), empty));
        }
        packet.setEquipment(equipment);
    }

    private Player getPlayer(int id) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getEntityId() == id) return player;
        }
        return null;
    }

    private boolean isCosmetic(ItemStack itemStack) {
        org.bukkit.inventory.ItemStack bukkitStack = SpigotConversionUtil.toBukkitItemStack(itemStack);
        return bukkitStack != null
                && bukkitStack.hasItemMeta()
                && bukkitStack.getItemMeta().hasCustomModelData();
    }
}
