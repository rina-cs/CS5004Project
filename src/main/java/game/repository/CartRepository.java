package game.repository;

import game.model.Cart;
import java.util.Optional;

public interface CartRepository extends FileRepository<Cart, Long> {
  Optional<Cart> findByUserId(Long userId);
}