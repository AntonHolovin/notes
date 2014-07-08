package com.golovin.notes.controller;

import com.golovin.notes.event.Event;
import com.golovin.notes.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class EventManager {

    private static EventManager sInstance;

    private List<EventHandler> mHandlers;

    private EventManager() {
        mHandlers = new ArrayList<EventHandler>();
    }

    public static EventManager getInstance() {
        if (sInstance == null) {
            sInstance = new EventManager();
        }

        return sInstance;
    }

    public void fireEvent(Event event) {
        for (EventHandler handler : mHandlers) {
            handler.handlerEvent(event);
        }
    }

    public void addHandler(EventHandler eventHandler) {
        mHandlers.add(eventHandler);
    }

    public void removeHandler(EventHandler eventHandler) {
        mHandlers.remove(eventHandler);
    }
}
