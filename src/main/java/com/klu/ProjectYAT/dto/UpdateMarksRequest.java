package com.klu.ProjectYAT.dto;

public class UpdateMarksRequest {
    private int marks;
    private String feedback;

    // No-args constructor
    public UpdateMarksRequest() {}

    // All-args constructor
    public UpdateMarksRequest(int marks, String feedback) {
        this.marks = marks;
        this.feedback = feedback;
    }

    // Getters and Setters
    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
