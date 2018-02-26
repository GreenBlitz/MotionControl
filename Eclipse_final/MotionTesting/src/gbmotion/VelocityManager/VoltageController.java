package gbmotion.VelocityManager;

import java.util.Date;
import java.util.List;

/**
 * Controls the voltage of an actuator using desired velocity. <br>
 * base equations: <br>
 * <a href=
 * "https://www.codecogs.com/eqnedit.php?latex=a_\omega&space;=&space;k_v&space;\cdot&space;V&space;&plus;&space;k_\omega&space;\cdot&space;\omega"
 * target="_blank"><img src=
 * "https://latex.codecogs.com/gif.latex?a_\omega&space;=&space;k_v&space;\cdot&space;V&space;&plus;&space;k_\omega&space;\cdot&space;\omega"
 * title="a_\omega = k_v \cdot V + k_\omega \cdot \omega" /></a> <br>
 * <br>
 * <a href=
 * "https://www.codecogs.com/eqnedit.php?latex=a_s&space;=&space;k_v&space;\cdot&space;V&space;&plus;&space;k_\omega&space;\cdot&space;\omega"
 * target="_blank"><img src=
 * "https://latex.codecogs.com/gif.latex?a_s&space;=&space;k_v&space;\cdot&space;V&space;&plus;&space;k_\omega&space;\cdot&space;\omega"
 * title="a_s = k_v \cdot V + k_\omega \cdot \omega" /></a> <br>
 * <a href=
 * "https://www.codecogs.com/eqnedit.php?latex=a_e&space;=&space;k_v&space;\cdot&space;V&space;&plus;&space;k_\omega&space;\cdot&space;(\omega&space;&plus;&space;a_s&space;\cdot&space;\Delta&space;t)"
 * target="_blank"><img src=
 * "https://latex.codecogs.com/gif.latex?a_e&space;=&space;k_v&space;\cdot&space;V&space;&plus;&space;k_\omega&space;\cdot&space;(\omega&space;&plus;&space;a_s&space;\cdot&space;\Delta&space;t)"
 * title=
 * "a_e = k_v \cdot V + k_\omega \cdot (\omega + a_s \cdot \Delta t)" /></a>
 * <br>
 * <a href=
 * "https://www.codecogs.com/eqnedit.php?latex=a_a&space;=&space;a_d&space;=&space;\frac{2\cdot&space;a_s&space;&plus;&space;a_e}{3}"
 * target="_blank"><img src=
 * "https://latex.codecogs.com/gif.latex?a_a&space;=&space;a_d&space;=&space;\frac{2\cdot&space;a_s&space;&plus;&space;a_e}{3}"
 * title="a_a = a_d = \frac{2\cdot a_s + a_e}{3}" /></a>
 * 
 * @author Alexey
 *
 */
public class VoltageController {
	
	/**
	 * Indicates on what to base the controller
	 * @author theem
	 *
	 */
	public static enum TimeOption{
		/**
		 * does the maximum possible effort to reach the desired velocity in this cycle, without passing it 
		 */
		ASAP,
		/**
		 * Tries to reach the desired velocity in the given time
		 */
		TIME_GIVEN,
		/**
		 * Tries to reach the desired velocity until a certain date (UNIX)
		 */
		DATE_GIVEN
	}
	
	public boolean inputBased() { return !timeOption.equals(TimeOption.ASAP); }
	
	public static final int currentVelocityIndex = 0,
							desiredVelocityIndex = 1,
							timeInputIndex = 2;
	
	protected TimeOption timeOption;
	
	/**
	 * Used to signfy when we dont have data on velocity, larger than the speed
	 * of light
	 */
	private final double NULL_VELOCITY = Double.POSITIVE_INFINITY;

	protected double desiredVelocity = NULL_VELOCITY;
	protected double currentVelocity = NULL_VELOCITY;

	/**
	 * The constant that determines how quickly we converge to the desired
	 * velocity. Should be 1 at all times in theory, but could be changed.
	 */
	protected double m_Ka;

	/**
	 * The velocity constant of this actuator. When voltage passed is 0 and the
	 * robot is moving, you can calculate the constant as: <a href=
	 * "https://www.codecogs.com/eqnedit.php?latex=k_u&space;=&space;-&space;\frac{a}{U}"
	 * target="_blank"><img src=
	 * "https://latex.codecogs.com/gif.latex?k_u&space;=&space;&space;\frac{a}{U}"
	 * title="k_u = \frac{a}{U}" /></a> where a is the actuator acceleration
	 * and U the actuator velocity.
	 */
	protected double m_Ku;

	/**
	 * The voltage constant of this actuator. can be calculated as: <a href=
	 * "https://www.codecogs.com/eqnedit.php?latex=k_v&space;=&space;\frac{a&space;&plus;&space;k_u&space;\cdot&space;U}{V}"
	 * target="_blank"><img src=
	 * "https://latex.codecogs.com/gif.latex?k_v&space;=&space;\frac{a&space;-&space;k_u&space;\cdot&space;U}{V}"
	 * title="k_v = \frac{a - k_u \cdot U}{V}" /></a> where a is the actuator
	 * acceleration, U the actuator velocity, Ku is <code>m_Ku</code> and V is
	 * the voltage passed.
	 */
	protected double m_Kv;

	/**
	 * In the calculation of the average time passed, how much importance is put
	 * on previous values compared to new ones. for example<br>
	 * <blockquote> 0 - no importance to past values <br>
	 * 1 - same importace as current calue <br>
	 * 20 - low importance to current values</blockquote>
	 */
	protected int m_pastTimeImportace;

	/**
	 * The last time this object was called
	 */
	protected Date m_lastCalled;

	/**
	 * The time it takes between each call of the controller
	 */
	protected double m_avarageCallTime = -1;

	private static final double DEFAULT_KA = 1;
	private static final int DEFAULT_PAST_IMP = 3;

	/**
	 * 
	 * @param Ku
	 *            {@link VoltageController#m_Ku}
	 * @param Kv
	 *            {@link VoltageController#m_Kv}
	 * @param Ka
	 *            {@link VoltageController#m_Ka}
	 * @param pastTimeImportance
	 *            {@link VoltageController#m_pastTimeImportace}
	 * 
	 * @throws RuntimeException
	 *             when Ku or Kv are equal to 0
	 */
	public VoltageController(String name, TimeOption to, double Ku, double Kv, double Ka, int pastTimeImportance) {
		timeOption = to;
		m_Ka = Ka;
		m_Ku = Ku;
		m_Kv = Kv;
		m_pastTimeImportace = pastTimeImportance;

		if (m_Ku == 0)
			throw new RuntimeException(
					"m_Ku was set to 0, are you implying the velocity has no affect over the acceleration?");
		if (m_Kv == 0)
			throw new RuntimeException(
					"m_Kv was set to 0, are you implying the voltage has no affect over the acceleration?");
	}

	/**
	 * @see VoltageController#VoltageController(double, double, double, int)
	 * @param Ku
	 * @param Kv
	 * @param pastTimeImportance
	 */
	public VoltageController(String name, TimeOption to, double Ku, double Kv, int pastTimeImportance) {
		this(name, to, Ku, Kv, DEFAULT_KA, pastTimeImportance);
	}

	/**
	 * @see VoltageController#VoltageController(double, double, double, int)
	 * @param Ku
	 * @param Kv
	 * @param Ka
	 */
	public VoltageController(String name, TimeOption to, double Ku, double Kv, double Ka) {
		this(name, to, Ku, Kv, Ka, DEFAULT_PAST_IMP);
	}

	/**
	 * @see VoltageController#VoltageController(double, double, double, int)
	 * @param Ku
	 * @param Kv
	 */
	public VoltageController(String name, TimeOption to, double Ku, double Kv) {
		this(name, to, Ku, Kv, DEFAULT_KA, DEFAULT_PAST_IMP);
	}

	/**
	 * @see VoltageController#m_Ka
	 * @param val
	 */
	public void setKa(double val) {
		m_Ka = val;
	}

	/**
	 * @see VoltageController#m_pastTimeImportace
	 * @param val
	 */
	public void setPastTimeImportance(int val) {
		m_pastTimeImportace = val;
	}

	/**
	 * Reset the average call time and the time last called, must be called
	 * before start of program.
	 */
	public void resetTimeInterval() {
		m_avarageCallTime = -1;
		m_lastCalled = new Date();
	}

	/**
	 * 
	 * @param desiredVelocity
	 * @param currentVelocity
	 *            as measured from sensors
	 * @return the voltage that should be supplied to the actuator
	 * @throws NullPointerException
	 *             {@link VoltageController#resetTimeInterval()} wasn't called
	 *             prior to this.
	 */
	public double getOptimalPowerVoltage(double desiredVelocity, double currentVelocity, double due) throws NullPointerException {
		return ((getStartAcceleration(desiredVelocity, currentVelocity, due) + m_Ku * currentVelocity) / m_Kv) / 12.0;
	}

	/**
	 * 
	 * @param desiredVelocity
	 * @param currentVelocity
	 *            as measured from sensors
	 * @return The desired average acceleration
	 */
	protected double getDesiredAcceleration(double desiredVelocity, double currentVelocity, double due) {
		switch (timeOption){
		case ASAP:
			return (desiredVelocity - currentVelocity) * (1 / m_avarageCallTime) * m_Ka;
		case TIME_GIVEN:
			return (desiredVelocity - currentVelocity) * (1 / due) * m_Ka;
		case DATE_GIVEN:
			return (desiredVelocity - currentVelocity) * (1000 / (due - System.currentTimeMillis())) * m_Ka;
		default:
			throw new RuntimeException("Here be dragons");
		}
	}

	/**
	 * 
	 * @param desiredVelocity
	 * @param currentVelocity
	 *            as measured from sensors
	 * @return The desired start acceleration
	 * @throws NullPointerException
	 *             {@link VoltageController#resetTimeInterval()} wasn't called
	 *             prior to this.
	 */
	protected double getStartAcceleration(double desiredVelocity, double currentVelocity, double due) throws NullPointerException {
		Date currDate = new Date();
		long milisecsPasses = currDate.getTime() - m_lastCalled.getTime();
		if (m_avarageCallTime == -1) {
			m_avarageCallTime = (milisecsPasses / 1000.0);
		} else {
			m_avarageCallTime = m_pastTimeImportace * m_avarageCallTime + (milisecsPasses / 1000.0);
			m_avarageCallTime /= m_pastTimeImportace + 1;
		}
		m_lastCalled = currDate;

		double ad = getDesiredAcceleration(desiredVelocity, currentVelocity, due);
		if (m_Ku * m_avarageCallTime == -1)
			return 0;
		return (ad * 3) / (3 + m_Ku * m_avarageCallTime);
	}


}
