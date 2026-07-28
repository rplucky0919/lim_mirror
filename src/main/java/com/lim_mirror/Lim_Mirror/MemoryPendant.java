package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class MemoryPendant extends Item implements ICurioItem {

    public MemoryPendant(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            long lastTick = player.getPersistentData().getLong("memoryPendantLastTick");
            long currentTick = player.level().getGameTime();

            if (currentTick - lastTick >= 10 * 20) {
                ItemStack mainHand = player.getMainHandItem();
                int smiteLevel = mainHand.getEnchantmentLevel(Enchantments.SMITE);

                if (!mainHand.isEmpty()) {
                    int addAmplifier = smiteLevel > 0 ? 2 : 1;

                    MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());
                    int newAmplifier;
                    if (currentPoise != null) {
                        newAmplifier = currentPoise.getAmplifier() + addAmplifier;
                    } else {
                        newAmplifier = 0;
                    }

                    int currentDuration = 0;
                    if (currentPoise != null) {
                        currentDuration = currentPoise.getDuration();
                    }
                    int totalDuration = currentDuration + 20 * 2;

                    player.removeEffect(Registration.POISE.get());
                    player.addEffect(new MobEffectInstance(
                            Registration.POISE.get(),
                            totalDuration,
                            newAmplifier,
                            false,
                            false
                    ));

                    player.getPersistentData().putLong("memoryPendantLastTick", currentTick);
                }
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putLong("memoryPendantLastTick", 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7手上拿着武器时，每10秒增加1级呼吸法强度"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7若武器带有亡灵杀手，则每10秒增加2级呼吸法强度"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7可以和其他饰品叠加"));
    }
}