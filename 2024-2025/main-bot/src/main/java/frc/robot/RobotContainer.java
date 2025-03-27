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

import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecond;

import java.util.List;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructArrayPublisher;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.LimelightConstants;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.drive_commands.AltMoveToAprilTagPosition;
import frc.robot.commands.drive_commands.LockOnAprilTag;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.CoralArmSubsystem;
import frc.robot.subsystems.CoralArmSubsystem.CoralArmLevels;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.ElevatorSubsystem.ElevatorPresets;
import frc.robot.subsystems.ClimberSubsystem;
import frc.robot.subsystems.LimeLightSubsystem;
import frc.robot.commands.preset_commands.moveToPreset;
import frc.robot.commands.preset_commands.intakePreset;
import frc.robot.commands.preset_commands.defaultState;
import frc.robot.commands.drive_commands.MoveToAprilTagPosition;
import frc.robot.commands.preset_commands.knockAlgaeOff;
import frc.robot.commands.drive_commands.DriveToTargetOffset;

/**
 * little secret comment OwO This class is where the bulk of the robot should be
 * declared. Since Command-based is a "declarative" paradigm, very little robot
 * logic should actually be handled in the {@link Robot} periodic methods (other
 * than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
        public record Subsystems(CommandSwerveDrivetrain drivetrain, CoralArmSubsystem coralArmSubsystem,
                        ElevatorSubsystem elevatorSubsystem, LimeLightSubsystem leftReefLL,
                        LimeLightSubsystem rightReefLL) {
        }

        // The robot's subsystems and commands are defined here...ElevatorConstants
        private final ElevatorSubsystem m_ElevatorSubsystem = new ElevatorSubsystem();
        // private final AlgaeArmSubsystem m_AlgaeArmSubsystem = new
        // AlgaeArmSubsystem();
        private final CoralArmSubsystem m_CoralArmSubsystem = new CoralArmSubsystem();
        private final ClimberSubsystem m_ClimberSubsystem = new ClimberSubsystem();

        // Controllers
        private final CommandXboxController m_driverController = new CommandXboxController(
                        OperatorConstants.kDriverControllerPort);
        private final CommandXboxController m_auxillaryController = new CommandXboxController(
                        Constants.OperatorConstants.kAuxiliaryControllerPort);

        private final SendableChooser<Command> autoChooser;

        /* Swerve drive platform setup */ // From Swerve Project Generator

        StructArrayPublisher<SwerveModuleState> publisher = NetworkTableInstance.getDefault()
                        .getStructArrayTopic("MyStates", SwerveModuleState.struct).publish();

        // kSpeedAt12Volts Desired Top speed←
        private double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond);
        // 3/4 of a rotation per second max angular velocity
        private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond);

        /* Setting up bindings for necessary control of the swerve drive platform */
        private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric().withDeadband(MaxSpeed * 0.1)
                        .withRotationalDeadband(MaxAngularRate * 0.1) // Add a 10% deadband
                        .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive
        private final Telemetry logger = new Telemetry(MaxSpeed);

        public final CommandSwerveDrivetrain m_DrivetrainSubsystem = TunerConstants.createDrivetrain();
        // // The following subsystems must be created after the drivetrain
        public final LimeLightSubsystem m_LeftReefLimeLightSubsystem = new LimeLightSubsystem(m_DrivetrainSubsystem,
                        Constants.LimelightConstants.kLeftReefLimelightName);
        public final LimeLightSubsystem m_RightReefLimeLightSubsystem = new LimeLightSubsystem(m_DrivetrainSubsystem,
                        Constants.LimelightConstants.kRightReefLimelightName);
        // public final LimeLightSubsystem m_IntakeLimeLightSubsystem = new
        // LimeLightSubsystem(m_DrivetrainSubsystem,
        // Constants.LimelightConstants.kIntakeLimelightName);
        // public final MapleSimSubsystem m_MapleSimSubsystem = new
        // MapleSimSubsystem(m_DrivetrainSubsystem);

        private final Field2d m_field;

        Trajectory m_trajectory;

        // Commands
        private final LockOnAprilTag m_LockOnAprilTag = new LockOnAprilTag(m_DrivetrainSubsystem,
                        m_RightReefLimeLightSubsystem, 0, m_driverController, false,
                        LimelightConstants.desiredLeftRotationOffset);
        private final MoveToAprilTagPosition m_MoveToAprilTagPosition = new MoveToAprilTagPosition(
                        m_DrivetrainSubsystem, m_RightReefLimeLightSubsystem, 0, m_driverController, false,
                        LimelightConstants.desiredLeftRotationOffset, LimelightConstants.desiredLeft3DXOffset,
                        LimelightConstants.desiredLeftArea);
        private final AltMoveToAprilTagPosition m_AltMoveToAprilTagPosition = new AltMoveToAprilTagPosition(
                        m_DrivetrainSubsystem, m_RightReefLimeLightSubsystem, 0, m_driverController, false,
                        LimelightConstants.desiredLeftRotationOffset, LimelightConstants.desiredLeftXOffset,
                        LimelightConstants.desiredLeftYOffset);

        private final DriveToTargetOffset m_DriveToTargetOffset = new DriveToTargetOffset(m_DrivetrainSubsystem,
                        m_LeftReefLimeLightSubsystem, 0, 0, 3, 3);

        private final moveToPreset m_MoveToPresetLvl1 = new moveToPreset(m_ElevatorSubsystem, m_CoralArmSubsystem,
                        m_driverController, m_auxillaryController, 1, false);
        private final moveToPreset m_MoveToPresetLvl2 = new moveToPreset(m_ElevatorSubsystem, m_CoralArmSubsystem,
                        m_driverController, m_auxillaryController, 2, false);
        private final moveToPreset m_MoveToPresetLvl3 = new moveToPreset(m_ElevatorSubsystem, m_CoralArmSubsystem,
                        m_driverController, m_auxillaryController, 3, false);
        private final moveToPreset m_MoveToPresetLvl4 = new moveToPreset(m_ElevatorSubsystem, m_CoralArmSubsystem,
                        m_driverController, m_auxillaryController, 4, false);
        private final defaultState m_DefaultStateCommand = new defaultState(m_ElevatorSubsystem, m_CoralArmSubsystem,
                        m_driverController, m_auxillaryController);
        private final intakePreset m_IntakePreset = new intakePreset(m_ElevatorSubsystem, m_CoralArmSubsystem,
                        m_driverController, m_auxillaryController);
        private final knockAlgaeOff m_KnockLowerAlgaeOff = new knockAlgaeOff(m_ElevatorSubsystem, m_CoralArmSubsystem,
                        true);
        private final knockAlgaeOff m_KnockUpperAlgaeOff = new knockAlgaeOff(m_ElevatorSubsystem, m_CoralArmSubsystem,
                        false);

        /**
         * The container for the robot. Contains subsystems, OI devices, and commands.
         */
        public RobotContainer() {

                // Create the trajectory to follow in autonomous. It is best to initialize
                // trajectories here to avoid wasting time in autonomous.
                m_trajectory = TrajectoryGenerator.generateTrajectory(new Pose2d(0, 0, Rotation2d.fromDegrees(0)),
                                List.of(new Translation2d(1, 1), new Translation2d(2, -1)),
                                new Pose2d(3, 0, Rotation2d.fromDegrees(0)),
                                new TrajectoryConfig(Units.feetToMeters(3.0), Units.feetToMeters(3.0)));
                // Create and push Field2d to SmartDashboard.
                m_field = new Field2d();
                SmartDashboard.putData(m_field);
                // Push the trajectory to Field2d.
                m_field.getObject("traj").setTrajectory(m_trajectory);

                // Configure the trigger bindings
                configureBindings(Math.random());

                // Build an auto chooser. This will use Commands.none() as the default option.
                autoChooser = AutoBuilder.buildAutoChooser();

                SmartDashboard.putData("Auto Chooser", autoChooser);

        }

        private SwerveRequest.FieldCentric defaultDriveLogic() {
                // Note that X is defined as forward according to WPILib convention,
                // and Y is defined as to the left according to WPILib convention.

                final var modifier = m_driverController.rightBumper().getAsBoolean() ? 4
                                : m_driverController.leftBumper().getAsBoolean() ? 2 : 1;

                final var modifiedMaxSpeed = MaxSpeed / modifier;
                final var modifiedMaxAngularRate = MaxAngularRate / modifier;

                // Do this in either robot periodic or subsystem periodic
                m_field.setRobotPose(m_DrivetrainSubsystem.getEstimatedPose());

                return drive
                                // Drive forward with negative Y (forward)
                                .withVelocityX(m_driverController.getLeftY() * modifiedMaxSpeed)
                                // Drive left with negative X (left)
                                .withVelocityY(m_driverController.getLeftX() * modifiedMaxSpeed)
                                // Drive counterclockwise with negative X (left)
                                .withRotationalRate(-m_driverController.getRightX() * modifiedMaxAngularRate);
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

                // ====================== Climber Subsystem ====================== //
                // m_driverController.leftTrigger().whileTrue(
                // new InstantCommand(m_ClimberSubsystem::setToActivePosition,
                // m_ClimberSubsystem));
                // m_driverController.rightTrigger()
                // .whileTrue(new InstantCommand(m_ClimberSubsystem::engageClimber,
                // m_ClimberSubsystem));

                m_driverController.leftTrigger()
                                .whileTrue(new InstantCommand(m_ClimberSubsystem::climberForward, m_ClimberSubsystem))
                                .onFalse(new InstantCommand(() -> m_ClimberSubsystem.climberStop(),
                                                m_ClimberSubsystem));
                m_driverController.rightTrigger()
                                .whileTrue(new InstantCommand(m_ClimberSubsystem::climberBackward, m_ClimberSubsystem))
                                .onFalse(new InstantCommand(() -> m_ClimberSubsystem.climberStop(),
                                                m_ClimberSubsystem));

                // ====================== Drive Subsystem ====================== //
                // Code below is from swerve drive project generator

                m_DrivetrainSubsystem.setDefaultCommand(m_DrivetrainSubsystem.applyRequest(this::defaultDriveLogic));

                m_driverController.x().whileTrue(m_LockOnAprilTag);

                // Align to left reef side
                m_driverController.b().whileTrue(m_MoveToAprilTagPosition);
                // Align to Right reef side
                m_driverController.a().whileTrue(m_AltMoveToAprilTagPosition);

                // m_driverController.pov(0).whileTrue(m_DriveToTargetOffset);

                // reset the field-centric heading on y press
                m_driverController.y()
                                .onTrue(m_DrivetrainSubsystem.runOnce(() -> m_DrivetrainSubsystem.seedFieldCentric()));

                // m_driverController.povCenter().onTrue(interrupt);
                // m_driverController.b().whileTrue(m_DrivetrainSubsystem.applyRequest(() ->
                // point.withModuleDirection(
                // new Rotation2d(-m_driverController.getLeftY(),
                // -m_driverController.getLeftX()))));

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

                m_DrivetrainSubsystem.registerTelemetry(logger::telemeterize);

                // Do this in either robot or subsystem init
                SmartDashboard.putData("Field", m_field);

                // ====================== Limelight Subsystems ====================== //
                // m_LeftReefLimeLightSubsystem.updateOdometry(m_DrivetrainSubsystem);
                // m_RightReefLimeLightSubsystem.updateOdometry(m_DrivetrainSubsystem);

                // ====================== Command Compositions ====================== //

                m_auxillaryController.pov(0)
                                .onTrue(new InstantCommand(() -> m_ElevatorSubsystem.increaseIntakeEncoderPosition()));
                m_auxillaryController.pov(180)
                                .onTrue(new InstantCommand(() -> m_ElevatorSubsystem.decreaseIntakeEncoderPosition()));

                moveToPreset m_MoveToPresetLvl1Auto = new moveToPreset(m_ElevatorSubsystem, m_CoralArmSubsystem,
                                m_driverController, m_auxillaryController, 1, true);
                moveToPreset m_MoveToPresetLvl2Auto = new moveToPreset(m_ElevatorSubsystem, m_CoralArmSubsystem,
                                m_driverController, m_auxillaryController, 2, true);
                moveToPreset m_MoveToPresetLvl3Auto = new moveToPreset(m_ElevatorSubsystem, m_CoralArmSubsystem,
                                m_driverController, m_auxillaryController, 3, true);
                moveToPreset m_MoveToPresetLvl4Auto = new moveToPreset(m_ElevatorSubsystem, m_CoralArmSubsystem,
                                m_driverController, m_auxillaryController, 4, true);

                m_auxillaryController.a().onTrue(m_MoveToPresetLvl1Auto);
                m_auxillaryController.b().onTrue(m_IntakePreset);
                m_auxillaryController.y().onTrue(m_MoveToPresetLvl3);
                m_auxillaryController.x().onTrue(m_MoveToPresetLvl4);
                m_auxillaryController.leftTrigger().onTrue(m_KnockLowerAlgaeOff);
                m_auxillaryController.rightTrigger().onTrue(m_KnockUpperAlgaeOff);
                m_auxillaryController.leftBumper().onTrue(m_IntakePreset);
                m_auxillaryController.rightBumper().onTrue(m_DefaultStateCommand);

                NamedCommands.registerCommand("Level 1 Preset Command", m_MoveToPresetLvl1Auto);
                NamedCommands.registerCommand("Level 2 Preset Command", m_MoveToPresetLvl2Auto);
                NamedCommands.registerCommand("Level 3 Preset Command", m_MoveToPresetLvl3Auto);
                NamedCommands.registerCommand("Level 4 Preset Command", m_MoveToPresetLvl4Auto);
                NamedCommands.registerCommand("Intake Command", m_IntakePreset);
                NamedCommands.registerCommand("Elevator/Coral Default Position", m_DefaultStateCommand);
        }

        public boolean isInDesiredState(ElevatorPresets elevatorPreset, CoralArmLevels coralArmLevel) {
                return m_ElevatorSubsystem.isElevatorAtDesiredState(elevatorPreset).getAsBoolean()
                                && m_CoralArmSubsystem.isCoralArmAtDesiredState(coralArmLevel).getAsBoolean();

        }

        public void debugMethod() {
                m_ElevatorSubsystem.debuggingMethod();
                m_CoralArmSubsystem.debuggingMethod();
                m_DrivetrainSubsystem.debuggingMethod();
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

// Code that I'm too attached to. I can't bring myself to delete it :(

// SequentialCommandGroup levelOneCommand = new SequentialCommandGroup(
// // Align with the reef
// // new RunCommand(() -> {
// // // Will have to test to make sure it is actually getting value
// // if (m_auxillaryController.getHID().getPOV() > 225
// // && m_auxillaryController.getHID().getPOV() < 315) {
// // // NOTE!!! Have to use the RIGHT limelight when trying to go for the
// // // LEFT side of the reef, just cause when the robot is on the left side,
// // // the right limelight has a better view
// // m_DrivetrainSubsystem.applyRequest(() -> drive
// // // Drive forward with negative Y (forward)
// // .withVelocityX(m_RightReefLimeLightSubsystem
// // .limelight_outtake_forward_speed(1))
// // // Drive left with negative X (left)
// // .withVelocityY(-m_driverController.getLeftX()
// // * MaxSpeed)
// // // Drive counterclockwise with negative X (left)
// // .withRotationalRate(m_RightReefLimeLightSubsystem
// // .limelight_aim_proportional()));
// // } else {
// // m_DrivetrainSubsystem.applyRequest(() -> drive
// // // Drive forward with negative Y (forward)
// // .withVelocityX(m_LeftReefLightSubsystem
// // .limelight_outtake_forward_speed(1))
// // // Drive left with negative X (left)
// // .withVelocityY(-m_driverController.getLeftX()
// // * MaxSpeed)
// // // Drive counterclockwise with negative X (left)
// // .withRotationalRate(m_LeftReefLightSubsystem
// // .limelight_aim_proportional()));
// // }
// // }).until(() -> {
// // if (usingLeftLimelightForAlignment) {
// // return m_LeftReefLightSubsystem.isRobotInDesiredReefPosition(1);
// // } else {
// // return m_RightReefLimeLightSubsystem.isRobotInDesiredReefPosition(1);
// // }
// // }),

// // Move elevator to the level 1 preset and coral arm to outtake
// new RunCommand(() -> {
// // if (!m_ElevatorSubsystem.isElevatorAtBottom()) {
// m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.Level1);
// // }
// if (m_ElevatorSubsystem
// .getMotorPosition() >
// Constants.ElevatorConstants.ElevatorPreset.level1EncoderValue) {
// m_CoralArmSubsystem.outtake(CoralArmLevels.lvl1, false);
// }

// // }, m_ElevatorSubsystem, m_CoralArmSubsystem).until(() ->
// m_ElevatorSubsystem
// // .isElevatorAtDesiredState(ElevatorPresets.Level1).getAsBoolean()
// // && m_CoralArmSubsystem.isCoralArmAtDesiredState(CoralArmLevels.lvl1)
// // .getAsBoolean()
// // && m_auxillaryController.getHID().getLeftStickButton()),
// }, m_ElevatorSubsystem, m_CoralArmSubsystem).until(() -> (isInDesiredState(
// ElevatorPresets.Level1, CoralArmLevels.lvl1)
// && m_auxillaryController.getHID().getLeftStickButton())
// || (m_auxillaryController.getHID().getLeftStickButton()
// && m_auxillaryController.getHID()
// .getRightStickButton())),

// new RunCommand(() -> {
// // if (!m_ElevatorSubsystem.isElevatorAtBottom()) {
// m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.Level1);
// // }
// if (m_ElevatorSubsystem
// .getMotorPosition() >
// Constants.ElevatorConstants.ElevatorPreset.level1EncoderValue) {
// m_CoralArmSubsystem.outtake(CoralArmLevels.lvl1, true);
// }
// }, m_ElevatorSubsystem, m_CoralArmSubsystem)

// );
// m_auxillaryController.a().onTrue(Commands.sequence(m_MoveToPresetLvl1));

// NamedCommands.registerCommand("Level 1 Preset Command", m_MoveToPresetLvl1);

// SequentialCommandGroup levelTwoCommand = new SequentialCommandGroup(

// // Move elevator to the level 2 preset and coral arm to outtake
// new RunCommand(() -> {
// // if (!m_ElevatorSubsystem.isElevatorAtBottom()) {
// m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.Level2);
// // }
// if (m_ElevatorSubsystem
// .getMotorPosition() >
// Constants.ElevatorConstants.ElevatorPreset.level1EncoderValue) {
// m_CoralArmSubsystem.outtake(CoralArmLevels.lvl2, false);
// }

// // }, m_ElevatorSubsystem, m_CoralArmSubsystem).until(() ->
// (m_ElevatorSubsystem
// // .isElevatorAtDesiredState(ElevatorPresets.Level2).getAsBoolean()
// // && m_CoralArmSubsystem.isCoralArmAtDesiredState(CoralArmLevels.lvl1)
// // // IMPORTANT NOTE!!! SINCE LVL1 AND LVL2 ARE SO SIMILAR
// // // IN POSITION, IT WILL NOT THINK IT IS LEVEL 2 BUT
// // // LEVEL 1, SO WE WILL BE USING LEVEL 1 FOR THIS CHECK
// // .getAsBoolean()
// // && m_auxillaryController.getHID().getLeftStickButton())
// // || (m_ElevatorSubsystem.isElevatorAtDesiredState(ElevatorPresets.Level2)
// // .getAsBoolean()
// // && m_CoralArmSubsystem
// // .isCoralArmAtDesiredState(
// // CoralArmLevels.lvl2)
// // .getAsBoolean()
// // && m_auxillaryController.getHID()
// // .getLeftStickButton())),

// }, m_ElevatorSubsystem, m_CoralArmSubsystem).until(() -> ((isInDesiredState(
// ElevatorPresets.Level2, CoralArmLevels.lvl2)
// || (isInDesiredState(ElevatorPresets.Level1, CoralArmLevels.lvl2)))
// && m_auxillaryController.getHID().getLeftStickButton())
// || (m_auxillaryController.getHID().getLeftStickButton()
// && m_auxillaryController.getHID()
// .getRightStickButton())),

// new RunCommand(() -> {
// // if (!m_ElevatorSubsystem.isElevatorAtBottom()) {
// m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.Level2);
// // }
// if (m_ElevatorSubsystem
// .getMotorPosition() >
// Constants.ElevatorConstants.ElevatorPreset.level1EncoderValue) {
// m_CoralArmSubsystem.outtake(CoralArmLevels.lvl2, true);
// }
// }, m_ElevatorSubsystem, m_CoralArmSubsystem));

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

// SequentialCommandGroup levelThreeCommand = new SequentialCommandGroup(// //
// Align with the reef
// // m_DrivetrainSubsystem.applyRequest(() -> drive
// // // Drive forward with negative Y (forward)
// // .withVelocityX(m_RightReefLimeLightSubsystem
// // .limelight_outtake_forward_speed(CoralArmLevels.lvl3,
// // m_driverController))
// // // Drive left with negative X (left)
// // .withVelocityY(m_RightReefLimeLightSubsystem
// // .limelight_outtake_side_speed(CoralArmLevels.lvl3, true,
// // m_driverController))
// // // Drive counterclockwise with negative X (left)
// // .withRotationalRate(0
// // // m_RightReefLimeLightSubsystem
// // // .limelight_aim_proportional()
// // ))

// // // }
// // .until(() -> {
// // return m_RightReefLimeLightSubsystem
// // .isRobotInDesiredReefPosition(
// // CoralArmLevels.lvl3, true);

// // })

// // Move elevator to the level 3 preset and coral arm to outtake
// new RunCommand(() -> {
// // if (!m_ElevatorSubsystem.isElevatorAtBottom()) {
// m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.Level3);
// // }
// if (m_ElevatorSubsystem
// .getMotorPosition() >
// Constants.ElevatorConstants.ElevatorPreset.level1EncoderValue) {
// m_CoralArmSubsystem.outtake(CoralArmLevels.lvl3, false);
// }

// }, m_ElevatorSubsystem, m_CoralArmSubsystem).until(() -> (isInDesiredState(
// ElevatorPresets.Level3, CoralArmLevels.lvl3)
// && m_auxillaryController.getHID().getLeftStickButton())
// || (m_auxillaryController.getHID().getLeftStickButton()
// && m_auxillaryController.getHID()
// .getRightStickButton())),
// // m_ElevatorSubsystem, m_CoralArmSubsystem).until(() ->
// // m_ElevatorSubsystem
// // .isElevatorAtDesiredState(ElevatorPresets.Level3).getAsBoolean()
// // && m_CoralArmSubsystem.isCoralArmAtDesiredState(CoralArmLevels.lvl3)
// // .getAsBoolean()
// // && m_auxillaryController.getHID().getLeftStickButton()),

// new RunCommand(() -> {
// if (!m_ElevatorSubsystem.isElevatorAtBottom()) {
// m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.Level3);
// }
// if (m_ElevatorSubsystem
// .getMotorPosition() >
// Constants.ElevatorConstants.ElevatorPreset.level1EncoderValue) {
// m_CoralArmSubsystem.outtake(CoralArmLevels.lvl3, true);
// }
// }, m_ElevatorSubsystem, m_CoralArmSubsystem)

// );
// .and(m_auxillaryController.povLeft())

// SequentialCommandGroup levelFourCommand = new SequentialCommandGroup(
// // Align with the reef
// // new RunCommand(() -> {
// // Will have to test to make sure it is actually getting value

// // NOTE!!! Have to use the RIGHT limelight when trying to go for the
// // LEFT side of the reef, just cause when the robot is on the left side,
// // the right limelight has a better view
// // m_DrivetrainSubsystem.applyRequest(() -> drive
// // // Drive forward with negative Y (forward)
// // .withVelocityX(m_RightReefLimeLightSubsystem
// // .limelight_outtake_forward_speed(CoralArmLevels.lvl4))
// // // Drive left with negative X (left)
// // .withVelocityY(m_RightReefLimeLightSubsystem
// // .limelight_outtake_side_speed(CoralArmLevels.lvl4,
// // true))
// // // Drive counterclockwise with negative X (left)
// // .withRotationalRate(0
// // // m_RightReefLimeLightSubsystem
// // // .limelight_aim_proportional()
// // ))

// // // }
// // .until(() -> {
// // return m_RightReefLimeLightSubsystem
// // .isRobotInDesiredReefPosition(
// // CoralArmLevels.lvl4, true);

// // })

// // // Move elevator to the level 4 preset and coral arm to outtake
// new RunCommand(() -> {
// // if (!m_ElevatorSubsystem.isElevatorAtBottom()) {
// m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.Level4);
// // }
// if (m_ElevatorSubsystem
// .getMotorPosition() >
// Constants.ElevatorConstants.ElevatorPreset.level1EncoderValue) {
// m_CoralArmSubsystem.outtake(CoralArmLevels.lvl4, false);
// }

// }, m_ElevatorSubsystem, m_CoralArmSubsystem).until(() -> (isInDesiredState(
// ElevatorPresets.Level4, CoralArmLevels.lvl4)
// && m_auxillaryController.getHID().getLeftStickButton())
// || (m_auxillaryController.getHID().getLeftStickButton()
// && m_auxillaryController.getHID()
// .getRightStickButton())),

// new RunCommand(() -> {
// m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.Level4);
// if (m_ElevatorSubsystem
// .getMotorPosition() >
// Constants.ElevatorConstants.ElevatorPreset.level4EncoderValue) {
// m_CoralArmSubsystem.outtake(CoralArmLevels.lvl4, true);
// }
// }, m_ElevatorSubsystem, m_CoralArmSubsystem));
// level 4 preset

// // Intake Function

// SequentialCommandGroup intakeCommand = new SequentialCommandGroup(
// // Align with the intake station
// // new RunCommand(() -> m_DrivetrainSubsystem.applyRequest(() -> drive
// // // Drive forward with negative Y (forward)
// // .withVelocityX(m_IntakeLimeLightSubsystem
// // .limelight_intake_forward_speed())
// // // Drive left with negative X (left)
// // .withVelocityY(-m_driverController.getLeftX() * MaxSpeed)
// // // Drive counterclockwise with negative X (left)
// // .withRotationalRate(m_IntakeLimeLightSubsystem
// // .limelight_aim_proportional()))),
// // // Move elevator to the intake preset
// new RunCommand(() -> {
// // if (!m_ElevatorSubsystem.isElevatorAtBottom()) {
// m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.intake);
// // }
// m_CoralArmSubsystem.intake();
// }, m_ElevatorSubsystem)

// // .until(() -> {
// // return m_CoralArmSubsystem.coralGrabbed();
// // }), new RunCommand(() -> {
// // m_ElevatorSubsystem.elevatorMoveToPresetMM(ElevatorPresets.defaultState);
// // m_CoralArmSubsystem.defaultState();
// // }, m_ElevatorSubsystem, m_CoralArmSubsystem)
// );