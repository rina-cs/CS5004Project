package game.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {

  private Long id;
  private Long userId;
  private List<CartItem> items = new ArrayList<>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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