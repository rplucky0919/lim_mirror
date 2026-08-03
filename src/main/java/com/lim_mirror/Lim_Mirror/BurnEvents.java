package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = lim_mirror.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BurnEvents {

    public static int CURRENT_MAX_LEVELS = 99;
    private static final int INTERVAL_TICKS = 20;
    private static final float DAMAGE_PER_LEVEL = 1.0f;

    // 未孵化的火种：冷却时间 600秒 = 12000 ticks
    private static final long UNHATCHED_SPARKS_COOLDOWN = 20 * 600;
    // 未孵化的火种：锁定持续时间 5秒 = 100 ticks
    private static final long UNHATCHED_SPARKS_DURATION = 20 * 5;

    // 余火：范围30格，间隔30秒 = 600 ticks
    private static final int EMBERS_RADIUS = 30;
    private static final int EMBERS_INTERVAL = 20 * 30;

    public static void updateMaxLevels(Player player) {
        if (player == null) {
            CURRENT_MAX_LEVELS = 99;
            return;
        }
        boolean hasBurningFate = player.getPersistentData().getBoolean("hasBurningFate");
        boolean hasScorchingCopperPipe = player.getPersistentData().getBoolean("hasScorchingCopperPipe");

        if (hasBurningFate && hasScorchingCopperPipe) {
            CURRENT_MAX_LEVELS = 150;
        } else if (hasBurningFate) {
            CURRENT_MAX_LEVELS = 129;
        } else if (hasScorchingCopperPipe) {
            CURRENT_MAX_LEVELS = 120;
        } else {
            CURRENT_MAX_LEVELS = 99;
        }
    }

    private static void applyBurn(LivingEntity target, Player attacker, int addAmplifier, int addDuration) {
        MobEffectInstance currentBurn = target.getEffect(Registration.BURN.get());
        int currentAmplifier = 0;
        int currentDuration = 0;
        if (currentBurn != null) {
            currentAmplifier = currentBurn.getAmplifier() + addAmplifier;
            currentDuration = currentBurn.getDuration();
        }
        int totalDuration = currentDuration + addDuration;

        target.removeEffect(Registration.BURN.get());
        target.addEffect(new MobEffectInstance(
                Registration.BURN.get(),
                totalDuration,
                Math.min(currentAmplifier, CURRENT_MAX_LEVELS - 1),
                false,
                false
        ));
    }

    private static void triggerBurnDamage(LivingEntity target) {
        MobEffectInstance burn = target.getEffect(Registration.BURN.get());
        if (burn == null) return;

        int level = Math.min(burn.getAmplifier() + 1, CURRENT_MAX_LEVELS);
        float damage = level * DAMAGE_PER_LEVEL;

        if (target.getAbsorptionAmount() > 0) {
            float absorption = target.getAbsorptionAmount();
            if (absorption >= damage) {
                target.setAbsorptionAmount(absorption - damage);
            } else {
                target.setAbsorptionAmount(0);
                float remaining = damage - absorption;
                target.hurt(target.damageSources().onFire(), remaining);
            }
        } else {
            target.hurt(target.damageSources().onFire(), damage);
        }
    }

    // ==================== 未孵化的火种 ====================

    /**
     * 优先级最高：检测致死伤害，触发保命效果
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurtUnhatchedSparksTrigger(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        // 检查是否佩戴了未孵化的火种
        if (!player.getPersistentData().getBoolean("hasUnhatchedSparks")) return;

        // 检查冷却
        long lastTrigger = player.getPersistentData().getLong("unhatchedSparksLastTrigger");
        long currentTick = player.level().getGameTime();
        if (currentTick - lastTrigger < UNHATCHED_SPARKS_COOLDOWN) return;

        // 检查是否是致死伤害（伤害 >= 当前生命值）
        float health = player.getHealth();
        float damage = event.getAmount();
        if (damage < health) return;

        // 防止自己伤害自己触发
        if (event.getSource().getEntity() == player) return;

        // ---- 触发保命效果 ----

        // 1. 将生命值设为1点
        player.setHealth(1.0f);

        // 2. 检查是否有烧伤效果（触发时检测）
        boolean hasBurn = player.hasEffect(Registration.BURN.get());

        // 3. 存储状态
        player.getPersistentData().putBoolean("unhatchedSparksActive", true);
        player.getPersistentData().putLong("unhatchedSparksActiveStart", currentTick);
        player.getPersistentData().putLong("unhatchedSparksLastTrigger", currentTick);
        player.getPersistentData().putBoolean("unhatchedSparksHasBurn", hasBurn);

        // 4. 取消原伤害（生命值已设为1点）
        event.setCanceled(true);

        // 5. 提示玩家
        player.displayClientMessage(Component.literal("§6[未孵化的火种] §c体力锁定为1点，持续5秒！"), true);
    }

    /**
     * 锁定期间：每次受伤强制保持1点生命值
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurtUnhatchedSparksLock(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        // 检查是否处于激活状态
        if (!player.getPersistentData().getBoolean("unhatchedSparksActive")) return;

        // 检查是否还在有效期内（5秒）
        long activeStart = player.getPersistentData().getLong("unhatchedSparksActiveStart");
        long currentTick = player.level().getGameTime();
        if (currentTick - activeStart >= UNHATCHED_SPARKS_DURATION) {
            // 已超时，清除激活状态（恢复逻辑在 onLivingTick 中处理）
            return;
        }

        // 强制生命值为1点
        player.setHealth(1.0f);

        // 取消伤害（已经强制设为1点，原伤害不再生效）
        event.setCanceled(true);
    }

    /**
     * 每 Tick 检查：5秒结束后恢复生命值
     */
    @SubscribeEvent
    public static void onLivingTickUnhatchedSparksRecover(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        // 检查是否处于激活状态
        if (!player.getPersistentData().getBoolean("unhatchedSparksActive")) return;

        long activeStart = player.getPersistentData().getLong("unhatchedSparksActiveStart");
        long currentTick = player.level().getGameTime();

        // 检查是否已满5秒
        if (currentTick - activeStart < UNHATCHED_SPARKS_DURATION) return;

        // ---- 5秒结束，恢复生命值 ----

        float maxHealth = player.getMaxHealth();
        boolean hasBurn = player.getPersistentData().getBoolean("unhatchedSparksHasBurn");
        float restorePercent = hasBurn ? 0.4f : 0.2f;
        float restoreAmount = maxHealth * restorePercent;

        // 恢复生命值（不超过上限）
        float newHealth = Math.min(player.getHealth() + restoreAmount, maxHealth);
        player.setHealth(newHealth);

        // 清除激活状态
        player.getPersistentData().putBoolean("unhatchedSparksActive", false);
        player.getPersistentData().putBoolean("unhatchedSparksHasBurn", false);

        // 提示玩家
        String percent = hasBurn ? "40%" : "20%";
        player.displayClientMessage(Component.literal("§6[未孵化的火种] §a恢复" + percent + "体力！"), true);
    }

    // ==================== 余火 ====================

    /**
     * 每30秒对30格内所有敌人延长/施加2秒烧伤
     */
    @SubscribeEvent
    public static void onLivingTickEmbersAOE(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (!player.getPersistentData().getBoolean("hasEmbers")) return;

        long currentTick = player.level().getGameTime();
        long lastTrigger = player.getPersistentData().getLong("embersLastTrigger");

        if (currentTick - lastTrigger < EMBERS_INTERVAL) return;

        AABB aabb = new AABB(player.blockPosition()).inflate(EMBERS_RADIUS);
        List<LivingEntity> targets = player.level().getEntitiesOfClass(
                LivingEntity.class, aabb,
                e -> e != player && e.isAlive()
        );

        for (LivingEntity target : targets) {
            MobEffectInstance currentBurn = target.getEffect(Registration.BURN.get());
            if (currentBurn != null) {
                int currentAmplifier = currentBurn.getAmplifier();
                int newDuration = currentBurn.getDuration() + 40;
                target.removeEffect(Registration.BURN.get());
                target.addEffect(new MobEffectInstance(
                        Registration.BURN.get(),
                        Math.min(newDuration, Integer.MAX_VALUE),
                        Math.min(currentAmplifier, CURRENT_MAX_LEVELS - 1),
                        false,
                        false
                ));
            } else {
                target.addEffect(new MobEffectInstance(
                        Registration.BURN.get(),
                        40,
                        Math.min(0, CURRENT_MAX_LEVELS - 1),
                        false,
                        false
                ));
            }
        }

        player.getPersistentData().putLong("embersLastTrigger", currentTick);
    }

    /**
     * 被火柴之焰攻击5次：减少目标1秒烧伤，额外触发一次烧伤
     */
    @SubscribeEvent
    public static void onLivingHurtEmbersCount(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasEmbers")) return;

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
            if (matchLevel <= 0) return;

            String targetKey = "embersCount_" + target.getStringUUID();
            int count = player.getPersistentData().getInt(targetKey) + 1;

            if (count >= 5) {
                player.getPersistentData().putInt(targetKey, 0);

                MobEffectInstance burn = target.getEffect(Registration.BURN.get());
                if (burn != null) {
                    int newDuration = Math.max(burn.getDuration() - 20, 0);
                    if (newDuration <= 0) {
                        target.removeEffect(Registration.BURN.get());
                    } else {
                        target.removeEffect(Registration.BURN.get());
                        target.addEffect(new MobEffectInstance(
                                Registration.BURN.get(),
                                newDuration,
                                Math.min(burn.getAmplifier(), CURRENT_MAX_LEVELS - 1),
                                false,
                                false
                        ));
                    }
                }

                triggerBurnDamage(target);
            } else {
                player.getPersistentData().putInt(targetKey, count);
            }
        }
    }

    // ==================== 原有烧伤逻辑 ====================

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (!(event.getSource().getEntity() instanceof Player attacker)) return;

        ItemStack weapon = attacker.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
        if (enchantLevel <= 0) return;

        MobEffectInstance currentBurn = target.getEffect(Registration.BURN.get());
        int currentAmplifier = 0;
        int currentDuration = 0;
        int addAmplifier = 1;
        int addDuration = 20;

        // 镇魂：火柴之焰改为+2秒+2层
        if (attacker.getPersistentData().getBoolean("hasRequiem")) {
            addAmplifier = 2;
            addDuration = 40;
        }

        // 点火手套：额外+2秒，+2层
        if (attacker.getPersistentData().getBoolean("hasIgnitionGloves")) {
            addAmplifier += 2;
            addDuration += 40;
        }

        // 重燃火花塞：额外+1秒
        if (attacker.getPersistentData().getBoolean("hasReignitionSparkPlug")) {
            addDuration += 20;
        }

        // 烹饪秘诀书：额外+2层
        if (attacker.getPersistentData().getBoolean("hasCookingSecretsBook")) {
            addAmplifier += 2;
        }

        // 火热多汁琵琶腿：额外+3层
        if (attacker.getPersistentData().getBoolean("hasSpicyDrumstick")) {
            addAmplifier += 3;
        }

        // 熔化的石蜡：额外 + 武器伤害/2 层
        if (attacker.getPersistentData().getBoolean("hasMeltedWax")) {
            float weaponDamage = 0;
            if (weapon.getItem() instanceof SwordItem sword) {
                weaponDamage = sword.getDamage();
            } else if (weapon.getItem() instanceof TieredItem tiered) {
                weaponDamage = tiered.getTier().getAttackDamageBonus();
            }
            if (weaponDamage > 0) {
                addAmplifier += (int)(weaponDamage / 2);
            }
        }

        if (currentBurn != null) {
            currentAmplifier = currentBurn.getAmplifier() + addAmplifier;
            currentDuration = currentBurn.getDuration();
        }
        int totalDuration = currentDuration + addDuration;

        target.removeEffect(Registration.BURN.get());
        target.addEffect(new MobEffectInstance(
                Registration.BURN.get(),
                totalDuration,
                Math.min(currentAmplifier, CURRENT_MAX_LEVELS - 1),
                false,
                false
        ));
    }

    // ==================== 点火手套：伤害+5 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtIgnitionGlovesDamage(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasIgnitionGloves")) return;

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
            if (matchLevel > 0) {
                event.setAmount(event.getAmount() + 5.0f);
            }
        }
    }

    // ==================== 重燃火花塞：伤害+5，最终伤害×1.2，降低护甲2点 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtReignitionSparkPlugDamage(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasReignitionSparkPlug")) return;

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
            if (matchLevel > 0) {
                event.setAmount(event.getAmount() + 5.0f);
                event.setAmount(event.getAmount() * 1.2f);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurtReignitionSparkPlugArmorReduce(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasReignitionSparkPlug")) return;

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
            if (matchLevel > 0) {
                target.getAttribute(Attributes.ARMOR).removeModifier(ReignitionSparkPlug.ARMOR_REDUCE);
                target.getAttribute(Attributes.ARMOR).addTransientModifier(ReignitionSparkPlug.ARMOR_REDUCE);
            }
        }
    }

    // ==================== 其他烧伤事件 ====================

    @SubscribeEvent
    public static void onLivingHurtBurnBoost(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasHellButterflyDream")) return;

            MobEffectInstance currentBurn = target.getEffect(Registration.BURN.get());
            if (currentBurn == null) return;

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);

            int addAmplifier = matchLevel > 0 ? 8 : 5;
            int newAmplifier = currentBurn.getAmplifier() + addAmplifier;
            int currentDuration = currentBurn.getDuration();
            int totalDuration = currentDuration + 20;

            target.removeEffect(Registration.BURN.get());
            target.addEffect(new MobEffectInstance(
                    Registration.BURN.get(),
                    totalDuration,
                    Math.min(newAmplifier, CURRENT_MAX_LEVELS - 1),
                    false,
                    false
            ));
        }
    }

    @SubscribeEvent
    public static void onLivingHurtBlazingFeather(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasBlazingFeather")) return;

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
            if (matchLevel <= 0) return;

            MobEffectInstance currentBurn = target.getEffect(Registration.BURN.get());
            if (currentBurn == null) {
                target.addEffect(new MobEffectInstance(
                        Registration.BURN.get(),
                        20 * 2,
                        Math.min(3, CURRENT_MAX_LEVELS - 1),
                        false,
                        false
                ));
                return;
            }

            int newAmplifier = currentBurn.getAmplifier() + 4;
            int currentDuration = currentBurn.getDuration();
            int totalDuration = currentDuration + 20;

            target.removeEffect(Registration.BURN.get());
            target.addEffect(new MobEffectInstance(
                    Registration.BURN.get(),
                    totalDuration,
                    Math.min(newAmplifier, CURRENT_MAX_LEVELS - 1),
                    false,
                    false
            ));
        }
    }

    @SubscribeEvent
    public static void onLivingHurtSinglePointLogicCircuit(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasSinglePointLogicCircuit")) return;

            ItemStack weapon = player.getMainHandItem();
            int smiteLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SMITE, weapon);
            if (smiteLevel <= 0) return;

            MobEffectInstance currentBurn = target.getEffect(Registration.BURN.get());
            if (currentBurn == null) {
                target.addEffect(new MobEffectInstance(
                        Registration.BURN.get(),
                        20 * 2,
                        Math.min(0, CURRENT_MAX_LEVELS - 1),
                        false,
                        false
                ));
                return;
            }

            int newAmplifier = currentBurn.getAmplifier() + 1;
            int currentDuration = currentBurn.getDuration();
            int totalDuration = currentDuration + 20;

            target.removeEffect(Registration.BURN.get());
            target.addEffect(new MobEffectInstance(
                    Registration.BURN.get(),
                    totalDuration,
                    Math.min(newAmplifier, CURRENT_MAX_LEVELS - 1),
                    false,
                    false
            ));
        }
    }

    @SubscribeEvent
    public static void onLivingHurtBurnDamageBoost(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasSinglePointLogicCircuit")) return;

            MobEffectInstance burn = target.getEffect(Registration.BURN.get());
            if (burn != null) {
                int level = burn.getAmplifier() + 1;
                if (level >= CURRENT_MAX_LEVELS) {
                    event.setAmount(event.getAmount() * 1.1f);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurtScorchedDisc(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasScorchedDisc")) return;

            MobEffectInstance burn = target.getEffect(Registration.BURN.get());
            if (burn == null) return;

            target.getAttribute(Attributes.ARMOR).removeModifier(ScorchedDisc.ARMOR_REDUCE);
            target.getAttribute(Attributes.ARMOR).addTransientModifier(ScorchedDisc.ARMOR_REDUCE);
        }
    }

    @SubscribeEvent
    public static void onLivingHurtRequiem(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasRequiem")) return;

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
            if (matchLevel > 0) {
                event.setAmount(event.getAmount() + 6.0f);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurtPolarizedLight(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasPolarizedLight")) return;

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
            if (matchLevel > 0) {
                event.setAmount(event.getAmount() * 1.2f);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurtSuppressedFire(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasSuppressedFire")) return;

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
            if (matchLevel <= 0) return;

            player.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SPEED,
                    20 * 3,
                    2,
                    false,
                    false
            ));

            MobEffectInstance currentSpeed = player.getEffect(MobEffects.MOVEMENT_SPEED);
            if (currentSpeed != null) {
                event.setAmount(event.getAmount() + 8.0f);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurtFirelightFlower(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasFirelightFlower")) return;

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
            if (matchLevel <= 0) return;

            event.setAmount(event.getAmount() + 3.0f);

            MobEffectInstance burn = target.getEffect(Registration.BURN.get());
            if (burn != null) {
                int burnLevel = burn.getAmplifier() + 1;
                float healthPercent = (target.getHealth() / target.getMaxHealth()) * 100;
                if (healthPercent > burnLevel) {
                    event.setAmount(event.getAmount() * 1.5f);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurtEternalStewpot(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasEternalStewpot")) return;

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
            if (matchLevel > 0) {
                event.setAmount(event.getAmount() + 3.0f);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurtEternalHearthfire(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasEternalHearthfire")) return;

            long lastTrigger = player.getPersistentData().getLong("eternalHearthfireLastTick");
            long currentTick = player.level().getGameTime();
            if (currentTick - lastTrigger < 200) return;

            MobEffectInstance burn = target.getEffect(Registration.BURN.get());
            if (burn == null) return;

            if (burn.getDuration() > 60) {
                triggerBurnDamage(target);
                player.getPersistentData().putLong("eternalHearthfireLastTick", currentTick);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurtCookingSecretsBook(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasCookingSecretsBook")) return;

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
            if (matchLevel > 0) {
                event.setAmount(event.getAmount() * 1.1f);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurtStolenFlame(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasStolenFlame")) return;

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
            if (matchLevel <= 0) return;

            MobEffectInstance burn = target.getEffect(Registration.BURN.get());
            if (burn != null && burn.getAmplifier() + 1 > 3) {
                triggerBurnDamage(target);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTickStolenFlame(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (entity.level().isClientSide()) return;

        if (!player.getPersistentData().getBoolean("hasStolenFlame")) return;

        long lastTrigger = player.getPersistentData().getLong("stolenFlameLastTick");
        long currentTick = entity.level().getGameTime();
        if (currentTick - lastTrigger < 200) return;

        AABB aabb = new AABB(player.blockPosition()).inflate(10.0);
        List<LivingEntity> entities = player.level().getEntitiesOfClass(
                LivingEntity.class, aabb,
                e -> e != player && e.isAlive()
        );

        for (LivingEntity target : entities) {
            MobEffectInstance burn = target.getEffect(Registration.BURN.get());
            if (burn != null && burn.getAmplifier() + 1 > 3) {
                triggerBurnDamage(target);
            }
        }

        player.getPersistentData().putLong("stolenFlameLastTick", currentTick);
    }

    @SubscribeEvent
    public static void onLivingTickStolenFlameWeakness(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (entity.level().isClientSide()) return;

        if (!player.getPersistentData().getBoolean("hasStolenFlame")) return;

        AABB aabb = new AABB(player.blockPosition()).inflate(10.0);
        List<LivingEntity> entities = player.level().getEntitiesOfClass(
                LivingEntity.class, aabb,
                e -> e != player && e.isAlive()
        );

        for (LivingEntity target : entities) {
            MobEffectInstance burn = target.getEffect(Registration.BURN.get());
            if (burn != null) {
                target.addEffect(new MobEffectInstance(
                        MobEffects.WEAKNESS,
                        20 * 2,
                        1,
                        false,
                        false
                ));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtStolenFlameDamage(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            if (player.getPersistentData().getBoolean("hasStolenFlame")) {
                event.setAmount(event.getAmount() * 1.2f);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurtRedTie(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasRedTie")) return;

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
            if (matchLevel > 0) {
                event.setAmount((event.getAmount() + 6.0f) * 1.3f);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurtUniformSixAssociation(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasUniformSixAssociation")) return;

            event.setAmount(event.getAmount() + 8.0f);

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);

            if (matchLevel > 0) {
                event.setAmount(event.getAmount() + 4.0f);
                event.setAmount(event.getAmount() * 1.5f);
            }

            event.setAmount(event.getAmount() * 1.2f);
        }
    }

    @SubscribeEvent
    public static void onLivingHurtRoyalJellyPerfume(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (!player.getPersistentData().getBoolean("hasRoyalJellyPerfume")) return;

        LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity ? (LivingEntity) event.getSource().getEntity() : null;
        if (attacker == null) return;

        MobEffectInstance currentBurn = attacker.getEffect(Registration.BURN.get());
        int currentAmplifier = 0;
        int currentDuration = 0;
        if (currentBurn != null) {
            currentAmplifier = currentBurn.getAmplifier() + 3;
            currentDuration = currentBurn.getDuration();
        }
        int totalDuration = currentDuration + 20;

        attacker.removeEffect(Registration.BURN.get());
        attacker.addEffect(new MobEffectInstance(
                Registration.BURN.get(),
                totalDuration,
                Math.min(currentAmplifier, CURRENT_MAX_LEVELS - 1),
                false,
                false
        ));
    }

    @SubscribeEvent
    public static void onLivingHurtRoyalJellyPerfumeResistance(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (!player.getPersistentData().getBoolean("hasRoyalJellyPerfume")) return;

        LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity ? (LivingEntity) event.getSource().getEntity() : null;
        if (attacker == null) return;

        MobEffectInstance burn = attacker.getEffect(Registration.BURN.get());
        if (burn != null) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.DAMAGE_RESISTANCE,
                    20 * 3,
                    0,
                    false,
                    false
            ));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtRoyalJellyPerfumeDamage(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasRoyalJellyPerfume")) return;

            MobEffectInstance burn = target.getEffect(Registration.BURN.get());
            if (burn != null && burn.getAmplifier() + 1 >= 60) {
                event.setAmount(event.getAmount() * 1.2f);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtRedDisasterExtract(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasRedDisasterExtract")) return;

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);

            if (matchLevel > 0) {
                event.setAmount(event.getAmount() + 3.0f);
                event.setAmount(event.getAmount() * 1.1f);
            }

            float healthPercent = (player.getHealth() / player.getMaxHealth()) * 100;
            if (healthPercent <= 70.0f) {
                event.setAmount(event.getAmount() * 1.3f);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurtBurningFate(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasBurningFate")) return;

            event.setAmount(event.getAmount() + 8.0f);
        }
    }

    @SubscribeEvent
    public static void onLivingHurtBurningFateTrigger(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasBurningFate")) return;

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
            if (matchLevel > 0) {
                triggerBurnDamage(target);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurtScorchingCopperPipe(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasScorchingCopperPipe")) return;

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
            if (matchLevel <= 0) return;

            event.setAmount(event.getAmount() * 1.3f);

            MobEffectInstance burn = target.getEffect(Registration.BURN.get());
            if (burn != null) {
                int burnLevel = burn.getAmplifier() + 1;
                float extraDamage = burnLevel * 1.5f;
                event.setAmount(event.getAmount() + extraDamage);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurtWingCandle(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasWingCandle")) return;

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
            if (matchLevel > 0) {
                event.setAmount((event.getAmount() + 6.0f) * 1.15f);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;

        MobEffectInstance burn = target.getEffect(Registration.BURN.get());
        if (burn == null) return;

        if (event.getSource().getEntity() instanceof Player player) {
            if (player.getPersistentData().getBoolean("hasBlazingWisdom")) {
                player.getPersistentData().putLong("blazingWisdomBoost", 20 * 30);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtBlazingWisdom(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            if (player.getPersistentData().getLong("blazingWisdomBoost") > 0) {
                event.setAmount(event.getAmount() * 1.2f);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target.level().isClientSide()) return;

        MobEffectInstance burn = target.getEffect(Registration.BURN.get());
        if (burn == null) return;

        long currentTick = target.level().getGameTime();
        if (currentTick % INTERVAL_TICKS != 0) return;

        int level = Math.min(burn.getAmplifier() + 1, CURRENT_MAX_LEVELS);
        float damage = level * DAMAGE_PER_LEVEL;

        if (target.getAbsorptionAmount() > 0) {
            float absorption = target.getAbsorptionAmount();
            if (absorption >= damage) {
                target.setAbsorptionAmount(absorption - damage);
                return;
            } else {
                target.setAbsorptionAmount(0);
                float remaining = damage - absorption;
                target.hurt(target.damageSources().onFire(), remaining);
                return;
            }
        }

        target.hurt(target.damageSources().onFire(), damage);
    }
}