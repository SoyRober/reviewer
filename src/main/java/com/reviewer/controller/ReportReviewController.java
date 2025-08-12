package com.reviewer.controller;


import com.reviewer.dto.request.ReviewReportRequest;
import com.reviewer.dto.response.ReviewReportResponse;
import com.reviewer.service.ReviewReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews/report")
public class ReportReviewController {
    private final ReviewReportService reviewReportService;

    @PostMapping
    public ResponseEntity<ReviewReportResponse> createReport(ReviewReportRequest request) {
        return ResponseEntity.ok(reviewReportService.create(request));
    }
}
