package kr.rtustudio.varmor.command;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import kr.rtustudio.framework.bukkit.api.command.RSCommand;
import kr.rtustudio.framework.bukkit.api.command.RSCommandData;
import kr.rtustudio.framework.bukkit.api.scheduler.CraftScheduler;
import kr.rtustudio.varmor.VanishArmor;
import kr.rtustudio.varmor.configuration.VanishConfig;
import kr.rtustudio.varmor.manager.ToggleManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;

import java.util.ArrayList;
import java.util.List;

public class MainCommand extends RSCommand<VanishArmor> {

    private final VanishConfig config;
    private final ToggleManager toggleManager;

    private final PlayerManager playerManager;

    private final ItemStack empty =
            SpigotConversionUtil.fromBukkitItemStack(new org.bukkit.inventory.ItemStack(Material.AIR));
    private final List<EquipmentSlot> slots =
            List.of(EquipmentSlot.HELMET, EquipmentSlot.CHEST_PLATE, EquipmentSlot.LEGGINGS, EquipmentSlot.BOOTS);

    public MainCommand(VanishArmor plugin) {
        super(plugin, "varmor");
        this.config = plugin.getVanishConfig();
        this.toggleManager = plugin.getToggleManager();
        this.playerManager = PacketEvents.getAPI().getPlayerManager();
    }

    @Override
    public Result execute(RSCommandData data) {
        if (!data.length(0)) return Result.WRONG_USAGE;
        Player player = player();
        if (player == null) return Result.ONLY_PLAYER;
        if (!hasPermission("vanish")) return Result.NO_PERMISSION;

        boolean isVanished = toggleManager.get(player.getUniqueId());
        if (isVanished) {
            toggleManager.off(player.getUniqueId());
            chat().announce(audience(), message().get(player(), "disable"));
        } else {
            toggleManager.on(player.getUniqueId());
            chat().announce(audience(), message().get(player(), "enable"));
        }
        CraftScheduler.delay(getPlugin(), player::updateInventory, 1, true);

        if (config.isHideOther()) {
            for (Player p : player.getTrackedPlayers()) {
                if (p.getUniqueId().equals(player.getUniqueId())) continue;
                if (config.isHideFromOther()) {
                    if (toggleManager.get(p.getUniqueId())) continue;
                }
                WrapperPlayServerEntityEquipment packet = buildEquipmentPacket(p, isVanished);
                playerManager.sendPacket(player, packet);
            }
        }
        if (config.isHideFromOther()) {
            WrapperPlayServerEntityEquipment packet = buildEquipmentPacket(player, isVanished);
            for (Player p : player.getTrackedPlayers()) {
                if (p.getUniqueId().equals(player.getUniqueId())) continue;
                playerManager.sendPacket(p, packet);
            }
        }
        return Result.SUCCESS;
    }

    @Override
    public void reload(RSCommandData data) {
        getPlugin().reloadConfiguration(VanishConfig.class);
    }

    private WrapperPlayServerEntityEquipment buildEquipmentPacket(Player target, boolean vanished) {
        List<Equipment> equipmentList = new ArrayList<>();
        EntityEquipment eq = target.getEquipment();
        for (EquipmentSlot slot : slots) {
            org.bukkit.inventory.ItemStack bukkitItem =
                    vanished ? switch (slot) {
                        case HELMET -> eq.getHelmet();
                        case CHEST_PLATE -> eq.getChestplate();
                        case LEGGINGS -> eq.getLeggings();
                        case BOOTS -> eq.getBoots();
                        default -> null;
                    } : null;
            ItemStack item = vanished && bukkitItem != null
                    ? SpigotConversionUtil.fromBukkitItemStack(bukkitItem)
                    : empty;
            equipmentList.add(new Equipment(slot, item));
        }
        return new WrapperPlayServerEntityEquipment(target.getEntityId(), equipmentList);
    }

}
