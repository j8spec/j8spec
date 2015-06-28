package j8spec;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static j8spec.BlockExecutionFlag.DEFAULT;
import static j8spec.BlockExecutionFlag.FOCUSED;
import static j8spec.BlockExecutionFlag.IGNORED;
import static j8spec.DescribeBlock.newRootDescribeBlock;

final class DescribeBlockDefinition {

    private final Class<?> specClass;
    private final String description;
    private final Runnable body;
    private final BlockExecutionFlag executionFlag;
    private final List<DescribeBlockDefinition> describeBlockDefinitions = new LinkedList<>();
    private final Map<String, ItBlockDefinition> itBlockDefinitions = new HashMap<>();
    private final List<Runnable> beforeAllBlocks = new LinkedList<>();
    private final List<Runnable> beforeEachBlocks = new LinkedList<>();

    DescribeBlockDefinition(Class<?> specClass) {
        this.specClass = specClass;
        this.description = specClass.getName();
        this.body = () -> {
            try {
                specClass.newInstance();
            } catch (J8SpecException e) {
                throw e;
            } catch (Exception e) {
                throw new SpecInitializationException("Failed to create instance of " + specClass + ".", e);
            }
        };
        this.executionFlag = DEFAULT;
    }

    private DescribeBlockDefinition(Class<?> specClass, String description, Runnable body, BlockExecutionFlag executionFlag) {
        this.specClass = specClass;
        this.description = description;
        this.body = body;
        this.executionFlag = executionFlag;
    }

    void describe(String description, Runnable body) {
        describeBlockDefinitions.add(new DescribeBlockDefinition(specClass, description, body, DEFAULT));
    }

    void xdescribe(String description, Runnable body) {
        describeBlockDefinitions.add(new DescribeBlockDefinition(specClass, description, body, IGNORED));
    }

    void fdescribe(String description, Runnable body) {
        describeBlockDefinitions.add(new DescribeBlockDefinition(specClass, description, body, FOCUSED));
    }

    void beforeAll(Runnable beforeAllBlock) {
        this.beforeAllBlocks.add(beforeAllBlock);
    }

    void beforeEach(Runnable beforeEachBlock) {
        this.beforeEachBlocks.add(beforeEachBlock);
    }

    void it(String description, ItBlockDefinition itBlockDefinition) {
        ensureIsNotAlreadyDefined(description, itBlockDefinitions.containsKey(description));
        itBlockDefinitions.put(description, itBlockDefinition);
    }

    private void ensureIsNotAlreadyDefined(String blockName, boolean result) {
        if (result) {
            throw new BlockAlreadyDefinedException(blockName + " block already defined");
        }
    }

    void evaluate(Context<DescribeBlockDefinition> context) {
        context.switchTo(this);

        this.body.run();
        this.describeBlockDefinitions.stream().forEach(b -> b.evaluate(context));

        context.restore();
    }

    DescribeBlock toDescribeBlock() {
        return toDescribeBlock(null);
    }

    private DescribeBlock toDescribeBlock(DescribeBlock parent) {
        DescribeBlock describeBlock = newDescribeBlock(parent);
        this.describeBlockDefinitions.stream().forEach(block -> block.toDescribeBlock(describeBlock));
        return describeBlock;
    }

    private DescribeBlock newDescribeBlock(DescribeBlock parent) {
        if (parent == null) {
            return newRootDescribeBlock(specClass, beforeAllBlocks, beforeEachBlocks, itBlockDefinitions);
        }
        return addDescribeBlockTo(parent);
    }

    private DescribeBlock addDescribeBlockTo(DescribeBlock parent) {
        if (IGNORED.equals(executionFlag)) {
            return parent.addIgnoredDescribeBlock(description, beforeAllBlocks, beforeEachBlocks, itBlockDefinitions);
        }

        if (FOCUSED.equals(executionFlag)) {
            return parent.addFocusedDescribeBlock(description, beforeAllBlocks, beforeEachBlocks, itBlockDefinitions);
        }

        return parent.addDescribeBlock(description, beforeAllBlocks, beforeEachBlocks, itBlockDefinitions);
    }
}
