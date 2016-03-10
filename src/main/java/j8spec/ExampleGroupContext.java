package j8spec;

import java.util.Stack;

final class ExampleGroupContext {

    private final Stack<ExampleGroupDefinition> stack = new Stack<>();

    void switchTo(ExampleGroupDefinition current) {
        stack.push(current);
    }

    void restore() {
        stack.pop();
    }

    ExampleGroupDefinition current() {
        return stack.peek();
    }
}
