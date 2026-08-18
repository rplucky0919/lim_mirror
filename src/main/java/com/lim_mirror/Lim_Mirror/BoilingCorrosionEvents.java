package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = lim_mirror.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BoilingCorrosionEvents {

    private static final int COOLDOWN_TICKS = 20 * 5;
    private static final double EXPLODE_RADIUS = 5.0;
    private static final int MAX_TREMOR_LEVEL = 99;

    private static final int GREEN_LIGHT_FRUIT_INTERVAL = 20 * 10;

    private static final int GLOW_VARIATOR_INTERVAL = 20 * 10;
    private static final int GLOW_VARIATOR_ADD_AMPLIFIER = 8;
    private static final int GLOW_VARIATOR_ADD_DURATION = 20 * 5;

    private static final int SHOCK_BRACELET_INTERVAL = 20 * 10;
    private static final int SHOCK_BRACELET_ADD_NORMAL = 2;
    private static final int SHOCK_BRACELET_ADD_SMITE = 4;

    private static final int RAINSTORM_INTERVAL = 20 * 10;
    private static final int RAINSTORM_RADIUS = 30;
    private static final int RAINSTORM_ADD_DURATION = 20 * 6;

    private static final int ECHO_SLOW_DURATION = 20 * 3;
    private static final int ECHO_WEAKNESS_DURATION = 20 * 3;
    private static final float ECHO_WEAK_MULTIPLIER = 1.3f;
    private static final float ECHO_STRONG_MULTIPLIER = 1.4f;

    private static final int TRUTH_CLOCK_DURATION = 20 * 20;
    private static final float TRUTH_CLOCK_MULTIPLIER = 1.4f;

    private static final int TREMOR_COUPLING_INTERVAL = 20 * 30;
    private static final int TREMOR_COUPLING_RADIUS = 30;
    private static final int TREMOR_COUPLING_ADD_AMPLIFIER = 5;
    private static final int TREMOR_COUPLING_ADD_DURATION = 20 * 10;
    private static final int TREMOR_COUPLING_EXPLODE_ADD_AMPLIFIER = 5;
    private static final float TREMOR_COUPLING_DAMAGE_MULTIPLIER = 1.2f;
    private static final int TREMOR_COUPLING_WEAKNESS_DURATION = 20 * 10;
    private static final int TREMOR_COUPLING_WEAKNESS_AMPLIFIER = 3;
    private static final int TREMOR_COUPLING_EXPLODE_COUNT_THRESHOLD = 3;
    private static final int TREMOR_COUPLING_EXPLODE_COUNT_RESET = 20 * 30;

    private static final float BIO_TOXIN_VIAL_MULTIPLIER = 1.25f;

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);
        if (enchantLevel <= 0) return;

        if (target == player) return;

        MobEffectInstance currentTremor = target.getEffect(Registration.TREMOR.get());

        int newAmplifier = 1;
        int newDuration = 20;

        if (currentTremor != null) {
            newAmplifier = Math.min(currentTremor.getAmplifier() + 1, MAX_TREMOR_LEVEL - 1);
            newDuration = currentTremor.getDuration() + 20;
        }

        long currentTick = player.level().getGameTime();

        long greenLastTrigger = player.getPersistentData().getLong("greenLightFruitLastTrigger");
        boolean hasGreenLightFruit = player.getPersistentData().getBoolean("hasGreenLightFruit");

        if (hasGreenLightFruit && currentTick - greenLastTrigger >= GREEN_LIGHT_FRUIT_INTERVAL) {
            newAmplifier = Math.min(newAmplifier + 8, MAX_TREMOR_LEVEL - 1);
            player.getPersistentData().putLong("greenLightFruitLastTrigger", currentTick);
            player.displayClientMessage(
                    Component.literal("§a[绿光果实] 震颤强度+8！"),
                    true
            );
        }

        long glowLastTrigger = player.getPersistentData().getLong("glowVariatorLastTrigger");
        boolean hasGlowVariator = player.getPersistentData().getBoolean("hasGlowVariator");

        if (hasGlowVariator && currentTick - glowLastTrigger >= GLOW_VARIATOR_INTERVAL) {
            newAmplifier = Math.min(newAmplifier + GLOW_VARIATOR_ADD_AMPLIFIER, MAX_TREMOR_LEVEL - 1);
            newDuration += GLOW_VARIATOR_ADD_DURATION;
            player.getPersistentData().putLong("glowVariatorLastTrigger", currentTick);
            player.displayClientMessage(
                    Component.literal("§a[辉光变动仪] 震颤+8级，+5秒！"),
                    true
            );
        }

        long shockLastTrigger = player.getPersistentData().getLong("shockBraceletLastTrigger");
        boolean hasShockBracelet = player.getPersistentData().getBoolean("hasShockBracelet");

        if (hasShockBracelet && currentTick - shockLastTrigger >= SHOCK_BRACELET_INTERVAL) {
            int smiteLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SMITE, weapon);
            int addAmplifier = (smiteLevel > 0) ? SHOCK_BRACELET_ADD_SMITE : SHOCK_BRACELET_ADD_NORMAL;
            newAmplifier = Math.min(newAmplifier + addAmplifier, MAX_TREMOR_LEVEL - 1);
            player.getPersistentData().putLong("shockBraceletLastTrigger", currentTick);
            player.displayClientMessage(
                    Component.literal("§a[震荡手环] 震颤+" + addAmplifier + "级！"),
                    true
            );
        }

        target.addEffect(new MobEffectInstance(
                Registration.TREMOR.get(),
                newDuration,
                newAmplifier,
                false,
                false
        ));
    }

    @SubscribeEvent
    public static void onLivingTickRainstorm(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (!player.getPersistentData().getBoolean("hasRainstorm")) return;

        long currentTick = player.level().getGameTime();
        long lastTrigger = player.getPersistentData().getLong("rainstormLastTrigger");

        if (currentTick - lastTrigger < RAINSTORM_INTERVAL) return;

        AABB aabb = new AABB(player.blockPosition()).inflate(RAINSTORM_RADIUS);
        List<LivingEntity> targets = player.level().getEntitiesOfClass(
                LivingEntity.class, aabb,
                e -> e != player && e.isAlive()
        );

        if (targets.isEmpty()) return;

        boolean hasTruthClock = player.getPersistentData().getBoolean("hasTruthClock");
        boolean hasTremorCoupling = player.getPersistentData().getBoolean("hasTremorCoupling");

        for (LivingEntity target : targets) {
            MobEffectInstance currentTremor = target.getEffect(Registration.TREMOR.get());

            int newAmplifier = Math.min(10 - 1, MAX_TREMOR_LEVEL - 1);
            int newDuration = RAINSTORM_ADD_DURATION;

            if (currentTremor != null) {
                newAmplifier = Math.min(currentTremor.getAmplifier() + 10 - 1, MAX_TREMOR_LEVEL - 1);
                newDuration = currentTremor.getDuration() + RAINSTORM_ADD_DURATION;
            }

            target.addEffect(new MobEffectInstance(
                    Registration.TREMOR.get(),
                    newDuration,
                    newAmplifier,
                    false,
                    false
            ));
        }

        for (LivingEntity target : targets) {
            MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
            if (tremor == null) continue;

            int level = tremor.getAmplifier() + 1;
            int duration = tremor.getDuration();

            float damage = level;
            target.hurt(target.damageSources().magic(), damage);

            int newDuration = Math.max(duration - 20 * 1, 0);
            if (newDuration <= 0) {
                target.removeEffect(Registration.TREMOR.get());
            } else {
                target.removeEffect(Registration.TREMOR.get());
                target.addEffect(new MobEffectInstance(
                        Registration.TREMOR.get(),
                        newDuration,
                        tremor.getAmplifier(),
                        false,
                        false
                ));
            }

            if (hasTruthClock) {
                target.getPersistentData().putLong("truthClockEnd", currentTick + TRUTH_CLOCK_DURATION);
            }

            applyTremorCouplingEffects(target, currentTick, hasTremorCoupling);
        }

        player.level().playSound(
                null,
                player.blockPosition(),
                Sounds.TREMOR_EXPLODE.get(),
                SoundSource.PLAYERS,
                1.0f,
                1.0f
        );

        player.getPersistentData().putBoolean("rainstormSecondExplode", true);
        player.getPersistentData().putLong("rainstormSecondExplodeTick", currentTick + 10);

        player.getPersistentData().putLong("rainstormLastTrigger", currentTick);
        player.displayClientMessage(
                Component.literal("§a[暴雨] 对 " + targets.size() + " 个敌人施加了10级震颤，并引爆两次！"),
                true
        );
    }

    @SubscribeEvent
    public static void onLivingTickRainstormSecond(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (!player.getPersistentData().getBoolean("hasRainstorm")) return;
        if (!player.getPersistentData().getBoolean("rainstormSecondExplode")) return;

        long currentTick = player.level().getGameTime();
        long secondTick = player.getPersistentData().getLong("rainstormSecondExplodeTick");

        if (currentTick < secondTick) return;

        player.getPersistentData().putBoolean("rainstormSecondExplode", false);

        AABB aabb = new AABB(player.blockPosition()).inflate(RAINSTORM_RADIUS);
        List<LivingEntity> targets = player.level().getEntitiesOfClass(
                LivingEntity.class, aabb,
                e -> e != player && e.isAlive()
        );

        if (targets.isEmpty()) return;

        boolean hasTruthClock = player.getPersistentData().getBoolean("hasTruthClock");
        boolean hasTremorCoupling = player.getPersistentData().getBoolean("hasTremorCoupling");

        for (LivingEntity target : targets) {
            MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
            if (tremor == null) continue;

            int level = tremor.getAmplifier() + 1;
            int duration = tremor.getDuration();

            float damage = level;
            target.hurt(target.damageSources().magic(), damage);

            int newDuration = Math.max(duration - 20 * 1, 0);
            if (newDuration <= 0) {
                target.removeEffect(Registration.TREMOR.get());
            } else {
                target.removeEffect(Registration.TREMOR.get());
                target.addEffect(new MobEffectInstance(
                        Registration.TREMOR.get(),
                        newDuration,
                        tremor.getAmplifier(),
                        false,
                        false
                ));
            }

            if (hasTruthClock) {
                target.getPersistentData().putLong("truthClockEnd", currentTick + TRUTH_CLOCK_DURATION);
            }

            applyTremorCouplingEffects(target, currentTick, hasTremorCoupling);
        }

        player.level().playSound(
                null,
                player.blockPosition(),
                Sounds.TREMOR_EXPLODE.get(),
                SoundSource.PLAYERS,
                1.0f,
                1.0f
        );
    }

    private static void applyTremorCouplingEffects(LivingEntity target, long currentTick, boolean hasTremorCoupling) {
        if (!hasTremorCoupling) return;

        MobEffectInstance currentTremor = target.getEffect(Registration.TREMOR.get());
        if (currentTremor != null) {
            int newAmplifier = Math.min(currentTremor.getAmplifier() + TREMOR_COUPLING_EXPLODE_ADD_AMPLIFIER - 1, MAX_TREMOR_LEVEL - 1);
            int newDuration = currentTremor.getDuration() + 20;
            target.removeEffect(Registration.TREMOR.get());
            target.addEffect(new MobEffectInstance(
                    Registration.TREMOR.get(),
                    newDuration,
                    newAmplifier,
                    false,
                    false
            ));
        }

        target.getPersistentData().putLong("tremorCouplingDamageEnd", currentTick + 20 * 3);

        String countKey = "tremorCouplingCount_" + target.getStringUUID();
        int count = target.getPersistentData().getInt(countKey) + 1;
        target.getPersistentData().putInt(countKey, count);
        target.getPersistentData().putLong("tremorCouplingCountReset", currentTick + TREMOR_COUPLING_EXPLODE_COUNT_RESET);

        if (count >= TREMOR_COUPLING_EXPLODE_COUNT_THRESHOLD) {
            target.addEffect(new MobEffectInstance(
                    MobEffects.WEAKNESS,
                    TREMOR_COUPLING_WEAKNESS_DURATION,
                    TREMOR_COUPLING_WEAKNESS_AMPLIFIER - 1,
                    false,
                    false
            ));
            target.getPersistentData().putInt(countKey, 0);
        }
    }

    @SubscribeEvent
    public static void onLivingTickTremorCoupling(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (!player.getPersistentData().getBoolean("hasTremorCoupling")) return;

        long currentTick = player.level().getGameTime();
        long lastTrigger = player.getPersistentData().getLong("tremorCouplingLastTrigger");

        if (currentTick - lastTrigger < TREMOR_COUPLING_INTERVAL) return;

        AABB aabb = new AABB(player.blockPosition()).inflate(TREMOR_COUPLING_RADIUS);
        List<LivingEntity> targets = player.level().getEntitiesOfClass(
                LivingEntity.class, aabb,
                e -> e != player && e.isAlive()
        );

        if (targets.isEmpty()) return;

        for (LivingEntity target : targets) {
            MobEffectInstance currentTremor = target.getEffect(Registration.TREMOR.get());

            int newAmplifier = Math.min(TREMOR_COUPLING_ADD_AMPLIFIER - 1, MAX_TREMOR_LEVEL - 1);
            int newDuration = TREMOR_COUPLING_ADD_DURATION;

            if (currentTremor != null) {
                newAmplifier = Math.min(currentTremor.getAmplifier() + TREMOR_COUPLING_ADD_AMPLIFIER - 1, MAX_TREMOR_LEVEL - 1);
                newDuration = currentTremor.getDuration() + TREMOR_COUPLING_ADD_DURATION;
            }

            target.addEffect(new MobEffectInstance(
                    Registration.TREMOR.get(),
                    newDuration,
                    newAmplifier,
                    false,
                    false
            ));
        }

        player.getPersistentData().putLong("tremorCouplingLastTrigger", currentTick);
    }

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (player == null) return;
        if (player.level().isClientSide()) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);
        if (enchantLevel <= 0) return;

        long lastTrigger = player.getPersistentData().getLong("boilingCorrosionLastTrigger");
        long currentTick = player.level().getGameTime();
        if (currentTick - lastTrigger < COOLDOWN_TICKS) {
            long remaining = (COOLDOWN_TICKS - (currentTick - lastTrigger)) / 20;
            player.displayClientMessage(
                    Component.literal("§c[沸腾腐蚀] 冷却中，剩余 " + remaining + " 秒"),
                    true
            );
            return;
        }

        AABB aabb = new AABB(player.blockPosition()).inflate(EXPLODE_RADIUS);
        List<LivingEntity> targets = player.level().getEntitiesOfClass(
                LivingEntity.class, aabb,
                e -> e != player && e.isAlive()
        );

        if (targets.isEmpty()) {
            player.displayClientMessage(
                    Component.literal("§7[沸腾腐蚀] 范围内没有目标"),
                    true
            );
            return;
        }

        int explodedCount = 0;
        boolean hasMeltedEyeball = player.getPersistentData().getBoolean("hasMeltedEyeball");
        boolean hasTruthClock = player.getPersistentData().getBoolean("hasTruthClock");
        boolean hasTremorCoupling = player.getPersistentData().getBoolean("hasTremorCoupling");

        for (LivingEntity target : targets) {
            MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
            if (tremor == null) {
                continue;
            }

            int level = tremor.getAmplifier() + 1;
            int duration = tremor.getDuration();

            float damage = level;
            target.hurt(target.damageSources().magic(), damage);

            int newDuration = Math.max(duration - 20 * 1, 0);
            if (newDuration <= 0) {
                target.removeEffect(Registration.TREMOR.get());
            } else {
                target.removeEffect(Registration.TREMOR.get());
                target.addEffect(new MobEffectInstance(
                        Registration.TREMOR.get(),
                        newDuration,
                        tremor.getAmplifier(),
                        false,
                        false
                ));
            }

            if (hasMeltedEyeball) {
                target.addEffect(new MobEffectInstance(
                        MobEffects.WEAKNESS,
                        20 * 10,
                        0,
                        false,
                        false
                ));
                player.getPersistentData().putLong("meltedEyeballBoostEnd", currentTick + 20 * 10);
            }

            if (hasTruthClock) {
                target.getPersistentData().putLong("truthClockEnd", currentTick + TRUTH_CLOCK_DURATION);
            }

            applyTremorCouplingEffects(target, currentTick, hasTremorCoupling);

            explodedCount++;
        }

        if (explodedCount == 0) {
            player.displayClientMessage(
                    Component.literal("§7[沸腾腐蚀] 范围内没有可引爆的目标"),
                    true
            );
            return;
        }

        player.level().playSound(
                null,
                player.blockPosition(),
                Sounds.TREMOR_EXPLODE.get(),
                SoundSource.PLAYERS,
                1.0f,
                1.0f
        );

        player.getPersistentData().putLong("boilingCorrosionLastTrigger", currentTick);

        player.displayClientMessage(
                Component.literal("§a[沸腾腐蚀] 成功引爆 " + explodedCount + " 个目标！"),
                true
        );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtGaoXue(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;

        if (!player.getPersistentData().getBoolean("hasGaoXue")) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);

        float multiplier = (enchantLevel > 0) ? 1.3f : 1.15f;
        event.setAmount(event.getAmount() * multiplier);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtMeltedEyeball(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;

        if (!player.getPersistentData().getBoolean("hasMeltedEyeball")) return;

        long currentTick = player.level().getGameTime();
        long boostEnd = player.getPersistentData().getLong("meltedEyeballBoostEnd");

        if (currentTick <= boostEnd) {
            event.setAmount(event.getAmount() * 1.1f);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtEcho(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;

        if (!player.getPersistentData().getBoolean("hasEcho")) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);
        if (enchantLevel <= 0) return;

        MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
        if (tremor == null) return;

        int tremorLevel = tremor.getAmplifier() + 1;
        float multiplier = 1.0f;

        if (tremorLevel >= 5 && tremorLevel <= 10) {
            target.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN,
                    ECHO_SLOW_DURATION,
                    1,
                    false,
                    false
            ));
            multiplier = ECHO_WEAK_MULTIPLIER;
        } else if (tremorLevel > 10) {
            target.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN,
                    ECHO_SLOW_DURATION,
                    1,
                    false,
                    false
            ));
            target.addEffect(new MobEffectInstance(
                    MobEffects.WEAKNESS,
                    ECHO_WEAKNESS_DURATION,
                    0,
                    false,
                    false
            ));
            multiplier = ECHO_STRONG_MULTIPLIER;
        }

        if (multiplier != 1.0f) {
            event.setAmount(event.getAmount() * multiplier);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtTruthClock(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;

        long endTime = target.getPersistentData().getLong("truthClockEnd");
        long currentTick = target.level().getGameTime();

        if (currentTick <= endTime) {
            event.setAmount(event.getAmount() * TRUTH_CLOCK_MULTIPLIER);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtTremorCoupling(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;

        long endTime = target.getPersistentData().getLong("tremorCouplingDamageEnd");
        long currentTick = target.level().getGameTime();

        if (currentTick <= endTime) {
            event.setAmount(event.getAmount() * TREMOR_COUPLING_DAMAGE_MULTIPLIER);
        }
    }

    @SubscribeEvent
    public static void onLivingTickReset(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null) return;
        if (entity.level().isClientSide()) return;

        long currentTick = entity.level().getGameTime();

        if (currentTick % 20 != 0) return;

        String[] keys = entity.getPersistentData().getAllKeys().toArray(new String[0]);
        for (String key : keys) {
            if (key.startsWith("tremorCouplingCount_")) {
                long resetTime = entity.getPersistentData().getLong("tremorCouplingCountReset");
                if (resetTime > 0 && currentTick > resetTime) {
                    entity.getPersistentData().putInt(key, 0);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtBioToxinVial(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;

        if (!player.getPersistentData().getBoolean("hasBioToxinVial")) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);
        if (enchantLevel <= 0) return;

        MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
        if (tremor == null) return;

        int tremorLevel = tremor.getAmplifier() + 1;
        int tremorDuration = tremor.getDuration();

        float durationSeconds = tremorDuration / 20.0f;
        float extraDamage = (durationSeconds + tremorLevel) * BIO_TOXIN_VIAL_MULTIPLIER;

        event.setAmount(event.getAmount() + extraDamage);
    }
}