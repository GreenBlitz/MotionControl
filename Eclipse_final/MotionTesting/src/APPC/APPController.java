package APPC;

import base.Input;
import base.IterativeController;
import base.Output;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class APPController extends IterativeController<Orientation2D, APPController.APPDriveData> {
	protected static final double DEFAULT_LOOKAHEAD = 0.5;
	protected static final double DEFAULT_TOLERANCE_DIST = 0.03;
	protected static final double DEFAULT_MIN_ON_TARGET_TIME = 0.02;
	protected static final double DEFAULT_SLOWDOWN = 0.5;

	/**
	 * the path the controller is following
	 */
	private Path.PathIterator m_path;
	
	/**
	 * Look ahead distance
	 */
	private double m_lookAhead;
	
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
	public APPController(Input<Orientation2D> in, Output<APPController.APPDriveData> out, Path path) {
		this(in, out, DEFAULT_PERIOD, path, DEFAULT_LOOKAHEAD, DEFAULT_TOLERANCE_DIST, DEFAULT_MIN_ON_TARGET_TIME,
				DEFAULT_SLOWDOWN);
	}

	/**
	 * 
	 * @param in
	 * @param out
	 * @param path
	 * @param lookAhead
	 * @param toleranceDist
	 * @param minOnTargetTime
	 * @param slowDownDistance
	 */
	public APPController(Input<Orientation2D> in, Output<APPController.APPDriveData> out, Path path, double lookAhead,
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
	public APPController(Input<Orientation2D> in, Output<APPController.APPDriveData> out, double period, Path path,
			double lookAhead, double toleranceDist, double minOnTargetTime, double slowDownDistance) {
		super(in, out, period, "APPController");
		m_path = path.iterator();
		m_lookAhead = lookAhead;
		setTolerance(new AbsoluteTimedTolerance(toleranceDist, minOnTargetTime));
		setDestination(path.getLast());
		m_slowDownDistance = slowDownDistance;
	}

	/**
	 * Updates the goal point the robot is trying to reach
	 * 
	 * @param loc
	 *            the robot location
	 * @param path
	 *            the path the robot is following
	 * @param lookAhead
	 *            the distance the goal point should be at
	 * @return the goal point
	 */
	private Orientation2D updateGoalPoint(Orientation2D loc, Path.PathIterator path, double lookAhead) {
		path.setCurrentIndex(path.getLength() - 1);
		Orientation2D close = path.peek();
		Orientation2D point;
		while (path.getCurrentIndex() > 0 && close.distance(loc) > lookAhead) {
			path.changeCurrentIndex(-1);
			point = path.peek();
			if (close.distance(loc) > point.distance(loc)) {
				close = point;
			}
		}
		
		SmartDashboard.putNumber("X-pos GP", close.getX());
		SmartDashboard.putNumber("Y-pos GP", close.getY());
		
		return close;
	}

	/**
	 * Calculate the curve the robot should follow to reach the goal point
	 * 
	 * @param loc
	 *            the robot location
	 * @param goal
	 *            the goal point
	 * @return the curve coefficient which is 1/R
	 */
	public double calculateCurve(Orientation2D loc, Orientation2D goal) {
		Orientation2D goalVector = goal.changePrespectiveTo(loc);
		double angle = Math.atan(goalVector.getX() / goalVector.getY()) / Math.PI * 180;
		m_environmentPort.putNumber("Angle", angle);
		return (2 * goalVector.getX()) / Math.pow(goalVector.length(), 2);
	}

	@Override
	public APPController.APPDriveData calculate(Orientation2D robotLocation) {
		Orientation2D goal = updateGoalPoint(robotLocation, m_path, m_lookAhead);
		System.out.println("next goal point: " + goal);
		return new APPController.APPDriveData(calculatePower(robotLocation, m_path, m_slowDownDistance),
				calculateCurve(robotLocation, goal));
	}

	public class AbsoluteTimedTolerance extends TimedTolerance {

		double m_toleranceDist;

		/**
		 * The object that describes the distance that is acceptable to count as
		 * finishing the path
		 * 
		 * @param toleranceDist
		 *            the acceptable distance
		 * @param minTime
		 *            minimum time before the tolerance will return true
		 */
		public AbsoluteTimedTolerance(double toleranceDist, double minTime) {
			super(minTime);
			m_toleranceDist = toleranceDist;
		}

		@Override
		/**
		 * return whether or not the path is finished
		 */
		protected boolean onInstantTimeTarget() {
			return APPController.this.getInput().distance(m_destination) < m_toleranceDist;
		}

	}

	public class AbsoluteTolerance implements ITolerance {

		double m_toleranceDist;

		/**
		 * Same as
		 * {@link APPController.AbsoluteTimedTolerance#AbsoluteTimedTolerance(double, double)}
		 * except that this doesn't have mintime 1 * @param toleranceDist
		 */
		public AbsoluteTolerance(double toleranceDist) {
			m_toleranceDist = toleranceDist;
		}

		@Override
		/**
		 * return whether or not the path is finished
		 */
		public boolean onTarget() {
			return APPController.this.getInput().distance(m_destination) <= m_toleranceDist;
		}

	}

	/**
	 * If the robot is farther then <code>slowDownDistance</code> return 1,
	 * otherwise return a 0.5 unless
	 * <code>path.getLast()) / slowDownDistance</code> is larger than 1, then
	 * return <code>path.getLast()) / slowDownDistance</code>
	 * 
	 * @param robotLoc
	 *            the location of the robot
	 * @param path
	 *            the path the robot is following
	 * @param slowDownDistance
	 *            the distance at which the robot stats to slow down
	 * @return the power which the robot will go at
	 */
	protected double calculatePower(Orientation2D robotLoc, Path.PathIterator path, double slowDownDistance) {
		double distanceOverSlowDown = robotLoc.distance(path.getLast()) / slowDownDistance;
		int sign = path.getLast().changePrespectiveTo(robotLoc).getY()>=0 ? 1:-1;
		if (distanceOverSlowDown > 1)
			return sign;
		if (distanceOverSlowDown > 0.4)
			return distanceOverSlowDown * sign;
		return 0.4 * sign;
	}

	@Override
	public Orientation2D getError(Orientation2D loc, Orientation2D dest) {
		return new Orientation2D(loc.getX() - dest.getX(), loc.getY() - dest.getY(),
				loc.getDirection() - dest.getDirection());
	}

	/**
	 * set the maximum and minimum power to be passed to m_output
	 * 
	 * @param limit
	 *            the maximum power and the lower limit (absaloute value)
	 */
	public void setPowerLimit(double limit) {
		setPowerRange(-limit, limit);
	}

	/**
	 * set the farthest input location to be within a certain distance
	 * 
	 * @param length
	 *            that distance
	 */
	public void setLocationMaxLength(double length) {
		setInputConstrain(input -> input.length() <= length ? input : input.scale(length / input.length()));
	}

	/**
	 * @param power
	 * @param curve
	 * @return DriveDate with given variables
	 */
	public APPController.APPDriveData of(double power, double curve) {
		return new APPController.APPDriveData(power, curve);
	}

	/**
	 * Set maximum power to be sent to m_output
	 * 
	 * @param min
	 * @param max
	 */
	public void setPowerRange(double min, double max) {
		setOutputConstrain(
				data -> data.power >= min ? (data.power <= max ? data : of(max, data.curve)) : of(min, data.curve));
	}

	public static class APPDriveData {
		public double power;
		public double curve;

		public APPDriveData(double power, double curve) {
			this.power = power;
			this.curve = curve;
		}

		@Override
		public String toString() {
			return "[power=" + power + ", curve=" + curve + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			long temp;
			temp = Double.doubleToLongBits(curve);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(power);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			APPController.APPDriveData other = (APPController.APPDriveData) obj;
			if (Double.doubleToLongBits(curve) != Double.doubleToLongBits(other.curve))
				return false;
			if (Double.doubleToLongBits(power) != Double.doubleToLongBits(other.power))
				return false;
			return true;
		}
	}
}
