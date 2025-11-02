package cats.on.head;

import net.fabricmc.api.ModInitializer;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.serialization.Codec;

import cats.on.head.effects.FatalPoisonStatusEffect;
import cats.on.head.effects.LoveOfTheCat;
import cats.on.head.item.CatItem;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;

public class CatsOnHead implements ModInitializer {
	public static final RegistryKey<CatVariant> NIKO = registerCatVariant("niko");
	public static final RegistryKey<CatVariant> ABIGAIL = registerCatVariant("abigail");

	public static final RegistryKey<LootTable> CAT_MORNING_GIFT_LEVEL_1 = registerLootTable("gameplay/cat_morning_gift_level_1");
	public static final RegistryKey<LootTable> CAT_MORNING_GIFT_LEVEL_2 = registerLootTable("gameplay/cat_morning_gift_level_2");
	public static final RegistryKey<LootTable> CAT_MORNING_GIFT_LEVEL_3 = registerLootTable("gameplay/cat_morning_gift_level_3");
	public static final RegistryKey<LootTable> CAT_MORNING_GIFT_LEVEL_4 = registerLootTable("gameplay/cat_morning_gift_level_4");

	public static final String MOD_ID = "cats_on_head";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Item CAT_ITEM;

	@Override
	public void onInitialize() {
		CAT_ITEM = new CatItem();
		LoveOfTheCat.initialize();
		FatalPoisonStatusEffect.initialize();

		TrinketRendererRegistry.registerRenderer(CAT_ITEM, new CatItemRenderer());
	}

	private static RegistryKey<CatVariant> registerCatVariant(String id) {
        return (RegistryKey.of(RegistryKeys.CAT_VARIANT, of(id)));
    }

    private static RegistryKey<LootTable> registerLootTable(String id) {
        return (RegistryKey.of(RegistryKeys.LOOT_TABLE, of(id)));
    }

	public static Item register(Item item, String id) {
		Identifier itemID = of(id);
		Item registeredItem = Registry.register(Registries.ITEM, itemID, item);
		return registeredItem;
	}

	public static Identifier of(String o)
	{
		return Identifier.of(MOD_ID, o);
	}

	public static String fixCatVariantId(String id) {
		if (id.contains(":"))
			id = id.replace(":", ".");

		return id;
	}

	public static boolean checkCat(CatEntity cat) {
		if (cat.isBaby() || cat.isDead())
			return false;

		return true;
	}

	public static boolean hasCat(PlayerEntity player) {
		return getCatStack(player, 0).getItem() == CAT_ITEM;
	}

	public static ItemStack getCatStack(PlayerEntity player, int slot) {
		if (!TrinketsApi.getTrinketComponent(player).isPresent())
			return ItemStack.EMPTY;

		try {
			ItemStack stack = TrinketsApi.getTrinketComponent(player).get().getInventory().get("head").get("cat").getStack(slot);
			return stack != null ? stack : ItemStack.EMPTY;
		}
		catch (Exception e) {
			return ItemStack.EMPTY;
		}
	}

	public static void removeCatFromPlayer(PlayerEntity player) {
		if (hasCat(player)) {
			((CatItem) getCatStack(player, 0).getItem()).removeFromHead(player.getEntityWorld(), player);
		}
	}

	public static String rgbToHex(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return String.format("#%02X%02X%02X", red, green, blue);
    }

	public static final ComponentType<RegistryKey<CatVariant>> VARIANT = Registry.register(Registries.DATA_COMPONENT_TYPE, of("cat_variant"), ComponentType.<RegistryKey<CatVariant>>builder().codec(RegistryKey.createCodec(RegistryKeys.CAT_VARIANT)).build());
	public static final ComponentType<String> CUSTOM_NAME = Registry.register(Registries.DATA_COMPONENT_TYPE, of("custom_name"), ComponentType.<String>builder().codec(Codec.string(0, Integer.MAX_VALUE)).build());
	public static final ComponentType<String> OWNER_NAME = Registry.register(Registries.DATA_COMPONENT_TYPE, of("owner_name"), ComponentType.<String>builder().codec(Codec.string(0, Integer.MAX_VALUE)).build());
	public static final ComponentType<String> OWNER_UUID = Registry.register(Registries.DATA_COMPONENT_TYPE, of("owner_uuid"), ComponentType.<String>builder().codec(Codec.string(0, Integer.MAX_VALUE)).build());
	public static final ComponentType<String> COLLAR_COLOR = Registry.register(Registries.DATA_COMPONENT_TYPE, of("collar_color"), ComponentType.<String>builder().codec(Codec.string(0, 256)).build());
	public static final ComponentType<Integer> COLLAR_COLOR_ID = Registry.register(Registries.DATA_COMPONENT_TYPE, of("collar_color_id"), ComponentType.<Integer>builder().codec(Codec.intRange(0, Integer.MAX_VALUE)).build());
	public static final ComponentType<Integer> FED_FISH_COUNT = Registry.register(Registries.DATA_COMPONENT_TYPE, of("fed_fish_count"), ComponentType.<Integer>builder().codec(Codec.intRange(0, Integer.MAX_VALUE)).build());
}