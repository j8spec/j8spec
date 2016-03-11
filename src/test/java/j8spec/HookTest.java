package j8spec;

import org.junit.Test;

import static j8spec.Hook.newHook;
import static j8spec.Hook.newOneTimeHook;
import static org.mockito.Mockito.*;

public class HookTest {

    @Test
    public void runs_given_block_only_once() throws Throwable {
        UnsafeBlock block = mock(UnsafeBlock.class);
        Hook beforeHook = newOneTimeHook(block);

        beforeHook.tryToExecute();
        beforeHook.tryToExecute();

        verify(block, times(1)).tryToExecute();
    }

    @Test
    public void runs_given_block_on_each_call() throws Throwable {
        UnsafeBlock block = mock(UnsafeBlock.class);
        Hook beforeHook = newHook(block);

        beforeHook.tryToExecute();
        beforeHook.tryToExecute();

        verify(block, times(2)).tryToExecute();
    }
}
