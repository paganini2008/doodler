package com.github.doodler.common.webmvc;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;

/**
 * @Description: CachedRequestBodyServletRequest
 * @Author: Fred Feng
 * @Date: 18/01/2023
 * @Version 1.0.0
 */
public class CachedRequestBodyServletRequest extends HttpServletRequestWrapper {

	public CachedRequestBodyServletRequest(HttpServletRequest request) {
		super(request);
	}

	private byte[] requestBodyBytes;

	@Override
	public ServletInputStream getInputStream() throws IOException {
		final HttpServletRequest request = (HttpServletRequest) getRequest();

		if (null == requestBodyBytes) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			IOUtils.copy(request.getInputStream(), baos);
			requestBodyBytes = baos.toByteArray();
		}

		final ByteArrayInputStream bais = new ByteArrayInputStream(requestBodyBytes);
		return new ServletInputStream() {

			@Override
			public boolean isFinished() {
				return false;
			}

			@Override
			public boolean isReady() {
				return false;
			}

			@Override
			public void setReadListener(ReadListener listener) {
			}

			@Override
			public int read() {
				return bais.read();
			}
		};
	}

	public byte[] getRequestBody() {
		return requestBodyBytes;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(this.getInputStream()));
	}
}