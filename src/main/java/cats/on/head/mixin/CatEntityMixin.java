package cats.on.head.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import cats.on.head.CatsOnHead;
import cats.on.head.interfaces.CatEntityVarsInterface;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.UseRemainderComponent;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

@Mixin(CatEntity.class)
public abstract class CatEntityMixin implements CatEntityVarsInterface {
    @Final
	@Mutable
	@Shadow
    private static TrackedData<Integer> COLLAR_COLOR;

    @Final
	@Mutable
	@Shadow
	private static TrackedData<Boolean> HEAD_DOWN;

    @Shadow
	private void setVariant(RegistryEntry<CatVariant> variant) {

    }

    private int eatedFish = 0;

    @Override
    public void set_Variant(RegistryEntry<CatVariant> entry) {
        this.setVariant(entry);
    }

    @Override
    public void set_HeadDown(boolean down) {
        CatEntity cat = (CatEntity) (Object) this;
        cat.getDataTracker().set(HEAD_DOWN, down);
    }

    @Override
    public boolean get_HeadDown() {
        CatEntity cat = (CatEntity) (Object) this;
        return cat.getDataTracker().get(HEAD_DOWN);
    }

    @Override
    public void set_COLLAR_COLOR(TrackedData<Integer> data) {
        COLLAR_COLOR = data;
    }

    @Override
    public TrackedData<Integer> get_COLLAR_COLOR() {
        return COLLAR_COLOR;
    }

    @Override
    public void set_eatedFish(int f) {
        eatedFish = f;
    }

    @Override
    public int get_eatedFish() {
        return eatedFish;
    }

    @Inject(at = @At("HEAD"), method = "interactMob", cancellable = true)
    public void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> info) {
        CatEntity cat = (CatEntity) (Object) this;
        if (cat.isTamed() && CatsOnHead.checkCat(cat)) {
            if (cat.isOwner(player)) {
                ItemStack itemStack = player.getStackInHand(hand);
                if ((itemStack.isOf(Items.COOKED_COD) || itemStack.isOf(Items.COOKED_SALMON)) && cat.getHealth() == cat.getMaxHealth()) {
                    if (!cat.getWorld().isClient()) {
                        int i = itemStack.getCount();
                        UseRemainderComponent useRemainderComponent = (UseRemainderComponent) itemStack.get(DataComponentTypes.USE_REMAINDER);
                        itemStack.decrementUnlessCreative(1, player);
                        if (useRemainderComponent != null) {
                            boolean var10003 = player.isInCreativeMode();
                            if (player != null) {
                                ItemStack stack = useRemainderComponent.convert(itemStack, i, var10003, player::giveOrDropStack);
                                player.setStackInHand(hand, stack);
                            }
                        }
      
                        FoodComponent foodComponent = (FoodComponent)itemStack.get(DataComponentTypes.FOOD);
                        cat.heal(foodComponent != null ? (float)foodComponent.nutrition() : 1.0F);
                        cat.playSound(SoundEvents.ENTITY_CAT_EAT, 1.0F, 1.0F);
                        eatedFish++;
                    }

                    info.setReturnValue(ActionResult.SUCCESS);
                }
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "writeCustomDataToNbt")
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo info) {
        nbt.putInt("eatedFish", eatedFish);
    }

    @Inject(at = @At("TAIL"), method = "readCustomDataFromNbt")
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo info) {
        eatedFish = nbt.getInt("eatedFish", 0);
    }
}
