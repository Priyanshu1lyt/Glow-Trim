package com.notheartyt.glowtrims;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

/**
 * On/off + strength setting, saved to config/glowtrims.properties.
 *
 * 0-100%   : trim light is blended toward full brightness (100% = max vanilla light).
 * 100-200% : on top of full brightness, an extra additive glow layer is drawn over the trim.
 */
public final class GlowConfig {
    public static final int MAX_PERCENT = 200;

    /** Texture atlas the vanilla armor trims are stitched into. */
    public static final Identifier TRIM_ATLAS = Identifier.withDefaultNamespace("textures/atlas/armor_trims.png");

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
        percent = Math.max(0, Math.min(MAX_PERCENT, value));
        save();
    }

    /** Blends the vanilla packed light toward full brightness (uses at most the first 100%). */
    public static int apply(int originalLight) {
        int p = Math.min(percent, 100);
        if (!enabled || p <= 0) {
            return originalLight;
        }
        if (p >= 100) {
            return 0xF000F0;
        }
        float t = p / 100.0f;
        int block = (originalLight >> 4) & 15;
        int sky = (originalLight >> 20) & 15;
        int newBlock = Math.round(block + (15 - block) * t);
        int newSky = Math.round(sky + (15 - sky) * t);
        return (newBlock << 4) | (newSky << 20);
    }

    /** 0.0 when no extra glow; up to 1.0 at 200%. */
    public static float extraBoost() {
        if (!enabled || percent <= 100) {
            return 0.0f;
        }
        return (percent - 100) / 100.0f;
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
            percent = Math.max(0, Math.min(MAX_PERCENT, p));
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
