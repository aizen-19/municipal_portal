package com.municipal.portal.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // Allows any local angular port (e.g. 4200, 50012)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                // Allow CORS preflight requests
                if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                    return true;
                }

                String path = request.getRequestURI();
                
                // Allow public auth, chat endpoints, and database console
                if (path.equals("/api/auth/signup") || 
                    path.equals("/api/auth/login") || 
                    path.equals("/api/ai/chat") || 
                    path.startsWith("/h2-console")) {
                    return true;
                }

                // Verify protected API endpoints
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
                    response.getWriter().write("{\"message\":\"Authentication token required or invalid.\"}");
                    return false;
                }

                return true;
            }
        }).addPathPatterns("/**");
    }
}
