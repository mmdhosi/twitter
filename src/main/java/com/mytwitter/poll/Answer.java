package com.mytwitter.poll;

public class Answer {
    private int votes,id;
    private String answer;

    public Answer(int votes, int id, String answer) {
        this.votes = votes;
        this.id = id;
        this.answer = answer;
    }

    public Answer() {
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
