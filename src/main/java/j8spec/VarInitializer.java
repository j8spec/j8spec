package j8spec;

import static j8spec.J8Spec.var;
import static java.util.Objects.isNull;

class VarInitializer<T> implements UnsafeBlock {

    private final Var<T> variable;
    private final UnsafeFunction<T> function;
    private T value;

    VarInitializer(Var<T> variable, UnsafeFunction<T> function) {
        this.variable = variable;
        this.function = function;
    }

    @Override
    public void tryToExecute() throws Throwable {
        if (isNull(value)) {
            value = function.tryToGet();
        }
        var(variable, value);
    }
}
