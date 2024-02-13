package com.listywave.common.util;

import java.util.regex.Pattern;

public class StringUtils {

    public static boolean match(String source, String keyword) {
        return source.matches(".*" + Pattern.quote(keyword) + ".*");
    }
}
