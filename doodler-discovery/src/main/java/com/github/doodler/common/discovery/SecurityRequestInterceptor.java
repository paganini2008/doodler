package com.github.doodler.common.discovery;

import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.github.doodler.common.Constants;
import com.github.doodler.common.SecurityKey;
import com.github.doodler.common.context.ENC;

/**
 * @Description: SecurityRequestInterceptor
 * @Author: Fred Feng
 * @Date: 31/10/2023
 * @Version 1.0.0
 */
public class SecurityRequestInterceptor implements ClientHttpRequestInterceptor {

	private final SecurityKey securityKey;

	public SecurityRequestInterceptor(String securityKey) {
		this(new SecurityKey(securityKey));
	}

	public SecurityRequestInterceptor(SecurityKey securityKey) {
		this.securityKey = securityKey;
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		String cipherText = ENC.encrypt(securityKey.getKey(), securityKey.getSalt());
		request.getHeaders().set(Constants.REQUEST_HEADER_ENDPOINT_SECURITY_KEY,
				String.format(Constants.ENC_PATTERN, cipherText));
		return execution.execute(request, body);
	}
}