package com.semi.config;

import com.semi.security.JwtFilter;
import com.semi.security.JwtProvider;
import com.semi.security.MemberDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties.class)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final MemberDetailsService memberDetailsService;

    @Value("${cors.allowed-origins:*}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 인증 필요 없는 엔드포인트
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/login.html", "/register.html", "/main.html", "/index.html").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/").permitAll()
                
                // ✅ 추가: favicon, robots.txt 등 정적 리소스는 누구나 접근 가능
                .requestMatchers("/favicon.ico", "/robots.txt").permitAll()

                // ✅ 수정: /admin 페이지는 ADMIN 역할 필요 (더 엄격한 설정)
                .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
                
                // API ADMIN endpoints require ADMIN role
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // 나머지는 인증 필수
                .anyRequest().authenticated()
            )
            // 401 응답을 일관된 JSON 형식으로 반환
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    if (request.getRequestURI().startsWith("/api")) {
                        response.setStatus(401);
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                        response.getWriter().write("{\"message\":\"인증이 필요합니다.\"}");
                    } else {
                        response.sendRedirect("/login.html");
                    }
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    if (request.getRequestURI().startsWith("/api")) {
                        response.setStatus(403);
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                        response.getWriter().write("{\"message\":\"접근 권한이 없습니다.\"}");
                    } else {
                        response.sendRedirect("/login.html");
                    }
                })
            )
            .addFilterBefore(new JwtFilter(jwtProvider, memberDetailsService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 허용 출처: CORS_ALLOWED_ORIGINS 환경변수로 제어 (기본값 * → 배포 시 도메인 지정 필수)
        config.setAllowedOriginPatterns(List.of(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
