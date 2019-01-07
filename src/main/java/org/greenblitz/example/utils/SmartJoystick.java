package org.greenblitz.example.utils;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;

public class SmartJoystick {
    private int m_leftAxisX = 0;
    private int m_leftAxisY = 1;
    private int m_leftTrigger = 2;
    private int m_rightTrigger = 3;
    private int m_rightAxisX = 4;
    private int m_rightAxisY = 5;

    private boolean m_leftInvertedY;
    private boolean m_leftInvertedX;
    private boolean m_rightInvertedY;
    private boolean m_rightInvertedX;

    private Joystick m_joystick;

    public final JoystickButton A,
            B,
            X,
            Y,
            L1,
            R1,
            START,
            BACK,
            L3,
            R3;

    public class SmartButton {

        private boolean m_lastState = false;

        private JoystickButton m_button;

        private Command while_held;
        private Command when_pressed;
        private Command when_released;

        public void whileHeld(Command command) {
            if (while_held != null && while_held.isRunning())
                while_held.cancel();

            while_held = command;

        }

        public void whenPressed(Command command) {
            if (when_pressed != null && when_pressed.isRunning())
                when_pressed.cancel();

            when_pressed = command;

        }

        public void whenReleased(Command command) {
            if (when_released != null && when_released.isRunning())
                when_released.cancel();

            when_released = command;
        }


        private class SmartTracker extends Command {

            public void execute() {
                if (m_button.get()) {
                    if (while_held != null && !while_held.isRunning()) while_held.start();
                    if (when_pressed != null && !m_lastState) when_pressed.start();
                } else {
                    if (while_held != null && while_held.isRunning()) while_held.cancel();
                    if (when_released != null && m_lastState) when_released.start();
                }
                m_lastState = m_button.get();
            }

            @Override
            protected boolean isFinished() {
                return false;
            }

        }
    }

    public static enum JoystickBinding {
        A,
        B,
        X,
        Y,
        L1,
        R1,
        BACK,
        START,
        L3,
        R3;
    }

    public static enum JoystickAxis {
        LEFT_X,
        LEFT_Y,
        LEFT_TRIGGER,
        RIGHT_TRIGGER,
        RIGHT_X,
        RIGHT_Y;
    }

    public SmartJoystick(int joystick_port) {
        this(new Joystick(joystick_port));
    }

    public SmartJoystick(Joystick stick) {
        m_joystick = stick;
        A = new JoystickButton(m_joystick, 1);
        B = new JoystickButton(m_joystick, 2);
        X = new JoystickButton(m_joystick, 3);
        Y = new JoystickButton(m_joystick, 4);
        L1 = new JoystickButton(m_joystick, 5);
        R1 = new JoystickButton(m_joystick, 6);
        BACK = new JoystickButton(m_joystick, 7);
        START = new JoystickButton(m_joystick, 8);
        L3 = new JoystickButton(m_joystick, 9);
        R3 = new JoystickButton(m_joystick, 10);
    }

    public void setAxisInverted(JoystickAxis axis, boolean inverted) {
        switch (axis) {
            case LEFT_Y:
                m_leftInvertedY = inverted;
                break;
            case LEFT_X:
                m_leftInvertedX = inverted;
                break;
            case RIGHT_Y:
                m_rightInvertedY = inverted;
                break;
            case RIGHT_X:
                m_rightInvertedX = inverted;
                break;
            default:
                break;
        }
    }

    public double getAxisValue(JoystickAxis axis) {
        if (m_joystick == null) return 0;
        switch (axis) {
            case LEFT_Y:
                return (m_leftInvertedY ? -1 : 1) * m_joystick.getRawAxis(m_leftAxisY);
            case LEFT_X:
                return (m_leftInvertedX ? -1 : 1) * m_joystick.getRawAxis(m_leftAxisX);
            case RIGHT_Y:
                return (m_rightInvertedY ? -1 : 1) * m_joystick.getRawAxis(m_rightAxisY);
            case RIGHT_X:
                return (m_rightInvertedX ? -1 : 1) * m_joystick.getRawAxis(m_rightAxisX);
            case LEFT_TRIGGER:
                return m_joystick.getRawAxis(m_leftTrigger);
            case RIGHT_TRIGGER:
                return m_joystick.getRawAxis(m_rightTrigger);
        }


        System.out.println("[SmartJoystick.getAxisValue()]Something went wrong");
        return -1;
    }

    public void bind(Joystick stick) {
        m_joystick = stick;
    }

    public void bind(int port) {
        bind(new Joystick(port));
    }

    public double getRawAxis(int raw_axis) {
        if (m_joystick == null) return 0;
        return m_joystick.getRawAxis(raw_axis);
    }

    public Joystick getRawJoystick() {
        return m_joystick;
    }
}
