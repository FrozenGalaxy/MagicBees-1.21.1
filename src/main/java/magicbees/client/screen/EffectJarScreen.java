package magicbees.client.screen;

import magicbees.MagicBees;
import magicbees.menu.EffectJarMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class EffectJarScreen extends AbstractContainerScreen<EffectJarMenu> {
    private static final ResourceLocation BACKGROUND = MagicBees.id("textures/inventory/jarscreen.png");
    private static final int BAR_HEIGHT = 40;
    public EffectJarScreen(EffectJarMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 166;
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) { }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        int health = Math.max(0, Math.min(100, menu.beeHealth()));
        int filled = health * BAR_HEIGHT / 100;
        if (filled <= 0) return;
        int color = menu.beeColour() | 0xFF000000;
        graphics.setColor(((color >> 16) & 255) / 255F, ((color >> 8) & 255) / 255F, (color & 255) / 255F, 1F);
        graphics.blit(BACKGROUND, leftPos + 117, topPos + 50 - filled, 176, 0, 10, filled, 256, 256);
        graphics.setColor(1F, 1F, 1F, 1F);
    }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
