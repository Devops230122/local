package com.alshaya.microservice.com.alshaya.microservice.localization.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public final class CollectionUtils {

    private CollectionUtils() {}

    public static <K, V> Map<K, V> zipToMap(final List<K> keys, final List<V> values) {
        return IntStream.range(0, keys.size()).boxed()
            .collect(HashMap::new, (m, index) -> m.put(keys.get(index), values.get(index)),
                HashMap::putAll);
    }
}
