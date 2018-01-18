package base;


import org.la4j.Matrix;


/**
 * A controller which can be represented as an LTI
 * <p>
 * LTI representation:
 *    _1(t)  = Ax(t) + Bu(t)
 *    _0'(t) = Cx(t) + Du(t)
 * when _0 is the input function, _1 is the output function and u is an internal function in the controller's theory
 *
 * here is graph:
 *
 *         * -> -> -> ->  D  -> -> -> -> *
 *         ^         _0`(t)   _0(t)        V
 * u(t) -> * -> B -> * -> G -> * -> C -> * -> _1(t)
 *                   ^         V
 *                   * <- A <- *
 * </p>
 */
public interface LTIController extends IController {
    /**
     * @return The LTI representation of the controller
     */
    Matrix[] toLTI();
}

