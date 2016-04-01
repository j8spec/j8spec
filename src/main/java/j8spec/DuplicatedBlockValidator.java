package j8spec;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

final class DuplicatedBlockValidator extends BlockDefinitionVisitor {

    private final Deque<Set<String>> groupDescriptions = new LinkedList<>();
    private final Deque<Set<String>> exampleDescriptions = new LinkedList<>();
    private final Deque<Set<Var<?>>> varInitializers = new LinkedList<>();

    @Override
    BlockDefinitionVisitor startGroup(ExampleGroupConfiguration config) {
        if (!groupDescriptions.isEmpty()) {
            if (groupDescriptions.peekLast().contains(config.description())) {
                throw new Exceptions.BlockAlreadyDefined(config.description());
            }
            groupDescriptions.peekLast().add(config.description());
        }
        groupDescriptions.addLast(new HashSet<>());
        varInitializers.addLast(new HashSet<>());
        exampleDescriptions.addLast(new HashSet<>());
        return this;
    }

    @Override
    BlockDefinitionVisitor example(ExampleConfiguration config, UnsafeBlock block) {
        if (exampleDescriptions.peekLast().contains(config.description())) {
            throw new Exceptions.BlockAlreadyDefined(config.description());
        }
        exampleDescriptions.peekLast().add(config.description());
        return this;
    }

    @Override
    <T> BlockDefinitionVisitor varInitializer(Var<T> var, UnsafeFunction<T> initFunction) {
        if (varInitializers.peekLast().contains(var)) {
            throw new Exceptions.VariableInitializerAlreadyDefined();
        }
        varInitializers.peekLast().add(var);
        return this;
    }

    @Override
    BlockDefinitionVisitor endGroup() {
        groupDescriptions.removeLast();
        varInitializers.removeLast();
        exampleDescriptions.removeLast();
        return this;
    }
}
