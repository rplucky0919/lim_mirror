package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
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

public class BloodMist extends Item implements ICurioItem {

    public BloodMist(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasBloodMist", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasBloodMist", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.blood_mist.1", "§7主手武器带有 §c桑丘之血 §7附魔时："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.blood_mist.2", "§7  每次攻击施加的流血强度与时间变为 §c2 倍§7（施加量）"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.blood_mist.3", "§7手持快捷栏第一格的武器时："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.blood_mist.4", "§7  造成伤害 §c+30%§7，最终伤害 §c+130%"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.blood_mist.5", "§7击杀敌方后：将流血时间与等级传播给死亡单位 10 格内所有敌方"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.blood_mist.6", "§7常驻：造成伤害 §c+10%§7，最终伤害 §c+30%"));
    }
}