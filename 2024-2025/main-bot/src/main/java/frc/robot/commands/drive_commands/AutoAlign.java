package frc.robot.commands.drive_commands;
// // Copyright (c) FIRST and other WPILib contributors.
// // Open Source Software; you can modify and/or share it under the terms of
// // the WPILib BSD license file in the root directory of this project.

// package frc2025.commands;

// import edu.wpi.first.math.MathUtil;
// import edu.wpi.first.math.Pair;
// import edu.wpi.first.math.controller.ProfiledPIDController;
// import edu.wpi.first.math.geometry.Pose2d;
// import edu.wpi.first.math.geometry.Translation2d;
// import edu.wpi.first.math.geometry.Twist2d;
// import edu.wpi.first.math.kinematics.ChassisSpeeds;
// import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;
// import edu.wpi.first.math.util.Units;
// import edu.wpi.first.wpilibj2.command.Command;
// import frc2025.Dashboard;
// import frc2025.RobotContainer;
// import frc2025.controlboard.Controlboard.ScoringLocation;
// import frc2025.logging.Logger;
// import frc2025.subsystems.drivetrain.Drivetrain;
// import frc2025.subsystems.superstructure.elevator.Elevator;
// import java.util.function.Supplier;
// import util.GeomUtil;

// public class AutoAlign extends Command {

// private final RobotContainer container;
// private final Drivetrain drivetrain;
// private final Elevator elevator;
// private Supplier<ScoringLocation> location;

// private boolean isAuto = false;

// private final ProfiledPIDController driveController =
// new ProfiledPIDController(10.0, 0, 0.0, new Constraints(3.8, 4.0));
// private final ProfiledPIDController headingController =
// new ProfiledPIDController(9.5, 0, 0, new
// Constraints(Units.degreesToRadians(360.0), 8.0));

// private Supplier<Pair<Pose2d, Pose2d>> targetPose;

// private Translation2d lastSetpointTranslation = new Translation2d();
// private double driveErrorAbs = 0.0;
// private double headingErrorAbs = 0.0;

// private final double driveTolerance = 0.01;
// private final double headingTolerance = Units.degreesToRadians(1.0);

// private double currentDistance = 0.0;

// private final double ffMinRadius = 0.1;
// private final double ffMaxRadius = 0.8;

// public AutoAlign(RobotContainer container, Supplier<ScoringLocation>
// location) {
// this.container = container;
// this.drivetrain = container.getSubsystems().drivetrain();
// this.elevator = container.getSubsystems().superstructure().getElevator();
// this.location = location;

// switch (location.get()) {
// case LEFT:
// case RIGHT:
// targetPose = () ->
// container.getRobotState().getTargetBranchPoses(location.get());
// break;
// case CENTER:
// targetPose =
// () ->
// Pair.of(
// container.getRobotState().getTargetAlgaeState().getFirst(),
// container.getRobotState().getTargetAlgaeState().getFirst());
// break;
// default:
// targetPose = () -> Pair.of(drivetrain.getEstimatedPose(),
// drivetrain.getEstimatedPose());
// break;
// }

// isAuto = false;

// headingController.enableContinuousInput(-Math.PI, Math.PI);

// addRequirements(drivetrain);
// }

// public AutoAlign(RobotContainer container, ScoringLocation location) {
// this.container = container;
// this.drivetrain = container.getSubsystems().drivetrain();
// this.elevator = container.getSubsystems().superstructure().getElevator();
// this.location = () -> location;

// switch (location) {
// case LEFT:
// case RIGHT:
// targetPose = () -> container.getRobotState().getTargetBranchPoses(location);
// break;
// case CENTER:
// targetPose =
// () ->
// Pair.of(
// container.getRobotState().getTargetAlgaeState().getFirst(),
// container.getRobotState().getTargetAlgaeState().getFirst());
// break;
// default:
// targetPose = () -> Pair.of(drivetrain.getEstimatedPose(),
// drivetrain.getEstimatedPose());
// break;
// }

// isAuto = true;

// headingController.enableContinuousInput(-Math.PI, Math.PI);

// addRequirements(drivetrain);
// }

// @Override
// public void initialize() {
// Pose2d currentPose = drivetrain.getEstimatedPose();
// Twist2d fieldVel = drivetrain.getFieldVelocity();
// Translation2d linearFieldVel = new Translation2d(fieldVel.dx, fieldVel.dy);
// driveController.reset(
// currentPose.getTranslation().getDistance(targetPose.get().getSecond().getTranslation()),
// Math.min(
// 0.0,
// -linearFieldVel
// .rotateBy(
// targetPose
// .get()
// .getSecond()
// .getTranslation()
// .minus(currentPose.getTranslation())
// .getAngle()
// .unaryMinus())
// .getX()));
// headingController.reset(currentPose.getRotation().getRadians(),
// fieldVel.dtheta);
// lastSetpointTranslation = currentPose.getTranslation();
// }

// @Override
// public void execute() {
// if (targetPose == null) {
// return;
// }
// Pose2d currentPose;
// if (Dashboard.useGlobalEstimateForAutoAlign.get()) {
// currentPose = drivetrain.getEstimatedPose();
// } else {
// currentPose = drivetrain.getEstimatedPose();
// }
// if (!isAuto) {
// switch (location.get()) {
// case LEFT:
// case RIGHT:
// targetPose = () ->
// container.getRobotState().getTargetBranchPoses(location.get());
// break;
// case CENTER:
// targetPose =
// () ->
// Pair.of(
// container.getRobotState().getTargetAlgaeState().getFirst(),
// container.getRobotState().getTargetAlgaeState().getFirst());
// break;
// default:
// targetPose = () -> Pair.of(drivetrain.getEstimatedPose(),
// drivetrain.getEstimatedPose());
// break;
// }
// }
// Pose2d targetPose =
// this.targetPose
// .get()
// .getFirst()
// .interpolate(
// this.targetPose.get().getSecond(),
// currentPose
// .getTranslation()
// .getDistance(this.targetPose.get().getFirst().getTranslation())
// * 1.5);

// driveController.setConstraints(new Constraints(elevator.getDriveScalar(),
// 4.0));

// currentDistance =
// currentPose.getTranslation().getDistance(targetPose.getTranslation());
// double ffScalar =
// MathUtil.clamp((currentDistance - ffMinRadius) / (ffMaxRadius - ffMinRadius),
// 0.0, 1.0);
// driveErrorAbs = currentDistance;
// driveController.reset(
// lastSetpointTranslation.getDistance(targetPose.getTranslation()),
// driveController.getSetpoint().velocity);
// double driveVelocityScalar =
// driveController.getSetpoint().velocity * ffScalar
// + driveController.calculate(driveErrorAbs, 0.0);

// if (currentDistance < driveTolerance) {
// driveVelocityScalar = 0.0;
// }

// lastSetpointTranslation =
// new Pose2d(
// targetPose.getTranslation(),
// currentPose.getTranslation().minus(targetPose.getTranslation()).getAngle())
// .transformBy(
// GeomUtil.translationToTransform(driveController.getSetpoint().position, 0.0))
// .getTranslation();

// double thetaVelocity =
// headingController.getSetpoint().velocity * ffScalar
// + headingController.calculate(
// currentPose.getRotation().getRadians(),
// targetPose.getRotation().getRadians());
// headingErrorAbs =
// Math.abs(currentPose.getRotation().minus(targetPose.getRotation()).getRadians());
// if (headingErrorAbs < headingTolerance) {
// thetaVelocity = 0.0;
// }

// Translation2d driveVelocity =
// new Pose2d(
// Translation2d.kZero,
// currentPose.getTranslation().minus(targetPose.getTranslation()).getAngle())
// .transformBy(GeomUtil.translationToTransform(driveVelocityScalar, 0.0))
// .getTranslation();

// drivetrain.setControl(
// drivetrain
// .getHelper()
// .getApplyRobotSpeeds(
// ChassisSpeeds.fromFieldRelativeSpeeds(
// driveVelocity.getX(),
// driveVelocity.getY(),
// thetaVelocity,
// currentPose.getRotation())));

// Logger.log("AutoAlign/TargetPose", targetPose);
// Logger.log(
// "AutoAlign/TargetPoseT",
// currentPose
// .getTranslation()
// .getDistance(this.targetPose.get().getFirst().getTranslation()));
// Logger.log("AutoAlign/ElevHeightScalar", elevator.getDriveScalar());
// }

// @Override
// public void end(boolean interrupted) {
// drivetrain.stop();
// }

// @Override
// public boolean isFinished() {
// return targetPose == null || (isAuto && currentDistance < driveTolerance);
// }

// public boolean hasReachedGoal() {
// return currentDistance < driveTolerance && headingController.atSetpoint();
// }

// public boolean hasReachedGoal(double distTolerance) {
// return currentDistance < distTolerance && headingController.atSetpoint();
// }
// }
