package com.lim_mirror.Lim_Mirror;

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

public class BarrelLiquor extends Item implements ICurioItem {

    public BarrelLiquor(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasBarrelLiquor", true);

            long lastTrigger = player.getPersistentData().getLong("barrelLiquorLastTick");
            long currentTick = player.level().getGameTime();

            if (lastTrigger == 0) {
                player.getPersistentData().putLong("barrelLiquorLastTick", currentTick);
                return;
            }

            if (currentTick - lastTrigger >= 20 * 5) {
                MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());

                if (currentPoise != null) {
                    int amplifier = currentPoise.getAmplifier();
                    int duration = currentPoise.getDuration();

                    // 条件1：呼吸法等级 > 20
                    if (amplifier >= 20) {
                        int newAmplifier = amplifier - 1;
                        int newDuration = duration + 20;
                        player.removeEffect(Registration.POISE.get());
                        player.addEffect(new MobEffectInstance(
                                Registration.POISE.get(),
                                newDuration,
                                newAmplifier,
                                false,
                                false
                        ));
                    }

                    // 条件3：呼吸法不低于20分钟 (1200秒)
                    int durationSeconds = duration / 20;
                    if (durationSeconds >= 20 * 60) {
                        MobEffectInstance currentPoiseAfter = player.getEffect(Registration.POISE.get());
                        int currentAmplifier = 0;
                        int currentDuration = 0;
                        if (currentPoiseAfter != null) {
                            currentAmplifier = currentPoiseAfter.getAmplifier();
                            currentDuration = currentPoiseAfter.getDuration();
                        }
                        int newAmplifier = currentAmplifier + 3;
                        int newDuration = currentDuration + 20 * 3;
                        player.removeEffect(Registration.POISE.get());
                        player.addEffect(new MobEffectInstance(
                                Registration.POISE.get(),
                                newDuration,
                                newAmplifier,
                                false,
                                false
                        ));
                    }

                    // 条件4：呼吸法强度 >= 40
                    MobEffectInstance currentPoiseAfter2 = player.getEffect(Registration.POISE.get());
                    if (currentPoiseAfter2 != null && currentPoiseAfter2.getAmplifier() >= 40) {
                        int newAmplifier = currentPoiseAfter2.getAmplifier() - 15;
                        int currentDuration2 = currentPoiseAfter2.getDuration();
                        player.removeEffect(Registration.POISE.get());
                        player.addEffect(new MobEffectInstance(
                                Registration.POISE.get(),
                                currentDuration2,
                                newAmplifier,
                                false,
                                false
                        ));
                        player.getPersistentData().putBoolean("barrelLiquorDamageBoost", true);
                    }
                }

                // 条件2：有呼吸法时获得迅捷2级
                if (player.getEffect(Registration.POISE.get()) != null) {
                    player.addEffect(new MobEffectInstance(
                            MobEffects.MOVEMENT_SPEED,
                            20 * 5,
                            1,
                            false,
                            false
                    ));
                }

                player.getPersistentData().putLong("barrelLiquorLastTick", currentTick);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasBarrelLiquor", false);
            player.getPersistentData().putBoolean("barrelLiquorDamageBoost", false);
            player.getPersistentData().putLong("barrelLiquorLastTick", 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7每5秒检测一次"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7呼吸法>20级：减1级，+1秒"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7有呼吸法时：迅捷2级，伤害+1"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7呼吸法≥20分钟：+3秒，+3级"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7呼吸法≥40级：减15级，伤害×1.2"));
    }
}