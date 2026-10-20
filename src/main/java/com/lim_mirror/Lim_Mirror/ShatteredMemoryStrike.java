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

public class ShatteredMemoryStrike extends Item implements ICurioItem {

    public ShatteredMemoryStrike(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasShatteredMemoryStrike", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasShatteredMemoryStrike", false);
            player.getPersistentData().putBoolean("shatteredMemoryStrikeNextAttack", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.shattered_memory_strike.1", "§7造成伤害 §c+6"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.shattered_memory_strike.2", "§7最终伤害 §c+10%"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.shattered_memory_strike.3", "§7若空手或携带打击武器："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.shattered_memory_strike.4", "§7  伤害 §c+20%"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.shattered_memory_strike.5", "§7造成打击伤害后："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.shattered_memory_strike.6", "§7  下一次攻击伤害随机增加 §c10%~30%"));
    }
}