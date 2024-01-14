package com.github.doodler.common.feign;

import feign.codec.Decoder;
import feign.codec.Encoder;

/**
 * @Description: EncoderDecoderFactory
 * @Author: Fred Feng
 * @Date: 18/09/2023
 * @Version 1.0.0
 */
public interface EncoderDecoderFactory {

	Encoder getEncoder();

	Decoder getDecoder();
}