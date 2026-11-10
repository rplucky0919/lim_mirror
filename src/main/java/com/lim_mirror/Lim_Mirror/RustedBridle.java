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

public class RustedBridle extends Item implements ICurioItem {

    public RustedBridle(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasRustedBridle", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasRustedBridle", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.rusted_bridle.1", "§7主手武器带有 §c桑丘之血 §7附魔时："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.rusted_bridle.2", "§7  造成伤害时增加 §c3 级流血强度"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.rusted_bridle.3", "§7  若为斩击（剑类武器）："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.rusted_bridle.4", "§7    改为增加 §c2 秒 5 级流血"));
    }
}