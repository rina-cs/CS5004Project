package game.service;

import game.model.Game;
import game.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

  private GameService gameService;

  @Mock
  private GameRepository gameRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    gameService = new GameService();

    // 使用反射设置私有字段
    try {
      java.lang.reflect.Field field = GameService.class.getDeclaredField("gameRepository");
      field.setAccessible(true);
      field.set(gameService, gameRepository);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void getAllGames() {
    Game game1 = new Game();
    game1.setId(1L);
    game1.setName("Game 1");

    Game game2 = new Game();
    game2.setId(2L);
    game2.setName("Game 2");

    List<Game> expectedGames = Arrays.asList(game1, game2);

    when(gameRepository.findAll()).thenReturn(expectedGames);

    List<Game> actualGames = gameService.getAllGames();

    assertEquals(2, actualGames.size());
    assertEquals("Game 1", actualGames.get(0).getName());
    assertEquals("Game 2", actualGames.get(1).getName());

    verify(gameRepository).findAll();
  }

  @Test
  void addGame() {
    Game gameToAdd = new Game();
    gameToAdd.setName("New Game");

    Game savedGame = new Game();
    savedGame.setId(1L);
    savedGame.setName("New Game");

    when(gameRepository.save(any(Game.class))).thenReturn(savedGame);

    Game result = gameService.addGame(gameToAdd);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("New Game", result.getName());

    verify(gameRepository).save(gameToAdd);
  }

  @Test
  void getGameById() {
    Game game = new Game();
    game.setId(1L);
    game.setName("Test Game");

    when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
    when(gameRepository.findById(2L)).thenReturn(Optional.empty());

    Optional<Game> found = gameService.getGameById(1L);
    Optional<Game> notFound = gameService.getGameById(2L);

    assertTrue(found.isPresent());
    assertEquals("Test Game", found.get().getName());
    assertFalse(notFound.isPresent());

    verify(gameRepository).findById(1L);
    verify(gameRepository).findById(2L);
  }

  @Test
  void getGameByName() {
    Game game1 = new Game();
    game1.setId(1L);
    game1.setName("Specific Game");

    Game game2 = new Game();
    game2.setId(2L);
    game2.setName("Another Game");

    List<Game> allGames = Arrays.asList(game1, game2);

    when(gameRepository.findAll()).thenReturn(allGames);

    Optional<Game> found = gameService.getGameByName("Specific Game");
    Optional<Game> notFound = gameService.getGameByName("Non-existent Game");

    assertTrue(found.isPresent());
    assertEquals(1L, found.get().getId());
    assertFalse(notFound.isPresent());

    verify(gameRepository, times(2)).findAll();
  }

  @Test
  void deleteGame() {
    doNothing().when(gameRepository).deleteById(1L);

    gameService.deleteGame(1L);

    verify(gameRepository).deleteById(1L);
  }

  @Test
  void updateGame() {
    Game gameToUpdate = new Game();
    gameToUpdate.setId(1L);
    gameToUpdate.setName("Updated Game");

    when(gameRepository.save(gameToUpdate)).thenReturn(gameToUpdate);

    Game result = gameService.updateGame(gameToUpdate);

    assertEquals("Updated Game", result.getName());
    verify(gameRepository).save(gameToUpdate);
  }

  @Test
  void findByNameContainingIgnoreCase() {
    Game game1 = new Game();
    game1.setName("Game with Keyword");

    List<Game> expectedGames = Arrays.asList(game1);

    when(gameRepository.findByNameContainingIgnoreCase("keyword")).thenReturn(expectedGames);

    List<Game> result = gameService.findByNameContainingIgnoreCase("keyword");

    assertEquals(1, result.size());
    assertEquals("Game with Keyword", result.get(0).getName());

    verify(gameRepository).findByNameContainingIgnoreCase("keyword");
  }

  @Test
  void findTop10ByOrderByLikesDesc() {
    Game game1 = new Game();
    game1.setLikes(100);

    Game game2 = new Game();
    game2.setLikes(50);

    List<Game> expectedGames = Arrays.asList(game1, game2);

    when(gameRepository.findTop10ByOrderByLikesDesc()).thenReturn(expectedGames);

    List<Game> result = gameService.findTop10ByOrderByLikesDesc();

    assertEquals(2, result.size());
    assertEquals(100, result.get(0).getLikes());

    verify(gameRepository).findTop10ByOrderByLikesDesc();
  }

  @Test
  void findTop10ByOrderByPriceAsc() {
    Game game1 = new Game();
    game1.setPrice(19.99);

    Game game2 = new Game();
    game2.setPrice(29.99);

    List<Game> expectedGames = Arrays.asList(game1, game2);

    when(gameRepository.findTop10ByOrderByPriceAsc()).thenReturn(expectedGames);

    List<Game> result = gameService.findTop10ByOrderByPriceAsc();

    assertEquals(2, result.size());
    assertEquals(19.99, result.get(0).getPrice());

    verify(gameRepository).findTop10ByOrderByPriceAsc();
  }
}