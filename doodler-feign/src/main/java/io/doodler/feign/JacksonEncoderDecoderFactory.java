package io.doodler.feign;

import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.doodler.common.utils.JacksonUtils;

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