package com.dre4m1nd.cipherlens.config;

import com.dre4m1nd.cipherlens.interceptor.LoginInterceptor;
import com.dre4m1nd.cipherlens.interceptor.RefreshInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author dre4m1nd
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private LoginInterceptor loginInterceptor;
    @Resource
    private RefreshInterceptor refreshInterceptor;

    @Override
    public void addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry registry) {
        registry.addInterceptor(refreshInterceptor)
                .addPathPatterns("/**")
                .order(0);
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/register",
                        "/user/login",
                        "/user/reset",
                        "/doc.html",
                        "/v3/api-docs",
                        "/swagger-ui.html/**",
                        "/actuator/**",
                        "/favicon.ico")
                .order(1);
    }

    @Override
    public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
                .allowCredentials(true)
                .maxAge(3600);
    }

}
