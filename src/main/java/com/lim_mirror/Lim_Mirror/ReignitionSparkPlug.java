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

public class ReignitionSparkPlug extends Item implements ICurioItem {

    // ==================== 护甲降低修饰符 ====================
    public static final AttributeModifier ARMOR_REDUCE = new AttributeModifier(
            UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef"),
            "reignition_sparkplug_armor_reduce",
            -2.0,
            AttributeModifier.Operation.ADDITION
    );

    public ReignitionSparkPlug(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasReignitionSparkPlug", true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasReignitionSparkPlug", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§7若武器带有火柴之焰附魔"));
        tooltip.add(Component.literal("§7造成伤害+5，最终伤害×1.2"));
        tooltip.add(Component.literal("§7施加烧伤额外+1s，降低敌方护甲2点"));
    }
}