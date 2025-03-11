// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

/*
 * This Is The Input List. Whenever Adding A New Input, Make Sure To Add It To The List Below
 * 
 *  Driver Controller:
 *    A - Brake
 *    B - Point
 *    Dpad Center - climber
 *    Back + Y - SysId Dynamic Forward
 *    Back + X - SysId Dynamic Reverse
 *    Start + Y - SysId Quasistatic Forward
 *    Start + X - SysId Quasistatic Reverse
 *    Left Joystick - Move (Field Orientated)
 *    Right Joystick - Turn
 *    LB - Reset Field Centric Seed
 *    RT - Align with Apriltag
 * 
 *  Auxiliary Controller:
 *    A - Level 1 Elevator + Coral Arm 
 *    B - Level 2 Elevator + Coral Arm
 *    Y - Level 3 Elevator + Coral Arm
 *    X - Level 4 Elevator + Coral Arm
 *    Left Joystick up - Move Elevator up
 *    Left Joystick down - Move Elevator down
 *    Dpad Left - Left Reef Side
 *    Dpad Right - Right Reef Side
 *    LB - Coral Arm Intake
 *    RB - Coral Arm Outtake
 *    DPad Center - Coral Arm Stop
 *    LT - Algae intake
 *    RT - Algae outtake
 *    
 */

package frc.robot;

import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.ctre.phoenix6.swerve.SwerveRequest;
import java.lang.Math;
import frc.robot.Constants.OperatorConstants;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.LimeLightSubsystem;
import frc.robot.subsystems.MapleSimSubsystem;
import frc.robot.subsystems.CoralArmSubsystem.CoralArmLevels;
import frc.robot.subsystems.ElevatorSubsystem.ElevatorPresets;
import frc.robot.subsystems.AlgaeArmSubsystem;
import frc.robot.subsystems.ClimberSubsystem;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.CoralArmSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.subsystems.ElevatorSubsystem.ElevatorPresets;
import org.littletonrobotics.junction.networktables.LoggedDashboardNumber;

/**
 * little secret comment OwO This class is where the bulk of the robot should be
 * declared. Since Command-based is a "declarative" paradigm, very little robot
 * logic should actually be handled in the {@link Robot} periodic methods (other
 * than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
        // The robot's subsystems and commands are defined here...ElevatorConstants
        // private final ElevatorSubsystem m_ElevatorSubsystem = new
        // ElevatorSubsystem();
        private final AlgaeArmSubsystem m_AlgaeArmSubsystem = new AlgaeArmSubsystem();
        // private final CoralArmSubsystem m_CoralArmSubsystem = new
        // CoralArmSubsystem();
        // private final ClimberSubsystem m_ClimberSubsystem = new ClimberSubsystem();

        // Controllers
        private final CommandXboxController m_driverController = new CommandXboxController(
                        OperatorConstants.kDriverControllerPort);
        private final CommandXboxController m_auxillaryController = new CommandXboxController(
                        Constants.OperatorConstants.kAuxiliaryControllerPort);

        // private final SendableChooser<Command> autoChooser;

        DigitalInput climberLimitSwitch = new DigitalInput(Constants.ClimberConstants.kClimberLimitSwitchID);
        /* Swerve drive platform setup */ // From Swerve Project Generator

        // kSpeedAt12Volts Desired Top speed←
        private double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond);
        // 3/4 of a rotation per second max angular velocity
        private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond);

        /* Setting up bindings for necessary control of the swerve drive platform */
        private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric().withDeadband(MaxSpeed * 0.1)
                        .withRotationalDeadband(MaxAngularRate * 0.1) // Add a 10% deadband
                        .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive
                                                                                 // motors
        private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
        private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

        private final Telemetry logger = new Telemetry(MaxSpeed);

        public final CommandSwerveDrivetrain m_DrivetrainSubsystem = TunerConstants.createDrivetrain();
        // // The following subsystems must be created after the drivetrain
        public final LimeLightSubsystem m_LeftReefLightSubsystem = new LimeLightSubsystem(m_DrivetrainSubsystem,
                        Constants.LimelightConstants.kLeftReefLimelightName);
        public final LimeLightSubsystem m_RightReefLimeLightSubsystem = new LimeLightSubsystem(m_DrivetrainSubsystem,
                        Constants.LimelightConstants.kRightReefLimelightName);
        public final LimeLightSubsystem m_IntakeLimeLightSubsystem = new LimeLightSubsystem(m_DrivetrainSubsystem,
                        Constants.LimelightConstants.kIntakeLimelightName);
        // public final MapleSimSubsystem m_MapleSimSubsystem = new
        // MapleSimSubsystem(m_DrivetrainSubsystem);

        // limelight constants
        public boolean usingLeftLimelightForAlignment = true;

        // End of Swerve Drive Platform setup

        /**
         * The container for the robot. Contains subsystems, OI devices, and commands.
         */
        public RobotContainer() {

                // Build an auto chooser. This will use Commands.none() as the default option.
                // autoChooser = AutoBuilder.buildAutoChooser();

                // SmartDashboard.putData("Auto Chooser", autoChooser);

                // Register named commands for use in autonomous routines
                // NamedCommands.registerCommand("Example Command",
                // m_exampleSubsystem.exampleMethodCommand());
                // NamedCommands.registerCommand("MoveElevatorToPresets",
                // m_elevatorSubsystem.commandMoveToPreset(null));

                // Configure the trigger bindings
                configureBindings(Math.random());
        }

        /**
         * Use this method to define your trigger->command mappings. Triggers can be
         * created via the {@link Trigger#Trigger(java.util.function.BooleanSupplier)}
         * constructor with an arbitrary predicate, or via the named factories in
         * {@link edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses
         * for {@link CommandXboxController
         * Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller PS4}
         * controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick
         * Flight joysticks}.
         * 
         * @param random
         */
        private void configureBindings(double random) {

                // ============================ Elevator Subsystem ============================

                // // Actual elevator commands
                // new Trigger(() -> -m_auxillaryController.getLeftY() > 0.1).whileTrue(new
                // RunCommand(() -> {
                // SmartDashboard.putBoolean("Going Up Triggered", true);
                // SmartDashboard.putNumber("Y-value", m_auxillaryController.getLeftY());
                // if (!m_ElevatorSubsystem.isElevatorAtTop()) {
                // m_ElevatorSubsystem.elevatorVoltageMM(
                // Constants.ElevatorConstants.kLeftElevatorEncoderTopValue);
                // } else {
                // m_ElevatorSubsystem.elevatorMaintainPositionMM();
                // }

                // }, m_ElevatorSubsystem)).onFalse(new InstantCommand(() -> {
                // m_ElevatorSubsystem.elevatorMaintainPositionMM();
                // SmartDashboard.putBoolean("Going Up Triggered", false);
                // }));

                // new Trigger(() -> -m_auxillaryController.getLeftY() < -0.1).whileTrue(new
                // RunCommand(() -> {
                // SmartDashboard.putBoolean("Going Down triggered", true);
                // SmartDashboard.putNumber("Y-value", m_auxillaryController.getLeftY());
                // if (!m_ElevatorSubsystem.isElevatorAtBottom()) {
                // m_ElevatorSubsystem.elevatorVoltageMM(
                // Constants.ElevatorConstants.kLeftElevatorEncoderBottomValue);
                // } else {
                // m_ElevatorSubsystem.elevatorMaintainPositionMM();
                // }
                // })).onFalse(new InstantCommand(() -> {
                // m_ElevatorSubsystem.elevatorMaintainPositionMM();
                // SmartDashboard.putBoolean("Going Down triggered", false);
                // }));

                // m_auxillaryController.povCenter().whileTrue(new InstantCommand(() ->
                // m_ElevatorSubsystem.sysIdTest()));

                // // The commands below make the elevator go to certain levels
                // // If a pressed and elevator is not at level 1 currently, go to level 1
                // m_auxillaryController.a().whileTrue(new RunCommand(() -> {
                // m_ElevatorSubsystem.elevatorMoveToPresetMM((ElevatorPresets.Level1));
                // }, m_ElevatorSubsystem));
                // // If b pressed and the method getElevatorState() does not return level 2, go
                // to
                // // level 2
                // m_auxillaryController.b().whileTrue(new RunCommand(() -> {
                // m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.Level2);
                // }, m_ElevatorSubsystem));
                // // If y pressed and the method getElevatorState() doesn't return level 3, go
                // to
                // // level 3
                // m_auxillaryController.y().whileTrue(new RunCommand(() -> {
                // m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.Level3);
                // }));
                // // If x pressed and the elevator is not currently at level 4, go to level 4
                // m_auxillaryController.x().whileTrue(new RunCommand(() -> {
                // m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.Level4);
                // }));

                // ====================== Algae Arm Subsystem ====================== //

                m_auxillaryController.leftTrigger()
                                // makes algae bar intake if LT pressed
                                .whileTrue(new RunCommand(() -> {
                                        m_AlgaeArmSubsystem.setAlgaeArmVoltage(-0.3);
                                        SmartDashboard.putBoolean("CoralArmIntake", true);
                                }, m_AlgaeArmSubsystem))
                                // Interrupts the command, causing motors to SLOWLY SPIN INWARDS (this way
                                // they
                                // can maintain control of the algae)
                                .onFalse(new RunCommand(() -> m_AlgaeArmSubsystem.setAlgaeArmVoltage(-0.1),
                                                m_AlgaeArmSubsystem));

                m_auxillaryController.rightTrigger().whileTrue(new RunCommand(() -> {
                        m_AlgaeArmSubsystem.setAlgaeArmVoltage(0.3);
                        SmartDashboard.putBoolean("CoralArmIntake", false);
                }, m_AlgaeArmSubsystem)).onFalse(new RunCommand(() -> m_AlgaeArmSubsystem.setAlgaeArmVoltage(random),
                                m_AlgaeArmSubsystem));

                // ====================== Climber Subsystem ====================== //
                // Trigger climberTrigger = new Trigger(() -> climberLimitSwitch.get());
                // climberTrigger.onTrue(m_ClimberSubsystem.runOnce(() ->
                // m_ClimberSubsystem.climberSwitchTriggered()));
                // m_auxillaryController.povCenter()
                // .onTrue(m_ClimberSubsystem.runOnce(() ->
                // m_ClimberSubsystem.engageClimber()));

                // ====================== Coral Arm Subsystem ====================== //
                // // makes coral arm go up

                // Motion magic code for Coral Arm!!!

                // m_auxillaryController.leftBumper().whileTrue(new RunCommand(() -> {
                // m_CoralArmSubsystem.intake();
                // }, m_CoralArmSubsystem)).whileFalse(new RunCommand(() -> {
                // m_CoralArmSubsystem.defaultState();
                // }, m_CoralArmSubsystem));

                // m_auxillaryController.rightBumper().whileTrue(new RunCommand(() -> {
                // m_CoralArmSubsystem.outtake();
                // }, m_CoralArmSubsystem)).whileFalse(new RunCommand(() -> {
                // m_CoralArmSubsystem.defaultState();
                // }, m_CoralArmSubsystem));

                // m_auxillaryController.leftStick().whileTrue(
                // new RunCommand(() -> m_CoralArmSubsystem.defaultState(),
                // m_CoralArmSubsystem));

                // m_auxillaryController.rightStick()
                // .whileTrue(new RunCommand(() -> m_CoralArmSubsystem.sysIdTest(),
                // m_CoralArmSubsystem));

                // Normal code !!!!!
                // m_auxillaryController.leftBumper()
                // .whileTrue(m_CoralArmSubsystem.CommandsetCoralArmVoltage(0.2,
                // CoralArmLevels.Up));
                // // makes coral arm go down
                // m_auxillaryController.rightBumper()
                // .whileTrue(m_CoralArmSubsystem.CommandsetCoralArmVoltage(-0.2,
                // CoralArmLevels.Down));
                // // Emergency Stop for Coral Arm (in case it goes past the top or bottom)
                // Note: I
                // // don't know if the onTrue method will only run once, so test it before you
                // use
                // // it
                // m_auxillaryController.povCenter().onTrue(m_CoralArmSubsystem.emergencyStop());
                // // stops coral arm

                // ====================== Drive Subsystem ====================== //
                // Code below is from swerve drive project generator

                // Note that X is defined as forward according to WPILib convention,
                // and Y is defined as to the left according to WPILib convention.
                m_DrivetrainSubsystem.setDefaultCommand(
                                // Drivetrain will execute this command periodically
                                m_DrivetrainSubsystem.applyRequest(() -> drive
                                                // Drive forward with negative Y (forward)
                                                .withVelocityX(-m_driverController.getLeftY() * MaxSpeed)
                                                // Drive left with negative X (left)
                                                .withVelocityY(-m_driverController.getLeftX() * MaxSpeed)
                                                // Drive counterclockwise with negative X (left)
                                                .withRotationalRate(-m_driverController.getRightX() * MaxAngularRate)));
                m_driverController.a().whileTrue(m_DrivetrainSubsystem.applyRequest(() -> brake));
                m_driverController.b().whileTrue(m_DrivetrainSubsystem.applyRequest(() -> point.withModuleDirection(
                                new Rotation2d(-m_driverController.getLeftY(), -m_driverController.getLeftX()))));

                // // limelight override
                // m_driverController.rightTrigger().whileTrue(m_DrivetrainSubsystem.applyRequest(()
                // -> drive
                // // Drive forward with negative Y (forward)
                // .withVelocityX(m_LimeLightSubsystem.limelight_range_proportional())
                // // Drive left with negative X (left)
                // .withVelocityY(-m_driverController.getLeftX() * MaxSpeed)
                // // Drive counterclockwise with negative X (left)
                // .withRotationalRate(m_LimeLightSubsystem.limelight_aim_proportional())));

                // // Run SysId routines when holding back/start and X/Y.
                // // Note that each routine should be run exactly once in a single log.
                // m_driverController.back().and(m_driverController.y())
                // .whileTrue(m_DrivetrainSubsystem.sysIdDynamic(Direction.kForward));
                // m_driverController.back().and(m_driverController.x())
                // .whileTrue(m_DrivetrainSubsystem.sysIdDynamic(Direction.kReverse));
                // m_driverController.start().and(m_driverController.y())
                // .whileTrue(m_DrivetrainSubsystem.sysIdQuasistatic(Direction.kForward));
                // m_driverController.start().and(m_driverController.x())
                // .whileTrue(m_DrivetrainSubsystem.sysIdQuasistatic(Direction.kReverse));

                // // reset the field-centric heading on left bumper press
                // m_driverController.leftBumper()
                // .onTrue(m_DrivetrainSubsystem.runOnce(() ->
                // m_DrivetrainSubsystem.seedFieldCentric()));

                // m_DrivetrainSubsystem.registerTelemetry(logger::telemeterize);

                // ====================== Command Compositions ====================== //

                // // Intake Function
                // m_auxillaryController.leftBumper().onTrue(Commands.sequence(
                // // Align with the intake station
                // new RunCommand(() -> m_DrivetrainSubsystem.applyRequest(() -> drive
                // // Drive forward with negative Y (forward)
                // .withVelocityX(m_IntakeLimeLightSubsystem
                // .limelight_intake_forward_speed())
                // // Drive left with negative X (left)
                // .withVelocityY(-m_driverController.getLeftX() * MaxSpeed)
                // // Drive counterclockwise with negative X (left)
                // .withRotationalRate(m_IntakeLimeLightSubsystem
                // .limelight_aim_proportional()))),
                // // Move elevator to the intake preset
                // new RunCommand(() -> {
                // if (!m_ElevatorSubsystem.isElevatorAtBottom()) {
                // m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.Level1);
                // }
                // }, m_ElevatorSubsystem).until(
                // m_ElevatorSubsystem.isElevatorAtDesiredState(ElevatorPresets.Level1)),
                // // Move the coral arm up
                // new RunCommand(() -> m_CoralArmSubsystem.coralArmIntake(),
                // m_CoralArmSubsystem).until(
                // m_CoralArmSubsystem.isCoralArmAtDesiredState(CoralArmLevels.outtake)),
                // // wait just to confirm everything is going okay
                // Commands.waitSeconds(2.0),
                // new RunCommand(() -> m_CoralArmSubsystem.coralArmOuttake(),
                // m_CoralArmSubsystem)
                // .until(m_CoralArmSubsystem.isCoralInArm())));

                // Note for future self, if I want to make the coral arm go up at the same time
                // as the elevator, I could make it so one command makes the elevator just go up
                // for a second, then another command makes the elevator and the coral arm go up
                // at the same time
                m_auxillaryController.rightStick().onTrue(Commands.sequence(
                                // Align with the reef
                                new RunCommand(() -> {
                                        // Will have to test to make sure it is actually getting value
                                        if (m_auxillaryController.getHID().getPOV() > 225
                                                        && m_auxillaryController.getHID().getPOV() < 315) {
                                                // NOTE!!! Have to use the RIGHT limelight when trying to go for the
                                                // LEFT side of the reef, just cause when the robot is on the left side,
                                                // the right limelight has a better view
                                                m_DrivetrainSubsystem.applyRequest(() -> drive
                                                                // Drive forward with negative Y (forward)
                                                                .withVelocityX(m_RightReefLimeLightSubsystem
                                                                                .limelight_outtake_forward_speed(1))
                                                                // Drive left with negative X (left)
                                                                .withVelocityY(-m_driverController.getLeftX()
                                                                                * MaxSpeed)
                                                                // Drive counterclockwise with negative X (left)
                                                                .withRotationalRate(m_RightReefLimeLightSubsystem
                                                                                .limelight_aim_proportional()));
                                        } else {
                                                m_DrivetrainSubsystem.applyRequest(() -> drive
                                                                // Drive forward with negative Y (forward)
                                                                .withVelocityX(m_LeftReefLightSubsystem
                                                                                .limelight_outtake_forward_speed(1))
                                                                // Drive left with negative X (left)
                                                                .withVelocityY(-m_driverController.getLeftX()
                                                                                * MaxSpeed)
                                                                // Drive counterclockwise with negative X (left)
                                                                .withRotationalRate(m_LeftReefLightSubsystem
                                                                                .limelight_aim_proportional()));
                                        }
                                }).until(() -> {
                                        if (usingLeftLimelightForAlignment) {
                                                return m_LeftReefLightSubsystem.isRobotInDesiredReefPosition(1);
                                        } else {
                                                return m_RightReefLimeLightSubsystem.isRobotInDesiredReefPosition(1);
                                        }
                                })

                // Move elevator to the level 1 preset
                // new RunCommand(() -> {
                // if (!m_ElevatorSubsystem.isElevatorAtBottom()) {
                // m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.Level1);
                // }
                // }, m_ElevatorSubsystem).until(
                // m_ElevatorSubsystem.isElevatorAtDesiredState(ElevatorPresets.Level1)),
                // // Move the coral arm up
                // new RunCommand(() -> m_CoralArmSubsystem.coralArmOuttake(),
                // m_CoralArmSubsystem).until(
                // m_CoralArmSubsystem.isCoralArmAtDesiredState(CoralArmLevels.outtake)),
                // // wait just to confirm everything is going okay
                // Commands.waitSeconds(2.0),
                // new RunCommand(() -> m_CoralArmSubsystem.coralArmOuttake(),
                // m_CoralArmSubsystem)
                // .until(m_CoralArmSubsystem.isCoralInArm())

                ));

                // // Level
                // m_auxillaryController.b()
                // .onTrue(Commands.sequence(
                // m_elevatorSubsystem.commandMoveToPreset(ElevatorPresets.Level2)
                // .until(m_elevatorSubsystem.isElevatorAtDesiredState(ElevatorPresets.Level2)),
                // m_CoralArmSubsystem.CommandsetCoralArmVoltage(0.1, CoralArmLevels.Up)
                // .until(m_CoralArmSubsystem.isCoralArmAtDesiredState(CoralArmLevels.Up)),
                // Commands.waitSeconds(.5), // Allow the driver time to move up to the reef
                // m_CoralArmSubsystem.CommandsetCoralArmVoltage(-0.1, CoralArmLevels.Down)
                // .until(m_CoralArmSubsystem.isCoralArmAtDesiredState(CoralArmLevels.Down)),
                // m_elevatorSubsystem.commandMoveToPreset(ElevatorPresets.Lowest)
                // .until(m_elevatorSubsystem.isElevatorAtDesiredState(ElevatorPresets.Highest))));
                // // Level 3 preset
                // m_auxillaryController.y()
                // .onTrue(Commands.sequence(
                // m_elevatorSubsystem.commandMoveToPreset(ElevatorPresets.Level3)
                // .until(m_elevatorSubsystem.isElevatorAtDesiredState(ElevatorPresets.Level3)),
                // m_CoralArmSubsystem.CommandsetCoralArmVoltage(0.1, CoralArmLevels.Up)
                // .until(m_CoralArmSubsystem.isCoralArmAtDesiredState(CoralArmLevels.Up)),
                // Commands.waitSeconds(.5), // Allow the driver time to move up to the reef
                // m_CoralArmSubsystem.CommandsetCoralArmVoltage(-0.1, CoralArmLevels.Down)
                // .until(m_CoralArmSubsystem.isCoralArmAtDesiredState(CoralArmLevels.Down)),
                // m_elevatorSubsystem.commandMoveToPreset(ElevatorPresets.Lowest)
                // .until(m_elevatorSubsystem.isElevatorAtDesiredState(ElevatorPresets.Highest))));
                // // Level 4 preset
                // m_auxillaryController.x()
                // .onTrue(Commands.sequence(
                // m_elevatorSubsystem.commandMoveToPreset(ElevatorPresets.Level4)
                // .until(m_elevatorSubsystem.isElevatorAtDesiredState(ElevatorPresets.Level4)),
                // m_CoralArmSubsystem.CommandsetCoralArmVoltage(0.1, CoralArmLevels.Up)
                // .until(m_CoralArmSubsystem.isCoralArmAtDesiredState(CoralArmLevels.Up)),
                // Commands.waitSeconds(.5), // Allow the driver time to move up to the reef
                // m_CoralArmSubsystem.CommandsetCoralArmVoltage(-0.1, CoralArmLevels.Down)
                // .until(m_CoralArmSubsystem.isCoralArmAtDesiredState(CoralArmLevels.Down)),
                // m_elevatorSubsystem.commandMoveToPreset(ElevatorPresets.Lowest)
                // .until(m_elevatorSubsystem.isElevatorAtDesiredState(ElevatorPrese
                // ts.Highest))));

        }

        public void debugMethod() {
                // m_ElevatorSubsystem.debuggingMethod();
                // m_CoralArmSubsystem.debuggingMethod();
        }

        /**
         * Use this to pass the autonomous command to the main {@link Robot} class.
         *
         * @return the command to run in autonomous
         */
        // public Command getAutonomousCommand() {
        // return autoChooser.getSelected();
        // }

}
// another why not comment :)
// I do love some good comments :D

// Saw this code in another teams robot and took it, may be interesting to
// implement
// Endgame alert triggers
