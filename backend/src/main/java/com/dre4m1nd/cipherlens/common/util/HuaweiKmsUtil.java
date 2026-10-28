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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;


@Slf4j
public final class HuaweiKmsUtil {

    public static final String REGION = "cn-south-1";
    public static final String KEY_ID = "fe7c0e06-381f-4d29-be61-f161e2d67225";
    public static final String ALGORITHM = "RSAES_OAEP_SHA_256";

    public static final KmsClient CLIENT;

    @Value("${huawei.ak}")
    private static String ak;
    @Value("${huawei.sk}")
    private static String sk;

    static {

        ICredential auth = new BasicCredentials()
                .withAk(ak)
                .withSk(sk);

        CLIENT = KmsClient.newBuilder()
                .withCredential(auth)
                .withRegion(KmsRegion.valueOf(REGION))
                .build();

    }

    public static String decrypt(String cipherText) {
        DecryptDataRequest request = new DecryptDataRequest();
        DecryptDataRequestBody body = new DecryptDataRequestBody();
        body.withKeyId(KEY_ID);
        body.withEncryptionAlgorithm(DecryptDataRequestBody.EncryptionAlgorithmEnum.fromValue(ALGORITHM));
        body.withCipherText(cipherText);
        request.withBody(body);
        try {
            // 3. 调用KMS解密接口
            DecryptDataResponse response = CLIENT.decryptData(request);
            log.info("调用接口状态码:{}", response.getHttpStatusCode());

            // 4. 提取解密结果（核心修复点）
            String plainText = response.getPlainText();
            if (plainText == null || plainText.isBlank()) {
                throw new RuntimeException("KMS解密失败：返回的明文为空");
            }

            log.info("KMS解密成功，原始明文：{}", plainText);
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
