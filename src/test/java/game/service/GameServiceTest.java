package game.service;

import game.model.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

  @Mock
  private FileStorageService fileStorageService;

  @InjectMocks
  private GameService gameService;

  private Game game1;
  private Game game2;
  private List<Game> gameList;

  @BeforeEach
  public void setUp() {
    // Initialize test data
    game1 = new Game();
    game1.setId(1L);
    game1.setName("Game 1");
    game1.setPrice(29.99);
    game1.setLikes(100);
    game1.setImage("/image1.jpg");

    game2 = new Game();
    game2.setId(2L);
    game2.setName("Game 2");
    game2.setPrice(19.99);
    game2.setLikes(200);
    game2.setImage("/image2.jpg");

    gameList = Arrays.asList(game1, game2);
  }

  @Test
  public void testGetAllGames() {
    // Given
    when(fileStorageService.readList(anyString(), eq(Game.class))).thenReturn(gameList);

    // When
    List<Game> result = gameService.getAllGames();

    // Then
    assertEquals(2, result.size());
    assertEquals(game1.getId(), result.get(0).getId());
    assertEquals(game2.getId(), result.get(1).getId());
    verify(fileStorageService, times(2)).readList("games.json", Game.class);
  }

  @Test
  public void testAddGame() {
    // Given
    Game newGame = new Game();
    newGame.setName("New Game");
    newGame.setPrice(39.99);
    newGame.setLikes(0);
    newGame.setImage("/new-image.jpg");

    List<Game> existingGames = new ArrayList<>(gameList);
    when(fileStorageService.readList(anyString(), eq(Game.class))).thenReturn(existingGames);
    doNothing().when(fileStorageService).writeList(anyString(), anyList());

    // When
    Game result = gameService.addGame(newGame);

    // Then
    assertNotNull(result.getId());
    assertEquals("New Game", result.getName());
    verify(fileStorageService, times(2)).readList("games.json", Game.class);
    verify(fileStorageService, times(1)).writeList(eq("games.json"), anyList());
  }

  @Test
  public void testGetGameById() {
    // Given
    when(fileStorageService.readList(anyString(), eq(Game.class))).thenReturn(gameList);

    // When
    Optional<Game> result = gameService.getGameById(1L);

    // Then
    assertTrue(result.isPresent());
    assertEquals(game1.getId(), result.get().getId());
    assertEquals(game1.getName(), result.get().getName());
    verify(fileStorageService, times(2)).readList("games.json", Game.class);
  }

  @Test
  public void testGetGameByIdNotFound() {
    // Given
    when(fileStorageService.readList(anyString(), eq(Game.class))).thenReturn(gameList);

    // When
    Optional<Game> result = gameService.getGameById(3L);

    // Then
    assertFalse(result.isPresent());
    verify(fileStorageService, times(2)).readList("games.json", Game.class);
  }

  @Test
  public void testGetGameByName() {
    // Given
    when(fileStorageService.readList(anyString(), eq(Game.class))).thenReturn(gameList);

    // When
    Optional<Game> result = gameService.getGameByName("Game 1");

    // Then
    assertTrue(result.isPresent());
    assertEquals(game1.getId(), result.get().getId());
    assertEquals(game1.getName(), result.get().getName());
    verify(fileStorageService, times(2)).readList("games.json", Game.class);
  }

  @Test
  public void testDeleteGame() {
    // Given
    List<Game> existingGames = new ArrayList<>(gameList);
    when(fileStorageService.readList(anyString(), eq(Game.class))).thenReturn(existingGames);
    doNothing().when(fileStorageService).writeList(anyString(), anyList());

    // When
    gameService.deleteGame(1L);

    // Then
    verify(fileStorageService, times(2)).readList("games.json", Game.class);
    verify(fileStorageService, times(1)).writeList(eq("games.json"), anyList());
  }

  @Test
  public void testUpdateGame() {
    // Given
    Game updatedGame = new Game();
    updatedGame.setId(1L);
    updatedGame.setName("Updated Game");
    updatedGame.setPrice(49.99);
    updatedGame.setLikes(150);
    updatedGame.setImage("/updated-image.jpg");

    List<Game> existingGames = new ArrayList<>(gameList);
    when(fileStorageService.readList(anyString(), eq(Game.class))).thenReturn(existingGames);
    doNothing().when(fileStorageService).writeList(anyString(), anyList());

    // When
    Game result = gameService.updateGame(updatedGame);

    // Then
    assertEquals(1L, result.getId());
    assertEquals("Updated Game", result.getName());
    assertEquals(49.99, result.getPrice(), 0.01);
    assertEquals(150, result.getLikes());
    assertEquals("/updated-image.jpg", result.getImage());
    verify(fileStorageService, times(2)).readList("games.json", Game.class);
    verify(fileStorageService, times(1)).writeList(eq("games.json"), anyList());
  }

  @Test
  public void testFindByNameContainingIgnoreCase() {
    // Given
    Game game3 = new Game();
    game3.setId(3L);
    game3.setName("Another Game");
    game3.setPrice(39.99);
    game3.setLikes(50);
    game3.setImage("/image3.jpg");

    List<Game> allGames = new ArrayList<>(gameList);
    allGames.add(game3);

    when(fileStorageService.readList(anyString(), eq(Game.class))).thenReturn(allGames);

    // When
    List<Game> result = gameService.findByNameContainingIgnoreCase("game");

    // Then
    assertEquals(3, result.size());
    verify(fileStorageService, times(2)).readList("games.json", Game.class);
  }

  @Test
  public void testFindTop10ByOrderByLikesDesc() {
    // Given
    List<Game> manyGames = new ArrayList<>();
    for (int i = 1; i <= 15; i++) {
      Game game = new Game();
      game.setId((long) i);
      game.setName("Game " + i);
      game.setLikes(i * 10);
      manyGames.add(game);
    }

    when(fileStorageService.readList(anyString(), eq(Game.class))).thenReturn(manyGames);

    // When
    List<Game> result = gameService.findTop10ByOrderByLikesDesc();

    // Then
    assertEquals(10, result.size());
    assertEquals(15L, result.get(0).getId());
    assertEquals(14L, result.get(1).getId());
    verify(fileStorageService, times(2)).readList("games.json", Game.class);
  }

  @Test
  public void testFindTop10ByOrderByPriceAsc() {
    // Given
    List<Game> manyGames = new ArrayList<>();
    for (int i = 1; i <= 15; i++) {
      Game game = new Game();
      game.setId((long) i);
      game.setName("Game " + i);
      game.setPrice(50 - i);
      manyGames.add(game);
    }

    when(fileStorageService.readList(anyString(), eq(Game.class))).thenReturn(manyGames);

    // When
    List<Game> result = gameService.findTop10ByOrderByPriceAsc();

    // Then
    assertEquals(10, result.size());
    assertEquals(15L, result.get(0).getId());
    assertEquals(14L, result.get(1).getId());
    verify(fileStorageService, times(2)).readList("games.json", Game.class);
  }
}