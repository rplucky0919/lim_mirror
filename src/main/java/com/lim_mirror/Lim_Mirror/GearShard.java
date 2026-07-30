package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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

public class GearShard extends Item implements ICurioItem {

    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");

    public GearShard(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player && !player.level().isClientSide()) {
            player.getPersistentData().putBoolean("hasGearShard", true);

            MobEffectInstance currentPoise = player.getEffect(Registration.POISE.get());

            if (currentPoise != null) {
                player.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
                player.getAttribute(Attributes.ARMOR).addTransientModifier(
                        new AttributeModifier(ARMOR_MODIFIER_UUID, "gear_shard_armor", 4.0, AttributeModifier.Operation.ADDITION)
                );

                player.addEffect(new MobEffectInstance(
                        MobEffects.DAMAGE_RESISTANCE,
                        20 * 2,
                        0,
                        false,
                        false
                ));
            } else {
                player.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasGearShard", false);
            player.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.literal("§7有呼吸法时护甲+4"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7获得抗性提升I"));
        tooltip.add(net.minecraft.network.chat.Component.literal("§7最终伤害×1.32"));
    }
}