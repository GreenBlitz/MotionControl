package base;

import base.exceptions.*;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.function.Supplier;

/**
 *
 * Abstract controller with input and output
 */

// TODO- Java documentation everything in here
// TODO- Alexey do your shit right already I'm sick of it
// TODO- create a new I/O objects limiting the current input receive and output use by existing limits
// TODO- consider changing the way the upper and lower bounds work in accordance with the above to do
// TODO- uncomment the generic bounds when the time will come. AND IT WILL!!!!! (someday)
public abstract class Controller<IN /*extends Comparable<IN>*/, OUT /*extends Comparable<OUT>*/> implements LiveWindowSendable, IController {
    public static final Input NO_INPUT = () -> null;
    public static final NullTolerance NO_TOLERANCE = new NullTolerance();
    
    public static enum State {
    	ENABLED, DISABLED, END
    }

    protected Output<OUT> m_output;
    protected Input<IN>  m_input = NO_INPUT;
    protected IN m_destination;

    protected OUT m_outputLowerBound, m_outputUpperBound;          // out of bounds output will be ignored
    protected IN m_inputLowerBound, m_inputUpperBound;            // out of bounds input will be ignored

    protected State m_controllerState = State.DISABLED;
    protected boolean m_free = false;
    
    protected ITolerance m_tolerance = NO_TOLERANCE;

    protected HashMap<String, Parameter<?>> m_parameters;

    protected final Object LOCK = new Object();

    protected String m_name;
    
    /**
     * The function set() tries to set a parameter and returns whether that set happened or not
     */
    @FunctionalInterface
    public interface ISetParameter {
        void set(String dashboardIndex);
    }


    protected final <P> Parameter<P> constructParam(String name) throws NoSuchFieldException {
        return new Parameter<>(this.getClass().getDeclaredField(name));
    }


    /**
     * This class is used in order to make Controller sendable.
     * The getter will be used by the SmartDashboard in order to display the values,
     * The setter will be used by the program so you could manually change values from the SmartDashboard
     * @param <T> The type of the parameter being saved
     */
    public class Parameter<T> // Top 10 anime ugliest solutions
    {

        protected ISetParameter setter;
        protected Supplier<T> getter;
        protected Field parameter;

        /**
         * Set using a inner variable inside the class from a dashboard value
         * @param dbi the string indicating the dashboard key that maps to the value the function will set
         * @return whether the set happened or not
         */
        public void set(String dbi) {
            if (setter == null)
                System.err.println(String.format("field '%s' is readonly", dbi));
            else
                setter.set(dbi);
        }

        /**
         * @return The value associated to this object
         */
        public T get() { return getter.get(); }

        /**
         * Full constructor
         * @param get the getter function used to retrieve the value
         * @param set the setter function
         */
        public Parameter(Supplier<T> get, ISetParameter set)
        {
            this.getter = get;
            this.setter = set;
        }

        /**
         * Read only constructor
         * @param get the getter function
         */
        public Parameter(Supplier<T> get) {
            this.getter = get;
        }


        /**
         * used by construct param to make a new parameter
         * @param field the parameter
         */
        private Parameter(Field field) {
            this.parameter = field;
            this.parameter.setAccessible(true);
            this.getter = this :: _get;
            this.setter = this :: _set;
        }

        /**
         * The default receive() function
         * @return the value
         */
        @SuppressWarnings("unchecked")
        private T _get() {
            try {
                return (T)parameter.get(Controller.this);
            }
            catch (IllegalAccessException e) { return null; }
        }

        /**
         * The default set function
         * @param key the name of the parameter being changed
         */
        private void _set(String key) {
            if (SmartDashboard.getData(key) == null) return;
            try {
                parameter.set(Controller.this, SmartDashboard.getData(key));
            } catch (IllegalAccessException e) {}
        }

    }    

    public Controller(String name){ 
    	initDashboard();
    	m_name = name;
    }

    public Controller(Input<IN> in, Output<OUT> out, IN destination, String name) {
        this(name);
        m_input = in;
        m_output = out;
        m_destination = destination;
    }

    @SuppressWarnings("unchecked")
    public Controller(Output<OUT> out, IN destination, String name) {
        this(NO_INPUT, out, destination, name);
    }

    public Controller(Input<IN> in, Output<OUT> out, String name) {
        this(name);
        m_input = in;
        m_output = out;
    }

    @SuppressWarnings("unchecked")
    public Controller(Output<OUT> out, String name) {
        this(NO_INPUT, out, name);
    }

    /**
     * Add the parameters to the SmartDashboard
     */
    private void initDashboard() {
        // TODO test with dashboard & robot
        m_parameters = new HashMap<>();
        try {
            m_parameters.put("Output lower bound", this.<OUT>constructParam("m_outputLowerBound"));
            m_parameters.put("Output upper bound", this.<OUT>constructParam("m_outputUpperBound"));
            m_parameters.put("Input lower bound", this.<IN>constructParam("m_inputLowerBound"));
            m_parameters.put("Input upper bound", this.<IN>constructParam("m_inputLowerBound"));
            m_parameters.put("Current error", new Parameter<>(Controller.this :: getError));
            // TODO m_parameters.put("Tolerance", new Parameter<Tolerance>("m_tolerance"));
            m_parameters.put("Input", new Parameter<>(m_input::toString));
            m_parameters.put("Output", new Parameter<>(m_output::toString));
            m_parameters.put("Active", this.<Boolean>constructParam("m_active"));
            m_parameters.put("Name", new Parameter<>(() -> m_name));
            // TODO add parameters as needed
            initParameters();
        } catch (NoSuchFieldException e) {
            System.err.println(String.format("Parameter `%s` is not defined, stack trace:", e.getMessage()));
            e.printStackTrace();
        }
    }

    @Override
    public final synchronized void start() {
    	System.out.printf("%s #%d has started running\n",
    			m_name, this.hashCode());
        m_controllerState = State.ENABLED;
    }

    @Override
    public final synchronized void stop() {
    	m_controllerState = State.DISABLED;
    }
    
    @Override
    public final synchronized void end() {
    	EventManager.fireEvent(ControllerStoppedEvent.of(this));
    	m_controllerState = State.END;
    }

    /**
     * Tolerance is the type of tolerance used to specify if the PID controller is on target.
     *
     * <p>The various implementations of this class such as PercentageTolerance and AbsoluteTolerance
     * specify types of tolerance specifications to use.
     */
    @FunctionalInterface
    public interface ITolerance extends Sendable {
        public boolean onTarget();

        @Override
        default void initTable(ITable subtable) { throw new NotImplementedException(); }

        @Override
        default ITable getTable() { throw new NotImplementedException();  }

        @Override
        default String getSmartDashboardType() { return "Tolerance"; }
    }

    public abstract class Tolerance implements ITolerance, Sendable {

        protected ITable m_table;

        @Override
        public void initTable(ITable subtable) {
            m_table = subtable;
        }

        @Override
        public ITable getTable() {
            return m_table;
        }

        @Override
        public String getSmartDashboardType() {
            return "Tolerance";
        }
    }

    public abstract class TimedTolerance extends Tolerance{

        private long m_onTargetTime;
        private boolean m_isOnTarget;
        private double m_minTime;

        public TimedTolerance(double minTime){
            m_minTime = minTime;
        }

        protected abstract boolean onInstantTimeTarget();

        @Override
        public boolean onTarget() {
            if(onInstantTimeTarget())
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

    public static class NullTolerance implements ITolerance {
        @Override
        public boolean onTarget() {
            throw new NullToleranceException("No tolerance value set when calling onTarget()");
        }
    }

    /**
     * Update the table for this sendable object with the latest values.
     */
    @Override
    public void updateTable() {
    }

    /**
     * Start having this sendable object automatically respond to value changes reflect the value on
     * the table.
     */
    @Override
    public void startLiveWindowMode() {
    }

    /**
     * Stop having this sendable object automatically respond to value changes.
     */
    @Override
    public void stopLiveWindowMode() {
    }

    /**
     * This is the function handling value changes in the table that relates to this controller
     */
    protected ITableListener m_listener = (table, valueChanged, newValue, isNew) -> {
        if (isNew) return;
        m_parameters.get(valueChanged).set(valueChanged);
    };

    protected ITable m_table;

    /**
     * Initializes a table for this {@link Sendable} object.
     *
     * @param subtable The table to put the values in.
     */
    @Override
    public void initTable(ITable subtable) {
        if (this.m_table != null) {
            m_table.removeTableListener(m_listener);
        }
        m_table = subtable;
        if (m_table == null) return;
        for (String key: m_parameters.keySet()) {
            m_table.putValue(key, m_parameters.get(key).get());
        }
        m_table.addTableListener(m_listener, false);
    }

    /**
     * The table that is associated with this {@link Sendable}.
     *
     * @return the table that is currently associated with the {@link Sendable}.
     */
    @Override
    public ITable getTable() {
        return m_table;
    }

    /**
     * The string representation of the named data type that will be used by the smart dashboard for
     * this {@link Sendable}.
     *
     * @return The type of this {@link Sendable}.
     */
    @Override
    public String getSmartDashboardType() {
        return "Controller (general)";
    }

    public State getControllerState() {
        return m_controllerState;
    }

    public synchronized void setInputRange(IN min, IN max) {
        m_inputLowerBound = min;
        m_inputUpperBound = max;
    }

    public synchronized void setOutputRange(OUT min, OUT max) {
        m_outputLowerBound = min;
        m_outputUpperBound = max;
    }

    public synchronized void setDestination(IN dest) {
        m_destination = dest;
    }

    public synchronized IN getDestination() {
        return m_destination;
    }

    public synchronized void setTolerance(ITolerance tolerance) {
        m_tolerance = tolerance;
    }

    public void free() {
    	System.out.printf("%s object #%d is now freed\n",
				this.getClass().getSimpleName(), this.hashCode());
        synchronized (LOCK) {
            m_controllerState = State.END;
            m_free = true;
            m_output = null;
            m_input = null;
        }
    }

    /**
     * the main function of the controller.
     * will be run every 20 milliseconds (by default).
     * should move the engines and receive data from the sensors
     * @param input
     * @return
     */
    public abstract OUT calculate(IN input);
    
    /**
     * Initialize any field that you want.
     * Note: don't use the (String, Controller) constructor, but instead <code>constructParam(String paramName)</code>
     * @throws NoSuchFieldException
     */
    public abstract void initParameters() throws NoSuchFieldException;
    
    public abstract IN getError(IN input);
}
