package com.reviewer.repository;

import com.reviewer.entity.ReviewReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewReportRepo extends MongoRepository<ReviewReport, UUID> {
    Optional<ReviewReport> findByClientAddressAndReviewId(String request, UUID reviewId);

    long deleteByCreatedAtBefore(Instant sixtyDaysAgo);
}
