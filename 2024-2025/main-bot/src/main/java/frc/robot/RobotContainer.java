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
 *    Dpad Left - Left Reef Side
 *    Dpad Right - Right Reef Side
 *    LB - Coral Arm Intake
 *    RB - Elevator and Coral arm default state
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
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.trajectory.PathPlannerTrajectory;
import com.pathplanner.lib.trajectory.PathPlannerTrajectoryState;
import com.pathplanner.lib.util.PathPlannerLogging;
import com.ctre.phoenix6.swerve.SwerveRequest;
import java.lang.Math;
import java.nio.file.Path;
import java.util.jar.Attributes.Name;

import frc.robot.Constants.ElevatorConstants;
import frc.robot.Constants.LimelightConstants;
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
import frc.robot.commands.LockOnAprilTag;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
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
        private final ElevatorSubsystem m_ElevatorSubsystem = new ElevatorSubsystem();
        private final AlgaeArmSubsystem m_AlgaeArmSubsystem = new AlgaeArmSubsystem();
        private final CoralArmSubsystem m_CoralArmSubsystem = new CoralArmSubsystem();
        // private final ClimberSubsystem m_ClimberSubsystem = new ClimberSubsystem();

        // Controllers
        private final CommandXboxController m_driverController = new CommandXboxController(
                        OperatorConstants.kDriverControllerPort);
        private final CommandXboxController m_auxillaryController = new CommandXboxController(
                        Constants.OperatorConstants.kAuxiliaryControllerPort);

        private final SendableChooser<Command> autoChooser;

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
        public final LimeLightSubsystem m_LeftReefLimeLightSubsystem = new LimeLightSubsystem(m_DrivetrainSubsystem,
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

                // Configure the trigger bindings
                configureBindings(Math.random());

                // Build an auto chooser. This will use Commands.none() as the default option.
                autoChooser = AutoBuilder.buildAutoChooser();

                SmartDashboard.putData("Auto Chooser", autoChooser);

                // // Register named commands for use in autonomous routines
                // NamedCommands.registerCommand("[New Command test]",
                // m_AlgaeArmSubsystem.commandAlgaeIntake(m_AlgaeArmSubsystem));
                // NamedCommands.registerCommand("What",
                // m_AlgaeArmSubsystem.commandAlgaeOuttake(m_AlgaeArmSubsystem));

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

                // Actual elevator commands
                // new Trigger(() -> -m_auxillaryController.getLeftY() > 0.1).whileTrue(new
                // RunCommand(() -> {
                // if (!m_ElevatorSubsystem.isElevatorAtTop()) {
                // m_ElevatorSubsystem.elevatorVoltageMM(
                // Constants.ElevatorConstants.kLeftElevatorEncoderTopValue);
                // } else {
                // m_ElevatorSubsystem.elevatorMaintainPositionMM();
                // }

                // }, m_ElevatorSubsystem)).onFalse(new InstantCommand(() -> {
                // m_ElevatorSubsystem.elevatorMaintainPositionMM();
                // }));

                // new Trigger(() -> -m_auxillaryController.getLeftY() < -0.1).whileTrue(new
                // RunCommand(() -> {
                // if (!m_ElevatorSubsystem.isElevatorAtBottom()) {
                // m_ElevatorSubsystem.elevatorVoltageMM(
                // Constants.ElevatorConstants.kLeftElevatorEncoderBottomValue);
                // } else {
                // m_ElevatorSubsystem.elevatorMaintainPositionMM();
                // }
                // })).onFalse(new InstantCommand(() -> {
                // m_ElevatorSubsystem.elevatorMaintainPositionMM();
                // }));

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

                RunCommand algaeIntakeCommand = new RunCommand(() -> {
                        m_AlgaeArmSubsystem.setAlgaeArmVoltage(0.6);
                        SmartDashboard.putBoolean("Algae Arm Intake", true);
                }, m_AlgaeArmSubsystem);

                RunCommand algaeOuttakeCommand = new RunCommand(() -> {
                        m_AlgaeArmSubsystem.setAlgaeArmVoltage(-0.7);
                        SmartDashboard.putBoolean("Algae Arm Outtake", false);
                }, m_AlgaeArmSubsystem);

                m_auxillaryController.leftTrigger()
                                // makes algae bar intake if LT pressed
                                .whileTrue(algaeIntakeCommand)// Interrupts the command, causing motors to SLOWLY SPIN
                                                              // INWARDS
                                .onFalse(new RunCommand(() -> m_AlgaeArmSubsystem.setAlgaeArmVoltage(0),
                                                m_AlgaeArmSubsystem, m_AlgaeArmSubsystem));

                m_auxillaryController.rightTrigger().whileTrue(algaeOuttakeCommand).onFalse(
                                new RunCommand(() -> m_AlgaeArmSubsystem.setAlgaeArmVoltage(0), m_AlgaeArmSubsystem));

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
                //

                // m_auxillaryController.rightStick(2
                // .whileTrue(new RunCommand(() -> m_CoralArmSubsystem.sysIdTest(),
                // m_CoralArmSubsystem));

                // ====================== Drive Subsystem ====================== //
                // Code below is from swerve drive project generator

                // Interrupt command
                InstantCommand interrupt = new InstantCommand(() -> {
                        m_DrivetrainSubsystem.applyRequest(
                                        () -> drive.withVelocityX(0).withVelocityY(0).withRotationalRate(0));
                }, m_DrivetrainSubsystem);

                // Note that X is defined as forward according to WPILib convention,
                // and Y is defined as to the left according to WPILib convention.
                m_DrivetrainSubsystem.setDefaultCommand(m_DrivetrainSubsystem.applyRequest(() -> {
                        final var modifier = m_driverController.rightBumper().getAsBoolean() ? 4
                                        : m_driverController.leftBumper().getAsBoolean() ? 2 : 1;
                        return drive
                                        // Drive forward with negative Y (forward)
                                        .withVelocityX(m_driverController.getLeftY() * MaxSpeed / modifier)
                                        // Drive left with negative X (left)
                                        .withVelocityY(m_driverController.getLeftX() * MaxSpeed / modifier)
                                        // Drive counterclockwise with negative X (left)
                                        .withRotationalRate(
                                                        -m_driverController.getRightX() * MaxAngularRate / modifier);
                }));
                // m_driverController.a().whileTrue(m_DrivetrainSubsystem.applyRequest(() ->
                // brake));
                // Align to left reef side
                // HAVE TO USE RIGHT LIMELIGHT!!!, that will be the side that actually sees the
                // limelight
                m_driverController.x().whileTrue(new LockOnAprilTag(m_DrivetrainSubsystem, m_LeftReefLimeLightSubsystem,
                                0, m_driverController, false, LimelightConstants.desiredLeftLLOutakeXOffsetLvl123));
                // Align to Right reef side
                // HAVE TO USE Left LIMELIGHT!!!, that will be the side that actually sees the
                // limelight
                m_driverController.b()
                                .whileTrue(new LockOnAprilTag(m_DrivetrainSubsystem, m_RightReefLimeLightSubsystem, 0,
                                                m_driverController, false,
                                                LimelightConstants.desiredRightLLOutakeXOffsetLvl123));

                m_driverController.y().onTrue(new InstantCommand(() -> {
                        SmartDashboard.putBoolean("Gyro Reset Occured", true);
                        m_DrivetrainSubsystem.getPigeon2().reset();
                }));
                // m_driverController.b().whileTrue(m_DrivetrainSubsystem.applyRequest(() ->
                // point.withModuleDirection(
                // new Rotation2d(-m_driverController.getLeftY(),
                // -m_driverController.getLeftX()))));

                m_driverController.povCenter().onTrue(interrupt);
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

                // Default state function

                SequentialCommandGroup elevatorDefaultPosition = new SequentialCommandGroup(new RunCommand(() -> {
                        m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.defaultState);
                        m_CoralArmSubsystem.defaultState();
                }, m_ElevatorSubsystem, m_CoralArmSubsystem));
                m_auxillaryController.rightBumper().onTrue(elevatorDefaultPosition);
                NamedCommands.registerCommand("Elevator default position", elevatorDefaultPosition);

                // // Intake Function

                SequentialCommandGroup intakeCommand = new SequentialCommandGroup(
                                // Align with the intake station
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
                                new RunCommand(() -> {
                                        // if (!m_ElevatorSubsystem.isElevatorAtBottom()) {
                                        m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.intake);
                                        // }
                                        m_CoralArmSubsystem.intake();
                                }, m_ElevatorSubsystem).until(() -> {
                                        return m_CoralArmSubsystem.coralGrabbed();
                                }), new RunCommand(() -> {
                                        m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.defaultState);
                                        m_CoralArmSubsystem.defaultState();
                                }, m_ElevatorSubsystem, m_CoralArmSubsystem));
                m_auxillaryController.leftBumper().onTrue(intakeCommand);
                NamedCommands.registerCommand("Intake command", intakeCommand);

                // Note for future self, if I want to make the coral arm go up at the same time
                // as the elevator, I could make it so one command makes the elevator just go up
                // for a second, then another command makes the elevator and the coral arm go up
                // at the same time

                // Knock Algae off

                // SequentialCommandGroup knockAlgaeOffCommand = new SequentialCommandGroup(new
                // RunCommand(() -> {
                // m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.knockAlgaeOff);
                // }, m_AlgaeArmSubsystem, m_ElevatorSubsystem).until(() -> m_ElevatorSubsystem
                // .isElevatorAtDesiredState(ElevatorPresets.knockAlgaeOff).getAsBoolean());
                // new RunCommand(() -> {
                // m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.knockAlgaeOff);
                // }, null));

                SequentialCommandGroup levelOneCommand = new SequentialCommandGroup(
                                // Align with the reef
                                // new RunCommand(() -> {
                                // // Will have to test to make sure it is actually getting value
                                // if (m_auxillaryController.getHID().getPOV() > 225
                                // && m_auxillaryController.getHID().getPOV() < 315) {
                                // // NOTE!!! Have to use the RIGHT limelight when trying to go for the
                                // // LEFT side of the reef, just cause when the robot is on the left side,
                                // // the right limelight has a better view
                                // m_DrivetrainSubsystem.applyRequest(() -> drive
                                // // Drive forward with negative Y (forward)
                                // .withVelocityX(m_RightReefLimeLightSubsystem
                                // .limelight_outtake_forward_speed(1))
                                // // Drive left with negative X (left)
                                // .withVelocityY(-m_driverController.getLeftX()
                                // * MaxSpeed)
                                // // Drive counterclockwise with negative X (left)
                                // .withRotationalRate(m_RightReefLimeLightSubsystem
                                // .limelight_aim_proportional()));
                                // } else {
                                // m_DrivetrainSubsystem.applyRequest(() -> drive
                                // // Drive forward with negative Y (forward)
                                // .withVelocityX(m_LeftReefLightSubsystem
                                // .limelight_outtake_forward_speed(1))
                                // // Drive left with negative X (left)
                                // .withVelocityY(-m_driverController.getLeftX()
                                // * MaxSpeed)
                                // // Drive counterclockwise with negative X (left)
                                // .withRotationalRate(m_LeftReefLightSubsystem
                                // .limelight_aim_proportional()));
                                // }
                                // }).until(() -> {
                                // if (usingLeftLimelightForAlignment) {
                                // return m_LeftReefLightSubsystem.isRobotInDesiredReefPosition(1);
                                // } else {
                                // return m_RightReefLimeLightSubsystem.isRobotInDesiredReefPosition(1);
                                // }
                                // }),

                                // Move elevator to the level 1 preset and coral arm to outtake
                                new RunCommand(() -> {
                                        // if (!m_ElevatorSubsystem.isElevatorAtBottom()) {
                                        m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.Level1);
                                        // }
                                        if (m_ElevatorSubsystem
                                                        .getMotorPosition() > Constants.ElevatorConstants.ElevatorPreset.level1EncoderValue) {
                                                m_CoralArmSubsystem.outtake(CoralArmLevels.lvl1, false);
                                        }

                                }, m_ElevatorSubsystem, m_CoralArmSubsystem).until(() -> m_ElevatorSubsystem
                                                .isElevatorAtDesiredState(ElevatorPresets.Level1).getAsBoolean()
                                                && m_CoralArmSubsystem.isCoralArmAtDesiredState(CoralArmLevels.lvl1)
                                                                .getAsBoolean()),

                                new RunCommand(() -> {
                                        // if (!m_ElevatorSubsystem.isElevatorAtBottom()) {
                                        m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.Level1);
                                        // }
                                        if (m_ElevatorSubsystem
                                                        .getMotorPosition() > Constants.ElevatorConstants.ElevatorPreset.level1EncoderValue) {
                                                m_CoralArmSubsystem.outtake(CoralArmLevels.lvl1, true);
                                        }
                                }, m_ElevatorSubsystem, m_CoralArmSubsystem)

                );
                m_auxillaryController.a().onTrue(levelOneCommand);
                NamedCommands.registerCommand("Level 1 Preset Command", levelOneCommand);

                SequentialCommandGroup levelTwoCommand = new SequentialCommandGroup(
                                // Align with the reef
                                // new RunCommand(() -> {
                                // // Will have to test to make sure it is actually getting value
                                // if (m_auxillaryController.getHID().getPOV() > 225
                                // && m_auxillaryController.getHID().getPOV() < 315) {
                                // // NOTE!!! Have to use the RIGHT limelight when trying to go for the
                                // // LEFT side of the reef, just cause when the robot is on the left side,
                                // // the right limelight has a better view
                                // m_DrivetrainSubsystem.applyRequest(() -> drive
                                // // Drive forward with negative Y (forward)
                                // .withVelocityX(m_RightReefLimeLightSubsystem
                                // .limelight_outtake_forward_speed(1))
                                // // Drive left with negative X (left)
                                // .withVelocityY(-m_driverController.getLeftX()
                                // * MaxSpeed)
                                // // Drive counterclockwise with negative X (left)
                                // .withRotationalRate(m_RightReefLimeLightSubsystem
                                // .limelight_aim_proportional()));
                                // } else {
                                // m_DrivetrainSubsystem.applyRequest(() -> drive
                                // // Drive forward with negative Y (forward)
                                // .withVelocityX(m_LeftReefLightSubsystem
                                // .limelight_outtake_forward_speed(1))
                                // // Drive left with negative X (left)
                                // .withVelocityY(-m_driverController.getLeftX()
                                // * MaxSpeed)
                                // // Drive counterclockwise with negative X (left)
                                // .withRotationalRate(m_LeftReefLightSubsystem
                                // .limelight_aim_proportional()));
                                // }
                                // }).until(() -> {
                                // if (usingLeftLimelightForAlignment) {
                                // return m_LeftReefLightSubsystem.isRobotInDesiredReefPosition(1);
                                // } else {
                                // return m_RightReefLimeLightSubsystem.isRobotInDesiredReefPosition(1);
                                // }
                                // }),

                                // Move elevator to the level 2 preset and coral arm to outtake
                                new RunCommand(() -> {
                                        // if (!m_ElevatorSubsystem.isElevatorAtBottom()) {
                                        m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.Level2);
                                        // }
                                        if (m_ElevatorSubsystem
                                                        .getMotorPosition() > Constants.ElevatorConstants.ElevatorPreset.level1EncoderValue) {
                                                m_CoralArmSubsystem.outtake(CoralArmLevels.lvl2, false);
                                        }

                                }, m_ElevatorSubsystem, m_CoralArmSubsystem).until(() -> (m_ElevatorSubsystem
                                                .isElevatorAtDesiredState(ElevatorPresets.Level2).getAsBoolean()
                                                && m_CoralArmSubsystem.isCoralArmAtDesiredState(CoralArmLevels.lvl1)
                                                                // IMPORTANT NOTE!!! SINCE LVL1 AND LVL2 ARE SO SIMILAR
                                                                // IN POSITION, IT WILL NOT THINK IT IS LEVEL 2 BUT
                                                                // LEVEL 1, SO WE WILL BE USING LEVEL 1 FOR THIS CHECK
                                                                .getAsBoolean())
                                                || (m_ElevatorSubsystem.isElevatorAtDesiredState(ElevatorPresets.Level2)
                                                                .getAsBoolean()
                                                                && m_CoralArmSubsystem
                                                                                .isCoralArmAtDesiredState(
                                                                                                CoralArmLevels.lvl2)
                                                                                .getAsBoolean())),

                                new RunCommand(() -> {
                                        // if (!m_ElevatorSubsystem.isElevatorAtBottom()) {
                                        m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.Level2);
                                        // }
                                        if (m_ElevatorSubsystem
                                                        .getMotorPosition() > Constants.ElevatorConstants.ElevatorPreset.level1EncoderValue) {
                                                m_CoralArmSubsystem.outtake(CoralArmLevels.lvl2, true);
                                        }
                                }, m_ElevatorSubsystem, m_CoralArmSubsystem)

                );
                m_auxillaryController.b().onTrue(Commands.sequence(levelTwoCommand));

                NamedCommands.registerCommand("Level 2 Preset Command", levelTwoCommand);

                // m_auxillaryController.y().and(m_auxillaryController.povRight()).onTrue(Commands.sequence(
                // // Align with the reef
                // m_DrivetrainSubsystem.applyRequest(() -> drive
                // // Drive forward with negative Y (forward)
                // .withVelocityX(m_LeftReefLimeLightSubsystem
                // .limelight_outtake_forward_speed(CoralArmLevels.lvl3,
                // m_driverController))
                // // Drive left with negative X (left)
                // .withVelocityY(m_LeftReefLimeLightSubsystem
                // .limelight_outtake_side_speed(CoralArmLevels.lvl3,
                // false, m_driverController))
                // // Drive counterclockwise with negative X (left)
                // .withRotationalRate(0
                // // m_RightReefLimeLightSubsystem
                // // .limelight_aim_proportional()
                // ))

                // // }
                // .until(() -> {
                // return m_LeftReefLimeLightSubsystem
                // .isRobotInDesiredReefPosition(
                // CoralArmLevels.lvl3, false);

                // })

                // Move elevator to the level 3 preset and coral arm to outtake
                // new RunCommand(() -> {
                // // if (!m_ElevatorSubsystem.isElevatorAtBottom()) {
                // m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.Level3);
                // // }
                // if (m_ElevatorSubsystem
                // .getMotorPosition() >
                // Constants.ElevatorConstants.ElevatorPreset.level1EncoderValue) {
                // m_CoralArmSubsystem.outtake(CoralArmLevels.lvl3, false);
                // }

                // }, m_ElevatorSubsystem, m_CoralArmSubsystem).until(() -> m_ElevatorSubsystem
                // .isElevatorAtDesiredState(ElevatorPresets.Level3).getAsBoolean()
                // && m_CoralArmSubsystem.isCoralArmAtDesiredState(CoralArmLevels.lvl3)
                // // IMPORTANT NOTE!!! SINCE LVL1 AND LVL2 ARE SO SIMILAR
                // // IN POSITION, IT WILL NOT THINK IT IS LEVEL 2 BUT
                // // LEVEL 1, SO WE WILL BE USING LEVEL 1 FOR THIS CHECK
                // .getAsBoolean()),

                // new RunCommand(() -> {
                // if (!m_ElevatorSubsystem.isElevatorAtBottom()) {
                // m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.Level3);
                // // }
                // if (m_ElevatorSubsystem
                // .getMotorPosition() >
                // Constants.ElevatorConstants.ElevatorPreset.level1EncoderValue) {
                // m_CoralArmSubsystem.outtake(CoralArmLevels.lvl3, true);
                // }
                // }, m_ElevatorSubsystem, m_CoralArmSubsystem)

                // ));

                SequentialCommandGroup levelThreeCommand = new SequentialCommandGroup(// // Align with the reef
                                // m_DrivetrainSubsystem.applyRequest(() -> drive
                                // // Drive forward with negative Y (forward)
                                // .withVelocityX(m_RightReefLimeLightSubsystem
                                // .limelight_outtake_forward_speed(CoralArmLevels.lvl3,
                                // m_driverController))
                                // // Drive left with negative X (left)
                                // .withVelocityY(m_RightReefLimeLightSubsystem
                                // .limelight_outtake_side_speed(CoralArmLevels.lvl3, true,
                                // m_driverController))
                                // // Drive counterclockwise with negative X (left)
                                // .withRotationalRate(0
                                // // m_RightReefLimeLightSubsystem
                                // // .limelight_aim_proportional()
                                // ))

                                // // }
                                // .until(() -> {
                                // return m_RightReefLimeLightSubsystem
                                // .isRobotInDesiredReefPosition(
                                // CoralArmLevels.lvl3, true);

                                // })

                                // Move elevator to the level 3 preset and coral arm to outtake
                                new RunCommand(() -> {
                                        // if (!m_ElevatorSubsystem.isElevatorAtBottom()) {
                                        m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.Level3);
                                        // }
                                        if (m_ElevatorSubsystem
                                                        .getMotorPosition() > Constants.ElevatorConstants.ElevatorPreset.level1EncoderValue) {
                                                m_CoralArmSubsystem.outtake(CoralArmLevels.lvl3, false);
                                        }

                                }, m_ElevatorSubsystem, m_CoralArmSubsystem).until(() -> m_ElevatorSubsystem
                                                .isElevatorAtDesiredState(ElevatorPresets.Level3).getAsBoolean()
                                                && m_CoralArmSubsystem.isCoralArmAtDesiredState(CoralArmLevels.lvl3)
                                                                // IMPORTANT NOTE!!! SINCE LVL1 AND LVL2 ARE SO SIMILAR
                                                                // IN POSITION, IT WILL NOT THINK IT IS LEVEL 2 BUT
                                                                // LEVEL 1, SO WE WILL BE USING LEVEL 1 FOR THIS CHECK
                                                                .getAsBoolean()),

                                new RunCommand(() -> {
                                        if (!m_ElevatorSubsystem.isElevatorAtBottom()) {
                                                m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.Level3);
                                        }
                                        if (m_ElevatorSubsystem
                                                        .getMotorPosition() > Constants.ElevatorConstants.ElevatorPreset.level1EncoderValue) {
                                                m_CoralArmSubsystem.outtake(CoralArmLevels.lvl3, true);
                                        }
                                }, m_ElevatorSubsystem, m_CoralArmSubsystem)

                );
                // .and(m_auxillaryController.povLeft())
                m_auxillaryController.y().onTrue(levelThreeCommand);

                NamedCommands.registerCommand("Level 3 Preset Command", levelThreeCommand);

                SequentialCommandGroup levelFourCommand = new SequentialCommandGroup(
                                // Align with the reef
                                // new RunCommand(() -> {
                                // Will have to test to make sure it is actually getting value

                                // NOTE!!! Have to use the RIGHT limelight when trying to go for the
                                // LEFT side of the reef, just cause when the robot is on the left side,
                                // the right limelight has a better view
                                // m_DrivetrainSubsystem.applyRequest(() -> drive
                                // // Drive forward with negative Y (forward)
                                // .withVelocityX(m_RightReefLimeLightSubsystem
                                // .limelight_outtake_forward_speed(CoralArmLevels.lvl4))
                                // // Drive left with negative X (left)
                                // .withVelocityY(m_RightReefLimeLightSubsystem
                                // .limelight_outtake_side_speed(CoralArmLevels.lvl4,
                                // true))
                                // // Drive counterclockwise with negative X (left)
                                // .withRotationalRate(0
                                // // m_RightReefLimeLightSubsystem
                                // // .limelight_aim_proportional()
                                // ))

                                // // }
                                // .until(() -> {
                                // return m_RightReefLimeLightSubsystem
                                // .isRobotInDesiredReefPosition(
                                // CoralArmLevels.lvl4, true);

                                // })

                                // // Move elevator to the level 4 preset and coral arm to outtake
                                new RunCommand(() -> {
                                        // if (!m_ElevatorSubsystem.isElevatorAtBottom()) {
                                        m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.Level4);
                                        // }
                                        if (m_ElevatorSubsystem
                                                        .getMotorPosition() > Constants.ElevatorConstants.ElevatorPreset.level1EncoderValue) {
                                                m_CoralArmSubsystem.outtake(CoralArmLevels.lvl4, false);
                                        }

                                }, m_ElevatorSubsystem, m_CoralArmSubsystem).until(() -> (m_ElevatorSubsystem
                                                .isElevatorAtDesiredState(ElevatorPresets.Level4).getAsBoolean()
                                                && m_CoralArmSubsystem.isCoralArmAtDesiredState(CoralArmLevels.lvl4)
                                                                .getAsBoolean()
                                                && m_auxillaryController.getHID().getLeftStickButton())),

                                new RunCommand(() -> {
                                        m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.Level4);
                                        if (m_ElevatorSubsystem
                                                        .getMotorPosition() > Constants.ElevatorConstants.ElevatorPreset.level4EncoderValue) {
                                                m_CoralArmSubsystem.outtake(CoralArmLevels.lvl4, true);
                                        }
                                }, m_ElevatorSubsystem, m_CoralArmSubsystem));
                // level 4 preset
                m_auxillaryController.x().onTrue(levelFourCommand);

                NamedCommands.registerCommand("Level 4 Preset Command", levelFourCommand);
        }

        public void debugMethod() {
                m_ElevatorSubsystem.debuggingMethod();
                m_CoralArmSubsystem.debuggingMethod();
                m_LeftReefLimeLightSubsystem.debuggingMethod("left");
                m_RightReefLimeLightSubsystem.debuggingMethod("right");

        }

        /**
         * Use this to pass the autonomous command to the main {@link Robot} class.
         *
         * @return the command to run in autonomous
         */
        public Command getAutonomousCommand() {
                SmartDashboard.putString("Auto Chosen????????", "" + autoChooser.getSelected());
                return autoChooser.getSelected();
        }

}
// another why not comment :)
// I do love some good comments :D

// Saw this code in another teams robot and took it, may be interesting to
// implement
// Endgame alert triggers
