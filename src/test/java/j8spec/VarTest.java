package j8spec;

import org.junit.Test;

import static j8spec.J8Spec.var;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class VarTest {

    @Test
    public void stores_value_in_variable() {
        final Var<String> s = var();

        var(s, "value");

        assertThat(var(s), is("value"));
    }
}
