package game.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CartTest {

  private Cart cart;
  private CartItem item1;
  private CartItem item2;

  @BeforeEach
  public void setUp() {
    cart = new Cart();
    cart.setId(1L);
    cart.setUserId(1L);

    item1 = new CartItem();
    item1.setId(1L);
    item1.setGameId(101L);
    item1.setGameName("Game 1");
    item1.setPrice(29.99);
    item1.setQuantity(1);
    item1.setCart(cart);

    item2 = new CartItem();
    item2.setId(2L);
    item2.setGameId(102L);
    item2.setGameName("Game 2");
    item2.setPrice(19.99);
    item2.setQuantity(1);
    item2.setCart(cart);
  }

  @Test
  public void testCartConstructorAndGetters() {
    // Given
    List<CartItem> items = Arrays.asList(item1, item2);

    // When
    cart.setItems(items);

    // Then
    assertEquals(1L, cart.getId());
    assertEquals(1L, cart.getUserId());
    assertEquals(2, cart.getItems().size());
    assertEquals(item1, cart.getItems().get(0));
    assertEquals(item2, cart.getItems().get(1));
  }

  @Test
  public void testGetTotal() {
    // Given
    List<CartItem> items = Arrays.asList(item1, item2);
    cart.setItems(items);

    // When
    Double total = cart.getTotal();

    // Then
    assertEquals(49.98, total, 0.01);
  }

  @Test
  public void testGetTotalWithEmptyCart() {
    // Given
    cart.setItems(new ArrayList<>());

    // When
    Double total = cart.getTotal();

    // Then
    assertEquals(0.0, total, 0.01);
  }

  @Test
  public void testContainsGame() {
    // Given
    List<CartItem> items = Arrays.asList(item1, item2);
    cart.setItems(items);

    // When & Then
    assertTrue(cart.containsGame(101L));
    assertTrue(cart.containsGame(102L));
    assertFalse(cart.containsGame(103L));
  }

  @Test
  public void testClear() {
    // Given
    List<CartItem> items = new ArrayList<>(Arrays.asList(item1, item2));
    cart.setItems(items);
    assertEquals(2, cart.getItems().size());

    // When
    cart.clear();

    // Then
    assertTrue(cart.getItems().isEmpty());
  }
}