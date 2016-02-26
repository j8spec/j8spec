package j8spec;

import org.junit.Test;

import static org.mockito.Mockito.*;

public class BeforeBlockTest {

    @Test
    public void runs_given_block_only_once() throws Throwable {
        UnsafeBlock block = mock(UnsafeBlock.class);
        BeforeBlock beforeBlock = BeforeBlock.newBeforeAllBlock(block);

        beforeBlock.tryToExecute();
        beforeBlock.tryToExecute();

        verify(block, times(1)).tryToExecute();
    }

    @Test
    public void runs_given_block_on_each_call() throws Throwable {
        UnsafeBlock block = mock(UnsafeBlock.class);
        BeforeBlock beforeBlock = BeforeBlock.newBeforeEachBlock(block);

        beforeBlock.tryToExecute();
        beforeBlock.tryToExecute();

        verify(block, times(2)).tryToExecute();
    }
}
