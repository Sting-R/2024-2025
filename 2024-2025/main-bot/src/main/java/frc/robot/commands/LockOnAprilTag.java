// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import static edu.wpi.first.units.Units.MetersPerSecond;

import java.util.function.Supplier;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.LimeLightSubsystem;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecond;

public class LockOnAprilTag extends Command {
  private LimeLightSubsystem m_Limelight;
  private frc.robot.subsystems.CommandSwerveDrivetrain m_Drivetrain;
  private int m_pipeline;
  private PIDController thetaController = new PIDController(.03, 0, 0.0015);
  private boolean targeting = false;
  private CommandXboxController controller;
  private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond);
  private double offsetOfDesired = 0;

  // (swerve, Limelight2, 0, driver, false
  public LockOnAprilTag(CommandSwerveDrivetrain drivetrain, LimeLightSubsystem Limelight, int pipeline,
      CommandXboxController controller, boolean robotcentric) {
    addRequirements(drivetrain);
    m_Drivetrain = drivetrain;
    m_Limelight = Limelight;
    m_pipeline = pipeline;
    this.controller = controller;
    // m_skew = skewDegrees;
  }

  public LockOnAprilTag(CommandSwerveDrivetrain drivetrain, LimeLightSubsystem Limelight, int pipeline,
      CommandXboxController controller, boolean robotcentric, double offsetOfDesired) {
    addRequirements(drivetrain);
    m_Drivetrain = drivetrain;
    m_Limelight = Limelight;
    m_pipeline = pipeline;
    this.controller = controller;
    this.offsetOfDesired = offsetOfDesired;
    // m_skew = skewDegrees;
  }

  private double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed

  private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
      .withDriveRequestType(DriveRequestType.OpenLoopVoltage).withDeadband(MaxSpeed * 0.1);
  private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
  private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_Limelight.setPipeline(m_pipeline);
    targeting = false;
    thetaController.reset();
    thetaController.setTolerance(Math.toRadians(1.5));
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    SmartDashboard.putBoolean("AllignOnLLTarget running", true);
    double thetaOutput = 0;
    double xOutput = controller.getLeftY();
    double yOutput = controller.getLeftX();
    if (m_Limelight.hasTarget()) {
      double vertical_angle = m_Limelight.getHorizontalAngleOfErrorDegrees();
      double horizontal_angle = m_Limelight.getHorizontalAngleOfErrorDegrees();
      double setpoint = offsetOfDesired;// + Math.toRadians(m_skew.get());
      thetaController.setSetpoint(setpoint);
      targeting = true;
      if (!thetaController.atSetpoint()) {
        SmartDashboard.putNumber("setpoint", setpoint);
        thetaOutput = thetaController.calculate(horizontal_angle, setpoint);
      }
      SmartDashboard.putNumber("targeting error", horizontal_angle);
    } else {
      System.out.println("NO TARGET");
    }
    m_Drivetrain.setControl(drive.withVelocityX(xOutput * MaxSpeed).withVelocityY(-yOutput * MaxSpeed)
        .withRotationalRate(thetaOutput * MaxAngularRate));
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    SmartDashboard.putBoolean("AllignOnLLTarget running", false);
    m_Drivetrain.setControl(drive.withVelocityX(0).withVelocityY(0).withRotationalRate(0));
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;// return targeting && Math.abs(m_Limelight.getVerticalAngleOfErrorDegrees() )
                 // <= 3;
  }
}