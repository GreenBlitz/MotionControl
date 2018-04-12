package gbmotion.appc;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import com.kauailabs.navx.frc.AHRS;

import gbmotion.base.EnvironmentPort;
import gbmotion.base.controller.Input;
import gbmotion.base.controller.IterativeController;
import gbmotion.base.point.IPoint2D;
import gbmotion.base.point.orientation.IOrientation2D;
import gbmotion.base.point.orientation.IOrientation2D.DirectionEffect;
import gbmotion.base.point.orientation.MOrientation2D;
import gbmotion.base.point.orientation.Orientation2D;
import gbmotion.util.SmartEncoder;

public class Localizer implements Input<IPoint2D> {
	public enum AngleDifferenceCalculation {
		ENCODER_BASED, GYRO_BASED
	}

	public static final double PERIOD = IterativeController.DEFAULT_PERIOD / 4;
	public static final double ENCODER_VALIDITY_TOLERANCE = 10e-4;
	public static final Object LOCK = new Object();

	private IOrientation2D m_location;

	public SmartEncoder[] m_leftEncoders;
	public SmartEncoder[] m_rightEncoders;

	private AHRS m_navx;
	private double referenceAngle = 0;

	private double m_lastGyroAngle = 0;

	private double m_wheelDistance;

	private AngleDifferenceCalculation m_angleCalculationType;

	private EnvironmentPort ePort = EnvironmentPort.DEFAULT;

	private volatile boolean m_enabled = false;

	private volatile boolean shouldReset = false;

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
	public Localizer(SmartEncoder[] left, SmartEncoder[] right, Orientation2D location, double wheelDistance, AHRS navx,
			AngleDifferenceCalculation angleCalculationType) {
		m_location = location;
		m_leftEncoders = left;
		m_rightEncoders = right;
		m_wheelDistance = wheelDistance;
		Timer m_timer = new Timer();
		m_timer.schedule(new LocalizeTimerTask(), 0, (long) (1000 * PERIOD));
		m_navx = navx;
		m_angleCalculationType = angleCalculationType;
		m_lastGyroAngle = getAngleRadians();
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
	public Localizer(SmartEncoder left, SmartEncoder right, Orientation2D location, double wheelDistance, AHRS navx) {
		this(new SmartEncoder[] { left }, new SmartEncoder[] { right }, location, wheelDistance, navx,
				AngleDifferenceCalculation.ENCODER_BASED);
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
	public static Localizer of(SmartEncoder left, SmartEncoder right, double wheelDist, AHRS navx) {
		return new Localizer(left, right, Orientation2D.mutable(0, 0, 0), wheelDist, navx);
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
	public static Localizer of(SmartEncoder left, SmartEncoder right, double wheelDist, AHRS navx,
			AngleDifferenceCalculation angleCalculationType) {
		return new Localizer(new SmartEncoder[] { left }, new SmartEncoder[] { right }, Orientation2D.mutable(0, 0, 0),
				wheelDist, navx, angleCalculationType);
	}

	public int getLeftTicks() {
		return Arrays.stream(m_leftEncoders).map(SmartEncoder::getTicks).reduce((a, b) -> a + b).orElse(0)
				/ m_leftEncoders.length;
	}

	public int getRightTicks() {
		return Arrays.stream(m_rightEncoders).map(SmartEncoder::getTicks).reduce((a, b) -> a + b).orElse(0)
				/ m_rightEncoders.length;
	}

	/**
	 * @return distance traveled by left encoders
	 */
	public double getLeftDistance() {
		return Arrays.stream(m_leftEncoders).map(SmartEncoder::getDistance).reduce((a, b) -> a + b).orElse(.0)
				/ m_leftEncoders.length;
	}

	/**
	 * @return distance traveled by right encoders
	 */
	public double getRightDistance() {
		return Arrays.stream(m_rightEncoders).map(SmartEncoder::getDistance).reduce((a, b) -> a + b).orElse(.0)
				/ m_rightEncoders.length;
	}

	public void resetLoc() {
		synchronized (LOCK) {
			m_location = new MOrientation2D(0, 0, 0);
		}
	}

	public class LocalizeTimerTask extends TimerTask {

		private double leftDist;
		private double rightDist;

		public void oldRun(){

		
			double rightDistDiff = -rightDist;
            double leftDistDiff = -leftDist;
            leftDist = getLeftDistance();
        	rightDist = getRightDistance();
        	rightDistDiff += rightDist;
        	leftDistDiff += leftDist;
        	Point2DGuyde old = new Point2DGuyde(m_location.getX(), m_location.getY(), m_location.getDirection());

            if (Math.abs(leftDistDiff - rightDistDiff) < 10e-4) {
                synchronized (LOCK) {
                	old = old.moveBy(0, leftDistDiff);
                	m_location = m_location.set(old.getX(), old.getY(), old.getDirection() );
                    return;
                }
            }
            
        	
    		boolean leftIsLong = leftDistDiff > rightDistDiff;
    		double shortDist = leftIsLong ? rightDistDiff : leftDistDiff;
    		
    		 
    		double angle = (rightDistDiff - leftDistDiff) / m_wheelDistance;

    		double radiusFromCenter = -(shortDist/angle + Math.signum(angle) * m_wheelDistance/2);
    		double adjustedRadiusFromCenter = radiusFromCenter;
    		Point2DGuyde rotationOrigin = old.moveBy(adjustedRadiusFromCenter, 0);
            synchronized (LOCK){
                old = old.rotateRelativeTo(rotationOrigin, angle);
                m_location = m_location.set(old.getX(), old.getY(), old.getDirection());
                
            }

        	if (shouldReset){
				resetSelf();
				shouldReset = false;
			}
		}
	/**
		 * Update the robot position
		 */
		public void run() {

			if (shouldReset) {
				resetSelf();
			}

			if (ePort.isEnabled() && m_enabled) {
				m_location.toDashboard("Robot location");

				double gyroAngle = getAngleRadians();
				double gyroAngleDiff = gyroAngle - m_lastGyroAngle;
				m_lastGyroAngle = gyroAngle;

				double rightDistDiff = -rightDist;
				double leftDistDiff = -leftDist;

				leftDist = getLeftDistance();
				rightDist = getRightDistance();
				rightDistDiff += rightDist;
				leftDistDiff += leftDist;

				ePort.putNumber("Left encoder", leftDist);
				ePort.putNumber("Right encoder", rightDist);

				double angleChange = -(rightDistDiff - leftDistDiff) / m_wheelDistance;

				switch (m_angleCalculationType) {
				case ENCODER_BASED:
					break;
				case GYRO_BASED:
					angleChange = gyroAngleDiff;
					break;
				default:
					throw new IllegalArgumentException(
							"thou shalt not make unto thee any graven angle calculation methods");
				}

				if (angleChange == 0) {
					synchronized (LOCK) {
						m_location.moveBy(0, (leftDistDiff + rightDistDiff) / 2, m_location.getDirection(), DirectionEffect.RESERVED);
					}
				} else {
					boolean leftLonger = leftDistDiff > rightDistDiff;
					double shortDist = leftLonger ? rightDistDiff : leftDistDiff;
					double signedRadiusFromCenter = -(shortDist / angleChange
							+ Math.signum(angleChange) * m_wheelDistance / 2);
					IOrientation2D rotationOrigin = Orientation2D.immutable(m_location).moveBy(signedRadiusFromCenter,
							0, m_location.getDirection(), DirectionEffect.RESERVED);
					synchronized (LOCK) {
						m_location.rotateAround(rotationOrigin, angleChange, DirectionEffect.CHANGED);
//						m_location.setDirection(getAngleRadians());
					}
				}
				ePort.putNumber("angle", angleChange);
				edu.wpi.first.wpilibj.networktables.NetworkTable motion = edu.wpi.first.wpilibj.networktables.NetworkTable.getTable("motion");
				motion.putNumber("locX", m_location.getX());
				motion.putNumber("locY", m_location.getY());
				motion.putNumber("locAngle", m_location.getDirection());
				motion.putNumber("gyroAngle", Math.toRadians(m_navx.getYaw()));
				//System.out.println(gyro.getYaw());
				//motion.putNumber("pathLength", 0);
				motion.putNumber("encLeft", getLeftDistance());
				motion.putNumber("encRight", getRightDistance());
				motion.putBoolean("isUpdated", true);
			} else {
				resetSelf();
			}

			if (shouldReset) {
				resetSelf();
				shouldReset = false;
			}
		}
	}

	@Override
	public IPoint2D recieve() {
		synchronized (LOCK) {
			return Orientation2D.immutable(m_location);
		}
	}

	private double getAngleRadians() {
		return (m_navx.getYaw() - referenceAngle) * (Math.PI / 180);
	}

	/**
	 * Reset the encoders and the localizer saved location.
	 */
	private void resetSelf() {
		synchronized (LOCK) {
			m_location = m_location.set(0, 0, 0);
			referenceAngle = m_navx.getYaw();
		}

	}

	public synchronized void reset() {
		shouldReset = true;
	}

	public void setEnvironmentPort(EnvironmentPort ePort) {
		this.ePort = ePort;
	}

	/**
	 * Enabled localizer to run
	 */
	public synchronized void start() {
		m_enabled = true;
		reset();
	}

	/**
	 * Forces localizer to stop calculating
	 */
	public synchronized void stop() {
		m_enabled = false;
	}
}
