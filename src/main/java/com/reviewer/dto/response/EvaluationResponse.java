package com.reviewer.dto.response;

import com.reviewer.dto.request.EvaluationRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationResponse {
    private String clientAddress;

    private String projectAddress;

    private String comment;

    private Instant createdAt;

    private Instant updatedAt;

    private EvaluationRequest evaluation;
}
