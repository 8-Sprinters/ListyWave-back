package com.listywave.common.util;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class DataUpdateUtils {

    public static <T> void update(List<T> before, List<T> after) {
        Set<T> removable = new LinkedHashSet<>(before);
        after.forEach(removable::remove);
        before.removeAll(removable);

        Set<T> addable = new LinkedHashSet<>(after);
        before.forEach(addable::remove);
        before.addAll(addable);
    }
}
