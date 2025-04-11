package game.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

  @Test
  public void testGameConstructorAndGetters() {
    // Given
    Game game = new Game();
    Long id = 1L;
    String name = "Test Game";
    int likes = 100;
    double price = 29.99;
    String image = "/path/to/image.jpg";

    // When
    game.setId(id);
    game.setName(name);
    game.setLikes(likes);
    game.setPrice(price);
    game.setImage(image);

    // Then
    assertEquals(id, game.getId());
    assertEquals(name, game.getName());
    assertEquals(likes, game.getLikes());
    assertEquals(price, game.getPrice(), 0.001);
    assertEquals(image, game.getImage());
  }

  @Test
  public void testSettersAndGetters() {
    // Given
    Game game = new Game();

    // When & Then
    // Test id
    Long id = 2L;
    game.setId(id);
    assertEquals(id, game.getId());

    // Test name
    String name = "Updated Game";
    game.setName(name);
    assertEquals(name, game.getName());

    // Test likes
    int likes = 200;
    game.setLikes(likes);
    assertEquals(likes, game.getLikes());

    // Test price
    double price = 39.99;
    game.setPrice(price);
    assertEquals(price, game.getPrice(), 0.001);

    // Test image
    String image = "/updated/path/to/image.jpg";
    game.setImage(image);
    assertEquals(image, game.getImage());
  }
}