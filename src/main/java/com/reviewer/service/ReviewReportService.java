package com.reviewer.service;

import com.reviewer.dto.request.ReviewReportRequest;
import com.reviewer.dto.response.ReviewReportResponse;
import com.reviewer.entity.ReviewReport;
import com.reviewer.mapper.ReviewReportMapper;
import com.reviewer.repository.ReviewReportRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewReportService {
    private final ReviewReportRepo reviewReportRepo;
    private final ReviewReportMapper reviewReportMapper;

    public ReviewReportResponse create(ReviewReportRequest request) {
        ReviewReport report = reviewReportRepo.findByClientIdAndReviewId(request, request.getReviewId())
                .orElseGet(ReviewReport::new);

        if (report.getId() == null) {
            report = ReviewReport.builder()
                    .id(UUID.randomUUID())
                    .reviewId(request.getReviewId())
                    .clientId(request.getClientId())
                    .createdAt(Instant.now())
                    .build();
            reviewReportRepo.save(report);
        }

        return reviewReportMapper.toReviewReportResponseList(report);
    }

    public List<ReviewReportResponse> getAllFiltered(ReviewReportRequest request) {
        List<ReviewReport> list;
        if(request.getReviewId() != null && request.getClientId() != null) {
            return reviewReportRepo.findByClientIdAndReviewId(request, request.getReviewId())
                    .stream()
                    .map(reviewReportMapper::toReviewReportResponseList)
                    .toList();
        } else if (request.getReviewId() != null) {
            return reviewReportRepo.findAllByReviewId(request.getReviewId())
                    .stream()
                    .map(reviewReportMapper::toReviewReportResponseList)
                    .toList();
        } else if (request.getClientId() != null) {
            list = reviewReportRepo.findAllByClientId(request.getClientId());
            return reviewReportMapper.toReviewReportResponseList(list);
        }

        return List.of(new ReviewReportResponse());
    }
}
