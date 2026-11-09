package ru.sandapocalypse.sandapocalypse.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.UseAnim;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CanteenItem extends Item {
    private static final String THIRST_KEY = "SandApocalypseThirst";
    private static final int RESTORE_AMOUNT = 35;

    public CanteenItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer player) {
            CompoundTag data = player.getPersistentData();
            int current = data.contains(THIRST_KEY) ? data.getInt(THIRST_KEY) : 100;
            if (current >= 100) {
                player.displayClientMessage(Component.translatable("message.sandapocalypse.not_thirsty"), true);
                return stack;
            }

            data.putInt(THIRST_KEY, Math.min(100, current + RESTORE_AMOUNT));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
                ItemStack emptyBottle = new ItemStack(Items.GLASS_BOTTLE);
                if (!player.getInventory().add(emptyBottle)) {
                    player.drop(emptyBottle, false);
                }
            }
            player.displayClientMessage(Component.translatable("message.sandapocalypse.drink", Math.min(100, current + RESTORE_AMOUNT)), true);
        }
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.sandapocalypse.canteen").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.sandapocalypse.canteen_amount", RESTORE_AMOUNT).withStyle(ChatFormatting.AQUA));
    }
}
