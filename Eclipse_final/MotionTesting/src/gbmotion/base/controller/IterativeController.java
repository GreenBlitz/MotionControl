package gbmotion.base.controller;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Timer;
import java.util.TimerTask;

import org.usfirst.frc.team4590.robot.Robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import gbmotion.base.EnvironmentPort;
import gbmotion.util.Tuple;

/**
 * Represents a controller which has the basic structure of a loop which calls
 * it's input and output
 */
public abstract class IterativeController<IN, OUT> extends AbstractController<IN, OUT> {
	public static final double DEFAULT_PERIOD = .025;

	protected final double m_period;

	protected Timer m_controllerLoop;

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
	public class IterativeCalculationTask extends TimerTask {
		public IterativeCalculationTask() {
		}

		@Override
		public void run() {
			IterativeController.this.run(m_controllerState, m_destination, m_tolerance, m_environmentPort);
		}

		public void init() {
			Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(Thread arg0, Throwable arg1) {
					arg1.printStackTrace();
					System.exit(arg1.hashCode());
				}
			});
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
		if (port.isEnabled()) {
			if (controllerState == State.ENABLED) {
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
					Robot.managedPrinter.printf(getClass(), "\n%s #%d:\n%s\n", m_name,
							IterativeController.this.hashCode(),
							IterativeController.this.generateActivityDescription(IO._1, IO._2));
				} else {
					m_controllerState = State.END;
					outputStop();
					Robot.managedPrinter.warnf(getClass(), ": ", "%s #%d has finished running\n", m_name,
							IterativeController.this.hashCode());
				}
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

	/**
	 * Acquires the input, calls {@link IterativeController#calculate(IN)} with
	 * it and uses the output
	 * 
	 * @return {@link Tuple} containing the input and output gained during the
	 *         process
	 */
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
		return String.format("\tinput: %s\n\tOutput: %s\n", input.toString(), output.toString());
	}
}
