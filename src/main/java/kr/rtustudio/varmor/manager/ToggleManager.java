package kr.rtustudio.varmor.manager;

import kr.rtustudio.storage.JSON;
import kr.rtustudio.storage.Storage;
import kr.rtustudio.varmor.VanishArmor;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

@RequiredArgsConstructor
public class ToggleManager {

    private final VanishArmor plugin;
    private final Map<UUID, Boolean> map = new Object2ObjectOpenHashMap<>();

    public boolean get(UUID uuid) {
        return map.getOrDefault(uuid, false);
    }

    public void addPlayer(UUID uuid) {
        Storage storage = plugin.getStorage("Toggle");
        storage.get(JSON.of("uuid", uuid.toString())).thenAccept(result -> {
            if (result == null || result.isEmpty()) {
                storage.add(JSON.of("uuid", uuid.toString()).append("toggle", false));
                map.put(uuid, false);
            } else map.put(uuid, result.getFirst().get("toggle").getAsBoolean());
        });
    }

    public void removePlayer(UUID uuid) {
        map.remove(uuid);
    }

    public void on(UUID uuid) {
        Storage storage = plugin.getStorage("Toggle");
        storage.set(JSON.of("uuid", uuid.toString()), JSON.of("toggle", true));
        map.put(uuid, true);
    }

    public void off(UUID uuid) {
        Storage storage = plugin.getStorage("Toggle");
        storage.set(JSON.of("uuid", uuid.toString()), JSON.of("toggle", false));
        map.put(uuid, false);
    }

}
