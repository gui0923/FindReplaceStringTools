package com.guilei.file;

import com.guilei.utils.FileStringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * 在当前普遍都实现前后端分离的情况下，前后端都会存在大量的配置文件，这其中以前端的修改配置最为恶心
 * 由于Vue等高级前端框架打包之后，文件都被完全序列化成低级的序列化语言，这时候通过原始的vim命令进行替换就会非常恶心
 * 此简单的工具类就是提供给服务器操作人员以一种简单的方式，从文件中查找指定的字符串，如果找到，打印到控制台，或者采取其他的命令给替换
 * @since 2019/12/11
 * @author guilei
 */
public class Main {
    public static void main(String[] args) throws IOException {
        if (args != null && args.length > 0) {
            File file = new File(args[0]);
            int mode = Integer.parseInt(args[1]);
            Properties p = new Properties();
            p.load(ClassLoader.getSystemResourceAsStream("allowdeny.properties"));
            String allow = p.getProperty("allow","");
            String deny = p.getProperty("deny","");
            if (mode == 0) {
                findStrings(file, args[2], allow, deny);
            } else {
                replaceFiles(file, args[2], args[3], allow, deny);

            }
        }
    }

    private static void findStrings(File file, String key, String allow, String deny) throws IOException {
        if (file.isFile()) {
            if (StringUtils.isNotBlank(deny)) {
                if (StringUtils.contains(deny, FileStringUtils.getFileSuffix(file))) {
                    return;
                }
            }
            if (StringUtils.isNotBlank(allow)) {
                if (!StringUtils.contains(allow, FileStringUtils.getFileSuffix(file))) {
                    return;
                }
            }
            checkFoundKeys(file, key);
        } else {
            File[] files = file.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    findStrings(file1, key, allow, deny);
                }
            }
        }
    }

    private static void replaceFiles(File file, String key, String value, String allow, String deny) throws IOException {
        if (file.isFile()) {
            if (StringUtils.isNotBlank(deny)) {
                if (StringUtils.contains(deny, StringUtils.substringAfterLast(file.getName(), "."))) {
                    return;
                }
            }
            if (StringUtils.isNotBlank(allow)) {
                if (!StringUtils.contains(allow, StringUtils.substringAfterLast(file.getName(), "."))) {
                    return;
                }
            }
            if (checkFoundKeys(file, key)) {
                rewriteFile(file, key, value);
            }
        } else {
            File[] files = file.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    replaceFiles(file1, key, value, allow, deny);
                }
            }
        }
    }

    /**
     * 查找指定的字符串，如果找到了，就会把文件名和找到的字符串的前后5个字节打印出来
     * @param file
     * @param key
     * @return
     * @throws IOException
     */
    private static boolean checkFoundKeys(File file, String key) throws IOException {
        List<String> lines = FileUtils.readLines(file, "UTF-8");
        boolean isFound = false;
        for (String line : lines) {
            if (StringUtils.contains(line, key)) {
                int index = line.indexOf(key);
                int start = Math.max(0,  index - 5);
                int end = Math.min(line.length(), index + key.length() + 5);
                System.out.println(file + ", " + StringUtils.substring(line, start, end));
                isFound = true;
            }
        }
        return isFound;
    }

    /**
     * 读取文件内容并替换字符串，然后重写回原文件
     * @param file
     * @param key
     * @param value
     * @throws IOException
     */
    private static void rewriteFile(File file, String key, String value) throws IOException {
        String string = FileUtils.readFileToString(file, "UTF-8");
        string = StringUtils.replace(string, key, value);
        FileUtils.write(file, string, "UTF-8");
    }
}
