package game.repository.impl;

import game.model.Game;
import game.repository.GameRepository;
import game.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class GameRepositoryImpl implements GameRepository {

  private static final String GAMES_FILE = "games.json";
  private final AtomicLong idGenerator = new AtomicLong(1);

  @Autowired
  private FileStorageService fileStorageService;

  public GameRepositoryImpl(FileStorageService fileStorageService) {
    this.fileStorageService = fileStorageService;
    initializeIdGenerator();
  }

  private void initializeIdGenerator() {
    List<Game> games = findAll();
    if (!games.isEmpty()) {
      long maxId = games.stream()
          .mapToLong(Game::getId)
          .max()
          .orElse(0);
      idGenerator.set(maxId + 1);
    }
  }

  @Override
  public List<Game> findAll() {
    return fileStorageService.readList(GAMES_FILE, Game.class);
  }

  @Override
  public Optional<Game> findById(Long id) {
    return findAll().stream()
        .filter(game -> game.getId().equals(id))
        .findFirst();
  }

  @Override
  public Game save(Game game) {
    List<Game> games = findAll();

    // Check if this is a new game (ID is null or 0)
    if (game.getId() == null || game.getId() == 0L) {
      // Find the maximum existing ID and add 1
      long maxId = games.stream()
          .mapToLong(Game::getId)
          .max()
          .orElse(0L);
      long newId = Math.max(maxId + 1, idGenerator.get());

      // Update the ID generator to ensure no duplicates in the future
      idGenerator.set(newId + 1);

      // Set the new ID on the game
      game.setId(newId);
      System.out.println("Created NEW game with ID: " + newId);
    } else {
      // Check if a game with this ID already exists
      boolean idExists = games.stream()
          .anyMatch(existingGame -> existingGame.getId().equals(game.getId()));

      if (idExists) {
        // This is an update to an existing game - remove the old version
        games = games.stream()
            .filter(existingGame -> !existingGame.getId().equals(game.getId()))
            .collect(Collectors.toList());
        System.out.println("Updating EXISTING game with ID: " + game.getId());
      } else {
        // This game has an ID set but it doesn't exist in our storage
        // This might be a duplicate - assign a new ID to be safe
        long maxId = games.stream()
            .mapToLong(Game::getId)
            .max()
            .orElse(0L);
        long newId = Math.max(maxId + 1, idGenerator.get());
        idGenerator.set(newId + 1);
        game.setId(newId);
        System.out.println("Assigned new ID: " + newId + " to avoid duplicate");
      }
    }

    // Add the game
    games.add(game);

    // Print all game IDs for debugging
    System.out.println("Current games in repository after save:");
    for (Game g : games) {
      System.out.println("  Game ID: " + g.getId() + ", Name: " + g.getName());
    }

    // Save all games to file
    fileStorageService.writeList(GAMES_FILE, games);
    return game;
  }

  @Override
  public void deleteById(Long id) {
    List<Game> games = findAll();
    List<Game> updatedGames = games.stream()
        .filter(game -> !game.getId().equals(id))
        .collect(Collectors.toList());
    fileStorageService.writeList(GAMES_FILE, updatedGames);
  }

  @Override
  public List<Game> findByNameContainingIgnoreCase(String name) {
    String lowercaseName = name.toLowerCase();
    return findAll().stream()
        .filter(game -> game.getName().toLowerCase().contains(lowercaseName))
        .collect(Collectors.toList());
  }

  @Override
  public List<Game> findTop10ByOrderByLikesDesc() {
    return findAll().stream()
        .sorted(Comparator.comparingInt(Game::getLikes).reversed())
        .limit(10)
        .collect(Collectors.toList());
  }

  @Override
  public List<Game> findTop10ByOrderByPriceAsc() {
    return findAll().stream()
        .sorted(Comparator.comparingDouble(Game::getPrice))
        .limit(10)
        .collect(Collectors.toList());
  }
}