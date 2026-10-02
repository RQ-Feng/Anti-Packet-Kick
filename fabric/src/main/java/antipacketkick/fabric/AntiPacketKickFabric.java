/*
 * This file is part of Anti Packet Kick.
 * Copyright (c) 2026 AntiPacketKick contributors.
 * SPDX-License-Identifier: GPL-3.0-only
 */

package antipacketkick.fabric;

import antipacketkick.AntiPacketKick;
import antipacketkick.config.AntiPacketKickConfig;
import antipacketkick.config.AntiPacketKickConfigScreen;
import antipacketkick.platform.Platform;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class AntiPacketKickFabric implements ClientModInitializer {
    private static KeyMapping openConfigKey;

    @Override
    public void onInitializeClient() {
        Platform.set(() -> FabricLoader.getInstance().getConfigDir());
        AntiPacketKickConfig.load();

        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key." + AntiPacketKick.MOD_ID + ".config",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath(AntiPacketKick.MOD_ID, "main"))
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            AntiPacketKickConfig.tick();

            while (openConfigKey.consumeClick()) {
                client.setScreen(AntiPacketKickConfigScreen.create(client.screen));
            }
        });
    }
}
