package game.config;

import game.model.Game;
import game.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class GameDataInitializer implements CommandLineRunner {

  @Autowired
  private GameRepository gameRepository;

  @Override
  public void run(String... args) throws Exception {
    if (gameRepository.count() == 0) {
      List<Game> initialGames = Arrays.asList(
          createGame("The Legend of Zelda", 30.0, 100, "/images/zelda.jpg"),
          createGame("Super Mario Odyssey", 20.0, 120, "/images/mario.jpg"),
          createGame("Minecraft", 30.0, 200, "/images/minecraft.jpg"),
          createGame("Cyberpunk 2077", 30.0, 90, "/images/cyberpunk.jpg"),
          createGame("Final Fantasy XIV", 30.0, 150, "/images/final-fantasy.jpg"),
          createGame("Call of Duty", 30.0, 85, "/images/cod.jpg"),
          createGame("Grand Theft Auto V", 50.0, 300, "/images/gta.jpg"),
          createGame("Elden Ring", 30.0, 250, "/images/elden-ring.jpg"),
          createGame("Hollow Knight", 20.0, 170, "/images/hollow-knight.jpg"),
          createGame("Stardew Valley", 15.0, 190, "/images/stardew.jpg")
      );
      gameRepository.saveAll(initialGames);
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