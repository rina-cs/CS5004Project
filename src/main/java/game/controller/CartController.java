package game.controller;

import game.model.Cart;
import game.model.Game;
import game.service.GameService;
import game.service.CartService;
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
  private CartService cartService;

  @Autowired
  private GameService gameService;

  @GetMapping("/{userId}")
  public ResponseEntity<Cart> getCartByUserId(@PathVariable Long userId) {
    Cart cart = cartService.getOrCreateCartForUser(userId);
    return ResponseEntity.ok(cart);
  }

  @PostMapping("/{userId}/items")
  public ResponseEntity<Cart> addItemToCart(
      @PathVariable Long userId,
      @RequestBody Game game) {

    Optional<Game> optionalGame = gameService.getGameById(game.getId());

    if (optionalGame.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    Game actualGame = optionalGame.get();
    Cart updatedCart = cartService.addItemToCart(userId, actualGame);
    return ResponseEntity.status(HttpStatus.CREATED).body(updatedCart);
  }


  @DeleteMapping("/{userId}/items/{gameId}")
  public ResponseEntity<Cart> removeItemFromCart(
      @PathVariable Long userId,
      @PathVariable Long gameId) {

    try {
      Cart updatedCart = cartService.removeItemFromCart(userId, gameId);
      return ResponseEntity.ok(updatedCart);
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
    try {
      cartService.clearCart(userId);
      return ResponseEntity.noContent().build();
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/{userId}/total")
  public ResponseEntity<Double> getCartTotal(@PathVariable Long userId) {
    Optional<Cart> optionalCart = cartService.findByUserId(userId);
    if (optionalCart.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    double total = cartService.getCartTotal(userId);
    return ResponseEntity.ok(total);
  }

  @PostMapping("/{userId}/checkout")
  public ResponseEntity<String> checkout(@PathVariable Long userId) {
    Optional<Cart> optionalCart = cartService.findByUserId(userId);
    if (optionalCart.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    Cart cart = optionalCart.get();
    if (cart.getItems().isEmpty()) {
      return ResponseEntity.badRequest().body("Cart is empty");
    }

    cartService.clearCart(userId);
    return ResponseEntity.ok("Thanks for your purchase");
  }
}