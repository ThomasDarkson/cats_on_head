package cats.on.head;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.CatVariant;
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

import cats.on.head.effects.LoveOfTheFeline;
import cats.on.head.item.AllBlackCatItem;
import cats.on.head.item.BlackCatItem;
import cats.on.head.item.BritishShortHairCatItem;
import cats.on.head.item.CalicoCatItem;
import cats.on.head.item.CatItem;
import cats.on.head.item.JellieCatItem;
import cats.on.head.item.PersianCatItem;
import cats.on.head.item.RagdollCatItem;
import cats.on.head.item.RedCatItem;
import cats.on.head.item.SiameseCatItem;
import cats.on.head.item.TabbyCatItem;
import cats.on.head.item.WhiteCatItem;

public class CatsOnHead implements ModInitializer {
	public static final CatVariant[] VARIANTS = {
		Registries.CAT_VARIANT.get(CatVariant.ALL_BLACK),
		Registries.CAT_VARIANT.get(CatVariant.BLACK),
		Registries.CAT_VARIANT.get(CatVariant.BRITISH_SHORTHAIR),
		Registries.CAT_VARIANT.get(CatVariant.CALICO),
		Registries.CAT_VARIANT.get(CatVariant.JELLIE),
		Registries.CAT_VARIANT.get(CatVariant.PERSIAN),
		Registries.CAT_VARIANT.get(CatVariant.RAGDOLL),
		Registries.CAT_VARIANT.get(CatVariant.RED),
		Registries.CAT_VARIANT.get(CatVariant.SIAMESE),
		Registries.CAT_VARIANT.get(CatVariant.TABBY),
		Registries.CAT_VARIANT.get(CatVariant.WHITE)
	};

	public static final RegistryKey<LootTable> CAT_MORNING_GIFT_LEVEL_1 = registerLootTable("gameplay/cat_morning_gift_level_1");
	public static final RegistryKey<LootTable> CAT_MORNING_GIFT_LEVEL_2 = registerLootTable("gameplay/cat_morning_gift_level_2");
	public static final RegistryKey<LootTable> CAT_MORNING_GIFT_LEVEL_3 = registerLootTable("gameplay/cat_morning_gift_level_3");
	public static final RegistryKey<LootTable> CAT_MORNING_GIFT_LEVEL_4 = registerLootTable("gameplay/cat_morning_gift_level_4");

	public static final String MOD_ID = "cats_on_head";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Item all_black = null;
	public static Item black = null;
	public static Item british_shorthair = null;
	public static Item calico = null;
	public static Item jellie = null;
	public static Item persian = null;
	public static Item ragdoll = null;
	public static Item red = null;
	public static Item siamese = null;
	public static Item tabby = null;
	public static Item white = null;

	@Override
	public void onInitialize() {
		all_black = new AllBlackCatItem();
		black = new BlackCatItem();
		british_shorthair = new BritishShortHairCatItem();
		calico = new CalicoCatItem();
		jellie = new JellieCatItem();
		persian = new PersianCatItem();
		ragdoll = new RagdollCatItem();
		red = new RedCatItem();
		siamese = new SiameseCatItem();
		tabby = new TabbyCatItem();
		white = new WhiteCatItem();

		LoveOfTheFeline.initialize();
		ServerTickEvents.END_SERVER_TICK.register(server -> {
            server.getPlayerManager().getPlayerList().forEach(player -> {
				ItemStack stack = player.getEquippedStack(EquipmentSlot.HEAD);
				if (stack.getItem() instanceof CatItem) {
					int amplifier = 0;
					if (stack.getComponents().contains(FED_FISH_COUNT))
						amplifier = (int) stack.get(FED_FISH_COUNT) / 64;
					if (amplifier > 4)
						amplifier = 4;

					if (!player.hasStatusEffect(LoveOfTheFeline.LOVE_OF_THE_FELINE)) {
						player.addStatusEffect(new StatusEffectInstance(LoveOfTheFeline.LOVE_OF_THE_FELINE, -1, amplifier, false, false, false));
					}
					else if (player.getStatusEffect(LoveOfTheFeline.LOVE_OF_THE_FELINE).getAmplifier() != amplifier) {
						player.removeStatusEffect(LoveOfTheFeline.LOVE_OF_THE_FELINE);
						player.addStatusEffect(new StatusEffectInstance(LoveOfTheFeline.LOVE_OF_THE_FELINE, -1, amplifier, false, false, false));
					}
				}
				else
					player.removeStatusEffect(LoveOfTheFeline.LOVE_OF_THE_FELINE);
            });
        });
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

	public static boolean checkCat(CatEntity cat) {
		if (cat.isBaby())
			return false;
		if (cat.getClass().getSuperclass() == CatEntity.class)
			return false;
		for (CatVariant catVariant : VARIANTS) {
			if (catVariant == cat.getVariant().value())
				return true;
		}

		return false;
	}

	public static String rgbToHex(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return String.format("#%02X%02X%02X", red, green, blue);
    }

	public static final ComponentType<String> CUSTOM_NAME = Registry.register(Registries.DATA_COMPONENT_TYPE, of("custom_name"), ComponentType.<String>builder().codec(Codec.string(0, Integer.MAX_VALUE)).build());
	public static final ComponentType<String> OWNER_NAME = Registry.register(Registries.DATA_COMPONENT_TYPE, of("owner_name"), ComponentType.<String>builder().codec(Codec.string(0, Integer.MAX_VALUE)).build());
	public static final ComponentType<String> OWNER_UUID = Registry.register(Registries.DATA_COMPONENT_TYPE, of("owner_uuid"), ComponentType.<String>builder().codec(Codec.string(0, Integer.MAX_VALUE)).build());
	public static final ComponentType<String> COLLAR_COLOR = Registry.register(Registries.DATA_COMPONENT_TYPE, of("collar_color"), ComponentType.<String>builder().codec(Codec.string(0, 256)).build());
	public static final ComponentType<Integer> COLLAR_COLOR_ID = Registry.register(Registries.DATA_COMPONENT_TYPE, of("collar_color_id"), ComponentType.<Integer>builder().codec(Codec.intRange(0, Integer.MAX_VALUE)).build());
	public static final ComponentType<Integer> FED_FISH_COUNT = Registry.register(Registries.DATA_COMPONENT_TYPE, of("fed_fish_count"), ComponentType.<Integer>builder().codec(Codec.intRange(0, Integer.MAX_VALUE)).build());
}