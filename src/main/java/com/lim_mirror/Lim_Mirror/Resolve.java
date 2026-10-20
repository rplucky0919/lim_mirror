package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
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

public class Resolve extends Item implements ICurioItem {

    private static final UUID MAX_HEALTH_MODIFIER_UUID = UUID.fromString("b1c2d3e4-f5a6-7890-bcde-f12345678901");

    public Resolve(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (!(living instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        player.getPersistentData().putBoolean("hasResolve", true);

        boolean isActive = player.getPersistentData().getBoolean("resolveActive");

        // ===== 常驻效果：手持剑类时 =====
        ItemStack mainHand = player.getMainHandItem();
        boolean isHoldingSword = mainHand.getItem() instanceof net.minecraft.world.item.SwordItem;

        if (isHoldingSword) {
            // 迅捷 II
            MobEffectInstance speed = player.getEffect(MobEffects.MOVEMENT_SPEED);
            if (speed == null || speed.getAmplifier() < 1 || speed.getDuration() < 40) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SPEED,
                        60,
                        1,
                        false,
                        false
                ));
            }
        }

        // ===== 开启后效果：体力上限扣除 =====
        if (isActive) {
            applyMaxHealthModifier(player);
        } else {
            removeMaxHealthModifier(player);
        }
    }

    private void applyMaxHealthModifier(Player player) {
        double maxHealth = player.getMaxHealth();
        AttributeModifier existing = player.getAttribute(Attributes.MAX_HEALTH).getModifier(MAX_HEALTH_MODIFIER_UUID);
        if (existing != null) {
            double expectedReduction = maxHealth * 0.5;
            if (existing.getAmount() == -expectedReduction) {
                return;
            }
            player.getAttribute(Attributes.MAX_HEALTH).removeModifier(MAX_HEALTH_MODIFIER_UUID);
        }

        double reduction = maxHealth * 0.5;
        AttributeModifier modifier = new AttributeModifier(
                MAX_HEALTH_MODIFIER_UUID,
                "resolve_max_health_reduction",
                -reduction,
                AttributeModifier.Operation.ADDITION
        );
        player.getAttribute(Attributes.MAX_HEALTH).addTransientModifier(modifier);
    }

    private void removeMaxHealthModifier(Player player) {
        if (player.getAttribute(Attributes.MAX_HEALTH).getModifier(MAX_HEALTH_MODIFIER_UUID) != null) {
            player.getAttribute(Attributes.MAX_HEALTH).removeModifier(MAX_HEALTH_MODIFIER_UUID);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasResolve", false);
            player.getPersistentData().putBoolean("resolveActive", false);
            removeMaxHealthModifier(player);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.resolve.1", ""));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.resolve.2", "§6=== 常驻效果 ==="));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.resolve.3", "§7手持剑类时："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.resolve.4", "§7  造成伤害 §c+6"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.resolve.5", "§7  获得 §b迅捷 II"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.resolve.6", ""));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.resolve.7", "§6=== 右键开关 ==="));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.resolve.8", "§e右键切换开启/关闭"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.resolve.9", ""));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.resolve.10", "§6=== 开启后额外效果 ==="));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.resolve.11", "§7扣除 §c50% §7体力上限"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.resolve.12", "§7受到伤害 §a-75%"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.resolve.13", "§7受到治疗效果 §c-75%"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.resolve.14", "§7增加 §c6 级 §7斩击伤害强化"));
    }
}