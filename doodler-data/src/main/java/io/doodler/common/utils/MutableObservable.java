package io.doodler.common.utils;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Description: MutableObservable
 * @Author: Fred Feng
 * @Date: 14/12/2022
 * @Version 1.0.0
 */
public class MutableObservable {

    private final ConcurrentMap<String, Observable> channels = new ConcurrentHashMap<>();
    private final boolean repeatable;

    public MutableObservable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public void addObserver(MutableObserver ob) {
        addObserver("default", ob);
    }

    public void addObserver(String channel, MutableObserver ob) {
        Observable obs = channels.get(channel);
        if ((obs == null) || (ob.isPrimary())) {
            if (ob.isPrimary()) {
                channels.put(channel, new PrimaryObservable());
            } else {
                channels.putIfAbsent(channel, new DefaultObservable());
            }
            obs = channels.get(channel);
        }
        obs.addObserver(ob);
    }

    public void notifyObservers(Object arg) {
        notifyObservers("default", arg);
    }

    public void notifyObservers(String channel, Object arg) {
        Observable obs = channels.get(channel);
        if (obs != null && obs.countObservers() > 0) {
            obs.notifyObservers(arg);
        }
    }

    public void notifyObservers(String channel, MatchMode matchMode, Object arg) {
        channels.entrySet().stream().filter(e -> matchMode.matches(channel, e.getKey())).forEach(e -> {
            Observable obs = e.getValue();
            if (obs != null && obs.countObservers() > 0) {
                obs.notifyObservers(arg);
            }
        });
    }

    public int countOfChannels() {
        return channels.size();
    }

    public int countObservers() {
        int n = 0;
        for (Observable obs : channels.values()) {
            n += obs.countObservers();
        }
        return n;
    }

    public void clear() {
        channels.clear();
    }

    private class DefaultObservable extends Observable {

        @Override
        public void notifyObservers(Object arg) {
            super.setChanged();
            super.notifyObservers(arg);
            if (!repeatable) {
                clearChanged();
                deleteObservers();
            }
        }
    }

    private class PrimaryObservable extends DefaultObservable {

        @Override
        public void addObserver(Observer o) {
            if (super.countObservers() == 0) {
                super.addObserver(o);
            }
        }
    }
}