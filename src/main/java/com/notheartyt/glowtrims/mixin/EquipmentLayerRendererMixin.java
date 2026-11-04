package com.notheartyt.glowtrims.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.notheartyt.glowtrims.GlowConfig;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Trim glow.
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
 * Only the trim layer passes a non-null atlas sprite:
 *  - its light is raised toward full brightness (0-100%)
 *  - for 100-200% an extra additive (eyes) copy of the trim is drawn on top
 */
@Mixin(EquipmentLayerRenderer.class)
public abstract class EquipmentLayerRendererMixin {
    @Redirect(
            method = {"method_64077", "method_64078"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/class_11785;method_73490",
                    remap = false
            ),
            remap = false,
            require = 1
    )
    private <S> void glowtrims$trimGlow(
            OrderedSubmitNodeCollector collector,
            Model<? super S> model,
            S state,
            PoseStack poseStack,
            RenderType renderType,
            int light,
            int overlay,
            int tint,
            TextureAtlasSprite sprite,
            int outlineColor,
            ModelFeatureRenderer.CrumblingOverlay crumbling
    ) {
        if (sprite == null) {
            collector.submitModel(model, state, poseStack, renderType, light, overlay, tint, sprite, outlineColor, crumbling);
            return;
        }

        collector.submitModel(model, state, poseStack, renderType, GlowConfig.apply(light), overlay, tint, sprite, outlineColor, crumbling);

        float extra = GlowConfig.extraBoost();
        if (extra > 0.0f) {
            int g = Math.round(255.0f * Math.min(1.0f, extra));
            int glowTint = 0xFF000000 | (g << 16) | (g << 8) | g;
            collector.submitModel(model, state, poseStack, RenderTypes.eyes(GlowConfig.TRIM_ATLAS), 0xF000F0, overlay, glowTint, sprite, outlineColor, null);
        }
    }
}
