package game.service;

import game.model.Cart;
import game.model.CartItem;
import game.model.Game;
import game.repository.CartRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CartService {

  @Autowired
  private CartRepository cartRepository;


  public Optional<Cart> findByUserId(Long userId) {
    return cartRepository.findById(userId);
  }
  public Cart save(Cart cart) {
    return cartRepository.save(cart);
  }

  public Cart getOrCreateCartForUser(Long userId) {
    Optional<Cart> existingCart = findByUserId(userId);
    if (existingCart.isPresent()) {
      return existingCart.get();
    } else {
      Cart newCart = new Cart();
      newCart.setUserId(userId);
      newCart.setItems(new ArrayList<>());
      return save(newCart);
    }
  }

  public Cart addItemToCart(Long userId, Game game) {
    Cart cart = getOrCreateCartForUser(userId);

    boolean gameExists = cart.getItems().stream()
        .anyMatch(item -> item.getGameId().equals(game.getId()));

    if (!gameExists) {
      CartItem cartItem = new CartItem();
      cartItem.setGameId(game.getId());
      cartItem.setGameName(game.getName());
      cartItem.setPrice(game.getPrice());
      cartItem.setQuantity(1);
      cartItem.setCart(cart);

      cart.getItems().add(cartItem);
      return save(cart);
    }

    return cart;
  }

  public Cart removeItemFromCart(Long userId, Long gameId) {
    Optional<Cart> optionalCart = findByUserId(userId);
    if (optionalCart.isEmpty()) {
      throw new RuntimeException("Cart not found for user: " + userId);
    }

    Cart cart = optionalCart.get();
    cart.getItems().removeIf(item -> item.getGameId().equals(gameId));

    return save(cart);
  }

  public void clearCart(Long userId) {
    Optional<Cart> optionalCart = findByUserId(userId);
    if (optionalCart.isEmpty()) {
      throw new RuntimeException("Cart not found for user: " + userId);
    }

    Cart cart = optionalCart.get();
    cart.clear();

    save(cart);
  }

  public double getCartTotal(Long userId) {
    Optional<Cart> optionalCart = findByUserId(userId);
    if (optionalCart.isEmpty()) {
      return 0.0;
    }

    return optionalCart.get().getTotal();
  }
}