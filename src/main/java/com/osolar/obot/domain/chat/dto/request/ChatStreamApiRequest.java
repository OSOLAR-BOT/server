package com.osolar.obot.domain.chat.dto.request;

import lombok.*;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatStreamApiRequest {
    private String action;
    private String question;
    private UserData user;
    private List<String> history;


    @Builder
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UserData{
        private Integer solarspaceId;
        private String businessNumber;
        private String firmName;
        private String representativePhone;
        private String plantName;
        private Boolean facilityConfirm;
        private String facilityCode;
        private String facilityCapa;
        private String facilityWeight;
        private String permitNumber;
        private String permitCapa;
        private String ppaContractTarget;
        private String ppaContractNumber;
        private String taxRegistrationId;
        private String ppaBranchOffice;
        private String ppaContactPhone;
        private String recTradingType;
        private String recContractTarget;
        private String commercialDate;
        private String recStartDate;
        private String subscription;
        private String certificateExpiresAt;
        private String representativeName;
        private String contactName;
        private String contactPhone;
        private String contactEmail;
    }
}
