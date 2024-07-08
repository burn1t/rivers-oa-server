package com.ocean.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class DataFilterUtils {

    public static List<Map<String, Object>> listDistinctByMapValue(List<Map<String, Object>> list, String key) {
        List<Map<String, Object>> result = new ArrayList<>();
        HashSet<Object> set = new HashSet<>();
        list.forEach(item -> {
            if (!set.contains(item.get(key))) {
                result.add(item);
                set.add(item.get(key));
            }
        });
        return result;
    }
}
