package com.notheartyt.glowtrims;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Settings screen opened by /glow gui: on/off button, strength slider (0-200%), quick presets. */
public final class GlowScreen extends Screen {
    private Button toggleButton;
    private StrengthSlider slider;

    public GlowScreen() {
        super(Component.literal("Glow Trims"));
    }

    private static Component toggleLabel() {
        return Component.literal("Glow Trims: " + (GlowConfig.isEnabled() ? "ON" : "OFF"));
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y = this.height / 2 - 52;

        toggleButton = Button.builder(toggleLabel(), button -> {
            GlowConfig.setEnabled(!GlowConfig.isEnabled());
            button.setMessage(toggleLabel());
        }).bounds(cx - 100, y, 200, 20).build();
        this.addRenderableWidget(toggleButton);

        slider = new StrengthSlider(cx - 100, y + 28, 200, 20);
        this.addRenderableWidget(slider);

        int[] presets = {50, 70, 100, 150, 200};
        for (int i = 0; i < presets.length; i++) {
            final int value = presets[i];
            this.addRenderableWidget(Button.builder(Component.literal(value + "%"), button -> {
                slider.setPercent(value);
                GlowConfig.setEnabled(true);
                toggleButton.setMessage(toggleLabel());
            }).bounds(cx - 100 + i * 41, y + 56, 38, 20).build());
        }

        this.addRenderableWidget(Button.builder(Component.literal("Done"), button -> this.onClose())
                .bounds(cx - 100, y + 84, 200, 20).build());
    }

    private static final class StrengthSlider extends AbstractSliderButton {
        StrengthSlider(int x, int y, int width, int height) {
            super(x, y, width, height, Component.empty(), GlowConfig.getPercent() / (double) GlowConfig.MAX_PERCENT);
            this.updateMessage();
        }

        void setPercent(int percent) {
            this.value = Math.max(0, Math.min(GlowConfig.MAX_PERCENT, percent)) / (double) GlowConfig.MAX_PERCENT;
            this.updateMessage();
            this.applyValue();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.literal("Glow Strength: " + (int) Math.round(this.value * GlowConfig.MAX_PERCENT) + "%"));
        }

        @Override
        protected void applyValue() {
            GlowConfig.setPercent((int) Math.round(this.value * GlowConfig.MAX_PERCENT));
        }
    }
}
