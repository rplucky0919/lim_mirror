package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class BreathSmoothEnchantment extends Enchantment {

    public BreathSmoothEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 15;
    }

    @Override
    public int getMaxCost(int level) {
        return 30;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return this != other && other != Enchantments.SHARPNESS;
    }
}