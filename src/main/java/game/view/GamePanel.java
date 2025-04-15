package game.view;

import game.controller.GameController;
import game.model.Game;
import org.springframework.http.ResponseEntity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GamePanel extends JPanel {
  private final GameController gameController;
  private final MainWindow mainWindow;

  private JTable gameTable;
  private DefaultTableModel tableModel;
  private JButton loginButton; // Add a login button

  public GamePanel(GameController gameController, MainWindow mainWindow) {
    this.gameController = gameController;
    this.mainWindow = mainWindow;

    setLayout(new BorderLayout());

    initializeComponents();
    setupLayout();
    refreshGameList();
  }

  private void initializeComponents() {
    // Create table for games
    String[] columns = {"ID", "Name", "Price", "Likes", "Image"};
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
  }

  private void setupLayout() {
    // Create game table panel
    JScrollPane tableScrollPane = new JScrollPane(gameTable);
    tableScrollPane.setBorder(BorderFactory.createTitledBorder("Game Lists"));

    // Add game table to the center
    add(tableScrollPane, BorderLayout.CENTER);

    // Add login button to the right top corner
    JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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

      System.out.println("Found " + games.size() + " games");

      // Add games to table
      for (Game game : games) {
        tableModel.addRow(new Object[]{
            game.getId().toString(), // Convert Long to String for display
            game.getName(),
            game.getPrice(),
            game.getLikes(),
            game.getImage()
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
}