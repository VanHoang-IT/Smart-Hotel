/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service.impl;

import com.hvh.dto.ReviewRequestDTO;
import com.hvh.dto.ReviewResponseDTO;
import com.hvh.pojo.CustomerProfile;
import com.hvh.pojo.Reservation;
import com.hvh.pojo.Review;
import com.hvh.pojo.User;
import com.hvh.repository.ReservationRepository;
import com.hvh.repository.ReviewRepository;
import com.hvh.service.ReviewService;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author 03358
 */
@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private ReservationRepository resRepo;

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviews(Map<String, String> params) {
        List<Review> reviews = this.reviewRepo.getReviews(params);
        return reviews.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByRoomId(Long roomId) {
        List<Review> reviews = this.reviewRepo.getReviewsByRoomId(roomId);
        return reviews.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Review getReviewById(Long id) {
        return this.reviewRepo.getReviewById(id);
    }

    @Override
    public void deleteReview(Long id) {
        this.reviewRepo.deleteReview(id);
    }

    private ReviewResponseDTO mapToDTO(Review review) {
        ReviewResponseDTO dto = new ReviewResponseDTO();
        if (review.getCreatedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String strDate = sdf.format(review.getCreatedAt());
            dto.setCreatedAt(strDate);
        }
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        Reservation res = review.getReservationId();
        if (res != null) {
            dto.setReservationId(res.getId());

            if (res.getCustomerId() != null && res.getCustomerId().getUserId() != null) {
                dto.setCustomerName(res.getCustomerId().getUserId().getFullName());
            }

            if (res.getReservationRoomSet() != null && !res.getReservationRoomSet().isEmpty()) {
                String roomNames = res.getReservationRoomSet().stream()
                        .map(resRoom -> resRoom.getRoomId().getName())
                        .collect(Collectors.joining(", "));
                dto.setRoomName(roomNames);
            }
        }
        return dto;
    }

    @Override
    public ReviewResponseDTO updateReview(Long reviewId, ReviewRequestDTO dto, User currentUser) {
        Review r = this.reviewRepo.getReviewById(reviewId);
        if (r == null) {
            throw new RuntimeException("Review không tồn tại!");
        }

        if (!r.getReservationId().getCustomerId().getUserId().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền sửa đánh giá này!");
        }

        r.setRating(dto.getRating());
        r.setComment(dto.getComment());

        this.reviewRepo.addReviewOrUpdate(r);
        return this.mapToDTO(r);
    }

    @Override
    public ReviewResponseDTO addReview(
            Long reservationId,
            ReviewRequestDTO dto,
            User currentUser) {

        Reservation res = this.resRepo.getReservationById(reservationId);

        if (res == null) {
            throw new RuntimeException("Đơn đặt phòng không tồn tại!");
        }

        if (!res.getCustomerId().getUserId().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền đánh giá đơn này!");
        }

        Review r = new Review();
        r.setReservationId(res);
        r.setRating(dto.getRating());
        r.setComment(dto.getComment());
        r.setCreatedAt(new Date());
        r.setVisible(true);

        this.reviewRepo.addReviewOrUpdate(r);

        return this.mapToDTO(r);
    }
}
