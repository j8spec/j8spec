package j8spec;

import static j8spec.J8Spec.*;

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
        if (value == null) {
            value = function.tryToGet();
        }
        var(variable, value);
    }
}
