// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

/*
 * This Is The Input List. Whenever Adding A New Input, Make Sure To Add It To The List Below
 * 
 *  Driver Controller:
 *    A - Brake
 *    B - Point
 *    Back + Y - SysId Dynamic Forward
 *    Back + X - SysId Dynamic Reverse
 *    Start + Y - SysId Quasistatic Forward
 *    Start + X - SysId Quasistatic Reverse
 *    DPad Up - Coral Arm Up
 *    DPad Down - Coral Arm Down
 *    DPad Center - Coral Arm Stop
 *    Right Stick - Coral Arm Emergency Stop
 *    LB - Reset Field Centric Seed
 * 
 *  Auxiliary Controller:
 *    A - Level 1 Elevator + Coral Arm 
 *    B - Level 2 Elevator + Coral Arm
 *    Y - Level 3 Elevator + Coral Arm
 *    X - Level 4 Elevator + Coral Arm
 *    Left Joystick up - Move Elevator up
 *    Left Joystick down - Move Elevator down
 *    LB - Coral Arm Up
 *    RB - Coral Arm Down
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
import frc.robot.commands.ExampleCommand;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.CoralArmSubsystem.CoralArmLevels;
import frc.robot.subsystems.ElevatorSubsystem.ElevatorPresets;
import frc.robot.subsystems.AlgaeArmSubsystem;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.CoralArmSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.subsystems.ElevatorSubsystem.ElevatorPresets;

/**
 * little secret comment OwO This class is where the bulk of the robot should be
 * declared. Since Command-based is a "declarative" paradigm, very little robot
 * logic should actually be handled in the {@link Robot} periodic methods (other
 * than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
    // The robot's subsystems and commands are defined here...
    // private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem();
    private final ElevatorSubsystem m_elevatorSubsystem = new ElevatorSubsystem(
            Constants.ElevatorConstants.kElevatorLeftMotorPort, Constants.ElevatorConstants.kElevatorRightMotorPort);
    private final AlgaeArmSubsystem m_AlgaeArmSubsystem = new AlgaeArmSubsystem(
            Constants.AlgaeArmConstants.algaeArmBarID);
    private final CoralArmSubsystem m_CoralArmSubsystem = new CoralArmSubsystem(Constants.CoralArmConstants.coralArmID);
    private final CommandXboxController m_driverController = new CommandXboxController(
            OperatorConstants.kDriverControllerPort);

    private final SendableChooser<Command> autoChooser;
    /* Swerve drive platform setup */ // From Swerve Project Generator
    private double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
    private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second
                                                                                      // max
                                                                                      // angular velocity

    /* Setting up bindings for necessary control of the swerve drive platform */
    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric().withDeadband(MaxSpeed * 0.1)
            .withRotationalDeadband(MaxAngularRate * 0.1) // Add a 10% deadband
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors
    private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
    private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

    private final Telemetry logger = new Telemetry(MaxSpeed);

    private final CommandXboxController m_auxillaryController = new CommandXboxController(
            Constants.OperatorConstants.kAuxiliaryControllerPort);

    public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();

    // End of Swerve Drive Platform setup

    /**
     * The container for the robot. Contains subsystems, OI devices, and commands.
     */
    public RobotContainer() {

        // Build an auto chooser. This will use Commands.none() as the default option.
        autoChooser = AutoBuilder.buildAutoChooser();

        // Another option that allows you to specify the default auto by its name
        // autoChooser = AutoBuilder.buildAutoChooser("My Default Auto");

        SmartDashboard.putData("Auto Chooser", autoChooser);

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
     * @param random TODO
     */
    private void configureBindings(double random) {

        // ================ Elevator Subsystem ================ //

        // Sets the default command for the elevator (the command that always runs)
        // Checks if the left joystick is being moved, if it is, it will run the
        // commandVoltage method with the left joystick's Y value
        m_elevatorSubsystem.setDefaultCommand((Math.abs(m_auxillaryController.getLeftY()) > 0.1)
                ? m_elevatorSubsystem.commandVoltage(m_auxillaryController.getLeftY())
                : m_elevatorSubsystem.commandVoltage(0));
        // 10 is a PLACEHOLDER value for now

        // The commands below make the elevator go to certain levels
        m_auxillaryController.a().onTrue(new RunCommand(
                () -> m_elevatorSubsystem.moveElevatorToPreset(ElevatorPresets.Level1), m_elevatorSubsystem));
        m_auxillaryController.b().onTrue(new RunCommand(
                () -> m_elevatorSubsystem.moveElevatorToPreset(ElevatorPresets.Level2), m_elevatorSubsystem));
        m_auxillaryController.x().onTrue(new RunCommand(
                () -> m_elevatorSubsystem.moveElevatorToPreset(ElevatorPresets.Level3), m_elevatorSubsystem));
        m_auxillaryController.y().onTrue(new RunCommand(
                () -> m_elevatorSubsystem.moveElevatorToPreset(ElevatorPresets.Level4), m_elevatorSubsystem));

        // ================ Algae Arm Subsystem ================ //
        m_auxillaryController.povUp().whileTrue(m_AlgaeArmSubsystem.commandMakeBarSpin(true, false, false))
                .onFalse(m_AlgaeArmSubsystem.commandMakeBarSpin(false, false, false));// makes algaeBar spin faster if A
                                                                                      // pressed, else stop
        m_auxillaryController.povDown().whileTrue(m_AlgaeArmSubsystem.commandMakeBarSpin(false, true, false))
                .onFalse(m_AlgaeArmSubsystem.commandMakeBarSpin(false, false, false));// makes algae bar spit out ball
                                                                                      // if B
                                                                                      // pressed, else stop
        m_auxillaryController.povLeft().whileTrue(m_AlgaeArmSubsystem.commandMakeBarSpin(false, false, true))
                .onFalse(m_AlgaeArmSubsystem.commandMakeBarSpin(false, false, false));// makes algae bar spin slowly if
                                                                                      // Y
                                                                                      // pressed, else stop
        // m_auxillaryController.a().whileTrue(m_AlgaeArmSubsystem.commandAlgaeIntake(m_AlgaeArmSubsystem))
        // .onFalse(m_AlgaeArmSubsystem.commandAlgaeOuttake(m_AlgaeArmSubsystem));
        // This command was taken out due to clashing and we already have a command that
        // does the same thing

        // Command Compositions for Elevator + Coral
        // Level 1 Preset
        // m_auxillaryController.a()
        // .onTrue(Commands.sequence(
        // m_elevatorSubsystem.commandMoveToPreset(ElevatorPresets.Level1)
        // .until(m_elevatorSubsystem.isElevatorAtDesiredState(ElevatorPresets.Level1)),
        // m_CoralArmSubsystem.CommandsetCoralArmVoltage(0.1, CoralArmLevels.Up)
        // .until(m_CoralArmSubsystem.isCoralArmAtDesiredState(CoralArmLevels.Up)),
        // Commands.waitSeconds(.5), // Allow the driver time to move up to the reef
        // m_CoralArmSubsystem.CommandsetCoralArmVoltage(-0.1, CoralArmLevels.Down)
        // .until(m_CoralArmSubsystem.isCoralArmAtDesiredState(CoralArmLevels.Down)),
        // m_elevatorSubsystem.commandMoveToPreset(ElevatorPresets.Lowest)
        // .until(m_elevatorSubsystem.isElevatorAtDesiredState(ElevatorPresets.Highest))));
        // // Level 2 Preset
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
        // .until(m_elevatorSubsystem.isElevatorAtDesiredState(ElevatorPresets.Highest))));

        // ================ Coral Arm Subsystem ================ //
        // makes coral arm go up
        m_auxillaryController.leftBumper()
                .whileTrue(m_CoralArmSubsystem.CommandsetCoralArmVoltage(0.5, CoralArmLevels.Up));
        // makes coral arm go down
        m_auxillaryController.rightBumper()
                .whileTrue(m_CoralArmSubsystem.CommandsetCoralArmVoltage(-0.5, CoralArmLevels.Down));
        // stops coral arm
        // m_auxillaryController.povCenter().whileTrue(m_CoralArmSubsystem.CommandsetCoralArmVoltage(0,
        // CoralArmLevels.Stop));
        // Emergency Stop for Coral Arm (in case it goes past the top or bottom) Note: I
        // don't know if the onTrue method will only run once, so test it before you use
        // it
        m_auxillaryController.povCenter().onTrue(m_CoralArmSubsystem.emergencyStop());

        // ================ Drive Subsystem ================ //
        // Code below is from swerve drive project generator

        // Note that X is defined as forward according to WPILib convention,
        // and Y is defined as to the left according to WPILib convention.
        drivetrain.setDefaultCommand(
                // Drivetrain will execute this command periodically
                drivetrain.applyRequest(() -> drive.withVelocityX(-m_driverController.getLeftY() * MaxSpeed) // Drive
                                                                                                             // forward
                                                                                                             // with
                                                                                                             // negative
                                                                                                             // Y
                                                                                                             // (forward)
                        .withVelocityY(-m_driverController.getLeftX() * MaxSpeed) // Drive left with negative X (left)
                        .withRotationalRate(-m_driverController.getRightX() * MaxAngularRate) // Drive counterclockwise
                                                                                              // with
                                                                                              // negative X (left)
                ));
        m_driverController.a().whileTrue(drivetrain.applyRequest(() -> brake));
        m_driverController.b().whileTrue(drivetrain.applyRequest(() -> point
                .withModuleDirection(new Rotation2d(-m_driverController.getLeftY(), -m_driverController.getLeftX()))));

        // Run SysId routines when holding back/start and X/Y.
        // Note that each routine should be run exactly once in a single log.
        m_driverController.back().and(m_driverController.y()).whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
        m_driverController.back().and(m_driverController.x()).whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
        m_driverController.start().and(m_driverController.y())
                .whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
        m_driverController.start().and(m_driverController.x())
                .whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));

        // reset the field-centric heading on left bumper press
        m_driverController.leftBumper().onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));

        drivetrain.registerTelemetry(logger::telemeterize);

    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        // An example command will be run in autonomous
        // return Autos.exampleAuto(m_exampleSubsystem);
        return autoChooser.getSelected();
    }
}
// another why not comment :)
// I do love some good comments :D
