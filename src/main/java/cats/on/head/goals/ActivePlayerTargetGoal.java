package cats.on.head.goals;

import cats.on.head.item.CatItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

@SuppressWarnings("rawtypes")
public class ActivePlayerTargetGoal extends ActiveTargetGoal {
    @SuppressWarnings("unchecked")
    public ActivePlayerTargetGoal(MobEntity mob, Class targetClass, boolean checkVisibility) {
        super(mob, targetClass, checkVisibility);
    }

    @Override
    public boolean canStart() {
        boolean start = super.canStart();
        if (this.targetEntity instanceof PlayerEntity player) {
            start = (start && !(player.getEquippedStack(EquipmentSlot.HEAD).getItem() instanceof CatItem));
        }
        return start;
    }
}