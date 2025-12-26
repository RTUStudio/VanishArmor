package kr.rtustudio.varmor.dependency;

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
    public String onRequest(OfflinePlayer offlinePlayer, String[] params) {
        if (params.length == 0) return "ERROR";
        if ("status".equalsIgnoreCase(params[0])) {
            boolean toggle = manager.get(offlinePlayer.getUniqueId());
            if (offlinePlayer instanceof Player player) {
                return message().get(player, toggle ? "placeholder.active" : "placeholder.inactive");
            }
            return message().get(toggle ? "placeholder.active" : "placeholder.inactive");
        }
        return "ERROR";
    }
}
