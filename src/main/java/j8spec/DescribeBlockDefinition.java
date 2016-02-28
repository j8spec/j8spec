package j8spec;

import java.util.LinkedList;
import java.util.List;

import static j8spec.BlockExecutionFlag.DEFAULT;
import static j8spec.DescribeBlock.newRootDescribeBlock;

final class DescribeBlockDefinition {

    private final String description;
    private final BlockExecutionFlag executionFlag;
    private final Context<DescribeBlockDefinition> context;

    private final List<UnsafeBlock> beforeAllBlocks = new LinkedList<>();
    private final List<UnsafeBlock> beforeEachBlocks = new LinkedList<>();
    private final List<ItBlockDefinition> itBlockDefinitions = new LinkedList<>();

    private final List<DescribeBlockDefinition> describeBlockDefinitions = new LinkedList<>();

    static DescribeBlockDefinition newDescribeBlockDefinition(
        Class<?> specClass,
        Context<DescribeBlockDefinition> context
    ) {
        DescribeBlockDefinition block = new DescribeBlockDefinition(specClass.getName(), DEFAULT, context);
        context.switchTo(block);

        try {
            specClass.newInstance();
        } catch (J8SpecException e) {
            throw e;
        } catch (Exception e) {
            throw new SpecInitializationException("Failed to create instance of " + specClass + ".", e);
        }

        return block;
    }

    private DescribeBlockDefinition(
        String description,
        BlockExecutionFlag executionFlag,
        Context<DescribeBlockDefinition> context
    ) {
        this.description = description;
        this.executionFlag = executionFlag;
        this.context = context;
    }

    void addDescribe(String description, SafeBlock block, BlockExecutionFlag executionFlag) {
        ensureIsNotAlreadyDefined(
            description,
            describeBlockDefinitions.stream().anyMatch(d -> d.description.equals(description))
        );

        DescribeBlockDefinition describeBlockDefinition = new DescribeBlockDefinition(
            description,
            executionFlag,
            context
        );
        describeBlockDefinitions.add(describeBlockDefinition);

        context.switchTo(describeBlockDefinition);
        block.execute();
        context.restore();
    }

    void addBeforeAll(UnsafeBlock beforeAllBlock) {
        this.beforeAllBlocks.add(beforeAllBlock);
    }

    void addBeforeEach(UnsafeBlock beforeEachBlock) {
        this.beforeEachBlocks.add(beforeEachBlock);
    }

    void addIt(ItBlockDefinition itBlockDefinition) {
        ensureIsNotAlreadyDefined(
            itBlockDefinition.description(),
            itBlockDefinitions.stream().anyMatch(i -> i.description().equals(itBlockDefinition.description()))
        );

        itBlockDefinitions.add(itBlockDefinition);
    }

    private void ensureIsNotAlreadyDefined(String blockName, boolean isAlreadyDefined) {
        if (isAlreadyDefined) {
            throw new BlockAlreadyDefinedException(blockName + " block already defined");
        }
    }

    DescribeBlock toDescribeBlock() {
        DescribeBlock root = newRootDescribeBlock(description, beforeAllBlocks, beforeEachBlocks, itBlockDefinitions);
        describeBlockDefinitions.stream().forEach(block -> block.addAllDescribeBlocksTo(root));
        return root;
    }

    private void addAllDescribeBlocksTo(DescribeBlock parent) {
        DescribeBlock describeBlock = parent.addDescribeBlock(
            description,
            beforeAllBlocks,
            beforeEachBlocks,
            itBlockDefinitions,
            executionFlag
        );

        describeBlockDefinitions.stream().forEach(block -> block.addAllDescribeBlocksTo(describeBlock));
    }
}
