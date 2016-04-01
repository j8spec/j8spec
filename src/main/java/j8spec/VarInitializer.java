package j8spec;

import static java.util.Objects.isNull;

class VarInitializer<T> implements UnsafeBlock {

    private final Var<T> var;
    private final UnsafeFunction<T> function;
    private T value;

    VarInitializer(Var<T> var, UnsafeFunction<T> function) {
        this.var = var;
        this.function = function;
    }

    @Override
    public void tryToExecute() throws Throwable {
        if (isNull(value)) {
            value = function.tryToGet();
        }
        Var.var(var, value);
    }
}
