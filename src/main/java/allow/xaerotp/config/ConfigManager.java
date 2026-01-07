package allow.xaerotp.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE =
            FabricLoader.getInstance().getConfigDir().resolve("xaero_free_tp.json").toFile();

    public static ModConfig CONFIG;

    public static void load() {
        try {
            if (!CONFIG_FILE.exists()) {
                CONFIG = new ModConfig();
                save();
                return;
            }
            CONFIG = GSON.fromJson(new FileReader(CONFIG_FILE), ModConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
            CONFIG = new ModConfig();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(CONFIG, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
