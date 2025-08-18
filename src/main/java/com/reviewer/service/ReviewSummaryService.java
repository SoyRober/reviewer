package com.reviewer.service;

import com.reviewer.dto.response.ReviewSummaryResponse;
import com.reviewer.entity.Review;
import com.reviewer.entity.ReviewSummary;
import com.reviewer.exception.NotFoundException;
import com.reviewer.mapper.ReviewSummaryMapper;
import com.reviewer.model.EvaluationSummary;
import com.reviewer.repository.ReviewSummaryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewSummaryService {
    private final ReviewSummaryRepo reviewSummaryRepo;
    private final ReviewSummaryMapper reviewSummaryMapper;

    public List<UUID> findAllReturningProject() {
        return reviewSummaryRepo.findProjectIdBy();
    }

    public void create(UUID projectId, Review review, Long totalReviews) {
        Optional<ReviewSummary> existingSummary = reviewSummaryRepo.findByProjectId(projectId);

        EvaluationSummary evaluationSummary = EvaluationSummary.builder()
                .communitySummary(review.getEvaluation().getCommunity())
                .potentialSummary(review.getEvaluation().getPotential())
                .securitySummary(review.getEvaluation().getPotential())
                .tokenomicsSummary(review.getEvaluation().getTokenomics())
                .trustSummary(review.getEvaluation().getTrust())
                .build();

        ReviewSummary newSummary = existingSummary.orElseGet(ReviewSummary::new);
        newSummary.setProjectId(projectId);
        newSummary.setEvaluationSummary(evaluationSummary);

        UUID id = newSummary.getId() != null ? newSummary.getId() : UUID.randomUUID();
        newSummary.setId(id);

        newSummary.setTotalReviews(totalReviews);
        newSummary.setAverage(review.getAverage());

        reviewSummaryRepo.save(newSummary);
    }

    public ReviewSummaryResponse getEvaluation(UUID projectId) {
        ReviewSummary summary = reviewSummaryRepo.findByProjectId(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));
        return reviewSummaryMapper.toReviewSummaryResponse(summary);
    }

    public boolean existsByProjectId(UUID projectId) {
        return reviewSummaryRepo.findByProjectId(projectId).isPresent();
    }

    public void update(UUID projectContract) {
        // Paso 1: Obtener el documento ReviewSummary existente
        ReviewSummary reviewSummary = reviewSummaryRepo.findByProjectId(projectContract)
                .orElseThrow(() -> {
                    log.info("This summary should exist, but it does not. Project ID: {}", projectContract);
                    return new NotFoundException("ReviewSummary not found");
                });

        // Paso 2: Usar el servicio de agregación para obtener las medias actualizadas
        Optional<EvaluationSummary> optionalSummary = getProjectEvaluationSummary(projectContract);

        // Paso 3: Si el resultado de la agregación existe, actualiza el documento y guárdalo
        if (optionalSummary.isPresent()) {
            EvaluationSummary newAverages = optionalSummary.get();

            reviewSummary.getEvaluationSummary().setTrustSummary(newAverages.getTrustSummary());
            reviewSummary.getEvaluationSummary().setSecuritySummary(newAverages.getSecuritySummary());
            reviewSummary.getEvaluationSummary().setTokenomicsSummary(newAverages.getTokenomicsSummary());
            reviewSummary.getEvaluationSummary().setCommunitySummary(newAverages.getCommunitySummary());
            reviewSummary.getEvaluationSummary().setPotentialSummary(newAverages.getPotentialSummary());

            // Aquí podrías actualizar otros campos si lo necesitas, como el número de reviews.
            // reviewSummary.setTotalReviews(totalReviews);

            // Paso 4: Guarda el documento actualizado en la base de datos
            reviewSummaryRepo.save(reviewSummary);
        } else {
            // Manejar el caso en el que no se encontraron reviews para el proyecto
            // Podrías lanzar una excepción, registrar un error o simplemente no hacer nada.
            log.warn("No reviews found for project ID: {}. Evaluation summary was not updated.", projectContract);
        }
    }

    private Optional<EvaluationSummary> getProjectEvaluationSummary(UUID projectContract) {
        return reviewSummaryRepo.findByProjectId(projectContract)
                .map(reviewSummary -> EvaluationSummary.builder()
                        .trustSummary(reviewSummary.getEvaluationSummary().getTrustSummary())
                        .securitySummary(reviewSummary.getEvaluationSummary().getSecuritySummary())
                        .tokenomicsSummary(reviewSummary.getEvaluationSummary().getTokenomicsSummary())
                        .communitySummary(reviewSummary.getEvaluationSummary().getCommunitySummary())
                        .potentialSummary(reviewSummary.getEvaluationSummary().getPotentialSummary())
                        .build());
    }
}
