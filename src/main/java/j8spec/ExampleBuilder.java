package j8spec;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static j8spec.BlockExecutionFlag.DEFAULT;
import static j8spec.Hook.newHook;
import static j8spec.Hook.newOneTimeHook;

final class ExampleBuilder extends BlockDefinitionVisitor {

    private static List<UnsafeBlock> asHooks(Deque<List<UnsafeBlock>> hookQueue) {
        List<UnsafeBlock> result = new LinkedList<>();
        hookQueue.forEach(result::addAll);
        return result;
    }

    private static List<VarInitializer<?>> allVarInitializers(Deque<List<VarInitializer<?>>> varInitializers) {
        List<VarInitializer<?>> result = new LinkedList<>();
        varInitializers.forEach(result::addAll);
        return result;
    }

    private final BlockExecutionStrategy executionStrategy;
    private final Deque<String> descriptions = new LinkedList<>();
    private final Deque<BlockExecutionFlag> executionFlags = new LinkedList<>();
    private final Deque<List<VarInitializer<?>>> varInitializers = new LinkedList<>();
    private final Deque<List<UnsafeBlock>> beforeAllBlocks = new LinkedList<>();
    private final Deque<List<UnsafeBlock>> beforeEachBlocks = new LinkedList<>();
    private final Deque<List<UnsafeBlock>> afterEachBlocks = new LinkedList<>();
    private final Deque<List<UnsafeBlock>> afterAllBlocks = new LinkedList<>();
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

        varInitializers.addLast(new LinkedList<>());
        beforeAllBlocks.addLast(new LinkedList<>());
        beforeEachBlocks.addLast(new LinkedList<>());
        afterEachBlocks.addFirst(new LinkedList<>());
        afterAllBlocks.addFirst(new LinkedList<>());

        rankGenerator.pushLevel(config);

        return this;
    }

    @Override
    <T> BlockDefinitionVisitor varInitializer(Var<T> var, UnsafeFunction<T> initFunction) {
        varInitializers.peekLast().add(new VarInitializer<>(var, initFunction));
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
    BlockDefinitionVisitor afterEach(UnsafeBlock block) {
        afterEachBlocks.peekFirst().add(newHook(block));
        return this;
    }

    @Override
    BlockDefinitionVisitor afterAll(UnsafeBlock block) {
        afterAllBlocks.peekFirst().add(newOneTimeHook(block));
        return this;
    }

    @Override
    BlockDefinitionVisitor example(ExampleConfiguration config, UnsafeBlock block) {
        Example.Builder builder = new Example.Builder()
            .containerDescriptions(new LinkedList<>(descriptions))
            .description(config.description())
            .rank(rankGenerator.generate());

        if (executionStrategy.shouldBeIgnored(config.executionFlag(), executionFlags.peekLast())) {
            builder.ignored();
        } else {
            builder
                .varInitializers(allVarInitializers(varInitializers))
                .beforeAllHooks(asHooks(beforeAllBlocks))
                .beforeEachHooks(asHooks(beforeEachBlocks))
                .afterEachHooks(asHooks(afterEachBlocks))
                .afterAllHooks(asHooks(afterAllBlocks))
                .block(block)
                .expectedException(config.expectedException())
                .timeout(config.timeout(), config.timeoutUnit());
        }

        examples.add(builder.build());

        return this;
    }

    @Override
    BlockDefinitionVisitor endGroup() {
        descriptions.removeLast();
        executionFlags.removeLast();
        varInitializers.removeLast();
        beforeAllBlocks.removeLast();
        beforeEachBlocks.removeLast();
        afterEachBlocks.removeFirst();
        afterAllBlocks.removeFirst();
        rankGenerator.popLevel();
        return this;
    }

    List<Example> build() {
        ArrayList<Example> result = new ArrayList<>(examples);

        if (result.size() > 1) {
            for (int i = 0; i < result.size() - 1; i++) {
                result.get(i).next(result.get(i + 1));
            }

            for (int j = result.size() - 1; j > 0; j--) {
                result.get(j).previous(result.get(j - 1));
            }
        }

        return result;
    }
}
