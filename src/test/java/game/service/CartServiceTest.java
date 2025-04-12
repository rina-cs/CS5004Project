package game.service;

import game.model.Cart;
import game.model.CartItem;
import game.model.Game;
import game.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

  private CartService cartService;

  @Mock
  private CartRepository cartRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    cartService = new CartService();

    try {
      java.lang.reflect.Field field = CartService.class.getDeclaredField("cartRepository");
      field.setAccessible(true);
      field.set(cartService, cartRepository);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void findByUserId() {
    Cart cart = new Cart();
    cart.setId(1L);
    cart.setUserId(101L);

    when(cartRepository.findByUserId(101L)).thenReturn(Optional.of(cart));
    when(cartRepository.findByUserId(102L)).thenReturn(Optional.empty());

    Optional<Cart> found = cartService.findByUserId(101L);
    Optional<Cart> notFound = cartService.findByUserId(102L);

    assertTrue(found.isPresent());
    assertEquals(1L, found.get().getId());
    assertFalse(notFound.isPresent());

    verify(cartRepository).findByUserId(101L);
    verify(cartRepository).findByUserId(102L);
  }

  @Test
  void save() {
    Cart cartToSave = new Cart();
    cartToSave.setUserId(101L);

    Cart savedCart = new Cart();
    savedCart.setId(1L);
    savedCart.setUserId(101L);

    when(cartRepository.save(any(Cart.class))).thenReturn(savedCart);

    Cart result = cartService.save(cartToSave);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(101L, result.getUserId());

    verify(cartRepository).save(cartToSave);
  }

  @Test
  void getOrCreateCartForUser_existingCart() {
    Cart existingCart = new Cart();
    existingCart.setId(1L);
    existingCart.setUserId(101L);

    when(cartRepository.findByUserId(101L)).thenReturn(Optional.of(existingCart));

    Cart result = cartService.getOrCreateCartForUser(101L);

    assertEquals(1L, result.getId());
    assertEquals(101L, result.getUserId());

    verify(cartRepository).findByUserId(101L);
    verify(cartRepository, never()).save(any(Cart.class));
  }

  @Test
  void getOrCreateCartForUser_newCart() {
    when(cartRepository.findByUserId(102L)).thenReturn(Optional.empty());

    Cart newCart = new Cart();
    newCart.setId(2L);
    newCart.setUserId(102L);

    when(cartRepository.save(any(Cart.class))).thenReturn(newCart);

    Cart result = cartService.getOrCreateCartForUser(102L);

    assertEquals(2L, result.getId());
    assertEquals(102L, result.getUserId());
    assertNotNull(result.getItems());

    verify(cartRepository).findByUserId(102L);
    verify(cartRepository).save(any(Cart.class));
  }

  @Test
  void addItemToCart_newItem() {
    Cart cart = new Cart();
    cart.setId(1L);
    cart.setUserId(101L);
    cart.setItems(new ArrayList<>());

    Game game = new Game();
    game.setId(201L);
    game.setName("Test Game");
    game.setPrice(19.99);

    when(cartRepository.findByUserId(101L)).thenReturn(Optional.of(cart));
    when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Cart updatedCart = cartService.addItemToCart(101L, game);

    assertEquals(1, updatedCart.getItems().size());
    assertEquals(201L, updatedCart.getItems().get(0).getGameId());
    assertEquals("Test Game", updatedCart.getItems().get(0).getGameName());
    assertEquals(19.99, updatedCart.getItems().get(0).getPrice());

    verify(cartRepository).findByUserId(101L);
    verify(cartRepository).save(any(Cart.class));
  }

  @Test
  void addItemToCart_existingItem() {
    Cart cart = new Cart();
    cart.setId(1L);
    cart.setUserId(101L);

    CartItem existingItem = new CartItem();
    existingItem.setGameId(201L);
    existingItem.setGameName("Test Game");
    existingItem.setPrice(19.99);

    cart.setItems(new ArrayList<>());
    cart.getItems().add(existingItem);

    Game game = new Game();
    game.setId(201L);
    game.setName("Test Game");
    game.setPrice(19.99);

    when(cartRepository.findByUserId(101L)).thenReturn(Optional.of(cart));

    Cart result = cartService.addItemToCart(101L, game);

    assertEquals(1, result.getItems().size()); // No new item should be added

    verify(cartRepository).findByUserId(101L);
    verify(cartRepository, never()).save(any(Cart.class));
  }

  @Test
  void removeItemFromCart() {
    Cart cart = new Cart();
    cart.setId(1L);
    cart.setUserId(101L);

    CartItem item1 = new CartItem();
    item1.setGameId(201L);

    CartItem item2 = new CartItem();
    item2.setGameId(202L);

    cart.setItems(new ArrayList<>());
    cart.getItems().add(item1);
    cart.getItems().add(item2);

    when(cartRepository.findByUserId(101L)).thenReturn(Optional.of(cart));
    when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Cart updatedCart = cartService.removeItemFromCart(101L, 201L);

    assertEquals(1, updatedCart.getItems().size());
    assertEquals(202L, updatedCart.getItems().get(0).getGameId());

    verify(cartRepository).findByUserId(101L);
    verify(cartRepository).save(any(Cart.class));
  }

  @Test
  void removeItemFromCart_cartNotFound() {
    when(cartRepository.findByUserId(101L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> {
      cartService.removeItemFromCart(101L, 201L);
    });

    verify(cartRepository).findByUserId(101L);
    verify(cartRepository, never()).save(any(Cart.class));
  }

  @Test
  void clearCart() {
    Cart cart = new Cart();
    cart.setId(1L);
    cart.setUserId(101L);

    CartItem item = new CartItem();
    item.setGameId(201L);

    cart.setItems(new ArrayList<>());
    cart.getItems().add(item);

    when(cartRepository.findByUserId(101L)).thenReturn(Optional.of(cart));
    when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

    cartService.clearCart(101L);

    assertTrue(cart.getItems().isEmpty());

    verify(cartRepository).findByUserId(101L);
    verify(cartRepository).save(cart);
  }

  @Test
  void clearCart_cartNotFound() {
    when(cartRepository.findByUserId(101L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> {
      cartService.clearCart(101L);
    });

    verify(cartRepository).findByUserId(101L);
    verify(cartRepository, never()).save(any(Cart.class));
  }

  @Test
  void getCartTotal() {
    Cart cart = new Cart();
    cart.setId(1L);
    cart.setUserId(101L);

    CartItem item1 = new CartItem();
    item1.setPrice(10.0);

    CartItem item2 = new CartItem();
    item2.setPrice(20.0);

    cart.setItems(new ArrayList<>());
    cart.getItems().add(item1);
    cart.getItems().add(item2);

    when(cartRepository.findByUserId(101L)).thenReturn(Optional.of(cart));

    double total = cartService.getCartTotal(101L);

    assertEquals(30.0, total);

    verify(cartRepository).findByUserId(101L);
  }

  @Test
  void getCartTotal_cartNotFound() {
    when(cartRepository.findByUserId(101L)).thenReturn(Optional.empty());

    double total = cartService.getCartTotal(101L);

    assertEquals(0.0, total);

    verify(cartRepository).findByUserId(101L);
  }
}
