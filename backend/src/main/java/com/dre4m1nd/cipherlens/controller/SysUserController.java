package com.dre4m1nd.cipherlens.controller;

import com.dre4m1nd.cipherlens.common.model.*;
import com.dre4m1nd.cipherlens.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * @author dre4m1nd
 */
@RestController
@RequestMapping("/user")
@Slf4j
@Tag(name = "系统用户接口")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @Operation(summary = "登录接口")
    @PostMapping("/login")
    public Result<Token> login(@Validated @RequestBody LoginForm loginForm) {
        return sysUserService.login(loginForm);
    }

    @Operation(summary = "登出接口")
    @GetMapping("/logout")
    public Result<?> logout() {
        return sysUserService.logout();
    }

    @Operation(summary = "注册接口")
    @PostMapping("/register")
    public Result<?> addUser(@Validated @RequestBody RegisterForm registerForm) throws Exception {
        return sysUserService.addUser(registerForm);
    }

    @Operation(summary = "发送重置密码验证码接口")
    @PostMapping("/reset/code")
    public Result<?> sendResetCode(@Validated @RequestBody ResetCodeForm resetCodeForm) {
        return sysUserService.sendResetCode(resetCodeForm);
    }

    @Operation(summary = "重置密码接口（需邮箱验证码）")
    @PostMapping("/reset")
    public Result<?> resetPassword(@Validated @RequestBody ResetPasswordForm resetPasswordForm) throws Exception {
        return sysUserService.resetPassword(resetPasswordForm);
    }

    @Operation(summary = "获取用户信息接口")
    @GetMapping("/info")
    public Result<UpdateUserForm> getUserInfo() {
        return sysUserService.getUserInfo();
    }

    @Operation(summary = "用户更新接口")
    @PostMapping("/update")
    public Result<?> updateSysUser(@Validated @RequestBody UpdateUserForm updateUserForm) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return sysUserService.updateSysUser(updateUserForm);
    }
}
