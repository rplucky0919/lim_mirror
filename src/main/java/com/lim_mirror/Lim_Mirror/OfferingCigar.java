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

public class OfferingCigar extends Item implements ICurioItem {

    public OfferingCigar(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasOfferingCigar", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasOfferingCigar", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7造成伤害×1.1，最终伤害×1.2（独立乘区）"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7造成伤害+12"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7目标震颤-灼热≥20级时，造成伤害+20%"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7消耗弹药后，造成伤害+20%，最终伤害×1.2"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7触发震颤引爆后，下次攻击伤害+6"));
    }
}