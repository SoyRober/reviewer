package com.reviewer.repository;

import com.reviewer.dto.response.ReviewResponse;
import com.reviewer.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepo extends MongoRepository<Review, UUID> {
    Optional<Review> findByClientAddressAndProjectId(String clientAddress, UUID projectId);

    Page<Review> findByProjectId(UUID projectId, Pageable pageable);

    Review findFirstByProjectIdOrderByCreatedAtDesc(String projectId);

    Page<Review> findByProjectIdAndIsActiveFalse(UUID projectId, Pageable pageable);

    Long countByProjectId(UUID projectId);

    Page<Review> findByClientAddressAndIsActiveFalse(String client, Pageable pageable);

    Page<Review> findByClientAddress(String client, Pageable pageable);

    List<Review> findTop3ByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
