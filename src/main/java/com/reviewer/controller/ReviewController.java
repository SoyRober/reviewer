package com.reviewer.controller;

import com.reviewer.dto.request.ReviewRequest;
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
    public ResponseEntity<ReviewResponse> create(@RequestBody @Valid ReviewRequest request) {
        return ResponseEntity.ok(reviewService.create(request));
    }
}
