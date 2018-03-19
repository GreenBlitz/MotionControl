package gbmotion.appc;

import java.util.function.Function;

import org.usfirst.frc.team4590.robot.Robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import gbmotion.base.controller.Input;
import gbmotion.base.controller.IterativeController;
import gbmotion.base.controller.Output;
import gbmotion.base.point.IPoint2D;
import gbmotion.base.point.Point2D;
import gbmotion.base.point.orientation.IOrientation2D;
import gbmotion.path.ArenaMap;
import gbmotion.path.IndexedPoint2D;


public class APPController extends IterativeController<IPoint2D, APPController.APPDriveData> {
	protected static final double DEFAULT_LOOKAHEAD = 0.6;
	protected static final double DEFAULT_TOLERANCE_DIST = 0.05;
	protected static final double DEFAULT_MIN_ON_TARGET_TIME = 0.02;
	protected static final double DEFAULT_SLOWDOWN = 1;

	/**
	 * the path the controller is following
	 */
	private ArenaMap m_map;

	/**
	 * Look ahead distance
	 */
	private double m_lookAhead;

	/**
	 * Radius from destination in which slow down begins
	 */
	private double m_slowDownDistance;

	/**
	 *
	 * @param in
	 *            The input object
	 * @param out
	 *            The motor manager object
	 * @param map
	 * 			  The arena map object containing the path
	 */
	public APPController(Input<IPoint2D> in, Output<APPController.APPDriveData> out, ArenaMap map) {
		this(in, out, DEFAULT_PERIOD, map, DEFAULT_LOOKAHEAD, DEFAULT_TOLERANCE_DIST, DEFAULT_MIN_ON_TARGET_TIME,
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
	public APPController(Input<IPoint2D> in, Output<APPController.APPDriveData> out, ArenaMap map, double lookAhead,
			double toleranceDist, double minOnTargetTime, double slowDownDistance) {
		this(in, out, DEFAULT_PERIOD, map, lookAhead, toleranceDist, minOnTargetTime, slowDownDistance);
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
	public APPController(Input<IPoint2D> in, Output<APPController.APPDriveData> out, double period, ArenaMap map,
			double lookAhead, double toleranceDist, double minOnTargetTime, double slowDownDistance) {
		super(in, out, period, "APPController");
		m_map = map;
		m_lookAhead = lookAhead;
		setTolerance(new AbsoluteTolerance(output -> output.length(), toleranceDist));
		setDestination(map.getLast());
		m_slowDownDistance = DEFAULT_SLOWDOWN;
	}

	/**
	 * Updates the current goal point
	 * 
	 * @param loc
	 *            current robot location
	 * @param map
	 *            current arena map
	 * @param lookAhead
	 *            look ahead distance
	 * @return new goal point
	 */
	private IPoint2D updateGoalPoint(IPoint2D loc, ArenaMap map, double lookAhead) {
		IPoint2D tmp = map.lastPointInRangeBF(loc, 0, lookAhead);
		Robot.managedPrinter.println(getClass(), "next goal point: " + tmp);
		tmp.toDashboard("Goal point");
		if (tmp != null)
			NetworkTable.getTable("motion").putNumber("pointIdx", ((IndexedPoint2D)tmp).getIndex());
		return tmp;
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
	@Deprecated
	public double calculateCurve(IOrientation2D loc, IPoint2D goal) {
		IPoint2D goalVector = goal.changePrespectiveTo(loc);
		loc.toDashboard("Robot");
		goal.toDashboard("Goal point");
		if (goalVector.length() == 0)
			return 0;
		return (2.0 * goalVector.getX()) / Math.pow(goalVector.length(), 2);
	}

	/**
	 * @param loc
	 *            robot location
	 * @param goal
	 *            current goal point
	 * @return Point2D containing the difference between given points
	 */
	public double[] calculateMovmentXY(IOrientation2D loc, IPoint2D goal) {
		IPoint2D goalVector = goal.changePrespectiveTo(loc);
		return new double[] { goalVector.getX(), goalVector.getY() };
	}

	@Override
	public APPController.APPDriveData calculate(IPoint2D robotLocation) {
		IPoint2D goal = updateGoalPoint(robotLocation, m_map, m_lookAhead);
		return new APPController.APPDriveData(calculatePower(robotLocation),
				calculateMovmentXY((IOrientation2D) robotLocation, goal));
	}

	/**
	 * Calculates the maximal power to be used on the engines, effectively
	 * slowing down near the destination and improves accuracy
	 * 
	 * @param robotLoc
	 *            current robot location
	 * @return calculated power
	 */
	protected double calculatePower(IPoint2D robotLoc) {
		double distanceOverSlowDown = robotLoc.distance(m_destination) / m_slowDownDistance;
		if (distanceOverSlowDown > 1)
			return 1;
		return Math.max(distanceOverSlowDown, 0.5);
	}

	/**
	 * Timed tolerance with absolute convergence radius
	 * 
	 * @author Hoffman
	 */
	public class AbsoluteTimedTolerance extends TimedTolerance {

		private double m_toleranceDist;

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

	@Override
	public Point2D getError(IPoint2D loc, IPoint2D dest) {
		return Point2D.immutable(loc.getX() - dest.getX(), loc.getY() - dest.getY());
	}

	/**
	 * set the maximum and minimum power to be passed to m_output
	 * 
	 * @param limit
	 *            the maximum power and the lower limit (absolute value)
	 */
	public void setPowerLimit(double limit) {
		setPowerRange(-limit, limit);
	}

	/**
	 * Set maximum power to be sent to m_output
	 * 
	 * @param min
	 *            minimal power
	 * @param max
	 *            maximal power
	 */
	public void setPowerRange(double min, double max) {
		setOutputRange(new APPDriveData(min, 0, 0), new APPDriveData(max, 0, 0),
				(data1, data2) -> Double.compare(data1.power, data2.power));
	}

	public void setPowerConstrain(Function<Double, Double> constrain) {
		setOutputConstrain(data -> new APPDriveData(constrain.apply(data.power), data.dx, data.dy));
	}

	/**
	 * APPController drive data used in APPCOutput
	 * 
	 * @see APPCOutput
	 * @author karlo
	 */
	public static class APPDriveData {
		public double power;
		public double dx;
		public double dy;

		public APPDriveData(double power, double x, double y) {
			this.power = power;
			this.dx = x;
			this.dy = y;
		}

		public APPDriveData(double power, double[] xy) {
			this.power = power;
			try {
				this.dx = xy[0];
				this.dy = xy[1];
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new IllegalArgumentException("xy array has to have at least 2 values");
			}
		}

		public static APPDriveData of(double power, double[] xy) {
			return new APPDriveData(power, xy[0], xy[1]);
		}

		public static APPDriveData of(double power, double x, double y) {
			return new APPDriveData(power, x, y);
		}

		@Override
		public String toString() {
			return "APPDriveData [power=" + power + ", dx=" + dx + ", dy=" + dy + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			long temp;
			temp = Double.doubleToLongBits(dx);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(dy);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(power);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof APPDriveData)) {
				return false;
			}
			APPDriveData other = (APPDriveData) obj;
			if (Double.doubleToLongBits(dx) != Double.doubleToLongBits(other.dx)) {
				return false;
			}
			if (Double.doubleToLongBits(dy) != Double.doubleToLongBits(other.dy)) {
				return false;
			}
			if (Double.doubleToLongBits(power) != Double.doubleToLongBits(other.power)) {
				return false;
			}
			return true;
		}

	}
}
