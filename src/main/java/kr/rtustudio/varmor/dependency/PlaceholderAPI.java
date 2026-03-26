package kr.rtustudio.varmor.dependency;

import kr.rtustudio.framework.bukkit.api.integration.wrapper.PlaceholderArgs;
import kr.rtustudio.framework.bukkit.api.integration.wrapper.PlaceholderWrapper;
import kr.rtustudio.varmor.VanishArmor;
import kr.rtustudio.varmor.manager.ToggleManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaceholderAPI extends PlaceholderWrapper<VanishArmor> {

    private final ToggleManager manager;

    public PlaceholderAPI(VanishArmor plugin) {
        super(plugin);
        this.manager = plugin.getToggleManager();
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, PlaceholderArgs params) {
        if (params.isEmpty()) return "ERROR";
        if (params.equalsIgnoreCase(0, "status")) {
            boolean toggle = manager.get(offlinePlayer.getUniqueId());
            if (offlinePlayer instanceof Player player) {
                return message.get(player, toggle ? "placeholder.active" : "placeholder.inactive");
            }
            return message.get(toggle ? "placeholder.active" : "placeholder.inactive");
        }
        return "ERROR";
    }
}
