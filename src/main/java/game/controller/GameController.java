package game.controller;

import game.model.Game;
import game.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class GameController {

  @Autowired
  private GameRepository gameRepository;

  @GetMapping
  public List<Game> getAllGames() {
    return gameRepository.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Game> getGameById(@PathVariable Long id) {
    Optional<Game> game = gameRepository.findById(id);
    if (game.isPresent()) {
      return ResponseEntity.ok(game.get());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping
  public Game createGame(@RequestBody Game game) {
    return gameRepository.save(game);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Game> updateGame(@PathVariable Long id, @RequestBody Game gameDetails) {
    Optional<Game> optionalGame = gameRepository.findById(id);

    if (optionalGame.isPresent()) {
      Game game = optionalGame.get();
      game.setName(gameDetails.getName());
      game.setPrice(gameDetails.getPrice());
      game.setLikes(gameDetails.getLikes());
      game.setImage(gameDetails.getImage());

      Game updatedGame = gameRepository.save(game);
      return ResponseEntity.ok(updatedGame);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
    Optional<Game> game = gameRepository.findById(id);

    if (game.isPresent()) {
      gameRepository.delete(game.get());
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/{id}/like")
  public ResponseEntity<Game> likeGame(@PathVariable Long id) {
    Optional<Game> optionalGame = gameRepository.findById(id);

    if (optionalGame.isPresent()) {
      Game game = optionalGame.get();
      game.setLikes(game.getLikes() + 1);

      Game updatedGame = gameRepository.save(game);
      return ResponseEntity.ok(updatedGame);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/top")
  public List<Game> getTopGames() {
    return gameRepository.findTop10ByOrderByLikesDesc();
  }

  @GetMapping("/cheapest")
  public List<Game> getCheapestGames() {
    return gameRepository.findTop10ByOrderByPriceAsc();
  }

  @GetMapping("/search")
  public List<Game> searchGames(@RequestParam String keyword) {
    return gameRepository.findByNameContainingIgnoreCase(keyword);
  }
}