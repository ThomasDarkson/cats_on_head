package cats.on.head.interfaces;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.registry.entry.RegistryEntry;

public interface CatEntityVarsInterface {
    void set_COLLAR_COLOR(TrackedData<Integer> data);
    TrackedData<Integer> get_COLLAR_COLOR();

    void set_eatedFish(int f);
    int get_eatedFish();

    void set_HeadDown(boolean down);
    boolean get_HeadDown();

    void set_Variant(RegistryEntry<CatVariant> entry);
}
