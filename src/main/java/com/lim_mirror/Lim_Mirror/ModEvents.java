package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = lim_mirror.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {

    @SubscribeEvent
    public static void onAttack(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            MobEffectInstance currentEffect = player.getEffect(Registration.POISE.get());

            if (currentEffect != null) {
                int currentDuration = currentEffect.getDuration();
                int amplifier = currentEffect.getAmplifier();

                player.removeEffect(Registration.POISE.get());
                if (currentDuration > 20) {
                    player.addEffect(new MobEffectInstance(Registration.POISE.get(), currentDuration - 20, amplifier, false, false));
                }

                int level = amplifier + 1;
                float critChance = Math.min(level * 0.05f, 1.0f);
                if (player.getRandom().nextFloat() < critChance) {
                    event.setAmount(event.getAmount() * 1.5f);

                    if (player.getPersistentData().getBoolean("hasClover")) {
                        long lastTrigger = player.getPersistentData().getLong("cloverLastTrigger");
                        long currentTime = System.currentTimeMillis();

                        if (currentTime - lastTrigger >= 5000) {
                            MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());

                            int newAmplifier;
                            if (currentPoise != null) {
                                newAmplifier = currentPoise.getAmplifier() + 5;
                            } else {
                                newAmplifier = 0;
                            }

                            int currentDurationClover = 0;
                            if (currentPoise != null) {
                                currentDurationClover = currentPoise.getDuration();
                            }
                            int totalDuration = currentDurationClover + 20 * 2;

                            player.removeEffect(Registration.POISE.get());
                            player.addEffect(new MobEffectInstance(
                                    Registration.POISE.get(),
                                    totalDuration,
                                    newAmplifier,
                                    false,
                                    false
                            ));

                            player.getPersistentData().putLong("cloverLastTrigger", currentTime);
                        }
                    }
                }
            }

            if (player.getPersistentData().getBoolean("hasPipeBonus")) {
                event.setAmount(event.getAmount() + 2.0f);
            }

            ItemStack mainHand = player.getMainHandItem();
            int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BREATH_SMOOTH.get(), mainHand);

            if (enchantLevel > 0) {
                MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());

                int newAmplifier;
                if (currentPoise != null) {
                    newAmplifier = currentPoise.getAmplifier() + 1;
                } else {
                    newAmplifier = 0;
                }

                int addDuration = 20 * 5;

                if (player.getPersistentData().getBoolean("hasStoneMound")) {
                    addDuration = addDuration * 2;
                }

                int currentDuration = 0;
                if (currentPoise != null) {
                    currentDuration = currentPoise.getDuration();
                }
                int totalDuration = currentDuration + addDuration;

                player.removeEffect(Registration.POISE.get());
                player.addEffect(new MobEffectInstance(
                        Registration.POISE.get(),
                        totalDuration,
                        newAmplifier,
                        false,
                        false
                ));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            if (player.getPersistentData().getBoolean("hasStoneMound")) {
                MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());

                int newAmplifier;
                if (currentPoise != null) {
                    newAmplifier = currentPoise.getAmplifier() + 8;
                } else {
                    newAmplifier = 0;
                }

                int addDuration = 20 * 4;

                int currentDuration = 0;
                if (currentPoise != null) {
                    currentDuration = currentPoise.getDuration();
                }
                int totalDuration = currentDuration + addDuration;

                player.removeEffect(Registration.POISE.get());
                player.addEffect(new MobEffectInstance(
                        Registration.POISE.get(),
                        totalDuration,
                        newAmplifier,
                        false,
                        false
                ));
            }
        }
    }
}