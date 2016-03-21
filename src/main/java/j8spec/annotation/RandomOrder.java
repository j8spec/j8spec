package j8spec.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Examples in a spec annotated with this will be executed in the same random order using the provided seed.
 *
 * <p>
 * <b>Note:</b> Use this annotation only to debug a spec that is failing when executed with a particular seed.
 * </p>
 *
 * @since 3.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface RandomOrder {

    /**
     * @return seed to be used when ordering the examples in the spec
     */
    long seed();
}
