package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class UltraPrecisionTimeAccelerator extends Item implements ICurioItem {

    // 精密时间加速效果注册ID
    public static final String PRECISION_TIME_ACCELERATION = "precision_time_acceleration";

    public UltraPrecisionTimeAccelerator(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (!(living instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        player.getPersistentData().putBoolean("hasUltraPrecisionTimeAccelerator", true);

        ItemStack weapon = player.getMainHandItem();
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(Registration.BOILING_CORROSION.get(), weapon);

        if (enchantLevel > 0) {
            // ===== 迅捷 V =====
            MobEffectInstance speed = player.getEffect(MobEffects.MOVEMENT_SPEED);
            if (speed == null || speed.getAmplifier() < 4 || speed.getDuration() < 40) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SPEED,
                        60,
                        4,
                        false,
                        false
                ));
            }

            // ===== 精密时间加速（不可被牛奶清除） =====
            MobEffectInstance precision = player.getEffect(Registration.PRECISION_TIME_ACCELERATION.get());
            if (precision == null || precision.getDuration() < 40) {
                player.addEffect(new MobEffectInstance(
                        Registration.PRECISION_TIME_ACCELERATION.get(),
                        60,
                        0,
                        false,
                        false
                ));
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasUltraPrecisionTimeAccelerator", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.ultra_precision_time_accelerator.1", "§7当武器带有震颤爆发附魔时"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.ultra_precision_time_accelerator.2", "§7获得迅捷 V"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.ultra_precision_time_accelerator.3", "§7获得精密时间加速"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.ultra_precision_time_accelerator.4", "§7- 攻击伤害 +5"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.ultra_precision_time_accelerator.5", "§7- 造成伤害 +50%"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.ultra_precision_time_accelerator.6", "§8不可被牛奶清除，持续至死亡"));
    }
}