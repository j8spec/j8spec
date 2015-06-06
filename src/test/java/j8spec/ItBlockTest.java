package j8spec;

import org.junit.Test;

import java.util.Collections;

import static j8spec.ItBlock.newItBlock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ItBlockTest {

    @Test
    public void runsGivenBody() {
        Runnable body = mock(Runnable.class);

        newItBlock(
            Collections.<String>emptyList(),
            "it block",
            Collections.<Runnable>emptyList(),
            body
        ).run();

        verify(body).run();
    }
}
