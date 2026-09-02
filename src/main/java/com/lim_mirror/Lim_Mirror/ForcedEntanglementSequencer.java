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

public class ForcedEntanglementSequencer extends Item implements ICurioItem {

    private static final int CONVERT_DURATION = 20 * 5;  // 5秒
    private static final int COOLDOWN = 20 * 10;          // 10秒

    public ForcedEntanglementSequencer(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasForcedEntanglementSequencer", true);

            long currentTick = player.level().getGameTime();
            long lastTrigger = player.getPersistentData().getLong("forcedEntanglementSequencerLastTrigger");

            if (currentTick - lastTrigger < COOLDOWN) return;

            MobEffectInstance tremor = player.getEffect(Registration.TREMOR.get());
            if (tremor == null) return;

            int currentDuration = tremor.getDuration();
            if (currentDuration < CONVERT_DURATION) return;

            // ===== 扣除 5 秒震颤 =====
            int newDuration = currentDuration - CONVERT_DURATION;
            int amplifier = tremor.getAmplifier();
            player.removeEffect(Registration.TREMOR.get());
            if (newDuration > 0) {
                player.addEffect(new MobEffectInstance(
                        Registration.TREMOR.get(),
                        newDuration,
                        amplifier,
                        false,
                        false
                ));
            }

            // ===== 添加 5 秒震颤-灼热 =====
            MobEffectInstance existingBurn = player.getEffect(Registration.TREMOR_BURN.get());
            int burnDuration = CONVERT_DURATION;
            int burnAmplifier = 0;
            if (existingBurn != null) {
                burnDuration = existingBurn.getDuration() + CONVERT_DURATION;
                burnAmplifier = Math.min(existingBurn.getAmplifier() + 1, 98);
            }
            player.removeEffect(Registration.TREMOR_BURN.get());
            player.addEffect(new MobEffectInstance(
                    Registration.TREMOR_BURN.get(),
                    burnDuration,
                    burnAmplifier,
                    false,
                    false
            ));

            player.getPersistentData().putLong("forcedEntanglementSequencerLastTrigger", currentTick);
            player.displayClientMessage(
                    Component.literal("§d[强制纠缠定序器] 将 5 秒震颤转化为震颤-灼热！"),
                    true
            );
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasForcedEntanglementSequencer", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§7当自身带有震颤时"));
        tooltip.add(Component.literal("§7将 5 秒震颤转化为 5 秒震颤-灼热"));
        tooltip.add(Component.literal("§7冷却 10 秒"));
        tooltip.add(Component.literal("§7造成伤害 +5"));
    }
}