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

public class BlackIronBarding extends Item implements ICurioItem {

    private static final int ABSORPTION_HEARTS = 15;
    private static final int ABSORPTION_AMPLIFIER = ABSORPTION_HEARTS - 1;
    private static final int ABSORPTION_DURATION = 20 * 30;
    private static final int ABSORPTION_COOLDOWN = 20 * 30;

    private static final int SPEED_AMPLIFIER = 1;
    private static final int RESISTANCE_AMPLIFIER = 1;
    private static final int BUFF_DURATION = 20 * 30;
    private static final int BUFF_COOLDOWN = 20 * 30;

    public BlackIronBarding(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (!(living instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        player.getPersistentData().putBoolean("hasBlackIronBarding", true);

        long currentTick = player.level().getGameTime();

        // ===== 1. 黄心：每30秒补满到15颗 =====
        long lastAbsorption = player.getPersistentData().getLong("blackIronBardingAbsorptionLast");
        if (currentTick - lastAbsorption >= ABSORPTION_COOLDOWN) {
            MobEffectInstance absorption = player.getEffect(MobEffects.ABSORPTION);
            int currentAmplifier = (absorption != null) ? absorption.getAmplifier() : -1;

            // 如果当前吸收等级 < 14（即不足15级），补满到15级
            if (currentAmplifier < ABSORPTION_AMPLIFIER) {
                player.removeEffect(MobEffects.ABSORPTION);
                player.addEffect(new MobEffectInstance(
                        MobEffects.ABSORPTION,
                        ABSORPTION_DURATION,
                        ABSORPTION_AMPLIFIER,
                        false,
                        false,
                        true
                ));
                player.displayClientMessage(
                        Component.literal("§d[黑铁马胄] 黄心已补满！"),
                        true
                );
            }
            player.getPersistentData().putLong("blackIronBardingAbsorptionLast", currentTick);
        }

        // ===== 2. 迅捷II + 抗性提升II：每30秒刷新，独立冷却 =====
        long lastBuff = player.getPersistentData().getLong("blackIronBardingBuffLast");
        if (currentTick - lastBuff >= BUFF_COOLDOWN) {
            // 迅捷II
            player.removeEffect(MobEffects.MOVEMENT_SPEED);
            player.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SPEED,
                    BUFF_DURATION,
                    SPEED_AMPLIFIER,
                    false,
                    false,
                    true
            ));

            // 抗性提升II
            player.removeEffect(MobEffects.DAMAGE_RESISTANCE);
            player.addEffect(new MobEffectInstance(
                    MobEffects.DAMAGE_RESISTANCE,
                    BUFF_DURATION,
                    RESISTANCE_AMPLIFIER,
                    false,
                    false,
                    true
            ));

            player.getPersistentData().putLong("blackIronBardingBuffLast", currentTick);
            player.displayClientMessage(
                    Component.literal("§d[黑铁马胄] 迅捷II + 抗性提升II 已刷新！"),
                    true
            );
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasBlackIronBarding", false);
            // 不主动移除buff，让它们自然消失
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.black_iron_barding.1", "§7每30秒补满15格黄心"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.black_iron_barding.2", "§7带有黄心时，命中敌人+2级震颤"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.black_iron_barding.3", "§7每30秒获得迅捷II + 抗性提升II（持续30秒）"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.black_iron_barding.4", "§8- 黄心和buff各自独立冷却"));
    }
}