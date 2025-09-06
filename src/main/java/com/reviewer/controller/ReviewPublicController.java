package com.reviewer.controller;

import com.reviewer.dto.request.PaginationRequest;
import com.reviewer.dto.request.ReviewRequest;
import com.reviewer.dto.response.PaginationResponse;
import com.reviewer.dto.response.ReviewResponse;
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
@RequestMapping("/api/public/reviews")
public class ReviewPublicController {
    private final ReviewService reviewService;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<PaginationResponse<ReviewResponse>> paginateReviewsFromProject(@PathVariable UUID projectId,
                                                                                         @ModelAttribute @Valid PaginationRequest request) {
        return ResponseEntity.ok(reviewService.getFromProject(projectId, request, true));
    }

    @GetMapping("/client/{clientAddress}")
    public ResponseEntity<PaginationResponse<ReviewResponse>> paginateReviewsFromClient(@PathVariable String clientAddress,
                                                                                        @ModelAttribute @Valid PaginationRequest request) {
        return ResponseEntity.ok(reviewService.getFromClient(clientAddress, request, true));
    }

    @GetMapping("/project/{projectId}/client/{clientAddress}")
    public ResponseEntity<ReviewResponse> getReviewFromProjectAndClient(@PathVariable UUID projectId,
                                                                        @PathVariable String clientAddress) {
        return ResponseEntity.ok(reviewService.getFromProjectAndClient(projectId, clientAddress));
    }
}
