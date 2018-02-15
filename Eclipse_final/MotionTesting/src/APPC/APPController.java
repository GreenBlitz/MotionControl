package APPC;

import java.util.function.Function;

import org.usfirst.frc.team4590.robot.Robot;
import org.usfirst.frc.team4590.robot.RobotStats;

import base.Input;
import base.IterativeController;
import base.Output;
import base.point.IPoint2D;
import base.point.orientation.IOrientation2D;
import base.point.orientation.Orientation2D;

public class APPController extends IterativeController<IPoint2D, APPController.APPDriveData> {
	protected static final double DEFAULT_LOOKAHEAD = 0.5;
	protected static final double DEFAULT_TOLERANCE_DIST = 0.15;
	protected static final double DEFAULT_MIN_ON_TARGET_TIME = 0.02;
	protected static final double DEFAULT_SLOWDOWN = 0.5;
	protected static final Orientation2D FRONT_RELATIVE_TO_CENTER = Orientation2D.immutable(0,
			RobotStats.VERTICAL_WHEEL_DIST / 2, 0);

	/**
	 * the path the controller is following
	 */
	private ArenaMap m_map;

	/**
	 * Look ahead distance
	 */
	private double m_lookAhead;

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
		setTolerance(new AbsoluteTolerance(toleranceDist));
		setDestination(map.getLast());
	}

	private IPoint2D updateGoalPoint(IPoint2D loc, ArenaMap map, double lookAhead) {
		IPoint2D tmp = map.lastPointInRange(loc, lookAhead);
		tmp.toDashboard("Goal point");
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
	public double calculateCurve(IOrientation2D loc, IPoint2D goal) {
		IPoint2D goalVector = goal.changePrespectiveTo(loc);
		loc.toDashboard("Robot");
		goal.toDashboard("Goal point");
		if (goalVector.length() == 0)
			return 0;
		return (2.0 * goalVector.getX()) / Math.pow(goalVector.length(), 2);
	}

	public double[] calculateMovmentXY(IOrientation2D loc, IPoint2D goal) {
		// IOrientation2D front = Orientation2D.mutable(loc);
		// front.moveBy(FRONT_RELATIVE_TO_CENTER.rotate(loc.getDirection()),
		// IOrientation2D.DirectionEffect.CHANGED);
		IPoint2D goalVector = goal.changePrespectiveTo(loc);
		// Robot.p.println(getClass(), goalVector);
		return new double[] { goalVector.getX(), goalVector.getY() };
	}

	@Override
	public APPController.APPDriveData calculate(IPoint2D robotLocation) {
		IPoint2D goal = updateGoalPoint(robotLocation, m_map, m_lookAhead);
		// Robot.p.println(getClass(), "WARNING next goal point: " + goal);
		return new APPController.APPDriveData(1, calculateMovmentXY((IOrientation2D) robotLocation, goal));
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
		private int calls = 0;

		/**
		 * Same as
		 * {@link APPController.AbsoluteTimedTolerance#AbsoluteTimedTolerance(double, double)}
		 * except that this doesn't have mintime 1 * @param toleranceDist
		 */
		public AbsoluteTolerance(double toleranceDist) {
			m_toleranceDist = toleranceDist;
		}

		/**
		 * return whether or not the path is finished
		 */
		@Override
		public boolean onTarget() {
			if ((calls++) % 400 == 0) {
				Robot.p.printf(getClass(), "actual error: %s, tolerance distance: %f", APPController.this.getError().length(), m_toleranceDist);
				calls = 0;
			}
			return APPController.this.getError().length() <= m_toleranceDist;
		}
	}

	@Override
	public Orientation2D getError(IPoint2D loc, IPoint2D dest) {
		Robot.p.warnln(getClass(), loc);
		return Orientation2D.immutable(loc.getX() - dest.getX(), loc.getY() - dest.getY(),
				((IOrientation2D) loc).getDirection());
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
	 * @param max
	 */
	public void setPowerRange(double min, double max) {
		setOutputConstrain(
				(data) -> data.power >= min ? (data.power <= max ? data : APPDriveData.of(max, data.dx, data.dy))
						: APPDriveData.of(min, data.dx, data.dy));
	}
	
	public void setPowerConstrain(Function<Double, Double> constrain) {
		setOutputConstrain(data -> new APPDriveData(constrain.apply(data.power), data.dx, data.dy));
	}

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

		/**
		 * @param power
		 * @param curve
		 * @return DriveDate with given variables
		 */
		public static APPDriveData of(double power, double[] xy) {
			return new APPDriveData(power, xy[0], xy[1]);
		}

		public static APPDriveData of(double power, double x, double y) {
			return new APPDriveData(power, x, y);
		}

		@Override
		public String toString() {
			return "APPDriveData [power= " + power + ", x diff= " + dx + ", y diff= " + dy + "]";
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
