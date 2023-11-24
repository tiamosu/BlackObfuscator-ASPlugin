package top.niunaijun.blackobfuscator.core;

import java.util.UUID;

public class CommonUtil {

    public static String getRandomString() {
        String s = UUID.randomUUID().toString();
        return s.substring(0, 8);
    }
}
