package com.dre4m1nd.cipherlens.interceptor;

import cn.hutool.core.util.StrUtil;
import com.dre4m1nd.cipherlens.common.util.RedisTokenUtil;
import com.dre4m1nd.cipherlens.common.util.UserHolderUtil;
import com.dre4m1nd.cipherlens.exception.UnAuthorizedException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private RedisTokenUtil redisTokenUtil;

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

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 线程池复用线程，务必清理 ThreadLocal
        UserHolderUtil.remove();
    }

}
