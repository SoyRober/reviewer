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
import com.reviewer.util.FilterUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepo reviewRepo;
    private final FilterUtil filterUtil;
    private final ReviewMapper reviewMapper;
    private final ProfilerService profilerService;
    private final ReviewSummaryService reviewSummaryService;

    public ReviewResponse create(ReviewRequest request, UUID projectId) throws IllegalAccessException {
        Review review = reviewRepo.findByClientAddressAndProjectId(request.getClientAddress(), projectId)
                .orElse(new Review());

        reviewMapper.updateReviewFromReviewRequest(request, review);

        if (review.getId() == null) {
            review.setId(UUID.randomUUID());
            review.setProjectId(projectId);
            review.setCreatedAt(Instant.now());
            review.setIsActive(true);

            profilerService.updateExperience(request.getClientAddress(), ActionType.ADD_REVIEW);
        }

        review.setAverage(calculateAvg(review.getEvaluation()));
        review = reviewRepo.save(review);

        if (!reviewSummaryService.existsByProjectId(projectId)) {
            reviewSummaryService.create(projectId, review, countProjectReviews(projectId));
        }

        return reviewMapper.toReviewResponse(review);
    }

    private BigDecimal calculateAvg(Evaluation evaluation) throws IllegalAccessException {
        long totalSum = 0L;
        int numberOfFields = 0;

        Field[] fields = Evaluation.class.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Long value = (Long) field.get(evaluation);

            if (value != null) {
                totalSum += value;
                numberOfFields++;
            }
        }

        if (numberOfFields == 0) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        return new BigDecimal((double) totalSum / numberOfFields / 20.0)
                .setScale(2, RoundingMode.HALF_UP);
    }

    //Todo: use filterutil
    public PaginationResponse<ReviewResponse> getFromProject(UUID projectId, @Valid PaginationRequest request, boolean isActive) {
        List<String> validSortFields = List.of("clientAddress", "projectId", "createdAt", "average");
        String defaultSortField = "createdAt";
        Sort sort = filterUtil.getDirectionAndField(request.isDirection(), request.getSortBy(), validSortFields, defaultSortField);
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

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

    public PaginationResponse<ReviewResponse> getFromClient(String client, @Valid PaginationRequest request, boolean isActive) {
        Sort sort = getDirectionAndField(request.isDirection(), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<Review> reviews = isActive ?
                reviewRepo.findByClientAddress(client, pageable) :
                reviewRepo.findByClientAddressAndIsActiveFalse(client, pageable);

        return PaginationResponse.<ReviewResponse>builder()
                .content(reviewMapper.toReviewResponseList(reviews.getContent()))
                .totalElements(reviews.getTotalElements())
                .build();
    }

    //Todo: use filterutil
    private Sort getDirectionAndField(boolean directionBool, String sortBy) {
        Sort.Direction sortDirection = Sort.Direction.ASC;
        String direction = directionBool ? "desc" : "asc";

        try {
            sortDirection = Sort.Direction.fromString(direction);
            log.error("Sort direction provided: {}", direction);
        } catch (IllegalArgumentException e) {
            log.error("Invalid sort direction provided: {}. Defaulting to ASC.", direction);
        }

        List<String> validSortByFields = List.of("clientAddress", "projectId", "createdAt", "isActive");
        String actualSortByField = validSortByFields.contains(sortBy.trim()) ?
                sortBy.trim() :
                "createdAt";

        return Sort.by(sortDirection, actualSortByField);
    }

    public Long countByProjectId(UUID projectContract) {
        return reviewRepo.countByProjectId(projectContract);
    }

    public ReviewResponse getFromProjectAndClient(UUID projectId, String clientAddress) {
        Review review = reviewRepo.findByClientAddressAndProjectId(clientAddress, projectId)
                .orElseThrow(() -> new NotFoundException("Review not found for project and client"));

        return reviewMapper.toReviewResponse(review);
    }

    public void deleteFromProjectAndClient(UUID projectId, String clientAddress) {
        reviewRepo.deleteByProjectIdAndClientAddress(projectId, clientAddress);
    }

    public ReviewResponse activateAndDeactivate(UUID reviewId) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found"));
        review.setIsActive(!review.getIsActive());

        return reviewMapper.toReviewResponse(reviewRepo.save(review));
    }
}
