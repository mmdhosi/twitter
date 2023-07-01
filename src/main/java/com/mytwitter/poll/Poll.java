package com.mytwitter.poll;

import java.util.ArrayList;

public class Poll {
    private ArrayList<Answer> answers=new ArrayList<>();
    String question;
    int id;
    int answeredId;

    public Poll() {
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }
    public void  addAnswer(Answer answer){
        answers.add(answer);
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAnsweredId() {
        return answeredId;
    }

    public void setAnsweredId(int answeredId) {
        this.answeredId = answeredId;
    }
}
