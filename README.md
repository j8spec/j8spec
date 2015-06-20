J8Spec [![Build Status](https://travis-ci.org/j8spec/j8spec.svg?branch=master)](https://travis-ci.org/j8spec/j8spec)
======

[RSpec](http://rspec.info/) / [Jasmine](http://jasmine.github.io/) like specs with Java 8 Lambda Expressions.

### Sample Spec:

[Sample Gradle Project](https://github.com/j8spec/j8spec-sample-gradle) for reference.

```java
package j8spec;

import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;

import java.math.BigInteger;

import static j8spec.J8Spec.*;
import static j8spec.Var.var;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(J8SpecRunner.class)
public class NumberTest {{

    final Var<Number> number = var();

    beforeEach(() -> {
        var(number, new Number(10));
    });

    describe("conversions", () -> {
        it("converts to int", () -> {
            assertThat(var(number).toInt(), is(10));
        });

        it("converts to big integer", () -> {
            assertThat(var(number).toBigInt(), is(new BigInteger("10")));
        });
    });

    describe("arithmetic operations", () -> {
        final Var<Number> value = var();

        beforeEach(() -> {
            var(value, new Number(5));
        });

        it("adds to a number", () -> {
            assertThat(var(number).plus(var(value)).toInt(), is(15));
        });

        it("subtracts from a number", () -> {
            assertThat(var(number).subtract(var(value)).toInt(), is(5));
        });

        it("multiples by a number", () -> {
            assertThat(var(number).times(var(value)).toInt(), is(50));
        });

        describe("division", () -> {
            it("divides by a number", () -> {
                assertThat(var(number).dividesBy(var(value)).toInt(), is(2));
            });

            it("does not divide by zero", () -> {
                assertThat(var(number).dividesBy(new Number(0)).toInt(), is(2));
            });
        });
    });
}}

```

### How does it look?

#### Intellij

![Intellij](https://github.com/j8spec/j8spec-sample-gradle/blob/master/screenshots/intellij.png)

#### Gradle Report

![Gradle Report](https://github.com/j8spec/j8spec-sample-gradle/blob/master/screenshots/gradle-details.png)

### Release 1.1.0 (Next Release)

- `beforeAll` (class level setup)
- `xdescribe` / `xit` (ignore)
- `fdescribe` / `fit` (focus)
- expected exception

### Release 1.0.0

- `describe`, `beforeEach`, and `it` constructions

### Someday

- `afterAll` (class level teardown)
- `afterEach`
- timeout
