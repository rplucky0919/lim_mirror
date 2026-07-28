package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class Clover extends Item implements ICurioItem {

    public Clover(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            long lastTrigger = player.getPersistentData().getLong("cloverLastTrigger");
            long currentTime = System.currentTimeMillis();
            boolean canTrigger = currentTime - lastTrigger >= 5000;
            player.getPersistentData().putBoolean("hasClover", canTrigger);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasClover", false);
        }
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<net.minecraft.network.chat.Component> tooltip, @Nonnull TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7当通过呼吸法造成暴击伤害时"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7获得2秒呼吸法和5级呼吸法强度"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7冷却时间5秒"));
    }
}