package com.dre4m1nd.cipherlens.interceptor;

import cn.hutool.core.util.StrUtil;
import com.dre4m1nd.cipherlens.common.util.RedisTokenUtil;
import com.dre4m1nd.cipherlens.common.util.UserHolderUtil;
import com.dre4m1nd.cipherlens.exception.UnAuthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private RedisTokenUtil redisTokenUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 检测是否登录
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long userId;
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            throw new UnAuthorizedException("未登录");
        }
        if (!redisTokenUtil.isValidToken(token)) {
            throw new UnAuthorizedException("无效令牌");
        }
        userId = redisTokenUtil.getUserId(token);
        UserHolderUtil.set(userId);
        return true;
    }

}
