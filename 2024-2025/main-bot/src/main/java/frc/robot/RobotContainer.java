// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.Subsystem;
// import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
// import frc.robot.subsystems.TestSubsystem;
// import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.ElevatorSubsystem.ElevatorPreset;

// import edu.wpi.first.wpilibj2.


public class RobotContainer {
  // Controllers
  CommandXboxController driveController = new CommandXboxController(0);
  CommandXboxController auxController = new CommandXboxController(1);

  //Subsystems
  // TestSubsystem testSubsystem =   new TestSubsystem();
  ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(10, 11);

  // Variables
  int count = 0;
  
  // constructor
  public RobotContainer() {
    configureBindings();
  }

  private void configureBindings() {

    // auxController.leftTrigger().whileTrue(elevatorSubsystem.commandVoltage(0.1)).onFalse(elevatorSubsystem.commandVoltage(0.0));
    // auxController.rightTrigger().whileTrue(elevatorSubsystem.commandVoltage(-0.1)).onFalse(elevatorSubsystem.commandVoltage(0.0));

    auxController.leftTrigger().onTrue(new RunCommand(() -> elevatorSubsystem.setMotorElevatorSpeed(0.1) , elevatorSubsystem));
    auxController.rightTrigger().onTrue(new RunCommand(() -> elevatorSubsystem.setMotorElevatorSpeed(-0.1) , elevatorSubsystem));

    auxController.a().onTrue(new RunCommand(() -> elevatorSubsystem.moveElevatorToPreset(ElevatorPreset.Level1), elevatorSubsystem));
    auxController.b().onTrue(new RunCommand(() -> elevatorSubsystem.moveElevatorToPreset(ElevatorPreset.Level2), elevatorSubsystem));
    auxController.x().onTrue(new RunCommand(() -> elevatorSubsystem.moveElevatorToPreset(ElevatorPreset.Level3), elevatorSubsystem));
    auxController.y().onTrue(new RunCommand(() -> elevatorSubsystem.moveElevatorToPreset(ElevatorPreset.Level4), elevatorSubsystem));





















    // Test Commands

    /* 
    controller.a().onTrue(testSubsystem.testCommand());

    controller.b().onTrue(new RunCommand(() -> testSubsystem.testMethod1(), testSubsystem));

    controller.x().onTrue(Commands.startEnd(
      () -> testSubsystem.testMethod2(), 
      () -> testSubsystem.testMethod3(),
      testSubsystem));

    controller.y().onTrue(
        new FunctionalCommand(
          () -> testSubsystem.testMethod0(), // prints something once it runs
          () -> count++, //increments count variable
          interrupted -> testSubsystem.testMethod3(),
          () -> count >= 10, 
          testSubsystem)
    );
  */

  }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command configured");


  }
}
