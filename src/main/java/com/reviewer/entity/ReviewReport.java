package com.reviewer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndexes({
        @CompoundIndex(name = "client_review_unique_idx", def = "{'clientAddress': 1, 'reviewId': 1}", unique = true)
})
public class ReviewReport {
    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    private UUID reviewId;

    private String clientAddress;

    @Builder.Default
    private Instant createdAt = Instant.now();
}
