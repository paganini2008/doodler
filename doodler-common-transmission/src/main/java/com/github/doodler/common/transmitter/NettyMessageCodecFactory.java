package com.github.doodler.common.transmitter;

import com.github.doodler.common.transmitter.utils.KryoSerializer;
import com.github.doodler.common.transmitter.utils.Serializer;
import io.netty.channel.ChannelHandler;

/**
 * 
 * @Description: NettyMessageCodecFactory
 * @Author: Fred Feng
 * @Date: 27/12/2024
 * @Version 1.0.0
 */
public class NettyMessageCodecFactory implements MessageCodecFactory {

    private final Serializer serializer;

    public NettyMessageCodecFactory() {
        this(new KryoSerializer());
    }

    public NettyMessageCodecFactory(Serializer serializer) {
        this.serializer = serializer;
    }

    public ChannelHandler getEncoder() {
        return new NettyEncoderDecoders.PacketEncoder(serializer);
    }

    public ChannelHandler getDecoder() {
        return new NettyEncoderDecoders.PacketDecoder(serializer);
    }

    public Serializer getSerializer() {
        return serializer;
    }

}
