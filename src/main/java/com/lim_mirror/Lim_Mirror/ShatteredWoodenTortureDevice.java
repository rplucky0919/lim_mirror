package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ShatteredWoodenTortureDevice extends Item implements ICurioItem {

    private static final int MAX_ARMOR_REDUCTION = 8;
    private static final int ARMOR_REDUCTION_PER_HIT = 2;
    private static final int DEBUFF_DURATION = 20 * 5;

    public ShatteredWoodenTortureDevice(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasShatteredWoodenTortureDevice", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasShatteredWoodenTortureDevice", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§7手持剑类武器时："));
        tooltip.add(Component.literal("§7  命中敌方时降低敌方 §c2 点 §7护甲（最多 §c8 点§7）"));
        tooltip.add(Component.literal("§7  自身每带有 §b1 级 §7缓慢，增加 §c6% §7伤害（上限 §c24%§7）"));
        tooltip.add(Component.literal("§8- 减甲持续 5 秒，每次命中刷新"));
    }
}