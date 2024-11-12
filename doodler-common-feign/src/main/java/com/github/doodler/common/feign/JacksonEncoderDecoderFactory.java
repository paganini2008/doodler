package com.github.doodler.common.feign;

import com.github.doodler.common.utils.JacksonUtils;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

/**
 * @Description: JacksonEncoderDecoderFactory
 * @Author: Fred Feng
 * @Date: 18/09/2023
 * @Version 1.0.0
 */
public class JacksonEncoderDecoderFactory implements EncoderDecoderFactory {

	@Override
	public Encoder getEncoder() {
		return new JacksonEncoder();
	}

	@Override
	public Decoder getDecoder() {
		return new JacksonDecoder(JacksonUtils.getObjectMapperForWebMvc());
	}
}