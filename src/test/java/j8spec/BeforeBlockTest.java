package j8spec;

import org.junit.Test;

import static org.mockito.Mockito.*;

public class BeforeBlockTest {

    @Test
    public void runs_given_body_only_once() throws Throwable {
        UnsafeBlock body = mock(UnsafeBlock.class);
        BeforeBlock beforeBlock = BeforeBlock.newBeforeAllBlock(body);

        beforeBlock.tryToExecute();
        beforeBlock.tryToExecute();

        verify(body, times(1)).tryToExecute();
    }

    @Test
    public void runs_given_body_on_each_call() throws Throwable {
        UnsafeBlock body = mock(UnsafeBlock.class);
        BeforeBlock beforeBlock = BeforeBlock.newBeforeEachBlock(body);

        beforeBlock.tryToExecute();
        beforeBlock.tryToExecute();

        verify(body, times(2)).tryToExecute();
    }
}
