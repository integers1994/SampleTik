package com.photex.tiktok.models.restmodels;

/**
 * Created by Ameer Hamza on 1/6/2017.
 */

public class ReqReportPost {
    String reportedBy;
    String postId;
    String reason;

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
