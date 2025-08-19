package com.reviewer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReportResponse {
    private UUID id;

    private UUID reviewId;

    private String clientAddress;

    private Instant createdAt;
}
