package com.mytwitter.tweet;

public class Bio {

    private String text;
    private String location;
    private String webAddress;

    public Bio(String text, String location, String webAddress) {
        this.text = text;
        this.location = location;
        this.webAddress = webAddress;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }
}
