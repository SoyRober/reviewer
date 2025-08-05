package com.reviewer.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRequest {

    @Min(0)
    @Max(100)
    @NotNull
    private Long trust;

    @Min(0)
    @Max(100)
    @NotNull
    private Long security;

    @Min(0)
    @Max(100)
    @NotNull
    private Long tokenomics;

    @Min(0)
    @Max(100)
    @NotNull
    private Long community;

    @Min(0)
    @Max(100)
    @NotNull
    private Long potential;
}
