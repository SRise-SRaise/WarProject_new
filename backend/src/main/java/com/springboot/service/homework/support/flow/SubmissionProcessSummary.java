package com.springboot.service.homework.support.flow;

public class SubmissionProcessSummary {

    private int submittedCount;
    private int pendingReviewCount;
    private int autoScore;

    public int getSubmittedCount() {
        return submittedCount;
    }

    public int getPendingReviewCount() {
        return pendingReviewCount;
    }

    public int getAutoScore() {
        return autoScore;
    }

    public void increaseSubmittedCount() {
        submittedCount++;
    }

    public void increasePendingReviewCount() {
        pendingReviewCount++;
    }

    public void addAutoScore(int score) {
        autoScore += score;
    }
}
