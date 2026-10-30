package com.dre4m1nd.cipherlens.common.util;

import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.auth.ICredential;
import com.huaweicloud.sdk.core.exception.ConnectionException;
import com.huaweicloud.sdk.core.exception.RequestTimeoutException;
import com.huaweicloud.sdk.core.exception.ServiceResponseException;
import com.huaweicloud.sdk.kms.v2.KmsClient;
import com.huaweicloud.sdk.kms.v2.model.DecryptDataRequest;
import com.huaweicloud.sdk.kms.v2.model.DecryptDataRequestBody;
import com.huaweicloud.sdk.kms.v2.model.DecryptDataResponse;
import com.huaweicloud.sdk.kms.v2.region.KmsRegion;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public final class HuaweiKmsUtil {

    private static final String DEFAULT_REGION = "cn-south-1";
    private static final String DEFAULT_ALGORITHM = "RSAES_OAEP_SHA_256";

    private static volatile KmsClient CLIENT;
    /** 密钥 ID 与算法由配置注入，避免写死在代码里（换密钥/换区域无需改代码） */
    private static volatile String keyId;
    private static volatile String algorithm = DEFAULT_ALGORITHM;

    @Value("${huawei.ak:}")
    private String ak;
    @Value("${huawei.sk:}")
    private String sk;
    @Value("${huawei.kms.key-id:}")
    private String configuredKeyId;
    @Value("${huawei.kms.region:" + DEFAULT_REGION + "}")
    private String configuredRegion;
    @Value("${huawei.kms.algorithm:" + DEFAULT_ALGORITHM + "}")
    private String configuredAlgorithm;

    @PostConstruct
    public void init() {
        keyId = configuredKeyId;
        if (configuredAlgorithm != null && !configuredAlgorithm.isBlank()) {
            algorithm = configuredAlgorithm;
        }
        if (ak == null || ak.isBlank() || sk == null || sk.isBlank()) {
            // 未配置 KMS 凭据时不初始化客户端，避免带着空 AK/SK 调用云服务
            log.warn("未配置华为云 KMS 凭据（HUAWEI_AK/HUAWEI_SK），相关解密功能不可用");
            return;
        }
        if (keyId == null || keyId.isBlank()) {
            log.warn("未配置华为云 KMS 密钥 ID（huawei.kms.key-id），相关解密功能不可用");
            return;
        }
        String region = (configuredRegion == null || configuredRegion.isBlank()) ? DEFAULT_REGION : configuredRegion;

        ICredential auth = new BasicCredentials()
                .withAk(ak)
                .withSk(sk);

        CLIENT = KmsClient.newBuilder()
                .withCredential(auth)
                .withRegion(KmsRegion.valueOf(region))
                .build();
    }

    public static String decrypt(String cipherText) {
        KmsClient client = CLIENT;
        String currentKeyId = keyId;
        if (client == null) {
            throw new IllegalStateException("未配置华为云 KMS 凭据，无法解密用户密钥");
        }
        if (currentKeyId == null || currentKeyId.isBlank()) {
            throw new IllegalStateException("未配置华为云 KMS 密钥 ID，无法解密用户密钥");
        }
        DecryptDataRequest request = new DecryptDataRequest();
        DecryptDataRequestBody body = new DecryptDataRequestBody();
        body.withKeyId(currentKeyId);
        body.withEncryptionAlgorithm(DecryptDataRequestBody.EncryptionAlgorithmEnum.fromValue(algorithm));
        body.withCipherText(cipherText);
        request.withBody(body);
        try {
            // 3. 调用KMS解密接口
            DecryptDataResponse response = client.decryptData(request);
            log.debug("调用接口状态码:{}", response.getHttpStatusCode());

            // 4. 提取解密结果
            String plainText = response.getPlainText();
            if (plainText == null || plainText.isBlank()) {
                throw new RuntimeException("KMS解密失败：返回的明文为空");
            }
            // 注意：明文属于敏感数据，禁止写入日志
            return plainText;

        } catch (ConnectionException e) {
            log.error("KMS解密失败：网络连接异常", e);
            throw new RuntimeException("KMS网络连接失败，请检查网络和区域配置", e);
        } catch (RequestTimeoutException e) {
            log.error("KMS解密失败：请求超时", e);
            throw new RuntimeException("KMS请求超时，请重试", e);
        } catch (ServiceResponseException e) {
            log.error("KMS解密失败：服务异常，错误信息：{}", e.getMessage());
            throw new RuntimeException(String.format("KMS服务异常：%s", e.getMessage()), e);
        } catch (Exception e) {
            log.error("KMS解密失败：未知异常", e);
            throw new RuntimeException("KMS解密失败", e);
        }
    }


}
