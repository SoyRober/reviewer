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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewSummaryService {
    private final ReviewSummaryRepo reviewSummaryRepo;
    private final ReviewSummaryMapper reviewSummaryMapper;

    public List<String> findAllReturningProject() {
        return reviewSummaryRepo.findProjectIdBy();
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
}
