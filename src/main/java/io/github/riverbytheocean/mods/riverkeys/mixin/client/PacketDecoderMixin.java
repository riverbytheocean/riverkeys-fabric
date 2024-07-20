package io.github.riverbytheocean.mods.riverkeys.mixin.client;

import io.github.riverbytheocean.mods.riverkeys.Riverkeys;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.PacketDecoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;

@Mixin(PacketDecoder.class)
public class PacketDecoderMixin {


    @Inject(at = @At("HEAD"), method = "decode")
    public void debugLogPackets(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list, CallbackInfo ci) {

//        try {
//            Riverkeys.LOGGER.info(Arrays.toString(byteBuf.array()));
//        } catch (UnsupportedOperationException e) {
//            Riverkeys.LOGGER.info(byteBuf.toString());
//        }

    }

}
