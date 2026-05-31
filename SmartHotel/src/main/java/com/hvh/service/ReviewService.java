/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service;

import com.hvh.dto.ReviewRequestDTO;
import com.hvh.dto.ReviewResponseDTO;
import com.hvh.pojo.Review;
import com.hvh.pojo.User;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 03358
 */
public interface ReviewService {

    List<ReviewResponseDTO> getReviews(Map<String, String> params);

    List<ReviewResponseDTO> getReviewsByRoomId(Long roomId);

    ReviewResponseDTO addReview(
            Long reservationId,
            ReviewRequestDTO dto,
            User currentUser);

    ReviewResponseDTO updateReview(Long id, ReviewRequestDTO dto, User user);

    Review getReviewById(Long id);

    void deleteReview(Long id);
}
