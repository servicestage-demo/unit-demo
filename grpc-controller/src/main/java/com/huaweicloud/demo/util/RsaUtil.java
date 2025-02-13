package com.huaweicloud.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

@Component
public class RsaUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(RsaUtil.class);

    private Cipher privateKeyCipher;

    private Cipher publicKeyCipher;

    @PostConstruct
    public void init() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
        InvalidKeyException {
        initPrivateKey();
        initPublicKey();
    }

    private void initPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
        InvalidKeyException {
        // 读取PEM文件
        Resource resource = new ClassPathResource("private_key.pem");
        StringBuilder pemContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                pemContent.append(line);
            }
        } catch (IOException e) {
            LOGGER.error("Cannot load the private key.");
            throw new RuntimeException(e);
        }

        // 去除PEM头部和尾部
        String privateKeyPEM = pemContent.toString();
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----", "");
        privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");
        privateKeyPEM = privateKeyPEM.replace("\n", "");

        // Base64解码
        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);

        // 创建PrivateKey对象
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        // 创建Cipher对象
        privateKeyCipher = Cipher.getInstance("RSA");
        privateKeyCipher.init(Cipher.DECRYPT_MODE, privateKey);
        LOGGER.info("PrivateKey loaded successfully.");
    }

    private void initPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
        InvalidKeyException {
        // 读取PEM文件
        Resource resource = new ClassPathResource("public_key.pem");
        StringBuilder pemContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                pemContent.append(line);
            }
        } catch (IOException e) {
            LOGGER.error("Cannot load the public key.");
            throw new RuntimeException(e);
        }

        // 去除PEM头部和尾部
        String publicKeyPEM = pemContent.toString();
        publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "");
        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
        publicKeyPEM = publicKeyPEM.replace("\n", "");

        // Base64解码
        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);

        // 创建PublicKey对象
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        // 创建Cipher对象
        publicKeyCipher = Cipher.getInstance("RSA");
        publicKeyCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        LOGGER.info("PublicKey loaded successfully.");
    }

    /**
     * 加密
     *
     * @param str the str
     * @return the string
     */
    public String encrypt(String str) {
        try {
            return Base64.getEncoder().encodeToString(publicKeyCipher.doFinal(str.getBytes()));
        } catch (IllegalBlockSizeException | BadPaddingException | IllegalArgumentException ex) {
            return str;
        }
    }

    /**
     * 解密
     *
     * @param str the str
     * @return the string
     */
    public String decrypt(String str) {
        try {
            return new String(privateKeyCipher.doFinal(Base64.getDecoder().decode(str)));
        } catch (IllegalBlockSizeException | BadPaddingException | IllegalArgumentException ex) {
            return str;
        }
    }
}
