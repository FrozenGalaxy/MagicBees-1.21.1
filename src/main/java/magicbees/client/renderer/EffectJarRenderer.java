package magicbees.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import forestry.api.apiculture.genetics.BeeLifeStage;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.core.genetics.capability.IIndividualHandlerItem;
import magicbees.block.entity.EffectJarBlockEntity;
import magicbees.config.MagicBeesConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class EffectJarRenderer implements BlockEntityRenderer<EffectJarBlockEntity> {
    public EffectJarRenderer(BlockEntityRendererProvider.Context context) {}
    @Override public void render(EffectJarBlockEntity jar, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        if (!MagicBeesConfig.CLIENT.fancyJarRenderer.get() || !Minecraft.useFancyGraphics()) return;
        if (!(IIndividualHandlerItem.getIndividual(jar.getQueenStack()) instanceof IBee bee)) return;
        ItemStack drone = bee.createStack(BeeLifeStage.DRONE);
        if (drone.isEmpty() || jar.getLevel() == null) return;
        double time = jar.getLevel().getGameTime() + partialTick;
        float angle = (float) (time % 360.0D);
        pose.pushPose();
        pose.translate(0.5D, 0.3125D, 0.5D);
        pose.scale(0.625F, 0.625F, 0.625F);
        pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(angle * 3F));
        pose.translate(0D, Math.cos(Math.toRadians(angle)) * 0.1D, 0D);
        Minecraft.getInstance().getItemRenderer().renderStatic(drone, ItemDisplayContext.GROUND, light, overlay, pose, buffers, jar.getLevel(), 0);
        pose.popPose();
    }
}
