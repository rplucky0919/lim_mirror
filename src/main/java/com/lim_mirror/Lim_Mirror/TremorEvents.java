package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
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
public class TremorEvents {

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

    private static final float MELTED_EYEBALL_MULTIPLIER = 2.5f;

    private static final int TREMOR_CHAIN_INTERVAL = 10;
    private static final int TREMOR_CHAIN_MAX_WEAKNESS = 3;
    private static final int TREMOR_CHAIN_DURATION = 20 * 60;

    private static final int TREMOR_COLLAPSE_DURATION = 20 * 20;

    private static final int CLAP_CLAP_REDUCE_DURATION = 20 * 2;
    private static final int CLAP_CLAP_ADD_DURATION = 20 * 10;
    private static final int CLAP_CLAP_MAX_LEVEL = 99;

    // ==================== 加热锤 ====================
    private static final int HEATING_HAMMER_TREMOR_BURN_THRESHOLD = 15;
    private static final int HEATING_HAMMER_BURN_INTERVAL = 3;
    private static final float HEATING_HAMMER_MAX_BONUS = 0.15f;

    // ==================== 再来一杯 ====================
    private static final int ONE_MORE_DRINK_INTERVAL = 20 * 30;
    private static final int ONE_MORE_DRINK_ADD_DURATION = 20 * 15;
    private static final float ONE_MORE_DRINK_DAMAGE_MULTIPLIER = 1.2f;

    // ==================== 不完善的预知眼 ====================
    private static final int IMPERFECT_EYE_CONVERT_COOLDOWN = 20 * 10;
    private static final float IMPERFECT_EYE_DAMAGE_MULTIPLIER = 1.8f;
    private static final float IMPERFECT_EYE_WEAKNESS_MULTIPLIER = 1.3f;
    private static final float IMPERFECT_EYE_BUFF_ADD_DAMAGE = 30.0f;
    private static final float IMPERFECT_EYE_BUFF_FINAL_MULTIPLIER = 1.2f;
    private static final int IMPERFECT_EYE_BUFF_ADD_BURN_LEVEL = 2;
    private static final int IMPERFECT_EYE_BUFF_ADD_TREMOR_LEVEL = 2;

    // ==================== 奉纳的雪茄 ====================
    private static final float OFFERING_CIGAR_DAMAGE_MULTIPLIER = 1.1f;
    private static final float OFFERING_CIGAR_FINAL_MULTIPLIER = 1.2f;
    private static final float OFFERING_CIGAR_ADD_DAMAGE = 12.0f;
    private static final int OFFERING_CIGAR_TREMOR_BURN_THRESHOLD = 20;
    private static final float OFFERING_CIGAR_TREMOR_BURN_MULTIPLIER = 1.2f;
    private static final float OFFERING_CIGAR_AMMO_MULTIPLIER = 1.2f;
    private static final float OFFERING_CIGAR_AMMO_FINAL_MULTIPLIER = 1.2f;
    private static final float OFFERING_CIGAR_EXPLODE_NEXT_ATTACK_ADD = 6.0f;

    // ==================== 定制 ====================
    private static final int CUSTOM_MADE_TREMOR_INTERVAL = 20 * 30;
    private static final int CUSTOM_MADE_TREMOR_DURATION = 20 * 6;
    private static final int CUSTOM_MADE_RESISTANCE_INTERVAL = 20 * 10;
    private static final int CUSTOM_MADE_RESISTANCE_DURATION = 20 * 5;
    private static final float CUSTOM_MADE_DAMAGE_MULTIPLIER = 1.2f;

    // ==================== 手镜 ====================
    private static final int HAND_MIRROR_SLOW_DURATION = 20 * 30;
    private static final int HAND_MIRROR_SLOW_AMPLIFIER = 4;
    private static final int HAND_MIRROR_DAMAGE_DURATION = 20 * 30;
    private static final float HAND_MIRROR_DAMAGE_MULTIPLIER = 1.3f;
    private static final int HAND_MIRROR_TREMOR_DOUBLE_DURATION = 20 * 60;

    // ==================== 沾染油污的扳手 ====================
    private static final int OILY_WRENCH_ADD_TREMOR_LEVEL = 2;
    private static final int OILY_WRENCH_TREMOR_DURATION = 20;
    private static final float OILY_WRENCH_DAMAGE_MULTIPLIER = 1.1f;

    // ==================== 银色的表壳 ====================
    private static final float SILVER_WATCH_CASE_DAMAGE_MULTIPLIER = 1.1f;
    private static final int SILVER_WATCH_CASE_ADD_TREMOR_DURATION = 20 * 4;
    private static final int SILVER_WATCH_CASE_COOLDOWN = 20 * 5;

    // ==================== 褪色的表壳 ====================
    private static final float FADED_WATCH_CASE_ADD_DAMAGE = 3.0f;
    private static final int FADED_WATCH_CASE_ADD_TREMOR_LEVEL = 3;
    private static final int FADED_WATCH_CASE_ADD_TREMOR_DURATION = 20 * 4;
    private static final int FADED_WATCH_CASE_COOLDOWN = 20 * 10;

    // ==================== 蚀刻的指针 ====================
    private static final int ETCHED_HAND_INTERVAL = 20 * 30;
    private static final int ETCHED_HAND_RADIUS = 10;
    private static final int ETCHED_HAND_ADD_TREMOR_LEVEL = 3;
    private static final int ETCHED_HAND_TREMOR_DURATION = 20;
    private static final int ETCHED_HAND_TREMOR_THRESHOLD = 12;
    private static final float ETCHED_HAND_DAMAGE_MULTIPLIER = 1.1f;

    // ==================== 锈蚀的指针 ====================
    private static final int RUSTED_HAND_ADD_TREMOR_LEVEL = 8;
    private static final int RUSTED_HAND_ADD_TREMOR_DURATION = 20 * 8;
    private static final int RUSTED_HAND_COOLDOWN = 20 * 30;
    private static final int RUSTED_HAND_TREMOR_THRESHOLD = 12;
    private static final float RUSTED_HAND_DAMAGE_REDUCTION = 0.9f;

    // ==================== 熄灭的提灯 ====================
    private static final int EXTINGUISHED_LANTERN_SLOT = 6;
    private static final int EXTINGUISHED_LANTERN_EXTRA_DURATION = 20;

    // ==================== 判断是否有任何震颤效果（public static，供TremorEvents2调用） ====================

    public static boolean hasAnyTremorEffect(LivingEntity target) {
        if (target == null) return false;
        return target.hasEffect(Registration.TREMOR.get())
                || target.hasEffect(Registration.TREMOR_BURN.get())
                || target.hasEffect(Registration.TREMOR_BLEED.get())
                || target.hasEffect(Registration.TREMOR_ECHO.get())
                || target.hasEffect(Registration.TREMOR_COLLAPSE.get())
                || target.hasEffect(Registration.TREMOR_CHAIN.get());
    }

    // ==================== 熄灭的提灯：快捷栏第7格有武器时自身震颤额外+1秒 ====================

    public static boolean shouldExtendSelfTremor(Player player) {
        if (player == null) return false;

        if (!player.getPersistentData().getBoolean("hasExtinguishedLantern")) return false;

        ItemStack slotStack = player.getInventory().getItem(EXTINGUISHED_LANTERN_SLOT);
        return !slotStack.isEmpty();
    }

    public static void addSelfTremor(Player player, int baseDuration, int amplifier) {
        int duration = baseDuration;
        if (shouldExtendSelfTremor(player)) {
            duration += EXTINGUISHED_LANTERN_EXTRA_DURATION;
        }

        MobEffectInstance existing = player.getEffect(Registration.TREMOR.get());
        int newDuration = (existing != null) ? existing.getDuration() + duration : duration;
        int newAmplifier = (existing != null) ? existing.getAmplifier() : amplifier;

        newAmplifier = Math.max(0, newAmplifier);

        player.removeEffect(Registration.TREMOR.get());
        player.addEffect(new MobEffectInstance(
                Registration.TREMOR.get(),
                newDuration,
                newAmplifier,
                false,
                false
        ));
    }

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

        int newAmplifier = 0;
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

        newAmplifier = Math.max(0, newAmplifier);

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
                int newAmplifierGem = Math.max(0, tremor.getAmplifier());
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
                int currentAmplifier = Math.max(0, tremor.getAmplifier() + 1);
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
            int newAmplifierEpi = (tremor == null) ? 0 : Math.max(0, tremor.getAmplifier());
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
            int newAmplifierTarget = (targetTremor == null) ? EIGHT_DIRECTION_BELL_ADD_AMPLIFIER - 1 : Math.min(Math.max(0, targetTremor.getAmplifier() + EIGHT_DIRECTION_BELL_ADD_AMPLIFIER), MAX_TREMOR_LEVEL - 1);
            target.removeEffect(Registration.TREMOR.get());
            target.addEffect(new MobEffectInstance(
                    Registration.TREMOR.get(),
                    newDurationTarget,
                    newAmplifierTarget,
                    false,
                    false
            ));

            MobEffectInstance selfTremor = player.getEffect(Registration.TREMOR.get());
            int amplifier = (selfTremor == null) ? 0 : Math.max(0, selfTremor.getAmplifier());
            addSelfTremor(player, EIGHT_DIRECTION_BELL_SELF_DURATION, amplifier);
        }

        // ==================== 加热锤 ====================
        applyHeatingHammer(player, target);

        // ==================== 拍手啪啪！ ====================
        applyClapClap(player, target);
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

            int newAmplifier = 9;
            int newDuration = RAINSTORM_ADD_DURATION;

            if (currentTremor != null) {
                newAmplifier = Math.min(Math.max(0, currentTremor.getAmplifier() + 10 - 1), MAX_TREMOR_LEVEL - 1);
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
            if (!hasAnyTremorEffect(target)) continue;

            MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
            int level = (tremor != null) ? Math.max(0, tremor.getAmplifier() + 1) : 0;
            int duration = (tremor != null) ? tremor.getDuration() : 0;

            float damage = level;
            if (damage > 0) {
                damage *= getHeatingHammerExplodeMultiplier(player, target);
                target.hurt(target.damageSources().magic(), damage);
            }

            if (tremor != null) {
                int newDuration = Math.max(duration - 20 * 1, 0);
                if (newDuration <= 0) {
                    target.removeEffect(Registration.TREMOR.get());
                } else {
                    target.removeEffect(Registration.TREMOR.get());
                    target.addEffect(new MobEffectInstance(
                            Registration.TREMOR.get(),
                            newDuration,
                            Math.max(0, tremor.getAmplifier()),
                            false,
                            false
                    ));
                }
            }

            reduceSpecialTremorDuration(target, Registration.TREMOR_BURN.get(), 20);
            reduceSpecialTremorDuration(target, Registration.TREMOR_BLEED.get(), 20);
            reduceSpecialTremorDuration(target, Registration.TREMOR_ECHO.get(), 20);
            reduceSpecialTremorDuration(target, Registration.TREMOR_COLLAPSE.get(), 20);
            reduceSpecialTremorDuration(target, Registration.TREMOR_CHAIN.get(), 20);

            applySilverWatchCaseTremorBoost(player, target);

            if (hasTruthClock) {
                target.getPersistentData().putLong("truthClockEnd", currentTick + TRUTH_CLOCK_DURATION);
            }

            applyTremorCouplingEffects(target, currentTick, hasTremorCoupling);
            applyMeshedGear(player, target, currentTick);
            applyTremorBurn(player, target);
            applyTremorBleed(player, target);
            applyTremorEcho(player, target);
            applyTremorCollapse(target);
            applyTremorChain(target);
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
            if (!hasAnyTremorEffect(target)) continue;

            MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
            int level = (tremor != null) ? Math.max(0, tremor.getAmplifier() + 1) : 0;
            int duration = (tremor != null) ? tremor.getDuration() : 0;

            float damage = level;
            if (damage > 0) {
                damage *= getHeatingHammerExplodeMultiplier(player, target);
                target.hurt(target.damageSources().magic(), damage);
            }

            if (tremor != null) {
                int newDuration = Math.max(duration - 20 * 1, 0);
                if (newDuration <= 0) {
                    target.removeEffect(Registration.TREMOR.get());
                } else {
                    target.removeEffect(Registration.TREMOR.get());
                    target.addEffect(new MobEffectInstance(
                            Registration.TREMOR.get(),
                            newDuration,
                            Math.max(0, tremor.getAmplifier()),
                            false,
                            false
                    ));
                }
            }

            reduceSpecialTremorDuration(target, Registration.TREMOR_BURN.get(), 20);
            reduceSpecialTremorDuration(target, Registration.TREMOR_BLEED.get(), 20);
            reduceSpecialTremorDuration(target, Registration.TREMOR_ECHO.get(), 20);
            reduceSpecialTremorDuration(target, Registration.TREMOR_COLLAPSE.get(), 20);
            reduceSpecialTremorDuration(target, Registration.TREMOR_CHAIN.get(), 20);

            applySilverWatchCaseTremorBoost(player, target);

            if (hasTruthClock) {
                target.getPersistentData().putLong("truthClockEnd", currentTick + TRUTH_CLOCK_DURATION);
            }

            applyTremorCouplingEffects(target, currentTick, hasTremorCoupling);
            applyMeshedGear(player, target, currentTick);
            applyTremorBurn(player, target);
            applyTremorBleed(player, target);
            applyTremorEcho(player, target);
            applyTremorCollapse(target);
            applyTremorChain(target);
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

    private static void reduceSpecialTremorDuration(LivingEntity target, MobEffect effect, int reduceTicks) {
        if (target == null || effect == null) return;

        MobEffectInstance instance = target.getEffect(effect);
        if (instance == null) return;

        int newDuration = Math.max(instance.getDuration() - reduceTicks, 0);
        if (newDuration <= 0) {
            target.removeEffect(effect);
        } else {
            target.removeEffect(effect);
            target.addEffect(new MobEffectInstance(
                    effect,
                    newDuration,
                    Math.max(0, instance.getAmplifier()),
                    false,
                    false
            ));
        }
    }

    private static void applySilverWatchCaseTremorBoost(Player player, LivingEntity target) {
        if (player == null || target == null) return;

        if (!player.getPersistentData().getBoolean("hasSilverWatchCase")) return;

        long lastTrigger = player.getPersistentData().getLong("silverWatchCaseLastTrigger");
        long currentTick = target.level().getGameTime();

        if (currentTick - lastTrigger < SILVER_WATCH_CASE_COOLDOWN) return;

        MobEffectInstance targetTremor = target.getEffect(Registration.TREMOR.get());
        if (targetTremor != null) {
            int newDuration = targetTremor.getDuration() + SILVER_WATCH_CASE_ADD_TREMOR_DURATION;
            int amplifier = Math.max(0, targetTremor.getAmplifier());
            target.removeEffect(Registration.TREMOR.get());
            target.addEffect(new MobEffectInstance(
                    Registration.TREMOR.get(),
                    newDuration,
                    amplifier,
                    false,
                    false
            ));
        } else {
            target.addEffect(new MobEffectInstance(
                    Registration.TREMOR.get(),
                    SILVER_WATCH_CASE_ADD_TREMOR_DURATION,
                    0,
                    false,
                    false
            ));
        }

        player.getPersistentData().putLong("silverWatchCaseLastTrigger", currentTick);
    }

    private static void applyHeatingHammer(Player player, LivingEntity target) {
        if (target == null) return;

        if (!player.getPersistentData().getBoolean("hasHeatingHammer")) return;

        MobEffectInstance tremorBurn = target.getEffect(Registration.TREMOR_BURN.get());
        if (tremorBurn == null) return;

        MobEffectInstance burn = target.getEffect(Registration.BURN.get());
        int burnAmplifier = (burn != null) ? Math.max(0, burn.getAmplifier() + 1) : 0;
        int burnDuration = (burn != null) ? burn.getDuration() + 20 : 20;

        int tremorBurnLevel = Math.max(0, tremorBurn.getAmplifier() + 1);
        if (tremorBurnLevel >= HEATING_HAMMER_TREMOR_BURN_THRESHOLD) {
            burnAmplifier += 1;
        }

        int maxBurnLevels = BurnEvents.getMaxBurnLevels(player);
        target.removeEffect(Registration.BURN.get());
        target.addEffect(new MobEffectInstance(
                Registration.BURN.get(),
                burnDuration,
                Math.min(burnAmplifier, maxBurnLevels - 1),
                false,
                false
        ));
    }

    private static float getHeatingHammerExplodeMultiplier(Player player, LivingEntity target) {
        if (player == null || target == null) return 1.0f;

        if (!player.getPersistentData().getBoolean("hasHeatingHammer")) return 1.0f;

        MobEffectInstance burn = target.getEffect(Registration.BURN.get());
        if (burn == null) return 1.0f;

        int burnLevel = Math.max(0, burn.getAmplifier() + 1);
        float bonus = Math.min(burnLevel / (float) HEATING_HAMMER_BURN_INTERVAL / 100.0f, HEATING_HAMMER_MAX_BONUS);

        return 1.0f + bonus;
    }

    private static void applyTremorCouplingEffects(LivingEntity target, long currentTick, boolean hasTremorCoupling) {
        if (!hasTremorCoupling) return;

        MobEffectInstance currentTremor = target.getEffect(Registration.TREMOR.get());
        if (currentTremor != null) {
            int newAmplifier = Math.min(Math.max(0, currentTremor.getAmplifier() + TREMOR_COUPLING_EXPLODE_ADD_AMPLIFIER - 1), MAX_TREMOR_LEVEL - 1);
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

    // ==================== 震颤-灼热：单吃烧伤 ====================

    private static void applyTremorBurn(Player player, LivingEntity target) {
        if (target == null) return;

        MobEffectInstance burn = target.getEffect(Registration.BURN.get());
        if (burn == null) return;

        int burnLevel = Math.max(0, burn.getAmplifier() + 1);
        if (burnLevel <= 0) return;

        float damage = burnLevel;
        damage *= getHeatingHammerExplodeMultiplier(player, target);
        target.hurt(target.damageSources().onFire(), damage);
    }

    // ==================== 震颤-大出血：单吃流血 ====================

    private static void applyTremorBleed(Player player, LivingEntity target) {
        if (target == null) return;

        MobEffectInstance bleed = target.getEffect(Registration.BLEED.get());
        if (bleed == null) return;

        int bleedLevel = Math.max(0, bleed.getAmplifier() + 1);
        if (bleedLevel <= 0) return;

        float damage = bleedLevel;
        damage *= getHeatingHammerExplodeMultiplier(player, target);
        target.hurt(target.damageSources().wither(), damage);
    }

    private static void applyTremorEcho(Player player, LivingEntity target) {
        if (target == null) return;

        MobEffectInstance tremorEcho = target.getEffect(Registration.TREMOR_ECHO.get());
        if (tremorEcho == null) return;

        int amplifier = Math.max(0, tremorEcho.getAmplifier());
        int damage = amplifier + 1;

        if (damage > 0) {
            float echoDamage = damage * getHeatingHammerExplodeMultiplier(player, target);
            target.hurt(target.damageSources().magic(), echoDamage);
        }
    }

    private static void applyTremorCollapse(LivingEntity target) {
        if (target == null) return;

        MobEffectInstance tremorCollapse = target.getEffect(Registration.TREMOR_COLLAPSE.get());
        if (tremorCollapse == null) return;

        MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
        if (tremor == null) return;

        int tremorLevel = Math.max(0, tremor.getAmplifier() + 1);
        float multiplier = tremorLevel / 40.0f;

        if (multiplier > 1.0f) {
            target.getPersistentData().putFloat("tremorCollapseMultiplier", multiplier);
            target.getPersistentData().putLong("tremorCollapseEnd", target.level().getGameTime() + TREMOR_COLLAPSE_DURATION);
        }
    }

    private static void applyTremorChain(LivingEntity target) {
        if (target == null) return;

        MobEffectInstance tremorChain = target.getEffect(Registration.TREMOR_CHAIN.get());
        if (tremorChain == null) return;

        MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
        if (tremor == null) return;

        int tremorLevel = Math.max(0, tremor.getAmplifier() + 1);
        int weaknessLevel = Math.min(tremorLevel / TREMOR_CHAIN_INTERVAL, TREMOR_CHAIN_MAX_WEAKNESS);

        if (weaknessLevel > 0) {
            target.addEffect(new MobEffectInstance(
                    MobEffects.WEAKNESS,
                    TREMOR_CHAIN_DURATION,
                    weaknessLevel - 1,
                    false,
                    false
            ));
        }
    }

    private static void applyClapClap(Player player, LivingEntity target) {
        if (target == null) return;

        if (!player.getPersistentData().getBoolean("hasClapClap")) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);
        if (enchantLevel <= 0) return;

        // ===== 1. 减少基础震颤2秒 =====
        MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
        if (tremor != null) {
            int newDuration = Math.max(tremor.getDuration() - CLAP_CLAP_REDUCE_DURATION, 0);
            if (newDuration <= 0) {
                target.removeEffect(Registration.TREMOR.get());
            } else {
                target.removeEffect(Registration.TREMOR.get());
                target.addEffect(new MobEffectInstance(
                        Registration.TREMOR.get(),
                        newDuration,
                        Math.max(0, tremor.getAmplifier()),
                        false,
                        false
                ));
            }
        }

        // ===== 2. 随机选择特殊震颤 =====
        MobEffect[] specials = new MobEffect[]{
                Registration.TREMOR_BURN.get(),
                Registration.TREMOR_BLEED.get(),
                Registration.TREMOR_ECHO.get(),
                Registration.TREMOR_COLLAPSE.get(),
                Registration.TREMOR_CHAIN.get()
        };
        int index = player.level().random.nextInt(specials.length);
        MobEffect selected = specials[index];

        MobEffectInstance current = target.getEffect(selected);

        float specialMultiplier = 1.0f;
        if (player.getPersistentData().getBoolean("hasPocketWatchTypeL")) {
            specialMultiplier = 1.5f;
        }

        int newAmplifier;
        int newDuration;

        if (current != null) {
            newAmplifier = Math.min((int)((Math.max(0, current.getAmplifier()) + 1) * specialMultiplier), CLAP_CLAP_MAX_LEVEL - 1);
            newDuration = (int)((current.getDuration() + CLAP_CLAP_ADD_DURATION) * specialMultiplier);
        } else {
            newAmplifier = Math.min((int)(1 * specialMultiplier) - 1, CLAP_CLAP_MAX_LEVEL - 1);
            newDuration = (int)(CLAP_CLAP_ADD_DURATION * specialMultiplier);
        }

        newAmplifier = Math.max(0, newAmplifier);

        // ===== 3. 触发转化事件 =====
        TremorConversionEvent conversionEvent = new TremorConversionEvent(
                target,
                player,
                TremorConversionEvent.ConversionSource.CLAP_CLAP,
                Registration.TREMOR.get(),
                target.getEffect(Registration.TREMOR.get()),
                selected,
                newAmplifier,
                newDuration,
                specialMultiplier
        );

        if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(conversionEvent)) {
            conversionEvent.applyConversion();

            String effectName = getSpecialTremorName(selected);
            player.displayClientMessage(
                    Component.literal("§a[拍手啪啪！] §f" + effectName + " §a+" + (conversionEvent.getTargetAmplifier() + 1) + "级！"),
                    true
            );

            // ===== 4. 交织的时间：转化后引爆 =====
            if (player.getPersistentData().getBoolean("hasIntertwinedTime")) {
                if (hasAnyTremorEffect(target)) {
                    explodeSingleTarget(player, target, player.level().getGameTime());
                    player.displayClientMessage(
                            Component.literal("§d[交织的时间] 引爆触发！"),
                            true
                    );
                }
            }
        } else {
            player.displayClientMessage(
                    Component.literal("§7[拍手啪啪！] 转化被阻止"),
                    true
            );
        }
    }

    private static String getSpecialTremorName(MobEffect effect) {
        if (effect == Registration.TREMOR_BURN.get()) return "震颤-灼热";
        if (effect == Registration.TREMOR_BLEED.get()) return "震颤-大出血";
        if (effect == Registration.TREMOR_ECHO.get()) return "震颤-回响";
        if (effect == Registration.TREMOR_COLLAPSE.get()) return "震颤-崩坏";
        if (effect == Registration.TREMOR_CHAIN.get()) return "震颤-锁链";
        return "未知";
    }

    // ==================== 再来一杯：每30秒自动充能和震颤 ====================

    @SubscribeEvent
    public static void onLivingTickOneMoreDrink(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (!player.getPersistentData().getBoolean("hasOneMoreDrink")) return;

        long currentTick = player.level().getGameTime();
        long lastTrigger = player.getPersistentData().getLong("oneMoreDrinkLastTrigger");

        if (currentTick - lastTrigger < ONE_MORE_DRINK_INTERVAL) return;

        MobEffectInstance charge = player.getEffect(Registration.CHARGE.get());
        if (charge != null) {
            int newDuration = Math.min(charge.getDuration() + ONE_MORE_DRINK_ADD_DURATION, 20 * 60);
            int amplifier = Math.max(0, charge.getAmplifier());
            player.removeEffect(Registration.CHARGE.get());
            player.addEffect(new MobEffectInstance(
                    Registration.CHARGE.get(),
                    newDuration,
                    amplifier,
                    false,
                    false
            ));
        } else {
            player.addEffect(new MobEffectInstance(
                    Registration.CHARGE.get(),
                    ONE_MORE_DRINK_ADD_DURATION,
                    0,
                    false,
                    false
            ));
        }

        addSelfTremor(player, ONE_MORE_DRINK_ADD_DURATION, 0);

        player.getPersistentData().putLong("oneMoreDrinkLastTrigger", currentTick);
        player.displayClientMessage(
                Component.literal("§a[再来一杯] 充能+15秒，震颤+15秒！"),
                true
        );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtOneMoreDrink(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;

        if (!player.getPersistentData().getBoolean("hasOneMoreDrink")) return;

        ItemStack weapon = player.getMainHandItem();
        int boilingLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);
        int spaceTearLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.SPACE_TEAR.get(), weapon);

        if (boilingLevel > 0 || spaceTearLevel > 0) {
            event.setAmount(event.getAmount() * ONE_MORE_DRINK_DAMAGE_MULTIPLIER);
        }
    }

    @SubscribeEvent
    public static void onLivingTickCustomMade(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (!player.getPersistentData().getBoolean("hasCustomMade")) return;

        long currentTick = player.level().getGameTime();

        long lastTremor = player.getPersistentData().getLong("customMadeLastTremor");
        if (currentTick - lastTremor >= CUSTOM_MADE_TREMOR_INTERVAL) {
            addSelfTremor(player, CUSTOM_MADE_TREMOR_DURATION, 0);
            player.getPersistentData().putLong("customMadeLastTremor", currentTick);
        }

        long lastResistance = player.getPersistentData().getLong("customMadeLastResistance");
        if (currentTick - lastResistance >= CUSTOM_MADE_RESISTANCE_INTERVAL) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.DAMAGE_RESISTANCE,
                    CUSTOM_MADE_RESISTANCE_DURATION,
                    0,
                    false,
                    false
            ));
            player.getPersistentData().putLong("customMadeLastResistance", currentTick);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtCustomMade(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasCustomMade")) return;

        event.setAmount(event.getAmount() * CUSTOM_MADE_DAMAGE_MULTIPLIER);
    }

    @SubscribeEvent
    public static void onLivingHurtImperfectEyeConvert(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasImperfectForesightEye")) return;

        long currentTick = player.level().getGameTime();
        long lastConvert = player.getPersistentData().getLong("imperfectEyeLastConvert");

        if (currentTick - lastConvert < IMPERFECT_EYE_CONVERT_COOLDOWN) return;

        MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
        if (tremor != null) {
            int tremorLevel = Math.max(0, tremor.getAmplifier());
            int tremorDuration = tremor.getDuration();

            MobEffectInstance tremorBurn = target.getEffect(Registration.TREMOR_BURN.get());
            int burnLevel = (tremorBurn != null) ? Math.max(0, tremorBurn.getAmplifier()) : -1;
            int burnDuration = (tremorBurn != null) ? tremorBurn.getDuration() : 0;

            float specialMultiplier = 1.0f;
            if (player.getPersistentData().getBoolean("hasPocketWatchTypeL")) {
                specialMultiplier = 1.5f;
            }

            int newBurnLevel = Math.min((int)((tremorLevel + burnLevel + 1) * specialMultiplier), MAX_TREMOR_LEVEL - 1);
            int newBurnDuration = (int)(Math.max(tremorDuration, burnDuration) * specialMultiplier);
            newBurnLevel = Math.max(0, newBurnLevel);

            TremorConversionEvent conversionEvent = new TremorConversionEvent(
                    target,
                    player,
                    TremorConversionEvent.ConversionSource.IMPERFECT_EYE,
                    Registration.TREMOR.get(),
                    tremor,
                    Registration.TREMOR_BURN.get(),
                    newBurnLevel,
                    newBurnDuration,
                    specialMultiplier
            );

            target.removeEffect(Registration.TREMOR.get());
            target.removeEffect(Registration.TREMOR_BURN.get());

            if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(conversionEvent)) {
                conversionEvent.applyConversion();
                player.displayClientMessage(
                        Component.literal("§a[不完善的预知眼] 转化成功！"),
                        true
                );

                if (player.getPersistentData().getBoolean("hasIntertwinedTime")) {
                    if (hasAnyTremorEffect(target)) {
                        explodeSingleTarget(player, target, currentTick);
                        player.displayClientMessage(
                                Component.literal("§d[交织的时间] 引爆触发！"),
                                true
                        );
                    }
                }
            } else {
                target.addEffect(new MobEffectInstance(
                        Registration.TREMOR.get(),
                        tremorDuration,
                        Math.max(0, tremor.getAmplifier()),
                        false,
                        false
                ));
                player.displayClientMessage(
                        Component.literal("§7[不完善的预知眼] 转化被阻止"),
                        true
                );
            }
        }

        if (hasAnyTremorEffect(target)) {
            explodeSingleTarget(player, target, currentTick);
        }

        player.getPersistentData().putLong("imperfectEyeLastConvert", currentTick);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtImperfectEyeCritExplode(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasImperfectForesightEye")) return;

        if (!player.getPersistentData().getBoolean("poiseCritThisAttack")) return;

        if (hasAnyTremorEffect(target)) {
            explodeSingleTarget(player, target, player.level().getGameTime());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtImperfectEyeDamage(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;

        if (!player.getPersistentData().getBoolean("hasImperfectForesightEye")) return;

        event.setAmount(event.getAmount() * IMPERFECT_EYE_DAMAGE_MULTIPLIER);

        if (target.hasEffect(MobEffects.WEAKNESS)) {
            event.setAmount(event.getAmount() * IMPERFECT_EYE_WEAKNESS_MULTIPLIER);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtInstantForesight(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.hasEffect(Registration.INSTANT_FORESIGHT.get())) return;

        event.setAmount(event.getAmount() + IMPERFECT_EYE_BUFF_ADD_DAMAGE);

        event.setAmount(event.getAmount() * IMPERFECT_EYE_BUFF_FINAL_MULTIPLIER);

        MobEffectInstance burn = target.getEffect(Registration.BURN.get());
        int burnAmplifier = (burn != null) ? Math.max(0, burn.getAmplifier() + IMPERFECT_EYE_BUFF_ADD_BURN_LEVEL) : IMPERFECT_EYE_BUFF_ADD_BURN_LEVEL - 1;
        int burnDuration = (burn != null) ? burn.getDuration() + 20 : 20;
        int maxBurn = BurnEvents.getMaxBurnLevels(player);
        target.removeEffect(Registration.BURN.get());
        target.addEffect(new MobEffectInstance(
                Registration.BURN.get(),
                burnDuration,
                Math.min(burnAmplifier, maxBurn - 1),
                false,
                false
        ));

        MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
        int tremorAmplifier = (tremor != null) ? Math.max(0, tremor.getAmplifier() + IMPERFECT_EYE_BUFF_ADD_TREMOR_LEVEL) : IMPERFECT_EYE_BUFF_ADD_TREMOR_LEVEL - 1;
        int tremorDuration = (tremor != null) ? tremor.getDuration() + 20 : 20;
        target.removeEffect(Registration.TREMOR.get());
        target.addEffect(new MobEffectInstance(
                Registration.TREMOR.get(),
                tremorDuration,
                Math.min(tremorAmplifier, MAX_TREMOR_LEVEL - 1),
                false,
                false
        ));
    }

    // ==================== 单目标引爆（public static，供TremorEvents2调用） ====================

    public static void explodeSingleTarget(Player player, LivingEntity target, long currentTick) {
        if (target == null || player == null) return;

        MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
        int level = (tremor != null) ? Math.max(0, tremor.getAmplifier() + 1) : 0;
        int duration = (tremor != null) ? tremor.getDuration() : 0;

        float damage = level;
        if (damage > 0) {
            damage *= getHeatingHammerExplodeMultiplier(player, target);
            target.hurt(target.damageSources().magic(), damage);
        }

        if (tremor != null) {
            int newDuration = Math.max(duration - 20, 0);
            if (newDuration <= 0) {
                target.removeEffect(Registration.TREMOR.get());
            } else {
                target.removeEffect(Registration.TREMOR.get());
                target.addEffect(new MobEffectInstance(
                        Registration.TREMOR.get(),
                        newDuration,
                        Math.max(0, tremor.getAmplifier()),
                        false,
                        false
                ));
            }
        }

        reduceSpecialTremorDuration(target, Registration.TREMOR_BURN.get(), 20);
        reduceSpecialTremorDuration(target, Registration.TREMOR_BLEED.get(), 20);
        reduceSpecialTremorDuration(target, Registration.TREMOR_ECHO.get(), 20);
        reduceSpecialTremorDuration(target, Registration.TREMOR_COLLAPSE.get(), 20);
        reduceSpecialTremorDuration(target, Registration.TREMOR_CHAIN.get(), 20);

        applySilverWatchCaseTremorBoost(player, target);

        applyTremorBurn(player, target);
        applyTremorBleed(player, target);
        applyTremorEcho(player, target);
        applyTremorCollapse(target);
        applyTremorChain(target);

        player.level().playSound(
                null,
                player.blockPosition(),
                Sounds.TREMOR_EXPLODE.get(),
                SoundSource.PLAYERS,
                1.0f,
                1.0f
        );

        if (player.getPersistentData().getBoolean("hasOfferingCigar")) {
            player.getPersistentData().putBoolean("offeringCigarNextAttackBoost", true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtOfferingCigar(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasOfferingCigar")) return;

        event.setAmount(event.getAmount() * OFFERING_CIGAR_DAMAGE_MULTIPLIER);

        event.setAmount(event.getAmount() + OFFERING_CIGAR_ADD_DAMAGE);

        MobEffectInstance tremorBurn = target.getEffect(Registration.TREMOR_BURN.get());
        if (tremorBurn != null) {
            int level = Math.max(0, tremorBurn.getAmplifier() + 1);
            if (level >= OFFERING_CIGAR_TREMOR_BURN_THRESHOLD) {
                event.setAmount(event.getAmount() * OFFERING_CIGAR_TREMOR_BURN_MULTIPLIER);
            }
        }

        long ammoBoostEnd = player.getPersistentData().getLong("meleeCombatManualDamageBoostEnd");
        long currentTick = player.level().getGameTime();
        if (currentTick <= ammoBoostEnd) {
            event.setAmount(event.getAmount() * OFFERING_CIGAR_AMMO_MULTIPLIER);
            event.setAmount(event.getAmount() * OFFERING_CIGAR_AMMO_FINAL_MULTIPLIER);
        }

        if (player.getPersistentData().getBoolean("offeringCigarNextAttackBoost")) {
            event.setAmount(event.getAmount() + OFFERING_CIGAR_EXPLODE_NEXT_ATTACK_ADD);
            player.getPersistentData().putBoolean("offeringCigarNextAttackBoost", false);
        }

        event.setAmount(event.getAmount() * OFFERING_CIGAR_FINAL_MULTIPLIER);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtSilverWatchCase(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasSilverWatchCase")) return;

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);
        if (enchantLevel <= 0) return;

        event.setAmount(event.getAmount() * SILVER_WATCH_CASE_DAMAGE_MULTIPLIER);
    }

    @SubscribeEvent
    public static void onLivingHurtHandMirror(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasHandMirror")) return;

        long currentTick = player.level().getGameTime();

        target.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN,
                HAND_MIRROR_SLOW_DURATION,
                HAND_MIRROR_SLOW_AMPLIFIER,
                false,
                false
        ));

        target.getPersistentData().putLong("handMirrorDamageEnd", currentTick + HAND_MIRROR_DAMAGE_DURATION);

        if (target.hasEffect(MobEffects.WEAKNESS)) {
            MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
            if (tremor != null) {
                int currentLevel = Math.max(0, tremor.getAmplifier() + 1);
                int doubledLevel = Math.min(currentLevel * 2, MAX_TREMOR_LEVEL);
                target.removeEffect(Registration.TREMOR.get());
                target.addEffect(new MobEffectInstance(
                        Registration.TREMOR.get(),
                        HAND_MIRROR_TREMOR_DOUBLE_DURATION,
                        doubledLevel - 1,
                        false,
                        false
                ));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtHandMirrorDamageBoost(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;

        long endTime = target.getPersistentData().getLong("handMirrorDamageEnd");
        long currentTick = target.level().getGameTime();

        if (currentTick <= endTime) {
            event.setAmount(event.getAmount() * HAND_MIRROR_DAMAGE_MULTIPLIER);
        }
    }

    @SubscribeEvent
    public static void onLivingHurtOilyWrench(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasOilyWrench")) return;

        if (!player.getMainHandItem().isEmpty()) return;

        MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
        if (tremor != null) {
            int newAmplifier = Math.min(Math.max(0, tremor.getAmplifier() + OILY_WRENCH_ADD_TREMOR_LEVEL), MAX_TREMOR_LEVEL - 1);
            int newDuration = tremor.getDuration();
            target.removeEffect(Registration.TREMOR.get());
            target.addEffect(new MobEffectInstance(
                    Registration.TREMOR.get(),
                    newDuration,
                    newAmplifier,
                    false,
                    false
            ));
        } else {
            target.addEffect(new MobEffectInstance(
                    Registration.TREMOR.get(),
                    OILY_WRENCH_TREMOR_DURATION,
                    OILY_WRENCH_ADD_TREMOR_LEVEL - 1,
                    false,
                    false
            ));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtOilyWrenchDamage(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasOilyWrench")) return;

        if (!player.getMainHandItem().isEmpty()) return;

        event.setAmount(event.getAmount() * OILY_WRENCH_DAMAGE_MULTIPLIER);
    }

    @SubscribeEvent
    public static void onLivingHurtFadedWatchCase(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasFadedWatchCase")) return;

        if (!player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) return;

        event.setAmount(event.getAmount() + FADED_WATCH_CASE_ADD_DAMAGE);

        long lastTrigger = player.getPersistentData().getLong("fadedWatchCaseLastTrigger");
        long currentTick = player.level().getGameTime();

        if (currentTick - lastTrigger >= FADED_WATCH_CASE_COOLDOWN) {
            MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
            int newAmplifier = (tremor != null)
                    ? Math.min(Math.max(0, tremor.getAmplifier() + FADED_WATCH_CASE_ADD_TREMOR_LEVEL), MAX_TREMOR_LEVEL - 1)
                    : FADED_WATCH_CASE_ADD_TREMOR_LEVEL - 1;
            int newDuration = (tremor != null) ? tremor.getDuration() + FADED_WATCH_CASE_ADD_TREMOR_DURATION : FADED_WATCH_CASE_ADD_TREMOR_DURATION;

            target.removeEffect(Registration.TREMOR.get());
            target.addEffect(new MobEffectInstance(
                    Registration.TREMOR.get(),
                    newDuration,
                    newAmplifier,
                    false,
                    false
            ));

            player.getPersistentData().putLong("fadedWatchCaseLastTrigger", currentTick);
        }
    }

    @SubscribeEvent
    public static void onLivingTickEtchedHand(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (!player.getPersistentData().getBoolean("hasEtchedHand")) return;

        long currentTick = player.level().getGameTime();
        long lastTrigger = player.getPersistentData().getLong("etchedHandLastTrigger");

        if (currentTick - lastTrigger < ETCHED_HAND_INTERVAL) return;

        AABB aabb = new AABB(player.blockPosition()).inflate(ETCHED_HAND_RADIUS);
        List<LivingEntity> targets = player.level().getEntitiesOfClass(
                LivingEntity.class, aabb,
                e -> e != player && e.isAlive()
        );

        if (targets.isEmpty()) return;

        for (LivingEntity target : targets) {
            MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
            if (tremor != null) {
                int newAmplifier = Math.min(Math.max(0, tremor.getAmplifier() + ETCHED_HAND_ADD_TREMOR_LEVEL), MAX_TREMOR_LEVEL - 1);
                int newDuration = tremor.getDuration();
                target.removeEffect(Registration.TREMOR.get());
                target.addEffect(new MobEffectInstance(
                        Registration.TREMOR.get(),
                        newDuration,
                        newAmplifier,
                        false,
                        false
                ));
            } else {
                target.addEffect(new MobEffectInstance(
                        Registration.TREMOR.get(),
                        ETCHED_HAND_TREMOR_DURATION,
                        ETCHED_HAND_ADD_TREMOR_LEVEL - 1,
                        false,
                        false
                ));
            }
        }

        player.getPersistentData().putLong("etchedHandLastTrigger", currentTick);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtEtchedHand(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasEtchedHand")) return;

        MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
        int tremorLevel = (tremor != null) ? Math.max(0, tremor.getAmplifier() + 1) : 0;

        int specialLevel = 0;
        MobEffectInstance tremorBurn = target.getEffect(Registration.TREMOR_BURN.get());
        if (tremorBurn != null) specialLevel = Math.max(specialLevel, Math.max(0, tremorBurn.getAmplifier() + 1));
        MobEffectInstance tremorBleed = target.getEffect(Registration.TREMOR_BLEED.get());
        if (tremorBleed != null) specialLevel = Math.max(specialLevel, Math.max(0, tremorBleed.getAmplifier() + 1));
        MobEffectInstance tremorEcho = target.getEffect(Registration.TREMOR_ECHO.get());
        if (tremorEcho != null) specialLevel = Math.max(specialLevel, Math.max(0, tremorEcho.getAmplifier() + 1));
        MobEffectInstance tremorCollapse = target.getEffect(Registration.TREMOR_COLLAPSE.get());
        if (tremorCollapse != null) specialLevel = Math.max(specialLevel, Math.max(0, tremorCollapse.getAmplifier() + 1));
        MobEffectInstance tremorChain = target.getEffect(Registration.TREMOR_CHAIN.get());
        if (tremorChain != null) specialLevel = Math.max(specialLevel, Math.max(0, tremorChain.getAmplifier() + 1));

        int maxLevel = Math.max(tremorLevel, specialLevel);

        if (maxLevel >= ETCHED_HAND_TREMOR_THRESHOLD) {
            event.setAmount(event.getAmount() * ETCHED_HAND_DAMAGE_MULTIPLIER);
        }
    }

    @SubscribeEvent
    public static void onLivingHurtRustedHandApply(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasRustedHand")) return;

        long lastTrigger = player.getPersistentData().getLong("rustedHandLastTrigger");
        long currentTick = player.level().getGameTime();

        if (currentTick - lastTrigger < RUSTED_HAND_COOLDOWN) return;

        MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
        int newAmplifier = (tremor != null)
                ? Math.min(Math.max(0, tremor.getAmplifier() + RUSTED_HAND_ADD_TREMOR_LEVEL), MAX_TREMOR_LEVEL - 1)
                : RUSTED_HAND_ADD_TREMOR_LEVEL - 1;
        int newDuration = (tremor != null)
                ? tremor.getDuration() + RUSTED_HAND_ADD_TREMOR_DURATION
                : RUSTED_HAND_ADD_TREMOR_DURATION;

        target.removeEffect(Registration.TREMOR.get());
        target.addEffect(new MobEffectInstance(
                Registration.TREMOR.get(),
                newDuration,
                newAmplifier,
                false,
                false
        ));

        player.getPersistentData().putLong("rustedHandLastTrigger", currentTick);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtRustedHandReduce(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Player player)) return;

        if (!player.getPersistentData().getBoolean("hasRustedHand")) return;

        LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity
                ? (LivingEntity) event.getSource().getEntity()
                : null;
        if (attacker == null) return;

        MobEffectInstance tremor = attacker.getEffect(Registration.TREMOR.get());
        int tremorLevel = (tremor != null) ? Math.max(0, tremor.getAmplifier() + 1) : 0;

        if (tremorLevel >= RUSTED_HAND_TREMOR_THRESHOLD) {
            event.setAmount(event.getAmount() * RUSTED_HAND_DAMAGE_REDUCTION);
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
                newAmplifier = Math.min(Math.max(0, currentTremor.getAmplifier() + TREMOR_COUPLING_ADD_AMPLIFIER - 1), MAX_TREMOR_LEVEL - 1);
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
            if (!hasAnyTremorEffect(target)) {
                continue;
            }

            MobEffectInstance tremor = target.getEffect(Registration.TREMOR.get());
            int level = (tremor != null) ? Math.max(0, tremor.getAmplifier() + 1) : 0;
            int duration = (tremor != null) ? tremor.getDuration() : 0;

            float damage = level;
            if (damage > 0) {
                damage *= getHeatingHammerExplodeMultiplier(player, target);
                target.hurt(target.damageSources().magic(), damage);
            }

            if (tremor != null) {
                int newDuration = Math.max(duration - 20 * 1, 0);
                if (newDuration <= 0) {
                    target.removeEffect(Registration.TREMOR.get());
                } else {
                    target.removeEffect(Registration.TREMOR.get());
                    target.addEffect(new MobEffectInstance(
                            Registration.TREMOR.get(),
                            newDuration,
                            Math.max(0, tremor.getAmplifier()),
                            false,
                            false
                    ));
                }
            }

            reduceSpecialTremorDuration(target, Registration.TREMOR_BURN.get(), 20);
            reduceSpecialTremorDuration(target, Registration.TREMOR_BLEED.get(), 20);
            reduceSpecialTremorDuration(target, Registration.TREMOR_ECHO.get(), 20);
            reduceSpecialTremorDuration(target, Registration.TREMOR_COLLAPSE.get(), 20);
            reduceSpecialTremorDuration(target, Registration.TREMOR_CHAIN.get(), 20);

            applySilverWatchCaseTremorBoost(player, target);

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
            applyTremorBurn(player, target);
            applyTremorBleed(player, target);
            applyTremorEcho(player, target);
            applyTremorCollapse(target);
            applyTremorChain(target);

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

        if (player.getPersistentData().getBoolean("hasOfferingCigar")) {
            player.getPersistentData().putBoolean("offeringCigarNextAttackBoost", true);
        }

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
            event.setAmount(event.getAmount() * MELTED_EYEBALL_MULTIPLIER);
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

        int tremorLevel = Math.max(0, tremor.getAmplifier() + 1);
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtTremorCollapse(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;

        long endTime = target.getPersistentData().getLong("tremorCollapseEnd");
        long currentTick = target.level().getGameTime();

        if (currentTick <= endTime) {
            float multiplier = target.getPersistentData().getFloat("tremorCollapseMultiplier");
            if (multiplier > 1.0f) {
                event.setAmount(event.getAmount() * multiplier);
            }
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

        int tremorLevel = Math.max(0, tremor.getAmplifier() + 1);
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
            int tremorLevel = Math.max(0, tremor.getAmplifier() + 1);
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

        int tremorLevel = Math.max(0, tremor.getAmplifier() + 1);
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
            int amplifier = Math.max(0, tremor.getAmplifier());
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
        int amplifier = Math.max(0, tremor.getAmplifier());

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

        int tremorLevel = Math.max(0, selfTremor.getAmplifier() + 1);
        int tremorDuration = selfTremor.getDuration();
        float durationSeconds = tremorDuration / 20.0f;

        float extraDamage1 = Math.min((1 + durationSeconds * EIGHT_DIRECTION_BELL_MULTIPLIER) * EIGHT_DIRECTION_BELL_BASE_DAMAGE, 240.0f);
        float extraDamage2 = Math.min((durationSeconds + tremorLevel + 5) / 100.0f, 0.5f);

        event.setAmount(event.getAmount() + extraDamage1);
        event.setAmount(event.getAmount() * (1 + extraDamage2));
    }

    @SubscribeEvent
    public static void onLivingDeathEightDirectionBell(LivingDeathEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;

        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (!player.getPersistentData().getBoolean("hasEightDirectionBell")) return;

        MobEffectInstance tremor = player.getEffect(Registration.TREMOR.get());
        if (tremor == null) return;

        int duration = tremor.getDuration();
        int newDuration = duration + EIGHT_DIRECTION_BELL_KILL_DURATION;
        int amplifier = Math.max(0, tremor.getAmplifier());

        addSelfTremor(player, EIGHT_DIRECTION_BELL_KILL_DURATION, amplifier);
        player.displayClientMessage(
                Component.literal("§a[无振八方钟] 自身震颤+6秒！"),
                true
        );
    }

    // ==================== 强制纠缠定序器：伤害+5 ====================

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtForcedEntanglementSequencer(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target == player) return;

        if (!player.getPersistentData().getBoolean("hasForcedEntanglementSequencer")) return;

        event.setAmount(event.getAmount() + 5.0f);
    }
}