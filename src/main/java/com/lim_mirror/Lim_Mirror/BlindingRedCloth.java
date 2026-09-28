package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
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

public class BlindingRedCloth extends Item implements ICurioItem {

    private static final int COOLDOWN = 20 * 30;
    private static final int MAX_STACKS = 6;

    public BlindingRedCloth(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasBlindingRedCloth", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasBlindingRedCloth", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§7手持剑类武器时："));
        tooltip.add(Component.literal("§7命中敌方时，造成伤害 §c+1 §7（最多 §c6 次§7）"));
        tooltip.add(Component.literal("§8- 冷却 30 秒"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7不死图腾触发时："));
        tooltip.add(Component.literal("§7增加 §c30 秒 §7斩击伤害提升 §cII"));
        tooltip.add(Component.literal("§7造成伤害 §c+20%"));
    }
}