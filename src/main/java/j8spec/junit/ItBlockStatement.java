package j8spec.junit;

import j8spec.ItBlock;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.runners.model.Statement;

final class ItBlockStatement extends Statement {

    private final ItBlock itBlock;

    public static Statement newStatement(ItBlock itBlock) {
        Statement statement = new ItBlockStatement(itBlock);

        if (itBlock.isExpectedToThrowAnException()) {
            statement = new ExpectException(statement, itBlock.expected());
        }

        return statement;
    }

    private ItBlockStatement(ItBlock itBlock) {
        this.itBlock = itBlock;
    }

    @Override
    public void evaluate() throws Throwable {
        itBlock.run();
    }
}
