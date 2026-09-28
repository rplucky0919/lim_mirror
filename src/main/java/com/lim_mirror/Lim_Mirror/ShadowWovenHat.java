package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
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

public class ShadowWovenHat extends Item implements ICurioItem {

    private static final int INTERVAL = 20 * 30;
    private static final int SPEED_DURATION = 20 * 30;
    private static final int SPEED_AMPLIFIER = 1;

    public ShadowWovenHat(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (!(living instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        player.getPersistentData().putBoolean("hasShadowWovenHat", true);

        long currentTick = player.level().getGameTime();
        long lastTrigger = player.getPersistentData().getLong("shadowWovenHatLastTrigger");

        if (currentTick - lastTrigger >= INTERVAL) {
            MobEffectInstance existing = player.getEffect(MobEffects.MOVEMENT_SPEED);
            int newDuration = SPEED_DURATION;
            int newAmplifier = SPEED_AMPLIFIER;

            if (existing != null) {
                newDuration = existing.getDuration() + SPEED_DURATION;
                newAmplifier = existing.getAmplifier() + 2;
            }

            player.removeEffect(MobEffects.MOVEMENT_SPEED);
            player.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SPEED,
                    newDuration,
                    newAmplifier,
                    false,
                    false
            ));

            player.getPersistentData().putLong("shadowWovenHatLastTrigger", currentTick);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasShadowWovenHat", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§7每 30 秒增加 §b2 级 §7迅捷"));
        tooltip.add(Component.literal("§7对敌方造成伤害 §c+（迅捷等级/3）% §7（最多 30%）"));
        tooltip.add(Component.literal("§7自身受到伤害 §a-（迅捷等级/3）% §7（最多 30%）"));
    }
}