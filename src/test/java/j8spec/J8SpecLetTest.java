package j8spec;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static j8spec.J8Spec.*;
import static j8spec.Var.var;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class J8SpecLetTest {

    static class SampleSpec {{
        Var<String> v1 = var();

        let(v1, () -> "value");

        it("assigns 'value' to v1", () -> {
            log.add(String.format("var is '%s'", var(v1)));
        });
    }}

    static class VarInitializerOverwrittenSpec {{
        Var<String> v1 = var();

        let(v1, () -> "value 1");
        let(v1, () -> "value 2");
    }}

    private static List<String> log;

    @Before
    public void resetLog() throws Throwable {
        log = new ArrayList<>();
    }

    @Test
    public void assigns_value_to_variable_before_example_is_executed() throws Throwable {
        executeSpec(SampleSpec.class);

        assertThat(log, is(singletonList("var is 'value'")));
    }

    @Test(expected = Exceptions.IllegalContext.class)
    public void does_not_allow_let_method_direct_invocation() {
        Var<String> var = var();
        let(var, () -> "value");
    }

    @Test(expected = Exceptions.VariableInitializerAlreadyDefined.class)
    public void does_not_allow_var_initializer_to_be_replaced() {
        read(VarInitializerOverwrittenSpec.class);
    }

    private void executeSpec(Class<?> specClass) throws Throwable {
        for (Example example : read(specClass)) {
            example.tryToExecute();
        }
    }
}
