package com.rwu.imin2.model;

import com.google.firebase.auth.FirebaseAuth;
import java.util.List;
import java.util.UUID;

public class Event {

    private FirebaseAuth mAuth;
    private String creatorUserId;
    private String creatorDisplayName;
    private String title;
    private String description;
    private String eventDate;
    private String eventTime;
    private List<String> invitedUsers;
    private QuestionList<Question> questionList;
    private String eventId;
    private String eventTimeHour;
    private String eventTimeMinute;

    /**
     * Standardconstructor
     */
    public Event() {

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null) {
            this.creatorUserId = mAuth.getCurrentUser().getUid();
            this.creatorDisplayName = mAuth.getCurrentUser().getDisplayName();
        }
        this.eventId = "EVENT_" + String.valueOf(UUID.randomUUID());
    }

    /**
     * Event Constructor
     * @param creatorUserId
     * @param eventId
     * @param title
     * @param description
     * @param eventDate
     * @param eventTime
     * @param invitedUsers
     * @param questionList
     */
    public Event(String creatorUserId, String eventId, String title, String description, String eventDate, String eventTime, List<String> invitedUsers, QuestionList<Question> questionList) {
        mAuth = FirebaseAuth.getInstance();
        this.eventId = this.eventId == null ? "EVENT_" + String.valueOf(UUID.randomUUID()) : eventId;
        this.creatorUserId = this.creatorUserId == null ? mAuth.getCurrentUser().getUid() : creatorUserId;
        this.creatorDisplayName = mAuth.getCurrentUser().getDisplayName();
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.invitedUsers = invitedUsers;
        this.questionList = questionList;
    }

    /**
     * GETTER & SETTER
     */

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public List<String> getInvitedUsers() {
        return invitedUsers;
    }

    public void setInvitedUsers(List<String> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    public QuestionList<Question> getQuestionList() {
        return this.questionList;
    }

    public void setQuestionList(QuestionList<Question> questionList) {
        this.questionList = questionList;
    }

    public String getEventId() {
        return this.eventId;
    }

    public String getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(String creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventTimeHour() {
        return eventTimeHour;
    }

    public void setEventTimeHour(String eventTimeHour) {
        this.eventTimeHour = eventTimeHour;
    }

    public String getEventTimeMinute() {
        return eventTimeMinute;
    }

    public void setEventTimeMinute(String eventTimeMinute) {
        this.eventTimeMinute = eventTimeMinute;
    }

    public String getCreatorDisplayName() {
        return creatorDisplayName;
    }

    public void setCreatorDisplayName(String creatorDisplayName) {
        this.creatorDisplayName = creatorDisplayName;
    }

}
