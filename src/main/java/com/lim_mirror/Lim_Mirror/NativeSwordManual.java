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

public class NativeSwordManual extends Item implements ICurioItem {

    public NativeSwordManual(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasNativeSwordManual", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasNativeSwordManual", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.native_sword_manual.1", "§7手持剑类武器时："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.native_sword_manual.2", "§7  玩家最终伤害 §c+20%"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.native_sword_manual.3", "§7  斩击伤害 §c+25%"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.native_sword_manual.4", "§7  每有 §b1 级 §7呼吸法，造成伤害 §c+1.5% §7（最多 §c30%§7）"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.native_sword_manual.5", "§7  造成伤害 §c+3 §7（基础伤害先加）"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.native_sword_manual.6", "§7  呼吸法暴击时增加 §c15% §7伤害（持续 2 秒）"));
    }
}