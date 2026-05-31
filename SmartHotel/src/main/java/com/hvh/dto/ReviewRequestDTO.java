package com.hvh.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class ReviewRequestDTO {

    @Min(value = 1, message = "Rating thấp nhất là 1 sao")
    @Max(value = 5, message = "Rating cao nhất là 5 sao")
    private int rating;

    @Size(max = 255, message = "Bình luận không quá 255 ký tự")
    private String comment;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}