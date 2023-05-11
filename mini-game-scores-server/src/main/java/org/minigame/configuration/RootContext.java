package org.minigame.configuration;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class RootContext {

    private Map<Class<?>, Object> beans;
    private final Clock clock;

    public RootContext(Clock clock) {
        this.clock = clock;
        beans = new HashMap<>();
    }

    public void add(Object object){
        beans.put(object.getClass(), object);
    }

    public Object get(Class<?> clazz) {
        return beans.get(clazz);
    }

    public Clock getClock() {
        return clock;
    }
}
