package base;

/**
 * Created by karlo on 14/12/2017.
 * Represents a generalized input for controller
 */
@FunctionalInterface
public interface Input<T> {
    T recieve();
}
