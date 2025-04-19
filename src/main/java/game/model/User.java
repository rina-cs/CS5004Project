package game.model;

// import java.util.ArrayList;
// import java.util.List;

public class User {
  public User() {
    this.admin = false;
    //this.favoriteGameIds = new ArrayList<>();
  }

  private Long id;
  private String username;
  private String password;
  private String email;
  private boolean admin;
  //private List<Long> favoriteGameIds;


  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Determines if the user has admin privileges
   * @return true if the user is an admin, false otherwise
   */
  public boolean isAdmin() {
    return admin;
  }

  /**
   * Sets the admin status of the user
   * @param admin true to make the user an admin, false otherwise
   */
  public void setAdmin(boolean admin) {
    this.admin = admin;
  }

  // /**
  //  * Gets the list of favorite game IDs for this user
  //  * @return List of game IDs that are favorites
  //  */
  // public List<Long> getFavoriteGameIds() {
  //   if (favoriteGameIds == null) {
  //     favoriteGameIds = new ArrayList<>();
  //   }
  //   return favoriteGameIds;
  // }

  // /**
  //  * Sets the list of favorite game IDs for this user
  //  * @param favoriteGameIds List of game IDs
  //  */
  // public void setFavoriteGameIds(List<Long> favoriteGameIds) {
  //   this.favoriteGameIds = favoriteGameIds;
  // }

  // /**
  //  * Adds a game to the user's favorites
  //  * @param gameId The ID of the game to add
  //  */
  // public void addFavoriteGame(Long gameId) {
  //   if (favoriteGameIds == null) {
  //     favoriteGameIds = new ArrayList<>();
  //   }
  //   if (!favoriteGameIds.contains(gameId)) {
  //     favoriteGameIds.add(gameId);
  //   }
  // }

  // /**
  //  * Removes a game from the user's favorites
  //  * @param gameId The ID of the game to remove
  //  * @return true if the game was removed, false if it wasn't in favorites
  //  */
  // public boolean removeFavoriteGame(Long gameId) {
  //   if (favoriteGameIds == null) {
  //     return false;
  //   }
  //   return favoriteGameIds.remove(gameId);
  // }

  // /**
  //  * Checks if a game is in the user's favorites
  //  * @param gameId The ID of the game to check
  //  * @return true if the game is a favorite, false otherwise
  //  */
  // public boolean isFavoriteGame(Long gameId) {
  //   if (favoriteGameIds == null) {
  //     return false;
  //   }
  //   return favoriteGameIds.contains(gameId);
  // }

}
