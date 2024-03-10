package com.listywave.common.util;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TimeUtils는 ")
class TimeUtilsTest {

    @Test
    void 시간_단위를_변환한다() {
        TimeUnit from = HOURS;
        TimeUnit to = MILLISECONDS;

        Long result = TimeUtils.convertTimeUnit(2, from, to);

        assertThat(result).isEqualTo(1000 * 60 * 60 * 2);
    }
}
