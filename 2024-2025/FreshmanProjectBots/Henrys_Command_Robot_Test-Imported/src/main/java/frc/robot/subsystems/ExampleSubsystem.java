// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;

public class ExampleSubsystem extends SubsystemBase {

  private final TalonSRX testMotor;
  /** Creates a new ExampleSubsystem. */
  public ExampleSubsystem(int testId) {
    testMotor = new TalonSRX(testId);
  }

  /**
   * Example command factory method.
   *
   * @return a command
   */
  public Command exampleMethodCommand() {
    // Inline construction of command goes here.
    // Subsystem::RunOnce implicitly requires `this` subsystem.
    return runOnce(()
                       -> {
                           /* one-time action goes here */
                       });
  }

  public Command setMotorVoltageCommand(double voltage) {
    return runOnce(()
                       -> {
                           setMotorVoltage(voltage);
                       });
  }

  /**
   * An example method querying a boolean state of the subsystem (for example, a
   * digital sensor).
   *
   * @return value of some boolean subsystem state, such as a digital sensor.
   */
  public boolean exampleCondition() {
    // Query some boolean state, such as a digital sensor.
    int x = 3;
    if (x != 1) {
      return false;
    }
    return true;

  }

  public void setMotorVoltage(double voltage) {
    testMotor.set(ControlMode.PercentOutput, voltage);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
