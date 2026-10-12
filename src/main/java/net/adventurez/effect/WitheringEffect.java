package net.adventurez.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.resources.Identifier;

public class WitheringEffect extends MobEffect {

    private static final AttributeModifier WITHERING = new AttributeModifier(Identifier.of("adventurez:withering"), -0.15D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    public WitheringEffect(MobEffectCategory type, int color) {
        super(type, color);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.damage(entity.getDamageSources().wither(), 0.5F);
        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % 80 == 0;
    }

    @Override
    public void onApplied(AttributeMap attributeContainer, int amplifier) {
        super.onApplied(attributeContainer, amplifier);
        AttributeInstance entityAttributeInstance = attributeContainer.getCustomInstance((Attributes.GENERIC_MOVEMENT_SPEED));
        if (entityAttributeInstance != null) {
            entityAttributeInstance.removeModifier(WITHERING);
            entityAttributeInstance.addPersistentModifier(WITHERING);
        }

    }

    @Override
    public void onRemoved(AttributeMap attributeContainer) {
        super.onRemoved(attributeContainer);
        AttributeInstance entityAttributeInstance = attributeContainer.getCustomInstance(Attributes.GENERIC_MOVEMENT_SPEED);
        if (entityAttributeInstance != null) {
            entityAttributeInstance.removeModifier(WITHERING);
        }

    }

}
