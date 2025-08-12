package com.reviewer.dto.response;

import java.time.Instant;
import java.util.UUID;

public class ReviewReportResponse {
    private UUID id;

    private UUID reviewId;

    private UUID clientId;

    private Instant createdAt;
}
