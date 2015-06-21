package j8spec;

public final class ItBlockDefinitionBuilder {

    private Runnable body;
    private Class<? extends Throwable> expectedException;

    public ItBlockDefinitionBuilder expected(Class<? extends Throwable> expectedException) {
        this.expectedException = expectedException;
        return this;
    }

    ItBlockDefinitionBuilder body(Runnable body) {
        this.body = body;
        return this;
    }

    ItBlockDefinition newItBlockDefinition() {
        return ItBlockDefinition.newItBlockDefinition(body, expectedException);
    }

    ItBlockDefinition newIgnoredItBlockDefinition() {
        return ItBlockDefinition.newIgnoredItBlockDefinition(body, expectedException);
    }

    ItBlockDefinition newFocusedItBlockDefinition() {
        return ItBlockDefinition.newFocusedItBlockDefinition(body, expectedException);
    }
}
