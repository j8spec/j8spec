package j8spec.junit;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class RunListenerHelper extends RunListener {

    private Description description;
    private Throwable exception;

    @Override
    public void testFailure(Failure failure) throws Exception {
        super.testFailure(failure);

        description = failure.getDescription();
        exception = failure.getException();
    }


    public Description getDescription() {
        return description;
    }

    public Throwable getException() {
        return exception;
    }
}
