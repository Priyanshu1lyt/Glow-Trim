package com.notheartyt.glowtrims;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Settings screen opened by /glow gui: dark panel, YT - NotHeartYT title, on/off, strength slider, presets. */
public final class GlowScreen extends Screen {
    private static final int PANEL_W = 240;
    private static final int PANEL_H = 150;

    private Button toggleButton;
    private StrengthSlider slider;

    public GlowScreen() {
        super(Component.literal("Glow Trims"));
    }

    private static Component toggleLabel() {
        boolean on = GlowConfig.isEnabled();
        return Component.literal("Glow Trims: " + (on ? "ON" : "OFF"))
                .withStyle(on ? ChatFormatting.GREEN : ChatFormatting.RED);
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int left = cx - PANEL_W / 2;
        int top = this.height / 2 - PANEL_H / 2;

        // Modern dark panel with a cyan accent border (added first so it draws behind the widgets).
        this.addRenderableOnly((graphics, mouseX, mouseY, delta) -> {
            int right = left + PANEL_W;
            int bottom = top + PANEL_H;
            graphics.fill(left, top, right, bottom, 0xD0101018);
            graphics.fill(left, top, right, top + 3, 0xFF00D4FF);
            graphics.fill(left, bottom - 1, right, bottom, 0xFF00D4FF);
            graphics.fill(left, top, left + 1, bottom, 0xFF00D4FF);
            graphics.fill(right - 1, top, right, bottom, 0xFF00D4FF);
        });

        this.addRenderableWidget(new StringWidget(
                cx - 100, top + 10, 200, 10,
                Component.literal("YT - NotHeartYT").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD),
                this.font).alignCenter());

        this.addRenderableWidget(new StringWidget(
                cx - 100, top + 24, 200, 10,
                Component.literal("Glow Trims Settings").withStyle(ChatFormatting.GRAY),
                this.font).alignCenter());

        int y = top + 42;

        toggleButton = Button.builder(toggleLabel(), button -> {
            GlowConfig.setEnabled(!GlowConfig.isEnabled());
            button.setMessage(toggleLabel());
        }).bounds(cx - 100, y, 200, 20).build();
        this.addRenderableWidget(toggleButton);

        slider = new StrengthSlider(cx - 100, y + 26, 200, 20);
        this.addRenderableWidget(slider);

        int[] presets = {50, 70, 100};
        for (int i = 0; i < presets.length; i++) {
            final int value = presets[i];
            this.addRenderableWidget(Button.builder(Component.literal(value + "%"), button -> {
                slider.setPercent(value);
                GlowConfig.setEnabled(true);
                toggleButton.setMessage(toggleLabel());
            }).bounds(cx - 100 + i * 68, y + 52, 64, 20).build());
        }

        this.addRenderableWidget(Button.builder(Component.literal("Done"), button -> this.onClose())
                .bounds(cx - 100, y + 78, 200, 20).build());
    }

    private static final class StrengthSlider extends AbstractSliderButton {
        StrengthSlider(int x, int y, int width, int height) {
            super(x, y, width, height, Component.empty(), GlowConfig.getPercent() / 100.0);
            this.updateMessage();
        }

        void setPercent(int percent) {
            this.value = Math.max(0, Math.min(100, percent)) / 100.0;
            this.updateMessage();
            this.applyValue();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.literal("Glow Strength: " + (int) Math.round(this.value * 100) + "%"));
        }

        @Override
        protected void applyValue() {
            GlowConfig.setPercent((int) Math.round(this.value * 100));
        }
    }
}
