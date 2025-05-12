package com.osolar.obot.domain.user.service;

import com.osolar.obot.domain.user.dto.request.LoginRequest;
import com.osolar.obot.domain.user.dto.request.RegisterRequest;
import com.osolar.obot.domain.user.dto.response.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserApplicationService {
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    public void register(RegisterRequest request){
        userCommandService.register(request);
    }

    public LoginResponse login(LoginRequest request, HttpServletResponse httpServletResponse){
        String username = userQueryService.checkUserInfo(request);
        return userCommandService.issueToken(username, httpServletResponse);
    }

    public void reissueToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        userCommandService.reissueToken(httpServletRequest, httpServletResponse);
    }
}
