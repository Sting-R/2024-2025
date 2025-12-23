package frc;

// import frc.robot.Utility;

/**
* Data used by other classes.
* <p>
* This is used to store data that needs to be accessed by multiple classes.
* </p>
*/
public final class CommonData {

    // Swerve Drive Variables //

    /**
    * desiredTurn is the desired angle of the robot.
    */
    private static double desiredTurn;

    /**
    * forwardSpeed is the desired forward and backward speed of the robot.
    */
    private static double forwardSpeed;

    /**
    * sideSpeed is the desired left and right speed of the robot.
    */
    private static double sideSpeed;

    /**
    * battenDownTheHatches is used to control the defensive stance.
    */
    private static boolean battenDownTheHatches;

    // NavX Variables //

    /**
    * calibrate is used to determine if the NavX should be calibrated.
    */
    private static boolean calibrate;

    // Autonomous Varaibles //

    /**
    * counter is used in the state machine to see what state the robot is in.
    */
    private static int counter;

    private static boolean followPath;

    // private static boolean shuffleBoardPID;

    // /*
    //  * Would like to convert to ENUM at some point with (FL, FR, Bl, BR) - Henry
    //  * Used to determine which turn motor the PID settings will be tuning
    //  */
    // private static String tuningMotor;

    // private static boolean[] tuningMotorArray = {true, false, false, false};

    // Constructor //
    // This is set to private as this is a utility class and
    // should not be instantiated
    private CommonData() {
        throw new UnsupportedOperationException(
            "This is a utility class and cannot be instantiated");
    }

    // Accesor Methods //

    /**
     * Method for returning desiredTurn.
     *s
     * @return desiredTurn
     */
    public static double getDesiredTurn() {
        return desiredTurn;
    }

    /**
     * Method for setting the desiredTurn.
     *
     * @param value  value to set desiredTurn
     */
    public static void setDesiredTurn(final double value) {
        desiredTurn = value;
    }

    /**
     * Method for returning forwardSpeed.
     *
     * @return forwardSpeed
     */
    public static double getForwardSpeed() {
        return forwardSpeed;
    }

    /**
     * Method for setting forwardSpeed.
     *
     * @param value  value to set forwardSpeed
     */
    public static void setForwardSpeed(final double value) {
        forwardSpeed = value;
    }

    /**
     * Method for returning sideSpeed.
     *
     * @return sideSpeed
     */
    public static double getSideSpeed() {
        return sideSpeed;
    }

    /**
     * Method for setting sideSpeed.
     *
     * @param value  value to set sideSpeed
     */
    public static void setSideSpeed(final double value) {
        sideSpeed = value;
    }

    public static void setFollowPath(final boolean value){
        followPath = value;
    }

    public static boolean getFollowPath(){
        return followPath;
    }

    /**
     * Method for returning battenDownTheHatches.
     *
     * @return battenDownTheHatches
     */
    public static boolean getBattenDownTheHatches() {
        return battenDownTheHatches;
    }

    /**
     * Method for setting battenDownTheHatches.
     *
     * @param value  value to set battenDownTheHatches
     */
    public static void setBattenDownTheHatches(final boolean value) {
        battenDownTheHatches = value;
    }

    /**
     * Method for returning calibrate.
     *
     * @return calibrate
     */
    public static boolean getCalibrate() {
        return calibrate;
    }

    /**
     * Method for setting calibrate.
     *
     * @param value  value to set calibrate
     */
    public static void setCalibrate(final boolean value) {
        calibrate = value;
    }

    /**
     * Method for returning counter.
     *
     * @return counter
     */
    public static int getCounter() {
        return counter;
    }

    /**
     * Method for setting counter.
     *
     * @param value  value to set counter
     */
    public static void setCounter(final int value) {
        counter = value;
    }

    /**
     * Method for displaying the PID values
     * 
     */
    // public static boolean getShuffleBoardPID() {
    //     return shuffleBoardPID;
    // }
    /**
     * Motor you are tuning 
     */
    // public static String setTuningMotor(String newTuningMotor) { //used to set turning motor in common data
    //     // If statement to make sure the chosen tuning motor is one of the four available
    //     if (newTuningMotor.equals("FL") || newTuningMotor.equals("FR") || newTuningMotor.equals("BL") || newTuningMotor.equals("BR")){
    //         tuningMotor = newTuningMotor;
    //         return tuningMotor;
    //     } else {
    //         // need to add some debug statement
    //         Utility.printLn("That's not a tuning motor :(");
    //         return tuningMotor;
    //     }
    // }
    // public static String getTuningMotor() {
    //     return tuningMotor;
    // }
    // public static Boolean getTuningMotorArray() {
        
    // }
}
