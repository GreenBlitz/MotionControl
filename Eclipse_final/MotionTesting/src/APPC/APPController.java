package APPC;

import base.Input;
import base.IterativeController;
import base.Output;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class APPController extends IterativeController<Point2D, APPController.APPDriveData> {
	protected static final double DEFAULT_LOOKAHEAD = 0.3;
	protected static final double DEFAULT_TOLERANCE_DIST = 0.1;
	protected static final double DEFAULT_MIN_ON_TARGET_TIME = 1;
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
	public APPController(Input<Point2D> in, Output<APPController.APPDriveData> out, Path path) {
		this(in, out, DEFAULT_PERIOD, path, DEFAULT_LOOKAHEAD, DEFAULT_TOLERANCE_DIST, DEFAULT_MIN_ON_TARGET_TIME,
				DEFAULT_SLOWDOWN);
	}

	public APPController(Input<Point2D> in, Output<APPController.APPDriveData> out, Path path, double lookAhead,
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
	public APPController(Input<Point2D> in, Output<APPController.APPDriveData> out, double period, Path path,
			double lookAhead, double toleranceDist, double minOnTargetTime, double slowDownDistance) {
		super(in, out, period, "APPController");
		m_path = path.iterator();
		m_lookAhead = lookAhead;
		setTolerance(new AbsoluteTolerance(toleranceDist, 20));
		setDestination(path.getLast());
		m_slowDownDistance = slowDownDistance;
	}

	private Point2D updateGoalPoint(Point2D loc, Path.PathIterator path, double lookAhead) {
		path.resetIterator();
		path.setCurrentIndex(path.getLength() - 1);
		Point2D close = path.peek();
		Point2D point;
		while (path.getCurrentIndex() > 0 && close.distance(loc) > lookAhead) {
			path.changeCurrentIndex(-1);
			point = path.peek();
			if (close.distance(loc) > point.distance(loc)) {
				close = point;
			}
		}
		return close;
	}

	public double calculateCurve(Point2D loc, Point2D goal) {
		Point2D goalVector = goal.changePrespectiveTo(loc);
		double angle = Math.atan(goalVector.getX() / goalVector.getY()) / Math.PI * 180;
		SmartDashboard.putNumber("Angle", angle);
		return (2 * goalVector.getX()) / Math.pow(goalVector.length(), 2);
	}

	@Override
	public APPController.APPDriveData calculate(Point2D robotLocation) {
		Point2D goal = updateGoalPoint(robotLocation, m_path, m_lookAhead);
		System.out.println("next goal point: " + goal);
		return new APPController.APPDriveData(calculatePower(robotLocation, m_path, m_slowDownDistance),
				calculateCurve(robotLocation, goal));
	}

	public class AbsoluteTolerance extends TimedTolerance {

		double m_toleranceDist;

		public AbsoluteTolerance(double toleranceDist, double minTime) {
			super(minTime);
			m_toleranceDist = toleranceDist;
		}

		@Override
		protected boolean onInstantTimeTarget() {
			return m_input.recieve().distance(m_destination) < m_toleranceDist;
		}

	}

	public class AbsoluteTolerance2 implements ITolerance {

		double m_toleranceDist;

		public AbsoluteTolerance2(double toleranceDist) {
			m_toleranceDist = toleranceDist;
		}

		@Override
		public boolean onTarget() {
			return m_input.recieve().distance(m_destination) <= m_toleranceDist;
		}

	}

	protected double calculatePower(Point2D robotLoc, Path.PathIterator path, double slowDownDistance) {
		return Math.min(1, (robotLoc.distance(path.getLast()) / slowDownDistance) + 0.5);
	}

	@Override
	public Point2D getError(Point2D loc, Point2D dest) {
		return new Point2D(loc.getX() - dest.getX(), loc.getY() - dest.getY(),
				loc.getDirection() - dest.getDirection());
	}

	public int compare(Point2D o1, Point2D o2) {
		return Double.compare(o1.length(), o2.length());
	}

	public void setPowerLimit(double limit) {
		setOutputConstrain(data -> Math.abs(data.power) <= limit ? data
				: new APPController.APPDriveData(limit * Math.signum(data.power), data.curve));
	}

	public void setLocationMaxLength(double length) {
		setInputConstrain(input -> input.length() <= length ? input : input.scale(length / input.length()));
	}

	public APPController.APPDriveData of(double power, double curve) {
		return new APPController.APPDriveData(power, curve);
	}

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
