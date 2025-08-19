package com.reviewer.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class FilterUtil {
    public Sort getDirectionAndField(boolean directionBool, String sortBy, List<String> validSortByFields, String defaultSortByField) {
        Sort.Direction sortDirection = Sort.Direction.ASC;
        String direction = directionBool ? "desc" : "asc";

        try {
            sortDirection = Sort.Direction.fromString(direction);
            log.error("Sort direction provided: {}", direction);
        } catch (IllegalArgumentException e) {
            log.error("Invalid sort direction provided: {}. Defaulting to ASC.", direction);
        }

        String actualSortByField = validSortByFields.contains(sortBy.trim()) ?
                sortBy.trim() :
                defaultSortByField;

        return Sort.by(sortDirection, actualSortByField);
    }
}
