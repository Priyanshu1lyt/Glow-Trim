package com.notheartyt.glowtrims.mixin;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Changes only the light value passed to the vanilla armor-trim submit call.
 * The original trim sprite and tint are left untouched, so every material
 * keeps its normal vanilla color while remaining visible in dark scenes.
 *
 * Call order inside renderLayers (1.21.11): base layer (0), layer glint (1),
 * trim (2), trim glint (3). Light is the 5th argument (index 4).
 */
@Mixin(EquipmentLayerRenderer.class)
public abstract class EquipmentLayerRendererMixin {
    @ModifyArg(
            method = "renderLayers",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel",
                    ordinal = 2
            ),
            index = 4,
            require = 1
    )
    private int glowtrims$makeVanillaTrimFullBright(int originalLight) {
        return LightTexture.FULL_BRIGHT;
    }
}
