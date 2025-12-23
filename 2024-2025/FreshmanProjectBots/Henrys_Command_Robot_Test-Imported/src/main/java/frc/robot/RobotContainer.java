// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Constants.OperatorConstants;
import frc.robot.Constants.DriveConstants;
import frc.robot.commands.Autos;
import frc.robot.commands.ExampleCommand;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.ExampleSubsystem;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.controllers.Xbox;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  // private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem(OperatorConstants.motorId);
  private final DriveSubsystem m_driveSubsystem = new DriveSubsystem(DriveConstants.kFLMotorId, DriveConstants.kFRMotorId, DriveConstants.kBLMotorId, DriveConstants.kBRMotorId);

  // Replace with CommandPS4Controller or CommandJoystick if needed
  private final CommandXboxController m_driverController =
      new CommandXboxController(OperatorConstants.kDriverControllerPort);

  // Creation of normal controller
  // private final XboxController normalXboxController = new XboxController(0);

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the trigger bindings
    configureBindings();
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link
   * CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {
    // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
    // new Trigger(m_exampleSubsystem::exampleCondition)
    //     .onTrue(new ExampleCommand(m_exampleSubsystem));
    System.out.println("Bindings Configured ********************************************");

    // Controls using the command based xbox controller
    // m_driverController.leftTrigger().whileTrue(m_driveSubsystem.setMotorVoltageCommand(m_driverController.getLeftX(), m_driverController.getRawAxis(3), m_driverController.getRawAxis(4)));
    // m_driverController.rightTrigger().whileTrue(m_driveSubsystem.setMotorVoltageCommand(m_driverController.getRawAxis(2), m_driverController.getRawAxis(3), m_driverController.getRawAxis(4)));
    // m_driverController.rightStick().whileTrue(m_driveSubsystem.setMotorVoltageCommand(m_driverController.getRawAxis(2), m_driverController.getRawAxis(3), m_driverController.getRawAxis(4)));
    m_driveSubsystem.setDefaultCommand(
      new RunCommand(
        () -> 
          m_driveSubsystem.setMotorVoltage(
            m_driverController.getLeftTriggerAxis(), 
            m_driverController.getRightTriggerAxis(), 
            m_driverController.getRightX()),
        m_driveSubsystem));

    // Controls using the normal xbox controller
    // Trigger leftTriggerActivated = new JoystickButton(normalXboxController, XboxController.Axis.kLeftTrigger.value);
    // leftTriggerActivated.whileTrue(m_driveSubsystem.setMotorVoltageCommand(normalXboxController.getRawAxis(2), normalXboxController.getRawAxis(3), normalXboxController.getRawAxis(4)));


    // Schedule `exampleMethodCommand` when the Xbox controller's B button is pressed,
    // cancelling on release.
    // m_driverController.b().whileTrue(m_exampleSubsystem.exampleMethodCommand());
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  // public Command getAutonomousCommand() {
  //   // An example command will be run in autonomous
  //   return Autos.exampleAuto(m_exampleSubsystem);
  // }
}
