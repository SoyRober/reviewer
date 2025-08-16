package com.reviewer.mapper;

import com.reviewer.dto.response.ReviewSummaryResponse;
import com.reviewer.entity.ReviewSummary;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewSummaryMapper {
    ReviewSummaryResponse toReviewSummaryResponse(ReviewSummary summary);
}
