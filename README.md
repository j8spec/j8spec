j8spec [![Build Status](https://travis-ci.org/tprado/j8spec.svg?branch=master)](https://travis-ci.org/tprado/j8spec)
======

RSpec/Jasmine like specs with Java Lambda Expressions.


### Sample Spec:

```java
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

```

### Output:

```
  j8spec.MyObject
    when initializing value via constructor
      adds given value to current value [OK]
    has a value property [OK]
    adds an integer to its value [OK]
```
