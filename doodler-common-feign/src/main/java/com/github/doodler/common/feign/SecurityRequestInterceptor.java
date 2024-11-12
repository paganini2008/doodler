package com.github.doodler.common.feign;

import org.apache.commons.lang3.ArrayUtils;
import com.github.doodler.common.Constants;
import com.github.doodler.common.SecurityKey;
import com.github.doodler.common.context.ENC;
import feign.RequestInterceptor;
import feign.RequestTemplate;

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