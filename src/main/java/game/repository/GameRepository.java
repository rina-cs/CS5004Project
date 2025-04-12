package game.repository;

import game.model.Game;
import java.util.List;

public interface GameRepository extends FileRepository<Game, Long> {
  List<Game> findByNameContainingIgnoreCase(String name);
  List<Game> findTop10ByOrderByLikesDesc();
  List<Game> findTop10ByOrderByPriceAsc();
}