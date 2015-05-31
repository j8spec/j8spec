package j8spec;

public class J8SpecException extends RuntimeException {
    public J8SpecException(String message, Exception e) {
        super(message, e);
    }

    public J8SpecException(String message) {
        super(message);
    }
}
