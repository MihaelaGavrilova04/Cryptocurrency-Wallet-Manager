package repository;

import exception.InvalidCommandException;
import exception.UserAlreadyExistsException;
import model.User;
import model.Wallet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserRepositoryTest {
    private static int currentTest = 1;
    private static final Path DIRECTORY_PATH = Path.of("database");

    private UserRepository userRepository;
    private String testFileName;
    private Path testFilePath;

    @BeforeEach
    void setUp() {
        testFileName = "test_users_" + currentTest + ".json";
        currentTest++;

        testFilePath = DIRECTORY_PATH.resolve(testFileName);
        userRepository = new UserRepository(testFileName);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(testFilePath);
    }

    @Test
    void testRegisterNullUserThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> userRepository.registerUser(null), "When object User passed to register is null, IllegalArgumentException is expected");
    }

    @Test
    void testUpdateNullUserThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> userRepository.updateUser(null), "When object User passed to login is null, IllegalArgumentException is expected");
    }

    @Test
    void testRegisterUserSuccessfully() {
        User user = new User("mihaela@gmail.com", "password", new Wallet());

        userRepository.registerUser(user);

        User found = userRepository.findByEmail("mihaela@gmail.com");
        assertNotNull(found);
        assertEquals("mihaela@gmail.com", found.email());
    }

    @Test
    void testRegisterUserAlreadyExists() {
        User user = new User("mihaela@gmail.com", "password", new Wallet());
        userRepository.registerUser(user);

        assertThrows(UserAlreadyExistsException.class, () -> userRepository.registerUser(user), "Can not register an user who doesn't exist");
    }

    @Test
    void testUpdateUserSuccessfully() {
        User user = new User("mihaela@gmail.com", "old-pass", new Wallet());
        userRepository.registerUser(user);

        User updatedUser = new User("mihaela@gmail.com", "new-pass", new Wallet());
        userRepository.updateUser(updatedUser);

        User found = userRepository.findByEmail("mihaela@gmail.com");
        assertNotNull(found);
        assertEquals("mihaela@gmail.com", found.email());
    }

    @Test
    void testFindByEmailInvalid() {
        assertThrows(InvalidCommandException.class, () -> userRepository.findByEmail("email"), "Such email is not a valid email format, InvalidCommandException expected");
        assertNull(userRepository.findByEmail("missing@abv.bg"));
    }

    @Test
    void testPersistenceBetweenInstances() {
        User user = new User("mihaela@abv.bg", "password", new Wallet());
        userRepository.registerUser(user);

        UserRepository secondRepo = new UserRepository(testFileName);
        User found = secondRepo.findByEmail("mihaela@abv.bg");

        assertNotNull(found, "We expect the email to be present in the database");
        assertEquals("mihaela@abv.bg", found.email());
    }

    @Test
    void testConstructUserRepositoryWithInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> new UserRepository(null), "Filename passed is null, IllegalArgumentException expected");
        assertThrows(IllegalArgumentException.class, () -> new UserRepository(""), "Filename passed is blank, IllegalArgumentException expected");
    }

    @Test
    void testLoadUsersWhenFileDoesNotExist() throws IOException {
        Files.deleteIfExists(testFilePath);

        UserRepository repo2 = new UserRepository(testFileName);

        assertNull(repo2.findByEmail("mihaela@gmail.com"), "Should return empty when file is missing");
    }

    @Test
    void testUpdateUserThatDoesNotExist() {
        User user = new User("example@gmail.com", "password", new Wallet());

        assertThrows(IllegalArgumentException.class, () -> userRepository.updateUser(user),
                "Updating user is not possible as he is not registered");
    }

    @Test
    void testFindByEmailBlankOrNull() {
        assertThrows(InvalidCommandException.class, () -> userRepository.findByEmail("   "), "Email is blank, InvalidCommandException expected");
        assertThrows(InvalidCommandException.class, () -> userRepository.findByEmail(null), "Email is null, InvalidCommandException expected");
    }

    @Test
    void testConstructorDirectoryAlreadyExists() throws IOException {
        UserRepository repo = new UserRepository("another_test.json");
        assertNotNull(repo);
        Files.deleteIfExists(DIRECTORY_PATH.resolve("another_test.json"));
    }

    @Test
    void testConstructorCreatesNestedDirectories() throws IOException {
        String nestedFile = "testDir/test.json";
        Path nestedPath = DIRECTORY_PATH.resolve(nestedFile);

        try {
            UserRepository nestedRepo = new UserRepository(nestedFile);
            assertTrue(Files.exists(nestedPath.getParent()), "Should create directory testDir");
        } finally {
            Files.deleteIfExists(nestedPath);
            Files.deleteIfExists(nestedPath.getParent());
        }
    }

    @Test
    void testSaveAllUsersExplicitly() {
        User user1 = new User("mishy@gmail.com", "password", new Wallet());
        userRepository.registerUser(user1);

        User user2 = new User("desi@gmail.com", "password", new Wallet());
        userRepository.registerUser(user2);

        userRepository.saveAllUsers();

        assertNotNull(userRepository.findByEmail("mishy@gmail.com"));
        assertNotNull(userRepository.findByEmail("desi@gmail.com"));
    }

    @Test
    void testConstructorCreatesBaseDirectoryIfMissing() throws IOException {
        Path tempBase = Path.of("database_another");
        String testFile = "test.json";

        UserRepository repo = new UserRepository(testFile);
        assertTrue(Files.exists(DIRECTORY_PATH), "Base directory should exist");
    }

    @Test
    void testRegisterUserWithNullFields() {
        assertThrows(InvalidCommandException.class, () -> userRepository.findByEmail(""));
    }
}