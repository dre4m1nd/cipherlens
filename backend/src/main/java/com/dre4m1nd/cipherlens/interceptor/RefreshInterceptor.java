package com.dre4m1nd.cipherlens.interceptor;

import cn.hutool.core.util.StrUtil;
import com.dre4m1nd.cipherlens.common.util.RedisTokenUtil;
import com.dre4m1nd.cipherlens.common.util.UserHolderUtil;
import com.dre4m1nd.cipherlens.exception.UnAuthorizedException;
import com.dre4m1nd.cipherlens.model.dto.SysUserDTO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class RefreshInterceptor implements HandlerInterceptor {

    @Resource
    private RedisTokenUtil redisTokenUtil;

    //检测登录状态是否过期
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        //未登录,放行
        if (StrUtil.isBlank(token)) {
            return true;
        }
        //无效token,放行（refresh 拦截器只负责续期，鉴权由 LoginInterceptor 完成）
        try {
            if (!redisTokenUtil.isValidToken(token)){
                return true;
            }
        } catch (UnAuthorizedException e) {
            return true;
        }
        Long userId = redisTokenUtil.getFieldByToken(token, SysUserDTO::getId);
        UserHolderUtil.set(userId);
        redisTokenUtil.refreshToken(token);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolderUtil.remove();
    }

}
