package j8spec;

final class BlockDefinitions {

    private BlockDefinitions() {}

    static abstract class Base implements BlockDefinition {
        private final UnsafeBlock block;

        Base(UnsafeBlock block) { this.block = block; }

        UnsafeBlock block() { return block; }
    }

    static final class BeforeAll extends Base {
        BeforeAll(UnsafeBlock block) { super(block); }

        @Override
        public void accept(BlockDefinitionVisitor visitor) {
            visitor.beforeAll(block());
        }
    }

    static final class BeforeEach extends Base {
        BeforeEach(UnsafeBlock block) { super(block); }

        @Override
        public void accept(BlockDefinitionVisitor visitor) {
            visitor.beforeEach(block());
        }
    }

    static final class Example extends Base {
        private final ExampleConfiguration config;

        Example(ExampleConfiguration config, UnsafeBlock block) {
            super(block);
            this.config = config;
        }

        String description() {
            return config.description();
        }

        @Override
        public void accept(BlockDefinitionVisitor visitor) {
            visitor.example(config, block());
        }
    }

    static final class AfterEach extends Base {
        AfterEach(UnsafeBlock block) { super(block); }

        @Override
        public void accept(BlockDefinitionVisitor visitor) {
            visitor.afterEach(block());
        }
    }

    static final class AfterAll extends Base {
        AfterAll(UnsafeBlock block) { super(block); }

        @Override
        public void accept(BlockDefinitionVisitor visitor) {
            visitor.afterAll(block());
        }
    }

    static final class VarInitializer<T> implements BlockDefinition {
        private final Var<T> var;
        private final UnsafeFunction<T> initFunction;

        VarInitializer(Var<T> var, UnsafeFunction<T> initFunction) {
            this.var = var;
            this.initFunction = initFunction;
        }

        @Override
        public void accept(BlockDefinitionVisitor visitor) {
            visitor.varInitializer(var, initFunction);
        }
    }
}
