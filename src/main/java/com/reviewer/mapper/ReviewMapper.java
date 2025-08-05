package com.reviewer.mapper;

import com.reviewer.dto.request.ReviewRequest;
import com.reviewer.dto.response.ReviewResponse;
import com.reviewer.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isBanned", ignore = true)
    void updateReviewFromReviewRequest(ReviewRequest request, @MappingTarget Review review);

    ReviewResponse toReviewResponse(Review review);
}
