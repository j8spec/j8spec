package j8spec;

import java.util.Stack;

final class Context<T> {

    private final Stack<T> stack = new Stack<>();

    void switchTo(T current) {
        stack.push(current);
    }

    void restore() {
        stack.pop();
    }

    T current() {
        return stack.peek();
    }
}
