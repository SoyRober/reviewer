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

    @DeleteMapping("/project/{projectId}/client/{clientAddress}")
    public ResponseEntity<Void> deleteReviewFromProjectAndClient(@PathVariable UUID projectId,
                                                                  @PathVariable String clientAddress) {
        reviewService.deleteFromProjectAndClient(projectId, clientAddress);
        return ResponseEntity.ok().build();
    }

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

    @PutMapping("/activate/{reviewId}")
    public ResponseEntity<ReviewResponse> activateReview(@PathVariable UUID reviewId) {
        return ResponseEntity.ok(reviewService.activateAndDeactivate(reviewId));
    }
}
