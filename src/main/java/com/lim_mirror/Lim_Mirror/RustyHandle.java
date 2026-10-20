package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
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

public class RustyHandle extends Item implements ICurioItem {

    private static final int INTERVAL = 20 * 30; // 30 秒
    private static final int DURATION = 20 * 4; // 4 秒
    private static final int AMPLIFIER = 1; // 2 级

    public RustyHandle(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (!(living instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        player.getPersistentData().putBoolean("hasRustyHandle", true);

        // 前置条件：手持剑类武器
        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof net.minecraft.world.item.SwordItem)) return;

        long currentTick = player.level().getGameTime();
        long lastTrigger = player.getPersistentData().getLong("rustyHandleLastTrigger");

        if (currentTick - lastTrigger >= INTERVAL) {
            // 增加 4 秒 2 级斩击伤害提升（叠加）
            MobEffectInstance existing = player.getEffect(Registration.SLASHING_POWER.get());
            int newDuration = DURATION;
            int newAmplifier = AMPLIFIER;

            if (existing != null) {
                newDuration = existing.getDuration() + DURATION;
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

            player.getPersistentData().putLong("rustyHandleLastTrigger", currentTick);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasRustyHandle", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.rusty_handle.1", "§7手持剑类武器时："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.rusty_handle.2", "§7每 30 秒增加 §c4 秒 §72 级斩击伤害提升"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.rusty_handle.3", "§8- 等级可叠加，无上限"));
    }
}