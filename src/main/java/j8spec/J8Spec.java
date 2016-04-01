package j8spec;

import java.util.List;
import java.util.function.Function;

import static j8spec.BlockExecutionFlag.DEFAULT;
import static j8spec.BlockExecutionFlag.FOCUSED;
import static j8spec.BlockExecutionFlag.IGNORED;
import static j8spec.ExampleGroupDefinition.newExampleGroupDefinition;
import static java.util.function.Function.identity;

/**
 * J8Spec main facade.
 *
 * <p>
 *     <b>Note:</b> this class is thread-safe.
 * </p>
 *
 * @since 1.0.0
 */
public final class J8Spec {

    private static final ThreadLocal<ExampleGroupContext> contexts = new ThreadLocal<>();

    /**
     * Defines a new example group.
     *
     * @param description textual description of the example group
     * @param block code that defines inner examples or example groups, like "describe", "it", etc - this code is
     *              executed immediately
     * @throws Exceptions.IllegalContext if called outside the context of the {@link #read(Class)} method
     * @throws Exceptions.BlockAlreadyDefined if another example group with the same description in the same
     * context has been defined already
     * @since 1.0.0
     */
    public static synchronized void describe(String description, SafeBlock block) {
        isValidContext("describe");
        ExampleGroupConfiguration config = new ExampleGroupConfiguration.Builder()
            .description(description)
            .executionFlag(DEFAULT)
            .build();
        contexts.get().current().addGroup(config, block);
    }

    /**
     * Alias for {@link #describe(String, SafeBlock)}.
     *
     * @param description textual description of the example group
     * @param block code that defines inner examples and example groups, like "describe", "it", etc - this code is
     *              executed immediately
     * @throws Exceptions.IllegalContext if called outside the context of the {@link #read(Class)} method
     * @throws Exceptions.BlockAlreadyDefined if another example group with the same description in the same context
     * has been defined already
     * @since 2.0.0
     */
    public static synchronized void context(String description, SafeBlock block) {
        isValidContext("context");
        ExampleGroupConfiguration config = new ExampleGroupConfiguration.Builder()
            .description(description)
            .executionFlag(DEFAULT)
            .build();
        contexts.get().current().addGroup(config, block);
    }

    /**
     * Defines a new ignored example group.
     *
     * @param description textual description of the new example group
     * @param block code that defines inner example and example groups, like "describe", "it", etc - this code is
     *              executed immediately
     * @throws Exceptions.IllegalContext if called outside the context of the {@link #read(Class)} method
     * @throws Exceptions.BlockAlreadyDefined if another example group with the same description in the same context
     * has been defined already
     * @throws Exceptions.OperationNotAllowedInCIMode if the system property <code>j8spec.ci.mode</code> is
     * <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void xdescribe(String description, SafeBlock block) {
        notAllowedWhenCIModeEnabled("xdescribe");
        isValidContext("xdescribe");
        ExampleGroupConfiguration config = new ExampleGroupConfiguration.Builder()
            .description(description)
            .executionFlag(IGNORED)
            .build();
        contexts.get().current().addGroup(config, block);
    }

    /**
     * Alias for {@link #xdescribe(String, SafeBlock)}.
     *
     * @param description textual description of the new example group
     * @param block code that defines inner examples and example groups, like "describe", "it", etc - this code is
     *              executed immediately
     * @throws Exceptions.IllegalContext if called outside the context of the {@link #read(Class)} method
     * @throws Exceptions.BlockAlreadyDefined if another example group with the same description in the same context
     * has been defined already
     * @throws Exceptions.OperationNotAllowedInCIMode if the system property <code>j8spec.ci.mode</code> is
     * <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void xcontext(String description, SafeBlock block) {
        notAllowedWhenCIModeEnabled("xcontext");
        isValidContext("xcontext");
        ExampleGroupConfiguration config = new ExampleGroupConfiguration.Builder()
            .description(description)
            .executionFlag(IGNORED)
            .build();
        contexts.get().current().addGroup(config, block);
    }

    /**
     * Defines a new focused example group.
     *
     * @param description textual description of the new example group
     * @param block code that defines inner examples and example groups, like "describe", "it", etc - this code is
     *              executed immediately
     * @throws Exceptions.IllegalContext if called outside the context of the {@link #read(Class)} method
     * @throws Exceptions.BlockAlreadyDefined if another example group with the same description in the same context
     * has been defined already
     * @throws Exceptions.OperationNotAllowedInCIMode if the system property <code>j8spec.ci.mode</code> is
     * <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void fdescribe(String description, SafeBlock block) {
        notAllowedWhenCIModeEnabled("fdescribe");
        isValidContext("fdescribe");
        ExampleGroupConfiguration config = new ExampleGroupConfiguration.Builder()
            .description(description)
            .executionFlag(FOCUSED)
            .build();
        contexts.get().current().addGroup(config, block);
    }

    /**
     * Alias for {@link #fdescribe(String, SafeBlock)}.
     *
     * @param description textual description of the example group
     * @param block code that defines inner examples and example group, like "describe", "it", etc - this code is
     *              executed immediately
     * @throws Exceptions.IllegalContext if called outside the context of the {@link #read(Class)} method
     * @throws Exceptions.BlockAlreadyDefined if another example group with the same description in the same context
     * has been defined already
     * @throws Exceptions.OperationNotAllowedInCIMode if the system property <code>j8spec.ci.mode</code> is <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void fcontext(String description, SafeBlock block) {
        notAllowedWhenCIModeEnabled("fcontext");
        isValidContext("fcontext");
        ExampleGroupConfiguration config = new ExampleGroupConfiguration.Builder()
            .description(description)
            .executionFlag(FOCUSED)
            .build();
        contexts.get().current().addGroup(config, block);
    }

    /**
     * Defines a new hook to run once before all examples in the group.
     *
     * @param block code to be executed once before all examples
     * @throws Exceptions.IllegalContext if called outside the context of the {@link #read(Class)} method
     * @since 2.0.0
     */
    public static synchronized void beforeAll(UnsafeBlock block) {
        isValidContext("beforeAll");
        contexts.get().current().addBeforeAll(block);
    }

    /**
     * Defines a new hook to run before each example in the group.
     *
     * @param block code to be executed before each example
     * @throws Exceptions.IllegalContext if called outside the context of the {@link #read(Class)} method
     * @since 1.0.0
     */
    public static synchronized void beforeEach(UnsafeBlock block) {
        isValidContext("beforeEach");
        contexts.get().current().addBeforeEach(block);
    }

    /**
     * Defines a new hook to run after each example in the group.
     *
     * @param block code to be executed after each example
     * @throws Exceptions.IllegalContext if called outside the context of the {@link #read(Class)} method
     * @since 3.0.0
     */
    public static synchronized void afterEach(UnsafeBlock block) {
        isValidContext("afterEach");
        contexts.get().current().addAfterEach(block);
    }

    /**
     * Defines a new hook to run once after all examples in the group.
     *
     * @param block code to be executed once after all examples
     * @throws Exceptions.IllegalContext if called outside the context of the {@link #read(Class)} method
     * @since 3.0.0
     */
    public static synchronized void afterAll(UnsafeBlock block) {
        isValidContext("afterAll");
        contexts.get().current().addAfterAll(block);
    }

    /**
     * Defines a new example.
     *
     * @param description textual description of the new example
     * @param block code to be executed
     * @throws Exceptions.IllegalContext if called outside the context of the {@link #read(Class)} method
     * @throws Exceptions.BlockAlreadyDefined if another block with the same description in the same context has been
     * defined already
     * @since 1.0.0
     */
    public static synchronized void it(String description, UnsafeBlock block) {
        it(description, identity(), block);
    }

    /**
     * Defines a new example group using custom configuration.
     *
     * @param description textual description of the new example
     * @param collector block configuration collector
     * @param block code to be executed
     * @throws Exceptions.IllegalContext if called outside the context of the {@link #read(Class)} method
     * @throws Exceptions.BlockAlreadyDefined if another block with the same description in the same context has been
     * defined already
     * @since 2.0.0
     */
    public static synchronized void it(
        String description,
        Function<ExampleConfiguration.Builder, ExampleConfiguration.Builder> collector,
        UnsafeBlock block
    ) {
        isValidContext("it");
        ExampleConfiguration config = collector.apply(new ExampleConfiguration.Builder())
            .description(description)
            .executionFlag(DEFAULT)
            .build();
        contexts.get().current().addExample(config, block);
    }

    /**
     * Defines a new ignored "it" block.
     *
     * @param description textual description of the new block
     * @param block code to be executed
     * @throws Exceptions.IllegalContext if called outside the context of the {@link #read(Class)} method
     * @throws Exceptions.BlockAlreadyDefined if another block with the same description in the same context has been
     * defined already
     * @throws Exceptions.OperationNotAllowedInCIMode if the system property <code>j8spec.ci.mode</code> is <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void xit(String description, UnsafeBlock block) {
        xit(description, identity(), block);
    }

    /**
     * Defines a new ignored example using custom configuration.
     *
     * @param description textual description of the new example
     * @param collector block configuration collector
     * @param block code to be executed
     * @throws Exceptions.IllegalContext if called outside the context of the {@link #read(Class)} method
     * @throws Exceptions.BlockAlreadyDefined if another example with the same description in the same context has been
     * defined already
     * @throws Exceptions.OperationNotAllowedInCIMode if the system property <code>j8spec.ci.mode</code> is
     * <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void xit(
        String description,
        Function<ExampleConfiguration.Builder, ExampleConfiguration.Builder> collector,
        UnsafeBlock block
    ) {
        notAllowedWhenCIModeEnabled("xit");
        isValidContext("xit");
        ExampleConfiguration config = collector.apply(new ExampleConfiguration.Builder())
            .description(description)
            .executionFlag(IGNORED)
            .build();
        contexts.get().current().addExample(config, block);
    }

    /**
     * Defines a new focused example group.
     *
     * @param description textual description of the new example
     * @param block code to be executed
     * @throws Exceptions.IllegalContext if called outside the context of the {@link #read(Class)} method
     * @throws Exceptions.BlockAlreadyDefined if another example with the same description in the same context has been
     * defined already
     * @throws Exceptions.OperationNotAllowedInCIMode if the system property <code>j8spec.ci.mode</code> is
     * <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void fit(String description, UnsafeBlock block) {
        fit(description, identity(), block);
    }

    /**
     * Defines a new focused example using custom configuration.
     *
     * @param description textual description of the new example
     * @param collector block configuration collector
     * @param block code to be executed
     * @throws Exceptions.IllegalContext if called outside the context of the {@link #read(Class)} method
     * @throws Exceptions.BlockAlreadyDefined if another example with the same description in the same context has been
     * defined already
     * @throws Exceptions.OperationNotAllowedInCIMode if the system property <code>j8spec.ci.mode</code> is
     * <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void fit(
        String description,
        Function<ExampleConfiguration.Builder, ExampleConfiguration.Builder> collector,
        UnsafeBlock block
    ) {
        notAllowedWhenCIModeEnabled("fit");
        isValidContext("fit");
        ExampleConfiguration config = collector.apply(new ExampleConfiguration.Builder())
            .description(description)
            .executionFlag(FOCUSED)
            .build();
        contexts.get().current().addExample(config, block);
    }

    /**
     * Initializes the provided variable before executing hooks and examples.
     *
     * @param var variable to be initialized
     * @param initFunction initialization function that will provide the value for the variable
     * @param <T> the type of the value stored by <code>var</code> and returned by <code>initFunction</code>
     * @throws Exceptions.IllegalContext if called outside the context of the {@link #read(Class)} method
     * @throws Exceptions.VariableInitializerAlreadyDefined if another initializer was defined for the provided
     * variable in the same context
     * @since 3.1.0
     */
    public static <T> void let(Var<T> var, UnsafeFunction<T> initFunction) {
        isValidContext("let");
        contexts.get().current().addVarInitializer(var, initFunction);
    }

    private static void notAllowedWhenCIModeEnabled(final String methodName) {
        if (Boolean.valueOf(System.getProperty("j8spec.ci.mode", "false"))) {
            throw new Exceptions.OperationNotAllowedInCIMode(methodName);
        }
    }

    private static void isValidContext(final String methodName) {
        if (contexts.get() == null) {
            throw new Exceptions.IllegalContext(methodName);
        }
    }

    /**
     * Creates a wrapper object to allow "final" variables to have their value modified. The initial
     * value is <code>null</code>.
     *
     * @param <T> type of value the variable object can store
     * @return new variable object
     * @since 3.1.0
     */
    public static <T> Var<T> var() {
        return new Var<>();
    }

    /**
     * Access the value stored in the given variable object.
     *
     * @param var variable object
     * @param <T> type of value the variable object can store
     * @return value stored in the variable object
     * @since 3.1.0
     */
    public static <T> T var(Var<T> var) {
        return var.value;
    }

    /**
     * Stores the given value in the provided variable object.
     *
     * @param var variable object
     * @param value value to be stored
     * @param <T> type of value the variable object can store
     * @return value stored in the variable object
     * @since 3.1.0
     */
    public static <T> T var(Var<T> var, T value) {
        return var.value = value;
    }

    /**
     * Uses the given spec class to build and populate a list of {@link Example} objects ready to be executed.
     *
     * @param specClass class with a public default constructor that contains the spec definition
     * @return {@link Example} objects that represent the spec definition and can be executed
     * @throws Exceptions.SpecInitializationFailed if it is not possible to create an instance of <code>specClass</code>
     * @since 2.0.0
     */
    public static synchronized List<Example> read(Class<?> specClass) {
        contexts.set(new ExampleGroupContext());
        try {
            ExampleGroupDefinition exampleGroupDefinition = newExampleGroupDefinition(specClass, contexts.get());

            exampleGroupDefinition.accept(new DuplicatedBlockValidator());

            BlockExecutionStrategySelector strategySelector = new BlockExecutionStrategySelector();
            exampleGroupDefinition.accept(strategySelector);

            ExampleBuilder exampleBuilder = new ExampleBuilder(strategySelector.strategy());
            exampleGroupDefinition.accept(exampleBuilder);

            return exampleBuilder.build();
        } finally {
            contexts.set(null);
        }
    }

    private J8Spec() {}
}
