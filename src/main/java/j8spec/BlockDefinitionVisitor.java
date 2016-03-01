package j8spec;

abstract class BlockDefinitionVisitor {

    BlockDefinitionVisitor describe(String description, BlockExecutionFlag executionFlag) {
        return this;
    }

    BlockDefinitionVisitor beforeAll(UnsafeBlock block) {
        return this;
    }

    BlockDefinitionVisitor beforeEach(UnsafeBlock block) {
        return this;
    }

    BlockDefinitionVisitor it(
        String description,
        UnsafeBlock block,
        BlockExecutionFlag executionFlag,
        Class<? extends Throwable> expectedException
    ) {
        return this;
    }

    BlockDefinitionVisitor describe() {
        return this;
    }
}
