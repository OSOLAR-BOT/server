package com.osolar.obot.domain.inquiry.service;

import com.osolar.obot.domain.inquiry.dto.request.InquiryRequest;
import com.osolar.obot.domain.inquiry.entity.Inquiry;
import com.osolar.obot.domain.inquiry.repository.InquiryRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    public void getInquiry(InquiryRequest inquiryRequest, String sessionId) {
        inquiryRepository.save(Inquiry.builder()
            .isSatisfied(inquiryRequest.getIsSatisfied())
            .satisfactionReason(inquiryRequest.getSatisfactionReason())
            .createdAt(LocalDateTime.now())
            .build());
    }

}
