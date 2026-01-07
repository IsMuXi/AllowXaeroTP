package allow.xaerotp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportCooldown {

    private static final Map<UUID, Long> LAST_REQUEST_TIME = new HashMap<>();
    private static final Map<UUID, Long> LAST_TP_TIME = new HashMap<>();
    private static final long DEBOUNCE_MS = 300;

    public static boolean isOnCooldown(UUID uuid, int cooldownSeconds) {
        long now = System.currentTimeMillis();
        Long last = LAST_TP_TIME.get(uuid);
        if (last == null) return false;
        return (now - last) < cooldownSeconds * 1000L;
    }

    public static boolean shouldIgnore(UUID uuid) {
        long now = System.currentTimeMillis();
        Long last = LAST_REQUEST_TIME.get(uuid);
        LAST_REQUEST_TIME.put(uuid, now);
        return last != null && (now - last) < DEBOUNCE_MS;
    }

    public static long getRemaining(UUID uuid, int cooldownSeconds) {
        long now = System.currentTimeMillis();
        Long last = LAST_TP_TIME.get(uuid);
        if (last == null) return 0;
        long remain = cooldownSeconds * 1000L - (now - last);
        return Math.max(0, remain / 1000);
    }

    public static void record(UUID uuid) {
        LAST_TP_TIME.put(uuid, System.currentTimeMillis());
    }
}