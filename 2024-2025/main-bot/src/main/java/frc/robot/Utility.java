package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * The {@code Utility} class is used to store general methods that are used
 * throughout the code.
 * <p>
 * Current methods include:
 * <ul>
 * <li>{@link Utility#printLn}</li>
 * <li>{@link Utility#clamp}</li>
 * <li>{@link Utility#snapToEdge}</li>
 * </ul>
 * Additional methods may be added in the future if and when they are needed.
 * </p>
 */
public final class Utility {

  // Constructor //
  // This is set to private as this is a utility class and
  // should not be instantiated
  // private Utility() {
  //   throw new UnsupportedOperationException(
  //       "This is a utility class and cannot be instantiated");
  // }

  /**
   * The {@code printCounter} variable is used to count how many times the
   * {@code System.out.println} method has been called.
   * This is used in the {@link Utility#printLn} method.
   */
  private static int printCounter = 0;

  /**
   * The {@code printLn} method is used to print a message to the
   * console every 50 times it is called.
   * This is to slow down the messages as it overwhlems the roboRIO
   * and causes the console to lag.
   *
   * @param messageString String to print to the console
   *
   * @see Utility#printCounter
   */
  public static void printLn(final String messageString) {//i dont have any idea what this does but it's here so yay
    if (printCounter == 50) {
      System.out.println(messageString);
      printCounter = 0;
    } else {
      printCounter++;
    }
  }

  /**
   * The {@code log} method is similar to the printLn method.
   * Instead of just printing a message, it will send it through DriverStation.
   * THe values of the log type goes as follows:
   * - 0 -> Info
   * - 1 -> Warn
   * - 3 -> Error
   * @param messageString
   * @param logType
   */
  public static void log(final String messageString, final int logType) {
    if (!(printCounter == 50)) {
      printCounter++;
    }

    if (logType == 1) {
      System.out.println(messageString);
    } else if (logType == 2) {
      DriverStation.reportWarning(messageString, null);
    } else if (logType == 3) {
      DriverStation.reportError(messageString, null);
    }
  }


















  // BELOW ARE THE METHODS THAT ARE NOT USED IN THE CODE (IMPORTS FROM LAST YEAR)

  // /**
  //  * The {@code clamp} method is used to clamp a value between a minimum and
  //  * maximum value.
  //  * <p>
  //  * If the value is less than the minimum, the minimum is returned.
  //  * If the value is greater than the maximum, the maximum is returned.
  //  * If the value is between the minimum and maximum, the value is returned.
  //  * </p>
  //  *
  //  * @param value  Value to clamp
  //  * @param min    Minimum value
  //  * @param max    Maximum value
  //  *
  //  * @return The clamped value
  //  */
  // public static double clamp(final double value, final double min,
  //                            final double max) {
  //   return value < min ? min : value > max ? max : value;
  // }

  // /**
  //  * Snap a value to the minimum or maximum value only if it is within a
  //  * threshold.
  //  * Although similar to the {@link Utility#clamp} method,
  //  * this method is used to snap a value to the minimum or maximum value
  //  * if it iswithin a threshold
  //  *
  //  * @param value      Value to snap
  //  * @param min        Minimum value
  //  * @param max        Maximum value
  //  * @param threshold  Threshold to snap to
  //  *
  //  * @return The snapped value
  //  */
  // public static double snapToEdge(final double value, final double min,
  //                                 final double max, final double threshold) {
  //   return value < -threshold ? min : value > threshold ? max : value;
  // }
}
