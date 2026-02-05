package repository;

// not making it a singleton to test it easily, using dependency injection

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {
    private static final Path DIRECTORY_PATH = Path.of("database");
    private static final Type TYPE_TOKEN = new TypeToken<ConcurrentHashMap<String, User>>() { }.getType();

    private final Path filePath;
    private final Gson gson;
    private final Map<String, User> users;

    public UserRepository(String filename) {
        this.filePath = Path.of(filename);
        this.gson = GsonProvider.getGson();
        users = loadUsersFromFile();
    }

    public synchronized void saveAllUsers() {
        saveUsersToFile();
    }

    public User findByEmail(String email) {
        return users.get(email);
    }

    public synchronized void registerUser(User toRegister) {
        if (users.containsKey(toRegister.email())) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        users.put(toRegister.email(), toRegister);
        saveUsersToFile();
    }

    private Map<String, User> loadUsersFromFile() {
        if (!Files.exists(filePath)) {
            return new ConcurrentHashMap<>();
        }

        try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath.toFile()))) {
            Map<String, User> loadedUsers = gson.fromJson(fileReader, TYPE_TOKEN);
            return loadedUsers != null ? loadedUsers : new ConcurrentHashMap<>();
        } catch (IOException e) {
            throw new RuntimeException("Could not read from file!");
        }
    }

    private void saveUsersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
            gson.toJson(users, TYPE_TOKEN, writer);
        } catch (IOException e) {
            throw new RuntimeException("Could not write the users to file!");
        }
    }

}
