package game.config;

import game.model.Game;
import game.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class FileDataInitializer implements CommandLineRunner {

  @Autowired
  private GameService gameService;

  @Override
  public void run(String... args) throws Exception {
    if (gameService.getAllGames().isEmpty()) {
      List<Game> initialGames = Arrays.asList(
          createGame("The Legend of Zelda", 30.0, 100, "/game-platform/src/assets/images/zelda.jpg"),
          createGame("Super Mario Odyssey", 20.0, 120, "/game-platform/src/assets/images/mario.jpg"),
          createGame("Minecraft", 30.0, 200, "/game-platform/src/assets/images/minecraft.jpg"),
          createGame("Cyberpunk 2077", 30.0, 90, "/game-platform/src/assets/images/cyberpunk.jpg"),
          createGame("Final Fantasy XIV", 30.0, 150, "/game-platform/src/assets/images/final-fantasy.jpg"),
          createGame("Call of Duty", 30.0, 85, "/game-platform/src/assets/images/cod.jpg"),
          createGame("Grand Theft Auto V", 50.0, 300, "/game-platform/src/assets/images/gta.jpg"),
          createGame("Elden Ring", 30.0, 250, "/game-platform/src/assets/images/elden-ring.jpg"),
          createGame("Hollow Knight", 20.0, 170, "/game-platform/src/assets/images/hollow-knight.jpg"),
          createGame("Stardew Valley", 15.0, 190, "/game-platform/src/assets/images/stardew.jpg")
      );

      for (Game game : initialGames) {
        gameService.addGame(game);
      }
      System.out.println("Database initialized with sample games");
    }
  }

  private Game createGame(String name, double price, int likes, String image) {
    Game game = new Game();
    game.setName(name);
    game.setPrice(price);
    game.setLikes(likes);
    game.setImage(image);
    return game;
  }
}