package j8spec;

abstract class BlockDefinitionVisitor {

    BlockDefinitionVisitor startGroup(ExampleGroupConfiguration config) {
        return this;
    }

    BlockDefinitionVisitor beforeAll(UnsafeBlock block) {
        return this;
    }

    BlockDefinitionVisitor beforeEach(UnsafeBlock block) {
        return this;
    }

    BlockDefinitionVisitor example(ExampleConfiguration config, UnsafeBlock block) {
        return this;
    }

    BlockDefinitionVisitor afterEach(UnsafeBlock block) {
        return this;
    }

    BlockDefinitionVisitor afterAll(UnsafeBlock block) {
        return this;
    }

    BlockDefinitionVisitor endGroup() {
        return this;
    }
}
