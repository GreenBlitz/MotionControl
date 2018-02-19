package gbmotion.appc;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.usfirst.frc.team4590.robot.Robot;

import gbmotion.base.EnvironmentPort;
import gbmotion.base.point.orientation.IOrientation2D;
import gbmotion.base.point.orientation.IOrientation2D.DirectionEffect;
import gbmotion.base.point.orientation.Orientation2D;
import gbmotion.controlflow.IChainConsumer;
import gbmotion.controlflow.IChainable;
import gbmotion.util.Tuple;

public class Localizer implements IChainConsumer<Tuple<List<Double>, Map<EnvironmentPort.ePortFunctions,Boolean>>, Boolean> {
	public static final double ENCODER_VALID_TOLERANCE = 10e-6;
	public static final Object LOCK = new Object();

	private IOrientation2D m_location;

	private double m_lastGyroAngle;

	private double m_wheelDistance;

	private EnvironmentPort ePort = EnvironmentPort.DEFAULT;
	private Map<EnvironmentPort.ePortFunctions, Boolean> m_ePortFuncs;

	double leftDist = 0;
	double rightDist = 0;

	/**
	 * 
	 * @param location
	 *            the initial location
	 * @param wheelDistance
	 *            distance between the wheels (right left)
	 */
	public Localizer(Orientation2D location, double wheelDistance) {
		m_location = location;
		m_wheelDistance = wheelDistance;
	}


	/**
	 * 
	 * @param wheelDist
	 *            distance between the wheels (right left)
	 * @return new localizer
	 */
	public static Localizer of(double wheelDist) {
		return new Localizer(Orientation2D.mutable(0, 0, 0), wheelDist);
	}

	private void run(double leftInput, double rightInput, double gyroInput) {

		if (m_ePortFuncs.get(EnvironmentPort.ePortFunctions.IS_ENABLED)) {
			m_location.toDashboard("Robot location");

			double gyroAngleDiff = gyroInput - m_lastGyroAngle;
			m_lastGyroAngle = gyroInput;

			double rightDistDiff = rightInput - rightDist;
			double leftDistDiff = leftInput - leftDist;

			ePort.putNumber("RLdiffDifference", rightDistDiff - leftDistDiff);

			leftDist = leftInput;
			rightDist = rightInput;
			double angle = (rightDistDiff - leftDistDiff) / m_wheelDistance;

			ePort.putNumber("Left encoder", leftDist);
			ePort.putNumber("Right encoder", rightDist);

			angle = isEncoderAngleValid(angle, gyroAngleDiff) ? angle : gyroAngleDiff;
			ePort.putNumber("angle", angle);

			if (angle == 0) {
				synchronized (LOCK) {
					m_location.moveBy(0, leftDistDiff, m_location.getDirection(), DirectionEffect.RESERVED);
				}
			} else {
				boolean leftIsLong = leftDistDiff > rightDistDiff;
				double shortDist = leftIsLong ? rightDistDiff : leftDistDiff;

				double signedRadiusFromCenter = -(shortDist / angle + Math.signum(angle) * m_wheelDistance / 2);
				IOrientation2D rotationOrigin = Orientation2D.immutable(m_location).moveBy(signedRadiusFromCenter, 0,
						m_location.getDirection(), DirectionEffect.RESERVED);
				synchronized (LOCK) {
					m_location.rotateAround(rotationOrigin, angle, DirectionEffect.CHANGED);
				}
			}
			Robot.managedPrinter.warnln(getClass(), "robot location: " + Orientation2D.immutable(m_location));
		} else {
			reset();
		}
	}

	private boolean isEncoderAngleValid(double encoderDiff, double gyroDiff) {
		return Math.abs(encoderDiff - gyroDiff) < ENCODER_VALID_TOLERANCE;
	}

	/**
	 * Reset the localizer saved location.
	 */
	public void reset() {
		m_location = m_location.set(0, 0, 0);
	}

	public void setEnvironmentPort(EnvironmentPort ePort) {
		this.ePort = ePort;
	}
	
	private HashSet<IChainable> inputs = new HashSet<>();
	
	@Override
	public void finalizeSimulation() {
		inputs = new HashSet<>();
	}

	@Override
	public boolean isCustomConsumer() {
		return false;
	}

	@Override
	public Boolean processData(Tuple<List<Double>, Map<EnvironmentPort.ePortFunctions, Boolean>> value) {
		run(value._1.get(0), value._1.get(1), value._1.get(2));
		m_ePortFuncs = value._2;
		return true;
	}

	@Override
	public boolean simulateInput(IChainable node) {
		return inputs.add(node);
	}

	@Override
	public boolean hasSimulatedInput() {
		return inputs.size() == 3;
	}
	
}
