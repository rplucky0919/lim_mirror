package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = lim_mirror.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BleedEvents {

    private static final int MAX_LEVELS = 99;
    private static final float DAMAGE_PER_LEVEL = 1.0f;
    private static final int COOLDOWN_TICKS = 10;

    private static final Map<UUID, double[]> lastPositions = new HashMap<>();
    private static final Map<UUID, Long> lastDamageTimes = new HashMap<>();

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;

        ItemStack weapon = attacker.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.SANCHO_BLOOD.get(), weapon);
        if (enchantLevel <= 0) return;

        MobEffectInstance currentBleed = target.getEffect(Registration.BLEED.get());
        int currentAmplifier = 0;
        int currentDuration = 0;
        if (currentBleed != null) {
            currentAmplifier = currentBleed.getAmplifier() + 1;
            currentDuration = currentBleed.getDuration();
        }
        int newAmplifier = Math.min(currentAmplifier, MAX_LEVELS - 1);
        int totalDuration = currentDuration + 20;

        target.removeEffect(Registration.BLEED.get());
        target.addEffect(new MobEffectInstance(
                Registration.BLEED.get(),
                totalDuration,
                newAmplifier,
                false,
                false
        ));
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null) return;
        if (entity.level().isClientSide()) return;

        MobEffectInstance bleed = entity.getEffect(Registration.BLEED.get());
        if (bleed == null) {
            lastPositions.remove(entity.getUUID());
            lastDamageTimes.remove(entity.getUUID());
            return;
        }

        UUID uuid = entity.getUUID();
        double[] currentPos = new double[]{entity.getX(), entity.getY(), entity.getZ()};
        double[] lastPos = lastPositions.get(uuid);

        if (lastPos == null) {
            lastPositions.put(uuid, currentPos);
            lastDamageTimes.put(uuid, entity.level().getGameTime());
            return;
        }

        boolean hasMoved = Math.abs(currentPos[0] - lastPos[0]) > 0.001 ||
                Math.abs(currentPos[1] - lastPos[1]) > 0.001 ||
                Math.abs(currentPos[2] - lastPos[2]) > 0.001;

        if (hasMoved) {
            long currentTick = entity.level().getGameTime();
            long lastDamageTick = lastDamageTimes.getOrDefault(uuid, 0L);

            if (currentTick - lastDamageTick >= COOLDOWN_TICKS) {
                int level = Math.min(bleed.getAmplifier() + 1, MAX_LEVELS);
                float damage = level * DAMAGE_PER_LEVEL;

                if (entity.getAbsorptionAmount() > 0) {
                    float absorption = entity.getAbsorptionAmount();
                    if (absorption >= damage) {
                        entity.setAbsorptionAmount(absorption - damage);
                    } else {
                        entity.setAbsorptionAmount(0);
                        float remaining = damage - absorption;
                        entity.hurt(entity.damageSources().genericKill(), remaining);
                    }
                } else {
                    entity.hurt(entity.damageSources().genericKill(), damage);
                }

                lastDamageTimes.put(uuid, currentTick);
            }
        }

        lastPositions.put(uuid, currentPos);
    }

    @SubscribeEvent
    public static void onEntityUseItem(LivingEntityUseItemEvent event) {
        LivingEntity living = event.getEntity();
        if (living == null) return;
        if (event.getItem().getItem() == Items.MILK_BUCKET) {
            living.removeEffect(Registration.BLEED.get());
            lastPositions.remove(living.getUUID());
            lastDamageTimes.remove(living.getUUID());
        }
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        MobEffectInstance effect = event.getEffectInstance();
        if (effect == null) return;
        if (!effect.getEffect().equals(Registration.BLEED.get())) return;
    }
}