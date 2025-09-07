package com.reviewer.entity;

import com.reviewer.model.Evaluation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@CompoundIndexes({
        @CompoundIndex(name = "client_project_unique_idx", def = "{'clientAddress': 1, 'projectId': 1}", unique = true)
})
public class Review {
    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    private String clientAddress;

    private UUID projectId;

    private String comment;

    @Builder.Default
    private Instant createdAt = Instant.now();

    @LastModifiedDate
    private Instant updatedAt;

    private Evaluation evaluation;

    @Builder.Default
    private Boolean isActive = true;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal average;

    public static List<String> getValidSortFields() {
        return List.of(
                Fields.clientAddress,
                Fields.projectId,
                Fields.createdAt,
                Fields.average
        );
    }

    public static String getDefaultSortField() {
        return Review.Fields.createdAt;
    }
}
