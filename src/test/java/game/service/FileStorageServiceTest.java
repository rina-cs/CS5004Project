package game.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FileStorageServiceTest {

  @TempDir
  Path tempDir;

  @Spy
  private ObjectMapper objectMapper = new ObjectMapper();

  @InjectMocks
  private FileStorageService fileStorageService;

  private File dataDir;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    dataDir = tempDir.resolve("data").toFile();
    if (!dataDir.exists()) {
      dataDir.mkdirs();
    }

    // Use reflection to set the DATA_DIR field to our temp directory
    try {
      java.lang.reflect.Field field = FileStorageService.class.getDeclaredField("DATA_DIR");
      field.setAccessible(true);
      field.set(fileStorageService, dataDir.getPath());
    } catch (Exception e) {
      fail("Failed to set data directory: " + e.getMessage());
    }
  }

  @Test
  public void testReadListEmptyFile() {
    // When
    List<TestObject> result = fileStorageService.readList("nonexistent.json", TestObject.class);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testWriteAndReadList() throws IOException {
    // Given
    String filename = "test-list.json";
    List<TestObject> testObjects = Arrays.asList(
        new TestObject(1L, "Test 1"),
        new TestObject(2L, "Test 2")
    );

    // When
    fileStorageService.writeList(filename, testObjects);
    List<TestObject> result = fileStorageService.readList(filename, TestObject.class);

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(1L, result.get(0).getId());
    assertEquals("Test 1", result.get(0).getName());
    assertEquals(2L, result.get(1).getId());
    assertEquals("Test 2", result.get(1).getName());
  }

  @Test
  public void testSaveAndReadObject() throws IOException {
    // Given
    String filename = "test-object.json";
    TestObject testObject = new TestObject(1L, "Test Object");

    // When
    fileStorageService.saveObject(filename, testObject);
    TestObject result = fileStorageService.readObject(filename, TestObject.class);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("Test Object", result.getName());
  }

  @Test
  public void testReadObjectNonexistent() {
    // When
    TestObject result = fileStorageService.readObject("nonexistent.json", TestObject.class);

    // Then
    assertNull(result);
  }

  // Test helper class
  public static class TestObject {
    private Long id;
    private String name;

    public TestObject() {
    }

    public TestObject(Long id, String name) {
      this.id = id;
      this.name = name;
    }

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }
}