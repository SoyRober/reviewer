package com.reviewer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    @NotNull
    @NotBlank
    private String clientAddress;

    @NotNull
    @NotBlank
    private String projectAddress;

    private String comment;

    private Instant createdAt;

    private Instant updatedAt;

    @NotNull
    private EvaluationRequest evaluation;
}
