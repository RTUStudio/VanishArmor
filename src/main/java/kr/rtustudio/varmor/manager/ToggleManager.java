package kr.rtustudio.varmor.manager;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import kr.rtustudio.storage.JSON;
import kr.rtustudio.storage.Storage;
import kr.rtustudio.varmor.VanishArmor;

import java.util.Map;
import java.util.UUID;

public class ToggleManager {

    private final Storage storage;
    private final Map<UUID, Boolean> map = new Object2ObjectOpenHashMap<>();

    public ToggleManager(VanishArmor plugin) {
        this.storage = plugin.getStorage("Toggle");
    }

    public boolean get(UUID uuid) {
        return map.getOrDefault(uuid, false);
    }

    public void addPlayer(UUID uuid) {
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
        storage.set(JSON.of("uuid", uuid.toString()), JSON.of("toggle", true));
        map.put(uuid, true);
    }

    public void off(UUID uuid) {
        storage.set(JSON.of("uuid", uuid.toString()), JSON.of("toggle", false));
        map.put(uuid, false);
    }

}
