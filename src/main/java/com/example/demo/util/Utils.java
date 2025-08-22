package com.example.demo.util;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static Map<Integer, String> languageCodes = new HashMap<>();
    static {
        languageCodes.put(0, "hy-AM");
        languageCodes.put(1, "en-US");
        languageCodes.put(2, "ru-RU");
        languageCodes.put(3, "az-AZ");
        languageCodes.put(4, "ka-GE");
        languageCodes.put(5, "tr-TR");
        languageCodes.put(6, "es-ES");
        languageCodes.put(7, "fr-FR");
    }
}
