package frc.controllers;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

/**
 * This class is used to create an Xbox controller object.
 * <p>
 * This class uses the {@code XboxController} class from the wpilibj library.
 * </p>
 * <p>
 * This uses a deadband that can be set if needed. This helps control drift.
 * This value can be in a range of 0 to 1. The higher the value, the more the
 * stick has to move to register. This value is set in the constructor and is
 * set alongside the controller ID. As a note: <i> The deadband is only applied
 * to the joystick axes. </i>
 * </p>
 *
 * @see LogitechF310
 * @see PS4
 *
 * @see edu.wpi.first.wpilibj.XboxController
 */
public class Xbox {

    /**
     * The controller object.
     * In this case, a {@code XboxController} object.
     */
    private XboxController controller;

    /**
     * The deadband value.
     */
    private double deadBand;

    /**
     * Constructor for the Xbox class.
     * <p>
     * This constructor is used to create a LogitechF310 object.
     * This takes in the controller ID and the deadband value.
     * </p>
     *
     * @param id            Controller ID (Port)
     * @param deadBandValue Deadband value
     */
    public Xbox(final int id, final double deadBandValue) {
        controller = new XboxController(id);
        deadBand = deadBandValue;
    }

    // Deadband Method //

    // Although this isn't really needed for newer controllers,
    // it's still here just in case one might develop drift

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
     * @see Xbox#getRightX
     * @see Xbox#getRightY
     * @see Xbox#getLeftY
     */
    public double getLeftX() {
        return deadBand(controller.getLeftX());
    }

    /**
     * Method for returning the Y value of the left joystick.
     *
     * @return Y value of the left joystick
     *
     * @see Xbox#getRightX
     * @see Xbox#getRightY
     * @see Xbox#getLeftX
     */
    public double getLeftY() {
        return deadBand(controller.getLeftY());
    }

    /**
     * Method for returning the X value of the right joystick.
     *
     * @return X value of the right joystick
     *
     * @see Xbox#getRightY
     * @see Xbox#getLeftX
     * @see Xbox#getLeftY
     */
    public double getRightX() {
        return deadBand(controller.getRightX());
    }

    /**
     * Method for returning the Y value of the right joystick.
     *
     * @return Y value of the right joystick
     *
     * @see Xbox#getRightX
     * @see Xbox#getLeftX
     * @see Xbox#getLeftY
     */
    public double getRightY() {
        return deadBand(controller.getRightY());
    }

    // Trigger Methods //

    /**
     * Method for returning the value of the left trigger.
     *
     * @return value of the left trigger
     *
     * @see Xbox#getRightTrigger
     */
    public double getLeftTrigger() {
        return controller.getLeftTriggerAxis();
    }

    /**
     * Method for returning the value of the right trigger.
     *
     * @return value of the right trigger
     *
     * @see Xbox#getLeftTrigger
     */
    public double getRightTrigger() {
        return controller.getRightTriggerAxis();
    }

    // Button Methods //

    /**
     * Method for returning the value of the A button.
     *
     * @return value of the A button
     *
     * @see Xbox#getBButton
     * @see Xbox#getXButton
     * @see Xbox#getYButton
     */
    public boolean getAButton() {
        return controller.getAButton();
    }

    /**
     * Method for returning the value of the B button.
     *
     * @return value of the B button
     *
     * @see Xbox#getAButton
     * @see Xbox#getXButton
     * @see Xbox#getYButton
     */
    public boolean getBButton() {
        return controller.getBButton();
    }

    /**
     * Method for returning the value of the X button.
     *
     * @return value of the X button
     *
     * @see Xbox#getAButton
     * @see Xbox#getBButton
     * @see Xbox#getYButton
     */
    public boolean getXButton() {
        return controller.getXButton();
    }

    /**
     * Method for returning the value of the Y button.
     *
     * @return value of the Y button
     *
     * @see Xbox#getAButton
     * @see Xbox#getBButton
     * @see Xbox#getXButton
     */
    public boolean getYButton() {
        return controller.getYButton();
    }

    /**
     * Method for returning the value of the start button.
     *
     * @return value of the start button
     *
     * @see Xbox#getBackButton
     */
    public boolean getStartButton() {
        return controller.getStartButton();
    }

    /**
     * Method for returning the value of the back button.
     *
     * @return value of the back button
     *
     * @see Xbox#getStartButton
     */
    public boolean getBackButton() {
        return controller.getBackButton();
    }

    /**
     * Method for returning the value of the left bumper.
     *
     * @return value of the left bumper
     *
     * @see Xbox#getRightBumper
     */
    public boolean getLeftBumper() {
        return controller.getLeftBumper();
    }

    /**
     * Method for returning the value of the right bumper.
     *
     * @return value of the right bumper
     *
     * @see Xbox#getLeftBumper
     */
    public boolean getRightBumper() {
        return controller.getRightBumper();
    }

    /**
     * Method for returning the value of the left stick button.
     *
     * @return value of the left stick button
     *
     * @see Xbox#getRightStickButton
     */
    public boolean getLeftStickButton() {
        return controller.getLeftStickButton();
    }

    /**
     * Method for returning the value of the right stick button.
     *
     * @return value of the right stick button
     *
     * @see Xbox#getLeftStickButton
     */
    public boolean getRightStickButton() {
        return controller.getRightStickButton();
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
     * @see Xbox.DPad
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

    /**
     * Method for setting the rumble of the controller.
     *
     * @param type  type of rumble to set
     * @param value value to set the rumble to
     *
     * @see edu.wpi.first.wpilibj.GenericHID.RumbleType
     */
    public void setRumble(final RumbleType type, final double value) {
        controller.setRumble(type, value);
    }

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
