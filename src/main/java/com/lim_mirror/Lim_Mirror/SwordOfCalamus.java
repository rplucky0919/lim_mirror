package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class SwordOfCalamus extends Item implements ICurioItem {

    private static final int CHECK_INTERVAL = 20;
    private static final int NO_DAMAGE_THRESHOLD = 20 * 10;
    private static final int SLASHING_DURATION = 20 * 4;
    private static final int POISE_AMPLIFIER = 7;
    private static final int POISE_DURATION = 20 * 2;

    public SwordOfCalamus(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (!(living instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        player.getPersistentData().putBoolean("hasSwordOfCalamus", true);

        long currentTick = player.level().getGameTime();

        if (currentTick % CHECK_INTERVAL != 0) return;

        // ===== 前置条件：手持剑类武器 =====
        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof SwordItem)) {
            player.getPersistentData().putBoolean("swordOfCalamusActive", false);
            return;
        }

        long lastHurtTime = player.getPersistentData().getLong("swordOfCalamusLastHurtTime");
        long lastTriggerTime = player.getPersistentData().getLong("swordOfCalamusLastTrigger");

        if (lastHurtTime == 0) {
            player.getPersistentData().putLong("swordOfCalamusLastHurtTime", currentTick);
            return;
        }

        boolean noDamage = currentTick - lastHurtTime >= NO_DAMAGE_THRESHOLD;
        boolean canTrigger = currentTick - lastTriggerTime >= NO_DAMAGE_THRESHOLD;

        if (noDamage && canTrigger) {
            // ===== 1. 斩击伤害强化：叠加 +1 级，时间增加 4 秒 =====
            MobEffectInstance existingSlashing = player.getEffect(Registration.SLASHING_POWER.get());
            int newSlashingDuration = SLASHING_DURATION;
            int newSlashingAmplifier = 0;

            if (existingSlashing != null) {
                newSlashingDuration = existingSlashing.getDuration() + SLASHING_DURATION;
                newSlashingAmplifier = existingSlashing.getAmplifier() + 1;
            }

            player.removeEffect(Registration.SLASHING_POWER.get());
            player.addEffect(new MobEffectInstance(
                    Registration.SLASHING_POWER.get(),
                    newSlashingDuration,
                    newSlashingAmplifier,
                    false,
                    false
            ));

            // ===== 2. 增加8级呼吸法强度 =====
            MobEffectInstance existingPoise = player.getEffect(Registration.POISE.get());
            int newPoiseDuration = POISE_DURATION;
            int newPoiseAmplifier = POISE_AMPLIFIER;

            if (existingPoise != null) {
                newPoiseDuration = existingPoise.getDuration() + POISE_DURATION;
                newPoiseAmplifier = existingPoise.getAmplifier() + POISE_AMPLIFIER + 1;
            }

            player.removeEffect(Registration.POISE.get());
            player.addEffect(new MobEffectInstance(
                    Registration.POISE.get(),
                    newPoiseDuration,
                    newPoiseAmplifier,
                    false,
                    false
            ));

            player.getPersistentData().putBoolean("swordOfCalamusActive", true);
            player.getPersistentData().putLong("swordOfCalamusActiveEnd", currentTick + 20);
            player.getPersistentData().putLong("swordOfCalamusLastTrigger", currentTick);
        } else {
            player.getPersistentData().putBoolean("swordOfCalamusActive", false);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasSwordOfCalamus", false);
            player.getPersistentData().putBoolean("swordOfCalamusActive", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.sword_of_calamus.1", ""));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.sword_of_calamus.2", "§6=== 触发条件 ==="));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.sword_of_calamus.3", "§7手持剑类武器时"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.sword_of_calamus.4", "§7每 10 秒检测一次"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.sword_of_calamus.5", "§7若过去 10 秒内没有受到伤害："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.sword_of_calamus.6", ""));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.sword_of_calamus.7", "§6=== 触发效果 ==="));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.sword_of_calamus.8", "§7斩击伤害强化 §c+1 级 §7（叠加，持续 4 秒）"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.sword_of_calamus.9", "§7获得 §b8 级 §7呼吸法强度"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.sword_of_calamus.10", "§7造成伤害 §c+20%（持续 1 秒）"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.sword_of_calamus.11", "§8- 每 10 秒最多触发一次"));
    }
}