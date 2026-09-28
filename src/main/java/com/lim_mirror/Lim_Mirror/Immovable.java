package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class Immovable extends Item implements ICurioItem {

    private static final int INTERVAL = 20 * 30;
    private static final int SLASHING_DURATION = 20 * 4;
    private static final int SLASHING_AMPLIFIER = 1;

    public Immovable(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (!(living instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        player.getPersistentData().putBoolean("hasImmovable", true);

        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof SwordItem)) return;

        long currentTick = player.level().getGameTime();
        long lastTrigger = player.getPersistentData().getLong("immovableLastTrigger");

        if (currentTick - lastTrigger >= INTERVAL) {
            MobEffectInstance existing = player.getEffect(Registration.SLASHING_POWER.get());
            int newDuration = SLASHING_DURATION;
            int newAmplifier = SLASHING_AMPLIFIER;

            if (existing != null) {
                newDuration = existing.getDuration() + SLASHING_DURATION;
                newAmplifier = existing.getAmplifier() + 1;
            }

            player.removeEffect(Registration.SLASHING_POWER.get());
            player.addEffect(new MobEffectInstance(
                    Registration.SLASHING_POWER.get(),
                    newDuration,
                    newAmplifier,
                    false,
                    false
            ));

            player.getPersistentData().putLong("immovableLastTrigger", currentTick);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasImmovable", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§7手持剑类武器时："));
        tooltip.add(Component.literal("§7每 30 秒获得 §c2 级 §74 秒斩击伤害提升"));
        tooltip.add(Component.literal("§7造成伤害 §c+35%"));
        tooltip.add(Component.literal("§7对目标额外造成 §d（呼吸法强度）§7的魔法伤害"));
    }
}