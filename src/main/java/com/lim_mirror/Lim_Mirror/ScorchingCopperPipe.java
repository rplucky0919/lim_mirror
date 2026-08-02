package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
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

public class ScorchingCopperPipe extends Item implements ICurioItem {

    public ScorchingCopperPipe(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            boolean wasEquipped = player.getPersistentData().getBoolean("hasScorchingCopperPipe");
            if (!wasEquipped) {
                player.getPersistentData().putBoolean("hasScorchingCopperPipe", true);
                BurnEvents.updateMaxLevels(player);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasScorchingCopperPipe", false);
            BurnEvents.updateMaxLevels(player);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7火柴之焰时伤害×1.3"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7额外造成 烧伤强度×1.5 的伤害"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7烧伤上限+21（可叠加）"));
    }
}