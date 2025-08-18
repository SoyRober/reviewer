package com.reviewer.repository;

import com.reviewer.entity.ReviewSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewSummaryRepo extends MongoRepository<ReviewSummary, UUID> {
    List<UUID> findProjectIdBy();

    Optional<ReviewSummary> findByProjectId(UUID projectId);
}
