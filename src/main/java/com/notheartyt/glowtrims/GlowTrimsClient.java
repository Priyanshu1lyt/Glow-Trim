package com.notheartyt.glowtrims;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GlowTrimsClient implements ClientModInitializer {
    public static final String MOD_ID = "glowtrims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /** Set by the command; the screen is opened on the next client tick so chat does not close it. */
    private static volatile boolean openGuiNextTick = false;

    @Override
    public void onInitializeClient() {
        GlowConfig.load();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("glow")
                        .then(ClientCommandManager.literal("gui").executes(ctx -> {
                            openGuiNextTick = true;
                            return 1;
                        }))));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openGuiNextTick) {
                openGuiNextTick = false;
                Minecraft.getInstance().setScreen(new GlowScreen());
            }
        });

        LOGGER.info("Glow Trims loaded. Use /glow gui to open the settings.");
    }
}
