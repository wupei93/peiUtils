package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertiesUtil {
    private static Properties properties = new Properties();
    static {
        try (InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("app.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("加载app.properties失败");
        }
    }

    public static String get(String key){
        return properties.getProperty(key);
    }
}
