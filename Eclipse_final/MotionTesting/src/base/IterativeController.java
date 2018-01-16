package base;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by karlo on 14/12/2017.
 * Represents a controller which has the basic structure of a loop which calls it's input and output
 */

public abstract class IterativeController<IN, OUT> extends Controller<IN, OUT> {
    public static final double DEFAULT_PERIOD = .05;

    protected final double m_period;

    protected Timer m_controllerLoop;                         // the loop which will calculate the controller

    public IterativeController(Input<IN> in, Output<OUT> out, IN destination, double period) {
        super(in, out, destination);
        m_period = period;

        m_controllerLoop = new Timer();
        m_controllerLoop.schedule(new IterativeCalculationTask(this), 0L, (long) (1000*period));
    }

    public IterativeController(Input<IN> in, Output<OUT> out, ITolerance tolerance, IN destination) {
        this(in, out, destination, DEFAULT_PERIOD);
    }

    public IterativeController(Input<IN> in, Output<OUT> out, IN destination) {
        this(in, out, NO_TOLERANCE, destination);
    }

    @SuppressWarnings("unchecked")
    public IterativeController(Output<OUT> out, IN destination) {
        this(NO_INPUT, out, destination);
    }

    public IterativeController(Input<IN> in, Output<OUT> out, double period){
        super(in, out);
        m_period = period;
    }

    public IterativeController(Input<IN> in, Output<OUT> out) {
        super(in, out);
        m_period = DEFAULT_PERIOD;
    }

    @SuppressWarnings("unchecked")
    public IterativeController(Output<OUT> out) {
        this(NO_INPUT, out);
    }

    protected static class IterativeCalculationTask extends TimerTask {
        protected IterativeController m_controller;

        public IterativeCalculationTask(IterativeController controller) {
            if (controller == null) throw new NullPointerException("null controller was given");
            m_controller = controller;
        }

        @Override
        public void run() { m_controller.calculate(); }
    }

    public void free() {
        m_controllerLoop.cancel();
        super.free();
        synchronized (LOCK) {
            m_controllerLoop = null;
        }
    }
}
