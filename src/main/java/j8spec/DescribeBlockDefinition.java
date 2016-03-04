package j8spec;

import java.util.LinkedList;
import java.util.List;

import static j8spec.BlockExecutionFlag.DEFAULT;
import static j8spec.BlockExecutionOrder.DEFINED;

final class DescribeBlockDefinition implements BlockDefinition {

    private final ExampleGroupConfiguration config;

    private final Context<DescribeBlockDefinition> context;
    private final List<BlockDefinition> blockDefinitions = new LinkedList<>();
    private final List<BlockDefinition> hooks = new LinkedList<>();

    static DescribeBlockDefinition newDescribeBlockDefinition(
        Class<?> specClass,
        Context<DescribeBlockDefinition> context
    ) {
        ExampleGroupConfiguration config = new ExampleGroupConfiguration.Builder()
            .description(specClass.getName())
            .executionFlag(DEFAULT)
            .build();
        DescribeBlockDefinition group = new DescribeBlockDefinition(config, context);
        context.switchTo(group);

        try {
            specClass.newInstance();
        } catch (J8SpecException e) {
            throw e;
        } catch (Exception e) {
            throw new SpecInitializationException("Failed to create instance of " + specClass + ".", e);
        }

        return group;
    }

    private DescribeBlockDefinition(ExampleGroupConfiguration config, Context<DescribeBlockDefinition> context) {
        this.config = config;
        this.context = context;
    }

    void addGroup(ExampleGroupConfiguration config, SafeBlock block) {
        DescribeBlockDefinition describeBlockDefinition = new DescribeBlockDefinition(config, context);

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

    void addExample(ItBlockConfiguration itBlockConfig, UnsafeBlock block) {
        blockDefinitions.add(new ItBlockDefinition(itBlockConfig, block));
    }

    @Override
    public void accept(BlockDefinitionVisitor visitor) {
        visitor.startGroup(config.description(), config.executionFlag(), DEFINED);

        for (BlockDefinition blockDefinition : hooks) {
            blockDefinition.accept(visitor);
        }

        for (BlockDefinition blockDefinition : blockDefinitions) {
            blockDefinition.accept(visitor);
        }

        visitor.endGroup();
    }
}
