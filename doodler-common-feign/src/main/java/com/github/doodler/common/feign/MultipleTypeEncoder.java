package com.github.doodler.common.feign;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.IteratorUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import feign.jackson.JacksonEncoder;

/**
 * @Description: MultipleTypeEncoder
 * @Author: Fred Feng
 * @Date: 29/05/2023
 * @Version 1.0.0
 */
public class MultipleTypeEncoder implements Encoder {

	private final Map<MediaType, Encoder> encoders = new ConcurrentHashMap<>();
	private Encoder defaultEncoder = new JacksonEncoder();

	public void addEncoder(MediaType mediaType, Encoder encoder) {
		encoders.put(mediaType, encoder);
	}

	public void removeEncoder(MediaType mediaType) {
		encoders.remove(mediaType);
	}

	public void setDefaultEncoder(Encoder defaultEncoder) {
		this.defaultEncoder = defaultEncoder;
	}

	@Override
	public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
		MediaType mediaType;
		try {
			String contentType = IteratorUtils.first(template.headers().get(HttpHeaders.CONTENT_TYPE).iterator());
			mediaType = MediaType.parseMediaType(contentType);
		} catch (RuntimeException e) {
			mediaType = MediaType.APPLICATION_JSON;
		}
		Encoder encoder = encoders.getOrDefault(mediaType, defaultEncoder);
		encoder.encode(object, bodyType, template);
	}
}