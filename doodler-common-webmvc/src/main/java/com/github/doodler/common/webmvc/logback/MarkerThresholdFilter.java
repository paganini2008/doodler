package com.github.doodler.common.webmvc.logback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * @Description: MarkerThresholdFilter
 * @Author: Fred Feng
 * @Date: 23/03/2023
 * @Version 1.0.0
 */
public class MarkerThresholdFilter extends AbstractMatcherFilter<ILoggingEvent> {

    private static final String[] internalMarkerNames = {"system"};
    private static final Collection<Marker> internalMarkers;

    static {
        List<Marker> list = new ArrayList<>();
        for (String markerName : internalMarkerNames) {
            list.add(MarkerFactory.getMarker(markerName));
        }
        internalMarkers = Collections.unmodifiableCollection(list);
    }

    Marker marker;
    Level level;

    @Override
    public void start() {
        if (this.level != null) {
            super.start();
        }
    }

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }
        Marker currentMarker = event.getMarker();
        if (currentMarker != null && marker != null) {
            if (marker.contains(currentMarker) || isInternalMarker(currentMarker)) {
                return onMatch;
            }
        } else {
            if (event.getLevel().isGreaterOrEqual(level)) {
                return FilterReply.NEUTRAL;
            } else {
                return FilterReply.DENY;
            }
        }
        return onMismatch;
    }

    private boolean isInternalMarker(Marker currentMarker) {
        for (Marker marker : internalMarkers) {
            if (marker.contains(currentMarker)) {
                return true;
            }
        }
        return false;
    }

    public void setLevel(String level) {
        if (StringUtils.isNotBlank(level)) {
            this.level = Level.toLevel(level);
        }
    }

    public void setMarker(String markerStr) {
        if (StringUtils.isNotBlank(markerStr)) {
            marker = MarkerFactory.getMarker(markerStr);
        }
    }
}