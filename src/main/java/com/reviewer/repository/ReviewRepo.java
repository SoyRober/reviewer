package com.reviewer.repository;

import com.reviewer.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepo extends MongoRepository<Review, UUID> {
    Optional<Review> findByClientAddressAndProjectAddress(String clientAddress, String projectAddress);

    Page<Review> findByProjectAddress(String project, Pageable pageable);

    Review findFirstByProjectAddressOrderByCreatedAtDesc(String project);
}
