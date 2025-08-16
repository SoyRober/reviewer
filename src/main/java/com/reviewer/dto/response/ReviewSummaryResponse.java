package com.reviewer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSummaryResponse {
    private String projectId;

    private EvaluationSummaryResponse evaluationSummary;

    private Float average;

    private Long totalReviews;
}
