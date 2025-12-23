package frc.controllers;

import edu.wpi.first.wpilibj.Joystick;

/**
 * This class is used to create an LogitechF310 controller object.
 * <p>
 * This class uses the {@code Joystick} class from the wpilibj library.
 * </p>
 * <p>
 * This uses a deadband that can be set if needed. This helps control drift.
 * This value can be in a range of 0 to 1. The higher the value, the more the
 * stick has to move to register. This value is set in the constructor and is
 * set alongside the controller ID. As a note: <i> The deadband is only applied
 * to the joystick axes. </i>
 * </p>
 *
 * @see PS4
 * @see Xbox
 *
 * @see edu.wpi.first.wpilibj.Joystick
 */
public class LogitechF310 {

    // Button IDs //

    /**
     * The axis ID for the left joystick x.
     */
    private static final int JOYSTICK_LEFT_X = 0;

    /**
     * The axis ID for the left joystick y.
     */
    private static final int JOYSTICK_LEFT_Y = 1;

    /**
     * The axis ID for the left trigger.
     */
    private static final int TRIGGER_LEFT = 2;

    /**
     * The axis ID for the right trigger.
     */
    private static final int TRIGGER_RIGHT = 3;

    /**
     * The axis ID for the right joystick x.
     */
    private static final int JOYSTICK_RIGHT_X = 4;

    /**
     * The axis ID for the right joystick y.
     */
    private static final int JOYSTICK_RIGHT_Y = 5;

    /**
     * The button ID for the A button.
     */
    private static final int BUTTON_A = 1;

    /**
     * The button ID for the B button.
     */
    private static final int BUTTON_B = 2;

    /**
     * The button ID for the X button.
     */
    private static final int BUTTON_X = 3;

    /**
     * The button ID for the Y button.
     */
    private static final int BUTTON_Y = 4;

    /**
     * The button ID for the left bumper.
     */
    private static final int BUTTON_LEFT_BUMPER = 5;

    /**
     * The button ID for the right bumper.
     */
    private static final int BUTTON_RIGHT_BUMPER = 6;

    /**
     * The button ID for the start button.
     */
    private static final int BUTTON_START = 7;

    /**
     * The button ID for the back button.
     */
    private static final int BUTTON_BACK = 8;

    /**
     * The controller object.
     * In this case, a {@code Joystick} object.
     *
     * @see edu.wpi.first.wpilibj.Joystick
     */
    private Joystick controller;

    /**
     * The deadband value.
     */
    private double deadBand;

    /**
     * Constructor for the LogitechF310 class.
     * <p>
     * This constructor is used to create a LogitechF310 object.
     * This takes in the controller ID and the deadband value.
     * </p>
     *
     * @param id            Controller ID (Port)
     * @param deadBandValue Deadband value
     */
    public LogitechF310(final int id, final double deadBandValue) {
        controller = new Joystick(id);
        deadBand = deadBandValue;
    }

    // Deadband Method //

    /**
     * Method for setting the deadband of the controller on the X and Y axes.
     * <p>
     * This sets a threshold the joystick must pass before registering a value.
     * This value can be in a range of 0 to 1. The higher the value,
     * the more the joystick has to move to register.
     * </p>
     *
     * @return Deadbanded value
     *
     * @param value Value to be deadbanded
     */
    private double deadBand(final double value) {
        return Math.abs(value) < deadBand ? 0 : value;
    }

    // Joystick Methods //

    /**
     * Method for returning the X value of the left joystick.
     *
     * @return X value of the left joystick
     *
     * @see LogitechF310#getRightX
     * @see LogitechF310#getRightY
     * @see LogitechF310#getLeftY
     */
    public double getLeftX() {
        return deadBand(controller.getRawAxis(JOYSTICK_LEFT_X));
    }

    /**
     * Method for returning the Y value of the left joystick.
     *
     * @return Y value of the left joystick
     *
     * @see LogitechF310#getRightX
     * @see LogitechF310#getRightY
     * @see LogitechF310#getLeftX
     */
    public double getLeftY() {
        return deadBand(controller.getRawAxis(JOYSTICK_LEFT_Y));
    }

    /**
     * Method for returning the X value of the right joystick.
     *
     * @return X value of the right joystick
     *
     * @see LogitechF310#getRightY
     * @see LogitechF310#getLeftX
     * @see LogitechF310#getLeftY
     */
    public double getRightX() {
        return deadBand(controller.getRawAxis(JOYSTICK_RIGHT_X));
    }

    /**
     * Method for returning the Y value of the right joystick.
     *
     * @return Y value of the right joystick
     *
     * @see LogitechF310#getRightX
     * @see LogitechF310#getLeftX
     * @see LogitechF310#getLeftY
     */
    public double getRightY() {
        return deadBand(controller.getRawAxis(JOYSTICK_RIGHT_Y));
    }

    // Trigger Methods //

    /**
     * Method for returning the value of the left trigger.
     *
     * @return Value of the left trigger
     *
     * @see LogitechF310#getRightTrigger
     */
    public double getLeftTrigger() {
        return deadBand(controller.getRawAxis(TRIGGER_LEFT));
    }

    /**
     * Method for returning the value of the right trigger.
     *
     * @return Value of the right trigger
     *
     * @see LogitechF310#getLeftTrigger
     */
    public double getRightTrigger() {
        return deadBand(controller.getRawAxis(TRIGGER_RIGHT));
    }

    // Button Methods //

    /**
     * Method for returning the value of the A button.
     *
     * @return Value of the A button
     *
     * @see LogitechF310#getBButton
     * @see LogitechF310#getXButton
     * @see LogitechF310#getYButton
     */
    public boolean getAButton() {
        return controller.getRawButton(BUTTON_A);
    }

    /**
     * Method for returning the value of the B button.
     *
     * @return Value of the B button
     *
     * @see LogitechF310#getAButton
     * @see LogitechF310#getXButton
     * @see LogitechF310#getYButton
     */
    public boolean getBButton() {
        return controller.getRawButton(BUTTON_B);
    }

    /**
     * Method for returning the value of the X button.
     *
     * @return Value of the X button
     *
     * @see LogitechF310#getAButton
     * @see LogitechF310#getBButton
     * @see LogitechF310#getYButton
     */
    public boolean getXButton() {
        return controller.getRawButton(BUTTON_X);
    }

    /**
     * Method for returning the value of the Y button.
     *
     * @return Value of the Y button
     *
     * @see LogitechF310#getAButton
     * @see LogitechF310#getBButton
     * @see LogitechF310#getXButton
     */
    public boolean getYButton() {
        return controller.getRawButton(BUTTON_Y);
    }

    /**
     * Method for returning the value of the back button.
     *
     * @return Value of the back button
     *
     * @see LogitechF310#getStartButton
     */
    public boolean getBackButton() {
        return controller.getRawButton(BUTTON_START);
    }

    /**
     * Method for returning the value of the start button.
     *
     * @return Value of the start button
     *
     * @see LogitechF310#getBackButton
     */
    public boolean getStartButton() {
        return controller.getRawButton(BUTTON_BACK);
    }

    /**
     * Method for returning the value of the left bumper.
     *
     * @return Value of the left bumper
     *
     * @see LogitechF310#getRightBumper
     */
    public boolean getLeftBumper() {
        return controller.getRawButton(BUTTON_LEFT_BUMPER);
    }

    /**
     * Method for returning the value of the right bumper.
     *
     * @return Value of the right bumper
     *
     * @see LogitechF310#getLeftBumper
     */
    public boolean getRightBumper() {
        return controller.getRawButton(BUTTON_RIGHT_BUMPER);
    }

    // D-Pad Methods //

    public enum DPad {
        /**
         * Up on the D-Pad.
         */
        up,

        /**
         * Down on the D-Pad.
         */
        down,

        /**
         * Right on the D-Pad.
         */
        left,

        /**
         * Left on the D-Pad.
         */
        right,

        /**
         * Up-Right on the D-Pad.
         */
        upright,

        /**
         * Up-Left on the D-Pad.
         */
        upleft,

        /**
         * Down-Right on the D-Pad.
         */
        downright,

        /**
         * Down-Left on the D-Pad.
         */
        downleft,

        /**
         * No direction on the D-Pad.
         */
        none
    }

    /**
     * Upright D-Pad POV value.
     */
    private static final int DPAD_UPRIGHT = 45;

    /**
     * Right D-Pad POV value.
     */
    private static final int DPAD_RIGHT = 90;

    /**
     * Downright D-Pad POV value.
     */
    private static final int DPAD_DOWNRIGHT = 135;

    /**
     * Down POV D-Pad value.
     */
    private static final int DPAD_DOWN = 180;

    /**
     * Downleft D-Pad POV value.
     */
    private static final int DPAD_DOWNLEFT = 225;

    /**
     * Left D-Pad POV value.
     */
    private static final int DPAD_LEFT = 270;

    /**
     * Upleft D-Pad POV value.
     */
    private static final int DPAD_UPLEFT = 315;

    /**
     * Up D-Pad POV value.
     */
    private static final int DPAD_UP = 360;

    /**
     * Method for returning the value of the D-Pad.
     *
     * @return value of the D-Pad
     *
     * @see LogitechF310.DPad
     */
    public DPad getDPad() {
        switch (controller.getPOV()) {
            case 0:
                return DPad.up;
            case DPAD_UPRIGHT:
                return DPad.upright;
            case DPAD_RIGHT:
                return DPad.right;
            case DPAD_DOWNRIGHT:
                return DPad.downright;
            case DPAD_DOWN:
                return DPad.down;
            case DPAD_DOWNLEFT:
                return DPad.downleft;
            case DPAD_LEFT:
                return DPad.left;
            case DPAD_UPLEFT:
                return DPad.upleft;
            case DPAD_UP:
                return DPad.up;
            default:
                return DPad.none;
        }
    }

    // Rumble Methods //

    // This controller does not have rumble functionality :(

    // General Methods //

    /**
     * Method for checking if the controller is connected.
     *
     * @return true if the controller is connected, false if not
     */
    public boolean isConnected() {
        return (controller.isConnected());
    }

    /**
     * Method for getting the name of the controller.
     *
     * @return name of the controller
     */
    public String getName() {
        return controller.getName();
    }
}
