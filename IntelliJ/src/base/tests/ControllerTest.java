package base.tests;

import APPC.APPController;
import base.Controller;
import base.Input;
import base.Output;
import edu.wpi.first.wpilibj.tables.ITableListener;

public class ControllerTest implements Input, Output {

    public ControllerTest() {
    }

    public static void main(String[] args) {
        ControllerTest test = new ControllerTest();
        for (int i = 0; i < 100; i++)
            test.test();
    }

    public void test() {
        int newDest = (int)(Math.random() * 10001);
        PseudoController controller = new PseudoController(this, this, 100);
        controller.getListener().valueChanged(
                controller.getTable(),
                "Destination",
                newDest,
                false
        );
        if ((int)controller.getDestination() == newDest){
            System.out.println("Success! :)");
        }
        else{
            System.out.println("Bamboozled again");
        }

    }

    @Override
    public double[] recieve() { return new double[0]; }

    @Override
    public void use(double[] output) { }

    @Override
    public String toString(){
        return "Dank maymays";
    }

    public class PseudoController extends Controller {

        public PseudoController(Input in, Output out, ITolerance tolerance, double destination) {
            super(in, out, tolerance, destination);
        }

        public ITableListener getListener() { return m_listener; }

        public PseudoController(Input in, Output out, double destination) {
            super(in, out, destination);
        }

        public PseudoController(Output out, double destination) {
            super(out, destination);
        }

        public PseudoController(Input in, Output out) {
            super(in, out);
        }

        public PseudoController(Output out) {
            super(out);
        }

        public PseudoController(Input in, Output out, ITolerance tolerance) {
            super(in, out, tolerance);
        }

        @Override
        public void calculate() { }

        @Override
        public void initOptionalParameters() { }

    }

}
