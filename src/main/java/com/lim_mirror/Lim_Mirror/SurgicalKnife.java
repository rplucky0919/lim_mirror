package com.lim_mirror.Lim_Mirror;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
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

public class SurgicalKnife extends Item implements ICurioItem {

    private static final int EFFECT_DURATION = 20 * 30;  // 30 秒
    private static final int COOLDOWN = 20 * 30;          // 30 秒

    public SurgicalKnife(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (!(living instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        player.getPersistentData().putBoolean("hasSurgicalKnife", true);

        long currentTick = player.level().getGameTime();
        long lastTrigger = player.getPersistentData().getLong("surgicalKnifeLastTrigger");

        // 冷却检查
        if (currentTick - lastTrigger < COOLDOWN) return;

        // ===== 条件1：手持剑类武器 =====
        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof SwordItem)) return;

        // ===== 条件2：第4格（索引3）或第5格（索引4）有剑 =====
        ItemStack slot4 = player.getInventory().getItem(3);
        ItemStack slot5 = player.getInventory().getItem(4);
        boolean hasSwordInSlot4 = slot4.getItem() instanceof SwordItem;
        boolean hasSwordInSlot5 = slot5.getItem() instanceof SwordItem;

        if (!hasSwordInSlot4 && !hasSwordInSlot5) return;

        // ===== 统计整个快捷栏（0-8）中剑的数量 =====
        int swordCount = 0;
        for (int i = 0; i < 9; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item.getItem() instanceof SwordItem) {
                swordCount++;
            }
        }

        if (swordCount <= 0) return;

        // ===== 迅捷等级 = 剑数（1把=I级，2把=II级...） =====
        int amplifier = swordCount - 1;

        // ===== 增加迅捷（不覆盖，时间叠加，等级取最高） =====
        MobEffectInstance existing = player.getEffect(MobEffects.MOVEMENT_SPEED);
        int newDuration = EFFECT_DURATION;
        int newAmplifier = amplifier;

        if (existing != null) {
            newDuration = existing.getDuration() + EFFECT_DURATION;
            newAmplifier = Math.max(existing.getAmplifier(), amplifier);
        }

        player.removeEffect(MobEffects.MOVEMENT_SPEED);
        player.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SPEED,
                newDuration,
                newAmplifier,
                false,
                false
        ));

        // ===== 记录冷却 =====
        player.getPersistentData().putLong("surgicalKnifeLastTrigger", currentTick);

        player.displayClientMessage(
                Component.literal("§d[手术用刀] §b迅捷 " + getRomanNumeral(swordCount) + " §a已激活，持续 30 秒！"),
                true
        );
    }

    private String getRomanNumeral(int number) {
        if (number <= 0) return "0";
        switch (number) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            case 6: return "VI";
            case 7: return "VII";
            case 8: return "VIII";
            case 9: return "IX";
            default: return String.valueOf(number);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living instanceof Player player) {
            player.getPersistentData().putBoolean("hasSurgicalKnife", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.surgical_knife.1", "§7手持剑类武器且快捷栏第4/5格有剑时"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.surgical_knife.2", "§7快捷栏中每有1把剑 +1 级迅捷（持续30秒，冷却30秒）"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.surgical_knife.3", "§8- 迅捷等级可叠加，不覆盖现有迅捷"));
        tooltip.add(Component.translatableWithFallback("tooltip.lim_mirror.surgical_knife.4", "§8- 第4格和第5格至少有一把剑即可触发"));
    }
}