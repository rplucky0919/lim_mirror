package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

// 这是一个简单的自定义效果类，用来绕过 protected 限制
public class ModEffects extends MobEffect {
    public ModEffects(MobEffectCategory category, int color) {
        super(category, color);
    }
}
