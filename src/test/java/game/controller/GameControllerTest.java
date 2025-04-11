package game.controller;

import game.model.Game;
import game.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class GameControllerTest {

  private MockMvc mockMvc;

  @Mock
  private GameService gameService;

  @InjectMocks
  private GameController gameController;

  private Game game1;
  private Game game2;
  private List<Game> gameList;

  @BeforeEach
  public void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();

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
  public void testGetAllGames() throws Exception {
    // Given
    when(gameService.getAllGames()).thenReturn(gameList);

    // When/Then
    mockMvc.perform(get("/api/games"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].name", is("Game 1")))
        .andExpect(jsonPath("$[1].id", is(2)))
        .andExpect(jsonPath("$[1].name", is("Game 2")));

    verify(gameService, times(1)).getAllGames();
  }

  @Test
  public void testGetGameById() throws Exception {
    // Given
    when(gameService.getGameById(1L)).thenReturn(Optional.of(game1));

    // When/Then
    mockMvc.perform(get("/api/games/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.name", is("Game 1")))
        .andExpect(jsonPath("$.price", is(29.99)))
        .andExpect(jsonPath("$.likes", is(100)))
        .andExpect(jsonPath("$.image", is("/image1.jpg")));

    verify(gameService, times(1)).getGameById(1L);
  }

  @Test
  public void testGetGameByIdNotFound() throws Exception {
    // Given
    when(gameService.getGameById(3L)).thenReturn(Optional.empty());

    // When/Then
    mockMvc.perform(get("/api/games/3"))
        .andExpect(status().isNotFound());

    verify(gameService, times(1)).getGameById(3L);
  }

  @Test
  public void testCreateGame() throws Exception {
    // Given
    Game newGame = new Game();
    newGame.setName("New Game");
    newGame.setPrice(39.99);
    newGame.setLikes(0);
    newGame.setImage("/new-image.jpg");

    Game savedGame = new Game();
    savedGame.setId(3L);
    savedGame.setName(newGame.getName());
    savedGame.setPrice(newGame.getPrice());
    savedGame.setLikes(newGame.getLikes());
    savedGame.setImage(newGame.getImage());

    when(gameService.addGame(any(Game.class))).thenReturn(savedGame);

    // When/Then
    mockMvc.perform(post("/api/games")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"New Game\",\"price\":39.99,\"likes\":0,\"image\":\"/new-image.jpg\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(3)))
        .andExpect(jsonPath("$.name", is("New Game")))
        .andExpect(jsonPath("$.price", is(39.99)))
        .andExpect(jsonPath("$.likes", is(0)))
        .andExpect(jsonPath("$.image", is("/new-image.jpg")));

    verify(gameService, times(1)).addGame(any(Game.class));
  }

  @Test
  public void testUpdateGame() throws Exception {
    // Given
    Game updatedGame = new Game();
    updatedGame.setId(1L);
    updatedGame.setName("Updated Game");
    updatedGame.setPrice(49.99);
    updatedGame.setLikes(150);
    updatedGame.setImage("/updated-image.jpg");

    when(gameService.getGameById(1L)).thenReturn(Optional.of(game1));
    when(gameService.updateGame(any(Game.class))).thenReturn(updatedGame);

    // When/Then
    mockMvc.perform(put("/api/games/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Updated Game\",\"price\":49.99,\"likes\":150,\"image\":\"/updated-image.jpg\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.name", is("Updated Game")))
        .andExpect(jsonPath("$.price", is(49.99)))
        .andExpect(jsonPath("$.likes", is(150)))
        .andExpect(jsonPath("$.image", is("/updated-image.jpg")));

    verify(gameService, times(1)).getGameById(1L);
    verify(gameService, times(1)).updateGame(any(Game.class));
  }

  @Test
  public void testUpdateGameNotFound() throws Exception {
    // Given
    when(gameService.getGameById(3L)).thenReturn(Optional.empty());

    // When/Then
    mockMvc.perform(put("/api/games/3")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Updated Game\",\"price\":49.99,\"likes\":150,\"image\":\"/updated-image.jpg\"}"))
        .andExpect(status().isNotFound());

    verify(gameService, times(1)).getGameById(3L);
    verify(gameService, never()).updateGame(any(Game.class));
  }

  @Test
  public void testDeleteGame() throws Exception {
    // Given
    when(gameService.getGameById(1L)).thenReturn(Optional.of(game1));
    doNothing().when(gameService).deleteGame(1L);

    // When/Then
    mockMvc.perform(delete("/api/games/1"))
        .andExpect(status().isOk());

    verify(gameService, times(1)).getGameById(1L);
    verify(gameService, times(1)).deleteGame(1L);
  }

  @Test
  public void testDeleteGameNotFound() throws Exception {
    // Given
    when(gameService.getGameById(3L)).thenReturn(Optional.empty());

    // When/Then
    mockMvc.perform(delete("/api/games/3"))
        .andExpect(status().isNotFound());

    verify(gameService, times(1)).getGameById(3L);
    verify(gameService, never()).deleteGame(anyLong());
  }

  @Test
  public void testLikeGame() throws Exception {
    // Given
    Game likedGame = new Game();
    likedGame.setId(1L);
    likedGame.setName("Game 1");
    likedGame.setPrice(29.99);
    likedGame.setLikes(101);
    likedGame.setImage("/image1.jpg");

    when(gameService.getGameById(1L)).thenReturn(Optional.of(game1));
    when(gameService.updateGame(any(Game.class))).thenReturn(likedGame);

    // When/Then
    mockMvc.perform(put("/api/games/1/like"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.name", is("Game 1")))
        .andExpect(jsonPath("$.likes", is(101)));

    verify(gameService, times(1)).getGameById(1L);
    verify(gameService, times(1)).updateGame(any(Game.class));
  }

  @Test
  public void testLikeGameNotFound() throws Exception {
    // Given
    when(gameService.getGameById(3L)).thenReturn(Optional.empty());

    // When/Then
    mockMvc.perform(put("/api/games/3/like"))
        .andExpect(status().isNotFound());

    verify(gameService, times(1)).getGameById(3L);
    verify(gameService, never()).updateGame(any(Game.class));
  }

  @Test
  public void testGetTopGames() throws Exception {
    // Given
    when(gameService.findTop10ByOrderByLikesDesc()).thenReturn(gameList);

    // When/Then
    mockMvc.perform(get("/api/games/top"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[1].id", is(2)));

    verify(gameService, times(1)).findTop10ByOrderByLikesDesc();
  }

  @Test
  public void testGetCheapestGames() throws Exception {
    // Given
    when(gameService.findTop10ByOrderByPriceAsc()).thenReturn(gameList);

    // When/Then
    mockMvc.perform(get("/api/games/cheapest"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[1].id", is(2)));

    verify(gameService, times(1)).findTop10ByOrderByPriceAsc();
  }

  @Test
  public void testSearchGames() throws Exception {
    // Given
    when(gameService.findByNameContainingIgnoreCase("game")).thenReturn(gameList);

    // When/Then
    mockMvc.perform(get("/api/games/search?keyword=game"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[1].id", is(2)));

    verify(gameService, times(1)).findByNameContainingIgnoreCase("game");
  }
}