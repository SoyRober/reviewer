package com.reviewer.service;

import com.reviewer.dto.request.ReviewReportRequest;
import com.reviewer.dto.response.ReviewReportResponse;
import com.reviewer.entity.ReviewReport;
import com.reviewer.repository.ReviewReportRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewReportService {
    private final ReviewReportRepo reviewReportRepo;


    public ReviewReportResponse create(ReviewReportRequest request) {
        ReviewReport report = reviewReportRepo.findByClientIdAndReviewId(request.getClientId(), request.getReviewId())
                .orElseGet(() -> new ReviewReport(request.getClientId(), request.getReviewId()));

        return null;
    }
}
