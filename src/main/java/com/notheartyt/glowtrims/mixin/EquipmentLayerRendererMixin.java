package com.notheartyt.glowtrims.mixin;

import com.notheartyt.glowtrims.GlowConfig;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * Makes only the armor-trim layer brighter, by the strength set with /glowtrims.
 *
 * Targets use Fabric intermediary names (remap = false) because Minecraft
 * 1.21.11 has two overloaded renderLayers methods and the Mojang-name refmap
 * could not be generated for them.
 *
 *   method_64077 / method_64078 = EquipmentLayerRenderer#renderLayers overloads
 *   class_11785                 = OrderedSubmitNodeCollector
 *   method_73490                = submitModel(model, state, pose, renderType,
 *                                 light, overlay, tint, sprite, outline, crumbling)
 *
 * Only the trim layer passes a non-null atlas sprite, so that is the only call
 * whose light value (argument 4) is changed. Base armor and glint stay vanilla.
 */
@Mixin(EquipmentLayerRenderer.class)
public abstract class EquipmentLayerRendererMixin {
    @ModifyArgs(
            method = {"method_64077", "method_64078"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/class_11785;method_73490",
                    remap = false
            ),
            remap = false,
            require = 1
    )
    private void glowtrims$makeVanillaTrimGlow(Args args) {
        if (args.get(7) != null) {
            int light = args.get(4);
            args.set(4, GlowConfig.apply(light));
        }
    }
}
