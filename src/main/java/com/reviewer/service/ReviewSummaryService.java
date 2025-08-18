package com.reviewer.service;

import com.reviewer.dto.response.ReviewSummaryResponse;
import com.reviewer.entity.Review;
import com.reviewer.entity.ReviewSummary;
import com.reviewer.exception.NotFoundException;
import com.reviewer.mapper.ReviewSummaryMapper;
import com.reviewer.model.EvaluationSummary;
import com.reviewer.repository.ReviewSummaryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewSummaryService {
    private final ReviewSummaryRepo reviewSummaryRepo;
    private final ReviewSummaryMapper reviewSummaryMapper;
    private final MongoTemplate mongoTemplate;

    public List<UUID> findAllReturningProject() {
        return reviewSummaryRepo.findProjectIdBy().stream().map(ReviewSummary::getProjectId).toList();
    }

    public void create(UUID projectId, Review review, Long totalReviews) {
        Optional<ReviewSummary> existingSummary = reviewSummaryRepo.findByProjectId(projectId);

        EvaluationSummary evaluationSummary = EvaluationSummary.builder()
                .communitySummary(review.getEvaluation().getCommunity())
                .potentialSummary(review.getEvaluation().getPotential())
                .securitySummary(review.getEvaluation().getPotential())
                .tokenomicsSummary(review.getEvaluation().getTokenomics())
                .trustSummary(review.getEvaluation().getTrust())
                .build();

        ReviewSummary newSummary = existingSummary.orElseGet(ReviewSummary::new);
        newSummary.setProjectId(projectId);
        newSummary.setEvaluationSummary(evaluationSummary);

        UUID id = newSummary.getId() != null ? newSummary.getId() : UUID.randomUUID();
        newSummary.setId(id);

        newSummary.setTotalReviews(totalReviews);
        newSummary.setAverage(review.getAverage());

        reviewSummaryRepo.save(newSummary);
    }

    public ReviewSummaryResponse getEvaluation(UUID projectId) {
        ReviewSummary summary = reviewSummaryRepo.findByProjectId(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));
        return reviewSummaryMapper.toReviewSummaryResponse(summary);
    }

    public boolean existsByProjectId(UUID projectId) {
        return reviewSummaryRepo.findByProjectId(projectId).isPresent();
    }

    public void update(UUID projectContract, Long totalReviews) {
        ReviewSummary reviewSummary = reviewSummaryRepo.findByProjectId(projectContract)
                .orElseThrow(() -> {
                    log.info("This summary should exist, but it does not. Project ID: {}", projectContract);
                    return new NotFoundException("ReviewSummary not found");
                });

        Optional<EvaluationSummary> optionalSummary = getProjectEvaluationSummary(projectContract);

        if (optionalSummary.isPresent()) {
            EvaluationSummary newAverages = optionalSummary.get();
            reviewSummary.getEvaluationSummary().setTrustSummary(newAverages.getTrustSummary());
            reviewSummary.getEvaluationSummary().setSecuritySummary(newAverages.getSecuritySummary());
            reviewSummary.getEvaluationSummary().setTokenomicsSummary(newAverages.getTokenomicsSummary());
            reviewSummary.getEvaluationSummary().setCommunitySummary(newAverages.getCommunitySummary());
            reviewSummary.getEvaluationSummary().setPotentialSummary(newAverages.getPotentialSummary());

            long sumOfAllAverages = newAverages.getTrustSummary()
                    + newAverages.getSecuritySummary()
                    + newAverages.getTokenomicsSummary()
                    + newAverages.getCommunitySummary()
                    + newAverages.getPotentialSummary();

            BigDecimal averageOn100Scale = new BigDecimal(sumOfAllAverages)
                    .divide(new BigDecimal(5), 2, RoundingMode.HALF_UP);
            BigDecimal finalAverage = averageOn100Scale.divide(new BigDecimal(20), 2, RoundingMode.HALF_UP);


            reviewSummary.setAverage(finalAverage);
            reviewSummary.setTotalReviews(totalReviews);

            reviewSummaryRepo.save(reviewSummary);

        } else {
            log.warn("No reviews found for project ID: {}. Evaluation summary was not updated.", projectContract);
            reviewSummary.getEvaluationSummary().setTrustSummary(0L);
            reviewSummary.getEvaluationSummary().setSecuritySummary(0L);
            reviewSummary.getEvaluationSummary().setTokenomicsSummary(0L);
            reviewSummary.getEvaluationSummary().setCommunitySummary(0L);
            reviewSummary.getEvaluationSummary().setPotentialSummary(0L);
            reviewSummary.setAverage(BigDecimal.ZERO);
            reviewSummary.setTotalReviews(0L);
            reviewSummaryRepo.save(reviewSummary);
        }
    }

    public Optional<EvaluationSummary> getProjectEvaluationSummary(UUID projectContract) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("projectId").is(projectContract)),
                group()
                        .avg("evaluation.trust").as("trustSummary")
                        .avg("evaluation.security").as("securitySummary")
                        .avg("evaluation.tokenomics").as("tokenomicsSummary")
                        .avg("evaluation.community").as("communitySummary")
                        .avg("evaluation.potential").as("potentialSummary")
        );

        AggregationResults<EvaluationSummary> results = mongoTemplate.aggregate(
                aggregation, "review", EvaluationSummary.class
        );

        return Optional.ofNullable(results.getUniqueMappedResult());
    }
}
