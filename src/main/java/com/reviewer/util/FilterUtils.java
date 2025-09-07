package com.reviewer.util;

import com.reviewer.dto.request.PaginationRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class FilterUtils {
    public static Sort getDirectionAndField(boolean directionBool, String sortBy, List<String> validSortByFields, String defaultSortByField) {
        Sort.Direction sortDirection = directionBool ? Sort.Direction.DESC : Sort.Direction.ASC;

        String actualSortByField = validSortByFields.contains(sortBy.trim()) ?
                sortBy.trim() :
                defaultSortByField;

        return Sort.by(sortDirection, actualSortByField);
    }

    public static Pageable buildPageable(PaginationRequest pagination, List<String> validSortFields, String defaultSortField) {
        Sort sort = FilterUtils.getDirectionAndField(pagination.isDirection(), pagination.getSortBy(), validSortFields, defaultSortField);
        return PageRequest.of(pagination.getPage(), pagination.getSize(), sort);
    }
}
