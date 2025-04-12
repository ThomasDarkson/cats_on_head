package cats.on.head.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;

import cats.on.head.CatsOnHead;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class LoveOfTheFeline extends StatusEffect {
    public static RegistryEntry<StatusEffect> LOVE_OF_THE_FELINE = Registry.registerReference(Registries.STATUS_EFFECT, CatsOnHead.of("love_of_the_feline"), new LoveOfTheFeline());
    
    EntityAttributeInstance i = null;
    EntityAttributeInstance i1 = null;
    EntityAttributeInstance i2 = null;
    EntityAttributeInstance i3 = null;
    
    public static void initialize() {
	}
	
    protected LoveOfTheFeline() {
		super(StatusEffectCategory.BENEFICIAL, 16753920);
	}

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity player) {
            i = player.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE);
            i1 = player.getAttributeInstance(EntityAttributes.WATER_MOVEMENT_EFFICIENCY);
            i2 = player.getAttributeInstance(EntityAttributes.JUMP_STRENGTH);
            i3 = player.getAttributeInstance(EntityAttributes.LUCK);

            if (amplifier >= 0) {
                Identifier id = CatsOnHead.of("feline_damage_boost");
                if (!i.hasModifier(id))
                    i.overwritePersistentModifier(new EntityAttributeModifier(id, 4d, Operation.ADD_VALUE));
            }
            if (amplifier >= 1) {
                Identifier id = CatsOnHead.of("feline_water_efficiency_boost");
                if (!i1.hasModifier(id))
                    i1.overwritePersistentModifier(new EntityAttributeModifier(id, 1d, Operation.ADD_VALUE));
            }
            if (amplifier >= 2) {
                Identifier id = CatsOnHead.of("feline_jump_boost");
                if (!i2.hasModifier(id))
                    i2.overwritePersistentModifier(new EntityAttributeModifier(id, 0.42d, Operation.ADD_VALUE));
            }
            if (amplifier >= 4) {
                Identifier id = CatsOnHead.of("feline_luck_boost");
                if (!i3.hasModifier(id))
                    i3.overwritePersistentModifier(new EntityAttributeModifier(id, 512d, Operation.ADD_VALUE));
            }
        }
    }

    @Override
    public void onRemoved(AttributeContainer attributeContainer) {
        super.onRemoved(attributeContainer);

        if (i != null)
            i.removeModifier(CatsOnHead.of("feline_damage_boost"));
        if (i1 != null)
            i1.removeModifier(CatsOnHead.of("feline_water_efficiency_boost"));
        if (i2 != null)
            i2.removeModifier(CatsOnHead.of("feline_jump_boost"));
        if (i3 != null)
            i3.removeModifier(CatsOnHead.of("feline_luck_boost"));
    }

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true;
	}
}