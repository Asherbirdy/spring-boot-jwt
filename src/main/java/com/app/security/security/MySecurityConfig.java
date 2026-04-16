package com.app.security.security;

import com.app.security.dao.MemberDao;
import com.app.security.dao.TokenDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
public class MySecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenDao tokenDao;

    @Autowired
    private MemberDao memberDao;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                // JWT 為 stateless，不需要 Session
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // JWT 驗證不需要 CSRF
                .csrf(csrf -> csrf.disable())

                // 設定 CORS 跨域
                .cors(cors -> cors
                        .configurationSource(createCorsConfig())
                )

                // 添加 JWT Filter
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtUtil, tokenDao, memberDao),
                        UsernamePasswordAuthenticationFilter.class
                )

                // 設定 api 的權限控制 (role 欄位值: "user", "vip", "movie_manager", "admin")
                .authorizeHttpRequests(request -> request
                        // 註冊和登入不需要認證
                        .requestMatchers("/auth/**").permitAll()

                        // 登出和查詢目前使用者需要認證
                        .requestMatchers("/logout", "/me", "/member/**").authenticated()

                        .anyRequest().denyAll()
                )

                .build();
    }

    private CorsConfigurationSource createCorsConfig() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://example.com"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
