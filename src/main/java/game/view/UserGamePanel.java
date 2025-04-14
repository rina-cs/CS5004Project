package game.view;

import game.controller.GameController;
import game.model.Game;
import game.model.User;
import org.springframework.http.ResponseEntity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * UserGamePanel provides the interface for regular users to view games,
 * add games to favorites, and remove games from favorites.
 */
public class UserGamePanel extends JPanel {
  private final GameController gameController;
  private User currentUser;
  private final MainWindow mainWindow;

  // UI components
  private JTabbedPane tabbedPane;
  private JTable allGamesTable;
  private JTable favoritesTable;
  private DefaultTableModel allGamesModel;
  private DefaultTableModel favoritesModel;
  private JButton addToFavoritesButton;
  private JButton removeFromFavoritesButton;
  private JButton refreshButton;
  private JButton logoutButton;
  private JTextField searchField;
  private JButton searchButton;

  /**
   * Constructs the user panel with required dependencies
   */
  public UserGamePanel(GameController gameController, User user, MainWindow mainWindow) {
    this.gameController = gameController;
    this.currentUser = user;
    this.mainWindow = mainWindow;

    setLayout(new BorderLayout());

    initializeComponents();
    setupLayout();
    refreshGameList();
  }

  /**
   * Initialize UI components
   */
  private void initializeComponents() {
    // Create tabbed pane
    tabbedPane = new JTabbedPane();

    // Create table models
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

    // Create tables
    allGamesTable = new JTable(allGamesModel);
    allGamesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    favoritesTable = new JTable(favoritesModel);
    favoritesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    // Create search components
    searchField = new JTextField(20);
    searchButton = new JButton("Search");

    // Create buttons
    addToFavoritesButton = new JButton("Add to Favorites");
    removeFromFavoritesButton = new JButton("Remove from Favorites");
    refreshButton = new JButton("Refresh");
    logoutButton = new JButton("Logout");

    // Add action listeners
    addToFavoritesButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        addToFavorites();
      }
    });

    removeFromFavoritesButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        removeFromFavorites();
      }
    });

    refreshButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        refreshGameList();
      }
    });

    logoutButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mainWindow.showLoginPanel();
      }
    });

    searchButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        searchGames();
      }
    });
  }

  /**
   * Set up the layout of the panel
   */
  private void setupLayout() {
    // Create all games panel
    JPanel allGamesPanel = new JPanel(new BorderLayout());

    // Add search panel at the top
    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    searchPanel.add(new JLabel("Search:"));
    searchPanel.add(searchField);
    searchPanel.add(searchButton);
    allGamesPanel.add(searchPanel, BorderLayout.NORTH);

    // Add table in the center
    JScrollPane allGamesScrollPane = new JScrollPane(allGamesTable);
    allGamesPanel.add(allGamesScrollPane, BorderLayout.CENTER);

    // Add buttons at the bottom
    JPanel allGamesButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    allGamesButtonPanel.add(addToFavoritesButton);
    allGamesPanel.add(allGamesButtonPanel, BorderLayout.SOUTH);

    // Create favorites panel
    JPanel favoritesPanel = new JPanel(new BorderLayout());
    JScrollPane favoritesScrollPane = new JScrollPane(favoritesTable);
    favoritesPanel.add(favoritesScrollPane, BorderLayout.CENTER);

    JPanel favoritesButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    favoritesButtonPanel.add(removeFromFavoritesButton);
    favoritesPanel.add(favoritesButtonPanel, BorderLayout.SOUTH);

    // Add panels to tabbed pane
    tabbedPane.addTab("All Games", allGamesPanel);
    tabbedPane.addTab("My Favorites", favoritesPanel);

    // Create bottom button panel
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    bottomPanel.add(refreshButton);
    bottomPanel.add(logoutButton);

    // Add components to main panel
    add(new JLabel("Game Inventory - User View", JLabel.CENTER), BorderLayout.NORTH);
    add(tabbedPane, BorderLayout.CENTER);
    add(bottomPanel, BorderLayout.SOUTH);
  }

  /**
   * Refresh the game list in both tables
   */
  public void refreshGameList() {
    // Clear tables
    allGamesModel.setRowCount(0);
    favoritesModel.setRowCount(0);

    // Get all games from controller
    List<Game> games = gameController.getAllGames();

    // Add all games to the all games table
    for (Game game : games) {
      allGamesModel.addRow(new Object[]{
          game.getId().toString(),
          game.getName(),
          game.getPrice(),
          game.getLikes(),
          game.getImage()
      });
    }

    // TODO: When you implement the favorites functionality, populate the favorites table
    // This would require additional methods in your User and UserController classes

    // For now, we'll just display a message
    if (favoritesModel.getRowCount() == 0) {
      favoritesModel.addRow(new Object[]{"", "No favorites yet", "", "", ""});
    }
  }

  /**
   * Search for games by name
   */
  private void searchGames() {
    String keyword = searchField.getText().trim();
    if (keyword.isEmpty()) {
      refreshGameList();
      return;
    }

    try {
      List<Game> searchResults = gameController.searchGames(keyword);

      // Clear the all games table
      allGamesModel.setRowCount(0);

      // Add search results to the table
      for (Game game : searchResults) {
        allGamesModel.addRow(new Object[]{
            game.getId().toString(),
            game.getName(),
            game.getPrice(),
            game.getLikes(),
            game.getImage()
        });
      }

      // Show a message if no results found
      if (searchResults.isEmpty()) {
        allGamesModel.addRow(new Object[]{"", "No games found matching '" + keyword + "'", "", "", ""});
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this,
          "Error searching for games: " + e.getMessage(),
          "Search Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Add the selected game to favorites
   */
  private void addToFavorites() {
    int selectedRow = allGamesTable.getSelectedRow();
    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(this,
          "Please select a game to add to favorites",
          "Selection Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    String gameId = (String) allGamesModel.getValueAt(selectedRow, 0);

    // TODO: Implement adding to favorites functionality
    // This requires additional methods in your User and UserController classes

    JOptionPane.showMessageDialog(this,
        "Adding to favorites is not implemented yet",
        "Not Implemented",
        JOptionPane.INFORMATION_MESSAGE);

    // When implemented, it would be something like:
    // userController.addGameToFavorites(currentUser.getId(), Long.parseLong(gameId));
    // refreshGameList();
  }

  /**
   * Remove the selected game from favorites
   */
  private void removeFromFavorites() {
    int selectedRow = favoritesTable.getSelectedRow();
    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(this,
          "Please select a game to remove from favorites",
          "Selection Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    // Check if we have the dummy "No favorites" row
    if (favoritesModel.getValueAt(selectedRow, 0).equals("")) {
      return;
    }

    String gameId = (String) favoritesModel.getValueAt(selectedRow, 0);

    // TODO: Implement removing from favorites functionality
    // This requires additional methods in your User and UserController classes

    JOptionPane.showMessageDialog(this,
        "Removing from favorites is not implemented yet",
        "Not Implemented",
        JOptionPane.INFORMATION_MESSAGE);

    // When implemented, it would be something like:
    // userController.removeGameFromFavorites(currentUser.getId(), Long.parseLong(gameId));
    // refreshGameList();
  }

  /**
   * Set the current user
   */
  public void setCurrentUser(User user) {
    this.currentUser = user;
  }
}