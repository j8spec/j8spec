package j8spec;

import org.junit.Test;

import static j8spec.Var.var;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class VarTest {

    @Test
    public void storesValueInVariable() {
        final Var<String> s = var();

        var(s, "value");

        assertThat(var(s), is("value"));
    }
}
