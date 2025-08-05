package com.reviewer.dto.response;

import com.reviewer.dto.request.EvaluationRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private String clientAddress;

    private String projectAddress;

    private String comment;

    private Instant createdAt;

    private Instant updatedAt;

    private EvaluationResponse evaluation;
}
