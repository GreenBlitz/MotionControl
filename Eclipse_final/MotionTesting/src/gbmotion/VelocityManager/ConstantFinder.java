package gbmotion.VelocityManager;

import java.util.ArrayList;
import java.util.List;

import org.usfirst.frc.team4590.robot.Robot;
import org.usfirst.frc.team4590.robot.RobotStats;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import gbmotion.util.Tuple;

/**
 * Finds Ku and Kv for velocity manager. Create an instance and execute run()
 * every loop. Instruction will be printed on screen.
 * 
 * @author Alexey
 *
 */
public class ConstantFinder {

	private static final int SAMPLE_AMOUNT = 6;
	private static final int PERIOD = 100;

	private CurrentStage m_currentStage = CurrentStage.PRE_FIND_KU;
	private double m_ku;
	private List<Double> m_kuSamples = new ArrayList<>();
	private double m_kv;
	private List<Double> m_kvSamples = new ArrayList<>();

	private Joystick dispairStick = new Joystick(0);
	private final double FULL_POWER = 0.8;

	private List<Tuple<CANTalon, Boolean>> actuators;

	private List<Tuple<Encoder, Boolean>> encoders;
	private int lastEncoderValue = 0;

	private long lastTest = System.currentTimeMillis();
	private double deltaTime;
	private double lastVelocity = 0;

	/**
	 * Make sure the encoders are connected to the relevant actuators. Also make
	 * sure you don't break the robot. ALL OF THE ACTUATORS WILL MOVE IN THE
	 * SPECIFIED DIRECTION.
	 * 
	 * @param encoders
	 *            A list of tuples, each containing a encoder and a boolean
	 *            indicating whether or not you should invert ti's value
	 * @param actuators
	 *            A list of tuples, each tuple containing a talon and whether to
	 *            invert the power when running it (for chasis)
	 */
	public ConstantFinder(List<Tuple<Encoder, Boolean>> encoders, List<Tuple<CANTalon, Boolean>> actuators) {
		this.encoders = encoders;
		this.actuators = actuators;
	}

	/**
	 * Make sure the encoder is connected to the relevant actuator. Also make
	 * sure you don't break the robot. THE ACTUATOR WILL MOVE IN THE SPECIFIED
	 * DIRECTION.
	 * 
	 * @param enc
	 *            encoder
	 * @param talon
	 */
	public ConstantFinder(Tuple<Encoder, Boolean> enc, Tuple<CANTalon, Boolean> talon) {
		List<Tuple<CANTalon, Boolean>> useT = new ArrayList<>();
		List<Tuple<Encoder, Boolean>> useE = new ArrayList<>();
		useT.add(talon);
		useE.add(enc);
		this.encoders = useE;
		this.actuators = useT;
	}

	private enum CurrentStage {
		PRE_FIND_KU, FIND_KU, PRE_FIND_KV, FIND_KV, DONE_PRINT, DONE
	}

	private int getEncoderTicks() {
		int total = 0;
		for (Tuple<Encoder, Boolean> encoder : encoders) {
			total += encoder._2 ? -encoder._1.get() : encoder._1.get();
		}
		total /= encoders.size();
		int ret = total - lastEncoderValue;
		lastEncoderValue = total;
		return ret;
	}

	private double getStandartDeviation(List<Double> data) {
		double mean = 0;
		for (int i = 0; i < data.size(); i++) {
			mean += data.get(i);
		}
		mean /= data.size();
		double squareSumAboutMean = 0;
		for (int i = 0; i < data.size(); i++) {
			squareSumAboutMean += Math.pow((data.get(i) - mean), 2);
		}
		return Math.sqrt(squareSumAboutMean / (data.size() - 1));
	}

	private double getEncoderRate() {
		int ticks = getEncoderTicks();
		long timeNow = System.currentTimeMillis();
		deltaTime = (timeNow - lastTest) / 1000.0;
		lastTest = timeNow;
		return ((double) ticks * RobotStats.ENCODER_TICKS_PER_RADIAN) / deltaTime;
	}

	private static double regulate(double velocity, final double FULL_POWER) {
		if (velocity < 0)
			velocity = Math.max(velocity, -FULL_POWER);
		else if (velocity > 0)
			velocity = Math.min(velocity, FULL_POWER);
		return velocity;
	}

	private double m_power;
	private double m_currentVelocity;
	private double m_accl;

	/**
	 * Run this every loop in a different thread
	 */
	public void run() {
		while (m_currentStage != CurrentStage.DONE) {
			try {
				Thread.sleep(PERIOD);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			m_currentVelocity = getEncoderRate();
			m_accl = (m_currentVelocity - lastVelocity) / deltaTime;
			Robot.managedPrinter.warn(this.getClass(),
					"Velocity: " + m_currentVelocity + "|~~~| Acceleration: " + m_accl);
			switch (m_currentStage) {
			case PRE_FIND_KU:
				Robot.managedPrinter.warn(this.getClass(),
						" Searching for KU please speed up the robot and then stop giving power");
				m_currentStage = CurrentStage.FIND_KU;
			case FIND_KU:

				m_power = regulate(dispairStick.getRawAxis(1), FULL_POWER);
				for (Tuple<CANTalon, Boolean> canTalon : actuators) {
					canTalon._1.set(canTalon._2 ? -m_power : m_power);
				}
				if (Math.abs(m_power) < 0.0001 && Math.abs(m_currentVelocity) > 0.0001) {
					m_kuSamples.add(m_accl / m_currentVelocity);
					Robot.managedPrinter.warn(this.getClass(), " Sample " + m_kuSamples.get(m_kuSamples.size() - 1)
							+ " added. " + m_kuSamples.size() + "/" + SAMPLE_AMOUNT);
				}
				if (m_kuSamples.size() >= SAMPLE_AMOUNT) {
					m_ku = m_kuSamples.stream().reduce((a, b) -> a + b).orElse(.0) / m_kuSamples.size();
					m_currentStage = CurrentStage.PRE_FIND_KV;
					Robot.managedPrinter.warn(this.getClass(), "ku found and it is: " + m_ku);
					Robot.managedPrinter.warn(this.getClass(),
							"Standart deveation: " + getStandartDeviation(m_kuSamples));
				}
				break;

			case PRE_FIND_KV:

				Robot.managedPrinter.warn(this.getClass(), " Searching for KV please move the robot around");
				m_currentStage = CurrentStage.FIND_KV;
			case FIND_KV:
				m_power = regulate(dispairStick.getRawAxis(1), FULL_POWER);
				for (Tuple<CANTalon, Boolean> canTalon : actuators) {
					canTalon._1.set(canTalon._2 ? -m_power : m_power);
				}

				if (Math.abs(m_power) > 0.2 && Math.abs(m_currentVelocity) > 0.01) {
					m_kvSamples.add((m_accl - m_ku * m_currentVelocity) / (m_power * 12));
					Robot.managedPrinter.warn(this.getClass(), " Sample " + m_kvSamples.get(m_kvSamples.size() - 1)
							+ " added. " + m_kvSamples.size() + "/" + SAMPLE_AMOUNT * 20);
				}

				if (m_kvSamples.size() >= SAMPLE_AMOUNT * 20) {
					m_kv = m_kvSamples.stream().reduce((a, b) -> a + b).orElse(.0) / m_kvSamples.size();
					m_currentStage = CurrentStage.DONE_PRINT;
					Robot.managedPrinter.warn(this.getClass(), "kv found and it is: " + m_kv);
					Robot.managedPrinter.warn(this.getClass(),
							"Standart deveation: " + getStandartDeviation(m_kvSamples));
				}
				break;

			case DONE_PRINT:
				for (Tuple<CANTalon, Boolean> canTalon : actuators) {
					canTalon._1.set(0);
				}
				Robot.managedPrinter.warn(this.getClass(), " You are DONE, ku: " + m_ku + " kv:" + m_kv);
				m_currentStage = CurrentStage.DONE;
			case DONE:
				break;
			default:
				throw new RuntimeException("Som bodi toucha mah inuum");
			}
			lastVelocity = m_currentVelocity;
		}
	}

}
