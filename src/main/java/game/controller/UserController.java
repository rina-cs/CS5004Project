package game.controller;

import game.model.User;
import game.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:8080")
public class UserController {

  @Autowired
  private UserService userService;

  // Create User
  @PostMapping
  public User createUser(@RequestBody User user) {
    return userService.createUser(user);
  }

  // Get User by ID
  @GetMapping("/{id}")
  public ResponseEntity<User> getUserById(@PathVariable Long id) {
    Optional<User> user = userService.getUserById(id);
    return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Get All Users
  @GetMapping
  public List<User> getAllUsers() {
    return userService.getAllUsers();
  }

  // Update User
  @PutMapping("/{id}")
  public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
    return ResponseEntity.ok(userService.updateUser(id, user));
  }

  // Delete User
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/login")
  public ResponseEntity<?> loginUser(@RequestBody User loginRequest) {

    Optional<User> user = userService.findByEmail(loginRequest.getEmail());
    if (user.isPresent() && user.get().getPassword().equals(loginRequest.getPassword())) {
      return ResponseEntity.ok(user.get());
    }

    return ResponseEntity.status(401).body("Invalid credentials");
  }

}
