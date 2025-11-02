package cats.on.head.item;

import java.util.Objects;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import cats.on.head.CatsOnHead;
import cats.on.head.effects.LoveOfTheCat;
import cats.on.head.interfaces.CatEntityVarsInterface;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Spawner;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.CatVariants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.util.hit.HitResult.Type;

public class CatItem extends TrinketItem {
    public CatItem() {
        super(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, CatsOnHead.of("cat_item")))
            .component(CatsOnHead.FED_FISH_COUNT, 0)
            .maxCount(1));

        CatsOnHead.register(this, "cat_item");
    }

    private static int getAmplifierLevel(ItemStack stack) {
        int amplifier = 0;
		if (stack.getComponents().contains(CatsOnHead.FED_FISH_COUNT))
		amplifier = (int) stack.get(CatsOnHead.FED_FISH_COUNT) / 64;
		if (amplifier > 4)
			amplifier = 4;
        if (amplifier < 0)
            amplifier = 0;

        return amplifier;
    }

    public void giveStatusEffect(LivingEntity player, ItemStack stack) {
		if (!player.hasStatusEffect(LoveOfTheCat.LOVE_OF_THE_CAT)) {
			player.addStatusEffect(new StatusEffectInstance(LoveOfTheCat.LOVE_OF_THE_CAT, -1, getAmplifierLevel(stack), false, false, false));
		}
		updateStatusEffect(player, stack);
    }

    public void updateStatusEffect(LivingEntity player, ItemStack stack) {
        if (player.hasStatusEffect(LoveOfTheCat.LOVE_OF_THE_CAT)) {
            if (player.getStatusEffect(LoveOfTheCat.LOVE_OF_THE_CAT).getAmplifier() != getAmplifierLevel(stack)) {
			    player.removeStatusEffect(LoveOfTheCat.LOVE_OF_THE_CAT);
			    player.addStatusEffect(new StatusEffectInstance(LoveOfTheCat.LOVE_OF_THE_CAT, -1, getAmplifierLevel(stack), false, false, false));
		    }
        }
    }

    @Override
    public RegistryEntry<SoundEvent> getEquipSound(ItemStack stack, SlotReference slot, LivingEntity entity) {
        return RegistryEntry.of(SoundEvents.ENTITY_CAT_AMBIENT);
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity player) {
        if (slot.inventory().getComponent().isEquipped(CatsOnHead.CAT_ITEM))
            giveStatusEffect(player, stack);
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity player) {
        player.removeStatusEffect(LoveOfTheCat.LOVE_OF_THE_CAT);
    }

    @Override
    public Text getName(ItemStack stack) {
        if (stack.getOrDefault(CatsOnHead.VARIANT, CatVariants.CALICO) == CatsOnHead.NIKO) {
            return Text.literal("Niko");
        }
        else if (stack.getOrDefault(CatsOnHead.VARIANT, CatVariants.CALICO) == CatsOnHead.ABIGAIL) {
            return Text.literal("Abigail");
        }

        if (stack.getComponents().contains(CatsOnHead.CUSTOM_NAME))
            return Text.literal(stack.get(CatsOnHead.CUSTOM_NAME)).setStyle(Style.EMPTY.withItalic(true));

        Text text = super.getName(stack);
        if (stack.contains(CatsOnHead.VARIANT)) {
            MutableText mText = Text.translatable("variant." + CatsOnHead.fixCatVariantId(stack.get(CatsOnHead.VARIANT).getValue().toString()));
            mText.append(Text.literal(" "));
            mText.append(text);
            return mText;
        }
        return text;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        int rgb = Formatting.GRAY.getColorValue();
        if (stack.contains(CatsOnHead.OWNER_NAME)) {
            MutableText text = Text.translatable("cats_on_head.translatable.owner_name").append(": ").setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            text.append(Text.literal(stack.get(CatsOnHead.OWNER_NAME)).setStyle(Style.EMPTY.withColor(rgb)));

            textConsumer.accept(text);
        }
        if (stack.contains(CatsOnHead.COLLAR_COLOR)) {
            rgb = Integer.parseInt(stack.get(CatsOnHead.COLLAR_COLOR));
            MutableText text = Text.translatable("cats_on_head.translatable.collar_color").append(": ").setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            text.append(Text.literal(CatsOnHead.rgbToHex(rgb)).setStyle(Style.EMPTY.withColor(rgb)));

            textConsumer.accept(text);
        }
        if (stack.contains(CatsOnHead.FED_FISH_COUNT)) {
            int count = stack.get(CatsOnHead.FED_FISH_COUNT);
            Formatting color = Formatting.GRAY;
            if (count >= 64)
                color = Formatting.GREEN;
            if (count >= 128)
                color = Formatting.AQUA;
            if (count >= 192)
                color = Formatting.DARK_PURPLE;
            if (count >= 256)
                color = Formatting.YELLOW;

            MutableText text = Text.translatable("cats_on_head.translatable.fed_fish_count").append(": ").setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            text.append(Text.literal("" + count).setStyle(Style.EMPTY.withColor(color)));

            textConsumer.accept(text);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (entity instanceof PlayerEntity player) {
            updateStatusEffect(player, stack);
            if (player.getMainHandStack() == stack) {
                if (world.getRandom().nextFloat() < 0.02F) {
                    world.playSound((PlayerEntity) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_CAT_PURR, entity.getSoundCategory());
                }
            }
            else
                try {
                    if (world.getRandom().nextFloat() < 0.0025F) 
                        world.playSound((PlayerEntity) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_CAT_PURR, entity.getSoundCategory());
                }
                catch (Exception e) {
                }
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        } else {
            ItemStack itemStack = context.getStack();
            ComponentMap components = itemStack.getComponents();
            BlockPos blockPos = context.getBlockPos();
            Direction direction = context.getSide();
            BlockState blockState = world.getBlockState(blockPos);
            BlockEntity var8 = world.getBlockEntity(blockPos);
            String ownerUuid = components.get(CatsOnHead.OWNER_UUID);

            @SuppressWarnings("rawtypes")
            EntityType entityType;
            if (var8 instanceof Spawner) {
                return ActionResult.PASS;
            } else if (ownerUuid == null || ownerUuid.equals(context.getPlayer().getUuidAsString())) {
                BlockPos blockPos2;
                if (blockState.getCollisionShape(world, blockPos).isEmpty()) {
                    blockPos2 = blockPos;
                } else {
                    blockPos2 = blockPos.offset(direction);
                }

                entityType = EntityType.CAT;
                Entity entity = entityType.spawnFromItemStack((ServerWorld)world, itemStack, context.getPlayer(), blockPos2, SpawnReason.SPAWN_ITEM_USE, true, !Objects.equals(blockPos, blockPos2) && direction == Direction.UP);
                if (entity != null) {
                    itemStack.decrement(1);
                    world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);

                    if (entity instanceof CatEntity cat) {
                        cat.setOwner(context.getPlayer());
                        cat.setTamed(true, true);
                        if (components.contains(CatsOnHead.VARIANT))
                            ((CatEntityVarsInterface) cat).set_Variant(cat.getRegistryManager().getEntryOrThrow(components.get(CatsOnHead.VARIANT)));
                        cat.setPersistent();
                        if (components.contains(CatsOnHead.FED_FISH_COUNT))
                            ((CatEntityVarsInterface) cat).set_eatedFish(components.get(CatsOnHead.FED_FISH_COUNT));

                        if (components.contains(CatsOnHead.COLLAR_COLOR_ID))
                            cat.getDataTracker().set(((CatEntityVarsInterface) (Object) cat).get_COLLAR_COLOR(), components.get(CatsOnHead.COLLAR_COLOR_ID));

                        if (components.contains(CatsOnHead.CUSTOM_NAME))
                            cat.setCustomName(Text.literal(components.get(CatsOnHead.CUSTOM_NAME)));
                    }
                }

                return ActionResult.SUCCESS;
            }
            else
                return ActionResult.FAIL;
        }
    }

    public ActionResult removeFromHead(World world, PlayerEntity user) {
        ItemStack itemStack = CatsOnHead.getCatStack(user, 0);
        ComponentMap components = itemStack.getComponents();
        BlockHitResult blockHitResult = raycast(world, user, FluidHandling.SOURCE_ONLY);
        String ownerUuid = components.get(CatsOnHead.OWNER_UUID);
        if (blockHitResult.getType() != Type.BLOCK) {
            return ActionResult.PASS;
        } else if (world instanceof ServerWorld && (ownerUuid == null || ownerUuid.equals(user.getUuidAsString()))) {
            ServerWorld serverWorld = (ServerWorld)world;
            BlockPos blockPos = blockHitResult.getBlockPos();
            if (world.canEntityModifyAt(user, blockPos) && user.canPlaceOn(blockPos, blockHitResult.getSide(), itemStack)) {
                EntityType<?> entityType = EntityType.CAT;
                Entity entity = entityType.spawnFromItemStack(serverWorld, itemStack, user, new BlockPos((int) user.getX(), (int)user.getY(), (int)user.getZ()), SpawnReason.TRIGGERED, false, false);
                if (entity == null) {
                    return ActionResult.PASS;
                } else {
                    itemStack.decrement(1);
                    user.incrementStat(Stats.USED.getOrCreateStat(this));
                    world.emitGameEvent(user, GameEvent.ENTITY_PLACE, entity.getEntityPos());

                    if (entity instanceof CatEntity cat) {
                        cat.setOwner(user);
                        cat.setTamed(true, true);
                        if (components.contains(CatsOnHead.VARIANT))
                            ((CatEntityVarsInterface) cat).set_Variant(cat.getRegistryManager().getEntryOrThrow(components.get(CatsOnHead.VARIANT)));
                        cat.setPersistent();
                        if (components.contains(CatsOnHead.FED_FISH_COUNT))
                            ((CatEntityVarsInterface) cat).set_eatedFish(components.get(CatsOnHead.FED_FISH_COUNT));

                        if (components.contains(CatsOnHead.COLLAR_COLOR_ID))
                            cat.getDataTracker().set(((CatEntityVarsInterface) (Object) cat).get_COLLAR_COLOR(), components.get(CatsOnHead.COLLAR_COLOR_ID));

                        if (components.contains(CatsOnHead.CUSTOM_NAME))
                            cat.setCustomName(Text.literal(components.get(CatsOnHead.CUSTOM_NAME)));
                    }
                    return ActionResult.SUCCESS;
                }
            } else {
                return ActionResult.FAIL;
            }
        } else {
            return ActionResult.FAIL;
        }
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, @Nullable Hand hand) {
        ItemStack itemStack = null;
        if (hand != null)
            itemStack = user.getStackInHand(hand);
        else if (CatsOnHead.hasCat(user)) {
            itemStack = CatsOnHead.getCatStack(user, 0);
        }

        ComponentMap components = itemStack.getComponents();
        BlockHitResult blockHitResult = raycast(world, user, FluidHandling.SOURCE_ONLY);
        String ownerUuid = components.get(CatsOnHead.OWNER_UUID);
        if (blockHitResult.getType() != Type.BLOCK) {
            return ActionResult.PASS;
        } else if (world instanceof ServerWorld && (ownerUuid == null || ownerUuid.equals(user.getUuidAsString()))) {
            ServerWorld serverWorld = (ServerWorld)world;
            BlockPos blockPos = blockHitResult.getBlockPos();
            if (!(world.getBlockState(blockPos).getBlock() instanceof FluidBlock)) {
                return ActionResult.PASS;
            } else if (world.canEntityModifyAt(user, blockPos) && user.canPlaceOn(blockPos, blockHitResult.getSide(), itemStack)) {
                EntityType<?> entityType = EntityType.CAT;
                Entity entity = entityType.spawnFromItemStack(serverWorld, itemStack, user, blockPos, SpawnReason.SPAWN_ITEM_USE, false, false);
                if (entity == null) {
                    return ActionResult.PASS;
                } else {
                    itemStack.decrementUnlessCreative(1, user);
                    user.incrementStat(Stats.USED.getOrCreateStat(this));
                    world.emitGameEvent(user, GameEvent.ENTITY_PLACE, entity.getEntityPos());

                    if (entity instanceof CatEntity cat) {
                        cat.setOwner(user);
                        cat.setTamed(true, true);
                        if (components.contains(CatsOnHead.VARIANT))
                            ((CatEntityVarsInterface) cat).set_Variant(cat.getRegistryManager().getEntryOrThrow(components.get(CatsOnHead.VARIANT)));
                        cat.setPersistent();
                        if (components.contains(CatsOnHead.FED_FISH_COUNT))
                            ((CatEntityVarsInterface) cat).set_eatedFish(components.get(CatsOnHead.FED_FISH_COUNT));

                        if (components.contains(CatsOnHead.COLLAR_COLOR_ID))
                            cat.getDataTracker().set(((CatEntityVarsInterface) (Object) cat).get_COLLAR_COLOR(), components.get(CatsOnHead.COLLAR_COLOR_ID));
                            
                        if (components.contains(CatsOnHead.CUSTOM_NAME))
                            cat.setCustomName(Text.literal(components.get(CatsOnHead.CUSTOM_NAME)));
                    }
                    return ActionResult.SUCCESS;
                }
            } else {
                return ActionResult.FAIL;
            }
        } else {
            return ActionResult.FAIL;
        }
    }
}
