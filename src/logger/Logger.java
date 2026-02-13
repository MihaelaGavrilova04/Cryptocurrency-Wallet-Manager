package logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Logger {

    private static final String DIRECTORY_NAME = "logs";
    private static final String FILENAME = "logs.txt";

    private static final String LOG_MESSAGE = "Log id: %d | User: %s | Exception: %s" + System.lineSeparator();
    private static final String STACK_TRACE_MESSAGE = "Stack trace:" + System.lineSeparator();

    private static int currentLogNumber = 0;
    private final Path filePath;

    private Logger() {
        Path dirPath = Path.of(DIRECTORY_NAME);
        filePath = dirPath.resolve(Path.of(FILENAME));

        try {
            if (Files.notExists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Can not create log files structure", e);
        }
    }

    public static Logger getInstance() {
        return LoggerHolder.INSTANCE;
    }

    public synchronized void log(Exception e, String user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), true))) {
            currentLogNumber++;

            String header = String.format(LOG_MESSAGE, currentLogNumber, user, e.toString());

            writer.write(header);

            writer.write(STACK_TRACE_MESSAGE);
            for (StackTraceElement element : e.getStackTrace()) {
                writer.write(element.toString());
                writer.newLine();
            }

            writer.flush();

        } catch (IOException e1) {
            throw new UncheckedIOException(e1);
        }
    }

    private static class LoggerHolder {
        private static final Logger INSTANCE = new Logger();
    }
}
