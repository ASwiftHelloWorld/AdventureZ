package net.adventurez.entity.render;

import net.adventurez.AdventureMain;
import net.adventurez.entity.model.RockModel;
import net.adventurez.entity.nonliving.ThrownRockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;

@Environment(EnvType.CLIENT)
public class ThrownRockRenderer extends EntityRenderer<ThrownRockEntity> {
    private static final Identifier GOLEM_TEXTURE = AdventureMain.identifierOf("textures/entity/thrown_golem_rock.png");
    private static final Identifier VOID_TEXTURE = AdventureMain.identifierOf("textures/entity/thrown_void_rock.png");
    private final RockModel model = new RockModel(RockModel.getTexturedModelData().createModel());

    public ThrownRockRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public int getBlockLight(ThrownRockEntity ThrownRockEntity, BlockPos blockPos) {
        return ThrownRockEntity.level().getLightLevel(blockPos);
    }

    @Override
    public void render(ThrownRockEntity thrownRockEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {

        matrixStack.push();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(this.getTexture(thrownRockEntity)));
        model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
        matrixStack.scale(1.5F, 1.5F, 1.5F);
        matrixStack.pop();
        super.render(thrownRockEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(ThrownRockEntity thrownRockEntity) {
        if (((ItemSupplier) thrownRockEntity).getStack().getItem() == thrownRockEntity.getDefaultItem()) {
            return GOLEM_TEXTURE;
        } else
            return VOID_TEXTURE;
    }
}
