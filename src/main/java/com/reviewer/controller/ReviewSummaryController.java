package com.reviewer.controller;

import com.reviewer.dto.response.ReviewSummaryResponse;
import com.reviewer.service.ReviewSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/review-summary")
public class ReviewSummaryController {
    private final ReviewSummaryService reviewSummaryService;

    @GetMapping("/project/evaluation/{projectId}")
    public ResponseEntity<ReviewSummaryResponse> getProjectEvaluation(@PathVariable UUID projectId) {
        return ResponseEntity.ok(reviewSummaryService.getEvaluation(projectId));
    }
}
