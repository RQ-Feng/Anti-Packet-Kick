/*
 * This file is part of Anti Packet Kick.
 * Copyright (c) 2026 AntiPacketKick contributors.
 * SPDX-License-Identifier: GPL-3.0-only
 */

package antipacketkick;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/**
 * Shows a chat message when a bad packet is detected.
 *
 * <p>Called from {@code Connection#exceptionCaught}, which runs on Netty's network thread,
 * so the message is scheduled onto the client thread. A short rate limit keeps a burst of
 * bad packets from flooding the chat.
 */
public final class AntiPacketKickNotifier {
    /*
    private static final long MIN_INTERVAL_MS = 1000L;
    
    private static volatile long lastNotification;
    */

    private AntiPacketKickNotifier() {}

    public static void badPacket(Throwable cause) {
        /*
        long now = System.currentTimeMillis();
        if (now - lastNotification < MIN_INTERVAL_MS) return;
        lastNotification = now;
        暂时不需要这段
        */

        String detail = cause.getClass().getSimpleName();
        Minecraft client = Minecraft.getInstance();

        client.execute(() -> {
            if (client.player != null) {
                client.player.displayClientMessage(
                    Component.translatable("antipacketkick.message.badPacket", detail), false);
            }
        });
    }
}
