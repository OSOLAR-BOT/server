package com.osolar.obot.domain.user.dto.userDetails;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserAccessDto {
    private String username;
    private String role;

    @Builder
    public UserAccessDto(String username, String role) {
        this.username = username;
        this.role = role;
    }
}

