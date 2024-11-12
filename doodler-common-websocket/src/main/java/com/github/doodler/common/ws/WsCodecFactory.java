package com.github.doodler.common.ws;

/**
 * @Description: WsCodecFactory
 * @Author: Fred Feng
 * @Date: 10/03/2023
 * @Version 1.0.0
 */
public interface WsCodecFactory {

    WsEncoder getEncoder();

    WsDecoder getDecoder();
}