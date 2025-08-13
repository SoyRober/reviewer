package com.reviewer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@Document
@AllArgsConstructor
@NoArgsConstructor
public class ReviewReport {
    @Id
    @Indexed(unique = true)
    private UUID id;

    private UUID reviewId;

    private UUID clientId;

    private Instant createdAt;
}
