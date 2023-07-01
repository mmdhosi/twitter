package com.mytwitter.direct;

public class Direct {
    private String username;
    private Message lastMessage;

    public Direct(String username, Message lastMessage) {
        this.username = username;
        this.lastMessage = lastMessage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
}
