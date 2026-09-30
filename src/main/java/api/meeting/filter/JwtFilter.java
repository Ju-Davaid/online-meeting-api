package api.meeting.filter;

import api.meeting.config.SecurityConfig;
import api.meeting.entity.enums.ResponseCode;
import api.meeting.entity.enums.UserRole;
import api.meeting.entity.enums.UserStatus;
import api.meeting.entity.po.User;
import api.meeting.service.UserService;
import api.meeting.utils.JwtTokenUtils;
import api.meeting.utils.ResponseUtils;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * JWT 过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserService userService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws IOException {
        String token = null;
        try {
            token = jwtTokenUtils.extractTokenFromRequest(request);
            if (StringUtils.hasText(token)) {
                JwtTokenUtils.Payload payload = jwtTokenUtils.parseAndValidate(token);
                log.info("JWT 过滤器解析JWT:{}", JSONUtil.toJsonStr(payload));
                if (payload != null) {

                    User user = userService.getById(payload.getUserId());
                    // 判断用户是否存在
                    if (user == null) {
                        clearContext(response);
                        return;
                    }
                    // 判断用户状态是否正常
                    if (!UserStatus.NORMAL.getCode().equals(user.getStatus())) {
                        clearContext(response);
                        return;
                    }
                    user.setToken(token);
                    UserRole role = UserRole.getRoleByCode(user.getRole());
                    UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(role, user);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    clearContext(response);
                    return;
                }
            } else {
                clearContext(response);
                return;
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("JWT 过滤器异常:{}", token, e);
            clearContext(response);
        }
    }

    /**
     * 获取认证令牌
     *
     * @param role 角色
     * @param user 用户
     * @return 认证令牌
     */
    private UsernamePasswordAuthenticationToken getAuthenticationToken(UserRole role, User user) {
        List<SimpleGrantedAuthority> grantedAuthorities = Collections.emptyList();
        if (role != null) {
            grantedAuthorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        }
        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                grantedAuthorities
        );
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return Arrays.stream(SecurityConfig.PUBLIC_PATH).anyMatch(path -> request.getRequestURI().startsWith(path));
    }

    /**
     * 清除上下文
     *
     * @param response 响应
     */
    private void clearContext(HttpServletResponse response) throws IOException {
        SecurityContextHolder.clearContext();
        ResponseUtils.writeErrorResponse(response, ResponseCode.FORBIDDEN);
    }
}
