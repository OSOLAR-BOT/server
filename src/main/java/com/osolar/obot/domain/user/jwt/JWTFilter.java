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
        String access = resolveToken(request, "Bearer");

        // 토큰 검증
        if (isValidToken(access)) {
            // 토큰 검증 후 사용자 인증 객체 생성
            authenticateUser(request, access);
        }

        // 일반 요청 처리
        filterChain.doFilter(request, response);
    }

//    private String resolveToken(HttpServletRequest request, String prefix) {
//        String access = request.getHeader("Authorization");
//
//        if (access == null || !access.startsWith(prefix)) {
//            return request.getParameter("access"); // SSE 요청에서 사용
//        }
//
//        System.out.println("access: " + access);
//        return access.substring(prefix.length()).trim();
//    }
    private String resolveToken(HttpServletRequest request, String prefix) {
        String access = request.getHeader("Authorization");
        log.info("Authorization Header: {}", access);

        if (access == null || !access.startsWith(prefix)) {
            String tokenFromParam = request.getParameter("access");
            log.info("Access Token from param: {}", tokenFromParam);
            return tokenFromParam;
        }

        String token = access.substring(prefix.length()).trim();
        log.info("Resolved Token: {}", token);
        return token;
    }


    private boolean isValidToken(String access) {
        System.out.println("isValidToken : " + access );
        // access 토큰이 존재하지 않는 경우 검증 skip 후 false 반환
        // access 토큰이 존재하는 경우 검증 결과 반환
        return access != null && validateToken(access);
    }

    private boolean validateToken(String token) {

        // validation1 - token expire
        if (jwtUtil.isExpired(token)) {
            log.error("Token expired");
            throw new JWTException.TokenExpiredException();
        }

        // validation2 - token type for access
        String tokenType = jwtUtil.getCategory(token);
        if (!tokenType.equals("access")) {
            log.error("Invalid token type: {}", tokenType);
            throw new JWTException.TokenTypeNotAccessException();
        }

        return true;
    }

    private void authenticateUser(HttpServletRequest request, String access) {
        // 사용자 정보 추출
        String username = jwtUtil.getUsername(access);
        String role = jwtUtil.getRole(access);
        String userId = jwtUtil.getUserId(access);

        UserAccessDto userAccessDto = UserAccessDto.builder()
                .userId(userId)
                .username(username)
                .role(role)
                .build();

        // 인증 객체 생성
        RoleUserDetails roleUserDetails = new RoleUserDetails(userAccessDto);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                roleUserDetails, null, roleUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // ADMIN 전용 추가 검증
        if (role.equals("ROLE_ADMIN")) {
            validateAdminPermissions(request);
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

    private static boolean isSSERequest(String requestURI) {
        return requestURI.equals("/api/chat/stream");
    }
}