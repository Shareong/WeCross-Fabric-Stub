package com.webank.wecross.account;

import com.moandjiezana.toml.Toml;
import org.apache.tomcat.util.descriptor.web.WebXml;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FabricConfigUtils {

    public static Toml readToml(String fileName) throws Exception {
        try {
            if (!fileIsExists(fileName)) {
                throw new Exception(fileName + " does not exists");
            }

            PathMatchingResourcePatternResolver resolver =
                    new PathMatchingResourcePatternResolver();
            Path path = Paths.get(resolver.getResource(fileName).getURI());
            String encryptedSecret = new String(Files.readAllBytes(path));
            return new Toml().read(encryptedSecret);
        } catch (Exception e) {
            throw new Exception("Read toml file error: " + e.getMessage());
        }
    }

    public static Map<String, Object> readTomlMap(String fileName) throws Exception {
        return readToml(fileName).toMap();
    }

    // Check if the file exists or not
    public static boolean fileIsExists(String path) {
        try {
            PathMatchingResourcePatternResolver resolver_temp =
                    new PathMatchingResourcePatternResolver();
            resolver_temp.getResource(path).getFile();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}