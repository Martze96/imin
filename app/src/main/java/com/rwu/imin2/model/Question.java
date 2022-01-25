package com.rwu.imin2.model;

import com.rwu.imin2.enums.questionType;

import java.io.Serializable;
import java.util.ArrayList;

public class Question implements Serializable {
    private String id;
    private String title;
    private questionType type;
    private ArrayList<String> optionList;
    private Answer answer;

    //Empty Constructor
    public Question() {

    }

    //Constructor
    public Question(String id, String title, questionType type, ArrayList<String> optionList) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.optionList = optionList;
    }

    public Question(String id, String title, questionType type, ArrayList<String> optionList, Answer answer) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.optionList = optionList;
        this.answer = answer;
    }

    public void addOption(String newOption) {
        this.optionList.add(newOption);
    }

    // Equals with ID-Comparison
    public boolean equals(Question q) {
        if (q.getId().equals(this.id)) {
            return true;
        }
        return false;
    }

    //Equals with Title-Comparison
    public boolean equalsTitle(Question q) {
        if (q.getTitle().equals(this.title)) {
            return true;
        }
        return false;
    }

    //GETTER SETTER
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public questionType getType() {
        return type;
    }

    public void setType(questionType type) {
        this.type = type;
    }

    public ArrayList<String> getOptionList() {
        return optionList;
    }

    public void setOptionList(ArrayList<String> optionList) {
        this.optionList = optionList;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }


}


