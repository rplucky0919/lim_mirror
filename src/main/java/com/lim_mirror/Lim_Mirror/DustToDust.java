package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class DustToDust extends Item implements ICurioItem {

    public DustToDust(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasDustToDust", true);

            long lastTrigger = player.getPersistentData().getLong("dustToDustLastTick");
            long currentTick = player.level().getGameTime();

            if (lastTrigger == 0) {
                player.getPersistentData().putLong("dustToDustLastTick", currentTick);
                return;
            }

            if (currentTick - lastTrigger >= 20 * 10) {
                AABB aabb = new AABB(player.blockPosition()).inflate(1.5);
                List<LivingEntity> entities = player.level().getEntitiesOfClass(
                        LivingEntity.class, aabb,
                        e -> e != player && e.isAlive()
                );

                for (LivingEntity target : entities) {
                    MobEffectInstance burn = target.getEffect(Registration.BURN.get());
                    if (burn != null) {
                        int newAmplifier = burn.getAmplifier() + 2;
                        int currentDuration = burn.getDuration();
                        int totalDuration = currentDuration + 20;

                        target.removeEffect(Registration.BURN.get());
                        target.addEffect(new MobEffectInstance(
                                Registration.BURN.get(),
                                totalDuration,
                                Math.min(newAmplifier, 98),
                                false,
                                false
                        ));
                    }
                }

                player.getPersistentData().putLong("dustToDustLastTick", currentTick);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasDustToDust", false);
            player.getPersistentData().putLong("dustToDustLastTick", 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7每10秒检测3×3×3范围内带有烧伤的生物"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7对目标增加2层烧伤强度"));
    }
}