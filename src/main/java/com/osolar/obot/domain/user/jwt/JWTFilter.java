package com.osolar.obot.domain.user.jwt;

import com.osolar.obot.common.apiPayload.failure.customException.JWTException;
import com.osolar.obot.domain.user.dto.userDetails.RoleUserDetails;
import com.osolar.obot.domain.user.dto.userDetails.UserAccessDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private static final List<String> whiteList = Arrays.asList(
            "/h2", "/favicon", "/api/health", "/api/register", "/api/login", "/api/reissue");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // 화이트리스트 처리
        if (whiteList.stream().anyMatch(requestURI::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 추출 (헤더 또는 SSE 경로인 경우 쿼리 파라미터)
        String access = request.getHeader("access");
        if (access == null && requestURI.equals("/api/chat/stream")) {
            access = request.getParameter("access");
            System.out.println("인증했다고 시발아");
        }

        // 토큰 검증
        validateToken(access);

        // 사용자 정보 추출
        String username = jwtUtil.getUsername(access);
        String role = jwtUtil.getRole(access);

        UserAccessDto userAccessDto = UserAccessDto.builder()
                .username(username)
                .role(role)
                .build();

        // 인증 객체 생성 (USER, ADMIN 공통 처리)
        RoleUserDetails roleUserDetails = new RoleUserDetails(userAccessDto);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                roleUserDetails, null, roleUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // ADMIN 전용 추가 검증
        if (role.equals("ROLE_ADMIN")) {
            validateAdminPermissions(request);
        }

        // SSE 요청인 경우 여기서 필터 체인 종료 (후속 보안 검증 방지)
        if (requestURI.equals("/api/chat/stream")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 일반 요청 처리
        filterChain.doFilter(request, response);
    }

    private void validateToken(String token) {
        // validation1 - token null
        if (token == null) {
            log.error("Token is null");
            throw new JWTException.TokenNullException();
        }

        // validation2 - token expire
        if (jwtUtil.isExpired(token)) {
            log.error("Token expired");
            throw new JWTException.TokenExpiredException();
        }

        // validation3 - token type for access
        String tokenType = jwtUtil.getCategory(token);
        if (!tokenType.equals("access")) {
            log.error("Invalid token type: {}", tokenType);
            throw new JWTException.TokenTypeNotAccessException();
        }
    }

    // ADMIN 전용 검증 메서드 (추가 확장 가능)
    private void validateAdminPermissions(HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        // ADMIN만 접근 가능한 경로 예시
        if (requestURI.startsWith("/api/admin")) {
            // 추가 권한 검증 로직
            log.info("Admin accessing protected resource: {}", requestURI);
        }

        // 추후 ADMIN 관련 추가 검증 로직 구현 가능
    }
}