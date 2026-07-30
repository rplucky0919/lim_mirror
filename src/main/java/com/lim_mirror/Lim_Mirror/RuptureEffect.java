package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class RuptureEffect extends MobEffect {

    public RuptureEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B4513);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
