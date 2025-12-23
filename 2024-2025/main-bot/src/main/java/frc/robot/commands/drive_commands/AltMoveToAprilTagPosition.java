// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.drive_commands;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.LimeLightSubsystem;

import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecond;

public class AltMoveToAprilTagPosition extends Command {
  private LimeLightSubsystem m_Limelight;
  private frc.robot.subsystems.CommandSwerveDrivetrain m_Drivetrain;
  private int m_pipeline;
  private PIDController thetaController = new PIDController(.03, 0, 0.0015);
  private PIDController sideController = new PIDController(0.03, 0, 0.0015);
  private PIDController forwardController = new PIDController(0.03, 0, 0.0015);
  private CommandXboxController controller;
  private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond);
  private double offsetOfDesired = 0;
  private double desiredXOffset = 1; // Temp???
  private double desiredYOffset = 1; // Temp???
  // private boolean isLeftSide;

  // (swerve, Limelight2, 0, driver, false
  public AltMoveToAprilTagPosition(CommandSwerveDrivetrain drivetrain, LimeLightSubsystem Limelight, int pipeline,
      CommandXboxController controller, boolean robotcentric) {
    addRequirements(drivetrain);
    m_Drivetrain = drivetrain;
    m_Limelight = Limelight;
    m_pipeline = pipeline;
    this.controller = controller;
    // m_skew = skewDegrees;
  }

  public AltMoveToAprilTagPosition(CommandSwerveDrivetrain drivetrain, LimeLightSubsystem Limelight, int pipeline,
      CommandXboxController controller, boolean robotcentric, double offsetOfDesired, double desiredXOffset,
      double desiredYOffset) {
    addRequirements(drivetrain);
    m_Drivetrain = drivetrain;
    m_Limelight = Limelight;
    m_pipeline = pipeline;
    this.controller = controller;
    this.offsetOfDesired = offsetOfDesired;
    this.desiredXOffset = desiredXOffset;
    this.desiredYOffset = desiredYOffset;
    // this.isLeftSide = isLeftSide;

    // m_skew = skewDegrees;
  }

  private double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed

  private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
      .withDriveRequestType(DriveRequestType.OpenLoopVoltage).withDeadband(MaxSpeed * 0.1);

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_Limelight.setPipeline(m_pipeline);
    thetaController.reset();
    sideController.reset();
    forwardController.reset();
    thetaController.setTolerance(Math.toRadians(1.5));
    sideController.setTolerance(1); // Temp value
    forwardController.setTolerance(1); // Temp value
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    SmartDashboard.putBoolean("LL Robot Not in Position", false);
    SmartDashboard.putBoolean("AllignOnTarget running", true);
    double thetaOutput = 0;
    double fowardOutput = controller.getLeftY();
    double sideOutput = controller.getLeftX();
    if (m_Limelight.hasTarget()) {
      double horizontal_angle = m_Limelight.getHorizontalAngleOfErrorDegrees();
      double horizontalDistance = m_Limelight.getTXNC();
      double verticalDistance = m_Limelight.getTYNC();
      double rotationSetpoint = offsetOfDesired;// + Math.toRadians(m_skew.get());
      double sideSetpoint = desiredXOffset;
      double forwardSetpoint = desiredYOffset;
      thetaController.setSetpoint(rotationSetpoint);
      sideController.setSetpoint(sideSetpoint);
      forwardController.setSetpoint(forwardSetpoint);

      // Theta (angle)
      if (!thetaController.atSetpoint()) {
        SmartDashboard.putNumber("Theta setpoint", rotationSetpoint);
        thetaOutput = thetaController.calculate(horizontal_angle, rotationSetpoint);
      }
      SmartDashboard.putNumber("Theta targeting error", horizontal_angle);
      // x (side value)
      if (!sideController.atSetpoint()) {
        SmartDashboard.putNumber("Side setpoint", sideSetpoint);
        sideOutput = sideController.calculate(horizontalDistance, sideSetpoint);
      }
      SmartDashboard.putNumber("Side Targeting error", horizontalDistance);
      // area (foward value)
      if (!forwardController.atSetpoint()) {
        SmartDashboard.putNumber("Area setpoint", forwardSetpoint);
        fowardOutput = forwardController.calculate(verticalDistance, forwardSetpoint);
      }
      SmartDashboard.putNumber("Area Targeting error", verticalDistance);
    } else {
      System.out.println("NO TARGET");
    }

    m_Drivetrain.setControl(drive.withVelocityX(fowardOutput * MaxSpeed).withVelocityY(-sideOutput * MaxSpeed)
        .withRotationalRate(thetaOutput * MaxAngularRate));
    SmartDashboard.putNumberArray("Limelight X Velocity, Y Velocity, and Rotation",
        new double[] { fowardOutput * MaxSpeed, -sideOutput * MaxSpeed, thetaOutput * MaxAngularRate });
    SmartDashboard.putNumber("LL X velocity", fowardOutput * MaxSpeed);
    SmartDashboard.putNumber("LL Y velocity", -sideOutput * MaxSpeed);
    SmartDashboard.putNumber("LL Theta output", thetaOutput * MaxAngularRate);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    SmartDashboard.putBoolean("AlignOnTarget running", false);
    m_Drivetrain.setControl(drive.withVelocityX(0).withVelocityY(0).withRotationalRate(0));
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    // return false;// return targeting &&
    // Math.abs(m_Limelight.getVerticalAngleOfErrorDegrees() )
    // <= 3;
    return thetaController.atSetpoint() && sideController.atSetpoint() && forwardController.atSetpoint();
  }
}