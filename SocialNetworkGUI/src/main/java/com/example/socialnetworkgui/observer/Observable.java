package com.example.socialnetworkgui.observer;

import com.example.socialnetworkgui.events.Event;

public interface Observable<E extends Event> {
    void addObserver(Observer<E> observer);
    void removeObserver(Observer<E> observer);
    void notifyObservers(E event);
}
