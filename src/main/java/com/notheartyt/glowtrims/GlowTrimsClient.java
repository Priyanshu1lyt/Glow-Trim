package com.notheartyt.glowtrims;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GlowTrimsClient implements ClientModInitializer {
    public static final String MOD_ID = "glowtrims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Glow Trims loaded: vanilla armor trim colors are preserved and rendered full-bright.");
    }
}
