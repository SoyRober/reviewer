package com.reviewer.service;

import com.reviewer.entity.ReviewSummary;
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

    public List<String> findAllReturningProject() {
        return reviewSummaryRepo.findProjectIdBy();
    }

    public void update(UUID projectId, EvaluationSummary evaluationSummary, Long totalReviews, Float avg) {
        Optional<ReviewSummary> existingSummary = reviewSummaryRepo.findByProjectId(projectId);

        // Creates or updates
        ReviewSummary newSummary = existingSummary.orElseGet(ReviewSummary::new);
        newSummary.setProjectId(projectId);
        newSummary.setEvaluationSummary(evaluationSummary);
        newSummary.setId(UUID.randomUUID());
        newSummary.setTotalReviews(totalReviews);
        newSummary.setAverage(avg);

        reviewSummaryRepo.save(newSummary);
    }
}
