package j8spec;

final class BeforeEachBlockDefinition implements BlockDefinition {
    private final UnsafeBlock block;

    BeforeEachBlockDefinition(UnsafeBlock block) {
        this.block = block;
    }

    @Override
    public void accept(BlockDefinitionVisitor visitor) {
        visitor.beforeEach(block);
    }
}
