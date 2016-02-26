package j8spec;

import java.util.function.Function;

import static j8spec.DescribeBlockDefinition.newDescribeBlockDefinition;
import static j8spec.ItBlockConfiguration.newItBlockConfiguration;
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

    private static final ThreadLocal<Context<DescribeBlockDefinition>> contexts = new ThreadLocal<>();

    /**
     * Defines a new "describe" block.
     *
     * @param description textual description of the new block
     * @param body code that defines inner blocks, like "describe", "it", etc - this code is executed
     *             immediately
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context has been
     * defined already
     * @since 1.0.0
     */
    public static synchronized void describe(String description, SafeBlock body) {
        isValidContext("describe");
        contexts.get().current().describe(description, body);
    }

    /**
     * Alias for {@link #describe(String, SafeBlock)}.
     *
     * @param description textual description of the new block
     * @param body code that defines inner blocks, like "describe", "it", etc - this code is executed
     *             immediately
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context has been
     * defined already
     * @since 2.0.0
     */
    public static synchronized void context(String description, SafeBlock body) {
        isValidContext("context");
        contexts.get().current().describe(description, body);
    }

    /**
     * Defines a new ignored "describe" block.
     *
     * @param description textual description of the new block
     * @param body code that defines inner blocks, like "describe", "it", etc - this code is executed
     *             immediately
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context has been
     * defined already
     * @throws CIModeEnabledException if the system property <code>j8spec.ci.mode</code> is <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void xdescribe(String description, SafeBlock body) {
        notAllowedWhenCIModeEnabled("xdescribe");
        isValidContext("xdescribe");
        contexts.get().current().xdescribe(description, body);
    }

    /**
     * Alias for {@link #xdescribe(String, SafeBlock)}.
     *
     * @param description textual description of the new block
     * @param body code that defines inner blocks, like "describe", "it", etc - this code is executed
     *             immediately
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context has been
     * defined already
     * @throws CIModeEnabledException if the system property <code>j8spec.ci.mode</code> is <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void xcontext(String description, SafeBlock body) {
        notAllowedWhenCIModeEnabled("xcontext");
        isValidContext("xcontext");
        contexts.get().current().xdescribe(description, body);
    }

    /**
     * Defines a new focused "describe" block.
     *
     * @param description textual description of the new block
     * @param body code that defines inner blocks, like "describe", "it", etc - this code is executed
     *             immediately
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context has been
     * defined already
     * @throws CIModeEnabledException if the system property <code>j8spec.ci.mode</code> is <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void fdescribe(String description, SafeBlock body) {
        notAllowedWhenCIModeEnabled("fdescribe");
        isValidContext("fdescribe");
        contexts.get().current().fdescribe(description, body);
    }

    /**
     * Alias for {@link #fdescribe(String, SafeBlock)}.
     *
     * @param description textual description of the new block
     * @param body code that defines inner blocks, like "describe", "it", etc - this code is executed
     *             immediately
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context has been
     * defined already
     * @throws CIModeEnabledException if the system property <code>j8spec.ci.mode</code> is <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void fcontext(String description, SafeBlock body) {
        notAllowedWhenCIModeEnabled("fcontext");
        isValidContext("fcontext");
        contexts.get().current().fdescribe(description, body);
    }

    /**
     * Defines a new "before all" block.
     *
     * @param body code to be executed before all "it" blocks
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @since 2.0.0
     */
    public static synchronized void beforeAll(UnsafeBlock body) {
        isValidContext("beforeAll");
        contexts.get().current().beforeAll(body);
    }

    /**
     * Defines a new "before each" block.
     *
     * @param body code to be executed before each "it" block
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @since 1.0.0
     */
    public static synchronized void beforeEach(UnsafeBlock body) {
        isValidContext("beforeEach");
        contexts.get().current().beforeEach(body);
    }

    /**
     * Defines a new "it" block.
     *
     * @param description textual description of the new block
     * @param body code to be executed
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context has been
     * defined already
     * @since 1.0.0
     */
    public static synchronized void it(String description, UnsafeBlock body) {
        it(description, identity(), body);
    }

    /**
     * Defines a new "it" block using custom configuration.
     *
     * @param description textual description of the new block
     * @param collector block configuration collector
     * @param body code to be executed
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context has been
     * defined already
     * @since 2.0.0
     */
    public static synchronized void it(
        String description,
        Function<ItBlockConfiguration, ItBlockConfiguration> collector,
        UnsafeBlock body
    ) {
        isValidContext("it");
        ItBlockDefinition itBlockDefinition = collector.apply(newItBlockConfiguration())
            .body(body)
            .newItBlockDefinition();
        contexts.get().current().it(description, itBlockDefinition);
    }

    /**
     * Defines a new ignored "it" block.
     *
     * @param description textual description of the new block
     * @param body code to be executed
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context has been
     * defined already
     * @throws CIModeEnabledException if the system property <code>j8spec.ci.mode</code> is <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void xit(String description, UnsafeBlock body) {
        xit(description, identity(), body);
    }

    /**
     * Defines a new ignored "it" block using custom configuration.
     *
     * @param description textual description of the new block
     * @param collector block configuration collector
     * @param body code to be executed
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context has been
     * defined already
     * @throws CIModeEnabledException if the system property <code>j8spec.ci.mode</code> is <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void xit(
        String description,
        Function<ItBlockConfiguration, ItBlockConfiguration> collector,
        UnsafeBlock body
    ) {
        notAllowedWhenCIModeEnabled("xit");
        isValidContext("xit");
        ItBlockDefinition itBlockDefinition = collector.apply(newItBlockConfiguration())
            .body(body)
            .newIgnoredItBlockDefinition();
        contexts.get().current().it(description, itBlockDefinition);
    }

    /**
     * Defines a new focused "it" block.
     *
     * @param description textual description of the new block
     * @param body code to be executed
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context has been
     * defined already
     * @throws CIModeEnabledException if the system property <code>j8spec.ci.mode</code> is <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void fit(String description, UnsafeBlock body) {
        fit(description, identity(), body);
    }

    /**
     * Defines a new focused "it" block using custom configuration.
     *
     * @param description textual description of the new block
     * @param collector block configuration collector
     * @param body code to be executed
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context has been
     * defined already
     * @throws CIModeEnabledException if the system property <code>j8spec.ci.mode</code> is <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void fit(
        String description,
        Function<ItBlockConfiguration, ItBlockConfiguration> collector,
        UnsafeBlock body
    ) {
        notAllowedWhenCIModeEnabled("fit");
        isValidContext("fit");
        ItBlockDefinition itBlockDefinition = collector.apply(newItBlockConfiguration())
            .body(body)
            .newFocusedItBlockDefinition();
        contexts.get().current().it(description, itBlockDefinition);
    }

    private static void notAllowedWhenCIModeEnabled(final String methodName) {
        if (Boolean.valueOf(System.getProperty("j8spec.ci.mode", "false"))) {
            throw new CIModeEnabledException("'" + methodName + "' not allowed when j8spec.ci.mode enabled");
        }
    }

    private static void isValidContext(final String methodName) {
        if (contexts.get() == null) {
            throw new IllegalContextException(
                "'" + methodName + "' should not be invoked from outside a spec definition."
            );
        }
    }

    /**
     * Uses the given spec class to build and populate a {@link DescribeBlock} object.
     *
     * @param specClass class with a public default constructor that contains the spec definition
     * @return {@link DescribeBlock} object that represents the spec definition
     * @throws SpecInitializationException if it is not possible to create an instance of <code>specClass</code>
     * @since 2.0.0
     */
    public static synchronized DescribeBlock read(Class<?> specClass) {
        contexts.set(new Context<>());
        try {
            return newDescribeBlockDefinition(specClass, contexts.get()).toDescribeBlock();
        } finally {
            contexts.set(null);
        }
    }

    private J8Spec() {}
}
