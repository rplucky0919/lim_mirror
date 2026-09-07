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
import java.util.Random;

public class ShatteredGreatsword extends Item implements ICurioItem {

    private static final Random RANDOM = new Random();
    private static final int SLASHING_INTERVAL = 20 * 30;
    private static final int SLASHING_SHORT_DURATION = 20 * 2; // 2 秒
    private static final int SLASHING_LAYER_DURATION = 20 * 30; // 30 秒
    private static final int SHIELD_RESISTANCE_DURATION = 20 * 3;
    private static final int SECOND_REDUCTION_DELAY = 20 * 30;
    private static final int SECOND_REDUCTION_DURATION = 20 * 5;

    public ShatteredGreatsword(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (!(living instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        player.getPersistentData().putBoolean("hasShatteredGreatsword", true);

        long currentTick = player.level().getGameTime();

        // ===== 每30秒触发 =====
        long lastTrigger = player.getPersistentData().getLong("shatteredGreatswordLastTrigger");
        if (currentTick - lastTrigger >= SLASHING_INTERVAL) {
            // 1. 增加2秒1级斩击伤害提升
            MobEffectInstance existing = player.getEffect(Registration.SLASHING_POWER.get());
            int newDuration = SLASHING_SHORT_DURATION;
            int newAmplifier = 0;

            if (existing != null) {
                newDuration = existing.getDuration() + SLASHING_SHORT_DURATION;
                newAmplifier = Math.max(existing.getAmplifier(), 0);
            }

            player.removeEffect(Registration.SLASHING_POWER.get());
            player.addEffect(new MobEffectInstance(
                    Registration.SLASHING_POWER.get(),
                    newDuration,
                    newAmplifier,
                    false,
                    false
            ));

            // 2. 增加1层斩击伤害提升（30秒）
            MobEffectInstance existingLayer = player.getEffect(Registration.SLASHING_POWER.get());
            int layerDuration = SLASHING_LAYER_DURATION;
            int layerAmplifier = 0;

            if (existingLayer != null) {
                layerDuration = existingLayer.getDuration() + SLASHING_LAYER_DURATION;
                layerAmplifier = existingLayer.getAmplifier() + 1;
            }

            player.removeEffect(Registration.SLASHING_POWER.get());
            player.addEffect(new MobEffectInstance(
                    Registration.SLASHING_POWER.get(),
                    layerDuration,
                    layerAmplifier,
                    false,
                    false
            ));

            // 3. 随机获得4~16秒抗性提升I
            int resistanceDuration = 20 * (4 + RANDOM.nextInt(13));
            MobEffectInstance res = player.getEffect(MobEffects.DAMAGE_RESISTANCE);
            if (res != null) {
                resistanceDuration = res.getDuration() + resistanceDuration;
            }
            player.removeEffect(MobEffects.DAMAGE_RESISTANCE);
            player.addEffect(new MobEffectInstance(
                    MobEffects.DAMAGE_RESISTANCE,
                    resistanceDuration,
                    0,
                    false,
                    false
            ));

            player.getPersistentData().putLong("shatteredGreatswordLastTrigger", currentTick);
        }

        // ===== 盾牌防御检测 =====
        boolean isBlocking = player.isBlocking();
        if (isBlocking) {
            // 抗性提升I（持续格挡持续刷新）
            MobEffectInstance res = player.getEffect(MobEffects.DAMAGE_RESISTANCE);
            if (res == null || res.getAmplifier() < 0 || res.getDuration() < 40) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.DAMAGE_RESISTANCE,
                        SHIELD_RESISTANCE_DURATION,
                        0,
                        false,
                        false
                ));
            }

            player.getPersistentData().putBoolean("shatteredGreatswordShieldBlocking", true);
        } else {
            player.getPersistentData().putBoolean("shatteredGreatswordShieldBlocking", false);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasShatteredGreatsword", false);
            player.getPersistentData().putBoolean("shatteredGreatswordShieldBlocking", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§6=== 常驻效果 ==="));
        tooltip.add(Component.literal("§7手持剑类武器时："));
        tooltip.add(Component.literal("§7  造成伤害 §c+6"));
        tooltip.add(Component.literal("§7  造成伤害 §c+30%"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§6=== 周期性效果 ==="));
        tooltip.add(Component.literal("§7每 30 秒："));
        tooltip.add(Component.literal("§7  获得 §c2 秒 §7斩击伤害提升 I"));
        tooltip.add(Component.literal("§7  增加 §c1 层 §7斩击伤害提升（持续 30 秒）"));
        tooltip.add(Component.literal("§7  随机获得 §a4~16 秒 §7抗性提升 I"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§6=== 盾牌防御 ==="));
        tooltip.add(Component.literal("§7使用盾牌防御时："));
        tooltip.add(Component.literal("§7  获得 §a抗性提升 I"));
        tooltip.add(Component.literal("§7  受到伤害 §a-3 点 §7（持续 3 秒）"));
        tooltip.add(Component.literal("§7  若仍受到伤害，30 秒后"));
        tooltip.add(Component.literal("§7  受到伤害再 §a-3 点 §7（持续 5 秒）"));
    }
}