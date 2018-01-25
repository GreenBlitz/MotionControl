package base;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Represents a controller which has the basic structure of a loop which calls
 * it's input and output
 */
public abstract class IterativeController<IN, OUT> extends AbstractController<IN, OUT> {
	public static final double DEFAULT_PERIOD = .05;

	protected final double m_period;

	protected Timer m_controllerLoop; // the loop which will calculate the
										// controller

	/**
	 * 
	 * @param in
	 * @param out
	 * @param destination
	 * @param period
	 *            the time interval between each call to
	 *            {@link IterativeController#calculate calculate}
	 * @param name
	 */
	public IterativeController(Input<IN> in, Output<OUT> out, IN destination, double period, String name) {
		super(in, out, destination, name);
		m_period = period;

		m_controllerLoop = new Timer();
		m_controllerLoop.schedule(new IterativeCalculationTask(), 0L, (long) (1000 * period));
	}

	/**
	 * 
	 * @param in
	 * @param out
	 * @param destination
	 * @param name
	 */
	public IterativeController(Input<IN> in, Output<OUT> out, IN destination, String name) {
		this(in, out, destination, DEFAULT_PERIOD, name);
	}

	/**
	 * 
	 * @param out
	 * @param destination
	 * @param name
	 */
	@SuppressWarnings("unchecked")
	public IterativeController(Output<OUT> out, IN destination, String name) {
		this(NO_INPUT, out, destination, name);
	}

	/**
	 * 
	 * @param in
	 * @param out
	 * @param period
	 *            the time interval between each call to
	 *            {@link IterativeController#calculate calculate}
	 * @param name
	 */
	public IterativeController(Input<IN> in, Output<OUT> out, double period, String name) {
		this(in, out, null, period, name);
	}

	/**
	 * 
	 * @param in
	 * @param out
	 * @param name
	 */
	public IterativeController(Input<IN> in, Output<OUT> out, String name) {
		this(in, out, DEFAULT_PERIOD, name);
	}

	/**
	 * 
	 * @param out
	 * @param name
	 */
	@SuppressWarnings("unchecked")
	public IterativeController(Output<OUT> out, String name) {
		this(NO_INPUT, out, name);
	}

	/**
	 * The task which will be run periodically
	 * 
	 * @author karlo
	 */
	protected class IterativeCalculationTask extends TimerTask {
		public IterativeCalculationTask() {
		}

		@Override
		public void run() {
			IterativeController.this.run(m_controllerState, m_destination, m_tolerance, m_environmentPort);
		}
	}

	/**
	 * 
	 * @param controllerState
	 *            current controller state
	 * @param input
	 *            controller input object
	 * @param output
	 *            controller output object
	 * @param destination
	 *            controller destination
	 * @param tolerance
	 *            controller tolerance
	 * @param port
	 *            SmartDashboard and DriverStation replacement
	 */
	public final void run(AbstractController.State controllerState, IN destination, ITolerance tolerance,
			EnvironmentPort port) {
		if (controllerState == State.DISABLED)
			outputStop();

		if (port.isEnabled() && controllerState == State.ENABLED) {
			if (destination == null) {
				System.err.println("WARNING - destination is null");
				return;
			}

			if (tolerance == NO_TOLERANCE) {
				System.err.println("WARNING - tolerance not set");
				return;
			}
			if (!tolerance.onTarget()) {
				Tuple<IN, OUT> IO = act();
				System.out.printf("%s #%d:\n%s\n", m_name, this.hashCode(),
						IterativeController.this.generateActivityDescription(IO._1, IO._2));
			} else {
				controllerState = State.END;
				outputStop();
				System.out.printf("WARNING: %s #%d has finished running\n", m_name, this.hashCode());
			}
		} else {
			free();
		}
	}

	public void free() {
		m_controllerLoop.cancel();
		super.free();
		synchronized (LOCK) {
			m_controllerLoop = null;
		}
	}

	protected Tuple<IN, OUT> act() {
		IN input = getInput();
		OUT output = calculate(input);
		useOutput(output);
		return new Tuple<IN, OUT>(input, output);
	}

	/**
	 * 
	 * @param input
	 * @param output
	 * @return String describing the actions this controller done before and
	 *         after {@link IterativeController#calculate}
	 */
	protected String generateActivityDescription(IN input, OUT output) {
		// Beep Boop! I'm a robot and this is what i just did!
		return String.format("\tLocation: %s\n\tOutput: %s\n", input.toString(), output.toString());
	}
}
