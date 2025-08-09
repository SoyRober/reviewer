package com.reviewer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationSummaryResponse {
    private Long trustSummary;

    private Long securitySummary;

    private Long tokenomicsSummary;

    private Long communitySummary;

    private Long potentialSummary;
}
