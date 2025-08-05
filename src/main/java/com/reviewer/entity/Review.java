package com.reviewer.entity;

import com.reviewer.model.Evaluation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndexes({
        @CompoundIndex(name = "client_project_unique_idx", def = "{'clientAddress': 1, 'projectAddress': 1}", unique = true)
})
public class Review {
    @Id
    private UUID id;

    @Indexed(unique = true)
    private String clientAddress;

    @Indexed(unique = true)
    private String projectAddress;

    private String comment;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    private Evaluation evaluation;

    private Boolean isBanned = false;
}
