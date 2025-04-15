package game.view;

import game.controller.GameController;
import game.model.Cart;
import game.model.CartItem;
import game.model.Game;
import game.model.User;
import game.service.CartService;
import game.service.GameService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CartPanel extends JPanel {
  private final CartService cartService;
  private final GameService gameService;
  private final MainWindow mainWindow;

  private JTable cartTable;
  private DefaultTableModel tableModel;

  public CartPanel(CartService cartService, GameService gameService, MainWindow mainWindow) {
    this.cartService = cartService;
    this.gameService = gameService;
    this.mainWindow = mainWindow;

    setLayout(new BorderLayout());

    initializeComponents();
    setupLayout();
    refreshCartList();
  }

  private void initializeComponents() {
    // Create table for cart items
    String[] columns = {"ID", "Name", "Price", "Quantity"};
    tableModel = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    cartTable = new JTable(tableModel);
    cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  }

  private void setupLayout() {
    // Create cart table panel
    JScrollPane tableScrollPane = new JScrollPane(cartTable);
    tableScrollPane.setBorder(BorderFactory.createTitledBorder("Cart Items"));

    // Add cart table to the center
    add(tableScrollPane, BorderLayout.CENTER);

    // Add buttons to the top corners
    JPanel topPanel = new JPanel(new BorderLayout());

    // Return button at the left top corner
    JButton returnButton = new JButton("Game");
    returnButton.addActionListener(e -> mainWindow.showGamePanel());
    JPanel topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    topLeftPanel.add(returnButton);
    topPanel.add(topLeftPanel, BorderLayout.WEST);

    // Login button at the right top corner
    JButton loginButton = new JButton("Login");
    loginButton.addActionListener(e -> mainWindow.showLoginPanel());
    JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    topRightPanel.add(loginButton);
    topPanel.add(topRightPanel, BorderLayout.EAST);

    add(topPanel, BorderLayout.NORTH);
  }

  public void refreshCartList() {
    try {
      // Clear table
      tableModel.setRowCount(0);

      // Get cart for the current user
      User currentUser = mainWindow.getCurrentUser();
      if (currentUser == null) {
        // If no user is logged in, display an empty cart
        return;
      }

      Cart cart = cartService.getOrCreateCartForUser(currentUser.getId());

      if (cart.getItems() == null || cart.getItems().isEmpty()) {
        System.out.println("No items in the cart");
        return;
      }

      // Add cart items to table
      for (CartItem item : cart.getItems()) {
        Game game = gameService.getGameById(item.getGameId()).orElse(null);
        if (game != null) {
          tableModel.addRow(new Object[]{
              game.getId().toString(),
              game.getName(),
              game.getPrice(),
              item.getQuantity()
          });
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(this,
          "Error refreshing cart list: " + e.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }
}