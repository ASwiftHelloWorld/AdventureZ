package net.adventurez.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class AdventureFlyingMob extends Mob {
    protected AdventureFlyingMob(EntityType<? extends AdventureFlyingMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void travel(Vec3 movementInput) {
        if (this.isEffectiveAi()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, movementInput);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.8D));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, movementInput);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
            } else {
                boolean onGround = this.onGround();
                float blockFriction = 1.0F;
                if (onGround) {
                    BlockPos movementPos = this.getBlockPosBelowThatAffectsMyMovement();
                    BlockState movementState = this.level().getBlockState(movementPos);
                    blockFriction = movementState.getBlock().getFriction();
                }
                float friction = onGround ? blockFriction * 0.91F : 0.91F;
                float groundAcceleration = 0.16277137F / (friction * friction * friction);
                this.moveRelative(onGround ? 0.1F * groundAcceleration : 0.02F, movementInput);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(friction));
            }
        }
        this.calculateEntityAnimation(false);
    }

    @Override
    protected void checkFallDamage(double distance, boolean landed, BlockState state, BlockPos pos) {
    }

    public boolean isClimbing() {
        return false;
    }
}