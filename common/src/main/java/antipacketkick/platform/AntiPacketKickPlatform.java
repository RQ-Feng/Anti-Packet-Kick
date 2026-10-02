/*
 * This file is part of Anti Packet Kick.
 * Copyright (c) 2026 AntiPacketKick contributors.
 * SPDX-License-Identifier: GPL-3.0-only
 */

package antipacketkick.platform;

import java.nio.file.Path;

/**
 * The only loader-specific thing the common code needs: where to write the config file.
 */
@FunctionalInterface
public interface AntiPacketKickPlatform {
    Path configDir();
}
