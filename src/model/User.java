package model;

import static util.HashingAlgorithm.getHashedPassword;
import static util.HashingAlgorithm.verifyPassword;

public record User(String email, String passwordHash, Wallet wallet) {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final int MINIMUM_PASSWORD_LENGTH = 6;

    public static User create(String email, String plainPassword) {
        validateEmail(email);
        validatePassword(plainPassword);

        String hashedPassword = getHashedPassword(plainPassword);

        return new User(email, hashedPassword, new Wallet());
    }

    public static User create(String email, String plainPassword, Wallet wallet) {
        validateEmail(email);
        validatePassword(plainPassword);
        validateWallet(wallet);

        String hashedPassword = getHashedPassword(plainPassword);

        return new User(email, hashedPassword, new Wallet(wallet));
    }

    public static User load(String email, String passwordHash, Wallet wallet) {
        validateEmail(email);
        validateWallet(wallet);

        return new User(email, passwordHash, wallet);
    }

    private static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("The email passed is invalid!");
        }

        if (!email.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("The email passed is invalid format! Expected: example@domain.com");
        }
    }

    private static void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("The password passed is invalid!");
        }

        if (password.length() < MINIMUM_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("The password passed is too short to be safe to use!");
        }
    }

    private static void validateWallet(Wallet wallet) {
        if (wallet == null) {
            throw new IllegalArgumentException("Parameter 'wallet' passed to function is null!");
        }
    }

    public boolean checkPassword(String plainPassword) {
        return verifyPassword(plainPassword, this.passwordHash);
    }
}
