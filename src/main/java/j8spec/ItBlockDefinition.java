package j8spec;

final class ItBlockDefinition implements BlockDefinition {

    private final ItBlockConfiguration config;
    private final UnsafeBlock block;

    ItBlockDefinition(ItBlockConfiguration config, UnsafeBlock block) {
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
