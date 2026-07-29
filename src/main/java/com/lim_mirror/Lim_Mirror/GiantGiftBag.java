package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class GiantGiftBag extends Item implements ICurioItem {

    public GiantGiftBag(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasGiantGiftBag", true);

            float health = player.getHealth();
            float maxHealth = player.getMaxHealth();
            float healthPercent = health / maxHealth * 100;

            if (healthPercent <= 10.0f) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.DAMAGE_RESISTANCE,
                        20 * 2,
                        1,
                        false,
                        false
                ));
            }

            long lastTrigger = player.getPersistentData().getLong("giftBagLastTick");
            long currentTick = player.level().getGameTime();

            if (lastTrigger == 0) {
                player.getPersistentData().putLong("giftBagLastTick", currentTick);
                return;
            }

            if (currentTick - lastTrigger >= 20 * 10) {
                MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                if (currentPoise != null) {
                    int newAmplifier = currentPoise.getAmplifier() + 2;
                    int newDuration = currentPoise.getDuration() + 20 * 2;

                    player.removeEffect(Registration.POISE.get());
                    player.addEffect(new MobEffectInstance(
                            Registration.POISE.get(),
                            newDuration,
                            newAmplifier,
                            false,
                            false
                    ));
                }
                player.getPersistentData().putLong("giftBagLastTick", currentTick);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasGiantGiftBag", false);
            player.getPersistentData().putLong("giftBagLastTick", 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7生命低于等于10%时获得抗性提升II"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7每10秒增加2秒呼吸法和2层呼吸法等级"));
    }
}