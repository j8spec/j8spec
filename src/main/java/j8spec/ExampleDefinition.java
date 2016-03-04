package j8spec;

final class ExampleDefinition implements BlockDefinition {

    private final ExampleConfiguration config;
    private final UnsafeBlock block;

    ExampleDefinition(ExampleConfiguration config, UnsafeBlock block) {
        this.config = config;
        this.block = block;
    }

    String description() {
        return config.description();
    }

    UnsafeBlock block() {
        return block;
    }

    @Override
    public void accept(BlockDefinitionVisitor visitor) {
        visitor.example(config.description(), block, config.executionFlag(), config.expectedException());
    }
}
