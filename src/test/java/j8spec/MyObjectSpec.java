package j8spec;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static j8spec.Spec.*;
import static j8spec.Var.*;

public class MyObjectSpec {{

    describe(MyObject.class, () -> {
        final Var<MyObject> myObject = var();

        beforeEach(() -> {
            var(myObject, new MyObject());
        });

        it("has a value property", () -> {
            var(myObject).setValue(5);

            assertThat(var(myObject).getMyValue(), is(5));
        });

        it("adds an integer to its value", () -> {
            assertThat(var(myObject).addToMyValue(4), is(4));
        });

        describe("when initializing value via constructor", () -> {
            beforeEach(() -> {
                var(myObject).setValue(10);
            });

            it("adds given value to current value", () -> {
                assertThat(var(myObject).addToMyValue(4), is(14));
            });
        });
    });

}}
