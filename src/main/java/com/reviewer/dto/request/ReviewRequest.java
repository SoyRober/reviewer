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

    private String comment;

    @NotNull
    private EvaluationRequest evaluation;

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress != null ? clientAddress.toLowerCase() : null;
    }
}
