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
import net.minecraftforge.event.entity.living.LivingDeathEvent;
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

    private static final int TOXIC_SKIN_ADD_AMPLIFIER = 4;
    private static final int TOXIC_SKIN_ADD_DURATION = 20 * 4;

    private static final int SOUR_WINE_AROMA_ADD_DAMAGE = 3;
    private static final float SOUR_WINE_AROMA_MULTIPLIER = 1.25f;

    private static final int MIRROR_TACTILE_SYNESTHESIA_INTERVAL = 20 * 20;

    private static final int GEAR_SPRING_ADD_DAMAGE = 3;
    private static final int GEAR_SPRING_BONUS = 5;

    private static final int GEM_PENDULUM_INTERVAL = 20 * 10;

    private static final int SHAKING_BARREL_MIN_TREMOR_LEVEL = 8;
    private static final int SHAKING_BARREL_ADD_DAMAGE = 3;

    private static final int MESHED_GEAR_EXPLODE_THRESHOLD = 6;
    private static final int MESHED_GEAR_DURATION = 20 * 60;

    private static final int EPICENTER_INTERVAL = 20 * 10;
    private static final int EPICENTER_ADD_DAMAGE = 9;
    private static final int EPICENTER_ADD_DURATION = 20 * 6;

    private static final int EIGHT_DIRECTION_BELL_ADD_DURATION = 20 * 6;
    private static final int EIGHT_DIRECTION_BELL_ADD_AMPLIFIER = 3;
    private static final int EIGHT_DIRECTION_BELL_SELF_DURATION = 20 * 16;
    private static final int EIGHT_DIRECTION_BELL_KILL_DURATION = 20 * 6;
    private static final float EIGHT_DIRECTION_BELL_BASE_DAMAGE = 3.0f;
    private static final float EIGHT_DIRECTION_BELL_MULTIPLIER = 0.4f;

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

        boolean hasToxicSkin = player.getPersistentData().getBoolean("hasToxicSkin");

        if (hasToxicSkin) {
            newAmplifier = Math.min(newAmplifier + TOXIC_SKIN_ADD_AMPLIFIER, MAX_TREMOR_LEVEL - 1);
            newDuration += TOXIC_SKIN_ADD_DURATION;
        }

        target.addEffect(new MobEffectInstance(
                Registration.TREMOR.get(),
                newDuration,
                newAmplifier,
                false,
                false
        ));

        long gemLastTrigger = player.getPersistentData().getLong("gemPendulumLastTrigger");
        boolean hasGemPendulum = player.getPersistentData().getBoolean("hasGemPendulum");

        if (hasGemPendulum && currentTick - gemLastTrigger >= GEM_PENDULUM_INTERVAL) {
            MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
            if (tremor != null) {
                int newDurationGem = tremor.getDuration() + 20 * 6;
                int newAmplifierGem = tremor.getAmplifier();
                target.removeEffect(Registration.TREMOR.get());
                target.addEffect(new MobEffectInstance(
                        Registration.TREMOR.get(),
                        newDurationGem,
                        newAmplifierGem,
                        false,
                        false
                ));
            }
            player.getPersistentData().putLong("gemPendulumLastTrigger", currentTick);
            player.displayClientMessage(
                    Component.literal("§a[宝石振子] 震颤+6秒！"),
                    true
            );
        }

        long mirrorLastTrigger = player.getPersistentData().getLong("mirrorTactileSynesthesiaLastTrigger");
        boolean hasMirrorTactileSynesthesia = player.getPersistentData().getBoolean("hasMirrorTactileSynesthesia");

        if (hasMirrorTactileSynesthesia && currentTick - mirrorLastTrigger >= MIRROR_TACTILE_SYNESTHESIA_INTERVAL) {
            MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
            if (tremor != null) {
                int currentAmplifier = tremor.getAmplifier() + 1;
                int addAmplifier = Math.max(currentAmplifier / 2, 1);
                int newAmplifierMirror = Math.min(currentAmplifier + addAmplifier - 1, MAX_TREMOR_LEVEL - 1);
                int duration = tremor.getDuration();

                target.removeEffect(Registration.TREMOR.get());
                target.addEffect(new MobEffectInstance(
                        Registration.TREMOR.get(),
                        duration,
                        newAmplifierMirror,
                        false,
                        false
                ));
                player.getPersistentData().putLong("mirrorTactileSynesthesiaLastTrigger", currentTick);
                player.displayClientMessage(
                        Component.literal("§a[镜触觉通感] 震颤强度+" + addAmplifier + "级！"),
                        true
                );
            }
        }

        boolean hasSourWineAroma = player.getPersistentData().getBoolean("hasSourWineAroma");
        boolean targetHasTremor = target.hasEffect(Registration.TREMOR.get());
        boolean targetHasWeakness = target.hasEffect(MobEffects.WEAKNESS);

        if (hasSourWineAroma && targetHasTremor && targetHasWeakness) {
            event.setAmount(event.getAmount() + SOUR_WINE_AROMA_ADD_DAMAGE);
            event.setAmount(event.getAmount() * SOUR_WINE_AROMA_MULTIPLIER);
        }

        long epicenterLastTrigger = player.getPersistentData().getLong("epicenterLastTrigger");
        boolean hasEpicenter = player.getPersistentData().getBoolean("hasEpicenter");

        if (hasEpicenter && currentTick - epicenterLastTrigger >= EPICENTER_INTERVAL) {
            MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
            int newDurationEpi = (tremor == null) ? EPICENTER_ADD_DURATION : tremor.getDuration() + EPICENTER_ADD_DURATION;
            int newAmplifierEpi = (tremor == null) ? 0 : tremor.getAmplifier();
            target.removeEffect(Registration.TREMOR.get());
            target.addEffect(new MobEffectInstance(
                    Registration.TREMOR.get(),
                    newDurationEpi,
                    newAmplifierEpi,
                    false,
                    false
            ));
            player.getPersistentData().putLong("epicenterLastTrigger", currentTick);
            player.displayClientMessage(
                    Component.literal("§a[震中] 目标震颤+6秒！"),
                    true
            );
        }

        // ==================== 无振八方钟 ====================
        boolean hasEightDirectionBell = player.getPersistentData().getBoolean("hasEightDirectionBell");

        if (hasEightDirectionBell) {
            MobEffectInstance targetTremor = target.getEffect(Registration.TREMOR.get());
            int newDurationTarget = (targetTremor == null) ? EIGHT_DIRECTION_BELL_ADD_DURATION : targetTremor.getDuration() + EIGHT_DIRECTION_BELL_ADD_DURATION;
            int newAmplifierTarget = (targetTremor == null) ? EIGHT_DIRECTION_BELL_ADD_AMPLIFIER - 1 : Math.min(targetTremor.getAmplifier() + EIGHT_DIRECTION_BELL_ADD_AMPLIFIER, MAX_TREMOR_LEVEL - 1);
            target.removeEffect(Registration.TREMOR.get());
            target.addEffect(new MobEffectInstance(
                    Registration.TREMOR.get(),
                    newDurationTarget,
                    newAmplifierTarget,
                    false,
                    false
            ));

            MobEffectInstance selfTremor = player.getEffect(Registration.TREMOR.get());
            int newDurationSelf = (selfTremor == null) ? EIGHT_DIRECTION_BELL_SELF_DURATION : selfTremor.getDuration() + EIGHT_DIRECTION_BELL_SELF_DURATION;
            int newAmplifierSelf = (selfTremor == null) ? 0 : selfTremor.getAmplifier();
            player.removeEffect(Registration.TREMOR.get());
            player.addEffect(new MobEffectInstance(
                    Registration.TREMOR.get(),
                    newDurationSelf,
                    newAmplifierSelf,
                    false,
                    false
            ));
        }
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
            applyMeshedGear(player, target, currentTick);
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
            applyMeshedGear(player, target, currentTick);
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

    private static void applyMeshedGear(Player player, LivingEntity target, long currentTick) {
        if (!player.getPersistentData().getBoolean("hasMeshedGear")) return;

        String meshedCountKey = "meshedGearCount_" + target.getStringUUID();
        int meshedCount = target.getPersistentData().getInt(meshedCountKey) + 1;
        target.getPersistentData().putInt(meshedCountKey, meshedCount);

        if (meshedCount >= MESHED_GEAR_EXPLODE_THRESHOLD) {
            target.addEffect(new MobEffectInstance(
                    MobEffects.WEAKNESS,
                    MESHED_GEAR_DURATION,
                    1,
                    false,
                    false
            ));
            target.getPersistentData().putLong("meshedGearDamageEnd", currentTick + MESHED_GEAR_DURATION);
            target.getPersistentData().putInt(meshedCountKey, 0);
            player.displayClientMessage(
                    Component.literal("§a[嵌合的齿轮] 目标获得虚弱II，受到伤害+10%！"),
                    true
            );
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
                    Component.literal("§c[震颤引爆] 冷却中，剩余 " + remaining + " 秒"),
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
                    Component.literal("§7[震颤引爆] 范围内没有目标"),
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
            applyMeshedGear(player, target, currentTick);

            explodedCount++;
        }

        if (explodedCount == 0) {
            player.displayClientMessage(
                    Component.literal("§7[震颤引爆] 范围内没有可引爆的目标"),
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
                Component.literal("§a[震颤引爆] 成功引爆 " + explodedCount + " 个目标！"),
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtGearSpring(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;

        if (!player.getPersistentData().getBoolean("hasGearSpring")) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);
        if (enchantLevel <= 0) return;

        event.setAmount(event.getAmount() + GEAR_SPRING_ADD_DAMAGE);

        MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
        if (tremor != null) {
            int tremorLevel = tremor.getAmplifier() + 1;
            int tremorDuration = tremor.getDuration();
            float durationSeconds = tremorDuration / 20.0f;
            float extraDamage = (durationSeconds + tremorLevel) + GEAR_SPRING_BONUS;
            event.setAmount(event.getAmount() + extraDamage);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtShakingBarrel(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;

        if (!player.getPersistentData().getBoolean("hasShakingBarrel")) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);
        if (enchantLevel <= 0) return;

        MobEffectInstance tremor = player.getEffect(Registration.TREMOR.get());
        if (tremor == null) return;

        int tremorLevel = tremor.getAmplifier() + 1;
        if (tremorLevel >= SHAKING_BARREL_MIN_TREMOR_LEVEL) {
            event.setAmount(event.getAmount() + SHAKING_BARREL_ADD_DAMAGE);
        }
    }

    @SubscribeEvent
    public static void onLivingDeathShakingBarrel(LivingDeathEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;

        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!player.getPersistentData().getBoolean("hasShakingBarrel")) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);
        if (enchantLevel <= 0) return;

        MobEffectInstance tremor = player.getEffect(Registration.TREMOR.get());
        if (tremor == null) return;

        int duration = tremor.getDuration();
        float durationSeconds = duration / 20.0f;
        int addDuration = (int)(durationSeconds / 2 * 20);

        if (addDuration > 0) {
            int newDuration = duration + addDuration;
            int amplifier = tremor.getAmplifier();
            player.removeEffect(Registration.TREMOR.get());
            player.addEffect(new MobEffectInstance(
                    Registration.TREMOR.get(),
                    newDuration,
                    amplifier,
                    false,
                    false
            ));
            player.displayClientMessage(
                    Component.literal("§a[摇晃的酒桶] 震颤+" + (addDuration / 20) + "秒！"),
                    true
            );
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtMeshedGear(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;

        long endTime = target.getPersistentData().getLong("meshedGearDamageEnd");
        long currentTick = target.level().getGameTime();

        if (currentTick <= endTime) {
            event.setAmount(event.getAmount() * 1.1f);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtEpicenter(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;

        if (!player.getPersistentData().getBoolean("hasEpicenter")) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);
        if (enchantLevel <= 0) return;

        event.setAmount(event.getAmount() + EPICENTER_ADD_DAMAGE);
    }

    @SubscribeEvent
    public static void onLivingDeathEpicenter(LivingDeathEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;

        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!player.getPersistentData().getBoolean("hasEpicenter")) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);
        if (enchantLevel <= 0) return;

        MobEffectInstance tremor = player.getEffect(Registration.TREMOR.get());
        if (tremor == null) return;

        int duration = tremor.getDuration();
        int newDuration = duration + EPICENTER_ADD_DURATION;
        int amplifier = tremor.getAmplifier();

        player.removeEffect(Registration.TREMOR.get());
        player.addEffect(new MobEffectInstance(
                Registration.TREMOR.get(),
                newDuration,
                amplifier,
                false,
                false
        ));
        player.displayClientMessage(
                Component.literal("§a[震中] 自身震颤+6秒！"),
                true
        );
    }

    // ==================== 无振八方钟：伤害加成（需要沸腾腐蚀附魔） ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtEightDirectionBell(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;

        if (!player.getPersistentData().getBoolean("hasEightDirectionBell")) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);
        if (enchantLevel <= 0) return;

        MobEffectInstance selfTremor = player.getEffect(Registration.TREMOR.get());
        if (selfTremor == null) return;

        int tremorLevel = selfTremor.getAmplifier() + 1;
        int tremorDuration = selfTremor.getDuration();
        float durationSeconds = tremorDuration / 20.0f;

        float extraDamage1 = (1 + durationSeconds * EIGHT_DIRECTION_BELL_MULTIPLIER) * EIGHT_DIRECTION_BELL_BASE_DAMAGE;
        float extraDamage2 = (durationSeconds + tremorLevel + 5) / 100.0f;

        event.setAmount(event.getAmount() + extraDamage1);
        event.setAmount(event.getAmount() * (1 + extraDamage2));
    }

    // ==================== 无振八方钟：击杀延长震颤（不需要附魔） ====================

    @SubscribeEvent
    public static void onLivingDeathEightDirectionBell(LivingDeathEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;

        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!player.getPersistentData().getBoolean("hasEightDirectionBell")) return;

        // 不需要沸腾腐蚀附魔检查

        MobEffectInstance tremor = player.getEffect(Registration.TREMOR.get());
        if (tremor == null) return;

        int duration = tremor.getDuration();
        int newDuration = duration + EIGHT_DIRECTION_BELL_KILL_DURATION;
        int amplifier = tremor.getAmplifier();

        player.removeEffect(Registration.TREMOR.get());
        player.addEffect(new MobEffectInstance(
                Registration.TREMOR.get(),
                newDuration,
                amplifier,
                false,
                false
        ));
        player.displayClientMessage(
                Component.literal("§a[无振八方钟] 自身震颤+6秒！"),
                true
        );
    }
}