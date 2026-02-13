package command.commands;

import exception.InvalidCommandException;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import repository.UserRepository;
import server.session.ClientContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoginCommandTest {

    private UserRepository userRepositoryMock;
    private ClientContext clientContextMock;

    @BeforeEach
    void setUp() {
        userRepositoryMock = Mockito.mock(UserRepository.class);
        clientContextMock = Mockito.mock(ClientContext.class);
    }

    @Test
    void testConstructLoginCommandInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                new LoginCommand(null, "mihaela@gmail.com", "123456", clientContextMock), "When userRepository is null, IllegalArgumentException is expected!");
        assertThrows(IllegalArgumentException.class, () ->
                new LoginCommand(userRepositoryMock, "mihaela@gmail.com", "123456", null), "When ClientContext is null, IllegalArgumentException is expected!");

        assertThrows(InvalidCommandException.class, () ->
                new LoginCommand(userRepositoryMock, "email", "123456", clientContextMock), "The email passed is not in a valid format, therefore InvalidCommandException is expected");

        assertThrows(InvalidCommandException.class, () ->
                new LoginCommand(userRepositoryMock, "mihaela@gmail.com", "  ", clientContextMock), "When password is blank, InvalidCommandException is expected");
    }

    @Test
    void testExecuteLoginSuccessfully() {
        String email = "mihaela@gmail.com";
        String password = "correct_password";
        User user = Mockito.mock(User.class);

        when(clientContextMock.isLoggedIn()).thenReturn(false);
        when(userRepositoryMock.findByEmail(email)).thenReturn(user);
        when(user.checkPassword(password)).thenReturn(true);
        when(user.email()).thenReturn(email);

        LoginCommand command = new LoginCommand(userRepositoryMock, email, password, clientContextMock);
        String result = command.execute();

        assertEquals("Login successful! Welcome, " + email, result);
        verify(clientContextMock, times(1)).login(user);
    }

    @Test
    void testExecuteAlreadyLoggedIn() {
        String email = "mihaela@gmail.com";
        User alreadyLoggedInUser = Mockito.mock(User.class);
        when(alreadyLoggedInUser.email()).thenReturn("someOtherLoggedInUser@abv.bg");

        when(clientContextMock.isLoggedIn()).thenReturn(true);
        when(clientContextMock.getLoggedInUser()).thenReturn(alreadyLoggedInUser);

        LoginCommand command = new LoginCommand(userRepositoryMock, email, "password", clientContextMock);
        String result = command.execute();

        assertEquals("You are already logged in as someOtherLoggedInUser@abv.bg", result);
    }

    @Test
    void testExecuteUserNotFound() {
        String email = "unknown@abv.bg";
        when(clientContextMock.isLoggedIn()).thenReturn(false);
        when(userRepositoryMock.findByEmail(email)).thenReturn(null);

        LoginCommand command = new LoginCommand(userRepositoryMock, email, "123456", clientContextMock);
        String result = command.execute();

        assertEquals("No such email has been registered", result);
    }

    @Test
    void testExecuteInvalidPassword() {
        String email = "mihaela@gmail.com";
        String password = "wrong_password";
        User user = Mockito.mock(User.class);

        when(clientContextMock.isLoggedIn()).thenReturn(false);
        when(userRepositoryMock.findByEmail(email)).thenReturn(user);
        when(user.checkPassword(password)).thenReturn(false);

        LoginCommand command = new LoginCommand(userRepositoryMock, email, password, clientContextMock);
        String result = command.execute();

        assertEquals("Invalid password.", result);
    }
}
