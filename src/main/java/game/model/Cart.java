package game.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
  private Long userId;
  private List<CartItem> items;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
      this.userId = userId;
  }

  public List<CartItem> getItems() {
    return items;
  }

  public void setItems(List<CartItem> items) {
    this.items = items;
  }

  public void addItem(CartItem item) {
    items.add(item);
  }
  
  public void removeItem(CartItem item) {
    items.remove(item);
  } 

  public Double getTotal() {
    return items.stream()
        .mapToDouble(item -> item.getPrice())
        .sum();
  }

  public boolean containsGame(Long gameId) {
    return items.stream()
        .anyMatch(item -> item.getGameId().equals(gameId));
  }

  public void clear() {
    items.clear();
  }
}