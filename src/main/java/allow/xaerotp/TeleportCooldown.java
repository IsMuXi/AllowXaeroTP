package allow.xaerotp;

import allow.xaerotp.config.ConfigManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public final class TeleportCooldown {

    private static final Map<UUID, Long> LAST_TP_TIME = new HashMap<>();
    private static final Map<UUID, Long> LAST_REQUEST_TIME = new HashMap<>();

    public static boolean shouldIgnore(UUID uuid) {
        long now = System.currentTimeMillis();
        Long last = LAST_REQUEST_TIME.get(uuid);

        LAST_REQUEST_TIME.put(uuid, now);

        return last != null &&
                (now - last) < ConfigManager.CONFIG.debounce_ms;
    }

    public static boolean isOnCooldown(UUID uuid) {
        Long last = LAST_TP_TIME.get(uuid);
        if (last == null) return false;

        return System.currentTimeMillis() - last <
                ConfigManager.CONFIG.cooldown_seconds * 1000L;
    }

    public static long getRemaining(UUID uuid) {
        Long last = LAST_TP_TIME.get(uuid);
        if (last == null) return 0;

        long remainMs =
                ConfigManager.CONFIG.cooldown_seconds * 1000L
                        - (System.currentTimeMillis() - last);

        return Math.max(0, remainMs / 1000);
    }

    public static void record(UUID uuid) {
        LAST_TP_TIME.put(uuid, System.currentTimeMillis());
    }
}