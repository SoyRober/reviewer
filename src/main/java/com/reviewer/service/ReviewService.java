package com.reviewer.service;

import com.reviewer.dto.request.PaginationRequest;
import com.reviewer.dto.request.ReviewRequest;
import com.reviewer.dto.response.EvaluationResponse;
import com.reviewer.dto.response.PaginationResponse;
import com.reviewer.dto.response.ReviewResponse;
import com.reviewer.entity.Review;
import com.reviewer.mapper.ReviewMapper;
import com.reviewer.model.EvaluationSummary;
import com.reviewer.repository.ReviewRepo;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Slf4j
@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewRepo reviewRepo;
    private final ReviewMapper reviewMapper;
    private final MongoTemplate mongoTemplate;
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
        }

        EvaluationSummary evaluationSummary = EvaluationSummary.builder()
                .communitySummary(request.getEvaluation().getCommunity())
                .potentialSummary(request.getEvaluation().getPotential())
                .securitySummary(request.getEvaluation().getPotential())
                .tokenomicsSummary(request.getEvaluation().getTokenomics())
                .trustSummary(request.getEvaluation().getTrust())
                .build();
        reviewSummaryService.create(projectId, evaluationSummary, countProjectReviews(projectId), 0f);

        review = reviewRepo.save(review);

        ReviewResponse response = reviewMapper.toReviewResponse(review);
        response.setAverage(calculateAvg(response.getEvaluation()));
        return response;
    }

    private Float calculateAvg(EvaluationResponse evaluation) throws IllegalAccessException {
        long totalSum = 0L;
        int numberOfFields = 0;

        Field[] fields = EvaluationResponse.class.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Long value = (Long) field.get(evaluation);

            if (value != null) {
                totalSum += value;
                numberOfFields++;
            }
        }

        if (numberOfFields == 0) return 0f;

        float avg = ((float) totalSum / numberOfFields) / 20F;

        return Math.round(avg * 100.0f) / 100.0f;
    }

    public PaginationResponse<ReviewResponse> getFromProject(String projectId, @Valid PaginationRequest request, boolean isActive) {
        Sort sort = getDirectionAndField(request.isDirection(), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<Review> reviews = isActive ?
                reviewRepo.findByProjectId(projectId, pageable) :
                reviewRepo.findByProjectIdAndIsActiveFalse(projectId, pageable);

        return PaginationResponse.<ReviewResponse>builder()
                .content(reviewMapper.toReviewResponseList(reviews.getContent()))
                .totalElements(reviews.getTotalElements())
                .build();
    }

    public Review findRecent(String projectId) {
        return reviewRepo.findFirstByProjectIdOrderByCreatedAtDesc(projectId);
    }

    public Long sumAllColumnValueByProject(String column, String projectId) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("projectId").is(projectId)),
                group().sum("evaluation." + column).as("totalSum")
        );

        AggregationResults<org.bson.Document> results = mongoTemplate.aggregate(
                aggregation,
                Review.class,
                org.bson.Document.class
        );

        org.bson.Document result = results.getUniqueMappedResult();

        return result != null ? result.getLong("totalSum") : 0L;
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

    private Sort getDirectionAndField (boolean directionBool, String sortBy) {
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
}
