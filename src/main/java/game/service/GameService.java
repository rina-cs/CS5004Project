package game.service;

import game.model.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class GameService {

  private static final String GAMES_FILE = "games.json";
  private final AtomicLong idGenerator = new AtomicLong(1);

  @Autowired
  private FileStorageService fileStorageService;

  public GameService(FileStorageService fileStorageService) {
    this.fileStorageService = fileStorageService;
    initializeIdGenerator();
  }

  private void initializeIdGenerator() {
    List<Game> games = getAllGames();
    if (!games.isEmpty()) {
      long maxId = games.stream().map(Game::getId).max(Long::compareTo).orElse(0L);
      idGenerator.set(maxId+1);
    }
  }

  public List<Game> getAllGames() {
    return fileStorageService.readList(GAMES_FILE, Game.class);
  }

  public Game addGame(Game game) {
    List<Game> games = getAllGames();

    if (game.getId()==null){
      game.setId(idGenerator.getAndIncrement());
    }
    games.add(game);
    fileStorageService.writeList(GAMES_FILE, games);
    return game;
  }

  public Optional<Game> getGameById(Long id) {
    return getAllGames().stream().filter(game -> game.getId().equals(id)).findFirst();
  }

  public Optional<Game> getGameByName(String name) {
    return getAllGames().stream().filter(game -> game.getName().equals(name)).findFirst();
  }


  public void deleteGame(Long id) {
    List<Game> games = getAllGames();
    List<Game> updatedGames = games.stream().filter(game -> game.getId().equals(id)).collect(Collectors.toList());
    fileStorageService.writeList(GAMES_FILE, updatedGames);
  }

  public Game updateGame(Game game) {
    List<Game> games = getAllGames();

    for (int i = 0; i < games.size(); i++) {
      if (games.get(i).getId().equals(game.getId())) {
        games.set(i, game);
        fileStorageService.writeList(GAMES_FILE, games);
        return game;
      }
    }
    return addGame(game);
  }

  public List<Game> findByNameContainingIgnoreCase(String name) {
    String lowercaseName = name.toLowerCase();
    return getAllGames().stream()
        .filter(game -> game.getName().toLowerCase().contains(lowercaseName))
        .collect(Collectors.toList());
  }

  public List<Game> findTop10ByOrderByLikesDesc() {
    return getAllGames().stream()
        .sorted((g1, g2) -> Integer.compare(g2.getLikes(), g1.getLikes()))
        .limit(10)
        .collect(Collectors.toList());
  }

  public List<Game> findTop10ByOrderByPriceAsc() {
    return getAllGames().stream()
        .sorted((g1, g2) -> Double.compare(g1.getPrice(), g2.getPrice()))
        .limit(10)
        .collect(Collectors.toList());
  }
}

