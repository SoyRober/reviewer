package com.reviewer.mapper;

import com.reviewer.dto.response.ReviewReportResponse;
import com.reviewer.entity.ReviewReport;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewReportMapper {
    ReviewReportResponse toReviewReportResponse(ReviewReport reviewReport);
}
