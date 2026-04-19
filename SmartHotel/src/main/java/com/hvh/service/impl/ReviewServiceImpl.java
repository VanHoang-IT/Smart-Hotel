/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service.impl;

import com.hvh.pojo.Review;
import com.hvh.repository.ReviewRepository;
import com.hvh.service.ReviewService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author 03358
 */
@Service
public class ReviewServiceImpl implements ReviewService{
    @Autowired
    private ReviewRepository reviewRepo;
    
    
    @Override
    public List<Review> getReviews(Map<String, String> params) {
        return this.reviewRepo.getReviews(params);
    }

    @Override
    public void addReviewOrUpdate(Review r) {
       this.reviewRepo.addReviewOrUpdate(r);
    }

    @Override
    public Review getReviewById(Long id) {
        return this.reviewRepo.getReviewById(id);
    }

    @Override
    public void deleteReview(Long id) {
        this.reviewRepo.deleteReview(id);
    }
    
}
