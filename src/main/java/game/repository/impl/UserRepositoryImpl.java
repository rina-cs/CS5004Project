package game.repository.impl;

import game.model.User;
import game.repository.UserRepository;
import game.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {

  private static final String USERS_FILE = "users.json";
  private final AtomicLong idGenerator = new AtomicLong(1);

  @Autowired
  private FileStorageService fileStorageService;

  public UserRepositoryImpl(FileStorageService fileStorageService) {
    this.fileStorageService = fileStorageService;
    initializeIdGenerator();
  }

  private void initializeIdGenerator() {
    List<User> users = findAll();
    if (!users.isEmpty()) {
      long maxId = users.stream()
          .mapToLong(User::getId)
          .max()
          .orElse(0);
      idGenerator.set(maxId + 1);
    }
  }

  @Override
  public List<User> findAll() {
    return fileStorageService.readList(USERS_FILE, User.class);
  }

  @Override
  public Optional<User> findById(Long id) {
    return findAll().stream()
        .filter(user -> user.getId().equals(id))
        .findFirst();
  }

  @Override
  public User save(User user) {
    List<User> users = findAll();

    if (user.getId() == null) {
      user.setId(idGenerator.getAndIncrement());
    } else {
      // 移除已存在的相同ID的用户
      users = users.stream()
          .filter(existingUser -> !existingUser.getId().equals(user.getId()))
          .collect(Collectors.toList());
    }

    users.add(user);
    fileStorageService.writeList(USERS_FILE, users);
    return user;
  }

  @Override
  public void deleteById(Long id) {
    List<User> users = findAll();
    List<User> updatedUsers = users.stream()
        .filter(user -> !user.getId().equals(id))
        .collect(Collectors.toList());
    fileStorageService.writeList(USERS_FILE, updatedUsers);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return findAll().stream()
        .filter(user -> user.getUsername().equals(username))
        .findFirst();
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return findAll().stream()
        .filter(user -> user.getEmail().equals(email))
        .findFirst();
  }
}