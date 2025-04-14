package game.view;

import game.controller.GameController;
import game.controller.UserController;
import game.model.User;
import org.springframework.http.ResponseEntity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * MainWindow serves as the primary container for the application UI.
 * It manages authentication and switching between admin and regular user views.
 */
public class MainWindow extends JFrame {
  // Controllers
  private final UserController userController;
  private final GameController gameController;

  // Currently logged in user
  private User currentUser;

  // Layout components
  private JPanel mainPanel;
  private CardLayout cardLayout;

  // Panels
  private JPanel loginPanel;
  private AdminGamePanel adminGamePanel;
  private UserGamePanel userGamePanel;

  // Login components
  private JTextField emailField;
  private JPasswordField passwordField;
  private JButton loginButton;
  private JLabel statusLabel;

  /**
   * Constructs the main window with required controllers
   */
  public MainWindow(UserController userController, GameController gameController) {
    this.userController = userController;
    this.gameController = gameController;

    setTitle("Game Inventory Management System");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1200, 800);
    setLocationRelativeTo(null);

    initializeComponents();
    setupLayout();

    // Start with login panel
    cardLayout.show(mainPanel, "login");
  }

  /**
   * Initialize UI components
   */
  private void initializeComponents() {
    // Set up card layout for panel switching
    cardLayout = new CardLayout();
    mainPanel = new JPanel(cardLayout);

    // Create login panel
    createLoginPanel();
  }

  /**
   * Create login panel with email/password fields
   */
  private void createLoginPanel() {
    loginPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);

    // Create login components
    JLabel titleLabel = new JLabel("Game Inventory Management", JLabel.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

    JLabel emailLabel = new JLabel("Email:");
    JLabel passwordLabel = new JLabel("Password:");

    emailField = new JTextField(20);
    passwordField = new JPasswordField(20);
    loginButton = new JButton("Login");
    statusLabel = new JLabel(" ");
    statusLabel.setForeground(Color.RED);

    // Add components to panel with proper layout
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    loginPanel.add(titleLabel, gbc);

    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.EAST;
    loginPanel.add(emailLabel, gbc);

    gbc.gridx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    loginPanel.add(emailField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.EAST;
    loginPanel.add(passwordLabel, gbc);

    gbc.gridx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    loginPanel.add(passwordField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    loginPanel.add(loginButton, gbc);

    gbc.gridy = 4;
    loginPanel.add(statusLabel, gbc);

    // Add login button action
    loginButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        attemptLogin();
      }
    });
  }

  /**
   * Handle login attempt
   */
  private void attemptLogin() {
    String email = emailField.getText();
    String password = new String(passwordField.getPassword());

    if (email.isEmpty() || password.isEmpty()) {
      statusLabel.setText("Email and password cannot be empty");
      return;
    }

    try {
      // Create a user object for login
      User loginRequest = new User();
      loginRequest.setEmail(email);
      loginRequest.setPassword(password);

      // Call the login endpoint
      ResponseEntity<?> response = userController.loginUser(loginRequest);

      if (response.getStatusCode().is2xxSuccessful() && response.getBody() instanceof User) {
        currentUser = (User) response.getBody();

        // Clear login fields
        emailField.setText("");
        passwordField.setText("");
        statusLabel.setText("");

        // Check if user is admin
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

  /**
   * Initialize and show the admin panel
   */
  private void showAdminPanel() {
    // Create admin panel if it doesn't exist
    if (adminGamePanel == null) {
      adminGamePanel = new AdminGamePanel(gameController, this);
      mainPanel.add(adminGamePanel, "admin");
    }

    // Show admin panel
    cardLayout.show(mainPanel, "admin");
  }

  /**
   * Initialize and show the user panel
   */
  private void showUserPanel() {
    // Create user panel if it doesn't exist
    if (userGamePanel == null) {
      userGamePanel = new UserGamePanel(gameController, currentUser, this);
      mainPanel.add(userGamePanel, "user");
    } else {
      userGamePanel.setCurrentUser(currentUser);
      userGamePanel.refreshGameList();
    }

    // Show user panel
    cardLayout.show(mainPanel, "user");
  }

  /**
   * Set up the main layout
   */
  private void setupLayout() {
    // Add panels to card layout
    mainPanel.add(loginPanel, "login");

    // Add main panel to frame
    getContentPane().add(mainPanel);
  }

  /**
   * Return to login panel (logout)
   */
  public void showLoginPanel() {
    cardLayout.show(mainPanel, "login");
    currentUser = null;
  }

  /**
   * Main method to launch the application
   */
  public static void main(String[] args) {
    // Create required controllers - in a real app, these would be injected
    UserController userController = new UserController();
    GameController gameController = new GameController();

    // Launch UI
    SwingUtilities.invokeLater(() -> {
      MainWindow mainWindow = new MainWindow(userController, gameController);
      mainWindow.setVisible(true);
    });
  }
}