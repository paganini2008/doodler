package com.github.doodler.common.context;

import cn.hutool.core.date.DateUtil;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description: Span
 * @Author: Fred Feng
 * @Date: 25/02/2023
 * @Version 1.0.0
 */
@Getter
@Setter
public class Span implements Serializable {

    private static final long serialVersionUID = 6171405304032811048L;

    private String traceId;
    private int spanId;
    private int parentSpanId;
    private String path;
    private long timestamp;
    private long elapsed;
    private int status;
    private boolean thirdparty = false;

    public Span() {
    }

    public Span(String traceId, int spanId) {
        this.traceId = traceId;
        this.spanId = spanId;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("traceId=").append(traceId).append("&spanId=").append(spanId).append("&parentSpanId=").append(
                parentSpanId).append("&path=").append(path).append("&datetime=").append(DateUtil.format(new Date(timestamp),
                        "yyyy-MM-dd HH:mm:ss")).append("&elapsed=").append(elapsed).append("&status=").append(
                                status).append("&thirdparty=").append(thirdparty);
        return str.toString();
    }
}