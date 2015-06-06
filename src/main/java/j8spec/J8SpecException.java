package j8spec;

public class J8SpecException extends RuntimeException {
    J8SpecException(String message, Exception e) {
        super(message, e);
    }

    J8SpecException(String message) {
        super(message);
    }
}
