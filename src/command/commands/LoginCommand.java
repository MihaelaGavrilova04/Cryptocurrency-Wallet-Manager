package command.commands;

import exception.InvalidCommandException;
import model.User;
import repository.UserRepository;
import server.session.ClientContext;

public final class LoginCommand implements PublicCommand {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private final UserRepository userRepository;
    private final String email;
    private final String password;
    private final ClientContext clientContext;

    public LoginCommand(UserRepository userRepository, String email, String password, ClientContext clientContext) {
        validateObjectConstruction(userRepository, email, password, clientContext);

        this.userRepository = userRepository;
        this.email = email;
        this.password = password;
        this.clientContext = clientContext;
    }

    private static void validateObjectConstruction(UserRepository userRepository, String email,
                                                   String password, ClientContext clientContext) {
        if (userRepository == null) {
            throw new IllegalArgumentException("Parameter 'userRepository' passed to construct LoginCommand is null!");
        }

        if (email == null || email.isBlank() || !email.matches(EMAIL_REGEX)) {
            throw new InvalidCommandException("The email passed to login is invalid!");
        }

        if (password == null || password.isBlank()) {
            throw new InvalidCommandException("The password passed to login is invalid!");
        }

        if (clientContext == null) {
            throw new IllegalArgumentException("Parameter 'clientContext' passed to construct LoginCommand is null!");
        }
    }

    @Override
    public String execute() {

        if (clientContext.isLoggedIn()) {
            return "You are already logged in as " + clientContext.getLoggedInUser().email();
        }

        User toLogin = userRepository.findByEmail(email);

        if (toLogin == null) {
            return "No such email has been registered";
        }

        if (toLogin.checkPassword(password)) {
            clientContext.login(toLogin);
            return "Login successful! Welcome, " + email;
        }

        return "Invalid password.";
    }
}
