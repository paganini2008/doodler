package com.github.doodler.common.ws;

import java.io.IOException;

/**
 * @Description: DefaultWsCodecFactory
 * @Author: Fred Feng
 * @Date: 10/03/2023
 * @Version 1.0.0
 */
public class DefaultWsCodecFactory implements WsCodecFactory {

    public DefaultWsCodecFactory(WsDecoder decoder) {
        this(NoopWsEncoder.INSTANCE, decoder);
    }

    public DefaultWsCodecFactory(WsEncoder encoder, WsDecoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    private WsEncoder encoder;
    private WsDecoder decoder;

    @Override
    public WsEncoder getEncoder() {
        return encoder;
    }

    @Override
    public WsDecoder getDecoder() {
        return decoder;
    }

    static class NoopWsEncoder implements WsEncoder {

        static final NoopWsEncoder INSTANCE = new NoopWsEncoder();

        @Override
        public Object encode(String channel, WsUser user, String text) throws IOException {
            return text;
        }
    }
}