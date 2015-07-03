package j8spec.junit;

import j8spec.DescribeBlock;
import j8spec.ItBlock;
import j8spec.J8Spec;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static j8spec.junit.ItBlockStatement.newStatement;
import static java.util.Collections.unmodifiableList;
import static org.junit.runner.Description.createTestDescription;

/**
 * JUnit runner for J8Spec specs.
 * @since 1.0.0
 */
public final class J8SpecRunner extends ParentRunner<ItBlock> {

    private final DescribeBlock describeBlock;
    private final Map<ItBlock, Description> descriptions = new HashMap<>();
    private List<ItBlock> itBlocks;

    public J8SpecRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        try {
            describeBlock = J8Spec.read(testClass);
        } catch (Exception e) {
            throw new InitializationError(e);
        }
    }

    @Override
    protected List<ItBlock> getChildren() {
        if (itBlocks == null) {
            itBlocks = unmodifiableList(describeBlock.flattenItBlocks());
        }
        return itBlocks;
    }

    @Override
    protected Description describeChild(ItBlock itBlock) {
        if (!descriptions.containsKey(itBlock)) {
            descriptions.put(
                itBlock,
                createTestDescription(describeBlock.specClass().getName(), buildChildName(itBlock))
            );
        }
        return descriptions.get(itBlock);
    }

    private String buildChildName(ItBlock itBlock) {
        List<String> name = new LinkedList<>();
        List<String> containerDescriptions = itBlock.containerDescriptions();
        name.add(itBlock.description());
        for (int i = 1; i < containerDescriptions.size(); i++) {
            name.add(containerDescriptions.get(i));
        }
        return String.join(", ", name);
    }

    @Override
    protected boolean isIgnored(ItBlock itBlock) {
        return itBlock.shouldBeIgnored();
    }

    @Override
    protected void runChild(ItBlock itBlock, RunNotifier notifier) {
        Description description = describeChild(itBlock);

        if (isIgnored(itBlock)) {
            notifier.fireTestIgnored(description);
            return;
        }

        runLeaf(newStatement(itBlock), description, notifier);
    }
}
