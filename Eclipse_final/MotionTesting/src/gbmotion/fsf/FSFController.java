package gbmotion.fsf;

/**
 *
 * This is the FSF controller
 */

/* ------------------------------------------------------------------

Implementation Instructions:
    This class is the full state feedback controller itself.
    This is the result of the calculations, namely finding K, which are done in FSFHelper

------------------------------------------------------------------ */

//TODO: complete this!

public class FSFController { /*extends IterativeController implements LTIController {
    private Basic2DMatrix m_A, m_B, m_C, m_D,m_K;

    public FSFController(Basic2DMatrix a, Basic2DMatrix b, Basic2DMatrix c, Basic2DMatrix d, Input in, Output out) {
        super(in, out);
        m_A = a;
        m_B = b;
        m_C = c;
        m_D = d;
    }

    public void setPole(int pole){
        //m_K = FSFHelper.calculateK(m_A,m_B,pole);
    }

    @Override
    public void calculate(){
        //double[][] states = {super.m_input.recieve()};
        //Basic2DMatrix m_X = new Basic2DMatrix(states);
        //m_X = (Basic2DMatrix) m_X.multiply(m_K);
        //super.m_output.use(m_X.toArray()[1]);
    }

    @Override
    public void initParameters() throws NoSuchFieldException {

    }


    public Matrix[] toLTI() { return new Matrix[] { m_A, m_B, m_C, m_D }; }*/
}
