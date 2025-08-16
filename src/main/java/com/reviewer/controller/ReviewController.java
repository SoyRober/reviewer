package com.reviewer.controller;

import com.reviewer.dto.request.PaginationRequest;
import com.reviewer.dto.request.ReviewRequest;
import com.reviewer.dto.response.PaginationResponse;
import com.reviewer.dto.response.ReviewResponse;
import com.reviewer.dto.response.ReviewSummaryResponse;
import com.reviewer.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/{projectId}")
    public ResponseEntity<ReviewResponse> create(@RequestBody @Valid ReviewRequest request, @PathVariable UUID projectId) throws IllegalAccessException {
        return ResponseEntity.ok(reviewService.create(request, projectId));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<PaginationResponse<ReviewResponse>> paginateReviewsFromProject(@PathVariable UUID projectId,
                                                                                         @ModelAttribute @Valid PaginationRequest request) {
        return ResponseEntity.ok(reviewService.getFromProject(projectId, request, true));
    }

    @GetMapping("/client/{client}")
    public ResponseEntity<PaginationResponse<ReviewResponse>> paginateReviewsFromClient(@PathVariable String client,
                                                                                         @ModelAttribute @Valid PaginationRequest request) {
        return ResponseEntity.ok(reviewService.getFromClient(client, request, true));
    }
}
