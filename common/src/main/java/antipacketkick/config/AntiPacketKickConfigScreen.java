/*
 * This file is part of Anti Packet Kick.
 * Copyright (c) 2026 AntiPacketKick contributors.
 * SPDX-License-Identifier: GPL-3.0-only
 */

package antipacketkick.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Cloth Config screen. Lives in {@code common} because the Cloth Config API is identical
 * on the Fabric and NeoForge artifacts — only the artifact coordinate differs.
 */
public final class AntiPacketKickConfigScreen {
    private AntiPacketKickConfigScreen() {}

    public static Screen create(Screen parent) {
        AntiPacketKickConfig cfg = AntiPacketKickConfig.get();

        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Component.translatable("antipacketkick.config.title"));

        ConfigEntryBuilder entries = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("antipacketkick.config.category.general"));

        general.addEntry(entries.startBooleanToggle(Component.translatable("antipacketkick.config.catchExceptions"), cfg.catchExceptions)
            .setDefaultValue(false)
            .setTooltip(Component.translatable("antipacketkick.config.catchExceptions.tooltip"))
            .setSaveConsumer(value -> cfg.catchExceptions = value)
            .build());

        general.addEntry(entries.startBooleanToggle(Component.translatable("antipacketkick.config.logExceptions"), cfg.logExceptions)
            .setDefaultValue(true)
            .setTooltip(Component.translatable("antipacketkick.config.logExceptions.tooltip"))
            .setSaveConsumer(value -> cfg.logExceptions = value)
            .build());

        general.addEntry(entries.startBooleanToggle(Component.translatable("antipacketkick.config.chatNotifications"), cfg.chatNotifications)
            .setDefaultValue(true)
            .setTooltip(Component.translatable("antipacketkick.config.chatNotifications.tooltip"))
            .setSaveConsumer(value -> cfg.chatNotifications = value)
            .build());

        builder.setSavingRunnable(AntiPacketKickConfig::save);
        return builder.build();
    }
}
