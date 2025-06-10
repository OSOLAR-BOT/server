package com.osolar.obot.common.annotation.resolver;

import com.osolar.obot.common.annotation.CurrentUser;
import com.osolar.obot.domain.user.dto.userDetails.RoleUserDetails;
import com.osolar.obot.domain.user.entity.User;
import com.osolar.obot.domain.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticatedMemberResolver implements HandlerMethodArgumentResolver {

    private final UserQueryService userQueryService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && User.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("auth is null" );
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof RoleUserDetails roleDetails) {
            String userId = roleDetails.getUserAccessDto().getUserId();
            return userQueryService.getUserById(userId);
        }

        return null;
    }
}