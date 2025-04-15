package game.view;

import game.controller.GameController;
import game.model.Game;
import game.model.User;
import game.service.CartService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import org.springframework.http.ResponseEntity;

public class GamePanel extends JPanel {
  private final GameController gameController;
  private final CartService cartService;
  private final User currentUser;
  private final MainWindow mainWindow;

  private JTable gameTable;
  private DefaultTableModel tableModel;
  private JButton loginButton;
  private JButton cartButton;

  public GamePanel(GameController gameController, CartService cartService, User currentUser, MainWindow mainWindow) {
    this.gameController = gameController;
    this.cartService = cartService;
    this.currentUser = currentUser;
    this.mainWindow = mainWindow;

    setLayout(new BorderLayout());

    initializeComponents();
    setupLayout();
    refreshGameList();
  }

  private void initializeComponents() {
    // Create table for games
    String[] columns = {"ID", "Name", "Price", "Likes", "Image", "Action"};
    tableModel = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    gameTable = new JTable(tableModel);
    gameTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    // Create login button
    loginButton = new JButton("Login");
    loginButton.addActionListener(e -> mainWindow.showLoginPanel()); // Action to switch to login panel

    // Create cart button
    cartButton = new JButton("Cart");
    cartButton.addActionListener(e -> mainWindow.showCartPanel()); // Action to switch to cart panel
  }

  private void setupLayout() {
    // Create game table panel
    JScrollPane tableScrollPane = new JScrollPane(gameTable);
    tableScrollPane.setBorder(BorderFactory.createTitledBorder("Game Lists"));

    // Add game table to the center
    add(tableScrollPane, BorderLayout.CENTER);

    // Add buttons to the right top corner
    JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    topRightPanel.add(cartButton);
    topRightPanel.add(loginButton);
    add(topRightPanel, BorderLayout.NORTH);
  }

  public void refreshGameList() {
    try {
      // Clear table
      tableModel.setRowCount(0);

      // Get all games from controller
      List<Game> games = gameController.getAllGames();

      if (games == null || games.isEmpty()) {
        System.out.println("No games found in the database");
        return;
      }

      // Sort games by likes in descending order
      games.sort((g1, g2) -> Integer.compare(g2.getLikes(), g1.getLikes()));

      System.out.println("Found " + games.size() + " games");

      // Add games to table
      for (Game game : games) {
        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.addActionListener(e -> addToCart(game.getId()));
        tableModel.addRow(new Object[]{
            game.getId().toString(), // Convert Long to String for display
            game.getName(),
            game.getPrice(),
            game.getLikes(),
            game.getImage(),
            addToCartButton
        });
      }
    } catch (Exception e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(this,
          "Error refreshing game list: " + e.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private void addToCart(Long gameId) {
    if (currentUser == null) {
      JOptionPane.showMessageDialog(this,
          "Please log in to add games to your cart",
          "Login Required",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    try {
      // Retrieve the Game object using the game ID
      ResponseEntity<Game> responseEntity = gameController.getGameById(gameId);

      if (responseEntity.getStatusCode().is2xxSuccessful()) {
        Game game = responseEntity.getBody();

        // Add the game to the cart
        cartService.addItemToCart(currentUser.getId(), game);
        JOptionPane.showMessageDialog(this,
            "Game added to cart successfully!",
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(this,
            "Game not found",
            "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    } catch (Exception e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(this,
          "Error adding game to cart: " + e.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }
}