package game.repository;

import game.model.User;
import java.util.Optional;

public interface UserRepository extends FileRepository<User, Long> {
  Optional<User> findByUsername(String username);
  Optional<User> findByEmail(String email);
}