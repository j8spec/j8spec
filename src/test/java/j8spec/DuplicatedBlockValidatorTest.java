package j8spec;

import org.junit.Test;

import static j8spec.BlockExecutionFlag.DEFAULT;
import static j8spec.UnsafeBlock.NOOP;

public class DuplicatedBlockValidatorTest {

    private final DuplicatedBlockValidator validator = new DuplicatedBlockValidator();

    @Test(expected = BlockAlreadyDefinedException.class)
    public void indicates_if_an_example_has_been_defined_with_the_same_description() {
        validator
            .startGroup("spec", DEFAULT)
                .example("example 1", NOOP, DEFAULT, null)
                .example("example 1", NOOP, DEFAULT, null)
            .endGroup();
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void indicates_if_a_example_group_has_been_defined_with_the_same_description() {
        validator
            .startGroup("spec", DEFAULT)
                .startGroup("group 1", DEFAULT)
                    .example("example 1", NOOP, DEFAULT, null)
                .endGroup()
                .startGroup("group 1", DEFAULT)
                    .example("example 1", NOOP, DEFAULT, null)
                .endGroup()
            .endGroup();
    }

    @Test()
    public void accepts_examples_with_same_description_in_different_groups() {
        validator
            .startGroup("spec", DEFAULT)
                .startGroup("group 1", DEFAULT)
                    .example("example 1", NOOP, DEFAULT, null)
                .endGroup()
                .startGroup("group 2", DEFAULT)
                    .example("example 1", NOOP, DEFAULT, null)
                .endGroup()
            .endGroup();
    }
}