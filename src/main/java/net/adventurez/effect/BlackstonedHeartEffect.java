package net.adventurez.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.resources.Identifier;

public class BlackstonedHeartEffect extends MobEffect {

    private static final AttributeModifier BLACKSTONED = new AttributeModifier(Identifier.of("adventurez:blackstoned"), 1.0D, AttributeModifier.Operation.ADD_VALUE);
    private static final AttributeModifier BLACKSTONED_HEALTH = new AttributeModifier(Identifier.of("adventurez:blackstoned_health"), 4.0D, AttributeModifier.Operation.ADD_VALUE);

    public BlackstonedHeartEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onApplied(AttributeMap attributeContainer, int amplifier) {
        super.onApplied(attributeContainer, amplifier);
        AttributeInstance entityAttributeInstance = attributeContainer.getCustomInstance((Attributes.ATTACK_DAMAGE));
        AttributeInstance otherEntityAttributeInstance = attributeContainer.getCustomInstance((Attributes.MAX_HEALTH));
        if (entityAttributeInstance != null && otherEntityAttributeInstance != null) {
            entityAttributeInstance.removeModifier(BLACKSTONED);
            entityAttributeInstance.addPersistentModifier(BLACKSTONED);

            otherEntityAttributeInstance.removeModifier(BLACKSTONED_HEALTH);
            otherEntityAttributeInstance.addPersistentModifier(BLACKSTONED_HEALTH);
        }
    }

    @Override
    public void onRemoved(AttributeMap attributeContainer) {
        super.onRemoved(attributeContainer);
        AttributeInstance entityAttributeInstance = attributeContainer.getCustomInstance(Attributes.ATTACK_DAMAGE);
        AttributeInstance otherEntityAttributeInstance = attributeContainer.getCustomInstance(Attributes.MAX_HEALTH);
        if (entityAttributeInstance != null) {
            entityAttributeInstance.removeModifier(BLACKSTONED);
        }
        if (otherEntityAttributeInstance != null) {
            otherEntityAttributeInstance.removeModifier(BLACKSTONED_HEALTH);
        }
    }

    @Override
    public void onEntityRemoval(LivingEntity entity, int amplifier, RemovalReason reason) {
        super.onEntityRemoval(entity, amplifier, reason);
        if (entity.getHealth() > entity.getMaxHealth()) {
            entity.setHealth(entity.getMaxHealth());
        }
    }

}
