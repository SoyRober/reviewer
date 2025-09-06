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
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/project/{project}")
    public ResponseEntity<PaginationResponse<ReviewResponse>> paginateReviewsFromProject(@PathVariable UUID project,
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

    @PutMapping("/activate/{Id}")
    public ResponseEntity<ReviewResponse> activateReview(@PathVariable UUID id) {
        return ResponseEntity.ok(reviewService.activateAndDeactivate(id));
    }

    @PostMapping("/{projectId}")
    public ResponseEntity<ReviewResponse> create(@RequestBody @Valid ReviewRequest request, @PathVariable UUID projectId) throws IllegalAccessException {
        return ResponseEntity.ok(reviewService.create(request, projectId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReviewFromProjectAndClient(@PathVariable UUID id) {
        reviewService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
