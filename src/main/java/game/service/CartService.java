package game.service;

import game.model.Cart;
import game.model.CartItem;
import game.model.Game;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CartService {

  private static final String CARTS_FILE = "carts.json";
  private final AtomicLong cartIdGenerator = new AtomicLong(1);
  private final AtomicLong itemIdGenerator = new AtomicLong(1);

  @Autowired
  private FileStorageService fileStorageService;

  public CartService(FileStorageService fileStorageService) {
    this.fileStorageService = fileStorageService;
    initializeIdGenerator();
  }

  private void initializeIdGenerator() {
    List<Cart> carts = getAllCarts();
    if (!carts.isEmpty()){
      long maxCartId = carts.stream().mapToLong(Cart::getId).max().orElse(0L);
      itemIdGenerator.set(maxCartId+1);
    }
  }

  public List<Cart> getAllCarts() {
    return fileStorageService.readList(CARTS_FILE, Cart.class);
  }

  public Optional<Cart> findByUserId(Long userId) {
    return getAllCarts().stream()
        .filter(cart -> cart.getUserId().equals(userId))
        .findFirst();
  }
  public Cart save(Cart cart) {
    List<Cart> carts = getAllCarts();

    if (cart.getId() == null) {
      cart.setId(cartIdGenerator.getAndIncrement());
    }

    // Assign IDs to any new cart items
    for (CartItem item : cart.getItems()) {
      if (item.getId() == null) {
        item.setId(itemIdGenerator.getAndIncrement());
      }
    }

    // Remove existing cart with same ID if exists
    List<Cart> updatedCarts = carts.stream()
        .filter(c -> !c.getId().equals(cart.getId()))
        .collect(Collectors.toList());

    // Add the updated cart
    updatedCarts.add(cart);
    fileStorageService.writeList(CARTS_FILE, updatedCarts);

    return cart;
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