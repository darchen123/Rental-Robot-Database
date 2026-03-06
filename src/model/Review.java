package model;

/**
 * Represents a customer review.
 */
public class Review {
    private final int reviewId;
    private int custId;
    private int rating;
    private String comments;
    private String date;

    public Review(int reviewId, int custId, int rating, String comments, String date) {
        this.reviewId = reviewId;
        this.custId = custId;
        this.rating = rating;
        this.comments = comments;
        this.date = date;
    }

    public int getReviewId() { return reviewId; }
    public int getCustId() { return custId; }
    public int getRating() { return rating; }
    public String getComments() { return comments; }
    public String getDate() { return date; }

    public void setRating(int rating) { this.rating = rating; }
    public void setComments(String comments) { this.comments = comments; }
    public void setDate(String date) { this.date = date; }

    @Override
    public String toString() {
        return "Review ID: " + reviewId + ", Customer: " + custId + ", Rating: " + rating + ", Date: " + date;
    }
}
