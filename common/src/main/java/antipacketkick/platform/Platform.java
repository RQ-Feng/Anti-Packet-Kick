/*
 * This file is part of Anti Packet Kick.
 * Copyright (c) 2026 AntiPacketKick contributors.
 * SPDX-License-Identifier: GPL-3.0-only
 */

package antipacketkick.platform;

/**
 * Holds the {@link AntiPacketKickPlatform} implementation supplied by the active loader.
 */
public final class Platform {
    private static AntiPacketKickPlatform instance;

    private Platform() {}

    public static void set(AntiPacketKickPlatform platform) {
        instance = platform;
    }

    public static AntiPacketKickPlatform get() {
        if (instance == null) {
            throw new IllegalStateException("AntiPacketKick platform has not been initialised yet");
        }
        return instance;
    }
}
