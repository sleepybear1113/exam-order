package cn.sleepybear.examorder.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/13 10:07
 */
@Slf4j
public class Util {

    public static <T> T parseJsonToObject(String s, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return parseJsonToObject(s, clazz, mapper);
    }

    public static <T> T parseJsonToObject(String s, TypeReference<T> valueTypeRef) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return parseJsonToObject(s, valueTypeRef, mapper);
    }

    public static <T> T parseJsonToObject(String s, Class<T> clazz, ObjectMapper mapper) {
        try {
            return mapper.readValue(s, clazz);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return null;
        }
    }

    public static <T> T parseJsonToObject(String s, TypeReference<T> valueTypeRef, ObjectMapper mapper) {
        try {
            return mapper.readValue(s, valueTypeRef);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return null;
        }
    }

    public static <T> T readFile(String path, Class<T> clazz) {
        try {
            String s = readFile(path);
            return parseJsonToObject(s, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T readFile(String path, TypeReference<T> valueTypeRef) {
        try {
            String s = readFile(path);
            if (StringUtils.isBlank(s)) {
                return null;
            }
            return parseJsonToObject(s, valueTypeRef);
        } catch (Exception e) {
            return null;
        }
    }

    public static String readFile(String path) {
        StringBuilder s = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path)), StandardCharsets.UTF_8))) {
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                s.append(line).append(System.lineSeparator());
            }
            return s.toString();
        } catch (IOException e) {
            return null;
        }
    }


    public static <T> void writeFile(T t, String path) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(t);
            writeFile(result, path);
        } catch (JsonProcessingException ignored) {
        }
    }

    public static void writeFile(String s, String path) {
        if (StringUtils.isBlank(path)) {
            return;
        }
        ensureParentDir(path);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(path)), StandardCharsets.UTF_8))) {
            bufferedWriter.write(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void ensureParentDir(String filename) {
        File file = new File(filename);
        File parentFile = file.getParentFile();
        if (parentFile == null) {
            return;
        }
        if (!parentFile.exists()) {
            if (!parentFile.mkdirs()) {
                log.info("创建文件夹 {} 失败", parentFile);
            }
        }
    }

    public static String getRandomStr(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int random = (int) (Math.random() * 62);
            if (random < 10) {
                sb.append(random);
            } else if (random < 36) {
                sb.append((char) (random + 55));
            } else {
                sb.append((char) (random + 61));
            }
        }
        return sb.toString();
    }

    public static String bytesToMd5(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try {
            // 创建 MessageDigest 实例并指定算法为 MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 将字节数组传递给 MessageDigest 更新
            md.update(bytes);

            // 计算哈希值并获取结果字节数组
            byte[] digest = md.digest();

            // 将结果字节数组转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                // 使用 "%02x" 格式将每个字节转换为两位十六进制数
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("无法找到 MD5 算法");
            return null;
        }
    }

    public static String getTime() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        int second = Calendar.getInstance().get(Calendar.SECOND);
        int millisecond = Calendar.getInstance().get(Calendar.MILLISECOND);
        return "%s-%02d-%02d_%02d-%02d-%02d.%03d".formatted(year, month, day, hour, minute, second, millisecond);
    }

    public static String getFileSize(Long size) {
        if (size == null) {
            return "null";
        }
        if (size < 1024) {
            return size + "B";
        }
        if (size < 1024 * 1024) {
            return String.format("%.2fKB", size / 1024.0);
        }
        if (size < 1024 * 1024 * 1024) {
            return String.format("%.2fMB", size / 1024.0 / 1024.0);
        }
        return String.format("%.2fGB", size / 1024.0 / 1024.0 / 1024.0);
    }
}
