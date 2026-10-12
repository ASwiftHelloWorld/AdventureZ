package net.adventurez.entity.render;

import net.adventurez.AdventureMain;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import net.adventurez.entity.VoidShadowEntity;
import net.adventurez.entity.model.VoidShadowModel;
import net.adventurez.entity.render.feature.VoidShadowBlackFeatureRenderer;
import net.adventurez.entity.render.feature.VoidShadowEyesFeatureRenderer;
import net.adventurez.init.RenderInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

@Environment(EnvType.CLIENT)
public class VoidShadowRenderer extends MobRenderer<VoidShadowEntity, VoidShadowModel<VoidShadowEntity>> {
    private static final Identifier TEXTURE = AdventureMain.identifierOf("textures/entity/void_shadow.png");

    public VoidShadowRenderer(EntityRendererProvider.Context context) {
        super(context, new VoidShadowModel<>(context.getPart(RenderInit.VOID_SHADOW_LAYER)), 1.7F);
        this.addFeature(new VoidShadowEyesFeatureRenderer(this));
        this.addFeature(new VoidShadowBlackFeatureRenderer(this));
    }

    @Override
    public void scale(VoidShadowEntity voidShadowEntity, PoseStack matrixStack, float f) {
        matrixStack.scale(9.0F, 9.0F, 9.0F);
    }

    @Override
    public Identifier getTexture(VoidShadowEntity voidShadowEntity) {
        return TEXTURE;
    }

    @Override
    public void render(VoidShadowEntity voidShadowEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        if (voidShadowEntity.ticksSinceDeath > 40) {
            matrixStack.push();
            VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(RenderType.getEntityDecal(TEXTURE));
            this.model.render(matrixStack, vertexConsumer2, i, OverlayTexture.getUv(0.0F, false));

            float m = ((float) voidShadowEntity.ticksSinceDeath + g) / 200.0F;
            matrixStack.translate(0.0F, 8.0F, -2.0F);
            renderDeathAnimation(matrixStack, m, vertexConsumerProvider.getBuffer(RenderType.getDragonRays()));
            renderDeathAnimation(matrixStack, m, vertexConsumerProvider.getBuffer(RenderType.getDragonRaysDepth()));

            matrixStack.pop();
        }
        super.render(voidShadowEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    private static void renderDeathAnimation(PoseStack matrices, float animationProgress, VertexConsumer vertexCOnsumer) {
        matrices.push();
        float f = Math.min(animationProgress > 0.8F ? (animationProgress - 0.8F) / 0.2F : 0.0F, 1.0F);
        int i = ARGB.Argb.fromFloats(1.0F - f, 1.0F, 1.0F, 1.0F);
        RandomSource random = RandomSource.create(432L);
        Vector3f vector3f = new Vector3f();
        Vector3f vector3f2 = new Vector3f();
        Vector3f vector3f3 = new Vector3f();
        Vector3f vector3f4 = new Vector3f();
        Quaternionf quaternionf = new Quaternionf();
        int k = Mth.floor((animationProgress + animationProgress * animationProgress) / 2.0F * 60.0F);

        for (int l = 0; l < k; l++) {
            quaternionf.rotationXYZ(random.nextFloat() * (float) (Math.PI * 2), random.nextFloat() * (float) (Math.PI * 2), random.nextFloat() * (float) (Math.PI * 2)).rotateXYZ(
                    random.nextFloat() * (float) (Math.PI * 2), random.nextFloat() * (float) (Math.PI * 2), random.nextFloat() * (float) (Math.PI * 2) + animationProgress * (float) (Math.PI / 2));
            matrices.multiply(quaternionf);
            float g = random.nextFloat() * 20.0F + 5.0F + f * 10.0F;
            float h = random.nextFloat() * 2.0F + 1.0F + f * 2.0F;
            vector3f2.set(-(float) (Math.sqrt(3.0) / 2.0) * h, g, -0.5F * h);
            vector3f3.set((float) (Math.sqrt(3.0) / 2.0) * h, g, -0.5F * h);
            vector3f4.set(0.0F, g, h);
            PoseStack.Entry entry = matrices.peek();
            vertexCOnsumer.vertex(entry, vector3f).color(i);
            vertexCOnsumer.vertex(entry, vector3f2).color(16777215);
            vertexCOnsumer.vertex(entry, vector3f3).color(16777215);
            vertexCOnsumer.vertex(entry, vector3f).color(i);
            vertexCOnsumer.vertex(entry, vector3f3).color(16777215);
            vertexCOnsumer.vertex(entry, vector3f4).color(16777215);
            vertexCOnsumer.vertex(entry, vector3f).color(i);
            vertexCOnsumer.vertex(entry, vector3f4).color(16777215);
            vertexCOnsumer.vertex(entry, vector3f2).color(16777215);
        }

        matrices.pop();
    }

}
