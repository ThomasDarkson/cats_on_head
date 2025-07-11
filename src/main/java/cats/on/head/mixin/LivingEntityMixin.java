package cats.on.head.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import cats.on.head.CatsOnHead;
import cats.on.head.effects.LoveOfTheCat;
import cats.on.head.interfaces.CatEntityVarsInterface;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	private static void dropLootCat(LivingEntity entity, ServerWorld world, DamageSource damageSource)
    {
        if ((entity instanceof CatEntity cat)) {
            CatEntity catEntity = cat;
            if (damageSource.getAttacker() instanceof PlayerEntity player && cat.getOwner() == player && !cat.isBaby()) {
                Item item = CatsOnHead.CAT_ITEM;
                int fed = ((CatEntityVarsInterface) cat).get_eatedFish();
                if (item != null)
                {
                    ItemStack stack = new ItemStack(item);
                    stack.set(CatsOnHead.VARIANT, cat.getVariant().getKey().get());
                    stack.set(CatsOnHead.COLLAR_COLOR, "" + catEntity.getCollarColor().getEntityColor());
                    stack.set(CatsOnHead.OWNER_UUID, player.getUuidAsString());
                    stack.set(CatsOnHead.OWNER_NAME, player.getNameForScoreboard());
                    stack.set(CatsOnHead.COLLAR_COLOR_ID, catEntity.getCollarColor().getIndex());
                    stack.set(CatsOnHead.FED_FISH_COUNT, fed);
                    if (cat.hasCustomName())
                        stack.set(CatsOnHead.CUSTOM_NAME, cat.getCustomName().getLiteralString());
                    entity.dropStack(world, stack);
                }
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "modifyAppliedDamage", cancellable = true)
    protected void modifyAppliedDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> info) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof PlayerEntity player) {
            if (player.hasStatusEffect(LoveOfTheCat.LOVE_OF_THE_CAT)) {
                StatusEffectInstance i = player.getStatusEffect(LoveOfTheCat.LOVE_OF_THE_CAT);
                if (i != null) {
                    if (i.getAmplifier() >= 3) {
                        float dmg = info.getReturnValue();
                        info.setReturnValue(Math.max(0, dmg - (dmg * 0.6F)));
                    }
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "damage", cancellable = true)
    protected void damage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (!source.isIn(DamageTypeTags.BYPASSES_EFFECTS)) {
            LivingEntity entity = (LivingEntity) (Object) this;
            if (entity instanceof CatEntity cat) {
                if (cat.isTamed() && source.getAttacker() == cat.getOwner() && CatsOnHead.checkCat(cat)) {
                    dropLootCat(cat, (ServerWorld) cat.getWorld(), source);
                    cat.remove(RemovalReason.DISCARDED);
                    info.cancel();
                }
            }
            else if (entity instanceof PlayerEntity player) {
                if (player.hasStatusEffect(LoveOfTheCat.LOVE_OF_THE_CAT)) {
                    StatusEffectInstance i = player.getStatusEffect(LoveOfTheCat.LOVE_OF_THE_CAT);
                    if (i != null) {
                        if (i.getAmplifier() >= 2) {
                            if (source.isIn(DamageTypeTags.IS_FALL)) {
                                info.setReturnValue(false);
                            }
                        }
                    }
                }
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "sleep")
    public void sleep(BlockPos pos, CallbackInfo info) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof PlayerEntity player) {
            CatsOnHead.removeCatFromPlayer(player);
        }
    }
}