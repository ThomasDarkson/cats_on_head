package cats.on.head.mixin;

import java.util.List;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import cats.on.head.CatsOnHead;
import cats.on.head.effects.FatalPoisonStatusEffect;
import cats.on.head.effects.LoveOfTheCat;
import cats.on.head.goals.ActivePlayerTargetGoal;
import cats.on.head.goals.SleepAndGiveGiftsToPlayerGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {
    @Final
	@Mutable
    @Shadow
    private GoalSelector goalSelector;

    @Final
	@Mutable
	@Shadow
	private GoalSelector targetSelector;

    @SuppressWarnings({"unchecked", "rawtypes" })
    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(CallbackInfo info) {
        MobEntity mob = (MobEntity) (Object) this;
        if ((mob instanceof CreeperEntity creeper)) {
            this.targetSelector.clear((goal) -> {
                return goal.getClass() == ActiveTargetGoal.class;
            });
            this.targetSelector.add(1, new ActivePlayerTargetGoal(mob, PlayerEntity.class, true));

            Predicate<LivingEntity> predicate = (entity) -> {
                if (entity instanceof PlayerEntity player) {
                    return CatsOnHead.hasCat(player);
                }
                return false;
            };
            goalSelector.add(1, new FleeEntityGoal(creeper, PlayerEntity.class, predicate, 10.0F, 1.2, 1.5, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
        }
        else if (mob instanceof CatEntity cat) {
            cat.clearGoals((goal) -> {
                return goal.getClass() == FollowOwnerGoal.class || goal.getClass().getName().equals("net.minecraft.class_1451$class_3699");
            });
            goalSelector.add(2, new FollowOwnerGoal(cat, 1.4d, 5F, 2.5F));
            goalSelector.add(3, new SleepAndGiveGiftsToPlayerGoal(cat));
        }
    }

    @Inject(method = "interactWithItem", at = @At("HEAD"), cancellable = true)
    private void interactWithItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> info) {
        MobEntity mob = (MobEntity) (Object) this;
        if (mob instanceof HostileEntity hostile) {
            ItemStack stack = player.getStackInHand(hand);
            List<Item> items = List.of(
                Items.COD,
                Items.SALMON,
                Items.COOKED_COD,
                Items.COOKED_SALMON
            );
            boolean isAcceptable = items.contains(stack.getItem());
            if (isAcceptable && stack.getCount() >= 2) {
                if (player.hasStatusEffect(LoveOfTheCat.LOVE_OF_THE_CAT)) {
                    StatusEffectInstance i = player.getStatusEffect(LoveOfTheCat.LOVE_OF_THE_CAT);
                    if (i != null && i.getAmplifier() >= 4) {
                        if (!hostile.hasStatusEffect(FatalPoisonStatusEffect.FATAL_POISON)) {
                            stack.decrementUnlessCreative(2, player);
                            hostile.addStatusEffect(new StatusEffectInstance(FatalPoisonStatusEffect.FATAL_POISON, -1, 0));
                            player.swingHand(hand, true);
                            info.setReturnValue(ActionResult.SUCCESS);
                        }
                        else if (hostile.getStatusEffect(FatalPoisonStatusEffect.FATAL_POISON).getAmplifier() == 0) {
                            stack.decrementUnlessCreative(2, player);
                            hostile.getStatusEffect(FatalPoisonStatusEffect.FATAL_POISON).upgrade(new StatusEffectInstance(FatalPoisonStatusEffect.FATAL_POISON, -1, 1));
                            player.swingHand(hand, true);
                            info.setReturnValue(ActionResult.SUCCESS);
                        }
                    }
                }
            }
        }
    }
}
