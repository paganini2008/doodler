package io.doodler.common.feign;

import org.apache.commons.lang3.ArrayUtils;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.doodler.common.Constants;
import io.doodler.common.SecurityKey;
import io.doodler.common.context.ENC;

/**
 * @Description: SecurityRequestInterceptor
 * @Author: Fred Feng
 * @Date: 28/11/2022
 * @Version 1.0.0
 */
public class SecurityRequestInterceptor implements RequestInterceptor {

	private final static String[] filteredUrls = new String[] {
			"https://api-stg.gmgiantgold.com"
	};
	
    private final SecurityKey securityKey;

    public SecurityRequestInterceptor(String securityKey) {
        this(new SecurityKey(securityKey));
    }

    public SecurityRequestInterceptor(SecurityKey securityKey) {
        this.securityKey = securityKey;
    }

    @Override
    public void apply(RequestTemplate template) {
    	String url = template.feignTarget().url();
    	if(ArrayUtils.contains(filteredUrls, url)) {
    		return;
    	}
        String cipherText = ENC.encrypt(securityKey.getKey(), securityKey.getSalt());
        template.header(Constants.REQUEST_HEADER_REST_CLIENT_SECURITY_KEY,
                String.format(Constants.ENC_PATTERN, cipherText));
    }
}