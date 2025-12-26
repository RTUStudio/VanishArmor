package kr.rtustudio.varmor;

import kr.rtustudio.varmor.command.MainCommand;
import kr.rtustudio.varmor.configuration.VanishConfig;
import kr.rtustudio.varmor.dependency.PlaceholderAPI;
import kr.rtustudio.varmor.listener.PlayerItemDamage;
import kr.rtustudio.varmor.listener.PlayerJoinQuit;
import kr.rtustudio.varmor.manager.ToggleManager;
import io.papermc.paper.inventory.ItemRarity;
import kr.rtustudio.framework.bukkit.api.RSPlugin;
import kr.rtustudio.varmor.protocol.ClientClickWindow;
import kr.rtustudio.varmor.protocol.ServerEntityEquipment;
import kr.rtustudio.varmor.protocol.ServerWindowItems;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionDefault;

public class VanishArmor extends RSPlugin {

    @Getter
    private static VanishArmor instance;
    @Getter
    private VanishConfig vanishConfig;
    @Getter
    private ToggleManager toggleManager;

    @Override
    public void load() {
        instance = this;
    }

    @Override
    public void enable() {
        initStorage("Toggle");

        vanishConfig = registerConfiguration(VanishConfig.class, "Vanish", 1);
        toggleManager = new ToggleManager(this);

        registerPermission("vanish", PermissionDefault.TRUE);

        registerCommand(new MainCommand(this), true);

        registerEvent(new PlayerItemDamage(this));
        registerEvent(new PlayerJoinQuit(this));

        registerIntegration(new ClientClickWindow(this));
        registerIntegration(new ServerEntityEquipment(this));
        registerIntegration(new ServerWindowItems(this));
        registerIntegration(new PlaceholderAPI(this));
    }

    public ItemStack replacement(ItemStack itemStack) {
        if (itemStack == null) return new ItemStack(Material.AIR);
        ItemStack clone = itemStack.clone();
        ItemMeta itemMeta = clone.getItemMeta();
        if (itemMeta == null) return new ItemStack(Material.AIR);
        Component displayName;
        if (itemMeta.hasDisplayName()) displayName = itemMeta.displayName();
        else {
            String key = clone.getType().translationKey();
            displayName = Component.translatable(key)
                    .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        }
        assert displayName != null;
        if (displayName.color() == null) {
            try {
                ItemRarity rarity = clone.getRarity();
                displayName = displayName.color(rarity.getColor());
            } catch (IllegalStateException ignored) {
                // 아이템에 Rarity가 존재하지 않습니다
            }
        }
        itemMeta.displayName(displayName);
        clone.setItemMeta(itemMeta);
        return clone;
    }

}
