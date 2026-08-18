package com.lim_mirror.Lim_Mirror;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Sounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, lim_mirror.MODID);

    // ==================== 震颤引爆音效 ====================
    public static final RegistryObject<SoundEvent> TREMOR_EXPLODE =
            SOUNDS.register("tremor_explode",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(lim_mirror.MODID, "tremor_explode")
                    )
            );

    public static void init(IEventBus modEventBus) {
        SOUNDS.register(modEventBus);
    }
}