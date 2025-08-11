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
    Optional<Review> findByClientAddressAndProjectContract(String clientAddress, String projectContract);

    Page<Review> findByProjectContract(String project, Pageable pageable);

    Review findFirstByProjectContractOrderByCreatedAtDesc(String project);

    Page<Review> findByProjectContractAndIsActiveFalse(String projectContract, Pageable pageable);

    Long countByProjectContract(String projectContract);

    Page<Review> findByClientAddressAndIsActiveFalse(String client, Pageable pageable);

    Page<Review> findByClientAddress(String client, Pageable pageable);
}
