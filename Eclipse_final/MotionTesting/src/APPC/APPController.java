package APPC;

import base.Input;
import base.IterativeController;
import base.Output;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class APPController extends IterativeController<Point2D, Double[]> {
<<<<<<< HEAD
    protected static final double DEFAULT_LOOKAHEAD = 1.5 /*0.3*/;
    protected static final double DEFAULT_EPSILON = 0.02;
    protected static final double DEFAULT_TOLERANCEDIST = 0.2;
    protected static final double DEFAULT_MINONTARGETTIME = 1;
    protected static final double DEFAULT_SLOWDOWN = 3;

    protected static final int LOOKBACK_DISTANCE = 25;

=======
    protected static final double DEFAULT_LOOKAHEAD = 0.3;
    protected static final double DEFAULT_TOLERANCE_DIST = 0.2;
    protected static final double DEFAULT_MIN_ON_TARGET_TIME = 1;
    protected static final double DEFAULT_SLOWDOWN = 0.5;
    
>>>>>>> 3958adb17261b79aabfa0e07a78b7bb53bf0ce28
    /**
     * The most recent robot location calc
     */
    private Point2D m_robotLoc;
    /**
     * the path the controller is following
     */
    private Path.PathIterator m_path;
    /**
     * Look ahead distance
     */
    private double m_lookAhead;
    /**
     * The point we are trying to reach in robot coordinates
     */
    private Point2D m_goalPoint;

    /**
     * starts slowing down when the distance to the end of path is shorter than
     * this
     */
    private double m_slowDownDistance;

    /**
     *
     * @param in
     *            The input object
     * @param out
     *            The motor manager object
     * @param path
     *            The path the robot will follow
     * @param lookAhead
     *            Look Ahead distance
     * @param toleranceDist
     *            Absolute tolerance distance
     * @param minOnTargetTime
     *            Minimal time on target required for the controller
     * @param slowDownDistance
     *            Distance from path end point in which the robot will slow down
     */
    public APPController(Input<Point2D> in, Output<Double[]> out, Path path) {
	this(in, out, DEFAULT_PERIOD, path, DEFAULT_LOOKAHEAD, DEFAULT_TOLERANCE_DIST,
		DEFAULT_MIN_ON_TARGET_TIME, DEFAULT_SLOWDOWN);
    }

    public APPController(Input<Point2D> in, Output<Double[]> out, Path path, double lookAhead,
	    double toleranceDist, double minOnTargetTime, double slowDownDistance) {
	this(in, out, DEFAULT_PERIOD, path, lookAhead, toleranceDist, minOnTargetTime, slowDownDistance);
    }

    /**
     *
     * @param in
     *            The input object
     * @param out
     *            The motor manager object
     * @param period
     *            The time period of calling the controller calculation
     * @param path
     *            The path the robot will follow
     * @param lookAhead
     *            Look Ahead distance
     * @param toleranceDist
     *            Absolute tolerance distance
     * @param minOnTargetTime
     *            Minimal time on target required for the controller
     * @param slowDownDistance
     *            Distance from path end point in which the robot will slow down
     */
    public APPController(Input<Point2D> in, Output<Double[]> out, double period, Path path, double lookAhead,
	    double toleranceDist, double minOnTargetTime, double slowDownDistance) {
	super(in, out, period);
	m_robotLoc = in.recieve();
	m_path = path.iterator();
	m_lookAhead = lookAhead;
	setTolerance(new AbsoluteTolerance2(toleranceDist));
	setDestination(path.getLast());
	m_slowDownDistance = slowDownDistance;
	m_goalPoint = null;
	updateGoalPoint();
    }

    /**
     * Calculates the robot position using information from encoders, gyro and
     * accelerometer. then sets it If we will have time we need to implement
     * Kalman Filter
     */
    public void updateRobotLocation() {
	m_robotLoc = m_input.recieve();
	// System.out.print("moving from-"+m_robotLoc);
	// m_robotLoc = m_robotLoc.moveBy(0,0.02);
	// System.out.println(" cur robot loc: "+m_robotLoc);
    }
    
    private void updateGoalPoint(){
	m_path.resetIterator();
	m_path.setCurrentIndex(m_path.getLength()-1);
	Point2D close = m_path.peek();
	Point2D point;
	while(m_path.getCurrentIndex()>0 && close.distance(m_robotLoc)>m_lookAhead){
	    m_path.changeCurrentIndex(-1);
	    point = m_path.peek();
	    if(close.distance(m_robotLoc) > point.distance(m_robotLoc)) {
		close = point;
	    }
	}
	m_goalPoint = close;
    }
    /**
     * Calculate the goal point we are trying to reach with path & lookahead
     */
<<<<<<< HEAD
    private void updateGoalPoint(){
        Point2D checkPoint;
        double min_distance = 0; //not relevant, set in order to not recieve an error
        boolean foundPoint = false;
        double distance;
        int goalPointIndex = 0; //not relevant, set in order to not recieve an error
        int i = 0;

        while(m_path.hasNext())
        {
        	//System.out.println(m_path.getCurrentIndex());
            checkPoint = m_path.get();
            distance = Math.abs(m_robotLoc.distance(checkPoint) - m_lookAhead);
            //System.out.println("dist between r:"+m_robotLoc+" c:"+checkPoint+"    is:"+distance);

            if(foundPoint) //once a point is found searches nearby points for a better point
            {
                if (distance<m_epsilon){
                	//System.out.println("another "+distance +" min is: "+min_distance +"THE POINT IS: "+checkPoint);
                    if(distance < min_distance){
                        min_distance = distance;
                        m_goalPointR = checkPoint;
                        goalPointIndex = m_path.getCurrentIndex();
                    }
                } else if (firstSearch) //preforms a global search on the first search
                    foundPoint = false;
                else {
                    m_path.setCurrentIndex(Math.max(goalPointIndex - LOOKBACK_DISTANCE,0));
                	//m_path.setCurrentIndex(Math.min(goalPointIndex - LOOKBACK_DISTANCE);
                    //System.out.println("robot: "+m_robotLoc);
                    //System.out.println("found a goalpoint!:: "+m_goalPointR);
                    return; //returns once the local search finishes
                }
            } else {
                if (distance<m_epsilon){
                	//System.out.println("found first dist "+distance);
                    foundPoint = true;
                    min_distance = distance;
                    m_goalPointR = checkPoint;
                    goalPointIndex = m_path.getCurrentIndex();
                }
            }
        }
        if(firstSearch){
            if(m_goalPointR == null){
                m_goalPointR = m_path.closestPointTo(m_robotLoc);
                //System.out.println("no goal");
            }
            firstSearch = false;
            m_path.setCurrentIndex(Math.max(goalPointIndex - LOOKBACK_DISTANCE,0));
            //System.out.println("in first search->");
        } else
            m_goalPointR = m_path.getLast();
        //System.out.println("None Found "+m_goalPointR);
        SmartDashboard.putNumber("X-pos GP", m_goalPointR.getX());
        SmartDashboard.putNumber("Y-pos GP", m_goalPointR.getY());
    }
    
    
    
    public double getCurve(){
    	Point2D goalVector = m_goalPointR.changePrespectiveTo(m_robotLoc);
    	double angle = Math.atan(goalVector.getX() / goalVector.getY()) / Math.PI * 180;
    	SmartDashboard.putNumber("Angle", angle);
    	return (2 * goalVector.getX()) / Math.pow(goalVector.length(), 2);
=======
    /**private void updateGoalPoint(){
	changeGoalPoint();
	SmartDashboard.putNumber("X-pos GP", m_goalPoint.getX());
	SmartDashboard.putNumber("Y-pos GP", m_goalPoint.getY());
    }
    private void changeGoalPoint() {
	double min_distance = 0; // not relevant, set to not receive an error
	boolean foundPoint = false;
	double distance;
	int goalPointIndex = 0; // not relevant, set to not receive an error

	for(Point2D point:m_path) {
	    // System.out.println(m_path.getCurrentIndex());
	    distance = Math.abs(m_robotLoc.distance(point) - m_lookAhead);
	    // System.out.println("dist between r:"+m_robotLoc+"
	    // c:"+checkPoint+" is:"+distance);

	    if (foundPoint) // once a point is found searches nearby points for a better point
	    {
		if (distance < m_epsilon) {
		    // System.out.println("another "+distance +" min is: "+min_distance +"THE POINT IS: "+checkPoint);
		    if (distance < min_distance) {
			min_distance = distance;
			m_goalPoint = point;
			goalPointIndex = m_path.getCurrentIndex();
			return;
		    }
		} else {
		    m_path.setCurrentIndex(Math.max(goalPointIndex - LOOKBACK_OFFSET, 0));
		    // m_path.setCurrentIndex(Math.min(goalPointIndex -
		    // LOOKBACK_DISTANCE);
		    // System.out.println("robot: "+m_robotLoc);
		    // System.out.println("found a goalpoint!:: "+m_goalPointR);
		    return; // returns once the local search finishes
		}
	    } else if (distance < m_epsilon) {
	    // System.out.println("found first dist "+distance);
	    foundPoint = true;
	    min_distance = distance;
	    m_goalPoint = point;
	    goalPointIndex = m_path.getCurrentIndex();
	    return;
	    }
	}
	m_goalPoint = m_path.getLast();
	// System.out.println("None Found "+m_goalPointR);
>>>>>>> 3958adb17261b79aabfa0e07a78b7bb53bf0ce28
    }
**/
    /**
    public void updateGoalPoint(){
	m_path.changeCurrentIndex(-LOOKBACK_OFFSET);
	Point2D close = m_path.peek();
	double closeVal = calcVal(close);
	Point2D curPoint = m_path.peek();
	int ticksFromLastChange = 0;
	while(ticksFromLastChange<30 && m_path.hasNext()){
	    if (calcVal(curPoint) < closeVal){
		ticksFromLastChange = 0;
		close = curPoint;
		closeVal = calcVal(close);
	    }
	    else ticksFromLastChange++;
	}
	
	m_goalPoint = close;
    }
**/
    public double getCurve() {
	Point2D goalVector = m_goalPoint.changePrespectiveTo(m_robotLoc);
	double angle = Math.atan(goalVector.getX() / goalVector.getY()) / Math.PI * 180;
	SmartDashboard.putNumber("Angle", angle);
	return (2 * goalVector.getX()) / Math.pow(goalVector.length(), 2);
    }

    // TODO: fix all the constructors to call this and not super
    /*
     * public APPController(Input<Point2D> in, Output<Double[]> out, Point2D
     * destination) { super(in, out, destination); }
     * 
     * public APPController(Output<Double[]> out, Point2D destination) {
     * super(out, destination); }
     * 
     * public APPController(Input<Point2D> in, Output<Double[]> out) { super(in,
     * out); }
     * 
     * public APPController(Output<Double[]> out) { super(out); }
     * 
     * public APPController(Input<Point2D> in, Output<Double[]> out, double
     * absoluteTolerance) { super(in, out); //setTolerance(new
     * AbsoluteTolerance(absoluteTolerance)); }
     */

    @Override
    public void calculate() {
<<<<<<< HEAD
       updateRobotLocation();
       updateGoalPoint();

       m_output.use(new Double[]{getPowerPrecent(),getCurve()});
   }
=======
	updateRobotLocation();
	updateGoalPoint();
	System.out.println("WARNING ---------------------------");
	System.out.println("WARNING - next goal point: " + m_goalPoint);
	
	
	System.out.println("WARNING ---------------------------");
	m_output.use(new Double[] { getPower(), getCurve() });
    }
>>>>>>> 3958adb17261b79aabfa0e07a78b7bb53bf0ce28

    @Override
    public void initParameters() throws NoSuchFieldException {
	m_parameters.put("Look-ahead distance", constructParam("m_lookAhead"));
    }

    public class AbsoluteTolerance extends TimedTolerance {

	double m_toleranceDist;

	public AbsoluteTolerance(double toleranceDist, double minTime) {
	    super(minTime);
	    m_toleranceDist = toleranceDist;
	}

	/*
	 * public AbsoluteTolerance(double toleranceDist) { super(2 *
	 * DEFAULT_PERIOD); m_toleranceDist = toleranceDist; }
	 */// discuss if this is needed
	@Override
	protected boolean onInstantTimeTarget() {
	    return m_robotLoc.distance(m_destination) < m_toleranceDist;
	}

    }

    public class AbsoluteTolerance2 extends Tolerance {

	double m_toleranceDist;

	public AbsoluteTolerance2(double toleranceDist) {
	    m_toleranceDist = toleranceDist;
	}

	/*
	 * public AbsoluteTolerance(double toleranceDist) { super(2 *
	 * DEFAULT_PERIOD); m_toleranceDist = toleranceDist; }
	 */// discuss if this is needed
	@Override
	public boolean onTarget() {
	    //Beep Boop! Those debug messages were generated automatically - do not change! Checksum will be checked
	    System.out.println("I am a robot, my location is " + m_robotLoc);
	    //System.out.println("I am a robot, I am going to a vacation at " + m_destination);
	    //System.out.println("I am a robot, I am not that patient, this is my tolerance for bullshit " + m_toleranceDist + "m");
	    System.out.println("Beep Boop! I know how to generate random numbers " + m_robotLoc.distance(m_destination));
	    return m_robotLoc.distance(m_destination) <= m_toleranceDist;
	}

    }

<<<<<<< HEAD
    protected double getPowerPrecent(){
    	// DON'T CHANGE
        return Math.min(1.0, m_robotLoc.distance(m_path.getLast()) / m_slowDownDistance);
=======
    protected double getPower() {
	// DON'T CHANGE
	return Math.min(1, m_robotLoc.distance(m_path.getLast()) / m_slowDownDistance);
>>>>>>> 3958adb17261b79aabfa0e07a78b7bb53bf0ce28
    }
}
