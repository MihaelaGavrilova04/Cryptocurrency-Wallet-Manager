package repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exception.InvalidCommandException;
import exception.UserAlreadyExistsException;
import logger.Logger;
import model.User;
import util.GsonProvider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {
    private static final Path DIRECTORY_PATH = Path.of("database");
    private static final Type TYPE_TOKEN = new TypeToken<ConcurrentHashMap<String, User>>() { }.getType();

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    private final Path filePath;
    private final Gson gson;
    private final Map<String, User> users;

    private static final Logger LOGGER = Logger.getInstance();

    public UserRepository(String filename) {
        validateFilename(filename);

        this.filePath = DIRECTORY_PATH.resolve(filename);
        this.gson = GsonProvider.getGson();

        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            } else if (!Files.isDirectory(DIRECTORY_PATH)) {
                Files.createDirectory(DIRECTORY_PATH);
            }
        } catch (IOException e) {
            LOGGER.log(e, "SYSTEM");
            throw new RuntimeException("Could not create directory!", e);
        }

        users = loadUsersFromFile();
    }

    public synchronized void saveAllUsers() {
        saveUsersToFile();
    }

    public User findByEmail(String email) {
        validateEmail(email);

        return users.get(email);
    }

    public synchronized void registerUser(User toRegister) {
        validateUser(toRegister);

        if (users.containsKey(toRegister.email())) {
            throw new UserAlreadyExistsException("User with this email already exists.");
        }

        users.put(toRegister.email(), toRegister);
        saveUsersToFile();
    }

    public synchronized void updateUser(User user) {
        validateUser(user);

        if (!users.containsKey(user.email())) {
            throw new IllegalArgumentException("User does not exist!");
        }

        users.put(user.email(), user);
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
            LOGGER.log(e, "SYSTEM");
            throw new RuntimeException("Could not read from file!");
        }
    }

    private void saveUsersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
            gson.toJson(users, TYPE_TOKEN, writer);
            writer.flush();
        } catch (IOException e) {
            LOGGER.log(e, "SYSTEM");
            throw new RuntimeException("Could not write the users to file!");
        } catch (Exception e) {
            LOGGER.log(e, "GSON");
            throw new RuntimeException("GSON crashed!", e);
        }
    }

    private static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidCommandException("Email passed is invalid!");
        }

        if (!email.matches(EMAIL_REGEX)) {
            throw new InvalidCommandException("Email passed is not the right format!");
        }
    }

    private static void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User passed is null!");
        }
    }

    private static void validateFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Filename passed to function is null or blank!");
        }
    }
}
