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

//    Only for testing purposes
//    @Scheduled(cron = "*/5 * * * * *")
    @Scheduled(cron = "0 0 * * * *")
    public void summarizeReviewsHourly() {
        List<UUID> notUpdatedProjects = reviewSummaryService.findAllReturningProject();
        log.info("Found {} projects to update", notUpdatedProjects.size());

        for (UUID projectContract : notUpdatedProjects) {
            Review lastReview = reviewService.findRecent(projectContract);
            Instant oneHourAgo = Instant.now().minusSeconds(3600);

            if (lastReview == null || !lastReview.getCreatedAt().isAfter(oneHourAgo)) continue;
            log.info("Updating summary for project: {}", projectContract);

            Long totalReviews = reviewService.countByProjectId(projectContract);

            reviewSummaryService.update(projectContract, totalReviews);
        }
    }
}
