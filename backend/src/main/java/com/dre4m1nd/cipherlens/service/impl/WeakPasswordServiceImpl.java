package com.dre4m1nd.cipherlens.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dre4m1nd.cipherlens.mapper.WeakPasswordMapper;
import com.dre4m1nd.cipherlens.model.entity.WeakPassword;
import com.dre4m1nd.cipherlens.service.WeakPasswordService;
import org.springframework.stereotype.Service;

/**
* @author dre4m1nd
* @description 针对表【weak_password(弱密码字典表（极简版）)】的数据库操作Service实现
* @createDate 2025-12-06 22:51:13
*/
@Service
public class WeakPasswordServiceImpl extends ServiceImpl<WeakPasswordMapper, WeakPassword>
    implements WeakPasswordService{

    @Override
    public boolean isWeakPassword(String password) {
        return this.lambdaQuery().eq(WeakPassword::getPassword, password).count() > 0;
    }
}




