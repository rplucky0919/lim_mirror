package com.lim_mirror.Lim_Mirror;

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

public class ScorchedDisc extends Item implements ICurioItem {

    public static final UUID ARMOR_REDUCE_UUID = UUID.fromString("b1c2d3e4-f5a6-7890-abcd-ef1234567891");
    public static final AttributeModifier ARMOR_REDUCE = new AttributeModifier(
            ARMOR_REDUCE_UUID,
            "scorched_disc_armor_reduce",
            -8.0,
            AttributeModifier.Operation.ADDITION
    );

    public ScorchedDisc(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasScorchedDisc", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasScorchedDisc", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7攻击烧伤目标时使其护甲减少8"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7可与炎陵的减16叠加"));
    }
}