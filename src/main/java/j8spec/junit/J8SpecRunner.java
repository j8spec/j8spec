package j8spec.junit;

import j8spec.Example;
import j8spec.J8Spec;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static j8spec.junit.ExampleStatement.newStatement;
import static org.junit.runner.Description.createTestDescription;

/**
 * JUnit runner for J8Spec specs.
 * @since 1.0.0
 */
public final class J8SpecRunner extends ParentRunner<Example> {

    private final String specName;
    private final Map<Example, Description> descriptions = new HashMap<>();
    private final List<Example> examples;

    public J8SpecRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        try {
            specName = testClass.getName();
            examples = J8Spec.read(testClass);
        } catch (Exception e) {
            throw new InitializationError(e);
        }
    }

    @Override
    protected List<Example> getChildren() {
        return examples;
    }

    @Override
    protected Description describeChild(Example example) {
        if (!descriptions.containsKey(example)) {
            descriptions.put(
                example,
                createTestDescription(specName, buildChildName(example))
            );
        }
        return descriptions.get(example);
    }

    private String buildChildName(Example example) {
        List<String> name = new LinkedList<>();
        List<String> containerDescriptions = example.containerDescriptions();
        name.add(example.description());
        for (int i = 1; i < containerDescriptions.size(); i++) {
            name.add(containerDescriptions.get(i));
        }
        return String.join(", ", name);
    }

    @Override
    protected boolean isIgnored(Example example) {
        return example.shouldBeIgnored();
    }

    @Override
    protected void runChild(Example example, RunNotifier notifier) {
        Description description = describeChild(example);

        if (isIgnored(example)) {
            notifier.fireTestIgnored(description);
            return;
        }

        runLeaf(newStatement(example), description, notifier);
    }
}
