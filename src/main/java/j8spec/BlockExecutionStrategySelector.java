package j8spec;

import static j8spec.BlockExecutionFlag.FOCUSED;
import static j8spec.BlockExecutionStrategy.BLACK_LIST;
import static j8spec.BlockExecutionStrategy.WHITE_LIST;

final class BlockExecutionStrategySelector extends BlockDefinitionVisitor {

    private BlockExecutionStrategy strategy = BLACK_LIST;

    BlockExecutionStrategy strategy() {
        return strategy;
    }

    @Override
    BlockDefinitionVisitor startGroup(
        String description,
        BlockExecutionFlag executionFlag,
        BlockExecutionOrder order
    ) {
        selectStrategy(executionFlag);
        return this;
    }

    @Override
    BlockDefinitionVisitor example(
        String description,
        UnsafeBlock block,
        BlockExecutionFlag executionFlag,
        Class<? extends Throwable> expectedException
    ) {
        selectStrategy(executionFlag);
        return this;
    }

    private void selectStrategy(BlockExecutionFlag executionFlag) {
        if (FOCUSED.equals(executionFlag)) {
            strategy = WHITE_LIST;
        }
    }
}
