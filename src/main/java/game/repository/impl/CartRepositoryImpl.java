package game.repository.impl;

import game.model.Cart;
import game.model.CartItem;
import game.repository.CartRepository;
import game.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class CartRepositoryImpl implements CartRepository {

  private static final String CARTS_FILE = "carts.json";
  private final AtomicLong cartIdGenerator = new AtomicLong(1);
  private final AtomicLong itemIdGenerator = new AtomicLong(1);

  @Autowired
  private FileStorageService fileStorageService;

  public CartRepositoryImpl(FileStorageService fileStorageService) {
    this.fileStorageService = fileStorageService;
    initializeIdGenerators();
  }

  private void initializeIdGenerators() {
    List<Cart> carts = findAll();
    if (!carts.isEmpty()) {
      long maxCartId = carts.stream()
          .mapToLong(Cart::getId)
          .max()
          .orElse(0);
      cartIdGenerator.set(maxCartId + 1);

      long maxItemId = carts.stream()
          .flatMap(cart -> cart.getItems().stream())
          .mapToLong(CartItem::getId)
          .max()
          .orElse(0);
      itemIdGenerator.set(maxItemId + 1);
    }
  }

  @Override
  public List<Cart> findAll() {
    return fileStorageService.readList(CARTS_FILE, Cart.class);
  }

  @Override
  public Optional<Cart> findById(Long id) {
    return findAll().stream()
        .filter(cart -> cart.getId().equals(id))
        .findFirst();
  }

  @Override
  public Cart save(Cart cart) {
    List<Cart> carts = findAll();

    if (cart.getId() == null) {
      cart.setId(cartIdGenerator.getAndIncrement());
    }

    for (CartItem item : cart.getItems()) {
      if (item.getId() == null) {
        item.setId(itemIdGenerator.getAndIncrement());
      }
    }

    carts = carts.stream()
        .filter(existingCart -> !existingCart.getId().equals(cart.getId()))
        .collect(Collectors.toList());

    carts.add(cart);
    fileStorageService.writeList(CARTS_FILE, carts);
    return cart;
  }

  @Override
  public void deleteById(Long id) {
    List<Cart> carts = findAll();
    List<Cart> updatedCarts = carts.stream()
        .filter(cart -> !cart.getId().equals(id))
        .collect(Collectors.toList());
    fileStorageService.writeList(CARTS_FILE, updatedCarts);
  }

  @Override
  public long count() {
    return findAll().size();
  }

  @Override
  public Optional<Cart> findByUserId(Long userId) {
    return findAll().stream()
        .filter(cart -> cart.getUserId().equals(userId))
        .findFirst();
  }
}