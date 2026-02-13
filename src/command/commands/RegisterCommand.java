package command.commands;

import exception.InvalidCommandException;
import model.User;
import model.Wallet;
import repository.UserRepository;

public final class RegisterCommand implements PublicCommand {
    private final UserRepository userRepository;
    private final String email;
    private final String password;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final int MINIMUM_PASSWORD_LENGTH = 6;

    public RegisterCommand(UserRepository userRepository, String email, String password) {
        validateObjectConstruction(userRepository, email, password);

        this.userRepository = userRepository;
        this.email = email;
        this.password = password;
    }

    @Override
    public String execute() {
        if (userRepository.findByEmail(email) != null) {
            return "User with this email already registered!";
        }

        User userToRegister = User.create(email, password, new Wallet());
        userRepository.registerUser(userToRegister);

        return String.format("User %s registered successfully!", email);
    }

    private static void validateObjectConstruction(UserRepository userRepository, String email, String password) {
        if (userRepository == null) {
            throw new IllegalArgumentException("Param 'userRepository' passed to construct RegisterCommand is null!");
        }

        if (email == null || email.isBlank() || !email.matches(EMAIL_REGEX)) {
            throw new InvalidCommandException("Invalid email format. Expected format: example@domain.com");
        }

        if (password == null || password.isBlank()) {
            throw new InvalidCommandException(String.format(
                    "Invalid password passed to register! Password should at least %d symbols for safety purposes!",
                    MINIMUM_PASSWORD_LENGTH
            ));
        }
    }
}
