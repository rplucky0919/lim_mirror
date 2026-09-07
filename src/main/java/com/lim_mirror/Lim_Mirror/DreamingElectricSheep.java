package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
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

public class DreamingElectricSheep extends Item implements ICurioItem {

    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private static final AttributeModifier ARMOR_MODIFIER = new AttributeModifier(
            ARMOR_MODIFIER_UUID,
            "dreaming_electric_sheep_armor",
            4.0,
            AttributeModifier.Operation.ADDITION
    );

    public DreamingElectricSheep(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (!(living instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        player.getPersistentData().putBoolean("hasDreamingElectricSheep", true);

        if (!player.getAttribute(Attributes.ARMOR).hasModifier(ARMOR_MODIFIER)) {
            player.getAttribute(Attributes.ARMOR).addTransientModifier(ARMOR_MODIFIER);
        }
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (!(living instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (!player.getAttribute(Attributes.ARMOR).hasModifier(ARMOR_MODIFIER)) {
            player.getAttribute(Attributes.ARMOR).addTransientModifier(ARMOR_MODIFIER);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (!(living instanceof Player player)) return;

        player.getPersistentData().putBoolean("hasDreamingElectricSheep", false);

        if (player.getAttribute(Attributes.ARMOR).hasModifier(ARMOR_MODIFIER)) {
            player.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_MODIFIER);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§7手持剑类武器击杀敌人时"));
        tooltip.add(Component.literal("§7获得 §c斩击威力提升 II§r§7，持续 60 秒（冷却 30 秒）"));
        tooltip.add(Component.literal("§7玩家增加 §a4 点护甲"));
        tooltip.add(Component.literal("§7造成伤害 §c+20%"));
    }
}