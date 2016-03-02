package j8spec;

abstract class BlockDefinitionVisitor {

    BlockDefinitionVisitor startGroup(String description, BlockExecutionFlag executionFlag) {
        return this;
    }

    BlockDefinitionVisitor beforeAll(UnsafeBlock block) {
        return this;
    }

    BlockDefinitionVisitor beforeEach(UnsafeBlock block) {
        return this;
    }

    BlockDefinitionVisitor example(
        String description,
        UnsafeBlock block,
        BlockExecutionFlag executionFlag,
        Class<? extends Throwable> expectedException
    ) {
        return this;
    }

    BlockDefinitionVisitor endGroup() {
        return this;
    }
}
