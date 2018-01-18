package base;

/**
 * Created by karlo on 08/01/2018.
 */
public abstract class SISOController extends IterativeController<Double, Double> {
    public SISOController(Input<Double> in, Output<Double> out, ITolerance tolerance, Double destination) {
        super(in, out, tolerance, destination);
    }

    public abstract class AbsoluteTolerance extends Tolerance{

        double m_toleranceDist;

        public AbsoluteTolerance(double toleranceDist){
            m_toleranceDist = toleranceDist;
        }

    }

    public abstract class PrecentageToerance extends Tolerance{

        double m_tolerancePrecent;

        public PrecentageToerance(double tolerancePrecent){
            m_tolerancePrecent = tolerancePrecent;
        }
    }

    public SISOController(Input<Double> in, Output<Double> out, Double destination) {
        super(in, out, destination);
    }

    public SISOController(Output<Double> out, Double destination) {
        super(out, destination);
    }

    public SISOController(Input<Double> in, Output<Double> out) {
        super(in, out);
    }

    public SISOController(Output<Double> out) {
        super(out);
    }
}
