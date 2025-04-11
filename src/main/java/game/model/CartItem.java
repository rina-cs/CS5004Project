package game.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

public class CartItem {

  private Long id;

  @JsonBackReference
  private Cart cart;

  private Long gameId;
  private String gameName;
  private Double price;
  private Integer quantity = 1;

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Cart getCart() {
    return cart;
  }

  public void setCart(Cart cart) {
    this.cart = cart;
  }

  public Long getGameId() {
    return gameId;
  }

  public void setGameId(Long gameId) {
    this.gameId = gameId;
  }

  public String getGameName() {
    return gameName;
  }

  public void setGameName(String gameName) {
    this.gameName = gameName;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

}