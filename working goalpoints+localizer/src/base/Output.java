package base;

/**
 * Created by karlo on 14/12/2017.
 * Represents controller usage
 */
@FunctionalInterface
public interface Output<T> {
    void use(T output);
    default T noPower() { return null; }
    default void stop() { use(noPower()); }
}
