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

    if (game.getId() == null) {
      game.setId(idGenerator.getAndIncrement());
    } else {
      games = games.stream()
          .filter(existingGame -> !existingGame.getId().equals(game.getId()))
          .collect(Collectors.toList());
    }

    games.add(game);
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
  public long count() {
    return findAll().size();
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