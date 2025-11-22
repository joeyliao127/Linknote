package com.penguin.linknote.domain.auth.filter;

import java.io.IOException;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.penguin.linknote.domain.auth.AuthFacade;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final AuthFacade authFacade;

    // ❶ 不需要 JWT 驗證的路徑（公開 API）
    private static final Set<String> EXCLUDED_PATHS = Set.of(
        "/api/users/signIn",
        "/api/users/signUp",
        "/api/users/token"
    );

    public JWTAuthenticationFilter(AuthFacade authFacade) {
        this.authFacade = authFacade;
    }

    @Override
    protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // ❷ OPTIONS 請求必須放行（處理 CORS Preflight）
        if ("OPTIONS".equalsIgnoreCase(method)) {
            chain.doFilter(request, response);
            return;
        }

        // ❸ 若在白名單，跳過 JWT 驗證
        if (isExcludedPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // ❹ 正式處理 JWT
        String token = extractToken(request);

        if (token != null && authFacade.verify(token)) {
            Authentication authentication = authFacade.createAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    // 判斷是否為免驗證路徑
    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.contains(path);
    }

    // 安全地提取 token，不會 NPE，不會 substring 錯誤
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }

        return header.substring(7);
    }
}