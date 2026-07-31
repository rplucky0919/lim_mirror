package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = lim_mirror.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BurnEvents {

    private static final int MAX_LEVELS = 99;
    private static final int INTERVAL_TICKS = 20;
    private static final float DAMAGE_PER_LEVEL = 1.0f;

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

        if (attacker.getPersistentData().getBoolean("hasRequiem")) {
            addAmplifier = 2;
            addDuration = 40;
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
        }
        int totalDuration = currentDuration + addDuration;

        target.removeEffect(Registration.BURN.get());
        target.addEffect(new MobEffectInstance(
                Registration.BURN.get(),
                totalDuration,
                currentAmplifier,
                false,
                false
        ));
    }

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
                    Math.min(newAmplifier, MAX_LEVELS - 1),
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
                        3,
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
                    Math.min(newAmplifier, MAX_LEVELS - 1),
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
                        0,
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
                    Math.min(newAmplifier, MAX_LEVELS - 1),
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
                if (level >= 99) {
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

        int level = Math.min(burn.getAmplifier() + 1, MAX_LEVELS);
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