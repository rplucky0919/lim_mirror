package com.lim_mirror.Lim_Mirror;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Registration {
    // 注册器
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, lim_mirror.MODID);

    // ✅ 定义 Buff (POISE) - 放在这里统一管理
    public static final RegistryObject<MobEffect> POISE = EFFECTS.register("poise", Poise::new);

    // ✅ 修改这里：加上 IEventBus 参数，解决报错
    public static void init(IEventBus modEventBus) {
        EFFECTS.register(modEventBus);
    }
}