package com.reviewer.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationRequest {

    @Min(0)
    private int page;

    @Min(1)
    @Max(50)
    private int size;

    private String sortBy = "createdAt";

    private boolean direction = false; // false for ASC, true for DESC

    private String searchTerm = null;
}
