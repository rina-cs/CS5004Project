package game.service;

import game.model.Game;
import game.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {

  @Autowired
  private GameRepository gameRepository;


  public List<Game> getAllGames() {
    return gameRepository.findAll();
  }

  public Game addGame(Game game) {
    return gameRepository.save(game);
  }

  public Optional<Game> getGameById(Long id) {
    return gameRepository.findById(id);
  }

  public Optional<Game> getGameByName(String name) {
    return getAllGames().stream().filter(game -> game.getName().equals(name)).findFirst();
  }


  public void deleteGame(Long id) {
    gameRepository.deleteById(id);
  }

  public Game updateGame(Game game) {
    return gameRepository.save(game);
  }

  public List<Game> findByNameContainingIgnoreCase(String name) {
    return gameRepository.findByNameContainingIgnoreCase(name);
  }

  public List<Game> findTop10ByOrderByLikesDesc() {
    return gameRepository.findTop10ByOrderByLikesDesc();
  }

  public List<Game> findTop10ByOrderByPriceAsc() {
    return gameRepository.findTop10ByOrderByPriceAsc();
  }
}

