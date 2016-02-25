package pl.edu.pw.appt.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 *
 * @author bestp
 */
public class FileHandler {
    
    public static String getFile(Path filePath) throws IOException {
        Path file = filePath;
        InputStream res = null; 
        if(file.toFile().exists()) {
            byte[] encoded = Files.readAllBytes(file);
            return new String(encoded, StandardCharsets.UTF_8);
        } else {
            return null;
        }
    }
    
    public static void saveFile(Path filePath, String data) throws IOException {
        Path file = filePath;
        Files.write(filePath, data.getBytes(), StandardOpenOption.CREATE);
    }   
}
