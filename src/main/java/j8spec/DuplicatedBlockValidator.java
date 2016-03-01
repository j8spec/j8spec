package j8spec;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

class DuplicatedBlockValidator extends BlockDefinitionVisitor {

    private final Deque<Set<String>> groupDescriptions = new LinkedList<>();
    private final Deque<Set<String>> exampleDescriptions = new LinkedList<>();

    @Override
    BlockDefinitionVisitor describe(String description, BlockExecutionFlag executionFlag) {
        if (!groupDescriptions.isEmpty()) {
            if (groupDescriptions.peekLast().contains(description)) {
                throw new BlockAlreadyDefinedException(description + " block already defined");
            }
            groupDescriptions.peekLast().add(description);
        }
        groupDescriptions.addLast(new HashSet<>());
        exampleDescriptions.addLast(new HashSet<>());
        return this;
    }

    @Override
    BlockDefinitionVisitor it(
        String description,
        UnsafeBlock block,
        BlockExecutionFlag executionFlag,
        Class<? extends Throwable> expectedException
    ) {
        if (exampleDescriptions.peekLast().contains(description)) {
            throw new BlockAlreadyDefinedException(description + " block already defined");
        }
        exampleDescriptions.peekLast().add(description);
        return this;
    }

    @Override
    BlockDefinitionVisitor describe() {
        groupDescriptions.removeLast();
        exampleDescriptions.removeLast();
        return this;
    }
}
