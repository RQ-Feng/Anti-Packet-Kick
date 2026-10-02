/*
 * This file is part of Anti Packet Kick, a modified extract of the Meteor Client distribution
 * (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 * Modified by AntiPacketKick contributors, 2026.
 * SPDX-License-Identifier: GPL-3.0-only
 */

package antipacketkick.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.network.CompressionDecoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 机制 1：去掉“解压后单包大小上限”（原常量 8388608 = 8 MiB）。
 */
@Mixin(CompressionDecoder.class)
public abstract class CompressionDecoderMixin {
    @ModifyExpressionValue(method = "decode", at = @At(value = "CONSTANT", args = "intValue=8388608"))
    private int antipacketkick$maximizeUncompressedPacketLimit(int original) {
        return Integer.MAX_VALUE;
    }
}
