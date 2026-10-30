package com.dre4m1nd.cipherlens.common.util;

import com.dre4m1nd.cipherlens.exception.BusinessException;
import com.dre4m1nd.cipherlens.exception.SystemException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

/**
 * 邮箱验证码服务（用于找回密码）
 * <p>
 * 验证码保存在 Redis：{@code reset:code:{username}}，5 分钟有效、只能使用一次；
 * 发送带 60 秒冷却，校验带失败次数上限，避免被暴力枚举。
 *
 * @author dre4m1nd
 */
@Slf4j
@Service
public class VerificationCodeService {

    private static final String CODE_KEY_PREFIX = "reset:code:";
    private static final String COOLDOWN_KEY_PREFIX = "reset:code:cooldown:";
    private static final int CODE_TTL_MINUTES = 5;
    private static final int COOLDOWN_SECONDS = 60;
    private static final int MAX_VERIFY_ATTEMPTS = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${spring.mail.host:}")
    private String mailHost;
    @Value("${spring.mail.username:}")
    private String mailFrom;
    /** 未配置 SMTP 时是否把验证码打到日志（仅开发环境建议开启） */
    @Value("${security.reset.code-log-enabled:false}")
    private boolean codeLogEnabled;

    /**
     * 生成并发送重置密码验证码
     *
     * @param username 用户名（验证码存储维度）
     * @param email    收件邮箱
     */
    public void sendResetCode(String username, String email) {
        String cooldownKey = COOLDOWN_KEY_PREFIX + username;
        Boolean acquired = stringRedisTemplate.opsForValue()
                .setIfAbsent(cooldownKey, "1", COOLDOWN_SECONDS, TimeUnit.SECONDS);
        if (!Boolean.TRUE.equals(acquired)) {
            throw new BusinessException("验证码发送过于频繁，请稍后再试");
        }

        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        String codeKey = CODE_KEY_PREFIX + username;
        stringRedisTemplate.opsForValue().set(codeKey, code, CODE_TTL_MINUTES, TimeUnit.MINUTES);
        try {
            deliver(email, code);
        } catch (RuntimeException e) {
            // 发送失败时清理，允许用户立即重试
            stringRedisTemplate.delete(codeKey);
            stringRedisTemplate.delete(cooldownKey);
            throw e;
        }
    }

    /**
     * 校验并消费验证码（校验通过后立即失效）
     *
     * @param username 用户名
     * @param code     用户输入的验证码
     */
    public void verifyAndConsume(String username, String code) {
        if (username == null || username.isBlank() || code == null || code.isBlank()) {
            throw new BusinessException("验证码错误或已过期");
        }
        String codeKey = CODE_KEY_PREFIX + username;
        String cached = stringRedisTemplate.opsForValue().get(codeKey);
        if (cached == null) {
            throw new BusinessException("验证码错误或已过期");
        }

        // 失败次数限制，防止 6 位验证码被暴力枚举
        String attemptsKey = codeKey + ":attempts";
        Long attempts = stringRedisTemplate.opsForValue().increment(attemptsKey);
        if (attempts != null && attempts == 1L) {
            stringRedisTemplate.expire(attemptsKey, CODE_TTL_MINUTES, TimeUnit.MINUTES);
        }
        if (attempts != null && attempts > MAX_VERIFY_ATTEMPTS) {
            stringRedisTemplate.delete(codeKey);
            stringRedisTemplate.delete(attemptsKey);
            throw new BusinessException("验证码错误次数过多，请重新获取");
        }

        if (!cached.equals(code.trim())) {
            throw new BusinessException("验证码错误或已过期");
        }
        stringRedisTemplate.delete(codeKey);
        stringRedisTemplate.delete(attemptsKey);
    }

    /**
     * 发送验证码邮件；未配置 SMTP 时按配置决定是否降级为日志输出（仅开发环境）
     */
    private void deliver(String email, String code) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailHost == null || mailHost.isBlank() || mailSender == null) {
            if (codeLogEnabled) {
                log.warn("[DEV] 未配置邮件服务，{} 的密码重置验证码为：{}", email, code);
                return;
            }
            throw new BusinessException("邮件服务未配置，无法发送验证码，请联系管理员");
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            if (mailFrom != null && !mailFrom.isBlank()) {
                message.setFrom(mailFrom);
            }
            message.setTo(email);
            message.setSubject("密鉴 CipherLens 密码重置验证码");
            message.setText("您的密码重置验证码为：" + code + "，" + CODE_TTL_MINUTES
                    + " 分钟内有效且仅可使用一次。如非本人操作，请忽略本邮件。");
            mailSender.send(message);
            log.info("密码重置验证码已发送至 {}", maskEmail(email));
        } catch (Exception e) {
            log.error("验证码邮件发送失败: {}", e.getMessage(), e);
            throw new SystemException("验证码邮件发送失败，请稍后重试");
        }
    }

    private String maskEmail(String email) {
        if (email == null) {
            return "";
        }
        int at = email.indexOf('@');
        if (at <= 1) {
            return "***";
        }
        return email.charAt(0) + "***" + email.substring(at);
    }
}
