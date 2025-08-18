package com.reviewer.scheduler;

import com.reviewer.entity.Review;
import com.reviewer.model.EvaluationSummary;
import com.reviewer.service.ReviewService;
import com.reviewer.service.ReviewSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewSummaryScheduler {
    private final ReviewService reviewService;
    private final ReviewSummaryService reviewSummaryService;

    //    @Scheduled(cron = "0 0 * * * *")
    @Scheduled(cron = "*/5 * * * * *")
    public void summarizeReviewsHourly() {
        List<UUID> notUpdatedProjects = reviewSummaryService.findAllReturningProject();

        for (UUID projectContract : notUpdatedProjects) {
            Review lastReview = reviewService.findRecent(projectContract);
            Instant oneHourAgo = Instant.now().minusSeconds(3600);

            if (lastReview == null || !lastReview.getCreatedAt().isAfter(oneHourAgo)) continue;

            // Step 1: Use a single aggregation query to get all the average scores
            Optional<EvaluationSummary> optionalSummary = reviewService.getProjectEvaluationSummary(projectContract);

            if (optionalSummary.isPresent()) {
                EvaluationSummary newAverages = optionalSummary.get();

                // Step 2: Use another query to get the total review count
                Long totalReviews = reviewService.countProjectReviews(projectContract);

                // Step 3: Call the update method with the calculated values
                // The `update` method should handle the saving logic.
                reviewSummaryService.update(projectContract, newAverages, totalReviews, null);
            } else {
                // Handle cases where no reviews are found for a project.
                // For example, you might reset the summary values to zero.
                log.warn("No reviews found for project ID: {}. Skipping update.", projectContract);
            }
        }
    }}
