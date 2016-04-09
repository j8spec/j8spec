package j8spec;

import org.junit.Test;

import static j8spec.J8Spec.*;
import static org.mockito.Mockito.*;

public class VarInitializerTest {

    @SuppressWarnings("unchecked")
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