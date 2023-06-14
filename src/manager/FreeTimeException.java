package manager;

public class FreeTimeException extends RuntimeException {
    private String message;

    FreeTimeException(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}

