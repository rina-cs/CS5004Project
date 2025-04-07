package game.repository;

import game.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
  CartItem findByGameId(Long gameId);
  void deleteByGameId(Long gameId);
}