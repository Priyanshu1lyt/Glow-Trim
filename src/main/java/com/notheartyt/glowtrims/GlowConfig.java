package com.notheartyt.glowtrims;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;

/** Simple on/off + strength (0-100%) setting, saved to config/glowtrims.properties. */
public final class GlowConfig {
    private static volatile boolean enabled = true;
    private static volatile int percent = 100;

    private GlowConfig() {}

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("glowtrims.properties");
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static int getPercent() {
        return percent;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
        save();
    }

    public static void setPercent(int value) {
        percent = Math.max(0, Math.min(100, value));
        save();
    }

    /** Blends the vanilla packed light toward full brightness by the chosen percentage. */
    public static int apply(int originalLight) {
        if (!enabled || percent <= 0) {
            return originalLight;
        }
        if (percent >= 100) {
            return 0xF000F0;
        }
        float t = percent / 100.0f;
        int block = (originalLight >> 4) & 15;
        int sky = (originalLight >> 20) & 15;
        int newBlock = Math.round(block + (15 - block) * t);
        int newSky = Math.round(sky + (15 - sky) * t);
        return (newBlock << 4) | (newSky << 20);
    }

    public static void load() {
        Path path = file();
        if (!Files.exists(path)) {
            save();
            return;
        }
        try (InputStream in = Files.newInputStream(path)) {
            Properties props = new Properties();
            props.load(in);
            enabled = Boolean.parseBoolean(props.getProperty("enabled", "true"));
            int p = Integer.parseInt(props.getProperty("percent", "100").trim());
            percent = Math.max(0, Math.min(100, p));
        } catch (IOException | NumberFormatException e) {
            GlowTrimsClient.LOGGER.warn("Could not read glowtrims.properties, using defaults", e);
            enabled = true;
            percent = 100;
        }
    }

    private static void save() {
        Properties props = new Properties();
        props.setProperty("enabled", Boolean.toString(enabled));
        props.setProperty("percent", Integer.toString(percent));
        try (OutputStream out = Files.newOutputStream(file())) {
            props.store(out, "Glow Trims settings");
        } catch (IOException e) {
            GlowTrimsClient.LOGGER.warn("Could not save glowtrims.properties", e);
        }
    }
}
