package game.view;

import game.controller.GameController;
import game.controller.UserController;
import game.model.User;
import game.service.CartService;
import game.service.GameService;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;
import org.springframework.http.ResponseEntity;

public class MainWindow extends JFrame {
  private final UserController userController;
  private final GameController gameController;
  private final CartService cartService;
  private final GameService gameService;

  private JPanel mainPanel;
  private CardLayout cardLayout;

  private JPanel loginPanel;
  private AdminGamePanel adminGamePanel;
  private UserGamePanel userGamePanel;
  private GamePanel gamePanel;
  private CartPanel cartPanel;

  private User currentUser;

  public MainWindow(UserController userController, GameController gameController, CartService cartService, GameService gameService) {
    this.userController = userController;
    this.gameController = gameController;
    this.cartService = cartService;
    this.gameService = gameService;
    this.currentUser = null;

    setTitle("Game Inventory Management System");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1200, 800);
    setLocationRelativeTo(null);

    initializeComponents();
    setupLayout();

    // Start with game panel
    cardLayout.show(mainPanel, "gamePanel");
  }

  private void initializeComponents() {
    cardLayout = new CardLayout();
    mainPanel = new JPanel(cardLayout);

    createLoginPanel();
    createGamePanel();
  }

  private void createLoginPanel() {
    loginPanel = new JPanel(new BorderLayout());

    // Create back button
    JButton backButton = new JButton("Back");
    backButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        cardLayout.show(mainPanel, "gamePanel"); // Switch back to game panel
      }
    });

    // Add back button to the left top corner
    JPanel topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    topLeftPanel.add(backButton);
    loginPanel.add(topLeftPanel, BorderLayout.NORTH);

    // Main login form
    JPanel loginFormPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);

    JLabel titleLabel = new JLabel("Game Inventory Management", JLabel.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

    JLabel emailLabel = new JLabel("Email:");
    JLabel passwordLabel = new JLabel("Password:");

    JTextField emailField = new JTextField(20);
    JPasswordField passwordField = new JPasswordField(20);
    JButton loginButton = new JButton("Login");
    JLabel statusLabel = new JLabel(" ");
    statusLabel.setForeground(Color.RED);

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    loginFormPanel.add(titleLabel, gbc);

    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.EAST;
    loginFormPanel.add(emailLabel, gbc);

    gbc.gridx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    loginFormPanel.add(emailField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.EAST;
    loginFormPanel.add(passwordLabel, gbc);

    gbc.gridx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    loginFormPanel.add(passwordField, gbc);

    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.anchor = GridBagConstraints.EAST;
    loginFormPanel.add(loginButton, gbc);

    gbc.gridy = 4;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    loginFormPanel.add(statusLabel, gbc);

    loginButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        attemptLogin(emailField, passwordField, statusLabel);
      }
    });

    loginPanel.add(loginFormPanel, BorderLayout.CENTER);

    mainPanel.add(loginPanel, "login");
  }

  private void createGamePanel() {
    gamePanel = new GamePanel(gameController, cartService, currentUser, this);
    mainPanel.add(gamePanel, "gamePanel");
  }

  private void createCartPanel() {
    cartPanel = new CartPanel(cartService, gameService, this);
    mainPanel.add(cartPanel, "cartPanel");
  }

  private void attemptLogin(JTextField emailField, JPasswordField passwordField, JLabel statusLabel) {
    String email = emailField.getText();
    String password = new String(passwordField.getPassword());

    if (email.isEmpty() || password.isEmpty()) {
      statusLabel.setText("Email and password cannot be empty");
      return;
    }

    try {
      User loginRequest = new User();
      loginRequest.setEmail(email);
      loginRequest.setPassword(password);

      ResponseEntity<?> response = userController.loginUser(loginRequest);

      if (response.getStatusCode().is2xxSuccessful() && response.getBody() instanceof User) {
        currentUser = (User) response.getBody();

        emailField.setText("");
        passwordField.setText("");
        statusLabel.setText("");

        if (currentUser.isAdmin()) {
          showAdminPanel();
        } else {
          showUserPanel();
        }
      } else {
        statusLabel.setText("Invalid email or password");
      }
    } catch (Exception ex) {
      statusLabel.setText("Login error: " + ex.getMessage());
    }
  }

  public void showLoginPanel() {
    cardLayout.show(mainPanel, "login");
    currentUser = null;
  }

  public void showAdminPanel() {
    if (adminGamePanel == null) {
      adminGamePanel = new AdminGamePanel(gameController, this);
      mainPanel.add(adminGamePanel, "admin");
    }
    cardLayout.show(mainPanel, "admin");
  }

  public void showUserPanel() {
    if (userGamePanel == null) {
      userGamePanel = new UserGamePanel(gameController, userController, currentUser, this);
      mainPanel.add(userGamePanel, "user");
    } else {
      userGamePanel.setCurrentUser(currentUser);
      userGamePanel.refreshGameList();
    }
    cardLayout.show(mainPanel, "user");
  }

  public void showGamePanel() {
    cardLayout.show(mainPanel, "gamePanel");
  }

  public void showCartPanel() {
    if (cartPanel == null) {
      createCartPanel();
    }
    cardLayout.show(mainPanel, "cartPanel");
  }

  public User getCurrentUser() {
    return currentUser;
  }

  public CartService getCartService() {
    return cartService;
  }

  public GameService getGameService() {
    return gameService;
  }

  private void setupLayout() {
    getContentPane().add(mainPanel);
  }

  public static void main(String[] args) {
    UserController userController = new UserController();
    GameController gameController = new GameController();
    CartService cartService = new CartService();
    GameService gameService = new GameService();

    SwingUtilities.invokeLater(() -> {
      MainWindow mainWindow = new MainWindow(userController, gameController, cartService, gameService);
      mainWindow.setVisible(true);
    });
  }
}