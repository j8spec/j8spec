package j8spec;

import org.junit.Test;

import static j8spec.Hook.newHook;
import static j8spec.Hook.newOneTimeHook;
import static org.mockito.Mockito.*;

public class HookTest {

    @Test
    public void runs_given_block_only_once() throws Throwable {
        UnsafeBlock block = mock(UnsafeBlock.class);
        Hook hook = newOneTimeHook(block);

        hook.tryToExecute();
        hook.tryToExecute();

        verify(block, times(1)).tryToExecute();
    }

    @Test
    public void runs_given_block_on_each_call() throws Throwable {
        UnsafeBlock block = mock(UnsafeBlock.class);
        Hook hook = newHook(block);

        hook.tryToExecute();
        hook.tryToExecute();

        verify(block, times(2)).tryToExecute();
    }

    @Test(expected = Exception.class)
    public void tries_to_run_one_time_hook_again_if_it_fails_the_first_time() throws Throwable {
        UnsafeBlock block = mock(UnsafeBlock.class);
        Hook hook = newOneTimeHook(block);

        doThrow(Exception.class).when(block).tryToExecute();

        try { hook.tryToExecute(); } catch(Exception e) {}

        hook.tryToExecute();
    }
}
