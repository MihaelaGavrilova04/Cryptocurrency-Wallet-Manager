package exception;

public class CoinAPIException extends RuntimeException {
    public CoinAPIException(String message) {
        super(message);
    }

    public CoinAPIException(String message, Throwable cause) {
        super(message, cause);
    }
}
