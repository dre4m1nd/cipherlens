package com.dre4m1nd.cipherlens.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dre4m1nd.cipherlens.common.model.*;
import com.dre4m1nd.cipherlens.model.entity.SysUser;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
* @author dre4m1nd
* @description 针对表【sys_user(用户信息表)】的数据库操作Service
* @createDate 2025-12-05 12:41:54
*/
public interface SysUserService extends IService<SysUser> {

    Result<Token> login(LoginForm loginForm);

    Result<?> logout();

    Result<?> addUser(RegisterForm registerForm) throws Exception;

    Result<?> sendResetCode(ResetCodeForm resetCodeForm);

    Result<?> resetPassword(ResetPasswordForm resetPasswordForm) throws NoSuchAlgorithmException, InvalidKeySpecException;

    Result<?> updateSysUser(UpdateUserForm updateUserForm) throws NoSuchAlgorithmException, InvalidKeySpecException;

    Result<UpdateUserForm> getUserInfo();
}
