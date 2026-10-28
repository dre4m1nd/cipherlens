package com.dre4m1nd.cipherlens.common.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA-4096 非对称加密静态工具类
 * 适配场景：加密/解密用户的AES密钥，符合密码管理器安全规范
 * 特性：纯静态方法、禁止实例化、支持PEM格式密钥读取、4096位密钥长度
 * 适配华为云KMS：算法为RSAES_OAEP_SHA_256
 */
public final class RSA4096Util {

    // -------------------------- 核心常量 --------------------------
    /** RSA算法名称 */
    private static final String RSA_ALGORITHM = "RSA";
    /** RSA密钥长度（4096位，工业级标准） */
    private static final int RSA_KEY_SIZE = 4096;
    /** 加密填充方式（适配华为云KMS的RSAES_OAEP_SHA_256） */
    private static final String RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    /** OAEP参数 - 哈希算法（匹配KMS的SHA-256） */
    private static final String OAEP_HASH_ALGORITHM = "SHA-256";
    /** OAEP参数 - 掩码生成函数（匹配KMS的MGF1） */
    private static final String OAEP_MGF_ALGORITHM = "MGF1";
    /** 公钥 Base64 编码*/
    public static final String PUBLIC_KEY_BASE64 = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEArxgcaiCCHlvNWMbOfK3DDzpQNsEjBGBU4p39aHnip41DEZWtjGULnFbyFpazMOdPmxj992v72Ujs4Lqmho91r6WCYQL8PhWSNY9a2lRCcXTwF97tOI3uhC1Dr/QDiw6a8H77IrLHgXXPC9emW8Y1Z1owNIPgSzWKQB3dNHHydzBSzt8X7CnVLgMkYU+u3O39j9UUNiEk2PmS8NdvI6rtEMZHyskzGxrbxdVfGUu0tM1r2gYe5F8qAbGoW9cBtSsOV0WQK+Hg60S/mjlGR3sXAvl3nOlpg70myBqbJ12UplzDAdWrJjN4fKNWlLsCbj4UgdQRIh2++tOH5lwxNc+gNRWifb0ghBv5DvuyrAphRhFXd0mAvRjFqHUIcw+wdflJkhv7rwOsuuWa5lwr6cg1cXLL+o5cFlJFBiCWNIPJbOvPXLTPurC3MjHkUy9yPFhvqYb7a3RVAXehfB1E1gViMWyiaR64BddlH2W8vCBJEC5v+yajKpkpUJ9+2B25ORpULLKNlZ1/FxuyYYDup9iCNtVP07f+++k/9mQyQGpZVU20kVdjA5RE+MOkB9M+w8ZDCdeHObJUhJsDPqOx3RmcKBRRCa7twdxKLOvzGJteoP6CCNc16oX3wAAWt932o3tL5PzmXqnHVQo56V+VQppSL+gw5YmlvM5XLi723/ntWj8CAwEAAQ==";
    // -------------------------- 核心静态方法 --------------------------

    /**
     * 用RSA公钥加密数据（适配加密用户AES密钥场景，兼容华为云KMS的RSAES_OAEP_SHA_256）
     * @param plainText 明文（如用户AES密钥，Base64编码）
     * @return Base64编码的密文
     * @throws Exception 加密失败
     */
    public static String encrypt(String plainText) throws Exception {
        // 参数校验
        if (plainText == null || plainText.isBlank()) {
            throw new IllegalArgumentException("明文内容不能为空");
        }

        // 1. 解码Base64公钥
        byte[] publicKeyBytes = Base64.getDecoder().decode(PUBLIC_KEY_BASE64);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        // 2. 初始化加密器（补充完整的OAEP参数，适配KMS的RSAES_OAEP_SHA_256）
        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
        OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                OAEP_HASH_ALGORITHM,
                OAEP_MGF_ALGORITHM,
                MGF1ParameterSpec.SHA256,
                PSource.PSpecified.DEFAULT
        );
        cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams);

        // 3. 执行加密（RSA 4096+OAEP_SHA256最大加密长度约422字节，满足AES密钥加密需求）
        byte[] cipherBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // 4. Base64编码返回
        return Base64.getEncoder().encodeToString(cipherBytes);
    }

    /**
     * 用RSA私钥解密数据（适配解密用户AES密钥场景，兼容华为云KMS的RSAES_OAEP_SHA_256）
     * @param cipherText Base64编码的密文
     * @param privateKeyBase64 Base64编码的RSA私钥（去除PEM头尾部后的纯字符串）
     * @return 明文（如用户AES密钥）
     * @throws Exception 解密失败
     */
    public static String decrypt(String cipherText, String privateKeyBase64) throws Exception {
        // 参数校验
        if (cipherText == null || cipherText.isBlank()) {
            throw new IllegalArgumentException("密文内容不能为空");
        }
        if (privateKeyBase64 == null || privateKeyBase64.isBlank()) {
            throw new IllegalArgumentException("RSA私钥不能为空");
        }

        // 1. 解码Base64私钥
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        // 2. 初始化解密器（补充完整的OAEP参数，适配KMS）
        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
        OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                OAEP_HASH_ALGORITHM,
                OAEP_MGF_ALGORITHM,
                MGF1ParameterSpec.SHA256,
                PSource.PSpecified.DEFAULT
        );
        cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);

        // 3. 执行解密
        byte[] plainBytes = cipher.doFinal(Base64.getDecoder().decode(cipherText));

        // 4. 转换为明文字符串
        return new String(plainBytes, StandardCharsets.UTF_8);
    }

    /**
     * 读取PEM格式的RSA公钥文件（适配OpenSSL/KMS生成的公钥文件）
     * @param pemFilePath 公钥文件路径（如：/opt/keys/public_key.pem）
     * @return Base64编码的公钥字符串（可直接传入encrypt方法）
     * @throws Exception 读取/解析失败
     */
    public static String readPublicKeyFromPem(String pemFilePath) throws Exception {
        // 读取PEM文件内容
        String pemContent = new String(Files.readAllBytes(Paths.get(pemFilePath)), StandardCharsets.UTF_8);

        // 去除PEM文件的头部、尾部和所有空白字符
        pemContent = pemContent.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        return pemContent;
    }

    /**
     * 读取PEM格式的RSA私钥文件（适配OpenSSL生成的私钥文件，无密码保护）
     * @param pemFilePath 私钥文件路径（如：/opt/keys/private_key_nopass.pem）
     * @return Base64编码的私钥字符串（可直接传入decrypt方法）
     * @throws Exception 读取/解析失败
     */
    public static String readPrivateKeyFromPem(String pemFilePath) throws Exception {
        // 读取PEM文件内容
        String pemContent = new String(Files.readAllBytes(Paths.get(pemFilePath)), StandardCharsets.UTF_8);

        // 去除PEM文件的头部、尾部和所有空白字符
        pemContent = pemContent.replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        return pemContent;
    }

    /**
     * 清理PEM格式公钥字符串（用于直接传入公钥文本的场景）
     * @param pemPublicKey PEM格式的公钥字符串（带-----BEGIN/END标识）
     * @return Base64编码的纯公钥字符串
     */
    public static String cleanPemPublicKey(String pemPublicKey) {
        if (pemPublicKey == null || pemPublicKey.isBlank()) {
            throw new IllegalArgumentException("PEM格式公钥不能为空");
        }
        return pemPublicKey.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
    }
}