// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

// Imports //
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.CommonData;
import frc.components.IComponent;
import frc.components.SwerveDrive;
import frc.components.autonomous.Autonomous;
import frc.controllers.PS4;
import frc.controllers.Xbox;
import java.io.IOException;

/**
 * The VM is configured to automatically run this class,
 * and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation.
 * If you change the name of this class or
 * the package after creating this project,
 * you must also update the build.gradle file in the
 * project.
 */
public final class Robot extends TimedRobot {

  /**
   * The settings object.
   * This is used to store the settings from the robot_settings.json file.
   */
  private static Settings settings;
  static {
    ObjectMapper mapper = new ObjectMapper();
    try {
      var filepath = Filesystem.getDeployDirectory().toPath().resolve(
          "robot_settings.json");
      settings = mapper.readValue(filepath.toFile(), Settings.class);
    } catch (IOException ex) {
      System.out.println(ex.toString());
    }
  }

  // Initialize the controllers //
  // NOTE: This might be used for a more dynamic way of selecting controllers

  /**
   * The main controller.
   * This is used for the main driver.
   * <p>
   * This is an {@code Xbox} object.
   * </p>
   * <p>
   * <i>Notice: This might be changed in the future.</i>
   * </p>
   */
  private Xbox controller = new Xbox(settings.controllerID, 0.1);

  /**
   * The auxiliary controller.
   * This is used for the auxiliary driver.
   * <p>
   * This is a {@code PS4} object.
   * </p>
   * <p>
   * <i>Notice: This might be changed in the future.</i>
   * </p>
   */
  // private PS4 auxController = new PS4(settings.auxControllerID, 0);

  // Set up the components
  /**
   * The components array.
   * This is used to store all of the components.
   * <p>
   * This is an array of {@code IComponent} objects.
   * </p>
   * <p>
   * <i>Note: More components will be added here.</i>
   * </p>
   */
  private IComponent[] components =
      new IComponent[] {new SwerveDrive(settings.swerveDrive)};

  /**
   * The autonomous object.
   */
  private Autonomous autonomous;

  @Override
  public void robotInit() {
    // Calibrate the gyro before the robot runs to ensure that it is accurate
    CommonData.setCalibrate(true);
    CommonData.setFollowPath(false);
  }

  @Override
  public void robotPeriodic() {

    // For each component in the components array, update it
    // This calls the update method in each of the component classes
    // Since this in the robotPeriodic method,
    // this will apply to all modes (autonomous, teleop, etc.)
    for (var actuator : components) {
      actuator.update();
    }
  }

  @Override
  public void autonomousInit() {
    // autonomous = new Autonomous();

    CommonData.setFollowPath(true);
  }

  @Override
  public void autonomousPeriodic() {
    // Update the autonomous states
    // autonomous.update();
  }

  @Override
  public void teleopInit() {
    CommonData.setFollowPath(false);
  }

  @Override
  public void teleopPeriodic() {

    // Update the controller values //

    // Update values for the Swerve Drive
    CommonData.setCalibrate(controller.getXButton());
    CommonData.setSideSpeed(controller.getLeftX());
    CommonData.setForwardSpeed(controller.getLeftY());
    CommonData.setDesiredTurn(
        Utility.snapToEdge(-controller.getRightX(), -1, 1, 0.9));
    // Utility.UIUpdate();
  }

  @Override
  public void disabledInit() {
    CommonData.setFollowPath(false);

    // "Turn off" all functions of the robot //

    CommonData.setDesiredTurn(0);  // Set the turn speed to 0
    CommonData.setForwardSpeed(0); // Set the drive speed to 0

    // Turn off the rumble on any controllers
    controller.setRumble(RumbleType.kBothRumble, 0);
    // auxController.setRumble(RumbleType.kBothRumble, 0);

    CommonData.setCounter(0); // Set the counter to 0 (used in autonomous)
  }

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {
    CommonData.setFollowPath(true);
  }

  @Override
  public void testPeriodic() {
    // Utility.printLn("Test Periodic running");
    // Utility.UIUpdate();
  }

  @Override
  public void simulationInit() {}

  @Override
  public void simulationPeriodic() {}
}
