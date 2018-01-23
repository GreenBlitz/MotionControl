package base;

import java.util.function.Function;

import base.exceptions.*;

/**
 * Abstract controller with input and output
 */

public abstract class AbstractController<IN, OUT> implements IController {
	public static final Input NO_INPUT = () -> null;
	public static final NullTolerance NO_TOLERANCE = NullTolerance.INSTANCE;

	/**
	 * Represents a controller state
	 *         <p>
	 *         {@link AbstractController.State#ENABLED ENABLED} - controller is
	 *         active
	 *         </p>
	 *         <p>
	 *         {@link AbstractController.State#ENABLED DISABLE} - controller is
	 *         halted
	 *         </p>
	 *         <p>
	 *         {@link AbstractController.State#ENABLED END} - controller is
	 *         ended
	 *         </p>
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
		 * Indicates an ended controller, which will no restart
		 */
		END
	}

	protected Output<OUT> m_output;
	protected Output<OUT> m_originalOutput;
	protected Input<IN> m_input = NO_INPUT;
	protected Input<IN> m_originalInput = NO_INPUT;
	protected IN m_destination;

	protected Function<OUT, OUT> m_outputConstrain;
	protected Function<IN, IN> m_inputConstrain;

	protected State m_controllerState = State.DISABLED;
	protected boolean m_free = false;

	protected ITolerance m_tolerance = NO_TOLERANCE;

	protected final Object LOCK = new Object();

	protected String m_name;

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

		m_originalInput = in;
		m_originalOutput = out;

		m_inputConstrain = inputConstrain;
		m_outputConstrain = outputConstrain;
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
	@SuppressWarnings("unchecked")
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
	@SuppressWarnings("unchecked")
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
	 */
	@FunctionalInterface
	public interface ITolerance {
		public boolean onTarget();
	}

	/**
	 * Tolerance with time limitation until onTarget() will return true
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
		 * @return
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

	public State getControllerState() {
		return m_controllerState;
	}

	/**
	 * 
	 * @param inputConstrain
	 */
	public synchronized void setInputRange(Function<IN, IN> inputConstrain) {
		m_inputConstrain = inputConstrain;
	}

	/**
	 * 
	 * @param outputConstrain
	 */
	public synchronized void setOutputRange(Function<OUT, OUT> outputConstrain) {
		m_outputConstrain = outputConstrain;
	}

	/**
	 * 
	 * @param dest
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
	 * Destroys the controller. <b> DO NOT TRY TO DO ANTHING WITH THE IT AFTER
	 * <code>free</code> WAS CALLED!!!</b>
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
		return getError(m_input.recieve());
	}

	public final IN getError(IN input) {
		return getError(input, m_destination);
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
	 * Initialize any field that you want. Note: don't use the (String,
	 * Controller) constructor, but instead
	 * <code>constructParam(String paramName)</code>
	 * 
	 * @throws NoSuchFieldException
	 */
	public abstract void initParameters() throws NoSuchFieldException;

	/**
	 * Calculates the error between two instances of input.
	 * 
	 * @param input
	 * @param dest
	 * @return error between those two inputs (assuming dest is the destination)
	 */
	public abstract IN getError(IN input, IN dest);
}
