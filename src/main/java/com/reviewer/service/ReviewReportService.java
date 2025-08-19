package com.reviewer.service;

import com.reviewer.dto.request.PaginationRequest;
import com.reviewer.dto.request.ReviewReportRequest;
import com.reviewer.dto.response.ReviewReportResponse;
import com.reviewer.entity.ReviewReport;
import com.reviewer.mapper.ReviewReportMapper;
import com.reviewer.repository.ReviewReportRepo;
import com.reviewer.util.FilterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewReportService {
    private final ReviewReportRepo reviewReportRepo;
    private final ReviewReportMapper reviewReportMapper;
    private final FilterUtil filterUtil;

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

    public List<ReviewReportResponse> getAllFiltered(PaginationRequest request) {
        List<String> validSortFields = List.of("clientAddress", "reviewId", "createdAt");
        String defaultSortField = "createdAt";
        Sort sort = filterUtil.getDirectionAndField(request.isDirection(), request.getSortBy(), validSortFields, defaultSortField);
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        List<ReviewReport> list = reviewReportRepo.findAll(pageable).getContent();

        return list.stream()
                .map(reviewReportMapper::toReviewReportResponse)
                .toList();
    }
}
