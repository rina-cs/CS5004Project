package game.service;

import game.model.User;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

  private static final String USERS_FILE = "users.json";
  private final AtomicLong idGenerator = new AtomicLong(1);

  @Autowired
  private FileStorageService fileStorageService;

  public UserService(FileStorageService fileStorageService) {
    this.fileStorageService = fileStorageService;
    initializeIdGenerator();
  }

  private void initializeIdGenerator() {
    List<User> users = getAllUsers();
    if(!users.isEmpty()) {
      long maxId = users.stream().mapToLong(user -> user.getId()).max().getAsLong();
      idGenerator.set(maxId+1);
    }
  }

  // Create User
  public User createUser(User user) {
    List<User> users = getAllUsers();

    if(user.getId()==null){
      user.setId(idGenerator.getAndIncrement());
    }
    users.add(user);
    fileStorageService.writeList(USERS_FILE, users);
    return user;
  }

  // Get User by ID
  public Optional<User> getUserById(Long id) {
    return getAllUsers().stream().filter(user -> user.getId().equals(id)).findFirst();
  }

  // Get All Users
  public List<User> getAllUsers() {
    return fileStorageService.readList(USERS_FILE,User.class);
  }

  // Update User
  public User updateUser(Long id, User newUserDetails) {
    List<User> users = getAllUsers();

    for (int i = 0; i < users.size(); i++) {
      User user = users.get(i);
      if(user.getId().equals(id)) {
        user.setUsername(newUserDetails.getUsername());
        user.setPassword(newUserDetails.getPassword());
        user.setEmail(newUserDetails.getEmail());
        users.set(i,user);
        fileStorageService.writeList(USERS_FILE, users);
        return user;
      }
    }
    throw new RuntimeException("User not found");
  }

  // Delete User
  public void deleteUser(Long id) {
    List<User> users = getAllUsers();
    List<User> updatedUsers = users.stream().filter(user -> user.getId().equals(id)).collect(
        Collectors.toList());
    fileStorageService.writeList(USERS_FILE, updatedUsers);
  }

  public Optional<User> findByEmail(String email) {
    return getAllUsers().stream().filter(user -> user.getEmail().equals(email)).findFirst();
  }

  public Optional<User> findByUsername(String username) {
    return getAllUsers().stream().filter(user -> user.getUsername().equals(username)).findFirst();
  }
}
