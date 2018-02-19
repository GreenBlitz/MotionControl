package gbmotion.base.controller;

import java.util.Comparator;
import java.util.function.Function;

import gbmotion.base.EnvironmentPort;
import gbmotion.base.exceptions.NullToleranceException;
import gbmotion.events.ControllerStoppedEvent;
import gbmotion.events.EventManager;

/**
 * Abstract controller with input and output
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class AbstractController<IN, OUT> implements IController {
	public static final Input NO_INPUT = () -> null;
	public static final NullTolerance NO_TOLERANCE = NullTolerance.INSTANCE;

	/**
	 * Represents a controller state
	 * <p>
	 * {@link AbstractController.State#ENABLED ENABLED} - controller is active
	 * </p>
	 * <p>
	 * {@link AbstractController.State#ENABLED DISABLE} - controller is halted
	 * </p>
	 * <p>
	 * {@link AbstractController.State#ENABLED END} - controller is ended
	 * </p>
	 * 
	 * @author karlo
	 */
	public static enum State {
		/**
		 * Indicates an active controller
		 */
		ENABLED,

		/**
		 * Indicates a stopped controller that might start again
		 */
		DISABLED,

		/**
		 * Indicates an ended controller, which will not restart
		 */
		END
	}

	/**
	 * Current output object
	 */
	private Output<OUT> m_output;

	/**
	 * Original output object
	 */
	private Output<OUT> m_originalOutput;

	/**
	 * Current input object
	 */
	private Input<IN> m_input = NO_INPUT;

	/**
	 * Original input object
	 */
	private Input<IN> m_originalInput = NO_INPUT;

	/**
	 * Current controller destination
	 */
	protected IN m_destination;

	/**
	 * Output constrain
	 */
	protected Function<OUT, OUT> m_outputConstrain;

	/**
	 * Input constrain
	 */
	protected Function<IN, IN> m_inputConstrain;

	/**
	 * Current controller state
	 * 
	 * @see AbstractController.State
	 */
	protected State m_controllerState = State.DISABLED;

	/**
	 * Has the controller been freed
	 */
	protected boolean m_free = false;

	/**
	 * Current tolerance object
	 * 
	 * @see AbstractController.ITolerance
	 */
	protected ITolerance m_tolerance = NO_TOLERANCE;

	protected final Object LOCK = new Object();

	/**
	 * Controller name
	 */
	protected String m_name;

	/**
	 * Portability with {@code DriverStation} and {@code SmartDashboard}
	 */
	protected EnvironmentPort m_environmentPort = EnvironmentPort.DEFAULT;

	/**
	 * Initializes I/O object using the constrains
	 * 
	 * @param in
	 *            Default input
	 * @param out
	 *            Default output
	 * @param inputConstrain
	 *            input constrain
	 * @param outputConstrain
	 *            output constrain
	 */
	protected final void initializeIO(Input<IN> in, Output<OUT> out, Function<IN, IN> inputConstrain,
			Function<OUT, OUT> outputConstrain) {
		m_originalInput = in;
		m_originalOutput = out;

		m_inputConstrain = inputConstrain;
		m_outputConstrain = outputConstrain;

		m_input = () -> inputConstrain.apply(in.recieve());
		m_output = new Output<OUT>() {
			@Override
			public void use(OUT output) {
				out.use(outputConstrain.apply(output));
			}

			@Override
			public OUT noPower() {
				return out.noPower();
			}

			@Override
			public void stop() {
				out.stop();
			}
		};
	}

	/**
	 * Calling
	 * {@link AbstractController#initializeIO(Input, Output, Function, Function)
	 * initializeIO } using identity constrains
	 * 
	 * @param in
	 * @param out
	 */
	private void initializeIO(Input<IN> in, Output<OUT> out) {
		initializeIO(in, out, obj -> obj, obj -> obj);
	}

	/**
	 * Most low-level constructor. a controller won't be able to work after
	 * calling this alone.
	 * 
	 * @param name
	 *            controller name
	 */
	private AbstractController(String name) {
		m_name = name;
	}

	/**
	 * 
	 * @param in
	 * @param out
	 * @param destination
	 * @param name
	 */
	public AbstractController(Input<IN> in, Output<OUT> out, IN destination, String name) {
		this(name);
		initializeIO(in, out);
		m_destination = destination;
	}

	/**
	 * Initializing a controller with no input
	 * 
	 * @param out
	 * @param destination
	 * @param name
	 */
	public AbstractController(Output<OUT> out, IN destination, String name) {
		this(NO_INPUT, out, destination, name);
	}

	/**
	 * 
	 * @param in
	 * @param out
	 * @param name
	 */
	public AbstractController(Input<IN> in, Output<OUT> out, String name) {
		this(name);
		initializeIO(in, out);
	}

	/**
	 * Initializing a controller with no input
	 * 
	 * @param out
	 * @param name
	 */
	public AbstractController(Output<OUT> out, String name) {
		this(NO_INPUT, out, name);
	}

	/**
	 * Starts the controller by setting it's state to
	 * {@link AbstractController.State#ENABLED State.ENABLED}
	 */
	@Override
	public final synchronized void start() {
		System.out.printf("%s #%d has started running\n", m_name, this.hashCode());
		m_controllerState = State.ENABLED;
	}

	/**
	 * Starts the controller by setting it's state to
	 * {@link AbstractController.State#DISABLED State.DISABLED}
	 */
	@Override
	public final synchronized void stop() {
		m_controllerState = State.DISABLED;
	}

	/**
	 * Starts the controller by setting it's state to
	 * {@link AbstractController.State#ENDED State.ENDED}
	 */
	@Override
	public final synchronized void end() {
		EventManager.fireEvent(ControllerStoppedEvent.of(this));
		m_controllerState = State.END;
	}

	/**
	 * Tolerance is the type of tolerance used to specify if the controller is
	 * on target.
	 *
	 * <p>
	 * The various implementations of this class such as PercentageTolerance and
	 * AbsoluteTolerance specify types of tolerance specifications to use.
	 * </p>
	 */
	@FunctionalInterface
	public interface ITolerance {
		public boolean onTarget();
	}

	/**
	 * Tolerance with time limitation until onTarget() will return true
	 * 
	 * @author karlo
	 */
	public abstract class TimedTolerance implements ITolerance {

		private long m_onTargetTime;
		private boolean m_isOnTarget;
		private double m_minTime;

		public TimedTolerance(double minTime) {
			m_minTime = minTime;
		}

		/**
		 * Override this to create the immediate on Target
		 * 
		 * @return is the destination reached at that moment
		 */
		protected abstract boolean onInstantTimeTarget();

		@Override
		public boolean onTarget() {
			if (onInstantTimeTarget())
				if (m_isOnTarget) {
					if (System.currentTimeMillis() - m_onTargetTime > m_minTime) {
						return true;
					}
				} else {
					m_onTargetTime = System.currentTimeMillis();
					m_isOnTarget = true;
				}
			else
				m_isOnTarget = false;
			return false;
		}
	}

	/**
	 * No tolerance- will throw a {@link NullToleranceException} when called
	 * 
	 * @author karlo
	 */
	public static class NullTolerance implements ITolerance {
		public static final NullTolerance INSTANCE = new NullTolerance();

		private NullTolerance() {
		}

		@Override
		public boolean onTarget() {
			throw new NullToleranceException("No tolerance value set when calling onTarget()");
		}
	}

	/**
	 * 
	 * @return current controller state
	 */
	public State getControllerState() {
		return m_controllerState;
	}

	/**
	 * Removes the input constrain's effect
	 */
	public synchronized void resetInputConstrain() {
		m_inputConstrain = obj -> obj;
		m_input = m_originalInput;
	}

	/**
	 * Removes the output constrain's effect
	 */
	public synchronized void resetOutputConstrain() {
		m_outputConstrain = obj -> obj;
		m_output = m_originalOutput;
	}

	/**
	 * Changing the input object such that every input will go throw new new
	 * inputConstrain
	 * 
	 * @param inputConstrain
	 */
	public synchronized void setInputConstrain(Function<IN, IN> inputConstrain) {
		m_inputConstrain = inputConstrain;
		m_input = () -> m_inputConstrain.apply(m_originalInput.recieve());
	}

	/**
	 * Changing the output object such that every output will go throw new new
	 * outputConstrain
	 * 
	 * @param outputConstrain
	 */
	public synchronized void setOutputConstrain(Function<OUT, OUT> outputConstrain) {
		m_outputConstrain = outputConstrain;
		m_output = new Output<OUT>() {

			@Override
			public void use(OUT output) {
				m_originalOutput.use(m_outputConstrain.apply(output));
			}

			@Override
			public OUT noPower() {
				return m_originalOutput.noPower();
			}

			@Override
			public void stop() {
				m_originalOutput.stop();
			}
		};
	}

	/**
	 * Put's a constrain on the input, such that he will always be smaller than
	 * max and larger than min
	 * 
	 * <p>
	 * <b>note:</b> in this method's default implementation, the minimum and
	 * maximum canno't be found once applied
	 * </p>
	 * 
	 * @param min
	 * @param max
	 * @param compare
	 *            Input comparator
	 */
	public synchronized void setInputRange(IN min, IN max, Comparator<IN> compare) {
		Function<IN, IN> inputConstrain = new Function<IN, IN>() {
			@Override
			public IN apply(IN input) {
				if (compare.compare(min, input) >= 0)
					return min;
				if (compare.compare(max, input) <= 0)
					return max;
				return input;
			}
		};
		setInputConstrain(inputConstrain);
	}

	/**
	 * Put's a constrain on the output, such that the used output can't be
	 * smaller than min or larger than max
	 * 
	 * <p>
	 * <b>note:</b> in this method's default implementation, the minimum and
	 * maximum canno't be found once applied
	 * </p>
	 * 
	 * @param min
	 * @param max
	 * @param compare
	 *            Input comparator
	 */
	public synchronized void setOutputRange(OUT min, OUT max, Comparator<OUT> compare) {
		Function<OUT, OUT> outputConstrain = new Function<OUT, OUT>() {
			@Override
			public OUT apply(OUT output) {
				if (compare.compare(min, output) >= 0)
					return min;
				if (compare.compare(max, output) <= 0)
					return max;
				return output;
			}
		};
		setOutputConstrain(outputConstrain);
	}

	public Input<IN> getInputObject() {
		return m_input;
	}

	public Output<OUT> getOutputObject() {
		return m_output;
	}

	public Input<IN> getOriginalInput() {
		return m_originalInput;
	}

	public Output<OUT> getOriginOutput() {
		return m_originalOutput;
	}

	public Function<IN, IN> getInputConstrain() {
		return m_inputConstrain;
	}

	public Function<OUT, OUT> getOutputConstrain() {
		return m_outputConstrain;
	}

	/**
	 * @param dest
	 *            new destination
	 */
	public synchronized void setDestination(IN dest) {
		m_destination = dest;
	}

	public synchronized IN getDestination() {
		return m_destination;
	}

	/**
	 * 
	 * @param tolerance
	 */
	public synchronized void setTolerance(ITolerance tolerance) {
		m_tolerance = tolerance;
	}

	/**
	 * Destroys the controller. <b> DO NOT TRY TO DO ANTHING WITH THE IT
	 * AFTER </b><code>free</code><b> WAS CALLED!!!</b>
	 */
	public void free() {
		System.out.printf("%s object #%d is now freed\n", m_name, this.hashCode());
		synchronized (LOCK) {
			m_controllerState = State.END;
			m_free = true;
			m_output = null;
			m_input = null;
		}
	}

	public final IN getError() {
		return getError(getInput());
	}

	public final IN getError(IN input) {
		return getError(input, m_destination);
	}

	protected IN getInput() {
		return m_input.recieve();
	}

	protected void useOutput(OUT output) {
		m_output.use(output);
	}

	protected void outputStop() {
		m_output.stop();
	}

	protected OUT noPower() {
		return m_output.noPower();
	}

	/**
	 * Changes the environment port- an object which is used as a layer between
	 * a controller and <code>DriverStation</code> and
	 * <code>SmartDashboard</code>
	 * 
	 * @param newPort
	 */
	public void setEnvironmentPort(EnvironmentPort newPort) {
		m_environmentPort = newPort;
	}

	/**
	 * the main function of the controller. will be run every 20 milliseconds
	 * (by default). should move the engines and receive data from the sensors
	 * 
	 * @param input
	 * @return
	 */
	public abstract OUT calculate(IN input);

	/**
	 * Calculates the error between two instances of input.
	 * 
	 * @param input
	 * @param dest
	 * @return error between those two inputs (assuming dest is the destination)
	 */
	public abstract IN getError(IN input, IN dest);
}
