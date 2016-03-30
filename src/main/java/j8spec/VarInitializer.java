package j8spec;

class VarInitializer<T> implements UnsafeBlock {

    private final Var<T> var;
    private final UnsafeFunction<T> function;

    VarInitializer(Var<T> var, UnsafeFunction<T> function) {
        this.var = var;
        this.function = function;
    }

    @Override
    public void tryToExecute() throws Throwable {
        Var.var(var, function.tryToGet());
    }
}
