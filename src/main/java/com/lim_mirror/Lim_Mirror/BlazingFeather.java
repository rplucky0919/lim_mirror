package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class BlazingFeather extends Item implements ICurioItem {

    public BlazingFeather(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasBlazingFeather", true);

            long lastTrigger = player.getPersistentData().getLong("blazingFeatherLastTick");
            long currentTick = player.level().getGameTime();

            if (lastTrigger == 0) {
                player.getPersistentData().putLong("blazingFeatherLastTick", currentTick);
                return;
            }

            if (currentTick - lastTrigger >= 20 * 30) {
                LivingEntity target = null;
                // 获取玩家攻击的目标（通过最近攻击的实体或当前瞄准的实体）
                // 简化实现：检测玩家周围5格内最近的有烧伤的实体
                double nearestDist = 6.0;
                for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class,
                        player.getBoundingBox().inflate(5.0),
                        e -> e != player && e.isAlive())) {
                    MobEffectInstance burn = entity.getEffect(Registration.BURN.get());
                    if (burn != null) {
                        int level = burn.getAmplifier() + 1;
                        if (level >= 20) {
                            double dist = player.distanceTo(entity);
                            if (dist < nearestDist) {
                                nearestDist = dist;
                                target = entity;
                            }
                        }
                    }
                }

                if (target != null) {
                    MobEffectInstance currentBurn = target.getEffect(Registration.BURN.get());
                    if (currentBurn != null) {
                        int currentAmplifier = currentBurn.getAmplifier();
                        int newAmplifier = (int)(currentAmplifier * 2.5f);
                        int currentDuration = currentBurn.getDuration();

                        target.removeEffect(Registration.BURN.get());
                        target.addEffect(new MobEffectInstance(
                                Registration.BURN.get(),
                                currentDuration,
                                Math.min(newAmplifier, 98),
                                false,
                                false
                        ));
                    }
                }

                player.getPersistentData().putLong("blazingFeatherLastTick", currentTick);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasBlazingFeather", false);
            player.getPersistentData().putLong("blazingFeatherLastTick", 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7主手有火柴之焰时攻击增加4级烧伤"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7每30秒检测烧伤≥20级目标，将其等级×2.5"));
    }
}