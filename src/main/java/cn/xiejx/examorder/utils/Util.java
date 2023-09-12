package cn.xiejx.examorder.utils;

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
                s.append(line).append(System.getProperty("line.separator"));
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
}
