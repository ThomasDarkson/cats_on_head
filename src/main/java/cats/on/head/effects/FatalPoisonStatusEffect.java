package cats.on.head.effects;

import cats.on.head.CatsOnHead;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;

public class FatalPoisonStatusEffect extends StatusEffect {
    public static RegistryEntry<StatusEffect> FATAL_POISON = Registry.registerReference(Registries.STATUS_EFFECT, CatsOnHead.of("fatal_poison"), new FatalPoisonStatusEffect());
    
    public FatalPoisonStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 5149489);
    }

    public static void initialize() {
	}

    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        if (entity.getMaxHealth() < 618F || entity.getHealth() >= 619F) {
            entity.damage(world, entity.getDamageSources().magic(), 1.0F);
        }

        return true;
    }

    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        int i = 25 >> amplifier;
        if (i > 0)
            return duration % i == 0;
        else
            return true;
    }
}
