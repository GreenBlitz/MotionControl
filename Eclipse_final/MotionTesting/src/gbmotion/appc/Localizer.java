package gbmotion.appc;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import org.usfirst.frc.team4590.robot.Robot;

import com.kauailabs.navx.frc.AHRS;

import gbmotion.base.EnvironmentPort;
import gbmotion.base.ScaledEncoder;
import gbmotion.base.controller.Input;
import gbmotion.base.controller.IterativeController;
import gbmotion.base.point.IPoint2D;
import gbmotion.base.point.orientation.IOrientation2D;
import gbmotion.base.point.orientation.IOrientation2D.DirectionEffect;
import gbmotion.base.point.orientation.Orientation2D;

public class Localizer implements Input<IPoint2D> {
	public enum AngleCalculation {
		ENCODER_BASED, GYRO_BASED, CONDITIONAL
	}

	public static final double PERIOD = IterativeController.DEFAULT_PERIOD / 4;
	public static final double ENCODER_VALIDITY_TOLERANCE = 10e-2;
	public static final Object LOCK = new Object();

	private IOrientation2D m_location;

	private ScaledEncoder[] m_leftWrappedEncoders;
	private ScaledEncoder[] m_rightWrappedEncoders;

	private AHRS m_navx;

	private double m_lastGyroAngle;

	private double m_wheelDistance;

	private AngleCalculation m_angleCalculationType;

	private EnvironmentPort ePort = EnvironmentPort.DEFAULT;

	/**
	 * 
	 * @param left
	 *            left encoders
	 * @param right
	 *            right encoders
	 * @param location
	 *            the initial location
	 * @param wheelDistance
	 *            distance between the wheels (right left)
	 * @param navx
	 * @param angleCalculationType
	 */
	public Localizer(ScaledEncoder[] left, ScaledEncoder[] right, Orientation2D location, double wheelDistance,
			AHRS navx, AngleCalculation angleCalculationType) {
		m_location = location;
		m_leftWrappedEncoders = left;
		m_rightWrappedEncoders = right;
		m_wheelDistance = wheelDistance;
		Timer m_timer = new Timer();
		m_timer.schedule(new LocalizeTimerTask(), 0, (long) (1000 * PERIOD));
		m_navx = navx;
		m_angleCalculationType = angleCalculationType;
	}

	/**
	 * 
	 * @param left
	 *            left encoders
	 * @param right
	 *            right encoders
	 * @param location
	 *            the initial location
	 * @param wheelDistance
	 *            distance between the wheels (right left)
	 * @param navx
	 */
	public Localizer(ScaledEncoder left, ScaledEncoder right, Orientation2D location, double wheelDistance, AHRS navx) {
		this(new ScaledEncoder[] { left }, new ScaledEncoder[] { right }, location, wheelDistance, navx, AngleCalculation.CONDITIONAL);
	}

	/**
	 * 
	 * @param left
	 *            left encoder
	 * @param right
	 *            right encoder
	 * @param wheelDist
	 *            distance between the wheels (right left)
	 * @param navx
	 * @return new localizer
	 */
	public static Localizer of(ScaledEncoder left, ScaledEncoder right, double wheelDist, AHRS navx) {
		return new Localizer(left, right, Orientation2D.mutable(0, 0, 0), wheelDist, navx);
	}

	/**
	 * @return distance traveled by left encoders
	 */
	public double getLeftDistance() {
		return Arrays.stream(m_leftWrappedEncoders).map(ScaledEncoder::getDistance).reduce((a, b) -> a + b).orElse(.0)
				/ m_leftWrappedEncoders.length;
	}

	/**
	 * @return distance traveled by right encoders
	 */
	public double getRightDistance() {
		return Arrays.stream(m_rightWrappedEncoders).map(ScaledEncoder::getDistance).reduce((a, b) -> a + b).orElse(.0)
				/ m_rightWrappedEncoders.length;
	}

	public class LocalizeTimerTask extends TimerTask {

		private double leftDist;
		private double rightDist;

		@Override
		/**
		 * Update the robot position
		 */
		public void run() {
			if (ePort.isEnabled()) {
				m_location.toDashboard("Robot location");

				double gyroAngle = m_navx.getAngle() * Math.PI / 180;
				double gyroAngleDiff = gyroAngle - m_lastGyroAngle;

				double rightDistDiff = -rightDist;
				double leftDistDiff = -leftDist;

				leftDist = getLeftDistance();
				rightDist = getRightDistance();
				rightDistDiff += rightDist;
				leftDistDiff += leftDist;
				ePort.putNumber("RLdiffDifference", rightDistDiff - leftDistDiff);
				ePort.putNumber("Left encoder", leftDist);
				ePort.putNumber("Right encoder", rightDist);
				
				double angle = (rightDistDiff - leftDistDiff) / m_wheelDistance;

				switch (m_angleCalculationType) {
				case ENCODER_BASED:
					break;
				case GYRO_BASED:
					angle = gyroAngleDiff;
					break;
				case CONDITIONAL:
					angle = isEncoderAngleValid(angle, gyroAngleDiff) ? angle : gyroAngleDiff;
					break;
				default:
					throw new IllegalArgumentException(
							"thou shalt not make unto thee any graven angle calculation methods");
				}
				m_lastGyroAngle = gyroAngle;

				if (angle == 0) {
					synchronized (LOCK) {
						m_location.moveBy(0, leftDistDiff, m_location.getDirection(), DirectionEffect.RESERVED);
					}
				} else {
					boolean leftIsLong = leftDistDiff > rightDistDiff;
					double shortDist = leftIsLong ? rightDistDiff : leftDistDiff;

					double signedRadiusFromCenter = -(shortDist / angle + Math.signum(angle) * m_wheelDistance / 2);
					IOrientation2D rotationOrigin = Orientation2D.immutable(m_location).moveBy(signedRadiusFromCenter,
							0, m_location.getDirection(), DirectionEffect.RESERVED);
					synchronized (LOCK) {
						m_location.rotateAround(rotationOrigin, angle, DirectionEffect.CHANGED);
					}
				}
				ePort.putNumber("angle", angle);
				Robot.managedPrinter.warnln(getClass(), "robot location: " + Orientation2D.immutable(m_location));
			} else {
				reset();
			}
		}
	}

	@Override
	public IPoint2D recieve() {
		synchronized (LOCK) {
			return Orientation2D.immutable(m_location);
		}
	}

	private boolean isEncoderAngleValid(double encoderDiff, double gyroDiff) {
		boolean ret = Math.abs(encoderDiff - gyroDiff) < ENCODER_VALIDITY_TOLERANCE;
		Robot.managedPrinter.warnln(this.getClass(), "angle condition resolved with " + (ret ? "encoders" : "gyro"));
		return ret;
	}

	/**
	 * Reset the encoders and the localizer saved location.
	 */
	public void reset() {
		for (ScaledEncoder enc : m_leftWrappedEncoders)
			enc.reset();

		for (ScaledEncoder enc : m_rightWrappedEncoders)
			enc.reset();

		m_location = m_location.set(0, 0, 0);
	}

	public void setEnvironmentPort(EnvironmentPort ePort) {
		this.ePort = ePort;
	}
}