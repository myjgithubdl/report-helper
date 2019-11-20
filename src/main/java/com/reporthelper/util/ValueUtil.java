package com.reporthelper.util;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Myron
 */
public class ValueUtil {

    public static String getString(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public static List<String> getStringList(Object value, String split) {
        if (value == null) {
            return null;
        }
        if (split == null) {
            split = ",";
        }
        List<String> list = new ArrayList<>();
        if (value instanceof String) {
            list.addAll(Arrays.asList(value.toString().split(split)));
        } else if (value instanceof List) {
            List<Object> ol = (List<Object>) value;
            ol.forEach(v -> {
                if (v != null) {
                    list.add(ol.toString());
                }
            });
        }
        return list;
    }

    public static Integer getInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (NumberUtils.isCreatable(value.toString())) {
            return NumberUtils.toInt(value.toString());
        }
        return null;
    }

    public static List<Integer> getIntegerList(Object value, String split) {
        if (value == null) {
            return null;
        }
        if (split == null) {
            split = ",";
        }
        List<Integer> list = new ArrayList<>();
        if (value instanceof String) {
            String[] strArray = value.toString().split(split);
            for (String s : strArray) {
                if (NumberUtils.isCreatable(s)) {
                    list.add(NumberUtils.toInt(s));
                }
            }
        } else if (value instanceof List) {
            List<Object> ol = (List<Object>) value;
            ol.forEach(v -> {
                if (v != null) {
                    if (NumberUtils.isCreatable(ol.toString())) {
                        list.add(NumberUtils.toInt(ol.toString()));
                    }
                }
            });
        }
        return list;
    }

    public static Double getDouble(Object value) {
        if (value == null) {
            return null;
        }

        if (NumberUtils.isCreatable(value.toString())) {
            return NumberUtils.toDouble(value.toString());
        }
        return null;
    }

    public static List<Double> getDoubleList(Object value, String split) {
        if (value == null) {
            return null;
        }
        if (split == null) {
            split = ",";
        }
        List<Double> list = new ArrayList<>();
        if (value instanceof String) {
            String[] strArray = value.toString().split(split);
            for (String s : strArray) {
                if (NumberUtils.isCreatable(s)) {
                    list.add(NumberUtils.toDouble(s));
                }
            }
        } else if (value instanceof List) {
            List<Object> ol = (List<Object>) value;
            ol.forEach(v -> {
                if (v != null) {
                    if (NumberUtils.isCreatable(ol.toString())) {
                        list.add(NumberUtils.toDouble(ol.toString()));
                    }
                }
            });
        }
        return list;
    }

}
