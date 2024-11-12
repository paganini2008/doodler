package com.github.doodler.common.feign;

import org.springframework.http.MediaType;
import com.github.doodler.common.utils.JacksonUtils;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.StringDecoder;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

/**
 * @Description: GenericEncoderDecoderFactory
 * @Author: Fred Feng
 * @Date: 18/09/2023
 * @Version 1.0.0
 */
public class GenericEncoderDecoderFactory implements EncoderDecoderFactory {

	@Override
	public Encoder getEncoder() {
		MultipleTypeEncoder encoder = new MultipleTypeEncoder();
		encoder.setDefaultEncoder(new JacksonEncoder(JacksonUtils.getObjectMapperForFeignClient()));
		encoder.addEncoder(MediaType.APPLICATION_FORM_URLENCODED, new FormEncoder());
		return encoder;
	}

	@Override
	public Decoder getDecoder() {
		MultipleTypeDecoder decoder = new MultipleTypeDecoder();
		decoder.setDefaultDecoder(new JacksonDecoder(JacksonUtils.getObjectMapperForWebMvc()));
		decoder.addDecoder(String.class, new StringDecoder());
		return decoder;
	}
}