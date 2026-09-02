package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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

public class ForTheCommander extends Item implements ICurioItem {

    private static final int SPEED_DURATION = 20 * 60;  // 60 秒

    public ForTheCommander(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasForTheCommander", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasForTheCommander", false);
        }
    }

    /**
     * 消耗弹药时调用，触发迅捷 III
     */
    public static void triggerSpeedBoost(Player player) {
        if (player == null) return;
        if (!player.getPersistentData().getBoolean("hasForTheCommander")) return;

        // 增加 3 级迅捷，可叠加
        MobEffectInstance existing = player.getEffect(MobEffects.MOVEMENT_SPEED);
        int newDuration = SPEED_DURATION;
        int newAmplifier = 2; // 迅捷 III（amplifier=2）

        if (existing != null) {
            // 叠加：时间相加，等级取最大
            newDuration = existing.getDuration() + SPEED_DURATION;
            newAmplifier = Math.max(existing.getAmplifier(), 2);
        }

        player.removeEffect(MobEffects.MOVEMENT_SPEED);
        player.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SPEED,
                newDuration,
                newAmplifier,
                false,
                false,
                true
        ));

        // 记录消耗弹药时间，用于命中判定
        player.getPersistentData().putLong("forTheCommanderLastAmmoConsume", player.level().getGameTime());
        player.getPersistentData().putBoolean("forTheCommanderActive", true);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§7造成伤害 +6"));
        tooltip.add(Component.literal("§7造成伤害 +30%"));
        tooltip.add(Component.literal("§7消耗弹药后：自身增加迅捷III（60秒，可叠加）"));
        tooltip.add(Component.literal("§7弹药消耗后持续时间内，命中时："));
        tooltip.add(Component.literal("§7  - 震颤引爆，扣除基础震颤4秒"));
        tooltip.add(Component.literal("§7  - 若目标有烧伤+震颤：追加（烧伤+震颤）火焰伤害"));
        tooltip.add(Component.literal("§8- 追加伤害不受+6/+30%加成"));
    }
}