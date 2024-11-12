package com.github.doodler.common.feign.logger;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.http.HttpStatus;
import com.github.doodler.common.Constants;
import com.github.doodler.common.context.ApiDebuger;
import com.github.doodler.common.utils.LruMap;
import com.github.doodler.common.utils.MapUtils;
import com.github.doodler.common.utils.Markers;
import feign.Request;
import feign.Response;
import feign.slf4j.Slf4jLogger;

/**
 * @Description: ElkLogger
 * @Author: Fred Feng
 * @Date: 29/05/2023
 * @Version 1.0.0
 */
public class ElkLogger extends Slf4jLogger {

    private static final String NEWLINE = System.getProperty("line.separator");

    public ElkLogger(String name) {
        this(name, Markers.SYSTEM);
    }

    public ElkLogger(String name, Marker marker) {
        super(name);
        this.marker = marker;
        this.log = LoggerFactory.getLogger(name);
    }

    private final Logger log;
    private final Marker marker;
    private final LruMap<String, List<String>> logLines = new LruMap<>(128);

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        String guid = null;
        try {
            guid = IteratorUtils.first(request.headers().get("guid").iterator());
        } catch (RuntimeException ignored) {
        }
        String logKey = configKey;
        if (StringUtils.isNotBlank(guid)) {
            logKey = guid + "@" + configKey;
            MapUtils.getOrCreate(logLines, logKey, CopyOnWriteArrayList::new);
        }
        super.logRequest(logKey, logLevel, request);
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response,
                                              long elapsedTime) throws IOException {
        String guid = null;
        try {
            guid = IteratorUtils.first(response.request().requestTemplate().headers().get("guid").iterator());
        } catch (RuntimeException ignored) {
        }
        String logKey = configKey;
        if (StringUtils.isNotBlank(guid)) {
            logKey = guid + "@" + configKey;
        }
        Response newResponse = super.logAndRebufferResponse(logKey, logLevel, response, elapsedTime);

        List<String> list = logLines.remove(logKey);
        if (CollectionUtils.isNotEmpty(list)) {
            StringWriter writer = new StringWriter();
            IOUtils.writeLines(list, NEWLINE, writer);
            String logBlock = writer.toString();
            if (StringUtils.isNotBlank(logBlock)) {
                logBlock = NEWLINE + logBlock;
                if (HttpStatus.valueOf(response.status()).isError()) {
                    if (log.isErrorEnabled()) {
                        if (marker != null) {
                            log.error(marker, logBlock);
                        } else {
                            log.error(logBlock);
                        }
                    }
                } else if (elapsedTime >= Constants.DEFAULT_MAXIMUM_RESPONSE_TIME) {
                    if (log.isWarnEnabled()) {
                        if (marker != null) {
                            log.warn(marker, logBlock);
                        } else {
                            log.warn(logBlock);
                        }
                    }
                } else if (ApiDebuger.enableRestClient()) {
                    if (log.isDebugEnabled()) {
                        if (marker != null) {
                            log.debug(marker, logBlock);
                        } else {
                            log.debug(logBlock);
                        }
                    }
                }
            }
        }
        return newResponse;
    }

    @Override
    protected void log(String logKey, String format, Object... args) {
        List<String> list = logLines.get(logKey);
        if (list != null) {
            list.add(String.format(methodTag(logKey) + format, args));
        } else {
            super.log(logKey, format, args);
        }
    }
}