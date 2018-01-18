package base;

/**
 *
 * Represents a generalized input for controller
 */
@FunctionalInterface
public interface Input<T> {
    T recieve();
}
