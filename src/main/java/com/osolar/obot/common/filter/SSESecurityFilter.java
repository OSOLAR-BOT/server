package com.osolar.obot.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osolar.obot.common.util.JWTUtil;
import com.osolar.obot.domain.user.dto.userDetails.UserAccessDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class SSESecurityFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // SSE 요청인지 확인
        if (isSSERequest(request)) {
            log.info("[SSESecurityFilter] SSE 요청 감지: {}", requestURI);
            
            try {
                // 1. 토큰 추출 및 검증
                String access = resolveToken(request);
                if (!isValidToken(access)) {
                    sendSSEError(response, "Unauthorized", "인증이 필요합니다");
                    return;
                }

                // 2. 사용자 정보 추출 및 인증 객체 생성
                String username = jwtUtil.getUsername(access);
                String role = jwtUtil.getRole(access);
                String userId = jwtUtil.getUserId(access);

                UserAccessDto userAccessDto = UserAccessDto.builder()
                        .userId(userId)
                        .username(username)
                        .role(role)
                        .build();

                // 3. 권한 검사
                if (!hasRequiredRole(role, requestURI)) {
                    sendSSEError(response, "Forbidden", "접근 권한이 없습니다");
                    return;
                }

                // 4. 인증 객체 설정
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userAccessDto, null, Collections.singletonList(new SimpleGrantedAuthority(role)));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 5. 모든 검증 통과 시 다음 필터로 진행
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                log.error("[SSESecurityFilter] 에러 발생: {}", e.getMessage());
                sendSSEError(response, "InternalServerError", "서버 내부 오류가 발생했습니다");
            }
            return;
        }

        // 일반 요청 처리
        filterChain.doFilter(request, response);
    }

    private boolean isSSERequest(HttpServletRequest request) {
        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
        return acceptHeader != null && acceptHeader.contains("text/event-stream");
    }

    private String resolveToken(HttpServletRequest request) {
        String access = request.getHeader("Authorization");
        if (access == null || !access.startsWith("Bearer ")) {
            return null;
        }
        return access.substring(7);
    }

    private boolean isValidToken(String token) {
        return token != null && jwtUtil.isValidAccessToken(token);
    }

    private boolean hasRequiredRole(String role, String requestURI) {
        // ADMIN 권한이 필요한 경로 체크
        if (requestURI.startsWith("/api/admin/")) {
            return "ROLE_ADMIN".equals(role);
        }
        // 일반 인증된 사용자 접근 가능한 경로
        return true;
    }

    private void sendSSEError(HttpServletResponse response, String error, String message) throws IOException {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        
        Map<String, String> errorResponse = Map.of(
            "error", error,
            "message", message
        );
        
        String errorJson = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write("data: " + errorJson + "\n\n");
    }
} 