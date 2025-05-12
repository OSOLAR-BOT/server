package com.osolar.obot.domain.user.controller;

import com.osolar.obot.common.apiPayload.success.SuccessApiResponse;
import com.osolar.obot.domain.user.dto.request.LoginRequest;
import com.osolar.obot.domain.user.dto.request.RegisterRequest;
import com.osolar.obot.domain.user.dto.response.LoginResponse;
import com.osolar.obot.domain.user.service.UserApplicationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {
    private final UserApplicationService userApplicationService;
    // 회원가입
    @PostMapping("/register")
    public SuccessApiResponse<Void> register(
            @RequestBody RegisterRequest request)
    {
        log.info("[AuthController - register] request = {}", request);

        userApplicationService.register(request);

        return SuccessApiResponse.Register();
    }

    // 로그인
    @PostMapping("/login")
    public SuccessApiResponse<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse httpServletResponse)
    {
        log.info("[AuthController - login] request = {}", request);

        return SuccessApiResponse.Login(userApplicationService.login(request, httpServletResponse));
    }

    // 토큰 재발급
    @PostMapping("/reissue")
    public SuccessApiResponse<Void> reissueToken(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse)
    {
        log.info("[AuthController - reissueToken]");

        userApplicationService.reissueToken(httpServletRequest, httpServletResponse);

        return SuccessApiResponse.ReissueToken();
    }
}
