package com.reviewer.entity;

import com.reviewer.model.EvaluationSummary;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSummary {

    @Id
    private UUID id;

    @Indexed(unique = true)
    private UUID projectId;

    @LastModifiedDate
    private Instant updatedAt;

    private EvaluationSummary evaluationSummary;

    private Long totalReviews;

    private Float average;
}
