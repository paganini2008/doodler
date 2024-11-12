package com.github.doodler.common.feign;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;

/**
 * @Description: MultipleTypeDecoder
 * @Author: Fred Feng
 * @Date: 13/02/2023
 * @Version 1.0.0
 */
public class MultipleTypeDecoder implements Decoder {

	private final Map<Type, Decoder> decoders = new ConcurrentHashMap<>();
	private Decoder defaultDecoder = new JacksonDecoder();

	public void addDecoder(Type type, Decoder decoder) {
		decoders.put(type, decoder);
	}
	
	public void removeDecoder(Type type) {
		decoders.remove(type);
	}

	public void setDefaultDecoder(Decoder defaultDecoder) {
		this.defaultDecoder = defaultDecoder;
	}

	@Override
	public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
		Decoder decoder = decoders.getOrDefault(type, defaultDecoder);
		return decoder.decode(response, type);
	}
}