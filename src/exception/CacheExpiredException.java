package exception;

public class CacheExpiredException extends RuntimeException {
    public CacheExpiredException(String message) {
        super(message);
    }

    public CacheExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
