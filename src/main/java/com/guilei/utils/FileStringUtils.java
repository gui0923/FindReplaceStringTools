package com.guilei.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * 文件操作类
 */
public class FileStringUtils {

    /**
     * 获取文件的前缀名
     * @param file
     * @return
     */
    public static String getFileSuffix(File file) {
        return getFileSuffix(file.getAbsolutePath());
    }

    public static String getFileSuffix(String fileName) {
        return StringUtils.substringAfterLast(fileName, ".");
    }
}
