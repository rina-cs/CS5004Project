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
    JPanel gamesGridPanel = new JPanel(new GridLayout(0, 4, 20, 20));
    gamesGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    add(gamesGridPanel, BorderLayout.CENTER);

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

  // Overload the method to maintain compatibility with existing code
  public void refreshGameList() {
    JPanel gamesPanel = (JPanel) getComponent(1); // This gets the games grid panel
    refreshGameList(gamesPanel);
  }

  private JPanel createGamePanel(Game game) {
    JPanel panel = new JPanel(new BorderLayout(16, 16));
    panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

    // 处理图片
    String imagePath = game.getImage();
    JLabel imageLabel;

    if (imagePath != null) {

      try {
        // 调试信息
        System.out.println("尝试加载图片: " + imagePath);

        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
          ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
          imageLabel = new JLabel(icon, SwingConstants.CENTER);
        } else {
          // 尝试使用类加载器
          URL imageUrl = getClass().getClassLoader().getResource(imagePath);
          if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            imageLabel = new JLabel(icon, SwingConstants.CENTER);
          } else {
            System.out.println("找不到图片文件: " + imagePath);
            imageLabel = new JLabel("Image Not Found", SwingConstants.CENTER);
          }
        }
      } catch (Exception e) {
        System.err.println("图片加载失败: " + e.getMessage());
        imageLabel = new JLabel("Error Loading Image", SwingConstants.CENTER);
      }
    } else {
      // 如果图片路径为null
      imageLabel = new JLabel("No Image Available", SwingConstants.CENTER);
    }

    // 设置一个标准大小以保持UI美观
    imageLabel.setPreferredSize(new Dimension(150, 150));
    panel.add(imageLabel, BorderLayout.CENTER);

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