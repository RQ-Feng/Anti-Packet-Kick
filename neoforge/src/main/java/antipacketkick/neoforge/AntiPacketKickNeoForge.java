/*
 * This file is part of Anti Packet Kick.
 * Copyright (c) 2026 AntiPacketKick contributors.
 * SPDX-License-Identifier: GPL-3.0-only
 */

package antipacketkick.neoforge;

import antipacketkick.AntiPacketKick;
import antipacketkick.config.AntiPacketKickConfig;
import antipacketkick.config.AntiPacketKickConfigScreen;
import antipacketkick.platform.Platform;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

@Mod(value = AntiPacketKick.MOD_ID, dist = Dist.CLIENT)
public class AntiPacketKickNeoForge {
    private static KeyMapping openConfigKey;

    public AntiPacketKickNeoForge(IEventBus modBus, ModContainer container) {
        Platform.set(() -> FMLPaths.CONFIGDIR.get());
        AntiPacketKickConfig.load();

        // 在“模组列表”里显示 Config 按钮（NeoForge 内置扩展点）。
        container.registerExtensionPoint(IConfigScreenFactory.class,
            (modContainer, parent) -> AntiPacketKickConfigScreen.create(parent));

        modBus.addListener(this::onRegisterKeys);
        NeoForge.EVENT_BUS.addListener(this::onClientTick);
    }

    private void onRegisterKeys(RegisterKeyMappingsEvent event) {
        openConfigKey = new KeyMapping(
            "key." + AntiPacketKick.MOD_ID + ".config",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "category." + AntiPacketKick.MOD_ID
        );
        event.register(openConfigKey);
    }

    private void onClientTick(ClientTickEvent.Post event) {
        AntiPacketKickConfig.tick();

        if (openConfigKey == null) return;

        Minecraft client = Minecraft.getInstance();
        while (openConfigKey.consumeClick()) {
            client.setScreen(AntiPacketKickConfigScreen.create(client.screen));
        }
    }
}
