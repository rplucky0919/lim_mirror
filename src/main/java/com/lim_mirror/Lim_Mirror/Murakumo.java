package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class Murakumo extends Item implements ICurioItem {

    private static final int INTERVAL = 20 * 30;
    private static final int SLASHING_DURATION = 20 * 4;
    private static final int SLASHING_AMPLIFIER = 1;
    private static final int MAX_HEALTH_BONUS = 5;
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("c1d2e3f4-a5b6-7890-cdef-123456789012");

    public Murakumo(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (!(living instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        player.getPersistentData().putBoolean("hasMurakumo", true);

        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof SwordItem)) return;

        long currentTick = player.level().getGameTime();
        long lastTrigger = player.getPersistentData().getLong("murakumoLastTrigger");

        // ===== 每30秒触发 =====
        if (currentTick - lastTrigger >= INTERVAL) {
            // ===== 1. 耐久附魔检测（由事件处理，这里设置标记） =====
            player.getPersistentData().putBoolean("murakumoDurabilityChecked", true);
            player.getPersistentData().putLong("murakumoDurabilityEnd", currentTick + INTERVAL);

            // ===== 2. 增加 4 秒 2 级斩击伤害提升 =====
            MobEffectInstance existing = player.getEffect(Registration.SLASHING_POWER.get());
            int newDuration = SLASHING_DURATION;
            int newAmplifier = SLASHING_AMPLIFIER;

            if (existing != null) {
                newDuration = existing.getDuration() + SLASHING_DURATION;
                newAmplifier = existing.getAmplifier() + 1;
            }

            player.removeEffect(Registration.SLASHING_POWER.get());
            player.addEffect(new MobEffectInstance(
                    Registration.SLASHING_POWER.get(),
                    newDuration,
                    newAmplifier,
                    false,
                    false
            ));

            player.getPersistentData().putLong("murakumoLastTrigger", currentTick);
        }

        // ===== 3. 每失去 10% 生命，伤害+1，护甲+1（最多5次） =====
        float maxHealth = player.getMaxHealth();
        float currentHealth = player.getHealth();
        float lostPercent = ((maxHealth - currentHealth) / maxHealth) * 100;
        int lostBonus = Math.min((int)(lostPercent / 10), MAX_HEALTH_BONUS);

        player.getPersistentData().putInt("murakumoLostBonus", lostBonus);

        // 护甲加成
        if (lostBonus > 0) {
            double armorBonus = lostBonus;
            AttributeModifier existingMod = player.getAttribute(Attributes.ARMOR).getModifier(ARMOR_MODIFIER_UUID);

            if (existingMod != null) {
                player.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
            }

            AttributeModifier armorModifier = new AttributeModifier(
                    ARMOR_MODIFIER_UUID,
                    "murakumo_armor_bonus",
                    armorBonus,
                    AttributeModifier.Operation.ADDITION
            );
            player.getAttribute(Attributes.ARMOR).addTransientModifier(armorModifier);
        } else {
            if (player.getAttribute(Attributes.ARMOR).getModifier(ARMOR_MODIFIER_UUID) != null) {
                player.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasMurakumo", false);
            player.getPersistentData().putBoolean("murakumoFriendlyKill", false);
            if (player.getAttribute(Attributes.ARMOR).getModifier(ARMOR_MODIFIER_UUID) != null) {
                player.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.murakumo.1", "§7手持剑类武器时："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.murakumo.2", "§7每 30 秒："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.murakumo.3", "§7  若武器有耐久附魔，造成伤害 §c+（80/耐久等级）%"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.murakumo.4", "§7  获得 §c4 秒 §72 级斩击伤害提升"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.murakumo.5", "§7每失去 §c10% §7生命："));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.murakumo.6", "§7  造成伤害 §c+1§7，护甲 §a+1 §7（最多 5 次）"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.murakumo.7", "§7击杀友方生物后 §c30 秒内 §7造成伤害 §c+20%"));
    }
}