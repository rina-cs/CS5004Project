package game.repository.impl;

import game.model.Game;
import game.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameRepositoryImplTest {

  private GameRepositoryImpl gameRepository;

  @Mock
  private FileStorageService fileStorageService;

  private List<Game> testGames;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    testGames = new ArrayList<>();
    Game game1 = new Game();
    game1.setId(1L);
    game1.setName("Test Game 1");
    game1.setPrice(19.99);
    game1.setLikes(100);

    Game game2 = new Game();
    game2.setId(2L);
    game2.setName("Test Game 2");
    game2.setPrice(29.99);
    game2.setLikes(50);

    testGames.add(game1);
    testGames.add(game2);

    when(fileStorageService.readList(eq("games.json"), eq(Game.class))).thenReturn(testGames);

    gameRepository = new GameRepositoryImpl(fileStorageService);
  }

  @Test
  void findAll() {
    List<Game> games = gameRepository.findAll();

    assertEquals(2, games.size());
    assertEquals("Test Game 1", games.get(0).getName());
    assertEquals("Test Game 2", games.get(1).getName());

    verify(fileStorageService, times(2)).readList("games.json", Game.class);
  }

  @Test
  void findById() {
    Optional<Game> game = gameRepository.findById(1L);

    assertTrue(game.isPresent());
    assertEquals("Test Game 1", game.get().getName());

    Optional<Game> nonExistentGame = gameRepository.findById(3L);
    assertFalse(nonExistentGame.isPresent());
  }

  @Test
  void save_newGame() {
    Game newGame = new Game();
    newGame.setName("New Game");
    newGame.setPrice(39.99);

    Game savedGame = gameRepository.save(newGame);

    assertNotNull(savedGame.getId());
    assertEquals("New Game", savedGame.getName());
    verify(fileStorageService).writeList(eq("games.json"), any(List.class));
  }

  @Test
  void save_existingGame() {
    Game existingGame = testGames.get(0);
    existingGame.setName("Updated Game");

    Game updatedGame = gameRepository.save(existingGame);

    assertEquals(1L, updatedGame.getId());
    assertEquals("Updated Game", updatedGame.getName());
    verify(fileStorageService).writeList(eq("games.json"), any(List.class));
  }

  @Test
  void deleteById() {
    gameRepository.deleteById(1L);

    verify(fileStorageService).writeList(eq("games.json"), any(List.class));
  }

  @Test
  void findByNameContainingIgnoreCase() {
    List<Game> games = gameRepository.findByNameContainingIgnoreCase("test");

    assertEquals(2, games.size());

    List<Game> games2 = gameRepository.findByNameContainingIgnoreCase("game 1");

    assertEquals(1, games2.size());
    assertEquals("Test Game 1", games2.get(0).getName());
  }

  @Test
  void findTop10ByOrderByLikesDesc() {
    List<Game> topGames = gameRepository.findTop10ByOrderByLikesDesc();

    assertEquals(2, topGames.size());
    assertEquals("Test Game 1", topGames.get(0).getName()); // 100 likes, should be first
    assertEquals("Test Game 2", topGames.get(1).getName()); // 50 likes, should be second
  }

  @Test
  void findTop10ByOrderByPriceAsc() {
    List<Game> cheapestGames = gameRepository.findTop10ByOrderByPriceAsc();

    assertEquals(2, cheapestGames.size());
    assertEquals("Test Game 1", cheapestGames.get(0).getName()); // 19.99, should be first
    assertEquals("Test Game 2", cheapestGames.get(1).getName()); // 29.99, should be second
  }
}