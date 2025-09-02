package com.reviewer.controller;


import com.reviewer.dto.request.PaginationRequest;
import com.reviewer.dto.request.ReviewReportRequest;
import com.reviewer.dto.response.ReviewReportResponse;
import com.reviewer.service.ReviewReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews/report")
public class ReportReviewController {
    private final ReviewReportService reviewReportService;

    @PostMapping
    public ResponseEntity<ReviewReportResponse> createReport(@RequestBody ReviewReportRequest request) {
        return ResponseEntity.ok(reviewReportService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ReviewReportResponse>> getAllReports(@ModelAttribute PaginationRequest request) {
        return ResponseEntity.ok(reviewReportService.getAllFiltered(request));
    }
}
