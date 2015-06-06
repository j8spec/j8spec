package j8spec;

public class SpecInitializationException extends J8SpecException {
    SpecInitializationException(String message, Exception e) {
        super(message, e);
    }
}
