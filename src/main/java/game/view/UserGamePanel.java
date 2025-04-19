package game.view;

import game.controller.GameController;
import game.controller.UserController;
import game.model.Game;
import game.model.User;
import game.model.Cart;
import game.model.CartItem;
import game.service.CartService;
import game.service.GameService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UserGamePanel extends JPanel {
  private final GameController gameController;
  private final UserController userController;
  private final CartService cartService;
  private final GameService gameService;
  private User currentUser;
  private final MainWindow mainWindow;

  // UI components
  private JTabbedPane tabbedPane;
  private JPanel allGamesPanel;
  private JTable cartTable;
  private DefaultTableModel allGamesModel;
  private DefaultTableModel cartModel;
  private JButton addToCartButton;
  private JButton removeFromCartButton;
  private JButton refreshButton;
  private JButton logoutButton;
  private JTextField searchField;
  private JButton searchButton;

  public UserGamePanel(GameController gameController, UserController userController, User user, MainWindow mainWindow) {
    this.gameController = gameController;
    this.userController = userController;
    this.cartService = mainWindow.getCartService();
    this.gameService = mainWindow.getGameService();
    this.currentUser = user;
    this.mainWindow = mainWindow;

    setLayout(new BorderLayout());

    initializeComponents();
    setupLayout();
    refreshGameList();
    refreshCartList(cartService.getOrCreateCartForUser(currentUser.getId()));
  }

  private void initializeComponents() {
    // Create tabbed pane
    tabbedPane = new JTabbedPane();

    // Table models
    String[] columns = {"ID", "Name", "Price", "Quantity"};
    allGamesModel = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    cartModel = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    // Cart table
    cartTable = new JTable(cartModel);
    cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    // Search
    searchField = new JTextField(20);
    searchButton = new JButton("Search");

    // Buttons
    addToCartButton = new JButton("Add to Cart");
    removeFromCartButton = new JButton("Remove from Cart");
    refreshButton = new JButton("Refresh");
    logoutButton = new JButton("Logout");

    // Listeners
    addToCartButton.addActionListener(e -> addToCart(addToCartButton));
    //removeFromCartButton.addActionListener(e -> removeFromCart());
    refreshButton.addActionListener(e -> refreshGameList());
    logoutButton.addActionListener(e -> mainWindow.showLoginPanel());
    searchButton.addActionListener(e -> searchGames());
  }

  private void setupLayout() {
    // All Games panel
    allGamesPanel = new JPanel(new GridLayout(0, 4, 10, 10));
    allGamesPanel.setBorder(BorderFactory.createTitledBorder("All Games"));

    // Add scroll pane for All Games
    JScrollPane allGamesScrollPane = new JScrollPane(allGamesPanel);
    allGamesScrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling

    // Cart panel
    JPanel cartPanel = new JPanel(new BorderLayout());
    JScrollPane cartScrollPane = new JScrollPane(cartTable);
    cartPanel.add(cartScrollPane, BorderLayout.CENTER);

    JPanel cartButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    cartButtonPanel.add(removeFromCartButton);
    cartPanel.add(cartButtonPanel, BorderLayout.SOUTH);

    // Search panel
    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    searchPanel.add(new JLabel("Search:"));
    searchPanel.add(searchField);
    searchPanel.add(searchButton);

    // Top panel with search and logout
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.add(searchPanel, BorderLayout.CENTER);

    // Logout button in the top right corner
    JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    logoutPanel.add(logoutButton);
    topPanel.add(logoutPanel, BorderLayout.EAST);

    // Tabs
    tabbedPane.addTab("All Games", allGamesScrollPane);
    tabbedPane.addTab("My Cart", cartPanel);

    // Bottom buttons
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    bottomPanel.add(refreshButton);

    // Add to main panel
    add(new JLabel("Game Inventory - User View", JLabel.CENTER), BorderLayout.NORTH);
    add(tabbedPane, BorderLayout.CENTER);
    add(bottomPanel, BorderLayout.SOUTH);
    add(topPanel, BorderLayout.NORTH);
  }

  private JPanel createGamePanel(Game game) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setPreferredSize(new Dimension(250, 280));
    panel.setMaximumSize(new Dimension(250, 280));
    panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

    // Image
    String imagePath = game.getImage();
    ImagePanel imagePanel = new ImagePanel(imagePath);
    imagePanel.setPreferredSize(new Dimension(250, 180));
    imagePanel.setLayout(new BorderLayout());
    panel.add(imagePanel, BorderLayout.CENTER);

    // Details
    JPanel detailsPanel = new JPanel(new GridLayout(2, 1));
    JPanel nameAndLike = new JPanel(new BorderLayout());
    JLabel nameLabel = new JLabel(game.getName());
    nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
    nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
    JLabel likeLabel = new JLabel("<html><font color='red'>❤</font> " + game.getLikes());
    likeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
    nameAndLike.add(nameLabel, BorderLayout.WEST);
    nameAndLike.add(likeLabel, BorderLayout.EAST);
    detailsPanel.add(nameAndLike);

    JPanel priceAndButton = new JPanel(new BorderLayout());
    JLabel priceLabel = new JLabel("$ " + game.getPrice());
    priceLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
    JButton addToCartButton = new JButton("Add to Cart");
    addToCartButton.setName(game.getId().toString());
    addToCartButton.addActionListener(e -> addToCart(addToCartButton));
    priceAndButton.add(priceLabel, BorderLayout.WEST);
    priceAndButton.add(addToCartButton, BorderLayout.EAST);
    detailsPanel.add(priceAndButton);

    panel.add(detailsPanel, BorderLayout.SOUTH);
    return panel;
  }
  
  public void refreshGameList() {
    allGamesPanel.removeAll();
    List<Game> games = gameController.getAllGames();

    games.sort((g1, g2) -> Integer.compare(g2.getLikes(), g1.getLikes()));

    for (Game game : games) {
      JPanel gamePanel = createGamePanel(game);
      allGamesPanel.add(gamePanel);
    }
    allGamesPanel.revalidate();
    allGamesPanel.repaint();
  }

  private void searchGames() {
    String keyword = searchField.getText().trim();
    if (keyword.isEmpty()) {
      refreshGameList();
      return;
    }
    try {
      List<Game> searchResults = gameController.searchGames(keyword);
      allGamesPanel.removeAll();
      for (Game game : searchResults) {
        JPanel gamePanel = createGamePanel(game);
        allGamesPanel.add(gamePanel);
      }
      allGamesPanel.revalidate();
      allGamesPanel.repaint();
      if (searchResults.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "No games found matching '" + keyword + "'",
            "Search Error",
            JOptionPane.ERROR_MESSAGE);
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this,
          "Error searching for games: " + e.getMessage(),
          "Search Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private void addToCart(JButton addToCartButton) {
    //check if user is logged in
    if (currentUser == null) {
        JOptionPane.showMessageDialog(this, "Please log in to add items to cart", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        // Get the game ID from the button's name
        Long gameId = Long.parseLong(addToCartButton.getName());
        
        // Get the game object
        Game game = gameController.getGameById(gameId).getBody();
        if (game == null) {
            throw new Exception("Game not found");
        }
        
        // Add game to cart
        Cart updatedCart = cartService.addItemToCart(currentUser.getId(), game);
        JOptionPane.showMessageDialog(this, "Game added to cart successfully!");
        refreshCartList(updatedCart); // Refresh cart list
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Failed to add game to cart: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  // private void removeFromCart() {
  //   int selectedRow = cartTable.getSelectedRow();
  //   if (selectedRow == -1) {
  //     JOptionPane.showMessageDialog(this,
  //         "Please select a game to remove from cart",
  //         "Selection Error",
  //         JOptionPane.ERROR_MESSAGE);
  //     return;
  //   }
  //   try {
  //     Long gameId = Long.parseLong(cartModel.getValueAt(selectedRow, 0).toString());
  //     cartService.removeItemFromCart(currentUser.getId(), gameId);
  //     refreshCartList();
  //   } catch (Exception e) {
  //     e.printStackTrace();
  //     JOptionPane.showMessageDialog(this,
  //         "Error removing game from cart: " + e.getMessage(),
  //         "Error",
  //         JOptionPane.ERROR_MESSAGE);
  //   }
  // }

  public void setCurrentUser(User user) {
    this.currentUser = user;
  }

  private void refreshCartList(Cart cart) {
    cartModel.setRowCount(0); //clear the table
    
    if (cart != null) {
      for (CartItem item : cart.getItems()) {
          cartModel.addRow(new Object[]{
              item.getGameId(),
              item.getGameName(),
              item.getPrice(),
              item.getQuantity()
          });
      }
    }
  }
}