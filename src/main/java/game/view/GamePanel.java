package game.view;

import game.controller.GameController;
import game.model.Game;
import game.model.User;
import game.service.CartService;

import java.net.URL;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.io.File;
import org.springframework.http.ResponseEntity;

public class GamePanel extends JPanel {
  private final GameController gameController;
  private final CartService cartService;
  private final User currentUser;
  private final MainWindow mainWindow;
  private final JPanel gamesGridPanel;

  public GamePanel(GameController gameController, CartService cartService, User currentUser, MainWindow mainWindow) {
    this.gameController = gameController;
    this.cartService = cartService;
    this.currentUser = currentUser;
    this.mainWindow = mainWindow;

    // Update the layout to BorderLayout to allow for top panel with buttons
    setLayout(new BorderLayout(10, 10));

    // Create top panel with login button
    JPanel topPanel = new JPanel(new BorderLayout());

    // Add login button at the left top corner
    JButton loginButton = new JButton("Login");
    loginButton.addActionListener(e -> mainWindow.showLoginPanel());
    JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    topRightPanel.add(loginButton);
    topPanel.add(topRightPanel, BorderLayout.EAST);

    // Add game list title at the left top corner
    JLabel gameListTitle = new JLabel("Game List");
    gameListTitle.setFont((new Font("Arial", Font.BOLD, 24)));
    gameListTitle.setHorizontalAlignment(SwingConstants.CENTER);
    gameListTitle.setBorder(BorderFactory.createEmptyBorder(15, 30, 0, 0));
    topPanel.add(gameListTitle, BorderLayout.WEST);

    // Add the top panel to the main panel
    add(topPanel, BorderLayout.NORTH);

    // Create a panel for game grid
    gamesGridPanel = new JPanel(new GridLayout(0, 4, 10, 10));
    gamesGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Wrap the gamesGridPanel in a JScrollPane
    JScrollPane scrollPane = new JScrollPane(gamesGridPanel);
    scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    // Manage the speed of scrolling
    JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
    verticalScrollBar.setUnitIncrement(16);
    verticalScrollBar.setBlockIncrement(32);

    JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
    horizontalScrollBar.setUnitIncrement(16);
    horizontalScrollBar.setBlockIncrement(32);

    add(scrollPane, BorderLayout.CENTER);

    refreshGameList(gamesGridPanel);
  }

  public void refreshGameList(JPanel gamesPanel) {
    try {
      // Clear panel
      gamesPanel.removeAll();

      // Get all games from controller
      List<Game> games = gameController.getAllGames();

      if (games == null || games.isEmpty()) {
        System.out.println("No games found in the database");
        return;
      }

      // Sort games by likes in descending order
      games.sort((g1, g2) -> Integer.compare(g2.getLikes(), g1.getLikes()));

      System.out.println("Found " + games.size() + " games");

      // Add games to panel
      for (Game game : games) {
        JPanel gamePanel = createGamePanel(game);
        gamesPanel.add(gamePanel);
      }

      // Revalidate and repaint the panel
      gamesPanel.revalidate();
      gamesPanel.repaint();
    } catch (Exception e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(this,
          "Error refreshing game list: " + e.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private JPanel createGamePanel(Game game) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setPreferredSize(new Dimension(250, 280));
    panel.setMaximumSize(new Dimension(250, 280));
    panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

    // Import Image
    String imagePath = game.getImage();
    ImagePanel imagePanel = new ImagePanel(imagePath);
    imagePanel.setPreferredSize(new Dimension(250, 180));
    imagePanel.setLayout(new BorderLayout());
    panel.add(imagePanel);

    // Game details
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
    addToCartButton.addActionListener(e -> addToCart(game.getId()));
    priceAndButton.add(priceLabel, BorderLayout.WEST);
    priceAndButton.add(addToCartButton, BorderLayout.EAST);
    detailsPanel.add(priceAndButton);

    panel.add(detailsPanel, BorderLayout.SOUTH);

    return panel;
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