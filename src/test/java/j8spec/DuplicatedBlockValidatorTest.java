package j8spec;

import org.junit.Test;

import static j8spec.BlockExecutionFlag.DEFAULT;
import static j8spec.UnsafeBlock.NOOP;

public class DuplicatedBlockValidatorTest {

    private final DuplicatedBlockValidator validator = new DuplicatedBlockValidator();

    @Test(expected = BlockAlreadyDefinedException.class)
    public void indicates_if_an_example_has_been_defined_with_the_same_description() {
        validator
            .describe("spec", DEFAULT)
                .it("example 1", NOOP, DEFAULT, null)
                .it("example 1", NOOP, DEFAULT, null)
            .describe();
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void indicates_if_a_example_group_has_been_defined_with_the_same_description() {
        validator
            .describe("spec", DEFAULT)
                .describe("group 1", DEFAULT)
                    .it("example 1", NOOP, DEFAULT, null)
                .describe()
                .describe("group 1", DEFAULT)
                    .it("example 1", NOOP, DEFAULT, null)
                .describe()
            .describe();
    }

    @Test()
    public void accepts_examples_with_same_description_in_different_groups() {
        validator
            .describe("spec", DEFAULT)
                .describe("group 1", DEFAULT)
                    .it("example 1", NOOP, DEFAULT, null)
                .describe()
                .describe("group 2", DEFAULT)
                    .it("example 1", NOOP, DEFAULT, null)
                .describe()
            .describe();
    }
}