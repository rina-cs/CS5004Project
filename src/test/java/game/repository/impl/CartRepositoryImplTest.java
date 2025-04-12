package game.repository.impl;

import game.model.Cart;
import game.model.CartItem;
import game.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartRepositoryImplTest {

  private CartRepositoryImpl cartRepository;

  @Mock
  private FileStorageService fileStorageService;

  private List<Cart> testCarts;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    testCarts = new ArrayList<>();

    Cart cart1 = new Cart();
    cart1.setId(1L);
    cart1.setUserId(101L);
    cart1.setItems(new ArrayList<>());

    CartItem item1 = new CartItem();
    item1.setId(1001L);
    item1.setGameId(501L);
    item1.setGameName("Test Game 1");
    item1.setPrice(19.99);
    item1.setQuantity(1);
    item1.setCart(cart1);

    cart1.getItems().add(item1);

    Cart cart2 = new Cart();
    cart2.setId(2L);
    cart2.setUserId(102L);
    cart2.setItems(new ArrayList<>());

    CartItem item2 = new CartItem();
    item2.setId(1002L);
    item2.setGameId(502L);
    item2.setGameName("Test Game 2");
    item2.setPrice(29.99);
    item2.setQuantity(2);
    item2.setCart(cart2);

    cart2.getItems().add(item2);

    testCarts.add(cart1);
    testCarts.add(cart2);

    when(fileStorageService.readList(eq("carts.json"), eq(Cart.class))).thenReturn(testCarts);

    cartRepository = new CartRepositoryImpl(fileStorageService);
  }

  @Test
  void findAll() {
    List<Cart> carts = cartRepository.findAll();

    assertEquals(2, carts.size());
    assertEquals(101L, carts.get(0).getUserId());
    assertEquals(102L, carts.get(1).getUserId());
    verify(fileStorageService, times(2)).readList("carts.json", Cart.class);
  }

  @Test
  void findById() {
    Optional<Cart> cart = cartRepository.findById(1L);

    assertTrue(cart.isPresent());
    assertEquals(101L, cart.get().getUserId());
    assertEquals(1, cart.get().getItems().size());
    assertEquals("Test Game 1", cart.get().getItems().get(0).getGameName());

    Optional<Cart> nonExistentCart = cartRepository.findById(3L);
    assertFalse(nonExistentCart.isPresent());
  }

  @Test
  void findByUserId() {
    Optional<Cart> cart = cartRepository.findByUserId(101L);

    assertTrue(cart.isPresent());
    assertEquals(1L, cart.get().getId());
    assertEquals(1, cart.get().getItems().size());

    Optional<Cart> nonExistentCart = cartRepository.findByUserId(103L);
    assertFalse(nonExistentCart.isPresent());
  }

  @Test
  void save_newCart() {
    Cart newCart = new Cart();
    newCart.setUserId(103L);
    newCart.setItems(new ArrayList<>());

    CartItem newItem = new CartItem();
    newItem.setGameId(503L);
    newItem.setGameName("New Game");
    newItem.setPrice(39.99);
    newItem.setQuantity(1);
    newItem.setCart(newCart);

    newCart.getItems().add(newItem);

    Cart savedCart = cartRepository.save(newCart);

    assertNotNull(savedCart.getId());
    assertEquals(103L, savedCart.getUserId());
    assertEquals(1, savedCart.getItems().size());
    assertNotNull(savedCart.getItems().get(0).getId());

    verify(fileStorageService).writeList(eq("carts.json"), any(List.class));
  }

  @Test
  void save_existingCart() {
    Cart existingCart = testCarts.get(0);

    CartItem newItem = new CartItem();
    newItem.setGameId(504L);
    newItem.setGameName("Another Game");
    newItem.setPrice(49.99);
    newItem.setQuantity(3);
    newItem.setCart(existingCart);

    existingCart.getItems().add(newItem);

    Cart updatedCart = cartRepository.save(existingCart);

    assertEquals(1L, updatedCart.getId());
    assertEquals(2, updatedCart.getItems().size());
    assertNotNull(updatedCart.getItems().get(1).getId());

    verify(fileStorageService).writeList(eq("carts.json"), any(List.class));
  }

  @Test
  void deleteById() {
    cartRepository.deleteById(1L);

    verify(fileStorageService).writeList(eq("carts.json"), any(List.class));
  }

  @Test
  void count() {
    long count = cartRepository.count();

    assertEquals(2, count);
  }

  @Test
  void idGeneratorsInitialization() {
    Cart newCart = new Cart();
    newCart.setUserId(104L);
    newCart.setItems(new ArrayList<>());

    CartItem newItem = new CartItem();
    newItem.setGameId(505L);
    newItem.setGameName("Test Generator");
    newItem.setPrice(59.99);
    newItem.setQuantity(1);
    newItem.setCart(newCart);

    newCart.getItems().add(newItem);

    Cart savedCart = cartRepository.save(newCart);

    assertTrue(savedCart.getId() > 2);
    assertTrue(savedCart.getItems().get(0).getId() > 1002);
  }
}