package j8spec;

import org.junit.Test;

import static j8spec.UnsafeBlock.NOOP;
import static j8spec.Var.var;

public class DuplicatedBlockValidatorTest {

    private final DuplicatedBlockValidator validator = new DuplicatedBlockValidator();

    @Test(expected = Exceptions.BlockAlreadyDefined.class)
    public void indicates_if_an_example_has_been_defined_with_the_same_description() {
        validator
            .startGroup(groupConfig().description("spec").build())
                .example(exampleConfig().description("example 1").build(), NOOP)
                .example(exampleConfig().description("example 1").build(), NOOP)
            .endGroup();
    }

    @Test(expected = Exceptions.BlockAlreadyDefined.class)
    public void indicates_if_a_example_group_has_been_defined_with_the_same_description() {
        validator
            .startGroup(groupConfig().description("spec").build())
                .startGroup(groupConfig().description("group 1").build())
                    .example(exampleConfig().description("example 1").build(), NOOP)
                .endGroup()
                .startGroup(groupConfig().description("group 1").build())
                    .example(exampleConfig().description("example 1").build(), NOOP)
                .endGroup()
            .endGroup();
    }

    @Test()
    public void accepts_examples_with_same_description_in_different_groups() {
        validator
            .startGroup(groupConfig().description("spec").build())
                .startGroup(groupConfig().description("group 1").build())
                    .example(exampleConfig().description("example 1").build(), NOOP)
                .endGroup()
                .startGroup(groupConfig().description("group 2").build())
                    .example(exampleConfig().description("example 1").build(), NOOP)
                .endGroup()
            .endGroup();
    }

    @Test(expected = Exceptions.VariableInitializerAlreadyDefined.class)
    public void indicates_if_an_initializer_has_been_defined_for_the_same_variable() {
        Var<String> v1 = var();

        validator
            .startGroup(groupConfig().description("spec").build())
                .varInitializer(v1, () -> "value 1")
                .varInitializer(v1, () -> "value 2")
                .example(exampleConfig().description("example 1").build(), NOOP)
            .endGroup();
    }

    private ExampleConfiguration.Builder exampleConfig() {
        return new ExampleConfiguration.Builder();
    }

    private ExampleGroupConfiguration.Builder groupConfig() {
        return new ExampleGroupConfiguration.Builder();
    }
}