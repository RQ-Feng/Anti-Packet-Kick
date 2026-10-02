/*
 * This file is part of Anti Packet Kick, a modified extract of the Meteor Client distribution
 * (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 * Modified by AntiPacketKick contributors, 2026.
 * SPDX-License-Identifier: GPL-3.0-only
 */

package antipacketkick.mixin;

import net.minecraft.nbt.NbtAccounter;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * 机制 2：把 NBT 读取的 size tracker 换成 unlimitedHeap，防止超大 NBT 踢人。
 */
@Mixin(FriendlyByteBuf.class)
public abstract class FriendlyByteBufMixin {
    @ModifyArg(
        method = "readNbt(Lio/netty/buffer/ByteBuf;)Lnet/minecraft/nbt/CompoundTag;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/FriendlyByteBuf;readNbt(Lio/netty/buffer/ByteBuf;Lnet/minecraft/nbt/NbtAccounter;)Lnet/minecraft/nbt/Tag;"
        )
    )
    private static NbtAccounter antipacketkick$unlimitedNbt(NbtAccounter original) {
        return NbtAccounter.unlimitedHeap();
    }
}
