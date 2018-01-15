package APPC;


import base.Input;
import base.IterativeController;
import base.Output;
import edu.wpi.first.wpilibj.DriverStation;

// TODO add factory

public class APPController extends IterativeController<Point2D, Double[]> {


    /**
     * The most recent robot location calc
     */
    private Point2D m_robotLoc;
    /**
     * the path the controller is following
     */
    private Path m_path;
    /**
     * Look ahead value
     */
    private double m_lookAhead;
    /**
     * tolerance constant when searching for the goal point
     */
    private double m_epsilon;
    /**
     * The point we are trying to reach in robot coordinates
     */
    private Point2D m_goalPointR;
    /**
     * The length of the robot form middle of back wheels to middle of front wheels, in m
     */
    public static double Lb = -1;

    /**
     *used in update() to know if this search is the first
     */
    private boolean firstSearch;

    /**
     * starts slowing down when the distance to the end of path is shorter than this
     */
    private int m_slowDownDistance;



    public static void setLb(double newVal){
        if (Lb != -1){
             throw new RuntimeException("Lb already set (wtf why your robot changes size)");
        }
        if (newVal <= 0){
            throw new RuntimeException(String.format("Lb must be positive but it was set to %d", newVal));
        }
        if (newVal > 100 || newVal < 40){
            DriverStation.reportWarning(String.format("Lb was set to %d, isn't that a little to big/small?", newVal), false);
        }
        Lb = newVal;
    }

    public APPController(Input<Point2D> in, Output<Double[]> out,Point2D robotLoc,Path path, double lookAhead,double epsilon,double toleranceDist,double minOnTargetTime,int slowDownDistance) {
        this(in,out,DEFAULT_PERIOD,robotLoc,path,lookAhead,epsilon,toleranceDist,minOnTargetTime,slowDownDistance);
    }

    public APPController(Input<Point2D> in, Output<Double[]> out,double period,Point2D robotLoc,Path path, double lookAhead,double epsilon,double toleranceDist,double minOnTargetTime,int slowDownDistance) {
        super(in,out,period);
        m_robotLoc = robotLoc;
        m_path = path;
        m_lookAhead = lookAhead;
        m_epsilon = epsilon;
        m_destination = path.getLast();
        m_tolerance = new AbsoluteTolerance(toleranceDist,minOnTargetTime);
        m_slowDownDistance = slowDownDistance;
        firstSearch = true;
        m_goalPointR = null;
        if(epsilon < 0)
            DriverStation.reportWarning(String.format("epsilon should be a positive number"), false);
    }


    /**
     * Calculates the robot position using information
     * from encoders, gyro and accelerometer. then sets it
     * If we will have time we need to implement Kalman Filter
     */
    public void getRobotLocation(){
        m_robotLoc = m_input.recieve();
    }

    /**
     * Calculate the goal point we are trying to reach with path & lookahead
     */
    private void updateGoalPoint(){
        Point2D checkPoint;
        double min_distance = 0; //not relevant, set in order to not recieve an error
        boolean foundPoint = false;
        double distance;
        int goalPointIndex = 0; //not relevant, set in order to not recieve an error
        while(m_path.hasNext())
        {
            checkPoint = m_path.get();
            distance = m_robotLoc.distance(checkPoint) - m_lookAhead;
            if(foundPoint) //once a point is found searches nearby points for a better point
            {
                if (distance < m_epsilon && distance > -m_epsilon){
                    if(distance < min_distance){
                        min_distance = distance;
                        m_goalPointR = checkPoint;
                        goalPointIndex = m_path.getCurrentIndex();
                    }
                } else if (firstSearch) //preforms a global search on the first search
                    foundPoint = false;
                else {
                    m_path.setCurrentIndex(goalPointIndex);
                    return; //returns once the local search finishes
                }
            } else {
                if (distance < m_epsilon && distance > -m_epsilon){
                    foundPoint = true;
                    min_distance = distance;
                    m_goalPointR = checkPoint;
                    goalPointIndex = m_path.getCurrentIndex();
                }
            }
        }
        if(firstSearch){
            if(m_goalPointR == null)
                m_goalPointR = m_path.closetPointTo(m_robotLoc);
            firstSearch = false;
            m_path.setCurrentIndex(goalPointIndex);
        } else
            m_goalPointR = m_path.getLast();


    }




    /**
     * recieve the motor power ratio (fastMotor : slowMotor) we need to use
     * <p>https://pdfs.semanticscholar.org/82aa/c3a57f1941d11f13e6eb53e136bdea23894b.pdf</p>
     * @return the angular speed
     */
    public double getMotorRatio(){
        if (Lb == -1){
            throw new RuntimeException("Lb wasn't set prior to run");
        }
        // equation from:
        // https://pdfs.semanticscholar.org/82aa/c3a57f1941d11f13e6eb53e136bdea23894b.pdf
        // page 87
        double R = Math.pow(m_lookAhead, 2) / (2 * m_goalPointR.getX());
        return (R - 0.5*Lb) / (R + 0.5*Lb);
    }

    public APPController(Input<Point2D> in, Output<Double[]> out, Point2D destination) {
        super(in, out, destination);
    }

    public APPController(Output<Double[]> out, Point2D destination) {
        super(out, destination);
    }

    public APPController(Input<Point2D> in, Output<Double[]> out) {
        super(in, out);
    }

    public APPController(Output<Double[]> out) {
        super(out);
    }

    public APPController(Input<Point2D> in, Output<Double[]> out, double absoluteTolerance) {
        super(in, out);
        //setTolerance(new AbsoluteTolerance(absoluteTolerance));
    }

    @Override
    public void calculate() {
        if(m_tolerance.onTarget())
            m_output.use(new Double[]{0.0,0.0});
        else{
            getRobotLocation();
            updateGoalPoint();
            m_output.use(new Double[]{getMotorRatio(),getPowerPrecent()});
        }
    }

    @Override
    public void initParameters() throws NoSuchFieldException {
        m_parameters.put("Look-ahead distance", constructParam("m_lookAhead"));
    }


    public class AbsoluteTolerance extends TimedTolerance{

        double m_toleranceDist;

        public AbsoluteTolerance(double toleranceDist,double minTime){
            super(minTime);
            m_toleranceDist = toleranceDist;
        }
        /*
        public AbsoluteTolerance(double toleranceDist) {
            super(2 * DEFAULT_PERIOD);
            m_toleranceDist = toleranceDist;
        }
        */// discuss if this is needed
        @Override
        protected boolean onInstantTimeTarget(){
            return m_robotLoc.distance(m_destination)<m_toleranceDist;
        }

    }

    protected double getPowerPrecent(){
        return Math.min(1.0,m_robotLoc.distance(m_path.getLast())/(double)m_slowDownDistance);
    }
}
