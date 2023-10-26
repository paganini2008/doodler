package io.doodler.feign.statistics;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import feign.Request;
import io.doodler.feign.HttpUtils;
import io.doodler.feign.RestClientConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: RequestHistory
 * @Author: Fred Feng
 * @Date: 29/01/2023
 * @Version 1.0.0
 */
@Getter
@Setter
@ToString
public class RequestHistory {

    RequestHistory(Request request) {
        this.protocol = request.protocolVersion() != null ? request.protocolVersion().toString() : "Unknown";
        this.method = request.httpMethod().name();
        String url = request.url();
        int index;
        if ((index = url.indexOf("?")) != -1) {
            path = url.substring(0, index);
            query = url.substring(index + 1);
        } else {
            path = url;
        }
        if(StringUtils.isNotBlank(query)) {
        	try {
				query = URLEncoder.encode(query, "UTF-8");
			} catch (UnsupportedEncodingException ignored) {
			}
        }
        this.body = request.charset() != null ? new String(request.body(), request.charset()) : "Binary data";
        this.requestHeaders = HttpUtils.getHttpHeaders(request).toSingleValueMap();
        this.sentBytes = request.length();
        String timestmap = HttpUtils.getFirstHeader(request, RestClientConstants.REQUEST_HEADER_TIMESTAMP);
        if(StringUtils.isNotBlank(timestmap)) {
        	this.elapsedTime = System.currentTimeMillis() - Long.parseLong(timestmap);
        }
    }

    RequestHistory() {
    }

    private String protocol;
    private String method;
    private String path;
    private String query;
    private String body;
    private Map<String, String> requestHeaders;
    private long sentBytes;
    private int status;
    private Map<String, String> responseHeaders;
    private long elapsedTime;
    private String[] errors;

    public String getRequestLine() {
        return protocol + " " + method + " " + path;
    }
}