package server.session;

import exception.UserAlreadyLoggedInException;
import model.User;

import java.nio.ByteBuffer;

public class ClientContext {
    private static final int BUFFER_SIZE = 2048;

    private final ByteBuffer buffer;
    private User loggedUser;

    public ClientContext() {
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
    }

    public void login(User toLogin) {
        if (loggedUser != null) {
            throw new UserAlreadyLoggedInException("This session already has a user logged in");
        }

        this.loggedUser = toLogin;
    }

    public void logout() {
        this.loggedUser = null;
    }

    public boolean isLoggedIn() {
        return loggedUser != null;
    }

    public User getLoggedInUser() {
        return loggedUser;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }
}
