package com.reviewer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponse {
    private Long trust;

    private Long security;

    private Long tokenomics;

    private Long community;

    private Long potential;
}
