package j8spec;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import static j8spec.BeforeBlock.newBeforeAllBlock;
import static j8spec.BeforeBlock.newBeforeEachBlock;
import static j8spec.BlockExecutionFlag.DEFAULT;
import static j8spec.ItBlock.newIgnoredItBlock;
import static j8spec.ItBlock.newItBlock;

final class ExecutableSpecBuilder extends BlockDefinitionVisitor {

    private final BlockExecutionStrategy executionStrategy;
    private final Deque<String> descriptions = new LinkedList<>();
    private final Deque<BlockExecutionFlag> executionFlags = new LinkedList<>();
    private final Deque<List<BeforeBlock>> beforeAllBlocks = new LinkedList<>();
    private final Deque<List<BeforeBlock>> beforeEachBlocks = new LinkedList<>();

    private final List<ItBlock> itBlocks = new LinkedList<>();

    ExecutableSpecBuilder(BlockExecutionStrategy executionStrategy) {
        this.executionStrategy = executionStrategy;
    }

    @Override
    BlockDefinitionVisitor describe(String description, BlockExecutionFlag executionFlag) {
        descriptions.addLast(description);

        if (executionFlags.isEmpty() || executionFlags.peekLast().equals(DEFAULT)) {
            executionFlags.addLast(executionFlag);
        } else {
            executionFlags.addLast(executionFlags.peekLast());
        }

        beforeAllBlocks.addLast(new LinkedList<>());
        beforeEachBlocks.addLast(new LinkedList<>());

        return this;
    }

    @Override
    BlockDefinitionVisitor beforeAll(UnsafeBlock block) {
        beforeAllBlocks.peekLast().add(newBeforeAllBlock(block));
        return this;
    }

    @Override
    BlockDefinitionVisitor beforeEach(UnsafeBlock block) {
        beforeEachBlocks.peekLast().add(newBeforeEachBlock(block));
        return this;
    }

    @Override
    BlockDefinitionVisitor it(
        String description,
        UnsafeBlock block,
        BlockExecutionFlag executionFlag,
        Class<? extends Throwable> expectedException
    ) {
        List<BeforeBlock> beforeBlocks = new LinkedList<>();
        beforeAllBlocks.forEach(beforeBlocks::addAll);
        beforeEachBlocks.forEach(beforeBlocks::addAll);

        if (executionStrategy.shouldBeIgnored(executionFlag, executionFlags.peekLast())) {
            itBlocks.add(newIgnoredItBlock(
                new LinkedList<>(descriptions),
                description
            ));
        } else {
            itBlocks.add(newItBlock(
                new LinkedList<>(descriptions),
                description,
                beforeBlocks,
                block,
                expectedException
            ));
        }

        return this;
    }

    @Override
    BlockDefinitionVisitor describe() {
        descriptions.removeLast();
        executionFlags.removeLast();
        beforeAllBlocks.removeLast();
        beforeEachBlocks.removeLast();
        return this;
    }

    List<ItBlock> build() {
        return itBlocks;
    }
}
