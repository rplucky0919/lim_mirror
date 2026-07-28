package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
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

public class Nebulizer extends Item implements ICurioItem {

    private boolean firstTick = true;

    public Nebulizer(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            if (firstTick) {
                String currentDimension = player.level().dimension().location().toString();
                player.getPersistentData().putString("nebulizerLastDimension", currentDimension);
                firstTick = false;
            }

            String currentDimension = player.level().dimension().location().toString();
            String lastDimension = player.getPersistentData().getString("nebulizerLastDimension");

            if (!currentDimension.equals(lastDimension)) {
                MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());

                int newAmplifier;
                if (currentPoise != null) {
                    newAmplifier = currentPoise.getAmplifier() + 5;
                } else {
                    newAmplifier = 0;
                }

                int currentDuration = 0;
                if (currentPoise != null) {
                    currentDuration = currentPoise.getDuration();
                }
                int totalDuration = currentDuration + 20 * 5;

                player.removeEffect(Registration.POISE.get());
                player.addEffect(new MobEffectInstance(
                        Registration.POISE.get(),
                        totalDuration,
                        newAmplifier,
                        false,
                        false
                ));

                player.getPersistentData().putString("nebulizerLastDimension", currentDimension);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putString("nebulizerLastDimension", "");
            firstTick = true;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7首次进入一个新维度时"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7增加5秒呼吸法和5层呼吸法强度"));
    }
}