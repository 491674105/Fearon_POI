package com.fearon.util.string;

import java.util.UUID;

/**
 * @description: 字符串处理工具
 * @author: Fearon
 * @create: 2019-04-01 10:08
 **/
public class StringUtil {
    /**
     * 字符串是否存在，字符串中是否有具体数据
     *
     * @param source
     * @return
     */
    public static boolean isNotEmpty(String source) {
        return !isEmpty(source);
    }

    public static boolean isEmpty(String source) {
        return null == source || source.trim().length() <= 0;
    }

    /**
     * 字符串是否存在
     *
     * @param source
     * @return
     */
    public static boolean isNotBlank(String source) {
        return !isBlank(source);
    }

    public static boolean isBlank(String source) {
        return null == source;
    }

    /**
     * 字符串快速拼接
     *
     * @param params
     * @return
     */
    public static String contact(String... params) {
        int length = params.length;
        if (length <= 0)
            return null;

        StringBuilder result = new StringBuilder();
        for (String param : params) {
            if (null != param)
                result.append(param);
        }

        return result.length() > 0 ? result.toString() : null;
    }

    /**
     * 生成32位UUID
     * @return
     */
    public static String getUUID32(String prefix, String suffix) {
        StringBuilder uuid = new StringBuilder();
        if(isNotEmpty(prefix))
            uuid.append(prefix);

        uuid.append(UUID.randomUUID().toString().replace("-", ""));

        if(isNotEmpty(suffix))
            uuid.append(suffix);

        return uuid.toString().toLowerCase();
    }
}
