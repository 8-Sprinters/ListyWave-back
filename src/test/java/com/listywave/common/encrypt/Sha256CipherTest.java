package com.listywave.common.encrypt;

import static org.assertj.core.api.Assertions.assertThat;

import com.listywave.common.IntegrationTest;
import org.junit.jupiter.api.Test;

class Sha256CipherTest extends IntegrationTest {

    @Test
    void 암호화를_한다() {
        // given
        String plainText = "1234";

        // when
        String encrypted1 = sha256Cipher.encrypt(plainText);
        String encrypted2 = sha256Cipher.encrypt(plainText);

        // then
        assertThat(plainText).isNotEqualTo(encrypted1);
        assertThat(encrypted1).isEqualTo(encrypted2);
    }
}
