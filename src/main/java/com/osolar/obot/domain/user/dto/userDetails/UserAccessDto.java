package com.osolar.obot.domain.user.dto.userDetails;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserAccessDto {
    private String userId;
    private String username;
    private String role;

    @Builder
    public UserAccessDto(String userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }
}

