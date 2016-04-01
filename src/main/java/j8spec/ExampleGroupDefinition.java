package j8spec;

import j8spec.annotation.DefinedOrder;
import j8spec.annotation.RandomOrder;

import java.util.LinkedList;
import java.util.List;

import static j8spec.BlockExecutionFlag.DEFAULT;
import static j8spec.BlockDefinition.visitAll;

final class ExampleGroupDefinition implements BlockDefinition {

    private final ExampleGroupConfiguration config;
    private final ExampleGroupContext context;
    private final List<BlockDefinition> blockDefinitions = new LinkedList<>();
    private final List<BlockDefinition> hooks = new LinkedList<>();
    private final List<BlockDefinition> varInitializers = new LinkedList<>();

    static ExampleGroupDefinition newExampleGroupDefinition(
        Class<?> specClass,
        ExampleGroupContext context
    ) {
        ExampleGroupConfiguration.Builder configBuilder = new ExampleGroupConfiguration.Builder()
            .description(specClass.getName())
            .executionFlag(DEFAULT);

        configureExecutionOrder(specClass, configBuilder);

        ExampleGroupDefinition group = new ExampleGroupDefinition(configBuilder.build(), context);
        context.switchTo(group);

        try {
            specClass.newInstance();
        } catch (Exceptions.Base e) {
            throw e;
        } catch (Exception e) {
            throw new Exceptions.SpecInitializationFailed(specClass, e);
        }

        return group;
    }

    private static void configureExecutionOrder(Class<?> specClass, ExampleGroupConfiguration.Builder configBuilder) {
        if (specClass.isAnnotationPresent(DefinedOrder.class)) {
            configBuilder.definedOrder();
        } else {
            configBuilder.randomOrder();
            if (specClass.isAnnotationPresent(RandomOrder.class)) {
                configBuilder.seed(specClass.getAnnotation(RandomOrder.class).seed());
            }
        }
    }

    private ExampleGroupDefinition(ExampleGroupConfiguration config, ExampleGroupContext context) {
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

    <T> void addVarInitializer(Var<T> var, UnsafeFunction<T> initFunction) {
        varInitializers.add(new BlockDefinitions.VarInitializer<>(var, initFunction));
    }

    void addBeforeAll(UnsafeBlock beforeAllBlock) {
        hooks.add(new BlockDefinitions.BeforeAll(beforeAllBlock));
    }

    void addBeforeEach(UnsafeBlock beforeEachBlock) {
        hooks.add(new BlockDefinitions.BeforeEach(beforeEachBlock));
    }

    void addAfterEach(UnsafeBlock afterEachBlock) {
        hooks.add(new BlockDefinitions.AfterEach(afterEachBlock));
    }

    void addAfterAll(UnsafeBlock afterAllBlock) {
        hooks.add(new BlockDefinitions.AfterAll(afterAllBlock));
    }

    void addExample(ExampleConfiguration exampleConfig, UnsafeBlock block) {
        blockDefinitions.add(new BlockDefinitions.Example(exampleConfig, block));
    }

    @Override
    public void accept(BlockDefinitionVisitor visitor) {
        visitor.startGroup(config);

        visitAll(visitor, varInitializers);
        visitAll(visitor, hooks);
        visitAll(visitor, blockDefinitions);

        visitor.endGroup();
    }
}
