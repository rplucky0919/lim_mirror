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

public class EarthToEarth extends Item implements ICurioItem {

    public EarthToEarth(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasEarthToEarth", true);

            long lastTrigger = player.getPersistentData().getLong("earthToEarthLastTick");
            long currentTick = player.level().getGameTime();

            if (lastTrigger == 0) {
                player.getPersistentData().putLong("earthToEarthLastTick", currentTick);
                return;
            }

            if (currentTick - lastTrigger >= 20 * 30) {
                AABB aabb = new AABB(player.blockPosition()).inflate(30.0);
                List<LivingEntity> entities = player.level().getEntitiesOfClass(
                        LivingEntity.class, aabb,
                        e -> e != player && e.isAlive()
                );

                for (LivingEntity target : entities) {
                    MobEffectInstance currentBurn = target.getEffect(Registration.BURN.get());
                    int newAmplifier = 11;
                    int currentDuration = 0;
                    int totalDuration = 20 * 6;

                    if (currentBurn != null) {
                        newAmplifier = currentBurn.getAmplifier() + 12;
                        currentDuration = currentBurn.getDuration();
                        totalDuration = currentDuration + 20 * 6;
                    }

                    target.removeEffect(Registration.BURN.get());
                    target.addEffect(new MobEffectInstance(
                            Registration.BURN.get(),
                            totalDuration,
                            Math.min(newAmplifier, 98),
                            false,
                            false
                    ));
                }

                player.getPersistentData().putLong("earthToEarthLastTick", currentTick);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasEarthToEarth", false);
            player.getPersistentData().putLong("earthToEarthLastTick", 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7每30秒对30格内生物增加6秒12级烧伤"));
    }
}