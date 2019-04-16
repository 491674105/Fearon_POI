package com.fearon.util.poi.util;

/**
 * @description: 数据类型处理
 * @author: Fearon
 * @create: 2019-04-11 10:41
 **/
public class ClassHandleUtil {
    /**
     * 强转未经检测的数据类型
     * @param object
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T castTheUnknownClass(Object object){
        try{
            return (T) object;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
