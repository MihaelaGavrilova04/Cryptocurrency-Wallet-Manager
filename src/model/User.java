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

        String hashedPassword = getHashedPassword(plainPassword);

        Wallet newWallet = new Wallet();
        newWallet.deposit(wallet.getDeposit());

        return new User(email, hashedPassword, newWallet);
    }

    public static User load(String email, String passwordHash, Wallet wallet) {
        validateEmail(email);
        return new User(email, passwordHash, wallet);
    }

    private static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            // TO DO : handle error & logging
        }

        if (!email.matches(EMAIL_REGEX)) {
            // TO DO : handle error & logging
        }
    }

    private static void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            // TO DO : handle error & logging
        }

        if (password.length() < MINIMUM_PASSWORD_LENGTH) {
            // TO DO : handle error & logging
        }
    }

    public boolean checkPassword(String plainPassword) {
        return verifyPassword(plainPassword, this.passwordHash);
    }
}
