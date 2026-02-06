package command.commands;

import api.AssetCache;
import model.User;
import repository.UserRepository;
import util.HashingAlgorithm;

public final class LoginCommand implements PublicCommand {
    private final UserRepository userRepository;
    private final String email;
    private final String password;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public LoginCommand(UserRepository userRepository, String email, String password) {
        validateObjectConstruction(userRepository, email, password);

        this.userRepository = userRepository;
        this.email = email;
        this.password = password;
    }

    @Override
    public String execute() {
        User toLogin = userRepository.findByEmail(email);

        if (toLogin == null) {
            return "No such email has been registered";
        }

        if (HashingAlgorithm.verifyPassword(password, toLogin.passwordHash())) {
            return "Login successful! Welcome, " + email;
        }

        return "Invalid email or password.";
    }

    private static void validateObjectConstruction(UserRepository userRepository, String email, String password) {
        if (userRepository == null) {
            throw new IllegalArgumentException("Parameter 'userRepository' passed to construct LoginCommand is null!");
        }

        if (email == null || email.isBlank() || !email.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("Parameter 'email' passed to construct LoginCommand is invalid!");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Parameter 'password' passed to construct LoginCommand is invalid!");
        }
    }
    }
