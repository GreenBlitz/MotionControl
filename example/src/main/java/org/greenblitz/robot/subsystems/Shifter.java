package org.greenblitz.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.greenblitz.motion.app.Localizer;
import org.greenblitz.robot.RobotMap;
import org.greenblitz.robot.RobotStats;

public class Shifter extends Subsystem {

    private static Shifter instance;
    private DoubleSolenoid m_piston;
    private ShifterState m_currentShift = ShifterState.POWER;

    /**
     * This constructor constructs the piston.
     */
    private Shifter() {
        m_piston = new DoubleSolenoid(RobotMap.Shifter.FORWARD, RobotMap.Shifter.REVERSE);
    }

    /**
     * This function creates a new instance of this class.
     */
    public static void init() {
        instance = new Shifter();
    }

    /**
     * This function returns an instance of the class as long as it isn't null.
     *
     * @return The current instance of the class
     */
    public static Shifter getInstance() {
        if (instance == null)
            init();
        return instance;
    }

    /**
     * This is an enum that works based on the state of piston.
     * POWER - Piston is in a forward state.
     * SPEED - Piston is in a reverse state.
     */
    public enum ShifterState {
        POWER(DoubleSolenoid.Value.kForward),
        SPEED(DoubleSolenoid.Value.kReverse);

        private DoubleSolenoid.Value mValue;

        ShifterState(DoubleSolenoid.Value value) {
            mValue = value;
        }

        /**
         * This function returns the current value of the piston
         *
         * @return The current state of the piston (off/forward/reverse)
         */
        public DoubleSolenoid.Value getValue() {
            return mValue;
        }
    }

    /**
     * This function sets the state of the piston based on the value received.
     *
     * @param state A value based off of the ShifterState enum. This value is then set as the state the piston is in.
     */
    public void setShift(ShifterState state) {
        if (state != m_currentShift) {

            Localizer.getInstance().setSleep(25,
                    Chassis.getInstance().getLeftSpeed(),
                    Chassis.getInstance().getRightSpeed());

            Chassis.getInstance().getLeftEncoder().setTicksPerMeter(state == ShifterState.POWER ? RobotStats.Ragnarok.EncoderTicksPerMeter.LEFT_POWER : RobotStats.Ragnarok.EncoderTicksPerMeter.LEFT_SPEED);
            Chassis.getInstance().getRightEncoder().setTicksPerMeter(state == ShifterState.POWER ? RobotStats.Ragnarok.EncoderTicksPerMeter.RIGHT_POWER : RobotStats.Ragnarok.EncoderTicksPerMeter.RIGHT_SPEED);

//            Localizer.getInstance().resetEncoders(
//                    Chassis.getInstance().getLeftDistance(),
//                    Chassis.getInstance().getRightDistance()
//            );

            m_currentShift = state;
        }
        m_piston.set(state.getValue());
    }

    /**
     * This function returns the current state of the piston through the ShifterState enum.
     *
     * @return The state of the piston through the ShifterState enum
     */
    public ShifterState getCurrentShift() {
        return m_currentShift;
    }

    @Override
    protected void initDefaultCommand() {
//        setDefaultCommand(new AutoChangeShift());
    }

    /**
     * This function updates the information from the subsystem when called by the robot.
     */
    public void update() {
        SmartDashboard.putString("Shifter::Shift", getCurrentShift().name());
        SmartDashboard.putString("Shifter::Command", getCurrentCommandName());
    }
}
