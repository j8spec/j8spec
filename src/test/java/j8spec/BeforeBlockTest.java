package j8spec;

import org.junit.Test;

import static org.mockito.Mockito.*;

public class BeforeBlockTest {

    @Test
    public void runsGivenBodyOnlyOnce() {
        Runnable body = mock(Runnable.class);
        BeforeBlock beforeBlock = BeforeBlock.newBeforeAllBlock(body);

        beforeBlock.run();
        beforeBlock.run();

        verify(body, times(1)).run();
    }

    @Test
    public void runsGivenBodyOnEachCall() {
        Runnable body = mock(Runnable.class);
        BeforeBlock beforeBlock = BeforeBlock.newBeforeEachBlock(body);

        beforeBlock.run();
        beforeBlock.run();

        verify(body, times(2)).run();
    }
}
