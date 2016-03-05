package j8spec;

final class BeforeEachDefinition implements BlockDefinition {
    private final UnsafeBlock block;

    BeforeEachDefinition(UnsafeBlock block) {
        this.block = block;
    }

    @Override
    public void accept(BlockDefinitionVisitor visitor) {
        visitor.beforeEach(block);
    }
}
