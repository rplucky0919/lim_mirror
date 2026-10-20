package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class StrikeEnhanceEffect extends MobEffect {

    public StrikeEnhanceEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xAA44AA);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}
