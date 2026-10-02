/*
 * This file is part of Anti Packet Kick, a modified extract of the Meteor Client distribution
 * (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 * Modified by AntiPacketKick contributors, 2026.
 * SPDX-License-Identifier: GPL-3.0-only
 */

package antipacketkick.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 机制 3：去掉“单个 bundle 的最大包数”上限（原常量 4096）。
 *
 * <p>目标是匿名内部类 {@code BundlerInfo$1$1}，是 4 处注入里最脆弱的一个：
 * 类名取决于 Mojang 的编译产物，版本更新后需要重新核对（javap）。
 */
@Mixin(targets = "net/minecraft/network/protocol/BundlerInfo$1$1")
public abstract class PacketBundlingMixin {
    @ModifyExpressionValue(method = "addPacket", at = @At(value = "CONSTANT", args = "intValue=4096"))
    private int antipacketkick$maximizeBundleSize(int original) {
        return Integer.MAX_VALUE;
    }
}
