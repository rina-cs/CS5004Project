package game.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileStorageService {
  private final ObjectMapper objectMapper;
  private final String DATA_DIR = "data";

  public FileStorageService() {
    this.objectMapper = new ObjectMapper();
    createDataDirectoryIfNotExists();
  }

  private void createDataDirectoryIfNotExists() {
    File directory = new File(DATA_DIR);
    if (!directory.exists()) {
      directory.mkdirs();
      System.out.println("Data directory created: " + DATA_DIR);
    }
  }

  public <T> List<T> readList(String filename, Class<T> valueType) {
    Path filePath = Paths.get(DATA_DIR, filename);
    if (!Files.exists(filePath)) {
      return new ArrayList<>();
    }

    try {
      CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, valueType);
      return objectMapper.readValue(filePath.toFile(), listType);
    } catch (IOException e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  public <T> void writeList(String filename, List<T> data) {
    Path filePath = Paths.get(DATA_DIR, filename);
    try {
      objectMapper.writeValue(filePath.toFile(), data);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Error writing to file: " + filename, e);
    }
  }

  public <T> void saveObject(String filename, T object) {
    Path filePath = Paths.get(DATA_DIR, filename);
    try {
      objectMapper.writeValue(filePath.toFile(), object);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Error writing object to file: " + filename, e);
    }
  }

  public <T> T readObject(String filename, Class<T> valueType) {
    Path filePath = Paths.get(DATA_DIR, filename);
    if (!Files.exists(filePath)) {
      return null;
    }

    try {
      return objectMapper.readValue(filePath.toFile(), valueType);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}