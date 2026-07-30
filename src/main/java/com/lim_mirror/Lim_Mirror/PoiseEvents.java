package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = lim_mirror.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PoiseEvents {

    @SubscribeEvent
    public static void onAttack(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            ItemStack mainHand = player.getMainHandItem();
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
                boolean hasBambooHat = player.getPersistentData().getBoolean("hasBrokenBambooHat");
                boolean isCrit = hasBambooHat || player.getRandom().nextFloat() < critChance;

                if (isCrit) {
                    float baseDamage = event.getAmount();
                    float additiveDamage = 0.0f;

                    if (hasBambooHat) {
                        if (mainHand.getItem() instanceof SwordItem) {
                            baseDamage = baseDamage * 1.5f;
                        }
                    }

                    if (player.getPersistentData().getBoolean("hasPipeBonus")) {
                        additiveDamage += 2.0f;
                    }

                    if (player.getPersistentData().getBoolean("stillWaterNextAttackBoost")) {
                        additiveDamage += 15.0f;
                        player.getPersistentData().putBoolean("stillWaterNextAttackBoost", false);
                    }

                    if (player.getPersistentData().getBoolean("hasDevilsDelight")) {
                        additiveDamage += 2.0f;
                    }

                    if (player.getPersistentData().getBoolean("hasLuckyBag")) {
                        MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                        if (currentPoise != null) {
                            additiveDamage += 9.0f;
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

                    if (player.getPersistentData().getBoolean("hasNostalgia")) {
                        MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                        if (currentPoise != null) {
                            int durationSeconds = currentPoise.getDuration() / 20;
                            if (durationSeconds >= 30 * 60) {
                                additiveDamage += 9.0f;
                            } else if (durationSeconds >= 20 * 60) {
                                additiveDamage += 6.0f;
                            } else if (durationSeconds >= 10 * 60) {
                                additiveDamage += 4.0f;
                            }
                        }
                    }

                    if (player.getPersistentData().getBoolean("hasMemoryOfADay")) {
                        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BREATH_SMOOTH.get(), mainHand);
                        if (enchantLevel > 0) {
                            additiveDamage += 6.0f;
                        }
                    }

                    if (player.getPersistentData().getBoolean("hasReminiscence")) {
                        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BREATH_SMOOTH.get(), mainHand);
                        if (enchantLevel > 0) {
                            additiveDamage += 3.0f;
                        }
                    }

                    if (player.getPersistentData().getBoolean("hasBarrelLiquor")) {
                        MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                        if (currentPoise != null) {
                            additiveDamage += 1.0f;
                        }
                    }

                    if (player.getPersistentData().getBoolean("hasBrokenBlade")) {
                        MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                        if (currentPoise != null) {
                            int poiseLevel = currentPoise.getAmplifier() + 1;
                            float extraDamage = poiseLevel / 2.0f;
                            additiveDamage += extraDamage;
                        }
                    }

                    boolean hasStillWater = player.getPersistentData().getBoolean("hasStillWater");
                    float multiplier = hasBambooHat ? 1.87f : (hasStillWater ? 3.5f : 1.5f);

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

                    if (player.getPersistentData().getBoolean("endOfEvilNextAttackBoost")) {
                        event.setAmount(event.getAmount() + 6.0f);
                        player.getPersistentData().putBoolean("endOfEvilNextAttackBoost", false);
                    }

                    event.setAmount((baseDamage + additiveDamage) * multiplier);

                    if (hasStillWater) {
                        player.getPersistentData().putBoolean("stillWaterNextAttackBoost", true);
                    }

                    if (player.getPersistentData().getBoolean("luckyBagNextAttackBoost")) {
                        event.setAmount(event.getAmount() * 1.5f);
                        player.getPersistentData().putBoolean("luckyBagNextAttackBoost", false);
                    }

                    if (player.getPersistentData().getBoolean("barrelLiquorDamageBoost")) {
                        event.setAmount(event.getAmount() * 1.2f);
                        player.getPersistentData().putBoolean("barrelLiquorDamageBoost", false);
                    }

                    if (player.getPersistentData().getBoolean("hasMoonInWater")) {
                        LivingEntity target = event.getEntity();
                        if (target instanceof LivingEntity) {
                            MobEffectInstance rupture = target.getEffect(Registration.RUPTURE.get());
                            if (rupture != null) {
                                int levelRupture = rupture.getAmplifier() + 1;
                                if (levelRupture >= 20) {
                                    event.setAmount(event.getAmount() * 1.46f);
                                }
                            }
                        }
                    }

                    if (player.getPersistentData().getBoolean("hasHappyPlushie")) {
                        event.setAmount(event.getAmount() * 1.45f);
                    }

                    if (player.getPersistentData().getBoolean("hasGearShard")) {
                        MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                        if (currentPoise != null) {
                            event.setAmount(event.getAmount() * 1.32f);
                        }
                    }

                    if (player.getPersistentData().getBoolean("hasEndOfEvil")) {
                        LivingEntity target = event.getEntity();
                        if (target instanceof LivingEntity && target != player) {
                            MobEffectInstance bleed = target.getEffect(Registration.BLEED.get());
                            if (bleed != null) {
                                event.setAmount(event.getAmount() * 1.1f);
                            }
                        }
                    }

                    if (player.getPersistentData().getLong("commandSanctuaryDamageBoost") > 0) {
                        event.setAmount(event.getAmount() * 1.3f);
                    }

                    if (player.getPersistentData().getBoolean("hasSomeonesGreenBlade")) {
                        MobEffectInstance absorption = player.getEffect(MobEffects.ABSORPTION);
                        if (absorption != null) {
                            event.setAmount(event.getAmount() * 1.1f);
                        }
                    }

                    if (player.getPersistentData().getBoolean("hasRopeCatcher")) {
                        LivingEntity target = event.getEntity();
                        if (target instanceof LivingEntity) {
                            target.addEffect(new MobEffectInstance(
                                    MobEffects.MOVEMENT_SLOWDOWN,
                                    20 * 5,
                                    1,
                                    false,
                                    false
                            ));
                            MobEffectInstance bleed = target.getEffect(Registration.BLEED.get());
                            if (bleed != null) {
                                target.addEffect(new MobEffectInstance(
                                        MobEffects.WEAKNESS,
                                        20 * 5,
                                        1,
                                        false,
                                        false
                                ));
                            }
                        }
                    }

                    if (player.getPersistentData().getBoolean("hasWhaleHeart")) {
                        if (player.getRandom().nextFloat() < 0.5f) {
                            event.setAmount(event.getAmount() * 1.1f);
                        }
                    }

                    if (player.getPersistentData().getBoolean("hasHarpoonGunLeg")) {
                        MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                        if (currentPoise != null) {
                            event.setAmount(event.getAmount() * 1.22f);
                        }
                    }

                    if (player.getPersistentData().getBoolean("hasGasLamp")) {
                        event.setAmount(event.getAmount() * 1.39f);
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

                    if (player.getPersistentData().getBoolean("hasSomeonesGreenBlade")) {
                        MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                        if (currentPoise != null) {
                            int poiseLevel = currentPoise.getAmplifier() + 1;
                            int absorptionLevel = Math.min(poiseLevel / 5, 15);
                            if (absorptionLevel > 0) {
                                player.addEffect(new MobEffectInstance(
                                        MobEffects.ABSORPTION,
                                        20 * 10,
                                        absorptionLevel - 1,
                                        false,
                                        false
                                ));
                            }
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
                } else {
                    if (player.getPersistentData().getBoolean("hasDevilsDelight")) {
                        event.setAmount(event.getAmount() + 2.0f);
                    }

                    if (player.getPersistentData().getBoolean("hasNostalgia")) {
                        MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                        if (currentPoise != null) {
                            int durationSeconds = currentPoise.getDuration() / 20;
                            if (durationSeconds >= 30 * 60) {
                                event.setAmount(event.getAmount() + 9.0f);
                            } else if (durationSeconds >= 20 * 60) {
                                event.setAmount(event.getAmount() + 6.0f);
                            } else if (durationSeconds >= 10 * 60) {
                                event.setAmount(event.getAmount() + 4.0f);
                            }
                        }
                    }

                    if (player.getPersistentData().getBoolean("hasMemoryOfADay")) {
                        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BREATH_SMOOTH.get(), mainHand);
                        if (enchantLevel > 0) {
                            event.setAmount(event.getAmount() + 6.0f);
                        }
                    }

                    if (player.getPersistentData().getBoolean("hasReminiscence")) {
                        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BREATH_SMOOTH.get(), mainHand);
                        if (enchantLevel > 0) {
                            event.setAmount(event.getAmount() + 3.0f);
                        }
                    }

                    if (player.getPersistentData().getBoolean("hasBarrelLiquor")) {
                        MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                        if (currentPoise != null) {
                            event.setAmount(event.getAmount() + 1.0f);
                        }
                    }

                    if (player.getPersistentData().getBoolean("hasSomeonesGreenBlade")) {
                        MobEffectInstance absorption = player.getEffect(MobEffects.ABSORPTION);
                        if (absorption != null) {
                            event.setAmount(event.getAmount() * 1.1f);
                        }
                    }

                    if (player.getPersistentData().getBoolean("hasWhaleHeart")) {
                        if (player.getRandom().nextFloat() < 0.5f) {
                            event.setAmount(event.getAmount() * 1.1f);
                        }
                    }

                    if (player.getPersistentData().getBoolean("hasHarpoonGunLeg")) {
                        MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                        if (currentPoise != null) {
                            event.setAmount(event.getAmount() * 1.22f);
                        }
                    }

                    if (player.getPersistentData().getBoolean("hasGasLamp")) {
                        event.setAmount(event.getAmount() * 1.39f);
                    }

                    if (player.getPersistentData().getBoolean("hasHappyPlushie")) {
                        event.setAmount(event.getAmount() * 1.45f);
                    }

                    if (player.getPersistentData().getBoolean("hasGearShard")) {
                        MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                        if (currentPoise != null) {
                            event.setAmount(event.getAmount() * 1.32f);
                        }
                    }
                }
            }

            // ===== 近身格斗教材：物品栏有无限弓时伤害×1.6（独立于暴击） =====
            if (player.getPersistentData().getBoolean("hasMeleeCombatManual")) {
                boolean hasInfinityBow = false;
                for (ItemStack stack : player.getInventory().items) {
                    if (!stack.isEmpty()) {
                        int infinityLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack);
                        if (infinityLevel > 0) {
                            hasInfinityBow = true;
                            break;
                        }
                    }
                }
                if (hasInfinityBow) {
                    event.setAmount(event.getAmount() * 1.6f);
                }
            }

            // ===== 憋闷的吐息 =====
            if (player.getPersistentData().getBoolean("hasStifledBreath")) {
                MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                if (currentPoise != null) {
                    int durationSeconds = currentPoise.getDuration() / 20;
                    if (durationSeconds >= 50 * 60) {
                        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BREATH_SMOOTH.get(), mainHand);
                        if (enchantLevel > 0) {
                            event.setAmount(event.getAmount() * 2.6f);
                        } else {
                            event.setAmount(event.getAmount() * 1.8f);
                        }
                    }
                }
            }

            // ===== 水中月：攻击破裂目标获得呼吸法 =====
            if (player.getPersistentData().getBoolean("hasMoonInWater")) {
                LivingEntity target = event.getEntity();
                if (target instanceof LivingEntity) {
                    MobEffectInstance rupture = target.getEffect(Registration.RUPTURE.get());
                    if (rupture != null) {
                        int durationSeconds = rupture.getDuration() / 20;
                        if (durationSeconds >= 5) {
                            MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                            int newAmplifier;
                            if (currentPoise != null) {
                                newAmplifier = currentPoise.getAmplifier() + 1;
                            } else {
                                newAmplifier = 0;
                            }
                            int currentDuration = 0;
                            if (currentPoise != null) {
                                currentDuration = currentPoise.getDuration();
                            }
                            int totalDuration = currentDuration + 20;

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

            // ===== 万恶之终结：攻击流血目标获得呼吸法（独立于暴击） =====
            if (player.getPersistentData().getBoolean("hasEndOfEvil")) {
                LivingEntity target = event.getEntity();
                if (target instanceof LivingEntity && target != player) {
                    MobEffectInstance bleed = target.getEffect(Registration.BLEED.get());
                    if (bleed != null) {
                        MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                        int newAmplifier;
                        if (currentPoise != null) {
                            newAmplifier = currentPoise.getAmplifier() + 3;
                        } else {
                            newAmplifier = 0;
                        }
                        int currentDuration = 0;
                        if (currentPoise != null) {
                            currentDuration = currentPoise.getDuration();
                        }
                        int totalDuration = currentDuration + 20 * 2;

                        player.removeEffect(Registration.POISE.get());
                        player.addEffect(new MobEffectInstance(
                                Registration.POISE.get(),
                                totalDuration,
                                newAmplifier,
                                false,
                                false
                        ));

                        player.getPersistentData().putBoolean("endOfEvilNextAttackBoost", true);
                    }
                }
            }

            // ===== 捕绳：攻击缓慢目标获得呼吸法 =====
            if (player.getPersistentData().getBoolean("hasRopeCatcher")) {
                LivingEntity target = event.getEntity();
                if (target instanceof LivingEntity) {
                    MobEffectInstance slow = target.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
                    if (slow != null) {
                        MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                        int newAmplifier;
                        if (currentPoise != null) {
                            newAmplifier = currentPoise.getAmplifier() + 3;
                        } else {
                            newAmplifier = 0;
                        }
                        int currentDuration = 0;
                        if (currentPoise != null) {
                            currentDuration = currentPoise.getDuration();
                        }
                        int totalDuration = currentDuration + 20 * 2;

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

            // ===== 鲸心：攻击流血目标获得呼吸法 =====
            if (player.getPersistentData().getBoolean("hasWhaleHeart")) {
                LivingEntity target = event.getEntity();
                if (target instanceof LivingEntity) {
                    MobEffectInstance bleed = target.getEffect(Registration.BLEED.get());
                    if (bleed != null) {
                        MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                        int newAmplifier;
                        if (currentPoise != null) {
                            newAmplifier = currentPoise.getAmplifier() + 1;
                        } else {
                            newAmplifier = 0;
                        }
                        int currentDuration = 0;
                        if (currentPoise != null) {
                            currentDuration = currentPoise.getDuration();
                        }
                        int totalDuration = currentDuration + 20;

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

            // ===== 近身格斗教材：射出箭时获得呼吸法 =====
            if (player.getPersistentData().getBoolean("hasMeleeCombatManual")) {
                if (event.getSource().getMsgId() != null && event.getSource().getMsgId().equals("arrow")) {
                    MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                    int newAmplifier;
                    if (currentPoise != null) {
                        newAmplifier = currentPoise.getAmplifier() + 3;
                    } else {
                        newAmplifier = 0;
                    }
                    int currentDuration = 0;
                    if (currentPoise != null) {
                        currentDuration = currentPoise.getDuration();
                    }
                    int totalDuration = currentDuration + 20 * 3;

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
            if (player.getPersistentData().getBoolean("hasEndOfEvil")) {
                LivingEntity target = event.getEntity();
                if (target instanceof LivingEntity) {
                    MobEffectInstance bleed = target.getEffect(Registration.BLEED.get());
                    if (bleed != null) {
                        player.addEffect(new MobEffectInstance(
                                MobEffects.DAMAGE_RESISTANCE,
                                20 * 10,
                                0,
                                false,
                                false
                        ));
                    }
                }
            }

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