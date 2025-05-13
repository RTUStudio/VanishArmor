package com.github.ipecter.rtustudio.varmor;

import com.github.ipecter.rtustudio.varmor.command.MainCommand;
import com.github.ipecter.rtustudio.varmor.configuration.VanishConfig;
import com.github.ipecter.rtustudio.varmor.dependency.PlaceholderAPI;
import com.github.ipecter.rtustudio.varmor.listener.PlayerItemDamage;
import com.github.ipecter.rtustudio.varmor.listener.PlayerJoinQuit;
import com.github.ipecter.rtustudio.varmor.manager.ToggleManager;
import com.github.ipecter.rtustudio.varmor.protocol.PlayClientWindowClickListener;
import com.github.ipecter.rtustudio.varmor.protocol.PlayServerEntityEquipmentListener;
import com.github.ipecter.rtustudio.varmor.protocol.PlayServerWindowItemsListener;
import kr.rtuserver.framework.bukkit.api.RSPlugin;
import lombok.Getter;
import org.bukkit.permissions.PermissionDefault;

public class VanishArmor extends RSPlugin {

    @Getter
    private static VanishArmor instance;
    @Getter
    private VanishConfig vanishConfig;
    @Getter
    private ToggleManager toggleManager;

    private PlayClientWindowClickListener playClientWindowClickListener;
    private PlayServerEntityEquipmentListener playServerEntityEquipmentListener;
    private PlayServerWindowItemsListener playServerWindowItemsListener;
    private PlaceholderAPI placeholder;

    @Override
    public void load() {
        instance = this;
    }

    @Override
    public void enable() {
        getConfigurations().getStorage().init("Toggle");

        vanishConfig = new VanishConfig(this);
        toggleManager = new ToggleManager(this);

        registerPermission(getName() + ".vanish", PermissionDefault.TRUE);

        registerCommand(new MainCommand(this), true);

        registerEvent(new PlayerItemDamage(this));
        registerEvent(new PlayerJoinQuit(this));

        playClientWindowClickListener = new PlayClientWindowClickListener(this);
        playClientWindowClickListener.register();
        playServerEntityEquipmentListener = new PlayServerEntityEquipmentListener(this);
        playServerEntityEquipmentListener.register();
        playServerWindowItemsListener = new PlayServerWindowItemsListener(this);
        playServerWindowItemsListener.register();

        if (getFramework().isEnabledDependency("PlaceholderAPI")) {
            placeholder = new PlaceholderAPI(this);
            placeholder.register();
        }
    }

    @Override
    public void disable() {
        playClientWindowClickListener.unregister();
        playServerEntityEquipmentListener.unregister();
        playServerWindowItemsListener.unregister();

        if (getFramework().isEnabledDependency("PlaceholderAPI")) {
            placeholder.unregister();
        }
    }

}
