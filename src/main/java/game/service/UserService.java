package game.service;

import game.model.User;
import game.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  // Create User
  public User createUser(User user) {
    return userRepository.save(user);
  }

  // Get User by ID
  public Optional<User> getUserById(Long id) {
    return userRepository.findById(id);
  }

  // Get All Users
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  // Update User
  public User updateUser(Long id, User newUserDetails) {
    return userRepository.findById(id).map(user -> {
      user.setUsername(newUserDetails.getUsername());
      user.setEmail(newUserDetails.getEmail());
      user.setPassword(newUserDetails.getPassword());
      return userRepository.save(user);
    }).orElseThrow(() -> new RuntimeException("User not found"));
  }

  // Delete User
  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }

  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }
}
