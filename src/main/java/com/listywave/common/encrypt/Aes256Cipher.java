package com.listywave.common.encrypt;

import static com.listywave.common.exception.ErrorCode.ENCRYPT_ERROR;

import com.listywave.common.exception.CustomException;
import jakarta.annotation.PostConstruct;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Aes256Cipher {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    @Value("${aes.password}")
    private String password;
    @Value("${aes.salt}")
    private String salt;

    private SecretKey secretKey;
    private IvParameterSpec iv;

    @PostConstruct
    public void init() throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.secretKey = createSecretKey();
        this.iv = createIv();
    }

    private SecretKey createSecretKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        return new SecretKeySpec(secretKeyFactory.generateSecret(keySpec).getEncoded(), "AES");
    }

    private IvParameterSpec createIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] cipherText = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(cipherText);
        } catch (
                NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                InvalidAlgorithmParameterException | BadPaddingException | InvalidKeyException e) {
            throw new CustomException(ENCRYPT_ERROR, e.getMessage());
        }
    }

    public String decrypt(String cipherText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(plainText);
        } catch (
                NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                InvalidAlgorithmParameterException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
