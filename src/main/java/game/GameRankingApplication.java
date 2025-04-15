package game;

import game.controller.GameController;
import game.controller.UserController;
import game.model.User;
import game.repository.UserRepository;
import game.service.CartService;
import game.service.GameService;
import game.view.MainWindow;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
public class GameRankingApplication {

	public static void main(String[] args) {
		// Important: Set headless mode to false
		System.setProperty("java.awt.headless", "false");
		SpringApplication.run(GameRankingApplication.class, args);
	}

	// Add a CommandLineRunner bean to initialize data
	@Bean
	public CommandLineRunner initializeData(UserRepository userRepository) {
		return args -> {
			// Check if admin user exists
			Optional<User> existingAdmin = userRepository.findByEmail("admin@neu.com");

			if (existingAdmin.isEmpty()) {
				// Create default admin user
				User adminUser = new User();
				adminUser.setUsername("admin");
				adminUser.setEmail("admin@neu.com");
				adminUser.setPassword("CS5004");
				adminUser.setAdmin(true);

				userRepository.save(adminUser);

				System.out.println("Default admin user created with email: admin@neu.com and password: CS5004");
			}
		};
	}

	@Component
	public static class SwingUIStarter {

		private final UserController userController;
		private final GameController gameController;
		private final CartService cartService;
		private final GameService gameService;

		public SwingUIStarter(UserController userController, GameController gameController, CartService cartService, GameService gameService) {
			this.userController = userController;
			this.gameController = gameController;
			this.cartService = cartService;
			this.gameService = gameService;
		}

		@EventListener(ApplicationReadyEvent.class)
		public void startSwingUI() {
			if (!GraphicsEnvironment.isHeadless()) {
				SwingUtilities.invokeLater(() -> {
					try {
						MainWindow mainWindow = new MainWindow(userController, gameController, cartService, gameService);
						mainWindow.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			} else {
				System.out.println("Headless environment detected - not starting GUI");
			}
		}
	}
}