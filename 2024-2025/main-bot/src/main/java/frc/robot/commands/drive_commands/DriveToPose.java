package frc.robot.commands.drive_commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.RobotContainer;
import frc.robot.RobotContainer.Subsystems;
// import frc.robot.logging.Logger;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.generated.DrivetrainConstants;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.ElevatorSubsystem;
// import frc.robot.subsystems.superstructure.elevator.Elevator;
// import frc.robot.subsystems.superstructure.elevator.ElevatorConstants;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import pid.ScreamPIDConstants;
import math.ScreamMath;
import util.GeomUtil;

public class DriveToPose extends Command {

    private final CommandSwerveDrivetrain drivetrain;
    private final ElevatorSubsystem elevator;
    private final Supplier<Pose2d> currentPose;
    private final Supplier<Pose2d> targetPose;

    private static final ProfiledPIDController driveController = DrivetrainConstants.DRIVE_ALIGNMENT_CONTROLLER;
    private static final ProfiledPIDController headingController = DrivetrainConstants.HEADING_CONTROLLER_PROFILED;

    public static double driveErrorAbs;
    private Translation2d lastSetpointTranslation;

    private Optional<DoubleSupplier> yOverride = Optional.empty();

    private BooleanSupplier slowMode = () -> false;

    private final double driveTolerance = 0.019;

    private final double logFrequency = 0.5;

    public DriveToPose(Subsystems subsystems, Supplier<Pose2d> targetPose) {
        this.drivetrain = subsystems.drivetrain();
        this.elevator = subsystems.elevatorSubsystem();
        this.currentPose = () -> drivetrain.getEstimatedPose();
        this.targetPose = targetPose;
        addRequirements(drivetrain);
        setName("DriveToPose");
        driveController.setTolerance(driveTolerance);
        headingController.setTolerance(Units.degreesToRadians(1));
        headingController.enableContinuousInput(-Math.PI, Math.PI);
    }

    public DriveToPose(Subsystems subsystems, Supplier<Pose2d> targetPose, DoubleSupplier yOverride) {
        this(subsystems, targetPose);
        this.yOverride = Optional.of(yOverride);
    }

    public DriveToPose(Subsystems subsystems, Supplier<Pose2d> targetPose, BooleanSupplier slowMode) {
        this(subsystems, targetPose);
        this.slowMode = slowMode;
    }

    public DriveToPose(Subsystems subsystems, Pose2d targetPose) {
        this(subsystems, () -> targetPose);
    }

    @Override
    public void initialize() {
        Pose2d currentPose = drivetrain.getEstimatedPose();
        driveController.reset(currentPose.getTranslation().getDistance(targetPose.get().getTranslation()),
                Math.min(0.0,
                        -new Translation2d(drivetrain.getFieldVelocity().dx, drivetrain.getFieldVelocity().dy)
                                .rotateBy(targetPose.get().getTranslation()
                                        .minus(drivetrain.getEstimatedPose().getTranslation()).getAngle().unaryMinus())
                                .getX()));
        headingController.reset(currentPose.getRotation().getRadians(), drivetrain.getYawRate().getRadians());
        lastSetpointTranslation = currentPose.getTranslation();
        // Controlboard.isSwitchingSide = false;
    }

    @Override
    public void execute() {
        Pose2d currentPose = drivetrain.getEstimatedPose();
        Pose2d targetPose = this.targetPose.get();

        double currentDistance = currentPose.getTranslation().getDistance(targetPose.getTranslation());
        double ffScalar = MathUtil.clamp((currentDistance) / (0.8), 0.0, 1.0);
        double elevHeightScalar = elevator.getMeasuredHeight().getInches();
        elevHeightScalar = MathUtil.clamp(elevHeightScalar, 0.0, ElevatorConstants.MAX_HEIGHT.getInches());
        elevHeightScalar = ScreamMath.mapRange(elevHeightScalar, 0.0, ElevatorConstants.MAX_HEIGHT.getInches(), 1.0,
                0.5);

        driveErrorAbs = currentDistance;

        lastSetpointTranslation = new Pose2d(targetPose.getTranslation(),
                currentPose.getTranslation().minus(targetPose.getTranslation()).getAngle())
                        .transformBy(GeomUtil.translationToTransform(driveController.getSetpoint().position, 0.0))
                        .getTranslation();

        driveController.reset(lastSetpointTranslation.getDistance(targetPose.getTranslation()),
                driveController.getSetpoint().velocity);
        double driveVelocity = driveController.getSetpoint().velocity * ffScalar
                + driveController.calculate(driveErrorAbs, 0.0) * elevHeightScalar;
        double headingVelocity = headingController.calculate(currentPose.getRotation().getRadians(),
                targetPose.getRotation().getRadians());

        Translation2d velocity;

        if (yOverride.isPresent()) {
            velocity = new Pose2d(Translation2d.kZero,
                    currentPose.getTranslation().minus(targetPose.getTranslation()).getAngle())
                            .transformBy(GeomUtil.translationToTransform(driveVelocity, 0.0)).getTranslation();
            velocity = new Translation2d(velocity.getX(), yOverride.get().getAsDouble());
        } else {
            velocity = new Pose2d(Translation2d.kZero,
                    currentPose.getTranslation().minus(targetPose.getTranslation()).getAngle())
                            .transformBy(GeomUtil.translationToTransform(driveVelocity, 0.0)).getTranslation();
        }

        if (yOverride.isPresent() && Math.abs(targetPose.minus(currentPose).getX()) < driveTolerance) {
            velocity = new Translation2d(0.0, yOverride.get().getAsDouble());
        }

        velocity = velocity.times(elevHeightScalar);

        if (slowMode.getAsBoolean()) {
            velocity = new Translation2d(MathUtil.clamp(velocity.getNorm(), 0.0, elevHeightScalar),
                    velocity.getAngle());
        }

        drivetrain.setControl(drivetrain.getHelper()
                .getApplyFieldSpeeds(new ChassisSpeeds(velocity.getX(), velocity.getY(), headingVelocity)));

        Logger.log("DriveToPose/FFScalar", ffScalar, logFrequency);
        Logger.log("DriveToPose/HeightScalar", elevHeightScalar, logFrequency);
        Logger.log("DriveToPose/MeasuredDistance", currentDistance, logFrequency);
        Logger.log("DriveToPose/DistanceSetpoint", driveController.getSetpoint().position, logFrequency);
        Logger.log("DriveToPose/MeasuredHeading", currentPose.getRotation().getDegrees(), logFrequency);
        Logger.log("DriveToPose/SetpointHeading", targetPose.getRotation().getDegrees(), logFrequency);
        Logger.log("DriveToPose/Setpoint",
                new Pose2d(lastSetpointTranslation, new Rotation2d(headingController.getSetpoint().position)),
                logFrequency);
        Logger.log("DriveToPose/TargetPose", targetPose, logFrequency);
        // SmartDashboard.putNumber("", headingVelocity)
    }

    public static void warmup(Subsystems subsystems) {
        ProfiledPIDController tempDriveController = DrivetrainConstants.DRIVE_ALIGNMENT_CONTROLLER;
        ProfiledPIDController tempHeadingController = DrivetrainConstants.HEADING_CONTROLLER_PROFILED;

        tempDriveController.setTolerance(0.01);
        tempHeadingController.setTolerance(Units.degreesToRadians(1));
        tempHeadingController.enableContinuousInput(-Math.PI, Math.PI);

        tempDriveController.calculate(0.1, 0.0);
        tempHeadingController.calculate(0.0, 0.1);

        Pose2d dummyPose = Pose2d.kZero;
        DriveToPose dummyCommand = new DriveToPose(subsystems, dummyPose);
        dummyCommand.initialize();
        dummyCommand.execute();
        dummyCommand.end(false);

        Logger.log("DriveToPose/FFScalar", 0.0);
        Logger.log("DriveToPose/HeightScalar", 0.0);
        Logger.log("DriveToPose/MeasuredDistance", 0.0);
        Logger.log("DriveToPose/DistanceSetpoint", 0.0);
        Logger.log("DriveToPose/MeasuredHeading", Rotation2d.kZero);
        Logger.log("DriveToPose/SetpointHeading", Rotation2d.kZero);
        Logger.log("DriveToPose/Setpoint", Pose2d.kZero);
        Logger.log("DriveToPose/TargetPose", Pose2d.kZero);
    }

    @Override
    public void end(boolean interrupted) {
        drivetrain.stop();
    }

    @Override
    public boolean isFinished() {
        return Math.abs(
                targetPose.get().getTranslation().minus(currentPose.get().getTranslation()).getX()) < driveTolerance
                && Math.abs(targetPose.get().getTranslation().minus(currentPose.get().getTranslation())
                        .getY()) < driveTolerance;
    }
}
