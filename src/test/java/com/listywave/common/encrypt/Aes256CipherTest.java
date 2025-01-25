package com.listywave.common.encrypt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.listywave.common.IntegrationTest;
import org.junit.jupiter.api.Test;

class Aes256CipherTest extends IntegrationTest {

    @Test
    void 암호화를_한다() {
        // given
        String plainText = "plain";

        // when
        String result = aes256Cipher.encrypt(plainText);
        String result2 = aes256Cipher.encrypt(plainText);
        String result3 = aes256Cipher.encrypt(plainText);

        // then
        assertAll(
                () -> assertThat(result).isEqualTo(result2).isEqualTo(result3),
                () -> assertThat(plainText).isNotEqualTo(result)
                        .isNotEqualTo(result2)
                        .isNotEqualTo(result3)
        );
    }

    @Test
    void 복호화를_한다() {
        // given
        String plainText = "plain";

        String encrypted = aes256Cipher.encrypt(plainText);

        // when
        String decrypted = aes256Cipher.decrypt(encrypted);

        // then
        assertThat(plainText).isEqualTo(decrypted);
    }
}
