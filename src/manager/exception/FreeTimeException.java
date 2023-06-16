package manager.exception;

public class FreeTimeException extends RuntimeException {
    private String message;

    public FreeTimeException(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}

