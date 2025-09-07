package com.reviewer.service;

import com.reviewer.client.profiler.model.ActionType;
import com.reviewer.client.profiler.service.ProfilerService;
import com.reviewer.dto.request.PaginationRequest;
import com.reviewer.dto.request.ReviewRequest;
import com.reviewer.dto.response.PaginationResponse;
import com.reviewer.dto.response.ReviewResponse;
import com.reviewer.entity.Review;
import com.reviewer.exception.NotFoundException;
import com.reviewer.mapper.ReviewMapper;
import com.reviewer.model.Evaluation;
import com.reviewer.repository.ReviewRepo;
import com.reviewer.util.FilterUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepo reviewRepo;
    private final ReviewMapper reviewMapper;
    private final ProfilerService profilerService;
    private final ReviewSummaryService reviewSummaryService;

    public ReviewResponse create(ReviewRequest request, UUID projectId) throws IllegalAccessException {
        Review review = reviewRepo.findByClientAddressAndProjectId(request.getClientAddress(), projectId)
                .orElse(null);

        if (review == null) {
            review = Review.builder().projectId(projectId).build();
            profilerService.updateExperience(request.getClientAddress(), ActionType.ADD_REVIEW);
        }

        review.setAverage(calculateAvg(review.getEvaluation()));
        reviewMapper.updateReviewFromReviewRequest(request, review);
        review = reviewRepo.save(review);

        if (!reviewSummaryService.existsByProjectId(projectId))
            reviewSummaryService.create(projectId, review, countProjectReviews(projectId));

        return reviewMapper.toReviewResponse(review);
    }

    private BigDecimal calculateAvg(Evaluation evaluation) throws IllegalAccessException {
        long totalSum = 0L;
        int numberOfFields = 0;

        Field[] fields = Evaluation.class.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Long value = (Long) field.get(evaluation);

            if (value == null) continue;
            totalSum += value;
            numberOfFields++;
        }

        if (numberOfFields == 0) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        return new BigDecimal((double) totalSum / numberOfFields / 20.0)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public PaginationResponse<ReviewResponse> getFromProject(UUID projectId, @Valid PaginationRequest request, boolean isActive) {
        Pageable pageable = FilterUtils.buildPageable(request, Review.getValidSortFields(), Review.getDefaultSortField());

        Page<Review> reviews = isActive ?
                reviewRepo.findByProjectId(projectId, pageable) :
                reviewRepo.findByProjectIdAndIsActiveFalse(projectId, pageable);

        return PaginationResponse.<ReviewResponse>builder()
                .content(reviewMapper.toReviewResponseList(reviews.getContent()))
                .totalElements(reviews.getTotalElements())
                .build();
    }

    public Review findRecent(UUID projectId) {
        return reviewRepo.findFirstByProjectIdOrderByCreatedAtDesc(projectId);
    }

    public Long countProjectReviews(UUID projectId) {
        return reviewRepo.countByProjectId(projectId);
    }

    public PaginationResponse<ReviewResponse> getFromClient(String clientAddress, @Valid PaginationRequest request, boolean isActive) {
        Pageable pageable = FilterUtils.buildPageable(request, Review.getValidSortFields(), Review.getDefaultSortField());

        Page<Review> reviews = isActive ?
                reviewRepo.findByClientAddress(clientAddress, pageable) :
                reviewRepo.findByClientAddressAndIsActiveFalse(clientAddress, pageable);

        return PaginationResponse.<ReviewResponse>builder()
                .content(reviewMapper.toReviewResponseList(reviews.getContent()))
                .totalElements(reviews.getTotalElements())
                .build();
    }

    public Long countByProjectId(UUID projectContract) {
        return reviewRepo.countByProjectId(projectContract);
    }

    public ReviewResponse activateAndDeactivate(UUID reviewId) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found"));
        review.setIsActive(!review.getIsActive());

        return reviewMapper.toReviewResponse(reviewRepo.save(review));
    }

    public ReviewResponse getFromProjectAndClient(UUID projectId, String clientAddress) {
        Review review = reviewRepo.findByClientAddressAndProjectIdAndIsActiveTrue(clientAddress, projectId)
                .orElseThrow(() -> new NotFoundException("Review not found"));
        return reviewMapper.toReviewResponse(review);
    }

    public void deleteById(UUID id) {
        reviewRepo.deleteById(id);
    }
}
