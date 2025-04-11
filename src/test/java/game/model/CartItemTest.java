package game.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CartItemTest {

  @Test
  public void testCartItemConstructorAndGetters() {
    // Given
    CartItem cartItem = new CartItem();
    Long id = 1L;
    Cart cart = new Cart();
    Long gameId = 101L;
    String gameName = "Test Game";
    Double price = 29.99;
    Integer quantity = 2;

    // When
    cartItem.setId(id);
    cartItem.setCart(cart);
    cartItem.setGameId(gameId);
    cartItem.setGameName(gameName);
    cartItem.setPrice(price);
    cartItem.setQuantity(quantity);

    // Then
    assertEquals(id, cartItem.getId());
    assertEquals(cart, cartItem.getCart());
    assertEquals(gameId, cartItem.getGameId());
    assertEquals(gameName, cartItem.getGameName());
    assertEquals(price, cartItem.getPrice());
    assertEquals(quantity, cartItem.getQuantity());
  }

  @Test
  public void testDefaultQuantity() {
    // Given
    CartItem cartItem = new CartItem();

    // When & Then
    assertEquals(1, cartItem.getQuantity());
  }

  @Test
  public void testSettersAndGetters() {
    // Given
    CartItem cartItem = new CartItem();

    // When & Then
    // Test id
    Long id = 2L;
    cartItem.setId(id);
    assertEquals(id, cartItem.getId());

    // Test cart
    Cart cart = new Cart();
    cart.setId(3L);
    cartItem.setCart(cart);
    assertEquals(cart, cartItem.getCart());

    // Test gameId
    Long gameId = 102L;
    cartItem.setGameId(gameId);
    assertEquals(gameId, cartItem.getGameId());

    // Test gameName
    String gameName = "Updated Game";
    cartItem.setGameName(gameName);
    assertEquals(gameName, cartItem.getGameName());

    // Test price
    Double price = 39.99;
    cartItem.setPrice(price);
    assertEquals(price, cartItem.getPrice());

    // Test quantity
    Integer quantity = 3;
    cartItem.setQuantity(quantity);
    assertEquals(quantity, cartItem.getQuantity());
  }
}