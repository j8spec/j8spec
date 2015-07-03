package j8spec;

import java.util.function.Function;

import static j8spec.DescribeBlockDefinition.newDescribeBlockDefinition;
import static java.util.function.Function.identity;

/**
 * J8Spec main facade.
 *
 * @since 1.0.0
 */
public final class J8Spec {

    private static final ThreadLocal<Context<DescribeBlockDefinition>> context = new ThreadLocal<>();

    /**
     * Defines a new "describe" block.
     *
     * @param description textual description of the new block
     * @param body code to be executed
     * @throws IllegalContextException if called directly
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context was defined
     * already
     * @since 1.0.0
     */
    public static synchronized void describe(String description, Runnable body) {
        isValidContext("describe");
        context.get().current().describe(description, body);
    }

    /**
     * Defines a new ignored "describe" block.
     *
     * @param description textual description of the new block
     * @param body code to be executed
     * @throws IllegalContextException if called directly
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context was defined
     * already
     * @throws CIModeEnabledException if the system property <code>j8spec.ci.mode</code> is <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void xdescribe(String description, Runnable body) {
        notAllowedWhenCIModeEnabled("xdescribe");
        isValidContext("xdescribe");
        context.get().current().xdescribe(description, body);
    }

    /**
     * Defines a new focused "describe" block.
     *
     * @param description textual description of the new block
     * @param body code to be executed
     * @throws IllegalContextException if called directly
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context was defined
     * already
     * @throws CIModeEnabledException if the system property <code>j8spec.ci.mode</code> is <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void fdescribe(String description, Runnable body) {
        notAllowedWhenCIModeEnabled("fdescribe");
        isValidContext("fdescribe");
        context.get().current().fdescribe(description, body);
    }

    /**
     * Defines a new "before all" block.
     *
     * @param body code to be executed before all "it" blocks
     * @throws IllegalContextException if called directly
     * @since 2.0.0
     */
    public static synchronized void beforeAll(Runnable body) {
        isValidContext("beforeAll");
        context.get().current().beforeAll(body);
    }

    /**
     * Defines a new "before each" block.
     *
     * @param body code to be executed before each "it" block
     * @throws IllegalContextException if called directly
     * @since 1.0.0
     */
    public static synchronized void beforeEach(Runnable body) {
        isValidContext("beforeEach");
        context.get().current().beforeEach(body);
    }

    /**
     * Defines a new "it" block.
     *
     * @param description textual description of the new block
     * @param body code to be executed
     * @throws IllegalContextException if called directly
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context was defined
     * already
     * @since 1.0.0
     */
    public static synchronized void it(String description, Runnable body) {
        it(description, identity(), body);
    }

    /**
     * Defines a new "it" block using custom configuration.
     *
     * @param description textual description of the new block
     * @param collector block configuration collector
     * @param body code to be executed
     * @throws IllegalContextException if called directly
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context was defined
     * @since 2.0.0
     */
    public static synchronized void it(
        String description,
        Function<ItBlockDefinitionBuilder, ItBlockDefinitionBuilder> collector,
        Runnable body
    ) {
        isValidContext("it");
        ItBlockDefinition itBlockDefinition = collector.apply(new ItBlockDefinitionBuilder())
            .body(body)
            .newItBlockDefinition();
        context.get().current().it(description, itBlockDefinition);
    }

    /**
     * Defines a new ignored "it" block.
     *
     * @param description textual description of the new block
     * @param body code to be executed
     * @throws IllegalContextException if called directly
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context was defined
     * @throws CIModeEnabledException if the system property <code>j8spec.ci.mode</code> is <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void xit(String description, Runnable body) {
        xit(description, identity(), body);
    }

    /**
     * Defines a new ignored "it" block using custom configuration.
     *
     * @param description textual description of the new block
     * @param collector block configuration collector
     * @param body code to be executed
     * @throws IllegalContextException if called directly
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context was defined
     * @throws CIModeEnabledException if the system property <code>j8spec.ci.mode</code> is <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void xit(
        String description,
        Function<ItBlockDefinitionBuilder, ItBlockDefinitionBuilder> collector,
        Runnable body
    ) {
        notAllowedWhenCIModeEnabled("xit");
        isValidContext("xit");
        ItBlockDefinition itBlockDefinition = collector.apply(new ItBlockDefinitionBuilder())
            .body(body)
            .newIgnoredItBlockDefinition();
        context.get().current().it(description, itBlockDefinition);
    }

    /**
     * Defines a new focused "it" block.
     *
     * @param description textual description of the new block
     * @param body code to be executed
     * @throws IllegalContextException if called directly
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context was defined
     * @throws CIModeEnabledException if the system property <code>j8spec.ci.mode</code> is <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void fit(String description, Runnable body) {
        fit(description, identity(), body);
    }

    /**
     * Defines a new focused "it" block using custom configuration.
     *
     * @param description textual description of the new block
     * @param collector block configuration collector
     * @param body code to be executed
     * @throws IllegalContextException if called directly
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context was defined
     * @throws CIModeEnabledException if the system property <code>j8spec.ci.mode</code> is <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void fit(
        String description,
        Function<ItBlockDefinitionBuilder, ItBlockDefinitionBuilder> collector,
        Runnable body
    ) {
        notAllowedWhenCIModeEnabled("fit");
        isValidContext("fit");
        ItBlockDefinition itBlockDefinition = collector.apply(new ItBlockDefinitionBuilder())
            .body(body)
            .newFocusedItBlockDefinition();
        context.get().current().it(description, itBlockDefinition);
    }

    private static void notAllowedWhenCIModeEnabled(final String methodName) {
        if (Boolean.valueOf(System.getProperty("j8spec.ci.mode", "false"))) {
            throw new CIModeEnabledException("'" + methodName + "' not allowed when j8spec.ci.mode enabled");
        }
    }

    private static void isValidContext(final String methodName) {
        if (context.get() == null) {
            throw new IllegalContextException(
                "'" + methodName + "' should not be invoked from outside a spec definition."
            );
        }
    }

    /**
     * Uses a spec class to build and populate a {@link DescribeBlock} object.
     *
     * @param specClass class that contains the spec definition
     * @return {@link DescribeBlock} object that represents the spec definition
     * @since 2.0.0
     */
    public static synchronized DescribeBlock read(Class<?> specClass) {
        context.set(new Context<>());
        try {
            return newDescribeBlockDefinition(specClass, context.get()).toDescribeBlock();
        } finally {
            context.set(null);
        }
    }

    private J8Spec() {}
}
