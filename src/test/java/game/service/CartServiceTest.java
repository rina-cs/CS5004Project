package game.service;

import game.model.Cart;
import game.model.CartItem;
import game.model.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

  @Mock
  private FileStorageService fileStorageService;

  @InjectMocks
  private CartService cartService;

  private Cart cart1;
  private Cart cart2;
  private Game game1;
  private Game game2;
  private CartItem item1;
  private CartItem item2;
  private List<Cart> cartList;

  @BeforeEach
  public void setUp() {
    // Initialize games
    game1 = new Game();
    game1.setId(1L);
    game1.setName("Game 1");
    game1.setPrice(29.99);
    game1.setLikes(100);
    game1.setImage("/image1.jpg");

    game2 = new Game();
    game2.setId(2L);
    game2.setName("Game 2");
    game2.setPrice(19.99);
    game2.setLikes(200);
    game2.setImage("/image2.jpg");

    // Initialize cart items
    item1 = new CartItem();
    item1.setId(1L);
    item1.setGameId(game1.getId());
    item1.setGameName(game1.getName());
    item1.setPrice(game1.getPrice());
    item1.setQuantity(1);

    item2 = new CartItem();
    item2.setId(2L);
    item2.setGameId(game2.getId());
    item2.setGameName(game2.getName());
    item2.setPrice(game2.getPrice());
    item2.setQuantity(1);

    // Initialize carts
    cart1 = new Cart();
    cart1.setId(1L);
    cart1.setUserId(1L);
    cart1.setItems(new ArrayList<>(Arrays.asList(item1)));
    item1.setCart(cart1);

    cart2 = new Cart();
    cart2.setId(2L);
    cart2.setUserId(2L);
    cart2.setItems(new ArrayList<>(Arrays.asList(item2)));
    item2.setCart(cart2);

    cartList = Arrays.asList(cart1, cart2);
  }

  @Test
  public void testGetAllCarts() {
    // Given
    when(fileStorageService.readList(anyString(), eq(Cart.class))).thenReturn(cartList);

    // When
    List<Cart> result = cartService.getAllCarts();

    // Then
    assertEquals(2, result.size());
    assertEquals(cart1.getId(), result.get(0).getId());
    assertEquals(cart2.getId(), result.get(1).getId());
    verify(fileStorageService, atLeast(1)).readList("carts.json", Cart.class);
  }

  @Test
  public void testFindByUserId() {
    // Given
    when(fileStorageService.readList(anyString(), eq(Cart.class))).thenReturn(cartList);

    // When
    Optional<Cart> result = cartService.findByUserId(1L);

    // Then
    assertTrue(result.isPresent());
    assertEquals(cart1.getId(), result.get().getId());
    assertEquals(cart1.getUserId(), result.get().getUserId());
    verify(fileStorageService, atLeast(1)).readList("carts.json", Cart.class);
  }

  @Test
  public void testFindByUserIdNotFound() {
    // Given
    when(fileStorageService.readList(anyString(), eq(Cart.class))).thenReturn(cartList);

    // When
    Optional<Cart> result = cartService.findByUserId(3L);

    // Then
    assertFalse(result.isPresent());
    verify(fileStorageService, atLeast(1)).readList("carts.json", Cart.class);
  }

  @Test
  public void testSave() {
    // Given
    Cart newCart = new Cart();
    newCart.setUserId(3L);
    newCart.setItems(new ArrayList<>());

    List<Cart> mutableCartList = new ArrayList<>(cartList);
    when(fileStorageService.readList(anyString(), eq(Cart.class))).thenReturn(mutableCartList);
    doNothing().when(fileStorageService).writeList(anyString(), anyList());

    // When
    Cart result = cartService.save(newCart);

    // Then
    assertNotNull(result.getId());
    assertEquals(3L, result.getUserId());
    verify(fileStorageService, atLeast(1)).readList("carts.json", Cart.class);
    verify(fileStorageService, times(1)).writeList(eq("carts.json"), anyList());
  }

  @Test
  public void testGetOrCreateCartForUserExists() {
    // Given
    when(fileStorageService.readList(anyString(), eq(Cart.class))).thenReturn(cartList);

    // When
    Cart result = cartService.getOrCreateCartForUser(1L);

    // Then
    assertEquals(cart1.getId(), result.getId());
    assertEquals(cart1.getUserId(), result.getUserId());
    verify(fileStorageService, atLeast(1)).readList("carts.json", Cart.class);
  }

  @Test
  public void testGetOrCreateCartForUserNew() {
    // Given
    when(fileStorageService.readList(anyString(), eq(Cart.class))).thenReturn(cartList);
    doNothing().when(fileStorageService).writeList(anyString(), anyList());

    // When
    Cart result = cartService.getOrCreateCartForUser(3L);

    // Then
    assertNotNull(result.getId());
    assertEquals(3L, result.getUserId());
    verify(fileStorageService, atLeast(1)).readList("carts.json", Cart.class);
    verify(fileStorageService, times(1)).writeList(eq("carts.json"), anyList());
  }

  @Test
  public void testAddItemToCart() {
    // Given
    when(fileStorageService.readList(anyString(), eq(Cart.class))).thenReturn(cartList);
    doNothing().when(fileStorageService).writeList(anyString(), anyList());

    // When
    Cart result = cartService.addItemToCart(1L, game2);

    // Then
    assertEquals(2, result.getItems().size());
    assertTrue(result.containsGame(game2.getId()));
    verify(fileStorageService, atLeast(1)).readList("carts.json", Cart.class);
    verify(fileStorageService, times(1)).writeList(eq("carts.json"), anyList());
  }

  @Test
  public void testAddItemToCartAlreadyExists() {
    // Given
    when(fileStorageService.readList(anyString(), eq(Cart.class))).thenReturn(cartList);

    // When
    Cart result = cartService.addItemToCart(1L, game1);

    // Then
    assertEquals(1, result.getItems().size());
    verify(fileStorageService, atLeast(1)).readList("carts.json", Cart.class);
  }

  @Test
  public void testRemoveItemFromCart() {
    // Given
    List<Cart> mutableCartList = new ArrayList<>();
    mutableCartList.add(cart1);
    mutableCartList.add(cart2);

    when(fileStorageService.readList(anyString(), eq(Cart.class))).thenReturn(mutableCartList);
    doNothing().when(fileStorageService).writeList(anyString(), anyList());

    // When
    Cart result = cartService.removeItemFromCart(1L, game1.getId());

    // Then
    assertEquals(0, result.getItems().size());
    verify(fileStorageService, atLeast(1)).readList("carts.json", Cart.class);
    verify(fileStorageService, times(1)).writeList(eq("carts.json"), anyList());
  }

  @Test
  public void testClearCart() {
    // Given
    List<Cart> mutableCartList = new ArrayList<>();
    mutableCartList.add(cart1);
    mutableCartList.add(cart2);

    when(fileStorageService.readList(anyString(), eq(Cart.class))).thenReturn(mutableCartList);
    doNothing().when(fileStorageService).writeList(anyString(), anyList());

    // When
    cartService.clearCart(1L);

    // Then
    verify(fileStorageService, atLeast(1)).readList("carts.json", Cart.class);
    verify(fileStorageService, times(1)).writeList(eq("carts.json"), anyList());
  }

  @Test
  public void testGetCartTotal() {
    // Given
    when(fileStorageService.readList(anyString(), eq(Cart.class))).thenReturn(cartList);

    // When
    double result = cartService.getCartTotal(1L);

    // Then
    assertEquals(29.99, result, 0.01);
    verify(fileStorageService, atLeast(1)).readList("carts.json", Cart.class);
  }

  @Test
  public void testGetCartTotalNotFound() {
    // Given
    when(fileStorageService.readList(anyString(), eq(Cart.class))).thenReturn(cartList);

    // When
    double result = cartService.getCartTotal(3L);

    // Then
    assertEquals(0.0, result, 0.01);
    verify(fileStorageService, atLeast(1)).readList("carts.json", Cart.class);
  }
}
