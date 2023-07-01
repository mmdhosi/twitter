package com.mytwitter.direct;

import com.mytwitter.tweet.Tweet;

import java.sql.Timestamp;

public class Message {
    private String content,sender,receiver;
    private Timestamp sendTime;
    private Boolean isSeen;
    private int id;

    public Message(int id, String sender, String receiver, String content, Timestamp sendTime, Boolean isSeen) {
        this.id = id;
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.sendTime = sendTime;
        this.isSeen = isSeen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Timestamp getSendTime() {
        return sendTime;
    }

    public void setSendTime(Timestamp sendTime) {
        this.sendTime = sendTime;
    }

    public Boolean getSeen() {
        return isSeen;
    }

    public void setSeen(Boolean seen) {
        isSeen = seen;
    }

}
