package j8spec;

public class IllegalSeedPropertyException extends J8SpecException {
    IllegalSeedPropertyException(NumberFormatException e) {
        super("Illegal 'j8spec.seed' property value.", e);
    }
}
