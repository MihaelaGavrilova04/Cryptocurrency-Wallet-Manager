package command.commands;

import exception.InvalidCommandException;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RegisterCommandTest {

    private UserRepository userRepositoryMock;

    @BeforeEach
    void setUp() {
        userRepositoryMock = Mockito.mock(UserRepository.class);
    }

    @Test
    void testConstructRegisterCommandInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                new RegisterCommand(null, "mihaela@gmail.com", "123456"), "When param userRepository is null, IllegalArgumentException is expected");

        assertThrows(InvalidCommandException.class, () ->
                new RegisterCommand(userRepositoryMock, "invalid-email", "123456"), "When invalid email is passed, InvalidCommandException is expected!");
        assertThrows(InvalidCommandException.class, () ->
                new RegisterCommand(userRepositoryMock, null, "123456"), "When null email is passed, InvalidCommandException is expected");
        assertThrows(InvalidCommandException.class, () ->
                new RegisterCommand(userRepositoryMock, "   ", "123456"), "When blank email is passed, InvalidCommandException is expected");

        assertThrows(InvalidCommandException.class, () ->
                new RegisterCommand(userRepositoryMock, "mihaela@gmail.com", null), "When password parameter is null, InvalidCommandException is expected");
        assertThrows(InvalidCommandException.class, () ->
                new RegisterCommand(userRepositoryMock, "mihaela@gmail.com", ""), "When password parameter is empty, InvalidCommandException is expected!");
    }

    @Test
    void testExecuteRegisterSuccessfully() {
        String email = "mihaela@gmail.com";
        String password = "password123";

        when(userRepositoryMock.findByEmail(email)).thenReturn(null);

        RegisterCommand command = new RegisterCommand(userRepositoryMock, email, password);
        String result = command.execute();

        assertNotNull(result);
        assertEquals(String.format("User %s registered successfully!", email), result);

        verify(userRepositoryMock, times(1)).registerUser(any(User.class));
    }

    @Test
    void testExecuteRegisterUserAlreadyExists() {
        String email = "mihaela@gmail.com";
        User existingUser = Mockito.mock(User.class);
        when(userRepositoryMock.findByEmail(email)).thenReturn(existingUser);

        RegisterCommand command = new RegisterCommand(userRepositoryMock, email, "password");
        String result = command.execute();

        assertEquals("User with this email already registered!", result);

        verify(userRepositoryMock, times(0)).registerUser(any(User.class));
    }
}