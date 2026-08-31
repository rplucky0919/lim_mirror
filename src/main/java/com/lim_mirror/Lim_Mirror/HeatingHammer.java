package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class HeatingHammer extends Item implements ICurioItem {

    public HeatingHammer(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasHeatingHammer", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasHeatingHammer", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7命中带震颤-灼热的敌人时："));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7  使其烧伤强度+1"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7  若震颤-灼热≥15级，额外施加1层烧伤"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7目标每3级烧伤，震颤引爆伤害+1%"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7最高+15%"));
    }
}