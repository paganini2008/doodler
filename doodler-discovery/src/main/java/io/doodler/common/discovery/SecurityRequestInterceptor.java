package io.doodler.common.discovery;

import io.doodler.common.Constants;
import io.doodler.common.SecurityKey;
import io.doodler.common.context.ENC;
import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

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