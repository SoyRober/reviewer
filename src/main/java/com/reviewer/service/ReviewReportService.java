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
        ReviewReport report = reviewReportRepo.findByClientAddressAndReviewId(request.getClientAddress(), request.getReviewId())
                .orElseGet(ReviewReport::new);

        if (report.getId() == null) {
            report = ReviewReport.builder()
                    .id(UUID.randomUUID())
                    .reviewId(request.getReviewId())
                    .clientAddress(request.getClientAddress())
                    .createdAt(Instant.now())
                    .build();
            reviewReportRepo.save(report);
        }

        return reviewReportMapper.toReviewReportResponse(report);
    }

    public List<ReviewReportResponse> getAllFiltered(ReviewReportRequest request) {
        List<ReviewReport> list = List.of();

        if (request.getReviewId() != null && request.getClientAddress() != null) {
            list = reviewReportRepo.findByClientAddressAndReviewId(request.getClientAddress(), request.getReviewId())
                    .map(List::of)
                    .orElse(List.of());
        } else if (request.getReviewId() != null) {
            list = reviewReportRepo.findAllByReviewId(request.getReviewId());
        } else if (request.getClientAddress() != null) {
            list = reviewReportRepo.findAllByClientAddress(request.getClientAddress());
        }

        return list.stream()
                .map(reviewReportMapper::toReviewReportResponse)
                .toList();
    }
}
