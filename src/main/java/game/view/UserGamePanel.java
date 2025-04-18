package game.view;

import game.controller.GameController;
import game.controller.UserController;
import game.model.Game;
import game.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UserGamePanel extends JPanel {
  private final GameController gameController;
  private final UserController userController;
  private User currentUser;
  private final MainWindow mainWindow;

  // UI components
  private JTabbedPane tabbedPane;
  private JPanel allGamesPanel;
  private JTable favoritesTable;
  private DefaultTableModel allGamesModel;
  private DefaultTableModel favoritesModel;
  private JButton addToFavoritesButton;
  private JButton removeFromFavoritesButton;
  private JButton refreshButton;
  private JButton logoutButton;
  private JTextField searchField;
  private JButton searchButton;

  public UserGamePanel(GameController gameController, UserController userController, User user, MainWindow mainWindow) {
    this.gameController = gameController;
    this.userController = userController;
    this.currentUser = user;
    this.mainWindow = mainWindow;

    setLayout(new BorderLayout());

    initializeComponents();
    setupLayout();
    refreshGameList();
  }

  private void initializeComponents() {
    // Create tabbed pane
    tabbedPane = new JTabbedPane();

    // Table models
    String[] columns = {"ID", "Name", "Price", "Likes", "Image"};
    allGamesModel = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    favoritesModel = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    // Favorites table
    favoritesTable = new JTable(favoritesModel);
    favoritesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    // Search
    searchField = new JTextField(20);
    searchButton = new JButton("Search");

    // Buttons
    addToFavoritesButton = new JButton("Add to Favorites");
    removeFromFavoritesButton = new JButton("Remove from Favorites");
    refreshButton = new JButton("Refresh");
    logoutButton = new JButton("Logout");

    // Listeners
    addToFavoritesButton.addActionListener(e -> addToFavorites());
    removeFromFavoritesButton.addActionListener(e -> removeFromFavorites());
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

    // Favorites panel
    JPanel favoritesPanel = new JPanel(new BorderLayout());
    JScrollPane favoritesScrollPane = new JScrollPane(favoritesTable);
    favoritesPanel.add(favoritesScrollPane, BorderLayout.CENTER);

    JPanel favoritesButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    favoritesButtonPanel.add(removeFromFavoritesButton);
    favoritesPanel.add(favoritesButtonPanel, BorderLayout.SOUTH);

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
    tabbedPane.addTab("My Favorites", favoritesPanel);

    // Bottom buttons
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    bottomPanel.add(refreshButton);

    // Add to main panel
    add(new JLabel("Game Inventory - User View", JLabel.CENTER), BorderLayout.NORTH);
    add(tabbedPane, BorderLayout.CENTER);
    add(bottomPanel, BorderLayout.SOUTH);
    add(topPanel, BorderLayout.NORTH);
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
    priceAndButton.add(priceLabel, BorderLayout.WEST);
    priceAndButton.add(addToCartButton, BorderLayout.EAST);
    detailsPanel.add(priceAndButton);

    panel.add(detailsPanel, BorderLayout.SOUTH);
    return panel;
  }

  private void addToFavorites() {
    int selectedRow = allGamesPanel.getComponentCount() - 1;
    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(this,
          "Please select a game to add to favorites",
          "Selection Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    JOptionPane.showMessageDialog(this,
        "Adding to favorites is not implemented yet",
        "Not Implemented",
        JOptionPane.INFORMATION_MESSAGE);
  }

  private void removeFromFavorites() {
    int selectedRow = favoritesTable.getSelectedRow();
    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(this,
          "Please select a game to remove from favorites",
          "Selection Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    JOptionPane.showMessageDialog(this,
        "Removing from favorites is not implemented yet",
        "Not Implemented",
        JOptionPane.INFORMATION_MESSAGE);
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

  public void setCurrentUser(User user) {
    this.currentUser = user;
  }
}