package fsf;

import base.Input;
import base.IterativeController;
import base.LTIController;
import base.Output;
import org.la4j.Matrix;

/**
 *
 * The internal controller in the default implementation of Full State Feedback
 */


/* ------------------------------------------------------------------

Implementation Instructions:
    This class is the controller which is presented in the following figures:
    1) https://en.wikipedia.org/wiki/Full_state_feedback#/media/File:Feedback-system.jpg
    2) Fig.3 in http://www.wseas.us/e-library/conferences/2007egypt/papers/601-265.pdf
    the implementation of this class should be done by the same group which creates IterativeController.
    note - this is an *open loop* controller. that's why there's no input!

------------------------------------------------------------------ */

//TODO: complete this!

public class FSFInternalDefaultController { /* extends IterativeController implements LTIController {
    private Matrix m_A, m_B, m_C, m_D;

    public FSFInternalDefaultController(Matrix a, Matrix b, Matrix c, Matrix d, Input in, Output out) {
        super(in, out);
        m_A = a;
        m_B = b;
        m_C = c;
        m_D = d;
    }

    public Matrix[] toLTI() { return new Matrix[] { m_A, m_B, m_C, m_D }; }

    @Override
    public void calculate() {

    }

    @Override
    public void initParameters() throws NoSuchFieldException {

    }
*/
}
