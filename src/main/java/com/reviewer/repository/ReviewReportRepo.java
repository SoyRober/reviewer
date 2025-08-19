package com.reviewer.repository;

import com.reviewer.entity.ReviewReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewReportRepo extends MongoRepository<ReviewReport, UUID> {
    Optional<ReviewReport> findByClientAddressAndReviewId(String request, UUID reviewId);

    List<ReviewReport> findAllByReviewId(UUID reviewId);

    List<ReviewReport> findAllByClientAddress(String clientId);
}
