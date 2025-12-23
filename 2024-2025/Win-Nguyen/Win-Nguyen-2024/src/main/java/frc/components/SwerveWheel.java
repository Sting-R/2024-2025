package frc.components;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.PositionDutyCycle;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import frc.robot.Settings;

public class SwerveWheel {
  /**
   * Wheel circumference in meters.
   */
  public final static double WHEEL_CIRCUMFERENCE = 0.3191858136;

  /**
   * Drive gear ratio of the swerve modules from motor rotations to wheel
   * rotations
   */
  public final static double DRIVE_GEAR_RATIO = 6.55;

  /**
   * The Falcon 500's maximum rated RPM.
   */
  public final static int MAXIMUM_FALCON_RPM = 6380;

  /**
   * The wheel's maximum rated RPM using a Falcon 500.
   */
  public final static double MAXIMUM_WHEEL_RPM =
      MAXIMUM_FALCON_RPM / DRIVE_GEAR_RATIO;

  /**
   * The Kraken's RPM
   */
  public final static int KRAKEN_RPM = 6000;

  /**
   * The maximum speed of the robot in meters per second.
   *
   * <p>
   * 60 is used to convert from minutes to seconds.
   * </p>
   */
  public final static double MAX_SPEED =
      MAXIMUM_WHEEL_RPM * WHEEL_CIRCUMFERENCE / 60;

  TalonFX turnMotor;
  TalonFX driveMotor;
  CANcoder encoder;

  // 0.238125 Meters
  // 0.365125 Meters

  Translation2d modulePos;

  double defensiveAngle;

  public SwerveWheel(Settings.SwerveDrive.Wheel settings) {
    turnMotor = new TalonFX(settings.turnID);
    driveMotor = new TalonFX(settings.driveID);
    encoder = new CANcoder(settings.encoderID);

    modulePos = new Translation2d(settings.posX, settings.posY);
  }

  public void set(SwerveModuleState swerveModuleState) {
    // swerveModuleState   = SwerveModuleState.optimize(swerveModuleState,
    // Rotation2d.fromDegrees(turnMotor.getSelectedSensorPosition()
    // / 11.3777778));
    swerveModuleState = SwerveModuleState.optimize(
        swerveModuleState,
        Rotation2d.fromDegrees(
            encoder.getAbsolutePosition().getValueAsDouble() * 360));

    // final double desiredTurnPos = swerveModuleState.angle.getDegrees()
    // * 11.3777778; // conversion from degrees to native encoder value
    final double desiredTurnPos =
        swerveModuleState.angle.getDegrees() /
        360; // conversion from degrees to native encoder value

    // turnMotor.set(ControlMode.Position, desiredTurnPos);
    turnMotor.setControl(new PositionDutyCycle(desiredTurnPos));

    // driveMotor.set(ControlMode.PercentOutput,
    // swerveModuleState.speedMetersPerSecond / maximumSpeed);
    driveMotor.setControl(
        new DutyCycleOut(swerveModuleState.speedMetersPerSecond / MAX_SPEED));
    // Using Velocity Can Be Used For Better Control But Percent Output Is
    // Better Used For Practice
  }

  public void defense() {
    // turnMotor.set(ControlMode.Position, defensiveAngle * 11.77777777);
    // driveMotor.set(ControlMode.PercentOutput, 0);
  }

  public SwerveModulePosition getOdometryPosition() {
    return new SwerveModulePosition(
        driveMotor.getPosition().getValueAsDouble() / DRIVE_GEAR_RATIO *
            WHEEL_CIRCUMFERENCE,
        Rotation2d.fromRotations(
            encoder.getAbsolutePosition().getValueAsDouble()));
  }

  public TalonFX getTurnMotor() { return turnMotor; }

  public TalonFX getDriveMotor() { return driveMotor; }

  public Translation2d getSwervePos() { return modulePos; }
  // public TalonFXConfigurator findConfig() {
  //     TalonFXConfigurator testValue = turnMotor.getConfigurator();
  //     testValue.
  //     return turnMotor.getConfigurator();
  // }

  // public void battenDownTheHatches()
  // {
  //     set(defensiveAngle, 0, 0);
  // }
}
