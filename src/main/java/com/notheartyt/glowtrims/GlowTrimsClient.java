package com.notheartyt.glowtrims;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GlowTrimsClient implements ClientModInitializer {
    public static final String MOD_ID = "glowtrims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        GlowConfig.load();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("glowtrims")
                        .executes(ctx -> {
                            ctx.getSource().sendFeedback(Component.literal(status()));
                            return 1;
                        })
                        .then(ClientCommandManager.literal("on").executes(ctx -> {
                            GlowConfig.setEnabled(true);
                            ctx.getSource().sendFeedback(Component.literal(status()));
                            return 1;
                        }))
                        .then(ClientCommandManager.literal("off").executes(ctx -> {
                            GlowConfig.setEnabled(false);
                            ctx.getSource().sendFeedback(Component.literal(status()));
                            return 1;
                        }))
                        .then(ClientCommandManager.argument("percent", IntegerArgumentType.integer(0, 100))
                                .executes(ctx -> {
                                    GlowConfig.setPercent(IntegerArgumentType.getInteger(ctx, "percent"));
                                    GlowConfig.setEnabled(true);
                                    ctx.getSource().sendFeedback(Component.literal(status()));
                                    return 1;
                                }))));

        LOGGER.info("Glow Trims loaded: trims render full-bright. Use /glowtrims on|off|<0-100>.");
    }

    private static String status() {
        return "Glow Trims: " + (GlowConfig.isEnabled() ? "ON" : "OFF")
                + " | strength " + GlowConfig.getPercent() + "%"
                + "  (/glowtrims on, /glowtrims off, /glowtrims 50)";
    }
}
