package com.rwu.imin2.model;

import java.util.ArrayList;
import java.util.UUID;

public class Answer {

    private String userID;
    private String userDisplayName;
    private ArrayList<String> answerList;
    private String additionalInfo;
    private String answerId;

    /**
     * StandardConstructor
     */
    public Answer() {
        this.answerId = "ANSWER_" + UUID.randomUUID().toString();
        this.userID = userID;
        this.userDisplayName = userDisplayName;
    }

    /**
     * Answer Constructor
     * @param userID
     * @param userDisplayName
     * @param answerList
     * @param additionalInfo
     * @param answerId
     */
    public Answer(String userID, String userDisplayName, ArrayList<String> answerList, String additionalInfo, String answerId) {
        this.userID = userID;
        this.userDisplayName = userDisplayName;
        this.answerList = answerList;
        this.additionalInfo = additionalInfo;
        this.answerId = this.getAnswerId() == null ? userDisplayName+"-"+"ANSWER_" + UUID.randomUUID().toString() : answerId;
    }

    /**
     * GETTER & SETTER
     */
    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public ArrayList<String> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(ArrayList<String> answerList) {
        this.answerList = answerList;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
