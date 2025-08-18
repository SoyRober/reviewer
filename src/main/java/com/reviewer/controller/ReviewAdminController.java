package com.reviewer.controller;

import com.reviewer.dto.request.PaginationRequest;
import com.reviewer.dto.request.ReviewRequest;
import com.reviewer.dto.response.PaginationResponse;
import com.reviewer.dto.response.ReviewResponse;
import com.reviewer.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews/admin")
public class ReviewAdminController {
    private final ReviewService reviewService;

    @GetMapping("/project/{project}")
    public ResponseEntity<PaginationResponse<ReviewResponse>> paginateReviewsFromProjectAdmin(@PathVariable UUID project,
                                                                                              @ModelAttribute @Valid PaginationRequest request,
                                                                                              @RequestParam(defaultValue = "true") Boolean isActive) {
        return ResponseEntity.ok(reviewService.getFromProject(project, request, isActive));
    }

    @GetMapping("/client/{client}")
    public ResponseEntity<PaginationResponse<ReviewResponse>> paginateReviewsFromClient(@PathVariable String client,
                                                                                        @ModelAttribute @Valid PaginationRequest request,
                                                                                        @RequestParam(defaultValue = "true") Boolean isActive) {
        return ResponseEntity.ok(reviewService.getFromClient(client, request, isActive));
    }
}
