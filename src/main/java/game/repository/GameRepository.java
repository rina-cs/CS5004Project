package game.repository;

import game.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

  List<Game> findByNameContainingIgnoreCase(String name);

  List<Game> findTop10ByOrderByLikesDesc();

  List<Game> findTop10ByOrderByPriceAsc();

}
