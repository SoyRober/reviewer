package com.reviewer.service;

import com.reviewer.dto.request.ReviewRequest;
import com.reviewer.dto.response.ReviewResponse;
import com.reviewer.entity.Review;
import com.reviewer.mapper.ReviewMapper;
import com.reviewer.repository.ReviewRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewRepo reviewRepo;
    private final ReviewMapper reviewMapper;

    public ReviewResponse create(ReviewRequest request) {
        Review review = reviewRepo.findByClientAddressAndProjectAddress(request.getClientAddress(), request.getProjectAddress()).orElse(new Review());

        if (review.getId() == null) {
            reviewMapper.updateReviewFromReviewRequest(request, review);
            review.setId(UUID.randomUUID());
            reviewRepo.save(review);
        }

        return reviewMapper.toReviewResponse(review);
    }
}
