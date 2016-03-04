package j8spec;

import org.junit.Test;

import static j8spec.UnsafeBlock.NOOP;

public class DuplicatedBlockValidatorTest {

    private final DuplicatedBlockValidator validator = new DuplicatedBlockValidator();

    @Test(expected = BlockAlreadyDefinedException.class)
    public void indicates_if_an_example_has_been_defined_with_the_same_description() {
        validator
            .startGroup(groupConfig().description("spec").build())
                .example(exampleConfig().description("example 1").build(), NOOP)
                .example(exampleConfig().description("example 1").build(), NOOP)
            .endGroup();
    }

    @Test(expected = BlockAlreadyDefinedException.class)
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

    private ExampleConfiguration.Builder exampleConfig() {
        return new ExampleConfiguration.Builder();
    }

    private ExampleGroupConfiguration.Builder groupConfig() {
        return new ExampleGroupConfiguration.Builder();
    }
}