package com.reviewer.scheduler;

import com.reviewer.service.ReviewReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewReportScheduler {
    private final ReviewReportService reviewReportService;

    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupOldReports() {
        log.info("Starting cleanup of old reports.");

        Instant sixtyDaysAgo = Instant.now().minus(60, ChronoUnit.DAYS);

        long deletedCount = reviewReportService.deleteAllCreatedAtBefore(sixtyDaysAgo);

        log.info("Finished cleanup. {} old reports were deleted.", deletedCount);
    }
}
