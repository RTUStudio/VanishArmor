package kr.rtustudio.varmor.manager;

import kr.rtustudio.framework.bukkit.api.platform.JSON;
import kr.rtustudio.framework.bukkit.api.storage.Storage;
import kr.rtustudio.varmor.VanishArmor;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class ToggleManager {

    private final VanishArmor plugin;
    private final Map<UUID, Boolean> map = new HashMap<>();

    public boolean get(UUID uuid) {
        return map.getOrDefault(uuid, false);
    }

    public void addPlayer(UUID uuid) {
        Storage storage = plugin.getStorage();
        storage.get("Toggle", JSON.of("uuid", uuid.toString())).thenAccept(result -> {
            if (result == null || result.isEmpty()) {
                storage.add("Toggle", JSON.of("uuid", uuid.toString()).append("toggle", false));
                map.put(uuid, false);
            } else map.put(uuid, result.getFirst().get("toggle").getAsBoolean());
        });
    }

    public void removePlayer(UUID uuid) {
        map.remove(uuid);
    }

    public void on(UUID uuid) {
        Storage storage = plugin.getStorage();
        storage.set("Toggle", JSON.of("uuid", uuid.toString()), JSON.of("toggle", true));
        map.put(uuid, true);
    }

    public void off(UUID uuid) {
        Storage storage = plugin.getStorage();
        storage.set("Toggle", JSON.of("uuid", uuid.toString()), JSON.of("toggle", false));
        map.put(uuid, false);
    }

}
