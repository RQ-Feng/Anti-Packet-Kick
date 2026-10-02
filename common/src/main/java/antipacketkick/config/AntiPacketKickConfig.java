/*
 * This file is part of Anti Packet Kick.
 * Copyright (c) 2026 AntiPacketKick contributors.
 * SPDX-License-Identifier: GPL-3.0-only
 */

package antipacketkick.config;

import antipacketkick.AntiPacketKickState;
import antipacketkick.platform.Platform;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Persisted config, stored as JSON at {@code <config>/antipacketkick.json}.
 * Gson ships with Minecraft on both loaders, so no extra dependency is needed.
 */
public final class AntiPacketKickConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "antipacketkick.json";

    private static AntiPacketKickConfig instance;
    private static long lastModified = -1L;
    private static int reloadCooldown;

    public boolean catchExceptions = false;
    public boolean logExceptions = true;
    public boolean chatNotifications = true;

    public static AntiPacketKickConfig get() {
        if (instance == null) load();
        return instance;
    }

    private static Path file() {
        return Platform.get().configDir().resolve(FILE_NAME);
    }

    private static long fileTimestamp(Path file) {
        try {
            return Files.exists(file) ? Files.getLastModifiedTime(file).toMillis() : 0L;
        } catch (IOException e) {
            return 0L;
        }
    }

    /**
     * Called from the client tick handler (once per second). Re-reads the config file when it
     * changes on disk, so editing {@code antipacketkick.json} externally also takes effect
     * without restarting the game.
     */
    public static void tick() {
        if (reloadCooldown > 0) {
            reloadCooldown--;
            return;
        }
        reloadCooldown = 20;

        if (fileTimestamp(file()) != lastModified) {
            load();
        }
    }
        

    public static void load() {
        Path file = file();
        lastModified = fileTimestamp(file);

        AntiPacketKickConfig loaded = null;
        try {
            if (Files.exists(file)) {
                loaded = GSON.fromJson(Files.readString(file), AntiPacketKickConfig.class);
            }
        } catch (Exception e) {
            AntiPacketKickState.LOG.warn("Failed to read {}, falling back to defaults", file, e);
        }

        instance = loaded != null ? loaded : new AntiPacketKickConfig();
        apply();
    }

    public static void save() {
        Path file = file();
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, GSON.toJson(get()));
        } catch (IOException e) {
            AntiPacketKickState.LOG.warn("Failed to write {}", file, e);
        }
        lastModified = fileTimestamp(file);
        apply();
    }

    /** Push the current values into the static state read by the mixins. */
    public static void apply() {
        AntiPacketKickConfig cfg = get();
        AntiPacketKickState.set(cfg.catchExceptions, cfg.logExceptions, cfg.chatNotifications);
    }
}
