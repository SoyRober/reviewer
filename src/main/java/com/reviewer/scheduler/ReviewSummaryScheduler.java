package com.reviewer.scheduler;

import com.reviewer.entity.Review;
import com.reviewer.entity.ReviewSummary;
import com.reviewer.model.Evaluation;
import com.reviewer.model.EvaluationSummary;
import com.reviewer.service.ReviewService;
import com.reviewer.service.ReviewSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReviewSummaryScheduler {
    private final ReviewService reviewService;
    private final ReviewSummaryService reviewSummaryService;

    //    @Scheduled(cron = "0 0 * * * *")
    @Scheduled(cron = "*/5 * * * * *")
    public void summarizeReviewsHourly() {
        List<String> notUpdatedProjects = reviewSummaryService.findAllReturningProject();

        for (String projectAddress : notUpdatedProjects) {
            Review lastReview = reviewService.findRecent(projectAddress);
            Instant oneHourAgo = Instant.now().minusSeconds(3600);

            if (lastReview == null || !lastReview.getCreatedAt().isAfter(oneHourAgo)) continue;

            // Obtén la lista de nombres de las columnas a sumar desde Evaluation
            List<String> evaluationColumns = Arrays.stream(Evaluation.class.getDeclaredFields())
                    .map(Field::getName)
                    .toList();

            // Usa un mapa para almacenar los totales de cada columna
            Map<String, Long> summaryValues = evaluationColumns.stream()
                    .collect(Collectors.toMap(
                            column -> column + "Summary", // Crea la clave con el sufijo "Summary"
                            column -> reviewService.sumAllColumnValueByProject(column, projectAddress) //Modificar para hacer la media de la columna
                    ));

            // Construye el objeto EvaluationSummary
            EvaluationSummary evaluationSummary = new EvaluationSummary();
            BeanUtils.copyProperties(summaryValues, evaluationSummary);
            Long totalReviews = reviewService.countProjectReviews(projectAddress);

            Long totalSumOfAllColumns = 0L;
            for (Long value : summaryValues.values()) {
                totalSumOfAllColumns += value;
            }

            int numberOfEvaluationColumns = evaluationColumns.size();
            Float avg = 0f;
            if (totalReviews != null && totalReviews > 0) {
                long totalEvaluations = totalReviews * numberOfEvaluationColumns;
                avg = (float) totalSumOfAllColumns / totalEvaluations;
            }

            reviewSummaryService.update(projectAddress, evaluationSummary, totalReviews, avg);
        }
    }
}
