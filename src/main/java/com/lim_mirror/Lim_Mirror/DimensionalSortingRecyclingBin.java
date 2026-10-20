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

public class DimensionalSortingRecyclingBin extends Item implements ICurioItem {

    public DimensionalSortingRecyclingBin(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasDimensionalSortingRecyclingBin", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasDimensionalSortingRecyclingBin", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.dimensional_sorting_recycling_bin.1", "§7空手或携带打击武器时："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.dimensional_sorting_recycling_bin.2", "§7造成伤害 §c+3"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.dimensional_sorting_recycling_bin.3", "§7最终伤害 §c+25%"));
    }
}