package ru.sandapocalypse.sandapocalypse.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import ru.sandapocalypse.sandapocalypse.registry.ModItems;

public class DesertMaster extends WanderingTrader {
    public DesertMaster(EntityType<? extends WanderingTrader> type, Level level) { super(type, level); }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer && !level().isClientSide) {
            if (held.is(Items.EMERALD) && player.isShiftKeyDown() && held.getCount() >= 16) {
                held.shrink(16); serverPlayer.getInventory().add(new ItemStack(ModItems.SAND_WARD.get()));
                serverPlayer.sendSystemMessage(Component.literal("Мастер: песчаный оберег защитит тебя от ударов пустынных чудовищ."));
                return InteractionResult.CONSUME;
            }
            if (held.is(Items.EMERALD) && held.getCount() >= 8) {
                held.shrink(8); serverPlayer.getInventory().add(new ItemStack(ModItems.DESERT_CHARM.get()));
                serverPlayer.sendSystemMessage(Component.literal("Мастер: держи амулет! Он замедляет жажду."));
                return InteractionResult.CONSUME;
            }
            serverPlayer.sendSystemMessage(Component.literal("Мастер пустыни: 8 изумрудов — амулет жажды; 16 — песчаный оберег. Держи изумруды в руке."));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }
}
