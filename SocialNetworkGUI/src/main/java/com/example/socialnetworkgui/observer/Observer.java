package com.example.socialnetworkgui.observer;

import com.example.socialnetworkgui.events.Event;

public interface Observer<E extends Event> {
    void update(E event);
}
