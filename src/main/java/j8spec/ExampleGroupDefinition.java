package j8spec;

import java.util.LinkedList;
import java.util.List;

import static j8spec.BlockExecutionFlag.DEFAULT;

final class ExampleGroupDefinition implements BlockDefinition {

    private final ExampleGroupConfiguration config;
    private final Context<ExampleGroupDefinition> context;
    private final List<BlockDefinition> blockDefinitions = new LinkedList<>();
    private final List<BlockDefinition> hooks = new LinkedList<>();

    static ExampleGroupDefinition newExampleGroupDefinition(
        Class<?> specClass,
        Context<ExampleGroupDefinition> context
    ) {
        ExampleGroupConfiguration config = new ExampleGroupConfiguration.Builder()
            .description(specClass.getName())
            .executionFlag(DEFAULT)
            .build();
        ExampleGroupDefinition group = new ExampleGroupDefinition(config, context);
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

    private ExampleGroupDefinition(ExampleGroupConfiguration config, Context<ExampleGroupDefinition> context) {
        this.config = config;
        this.context = context;
    }

    void addGroup(ExampleGroupConfiguration config, SafeBlock block) {
        ExampleGroupDefinition exampleGroupDefinition = new ExampleGroupDefinition(config, context);

        blockDefinitions.add(exampleGroupDefinition);

        context.switchTo(exampleGroupDefinition);
        block.execute();
        context.restore();
    }

    void addBeforeAll(UnsafeBlock beforeAllBlock) {
        hooks.add(new BeforeAllBlockDefinition(beforeAllBlock));
    }

    void addBeforeEach(UnsafeBlock beforeEachBlock) {
        hooks.add(new BeforeEachBlockDefinition(beforeEachBlock));
    }

    void addExample(ExampleConfiguration itBlockConfig, UnsafeBlock block) {
        blockDefinitions.add(new ExampleDefinition(itBlockConfig, block));
    }

    @Override
    public void accept(BlockDefinitionVisitor visitor) {
        visitor.startGroup(config);

        for (BlockDefinition blockDefinition : hooks) {
            blockDefinition.accept(visitor);
        }

        for (BlockDefinition blockDefinition : blockDefinitions) {
            blockDefinition.accept(visitor);
        }

        visitor.endGroup();
    }
}
