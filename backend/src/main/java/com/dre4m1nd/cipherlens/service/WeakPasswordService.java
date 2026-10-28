package com.dre4m1nd.cipherlens.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dre4m1nd.cipherlens.model.entity.WeakPassword;

/**
* @author dre4m1nd
* @description 针对表【weak_password(弱密码字典表（极简版）)】的数据库操作Service
* @createDate 2025-12-06 22:51:13
*/
public interface WeakPasswordService extends IService<WeakPassword> {
    boolean isWeakPassword(String password);
}
