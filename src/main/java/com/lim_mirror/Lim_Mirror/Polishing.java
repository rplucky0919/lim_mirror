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

public class Polishing extends Item implements ICurioItem {

    private static final int INTERVAL = 20 * 30;
    private static final int SLASHING_DURATION = 20 * 4;
    private static final int SLASHING_AMPLIFIER = 1;
    private static final int POISE_THRESHOLD = 30;
    private static final int POISE_REDUCE = 10;
    private static final int SLASHING_EXTRA_AMPLIFIER = 2;

    public Polishing(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (!(living instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        player.getPersistentData().putBoolean("hasPolishing", true);

        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof SwordItem)) return;

        long currentTick = player.level().getGameTime();
        long lastTrigger = player.getPersistentData().getLong("polishingLastTrigger");

        if (currentTick - lastTrigger >= INTERVAL) {
            // ===== 1. 增加 4 秒 2 级斩击伤害提升 =====
            MobEffectInstance existingSlashing = player.getEffect(Registration.SLASHING_POWER.get());
            int newDuration = SLASHING_DURATION;
            int newAmplifier = SLASHING_AMPLIFIER;

            if (existingSlashing != null) {
                newDuration = existingSlashing.getDuration() + SLASHING_DURATION;
                newAmplifier = existingSlashing.getAmplifier() + 1;
            }

            player.removeEffect(Registration.SLASHING_POWER.get());
            player.addEffect(new MobEffectInstance(
                    Registration.SLASHING_POWER.get(),
                    newDuration,
                    newAmplifier,
                    false,
                    false
            ));

            // ===== 2. 检测呼吸法强度 > 30 =====
            MobEffectInstance poise = player.getEffect(Registration.POISE.get());
            if (poise != null) {
                int poiseLevel = poise.getAmplifier() + 1;
                if (poiseLevel > POISE_THRESHOLD) {
                    // 减少 10 级呼吸法
                    int newPoiseAmplifier = poise.getAmplifier() - POISE_REDUCE;
                    int poiseDuration = poise.getDuration();

                    player.removeEffect(Registration.POISE.get());
                    if (newPoiseAmplifier >= 0) {
                        player.addEffect(new MobEffectInstance(
                                Registration.POISE.get(),
                                poiseDuration,
                                newPoiseAmplifier,
                                false,
                                false
                        ));
                    }

                    // 获得 2 级斩击伤害提升
                    MobEffectInstance currentSlashing = player.getEffect(Registration.SLASHING_POWER.get());
                    int extraDuration = 20 * 4;
                    int extraAmplifier = SLASHING_EXTRA_AMPLIFIER;

                    if (currentSlashing != null) {
                        extraDuration = currentSlashing.getDuration() + 20 * 4;
                        extraAmplifier = currentSlashing.getAmplifier() + SLASHING_EXTRA_AMPLIFIER;
                    }

                    player.removeEffect(Registration.SLASHING_POWER.get());
                    player.addEffect(new MobEffectInstance(
                            Registration.SLASHING_POWER.get(),
                            extraDuration,
                            extraAmplifier,
                            false,
                            false
                    ));
                }
            }

            player.getPersistentData().putLong("polishingLastTrigger", currentTick);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasPolishing", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§7手持剑类武器时："));
        tooltip.add(Component.literal("§7每 30 秒增加 §c4 秒 §72 级斩击伤害提升"));
        tooltip.add(Component.literal("§7造成伤害 §c+35%"));
        tooltip.add(Component.literal("§7呼吸法暴击时，额外追加 §d（呼吸法强度/2）§7的魔法伤害"));
        tooltip.add(Component.literal("§7每 30 秒检测：若呼吸法强度 > 30"));
        tooltip.add(Component.literal("§7  减少 §c10 级 §7呼吸法，获得 §c2 级 §7斩击伤害提升"));
    }
}