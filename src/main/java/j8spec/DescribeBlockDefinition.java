package j8spec;

import java.util.LinkedList;
import java.util.List;

import static j8spec.BlockExecutionFlag.DEFAULT;
import static j8spec.ItBlockDefinition.newItBlockDefinition;

final class DescribeBlockDefinition implements BlockDefinition {

    private final String description;
    private final BlockExecutionFlag executionFlag;
    private final Context<DescribeBlockDefinition> context;
    private final List<BlockDefinition> blockDefinitions = new LinkedList<>();
    private final List<BlockDefinition> hooks = new LinkedList<>();

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
        DescribeBlockDefinition describeBlockDefinition = new DescribeBlockDefinition(
            description,
            executionFlag,
            context
        );

        blockDefinitions.add(describeBlockDefinition);

        context.switchTo(describeBlockDefinition);
        block.execute();
        context.restore();
    }

    void addBeforeAll(UnsafeBlock beforeAllBlock) {
        hooks.add(new BeforeAllBlockDefinition(beforeAllBlock));
    }

    void addBeforeEach(UnsafeBlock beforeEachBlock) {
        hooks.add(new BeforeEachBlockDefinition(beforeEachBlock));
    }

    void addIt(ItBlockConfiguration itBlockConfig) {
        ItBlockDefinition itBlockDefinition = newItBlockDefinition(
            itBlockConfig.description(),
            itBlockConfig.block(),
            itBlockConfig.executionFlag(),
            itBlockConfig.expected()
        );

        blockDefinitions.add(itBlockDefinition);
    }

    @Override
    public void accept(BlockDefinitionVisitor visitor) {
        visitor.describe(description, executionFlag);

        for (BlockDefinition blockDefinition : hooks) {
            blockDefinition.accept(visitor);
        }

        for (BlockDefinition blockDefinition : blockDefinitions) {
            blockDefinition.accept(visitor);
        }

        visitor.describe();
    }
}
