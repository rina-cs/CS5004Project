package game.view;

import game.controller.GameController;
import game.model.Game;
import org.springframework.http.ResponseEntity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * AdminGamePanel provides the interface for administrators to manage the game inventory.
 * It allows adding, editing, and deleting games with all their properties.
 */
public class AdminGamePanel extends JPanel {
  // Controller
  private final GameController gameController;

  // Reference to main window for navigation
  private final MainWindow mainWindow;

  // UI components
  private JTable gameTable;
  private DefaultTableModel tableModel;

  // Buttons
  private JButton addNewGameButton;
  private JButton updateGameButton;
  private JButton deleteGameButton;
  private JButton clearFormButton;
  private JButton refreshButton;
  private JButton logoutButton;

  // Input fields for game details
  private JTextArea gameIdField;
  private JTextArea gameNameField;
//  private JSpinner likesSpinner;
  private JTextArea priceField;
  private JTextArea imagePathField;

  /**
   * Constructs the admin panel with required dependencies
   */
  public AdminGamePanel(GameController gameController, MainWindow mainWindow) {
    this.gameController = gameController;
    this.mainWindow = mainWindow;

    // Try to set system look and feel for better compatibility
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }

    setLayout(new BorderLayout());

    initializeComponents();
    setupLayout();

    // Test connection to backend
    if (!testConnection()) {
      JOptionPane.showMessageDialog(this,
          "Warning: Connection to backend failed. Game management functions may not work.",
          "Connection Error",
          JOptionPane.WARNING_MESSAGE);
    }

    refreshGameList();
  }

  /**
   * Test connection to backend
   */
  private boolean testConnection() {
    try {
      List<Game> games = gameController.getAllGames();
      System.out.println("Connection test successful - retrieved " +
          (games != null ? games.size() : 0) + " games");
      return true;
    } catch (Exception e) {
      System.err.println("Connection test failed: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Initialize UI components
   */
  private void initializeComponents() {
    // Create table for games
    String[] columns = {"ID", "Name", "Price", "Image Path"};
    tableModel = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    gameTable = new JTable(tableModel);
    gameTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    // Create game table selection listener for populating form fields
    gameTable.getSelectionModel().addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        int selectedRow = gameTable.getSelectedRow();
        if (selectedRow >= 0) {
          gameIdField.setText((String) tableModel.getValueAt(selectedRow, 0));
          gameNameField.setText((String) tableModel.getValueAt(selectedRow, 1));
          priceField.setText(tableModel.getValueAt(selectedRow, 2).toString());
//          likesSpinner.setValue(tableModel.getValueAt(selectedRow, 3));

          // Image path could be null
          Object imagePath = tableModel.getValueAt(selectedRow, 4);
          imagePathField.setText(imagePath != null ? imagePath.toString() : "");
        }
      }
    });

    // Create buttons
    addNewGameButton = new JButton("Add New Game");
    updateGameButton = new JButton("Update Game");
    deleteGameButton = new JButton("Delete Game");
    clearFormButton = new JButton("Clear Form");
    refreshButton = new JButton("Refresh List");
    logoutButton = new JButton("Logout");

    // Create form fields with enhanced focus and input handling
    gameIdField = new JTextArea(1, 10);
    gameIdField.setName("ID Field");
    gameIdField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    gameIdField.setLineWrap(false);
    gameIdField.setEditable(false); // Make ID field non-editable
    gameIdField.setFocusable(true);
    gameIdField.setBackground(new Color(240, 240, 240)); // Light gray to indicate read-only

    gameNameField = new JTextArea(1, 20);
    gameNameField.setName("Name Field");
    gameNameField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    gameNameField.setLineWrap(false);
    gameNameField.setEditable(true);
    gameNameField.setFocusable(true);

    // Create the likes spinner
//    likesSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));

    priceField = new JTextArea(1, 10);
    priceField.setName("Price Field");
    priceField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    priceField.setLineWrap(false);
    priceField.setEditable(true);
    priceField.setFocusable(true);

    imagePathField = new JTextArea(1, 20);
    imagePathField.setName("Image Field");
    imagePathField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    imagePathField.setLineWrap(false);
    imagePathField.setEditable(true);
    imagePathField.setFocusable(true);

    // Test values to verify fields can display text
    clearFormFields();

    // Add mouse listeners to ensure fields receive mouse events
    MouseAdapter fieldMouseAdapter = new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        Component component = e.getComponent();
        if (component instanceof JTextArea) {
          JTextArea field = (JTextArea) component;
          field.requestFocusInWindow();
          System.out.println("Mouse clicked on: " + field.getName());
        }
      }
    };

    gameNameField.addMouseListener(fieldMouseAdapter);
    priceField.addMouseListener(fieldMouseAdapter);
    imagePathField.addMouseListener(fieldMouseAdapter);

    // Add focus listeners to debug focus events
    FocusAdapter focusAdapter = new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        Component component = e.getComponent();
        if (component instanceof JTextArea) {
          System.out.println("Focus gained on: " + ((JTextArea) component).getName());
        }
      }
    };

    gameNameField.addFocusListener(focusAdapter);
    priceField.addFocusListener(focusAdapter);
    imagePathField.addFocusListener(focusAdapter);

    // Add key listeners to debug key events
    KeyAdapter keyAdapter = new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent e) {
        JTextArea field = (JTextArea) e.getSource();
        System.out.println("Key typed in " + field.getName() + ": " + e.getKeyChar());
      }
    };

    gameNameField.addKeyListener(keyAdapter);
    priceField.addKeyListener(keyAdapter);
    imagePathField.addKeyListener(keyAdapter);

    // Set up focus traversal
    gameNameField.setNextFocusableComponent(priceField);
//    priceField.setNextFocusableComponent(likesSpinner);
    // Get the JSpinner's editor component and make it part of the focus cycle
//    Component spinnerEditor = likesSpinner.getEditor();
//    if (spinnerEditor instanceof JSpinner.DefaultEditor) {
//      ((JSpinner.DefaultEditor)spinnerEditor).getTextField().setNextFocusableComponent(imagePathField);
//    }
//    imagePathField.setNextFocusableComponent(addNewGameButton);

    // Add action listeners
    addNewGameButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        createNewGame();
      }
    });

    updateGameButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateGame();
      }
    });

    deleteGameButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        deleteGame();
      }
    });

    clearFormButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        clearFormFields();
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
  }

  /**
   * Set up the layout of the panel
   */
  private void setupLayout() {
    // Create game table panel
    JScrollPane tableScrollPane = new JScrollPane(gameTable);
    tableScrollPane.setBorder(BorderFactory.createTitledBorder("Game Inventory"));

    // Create form panel for game details
    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setBorder(BorderFactory.createTitledBorder("Game Details"));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;

    // Add form components
    gbc.gridx = 0;
    gbc.gridy = 0;
    formPanel.add(new JLabel("Game ID:"), gbc);

    gbc.gridx = 1;
    formPanel.add(gameIdField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    formPanel.add(new JLabel("Game Name:"), gbc);

    gbc.gridx = 1;
    formPanel.add(gameNameField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    formPanel.add(new JLabel("Price:"), gbc);

    gbc.gridx = 1;
    formPanel.add(priceField, gbc);

//    gbc.gridx = 0;
//    gbc.gridy = 3;
//    formPanel.add(new JLabel("Likes:"), gbc);

//    gbc.gridx = 1;
//    formPanel.add(likesSpinner, gbc);

    gbc.gridx = 0;
    gbc.gridy = 4;
    formPanel.add(new JLabel("Image Path:"), gbc);

    gbc.gridx = 1;
    formPanel.add(imagePathField, gbc);

    // Add note about ID field
    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    JLabel noteLabel = new JLabel("Note: Game ID is auto-generated when adding new games");
    noteLabel.setFont(noteLabel.getFont().deriveFont(Font.ITALIC));
    formPanel.add(noteLabel, gbc);

    // Add some spacing
    gbc.gridx = 0;
    gbc.gridy = 6;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(15, 5, 5, 5);
    formPanel.add(new JSeparator(), gbc);

    // Add buttons to form panel
    gbc.gridx = 0;
    gbc.gridy = 7;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.CENTER;

    JPanel formButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    formButtonPanel.add(addNewGameButton);
    formButtonPanel.add(updateGameButton);
    formButtonPanel.add(deleteGameButton);
    formButtonPanel.add(clearFormButton);
    formPanel.add(formButtonPanel, gbc);

    // Create bottom button panel
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    bottomPanel.add(refreshButton);
    bottomPanel.add(logoutButton);

    // Add panels to the main panel
    add(new JLabel("Game Inventory Management System - Admin Panel", JLabel.CENTER), BorderLayout.NORTH);

    // Create split pane for table and form
    JSplitPane splitPane = new JSplitPane(
        JSplitPane.HORIZONTAL_SPLIT,
        tableScrollPane,
        formPanel
    );
    splitPane.setDividerLocation(550);
    add(splitPane, BorderLayout.CENTER);

    add(bottomPanel, BorderLayout.SOUTH);
  }

  /**
   * Refresh the game list in the table
   */
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
//            game.getLikes(),
            game.getImage()
        });
      }

      // Clear form fields
      clearFormFields();
    } catch (Exception e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(this,
          "Error refreshing game list: " + e.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Clear the form fields
   */
  private void clearFormFields() {
    gameIdField.setText("");
    gameNameField.setText("");
//    likesSpinner.setValue(0);
    priceField.setText("0.0");
    imagePathField.setText("");

    // Request focus on the name field to make it easier to start entering a new game
    SwingUtilities.invokeLater(() -> gameNameField.requestFocusInWindow());
  }

  /**
   * Create a new game
   */
  private void createNewGame() {
    try {
      String name = gameNameField.getText().trim();
      String priceText = priceField.getText().trim();
//      int likes = (Integer) likesSpinner.getValue();
      String imagePath = imagePathField.getText().trim();

      System.out.println("Creating new game with values:");
      System.out.println("Name: '" + name + "'");
      System.out.println("Price: '" + priceText + "'");
//      System.out.println("Likes: " + likes);
      System.out.println("Image Path: '" + imagePath + "'");

      // Validate input
      if (name.isEmpty() || priceText.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Please fill in all required fields (Name, Price)",
            "Input Error",
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      // Convert price text to double
      double price;
      try {
        price = Double.parseDouble(priceText);
        if (price < 0) {
          JOptionPane.showMessageDialog(this,
              "Price cannot be negative",
              "Input Error",
              JOptionPane.ERROR_MESSAGE);
          return;
        }
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
            "Price must be a valid number",
            "Input Error",
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      // Create game object
      Game game = new Game();
      game.setName(name);
      game.setPrice(price);
//      game.setLikes(likes);
      game.setImage(imagePath.isEmpty() ? null : imagePath);

      System.out.println("Game object created: " + game.getName() + ", sending to server...");

      try {
        Game createdGame = gameController.createGame(game);
        System.out.println("Response received from server");

        if (createdGame != null && createdGame.getId() != null) {
          System.out.println("Game created successfully with ID: " + createdGame.getId());
          JOptionPane.showMessageDialog(this,
              "Game created successfully with ID: " + createdGame.getId(),
              "Success",
              JOptionPane.INFORMATION_MESSAGE);

          // Refresh table and clear form
          refreshGameList();
        } else {
          System.err.println("Server returned null game object or null ID");
          JOptionPane.showMessageDialog(this,
              "Error creating game: Server returned invalid response",
              "Error",
              JOptionPane.ERROR_MESSAGE);
        }
      } catch (Exception e) {
        System.err.println("Error creating game: " + e.getMessage());
        e.printStackTrace();

        // Show more detailed error
        String errorDetails = e.getMessage();
        if (e.getCause() != null) {
          errorDetails += "\nCause: " + e.getCause().getMessage();
        }

        JOptionPane.showMessageDialog(this,
            "Error creating game: " + errorDetails,
            "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    } catch (Exception e) {
      System.err.println("Unexpected error: " + e.getMessage());
      e.printStackTrace();

      JOptionPane.showMessageDialog(this,
          "Unexpected error: " + e.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Update an existing game
   */
  private void updateGame() {
    try {
      String idText = gameIdField.getText().trim();

      if (idText.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Please select a game to update first",
            "Selection Error",
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      String name = gameNameField.getText().trim();
      String priceText = priceField.getText().trim();
//      int likes = (Integer) likesSpinner.getValue();
      String imagePath = imagePathField.getText().trim();

      System.out.println("Updating game with values:");
      System.out.println("ID: '" + idText + "'");
      System.out.println("Name: '" + name + "'");
      System.out.println("Price: '" + priceText + "'");
//      System.out.println("Likes: " + likes);
      System.out.println("Image Path: '" + imagePath + "'");

      // Validate input
      if (name.isEmpty() || priceText.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Please fill in all required fields (Name, Price)",
            "Input Error",
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      // Convert price text to double
      double price;
      try {
        price = Double.parseDouble(priceText);
        if (price < 0) {
          JOptionPane.showMessageDialog(this,
              "Price cannot be negative",
              "Input Error",
              JOptionPane.ERROR_MESSAGE);
          return;
        }
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
            "Price must be a valid number",
            "Input Error",
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      try {
        Long id = Long.parseLong(idText);

        // Create game object
        Game game = new Game();
        game.setId(id);
        game.setName(name);
        game.setPrice(price);
//        game.setLikes(likes);
        game.setImage(imagePath.isEmpty() ? null : imagePath);

        System.out.println("Updating game with ID: " + id);

        ResponseEntity<Game> responseEntity = gameController.updateGame(id, game);
        System.out.println("Response received with status: " + responseEntity.getStatusCode());

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
          JOptionPane.showMessageDialog(this,
              "Game updated successfully!",
              "Success",
              JOptionPane.INFORMATION_MESSAGE);

          // Refresh table
          refreshGameList();
        } else {
          JOptionPane.showMessageDialog(this,
              "Error updating game. Status: " + responseEntity.getStatusCode(),
              "Error",
              JOptionPane.ERROR_MESSAGE);
        }
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
            "ID must be a valid number",
            "Input Error",
            JOptionPane.ERROR_MESSAGE);
      } catch (Exception e) {
        System.err.println("Error updating game: " + e.getMessage());
        e.printStackTrace();

        // Show more detailed error
        String errorDetails = e.getMessage();
        if (e.getCause() != null) {
          errorDetails += "\nCause: " + e.getCause().getMessage();
        }

        JOptionPane.showMessageDialog(this,
            "Error updating game: " + errorDetails,
            "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    } catch (Exception e) {
      System.err.println("Unexpected error: " + e.getMessage());
      e.printStackTrace();

      JOptionPane.showMessageDialog(this,
          "Unexpected error: " + e.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Delete a game
   */
  private void deleteGame() {
    try {
      int selectedRow = gameTable.getSelectedRow();
      if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this,
            "Please select a game to delete",
            "Selection Error",
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      String idText = (String) tableModel.getValueAt(selectedRow, 0);
      Long gameId;
      try {
        gameId = Long.parseLong(idText);
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
            "Invalid game ID format",
            "Error",
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      int option = JOptionPane.showConfirmDialog(this,
          "Are you sure you want to delete this game?",
          "Confirm Deletion",
          JOptionPane.YES_NO_OPTION);

      if (option == JOptionPane.YES_OPTION) {
        System.out.println("Deleting game with ID: " + gameId);

        try {
          ResponseEntity<Void> responseEntity = gameController.deleteGame(gameId);

          System.out.println("Game deletion request completed with status: " + responseEntity.getStatusCode());

          if (responseEntity.getStatusCode().is2xxSuccessful()) {
            refreshGameList();
            JOptionPane.showMessageDialog(this,
                "Game deleted successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
          } else {
            JOptionPane.showMessageDialog(this,
                "Error deleting game. Status: " + responseEntity.getStatusCode(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
          }
        } catch (Exception e) {
          System.err.println("Error deleting game: " + e.getMessage());
          e.printStackTrace();

          JOptionPane.showMessageDialog(this,
              "Error deleting game: " + e.getMessage(),
              "Error",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    } catch (Exception e) {
      System.err.println("Unexpected error: " + e.getMessage());
      e.printStackTrace();

      JOptionPane.showMessageDialog(this,
          "Unexpected error: " + e.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }
}