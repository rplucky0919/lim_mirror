package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class BloodyIronSpike extends Item implements ICurioItem {

    public static final UUID ARMOR_REDUCE_UUID = UUID.fromString("c4d5e6f7-a8b9-4c1d-9e2f-3a4b5c6d7e8f");
    public static final AttributeModifier ARMOR_REDUCE = new AttributeModifier(
            ARMOR_REDUCE_UUID,
            "bloody_iron_spike_armor_reduce",
            -2.0,
            AttributeModifier.Operation.ADDITION
    );

    public BloodyIronSpike(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasBloodyIronSpike", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasBloodyIronSpike", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.bloody_iron_spike.1", "§7造成伤害时："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.bloody_iron_spike.2", "§7  使目标获得 §c1 级流血强度§7，并降低其护甲 §c2 点§7（不叠加）"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.bloody_iron_spike.3", "§7若目标已带有流血："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.bloody_iron_spike.4", "§7  改为获得 §c3 级流血强度"));
    }
}