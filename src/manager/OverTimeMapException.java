package manager;

public class OverTimeMapException extends RuntimeException {
        private String message;

    OverTimeMapException(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
}
