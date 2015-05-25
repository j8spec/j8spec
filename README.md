j8spec [![Build Status](https://travis-ci.org/tprado/j8spec.svg?branch=master)](https://travis-ci.org/tprado/j8spec)
======

RSpec/Jasmine like specs with Java Lambda Expressions.


### Sample Spec:

```java
@RunWith(J8SpecRunner.class)
public class MyObjectTest {{


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

}}

```
