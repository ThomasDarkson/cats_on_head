package cats.on.head.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cats.on.head.CatsOnHead;
import cats.on.head.item.CatItem;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Shadow
    private int pickupDelay;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(CallbackInfo info) {
        ItemEntity item = (ItemEntity) (Object) this;
        ItemStack stack = item.getStack();
        if (stack != null && stack.getItem() instanceof CatItem) {
            item.setNeverDespawn();
        }
    }

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    public void onPlayerCollision(PlayerEntity player, CallbackInfo info) {
        ItemEntity entity = (ItemEntity) (Object) this;
        if (!entity.getWorld().isClient) {
            ItemStack itemStack = entity.getStack();
            Item item = itemStack.getItem();
            if (item instanceof CatItem) {
                String uuid = itemStack.get(CatsOnHead.OWNER_UUID);
                if (uuid != null && !uuid.equals(player.getUuidAsString())) 
                    info.cancel();
            }
        }
    }
}