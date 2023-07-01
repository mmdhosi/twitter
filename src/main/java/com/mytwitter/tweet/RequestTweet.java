package com.mytwitter.tweet;

import com.mytwitter.poll.Poll;

public class RequestTweet {
    private String content;
    private String image;
    private Poll poll;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }
}
