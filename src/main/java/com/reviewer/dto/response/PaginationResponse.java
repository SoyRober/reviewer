package com.reviewer.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class PaginationResponse<T> {
    @Singular("content")
    List<T> content;

    long totalElements;
}
