package j8spec;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static j8spec.Hook.newOneTimeHook;
import static j8spec.Hook.newHook;
import static j8spec.BlockExecutionFlag.DEFAULT;
import static j8spec.Example.newExample;
import static j8spec.Example.newIgnoredExample;

final class ExampleBuilder extends BlockDefinitionVisitor {

    private final BlockExecutionStrategy executionStrategy;
    private final Deque<String> descriptions = new LinkedList<>();
    private final Deque<BlockExecutionFlag> executionFlags = new LinkedList<>();
    private final Deque<List<Hook>> beforeAllBlocks = new LinkedList<>();
    private final Deque<List<Hook>> beforeEachBlocks = new LinkedList<>();
    private final RankGenerator rankGenerator = new RankGenerator();

    private final SortedSet<Example> examples = new TreeSet<>();

    ExampleBuilder(BlockExecutionStrategy executionStrategy) {
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

        rankGenerator.pushLevel(config);

        return this;
    }

    @Override
    BlockDefinitionVisitor beforeAll(UnsafeBlock block) {
        beforeAllBlocks.peekLast().add(newOneTimeHook(block));
        return this;
    }

    @Override
    BlockDefinitionVisitor beforeEach(UnsafeBlock block) {
        beforeEachBlocks.peekLast().add(newHook(block));
        return this;
    }

    @Override
    BlockDefinitionVisitor example(ExampleConfiguration config, UnsafeBlock block) {
        List<Hook> beforeHooks = new LinkedList<>();
        beforeAllBlocks.forEach(beforeHooks::addAll);
        beforeEachBlocks.forEach(beforeHooks::addAll);

        if (executionStrategy.shouldBeIgnored(config.executionFlag(), executionFlags.peekLast())) {
            examples.add(newIgnoredExample(
                new LinkedList<>(descriptions),
                config.description(),
                rankGenerator.generate()
            ));
        } else {
            examples.add(newExample(
                new LinkedList<>(descriptions),
                config.description(),
                beforeHooks,
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

    List<Example> build() {
        return new LinkedList<>(examples);
    }
}
