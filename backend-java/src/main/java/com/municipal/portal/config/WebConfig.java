package com.municipal.portal.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtUtil jwtUtil;

    public WebConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // Allows any local origin/port
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new HandlerInterceptor() {

            @Override
            public boolean preHandle(HttpServletRequest request,
                                     HttpServletResponse response,
                                     Object handler) throws Exception {

                // 1. Return HTTP 200 OK directly for CORS preflight OPTIONS requests
                if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    return true;
                }

                String path = request.getRequestURI();

                // 2. Public endpoints
                if (path.equals("/api/auth/signup")
                        || path.equals("/api/auth/login")
                        || path.equals("/api/ai/chat")
                        || path.startsWith("/h2-console")) {
                    return true;
                }

                // 3. Protected endpoints
                if (path.startsWith("/api/")) {

                    String authHeader = request.getHeader("Authorization");

                    if (authHeader != null && authHeader.startsWith("Bearer ")) {

                        String token = authHeader.substring(7);

                        if (jwtUtil.validateToken(token)) {

                            Claims claims = jwtUtil.extractAllClaims(token);

                            request.setAttribute("userId", claims.get("id", String.class));
                            request.setAttribute("userName", claims.get("name", String.class));
                            request.setAttribute("userEmail", claims.get("email", String.class));

                            return true;
                        }
                    }

                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write(
                            "{\"message\":\"Authentication token required or invalid.\"}"
                    );

                    return false;
                }

                return true;
            }
        }).addPathPatterns("/**");
    }
}