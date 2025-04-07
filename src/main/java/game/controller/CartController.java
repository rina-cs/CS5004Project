package game.controller;

import game.model.Cart;
import game.model.CartItem;
import game.model.Game;
import game.repository.CartRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/carts")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class CartController {

  @Autowired
  private CartRepository cartRepository;

  @GetMapping("/{userId}")
  public ResponseEntity<Cart> getCartByUserId(@PathVariable Long userId) {
    Optional<Cart> optionalCart = cartRepository.findByUserId(userId);
    Cart cart;

    if (optionalCart.isPresent()) {
      cart = optionalCart.get();
    } else {
      cart = new Cart();
      cart.setUserId(userId);
      cart = cartRepository.save(cart);
    }

    if (cart.getItems() == null) {
      cart.setItems(new ArrayList<>());
    }

    return ResponseEntity.ok(cart);
}

  public static class CartItemResponse {
    private Long gameId;
    private String name;
    private double price;
    private int quantity;

    public CartItemResponse(Long gameId, String name, double price, int quantity) {
      this.gameId = gameId;
      this.name = name;
      this.price = price;
      this.quantity = quantity;
    }

    public Long getGameId() {
      return gameId;
    }

    public void setGameId(Long gameId) {
      this.gameId = gameId;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public double getPrice() {
      return price;
    }

    public void setPrice(double price) {
      this.price = price;
    }

    public int getQuantity() {
      return quantity;
    }

    public void setQuantity(int quantity) {
      this.quantity = quantity;
    }
  }

  public static class CartResponse {
    private Long cartId;
    private Long userId;
    private List<CartItemResponse> items;

    public CartResponse(Long cartId, Long userId, List<CartItemResponse> items) {
      this.cartId = cartId;
      this.userId = userId;
      this.items = items;
    }

    public Long getCartId() {
      return cartId;
    }

    public void setCartId(Long cartId) {
      this.cartId = cartId;
    }

    public Long getUserId() {
      return userId;
    }

    public void setUserId(Long userId) {
      this.userId = userId;
    }

    public List<CartItemResponse> getItems() {
      return items;
    }

    public void setItems(List<CartItemResponse> items) {
      this.items = items;
    }
  }

  @PostMapping("/{userId}/items")
  public ResponseEntity<Cart> addItemToCart(
      @PathVariable Long userId,
      @RequestBody Game game) {

    Cart cart = cartRepository.findByUserId(userId)
        .orElseGet(() -> {
          Cart newCart = new Cart();
          newCart.setUserId(userId);
          return cartRepository.save(newCart);
        });

    boolean gameExists = cart.getItems().stream()
        .anyMatch(item -> item.getGameId().equals(game.getId()));

    if (gameExists) {
      return ResponseEntity.ok(cart);
    } else {
      CartItem cartItem = new CartItem();
      cartItem.setGameId(game.getId());
      cartItem.setPrice(game.getPrice());
      cartItem.setGameName(game.getName());
      cartItem.setCart(cart);
      cartItem.setQuantity(1);
      cart.getItems().add(cartItem);
    }

    Cart updatedCart = cartRepository.save(cart);
    return ResponseEntity.status(HttpStatus.CREATED).body(updatedCart);
  }


  @DeleteMapping("/{userId}/items/{gameId}")
  public ResponseEntity<Cart> removeItemFromCart(
      @PathVariable Long userId,
      @PathVariable Long gameId) {

    Optional<Cart> optionalCart = cartRepository.findByUserId(userId);
    if (optionalCart.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    Cart cart = optionalCart.get();
    cart.getItems().removeIf(item -> item.getGameId().equals(gameId));

    Cart updatedCart = cartRepository.save(cart);
    return ResponseEntity.ok(updatedCart);
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
    Optional<Cart> optionalCart = cartRepository.findByUserId(userId);
    if (optionalCart.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    Cart cart = optionalCart.get();
    cart.clear();
    cartRepository.save(cart);

    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{userId}/total")
  public ResponseEntity<Double> getCartTotal(@PathVariable Long userId) {
    Optional<Cart> optionalCart = cartRepository.findByUserId(userId);
    if (optionalCart.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    Cart cart = optionalCart.get();
    double total = cart.getTotal();

    return ResponseEntity.ok(total);
  }

  @PostMapping("/{userId}/checkout")
  public ResponseEntity<String> checkout(@PathVariable Long userId) {
    Optional<Cart> optionalCart = cartRepository.findByUserId(userId);
    if (optionalCart.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    Cart cart = optionalCart.get();
    if (cart.getItems().isEmpty()) {
      return ResponseEntity.badRequest().body("Cart is empty");
    }

    cart.clear();
    cartRepository.save(cart);

    return ResponseEntity.ok("Thanks for your purchase");
  }
}