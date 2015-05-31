package j8spec.junit;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class RunListenerHelper extends RunListener {

    private Description description;
    private Throwable expection;

    @Override
    public void testFailure(Failure failure) throws Exception {
        super.testFailure(failure);

        description = failure.getDescription();
        expection = failure.getException();
    }


    public Description getDescription() {
        return description;
    }

    public Throwable getExpection() {
        return expection;
    }
}
