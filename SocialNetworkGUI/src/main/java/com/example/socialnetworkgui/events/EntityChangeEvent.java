package com.example.socialnetworkgui.events;

import com.example.socialnetworkgui.domain.User;

public class EntityChangeEvent implements Event {
    private final ChangeEventType type;
    private final Object data;
    private Object oldData;

    public EntityChangeEvent(ChangeEventType type, Object data) {
        this.type = type;
        this.data = data;
    }
    public EntityChangeEvent(ChangeEventType type, Object data, Object oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public Object getOldData() {
        return oldData;
    }

}