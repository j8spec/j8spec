package j8spec;

final class BeforeAllDefinition implements BlockDefinition {
    private final UnsafeBlock block;

    BeforeAllDefinition(UnsafeBlock block) {
        this.block = block;
    }

    @Override
    public void accept(BlockDefinitionVisitor visitor) {
        visitor.beforeAll(block);
    }
}
