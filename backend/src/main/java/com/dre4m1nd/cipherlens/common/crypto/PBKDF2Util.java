package com.dre4m1nd.cipherlens.common.crypto;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * PBKDF2WithHmacSHA256 静态工具类
 * 所有方法均为静态，无需实例化，直接通过类名调用
 * 功能：生成盐值、生成密码哈希、验证密码
 * 无外部依赖，仅依赖JDK内置类库
 */
public final class PBKDF2Util {


    // -------------------------- 核心配置常量（静态） --------------------------
    /** PBKDF2算法名称 */
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    /** 迭代次数：行业推荐安全值，平衡性能与安全性 */
    private static final int ITERATIONS = 65536;
    /** 哈希密钥长度：256位 */
    private static final int KEY_LENGTH = 256;
    /** 盐值长度：16字节（128位），防彩虹表攻击 */
    private static final int SALT_LENGTH = 16;
    /** 安全随机数生成器（静态单例，避免重复初始化） */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // -------------------------- 核心静态方法 --------------------------
    /**
     * 生成随机盐值（Base64编码，便于数据库存储）
     * @return Base64格式的盐值字符串
     */
    public static String generateSalt() {
        byte[] saltBytes = new byte[SALT_LENGTH];
        SECURE_RANDOM.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    /**
     * 生成密码的PBKDF2哈希值（Base64编码）
     * @param rawPassword 明文密码（用户输入）
     * @param salt Base64编码的盐值
     * @return Base64格式的密码哈希值
     * @throws NoSuchAlgorithmException 算法不支持（JDK8+不会触发）
     * @throws InvalidKeySpecException 密钥规格错误
     */
    public static String generatePasswordHash(String rawPassword, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 参数校验
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("明文密码不能为空");
        }
        if (salt == null || salt.isBlank()) {
            throw new IllegalArgumentException("盐值不能为空");
        }

        // 解码Base64盐值为字节数组
        byte[] saltBytes = Base64.getDecoder().decode(salt);
        // 构建PBE密钥规格
        KeySpec keySpec = new PBEKeySpec(rawPassword.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH);
        // 生成哈希值
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hashBytes = factory.generateSecret(keySpec).getEncoded();
        // 转换为Base64字符串返回
        return Base64.getEncoder().encodeToString(hashBytes);
    }

    /**
     * 验证明文密码是否匹配存储的哈希值
     * @param rawPassword 待验证的明文密码
     * @param storedHash 数据库中存储的密码哈希值（Base64）
     * @param storedSalt 数据库中存储的盐值（Base64）
     * @return 匹配返回true，不匹配/异常返回false
     */
    public static boolean verifyPassword(String rawPassword, String storedHash, String storedSalt) {
        // 空值快速失败
        if (rawPassword == null || storedHash == null || storedSalt == null) {
            return false;
        }

        try {
            // 重新计算输入密码的哈希值
            String computedHash = generatePasswordHash(rawPassword, storedSalt);
            // 常量时间比较，防止时序攻击
            return constantTimeEquals(computedHash, storedHash);
        } catch (Exception e) {
            // 任何异常都返回验证失败，不暴露具体错误
            return false;
        }
    }

    // -------------------------- 私有静态辅助方法 --------------------------
    /**
     * 常量时间比较两个字符串，防止时序攻击
     * @param a 字符串1
     * @param b 字符串2
     * @return 相等返回true，否则false
     */
    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

}