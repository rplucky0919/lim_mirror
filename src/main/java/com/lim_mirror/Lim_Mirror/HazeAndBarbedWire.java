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

public class HazeAndBarbedWire extends Item implements ICurioItem {

    public HazeAndBarbedWire(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasHazeAndBarbedWire", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasHazeAndBarbedWire", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.haze_and_barbed_wire.1", "§7主手武器带有 §c桑丘之血 §7附魔时："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.haze_and_barbed_wire.2", "§7  增加的流血强度变为 §c2.5 倍"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.haze_and_barbed_wire.3", "§7  命中使敌方获得 §b缓慢 III"));
    }
}