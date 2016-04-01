package j8spec;

import org.junit.Test;

import static j8spec.Var.var;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VarInitializerTest {

    @Test
    public void executes_init_function_only_once() throws Throwable {
        UnsafeFunction<String> initFunction = mock(UnsafeFunction.class);
        when(initFunction.tryToGet()).thenReturn("value");

        VarInitializer<String> varInit = new VarInitializer<>(var(), initFunction);

        varInit.tryToExecute();
        varInit.tryToExecute();

        verify(initFunction, times(1)).tryToGet();
    }
}