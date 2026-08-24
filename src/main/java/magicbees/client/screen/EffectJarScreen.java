package magicbees.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import forestry.api.apiculture.ForestryBeeEffects;
import forestry.api.apiculture.ForestryBeeSpecies;
import forestry.api.apiculture.genetics.BeeLifeStage;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.core.genetics.alleles.BeeChromosomes;
import forestry.api.core.genetics.capability.IIndividualHandlerItem;
import forestry.core.platform.gui.GuiForestry;
import forestry.core.platform.gui.ledgers.Ledger;
import forestry.core.platform.gui.ledgers.LedgerManager;
import forestry.core.platform.util.GeneticsUtil;
import forestry.core.platform.util.SpeciesUtil;
import magicbees.MagicBees;
import magicbees.block.entity.EffectJarBlockEntity;
import magicbees.menu.EffectJarMenu;
import magicbees.registry.MagicBeesBlocks;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class EffectJarScreen extends GuiForestry<EffectJarMenu> {
    private static final ResourceLocation BACKGROUND = MagicBees.id("textures/inventory/jarscreen.png");
    private static final int BAR_X = 115;
    private static final int BAR_Y = 20;
    private static final int BAR_WIDTH = 10;
    private static final int BAR_HEIGHT = 40;
    private static final int BAR_U = 178;
    private static final int BAR_V = 2;

    public EffectJarScreen(EffectJarMenu menu, Inventory inventory, Component title) {
        super(BACKGROUND, menu, inventory, title);
        imageWidth = 176;
        imageHeight = 166;
    }

    @Override
    protected void addLedgers() {
        addErrorLedger(menu.jar());
        addClimateLedger(menu.jar());
        ledgerManager.add(new EffectJarInfoLedger(ledgerManager));
        ledgerManager.add(new ContainedBeeLedger(ledgerManager, this));
    }

    @Override
    protected void drawBackground(GuiGraphics graphics) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        RenderSystem.disableBlend();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(graphics, partialTick, mouseX, mouseY);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int filled = healthFill();
        if (filled > 0) {
            int color = menu.beeColour() | 0xFF000000;
            graphics.setColor(((color >> 16) & 255) / 255F, ((color >> 8) & 255) / 255F, (color & 255) / 255F, 1F);
            graphics.blit(BACKGROUND, leftPos + BAR_X, topPos + BAR_Y + BAR_HEIGHT - filled,
                    BAR_U, BAR_V + BAR_HEIGHT - filled, BAR_WIDTH, filled, 256, 256);
            graphics.setColor(1F, 1F, 1F, 1F);
        }
        RenderSystem.disableBlend();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        if (isHovering(BAR_X, BAR_Y, BAR_WIDTH, BAR_HEIGHT, mouseX, mouseY)) {
            List<Component> tooltip = displayBee() == null
                    ? List.of(Component.translatable("gui.magicbees.effectjar.empty"))
                    : List.of(
                            Component.translatable("gui.magicbees.effectjar.health_bar.value", clampedHealth()),
                            Component.translatable("gui.magicbees.effectjar.health_bar.time_left", formatTicks(menu.ticksUntilDeath()))
                    );
            graphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
        }
    }

    private int clampedHealth() {
        return Math.max(0, Math.min(100, menu.beeHealth()));
    }

    private int healthFill() {
        IBee bee = displayBee();
        if (bee == null) return 0;
        int maxHealth = bee.getMaxHealth();
        double healthPercent = clampedHealth();
        if (maxHealth > 0 && menu.isActive()) {
            healthPercent -= EffectJarBlockEntity.AGE_STEP * menu.ageProgress() / maxHealth;
        }
        return Math.max(0, Math.min(BAR_HEIGHT, (int) Math.ceil(healthPercent * BAR_HEIGHT / 100.0D)));
    }

    private IBee displayBee() {
        if (IIndividualHandlerItem.getIndividual(menu.jar().getQueenStack()) instanceof IBee queen) return queen;
        if (IIndividualHandlerItem.getIndividual(menu.jar().getVisibleStack()) instanceof IBee drone) return drone;
        return null;
    }

    private ItemStack displayStack() {
        if (IIndividualHandlerItem.getIndividual(menu.jar().getQueenStack()) instanceof IBee queen) {
            return queen.getSpecies().createStack(queen, BeeLifeStage.QUEEN);
        }
        ItemStack visible = menu.jar().getVisibleStack();
        return visible.isEmpty() ? ItemStack.EMPTY : visible.copyWithCount(1);
    }

    private static ItemStack emptyBeeIcon() {
        var species = SpeciesUtil.getBeeSpecies(ForestryBeeSpecies.FOREST);
        return species == null ? ItemStack.EMPTY : species.createStack(BeeLifeStage.QUEEN);
    }

    private static void drawDimmedEmptyBeeIcon(GuiGraphics graphics, int x, int y) {
        ItemStack emptyIcon = emptyBeeIcon();
        if (!emptyIcon.isEmpty()) {
            graphics.renderItem(emptyIcon, x, y);
        }
        graphics.fill(x, y, x + 16, y + 16, 0xAA808080);
    }

    private static Component effectName(IBee bee) {
        ResourceLocation effect = bee.getGenome().getActiveValue(BeeChromosomes.EFFECT);
        if (ForestryBeeEffects.NONE.equals(effect)) return Component.translatable("gui.magicbees.effectjar.none");
        return GeneticsUtil.getActiveName(bee.getGenome(), BeeChromosomes.EFFECT);
    }

    private static Component formatTicks(int ticks) {
        if (ticks <= 0) return Component.translatable("gui.magicbees.effectjar.none");
        int seconds = (ticks + 19) / 20;
        int minutes = seconds / 60;
        int remainder = seconds % 60;
        if (minutes <= 0) return Component.translatable("gui.magicbees.effectjar.time.seconds", remainder);
        return Component.translatable("gui.magicbees.effectjar.time.minutes_seconds", minutes, remainder);
    }

    private static final class ContainedBeeLedger extends Ledger {
        private final EffectJarScreen screen;

        private ContainedBeeLedger(LedgerManager manager, EffectJarScreen screen) {
            super(manager, "climate");
            this.screen = screen;
            this.maxHeight = 88;
        }

        @Override
        public void draw(GuiGraphics graphics, int y, int x) {
            drawBackground(graphics, y, x);
            ItemStack stack = screen.displayStack();
            if (!stack.isEmpty()) {
                graphics.renderItem(stack, x + 4, y + 4);
            } else {
                drawDimmedEmptyBeeIcon(graphics, x + 4, y + 4);
            }
            if (!isFullyOpened()) return;

            IBee bee = screen.displayBee();
            drawHeader(graphics, Component.translatable("gui.magicbees.effectjar.ledger"), x + 22, y + 8);
            if (bee == null) {
                drawSplitText(graphics, Component.translatable("gui.magicbees.effectjar.empty"), x + 8, y + 28, maxTextWidth);
                return;
            }

            drawSplitText(graphics, Component.translatable("gui.magicbees.effectjar.ledger.species", bee.getSpecies().getDisplayName()), x + 8, y + 24, maxTextWidth);
            drawSplitText(graphics, Component.translatable("gui.magicbees.effectjar.ledger.effect", effectName(bee)), x + 8, y + 38, maxTextWidth);
            drawSplitText(graphics, Component.translatable("gui.magicbees.effectjar.ledger.time_left", formatTicks(screen.menu.ticksUntilDeath())), x + 8, y + 52, maxTextWidth);
            drawSplitText(graphics, Component.translatable(screen.menu.isActive()
                    ? "gui.magicbees.effectjar.status.working_short"
                    : "gui.magicbees.effectjar.status.paused_short"), x + 8, y + 66, maxTextWidth);
        }

        @Override
        public Component getTooltip() {
            IBee bee = screen.displayBee();
            if (bee == null) return Component.translatable("gui.magicbees.effectjar.empty");
            return Component.translatable("gui.magicbees.effectjar.ledger.tooltip", bee.getSpecies().getDisplayName(), effectName(bee));
        }
    }

    private static final class EffectJarInfoLedger extends Ledger {
        private EffectJarInfoLedger(LedgerManager manager) {
            super(manager, "hint");
            this.maxHeight = 76;
        }

        @Override
        public void draw(GuiGraphics graphics, int y, int x) {
            drawBackground(graphics, y, x);
            graphics.renderItem(new ItemStack(MagicBeesBlocks.EFFECT_JAR.get()), x + 4, y + 4);
            if (!isFullyOpened()) return;
            drawHeader(graphics, Component.translatable("gui.magicbees.effectjar.info"), x + 22, y + 8);
            drawSplitText(graphics, Component.translatable("gui.magicbees.effectjar.info.text"), x + 8, y + 24, maxTextWidth);
        }

        @Override
        public Component getTooltip() {
            return Component.translatable("gui.magicbees.effectjar.info.tooltip");
        }
    }
}
