package frc.controllers;

import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

/**
 * This class is used to create an PS4 controller object.
 * <p>
 * This class uses the {@code PS4Controller} class from the wpilibj library.
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
 * @see Xbox
 *
 * @see edu.wpi.first.wpilibj.PS4Controller
 */
public class PS4 {

    /**
     * The controller object.
     * In this case, a {@code PS4Controller} object.
     *
     * @see edu.wpi.first.wpilibj.PS4Controller
     */
    private PS4Controller controller;

    /**
     * The deadband value.
     */
    private double deadBand;

    /**
     * Constructor for the PS4 class.
     * <p>
     * This constructor is used to create a LogitechF310 object.
     * This takes in the controller ID and the deadband value.
     * </p>
     *
     * @param id            Controller ID (Port)
     * @param deadBandValue Deadband value
     */
    public PS4(final int id, final double deadBandValue) {
        controller = new PS4Controller(id);
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
     * @see PS4#getRightX
     * @see PS4#getRightY
     * @see PS4#getLeftY
     */
    public double getLeftX() {
        return deadBand(controller.getLeftX());
    }

    /**
     * Method for returning the Y value of the left joystick.
     *
     * @return Y value of the left joystick
     *
     * @see PS4#getRightX
     * @see PS4#getRightY
     * @see PS4#getLeftX
     */
    public double getLeftY() {
        return deadBand(controller.getLeftY());
    }

    /**
     * Method for returning the X value of the right joystick.
     *
     * @return X value of the right joystick
     *
     * @see PS4#getLeftX
     * @see PS4#getRightY
     * @see PS4#getLeftY
     */
    public double getRightX() {
        return deadBand(controller.getRightX());
    }

    /**
     * Method for returning the Y value of the right joystick.
     *
     * @return Y value of the right joystick
     *
     * @see PS4#getRightX
     * @see PS4#getLeftX
     * @see PS4#getLeftY
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
     * @see PS4#getRightTrigger
     */
    public double getLeftTrigger() {
        return deadBand(controller.getL2Axis());
    }

    /**
     * Method for returning the value of the right trigger.
     *
     * @return value of the right trigger
     *
     * @see PS4#getLeftTrigger
     */
    public double getRightTrigger() {
        return deadBand(controller.getR2Axis());
    }

    // Button Methods //

    /**
     * Method for returning the value of the X button.
     *
     * @return value of the X button
     *
     * @see PS4#getSquareButton
     * @see PS4#getCircleButton
     * @see PS4#getTriangleButton
     * @see PS4#getShareButton
     * @see PS4#getOptionsButton
     * @see PS4#getPlayStationButton
     * @see PS4#getTouchpadButton
     */
    public boolean getXButton() {
        return controller.getCrossButton();
    }

    /**
     * Method for returning the value of the Square button.
     *
     * @return value of the Square button
     *
     * @see PS4#getXButton
     * @see PS4#getCircleButton
     * @see PS4#getTriangleButton
     * @see PS4#getShareButton
     * @see PS4#getOptionsButton
     * @see PS4#getPlayStationButton
     * @see PS4#getTouchpadButton
     */
    public boolean getSquareButton() {
        return controller.getSquareButton();
    }

    /**
     * Method for returning the value of the Circle button.
     *
     * @return value of the Circle button
     *
     * @see PS4#getXButton
     * @see PS4#getSquareButton
     * @see PS4#getTriangleButton
     * @see PS4#getShareButton
     * @see PS4#getOptionsButton
     * @see PS4#getPlayStationButton
     * @see PS4#getTouchpadButton
     */
    public boolean getCircleButton() {
        return controller.getCircleButton();
    }

    /**
     * Method for returning the value of the Triangle button.
     *
     * @return value of the Triangle button
     *
     * @see PS4#getXButton
     * @see PS4#getSquareButton
     * @see PS4#getCircleButton
     * @see PS4#getShareButton
     * @see PS4#getOptionsButton
     * @see PS4#getPlayStationButton
     * @see PS4#getTouchpadButton
     */
    public boolean getTriangleButton() {
        return controller.getTriangleButton();
    }

    /**
     * Method for returning the value of the Share button.
     *
     * @return value of the Share button
     *
     * @see PS4#getXButton
     * @see PS4#getSquareButton
     * @see PS4#getCircleButton
     * @see PS4#getTriangleButton
     * @see PS4#getOptionsButton
     * @see PS4#getPlayStationButton
     * @see PS4#getTouchpadButton
     */
    public boolean getShareButton() {
        return controller.getShareButton();
    }

    /**
     * Method for returning the value of the Options button.
     *
     * @return value of the Options button
     *
     * @see PS4#getXButton
     * @see PS4#getSquareButton
     * @see PS4#getCircleButton
     * @see PS4#getTriangleButton
     * @see PS4#getShareButton
     * @see PS4#getPlayStationButton
     * @see PS4#getTouchpadButton
     */
    public boolean getOptionsButton() {
        return controller.getOptionsButton();
    }

    /**
     * Method for returning the value of the PlayStation button.
     *
     * @return value of the PlayStation button
     *
     * @see PS4#getXButton
     * @see PS4#getSquareButton
     * @see PS4#getCircleButton
     * @see PS4#getTriangleButton
     * @see PS4#getShareButton
     * @see PS4#getOptionsButton
     * @see PS4#getTouchpadButton
     */
    public boolean getPlayStationButton() {
        return controller.getPSButton();
    }

    /**
     * Method for returning the value of the left bumper.
     *
     * @return value of the left bumper
     *
     * @see PS4#getRightBumper
     */
    public boolean getLeftBumper() {
        return controller.getL1Button();
    }

    /**
     * Method for returning the value of the right bumper.
     *
     * @return value of the right bumper
     *
     * @see PS4#getLeftBumper
     */
    public boolean getRightBumper() {
        return controller.getR1Button();
    }

    /**
     * Method for returning the value of the left joystick button.
     *
     * @return value of the left joystick button
     *
     * @see PS4#getRightJoystickButton
     */
    public boolean getLeftJoystickButton() {
        return controller.getL3Button();
    }

    /**
     * Method for returning the value of the right joystick button.
     *
     * @return value of the right joystick button
     *
     * @see PS4#getLeftJoystickButton
     */
    public boolean getRightJoystickButton() {
        return controller.getR3Button();
    }

    // Touchpad Methods //

    /**
     * Method for returning the value of the Touchpad button.
     *
     * @return value of the Touchpad button
     *
     * @see PS4#getXButton
     * @see PS4#getSquareButton
     * @see PS4#getCircleButton
     * @see PS4#getTriangleButton
     * @see PS4#getShareButton
     * @see PS4#getOptionsButton
     * @see PS4#getPlayStationButton
     */
    public boolean getTouchpadButton() {
        return controller.getTouchpad();
    }

    // There is a 'touchpad' that uses an event loop
    // but for the purpose of this class, it is not needed
    // It might be added in a future version if there becomes a need for it
    // If you have a need for it or have added it yourself,
    // Submit a pull request or submit an issue on the GitHub repository
    // https://github.com/J-The-Fox/FRC-Team-5098

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
     * @see PS4.DPad
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
