package j8spec;

class AfterEachDefinition implements BlockDefinition {

    private final UnsafeBlock block;

    AfterEachDefinition(UnsafeBlock block) {
        this.block = block;
    }

    @Override
    public void accept(BlockDefinitionVisitor visitor) {
        visitor.afterEach(block);
    }
}
