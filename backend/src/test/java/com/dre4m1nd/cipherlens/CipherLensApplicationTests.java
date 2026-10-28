package com.dre4m1nd.cipherlens;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.dre4m1nd.cipherlens.common.crypto.PBKDF2Util;
import com.dre4m1nd.cipherlens.common.crypto.RSA4096Util;
import com.dre4m1nd.cipherlens.common.util.HuaweiKmsUtil;
import com.dre4m1nd.cipherlens.common.util.RedisTokenUtil;
import com.dre4m1nd.cipherlens.model.entity.WeakPassword;
import com.dre4m1nd.cipherlens.service.WeakPasswordService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@SpringBootTest
class CipherLensApplicationTests {

    @Resource
    private RedisTokenUtil redisTokenUtil;

    @Resource
    private WeakPasswordService weakPasswordService;


//    @Test
//    public void contextLoads() throws Exception {
//        List<String> strings = FileUtil.readLines(, StandardCharsets.UTF_8);
//        for (String string : strings) {
//            WeakPassword weakPassword = new WeakPassword();
//            weakPassword.setPassword(string);
//            weakPasswordService.save(weakPassword);
//        }
//
//    }

}
