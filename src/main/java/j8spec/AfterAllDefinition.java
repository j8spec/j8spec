package j8spec;

class AfterAllDefinition implements BlockDefinition {

    private final UnsafeBlock block;

    AfterAllDefinition(UnsafeBlock block) {
        this.block = block;
    }

    @Override
    public void accept(BlockDefinitionVisitor visitor) {
        visitor.afterAll(block);
    }
}
