package com.qi.backend.util;

import org.springframework.beans.BeanUtils;

/**
 * 对象拷贝工具类，用来转换的
 */
public class BenCopyUtil {
    /**
     * 对象拷贝
     * @param source 源对象（如UserDO）
     * @param clazz  目标类（如UserVO.class）
     * @return 目标对象
     */
    public static <T> T copy(Object source, Class<T> clazz) {
        if (source == null || clazz == null) {
            return null;
        }
        T target = BeanUtils.instantiateClass(clazz);
        BeanUtils.copyProperties(source, target);
        return target;
    }
}
