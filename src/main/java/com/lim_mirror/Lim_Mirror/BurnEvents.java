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

    private static final int INTERVAL_TICKS = 20;
    private static final float DAMAGE_PER_LEVEL = 1.0f;

    public static final int BASE_MAX_LEVELS = 99;
    private static final int BURNING_FATE_MAX_LEVELS = 129;
    private static final int SCORCHING_COPPER_PIPE_MAX_LEVELS = 120;
    private static final int BOTH_MAX_LEVELS = 150;

    // 烧伤维持时间：60 秒（不攻击时保持在当前等级）
    private static final long BURN_MAINTAIN_DURATION = 20 * 60;

    // 未孵化的火种
    private static final long UNHATCHED_SPARKS_COOLDOWN = 20 * 600;
    private static final long UNHATCHED_SPARKS_DURATION = 20 * 5;

    // 余火
    private static final int EMBERS_RADIUS = 30;
    private static final int EMBERS_INTERVAL = 20 * 30;

    // 血炎刀
    private static final int BLOOD_FLAME_BLADE_INTERVAL = 20 * 30;

    // ==================== 上限相关 ====================

    public static int getMaxBurnLevels(Player player) {
        if (player == null) return BASE_MAX_LEVELS;

        boolean hasBurningFate = player.getPersistentData().getBoolean("hasBurningFate");
        boolean hasScorchingCopperPipe = player.getPersistentData().getBoolean("hasScorchingCopperPipe");

        if (hasBurningFate && hasScorchingCopperPipe) {
            return BOTH_MAX_LEVELS;
        } else if (hasBurningFate) {
            return BURNING_FATE_MAX_LEVELS;
        } else if (hasScorchingCopperPipe) {
            return SCORCHING_COPPER_PIPE_MAX_LEVELS;
        }
        return BASE_MAX_LEVELS;
    }

    private static int getStoredBurnMaxLevel(LivingEntity target) {
        if (target == null) return BASE_MAX_LEVELS;
        int stored = target.getPersistentData().getInt("burnMaxLevel");
        return stored > 0 ? stored : BASE_MAX_LEVELS;
    }

    private static void setStoredBurnMaxLevel(LivingEntity target, int maxLevel) {
        if (target == null) return;
        target.getPersistentData().putInt("burnMaxLevel", maxLevel);
    }

    private static void clearStoredBurnMaxLevel(LivingEntity target) {
        if (target == null) return;
        target.getPersistentData().remove("burnMaxLevel");
    }

    private static void applyBurn(LivingEntity target, Player attacker, int addAmplifier, int addDuration) {
        int maxLevels = getMaxBurnLevels(attacker);
        setStoredBurnMaxLevel(target, maxLevels);

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
                Math.min(currentAmplifier, maxLevels - 1),
                false,
                false
        ));

        target.getPersistentData().putLong("burnLastAttackTick", target.level().getGameTime());
        target.getPersistentData().putLong("burnLastAttackPlayerTick", target.level().getGameTime());
    }

    private static void triggerBurnDamage(LivingEntity target) {
        MobEffectInstance burn = target.getEffect(Registration.BURN.get());
        if (burn == null) return;

        int level = burn.getAmplifier() + 1;
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurtUnhatchedSparksTrigger(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (!player.getPersistentData().getBoolean("hasUnhatchedSparks")) return;

        long lastTrigger = player.getPersistentData().getLong("unhatchedSparksLastTrigger");
        long currentTick = player.level().getGameTime();
        if (currentTick - lastTrigger < UNHATCHED_SPARKS_COOLDOWN) return;

        float health = player.getHealth();
        float damage = event.getAmount();
        if (damage < health) return;

        if (event.getSource().getEntity() == player) return;

        player.setHealth(1.0f);

        boolean hasBurn = player.hasEffect(Registration.BURN.get());

        player.getPersistentData().putBoolean("unhatchedSparksActive", true);
        player.getPersistentData().putLong("unhatchedSparksActiveStart", currentTick);
        player.getPersistentData().putLong("unhatchedSparksLastTrigger", currentTick);
        player.getPersistentData().putBoolean("unhatchedSparksHasBurn", hasBurn);

        event.setCanceled(true);

        player.displayClientMessage(Component.literal("§6[未孵化的火种] §c体力锁定为1点，持续5秒！"), true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurtUnhatchedSparksLock(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (!player.getPersistentData().getBoolean("unhatchedSparksActive")) return;

        long activeStart = player.getPersistentData().getLong("unhatchedSparksActiveStart");
        long currentTick = player.level().getGameTime();
        if (currentTick - activeStart >= UNHATCHED_SPARKS_DURATION) {
            return;
        }

        player.setHealth(1.0f);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLivingTickUnhatchedSparksRecover(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (!player.getPersistentData().getBoolean("unhatchedSparksActive")) return;

        long activeStart = player.getPersistentData().getLong("unhatchedSparksActiveStart");
        long currentTick = player.level().getGameTime();

        if (currentTick - activeStart < UNHATCHED_SPARKS_DURATION) return;

        float maxHealth = player.getMaxHealth();
        boolean hasBurn = player.getPersistentData().getBoolean("unhatchedSparksHasBurn");
        float restorePercent = hasBurn ? 0.4f : 0.2f;
        float restoreAmount = maxHealth * restorePercent;

        float newHealth = Math.min(player.getHealth() + restoreAmount, maxHealth);
        player.setHealth(newHealth);

        player.getPersistentData().putBoolean("unhatchedSparksActive", false);
        player.getPersistentData().putBoolean("unhatchedSparksHasBurn", false);

        String percent = hasBurn ? "40%" : "20%";
        player.displayClientMessage(Component.literal("§6[未孵化的火种] §a恢复" + percent + "体力！"), true);
    }

    // ==================== 血炎刀 ====================

    @SubscribeEvent
    public static void onLivingTickBloodFlameBladeCharge(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (!player.getPersistentData().getBoolean("hasBloodFlameBlade")) return;

        long lastCharge = player.getPersistentData().getLong("bloodFlameBladeLastCharge");
        long currentTick = player.level().getGameTime();

        if (lastCharge == 0) {
            player.getPersistentData().putLong("bloodFlameBladeLastCharge", currentTick);
            return;
        }

        if (currentTick - lastCharge >= BLOOD_FLAME_BLADE_INTERVAL) {
            if (!player.getPersistentData().getBoolean("bloodFlameBladeCharged")) {
                player.getPersistentData().putBoolean("bloodFlameBladeCharged", true);
                player.getPersistentData().putLong("bloodFlameBladeLastCharge", currentTick);
                player.displayClientMessage(Component.literal("§6[血炎刀] §a充能完成！下一次火柴之焰攻击额外+5层烧伤"), true);
            }
        }
    }

    // ==================== 余火 ====================

    @SubscribeEvent
    public static void onLivingTickEmbersAOE(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (!player.getPersistentData().getBoolean("hasEmbers")) return;

        long currentTick = player.level().getGameTime();
        long lastTrigger = player.getPersistentData().getLong("embersLastTrigger");

        if (currentTick - lastTrigger < EMBERS_INTERVAL) return;

        int maxLevels = getMaxBurnLevels(player);

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
                        newDuration,
                        Math.min(currentAmplifier, maxLevels - 1),
                        false,
                        false
                ));
                setStoredBurnMaxLevel(target, maxLevels);
                target.getPersistentData().putLong("burnLastAttackTick", currentTick);
                target.getPersistentData().putLong("burnLastAttackPlayerTick", currentTick);
            } else {
                target.addEffect(new MobEffectInstance(
                        Registration.BURN.get(),
                        40,
                        0,
                        false,
                        false
                ));
                setStoredBurnMaxLevel(target, maxLevels);
                target.getPersistentData().putLong("burnLastAttackTick", currentTick);
                target.getPersistentData().putLong("burnLastAttackPlayerTick", currentTick);
            }
        }

        player.getPersistentData().putLong("embersLastTrigger", currentTick);
    }

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
                        clearStoredBurnMaxLevel(target);
                    } else {
                        int maxLevels = getStoredBurnMaxLevel(target);
                        target.removeEffect(Registration.BURN.get());
                        target.addEffect(new MobEffectInstance(
                                Registration.BURN.get(),
                                newDuration,
                                Math.min(burn.getAmplifier(), maxLevels - 1),
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

    // ==================== 核心烧伤施加 ====================

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (!(event.getSource().getEntity() instanceof Player attacker)) return;

        ItemStack weapon = attacker.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
        if (enchantLevel <= 0) return;

        int maxLevels = getMaxBurnLevels(attacker);
        setStoredBurnMaxLevel(target, maxLevels);

        MobEffectInstance currentBurn = target.getEffect(Registration.BURN.get());
        int currentAmplifier = 0;
        int currentDuration = 0;
        int addAmplifier = 1;
        int addDuration = 20;

        if (attacker.getPersistentData().getBoolean("hasRequiem")) {
            addAmplifier = 2;
            addDuration = 40;
        }

        if (attacker.getPersistentData().getBoolean("hasIgnitionGloves")) {
            addAmplifier += 2;
            addDuration += 40;
        }

        if (attacker.getPersistentData().getBoolean("hasReignitionSparkPlug")) {
            addDuration += 20;
        }

        if (attacker.getPersistentData().getBoolean("hasBloodFlameBlade")) {
            if (attacker.getPersistentData().getBoolean("bloodFlameBladeCharged")) {
                addAmplifier += 5;
                attacker.getPersistentData().putBoolean("bloodFlameBladeCharged", false);
                attacker.getPersistentData().putLong("bloodFlameBladeLastCharge", attacker.level().getGameTime());
                attacker.displayClientMessage(Component.literal("§6[血炎刀] §c已消耗充能，烧伤+5层！"), true);
            }
        }

        if (attacker.getPersistentData().getBoolean("hasCookingSecretsBook")) {
            addAmplifier += 2;
        }

        if (attacker.getPersistentData().getBoolean("hasSpicyDrumstick")) {
            addAmplifier += 3;
        }

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
        } else {
            currentAmplifier = addAmplifier - 1;
        }
        int totalDuration = currentDuration + addDuration;

        target.removeEffect(Registration.BURN.get());
        target.addEffect(new MobEffectInstance(
                Registration.BURN.get(),
                totalDuration,
                Math.min(currentAmplifier, maxLevels - 1),
                false,
                false
        ));

        target.getPersistentData().putLong("burnLastAttackTick", target.level().getGameTime());
        target.getPersistentData().putLong("burnLastAttackPlayerTick", target.level().getGameTime());
        target.getPersistentData().putString("burnLastAttacker", attacker.getStringUUID());
    }

    // ==================== 伤害倍率 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtDamageMultipliers(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;

        float multiplier = 1.0f;

        if (player.getPersistentData().getBoolean("hasReignitionSparkPlug")) {
            multiplier *= 1.2f;
        }

        if (player.getPersistentData().getBoolean("hasScorchingCopperPipe")) {
            multiplier *= 1.3f;
        }

        if (player.getPersistentData().getBoolean("hasPolarizedLight")) {
            multiplier *= 1.2f;
        }

        if (player.getPersistentData().getBoolean("hasStolenFlame")) {
            multiplier *= 1.2f;
        }

        if (player.getPersistentData().getBoolean("hasCookingSecretsBook")) {
            multiplier *= 1.1f;
        }

        if (player.getPersistentData().getLong("blazingWisdomBoost") > 0) {
            multiplier *= 1.2f;
        }

        MobEffectInstance burn = target.getEffect(Registration.BURN.get());
        if (burn != null && player.getPersistentData().getBoolean("hasSinglePointLogicCircuit")) {
            int level = burn.getAmplifier() + 1;
            if (level >= 99) {
                multiplier *= 1.1f;
            }
        }

        if (burn != null && player.getPersistentData().getBoolean("hasRoyalJellyPerfume")) {
            int level = burn.getAmplifier() + 1;
            if (level >= 60) {
                multiplier *= 1.2f;
            }
        }

        if (player.getPersistentData().getBoolean("hasRedDisasterExtract")) {
            float healthPercent = (player.getHealth() / player.getMaxHealth()) * 100;
            if (healthPercent <= 70.0f) {
                multiplier *= 1.3f;
            }
        }

        if (multiplier != 1.0f) {
            event.setAmount(event.getAmount() * multiplier);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtUniformSixAssociationMultiplier(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;

        if (!player.getPersistentData().getBoolean("hasUniformSixAssociation")) return;

        float multiplier = 1.0f;

        ItemStack weapon = player.getMainHandItem();
        int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
        if (matchLevel > 0) {
            multiplier *= 1.5f;
        }

        multiplier *= 1.2f;

        if (multiplier != 1.0f) {
            event.setAmount(event.getAmount() * multiplier);
        }
    }

    // ==================== 加算伤害 ====================

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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtBurningFateDamage(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasBurningFate")) return;

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
            if (matchLevel <= 0) return;

            event.setAmount(event.getAmount() + 8.0f);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtRequiemDamage(LivingHurtEvent event) {
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtRedTieDamage(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasRedTie")) return;

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
            if (matchLevel > 0) {
                event.setAmount(event.getAmount() + 6.0f);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtUniformSixAssociationDamage(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasUniformSixAssociation")) return;

            event.setAmount(event.getAmount() + 8.0f);

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
            if (matchLevel > 0) {
                event.setAmount(event.getAmount() + 4.0f);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtRedDisasterExtractDamage(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasRedDisasterExtract")) return;

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
            if (matchLevel > 0) {
                event.setAmount(event.getAmount() + 3.0f);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtWingCandleDamage(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasWingCandle")) return;

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
            if (matchLevel > 0) {
                event.setAmount(event.getAmount() + 6.0f);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtSuppressedFireDamage(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasSuppressedFire")) return;

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
            if (matchLevel <= 0) return;

            MobEffectInstance currentSpeed = player.getEffect(MobEffects.MOVEMENT_SPEED);
            if (currentSpeed != null) {
                event.setAmount(event.getAmount() + 8.0f);
            }
        }
    }

    // ==================== 烧伤触发 ====================

    @SubscribeEvent
    public static void onLivingHurtScorchingCopperPipeExtraDamage(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target == null) return;

            if (!player.getPersistentData().getBoolean("hasScorchingCopperPipe")) return;

            ItemStack weapon = player.getMainHandItem();
            int matchLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.MATCH_FLAME.get(), weapon);
            if (matchLevel <= 0) return;

            MobEffectInstance burn = target.getEffect(Registration.BURN.get());
            if (burn != null) {
                int burnLevel = burn.getAmplifier() + 1;
                float extraDamage = burnLevel * 1.5f;
                event.setAmount(event.getAmount() + extraDamage);
            }
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

    // ==================== 各饰品烧伤叠加 ====================

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

            int maxLevels = getMaxBurnLevels(player);
            setStoredBurnMaxLevel(target, maxLevels);

            int addAmplifier = matchLevel > 0 ? 8 : 5;
            int newAmplifier = currentBurn.getAmplifier() + addAmplifier;
            int currentDuration = currentBurn.getDuration();
            int totalDuration = currentDuration + 20;

            target.removeEffect(Registration.BURN.get());
            target.addEffect(new MobEffectInstance(
                    Registration.BURN.get(),
                    totalDuration,
                    Math.min(newAmplifier, maxLevels - 1),
                    false,
                    false
            ));

            target.getPersistentData().putLong("burnLastAttackTick", target.level().getGameTime());
            target.getPersistentData().putLong("burnLastAttackPlayerTick", target.level().getGameTime());
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

            int maxLevels = getMaxBurnLevels(player);
            setStoredBurnMaxLevel(target, maxLevels);

            MobEffectInstance currentBurn = target.getEffect(Registration.BURN.get());
            if (currentBurn == null) {
                target.addEffect(new MobEffectInstance(
                        Registration.BURN.get(),
                        20 * 2,
                        3,
                        false,
                        false
                ));
                target.getPersistentData().putLong("burnLastAttackTick", target.level().getGameTime());
                target.getPersistentData().putLong("burnLastAttackPlayerTick", target.level().getGameTime());
                return;
            }

            int newAmplifier = currentBurn.getAmplifier() + 4;
            int currentDuration = currentBurn.getDuration();
            int totalDuration = currentDuration + 20;

            target.removeEffect(Registration.BURN.get());
            target.addEffect(new MobEffectInstance(
                    Registration.BURN.get(),
                    totalDuration,
                    Math.min(newAmplifier, maxLevels - 1),
                    false,
                    false
            ));

            target.getPersistentData().putLong("burnLastAttackTick", target.level().getGameTime());
            target.getPersistentData().putLong("burnLastAttackPlayerTick", target.level().getGameTime());
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

            int maxLevels = getMaxBurnLevels(player);
            setStoredBurnMaxLevel(target, maxLevels);

            MobEffectInstance currentBurn = target.getEffect(Registration.BURN.get());
            if (currentBurn == null) {
                target.addEffect(new MobEffectInstance(
                        Registration.BURN.get(),
                        20 * 2,
                        0,
                        false,
                        false
                ));
                target.getPersistentData().putLong("burnLastAttackTick", target.level().getGameTime());
                target.getPersistentData().putLong("burnLastAttackPlayerTick", target.level().getGameTime());
                return;
            }

            int newAmplifier = currentBurn.getAmplifier() + 1;
            int currentDuration = currentBurn.getDuration();
            int totalDuration = currentDuration + 20;

            target.removeEffect(Registration.BURN.get());
            target.addEffect(new MobEffectInstance(
                    Registration.BURN.get(),
                    totalDuration,
                    Math.min(newAmplifier, maxLevels - 1),
                    false,
                    false
            ));

            target.getPersistentData().putLong("burnLastAttackTick", target.level().getGameTime());
            target.getPersistentData().putLong("burnLastAttackPlayerTick", target.level().getGameTime());
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

    @SubscribeEvent
    public static void onLivingHurtRoyalJellyPerfume(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (!player.getPersistentData().getBoolean("hasRoyalJellyPerfume")) return;

        LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity ? (LivingEntity) event.getSource().getEntity() : null;
        if (attacker == null) return;

        int maxLevels = getMaxBurnLevels(player);
        setStoredBurnMaxLevel(attacker, maxLevels);

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
                Math.min(currentAmplifier, maxLevels - 1),
                false,
                false
        ));

        attacker.getPersistentData().putLong("burnLastAttackTick", attacker.level().getGameTime());
        attacker.getPersistentData().putLong("burnLastAttackPlayerTick", attacker.level().getGameTime());
        attacker.getPersistentData().putString("burnLastAttacker", player.getStringUUID());
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

    // ==================== 核心烧伤 Tick ====================

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target.level().isClientSide()) return;

        MobEffectInstance burn = target.getEffect(Registration.BURN.get());
        if (burn == null) {
            clearStoredBurnMaxLevel(target);
            return;
        }

        long currentTick = target.level().getGameTime();

        // ===== 1. 修正等级到存储的上限 =====
        int storedMax = getStoredBurnMaxLevel(target);
        int currentAmplifier = burn.getAmplifier();
        int maxAmplifier = storedMax - 1;

        if (currentAmplifier > maxAmplifier) {
            target.removeEffect(Registration.BURN.get());
            target.addEffect(new MobEffectInstance(
                    Registration.BURN.get(),
                    burn.getDuration(),
                    maxAmplifier,
                    false,
                    false
            ));
            burn = target.getEffect(Registration.BURN.get());
            if (burn == null) return;
            currentAmplifier = maxAmplifier;
        }

        // ===== 2. 每 60 秒根据最后攻击者饰品刷新上限 =====
        long lastRefresh = target.getPersistentData().getLong("burnLastRefreshTick");
        if (lastRefresh == 0) {
            lastRefresh = target.getPersistentData().getLong("burnLastAttackTick");
        }

        if (currentTick - lastRefresh >= BURN_MAINTAIN_DURATION) {
            String attackerUUID = target.getPersistentData().getString("burnLastAttacker");
            int newMax = BASE_MAX_LEVELS;

            if (!attackerUUID.isEmpty() && target.level().getServer() != null) {
                Player attacker = target.level().getServer().getPlayerList().getPlayer(java.util.UUID.fromString(attackerUUID));
                if (attacker != null) {
                    newMax = getMaxBurnLevels(attacker);
                }
            }

            setStoredBurnMaxLevel(target, newMax);

            target.getPersistentData().putLong("burnLastRefreshTick", currentTick);

            burn = target.getEffect(Registration.BURN.get());
            if (burn == null) return;

            currentAmplifier = burn.getAmplifier();
            maxAmplifier = newMax - 1;

            if (currentAmplifier > maxAmplifier) {
                target.removeEffect(Registration.BURN.get());
                target.addEffect(new MobEffectInstance(
                        Registration.BURN.get(),
                        burn.getDuration(),
                        maxAmplifier,
                        false,
                        false
                ));
                burn = target.getEffect(Registration.BURN.get());
                if (burn == null) return;
                currentAmplifier = maxAmplifier;
            }
        }

        // ===== 3. 伤害 =====
        int level = currentAmplifier + 1;
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