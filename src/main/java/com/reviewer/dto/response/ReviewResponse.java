package com.reviewer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private String clientAddress;

    private String projectId;

    private String comment;

    private Instant createdAt;

    private Instant updatedAt;

    private EvaluationResponse evaluation;

    private Float average;
}
