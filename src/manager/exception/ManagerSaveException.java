package manager.exception;

public class ManagerSaveException extends RuntimeException {
    String message;
    public ManagerSaveException(String message) {
        this.message = message;
    }
}