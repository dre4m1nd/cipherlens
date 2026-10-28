package com.dre4m1nd.cipherlens.common.crypto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * PBKDF2Util 单元测试类
 */
public class PBKDF2UtilTest {

    private String testPassword;
    private String salt;
    private String passwordHash;

    @BeforeEach
    void setUp() throws Exception {
        testPassword = "TestPassword123!";
        salt = PBKDF2Util.generateSalt();
        passwordHash = PBKDF2Util.generatePasswordHash(testPassword, salt);
    }

    @Test
    void testGenerateSalt() {
        // 测试盐值生成
        String salt1 = PBKDF2Util.generateSalt();
        String salt2 = PBKDF2Util.generateSalt();
        
        assertNotNull(salt1);
        assertNotNull(salt2);
        assertNotEquals(salt1, salt2);
        assertTrue(salt1.length() > 0);
    }

    @Test
    void testGeneratePasswordHash() throws Exception {
        // 测试密码哈希生成
        assertNotNull(passwordHash);
        assertTrue(passwordHash.length() > 0);
        
        // 相同密码和盐应该生成相同的哈希
        String samePasswordHash = PBKDF2Util.generatePasswordHash(testPassword, salt);
        assertEquals(passwordHash, samePasswordHash);
        
        // 不同密码相同盐应该生成不同的哈希
        String differentPasswordHash = PBKDF2Util.generatePasswordHash("DifferentPassword123!", salt);
        assertNotEquals(passwordHash, differentPasswordHash);
        
        // 相同密码不同盐应该生成不同的哈希
        String differentSalt = PBKDF2Util.generateSalt();
        String samePasswordDifferentSaltHash = PBKDF2Util.generatePasswordHash(testPassword, differentSalt);
        assertNotEquals(passwordHash, samePasswordDifferentSaltHash);
    }

    @Test
    void testVerifyPassword() throws Exception {
        // 测试密码验证
        assertTrue(PBKDF2Util.verifyPassword(testPassword, passwordHash, salt));
        assertFalse(PBKDF2Util.verifyPassword("WrongPassword", passwordHash, salt));
        
        // 测试空值处理
        assertFalse(PBKDF2Util.verifyPassword(null, passwordHash, salt));
        assertFalse(PBKDF2Util.verifyPassword(testPassword, null, salt));
        assertFalse(PBKDF2Util.verifyPassword(testPassword, passwordHash, null));
        assertFalse(PBKDF2Util.verifyPassword(null, null, null));
    }

    @Test
    void testGeneratePasswordHash_WithInvalidInput() {
        // 测试无效输入
        assertThrows(IllegalArgumentException.class, () -> {
            PBKDF2Util.generatePasswordHash(null, salt);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            PBKDF2Util.generatePasswordHash("", salt);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            PBKDF2Util.generatePasswordHash(testPassword, null);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            PBKDF2Util.generatePasswordHash(testPassword, "");
        });
    }
}
