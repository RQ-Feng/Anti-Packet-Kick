/*
 * This file is part of Anti Packet Kick, a modified extract of the Meteor Client distribution
 * (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 * Modified by AntiPacketKick contributors, 2026.
 * SPDX-License-Identifier: GPL-3.0-only
 */

package antipacketkick;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loader-agnostic snapshot of the config.
 *
 * <p>Mixins read this instead of touching Cloth Config (or any loader API), because the
 * packet handlers run on Netty's network thread very early in the connection lifecycle.
 * The platform entrypoints push the current config values in via {@link #set}.
 */
public final class AntiPacketKickState {
    public static final Logger LOG = LoggerFactory.getLogger("AntiPacketKick");

    private static volatile boolean catchExceptions = false;
    private static volatile boolean logExceptions = true;
    private static volatile boolean chatNotifications = true;

    private AntiPacketKickState() {}

    /** True when corrupted-packet exceptions should be swallowed instead of disconnecting. */
    public static boolean shouldCatchExceptions() {
        return catchExceptions;
    }

    public static boolean logExceptions() {
        return logExceptions;
    }

    /** True when a chat message should be shown as soon as a bad packet is detected. */
    public static boolean chatNotifications() {
        return chatNotifications;
    }

    public static void set(boolean catchExceptions, boolean logExceptions, boolean chatNotifications) {
        AntiPacketKickState.catchExceptions = catchExceptions;
        AntiPacketKickState.logExceptions = logExceptions;
        AntiPacketKickState.chatNotifications = chatNotifications;
    }
}
