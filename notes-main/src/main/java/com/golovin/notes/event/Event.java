package com.golovin.notes.event;

import java.util.HashMap;
import java.util.Map;

public class Event {

    public static final String ACTION_KEY = "actionKey";

    private Map<String, Object> mParamsMap = new HashMap<String, Object>();

    private EventType mEventType;

    public Event(EventType eventType) {
        mEventType = eventType;
    }

    public enum EventType {
        TEXT_ENTERED,
        NOTE_REMOVED
    }

    public EventType getEventType() {
        return mEventType;
    }

    public void putParam(String key, Object param) {
        mParamsMap.put(key, param);
    }

    public Object getParam(String name) {
        return mParamsMap.get(name);
    }
}
