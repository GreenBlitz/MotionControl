package base;

import java.util.Timer;
import java.util.TimerTask;

import edu.wpi.first.wpilibj.DriverStation;

/**
 *
 * Represents a controller which has the basic structure of a loop which calls
 * it's input and output
 */

public abstract class IterativeController<IN, OUT> extends AbstractController<IN, OUT> {
	public static final double DEFAULT_PERIOD = .05;

	protected final double m_period;

	protected Timer m_controllerLoop; // the loop which will calculate the
	// controller

	private IterativeController(Input<IN> in, Output<OUT> out, IN destination, double period, String name) {
		super(in, out, destination, name);
		m_period = period;

		m_controllerLoop = new Timer();
		m_controllerLoop.schedule(new IterativeCalculationTask(), 0L, (long) (1000 * period));
	}

	public IterativeController(Input<IN> in, Output<OUT> out, IN destination, String name) {
		this(in, out, destination, DEFAULT_PERIOD, name);
	}

	@SuppressWarnings("unchecked")
	public IterativeController(Output<OUT> out, IN destination, String name) {
		this(NO_INPUT, out, destination, name);
	}

	public IterativeController(Input<IN> in, Output<OUT> out, double period, String name) {
		this(in, out, null, period, name);
	}

	public IterativeController(Input<IN> in, Output<OUT> out, String name) {
		this(in, out, DEFAULT_PERIOD, name);
	}

	@SuppressWarnings("unchecked")
	public IterativeController(Output<OUT> out, String name) {
		this(NO_INPUT, out, name);
	}

	protected class IterativeCalculationTask extends TimerTask {
		public IterativeCalculationTask() {
		}

		@Override
		public void run() {
			if (DriverStation.getInstance().isEnabled()) {
				if (m_controllerState == State.ENABLED) {
					if (m_destination == null) {
						System.err.println("WARNING - destination is null");
						return;
					}

					if (m_tolerance == NO_TOLERANCE) {
						System.err.println("WARNING - tolerance not set");
						return;
					}
					if (!m_tolerance.onTarget()) {
						IN input = m_input.recieve();
						OUT output = calculate(input);
						m_output.use(output);
						System.out.printf(
								"%s #%d:\n%s\n",
								m_name, this.hashCode(),
								IterativeController.this.generateActivityDescription(input, output));
					} else {
						m_controllerState = State.END;
						m_output.stop();
						System.out.printf(
								"WARNING: %s #%d has finished running\n",
								m_name, this.hashCode());
					}
				} else {
					if (m_controllerState == State.END)
						stop();
				}
			} else {
				free();
			}
		}
	}

	public void free() {
		m_controllerLoop.cancel();
		super.free();
		synchronized (LOCK) {
			m_controllerLoop = null;
		}
	}
	
	protected String generateActivityDescription(IN input, OUT output) {
		// Beep Boop! I'm a robot and this is what i just did!
		return String.format("\tLocation: %s\n\tOutput: %s\n", input.toString(), output.toString());
	}
	
}
