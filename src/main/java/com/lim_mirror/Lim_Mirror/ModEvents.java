package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
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
                int currentDurationPoise = currentEffect.getDuration();
                int amplifier = currentEffect.getAmplifier();

                player.removeEffect(Registration.POISE.get());
                if (currentDurationPoise > 20) {
                    player.addEffect(new MobEffectInstance(Registration.POISE.get(), currentDurationPoise - 20, amplifier, false, false));
                }

                int level = amplifier + 1;
                float critChance = Math.min(level * 0.05f, 1.0f);
                if (player.getRandom().nextFloat() < critChance) {
                    float baseDamage = event.getAmount();
                    float additiveDamage = 0.0f;

                    if (player.getPersistentData().getBoolean("hasPipeBonus")) {
                        additiveDamage += 2.0f;
                    }

                    if (player.getPersistentData().getBoolean("stillWaterNextAttackBoost")) {
                        additiveDamage += 15.0f;
                        player.getPersistentData().putBoolean("stillWaterNextAttackBoost", false);
                    }

                    boolean hasStillWater = player.getPersistentData().getBoolean("hasStillWater");
                    float multiplier = hasStillWater ? 3.5f : 1.5f;

                    if (player.getPersistentData().getBoolean("hasLuckyBag")) {
                        MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                        int newAmplifier;
                        if (currentPoise != null) {
                            newAmplifier = currentPoise.getAmplifier() + 7;
                        } else {
                            newAmplifier = 0;
                        }
                        int currentDurationLucky = 0;
                        if (currentPoise != null) {
                            currentDurationLucky = currentPoise.getDuration();
                        }
                        int totalDuration = currentDurationLucky + 20 * 7;

                        player.removeEffect(Registration.POISE.get());
                        player.addEffect(new MobEffectInstance(
                                Registration.POISE.get(),
                                totalDuration,
                                newAmplifier,
                                false,
                                false
                        ));
                    }

                    event.setAmount((baseDamage + additiveDamage) * multiplier);

                    if (hasStillWater) {
                        player.getPersistentData().putBoolean("stillWaterNextAttackBoost", true);
                    }

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
                            player.getPersistentData().putBoolean("hasClover", false);
                        }
                    }

                    if (player.getPersistentData().getBoolean("hasOldWoodenFigurine")) {
                        LivingEntity target = event.getEntity();
                        if (target instanceof LivingEntity) {
                            MobEffectInstance currentWither = target.getEffect(MobEffects.WITHER);
                            int newAmplifier = 0;
                            int currentDurationWither = 0;
                            if (currentWither != null) {
                                newAmplifier = Math.min(currentWither.getAmplifier() + 1, 4);
                                currentDurationWither = currentWither.getDuration();
                            }
                            int totalDuration = currentDurationWither + 20 * 3;

                            target.removeEffect(MobEffects.WITHER);
                            target.addEffect(new MobEffectInstance(
                                    MobEffects.WITHER,
                                    totalDuration,
                                    newAmplifier,
                                    false,
                                    false
                            ));
                        }
                    }
                }
            }

            if (player.getPersistentData().getBoolean("hasDevilsDelight")) {
                event.setAmount(event.getAmount() + 2.0f);
            }

            if (player.getPersistentData().getBoolean("hasLuckyBag")) {
                MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                if (currentPoise != null) {
                    event.setAmount(event.getAmount() + 9.0f);

                    player.addEffect(new MobEffectInstance(
                            MobEffects.DAMAGE_BOOST,
                            20 * 3,
                            11,
                            false,
                            false
                    ));

                    player.getPersistentData().putBoolean("luckyBagNextAttackBoost", true);
                }
            }

            if (player.getPersistentData().getBoolean("luckyBagNextAttackBoost")) {
                event.setAmount(event.getAmount() * 1.5f);
                player.getPersistentData().putBoolean("luckyBagNextAttackBoost", false);
            }

            if (player.getPersistentData().getBoolean("hasDevilsDelight")) {
                MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                if (currentPoise != null) {
                    event.setAmount(event.getAmount() + 2.0f);
                }
            }

            if (player.getPersistentData().getBoolean("hasNostalgia")) {
                MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                if (currentPoise != null) {
                    int durationSeconds = currentPoise.getDuration() / 20;
                    int extraDamage = 0;
                    if (durationSeconds >= 30 * 60) {
                        extraDamage = 9;
                    } else if (durationSeconds >= 20 * 60) {
                        extraDamage = 6;
                    } else if (durationSeconds >= 10 * 60) {
                        extraDamage = 4;
                    }
                    if (extraDamage > 0) {
                        event.setAmount(event.getAmount() + extraDamage);
                    }
                }
            }

            ItemStack mainHand = player.getMainHandItem();
            int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BREATH_SMOOTH.get(), mainHand);

            if (enchantLevel > 0) {
                MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());

                int addDuration = 20 * 5;
                int extraAmplifier = 0;

                if (player.getPersistentData().getBoolean("hasEndorphinKit")) {
                    LivingEntity target = event.getEntity();
                    if (target instanceof Monster) {
                        addDuration += 20 * 4;
                        extraAmplifier += 4;
                    } else {
                        addDuration += 20 * 3;
                        extraAmplifier += 3;
                    }
                }

                if (player.getPersistentData().getBoolean("hasStoneMound")) {
                    addDuration = addDuration * 2;
                }

                int newAmplifier;
                if (currentPoise != null) {
                    newAmplifier = currentPoise.getAmplifier() + 1 + extraAmplifier;
                } else {
                    newAmplifier = 0 + extraAmplifier;
                }

                int currentDurationBreath = 0;
                if (currentPoise != null) {
                    currentDurationBreath = currentPoise.getDuration();
                }
                int totalDuration = currentDurationBreath + addDuration;

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

                int currentDurationStone = 0;
                if (currentPoise != null) {
                    currentDurationStone = currentPoise.getDuration();
                }
                int totalDuration = currentDurationStone + addDuration;

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