package com.reviewer.service;

import com.reviewer.dto.request.PaginationRequest;
import com.reviewer.dto.request.ReviewRequest;
import com.reviewer.dto.response.EvaluationResponse;
import com.reviewer.dto.response.PaginationResponse;
import com.reviewer.dto.response.ReviewResponse;
import com.reviewer.entity.Review;
import com.reviewer.mapper.ReviewMapper;
import com.reviewer.model.Evaluation;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Slf4j
@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewRepo reviewRepo;
    private final ReviewMapper reviewMapper;
    private final MongoTemplate mongoTemplate;
    private final ReviewSummaryService reviewSummaryService;

    public ReviewResponse create(ReviewRequest request) throws IllegalAccessException {
        Review review = reviewRepo.findByClientAddressAndProjectAddress(request.getClientAddress(), request.getProjectAddress())
                .orElse(new Review());

        if (review.getId() == null) {
            reviewMapper.updateReviewFromReviewRequest(request, review);
            review.setId(UUID.randomUUID());
            review.setCreatedAt(Instant.now());
            review = reviewRepo.save(review);

            EvaluationSummary evaluationSummary = EvaluationSummary.builder()
                    .communitySummary(request.getEvaluation().getCommunity())
                    .potentialSummary(request.getEvaluation().getPotential())
                    .securitySummary(request.getEvaluation().getPotential())
                    .tokenomicsSummary(request.getEvaluation().getTokenomics())
                    .trustSummary(request.getEvaluation().getTrust())
                    .build();
            reviewSummaryService.update(request.getProjectAddress(), evaluationSummary, countProjectReviews(request.getProjectAddress()), 0f);
        }

        ReviewResponse response = reviewMapper.toReviewResponse(review);
        response.setAverage(calculateAvg(response.getEvaluation(), review.getProjectAddress()));
        return response;
    }

    private Float calculateAvg(EvaluationResponse evaluation, String projectAddress) throws IllegalAccessException {
        long totalSum = 0L;
        int numberOfFields = 0;

        // Obtener todos los campos de la clase EvaluationResponse
        Field[] fields = EvaluationResponse.class.getDeclaredFields();

        for (Field field : fields) {
            // Asegurarse de que el campo sea accesible (en caso de que sea privado)
            field.setAccessible(true);
                // Obtener el valor del campo del objeto 'evaluation'
                // El valor se leerá como un Long, según la definición de la clase
                Long value = (Long) field.get(evaluation);

                // Si el valor no es nulo, sumarlo al total
                if (value != null) {
                    totalSum += value;
                    numberOfFields++;
                }
        }

        // Evitar una división por cero
        if (numberOfFields == 0) return 0f;

        float avg = ((float) totalSum / numberOfFields) / 20F;

        return Math.round(avg * 100.0f) / 100.0f;
    }

    public PaginationResponse<ReviewResponse> getFromProject(String project, @Valid PaginationRequest request, boolean isActive) {
        Sort.Direction sortDirection = Sort.Direction.ASC;
        String direction = request.isDirection() ? "desc" : "asc";

        try {
            sortDirection = Sort.Direction.fromString(direction);
            log.error("Sort direction provided: {}", direction);
        } catch (IllegalArgumentException e) {
            log.error("Invalid sort direction provided: {}. Defaulting to ASC.", direction);
        }

        List<String> validSortByFields = List.of("clientAddress", "projectAddress", "createdAt", "isActive");
        String actualSortByField = validSortByFields.contains(request.getSortBy().trim()) ?
                request.getSortBy().trim() :
                "createdAt";

        Sort sort = Sort.by(sortDirection, actualSortByField);
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<Review> reviews = isActive ?
                reviewRepo.findByProjectAddress(project, pageable) :
                reviewRepo.findByProjectAddressAndIsActiveFalse(project, pageable);

        return PaginationResponse.<ReviewResponse>builder()
                .content(reviewMapper.toReviewResponseList(reviews.getContent()))
                .totalElements(reviews.getTotalElements())
                .build();
    }

    public Review findRecent(String project) {
        return reviewRepo.findFirstByProjectAddressOrderByCreatedAtDesc(project);
    }

    public Long sumAllColumnValueByProject(String column, String project) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("projectAddress").is(project)),
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

    public Long countProjectReviews(String projectAddress) {
        return reviewRepo.countByProjectAddress(projectAddress);
    }
}
