package com.osolar.obot.domain.inquiry.controller;

import com.osolar.obot.common.apiPayload.success.SuccessApiResponse;
import com.osolar.obot.domain.inquiry.dto.request.InquiryRequest;
import com.osolar.obot.domain.inquiry.service.InquiryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "사용자 설문 API", description = "세션 종료 후 사용자 만족도 조사 API")
public class InquiryController {

    private final InquiryService inquiryService;

    @Operation(summary = "[만족도 조사] 만족 여부 및 만족 이유 저장 API")
    @PostMapping("/inquiry")
    public SuccessApiResponse<Void> getInquiry(
        @RequestParam String sessionId,
        @RequestBody InquiryRequest inquiryRequest
    ) {
        inquiryService.getInquiry(inquiryRequest, sessionId);
        return SuccessApiResponse.GetInquiry();
    }

}
