package com.dre4m1nd.cipherlens.common.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-256-GCM 对称加密静态工具类
 * 特性：
 * 1. 纯静态方法，禁止实例化
 * 2. 支持AES-256-GCM加解密（可逆）
 * 3. 自动生成安全的密钥/IV，防重放攻击
 * 4. 无外部依赖，仅使用JDK内置类库
 */
public final class AES256GCMUtil {
    // 私有化构造方法，禁止实例化
    private AES256GCMUtil() {
        throw new IllegalStateException("Utility class");
    }

    // -------------------------- 核心安全配置（静态常量） --------------------------
    /** AES算法名称（GCM模式） */
    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";
    /** 密钥派生算法（用于从密码生成256位密钥） */
    private static final String KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256";
    /** AES-256 密钥长度（必须32字节） */
    private static final int AES_KEY_SIZE = 256;
    /** GCM模式IV长度（推荐12字节，符合NIST标准） */
    private static final int GCM_IV_LENGTH = 12;
    /** GCM模式认证标签长度（128位，最高安全性） */
    private static final int GCM_TAG_LENGTH = 128;
    /** 密钥派生迭代次数（平衡性能与安全性） */
    private static final int KEY_DERIVATION_ITERATIONS = 65536;
    /** 安全随机数生成器（单例，避免重复初始化） */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // -------------------------- 核心静态方法 --------------------------
    /**
     * 生成AES-256 密钥（Base64编码，便于存储）
     * @return Base64格式的256位AES密钥
     */
    public static String generateAESKey() {
        // 生成32字节（256位）随机密钥
        byte[] keyBytes = new byte[AES_KEY_SIZE / 8];
        SECURE_RANDOM.nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    /**
     * AES-256-GCM 加密
     * @param plainText 明文（需要加密的内容，如账号、密码、备注）
     * @param aesKey Base64编码的AES-256密钥
     * @return Base64格式的密文（包含IV+密文+认证标签，可直接存储）
     * @throws Exception 加密失败时抛出（可根据业务封装为运行时异常）
     */
    public static String encrypt(String plainText, String aesKey) throws Exception {
        // 参数校验
        if (plainText == null || plainText.isBlank()) {
            throw new IllegalArgumentException("明文内容不能为空");
        }
        if (aesKey == null || aesKey.isBlank()) {
            throw new IllegalArgumentException("AES密钥不能为空");
        }

        // 1. 解码Base64密钥为字节数组
        byte[] keyBytes = Base64.getDecoder().decode(aesKey);
        if (keyBytes.length != AES_KEY_SIZE / 8) {
            throw new IllegalArgumentException("AES密钥必须为256位（32字节）");
        }
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        // 2. 生成12字节随机IV（GCM模式推荐）
        byte[] iv = new byte[GCM_IV_LENGTH];
        SECURE_RANDOM.nextBytes(iv);

        // 3. 初始化GCM参数
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        // 4. 初始化加密器
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

        // 5. 执行加密
        byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // 6. 拼接IV+密文（IV用于解密，需和密文一起存储）
        byte[] result = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(cipherText, 0, result, iv.length, cipherText.length);

        // 7. Base64编码返回（便于存储和传输）
        return Base64.getEncoder().encodeToString(result);
    }

    /**
     * AES-256-GCM 解密
     * @param cipherText Base64格式的密文（加密返回的内容）
     * @param aesKey Base64编码的AES-256密钥
     * @return 明文内容
     * @throws Exception 解密失败时抛出（如密钥错误、密文被篡改）
     */
    public static String decrypt(String cipherText, String aesKey) throws Exception {
        // 参数校验
        if (cipherText == null || cipherText.isBlank()) {
            throw new IllegalArgumentException("密文内容不能为空");
        }
        if (aesKey == null || aesKey.isBlank()) {
            throw new IllegalArgumentException("AES密钥不能为空");
        }

        // 1. 解码Base64密文为字节数组
        byte[] cipherBytes = Base64.getDecoder().decode(cipherText);
        if (cipherBytes.length < GCM_IV_LENGTH) {
            throw new IllegalArgumentException("无效的密文格式");
        }

        // 2. 拆分IV和密文（前12字节是IV，剩余是密文+认证标签）
        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] encryptedData = new byte[cipherBytes.length - GCM_IV_LENGTH];
        System.arraycopy(cipherBytes, 0, iv, 0, GCM_IV_LENGTH);
        System.arraycopy(cipherBytes, GCM_IV_LENGTH, encryptedData, 0, encryptedData.length);

        // 3. 解码Base64密钥
        byte[] keyBytes = Base64.getDecoder().decode(aesKey);
        if (keyBytes.length != AES_KEY_SIZE / 8) {
            throw new IllegalArgumentException("AES密钥必须为256位（32字节）");
        }
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        // 4. 初始化GCM参数
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        // 5. 初始化解密器
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

        // 6. 执行解密（GCM模式会自动校验认证标签，防止密文被篡改）
        byte[] plainBytes = cipher.doFinal(encryptedData);

        // 7. 转换为明文字符串
        return new String(plainBytes, StandardCharsets.UTF_8);
    }


}