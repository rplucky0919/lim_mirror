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

public class OldHandle extends Item implements ICurioItem {

    private static final int INTERVAL = 20 * 30;
    private static final int DURATION = 20 * 2;
    private static final int AMPLIFIER = 0;

    public OldHandle(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (!(living instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        player.getPersistentData().putBoolean("hasOldHandle", true);

        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof SwordItem)) return;

        long currentTick = player.level().getGameTime();
        long lastTrigger = player.getPersistentData().getLong("oldHandleLastTrigger");

        if (currentTick - lastTrigger >= INTERVAL) {
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

            player.getPersistentData().putLong("oldHandleLastTrigger", currentTick);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasOldHandle", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.old_handle.1", "§7手持剑类武器时："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.old_handle.2", "§7每 30 秒获得 §c2 秒 §71 级斩击伤害提升"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.old_handle.3", "§7若武器带有 §d亡灵杀手 §7附魔："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.old_handle.4", "§7  造成伤害 §c+20%"));
    }
}