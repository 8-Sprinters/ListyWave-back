package com.listywave.common.util;

import java.util.Random;

public class RandomEnumGenerator<T extends Enum<T>> {

    private static final Random PRNG = new Random();
    private final T[] values;

    public RandomEnumGenerator(Class<T> e) {
        values = e.getEnumConstants();
    }

    public T getRandomEnum() {
        return values[PRNG.nextInt(values.length)];
    }
}
