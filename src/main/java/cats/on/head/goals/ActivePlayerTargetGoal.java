package cats.on.head.goals;

import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import cats.on.head.CatsOnHead;

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
            start = (start && !CatsOnHead.hasCat(player));
        }
        return start;
    }
}