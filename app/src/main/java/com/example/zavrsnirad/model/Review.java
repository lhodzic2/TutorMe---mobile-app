package com.example.zavrsnirad.model;

public class Review {
    private String reviewerID,review,reviewerName;
    private float rating;

    public Review(String reviewerID, String review, String reviewerName, float rating) {
        this.reviewerID = reviewerID;
        this.review = review;
        this.reviewerName = reviewerName;
        this.rating = rating;
    }

    public Review() {
    }


    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getReviewerID() {
        return reviewerID;
    }

    public void setReviewerID(String reviewerID) {
        this.reviewerID = reviewerID;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
