/*
 * This file is part of Anti Packet Kick, a modified extract of the Meteor Client distribution
 * (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 * Modified by AntiPacketKick contributors, 2026.
 * SPDX-License-Identifier: GPL-3.0-only
 */

package antipacketkick.mixin;

import antipacketkick.AntiPacketKickNotifier;
import antipacketkick.AntiPacketKickState;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.TimeoutException;
import net.minecraft.network.Connection;
import net.minecraft.network.SkipPacketEncoderException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 机制 4（catch-exceptions）：吞掉损坏包触发的异常，防止“收包出错 -> 断线”。
 *
 * <p>必须保留对 {@link TimeoutException} 与 {@link SkipPacketEncoderException} 的排除，
 * 否则会吞掉真实的超时/跳过编码错误。
 */
@Mixin(Connection.class)
public abstract class ConnectionExceptionMixin {
    @Inject(method = "exceptionCaught", at = @At("HEAD"), cancellable = true)
    private void antipacketkick$exceptionCaught(ChannelHandlerContext context, Throwable cause, CallbackInfo ci) {
        if (cause instanceof TimeoutException || cause instanceof SkipPacketEncoderException) return;

        if (AntiPacketKickState.chatNotifications()) {
            AntiPacketKickNotifier.badPacket(cause);
        }

        if (!AntiPacketKickState.shouldCatchExceptions()) return;

        if (AntiPacketKickState.logExceptions()) {
            AntiPacketKickState.LOG.warn("Caught packet exception: {}", cause.toString());
        }
        ci.cancel();
    }
}
