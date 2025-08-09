package com.reviewer.service;

import com.reviewer.entity.Review;
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
        return reviewSummaryRepo.findProjectAddressBy();
    }

    public void save(ReviewSummary newSummary) {
        reviewSummaryRepo.save(newSummary);
    }

    public void update(String projectAddress, EvaluationSummary evaluationSummary) {
        Optional<ReviewSummary> existingSummary = reviewSummaryRepo.findByProjectAddress(projectAddress);

        // Creates or updates
        ReviewSummary newSummary = existingSummary.orElseGet(ReviewSummary::new);
        newSummary.setProjectAddress(projectAddress);
        newSummary.setEvaluationSummary(evaluationSummary);
        newSummary.setId(UUID.randomUUID());

        reviewSummaryRepo.save(newSummary);
    }

//    No hace falta, siempre se actualiza cada hora
//    public List<ReviewSummary> findNotUpdated() {
//        Instant oneHourAgo = Instant.now().minusSeconds(3600);
//        return reviewSummaryRepo.findByUpdatedAtBefore(oneHourAgo);
//    }
}
