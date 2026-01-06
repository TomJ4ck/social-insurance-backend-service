package jp.asatex.matianchi.social_insurance_backend_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * CORS（跨域资源共享）配置类
 * 配置允许所有源访问的跨域策略
 */
@Configuration
public class CorsConfig {

    /**
     * 配置CORS过滤器
     * 允许所有源、所有方法和所有请求头
     * 
     * @return CorsWebFilter CORS过滤器
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        
        // 允许所有源访问
        corsConfiguration.addAllowedOriginPattern("*");
        
        // 允许所有HTTP方法
        corsConfiguration.addAllowedMethod("*");
        
        // 允许所有请求头
        corsConfiguration.addAllowedHeader("*");
        
        // 允许发送凭证（cookies、authorization headers等）
        corsConfiguration.setAllowCredentials(true);
        
        // 预检请求的缓存时间（秒）
        corsConfiguration.setMaxAge(3600L);
        
        // 配置CORS源
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        
        return new CorsWebFilter(source);
    }
}

