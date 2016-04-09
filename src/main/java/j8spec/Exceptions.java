package j8spec;

/**
 * J8Spec exceptions.
 */
public final class Exceptions {

    private Exceptions() {}

    /**
     * J8Spec base exception.
     * @since 3.0.0
     */
    public static class Base extends RuntimeException {
        Base(String message) { super(message); }

        Base(String message, Throwable cause) { super(message, cause); }

        Base(String message, Throwable cause, boolean suppression) { super(message, cause, suppression, true); }
    }

    /**
     * Thrown when an example or example group with the same description has already been defined in the same context.
     * @since 3.0.0
     */
    public static class BlockAlreadyDefined extends Base {
        BlockAlreadyDefined(String blockDescription) {
            super("'" + blockDescription + "' block already defined.");
        }
    }

    /**
     * Thrown when a variable initializer has already been defined in the same context for the same variable.
     * @since 3.1.0
     */
    public static class VariableInitializerAlreadyDefined extends Base {
        VariableInitializerAlreadyDefined() {
            super("Variable initializer already defined.");
        }
    }

    /**
     * Thrown when a method that ignores or focuses an example or example group
     * (like {@link j8spec.J8Spec#xit(String, UnsafeBlock) xit} or {@link j8spec.J8Spec#fit(String, UnsafeBlock) fit})
     * is used while the system property <code>j8spec.ci.mode</code> is <code>true</code>.
     * @since 3.0.0
     */
    public static class OperationNotAllowedInCIMode extends Base {
        OperationNotAllowedInCIMode(String methodName) {
            super("'" + methodName + "' not allowed when j8spec.ci.mode enabled.");
        }
    }

    /**
     * Thrown when a spec has the seed for the random order hard-coded while the system
     * property <code>j8spec.ci.mode</code> is <code>true</code>.
     * @since 3.0.0
     */
    public static class HardCodedSeedNotAllowedInCIMode extends Base {
        HardCodedSeedNotAllowedInCIMode() {
            super("Hard-coded seed not allowed when j8spec.ci.mode enabled.");
        }
    }

    /**
     * Thrown when a block definition method is called outside the context of
     * the {@link j8spec.J8Spec#read(Class)} method.
     * @since 3.0.0
     */
    public static class IllegalContext extends Base {
        IllegalContext(String methodName) {
            super("'" + methodName + "' should not be invoked from outside a spec definition.");
        }
    }

    /**
     * Thrown when an instance of the class that contains the spec definition cannot be created.
     * @since 3.0.0
     */
    public static class SpecInitializationFailed extends Base {
        SpecInitializationFailed(Class<?> specClass, Exception e) {
            super("Failed to create instance of " + specClass + ".", e);
        }
    }

    /**
     * Thrown when the <code>j8spec.seed</code> property has an invalid value.
     * @since 3.0.0
     */
    public static class IllegalSeedProperty extends Base {
        IllegalSeedProperty(NumberFormatException e) {
            super("Illegal 'j8spec.seed' property value.", e);
        }
    }

    /**
     * Thrown when an example has multiple failures.
     * @since 3.1.0
     */
    public static class MultipleFailures extends Base {
        MultipleFailures() {
            super("Multiple failures.", null, true);
        }
    }

    static class Collector {
        @SuppressWarnings("ThrowableInstanceNeverThrown")
        private final Throwable throwable = new MultipleFailures();

        void execute(UnsafeBlock unsafeBlock) {
            try {
                unsafeBlock.tryToExecute();
            } catch (Throwable cause) {
                throwable.addSuppressed(cause);
            }
        }

        void haltOnFailure() throws Throwable {
            if (isEmpty()) {
                return;
            }

            if (throwable.getSuppressed().length == 1) {
                throw throwable.getSuppressed()[0];
            }

            throw throwable;
        }

        void executeOrSkip(UnsafeBlock unsafeBlocks) {
            if (isEmpty()) {
                execute(unsafeBlocks);
            }
        }

        boolean isEmpty() {
            return throwable.getSuppressed().length == 0;
        }
    }
}
