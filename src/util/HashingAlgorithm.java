package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingAlgorithm {
    private static final String HASHING_ALGORITHM = "SHA-256";
    private static final String BYTE_FORMAT = "%02x";

    public static String getHashedPassword(String password) {

        if (password == null) {
            // TO DO:
            // ERROR HANDLING
        }

        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HASHING_ALGORITHM);
            byte[] hashBytes = messageDigest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format(BYTE_FORMAT, b));
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            // TO DO : HANDLE
            throw new RuntimeException(e);
        }
    }

    public static boolean verifyPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }
        String hashToCheck = getHashedPassword(plainPassword);
        return hashToCheck.equals(storedHash);
    }
}
