package game.controller;

import game.model.Cart;
import game.model.CartItem;
import game.model.Game;
import game.service.CartService;
import game.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CartControllerTest {

  private MockMvc mockMvc;

  @Mock
  private CartService cartService;

  @Mock
  private GameService gameService;

  @InjectMocks
  private CartController cartController;

  private Cart cart;
  private Game game1;
  private Game game2;
  private CartItem cartItem1;

  @BeforeEach
  public void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();

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
    cartItem1 = new CartItem();
    cartItem1.setId(1L);
    cartItem1.setGameId(game1.getId());
    cartItem1.setGameName(game1.getName());
    cartItem1.setPrice(game1.getPrice());
    cartItem1.setQuantity(1);

    // Initialize cart
    cart = new Cart();
    cart.setId(1L);
    cart.setUserId(1L);
    cart.setItems(new ArrayList<>(Arrays.asList(cartItem1)));
    cartItem1.setCart(cart);
  }

  @Test
  public void testGetCartByUserId() throws Exception {
    // Given
    when(cartService.getOrCreateCartForUser(1L)).thenReturn(cart);

    // When/Then
    mockMvc.perform(get("/api/carts/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.userId", is(1)))
        .andExpect(jsonPath("$.items", hasSize(1)))
        .andExpect(jsonPath("$.items[0].gameId", is(1)))
        .andExpect(jsonPath("$.items[0].gameName", is("Game 1")))
        .andExpect(jsonPath("$.items[0].price", is(29.99)));

    verify(cartService, times(1)).getOrCreateCartForUser(1L);
  }

  @Test
  public void testAddItemToCart() throws Exception {
    // Given
    when(gameService.getGameById(2L)).thenReturn(Optional.of(game2));

    Cart updatedCart = new Cart();
    updatedCart.setId(1L);
    updatedCart.setUserId(1L);

    CartItem cartItem1Copy = new CartItem();
    cartItem1Copy.setId(1L);
    cartItem1Copy.setGameId(game1.getId());
    cartItem1Copy.setGameName(game1.getName());
    cartItem1Copy.setPrice(game1.getPrice());
    cartItem1Copy.setQuantity(1);
    cartItem1Copy.setCart(updatedCart);

    CartItem cartItem2 = new CartItem();
    cartItem2.setId(2L);
    cartItem2.setGameId(game2.getId());
    cartItem2.setGameName(game2.getName());
    cartItem2.setPrice(game2.getPrice());
    cartItem2.setQuantity(1);
    cartItem2.setCart(updatedCart);

    updatedCart.setItems(new ArrayList<>(Arrays.asList(cartItem1Copy, cartItem2)));

    when(cartService.addItemToCart(eq(1L), any(Game.class))).thenReturn(updatedCart);

    // When/Then
    mockMvc.perform(post("/api/carts/1/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"id\":2,\"name\":\"Game 2\",\"price\":19.99,\"likes\":200,\"image\":\"/image2.jpg\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.userId", is(1)))
        .andExpect(jsonPath("$.items", hasSize(2)))
        .andExpect(jsonPath("$.items[1].gameId", is(2)))
        .andExpect(jsonPath("$.items[1].gameName", is("Game 2")))
        .andExpect(jsonPath("$.items[1].price", is(19.99)));

    verify(gameService, times(1)).getGameById(2L);
    verify(cartService, times(1)).addItemToCart(eq(1L), any(Game.class));
  }

  @Test
  public void testAddItemToCartGameNotFound() throws Exception {
    // Given
    when(gameService.getGameById(3L)).thenReturn(Optional.empty());

    // When/Then
    mockMvc.perform(post("/api/carts/1/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"id\":3,\"name\":\"Game 3\",\"price\":39.99,\"likes\":50,\"image\":\"/image3.jpg\"}"))
        .andExpect(status().isNotFound());

    verify(gameService, times(1)).getGameById(3L);
    verify(cartService, never()).addItemToCart(anyLong(), any(Game.class));
  }

  @Test
  public void testRemoveItemFromCart() throws Exception {
    // Given
    Cart updatedCart = new Cart();
    updatedCart.setId(1L);
    updatedCart.setUserId(1L);
    updatedCart.setItems(new ArrayList<>());

    when(cartService.removeItemFromCart(1L, 1L)).thenReturn(updatedCart);

    // When/Then
    mockMvc.perform(delete("/api/carts/1/items/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.userId", is(1)))
        .andExpect(jsonPath("$.items", hasSize(0)));

    verify(cartService, times(1)).removeItemFromCart(1L, 1L);
  }

  @Test
  public void testRemoveItemFromCartNotFound() throws Exception {
    // Given
    when(cartService.removeItemFromCart(1L, 3L)).thenThrow(new RuntimeException("Item not found"));

    // When/Then
    mockMvc.perform(delete("/api/carts/1/items/3"))
        .andExpect(status().isNotFound());

    verify(cartService, times(1)).removeItemFromCart(1L, 3L);
  }

  @Test
  public void testClearCart() throws Exception {
    // Given
    doNothing().when(cartService).clearCart(1L);

    // When/Then
    mockMvc.perform(delete("/api/carts/1"))
        .andExpect(status().isNoContent());

    verify(cartService, times(1)).clearCart(1L);
  }

  @Test
  public void testClearCartNotFound() throws Exception {
    // Given
    doThrow(new RuntimeException("Cart not found")).when(cartService).clearCart(3L);

    // When/Then
    mockMvc.perform(delete("/api/carts/3"))
        .andExpect(status().isNotFound());

    verify(cartService, times(1)).clearCart(3L);
  }

  @Test
  public void testGetCartTotal() throws Exception {
    // Given
    when(cartService.findByUserId(1L)).thenReturn(Optional.of(cart));
    when(cartService.getCartTotal(1L)).thenReturn(29.99);

    // When/Then
    mockMvc.perform(get("/api/carts/1/total"))
        .andExpect(status().isOk())
        .andExpect(content().string("29.99"));

    verify(cartService, times(1)).findByUserId(1L);
    verify(cartService, times(1)).getCartTotal(1L);
  }

  @Test
  public void testGetCartTotalNotFound() throws Exception {
    // Given
    when(cartService.findByUserId(3L)).thenReturn(Optional.empty());

    // When/Then
    mockMvc.perform(get("/api/carts/3/total"))
        .andExpect(status().isNotFound());

    verify(cartService, times(1)).findByUserId(3L);
    verify(cartService, never()).getCartTotal(anyLong());
  }

  @Test
  public void testCheckout() throws Exception {
    // Given
    when(cartService.findByUserId(1L)).thenReturn(Optional.of(cart));
    doNothing().when(cartService).clearCart(1L);

    // When/Then
    mockMvc.perform(post("/api/carts/1/checkout"))
        .andExpect(status().isOk())
        .andExpect(content().string("Thanks for your purchase"));

    verify(cartService, times(1)).findByUserId(1L);
    verify(cartService, times(1)).clearCart(1L);
  }

  @Test
  public void testCheckoutCartNotFound() throws Exception {
    // Given
    when(cartService.findByUserId(3L)).thenReturn(Optional.empty());

    // When/Then
    mockMvc.perform(post("/api/carts/3/checkout"))
        .andExpect(status().isNotFound());

    verify(cartService, times(1)).findByUserId(3L);
    verify(cartService, never()).clearCart(anyLong());
  }

  @Test
  public void testCheckoutEmptyCart() throws Exception {
    // Given
    Cart emptyCart = new Cart();
    emptyCart.setId(2L);
    emptyCart.setUserId(2L);
    emptyCart.setItems(new ArrayList<>());

    when(cartService.findByUserId(2L)).thenReturn(Optional.of(emptyCart));

    // When/Then
    mockMvc.perform(post("/api/carts/2/checkout"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Cart is empty"));

    verify(cartService, times(1)).findByUserId(2L);
    verify(cartService, never()).clearCart(anyLong());
  }
}