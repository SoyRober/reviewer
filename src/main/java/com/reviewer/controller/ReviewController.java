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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> create(@RequestBody @Valid ReviewRequest request) throws IllegalAccessException {
        return ResponseEntity.ok(reviewService.create(request));
    }

    @GetMapping("/project/{project}")
    public ResponseEntity<PaginationResponse<ReviewResponse>> paginateReviewsFromProject(@PathVariable String project,
                                                                                         @ModelAttribute @Valid PaginationRequest request) {
        return ResponseEntity.ok(reviewService.getFromProject(project, request, true));
    }

//    @GetMapping("/client/{address}")
//    public ResponseEntity<List<ReviewResponse>> paginateReviewsFromProject(@PathVariable String project) {
//
//    }
}
