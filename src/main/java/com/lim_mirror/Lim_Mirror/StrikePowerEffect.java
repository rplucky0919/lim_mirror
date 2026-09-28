package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class StrikePowerEffect extends MobEffect {

    public StrikePowerEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF8800);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}