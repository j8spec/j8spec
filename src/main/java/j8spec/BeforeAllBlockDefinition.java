package j8spec;

final class BeforeAllBlockDefinition implements BlockDefinition {
    private final UnsafeBlock block;

    BeforeAllBlockDefinition(UnsafeBlock block) {
        this.block = block;
    }

    @Override
    public void accept(BlockDefinitionVisitor visitor) {
        visitor.beforeAll(block);
    }
}
