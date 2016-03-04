package j8spec;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

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
    private final RankGenerator rankGenerator = new RankGenerator();

    private final SortedSet<ItBlock> examples = new TreeSet<>();

    ExecutableSpecBuilder(BlockExecutionStrategy executionStrategy) {
        this.executionStrategy = executionStrategy;
    }

    @Override
    BlockDefinitionVisitor startGroup(ExampleGroupConfiguration config) {
        descriptions.addLast(config.description());

        if (executionFlags.isEmpty() || executionFlags.peekLast().equals(DEFAULT)) {
            executionFlags.addLast(config.executionFlag());
        } else {
            executionFlags.addLast(executionFlags.peekLast());
        }

        beforeAllBlocks.addLast(new LinkedList<>());
        beforeEachBlocks.addLast(new LinkedList<>());

        rankGenerator.pushLevel(RankGenerator.Incremental.INSTANCE);

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
    BlockDefinitionVisitor example(ExampleConfiguration config, UnsafeBlock block) {
        List<BeforeBlock> beforeBlocks = new LinkedList<>();
        beforeAllBlocks.forEach(beforeBlocks::addAll);
        beforeEachBlocks.forEach(beforeBlocks::addAll);

        if (executionStrategy.shouldBeIgnored(config.executionFlag(), executionFlags.peekLast())) {
            examples.add(newIgnoredItBlock(
                new LinkedList<>(descriptions),
                config.description(),
                rankGenerator.generate()
            ));
        } else {
            examples.add(newItBlock(
                new LinkedList<>(descriptions),
                config.description(),
                beforeBlocks,
                block,
                config.expectedException(),
                rankGenerator.generate()
            ));
        }

        rankGenerator.next();

        return this;
    }

    @Override
    BlockDefinitionVisitor endGroup() {
        descriptions.removeLast();
        executionFlags.removeLast();
        beforeAllBlocks.removeLast();
        beforeEachBlocks.removeLast();
        rankGenerator.popLevel();
        return this;
    }

    List<ItBlock> build() {
        return new LinkedList<>(examples);
    }
}
