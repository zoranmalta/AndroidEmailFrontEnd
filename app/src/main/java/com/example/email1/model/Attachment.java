package com.example.email1.model;

import android.util.Base64;

public class Attachment {
    private long id;
    private Base64 data;
    private String type;
    private String name;
    private Message message;

    public Attachment(long id, Base64 data, String type,Message message, String name) {
        this.id = id;
        this.data = data;
        this.type = type;
        this.name = name;
        this.message=message;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Base64 getData() {
        return data;
    }

    public void setData(Base64 data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
